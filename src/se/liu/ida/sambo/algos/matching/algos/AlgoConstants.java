/*
 * AlgoConstants.java
 
 */

package se.liu.ida.sambo.algos.matching.algos;

import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.ui.SettingsInfo;

/** 
 * The constants used for matching and recommendation algorithms.
 *
 * @authors Tan He, Rajaram
 * @version 2.0
 */
public class AlgoConstants {
    
    /**
     * A combinated matcher including ngram, edit distance
     * and word matching (stemming).
     */
    public static final int LIN_COMBINATION  = 2;    
    /**
     * The terminology matching including Wordnet.
     */
    public static final int LIN_COMBINATION_PLUS = 3;
    /**
     * A combinated matcher including ngram, edit distance
     * and word matching (stemming).
     */
    public static final int TERM_BASIC = 4;
    /**
     * The terminology matching including Wordnet.
     */
    public static final int TERM_WN = 5;    
    /**
     * A matcher quering UMLS.
     *
     *This matcher will not be used together with UMLS
     */
    public static final int UMLS = 6;    
    /**
     * Hierarchical matcher.
     */
    public static final int HIERARCHY = 7;    
    /**
     * Machine learning matcher.
     */
    public static final int LEARNING = 8;    
    /**
     * Final similarity value.
     */
    public static final int FINAL_VALUE = 5;   
    /**
     *the number of similarity values.
     *i.e. the number of available matchers
     */
    public static final int SIM_VALUE_NUM = 6;    
    /**
     * Number of available matchers+PRA_search.
     */
    public static final int NO_OF_MATCHERS = 9;//+1;    
    /**
     * The final value set to the identified mapping.
     */
    public static final int ALIGNMENT = 100;    
    /**
     * The final value set to the identified unmapping.
     */
    public static final int NO_ALIGNMENT = -100;    
    /**
     * Number of Linguistic matcher.
     */    
    public static final int LIN_MATCHER_NUM = 3;    
    /**
     * Porter matcher.
     */
    public static final int PORTER_WORDNET = 2;    
    /**
     * Edit Distance matcher.
     */
    public static final int EDIT_DISTANCE = 0;    
    /**
     * N-gram matcher.
     */
    public static final int NGRAM = 1;    
    /**
     * The weights for the linguistic matchers.
     */
    public static final double[] WEIGHT_LIN = {0.3, 0.3, 0.21};
    //public static final double[] WEIGHT_LIN = {0, 0, 1};
    
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // matcher parameters
    //%%%%%%%%%%%%%%%%%%%%%%
    
    /**
     * Porter with WordNet matcher.
     **/
    public static final int NGRAM_SIZE = 2;    
    /**
     * The edge weight in WordNet.
     */
    public static final double WORDNET_WEIGHT = 0.2;    
    /**
     * The edge weight in WordNet.
     */
    public static final double PARENTS_CHILDREN = 0.7;    
    /**
     * The weight assigned to UMLS synonyms.
     */
    public static final double UMLS_SYN_VALUE = 0.6;    
    /**
     * The weight assigned to UMLS relations.
     */
    public static final double UMLS_RELATED_VALUE = 0.2;    
    
    
    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // external file loations
    //%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    /**
     * The directories containing the training examples.
     */
    public static String LEARNING_DIR1;
    public static String LEARNING_DIR2;    
    /** 
     * The location of wordnet dictionary.
     */
    public static String WORDNET_DIC = null;    
    /** 
     * The location of stopword file.
     */
    public static String STOPWORD_FILE = null;    
    /**
     * To stop the matching process any time user want.
     */   
    public static boolean STOPMATACHING_PROCESS = false;
    /**
     * To enable interrupt so that matching process will stop at particular
     * concept pairs.
     * 
     * Note: Whenever you set true value for "SET_INTERRUPT_ON" variable, do
     * Set value for "STOP_COMPUTATION_AT" variable to activate the interrupt.
     */
    public static boolean SET_INTERRUPT_ON = false;
    /**
     * To enable interrupt so that matching process will stop at particular
     * concept pairs, assign value for this constant whenever you set true 
     * value for "SET_INTERRUPT_ON".
     */
    public static int STOP_COMPUTATION_AT;    
    /**
     * To remember at which concept pair user interrupted computation.
     */
    public static int USER_INTERRUPT_AT = 0;    
    /** 
     * To check whether we are in recommendation process.
     */ 
    public static boolean ISRECOMMENDATION_PROCESS = false;    
    /**
     * To have copy of OntManager, will be useful in recommendation process.
     */ 
    public static OntManager ontManager;  
    public static testOntManager testontManager;  
    /**
     * Use to sepearate two values.
     * 
     * eg: (concept1#concept2)
     */
    public final static String SEPERATOR = "#";    
    /**
     * To have a count on no.of concept pairs generated.
     */    
    public static int NO_OF_PAIRS;
    /**
     * To have copy of session settings.
     */    
    public static SettingsInfo settingsInfo;
}
