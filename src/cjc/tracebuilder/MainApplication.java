package cjc.tracebuilder;

import cjc.tracebuilder.execution.*;
import cjc.tracebuilder.input.types.InputType;
import cjc.tracebuilder.util.UserParamParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.*;

/**
 *  The main executable class for the TraceBuilder application.  This class's main method is executed when run as a jar file.
 */
public class MainApplication {

    private static boolean _inputFinished = false;
    private static boolean _parsingFinished = false;
    private static boolean _outputFinished = false;
    private static boolean _traceHandlingFinished = false;

    private static boolean expectedShutdown = false;

    //TODO make singletons?
    private static ExecutionStatusManager manager;
    private static ExecutorService inputTraceReaderService;
    private static ExecutorService traceParserReaderService;
    private static ExecutorService outputWriterService;
    private static ExecutorService finishedTraceHandlerService;

    /**
     * The main method executed from java -jar <i>thisJarFile.jar</i>
     * @param args the application's arguments (normally provided from the user console).
     */
    public static void main(String[] args) {

        System.out.println("Started " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance().getTime()));

        addShutdownHook();
        int exitcode = 0;
        //TODO test multi threading
        manager = ExecutionStatusManager.getInstance();
        manager.setUserParams(UserParamParser.parseParameters(args));
        boolean isStdIn = manager.getUserParams().getInputType()==InputType.STDIN;

        inputTraceReaderService = Executors.newSingleThreadExecutor();
        inputTraceReaderService.execute(new ExecutorRunnable(InputTraceReader.getInstance()));

        traceParserReaderService = Executors.newSingleThreadExecutor();
        traceParserReaderService.execute(new ExecutorRunnable(TraceParser.getInstance()));

        outputWriterService = Executors.newSingleThreadExecutor();
        outputWriterService.execute(new ExecutorRunnable(OutputTraceWriter.getInstance()));

        finishedTraceHandlerService = Executors.newSingleThreadExecutor();
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
        if(!isStdIn) {
            System.out.println("All procesing complete.");
            System.out.println("Ended " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(Calendar.getInstance().getTime()));
        }
        expectedShutdown = true;
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

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                if(!expectedShutdown){
                    System.out.println("Unexpected Interruption.  Shutting down all executors.");
                }
                if(inputTraceReaderService != null && !inputTraceReaderService.isShutdown()){inputTraceReaderService.shutdown();}
                if(traceParserReaderService != null && !traceParserReaderService.isShutdown()){traceParserReaderService.shutdown();}
                if(finishedTraceHandlerService != null && !finishedTraceHandlerService.isShutdown()){finishedTraceHandlerService.shutdown();}
                if(outputWriterService != null && !outputWriterService.isShutdown()){outputWriterService.shutdown();}
            }
        });
    }

}
