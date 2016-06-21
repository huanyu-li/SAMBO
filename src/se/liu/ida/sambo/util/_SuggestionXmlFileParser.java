/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class _SuggestionXmlFileParser {

    private static final Logger logger = Logger.getLogger(SuggestionXmlFileParser.class.getName());

    public static void main(String args[]) {
        Set<String> sugs = SuggestionXmlFileParser.getSuggestions("E://My Work//WorkTrack//2010-11-01 MasterThesis - Shahab//project//SAMBO-WebApp//build//web//sessions//Suggestions//tester_SuggestionList0.xml");
        for(String sug : sugs){
            System.out.println(sug);
        }
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
}
