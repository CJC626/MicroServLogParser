package cjc.tracebuilder.execution;

import cjc.tracebuilder.input.UserParams;
import cjc.tracebuilder.input.types.InputType;

import java.io.*;

public class InputTraceReader extends TraceBuilderExecutorBase {
    private static InputTraceReader ourInstance = new InputTraceReader();

    public static InputTraceReader getInstance() {
        return ourInstance;
    }

    private InputTraceReader() {
    }

    @Override
    public void startExecution() {
        //TODO maybe concat string
        System.out.println("startExecution - Input");
        BufferedReader br = createBufferedReader();
        String currentline;
        while((currentline = readLine(br)) != null){
            TraceParser.getInstance().addStringToParsingQueue(currentline);
        }
        closeReader(br);
        System.out.println("Input finished.");
        ExecutionStatusManager.getInstance().setInputFinished(true);

    }

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
                e.printStackTrace();
            }
        }
        //Default to STDIN
        return new BufferedReader(new InputStreamReader((System.in)));
    }

    private String readLine(BufferedReader reader){
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void closeReader(BufferedReader reader){
        try {
            System.out.println("Closing reader...");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
