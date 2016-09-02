/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.io.*;
import static java.lang.Thread.sleep;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
import se.liu.ida.sambo.MModel.testMProperty;
import static se.liu.ida.sambo.Merger.Constants.NGram;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.EditDistance;
import se.liu.ida.sambo.algos.matching.testMatchingAlgos;
import se.liu.ida.sambo.util.testPair;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.NGram;
import se.liu.ida.sambo.algos.matching.algos.Porter_WordNet;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructor;
import se.liu.ida.sambo.algos.matching.algos.UMLSKSearch_V6;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.*;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.testHistory;

/**
 *
 * @author huali50
 *//*
 * testMergerManager.java
 *
 */

/**
 * <p>
 * The class controls the merging process.
 * </p>
 *
 * 
 */
public class testMergerManager {

    //the model manager
    public int list_block_size = 10000000;
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
    private HashSet<testPair> suggestedpairs = new HashSet<testPair>();
    private HashMap<Integer, Task> tasklist = new HashMap<Integer, Task>();
    private MapOntologyGenerateQuery mapontologyTable;
    private MapConceptGenerateQuery mapconceptTable;
    private ArrayList<testMappingtask> executortasks;
    private Matcher matcher= null;
    private boolean is_Large = false;
    private int combination;
    private double[] weight;
    private boolean is_Database  = false;
    private int pageSize = 0;
    private int mapping_count = 0;

    public static void main(String args[]) throws OWLOntologyCreationException {
        testMergerManager mm = new testMergerManager();
        
        //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\nose_MA_1.owl", "C:\\Users\\huali50\\Desktop\\ontologies\\nose_MeSH_2.owl");
        //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\ear_MA_1.owl", "C:\\Users\\huali50\\Desktop\\ontologies\\ear_MeSH_2.owl");
        //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\eye_MA_1.owl", "C:\\Users\\huali50\\Desktop\\ontologies\\eye_MeSH_2.owl");

       //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_FMA_small_overlapping_nci.owl","C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_NCI_small_overlapping_fma.owl");
        //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_FMA_whole_ontology.owl","C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_NCI_whole_ontology.owl");
         //mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_SNOMED_extended_overlapping_fma_nci.owl","C:\\Users\\huali50\\Desktop\\ontologies\\oaei2014_FMA_whole_ontology.owl");
        
//mm.loadOntologies("C:\\Program Files\\Apache Software Foundation\\Apache Tomcat 8.0.27\\webapps\\SAMBOWebAppSession\\build\\web\\ontologies\\OWL\\oaei2014_FMA_whole_ontology.owl","C:\\Program Files\\Apache Software Foundation\\Apache Tomcat 8.0.27\\webapps\\SAMBOWebAppSession\\build\\web\\ontologies\\OWL\\oaei2014_NCI_whole_ontology.owl");
        
        mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\human.owl","C:\\Users\\huali50\\Desktop\\ontologies\\mouse.owl");
        //long t1 = System.currentTimeMillis();
        //mm.generate_classtasklist(0);
        //long t2 = System.currentTimeMillis();
        //System.out.println( "Time Taken to Generate List " + (t2-t1) + " ms" );
        mm.init();
        //mm.setDatabase();
        HashSet<Integer> matcherlist = new HashSet<Integer>();
        matcherlist.add(AlgoConstants.EDIT_DISTANCE);
        matcherlist.add(AlgoConstants.NGRAM);
        //mm.getmatchingalgos().calculateclasssim(matcherlist, mm);
        HashMap<Integer,Task> task_list = new HashMap<Integer,Task>();
        //mm.matcher = new UMLSKSearch_V6();
        //mm.matcher = new Porter_WordNet(true);
        mm.matcher_list.add(Constants.NGram);
        mm.matcher_list.add(Constants.EditDistance);
        mm.weight = new double[9];
        mm.weight[Constants.NGram] = 1.0;
        mm.weight[Constants.EditDistance] = 1.0;
        mm.combination = Constants.MAXBASED;
        mm.generate_tasklist_match(Constants.STEP_CLASS,0.4,0.7);
        Integer step = new Integer(Constants.STEP_CLASS);
        Commons.hasProcessStarted = true;
        Commons.isFinalized = 0;
        AlgoConstants.STOPMATACHING_PROCESS = false;
        AlgoConstants.ISRECOMMENDATION_PROCESS = false;
        //mm.getSuggestions(Constants.STEP_CLASS, getWeight(step,mm), 0.6, combinationMethod)
        System.out.println("Mapping Count = "+mm.getMappingcount());
    }
    /**
     * Compute in Parallel with fork/join
     * author huali50
     * @param matcher_list
     * @param task_list
     **/
    public void match(Matcher matcher_list, HashMap<Integer,Task> task_list) {
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getClassLexicons();
        Set<Integer> targetlexicons = targetontology.getClassLexicons();
        int count = 0;
        int mappingcount = 0;
        long t1 = System.currentTimeMillis();
        
        Matcher matcher = new EditDistance();
        Integer block_num = (sourcelexicons.size()*targetlexicons.size())/this.list_block_size;
        Integer last_block_size = (sourcelexicons.size()*targetlexicons.size())%this.list_block_size;
        if(last_block_size>0)
            block_num++;
        int blockid=0;
        for (Integer i : sourcelexicons) {
            for (Integer j : targetlexicons) {
                //mapinparallel(sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j));
                //System.out.println(edfinalvalue);
                    
                if (count < this.list_block_size) {
                    task_list.put(mappingcount,new Task(i,j,sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j)));
                    mappingcount++;
                    count++;
                    
                }
                if (count == this.list_block_size) {
                    matchforkjoin(task_list,matcher,blockid, false, last_block_size);
                    //matchexecutor(tasklist);
                    count = 0;
                    blockid++;
                    //task_list.clear();
                }
                if((blockid == block_num-1)&& (count == last_block_size)){
                    matchforkjoin(task_list,matcher,blockid, true,last_block_size);
                    count = 0;
                    blockid++;
                }
               
            }
        }    
    }
    /**
     * Parallel matching using ExecutorService (Future and Callable)
     * @author huali50
     * @param tasks
     * @param downthresh
     * @param upthresh
     * @return 
     */
    public Vector matchexecutor(ArrayList<testMappingtask> tasks,double downthresh,double upthresh) {
        int flag = 0;
        if((upthresh > downthresh) && (downthresh >= 0))
            flag = 1;
        //Vector Suggestions = new Vector();
        List<Future<testPair>> results;
        ExecutorService exec = Executors.newFixedThreadPool(thread);
        try {
            results = exec.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            results = new ArrayList<Future<testPair>>();
        }
        exec.shutdown();
        testPair tm = null;
        for (Future<testPair> ftm : results) {
            try {
                ii++;
                tm = ftm.get(10,TimeUnit.SECONDS);
                double similarity = tm.getSim();
                if(flag == 0){
                    if(similarity >= downthresh){
                        this.generalSuggestionVector.add(tm);
                        //Suggestions.add(tm);
                        this.mapping_count++;
                    }
                    
                }
                else if(flag == 1){
                    if((similarity >= downthresh)&&(similarity <= upthresh)){
                        this.generalSuggestionVector.add(tm);
                        //Suggestions.add(tm);
                        this.mapping_count++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.generalSuggestionVector;
    }
    /**
     * Task implements Callable<testPair>
     * @author huali50
     */
    private class testMappingtask implements Callable<testPair> {

        private String sourceURI;
        private String targetURI;
        

        testMappingtask(String sourceURI, String targetURI) {
            this.sourceURI = sourceURI;
            this.targetURI = targetURI;
        }

        public testPair call() {
            return new testPair(sourceURI,targetURI,compute_sim(sourceURI,targetURI));
            
        }
    }
    /**
     * Compute Similarity in parallel
     * @author huali50
     * @param sourceURI
     * @param targetURI
     * @return 
     */
    public double compute_sim(String sourceURI,String targetURI){
        double max_sim = 0;
        double[] sim = new double[9];
        Integer sourceId = this.testOntManager.getontology(Constants.ONTOLOGY_1).getURITable().getIndex(sourceURI);
        Integer targetId = this.testOntManager.getontology(Constants.ONTOLOGY_2).getURITable().getIndex(targetURI);
        HashSet<testLexicon> sourcelexicon = this.getOntManager().getontology(Constants.ONTOLOGY_1).getclasslexicons(sourceId);
        HashSet<testLexicon> targetlexicon = this.getOntManager().getontology(Constants.ONTOLOGY_2).getclasslexicons(targetId);
        for(testLexicon stl : sourcelexicon){
            for(testLexicon ttl : targetlexicon){
                if(stl.getlanguage().equals(ttl.getlanguage())){
                    for(Integer i : this.matcher_list){
                        if(i == Constants.EditDistance){
                            Matcher matcher = new EditDistance();
                            sim[i] = matcher.getSimValue(stl.getname(),ttl.getname());
                            if (sim[i] >= max_sim)
                                max_sim =sim[i];
                        }
                        else if(i == Constants.NGram){
                            Matcher matcher = new NGram(3);
                            sim[i] = matcher.getSimValue(stl.getname(),ttl.getname());
                            if (sim[i] >= max_sim)
                                max_sim =sim[i];
                        }
                        else if(i == Constants.WordNet_Plus){
                            Matcher matcher = new Porter_WordNet(true);
                            sim[i] = matcher.getSimValue(stl.getname(),ttl.getname());
                            if (sim[i] >= max_sim)
                                max_sim =sim[i];
                        }
                        else if(i == Constants.UMLS){
                            Matcher matcher = new UMLSKSearch_V6();
                            sim[i] = matcher.getSimValue(stl.getname(),ttl.getname());
                            if (sim[i] >= max_sim)
                                max_sim =sim[i];
                        }
                    }
                }
            }
        }
        if(this.combination == Constants.MAXBASED){
            return max_sim;
        }
        else if(this.combination == Constants.WEIGHTBASED){
            double numerator = 0;
            double denominator =0;
            for(Integer i :this.matcher_list){
                numerator += this.weight[i.intValue()] * sim[i.intValue()];
                denominator += this.weight[i.intValue()];
            }
            max_sim = numerator / denominator;
            return max_sim;
        }
        else{
            return max_sim;
        }
        //System.out.println("source "+this.source_id+"--target "+this.target_id+"==Similarity "+this.value);
    }
    private double mapresults(String source, String target) {
        double sim = 0;
        EditDistance test = new EditDistance();
        sim = test.getSimValue(source, target);
        return sim;
    }

    /**
     * Creates new Merger
     * author huali50
     */
    public testMergerManager() {
        testOntManager = new testOntManager();
        thread = Runtime.getRuntime().availableProcessors();
        //thread = 8;
        System.out.println("Thread num: " + thread);
        generalSuggestionVector = new Vector<testPair>();
    }

    /**
     * Loads the ontologies based on String paths
     * author huali50
     **@param url1 the url of the ontology-1
     **@param url2 the url of the ontology-2
     */
    public void loadOntologies(String url1, String url2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(url1, url2);
    }
    /**
     * Loads the ontologies based on URLs
     * author huali50
     **@param url1 the url of the ontology-1
     **@param url2 the url of the ontology-2
     */
    public void loadOntologies(URL url1, URL url2) throws OWLOntologyCreationException, SQLException {
        testOntManager.loadOntologies(url1, url2);

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
        sqlConn.close();

    }
    /**
     * Loads the ontologies
     * author huali50
     **@param uri1 the url of the ontology-1
     **@param uri2 the url of the ontology-2
     */
    public void loadOntologies(URI uri1, URI uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);
    }
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

            return currentSuggestionVector = Constants.testgetHoldingPairs(pair.getSource(), generalSuggestionVector,0);

        }
        return currentSuggestionVector;

    }
    /**
     * Finalizes the slot merging It is not possible to undo merged slots after
     * the finalize method has been run.
     */
    public void finalizeSlotSuggestions() {

        //clear lists
        historyStack.removeAllElements();
        generalSuggestionVector.removeAllElements();

    }
    /**
    * Fork-Join Function
    * author huali50
    * @param tasklist
    * @param matcher
    * @param block_id
    * @param is_last
    * @param last_block_size
    */
    public void matchforkjoin(HashMap<Integer, Task> tasklist,Matcher matcher,int block_id,boolean is_last,int last_block_size) {
        int tasksize = tasklist.size();
        int start = block_id * this.list_block_size;
        int end = start + this.list_block_size - 1;
        if(is_last == true)
            end = start + last_block_size - 1;
        ComputeTask task = new ComputeTask(start, end, matcher);
        task.settasklist(tasklist);
        task.compute();
    }
    /**
    * Get the set of Matcher
    * author huali50
    * @return matcher_list
    **/
    public HashSet getMatcherList() {
        return this.matcher_list;
    }
    /**
     * Generate task list
     * @author huali50
     * @param step 
     */
    public void generate_tasklist(int step) {
        this.executortasks = new ArrayList<testMappingtask>();
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
        Connection sqlConn = null;
        ArrayList<String> insertStatement = null;
        insertStatement = new ArrayList<String>();
        try {
            sqlConn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
        mapconceptTable = new MapConceptGenerateQuery(sqlConn);
        int source_size = sourceconcepts.size();
        int target_size = targetconcepts.size();
        for (Integer sid : sourceconcepts) {
            String sourcelocalname = getLocalName(getConceptURI(sid, Constants.ONTOLOGY_1));
            for (Integer tid : targetconcepts) {
                count++;
                if(this.is_Database == false){
                    if(step == Constants.STEP_CLASS){
                        this.executortasks.add(new testMappingtask(sourceontology.getURITable().getURI(sid),targetontology.getURITable().getURI(tid)));
                    }
                    else if(step == Constants.STEP_SLOT){
                        String targetlocalname = getLocalName(getConceptURI(tid, Constants.ONTOLOGY_2));
                        tasklist.put(count, new Task(sid, tid, sourceontology.getlexicons(sid), targetontology.getlexicons(tid)));
                        int conceptId = mapconceptTable.getCPairId(this.get_mappableontologiesId(), sourcelocalname, targetlocalname);
                        if (conceptId < 0) {
                            insertStatement.add(mapconceptTable.generateInsertStatement(this.get_mappableontologiesId(), sourcelocalname, targetlocalname,step));
                        }   
                    }
                }
                if(this.is_Database == true){
                    String targetlocalname = getLocalName(getConceptURI(tid, Constants.ONTOLOGY_2));
                    tasklist.put(count, new Task(sid, tid, sourceontology.getlexicons(sid), targetontology.getlexicons(tid)));
                    if(count % 1000000 == 0)
                        System.out.println("count = " + count);
                    //int conceptId = mapconceptTable.getCPairId(this.get_mappableontologiesId(), sourcelocalname, targetlocalname);
                    //if (conceptId < 0) {
                        insertStatement.add(mapconceptTable.generateInsertStatement(this.get_mappableontologiesId(), sourcelocalname, targetlocalname,step));
                    //}   
                }
            }
        }
        if((this.is_Database == true) || (step == Constants.STEP_SLOT)){
            if (insertStatement.size() > 0) {
                mapconceptTable.executeStatements(insertStatement);
            }
        }
        ResourceManager.close(sqlConn);
    }
    /**
    * Generate a class list to be matched
    * author huali50
    * @param step: property or class
    */
    public void generate_classtasklist(int step) {
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getClassLexicons();
        Set<Integer> targetlexicons = targetontology.getClassLexicons();
        int count = 1;
        /*
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
        */
        for (Integer i : sourcelexicons) {

            for (Integer j : targetlexicons) {
                tasklist.put(count, new Task(i, j, sourceontology.getclasslexicons(i), targetontology.getclasslexicons(j)));
                count++;
                System.out.println(count);
            }

        }

    }
    /**
     * Get Task_List
     * author huali50
     * @param tasklist 
     */
    public HashMap<Integer, Task> getTasklist() {
        return this.tasklist;
    }
    /**
     * Get matching algos instance
     * author huali50
     * @param matchingAlgos 
     */
    public testMatchingAlgos getmatchingalgos() {
        return this.matchingAlgos;
    }
    /**
     * Encapsulates the slot suggestion with merging info, and add it to the history stack.
     *
     * @param history encapsulation of the accepted suggestion.
     *
     */
    public void processSlotSuggestion(testHistory history) {

        generalSuggestionVector.remove(history.getPair());
        historyStack.add(history);

        //set merging information to the slot
        setSlotInfo(
                history, ToDo);

    }
    /**
     * Encapsulates the class suggestion with merging info, and add it to the history stack.
     * update huali50
     * @param history encapsulation of the accepted suggestion.
     *
     */
    public int processClassSuggestion(testHistory history) {
        testPair pair = history.getPair();
        if (!historyStack.contains(history)) {
            historyStack.add(history);
        }
        if(generalSuggestionVector.isEmpty() == false){
            testPair temp = (testPair) generalSuggestionVector.get(0);
            if(temp != null){
                boolean result = pair.equals(temp);
                 generalSuggestionVector.remove(pair);
        
            }
        }


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
        testPair pair = h.getPair();

        //to do the action
        testMOntology source_ontology = this.getOntManager().getontology(Constants.ONTOLOGY_1);
        testMOntology target_ontology = this.getOntManager().getontology(Constants.ONTOLOGY_2);
        testMProperty p1 = source_ontology.getProperty(pair.getSource());
        testMProperty p2 = target_ontology.getProperty(pair.getTarget());
        if (set) {
            //to merge the pair of slots
            if (h.getAction() == Constants.ALIGN_SLOT) {
                if (h.getName() != null && h.getName().trim().length() > 0) {                   
                    p1.setAlignName(h.getName());
                    p2.setAlignName(h.getName());
                }
                p1.setAlignElement(p2);
                p2.setAlignElement(p1);
            }
            if (h.getComment() != null && h.getComment().trim().length() > 0) {
                p1.addAlignComment(h.getComment());
                p2.addAlignComment(h.getComment());
            } //undo the action
        } else {
            p1.setAlignElement(null);
            p2.setAlignElement(null);
            p1.setAlignName(null);
            p2.setAlignName(null);
            p1.addAlignComment(null);
            p2.addAlignComment(null);
        }
    }
    /**
     * Set the matching information
     * update huali50
     * @param h: testHistory the history of matching process
     * @param set: if the pair is matched
     **/
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
    /**
     * get the number of remaining suggestions in the current step
     * @return the number of the remaining suggestions
     */
    public int suggestionsRemaining() {

        return generalSuggestionVector.size() - 1;
    }
   /**
     * Undo the previous action on slots
     */
    public void undoSlotMerge() {

        if (!historyStack.isEmpty()) {
            //the previously processed pair of elements
            testHistory history = (testHistory) historyStack.remove();
            //insert this suggestion to the first element of the suggestion list
            generalSuggestionVector.add(0, history.getPair());
            setSlotInfo(history, UnDo);

        }
    }
    /**
     * Undo the latest action
     */
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
    /**
    * Return the id of ontologies
    * author huali50
    **/
    public int get_mappableontologiesId() {
        return this.mappable_ontologies_id;
    }
    /**
    * Return the concept URI
    * author huali50
    * @param Id-concept ID
    * @param ontology-source or target
    **/
    public String getConceptURI(int Id, int ontology) {
        return this.testOntManager.getontology(ontology).getURITable().getURI(Id);
    }
    /**
    * Return Local name
    * author huali50
    **/
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
    /**
    * Return OntManager object
    * author huali50
    * @return testIntManager
    **/
    public testOntManager getOntManager() {
        return this.testOntManager;
    }
    /*check name conflict
     *
     *@name the name to be checked
     *
     *@return the number of the ontology in which the name already exist
     *        if the name is unique, return -1;
     */
    public int checkLabelConflict(String name) {

        if (existInOnto(name, Constants.ONTOLOGY_1)) {
            return Constants.ONTOLOGY_1;

        }

        if (existInOnto(name, Constants.ONTOLOGY_2)) {
            return Constants.ONTOLOGY_2;

        }

        return Constants.UNIQUE;
    }
    /*check name conflict
     *
     *@name the name
     *@ontonum the number of the ontology
     *
     *@return if the name also exist in the indicated ontology, return the num;
     *        if the name is unique, return -1;
     */
    public int checkLabelConflict(String name, int ontonum) {
        if(name == null)
            return ontonum;
        if (existInOnto(name, ontonum)) {
            return ontonum;
        }
        return Constants.UNIQUE;
    }
    /* check the new name whether exists in the ontology
     *update huali50
     *@param name
     *@param ontonum
     *@return if exists, return true
     */
    private boolean existInOnto(String name, int ontonum) {
        for (Integer i : getOntManager().getontology(ontonum).getClasses().keySet()) {
            testMClass c = getOntManager().getontology(ontonum).getClasses().get(i);
            if (name.equalsIgnoreCase(c.getLabel())) {
                return true;
            }
        }
        return false;
    }
    /** Check if the labels from two class are same
    * author huali50
    * @param tc1: class 1
    * @param tc2: class 2
    **/
    public boolean hasEquivLabel(testMClass tc1, testMClass tc2) {
        if ((tc1.getLabel() == null) ||(tc2.getLabel() == null) )
            return false;
        if (tc1.getLabel().equalsIgnoreCase(tc2.getLabel())) {
            return true;
        }
        return false;
    }
    /** Merge the remaining suggestions
    * update huali50
    **/
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
    /** Finalize the class merging
    * update huali50
    **/
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
    /** Get History Vector
    * author huali50
    * @return historyVector
    **/
    public Vector getHistory() {
        return historyVector;
    }
    /** Get History Stack
    * author huali50
    * @return historyStack
    **/
    public Vector getCurrentHistory() {
        return historyStack;
    }
    /** Get Suggestion Vector
    * author huali50
    * @return generalSuggestionVector
    **/
    public Vector getRemainingSuggestions() {
        return generalSuggestionVector;
    }
    /** Save matching result to files
    * author huali50
    * @param String alignfile: alignment file
    * @param String mergefile: merge file(no use so far)
    **/
    public void finalize(String alignfile, String mergefile) throws FileNotFoundException{
        saveRDF(alignfile);
    }
    /** Save the matching result into a file
    * author huali50
    * @param String file: file path
    * 
    **/
    public void saveRDF(String file) throws FileNotFoundException{
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        String sourceOURI = sourceontology.getOntologyURI();
        String targetOURI = targetontology.getOntologyURI();
        PrintWriter outStream =new PrintWriter(new FileOutputStream(file));
        outStream.println("<?xml version='1.0' encoding='utf-8'?>");
        outStream.println("<rdf:RDF");
        outStream.println("\t xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'");
        outStream.println("\t xmlns:owl='http://www.w3.org/2002/07/owl#'");
        outStream.println("\t xmlns:rdfs='http://www.w3.org/2000/01/rdf-schema#'");
        outStream.println("\t xmlns:xsd='http://www.w3.org/2001/XMLSchema#' ");
        outStream.println("\t xml:base='SAMBO-Session'>");
        outStream.println("\t\t <owl:Ontology rdf:about=\"\">");
        outStream.println("\t\t\t <owl:imports rdf:resource='" + sourceOURI +"'/>" );
        outStream.println("\t\t\t <owl:imports rdf:resource='" + targetOURI +"'/>" );
        outStream.println("</owl:Ontology>");
        for(Integer i : sourceontology.getClasses().keySet()){
            String out = classToRDF(sourceontology.getClasses().get(i));
            if(out != null){
                out =  "\t\t<rdf:Description rdf:about=\"" + sourceontology.getClasses().get(i).getURI() +"\">\n" + out;
                out = out + "\t\t</rdf:Description>";
                outStream.println(out);
            }
        }
        for(Integer j : sourceontology.getProperties()){
            testMProperty property = sourceontology.getPropertyById(j);
            String out = propertyToRDF(property);
            if(out!= null){
                out =  "\t\t<rdf:Description rdf:about=\"" + property.getURI() +"\">\n" + out;
                out = out + "\t\t</rdf:Description>";
                outStream.println(out);
            }
        }
        outStream.println("</rdf:RDF>");
        outStream.close();
    }
    /** Transfer matching information of each class in ontology
    * author huali50
    * @param testMClass mclass:class instance in ontology
    * 
    **/
    public String classToRDF(testMClass mclass){
        String out = "";
        if(mclass.getAlignClass() == null && mclass.getAlignSubs().isEmpty() && mclass.getAlignSupers().isEmpty())
            return null;
        else
        {
            if(mclass.getAlignClass() != null){
                out = out + "\t\t\t<owl:equivalentClass rdf:resource=\"" + mclass.getAlignClass().getURI() + "\"/>\n";
                out = out + "\t\t\t\t<rdfs:comment>" + mclass.getAlignComment() + " </rdfs:comment>\n";
            }
            
            if(mclass.getAlignSubs()!=null){
                for(Integer i : mclass.getAlignSubs().keySet()){
                    testMClass targetclass = mclass.getAlignSubs().get(i);
                    out = out + "\t\t\t<rdfs:subClassOf>\n";
                    out = out + "\t\t\t\t<rdf:Description rdf:about=\"" + targetclass.getURI() + "\">\n";
                    out = out + "\t\t\t\t\t<rdfs:comment>" + mclass.getAlignComment() + " </rdfs:comment>\n";
                    out = out + "\t\t\t\t</rdf:Description>\n";
                    out = out + "\t\t\t</rdfs:subClassOf>\n";
                }
            }
            if(mclass.getAlignSupers()!=null){
                for(Integer j : mclass.getAlignSupers().keySet()){
                    testMClass targetclass = mclass.getAlignSupers().get(j);
                    out = out + "\t\t\t<rdfs:superClassOf>\n";
                    out = out + "\t\t\t\t<rdf:Description rdf:about=\"" + targetclass.getURI() + "\">\n";
                    out = out + "\t\t\t\t\t<rdfs:comment>" + mclass.getAlignComment() + " </rdfs:comment>\n";
                    out = out + "\t\t\t\t</rdf:Description>\n";
                    out = out + "\t\t\t</rdfs:superClassOf>\n";
                }
            }
        }
        return out;
    }
    /**
     * Transfer matching information of each property in ontology
     * @author huali50
     * @param property
     * @return 
     */
    public String propertyToRDF(testMProperty property){
        String out = "";
        if(property.getAlignElement()== null )
            return null;
        else
        {
            if(property.getAlignElement()!= null){
                out = out + "\t\t\t<owl:equivalentProperty rdf:resource=\"" + property.getAlignElement().getURI() + "\"/>\n";
                out = out + "\t\t\t\t<rdfs:comment>" + property.getAlignComment() + " </rdfs:comment>\n";
            }
        }
        return out;
    }
    /** Get the sourceontology-targetontology id used in database
    * author huali50
    * @return mappable_ontologies_id
    * 
    **/
    public int getMoid(){
        return this.mappable_ontologies_id;
    }
     /** Get the sourceontology-targetontology id used in database
    * author huali50
    * @return mappable_ontologies_id
    *  
    **/
    public HashSet<testPair> getSuggestedpairs(){
        return this.suggestedpairs;
    }
    /**
     * Get List for parallel matching
     * "author huali50
     * @return 
     */
    public ArrayList<testMappingtask> getExecutorlist(){
        return this.executortasks;
    }
    /**
     * Set matcher weight
     * "author huali50
     * @param weight 
     */
    public void setWeight(double[] weight){
        this.weight = weight;
    }
    /**
     * Set combination
     * @author huali50
     * @param combination 
     */
    public void setCombination(int combination){
        this.combination = combination;
    }
    /**
     * Set is_Large
     * @author huali50
     * @param is_Large 
     */
    public void setLarge_scale(){
        this.is_Large = true;
    }
    /**
     * Get is_Large
     * @author huali50
     * @return 
     */
    public boolean getIsLarge(){
        return this.is_Large;
    }
    /**
     * Set is_Paging
     * ¨@author huali50
     */
    public void setDatabase(){
        this.is_Database = true;
    }
    /**
     * Get is_Database
     * @author huali50
     * @return is_Database
     */
    public boolean getIsDatabase(){
        return this.is_Database;
    }
    /**
     * Generate task list and match by block
     * @author huali50
     * @param step 
     */
    public Vector generate_tasklist_match(int step,double downthresh,double upthresh) {
        this.executortasks = new ArrayList<testMappingtask>();
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
        int count = 0;
        int k=0;
        int source_size = sourceconcepts.size();
        int target_size = targetconcepts.size();
        int tasks_size = source_size * target_size;
        this.pageSize = 1000000;
        int task_block_count = tasks_size / this.pageSize;
        int last_block_size = tasks_size % this.pageSize;
        int block_count = 0;
        long time =0;
        for (Integer sid : sourceconcepts) {
            for (Integer tid : targetconcepts) {
                this.executortasks.add(new testMappingtask(sourceontology.getURITable().getURI(sid),targetontology.getURITable().getURI(tid)));
                count++;
                //System.out.println("count : "+count);
                if(count == this.pageSize){
                    //match
                    long start = System.currentTimeMillis();
                    matchexecutor(this.executortasks, downthresh, upthresh);
                    long end = System.currentTimeMillis();
                    //System.out.println("blockcount : "+block_count+" Time: "+(end-start)+"ms.");
                    time += end-start;
                    block_count++;
                    count = 0;
                    this.executortasks.clear();
                }
            }
        }
        if(this.executortasks.size()>0){
            long start = System.currentTimeMillis();
           matchexecutor(this.executortasks, downthresh, upthresh);
            long end = System.currentTimeMillis();
        }
        System.out.println( "Time Taken to Compute " + time + " ms." );
        return this.generalSuggestionVector;
    }
    /**
     * Get Mapping count
     * @author huali50
     * @return 
     */
    public int getMappingcount(){
        return this.mapping_count;
    }
}
