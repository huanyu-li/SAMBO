/*
 * 
 *
 */

package se.liu.ida.sambo.algos.matching.algos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.PRAalg.dtfPRA;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.ResultsForCombinationAccessDB;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.Pair;

/**
 * <p>
 * Used during the recommendation process, the one which uses mapping decisions 
 * alone. 
 * 
 * Note: This class may be removed in the future.
 * </p>
 * 
 * @author  Rajaram
 * @version 1.0
 */
public class SimValueConstructorUserListPair {
    /**
     * To manage the ontology contents.
     */
    private OntManager ontManager = AlgoConstants.ontManager;    
    /**
     * No.of avaliable matchers.
     */
    private int noOfMatchers = AlgoConstants.NO_OF_MATCHERS;
    /**
     * Matchers.
     */
    private Matcher[] matchers = new Matcher[noOfMatchers];    
    /**
     * Linguistic Matchers.
     */ 
    private Matcher[] linMatchers = new Matcher[AlgoConstants.LIN_MATCHER_NUM];
    /**
     * To check the availability of matcher's computation results.
     */
    private boolean[] matchersCompResult = new boolean[noOfMatchers];
    /**
     * Ontologies pair name.
     */
    private String ontologiesName;
    /**
     * Mapping suggestions that are validated by the user.
     */
    private ArrayList<String> conceptPairs;    
    /**
     * For querying the computation results.
     */
    private SimValueGenerateQuery simValueTable; 
    /**
     * For querying the computation results availability table.
     */
    private ResultsForCombinationAccessDB matcherResultTable;
    /**
     * MySQL connection.
     */
    private Connection selectConn, updateConn;   
    /**
     *<p>
     * This constructor establishes a connection to access the SQL server,
     * initialize the ontology contents etc.
     * </p>
     * 
     * @param conceptPairs      User's concept pairs.   
     *    
     */
    public SimValueConstructorUserListPair(ArrayList<String> uConceptPairs) {
        
        selectConn = makeConnection();
        updateConn = makeConnection();        
        ontologiesName = AlgoConstants.settingsInfo.getName(
                Constants.ONTOLOGY_1).concat("#").concat(
                AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        simValueTable = new SimValueGenerateQuery(ontologiesName, selectConn); 
        matcherResultTable = new ResultsForCombinationAccessDB(selectConn);
        
        
        conceptPairs = uConceptPairs;
        
        // Checking availiablity of the computation results of all matchers.
        for (int i = 0; i < noOfMatchers; i++) {
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
                    ontologiesName, matcherName, selectConn);            
            
            if (resultForMatcher[0] && resultForMatcher[1]) {
                this.matchersCompResult[i] = true;
            } else {
                this.matchersCompResult[i] = false;
            }
        }
        
        matchers[AlgoConstants.UMLS] = new UMLSKSearch_V6();
        
    }
    
    /**
     * Create a new SQL server connection.
     *
     * @return conn     SQL server connection.
     */    
    private Connection makeConnection() {
        Connection conn=null;
        try {
            conn=ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()
                    ).log(Level.SEVERE, null, ex);
        }        
        return conn;
    }
    
    /**
     * To get mapping suggestions for an alignment strategy.
     * 
     * Note : This method is used for the strategies that has double threshold
     * filtering.     
     * 
     * @param matcher           List of matchers.    
     * @param combination       Combination type(weighted/maximumBased)
     * @param weight            Weight of the matchers.
     * @param upperthreshold    Highest threshold level.
     * @param lowerthreshold    Lowest threshold level.
     * 
     * @return  List of mapping suggestions.
     */
    
    public ArrayList<String> getSuggestions(int[] matcher, String combination, 
            double[] weight, double upperthreshold, double lowerthreshold) {               
 
        ArrayList<String> suggestions = new ArrayList<String>();     
        
        for (int i:matcher) {
            /**
             * Compute sim value for the matchers whose sim value is not 
             * available in the database.
             */ 
        if(!matchersCompResult[i]) {
            calculateSimilarityValue(i);
            }        
        }     
        Iterator pairList = getPairList(weight, upperthreshold, lowerthreshold, 
                combination).iterator();  
        // Adding suggestions in string format.
        while (pairList.hasNext()) {
            Pair pair = (Pair) pairList.next();            
            String concept1 = ((MElement) pair.getObject1()).getId();
            String concept2 = ((MElement) pair.getObject2()).getId();            
            suggestions.add(concept1.concat(AlgoConstants.SEPERATOR).
                    concat(concept2));      
            
        }               
        return suggestions;       
    }
    
    /**
     * To get mapping suggestions for an alignment strategy.
     * 
     * Note : This method is used for the strategies that has single threshold
     * filtering.     
     *
     * @param matcher           List of matchers.    
     * @param combination       Combination type(weighted/maximumBased)
     * @param weight            Weight of the matchers.
     * @param threshold         Threshold value for filtering.     
     * 
     * @return  List of mapping suggestions.
     */    
    public ArrayList<String> getSuggestions(int[] matcher, String combination,
            double[] weight, double threshold) {
        
        ArrayList<String> suggestions = new ArrayList<String>();        
        
        for (int i:matcher) {
            /**
             * Compute sim value for the matchers whose sim value is not 
             * available in the database.
             */
            if(!matchersCompResult[i]) {
                calculateSimilarityValue(i);
            }
        }
        Iterator pairList = getPairList(weight, threshold,
                combination).iterator();
        // Adding suggestions in string format.
        while (pairList.hasNext()) {
            Pair pair = (Pair) pairList.next();            
            String concept1 = ((MElement) pair.getObject1()).getId();
            String concept2 = ((MElement) pair.getObject2()).getId();            
            suggestions.add(concept1.concat(AlgoConstants.SEPERATOR).
                    concat(concept2));      
            
        }
        return suggestions;
    }

    
    /** 
     * To get mapping suggestions if more than one matcher is selected.     
     *
     * @param threshold     Single threshold value for filtering mapping
     *                      suggestions.
     * @param weight        weight of the matchers, if the matcher is not 
     *                      selected then its weight should be 0.
     * @param combination   combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */  

    private Vector getPairList(double[] weight, double threshold,
            String combination){
        // List of mapping suggestions to be return.       
        Vector suggestions = new Vector();        
        Object concept1 = "";
        Object concept2 = "";                
        String concept1ID = "";
        String concept2ID = "";        
        Connection selectConn = null;
        double finalSimValue = 0;       
        ArrayList<String> queryResult=null;        
        // Querying database.
        if (combination.equalsIgnoreCase("maximum")) {
            queryResult = simValueTable.getSuggestionsMaximumBased(
                    weight, threshold);                
        } else {
            queryResult = simValueTable.getSuggestionsWeightedBased(
                    weight, threshold);                
        }
        /**
         * The above query will return result in the form of array list,
         * so in this step we will convert this result into concept pairs.
         */
        for (String data:queryResult) {
            String[] resultParams = data.split("#");
            concept1ID = resultParams[0];
            concept2ID = resultParams[1];
            finalSimValue = Double.valueOf(resultParams[2]).doubleValue();                    
            concept1 = null;
            concept2 = null;            
            // Quering ontoManager with conceptIDs.
            concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(concept1ID);
            concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(concept2ID);
            // String pair
            String strPair = concept1ID.concat(AlgoConstants.SEPERATOR).
                    concat(concept2ID);
            // Generating concept pairs and adding to the suggestion list.
            if (concept1 != null && concept2 !=null && conceptPairs.
                    contains(strPair)) {
                Pair pair = new Pair(concept1,concept2);                
                pair.setSim(finalSimValue);    
                suggestions.add(pair);
            }
        }   
        return suggestions;
    }
    
    
    /** 
     * To get mapping suggestions for the double threshold filtering, if more 
     * than one matcher is selected.     
     *
     * @param weight            weight of the matchers, if the matcher is not 
     *                          selected then its weight should be 0.
     * @param upperthreshold    Higher threshold.
     * @param lowerthreshold    Lower threshold.
     * @param combination       combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */
    private Vector getPairList(double[] weight, double upperthreshold,
            double lowerthreshold, String combination) {
        // List of mapping suggestions to be return.        
        Vector suggestion = new Vector();
        // List of pairs between upper and lower threshold.
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
            Pair pair = (Pair) pairs.next();            
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
            dtfPRA dtf = new dtfPRA(ontManager.getMOnt(1),
                    ontManager.getMOnt(2), suggestion);
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
     * Close all SQL server connections created by the instance of this class.
     */
    public void closeAllConnections() {
        ResourceManager.close(selectConn);
        ResourceManager.close(updateConn);
    }   
    
    /**
     * This method starts the computation for the specified matcher.
     * 
     * @param matcher   Matcher whose sim value computation should be start. 
     */
    public void calculateSimilarityValue(int matcher) {        
        
        switch(matcher){
            case AlgoConstants.EDIT_DISTANCE:
                getEditDistanceValues();
                break;                
            case AlgoConstants.NGRAM:
                getNGramValues();
                break;                
            case AlgoConstants.LIN_COMBINATION:
                getLinValues(false);
                break;                
            case AlgoConstants.LIN_COMBINATION_PLUS:
                getLinValues(true);
                break;                
            case AlgoConstants.TERM_BASIC:
                getTermWNValues(false);
                break;                
            case AlgoConstants.TERM_WN:
                getTermWNValues(true);
                break;                
            case AlgoConstants.UMLS:
                getUMLSValue();
                break;                
            case AlgoConstants.HIERARCHY:
                getStrValue();
                break;                
            case AlgoConstants.LEARNING:
                getLearningValue();
                break;            
        }
    }

    private void getEditDistanceValues() {
        matchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();        
        perform(AlgoConstants.EDIT_DISTANCE, 
                matchers[AlgoConstants.EDIT_DISTANCE]);
    }

    private void getNGramValues() {
        matchers[AlgoConstants.NGRAM] =  new NGram(3);        
        perform(AlgoConstants.NGRAM,matchers[AlgoConstants.NGRAM]);
    }
    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */  
    private void getLinValues(boolean WordNet_ON) {
        
        if (WordNet_ON) {
            // To avoid installing WordNet multiple times.
            if(!RecommendationConstants.IS_WORDNET_INSTALLED) {
            linMatchers[AlgoConstants.PORTER_WORDNET] =
                    new Porter_WordNet(WordNet_ON);
            RecommendationConstants.REF_PORTER_WORDNET = 
                    linMatchers[AlgoConstants.PORTER_WORDNET];
            } else {
                linMatchers[AlgoConstants.PORTER_WORDNET] = 
                        RecommendationConstants.REF_PORTER_WORDNET;
            }
            linMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linMatchers[AlgoConstants.NGRAM] =
                    new NGram(AlgoConstants.NGRAM_SIZE);                     
            RecommendationConstants.IS_WORDNET_INSTALLED = true;
            perform(AlgoConstants.LIN_COMBINATION_PLUS, linMatchers,
                    AlgoConstants.WEIGHT_LIN);
        } else {
            linMatchers[AlgoConstants.PORTER_WORDNET] =
                    new Porter_WordNet(WordNet_ON);
            linMatchers[AlgoConstants.EDIT_DISTANCE] =
                    new EditDistance();
            linMatchers[AlgoConstants.NGRAM] =
                    new NGram(AlgoConstants.NGRAM_SIZE);
            perform(AlgoConstants.LIN_COMBINATION, linMatchers,
                    AlgoConstants.WEIGHT_LIN);
        }
    }
    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */     
    private void getTermWNValues(boolean WordNet_ON) {
        if (WordNet_ON) {            
            // To avoid installing WordNet multiple times
            if(!RecommendationConstants.IS_WORDNET_INSTALLED) {
                linMatchers[AlgoConstants.PORTER_WORDNET] = 
                        new Porter_WordNet(WordNet_ON);
                RecommendationConstants.REF_PORTER_WORDNET = 
                        linMatchers[AlgoConstants.PORTER_WORDNET];
            } else {
                linMatchers[AlgoConstants.PORTER_WORDNET] = 
                        RecommendationConstants.REF_PORTER_WORDNET;
            }
            linMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linMatchers[AlgoConstants.NGRAM] = 
                    new NGram(AlgoConstants.NGRAM_SIZE);            
            RecommendationConstants.IS_WORDNET_INSTALLED = true;            
            perform(AlgoConstants.TERM_WN, linMatchers,
                    AlgoConstants.WEIGHT_LIN);          
        } else {
            linMatchers[AlgoConstants.PORTER_WORDNET] =
                    new Porter_WordNet(WordNet_ON);                   
            linMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linMatchers[AlgoConstants.NGRAM] = 
                    new NGram(AlgoConstants.NGRAM_SIZE);           
            perform(AlgoConstants.TERM_BASIC, linMatchers,
                    AlgoConstants.WEIGHT_LIN);            
        }
    }
        
    /**
     * calculate similarity value with help of some dictionary, 
     * e.x. a specific biological dictionary UMLS
     */ 
    private void getUMLSValue() {     
        //matchers[AlgoConstants.UMLS] = new UMLSKSearch_V6();         
        perform(AlgoConstants.UMLS, matchers[AlgoConstants.UMLS]);        
    }    
    
    /**
     * calculate similarity values based a structural propagation algorithms.
     */ 
    private void getStrValue() {                
        //(new HierarchySearch(this)).propogateSimValue();
    }
    
    private void getLearningValue() {
        AlgoConstants.LEARNING_DIR1 = "/home/rajka62/"
                + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                + "CorpusForTesting/Corpus/100/Mouse";       
        
        AlgoConstants.LEARNING_DIR2="/home/rajka62/"
                + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                + "CorpusForTesting/Corpus/100/Human";
                  
        matchers[AlgoConstants.LEARNING] = 
                new BayesLearning(AlgoConstants.LEARNING_DIR1,
                AlgoConstants.LEARNING_DIR2);        
        perform(AlgoConstants.LEARNING, matchers[AlgoConstants.LEARNING]);        
    }   
    
    /**
     * A linguistic matcher calculate similarity.
     */ 
    private void perform(int matcher, Matcher matcher_single) {        
        Matcher[] matcher_list = {matcher_single};
        double[] weight = {1.0};       
        perform(matcher, matcher_list, weight);        
    }
    
    /**
     * This method calculate similarity values for the two given
     * ontologies and the matcher list.
     * 
     * @param matcher       Matcher 
     * @param matcherList   In matchers like wordnet and wordlist the algorithm
     *                      will use more than one matcher(matching algorithm).
     * @param weight        Used for the matcher's like wordnet and wordlist. 
     */    
    private void perform(int matcher, Matcher[] matcherList, double[] weight) {
         // To store multiple sql statement.      
         ArrayList<String> updateStatement = new ArrayList<String>();
         ArrayList<String> insertStatement = new ArrayList<String>();       
         String matcherColumnName = "matcher"+matcher;
         //pretty name
         String concept1Name;
         String concept2Name;
         //Syn for concept 1
         String symns1;
         boolean isColumnAvailable = simValueTable.isColumnAvailable(
                 matcherColumnName);
         boolean simValueFound, isPairFound;
         // final sim value.
         double simValues;
         
         // creating new column in the savesimvalue table.
         if (!isColumnAvailable) {             
             simValueTable.createColumn(matcherColumnName);              
         }
         for (String concepts:conceptPairs) {
             
             double[] values = new double[weight.length];
             
             String[] conceptIDs = concepts.split(AlgoConstants.SEPERATOR);
             Object concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(conceptIDs[0]);
             Object concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(conceptIDs[1]);
             Pair pair = new Pair(concept1, concept2);
             simValueFound = false; 
             isPairFound = false;
             /**
              * Accessing DB to find the sim value for the concept pair.
              * 
              * result[0]- Is the concept pair available in the DB.
              * result[1]- Is sim value for the concept pair available. 
              */
             boolean[] resultFromDB = simValueTable.getPairParams(conceptIDs[0],
                     conceptIDs[1], matcherColumnName, selectConn);
             
             isPairFound = resultFromDB[0];
             
             if (isPairFound) {
                 simValueFound = resultFromDB[1];
             }
             
             if (!simValueFound) {                 
                
                // Getting pretty name
                concept1Name = ((MElement) pair.getObject1()).getPrettyName();
                concept2Name = ((MElement) pair.getObject2()).getPrettyName();
                // Calculating sim value with pretty name.
                calculate(matcherList, values, concept1Name, concept2Name);                
                // Calculating sim value with pretty synonyms.
                if (((MElement) pair.getObject1()).isMClass() &&
                        ((MElement) pair.getObject2()).isMClass()) {
                    for (Enumeration en1 = ((MClass) pair.getObject1()
                            ).getPrettySyn().elements(); 
                            en1.hasMoreElements();) {
                        
                        symns1 = (String) en1.nextElement();
                        calculate(matcherList, values, symns1, concept2Name);
                        for (Enumeration en2 = ((MClass) pair.getObject2()
                                ).getPrettySyn().elements(); 
                                en2.hasMoreElements();) {
                            calculate(matcherList, values, symns1, 
                                    (String) en2.nextElement());
                            }
                        }
                    for (Enumeration en2 = ((MClass) pair.getObject2()
                            ).getPrettySyn().elements(); 
                            en2.hasMoreElements();) {
                        calculate(matcherList, values, concept1Name, 
                                (String) en2.nextElement());
                    }
                }
                simValues = Comb.weight(values, weight);               
                 /**
                  * Creating new row in the database if the concept pair
                  * is not found in the DB.
                  */                           
                if (!isPairFound) {
                    String statement = simValueTable.generateInsertStatement(
                            conceptIDs[0], conceptIDs[1], matcher, simValues);
                    insertStatement.add(statement);              
                }               
                 /**
                  * If the concept pair is found in the DB then update 
                  * its matcher value in the database.
                  */ 
                else if (isPairFound) {
                    String statement = simValueTable.generateUpdateStatement(
                    conceptIDs[0], conceptIDs[1], matcher, simValues);
                    updateStatement.add(statement);
                }
             }
         }
          
         if (insertStatement.size() > 0) {
             simValueTable.executeStatements(insertStatement, 
                     updateConn);
             delayLine(100);                
         }            
         if (updateStatement.size() >0) {
             simValueTable.executeStatements(updateStatement,
                     updateConn);
             delayLine(100);                
         }
    }
    
    private void calculate(Matcher[] matcher_list, double[] values,
            String str1, String str2) {
        double value; 
        
        for (int i = 0; i < matcher_list.length; i++) {
            value = matcher_list[i].getSimValue(str1, str2);
            if(values[i] < value) {
                values[i] = value;
            }
        }
    }
    
    /**
     * This method create delay in a program.
     * 
     * @param delayTime Delay time in milli second. 
     */
    private void delayLine(int i) {
        try {        
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimValueConstructorUserListPair.class.getName()
                    ).log(Level.SEVERE, null, ex);
        }
        
    }
}
