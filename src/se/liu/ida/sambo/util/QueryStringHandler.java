package se.liu.ida.sambo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Shahab
 */
public class QueryStringHandler {

    public static String ParseSessionId(String query)
    {
        String sid = "";
        try
        {
            Map<String, String> map = getQueryMap(query);
            Set<String> keys = map.keySet();
            for (String key : keys)
            {
                if(key.equals("sid"))
                    sid = map.get(key);
            }
        }
        catch(Exception _ex)
        {
            _ex.printStackTrace();
        }
        
        return sid;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        String value = "";
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String name = param.split("=")[0];
            if(param.length() > 4)
                value = param.split("=")[1];
            else
                value = "";
            map.put(name, value);
        }
        return map;
    }
}
