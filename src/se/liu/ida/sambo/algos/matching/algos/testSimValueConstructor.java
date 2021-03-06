/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelProgramingtry.testSplitSimValueCalculationTask;
import se.liu.ida.PRAalg.testdtfPRA;
import se.liu.ida.sambo.MModel.testMClass;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.Task;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.jdbc.simvalue.ResultsForCombinationAccessDB;
import se.liu.ida.sambo.jdbc.simvalue.*;
import se.liu.ida.sambo.util.testPair;

/**
 *
 * @author huali50
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public final class testSimValueConstructor {

    /**
     * Acts as a temporary database to store concepts of the ontology.
     */
    private testMergerManager mergermanager;
    private testOntManager ontmanager;
    private testMOntology source_ontology;
    private testMOntology target_ontology;
    private Set<Integer> source_content;
    private Set<Integer> target_content;
    /**
     * To indicate matching process steps(slot matching/class matching).
     */
    private String matchingStep = "";

    /**
     * SQL connection to accessing stored computation results.
     */
    private Connection sqlConn = null;
    /**
     * No.of avaliable matchers.
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
    private SimilarityGenerateQuery simValueTable;
    private MapConceptGenerateQuery mapconceptTable;
    private SimilarityGenerateQuery similarityTable;
    /**
     * For querying the computation results availability table.
     */
    private ResultsForCombinationAccessDB matcherResultTable;
    /**
     * Linguistic Matchers.
     */
    private Matcher[] linsMatchers = new Matcher[AlgoConstants.LIN_MATCHER_NUM];
    /**
     * To compute similarity value for a single conceptPair.
     */
    private testSinglePairComputation singleComp;

    /**
     * <p>
     * This constructor establishes a connection to access the SQL server,
     * initialize ontology contents etc.
     * </p>
     *
     * @param step Matching step.
     * @param ontMan Ontologies content manager.
     */
    public testSimValueConstructor(final String step, final testOntManager ontMan, testMergerManager merge) {

        matchingStep = step;
        this.ontmanager = ontMan;
        this.mergermanager = merge;
        this.source_ontology = ontmanager.getontology(Constants.ONTOLOGY_1);
        this.target_ontology = ontmanager.getontology(Constants.ONTOLOGY_2);
        singleComp = new testSinglePairComputation(this.ontmanager);
        /**
         * To make sure that the system use single SQL connection for the entire
         * recommendation process.
         */
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            sqlConn = RecommendationConstants.SQL_CONN;
        } else {
            sqlConn = makeConnection();
        }
        // To get slot concepts.
        if (matchingStep.equalsIgnoreCase("Init slot")) {
            source_content = this.source_ontology.getProperties();
            target_content = this.target_ontology.getProperties();
        } // To get class concepts.
        else if (matchingStep.equalsIgnoreCase("Init class")) {
            source_content = this.source_ontology.getMClasses();
            target_content = this.target_ontology.getMClasses();
        }
        // Name of the ontology pair. 
        //ontologiesName = AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1).concat(AlgoConstants.SEPERATOR).concat(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        // To query sim value table
        ontologiesName = "";
        simValueTable = new SimilarityGenerateQuery(sqlConn);
        matcherResultTable = new ResultsForCombinationAccessDB(sqlConn);
        mapconceptTable = new MapConceptGenerateQuery(sqlConn);
        similarityTable = new SimilarityGenerateQuery(sqlConn);
        // Checking availiablity of the computation results of all matchers.
        for (int i = 0; i < numOfMatchers; i++) {

            // Name of the matcher in the database.
            String matcherName = "matcher" + i;
            /**
             * resultForMatcher[0]- Is column for the matcher is available in
             * savesimvalues table.
             *
             * resultForMatcher[1]- Is complete computation result for the user
             * selected pairs of the ontologies is available in the data base.
             * (i.e) previous CS is done without any interrupt
             */
            boolean[] resultForMatcher = matcherResultTable.getMatcherInfo(
                    ontologiesName, matcherName, sqlConn);
            /**
             * In current implementation computation results of slot matching
             * processes is not taken into account.
             */
            if (resultForMatcher[0] && resultForMatcher[1]
                    && !matchingStep.equalsIgnoreCase("Init slot")) {
                //this.matchersCompResult[i] = true;
            } else {
                this.matchersCompResult[i] = false;
            }
        }
    }

    /**
     * This method is called during the recommendation process, to get a
     * similarity value for a single concept pair.
     *
     * @param conceptPair conceptPair.
     * @param weight Matchers weight.
     * @param selectConn SQL server connection.
     *
     * @return Similarity value for the selected matchers.
     */
    public double[] loadSimvalueFromDB(final testPair pair, final double[] weight,
            Connection selectConn) {

        // Getting concept's IDs.
        //int source_concept_id = pair.getsourceid();
        //int target_concept_id = pair.gettargetid();
        double[] value = new double[weight.length];

        for (int i = 0; i < weight.length; i++) {
            // weight will be 0 if the matcher is not selected.
            if (weight[i] != 0) {
                //value[i] = simValueTable.getSimValue(source_concept_id, target_concept_id,i);
                /**
                 * SimValue for the matcher is not found, then the compute the
                 * SimValue for this particular concept pair.
                 */
                if (value[i] == -1) {
                    //value[i] = singleComp.calculateSimilarityValue(i, pair,ontologiesName, sqlConn);
                }
            } else {
                value[i] = 0;
            }
        }

        return value;
    }

    /**
     * This method create new MySQL server connection.
     *
     * @return conn New MySQL server connection.
     */
    private Connection makeConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return conn;
    }

    /**
     * To get mapping suggestions for a single matcher, usually used in slot
     * matching, segment pairs recommendation process.
     *
     * @param threshold Single threshold value for filtering mapping
     * suggestions.
     * @param matcher matcher.
     *
     * @return List of mapping suggestions.
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
     * To get mapping suggestions if more than one matcher is selected, usually
     * used in slot matching, segment pairs recommendation process.
     *
     * @param threshold Single threshold value for filtering mapping
     * suggestions.
     * @param weight Weight of the matchers, if the matcher is not selected then
     * its weight should be 0.
     * @param combination Combination method(weighted/maxBased).
     *
     * @return List of mapping suggestions.
     */
    public Vector getPairListSegmentPairs(final double[] weight,final double threshold, final String combination) {

        // List of pairs to be return.        
        Vector suggestions = new Vector();

        String concept1ID = "";
        String concept2ID = "";
        double[] values;
        double finalSimValue;
        /**
         * To avoid combining simValue of the HierarchyMatcher with other
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
        for (Integer i : source_content) {
            String source_element_uri = source_ontology.getURITable().getURI(i);
            concept1ID = source_ontology.getElement(source_element_uri).getLocalName();
            //String source_class_local_name = source_ontology.getURITable().getLocalname(source_class_uri);
            for (Integer j : target_content) {
                String target_element_uri = target_ontology.getURITable().getURI(j);
                concept2ID = target_ontology.getElement(target_element_uri).getLocalName();
                //String target_class_local_name = source_ontology.getURITable().getLocalname(target_class_uri);
                testPair pair = new testPair(source_element_uri, target_element_uri);
                values = loadSimvalueFromDB(pair, weight, selectConn);
                finalSimValue = 0;

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

                } /**
                 * The Hierarchy Matcher will not be taken into account if the
                 * combination method is maximum based.
                 */
                else if (combination.equalsIgnoreCase("maximum")) {
                    finalSimValue = Comb.max(values, weight);
                } else {
                    finalSimValue = Comb.average(values, weight);
                }

                if (finalSimValue >= threshold) {
                    /**
                     * Segment pairs used in the recommendation process will
                     * have its own onto manager, so aligned suggestions wont
                     * affect the recommendation process, but this step is
                     * needed since this method is used for the slot matching
                     * process.
                     */
                    if (!AlignmentConstants.IsAligned.
                            contains(concept1ID + AlgoConstants.SEPERATOR
                                    + concept2ID)) {
                        pair.setSim(finalSimValue);
                        suggestions.add(pair);
                    }
                }
            }
        }

        /**
         * Non recommendation process will use its own SQL connection (we are
         * doing this for the performance gain), so this step will close those
         * connections.
         */
        if (!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(selectConn);
        }
        return suggestions;
    }
    public Vector getPropertyList(double[] weight, double threshold, String combination, int step, int moid){
        Vector suggestions = new Vector();
        String concept1 = "";
        String concept2 = "";
        String concept1ID = "";
        String concept2ID = "";
        Connection selectConn = null;
        double finalSimValue = 0;
        boolean hierarchyMatcherON = false;
        ArrayList<String> queryResult = null;

        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            selectConn = sqlConn;
        } else {
            selectConn = makeConnection();
        }

        if (weight[AlgoConstants.HIERARCHY] != 0) {
            hierarchyMatcherON = true;
        }
        queryResult = simValueTable.generateWeightedBasedSql(weight, threshold,step,moid);
        for (String concept_pair : queryResult) {

            String[] resultParams = concept_pair.split("#");
            int concept_pairId = Integer.valueOf(resultParams[0]).intValue();
            finalSimValue = Double.valueOf(resultParams[1]).doubleValue();

            String concepts = mapconceptTable.getconcepts(concept_pairId);
            String[] conceptsParams = concepts.split("#");
            concept1ID = conceptsParams[0];
            concept2ID = conceptsParams[1];
            //finalSimValue = Double.valueOf(resultParams[2]).doubleValue();                    

            /**
             * To avoid combining sim value of the Hierarchy Matcher, but add
             * its sim value to final sim value, in case if the final sim value
             * is greater than 1 then make it as 1.
             */
            if (hierarchyMatcherON && finalSimValue > 1) {
                finalSimValue = 1;
            }
            concept1 = source_ontology.getElementURI(concept1ID);
            concept2 = target_ontology.getElementURI(concept2ID);

            // Generating concept pairs and adding to the suggestion list.
            if (!concept1ID.equals("") && !concept2ID.equals("")) {
                testPair pair = new testPair(concept1, concept2);
                // Checking if the suggestion aligned in the previous round.
                if (source_ontology.getElement(concept1).getAlignElement() == null || target_ontology.getElement(concept2).getAlignElement() == null) {
                    if (!AlignmentConstants.IsAligned.contains(
                            concept1ID + AlgoConstants.SEPERATOR + concept2ID)) {
                        pair.setSim(finalSimValue);
                        suggestions.add(pair);
                    }
                }
            }
        }
        if (!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(selectConn);
        }
        return suggestions;
    }
    /**
     * To get mapping suggestions if more than one matcher is selected, usually
     * used in the class matching (non recommendation process).
     *
     * @param threshold Single threshold value for filtering mapping
     * suggestions.
     * @param weight weight of the matchers, if the matcher is not selected then
     * its weight should be 0.
     * @param combination combination method(weighted/maxBased).
     *
     * @return List of mapping suggestions.
     */
    public Vector getPairList(double[] weight, double threshold, String combination,int step,int moid) {

        // List of pairs to be return.    
        Vector suggestions = new Vector();
        String concept1 = "";
        String concept2 = "";
        String concept1ID = "";
        String concept2ID = "";
        Connection selectConn = null;
        double finalSimValue = 0;

        /**
         * To avoid combining simValue of the HierarchyMatcher with the other
         * matchers.
         */
        boolean hierarchyMatcherON = false;
        ArrayList<String> queryResult = null;

        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            selectConn = sqlConn;
        } else {
            selectConn = makeConnection();
        }

        if (weight[AlgoConstants.HIERARCHY] != 0) {
            hierarchyMatcherON = true;
        }
        if (combination.equalsIgnoreCase("maximum")) {
            queryResult = simValueTable.generateMaximumBasedSql(weight, threshold,step,moid);
        } else {
            queryResult = simValueTable.generateWeightedBasedSql(weight, threshold,step,moid);
        }
        /**
         * The above query will return results in the form of array list, so in
         * this step we will convert these array list into concept pairs.
         */
        ArrayList<String> Statements = new ArrayList<String>();
        for (String concept_pair : queryResult) {

            String[] resultParams = concept_pair.split("#");
            int concept_pairId = Integer.valueOf(resultParams[0]).intValue();
            finalSimValue = Double.valueOf(resultParams[1]).doubleValue();

            String concepts = mapconceptTable.getconcepts(concept_pairId);
            String[] conceptsParams = concepts.split("#");
            concept1ID = conceptsParams[0];
            concept2ID = conceptsParams[1];
            //finalSimValue = Double.valueOf(resultParams[2]).doubleValue();                    

            /**
             * To avoid combining sim value of the Hierarchy Matcher, but add
             * its sim value to final sim value, in case if the final sim value
             * is greater than 1 then make it as 1.
             */
            if (hierarchyMatcherON && finalSimValue > 1) {
                finalSimValue = 1;
            }
            concept1 = source_ontology.getElementURI(concept1ID);
            concept2 = target_ontology.getElementURI(concept2ID);

            // Generating concept pairs and adding to the suggestion list.
            if (!concept1ID.equals("") && !concept2ID.equals("")) {
                testPair pair = new testPair(concept1, concept2);
                // Checking if the suggestion aligned in the previous round.
                if (source_ontology.getElement(concept1).getAlignElement() == null || target_ontology.getElement(concept2).getAlignElement() == null) {
                    if (!AlignmentConstants.IsAligned.contains(
                            concept1ID + AlgoConstants.SEPERATOR + concept2ID)) {
                        pair.setSim(finalSimValue);
                        suggestions.add(pair);
                    }
                }
            }
        }

        if (!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(selectConn);
        }

        return suggestions;
    }
    public Vector getPairListLocal(double[] weight, double downthreshold, String combination,int step){
        Vector suggestions = new Vector();
        suggestions = mergermanager.matchexecutor(mergermanager.getExecutorlist(), downthreshold, 0);
        return suggestions;
    }
    public Vector<testPair> getPairListLocal(double[] weight, double upthreshold, double downthreshold, String combination,int step) {
        Vector suggestions = new Vector();
        suggestions = mergermanager.matchexecutor(mergermanager.getExecutorlist(), downthreshold, upthreshold);
        return suggestions;
    }
    /**
     * To get mapping suggestions for the double threshold filtering, if more
     * than one matcher is selected, usually used in the class matching (non
     * recommendation process).
     *
     * @param weight weight of the matchers, if the matcher is not selected then
     * its weight should be 0.
     * @param upperthreshold Higher threshold.
     * @param lowerthreshold Lower threshold.
     * @param combination combination method(weighted/maxBased).
     *
     * @return List of mapping suggestions.
     */
    public Vector<testPair> getPairList(double[] weight, double upperthreshold, double lowerthreshold, String combination,int step,int moid) {
        // List of mapping suggestions to be return.        
        Vector suggestion = new Vector<testPair>();
        // List of mapping suggestions between upper and lower threshold.
        Vector unprocessedSuggestions = new Vector();
        // Getting mapping suggestions above or equal to lower threshold.
        Iterator pairs = getPairList(weight, lowerthreshold,combination,step,moid).iterator();
        /**
         * Separating mapping suggestions. (i.e) The suggestions with a simvalue
         * greater than or equals to upper threshold will be added to the final
         * suggestion list, the suggestions between upper and lower threshold
         * will be added to the unprocessed suggestions list which will be
         * further filtered by the dtf algorithm.
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
        } else if (unprocessedSuggestions.size() > 0) {
            dftResult = unprocessedSuggestions.iterator();

        }
        while (dftResult != null && dftResult.hasNext()) {
            suggestion.add(dftResult.next());
        }
        return suggestion;

    }

    /**
     * To get mapping suggestions for the double threshold filtering, if more
     * than one matcher is selected, usually used in the recommendation process.
     *
     * @param weight weight of the matchers, if the matcher is not selected then
     * its weight should be 0.
     * @param upperthreshold Higher threshold.
     * @param lowerthreshold Lower threshold.
     * @param combination combination method(weighted/maxBased).
     *
     * @return List of mapping suggestions.
     */
    public Vector getPairListSegmentPairs(double[] weight,
            double upperthreshold, double lowerthreshold, String combination) {

        // List of mapping suggestions to be return.        
        Vector suggestion = new Vector();
        // List of mapping suggestions between upper and lower threshold.
        Vector unprocessedSuggestions = new Vector();
        // Getting mapping suggestions above or equal to lower threshold.
        Iterator pairs = getPairListSegmentPairs(weight, lowerthreshold,
                combination).iterator();
        /**
         * Separating suggestions. (i.e) The suggestions with the simvalue
         * greater than or equal to upper the threshold will be added to the
         * final mapping suggestion list, the suggestions between upper and
         * lower threshold will be added to the unprocessed suggestions list
         * which will be further filtered by the dtf algorithm.
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
         * We need suggestions greater than or equals to the upper threshold to
         * get consistent group and of course suggestions between upper and
         * lower threshold to apply the dtf algorithm.
         */
        Iterator dftResult = null;
        if (suggestion.size() > 0 && unprocessedSuggestions.size() > 0) {
            testdtfPRA dtf = new testdtfPRA(source_ontology,
                    target_ontology, suggestion);
            dftResult = dtf.getResults(unprocessedSuggestions,
                    upperthreshold, lowerthreshold).iterator();
        } else if (unprocessedSuggestions.size() > 0) {
            dftResult = unprocessedSuggestions.iterator();

        }
        while (dftResult != null && dftResult.hasNext()) {
            suggestion.add(dftResult.next());
        }
        return suggestion;
    }

    /**
     * This method used in the session loading, it returns remaining mapping
     * suggestions that are not validated by the user yet.
     *
     * @return List of mapping suggestions yet to be validate by the user.
     */
    public Vector loadPairList() {

        Vector remainingSuggestions = new Vector();
        Iterator itrXMLFile = Commons.XMLSuggestionVector.iterator();
        String concept1;
        String concept2;
        String concept1ID;
        String concept2ID;
        testPair pair;
        String[] floatContent = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "."};
        List<String> floatCont = Arrays.asList(floatContent);

        double[] simValues;
        double[] defaultSimValues = new double[numOfMatchers];
        int lastIndexCommaPair;
        //initialize the defaultSimValues
        for (int i = 0; i < numOfMatchers; i++) {
            defaultSimValues[i] = 0;
        }

        while (itrXMLFile.hasNext()) {

            String suggestions = itrXMLFile.next().toString();
            concept1 = "";
            concept2 = "";
            // Getting concept 1 from the ontoManager
            for (Integer i : source_content) {
                concept1 = source_ontology.getURITable().getURI(i);
                concept1ID = source_ontology.getElement(concept1).getLocalName();

                /**
                 * Note suggestions="[[class:MA_0001328,1],
                 * [class:MESH_A.04.531.591.940,2], 0.4349019607843137]" and
                 * concept1ID.toString()= "[class:MA_0001328,1]" so using
                 * contains won't raise error here.
                 */
                if (suggestions.contains(concept1ID)) {
                    break;
                }
            }
            // Getting concept 2 from the ontoManager 
            for (Integer j : target_content) {
                concept2 = target_ontology.getURITable().getURI(j);
                concept2ID = target_ontology.getElement(concept2).getLocalName();
                if (suggestions.contains(concept2ID)) {
                    break;
                }
            }

            // Generating pairs.
            pair = new testPair(concept1, concept2);
            lastIndexCommaPair = pair.toString().lastIndexOf(",");
            // Checking if the suggestion is aligned.
            /*
            if(((MElement)pair.getObject1()).getAlignElement() == null
                    || ((MElement)pair.getObject2()
                    ).getAlignElement() == null ) {             
                
             */
            if (true) {
                int lastIndexBracketPair = pair.toString().lastIndexOf("]");
                /**
                 * This process will eliminate the conceptPair IDs from the XML
                 * suggestion.
                 */
                String strSimValue = suggestions.substring(lastIndexCommaPair,
                        lastIndexBracketPair);
                /**
                 * This process will eliminate non numeric characters but retain
                 * dot.
                 *
                 * eg: " A[0.8" will be retained as "0.8".
                 */
                for (int i = 0; i < strSimValue.length(); i++) {

                    String charAtIndex = Character.toString(
                            strSimValue.charAt(i));
                    if (!floatCont.contains(charAtIndex)) {
                        strSimValue = strSimValue.replace(charAtIndex, "");
                    }

                }
                //initialize the SimValues                        
                simValues = defaultSimValues;

                if (simValues[AlgoConstants.FINAL_VALUE]
                        != AlgoConstants.ALIGNMENT) {
                    simValues[AlgoConstants.FINAL_VALUE]
                            = Double.parseDouble(strSimValue);
                    pair.setSim(simValues[AlgoConstants.FINAL_VALUE]);
                    remainingSuggestions.add(pair);
                }
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date).toString());

        return remainingSuggestions;
    }

    /**
     * This method starts the computation for the specified matcher.
     *
     * @param matcher Matcher whose sim value computation should be start.
     */
    /**
     * calculate the linguistic Sim Value using Porter, Ngram and EditDistance
     * individually.
     */
    private void getLearningValue() {
        AlgoConstants.LEARNING_DIR1 = "/home/rajka62/"
                + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                + "CorpusForTesting/Corpus/100/Mouse";
        AlgoConstants.LEARNING_DIR2 = "/home/rajka62/"
                + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                + "CorpusForTesting/Corpus/100/Human";

        matchers[AlgoConstants.LEARNING] = new BayesLearning(
                AlgoConstants.LEARNING_DIR1, AlgoConstants.LEARNING_DIR2);

        perform(AlgoConstants.LEARNING, matchers[AlgoConstants.LEARNING]);

        if (!AlgoConstants.SET_INTERRUPT_ON
                && !AlgoConstants.ISRECOMMENDATION_PROCESS) {
            matchersCompResult[AlgoConstants.LEARNING] = true;
        }
    }

    /**
     * A linguistic matcher calculate similarity.
     */
    private void perform(int matcher, Matcher matcher_single) {
        //Matcher[] matcher_list = {matcher_single};
        double[] weight = {1.0};
        //perform(matcher, matcher_single, weight);        
    }

    /**
     * This method calculate similarity values for the two given ontologies and
     * the matcher list.
     *
     * @param matcher Matcher
     * @param matcherList In matchers like wordnet and wordlist the algorithm
     * will use more than one matcher(matching algorithm).
     * @param weight Used for the matcher's like wordnet and wordlist.
     */
    /**
     * This method use multi thread technique to calculate the similarity
     * values.
     *
     * (Note:) It increases the system performances.
     *
     * @param matcher
     * @param matcherList
     * @param weight
     */
    /*
    private void multiThreadPerform(int matcher, Matcher[] matcherList, 
            double[] weight) {
        
        String matcherColumnName = "matcher"+matcher;
        boolean isColumnAvailable = testsimValueTable.isColumnAvailable(
                matcherColumnName);
        // create new column in the savesimvalue table    
        if (!isColumnAvailable) {            
            testsimValueTable.createColumn(matcherColumnName);
        }
        
        
        testSplitSimValueCalculationTask par = new 
                testSplitSimValueCalculationTask(ontmanager,matcher,matcherList,
                weight);        
        par.runTask(4);
        
    }
     */
    /**
     * This method use only one thread to calculate the similarity values.
     *
     * @param matcher
     * @param matcherList
     * @param weight
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
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is used at the recommendation(segPair and decision) which
     * uses mapping decisions to create an oracle.
     *
     * Note: This method return only accepted mapping suggestions, use
     * getRejectedPairs() method to get rejected mapping suggestions.
     *
     * @return Accepted mapping suggestion.
     */
    public ArrayList<String> getAcceptedPairs() {
        String conceptPair = "";
        String concept1 = "";
        String concept2 = "";
        String concept1ID = "";
        String concept2ID = "";
        ArrayList<String> alignment = new ArrayList();
        for (Integer i : source_content) {
            concept1 = source_ontology.getURITable().getURI(i);
            concept1ID = source_ontology.getElement(concept1).getLocalName();
            for (Integer j : target_content) {
                concept2 = target_ontology.getURITable().getURI(j);
                concept2ID = target_ontology.getElement(concept2).getLocalName();
                conceptPair = concept1ID + AlgoConstants.SEPERATOR + concept2ID;
                if (RecommendationConstants.ACCEPTED_SUGGESTIONS.
                        contains(conceptPair)) {
                    alignment.add(concept1ID + " == " + concept2ID);
                }
            }
        }

        return alignment;
    }

    /**
     * This method is used at the recommendation(segPair and decision) which
     * uses mapping decisions to create an oracle.
     *
     * Note: This method return only rejected mapping suggestions, use
     * getAcceptedPairs() method to get accepted mapping suggestions.
     *
     * @return Rejected mapping suggestions.
     */
    public ArrayList<String> getPRARejection() {

        String ID1 = "";
        String ID2 = "";
        String pair = "";
        String c1 = "";
        String c2 = "";
        ArrayList<String> Alignment = new ArrayList();
        for (Integer i : source_content) {
            c1 = source_ontology.getURITable().getURI(i);
            ID1 = source_ontology.getElement(c1).getLocalName();
            for (Integer j : target_content) {
                c2 = source_ontology.getURITable().getURI(j);
                ID2 = source_ontology.getElement(c2).getLocalName();
                pair = ID1 + AlgoConstants.SEPERATOR + ID2;
                if (RecommendationConstants.REJECTED_SUGGESTIONS.
                        contains(pair)) {
                    Alignment.add(ID1 + " == " + ID2);
                }
            }
        }

        return Alignment;
    }
    /**
     * Calculate Concepts Similarities
     * @author huali50
     * @param matcherlist
     * @param merge
     * @param step 
     */
    public void calculate_concept_sim(HashSet<Integer> matcherlist, testMergerManager merge,int step) {
        HashMap<Integer, Matcher> matcher_list = new HashMap<Integer, Matcher>();
        double[] weight = {};
        int computationCounter = 0;
        Connection updateConn = null;
        ArrayList<String> updateStatement = new ArrayList<String>();
        ArrayList<String> insertStatement = new ArrayList<String>();
        boolean simValueFound, isPairFound;
        int matcher_count = 0;
        for (Integer i : matcherlist) {
            switch (i) {
                case AlgoConstants.EDIT_DISTANCE:
                    matcher_list.put(matcher_count, new EditDistance());
                    matcher_count++;
                    break;
                case AlgoConstants.NGRAM:
                    matcher_list.put(matcher_count, new NGram());
                    matcher_count++;
                    break;
                case AlgoConstants.TERM_BASIC:
                    matcher_list.put(matcher_count, new EditDistance());
                    matcher_count++;
                    matcher_list.put(matcher_count, new NGram());
                    matcher_count++;
                default:
                    break;
            }
        }
        if (AlgoConstants.ISRECOMMENDATION_PROCESS) {
            updateConn = sqlConn;
        } else {
            updateConn = makeConnection();
        }
        for (Task task : merge.getTasklist().values()) {
            if (AlgoConstants.STOPMATACHING_PROCESS) {
                //Reseting this variable 
                AlgoConstants.STOPMATACHING_PROCESS = false;
                break;
            }
            for (Integer i : matcher_list.keySet()) {
                if (AlgoConstants.SET_INTERRUPT_ON && AlgoConstants.STOP_COMPUTATION_AT == computationCounter) {
                    AlgoConstants.STOPMATACHING_PROCESS = true;
                    break;
                }
                // To interrupt anytime user want
                if (AlgoConstants.STOPMATACHING_PROCESS) {
                    /**
                     * To apply interrupt to other matchers if the alignment
                     * strategy has more than one matcher.
                     */
                    AlgoConstants.SET_INTERRUPT_ON = true;
                    AlgoConstants.STOP_COMPUTATION_AT = computationCounter;
                    break;
                }
                computationCounter++;

                /**
                 * To remember at which concept pair user interrupted the
                 * computation.
                 */
                AlgoConstants.USER_INTERRUPT_AT = computationCounter;

                String sourceconceptname = merge.getLocalName(merge.getConceptURI(task.getsourceid(), Constants.ONTOLOGY_1));
                String targetconceptname = merge.getLocalName(merge.getConceptURI(task.gettargetid(), Constants.ONTOLOGY_2));
                int concept_id = mapconceptTable.getCPairId(merge.get_mappableontologiesId(), sourceconceptname, targetconceptname);
                if (concept_id > 0) {
                    task.compute_sim(matcher_list.get(i));
                    int simvalue_id = similarityTable.getSimvalueId(concept_id, i);
                    if (simvalue_id > 0) {
                        updateStatement.add(similarityTable.generateUpdateStatement(concept_id, i, task.getsimilarity()));
                    } else {
                        insertStatement.add(similarityTable.generateInsertStatement(concept_id,merge.getMoid(), i, task.getsimilarity(),step));
                    }
                }
                if (insertStatement.size() > 100000) {
                    similarityTable.executeStatements(insertStatement);
                    /**
                     * Computation of matchers like EditDistance and NGram are
                     * faster so delay will make sure that the sim values are
                     * stored in the DB.
                     */
                    if (matcher_list.size() < 3) {
                        delayLine(50);
                    }
                    insertStatement.clear();
                }
                if (updateStatement.size() > 100000) {

                    similarityTable.executeStatements(updateStatement);
                    if (matcher_list.size() < 3) {
                        delayLine(50);
                    }
                    updateStatement.clear();
                }
            }

        }
        //If we have atleast one insert statement.
        if (insertStatement.size() > 0) {
            similarityTable.executeStatements(insertStatement);
            if (matcher_list.size() < 3) {
                delayLine(50);
            }
        }
        //If we have atleast one update statement.
        if (updateStatement.size() > 0) {
            similarityTable.executeStatements(updateStatement);
            if (matcher_list.size() < 3) {
                delayLine(50);
            }
        }
        if (!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            ResourceManager.close(updateConn);
        }
    }
}
