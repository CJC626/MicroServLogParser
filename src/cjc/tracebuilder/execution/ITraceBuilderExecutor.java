package cjc.tracebuilder.execution;

/**
 *   The interface that describe an executor in our application.  Contains two methods:
 *    - startExection - contains the work performed by our executor
 *    - noMoreWorkToDo - flag that indicates work for this executor is finished
 */
public interface ITraceBuilderExecutor {
    /**
     *  Contains the execution logic for our executor.
     */
    public void startExecution();

    /**
     * Flag that indicates work for this executor is finished.
     * @return true if our executor has no more work to do.
     */
    public boolean noMoreWorkToDo();
}
