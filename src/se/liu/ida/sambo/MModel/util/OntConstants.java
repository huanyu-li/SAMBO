/*
 * Constants.java
 *
 * Created on den 24 augusti 2006, 10:48
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package se.liu.ida.sambo.MModel.util;


import com.objectspace.jgl.Array;

import java.util.StringTokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author hetan
 */
public class OntConstants {
    
    /*the label language
     */
    public static final String lan = "en";
    public static final String sn_lan = "sn";
    
    public static final int ONTOLOGY_INF = -1;
    
    /**
     * constant  value indicating the name of property for part_of
     */
    public static final String part = "part_of";
    
    public static String ALIGN_COMMENT = "[SAMBO - " + java.util.Calendar.getInstance().getTime() + "].\n ";
    
    //the base uri to be used when converting relative URI's to absolute URI's.
    public static final String base = "http://www.ida.liu.se/~iislab/projects/sambo#";
    
    public static final String namespace = "http://www.ida.liu.se/~iislab/projects/sambo";
    
    //Add RDF statements represented in language lang to the model.
    public static final String lang = "RDF/XML-ABBREV";
    
    //The hashtable where stopwords are indexed
    private static Array stopwords = null;
    
    //the stop word file location on the server
    public static String STOPWORD_FILE;

    // the elements with the following namespace are about syntax and schema
    // which is not meaningful for alignment
    public static String EXTERNAL_NAMESPACE =
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#" + ";" +
            "http://www.w3.org/TR/xmlschema-2/#" + ";" +
            "http://www.w3.org/2002/07/owl#" + ";" +
            "http://www.geneontology.org/formats/oboInOwl#" + ";" +
            "http://www.w3.org/2000/01/rdf-schema#" + ";" +
            "http://xmlns.com/foaf/0.1/" + ";" +
            "http://purl.org/dc/elements/1.1/";
        /*
    static String clearname(String input){
        
        char[] chars = input.toLowerCase().toCharArray();
        
        int l = chars.length;
        for (int i = 0; i < l; i++) {
            if(!Character.isLetterOrDigit(chars[i]))
                chars[i] = ' ';
        }
        
    /*    if(stopwords != null){
            StringTokenizer st = new StringTokenizer(new String(chars));                        
            String out ="";
            while(st.hasMoreTokens()){
                
                String token = st.nextToken().trim();
                if(!stopwords.contains(token))
                    out += token + " ";
            }
            
            if(out.length() <= 0)
                return (new String(chars)).trim();
                    
            return out.trim();            
        }*/
        
//        return (new String(chars)).trim();
//    }
//
//    /** Load the stopwords from file to the hashtable where they are indexed. */
//    static void loadStopWords() {
//
//        // Initialize hashtable to proper size given known number of
//        // stopwords in the file and a default 75% load factor with
//        // 10 extra slots for spare room.
//        stopwords = new Array();
//
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(STOPWORD_FILE));
//            String line;
//            while ((line = in.readLine()) != null)
//                stopwords.add(line.trim());
//
//            in.close();
//        } catch (IOException e) {
//            System.out.println("\nCould not load stopwords file: " + STOPWORD_FILE);
//        }
//    }

}
