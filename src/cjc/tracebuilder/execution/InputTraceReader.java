package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.types.InputType;

import java.io.*;
import java.util.Scanner;

/**
 *  A singleton executor that reads each log line the input file provided as user input with the -f parameter (or from STDIN by default)
 *  and inserts each log line into the Queue/LinkedList of log strings to parse.
 */
public class InputTraceReader extends TraceBuilderExecutorBase {
    private static InputTraceReader ourInstance = new InputTraceReader();
    private BufferedReader br;

    /**
     * Obtains our instance of the trace input reader.
     * @return our trace reader instance.
     */
    public static InputTraceReader getInstance() {
        return ourInstance;
    }

    private InputTraceReader() {
        if(ExecutionStatusManager.getInstance().getUserParams().getInputType()==InputType.TEXTFILE){
            br = createBufferedReader();
        }
    }

    /**
     * Begins execution of the trace input reader.  Opens the file specified in the user input parameters with a Buffered reader,
     * then reads each line and places the log string into a parsing Queue (LinkedList), to be parsed by our TraceParser.  If no
     * input file is specified, reads from STDIN.  For STDIN, we close the reader when the user enters "/q".
     */
    @Override
    public void startExecution() {
        //TODO handle system interrupt
        System.out.println("startExecution - Input");
        String currentline;
        boolean isStdIn = ExecutionStatusManager.getInstance().getUserParams().getInputType()==InputType.STDIN;
        if(isStdIn){
            Scanner stdinScanner = new Scanner(System.in);
            System.out.println("Please enter the first log trace: ");
            String stdinLine = stdinScanner.nextLine();
            while(!"/q".equals(stdinLine)){
                TraceParser.getInstance().addStringToParsingQueue(stdinLine);
                System.out.println("Please enter the next log trace: ");
                stdinLine = stdinScanner.nextLine();
            }
            ExecutionStatusManager.getInstance().setStdInReadingFinished(true);
        } else {
            System.out.println("Reading from " + ExecutionStatusManager.getInstance().getUserParams().getInputFilePath());
            br = createBufferedReader();
            try{
                if(br == null){
                    System.out.println("Should not get here.  Input file likely not found, and probably a bug.");
                }
                while ((currentline = br.readLine()) != null) {
                    TraceParser.getInstance().addStringToParsingQueue(currentline);
                }
            }catch(IOException e){
                e.printStackTrace();
            } finally {
                closeReader(br);
            }
        }
        if(!isStdIn){
            System.out.println("Input finished.");
        }
        ExecutionStatusManager.getInstance().setInputFinished(true);

    }

    /**
     * Flag to indicate that our input reader has no more work to do.
     *  - have we read our entire input file provided with the -f parameter (or from STDIN)
     * @return true if our input reader is finished working.
     */
    @Override
    public boolean noMoreWorkToDo() {
        return ExecutionStatusManager.getInstance().isInputFinished();
    }

    private BufferedReader createBufferedReader(){
        if(this.getUserParams().getInputType() == InputType.TEXTFILE){
            try {
                return new BufferedReader(new InputStreamReader((new FileInputStream(
                        this.getUserParams().getInputFilePath()))));
            } catch (FileNotFoundException e) {
                System.out.println("Input file " + ExecutionStatusManager.getInstance().getUserParams().getInputFilePath() + " not found.");
                System.exit(1);
            }
        }
        return null;
    }

    private String readLine(BufferedReader reader) throws IOException{
        return reader.readLine();
    }

    private void closeReader(BufferedReader reader){
        if (reader != null) {
            try {
                System.out.println("Closing reader...");
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadInputTraceRunnable implements Runnable{

        @Override
        public void run() {

        }
    }
}
