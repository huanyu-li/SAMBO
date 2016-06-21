/*
 * Merger.java
 *
 */
package se.liu.ida.sambo.Merger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
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
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.MModel.MProperty;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.algos.matching.MatchingAlgos;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.HistoryStack;
import se.liu.ida.sambo.util.Pair;

/** 
 * <p>
 * The class controls the merging process.
 * </p>
 * 
 * @authors  He Tan, Rajaram.
 * @version 2.0
 */
public class MergeManager {

    //the model manager
    private OntManager ontManager;
    //the matchingAlgos actually perform matchings
    private MatchingAlgos matchingAlgos;
    
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

    public static void main(String args[]) {
        MergeManager mm = new MergeManager();
        long t1 = System.currentTimeMillis();
        mm.loadOntologies("file:///C:/Users/huali50/Downloads/LargeBio_dataset_oaei2015/oaei2014_FMA_whole_ontology.owl","file:///C:/Users/huali50/Downloads/LargeBio_dataset_oaei2015/oaei2014_NCI_whole_ontology.owl");
        long t2 = System.currentTimeMillis();
        System.out.println( "Time Taken to LOAD FILE " + (t2-t1) + " ms" );
        //mm.init();

        mm.finalize("file:///C:/Users/huali50/Downloads/LargeBio_dataset_oaei2015/ontologies/nose_MA_1.owl","file:///C:/Users/huali50/Downloads/LargeBio_dataset_oaei2015/ontologies/nose_MeSH_2.owl");

    }

    /** Creates new Merger
     */
    public MergeManager() {

        ontManager = new OntManager();
    }

    /**Create a new merger with already loaded ontologies.
     */
    public MergeManager(OntManager ontManager) {

        this.ontManager = ontManager;
    }

    /** Loads the ontologies
     *
     *@param url1 the url of the ontology-1
     *@param url2 the url of the ontology-2
     */
    public void loadOntologies(URL url1, URL url2) {

        ontManager.loadOntology(url1, Constants.ONTOLOGY_1);
        ontManager.loadOntology(url2, Constants.ONTOLOGY_2);
    }

    /** Load Ontology-1
     *
     *@param url1 the url of the ontology-1
     */
    public void loadOntology1(URL url1) {

        ontManager.loadOntology(url1, Constants.ONTOLOGY_1);
    }

    /** Load Ontology-2
     *
     *@param url2 the url of the ontology-2
     */
    public void loadOntology2(URL url2) {

        ontManager.loadOntology(url2, Constants.ONTOLOGY_2);
    }

    /** Loads the ontologies
     *
     **@param url1 the url of the ontology-1
     **@param url2 the url of the ontology-2
     */
    public void loadOntologies(String url1, String url2) {

        ontManager.loadOntology(url1, Constants.ONTOLOGY_1);
        ontManager.loadOntology(url2, Constants.ONTOLOGY_2);
    }

    /** Load Ontology-1
     *
     *@param url1 the url of the ontology-1
     */
    public void loadOntology1(String url1) {

        ontManager.loadOntology(url1, Constants.ONTOLOGY_1);
    }

    /** Load Ontology-2
     *
     *@param url1 the url of the ontology-1
     */
    public void loadOntology2(String url2) {

        ontManager.loadOntology(url2, Constants.ONTOLOGY_2);
    }

    /* Prepare to begin matching
     **/
    public void init() {

        //initialize the history stack
        historyStack = new HistoryStack();
        currentSuggestionVector = new Vector();

        historyVector = new Vector();

        matchingAlgos = new MatchingAlgos(ontManager);

        tempSuggestionVector = new Vector();
    }

    /** Clear the current merge process
     *
     * @return a new merge manager with already loaded ontologies.
     */
    public MergeManager clearMerge() {

        ontManager.clearOntology(Constants.ONTOLOGY_NEW);

        ontManager.resetOntology(Constants.ONTOLOGY_1);
        ontManager.resetOntology(Constants.ONTOLOGY_2);

        return new MergeManager(ontManager);
    }

    /** Clear the current merge process
     *
     * @parameter n the number indicate which ontology is closed.
     *
     * @return a new merge manager with already loaded ontologies.
     */
    public MergeManager clearMerge(int n) {

        ontManager.clearOntology(Constants.ONTOLOGY_NEW);
        ontManager.clearOntology(n);

        if (n == Constants.ONTOLOGY_1) {
            ontManager.resetOntology(Constants.ONTOLOGY_2);
        } else {
            ontManager.resetOntology(Constants.ONTOLOGY_1);
        }

        return new MergeManager(ontManager);
    }

    /**
     * Calls the matching algorithms to calculate similarity value
     *
     * @param step the steps: STEP_SLOT, STEP_CLASS.
     * @param matcher the matcher
     *
     * @return a list of suggestions
     */
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
    public void loadProcessedSuggestions(int step, double[] weights) {
        historyStack.clear();
        for (int i = 0; i < Commons.XMLProcessedSuggestionVector.size(); i++) {
            Pattern pattern = Pattern.compile("\\[\\[(.*):(.*),1\\], "
                    + "\\[(.*):(.*),2\\], (.*)\\]");
            Matcher matcher = pattern.matcher(Commons.
                    XMLProcessedSuggestionVector.elementAt(i).toString());
            String localname1 = "";
            String localname2 = "";
            if (matcher.find()) {
                localname1 = matcher.group(2).trim();
                localname2 = matcher.group(4).trim();
            }
            int aInt = Integer.parseInt(Commons.
                    strProcessedSuggestionsNum[i]);
            int action = Integer.parseInt(Commons.
                    strProcessedSuggestionsAction[i]);
            Pair pair;
            if (step == Constants.STEP_SLOT) {
                pair = new Pair(this.getOntology(Constants.ONTOLOGY_1).
                        getProperty(localname1),
                        this.getOntology(Constants.ONTOLOGY_2).
                        getProperty(localname2));
                reProcessSlotSuggestion(new History(pair, Commons.
                        strProcessedSuggestionsName[i],
                        aInt, action, Commons.
                        strProcessedSuggestionsComment[i]));
            } else {
                pair = new Pair(this.getOntology(Constants.ONTOLOGY_1).
                        getClass(localname1),
                        this.getOntology(Constants.ONTOLOGY_2).
                        getClass(localname2));
                processClassSuggestion(new History(pair, Commons.
                        strProcessedSuggestionsName[i],
                        aInt, action, Commons.
                        strProcessedSuggestionsComment[i]));
            }
        }
    }
    
    /**
     * Gets validated mapping suggestion.
     * 
     * @param step  Slot/class matching step.
     */
    public void loadProcessedSuggestions(int step) {
        historyStack.clear();
        for (int i = 0; i < Commons.XMLProcessedSuggestionVector.size(); i++) {
            Pattern pattern = Pattern.compile("\\[\\[(.*):(.*),1\\], "
                    + "\\[(.*):(.*),2\\], (.*)\\]");
            Matcher matcher = pattern.matcher(Commons.
                    XMLProcessedSuggestionVector.elementAt(i).toString());
            String localname1 = "";
            String localname2 = "";
            if (matcher.find()) {
                localname1 = matcher.group(2).trim();
                localname2 = matcher.group(4).trim();
            }
            int aInt = Integer.parseInt(Commons.
                    strProcessedSuggestionsNum[i]);
            int action = Integer.parseInt(Commons.
                    strProcessedSuggestionsAction[i]);
            Pair pair;
            if (step == Constants.STEP_SLOT) {
                pair = new Pair(this.getOntology(Constants.ONTOLOGY_1).
                        getProperty(localname1),
                        this.getOntology(Constants.ONTOLOGY_2).
                        getProperty(localname2));
                reProcessSlotSuggestion(new History(pair, Commons.
                        strProcessedSuggestionsName[i],
                        aInt, action, Commons.
                        strProcessedSuggestionsComment[i]));
            } else {
                pair = new Pair(this.getOntology(Constants.ONTOLOGY_1).
                        getClass(localname1),
                        this.getOntology(Constants.ONTOLOGY_2).
                        getClass(localname2));
                processClassSuggestion(new History(pair, Commons.
                        strProcessedSuggestionsName[i],
                        aInt, action, Commons.
                        strProcessedSuggestionsComment[i]));
            }
        }
    }
    
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
    public Pair getNextSuggestion() {
        if (generalSuggestionVector.isEmpty()) {
            return new Pair();


        } else {
            return (Pair) generalSuggestionVector.firstElement();


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
            Pair pair = (Pair) generalSuggestionVector.firstElement();


            return currentSuggestionVector = Constants.getHoldingPairs(pair.getObject1(), generalSuggestionVector);


        }
        return currentSuggestionVector;


    }

    /**
     * Encapsulates the slot suggestion with merging info, and add it to the history stack.
     *
     * @param history encapsulation of the accepted suggestion.
     *
     */
    public void processSlotSuggestion(History history) {

        generalSuggestionVector.remove(history.getPair());
        historyStack.add(history);

        //set merging information to the slot
        setSlotInfo(
                history, ToDo);


    }

    public void reProcessSlotSuggestion(History history) {

        //generalSuggestionVector.remove(history.getPair());
        historyStack.add(history);

        //set merging information to the slot
        setSlotInfo(
                history, ToDo);


    }

    /**
     * Undo the previous action on slots
     */
    public void undoSlotMerge() {

        if (!historyStack.isEmpty()) {
            //the previously processed pair of elements
            History history = (History) historyStack.remove();
            //insert this suggestion to the first element of the suggestion list
            generalSuggestionVector.add(0, history.getPair());
            setSlotInfo(
                    history, UnDo);


        }
    }

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
    public void reFinalizeSlotSuggestions() {

        //clear lists
        historyStack.removeAllElements();
        //generalSuggestionVector.removeAllElements();


    }

    /**
     * Encapsulates the class suggestion with merging info, and add it to the history stack.
     *
     * @param history encapsulation of the accepted suggestion.
     *
     */
    public int processClassSuggestion(History history) {

        Pair pair = history.getPair();
        
        if(!historyStack.contains(history))
        {
        historyStack.add(history);
        }
        generalSuggestionVector.remove(pair);
        

        //update historyStack due to the merged classes in the pair to be merged
        
        
        
        //******************** UNCOMMEND IT LATER TO TURN ON FILT-FILTER******************


        if (history.getAction() == Constants.ALIGN_CLASS) {
            for (Enumeration e = Constants.getHoldingPairs(pair, generalSuggestionVector).elements(); e.hasMoreElements();) {
                Pair p = (Pair) e.nextElement();
                
                if(!historyStack.contains(history))
                {
                historyStack.updateMostRecent(new History(p));
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

    /**
     * Merge the remaining suggestions
     */
    public void mergeRemaining() {

        Pair pair = (Pair) generalSuggestionVector.firstElement();
        History history = new History(pair, null, Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS);
        historyStack.add(history);
        setClassInfo(
                history, ToDo);



        while (true) {
            generalSuggestionVector.remove(pair);

            //update historyStack due to the classes in the pair to be merged


            for (Enumeration e = Constants.getHoldingPairs(pair, generalSuggestionVector).elements(); e.hasMoreElements();) {
                Pair p = (Pair) e.nextElement();
                generalSuggestionVector.remove(p);
                historyStack.updateMostRecent(new History(p));


            }

            if (generalSuggestionVector.isEmpty()) {
                break;


            }

            pair = (Pair) generalSuggestionVector.firstElement();
            history = new History(pair, null, Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS);
            historyStack.updateMostRecent(history);
            setClassInfo(
                    history, ToDo);


        }
    }

    /**
     * Undo the latest action
     */
    public void undoClassMerge() {

        if (!historyStack.empty()) {
            //the previously processed list of suggestions
            Vector previous = historyStack.removeMostRecent();


            for (Enumeration e = previous.elements(); e.hasMoreElements();) {

                History history = (History) e.nextElement();
                //insert this suggestion to the first element of the suggestion list
                generalSuggestionVector.add(0, history.getPair());
                setClassInfo(
                        history, UnDo);


            }
        }
    }

    /** Finalize the class merging
     * class merges cannot be undone after this step
     */
    public void finalizeClassSuggestions() {

        //backup the class history stack
        for (Enumeration e = historyStack.elements(); e.hasMoreElements();) {
            History h = (History) e.nextElement();



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

    /**
     * Finish the process.
     *
     *@param language the number indicating the ontology language
     */
    public void finalize(String alignfile, String mergefile) {

        ontManager.createAlignOntModel();
        ontManager.writeOntology(alignfile, mergefile);


    }

    /**
     * Returns a list suggestions which have been processed suggestions by the user.
     * @return a vector processed suggestion
     */
    public Vector getHistory() {
        return historyVector;


    }

    /**
     * get the number of remaining suggestions in the current step
     * @return the number of the remaining suggestions
     */
    public int suggestionsRemaining() {

        return generalSuggestionVector.size() - 1;


    }

    /**
     * get the remaining suggestions in the current step
     * @return the number of the remaining suggestions
     */
    public Vector getRemainingSuggestions() {

        return generalSuggestionVector;


    }

    /**
     *get the list of the history in current step
     *@return the vector
     */
    public Vector getCurrentHistory() {
        return historyStack;


    }

    /**
     *get one ontology involved in the merging
     *
     *@param num the number indicate the ontology
     *
     *@return the ontology
     */
    public MOntology getOntology(int num) {
        return ontManager.getMOnt(num);


    }

    /**
     *get the ontology manager
     *
     *@return the ontology manager
     */
    public OntManager getOntManager() {
        return ontManager;


    } ///////////////////////////////////////
    //set merging information to the slots

    void setSlotInfo(History h, boolean set) {

        Pair pair = h.getPair();

        //to do the action


        if (set) {
            //to merge the pair of slots
            if (h.getAction() == Constants.ALIGN_SLOT) {

                if (h.getName() != null && h.getName().trim().length() > 0) {

                    ((MProperty) pair.getObject1()).setAlignName(h.getName());


                    ((MProperty) pair.getObject2()).setAlignName(h.getName());


                }

                ((MProperty) pair.getObject1()).setAlignElement((MProperty) pair.getObject2());


                ((MProperty) pair.getObject2()).setAlignElement((MProperty) pair.getObject1());


            }

            if (h.getComment() != null && h.getComment().trim().length() > 0) {

                ((MProperty) pair.getObject1()).addAlignComment(h.getComment());


                ((MProperty) pair.getObject2()).addAlignComment(h.getComment());


            } //undo the action
        } else {

            ((MProperty) pair.getObject1()).setAlignElement(null);


            ((MProperty) pair.getObject2()).setAlignElement(null);


            ((MProperty) pair.getObject1()).setAlignName(null);


            ((MProperty) pair.getObject2()).setAlignName(null);



            ((MProperty) pair.getObject1()).addAlignComment(null);


            ((MProperty) pair.getObject2()).addAlignComment(null);


        }
    }

    /////////////////////////////////////////////////
    //set or unset merging action info to the classes
    int setClassInfo(History h, boolean set) {

        Pair pair = h.getPair();

        //to do the action specified in the history
        //and check the name conflict and give the warning


        if (set) {
            //to merge the pair of the classes
            if (h.getAction() == Constants.ALIGN_CLASS) {

                if (h.getName() != null && h.getName().trim().length() > 0) {
                    //the value will be minus, if the new name conflict                    
                    h.setWarning(checkLabelConflict(h.getName()));
                    System.out.println(h.getName());


                    ((MClass) pair.getObject1()).setAlignName(h.getName());


                    ((MClass) pair.getObject2()).setAlignName(h.getName());



                } else {

                    //if the classes to aligned do not have equivalent names or synonyms,
                    //they could be equal to other names or synonyms
                    if (!hasEquivLabel((MClass) pair.getObject1(), (MClass) pair.getObject2())) //if name1 is unique, check the name2
                    {
                        if (h.setWarning(checkLabelConflict(((MClass) pair.getObject1()).getLabel(), Constants.ONTOLOGY_2)) == Constants.UNIQUE) {
                            h.setWarning(checkLabelConflict(((MClass) pair.getObject2()).getLabel(), Constants.ONTOLOGY_1));


                        }
                    }
                }

                //the name for the merged class is the name of the first class
                ((MClass) pair.getObject1()).setAlignElement((MClass) pair.getObject2());


                ((MClass) pair.getObject2()).setAlignElement((MClass) pair.getObject1());

                //to include is-a relation between the pair of the classes


            } else if (h.getAction() == Constants.IS_A_CLASS) {

                //the first class is the super-class
                if (h.getNum() == Constants.ONTOLOGY_1) {
                    ((MClass) pair.getObject2()).addAlignSuper((MClass) h.getPair().getObject1());


                } //the second class is the super-class
                else {
                    ((MClass) pair.getObject1()).addAlignSuper((MClass) h.getPair().getObject2());


                } //if the classes has the same name or synonyms
                if (hasEquivLabel((MClass) pair.getObject1(), (MClass) pair.getObject2())) {
                    h.setWarning(Constants.ONTOLOGY_1);


                }

            } else if (h.getAction() == Constants.NO) {

                if (hasEquivLabel((MClass) pair.getObject1(), (MClass) pair.getObject2())) {
                    h.setWarning(Constants.ONTOLOGY_1);


                }
            }


            if (h.getComment() != null && h.getComment().trim().length() > 0) {

                ((MClass) pair.getObject1()).addAlignComment(h.getComment());


                ((MClass) pair.getObject2()).addAlignComment(h.getComment());


            } //undo the action specified in the history
        } else {

            ((MClass) pair.getObject1()).setAlignName(null);


            ((MClass) pair.getObject2()).setAlignName(null);



            ((MClass) pair.getObject1()).addAlignComment(null);


            ((MClass) pair.getObject2()).addAlignComment(null);

            //undo the merge action


            if (h.getAction() == Constants.ALIGN_CLASS) {

                ((MClass) pair.getObject1()).setAlignElement(null);


                ((MClass) pair.getObject2()).setAlignElement(null);

                //undo include is-a relation action


            } else if (h.getAction() == Constants.IS_A_CLASS) {

                //the first class is the super class
                if (h.getNum() == Constants.ONTOLOGY_1) {
                    ((MClass) pair.getObject2()).removeAlignSuper();


                } else {
                    ((MClass) pair.getObject1()).removeAlignSuper();


                }
            }
        }

        return h.getWarning();


    }

    /* check the new name whether exists in the ontology
     *
     *@param name
     *@param ontonum
     *@return if exists, return true
     */
    private boolean existInOnto(String name, int ontonum) {

        for (Enumeration e1 = getOntology(ontonum).getProperties().elements(); e1.hasMoreElements();) {
            MProperty p = (MProperty) e1.nextElement();



            if (name.equalsIgnoreCase(p.getLabel())) {
                return true;


            }

            if ((p.getAlignElement() != null && p.getAlignElement().getLabel().equalsIgnoreCase(name))) {
                return true;


            }

            if ((p.getAlignName() != null && p.getAlignName().equalsIgnoreCase(name))) {
                return true;


            }
        }


        for (Enumeration e2 = getOntology(ontonum).getClasses().elements(); e2.hasMoreElements();) {
            MClass c = (MClass) e2.nextElement();



            if (name.equalsIgnoreCase(c.getLabel())) {
                return true;


            }

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
        }

        return false;


    }

    /* check name conflict
     *
     * @name the name to be check
     * @class check whether the name equals to the class's name and its synonym
     *
     * @return if equals, return true
     */
    public boolean hasEquivLabel(MElement c1, MElement c2) {
        
        //Concept's label without special characters
//        String c1Label=labelClean.basicCleanName(c1.getLabel());
//        String c2Label=labelClean.basicCleanName(c2.getLabel());        
//
//        if (c1Label.equalsIgnoreCase(c2Label)) {
//            return true;
//
//
//        }
        
        
        if (c1.getPrettyName().equalsIgnoreCase(c2.getPrettyName())) {
            return true;


        }


        if (c1.isMClass() && c2.isMClass()) {
            // if there is a c1's prettySyn == c2.prettyName
            for (Enumeration en1 = ((MClass) c1).getPrettySyn().elements(); en1.hasMoreElements();) {
                if (c2.getPrettyName().equalsIgnoreCase(((String) en1.nextElement()))) {
                    return true;


                }
            }
            // if there is a c2's prettySyn == c1.prettyName
            for (Enumeration en2 = ((MClass) c2).getPrettySyn().elements(); en2.hasMoreElements();) {
                if (c1.getPrettyName().equalsIgnoreCase(((String) en2.nextElement()))) {
                    return true;


                }
            }
            // if there is one case that c1's prettySyn == c2's prettySyn
            for (Enumeration en1 = ((MClass) c1).getPrettySyn().elements(); en1.hasMoreElements();) {
                String s1 = (String) en1.nextElement();



                for (Enumeration en2 = ((MClass) c2).getPrettySyn().elements(); en2.hasMoreElements();) {
                    if (((String) en2.nextElement()).equalsIgnoreCase(s1)) {
                        return true;


                    }
                }
            }
        }

        return false;


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

        if (existInOnto(name, ontonum)) {
            return ontonum;


        }

        return Constants.UNIQUE;



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

    
    //**Added By Rajaram For PRA as oracle Recommendation
    public ArrayList<String> getAlignment() 
    {
       return matchingAlgos.getAcceptedPairs();
                }
    
    public ArrayList<String> getPRARejection() 
    {
       return matchingAlgos.getRejectedPairs();
                }
    //**

    
}
