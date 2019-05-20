package cjc.tracebuilder.util;

import cjc.tracebuilder.input.UserParams;
import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.output.types.OutputType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *  A utility class that parses user input parameters, while also performing validation on such parameters.
 *      - exits the application if the user specifies InputType (-i) as TEXTFILE but does not specify an InputFilePath (-f)
 *      - exits the application if the user specifies OutputType (-o) as TEXTFILE but does not specify an OutputFilePath (-g)
 *      - exits the application if the user specifies an invalid number type for:
 *          - NumOfThreads (-n)
 *          - ProcessTimeoutPeriod (-t)
 *          - ServiceTimeoutPeriod (-s)
 */
public class UserParamParser {

    private static final Map<String, String> PARAM_MAP = new HashMap<String, String>(){{
        put("-i","setInputType");
        put("-f","setInputFilePath");
        put("-o","setOutputType");
        put("-g","setOutputFilePath");
        put("-n","setNumOfThreads");
        put("-t","setProcessTimeoutPeriod");
        put("-s","setServiceTimeoutPeriod");
    }};

    /**
     * Parses the user input parameters from a string array format.  Format comes as follows:
     * -i <i>InputType</i> -f <i>InputFilePath</i> -o <i>OutputType</i> -g <i>OutputFilePath</i> -n <i>NumOfThreads</i> -t <i>ProcessTimeoutPeriod</i> -s <i>ServiceTimeoutPeriod</i>
     * Exits the application if an invalid input parameter is provided.
     * @param args the user input paramters, provided in a string array format.
     * @return the parsed UserParams object to be stored in the ExecutionStatusManager.
     */
    public static UserParams parseParameters(String[] args){
        UserParams userParams = new UserParams();
        int argctr = 0;
        while(argctr < args.length){
            String currentArg = args[argctr];
            if(!PARAM_MAP.containsKey(args[argctr])){
                //TODO Concat which field
                System.out.println("Invalid field provided: " + args[argctr]);
                System.exit(1);
            }
            String currentSetMethod = PARAM_MAP.get(currentArg);
            argctr++;
            if(argctr >= args.length){
                System.out.println("Invalid parameter value provided for " + args[argctr-1]);
                System.exit(1);
            }
            setParamValue(userParams, currentSetMethod, args[argctr]);
            argctr++;
        }
        validateUserParams(userParams);
        return userParams;
    }

    private static void setParamValue(UserParams userParams, String methodname, Object value)  {
        try {
            Method setMethod = UserParams.class.getMethod(methodname, String.class);
            setMethod.invoke(userParams, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }

    private static void validateUserParams(UserParams userParams){
        if(userParams.getInputType() == InputType.TEXTFILE && userParams.getInputFilePath() == null){
            System.out.println("Input Type was TEXTFILE, but no input file was provided (-f).");
            System.exit(1);
        }
        if(userParams.getOutputType() == OutputType.TEXTFILE && userParams.getOutputFilePath() == null){
            System.out.println("Output Type was TEXTFILE, but no output file was provided (-g).");
            System.exit(1);
        }
    }


}
