/*
 * SimValues.java
 *
 */
package se.liu.ida.sambo.algos.matching.algos;

import com.objectspace.jgl.OrderedMap;
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
import se.liu.ida.sambo.jdbc.MappableConceptPairsDB;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.ResultsForCombinationAccessDB;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.Pair;


/**
 * <p>
 * Handles various event related to similarity value computation and finding 
 * mapping suggestions for mappable parts of the ontologies.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class SimValueConstructorMappableGrp {
    
    /**
     * Acts as a temporary database to store concepts of the ontology.
     */
    private OrderedMap ontogy1Content, onto2Content;
    /**
     * To indicate matching process steps(slot matching/class matching).
     */
    private String matchingStep = "";
    /**
     * To manage the ontology contents.
     */
    private OntManager ontManager;
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
    private SinglePairComputation singleComp= new SinglePairComputation();
    /**
     * To avoid creating default constructor.
     */
    private SimValueConstructorMappableGrp() {
        
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
    public SimValueConstructorMappableGrp(final String step, 
            final OntManager ontMan) {
        
        matchingStep = step;
        ontManager = ontMan;

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
            ontogy1Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_1).getProperties();
            onto2Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_2).getProperties();
        }
        // To get the class concepts.
        else if (matchingStep.equalsIgnoreCase("Init class")) {
            ontogy1Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_1).getClasses();
            onto2Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_2).getClasses();
        }   
        // Name of the ontology pair. 
        ontologiesName = AlgoConstants.settingsInfo.getName(
                Constants.ONTOLOGY_1).concat(AlgoConstants.SEPERATOR).concat(
                AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
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
    public double[] loadSimvalueFromDB(final Pair pair, final double[] weight,
            Connection selectConn) {

        // Getting concept's IDs.
        String concept1 = ((MElement) pair.getObject1()).getId();
        String concept2 = ((MElement) pair.getObject2()).getId();

        double[] value = new double[weight.length];

       for (int i = 0; i < weight.length; i++) {
           // weight will be 0 if the matcher is not selected.
           if (weight[i] != 0) {
               value[i] = simValueTable.getSimValue(concept1, concept2, 
                       ("matcher" + i));
               /**
                * If the sim value for a concept pair is not found, then 
                * compute sim value for this concept pair.
                */
               if (value[i] == -1) {
                   value[i] = singleComp.calculateSimilarityValue(i, pair,
                           ontologiesName, sqlConn);
               }           
           } else {
               value[i] = 0;
           }
       }

        return value;
    }

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
    public Vector getPairListSingleMatcher(final double threshold,
            final int matcher) {
        double[] weight = new double[numOfMatchers];
        //Assigning weight 0 to macthers which are not used.
        for (int i = 0; i < numOfMatchers; i++) {
            if (i == matcher) {
                weight[i] = 1;
            } else {
                weight[i] = 0;
            }
        }
        return getPairListSegmentPairs(weight, threshold, "weighted");
    }
    
    
    
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
    public Vector getPairListSegmentPairs(final double[] weight,
            final double threshold, final String combination) {
        
        // List of mapping suggestions to be return.        
        Vector suggestions = new Vector();
        Object concept1 = "";
        Object concept2 = "";
        String concept1ID = "";
        String concept2ID = "";
        String conceptPairs = "";
        double[] values;
        double finalSimValue;
        /**
         * To avoid combining sim value of the HierarchyMatcher with other 
         * matchers.
         */ 
        boolean hierarchyMatcherON = false;
        
        if (weight[AlgoConstants.HIERARCHY] != 0) {
            hierarchyMatcherON = true;        
        }
        Connection selectConn = null;
        // Single SQL connection is used for the entire recommendation process.
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            selectConn = sqlConn;
        } else {
            selectConn = makeConnection();
        }
         
        for (Enumeration e1 = ontogy1Content.elements();
                e1.hasMoreElements();) { 
            
            concept1 = e1.nextElement();        
                
            for (Enumeration e2 = onto2Content.elements();
                    e2.hasMoreElements();) { 
                
                concept2 = e2.nextElement();
                Pair pair = new Pair(concept1,concept2);
                conceptPairs = concept1ID.concat(AlgoConstants.SEPERATOR).concat
                        (concept2ID);
                
                if (mappableConcepts.contains(conceptPairs) && 
                        (((MElement)pair.getObject1()).getAlignElement() == null
                    || ((MElement)pair.getObject2()).getAlignElement() == null)
                        ) { 
                    /**
                     * Getting final similarity value for the selected matchers.
                     */ 
                    values = loadSimvalueFromDB(pair, weight, selectConn);
                    finalSimValue = 0;
                    concept1ID = ((MElement) pair.getObject1()).getId();
                    concept2ID = ((MElement) pair.getObject2()).getId();
                    /**
                     * To avoid combining sim value of the Hierarchy Matcher, 
                     * but add it to the final sim value, in case if the
                     * final sim value is greater than 1 then make it as 1.
                     */ 
                    if (hierarchyMatcherON) {
                        double[] NewWeight = weight;
                        NewWeight[AlgoConstants.HIERARCHY] = 0;
                   
                        if (combination.equalsIgnoreCase("maximum")) {
                            finalSimValue = Comb.max(values, NewWeight);
                        } else {
                            finalSimValue = Comb.average(values, NewWeight);
                        }
                        
                        finalSimValue += values[AlgoConstants.HIERARCHY];
                        if (finalSimValue > 1) {
                            finalSimValue = 1;
                        }
                  } 
                    /**
                     * Hierarchy Matcher will not be taken into account if the
                     * combination method is maximum based.
                     */
                    else {                        
                        if (combination.equalsIgnoreCase("maximum")) {
                          finalSimValue = Comb.max(values, weight);
                      } else {
                          finalSimValue = Comb.average(values, weight);
                      }                  
                  }
                    
                    if (finalSimValue >= threshold) {
                        /**
                         * Segment pairs used in the recommendation process will
                         * have its own onto manager, so aligned suggestions
                         * wont affect the recommendation process, but this if 
                         * block is needed since this method is used for the
                         * slot matching process.
                         */ 
                        if(!AlignmentConstants.IsAligned.
                                contains(conceptPairs)) { 
                            pair.setSim(finalSimValue);
                            suggestions.add(pair);
                        }
                    }
                }
            }
        }
        /**
         * Non recommendation process will use its own SQL connection
         * (we are doing this for performance gain), so this step will close
         * those connections.
         */ 
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(selectConn);
                }
        return suggestions;
    }
    
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
        Object concept1 = "";
        Object concept2 = "";                
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
            concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(concept1ID);
            concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(concept2ID);
            conceptPairs = concept1ID.concat(AlgoConstants.SEPERATOR).concat
                        (concept2ID);
            // Generating concept pairs and adding to the suggestion list.
            if ( !concept1.equals("") && !concept2.equals("")) {
                Pair pair = new Pair(concept1,concept2);
                // Checking if the suggestion is aligned in the previous round.
                if(mappableConcepts.contains(conceptPairs) && (((MElement)pair.
                        getObject1()).getAlignElement() == null
                    || ((MElement)pair.getObject2()).getAlignElement() == null)
                        ) {
                                   
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
    public Vector getPairListSegmentPairs(double[] weight,
            double upperthreshold, double lowerthreshold, String combination) {
               
        // List of final mapping suggestions.        
        Vector suggestion = new Vector();
        // List of mapping suggestions between upper and lower threshold.
        Vector unprocessedSuggestions = new Vector();  
        // Getting suggestions above or equal to lower threshold.
        Iterator pairs = getPairListSegmentPairs(weight, lowerthreshold,
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
        while(dftResult!= null && dftResult.hasNext()) {
                suggestion.add(dftResult.next());
            }
        return suggestion;
    }    
    
    /**
     * This method starts the computation for the specified matcher.
     * 
     * @param matcher   Matcher whose sim value computation should be start. 
     */
    public void calculateSimilarityValue(int matcher) {
               
        switch(matcher){
            case AlgoConstants.EDIT_DISTANCE:
                if(!matchersCompResult[AlgoConstants.EDIT_DISTANCE]) {
                    getEditDistanceValues();
                }
                break;
            case AlgoConstants.NGRAM:
                if(!matchersCompResult[AlgoConstants.NGRAM]) {
                    getNGramValues();
                }
                break;
            case AlgoConstants.LIN_COMBINATION:
                if(!matchersCompResult[AlgoConstants.LIN_COMBINATION]) {
                    getLinValues(false);
                }
                break;
            case AlgoConstants.LIN_COMBINATION_PLUS:
                if(!matchersCompResult[AlgoConstants.LIN_COMBINATION_PLUS]) {
                    getLinValues(true);
                }
                break;
            case AlgoConstants.TERM_BASIC:
                if(!matchersCompResult[AlgoConstants.TERM_BASIC]) {
                    getTermWNValues(false);
                }
                break;
            case AlgoConstants.TERM_WN:
                if(!matchersCompResult[AlgoConstants.TERM_WN]) {
                    getTermWNValues(true);
                }
                break;
            case AlgoConstants.UMLS:
                if(!matchersCompResult[AlgoConstants.UMLS]) {
                    getUMLSValue();
                }
                break;
            case AlgoConstants.HIERARCHY:
                    getStrValue();
                break;
            case AlgoConstants.LEARNING:
                if(!matchersCompResult[AlgoConstants.LEARNING]) {
                    getLearningValue();
                }
                break;
        }
    }

    private void getEditDistanceValues() {
        
        matchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
        perform(AlgoConstants.EDIT_DISTANCE, 
                matchers[AlgoConstants.EDIT_DISTANCE]);
                
        if(!AlgoConstants.SET_INTERRUPT_ON && 
                !AlgoConstants.ISRECOMMENDATION_PROCESS) {
            matchersCompResult[AlgoConstants.EDIT_DISTANCE] = true;
        }
    }

    private void getNGramValues() { 
        
        matchers[AlgoConstants.NGRAM] =  new NGram(3);
        perform(AlgoConstants.NGRAM,matchers[AlgoConstants.NGRAM]);
        
        
        if(!AlgoConstants.SET_INTERRUPT_ON &&
                !AlgoConstants.ISRECOMMENDATION_PROCESS) {
            matchersCompResult[AlgoConstants.NGRAM] = true;
        }
    }
    /** 
     * calculate the linguistic Sim Value using Porter, 
     * Ngram and EditDistance individually.
     */ 
    private void getLinValues(boolean WordNet_ON){  
        
        linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                new Porter_WordNet(WordNet_ON);
        linsMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
        linsMatchers[AlgoConstants.NGRAM] = new NGram(AlgoConstants.NGRAM_SIZE);       
        
        if (WordNet_ON) {             
            perform(AlgoConstants.LIN_COMBINATION_PLUS,
                    linsMatchers, AlgoConstants.WEIGHT_LIN);
            if(!AlgoConstants.SET_INTERRUPT_ON &&
                    !AlgoConstants.ISRECOMMENDATION_PROCESS) {
                matchersCompResult[AlgoConstants.LIN_COMBINATION_PLUS] = true;
            }            
        } else {  
            perform(AlgoConstants.LIN_COMBINATION, linsMatchers,
                    AlgoConstants.WEIGHT_LIN); 
            if(!AlgoConstants.SET_INTERRUPT_ON &&
                    !AlgoConstants.ISRECOMMENDATION_PROCESS) {
                matchersCompResult[AlgoConstants.LIN_COMBINATION] = true;
            }
        }
    }    
    /**
     * calculate the linguistic SimValue using Porter,
     * Ngram and EditDistance individually.
     */     
    private void getTermWNValues(boolean WordNet_ON) {
        
        linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                new Porter_WordNet(WordNet_ON);
        linsMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
        linsMatchers[AlgoConstants.NGRAM] = new NGram(AlgoConstants.NGRAM_SIZE); 

        if (WordNet_ON) {
            perform(AlgoConstants.TERM_WN, linsMatchers,
                    AlgoConstants.WEIGHT_LIN);            
            if(!AlgoConstants.SET_INTERRUPT_ON &&
                    !AlgoConstants.ISRECOMMENDATION_PROCESS) {
                matchersCompResult[AlgoConstants.TERM_WN] = true;
            }
        } else {
            perform(AlgoConstants.TERM_BASIC, linsMatchers,
                    AlgoConstants.WEIGHT_LIN);            
            if(!AlgoConstants.SET_INTERRUPT_ON &&
                    !AlgoConstants.ISRECOMMENDATION_PROCESS) {
                matchersCompResult[AlgoConstants.TERM_BASIC] = true;
            }
        }
    }
        
    /**
     * calculate similarity value with help of some dictionary, 
     * e.x. a specific biological dictionary UMLS
     */ 
    private void getUMLSValue() {        
        matchers[AlgoConstants.UMLS] = new UMLSKSearch_V6();  
        perform(AlgoConstants.UMLS, matchers[AlgoConstants.UMLS]);
        
        if(!AlgoConstants.SET_INTERRUPT_ON &&
                !AlgoConstants.ISRECOMMENDATION_PROCESS) {
            matchersCompResult[AlgoConstants.UMLS] = true;
        }
    }
    
    /**
     * calculate similarity value based a structural propagation 
     * algorithms.
     */
    private void getStrValue() {
        HierarchySearch Struct=new HierarchySearch(ontogy1Content,
                onto2Content);
        Struct.propogateSimValue();
    }    
    
    
    private void getLearningValue() { 
     AlgoConstants.LEARNING_DIR1="/home/rajka62/"
             + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
             + "CorpusForTesting/Corpus/100/Mouse";
     AlgoConstants.LEARNING_DIR2="/home/rajka62/"
             + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
             + "CorpusForTesting/Corpus/100/Human";
                
     matchers[AlgoConstants.LEARNING] = new BayesLearning(
             AlgoConstants.LEARNING_DIR1, AlgoConstants.LEARNING_DIR2);
        
     perform(AlgoConstants.LEARNING, matchers[AlgoConstants.LEARNING]);
        
     if(!AlgoConstants.SET_INTERRUPT_ON &&
             !AlgoConstants.ISRECOMMENDATION_PROCESS) {
            matchersCompResult[AlgoConstants.LEARNING] = true;
        }
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
        
        int computationCounter = 0;
        // To store multiple sql statement
        ArrayList<String> updateStatement = new ArrayList<String>();
        ArrayList<String> insertStatement = new ArrayList<String>();
        Connection updateConn = null;
        Connection selectConn = null;
        String matcherColumnName;
        boolean isColumnAvailable;
        long startTime = System.currentTimeMillis();
        Object concept1;
        Object concept2;
        Pair pair;                
        double[] values;                
        boolean simValueFound, isPairFound;                
        //Final similarity value
        double finalSimValues;                
        //pretty name
        String concept1Name;
        String concept2Name;               
        //conceptID
        String concept1ID;
        String concept2ID;                
        //Syn for concept 1
        String symn1;
        
        /**
         * Single SQL connection is used for the entire recommendation process.
         * 
         * Separate SQL connection will increases the performance, but the
         * segment pairs recommendation process will create an instance
         * of this class for each segment pair, so creating separate SQL
         * connection during the recommendation process will lead to the SQL 
         * exceptions.
         */
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            updateConn = sqlConn;
            selectConn = sqlConn;                   
        } else {
            updateConn = makeConnection();
            selectConn = makeConnection();                    
        }
        
        matcherColumnName = "matcher"+matcher;
        isColumnAvailable = simValueTable.isColumnAvailable(
                matcherColumnName);
        // create new column in the savesimvalue table    
        if (!isColumnAvailable) {            
            simValueTable.createColumn(matcherColumnName);
        }   
        concept1="";
        concept2="";
                
        for(String concepts : mappableConcepts) {
            
            String[] conceptIDs = concepts.split(AlgoConstants.SEPERATOR);
            
            // Quering ontoManager with conceptID.
            concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(conceptIDs[0]);
            concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(conceptIDs[1]);
            pair = new Pair(concept1,concept2);
            
            if (AlgoConstants.STOPMATACHING_PROCESS) {
                //Reseting this variable 
                AlgoConstants.STOPMATACHING_PROCESS = false;
                /**
                 * To apply interrupt to other matchers if the alignment 
                 * strategy has more than one matcher.
                 */ 
                AlgoConstants.SET_INTERRUPT_ON = true;
                AlgoConstants.STOP_COMPUTATION_AT = computationCounter-1;
                AlgoConstants.USER_INTERRUPT_AT = computationCounter -1;
                break;
            }
            /**
             * To remember at which concept pair user interrupted the
             * computation.
             */                
            if (AlgoConstants.SET_INTERRUPT_ON &&
                    AlgoConstants.STOP_COMPUTATION_AT == computationCounter) {                
                break;                
            }
            computationCounter++;            
            values = new double[weight.length];
                
            // Getting pretty name
            concept1Name = ((MElement) pair.getObject1()).getPrettyName();
            concept2Name = ((MElement) pair.getObject2()).getPrettyName();
            //concept ID
            concept1ID = ((MElement) pair.getObject1()).getId();
            concept2ID = ((MElement) pair.getObject2()).getId();
            simValueFound=false; 
            isPairFound=false;                
            /**
             * Accessing DB to find the sim value for the concept pair.
             * 
             * result[0]- Is the concept pair available in the DB.
             * result[1]- Is sim value for the concept pair available. 
             */                
            boolean[] resultFromDB = simValueTable.getPairParams(concept1ID, 
                    concept2ID, matcherColumnName, selectConn);
                
            isPairFound = resultFromDB[0];
                
            if (isPairFound) {
                simValueFound = resultFromDB[1];                
            }
            
            if (!simValueFound) {
                // Calculating sim value with pretty name.                 
                calculate(matcherList, values, concept1Name, concept2Name);                
                //Calculating sim value with pretty synonyms
                if (((MElement) pair.getObject1()).isMClass() && 
                        ((MElement) pair.getObject2()).isMClass()) {
                    
                    for (Enumeration en1 = ((MClass) pair.getObject1()
                            ).getPrettySyn().elements();
                            en1.hasMoreElements();) {
                        
                        symn1 = (String) en1.nextElement();
                        calculate(matcherList, values, symn1, concept2Name);
                        for (Enumeration en2 = ((MClass) pair.getObject2()
                                ).getPrettySyn().elements();
                                en2.hasMoreElements();) {
                            calculate(matcherList, values, symn1,
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
                finalSimValues = Comb.weight(values, weight);    
                /**
                 * Creating new row in the database if the concept pair
                 * is not found in the DB.
                 */                             
                if (!isPairFound) {
                    String statement = simValueTable.generateInsertStatement
                            (concept1ID, concept2ID, matcher, finalSimValues);
                    insertStatement.add(statement);                    
                } 
                /**
                 * If the concept pair is found in the DB then update 
                 * its matcher value in the database.
                 */ 
                else if (isPairFound) {
                    String statement = simValueTable.generateUpdateStatement(
                    concept1ID, concept2ID, matcher, finalSimValues);
                    updateStatement.add(statement);                       
                }               
            }
                
            if (insertStatement.size() > 100000) {
                simValueTable.executeStatements(insertStatement,
                        updateConn);                
                /**
                 * Computation of matchers like EditDistance and NGram are
                 * faster so delay will make sure that the sim values are 
                 * stored in the DB.
                 */
                if(matcherList.length<3) {
                    delayLine(50);                    
                }
                insertStatement.clear();                
            }
            if (updateStatement.size() > 100000) {
                simValueTable.executeStatements(updateStatement,
                        updateConn);
                if(matcherList.length<3) {
                    delayLine(50);                   
                }
                updateStatement.clear();               
            }        
        }        
        
        //If we have atleast one insert statement.
        if (insertStatement.size() > 0) {
            simValueTable.executeStatements(insertStatement, 
                    updateConn);
            if (matcherList.length < 3) {
                delayLine(50);
            }            
        }           
        //If we have atleast one update statement.
        if (updateStatement.size() > 0) {
            simValueTable.executeStatements(updateStatement,
                    updateConn);
            if( matcherList.length < 3) {
                delayLine(50);
            }            
        }
        long endTime = System.currentTimeMillis();            
        System.out.println("COMPUTATION DONE----------------------------"
                + "---------"+computationCounter);
        System.out.println( "Time Taken to do computation " +
                (endTime-startTime) + " ms" );

        
        if (!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(updateConn);          
            ResourceManager.close(selectConn);
        }
    }
    
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
