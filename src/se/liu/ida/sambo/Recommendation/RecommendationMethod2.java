/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Recommendation;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.segPairSelAlgs.SubG;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.util.Pair;

/**
 *<p>
 * To compute recommendation 2 , the one which uses segment pairs and decision 
 * on mapping suggestions as an oracle.
 * </p>
 * @author Rajaram
 * @version 1.0
 */
public class RecommendationMethod2 {
    /**
     * URL of ontology 1.
     */
    private URL ontology1;
    /**
     * URL of ontology 2.
     */
    private URL ontology2;
    /**
     * generated segment pairs.
     */
    private int noOfSegmentPairs = 0;
    /**
     * Number of generated segment pairs.
     */
    private int noOfSegmentPairsGen = 0;
    /**
     * Path for segment pairs location.
     */
    private String segmentLocation = "";    
    /**
     * Ontology model manager.
     */ 
    private OntManager ontManager;    
    /**
     * 
     */
    private MergeManager merge;    
    /**
     * Single SQL server connection, will be used for the entire recommendation
     * process.
     */
    private Connection sqlConn = makeConnection(); 
    /**
     * Accepted mapping suggestions.
     */
    private ArrayList<String> acceptedMappingSugg = new ArrayList<String> ();
    /**
     * Rejected mapping suggestions.
     */
    private ArrayList<String> rejectedMappingSugg = new ArrayList<String> ();
    /**
     * 
     * @param ont1
     * @param ont2
     * @param noSegPair
     * @param segLoc
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public RecommendationMethod2(URL ont1, URL ont2, int noSegPair, 
            String segLoc) throws FileNotFoundException, IOException {
        
        ontology1 = ont1;
        ontology2 = ont2;
        noOfSegmentPairs = noSegPair;
        segmentLocation = segLoc;        
        ontManager = new OntManager();
        merge = new MergeManager(ontManager);
        /**
         * This SQL connection will be used for the entire recommendation 
         * process.
         */ 
        RecommendationConstants.SQL_CONN = sqlConn; 
    }

    /**
     * Create new SQL server connection.
     * 
     * @return  SQL server connection. 
     */
    private Connection makeConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationMethod1.class.getName()
                    ).log(Level.SEVERE, null, ex);
        }  
        
        return conn;
    }  

    /**
     * Generate segment pairs for the two given ontologies.
     * 
     * @return numofSegmPairs   Number of segment pairs generated.
     */
    public int generateSegmentPairs() {
        System.out.println("Goes to the segment location :" + segmentLocation);
        int numofSegmPairs = 0;
        File dir = new File(segmentLocation);
        if (dir.isDirectory()) {
            // Delete existing segment pairs.
            this.cleanDirectory(dir);
            SubG sg = new SubG(ontology1.toString(), ontology2.toString(),
                    noOfSegmentPairs);
            numofSegmPairs = sg.run(segmentLocation);
            noOfSegmentPairsGen = numofSegmPairs;
        }
        return numofSegmPairs;
    }
    
    /**
     * This method return parameters A, B, C, D for an input alignment strategy.
     * 
     * @param matchers      Matchers in an alignment strategy.
     * @param weights       Weight for matchers.
     * @param combination   Combination type(weighted/maximum)
     * @param thresholds    Thresholds in an alignment strategy.
     * 
     * @return scoreParam   Which can be use to calculate the recommendation 
     *                      score.
     * 
     * @throws MalformedURLException
     * @throws IOException
     */
    public double[] getParams(String matchers, String weights, 
            String combination, String thresholds) throws MalformedURLException, 
            IOException {
        
        /**
         * Parameters to calculate distance.
         *
         *           |      real
         *           | 
         *           |  cor   wrg    
         *       ----|-------------
         *  a   cor  |   A     B
         *  l        |
         *  g   wrg  |   C     D  
         */       
        double paramA = 0;
        double paramB = 0;
        double paramC = 0;
        double paramD = 0;   
        double paramE = 0;
        Vector suggestionVector = new Vector();        
        boolean isSingleThreshold;
        double singleThreshold = 0; 
        double upperThreshold = 0;
        double lowerThreshold = 0;
        //Split thresholds from the strategy.
        String[] thresholdstr = thresholds.split("\\;");
        
        if(thresholdstr.length > 1) {
            isSingleThreshold = false;                
            lowerThreshold = Double.valueOf(thresholdstr[0]).doubleValue();
            upperThreshold = Double.valueOf(thresholdstr[1]).doubleValue();            
        } else {
            isSingleThreshold = true;
            singleThreshold = Double.valueOf(thresholdstr[0]).doubleValue();           
        }
        /**
         * Checking an alignment strategy performance on generated 
         * segment pairs.
         */
        for (int i = 0; i < noOfSegmentPairsGen; i++) {
            System.out.println("Run strategy for segment pair : " + i);
            try {
                MergeManager mergeSegment = new MergeManager();
                boolean exists = (new File(Commons.SEGMENT + 
                        "SubG/Onto1-segment-" + i + ".owl")).exists() && 
                        (new File(Commons.SEGMENT + "SubG/Onto2-segment-" + 
                        i + ".owl")).exists();
                    if (exists) {
                        mergeSegment.loadOntologies(new URL("file:///" + 
                                Commons.SEGMENT + "SubG/" + "Onto1-segment-" 
                                + i + ".owl"), new 
                                URL("file:///" + Commons.SEGMENT + "SubG/" 
                                + "Onto2-segment-" + i + ".owl"));
                        mergeSegment.init();                        
                        acceptedMappingSugg.clear();
                        rejectedMappingSugg.clear();
                        // Geeting reference alignment for this segment pair.
                        acceptedMappingSugg = mergeSegment.getAlignment();
                        rejectedMappingSugg = mergeSegment.getPRARejection();
                        

                        
                        if (isSingleThreshold) {
                        suggestionVector = mergeSegment.getSuggestions
                                (Constants.STEP_CLASS, getWeight(
                                mergeSegment, matchers, weights), 
                                singleThreshold, combination);
                        } else {
                            suggestionVector = mergeSegment.
                                    getSuggestions(getWeight(mergeSegment, 
                                    matchers, weights), upperThreshold, 
                                    lowerThreshold, combination);
                        }
                    } else {
                        System.out.print("Wrong! Segment pairs do not exist!");
                        return null;
                    }                
            } catch (Exception ex) {
                    System.out.print(ex.getMessage());                
            }


       
            double[] scoreParams = countScoreParams(suggestionVector);
            
            
            paramA += scoreParams[0];
            paramB += scoreParams[1];
            paramC += scoreParams[2];
            paramD += scoreParams[3];
            paramE += scoreParams[4];
            
        }
        
        double [] scoreParams = {paramA, paramB, paramC, paramD, paramE};        
        return scoreParams;
        
    }
    
    
    /**
     * This method convert weights from string to double format and do the sim 
     * value computation if it is need.
     *      
     * @param merge     Manages segment pairs.
     * @param matchers  Matchers in an alignment strategy.
     * @param weights   Weight for the matchers.
     * 
     * @return weight   In double format.
     */
    private double[] getWeight(MergeManager merge, String matchers, 
            String weights) {
        
        double[] weight = new double[Constants.singleMatchers.length];        
        String[] matcher=matchers.split(";");
        String[] weightStr=weights.split(";");

        for (int i = 0; i < matcher.length; i++) {
            
            if (matcher[i].equalsIgnoreCase("EditDistance")) {
                 /**
                 * Checking sim values result, if not found then computation 
                 * is done.
                 */
                merge.matching(Constants.STEP_CLASS, Constants.EditDistance);
                weight[Constants.EditDistance] = Double.parseDouble
                        (weightStr[i]);
            }            
            if (matcher[i].equalsIgnoreCase("NGram")) {
                merge.matching(Constants.STEP_CLASS, Constants.NGram);
                weight[Constants.NGram] = Double.parseDouble(weightStr[i]);
            }            
            if (matcher[i].equalsIgnoreCase("WL")) {
                merge.matching(Constants.STEP_CLASS, Constants.WL);
                weight[Constants.WL] = Double.parseDouble(weightStr[i]);
            }            
            if (matcher[i].equalsIgnoreCase("WN")) {
                merge.matching(Constants.STEP_CLASS, Constants.WN);
                weight[Constants.WN] = Double.parseDouble(weightStr[i]);
            }          
            if (matcher[i].equalsIgnoreCase("TermBasic")) {
                merge.matching(Constants.STEP_CLASS, Constants.Terminology);
                weight[Constants.Terminology] = Double.parseDouble
                        (weightStr[i]);
            }            
            if (matcher[i].equalsIgnoreCase("TermWN")) {
                merge.matching(Constants.STEP_CLASS, Constants.WordNet_Plus);
                weight[Constants.WordNet_Plus] = Double.parseDouble
                        (weightStr[i]);
            }
            if (matcher[i].equalsIgnoreCase("UMLSKSearch")) {
                merge.matching(Constants.STEP_CLASS, Constants.UMLS);
                weight[Constants.UMLS] = Double.parseDouble(weightStr[i]);
            }
            if (matcher[i].equalsIgnoreCase("BayesLearning")) {
                merge.matching(Constants.STEP_CLASS, Constants.Bayes);
                weight[Constants.Bayes] = Double.parseDouble(weightStr[i]);
            }
            if (matcher[i].equalsIgnoreCase("Hierarchy")) {
                merge.matching(Constants.STEP_CLASS, Constants.Hierarchy);
                weight[Constants.Hierarchy] = Double.parseDouble(weightStr[i]);
            }
        }
        return weight;
    }
    
    
    /**
     * Convert mapping suggestions from the pair list format to the array list 
     * format.
     * 
     * @param mappingSuggestions    Mapping suggestions for an alignment 
     *                              strategy.
     * @return suggestions     Mapping suggestions in ArrayList format.
     */
    private ArrayList<String> getSuggestions(Vector mappingSuggestions) {
        
        ArrayList <String> suggestions = new ArrayList<String> ();
        
        for (Object p : mappingSuggestions) {
            Pair pair=(Pair)p;
            String concept1 = ((MElement) pair.getObject1()).getId();
            String concept2 = ((MElement) pair.getObject2()).getId();
            suggestions.add(concept1+" == "+concept2);
        }
        
        return suggestions;
    }
    
    
    /**
     * 
     *     
     * @param strategySuggestionList    Mapping suggestions given by alignment
     *                                  strategy.
     * @return params   Returns score parameters.
     */
    public double[] countScoreParams(Vector strategySuggestionList) {
        
        /**
         * [0] for parameter A.
         * [1] for parameter B.
         * [2] for parameter C.
         * [3] for parameter D.
         * [4] for parameter E.
         */
        double[] params=new double[5];        
        ArrayList<String> suggestions = getSuggestions(strategySuggestionList);
        // Counting params A, B and E.
        for (String suggestion : suggestions) {
            
            if (acceptedMappingSugg.contains(suggestion)) {
                params[0]++;
            } else if(rejectedMappingSugg.contains(suggestion)) {
                params[1]++;
            } else {
                params[4]++;
            } 
        }
        
        // Counting params C.
        for (String sug : acceptedMappingSugg) {            
            if (!suggestions.contains(sug)) {
                params[2]++;
            }                       
        }        
        
        params[3] = (RecommendationConstants.NO_OF_PAIRS) -(params[0] + 
                params[1] + params[2] + params[4]);
        
        return params;
    }    
    
    /**
     * Delete all files in a directory.
     * 
     * @param dir   Path of directory. 
     */
    private void cleanDirectory(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".owl") || name.endsWith(".OWL");
            }
        });
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

}
