package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.output.types.OutputType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An executor singleton that monitors finished output traces ready to be written to our output file (or STDOUT by default).  It opens a BufferedWriter
 * to keep open while it collects traces to write.  It monitors a Queue/LinkedList of traces that are ready to be written to this buffered writer.
 * When there is no more work to do, it closes the buffered writer and marks the executor as finisehd.
 */
public class OutputTraceWriter extends TraceBuilderExecutorBase {
    private static OutputTraceWriter ourInstance = new OutputTraceWriter();
    private Queue<OutputTrace> _tracesReadyToWrite;

    /**
     * Returns the singleton instance of the output trace writer.
     * @return the singleton instance
     */
    public static OutputTraceWriter getInstance() {
        return ourInstance;
    }

    private OutputTraceWriter() {
        _tracesReadyToWrite = new LinkedList<OutputTrace>();
    }

    /**
     *  Begins execution of our output writer, which monitors the queue of ready traces an writes them to our buffered writer.
     */
    @Override
    public void startExecution() {
        boolean isStdIn = ExecutionStatusManager.getInstance().getUserParams().getInputType()== InputType.STDIN;
        boolean isStdOut = ExecutionStatusManager.getInstance().getUserParams().getOutputType()== OutputType.STDOUT;
        //TODO handle system interrupt
        if(!isStdIn) {
            System.out.println("startExecution - Output.");
        }
        BufferedWriter bw = createBufferedWriter();

        try {
            while (!noMoreWorkToDo()) {
                synchronized (this) {
                    // Do we have traces ready to write?
                    if (!_tracesReadyToWrite.isEmpty()) {
                        OutputTrace trace = _tracesReadyToWrite.remove();
                        writeTrace(trace, bw);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        // Create a finally statement to ensure the writer is closed.
        } finally {
            // Output writer is finished.  Close the writer. and mark as finished.
            try {
                if(!isStdOut) {
                    System.out.println("Writing finished.  Traces written to " + ExecutionStatusManager.getInstance().getUserParams().getOutputFilePath() + ".  Closing writer.");
                }
                if(bw != null){
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ExecutionStatusManager.getInstance().setOutputFinished(true);
    }

    /**
     * Flag to indicate that the output writer is finished with its work.
     *  - The input writer is finished
     *  - The trace parser is finished
     *  - The pending output traces Queue/LinkedHashMap is empty
     *  - The queue of traces ready for writing is empty
     * @return true if work for the output writer is finished
     */
    @Override
    public boolean noMoreWorkToDo() {
        return ExecutionStatusManager.getInstance().isInputFinished() &&
                ExecutionStatusManager.getInstance().isProcessingFinished() &&
                TraceParser.getInstance().getPendingOutputTraces().isEmpty() &&
                _tracesReadyToWrite.isEmpty();
    }

    /**
     * Adds a trace ready to be written to output to the ready queue
     * @param trace output trace to add to the ready-to-write queue
     * @return true if output trace was added successfully
     */
    public synchronized boolean addPendingTraceToReadyQueue(OutputTrace trace){
            return _tracesReadyToWrite.add(trace);
    }

    private BufferedWriter createBufferedWriter(){
        if(this.getUserParams().getOutputType() == OutputType.TEXTFILE){
            try {
                return new BufferedWriter(new FileWriter(
                        this.getUserParams().getOutputFilePath()));
            } catch (IOException e) {
                //TODO inidicate that provided path is bad?
                e.printStackTrace();
            }
        }
        //Default to STDOUT
        return new BufferedWriter(new PrintWriter(System.out));
    }

    private void writeTrace(OutputTrace trace, BufferedWriter writer) throws IOException {
            ObjectMapper om = new ObjectMapper();
            om.setDateFormat(new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss.SSS'Z'"));
            writer.write(om.writeValueAsString(trace) + "\n");
    }

}
