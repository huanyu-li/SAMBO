/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Recommendation;

import java.sql.Connection;
import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.Matcher;

/**
 * Constants used in the recommendation processes.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class RecommendationConstants {
    
    /**
     * To avoid creating an instance.
     */
    private RecommendationConstants() {
    }   
    /**
     * SQL server connections, to make sure limited SQL connections are created
     * during recommendation process.
     */
    public static Connection SQL_CONN=null;    
    /**
     * To copy reference variable for Porter_WordNet, so that no need to 
     * install WordNet every time an instance is created for 
     * SimValueConstructorSinglePair class.
     */ 
    public static Matcher REF_PORTER_WORDNET;    
    /**
     * To check whether wordnet is installed for that particular session.
     */ 
    public static boolean IS_WORDNET_INSTALLED = false;
    /**
     * Number of concept pairs, usually used for recommendation 1 & 2 methods. 
     */
    public static int NO_OF_PAIRS = 0;    
    /**
     * Number of recommendation methods.
     */
    public static final int NUMBER_OF_RECOM_ALG = 3;
    /**
     * Recommendation method 1, the one which uses segment pairs and UMLS as
     * an oracle.
     */
    public static final int RECOMMENDATION_METHOD1 = 1;
    /**
     * Recommendation method 2, the one which uses segment pairs and decision on
     * mapping suggestions as an oracle.
     */
    public static final int RECOMMENDATION_METHOD2 = 2;
    /**
     * Recommendation method 3, the one which uses decision on mapping
     * suggestions alone.
     */
    public static final int RECOMMENDATION_METHOD3 = 3;
     /**
      * To compute Recommendation method 2, the one which uses segment pairs
      * and decision on mapping suggestions as an oracle.     
      */
    public static boolean DO_RECOMMENDATION_MTH2 = false;
    /**
      * To compute Recommendation method 3, the one which uses decision on
      * mapping suggestions alone.     
      */
    public static boolean DO_RECOMMENDATION_MTH3 = false;
    /**
     * MySQL table name for recommendation method 1.
     */
    public static final String RECOM_METHD1_TABLE = 
            "dbsambo.recommendationMethod1";
    /**
     * MySQL table name for recommendation method 2.
     */
    public static final String RECOM_METHD2_TABLE = 
            "dbsambo.recommendationMethod2";
    /**
     * MySQL table name for recommendation method 3.
     */
    public static final String RECOM_METHD3_TABLE = 
            "dbsambo.recommendationMethod3";   
    /**
     * All Mapping suggestions validated in a session.
     */
    public static ArrayList<String> VALIDATED_SUGGESTIONS = new 
            ArrayList<String>();
    /**
     * Accepted Mapping suggestions in a session.
     */
    public static ArrayList<String> ACCEPTED_SUGGESTIONS = new 
            ArrayList<String>();
    /**
     * Rejected Mapping suggestions in a session.
     */
    public static ArrayList<String> REJECTED_SUGGESTIONS = new 
            ArrayList<String>();
    /**
     * Generated Rejected Mapping suggestions.
     */
    public static ArrayList<String> GENERATED_REJECTED_SUGGESTIONS = new 
            ArrayList<String>();
    /**
     * Recommendation parameters that will be displayed to user.
     */
    public static final String[] DISPLAY_PARAMS = 
    {"ontologies", "matcher", "weight", "combination", "threshold", 
        "recallCorrect", "precisionCorrect", "fmeasureCorrect", 
        "score1", "score2"};
    
}
