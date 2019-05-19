package cjc.tracebuilder.execution;

import cjc.tracebuilder.execution.pending.PendingOutputTrace;
import com.sun.deploy.trace.Trace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinishedTraceHandler extends TraceBuilderExecutorBase {
    private static FinishedTraceHandler ourInstance = new FinishedTraceHandler();

    public static FinishedTraceHandler getInstance() {
        return ourInstance;
    }

    private FinishedTraceHandler() {
    }

    @Override
    public void startExecution() {
        System.out.println("startExecution - FinishTraceHandling");
        while(!noMoreWorkToDo()){
            int serviceTimeoutPeriod = ExecutionStatusManager.getInstance().getUserParams().getServiceTimeoutPeriod();
            synchronized (TraceParser.getInstance().getPendingOutputTraces()){
                if(!TraceParser.getInstance().getPendingOutputTraces().isEmpty()) {
                    //Get the first value entered into the LinkedHashMap
                    PendingOutputTrace headPendingOutputTrace = TraceParser.getInstance().getPendingOutputTraces().entrySet().iterator().next().getValue();
                    if (headPendingOutputTrace.getCreationTime().getTime() < Calendar.getInstance().getTimeInMillis() - serviceTimeoutPeriod) {
                            if (headPendingOutputTrace.isRootNodeFound()) {
                                OutputTraceWriter.getInstance().addPendingTraceToReadyQueue(headPendingOutputTrace.buildFinishedOutputTrace("null", null));
                            } else {
                                //TODO - log elsewhere
                                System.out.println("Root Node not found.  Considered orphan. " + headPendingOutputTrace.getOutputTrace().getId());
                            }
                        TraceParser.getInstance().getPendingOutputTraces().remove(headPendingOutputTrace.getOutputTrace().getId());
                    }
                }
            }
        }
        System.out.println("FinishedTraceHandler - execution done.");
        ExecutionStatusManager.getInstance().setTraceHandlingFinished(true);
    }

    @Override
    public boolean noMoreWorkToDo() {
        return ExecutionStatusManager.getInstance().isInputFinished() &&
                ExecutionStatusManager.getInstance().isProcessingFinished() &&
                TraceParser.getInstance().getPendingOutputTraces().isEmpty() &&
                TraceParser.getInstance().getTraceStringsToParseQueue().isEmpty();
    }
}
