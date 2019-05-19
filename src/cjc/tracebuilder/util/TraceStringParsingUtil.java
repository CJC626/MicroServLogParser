package cjc.tracebuilder.util;

import cjc.tracebuilder.execution.pending.PendingServiceCall;
import cjc.tracebuilder.output.OutputTrace;
import cjc.tracebuilder.output.ServiceCall;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TraceStringParsingUtil {

    private static final List<String> VALID_DATE_FORMATS = Arrays.asList("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'","yyyy-MM-dd'T'hh:mm:ss.SS'Z'","yyyy-MM-dd'T'hh:mm:ss.S'Z'","yyyy-MM-dd'T'hh:mm:ss'Z'");

    public static OutputTrace parseStringToOutputTrace(String[] splitTraceString){
        OutputTrace outputTrace = new OutputTrace();
        outputTrace.setId(splitTraceString[2]);
        return outputTrace;
    }

    public static PendingServiceCall parseStringtToPendingServiceCall(String[] splitTraceString){
        //TODO - maybe print out what makes the line invalid
        if(splitTraceString.length != 5){
            return null;
        }
        Date startDate = attemptToParseDate(splitTraceString[0]);
        if(startDate==null){
            return null;
        }
        Date endDate = attemptToParseDate(splitTraceString[1]);
        if(endDate==null || !splitTraceString[4].contains("->")){
            //Invalid Date or svc->svc fields.
            return null;
        }
        PendingServiceCall call = new PendingServiceCall();
        call.setStartDate(startDate);
        call.setEndDate(endDate);
        call.setServiceName(splitTraceString[3]);
        call.setOriginAndDestinationFromSplitString(splitTraceString[4].split("->"));
        return call;
    }

    private static Date attemptToParseDate(String dateString){
        DateFormat df;
        Date d;
        for(String dfs: VALID_DATE_FORMATS){
            try{
                df = new SimpleDateFormat(dfs);
                d = df.parse(dateString);
                return d;
            } catch (ParseException e) {
                //Try next date format
            }
        }
        return null;
    }

}
