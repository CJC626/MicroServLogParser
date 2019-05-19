package cjc.tracebuilder.input;

import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.output.types.OutputType;

import java.text.NumberFormat;

public class UserParams {

    private OutputType _outputType;
    private String _outputFilePath;
    private int _numOfThreads;
    private long _processTimeoutPeriod;
    private int _serviceTimeoutPeriod;
    private InputType _inputType;
    private String _inputFilePath;

    public UserParams(){
        _outputType = OutputType.STDOUT;
        _outputFilePath = null;
        _numOfThreads = Runtime.getRuntime().availableProcessors();
        _processTimeoutPeriod = -1; //never time out
        _serviceTimeoutPeriod = 5000;
        _inputFilePath = null;
    }

    public InputType getInputType() {
        return _inputType;
    }

    public void setInputType(String _inputType) {
        try {
            this._inputType = InputType.valueOf(_inputType);
        }catch(IllegalArgumentException e){
            //TODO Concat Value
            System.out.println("Invalid Input Type");
            System.exit(1);
        }
    }

    public String getInputFilePath() {
        return _inputFilePath;
    }

    public void setInputFilePath(String _inputFilePath) {
        this._inputFilePath = _inputFilePath;
    }

    public OutputType getOutputType() {
        return _outputType;
    }

    public void setOutputType(String _outputType) {
        try{
            this._outputType = OutputType.valueOf(_outputType);
        }catch(IllegalArgumentException e){
            //TODO contact value
            System.out.println("Invalid Output Type");
            System.exit(1);
        }
    }

    public String getOutputFilePath() {
        return _outputFilePath;
    }

    public void setOutputFilePath(String _outputFilePath) {
        this._outputFilePath = _outputFilePath;
    }

    public int getNumOfThreads() {
        return _numOfThreads;
    }

    public void setNumOfThreads(String _numOfThreads) {
        try {
            this._numOfThreads = Integer.valueOf(_numOfThreads);
        } catch(NumberFormatException e){
            //TODO string concat
            System.out.println("Incorrect Number Format");
            System.exit(1);
        }
    }

    public long getProcessTimeoutPeriod() {
        return _processTimeoutPeriod;
    }

    public void setProcessTimeoutPeriod(String _processTimeoutPeriod) {
        try{
            this._processTimeoutPeriod = Long.valueOf(_processTimeoutPeriod);
        } catch(NumberFormatException e){
            //TODO string concat
            System.out.println("Incorrect Number Format");
            System.exit(1);
        }
    }

    public int getServiceTimeoutPeriod() {
        return _serviceTimeoutPeriod;
    }

    public void setServiceTimeoutPeriod(String _serviceTimeoutPeriod) {
        try{
            this._serviceTimeoutPeriod = Integer.valueOf(_serviceTimeoutPeriod);
        } catch(NumberFormatException e){
            //TODO string concat
            System.out.println("Incorrect Number Format");
            System.exit(1);
        }
    }


}
