package cjc.tracebuilder.util;

import cjc.tracebuilder.input.UserParams;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UserParamParser {

    private static Map<String, String> _paramMap = new HashMap<String, String>(){{
        put("-i","setInputType");
        put("-f","setInputFilePath");
        put("-o","setOutputType");
        put("-g","setOutputFilePath");
        put("-n","setNumOfThreads");
        put("-t","setProcessTimeoutPeriod");
        put("-s","setServiceTimeoutPeriod");
    }};

    public static UserParams parseParameters(String[] args){
        UserParams _userParams = new UserParams();
        int argctr = 0;
        while(argctr < args.length){
            String currentArg = args[argctr];
            if(!_paramMap.containsKey(args[argctr])){
                //TODO Concat which field
                System.out.println("Invalid field provided.");
                System.exit(1);
            }
            String currentSetMethod = _paramMap.get(currentArg);
            argctr++;
            setParamValue(_userParams, currentSetMethod, args[argctr]);
            argctr++;
        }
        return _userParams;
    }

    private static void setParamValue(UserParams userParams, String methodname, Object value)  {
        try {
            Method setMethod = UserParams.class.getMethod(methodname, String.class);
            setMethod.invoke(userParams, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }


}
