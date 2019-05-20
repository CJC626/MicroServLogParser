package cjc.tracebuilder.input;

import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.output.types.OutputType;

/**
 *  The defintion of the parameters for this application provided by the user (or set to default values otherwise).
 */
public class UserParams {

    private OutputType _outputType;
    private String _outputFilePath;
    private int _numOfThreads;
    private long _processTimeoutPeriod;
    private int _serviceTimeoutPeriod;
    private InputType _inputType;
    private String _inputFilePath;

    /**
     * Constucts a UserParams object with the following defaults:
     *  - OutputType: STDOUT
     *  - OutputFilePath: null
     *  - NumOfThreads: Runtime.getRuntime().availableProcessors() / 2
     *  - ProcessTimeoutPeriod (not used): -1 (never time out)
     *  - ServiceTimeoutPeriod: 500 0ms
     *  - InputFilePath: null
     */
    public UserParams(){
        _inputType = InputType.STDIN;
        _outputType = OutputType.STDOUT;
        _outputFilePath = null;
        //TODO move to function
        _numOfThreads = Runtime.getRuntime().availableProcessors() / 2;
        //Not currently used
        _processTimeoutPeriod = -1; //never time out
        _serviceTimeoutPeriod = 5000;
        _inputFilePath = null;
    }

    /**
     * Obtains the user input type (TEXTFILE or STDIN).
     * @return the user input type
     */
    public InputType getInputType() {
        return _inputType;
    }

    /**
     * Sets the user input type.  Must be TEXTFILE or STDIN.  Otherwise application exits due to Invalid Input Type.
     * @param _inputType the user input type (TEXTFILE or STDIN)
     */
    public void setInputType(String _inputType) {
        try {
            this._inputType = InputType.valueOf(_inputType);
        }catch(IllegalArgumentException e){
            //TODO Concat Value
            System.out.println("Invalid Input Type: " + _inputType);
            System.exit(1);
        }
    }

    /**
     * Obtains the input file path.
     * @return the input file path (if InputType==TEXTFILE), otherwise null.
     */
    public String getInputFilePath() {
        return _inputFilePath;
    }

    /**
     * Sets the input file path. Required if InputType==TEXTFILE.
     * @param _inputFilePath the input file path.
     */
    public void setInputFilePath(String _inputFilePath) {
        this._inputFilePath = _inputFilePath;
    }

    /**
     * Obtains the output type (TEXTFILE or STDIN).
     * @return the output type
     */
    public OutputType getOutputType() {
        return _outputType;
    }

    /**
     * Sets the output type (TEXTFILE or STDIN)
     * @param _outputType the output type
     */
    public void setOutputType(String _outputType) {
        try{
            this._outputType = OutputType.valueOf(_outputType);
        }catch(IllegalArgumentException e){
            //TODO contact value
            System.out.println("Invalid Output Type");
            System.exit(1);
        }
    }

    /**
     * Obtains the output file path.
     * @return the output file path (if OutputType==TEXTFILE), otherwise null.
     */
    public String getOutputFilePath() {
        return _outputFilePath;
    }

    /**
     * Sets the output file path. Required if OutputType==TEXTFILE.
     * @param _outputFilePath the output file path.
     */
    public void setOutputFilePath(String _outputFilePath) {
        this._outputFilePath = _outputFilePath;
    }

    /**
     * Obtains the number of trace parser worker threads.
     * @return the number of trace parser worker threads.
     */
    public int getNumOfThreads() {
        return _numOfThreads;
    }

    /**
     * Sets the number of trace parser worker threads.
     * @param _numOfThreads the number of trace parser worker threads.
     */
    public void setNumOfThreads(String _numOfThreads) {
        try {
            this._numOfThreads = Integer.valueOf(_numOfThreads);
        } catch(NumberFormatException e){
            System.out.println("Incorrect Number Format for -n");
            System.exit(1);
        }
    }

    /**
     * Obtains the application's timeout window (not yet used).
     * @return the application's timeout window
     */
    public long getProcessTimeoutPeriod() {
        return _processTimeoutPeriod;
    }

    /**
     * Sets the application's timeout window (not yet used).
     * @param _processTimeoutPeriod the application's timeout window
     */
    public void setProcessTimeoutPeriod(String _processTimeoutPeriod) {
        try{
            this._processTimeoutPeriod = Long.valueOf(_processTimeoutPeriod);
        } catch(NumberFormatException e){
            //TODO string concat
            System.out.println("Incorrect Number Format for -t");
            System.exit(1);
        }
    }

    /**
     * Obtains the finished trace handler's "wait" period for pending output traces
     * (starting from creation time) before assuming the pending output trace has collected
     * all of its service calls.
     * @return the trace handler's timeout period for new service calls.
     */
    public int getServiceTimeoutPeriod() {
        return _serviceTimeoutPeriod;
    }

    /**
     * Sets the finished trace handler's "wait" period for pending output traces
     * (starting from creation time) before assuming the pending output trace has collected
     * all of its service calls.
     * @param _serviceTimeoutPeriod the trace handler's timeout period for new service calls.
     */
    public void setServiceTimeoutPeriod(String _serviceTimeoutPeriod) {
        try{
            this._serviceTimeoutPeriod = Integer.valueOf(_serviceTimeoutPeriod);
        } catch(NumberFormatException e){
            //TODO string concat
            System.out.println("Incorrect Number Format for -s");
            System.exit(1);
        }
    }


}
