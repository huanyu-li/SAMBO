/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.GoogleUrlTable;

/**
 * <p>
 * To query google search engine for computing similarity value.
 * <p>
 * @author Rajaram
 * @version 1.0
 */
public class GoogleSearch extends Matcher {
    /**
     * Put your website here
     */ 
    private final String HTTP_REFERER = "http://www.example.com/";
    /**
     * To store URLs in the local database. 
     */
    private GoogleUrlTable urlDB = new GoogleUrlTable();

    /**
     * Query google search engine for the given term.
     * 
     * @param term      Term
     * @return  List of URLs for the given term.
     */
    private JSONArray makeQuery(String term) {
        System.out.println("\nQuerying for : " + term);
        JSONArray ja=null;
        
        try {
            // Convert spaces to +, etc. to make a valid URL
            term = URLEncoder.encode(term, "UTF-8");
            URL url = new URL("http://ajax.googleapis.com/ajax/services/search"
                    + "/web?start=0&rsz=large&v=1.0&q=" + term);
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer", HTTP_REFERER);

            // Get the JSON response
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            String response = builder.toString();
            JSONObject json = new JSONObject(response);


            try {
                System.out.println("Total results = " +
                        json.getJSONObject("responseData").
                        getJSONObject("cursor")
                        .getString("estimatedResultCount"));
            } catch(Exception e) {
                System.out.println("Error occur at googling....... ");
                json=null;
            }
               
            if(json!=null) {
                ja = json.getJSONObject("responseData").getJSONArray("results");
            }
            
            }catch (Exception e) {
        System.err.println("Something went wrong...");
        e.printStackTrace();    
            }
        return ja;
    }
    
    /**
     * To create delay.
     * 
     * @param i 
     */
    private void delayLine(int i) {
            try {
                Thread.sleep(i);
            } catch (InterruptedException ex) {
                Logger.getLogger(GoogleSearch.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
    }
    
    /**
     * Query google search engine for urls.
     * 
     * @param term
     * @return
     * @throws JSONException 
     */
    private ArrayList<String> getURLs(String term) throws JSONException {
        
        ArrayList<String> urls = urlDB.selectUrls(addSlashes(term));
        int countError = 0;
        
        if(!urls.isEmpty()) {
            return urls;
        }
        else {
              
            JSONArray jArray = makeQuery(term);            
            String urlGroup = "";
            
            while (jArray == null && countError < 2) {
                
                int delay = 90000;                
                float d = delay, min = 60000;
                
                System.out.println("Google time out wait for "
                        + ""+(d/min)+" min(s)");
                delayLine(delay);  
                System.out.println("\n\nQuerying restarted:");
                countError++;
                jArray = makeQuery(term);
            }
            
            if (jArray != null) {
                
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject j = jArray.getJSONObject(i);			
                    urls.add(j.getString("url"));
                }
                
                for(String url:urls) {
                    urlGroup = urlGroup.concat(url);
                    urlGroup = urlGroup.concat("#");
                }
                
                if (!urlGroup.isEmpty() && urlGroup.charAt
                        ((urlGroup.length()-1))=='#') {
                     // To remove last #
                    urlGroup = urlGroup.substring(0, (urlGroup.length()-1));
                }
            }
            
            term = addSlashes(term);
            urlGroup = addSlashes(urlGroup);
            urlDB.insertUrls(term, urlGroup);
        }
        return urls;
    }
    
    /**
     * To add slash to the strings to be stored in the database.
     * 
     * @param str
     * @return 
     */
    private String addSlashes(String str){
        if(str==null) {
            return "";
        }
        StringBuffer s = new StringBuffer ((String) str);
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt (i) == '\'') {
                s.insert (i++, '\\');
            }
        }
        return s.toString();
    }
    
    /**
     * 
     * Calculate the similarity value between any two strings.
     * 
     * @param term1     The first string.
     * @param term2     The second string.
     *
     * @return          similarity value between two given strings.
     */
    public double getSimValue(String term1, String term2) {
        
        try {
            ArrayList<String> term1Urls = getURLs(term1);            
            ArrayList<String> term2Urls = getURLs(term2);
            
            double sim1 = 0;
            double sim2 = 0;
            double commonLinks = 0;
            
            for(String url:term1Urls) {
                if(term2Urls.contains(url)) {
                    commonLinks++;
                }
            }
            
            sim1 = commonLinks/term1Urls.size();
            
            commonLinks = 0;
            
            for(String url:term2Urls) {
                if(term1Urls.contains(url)) {
                    commonLinks++;
                }
            }
            
            sim2 = commonLinks/term2Urls.size();
            
            if (sim1 > sim2) {
                return sim1;
            }
            else {
                return sim2;
            }
            
        } catch (JSONException ex) {
            Logger.getLogger(GoogleSearch.class.getName()).
                    log(Level.SEVERE, null, ex);
        }        
        return 0;
    }
}
