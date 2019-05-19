package cjc.tracebuilder.execution;

import cjc.tracebuilder.execution.pending.PendingOutputTrace;
import cjc.tracebuilder.execution.pending.PendingServiceCall;
import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.util.TraceStringParsingUtil;

import java.util.*;
import java.util.concurrent.*;

public class TraceParser extends TraceBuilderExecutorBase {
    private static TraceParser ourInstance = new TraceParser();

    private ExecutorService _executor;
    private Queue<String> _traceStringsToParse;
    private LinkedHashMap<String, PendingOutputTrace> _pendingOutputTraces;

    public static TraceParser getInstance() {
        return ourInstance;
    }

    private TraceParser() {
        _executor = Executors.newFixedThreadPool(this.getUserParams().getNumOfThreads());
        _traceStringsToParse = new LinkedList<String>();
        _pendingOutputTraces = new LinkedHashMap<String, PendingOutputTrace>();
    }

    public LinkedHashMap<String, PendingOutputTrace> getPendingOutputTraces(){
        return _pendingOutputTraces;
    }

    public PendingOutputTrace removePendingOutputTrace(String traceId){
        synchronized (this) {
            return _pendingOutputTraces.remove(traceId);
        }
    }

    public boolean addStringToParsingQueue(String str){
        synchronized (_traceStringsToParse) {
            return _traceStringsToParse.add(str);
        }
    }

    public Queue<String> getTraceStringsToParseQueue(){
        //synchronized (this) {
            return _traceStringsToParse;
        //}
    }

    @Override
    public void startExecution() {
        System.out.println("startExecution - Processing");
        int tc = 0;
        while(tc < ExecutionStatusManager.getInstance().getUserParams().getNumOfThreads()){
            _executor.submit(new ParseTraceRunnable());
            //new ParseTraceRunnable().run();
            tc++;
        }
        while(!noMoreWorkToDo()){
            //nothing
        }
        shutDownParser();
        ExecutionStatusManager.getInstance().setProcessingFinished(true);
    }

    @Override
    public boolean noMoreWorkToDo() {
        synchronized (_traceStringsToParse) {
            return ExecutionStatusManager.getInstance().isInputFinished() &&
                    _traceStringsToParse.isEmpty();
        }
    }

    private void shutDownParser(){
        System.out.println("Shutting down Parser.");
        _executor.shutdown();
        try {
            _executor.awaitTermination(30000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ParseTraceRunnable implements Runnable{

        private int itemCt = 0;


        @Override
        public void run() {
            System.out.println("Running TraceParser worker. Name " + Thread.currentThread().getName());
            //TODO - create app status class to know when to stop thread
            while(!noMoreWorkToDo()){
                String traceString;
                String[] splitTraceString = null;
                synchronized (_traceStringsToParse) {
                    if (!_traceStringsToParse.isEmpty()) {
                        try {
                            traceString = _traceStringsToParse.remove();
                            splitTraceString = traceString.split(" ");
                            itemCt++;
                        } catch (NoSuchElementException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
                PendingServiceCall newServiceCall = TraceStringParsingUtil.parseStringtToPendingServiceCall(splitTraceString);
                if(newServiceCall==null){
                    System.out.println("Invalid Line Provided");
                    continue;
                }
                PendingOutputTrace currentTrace;
                //TODO SYNCHRONIZED BECAUSE...
                synchronized (_pendingOutputTraces) {
                    if (_pendingOutputTraces.containsKey(splitTraceString[2])) {
                        currentTrace = _pendingOutputTraces.get(splitTraceString[2]);
                        currentTrace.setDateLastNodeAdded(Calendar.getInstance().getTime());
                    } else {
                        PendingOutputTrace newOutputTrace = new PendingOutputTrace();
                        Date creationTime = Calendar.getInstance().getTime();
                        newOutputTrace.setCreationTime(creationTime);
                        newOutputTrace.setDateLastNodeAdded(creationTime);
                        newOutputTrace.setOutputTrace(TraceStringParsingUtil.parseStringToOutputTrace(splitTraceString));
                        currentTrace = newOutputTrace;
                        _pendingOutputTraces.put(splitTraceString[2], newOutputTrace);
                    }
                    if (newServiceCall.getOrigin().equals("null")) {
                        currentTrace.setRootNodeFound(true);
                    }
                    currentTrace.addToCollectedServiceCalls(newServiceCall);
                }
                    //TODO LOOK HERE FOR SYNC ISSUES
            }
            System.out.println(Thread.currentThread().getName() + "-no more items to process.  Processed " + itemCt);
        }
    }
}
