/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.sambo.session;

import java.net.URL;
import java.util.*;
import se.liu.ida.sambo.MModel.MOntology;

/**
 *
 * @author mzk
 */
public class Commons {

    /**
     * Properties name with complete path
     */
    public static String PROPERTIES_NAME;

    /**
     * File home directory
     */
    public static String FILEHOME;

    /**
     * File storage path for the user
     */
    public static String DATA_PATH;

    /*
     * Segment storage path for user
     */
    public static String SEGMENT;

    /**
     * User name of the currently logged in user
     */
    public static String USER_NAME;

    /**
     * Defining at which stage the system is right now.
     */
    public static String SESSION_TYPE;

    /**
     * Defining a Session ID
     */
    public static int S_ID;

    /**
     * Ontology file 1
     */
    public static String OWL_1;
    
    /**
     * Ontology file 2
     */
    public static String OWL_2;
    
    /**
     * At which step the system is right now
     */
    public static int STEP_VALUE;

    /*
     *  URL Ontology file 1
     */
    public static URL URL_OWL_1;

    /*
     *  URL Ontology file 1
     */
    public static URL URL_OWL_2;

    /*
     *  User Session ID
     */
    public static int USER_SESSION_ID;

    /**
     * used Threshold value
     */
    public static String THRESHOLD_VALUE;
    
    /**
     * Array of available matchers
     */
    public static String[] Matchers_Available = {"EditDistance","NGram","WL","WN","TermBasic","TermWN","UMLSKSearch","Hierarchy","BayesLearning"};
    
    /**
     *  ArrayList to store used matchers in session
     */
    public static  ArrayList<String> usedMatchersList = new ArrayList();
    
    /**
     * ArrayList to store used weight values in the session
     */
    public static  ArrayList<String> usedWeightValuesList = new ArrayList();

    /**
     *  ArrayList to store database matchers in session
     */
    public static  ArrayList<String> dbMatchersList = new ArrayList();

    /**
     * ArrayList to store database weight values in the session
     */
    public static  ArrayList<String> dbWeightValuesList = new ArrayList();

    /**
     * ArrayList to store sub matchers weight values list in the session
     */
    public static  ArrayList<String> subMatchersWeightValuesList = new ArrayList();

    /**
     *  ArrayList to store quality measure for different strategies
     */
    public static  ArrayList<String> qualityMeasureList = new ArrayList();

     /**
     *  ArrayList to f-measure for different strategies
     */
    public static  ArrayList<String> fmeasureList = new ArrayList();

    /**
     * ArrayList to store execution measures for different strategies
     */
    public static  ArrayList<String> executionMeasureList = new ArrayList();

    /**
     * ArrayList to store precision for different strategies
     */
    public static  ArrayList precisionList = new ArrayList();

    /**
     * ArrayList to store recall for different strategies
     */
    public static  ArrayList recallList = new ArrayList();

     /**
     *  ArrayList to f-measure for different strategies
     */
    public static  ArrayList<String> recommendationScoreList = new ArrayList();

    /**
     *  Index sub-matcher weights
     */
    public static  int IndexSubMatcherWeights = 0;
    
    /**
     * Current Time
     */
    public static String CTime;

    /**
     * Last Accessed Time
     */
    public static String LATime;

    /**
     *
     */
    public static String SESSION_ID;
    /**
     *
     */
    public static boolean isLoadedSession = false;
    /**
     *
     */
    public static int NO;

    /**
     *
     */
    public static String strProcessedSuggestionsPair[];
    /**
     *
     */
    public static String strProcessedSuggestionsName[];
    /**
     *
     */
    public static String strProcessedSuggestionsNum[];
    /**
     *
     */
    public static String strProcessedSuggestionsComment[];
    /**
     *
     */
    public static String strProcessedSuggestionsAction[];

    /**
     * Color value for Ontology 1
     */
    public static String colorOnt1;

    /**
     * Color value for Ontology 2
     */
    public static String colorOnt2;

    /**
     * A vector used to store the suggestion list from XML file
     */
    public static Vector XMLSuggestionVector;

    /**
    * A vector used to store the processed suggestion list from XML file
     */
    public static Vector XMLProcessedSuggestionVector;

    /**
     * Vector List used for temporary storage of list values
     */
    public static Vector vList;

     /**
     * Vector List used for temporary storage of list values
     */
    public static Vector remainingSuggestionVector = new Vector();

    /**
     * Array of Objects
     */
    public static Object[] strings;

    /**
     * Array List of Recommended Matchers
     */
    public static ArrayList<String> strRecommendedMatchers = new ArrayList();

    /**
     * Array List of Recommended Weight values
     */
    public static ArrayList<String> strRecommendedWeight = new ArrayList();

    /**
     * Recommended Threshold Value
     */
    public static String RecommendedThresholdValue;

    /*
     * Strategy Id
     */
    public static String StrategyId;

    /**
     * If user have finalized!
     */
    public static int isFinalized = 0;

    /**
     * the current page position of user
     */
    public static int currentPosition = 0;

    /**
     * If user have finished!
     */
    public static boolean isFinished = false;
    
    /**
     *  If the process has started!
     */
    public static boolean hasProcessStarted = false;


    /**
     *  Execution Performance Strategy
     */

    public static double ExecutionPerformance = 0.0;

    /**
     *  Quality Strategy
     */

    public static double Quality = 0.0;

    
    /**
     *  Threshold value
     */
    public static double threshold = 0;

    /**
     *  New Session
     */

    public static int new_session = 0;

    // Ontology 1 and Ontology2
    public static MOntology monto1=null;
    public static MOntology monto2=null;

    /*
     * LocatedSavedStrategy
     */
    public static int LocatedSavedStrategy = 0;
    public static String CONTEXT_PATH;
}