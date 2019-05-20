package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.UserParams;

/**
 *   Base implementation of an executor, extended by default by all our executor implementations.
 */
abstract class TraceBuilderExecutorBase implements ITraceBuilderExecutor{

    /**
     * Returns the user defined input parameters for this application.
     * @return the user input parameters provided
     */
    public UserParams getUserParams() {
        return ExecutionStatusManager.getInstance().getUserParams();
    }


    /**
     *  Starts the execution logic for this executor.
     */
    public void invokeExecutor(){
        if(getUserParams() == null){
            //TODO - cleanup
            System.out.println("ERROR - No user parameters were provided.");
            System.exit(1);
        }
        this.startExecution();
    }

}
