/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.EditDistance;
import se.liu.ida.sambo.algos.matching.testMatchingAlgos;
import se.liu.ida.sambo.util.testPair;
import se.liu.ida.sambo.algos.matching.Matcher;
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
 * @authors  He Tan, Rajaram.
 * @version 2.0
 */
public class testMergerManager {

    //the model manager
    private testOntManager testOntManager;
    private int thread;
    //the matchingAlgos actually perform matchings
    private testMatchingAlgos matchingAlgos;
    private int ii=0;
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
    NameProcessor labelClean=new NameProcessor();
    private HashSet<Integer> matcher_list = new HashSet<Integer>();
    private HashMap<Integer,Task> tasklist = new HashMap<Integer,Task>();

    public static void main(String args[]) throws OWLOntologyCreationException {
        testMergerManager mm = new testMergerManager();
        mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\nose_MA_1.owl","C:\\Users\\huali50\\Desktop\\ontologies\\nose_MeSH_2.owl");
        mm.generate_tasklist();
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
    public void match(Matcher matcher_list){
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getLexicons();
        Set<Integer> targetlexicons = targetontology.getLexicons();
        int count=0;
        int mappingcount = 0;
        long t1 = System.currentTimeMillis();
        for(Integer i : sourcelexicons)
        {
            for(Integer j : targetlexicons)
            {
                //mapinparallel(sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j));
                //System.out.println(edfinalvalue);
                if(count < 1000)
                {
                    //tasklist.put(new Task(i,j,sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j)));
                    mappingcount++;
                    count++;
                }
                if(count == 1000)
                {
                    //System.out.println("----------------------------------------------------------------------------");
                    //matchforkjoin(tasklist);
                    //matchexecutor(tasklist);
                    count =0;
                    tasklist.clear();
                }
                
            }
        }
        
        //matchforkjoin(tasklist);
        long t2 = System.currentTimeMillis();
        System.out.println( "Time Taken to generate " + mappingcount + " mappings in " + (t2-t1) + " ms" );
    }
    public void matchexecutor(ArrayList<Task> tasklist){
        
        ArrayList<testMappingtask> tasks = new ArrayList<testMappingtask>();
        List<Future<testMapping>> results;
        for(int i=0;i<tasklist.size();i++)
        {
            testMappingtask e = new testMappingtask(tasklist.get(i).getsource(),tasklist.get(i).gettarget());
            tasks.add(e);
        }
        ExecutorService exec = Executors.newFixedThreadPool(thread);
        try
        {
            results = exec.invokeAll(tasks);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            results = new ArrayList<Future<testMapping>>();
        }
        exec.shutdown();
        
        for(Future<testMapping> ftm : results)
        {
            try
            {
                ii++;
                testMapping tm = ftm.get();
                
            }
            catch(Exception e)
            {
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
    private class testMappingtask implements Callable<testMapping>
    {
       private HashSet<testLexicon> sourcelexicon;
    private HashSet<testLexicon> targetlexicon;
        testMappingtask(HashSet<testLexicon> s, HashSet<testLexicon> t)
        {
            sourcelexicon = s;
            targetlexicon = t;
        }
        public testMapping call(){
            testMapping tm = new testMapping(sourcelexicon, targetlexicon);
            tm.compute_sim();
            return tm;
        }
    }
    private double mapresults(String source, String target)
    {
        double sim = 0;
        EditDistance test = new EditDistance();
        sim = test.getSimValue(source,target);
        return sim;
    }
    /** Creates new Merger
     */
    public testMergerManager() {
        testOntManager=new testOntManager();
        //thread = Runtime.getRuntime().availableProcessors();
        thread = 4;
        System.out.println("Thread num: "+thread);
       
    }




    /** Loads the ontologies
     *
     **@param url1 the url of the ontology-1
     **@param url2 the url of the ontology-2
     */
    public void loadOntologies(String uri1, String uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);
    }
    public void loadOntologies(URI uri1, URI uri2) throws OWLOntologyCreationException {
        testOntManager.loadOntologies(uri1, uri2);
    }
        /** Loads the ontologies
     *


    /** Load Ontology-1
     *
     *@param url1 the url of the ontology-1
     */
   

    /* Prepare to begin matching
     **/
    public void init() {

        //initialize the history stack
        historyStack = new HistoryStack();
        currentSuggestionVector = new Vector();

        historyVector = new Vector();

        matchingAlgos = new testMatchingAlgos(testOntManager,this);

        tempSuggestionVector = new Vector();
    }

    /** Clear the current merge process
     *
     * @return a new merge manager with already loaded ontologies.
     */
   

    /** Clear the current merge process
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
    public void test_match(int step){
         switch (step) {
            case Constants.STEP_SLOT:
                
                break;
            case Constants.STEP_CLASS:
                
                break;
        }
    }
    /**
     * Calls the matching algorithms to calculate similarity value for MappableGroup
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
     * @param step  Slot/Class matching step.
     * 
     * @return  List of un validated mapping suggestion
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
     * @param step  Slot/class matching step.
     */
   
    
    /**
     * Load mapping suggestions for single threshold alignment strategy.
     * 
     * @param step                  Slot/Class matching. 
     * @param weights               Matchers weight.
     * @param threshold             Threshold for filtering.
     * @param combinationMethod     Combination method.
     * 
     * @return      List of mapping suggestions. 
     */
    public Vector getSuggestions(int step, double[] weights, double threshold, 
            String combinationMethod) {
        
        switch (step) {
            case Constants.STEP_SLOT:
                generalSuggestionVector = matchingAlgos.getSlotSugs(weights,
                        threshold, combinationMethod);
                break;
            case Constants.STEP_CLASS:
                generalSuggestionVector = matchingAlgos.getClassSugs(weights, 
                        threshold, combinationMethod);
                break;
        }
        return generalSuggestionVector;
    }
    
    /**
     * Load mapping suggestions for double threshold alignment strategy.
     * 
     * @param weights               Matchers weight.
     * @param upperthreshold        Upper threshold.
     * @param lowerthreshold        Lower threshold.
     * @param combinationMethod     Combination method.
     * 
     * @return      List of mapping suggestions.
     */
    public Vector getSuggestions(double[] weights, double upperthreshold, 
            double lowerthreshold, String combinationMethod) {
        
        generalSuggestionVector = matchingAlgos.getClassSugs(weights, 
                upperthreshold, lowerthreshold, combinationMethod);
        return generalSuggestionVector;
    }
    
    /**
     * Load mapping suggestions for single threshold alignment strategy which 
     * uses mappable concept pairs alone.
     * 
     * @param weights               Matchers weight.
     * @param threshold             Threshold for filtering.
     * @param combinationMethod     Combination method.
     * 
     * @return      List of mapping suggestions.
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
     * @param weights               Matchers weight.
     * @param upperthreshold        Upper threshold.
     * @param lowerthreshold        Lower threshold.
     * @param combinationMethod     Combination method.
     * 
     * @return      List of mapping suggestions.
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
     *  from the first ontology, that is one element from the first ontology and the
     *  possibilities for merging with different elements in the second ontology.
     *
     * @return the vector of Pairs containing the suggested merges
     */
    public Vector getNextSuggestionList() {
        currentSuggestionVector.removeAllElements();


        if (!generalSuggestionVector.isEmpty()) {
            //Pick out the first element and group all suggestions containing that element
            testPair pair = (testPair) generalSuggestionVector.firstElement();


            //return currentSuggestionVector = Constants.getHoldingPairs(pair.getObject1(), generalSuggestionVector);
            return null;

        }
        return currentSuggestionVector;


    }

    /**
     * Encapsulates the slot suggestion with merging info, and add it to the history stack.
     *
     * @param history encapsulation of the accepted suggestion.
     *
     */
  

   

    /**
     * Undo the previous action on slots
     */
   
    /**
     * Finalizes the slot merging
     * It is not possible to undo merged slots after the finalize method has been run.
     */
    public void finalizeSlotSuggestions() {

        //clear lists
        historyStack.removeAllElements();
        generalSuggestionVector.removeAllElements();


    }

    //Added by MZK
    public void matchforkjoin(ArrayList<Task> tasklist){
        int tasksize = tasklist.size();
        ComputeTask task =new ComputeTask(0,tasksize);
        task.settasklist(tasklist);
        task.compute();
    }
    public HashSet getMatcherList(){
        return this.matcher_list;
    }
    public void generate_tasklist(){
        testMOntology sourceontology = testOntManager.getontology(Constants.ONTOLOGY_1);
        testMOntology targetontology = testOntManager.getontology(Constants.ONTOLOGY_2);
        Set<Integer> sourceclasses = sourceontology.getMClasses();
        Set<Integer> targetclasses = targetontology.getMClasses();
        Set<Integer> sourcelexicons = sourceontology.getLexicons();
        Set<Integer> targetlexicons = targetontology.getLexicons();
        int count = 1;
        for(Integer i : sourcelexicons)
        {
            for(Integer j : targetlexicons)
            {
                tasklist.put(count,new Task(i,j,sourceontology.getclasslexicons(i),targetontology.getclasslexicons(j)));
                count++;
            }
        }
    }
    public HashMap<Integer,Task> getTasklist(){
        return this.tasklist;
    }
    public testMatchingAlgos getmatchingalgos(){
        return this.matchingAlgos;
    }
}
    
