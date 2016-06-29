/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.PRAalg.testdtfPRA;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.MappableConceptPairsDB;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.ResultsForCombinationAccessDB;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.testPair;
/**
 *
 * @author huali50
 */





/**
 * <p>
 * Handles various event related to similarity value computation and finding 
 * mapping suggestions for mappable parts of the ontologies.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class testSimValueConstructorMappableGrp {
    
    /**
     * Acts as a temporary database to store concepts of the ontology.
     */
    private Set<Integer> source_content, target_content;
    /**
     * To indicate matching process steps(slot matching/class matching).
     */
    private String matchingStep = "";
    private testMOntology source_ontology, target_ontology;
    /**
     * To manage the ontology contents.
     */
    private testOntManager ontManager;
    /**
     * SQL connection to accessing stored computation result database.
     */
    private Connection sqlConn = null;
    /**
     * No.of available matchers.
     */
    private int numOfMatchers = AlgoConstants.NO_OF_MATCHERS;
    /**
     * Matchers.
     */
    private Matcher[] matchers = new Matcher[numOfMatchers];
    /**
     * To check the availability of matchers computation results.
     */
    private boolean[] matchersCompResult = new boolean[numOfMatchers];
    /**
     * Ontologies pair name.
     */
    private String ontologiesName;
    /**
     * For querying the computation results of the matchers.
     */
    private SimValueGenerateQuery simValueTable;
    /**
     * For querying the computation results availability table.
     */
    private ResultsForCombinationAccessDB matcherResultTable;
    /**
     * Mappable concepts for the particular pair of an ontologies.
     */
    private ArrayList<String> mappableConcepts;  
    /**
     * Linguistic Matchers.
     */
    private Matcher[] linsMatchers = new Matcher[AlgoConstants.LIN_MATCHER_NUM];
    /**
     * To compute sim value for a single concept pair.
     */
    private testSinglePairComputation singleComp;
    /**
     * To avoid creating default constructor.
     */
    private testSimValueConstructorMappableGrp() {
        
    }

    /**
     * <p>
     * This constructor establishes a connection to access the SQL server,
     * initialize the ontology contents etc.
     * </p>
     *
     * @param step      Which matching step.
     * @param ontMan    ontologies content manager.
     */
    public testSimValueConstructorMappableGrp(final String step, 
            final testOntManager ontMan) {
        
        matchingStep = step;
        ontManager = ontMan;
        singleComp = new testSinglePairComputation(ontMan);
        this.source_ontology = ontManager.getontology(Constants.ONTOLOGY_1);
        this.target_ontology = ontManager.getontology(Constants.ONTOLOGY_2);
        /**
         * To make sure that the system using single SQL connection for the
         * entire recommendation process.
         */
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            sqlConn = RecommendationConstants.SQL_CONN;
        } else {
            sqlConn = makeConnection();
        }
        // To get the slot concepts.
        if (matchingStep.equalsIgnoreCase("Init slot")) {
            source_content = this.source_ontology.getProperties();
            target_content = this.target_ontology.getProperties();
        }
        // To get the class concepts.
        else if (matchingStep.equalsIgnoreCase("Init class")) {
            source_content = this.source_ontology.getMClasses();
            target_content = this.target_ontology.getMClasses();
        }   
        // Name of the ontology pair. 
        //ontologiesName = AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1).concat(AlgoConstants.SEPERATOR).concat(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        ontologiesName = "";
        // To query the simvalue table
        simValueTable = new SimValueGenerateQuery(ontologiesName, sqlConn);
        matcherResultTable = new ResultsForCombinationAccessDB(sqlConn);
        // Getting mappable concepts for this particular pairs of ontologies
        mappableConcepts = getMappableConcepts();

        // Checking availiablity of computation results of all matchers.
        for (int i = 0; i < numOfMatchers; i++) {

            // Name of the matcher in the database.
            String matcherName = "matcher" + i;
            /**
             * resultForMatcher[0]- Is the column for matcher is available in 
             * the simvalue table.
             *
             * resultForMatcher[1]- Is the complete matcher computation result
             * is available in the data base.
             * 
             * (i.e) previous CS is done without any interrupt
             */
            boolean[] resultForMatcher = matcherResultTable.getMatcherInfo(
                    ontologiesName, matcherName, sqlConn);
            /**
             * In current implementation computation results of the slot 
             * matching processes is not taken into account.
             */
            if (resultForMatcher[0] && resultForMatcher[1]
                    && !matchingStep.equalsIgnoreCase("Init slot")) {
                this.matchersCompResult[i] = true;
            } else {
                this.matchersCompResult[i] = false;
            }
        }
    }
    
    /**
     * This method is used during the recommendation process, to get a 
     * sim value for a single concept pair.
     * 
     * @param conceptPair           concept pair.
     * @param weight                Matchers weight.
     * @param selectConn            SQL server connection.
     *
     * @return      Similarity value.
     */
  

    /**
     * Create new SQL server connection.
     *
     * @return conn     SQL server connection.
     */
    private Connection makeConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(se.liu.ida.sambo.algos.matching.algos.
                    SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    
    
    /**
     * To get mapping suggestions for a single matcher, usually used in the
     * slot matching and segment pairs recommendation process.
     *
     * @param threshold     Single threshold value for filtering mapping
     *                      suggestions.
     * @param matcher       matcher.
     *
     * @return              List of mapping suggestions.
     *                      
     */
    
    
    
    
    /** 
     * To get mapping suggestions if more than one matcher is selected,
     * usually used in the slot matching and segment pairs recommendation 
     * process.     
     *
     * @param threshold     Single level threshold for filtering mapping
     *                      suggestions.
     * @param weight        weight of the matchers, if the matcher is not 
     *                      selected then its weight should be 0.
     * @param combination   combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */
   
    
    /** 
     * To get mapping suggestions if more than one matcher is selected,
     * usually used in the class matching (non recommendation process).     
     *
     * @param threshold     Single threshold value for filtering mapping
     *                      suggestions.
     * @param weight        weight of the matchers, if the matcher is not 
     *                      selected then its weight should be 0.
     * @param combination   combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */  

    public Vector getPairList(double[] weight, double threshold,
            String combination){
        // List of mapping suggestions to be return.       
        Vector suggestions = new Vector();        
        String concept1 = "";
        String concept2 = "";                
        String concept1ID = "";
        String concept2ID = "";
        String conceptPairs = "";
        Connection selectConn = null;
        double finalSimValue = 0;
        /**
         * To avoid combining sim value of the HierarchyMatcher with other 
         * matchers.
         */ 
        boolean hierarchyMatcherON = false;
        ArrayList<String> queryResult = null;
        // Single SQL connection is used for the entire recommendation process.    
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            selectConn= sqlConn;            
        } else {
             selectConn = makeConnection();            
        }      
                
        if(weight[AlgoConstants.HIERARCHY]!=0) {
            hierarchyMatcherON=true;                
        }
        // Querying database.
        if (combination.equalsIgnoreCase("maximum")) {
            queryResult = simValueTable.getSuggestionsMaximumBased(
                    weight, threshold);                
        } else {
            queryResult = simValueTable.getSuggestionsWeightedBased(
                    weight, threshold);                
        }
        /**
         * The above query will return result in a form of array list,
         * so in this step we will convert these results into concept pairs.
         */
        for (String data:queryResult) {
            String[] resultParams = data.split(AlgoConstants.SEPERATOR);
            concept1ID = resultParams[0];
            concept2ID = resultParams[1];
            finalSimValue = Double.valueOf(resultParams[2]).doubleValue();                    
            concept1 = "";
            concept2 = "";
            
            /**
             * To avoid combining sim value of the Hierarchy Matcher, 
             * but add it to the final sim value, in case if the
             * final sim value is greater than 1 then make it as 1.
             */                     
            if (hierarchyMatcherON && finalSimValue > 1) {
                finalSimValue = 1;
            }
            // Quering ontoManager with conceptID.
            concept1 = source_ontology.getElementURI(concept1ID);
            concept2 = target_ontology.getElementURI(concept2ID);
            conceptPairs = concept1ID.concat(AlgoConstants.SEPERATOR).concat
                        (concept2ID);
            // Generating concept pairs and adding to the suggestion list.
            if ( !concept1.equals("") && !concept2.equals("")) {
                testPair pair = new testPair(concept1,concept2);
                // Checking if the suggestion is aligned in the previous round.
                if(mappableConcepts.contains(conceptPairs) && source_ontology.getElement(concept1).getAlignElement() == null
                        || target_ontology.getElement(concept2).getAlignElement() == null){
                                   
                    if(!AlignmentConstants.IsAligned.contains(conceptPairs)) {
                        pair.setSim(finalSimValue);    
                        suggestions.add(pair);                               
                    }
                }                
            }
        }        
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(selectConn);
        }
        
        return suggestions;
    }
    
    
    /** 
     * To get mapping suggestions for the double threshold filtering, if more 
     * than one matcher is selected, usually used in the class matching
     * (non recommendation process).     
     *
     * @param weight            weight of the matchers, if the matcher is not 
     *                          selected then its weight should be 0.
     * @param upperthreshold    Higher threshold.
     * @param lowerthreshold    Lower threshold.
     * @param combination       combination method(weighted/maxBased).
     *
     * @return   List of  mapping suggestions.
     */
    public Vector getPairList(double[] weight, double upperthreshold,
            double lowerthreshold, String combination) {
        // List of final mapping suggestions.        
        Vector suggestion = new Vector();
        // List of mappping suggestions between upper and lower threshold.
        Vector unprocessedSuggestions = new Vector();  
        // Getting suggestions above or equal to lower threshold.
        Iterator pairs = getPairList(weight, lowerthreshold,
                combination).iterator();
        /**
         * Separating mapping suggestions.
         * (i.e) The suggestions with a simvalue greater than or equals to upper
         * threshold will be added to the final suggestion list, 
         * the suggestions between upper and lower threshold will be added 
         * to the unprocessed suggestions list which will be further filtered 
         * by the dtf algorithm.
         */
        while (pairs.hasNext()) {            
            testPair pair = (testPair) pairs.next();            
            if (pair.getSim() >= upperthreshold) {
                suggestion.add(pair);
            } else {
                unprocessedSuggestions.add(pair);
            }            
        }
        /**
         * We need suggestions greater than or equals to upper threshold to get
         * consistent group and of course suggestions between upper and lower
         * threshold to apply the dtf algorithm.
         */
        Iterator dftResult = null;
        if (suggestion.size() > 0 && unprocessedSuggestions.size() > 0) {
            testdtfPRA dtf = new testdtfPRA(source_ontology,
                    target_ontology, suggestion);
            dftResult = dtf.getResults(unprocessedSuggestions,
                    upperthreshold, lowerthreshold).iterator();         
        } else if (unprocessedSuggestions.size() > 0){
            dftResult = unprocessedSuggestions.iterator();
            
        }
        while (dftResult!= null && dftResult.hasNext()) {
                suggestion.add(dftResult.next());
            }

        return suggestion;
    
        }
    
    /** 
     * To get mapping suggestions for the double threshold filtering, if more 
     * than one matcher is selected, usually used in the recommendation process.     
     *
     * @param weight            weight of the matchers, if the matcher is not 
     *                          selected then its weight should be 0.
     * @param upperthreshold    Higher threshold.
     * @param lowerthreshold    Lower threshold.
     * @param combination       combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */
   
    
    /**
     * This method starts the computation for the specified matcher.
     * 
     * @param matcher   Matcher whose sim value computation should be start. 
     */
   
    /** 
     * calculate the linguistic Sim Value using Porter, 
     * Ngram and EditDistance individually.
     */ 
    
    
    
    /**
     * This method calculate similarity values for the two given
     * ontologies and the matcher list.
     * 
     * @param matcher       Matcher 
     * @param matcherList   In matchers like wordnet and wordlist the algorithm
     *                      will use more than one matcher(matching algorithm).
     * @param weight        Used for the matcher's like wordnet and wordlist. 
     */
    
    
    private void calculate(Matcher[] matcher_list, double[] values,
            String str1, String str2) {
        
        double value;        
        for (int i = 0; i < matcher_list.length; i++) {
            value = matcher_list[i].getSimValue(str1, str2);                        
            if (values[i] < value) {
                values[i] = value;
            }
        }
    }
    /**
     * This method create delay in a program.
     * 
     * @param delayTime Delay time in milli second. 
     */
    private void delayLine(int delayTime) {
        try {        
            Thread.sleep(delayTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimValueConstructorMappableGrp.class.getName()
                    ).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * This method will get mappable concept pairs from the database.
     * 
     * @return List of Concept pairs.
     */
    private  ArrayList<String> getMappableConcepts() {
        MappableConceptPairsDB db = new MappableConceptPairsDB(sqlConn);
        ArrayList<String> mappableConcepts = new ArrayList<String> ();
        mappableConcepts = db.getConceptPairs(ontologiesName);
        
        return mappableConcepts;
    }
    
}
