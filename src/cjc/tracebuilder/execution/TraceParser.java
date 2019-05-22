package cjc.tracebuilder.execution;

import cjc.tracebuilder.execution.pending.PendingOutputTrace;
import cjc.tracebuilder.execution.pending.PendingServiceCall;
import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.util.TraceStringParsingUtil;

import java.util.*;
import java.util.concurrent.*;


/**
 *  A singleton that monitors a Queue (LinkedList) of trace strings obtained from the user defined input file (with -f, or STDIN by default).
 *  Begins creating pending output traces.  A "pending output trace" is a temporary object that collects all service calls
 *  recorded on a single trace ID.  It keeps this trace in memory until a certain time threshold (configured by the user)
 *  after creation of the pending output trace is reached.  Then the application assumes all service calls have been collected
 *  and will begin building the final trace (to be written as JSON to the user specified output file - or STDOUT by default).
 */
public class TraceParser extends TraceBuilderExecutorBase {
    private static TraceParser ourInstance = new TraceParser();

    private ExecutorService _executor;
    private Queue<String> _traceStringsToParse;
    private LinkedHashMap<String, PendingOutputTrace> _pendingOutputTraces;
    private boolean _isStdIn;

    /**
     * Obtains the instance of the trace parser.
     * @return Obtains the instance of the trace parser.
     */
    public static TraceParser getInstance() {
        return ourInstance;
    }

    private TraceParser() {
        _executor = Executors.newFixedThreadPool(this.getUserParams().getNumOfThreads());
        _traceStringsToParse = new LinkedList<String>();
        _pendingOutputTraces = new LinkedHashMap<String, PendingOutputTrace>();
    }

    /**
     * Obtains the LinkedHashMap of pending output traces.
     * @return
     */
    public LinkedHashMap<String, PendingOutputTrace> getPendingOutputTraces(){
        return _pendingOutputTraces;
    }

    /**
     * Adds a log line string read from the user provided input to the queue of strings to parse.
     * @param str the string to add to the queue.
     * @return true if the string was successfully added to the queue.
     */
    public boolean addStringToParsingQueue(String str){
        synchronized (_traceStringsToParse) {
            return _traceStringsToParse.add(str);
        }
    }

    /**
     * Obtians the queue of log strings to be parsed.
     * @return the queue of log strings to be parsed
     */
    public Queue<String> getTraceStringsToParseQueue(){
            return _traceStringsToParse;
    }

    /**
     * Begins execution of the trace parser.  Creates a pool of threads, where the thread count is
     * defined in user input (-n parameter) or Number of processers defined by:
     *
     *  Runtime.getRuntime().availableProcessors() / 2
     *
     *  Each thread submits a trace parser worker that reads from the queue of strings to be parsed.
     *  When it obtains a string, it parses the string into a pending service call and looks to see
     *  if its trace ID has an entry in the pending output trace queue.  If so, it add the pending service call
     *  to that pending trace.  If not, it creates a new pending trace.  It also lets the existing (or new)
     *  pending trace know if the root node has been found (if "null".equals(span)).  When work is finished,
     *  The Java executioner shuts down all threads.
     */
    @Override
    public void startExecution() {
        _isStdIn = ExecutionStatusManager.getInstance().getUserParams().getInputType()== InputType.STDIN;
        if(!_isStdIn) {
            System.out.println("startExecution - Processing");
        }
        int tc = 0;
        //Create thread pool for trace parser workers.
        while(tc < ExecutionStatusManager.getInstance().getUserParams().getNumOfThreads()){
            _executor.submit(new ParseTraceRunnable());
            tc++;
        }
        while(!noMoreWorkToDo()){
            //nothing
        }
        //All work complete.  Shut down parser and workers.
        shutDownParser();
        ExecutionStatusManager.getInstance().setProcessingFinished(true);
    }

    /**
     * Flag that indicates the trace parser is finished working.
     *  - The input reader has finished working
     *  - The queue of trace strings to parse is empty
     * @return true if the trace parser is finished working
     */
    @Override
    public boolean noMoreWorkToDo() {
        synchronized (_traceStringsToParse) {
            return ExecutionStatusManager.getInstance().isInputFinished() &&
                    _traceStringsToParse.isEmpty();
        }
    }

    /**
     *  Shuts down all worker threads in the trace parser.
     */
    private void shutDownParser(){
        if(!_isStdIn) {
            System.out.println("Shutting down Parser.");
        }
        _executor.shutdown();
        try {
            //TODO possibly make configurable?
            _executor.awaitTermination(30000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ParseTraceRunnable implements Runnable{

        private long itemCt = 0;

        /**
         *  Executes string parsing as a worker thread from the queue of parsed log strings.  Reads from the
         *  head of the queue of trace strings to parse, parses the obtained string, and adds the generated
         *  pending service call to the queue of pending output traces (or creates a new pending output trace
         *  if one does not exist for the provided trace ID).
         */
        @Override
        public void run() {
            if(!_isStdIn) {
                System.out.println("Running TraceParser worker. Name " + Thread.currentThread().getName());
            }
            while(!noMoreWorkToDo()){
                String traceString;
                String[] splitTraceString = null;
                /**
                 *  Do we have trace strings to parse?  Adding synchronized block since input reader is writing to it in parallel.
                 *  Was seeing NoSuchElementExceptions earlier, likely due to sync issues which appear to be resolved with this
                 *  synchonized block.
                 */
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
                    } else {
                        continue;
                    }
                }
                PendingServiceCall newServiceCall = TraceStringParsingUtil.parseStringtToPendingServiceCall(splitTraceString);
                if(newServiceCall==null){
                    //Log entry had an invalid format.  Tolerate and continue.
                    //TODO - maybe log number of invalid lines to STDERR or user input file.  Maybe -e?
                    continue;
                }
                PendingOutputTrace currentTrace;
                /**
                 *  Adding synchronized block to obtain lock on _pendingOutputTraces, so each executor accessing it
                 *  obtains it in its actual state.
                 */
                synchronized (_pendingOutputTraces) {
                    if (_pendingOutputTraces.containsKey(splitTraceString[2])) {
                        currentTrace = _pendingOutputTraces.get(splitTraceString[2]);
                    } else {
                        PendingOutputTrace newOutputTrace = new PendingOutputTrace();
                        Date creationTime = Calendar.getInstance().getTime();
                        newOutputTrace.setCreationTime(creationTime);
                        newOutputTrace.setOutputTrace(TraceStringParsingUtil.parseStringToOutputTrace(splitTraceString));
                        currentTrace = newOutputTrace;
                        _pendingOutputTraces.put(splitTraceString[2], newOutputTrace);
                    }
                    if ("null".equals(newServiceCall.getOrigin())) {
                        currentTrace.setRootNodeFound(true);
                    }
                    currentTrace.addToCollectedServiceCalls(newServiceCall);
                } //TODO maybe move up a level to release sync lock sooner
            }
            //TODO Possibly write # of log lines processed in ExecutionStatusManager
            if(!_isStdIn) {
                System.out.println(Thread.currentThread().getName() + "-no more items to process.  Processed " + itemCt);
            }
        }
    }
}
