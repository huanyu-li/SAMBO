/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.liu.ida.sambo.session.Commons;

/**
 * @author Qiang Liu
 * Created on Aug 11, 2011, 1:46:18 PM
 */

/* Here is an example of suggestion xml file 

<?xml version="1.0" encoding="UTF-8"?><Suggestions>
<Pair>[[class:MESH_A.09.371.894.513,1], [class:MA_0000273,2], 0.0]</Pair>
<Pair>[[class:MESH_A.09.371.894.513.780,1], [class:MA_0001292,2], 0.0]</Pair>
</Suggestions>
 */
public class SuggestionXmlFileParser {

    private static final Logger logger = Logger.getLogger(SuggestionXmlFileParser.class.getName());

    public static void main(String args[]) {
        //Set<String> sugs = SuggestionXmlFileParser.getSuggestions("E://My Work//WorkTrack//2010-11-01 MasterThesis - Shahab//project//SAMBO-WebApp//build//web//sessions//Suggestions//tester_SuggestionList0.xml");
        //Set<String> sugs = SuggestionXmlFileParser.getSuggestions("D://SAMBO-WebApp//SAMBO-WebApp//build//web//sessions//tester_SuggestionList.xml");
        /*for(String sug : sugs){
            System.out.println(sug);
        }*/

        // I have to read xml file in string and pass it to getProcessedSuggestions to get the list of suggestions.

        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ProcessedSuggestions><Suggestion  pair=\"[[class:MA_0000262,1], [class:MESH_A.09.371.060.067,2], 1.0]\" name=\"\" num=\"0\" comment=\"\" action=\"2\"/><Suggestion  pair=\"[[class:MA_0000262,1], [class:MESH_A.09.371.060.067,2], 1.0]\" name=\"\" num=\"0\" comment=\"\" action=\"2\"/></ProcessedSuggestions>";

        //String str="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Suggestions><Pair>[[class:MA_0000264,1], [class:MESH_A.09.371.894.280,2], 1.0]</Pair><Pair>[[class:MA_0000265,1], [class:MESH_A.09.371.192,2], 1.0]</Pair>";
        Vector sugs = SuggestionXmlFileParser.getProcessedSuggestions(str);

        // List of suggestions
        Iterator itr_sugg = sugs.iterator();
        while(itr_sugg.hasNext())
        {
            System.out.print(itr_sugg.next().toString());
        }

       //Pattern datePattern = Pattern.compile("<Suggestion  pair=\"\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]\" name=\"\" num=\"0\" comment=\"\" action=\"1|2\"/>");
        //Matcher matcher = datePattern.matcher("<Suggestion  pair=\"[[class:MA_0000263,1], [class:MESH_A.09.371.894.223,2], 1.0]\" name=\"\" num=\"0\" comment=\"\" action=\"2\"/>");

       /*if(matcher.find())
            System.out.println(matcher.group(1).trim() + " == " + matcher.group(2).trim());
        
        */
        
    }

    public static Set<String> getSuggestions(String xmlfile) {
        BufferedReader reader = null;
        try {
            Set<String> suggestions = new HashSet<String>();
            reader = new BufferedReader(new FileReader(xmlfile));
            Pattern datePattern = Pattern.compile("<Pair>\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]</Pair>");
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = datePattern.matcher(line);
                if (line != null && matcher.find()) {
                    suggestions.add(matcher.group(1).trim() + " == " + matcher.group(2).trim());
                }
            }
            return suggestions;
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    // This Function gets processed suggestion in particular only matched suggestion with equilivent relation
    public static Vector getProcessedSuggestions(String processedSuggestionInput) {
        Vector suggestions = new Vector();
        int counter = 0;
        try
        {
            //String patternStr = "</Pair>";
            String patternStr = "/>";
            String[] lines = processedSuggestionInput.split(patternStr);
           
            for(String line : lines)
            {
               Pattern datePattern = Pattern.compile("<Suggestion  pair=\"\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]\" name=\"(.*)\" num=\"0\" comment=\"(.*)\" action=\"1|2\"");
               //Pattern datePattern = Pattern.compile("\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]");
               //Pattern datePattern = Pattern.compile("<Pair>\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]");
               if(counter == 0)
                   line = line.substring(60);
                   //line = line.substring(51);
               Matcher matcher = datePattern.matcher(line);
               if(matcher.find())
               {
                   line = line.substring(18,line.lastIndexOf("name")).trim();
                   Pattern datePattern1 = Pattern.compile("\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]");
                   Matcher matcher1 = datePattern1.matcher(line);
                   if(matcher1.find())
                   {
                        //suggestions.add(matcher1.group(1).trim()+ " == " + matcher1.group(2).trim());

                       suggestions.add(new Pair(Commons.monto1.getClass(matcher1.group(1).trim()),Commons.monto2.getClass(matcher1.group(2).trim())));
                   }
               }
               counter++;
            }
        }
        catch(Exception _e)
        {
            _e.printStackTrace();
        }
        return suggestions;
    }
}

