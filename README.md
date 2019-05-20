# MicroServLogParser
This is a utility, run as a jar file, that reads from a collection of log of request (let's say HTTP) traces and parses all traces with a common trace ID into a JSON formatted execution tree.  The format of a request trace in the log may be structured accordingly:
```
[start-timestamp] [end-timestamp] [trace-id] [service-name] [caller-span]->[span]
```
An example collection of request traces may look as follows:
```
2013-10-23T10:12:35.019Z 2013-10-23T10:12:35.019Z ngr7jl6y service2 5dm5aee3->z35dizqs
2013-10-23T10:12:35.019Z 2013-10-23T10:12:35.019Z ngr7jl6y service1 5dm5aee3->iefxqg3m
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service9 fs5tyi4d->vz6i3piz
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service5 fs5tyi4d->zxg7j6v2
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service3 vz6i3piz->lbfwtwbj
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service4 zjl2uj44->3dujdlyx
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service5 5dm5aee3->fs5tyi4d
2013-10-23T10:12:35.019Z 2013-10-23T10:12:35.020Z ngr7jl6y service6 null->5dm5aee3
2013-10-23T10:12:35.020Z 2013-10-23T10:12:35.020Z ngr7jl6y service5 5dm5aee3->zjl2uj44
```
This utility reads a collection of logged request traces (either from a user defined input text file, or from the standard-input), aggregates collections of log traces based on trace-id, and generates a JSON formatted trace output tree that describes the path of the root service call (defined with caller-span "null") and every "sub-service" call made in the tree. 

As a result, the resulting trace parsing utility would then return a JSON output like this:
```
{
	"id":"ngr7jl6y",
	"root":{
		"service":"service1",
		"start":"2013-10-23T10:12:35.019Z",
		"end":"2013-10-23T10:12:35.020Z",
		"calls":[
			{
				"service":"service3",
				"start":"2013-10-23T10:12:35.020Z",
				"end":"2013-10-23T10:12:35.020Z",
				"calls":[
					{
						"service":"service4",
						"start":"2013-10-23T10:12:35.020Z",
						"end":"2013-10-23T10:12:35.020Z",
						"calls":[],
						"span":"zxg7j6v2"
					}],
				"span":"fs5tyi4d"
			},{
				"service":"service2",
				"start":"2013-10-23T10:12:35.019Z",
				"end":"2013-10-23T10:12:35.019Z",
				"calls":[],
				"span":"iefxqg3m"
			}
		],
		"span":"5dm5aee3"
	}
}
```
## Usage
The utility is to be used as an executable jar file.  This executable can be ran without any user parameters (it will default most values).  However, users can provide the following input parameters:
 - -i Input Type (STDIN or TEXTFILE. STDIN is the default)
 - -f Input File Path (Absolute.  Defaults to null.  Must be provided if Input Type is TEXTFILE)
 - -o Output Type (STDOUT or TEXTFILE.  STDOUT is the default)
 - -g Output File path (Absolute.  Defaults to null.  Must be provided if Output Type is TEXTFILE)
 - -n Number of threads the main trace parser will use (Defaults to Runtime.getRuntime().availableProcessors() / 2)
 - -s "Service Trace Timeout" time in milliseconds the parser will wait to collect pending service calls on an individual trace ID (Defaults to 5000 ms)
 
 Example:
 ```
 java -jar MicroServLogParser.jar -i TEXTFILE -f /users/cjc626/my-trace-log.txt -o TEXTFILE -g /users/cjc626/my-trace-jsons.txt -n 4 -s 5000
 ```
## Building
### IntelliJ IDEA
1. Fork the master repo to create your own branch
2. In IntellJ, select "Import Project"
3. Select the root folder from the forked branch
4. Select "Create project from existing sources"
5. Click "Next"
6. When it says "Source files for your project have been found.", select both "src" and "test" folders and click "Next"
7. When it says "Please review libraries found", make sure "junit-4.13-beta-3" is selected and click "Next"
8. When it says "Please review suggested module structure for the project", click "Next"
9. Select your Java SDK and click "Next"
10.  Click "Finish"

You can now manage the code in IntelliJ.  If you want to build an executable Jar from IntelliJ:
1.  Go to File -> Project Structure
2.  Select Project Settings -> Artifacts
3.  Click the green + and select JAR -> From modules with dependencies
4.  In "Create JAR From Modules", click "OK"
7.  Click "Apply" then "OK"

Now to build your JAR:
1.  Go to Build -> Build Artifacts
2.  Select your IntelliJ project -> Build

Your IDEA project should now contain an out/artifacts/MicroServLogParser_jar/MicroServLogParser.jar file.  You can now execute this using the parameters defined in the Usage section.

## Notes
1.  This utility uses the following FasterXML/Jackson libaries to convert Java objects into JSON:
  - [jackson-annotations](https://github.com/FasterXML/jackson-annotations) (v2.9.9)
  - [jackson-core](https://github.com/FasterXML/jackson-core) (v2.9.9)
  - [jackson-databind] (https://github.com/FasterXML/jackson-databind) (v2.9.9)
2.  Personally, I have found that this runs most optimally with -n set to 4 (my test machine has 8 processors).  As this already uses an individual thread each for an input reader, trace parser, finished trace handler and output writer singleton, Using 4 more appears to get the best utilization out of the processors I have.  Your results may vary depending on the specs of your machine.
