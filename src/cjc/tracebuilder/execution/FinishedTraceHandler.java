package cjc.tracebuilder.execution;

import cjc.tracebuilder.execution.pending.PendingOutputTrace;
import cjc.tracebuilder.input.types.InputType;

import java.util.Calendar;

/**
 * An executor singleton that monitors a queue of pending output traces.  It obtains the oldest pending output trace (first element iterated in a LinkedHashMap)
 * and checks if the pending trace is "ready" (i.e. there are no more service calls to wait for, based on the amount of time passed since its creation.  This amount of
 * time is an input param from the user).  If ready, the FinishedTraceHandler parses the pending trace into the final output tree, to be written to the output file
 * provided with the -g parameter (or STDIN by default) by the output writer executor.
 */
public class FinishedTraceHandler extends TraceBuilderExecutorBase {
    private static FinishedTraceHandler ourInstance = new FinishedTraceHandler();

    /**
     * Our finished trace handler instance.
     * @return Our finished trace handler instance.
     */
    public static FinishedTraceHandler getInstance() {
        return ourInstance;
    }

    private FinishedTraceHandler() { }

    /**
     *  Begins execution of the FinishedTraceHandler, monitoring the pending output trace queue.
     */
    @Override
    public void startExecution() {
        boolean isStdIn = ExecutionStatusManager.getInstance().getUserParams().getInputType()== InputType.STDIN;
        if(!isStdIn) {
            System.out.println("startExecution - FinishTraceHandling");
        }
        while(!noMoreWorkToDo()){
            long serviceTimeoutPeriod = ExecutionStatusManager.getInstance().getUserParams().getServiceTimeoutPeriod();
            // block our LinkedHashMap
            synchronized (TraceParser.getInstance().getPendingOutputTraces()){
                if(!TraceParser.getInstance().getPendingOutputTraces().isEmpty()) {
                    // Get the first value entered into the LinkedHashMap.  Assumes LinkedHashMap.entrySet.iterator.next() returns the "head" entry (the oldest entry in the map).
                    PendingOutputTrace headPendingOutputTrace = TraceParser.getInstance().getPendingOutputTraces().entrySet().iterator().next().getValue();
                    // Has enough time passed to assume that our pending trace has all its service calls?
                    if (ExecutionStatusManager.getInstance().isStdInReadingFinished() ||
                            hasPendingOutputTraceExpired(headPendingOutputTrace, serviceTimeoutPeriod) ) {
                        // Did we ever find the root service call of the output trace.  If not, consider it orphaned and do not log it to our file.
                        if (headPendingOutputTrace.isRootNodeFound()) {
                            OutputTraceWriter.getInstance().addPendingTraceToReadyQueue(headPendingOutputTrace.buildFinishedOutputTrace("null", null));
                        } else {
                            //TODO - Possibly log elsewhere
                            System.out.println("Root Node not found.  Considered orphan. " + headPendingOutputTrace.getOutputTrace().getId());
                        }
                        // Remove the pending trace from the pending Queue/LinkedHashMap
                        TraceParser.getInstance().getPendingOutputTraces().remove(headPendingOutputTrace.getOutputTrace().getId());
                    }
                }
            }
        }
        //No more work to do.  Mark as finished.
        if(!isStdIn){
            System.out.println("FinishedTraceHandler - execution done.");
        }
        ExecutionStatusManager.getInstance().setTraceHandlingFinished(true);
    }

    /**
     * Flag that checks if the FinishedTraceHandler has no more work to do.
     *  - Is the input writer finished?
     *  - Is the trace parser finished?
     *  - Is the LinkedHashMap of pending output traces empty?
     *  - Is the LinkedList of trace strings (log lines) empty?
     * @return true if the FinishedTraceHandler has no more work to do.
     */
    @Override
    public boolean noMoreWorkToDo() {
        return ExecutionStatusManager.getInstance().isInputFinished() &&
                ExecutionStatusManager.getInstance().isProcessingFinished() &&
                TraceParser.getInstance().getPendingOutputTraces().isEmpty() &&
                TraceParser.getInstance().getTraceStringsToParseQueue().isEmpty();
    }

    private boolean hasPendingOutputTraceExpired(PendingOutputTrace trace, long serviceTimeoutPeriod){
        //Output traces from Stdin do not expire.  Might want to configure STDIN with long ServiceTimeoutPeriod instead.
        return ExecutionStatusManager.getInstance().getUserParams().getInputType() == InputType.TEXTFILE &&
                trace.getCreationTime().getTime() < Calendar.getInstance().getTimeInMillis() - serviceTimeoutPeriod;
    }
}
