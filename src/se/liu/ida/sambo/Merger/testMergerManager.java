/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.algos.matching.MatchingAlgos;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.HistoryStack;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import se.liu.ida.sambo.MModel.testLexicon;
import se.liu.ida.sambo.MModel.testMClass;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.EditDistance;
import se.liu.ida.sambo.algos.matching.testMatchingAlgos;
import se.liu.ida.sambo.util.testPair;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructor;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.*;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.testHistory;

/**
 *
 * @author huali50
 *//*
 * Merger.java
 *
 */

/**
 * <p>
 * The class controls the merging process.
 * </p>
 *
 * @authors He Tan, Rajaram.
 * @version 2.0
 */
public class testMergerManager {

    //the model manager
    private testOntManager testOntManager;
    private int thread;
    private int mappable_ontologies_id;
    //the matchingAlgos actually perform matchings
    private testMatchingAlgos matchingAlgos;
    private int ii = 0;
    //private Constants constants;
    //a list of pairs of suggestions
    Vector generalSuggestionVector;
    //keeps track of the accepted suggestions
    HistoryStack historyStack;
    //a list of suggestions, which are fetched from the genenralSuggestions,
    //those pairs containing the same element
    Vector currentSuggestionVector;
    //backup the list of mering histories
    Vector historyVector;
    Vector tempSuggestionVector;
    private final boolean ToDo = true, UnDo = false;
    NameProcessor labelClean = new NameProcessor();
    private HashSet<Integer> matcher_list = new HashSet<Integer>();
    private HashMap<Integer, Task> tasklist = new HashMap<Integer, Task>();
    private MapOntologyGenerateQuery mapontologyTable;
    private MapConceptGenerateQuery mapconceptTable;

    public static void main(String args[]) throws OWLOntologyCreationException {
        testMergerManager mm = new testMergerManager();
        mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\nose_MA_1.owl", "C:\\Users\\huali50\\Desktop\\ontologies\\nose_MeSH_2.owl");
        mm.generate_classtasklist(0);
        //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_FMA_whole_ontology.owl","C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_NCI_whole_ontology.owl");
        mm.init();
        HashSet<Integer> matcherlist = new HashSet<Integer>();
        matcherlist.add(AlgoConstants.EDIT_DISTANCE);
        matcherlist.add(AlgoConstants.NGRAM);
        mm.getmatchingalgos().calculateclasssim(matcherlist, mm);
        //long t1 = System.currentTimeMillis();
        //Matcher matcher = new EditDistance();
        //mm.match(matcher);
        //long t2 = System.currentTimeMillis();
        //System.out.println( "Time Taken to LOAD FILE " + (t2-t1) + " ms" );
        Integer step = new Integer(Constants.STEP_CLASS);
        Commons.hasProcessStarted = true;
        Commons.isFinalized = 0;
        AlgoConstants.STOPMATACHING_PROCESS = false;
        AlgoConstants.ISRECOMMENDATION_PROCESS = false;
        //mm.getSuggestions(Constants.STEP_CLASS, getWeight(step,mm), 0.6, combinationMethod)

    }

    public void match(Matcher matcher_list) {
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getClassLexicons();
        Set<Integer> targetlexicons = targetontology.getClassLexicons();
        int count = 0;
        int mappingcount = 0;
        long t1 = System.currentTimeMillis();
        for (Integer i : sourcelexicons) {
            for (Integer j : targetlexicons) {
                //mapinparallel(sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j));
                //System.out.println(edfinalvalue);
                if (count < 1000) {
                    //tasklist.put(new Task(i,j,sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j)));
                    mappingcount++;
                    count++;
                }
                if (count == 1000) {
                    //System.out.println("----------------------------------------------------------------------------");
                    //matchforkjoin(tasklist);
                    //matchexecutor(tasklist);
                    count = 0;
                    tasklist.clear();
                }

            }
        }

        //matchforkjoin(tasklist);
        long t2 = System.currentTimeMillis();
        System.out.println("Time Taken to generate " + mappingcount + " mappings in " + (t2 - t1) + " ms");
    }

    public void matchexecutor(ArrayList<Task> tasklist) {

        ArrayList<testMappingtask> tasks = new ArrayList<testMappingtask>();
        List<Future<testMapping>> results;
        for (int i = 0; i < tasklist.size(); i++) {
            testMappingtask e = new testMappingtask(tasklist.get(i).getsource(), tasklist.get(i).gettarget());
            tasks.add(e);
        }
        ExecutorService exec = Executors.newFixedThreadPool(thread);
        try {
            results = exec.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            results = new ArrayList<Future<testMapping>>();
        }
        exec.shutdown();

        for (Future<testMapping> ftm : results) {
            try {
                ii++;
                testMapping tm = ftm.get();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*
        EditDistance test = new EditDistance();
        double simvalue;
        double maxvalue = 0;
        for(testLexicon stl : sourcelexicon)
        {
            for(testLexicon ttl : targetlexicon)
            {
                if(stl.getlanguage().equals(ttl.getlanguage())){
                    simvalue = test.getSimValue(stl.getname(), ttl.getname());
                    if(simvalue > maxvalue)
                        maxvalue = simvalue;
                }

            }
        }
         */

    }

    private class testMappingtask implements Callable<testMapping> {

        private HashSet<testLexicon> sourcelexicon;
        private HashSet<testLexicon> targetlexicon;

        testMappingtask(HashSet<testLexicon> s, HashSet<testLexicon> t) {
            sourcelexicon = s;
            targetlexicon = t;
        }

        public testMapping call() {
            testMapping tm = new testMapping(sourcelexicon, targetlexicon);
            tm.compute_sim();
            return tm;
        }
    }

    private double mapresults(String source, String target) {
        double sim = 0;
        EditDistance test = new EditDistance();
        sim = test.getSimValue(source, target);
        return sim;
    }

    /**
     * Creates new Merger
     */
    public testMergerManager() {
        testOntManager = new testOntManager();
        //thread = Runtime.getRuntime().availableProcessors();
        thread = 4;
        System.out.println("Thread num: " + thread);
        generalSuggestionVector = new Vector<testPair>();
    }

    /**
     * Loads the ontologies
     *
     **@param url1 the url of the ontology-1
     **@param url2 the url of the ontology-2
     */
    public void loadOntologies(String uri1, String uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);
    }

    public void loadOntologies(URL uri1, URL uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);

        Connection sqlConn = null;
        try {
            sqlConn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        mapontologyTable = new MapOntologyGenerateQuery(sqlConn);
        this.mappable_ontologies_id = mapontologyTable.getOPairId(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1), AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        if (mappable_ontologies_id < 0) {
            mapontologyTable.execute(mapontologyTable.generateInsertStatement(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1), AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2)));
            mappable_ontologies_id = mapontologyTable.getOPairId(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1), AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        }
        ResourceManager.close(sqlConn);

    }

    public void loadOntologies(URI uri1, URI uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);
    }

    /**
     * Loads the ontologies
     *
     *
     *
     * /** Load Ontology-1
     *
     * @param url1 the url of the ontology-1
     */
    /* Prepare to begin matching
     **/
    public void init() {

        //initialize the history stack
        historyStack = new HistoryStack();
        currentSuggestionVector = new Vector();

        historyVector = new Vector();

        matchingAlgos = new testMatchingAlgos(testOntManager, this);

        tempSuggestionVector = new Vector();
    }

    /**
     * Clear the current merge process
     *
     * @return a new merge manager with already loaded ontologies.
     */
    /**
     * Clear the current merge process
     *
     * @parameter n the number indicate which ontology is closed.
     *
     * @return a new merge manager with already loaded ontologies.
     */
    /**
     * Calls the matching algorithms to calculate similarity value
     *
     * @param step the steps: STEP_SLOT, STEP_CLASS.
     * @param matcher the matcher
     *
     * @return a list of suggestions
     */
    /*
    public void matching(int step, int matcher) {

        switch (step) {
            case Constants.STEP_SLOT:
                matchingAlgos.calculateSlotSimValue(matcher);
                break;
            case Constants.STEP_CLASS:
                matchingAlgos.calculateClassSimValue(matcher);
                break;
        }
    }
     */
    public void test_match(int step) {
        switch (step) {
            case Constants.STEP_SLOT:

                break;
            case Constants.STEP_CLASS:

                break;
        }
    }

    /**
     * Calls the matching algorithms to calculate similarity value for
     * MappableGroup
     *
     *
     * @param matcher the matcher
     *
     * @return a list of suggestions
     */
    public void matching(int matcher) {

        matchingAlgos.calculateClassSimValueMGrp(matcher);

    }

    /**
     * Gets un validated mapping suggestion when the saved session is resumed.
     *
     * @param step Slot/Class matching step.
     *
     * @return List of un validated mapping suggestion
     */
    public Vector loadSuggestions(int step) {
        switch (step) {
            case Constants.STEP_SLOT:
                generalSuggestionVector = matchingAlgos.loadSlotSugs();
                break;
            case Constants.STEP_CLASS:
                generalSuggestionVector = matchingAlgos.loadClassSugs();
                break;
        }

        return generalSuggestionVector;

    }

    //Added by MZK
    // updated by Qiang
    /**
     * Gets validated mapping suggestion.
     *
     * @param step Slot/class matching step.
     */
    /**
     * Load mapping suggestions for single threshold alignment strategy.
     *
     * @param step Slot/Class matching.
     * @param weights Matchers weight.
     * @param threshold Threshold for filtering.
     * @param combinationMethod Combination method.
     *
     * @return List of mapping suggestions.
     */
    public Vector getSuggestions(int step, double[] weights, double threshold,String combinationMethod) {

        switch (step) {
            case Constants.STEP_SLOT:
                generalSuggestionVector = matchingAlgos.getSlotSugs(weights,threshold, combinationMethod,step);
                break;
            case Constants.STEP_CLASS:
                generalSuggestionVector = matchingAlgos.getClassSugs(weights,threshold, combinationMethod,step);
                break;
        }
        return generalSuggestionVector;
    }

    /**
     * Load mapping suggestions for double threshold alignment strategy.
     *
     * @param weights Matchers weight.
     * @param upperthreshold Upper threshold.
     * @param lowerthreshold Lower threshold.
     * @param combinationMethod Combination method.
     *
     * @return List of mapping suggestions.
     */
    public Vector getSuggestions(double[] weights, double upperthreshold,double lowerthreshold, String combinationMethod, int step) {

        generalSuggestionVector = matchingAlgos.getClassSugs(weights,
                upperthreshold, lowerthreshold, combinationMethod,step);
        return generalSuggestionVector;
    }

    /**
     * Load mapping suggestions for single threshold alignment strategy which
     * uses mappable concept pairs alone.
     *
     * @param weights Matchers weight.
     * @param threshold Threshold for filtering.
     * @param combinationMethod Combination method.
     *
     * @return List of mapping suggestions.
     */
    public Vector getSuggestionsMGBased(double[] weights, double threshold,
            String combinationMethod) {
        generalSuggestionVector = matchingAlgos.getClassSugsMGBased(weights,
                threshold, combinationMethod);
        return generalSuggestionVector;
    }

    /**
     * Load mapping suggestions for double threshold alignment strategy which
     * uses mappable concept pairs alone.
     *
     * @param weights Matchers weight.
     * @param upperthreshold Upper threshold.
     * @param lowerthreshold Lower threshold.
     * @param combinationMethod Combination method.
     *
     * @return List of mapping suggestions.
     */
    public Vector getSuggestionsMGBased(double[] weights, double upperthreshold,
            double lowerthreshold, String combinationMethod) {
        generalSuggestionVector = matchingAlgos.getClassSugsMGBased(weights,
                upperthreshold, lowerthreshold, combinationMethod);
        return generalSuggestionVector;
    }

    //ADDED BY MZK
    public void getSuggestionsXML(String filename) {

        try {
            Commons.vList = generalSuggestionVector;
            Commons.strings = new Object[generalSuggestionVector.size()];

            int i = 0;

            for (Object str : generalSuggestionVector) {
                Commons.strings[i++] = str;

            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.newDocument();

            Element root = document.createElement("Suggestions");
            document.appendChild(root);

            int no = Commons.vList.size();

            for (int j = 0; j
                    < no; j++) {
                Element Rootchild = document.createElement("Pair");
                root.appendChild(Rootchild);
                Text text = document.createTextNode(Commons.strings[j].toString());
                Rootchild.appendChild(text);

            }

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = sw.toString();

            File file = new File(filename);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(xmlString);
            bw.flush();
            bw.close();

        } catch (Exception e) {
            System.out.println(e);

        }

    }

    //Added by Shahab
    /*
     *  The purpose of this method is to generate Suggestions XML for each of the segments
     */
    public void getSuggestionsXML(String filename, Vector generalSuggestionSegmentVector) {

        try {
            Commons.vList = generalSuggestionSegmentVector;
            Commons.strings = new Object[generalSuggestionSegmentVector.size()];

            int i = 0;

            for (Object str : generalSuggestionSegmentVector) {
                Commons.strings[i++] = str;

            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.newDocument();

            Element root = document.createElement("Suggestions");
            document.appendChild(root);

            int no = Commons.vList.size();

            for (int j = 0; j
                    < no; j++) {
                Element Rootchild = document.createElement("Pair");
                root.appendChild(Rootchild);
                Text text = document.createTextNode(Commons.strings[j].toString());
                Rootchild.appendChild(text);

            }

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = sw.toString();

            File file = new File(filename);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(xmlString);
            bw.flush();
            bw.close();

        } catch (Exception e) {
            System.out.println(e);

        }

    }

    public Vector getGeneralSuggestionVector() {
        return generalSuggestionVector;

    }

    /**
     * Returns next element in the suggestions
     *
     * @return the first suggestion in the list
     */
    public testPair getNextSuggestion() {
        if (generalSuggestionVector.isEmpty()) {
            return new testPair();

        } else {
            return (testPair) generalSuggestionVector.firstElement();

        }
    }

    /**
     * Returns a set of suggestions, grouped by a element (class or property)
     * from the first ontology, that is one element from the first ontology and
     * the possibilities for merging with different elements in the second
     * ontology.
     *
     * @return the vector of Pairs containing the suggested merges
     */
    public Vector getNextSuggestionList() {
        currentSuggestionVector.removeAllElements();

        if (!generalSuggestionVector.isEmpty()) {
            //Pick out the first element and group all suggestions containing that element
            testPair pair = (testPair) generalSuggestionVector.firstElement();

            return currentSuggestionVector = Constants.testgetHoldingPairs(pair, generalSuggestionVector);

        }
        return currentSuggestionVector;

    }

    /**
     * Encapsulates the slot suggestion with merging info, and add it to the
     * history stack.
     *
     * @param history encapsulation of the accepted suggestion.
     *
     */
    /**
     * Undo the previous action on slots
     */
    /**
     * Finalizes the slot merging It is not possible to undo merged slots after
     * the finalize method has been run.
     */
    public void finalizeSlotSuggestions() {

        //clear lists
        historyStack.removeAllElements();
        generalSuggestionVector.removeAllElements();

    }

    //Added by MZK
    public void matchforkjoin(ArrayList<Task> tasklist) {
        int tasksize = tasklist.size();
        ComputeTask task = new ComputeTask(0, tasksize);
        task.settasklist(tasklist);
        task.compute();
    }

    public HashSet getMatcherList() {
        return this.matcher_list;
    }

    public void generate_tasklist(int step) {
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceconcepts = null;
        Set<Integer> targetconcepts = null;
        Set<Integer> sourcelexicons = null;
        Set<Integer> targetlexicons = null;
        if (step == Constants.STEP_SLOT) {
            sourceconcepts = sourceontology.getProperties();
            targetconcepts = targetontology.getProperties();
            sourcelexicons = sourceontology.getPropertiesLexicons();
            targetlexicons = targetontology.getPropertiesLexicons();
        } else if (step == Constants.STEP_CLASS) {
            sourceconcepts = sourceontology.getMClasses();
            targetconcepts = targetontology.getMClasses();
            sourcelexicons = sourceontology.getClassLexicons();
            targetlexicons = targetontology.getClassLexicons();
        }
        int count = 1;
        ArrayList<String> insertStatement = new ArrayList<String>();
        Connection sqlConn = null;
        try {
            sqlConn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        mapconceptTable = new MapConceptGenerateQuery(sqlConn);
        for (Integer sid : sourceconcepts) {
            String sourcelocalname = getLocalName(getConceptURI(sid, Constants.ONTOLOGY_1));
            for (Integer tid : targetconcepts) {
                String targetlocalname = getLocalName(getConceptURI(tid, Constants.ONTOLOGY_2));
                int conceptId = mapconceptTable.getCPairId(this.get_mappableontologiesId(), sourcelocalname, targetlocalname);
                if (conceptId < 0) {
                    insertStatement.add(mapconceptTable.generateInsertStatement(this.get_mappableontologiesId(), sourcelocalname, targetlocalname,step));
                }
                if (insertStatement.size() > 100000) {
                    mapconceptTable.executeStatements(insertStatement);
                    insertStatement.clear();
                }

            }
        }
        if (insertStatement.size() > 0) {
            mapconceptTable.executeStatements(insertStatement);
        }
        ResourceManager.close(sqlConn);
        for (Integer i : sourcelexicons) {
            for (Integer j : targetlexicons) {
                tasklist.put(count, new Task(i, j, sourceontology.getlexicons(i), targetontology.getlexicons(j)));
                count++;
            }

        }

    }

    public void generate_classtasklist(int step) {
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getClassLexicons();
        Set<Integer> targetlexicons = targetontology.getClassLexicons();
        int count = 1;
        ArrayList<String> insertStatement = new ArrayList<String>();
        Connection sqlConn = null;
        try {
            sqlConn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        mapconceptTable = new MapConceptGenerateQuery(sqlConn);
        for (Integer sid : sourceclasses) {
            String sourceURI = getLocalName(getConceptURI(sid, Constants.ONTOLOGY_1));
            for (Integer tid : targetclasses) {
                String targetURI = getLocalName(getConceptURI(tid, Constants.ONTOLOGY_2));
                int conceptId = mapconceptTable.getCPairId(this.get_mappableontologiesId(), sourceURI, targetURI);
                if (conceptId < 0) {
                    insertStatement.add(mapconceptTable.generateInsertStatement(this.get_mappableontologiesId(), sourceURI, targetURI,step));
                }
                if (insertStatement.size() > 100000) {
                    mapconceptTable.executeStatements(insertStatement);
                    insertStatement.clear();
                }

            }
        }
        if (insertStatement.size() > 0) {
            mapconceptTable.executeStatements(insertStatement);
        }
        ResourceManager.close(sqlConn);
        for (Integer i : sourcelexicons) {

            for (Integer j : targetlexicons) {
                tasklist.put(count, new Task(i, j, sourceontology.getclasslexicons(i), targetontology.getclasslexicons(j)));
                count++;
            }

        }

    }

    public HashMap<Integer, Task> getTasklist() {
        return this.tasklist;
    }

    public testMatchingAlgos getmatchingalgos() {
        return this.matchingAlgos;
    }

    public void processSlotSuggestion(testHistory history) {

        generalSuggestionVector.remove(history.getPair());
        historyStack.add(history);

        //set merging information to the slot
        setSlotInfo(
                history, ToDo);

    }

    public int processClassSuggestion(testHistory history) {
        testPair pair = history.getPair();
        if (!historyStack.contains(history)) {
            historyStack.add(history);
        }
        testPair temp = (testPair) generalSuggestionVector.get(0);
        boolean result = pair.equals(temp);
        generalSuggestionVector.remove(pair);

        if (history.getAction() == Constants.ALIGN_CLASS) {
            for (Enumeration e = Constants.testgetHoldingPairs(pair, generalSuggestionVector).elements(); e.hasMoreElements();) {
                testPair p = (testPair) e.nextElement();

                if (!historyStack.contains(history)) {
                    historyStack.updateMostRecent(new testHistory(p));
                }
                generalSuggestionVector.remove(p);
            }
        }

        //**************************************** //
        int c = setClassInfo(history, ToDo);
        // return setClassInfo(history, ToDo);
        System.out.println(c);
        return c;
    }

    public void setSlotInfo(testHistory h, boolean set) {

    }

    int setClassInfo(testHistory h, boolean set) {

        testPair pair = h.getPair();

        //to do the action specified in the history
        //and check the name conflict and give the warning
        testMOntology source_ontology = this.getOntManager().getontology(Constants.ONTOLOGY_1);
        testMOntology target_ontology = this.getOntManager().getontology(Constants.ONTOLOGY_2);
        testMClass source_class = source_ontology.getClasses().get(source_ontology.getURITable().getIndex(pair.getSource()));
        testMClass target_class = target_ontology.getClasses().get(target_ontology.getURITable().getIndex(pair.getTarget()));

        if (set) {

            //to merge the pair of the classes
            if (h.getAction() == Constants.ALIGN_CLASS) {

                if (h.getName() != null && h.getName().trim().length() > 0) {
                    //the value will be minus, if the new name conflict                    
                    h.setWarning(checkLabelConflict(h.getName()));
                    System.out.println(h.getName());

                    source_class.setAlignName(h.getName());
                    target_class.setAlignName(h.getName());
                } else //if the classes to aligned do not have equivalent names or synonyms,
                //they could be equal to other names or synonyms
                 if (!hasEquivLabel(source_class, target_class)) //if name1 is unique, check the name2
                    {
                        if (h.setWarning(checkLabelConflict(source_class.getLabel(), Constants.ONTOLOGY_2)) == Constants.UNIQUE) {
                            h.setWarning(checkLabelConflict(target_class.getLabel(), Constants.ONTOLOGY_1));
                        }
                    }

                //the name for the merged class is the name of the first class
                source_class.setAlignClass(target_class);
                target_class.setAlignClass(source_class);

                //to include is-a relation between the pair of the classes
            } else if (h.getAction() == Constants.IS_A_CLASS) {

                //the first class is the super-class
                if (h.getNum() == Constants.ONTOLOGY_1) {
                    target_class.addAlignSuper(source_class);
                } //the second class is the super-class
                else {
                    source_class.addAlignSuper(target_class);
                } //if the classes has the same name or synonyms
                if (hasEquivLabel(source_class, target_class)) {
                    h.setWarning(Constants.ONTOLOGY_1);

                }

            } else if (h.getAction() == Constants.NO) {

                if (hasEquivLabel(source_class, target_class)) {
                    h.setWarning(Constants.ONTOLOGY_1);

                }
            }

            if (h.getComment() != null && h.getComment().trim().length() > 0) {
                source_class.addAlignComment(h.getComment());
                target_class.addAlignComment(h.getComment());

            } //undo the action specified in the history
        } else {
            source_class.setAlignName(null);
            target_class.setAlignName(null);
            source_class.addAlignComment(null);
            target_class.addAlignComment(null);

            //undo the merge action
            if (h.getAction() == Constants.ALIGN_CLASS) {
                source_class.setAlignElement(null);
                target_class.setAlignElement(null);

                //undo include is-a relation action
            } else if (h.getAction() == Constants.IS_A_CLASS) {

                //the first class is the super class
                if (h.getNum() == Constants.ONTOLOGY_1) {
                    target_class.removeAlignSuper(source_class);

                } else {
                    source_class.removeAlignSuper(target_class);

                }
            }
        }

        return h.getWarning();

    }

    public int suggestionsRemaining() {

        return generalSuggestionVector.size() - 1;
    }

    public void undoSlotMerge() {

        if (!historyStack.isEmpty()) {
            //the previously processed pair of elements
            testHistory history = (testHistory) historyStack.remove();
            //insert this suggestion to the first element of the suggestion list
            generalSuggestionVector.add(0, history.getPair());
            setSlotInfo(history, UnDo);

        }
    }

    public void undoClassMerge() {

        if (!historyStack.empty()) {
            //the previously processed list of suggestions
            Vector previous = historyStack.removeMostRecent();

            /*
            for (Enumeration e = previous.elements(); e.hasMoreElements();) {

                History history = (History) e.nextElement();
                //insert this suggestion to the first element of the suggestion list
                generalSuggestionVector.add(0, history.getPair());
                setClassInfo(
                        history, UnDo);


            }
             */
        }
    }

    public int get_mappableontologiesId() {
        return this.mappable_ontologies_id;
    }

    public String getConceptURI(int Id, int ontology) {
        return this.testOntManager.getontology(ontology).getURITable().getURI(Id);
    }

    public String getLocalName(String uri) {
        if (uri == null) {
            return null;
        } else {
            int i = uri.indexOf("#") + 1;
            if (i == 0) {
                i = uri.lastIndexOf("/") + 1;
            }
            return uri.substring(i);
        }
    }

    public testOntManager getOntManager() {
        return this.testOntManager;
    }

    public int checkLabelConflict(String name) {

        if (existInOnto(name, Constants.ONTOLOGY_1)) {
            return Constants.ONTOLOGY_1;

        }

        if (existInOnto(name, Constants.ONTOLOGY_2)) {
            return Constants.ONTOLOGY_2;

        }

        return Constants.UNIQUE;
    }

    public int checkLabelConflict(String name, int ontonum) {

        if (existInOnto(name, ontonum)) {
            return ontonum;
        }
        return Constants.UNIQUE;
    }

    private boolean existInOnto(String name, int ontonum) {
        for (Integer i : getOntManager().getontology(ontonum).getClasses().keySet()) {
            testMClass c = getOntManager().getontology(ontonum).getClasses().get(i);
            if (name.equalsIgnoreCase(c.getLabel())) {
                return true;

            }
            /*
            for (Enumeration en = c.getSynonyms().elements(); en.hasMoreElements();) {
                if (name.equalsIgnoreCase(((String) en.nextElement()))) {
                    return true;


                }
            }
            if ((c.getAlignElement() != null && c.getAlignElement().getLabel().equalsIgnoreCase(name))) {
                return true;


            }

            if ((c.getAlignName() != null && c.getAlignName().equalsIgnoreCase(name))) {
                return true;


            }
             */
        }

        return false;

    }

    public boolean hasEquivLabel(testMClass tc1, testMClass tc2) {
        if (tc1.getLabel().equalsIgnoreCase(tc2.getLabel())) {
            return true;
        }
        return false;
    }

    public void mergeRemaining() {
        testPair pair = (testPair) generalSuggestionVector.firstElement();
        testHistory history = new testHistory(pair, null, Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS);
        historyStack.add(history);
        setClassInfo(history, ToDo);
        while (true) {
            generalSuggestionVector.remove(pair);

            //update historyStack due to the classes in the pair to be merged
            for (Enumeration e = Constants.testgetHoldingPairs(pair, generalSuggestionVector).elements(); e.hasMoreElements();) {
                testPair p = (testPair) e.nextElement();
                generalSuggestionVector.remove(p);
                historyStack.updateMostRecent(new testHistory(p));
            }

            if (generalSuggestionVector.isEmpty()) {
                break;
            }
            pair = (testPair) generalSuggestionVector.firstElement();
            history = new testHistory(pair, null, Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS);
            historyStack.updateMostRecent(history);
            setClassInfo(history, ToDo);
        }
    }

    public void finalizeClassSuggestions() {
        //backup the class history stack
        for (Enumeration e = historyStack.elements(); e.hasMoreElements();) {
            testHistory h = (testHistory) e.nextElement();
            if (h.getAction() == Constants.ALIGN_CLASS) {
                matchingAlgos.setAlignment(h.getPair());
            }
            historyVector.add(h);
        }

        //clear the lists
        generalSuggestionVector.removeAllElements();
        historyStack.removeAllElements();
        currentSuggestionVector.removeAllElements();
    }

    public Vector getHistory() {
        return historyVector;
    }

    public Vector getCurrentHistory() {
        return historyStack;
    }

    public Vector getRemainingSuggestions() {
        return generalSuggestionVector;
    }
    public void finalize(String alignfile, String mergefile){
    
    }
    public int getMoid(){
        return this.mappable_ontologies_id;
    }
}
