package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.UserParams;

abstract class TraceBuilderExecutorBase implements ITraceBuilderExecutor{


    public UserParams getUserParams() {
        return ExecutionStatusManager.getInstance().getUserParams();
    }


    public void invokeExecutor(){
        if(getUserParams() == null){
            //TODO - cleanup
            System.out.println("No user parameters were provided.");
            System.exit(1);
        }
        this.startExecution();
    }

    //TODO maybe make configurable
    protected void putExecutorToBriefSleep(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
