package cjc.tracebuilder.execution;

import cjc.tracebuilder.execution.pending.PendingOutputTrace;
import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.output.types.OutputType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.trace.Trace;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OutputTraceWriter extends TraceBuilderExecutorBase {
    private static OutputTraceWriter ourInstance = new OutputTraceWriter();
    private Queue<OutputTrace> _tracesReadyToWrite;

    public static OutputTraceWriter getInstance() {
        return ourInstance;
    }

    private OutputTraceWriter() {
        _tracesReadyToWrite = new LinkedList<OutputTrace>();
    }

    @Override
    public void startExecution() {
        System.out.println("startExecution - Output.");
        BufferedWriter bw = createBufferedWriter();

        while(!noMoreWorkToDo()){
            synchronized (this) {
                if (!_tracesReadyToWrite.isEmpty()) {
                    OutputTrace trace = _tracesReadyToWrite.remove();
                    writeTrace(trace, bw);
                }
            }
        }

        try {
            System.out.println("Writing finished.  Closing buffer.");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExecutionStatusManager.getInstance().setOutputFinished(true);
    }

    @Override
    public boolean noMoreWorkToDo() {
        return ExecutionStatusManager.getInstance().isInputFinished() &&
                ExecutionStatusManager.getInstance().isProcessingFinished() &&
                TraceParser.getInstance().getPendingOutputTraces().isEmpty() &&
                _tracesReadyToWrite.isEmpty();
    }

    public boolean addPendingTraceToReadyQueue(OutputTrace trace){
        synchronized (this){
            return _tracesReadyToWrite.add(trace);
        }
    }

    private BufferedWriter createBufferedWriter(){
        if(this.getUserParams().getOutputType() == OutputType.TEXTFILE){
            try {
                return new BufferedWriter(new FileWriter(
                        this.getUserParams().getOutputFilePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Default to STDOUT
        return new BufferedWriter(new PrintWriter(System.out));
    }

    private void writeTrace(OutputTrace trace, BufferedWriter writer){
        try {
            //TODO maybe make newline char constant
            ObjectMapper om = new ObjectMapper();
            om.setDateFormat(new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss.SSS'Z'"));
            writer.write(om.writeValueAsString(trace) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
