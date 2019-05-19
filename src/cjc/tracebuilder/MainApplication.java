package cjc.tracebuilder;

import cjc.tracebuilder.execution.*;
import cjc.tracebuilder.input.InputTrace;
import cjc.tracebuilder.util.UserParamParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.*;

/**
 *
 */
public class MainApplication {

    private static boolean _inputFinished = false;
    private static boolean _parsingFinished = false;
    private static boolean _outputFinished = false;
    private static boolean _traceHandlingFinished = false;

    /**
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("Started " + (new SimpleDateFormat("hh:mm:ss")).format(Calendar.getInstance().getTime()));

        int exitcode = 0;
        //TODO test multi threading
        ExecutionStatusManager manager = ExecutionStatusManager.getInstance();
        manager.setUserParams(UserParamParser.parseParameters(args));

        ExecutorService inputTraceReaderService = Executors.newSingleThreadExecutor();
        inputTraceReaderService.execute(new ExecutorRunnable(InputTraceReader.getInstance()));

        ExecutorService traceParserReaderService = Executors.newSingleThreadExecutor();
        traceParserReaderService.execute(new ExecutorRunnable(TraceParser.getInstance()));

        ExecutorService outputWriterService = Executors.newSingleThreadExecutor();
        outputWriterService.execute(new ExecutorRunnable(OutputTraceWriter.getInstance()));

        ExecutorService finishedTraceHandlerService = Executors.newSingleThreadExecutor();
        finishedTraceHandlerService.execute(new ExecutorRunnable(FinishedTraceHandler.getInstance()));

        while(stillHaveWorkToDo(manager)){
            if(manager.isInputFinished() && !inputTraceReaderService.isShutdown()){
                inputTraceReaderService.shutdown();
                _inputFinished = true;
            }
            if(manager.isProcessingFinished() && !traceParserReaderService.isShutdown()){
                traceParserReaderService.shutdown();
                _parsingFinished = true;
            }
            if(manager.isOutputFinished() && !outputWriterService.isShutdown()){
                outputWriterService.shutdown();
                _outputFinished = true;
            }
            if(manager.isTraceHandlingFinished() && !finishedTraceHandlerService.isShutdown()){
                finishedTraceHandlerService.shutdown();
                _traceHandlingFinished = true;
            }
        }
        System.out.println("All procesing complete.");
        System.out.println("Ended "+ (new SimpleDateFormat("hh:mm:ss")).format(Calendar.getInstance().getTime()));
        System.exit(exitcode);

    }

    //TODO move to its own class
    private static class ExecutorRunnable implements Runnable{

        private ITraceBuilderExecutor _executor;

        public ExecutorRunnable(ITraceBuilderExecutor executor){
            _executor = executor;
        }

        @Override
        public void run() {
            _executor.startExecution();
        }
    }

    private static boolean stillHaveWorkToDo(ExecutionStatusManager manager){
            return !_inputFinished ||
                    !_outputFinished ||
                    !_parsingFinished ||
                    !_traceHandlingFinished;
    }

}
