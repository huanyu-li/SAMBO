/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.liu.ida.sambo.MModel.testMClass;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.testFormHandler;
import se.liu.ida.sambo.ui.web.testPageHandler;
import se.liu.ida.sambo.util.testHistory;
import se.liu.ida.sambo.util.testPair;
import se.liu.ida.sambo.util.testSuggestion;

/**
 *
 * @author huali50
 */
public class testClassServlet extends HttpServlet {

     //browse the two source ontologies
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 4;
        // Add merge object to the session
        testMergerManager merge = (testMergerManager)session.getAttribute(session.getId());
        SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
        /*
        //expand tree structure
        if(req.getParameter("classname1") != null)
            merge.getOntology(Constants.ONTOLOGY_1).getClass(req.getParameter("classname1")).turnDisplay();
        else if(req.getParameter("classname2") != null)
            merge.getOntology(Constants.ONTOLOGY_2).getClass(req.getParameter("classname2")).turnDisplay();
        
        //showAll or hideAll
        if(req.getParameter("allOne") != null)
            showAll(session, merge.getOntology(Constants.ONTOLOGY_1), "display1");
        
        if(req.getParameter("allTwo") != null)
            showAll(session, merge.getOntology(Constants.ONTOLOGY_2), "display2");
        */
        
        try{
            
            out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
            out.println(testFormHandler.createManualClassForm(merge, settings, Constants.UNIQUE));
            out.println(testPageHandler.createFooter() );
            
        } finally {
            out.close();
        }
        
    }
    
    //show/hide all hierarchy
    private void showAll(HttpSession session, testMOntology onto, String whichOnto){
        
        boolean display = !((Boolean)session.getAttribute(whichOnto)).booleanValue();
        session.setAttribute(whichOnto,  new Boolean(display));
        for(Integer i : onto.getClasses().keySet()){
            testMClass tl = onto.getClasses().get(i);
            tl.setDisplay(display);
        }
    }
    
    
    //diplay the searched class
    private void display(testMClass mclass){
        
        mclass.setDisplay(true);
        for(Integer i : mclass.getSuperClasses().keySet()){
            testMClass tl = mclass.getSuperClasses().get(i);
            display(tl);
        }
        for(Integer i : mclass.getPartOf().keySet()){
            testMClass tl = mclass.getSuperClasses().get(i);
            display(tl);
        }
    }
    
    
    //extract the indicated pair of class from the list
    //the default one is the first pair in the list
    private testPair acceptedSug(HttpServletRequest req, HttpSession session, Vector sug){
        
        // Extract the parameter sent from the selection form
        String acceptedPairnum = req.getParameter("classPair");
        if (acceptedPairnum != null) {
            int choice = Integer.parseInt(acceptedPairnum);
            return (testPair)sug.get(choice);
        } else{
            return (testPair)sug.get(0);
        }
    }
    
    
    //handle the actions
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        
        // get objects attached to the session
        testMergerManager  merge = (testMergerManager) session.getAttribute(session.getId());
        SettingsInfo  settings = (SettingsInfo) session.getAttribute("settings");
        testSuggestion sug = ((testSuggestion) session.getAttribute("sug"));        
        
        //set the mode
        if (req.getParameter("suggestion") != null)
            session.setAttribute("mode", Constants.MODE_SUGGESTION);
        
        if (req.getParameter("manual") != null)
            session.setAttribute("mode", Constants.MODE_MANUAL);
        
        String mode = (String) session.getAttribute("mode");
        mode = "s";
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();        
        
        int warning = Constants.UNIQUE;
        
        // merge one pair of class suggestion
        if (req.getParameter("merge") != null) {
            
            testPair acceptedSuggestion = acceptedSug(req, session, sug.getPairList());
            
            warning = merge.processClassSuggestion(new testHistory(acceptedSuggestion, req.getParameter("newmergename"),
                              Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS, req.getParameter("comment")));
            
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
            
        }else if ((req.getParameter("subclass")!=null) || req.getParameter("superclass")!=null){
            
            testPair acceptedSuggestion = acceptedSug(req, session, sug.getPairList());
            
            if(req.getParameter("subclass")!=null)
                warning = merge.processClassSuggestion(new testHistory(acceptedSuggestion, null, Constants.ONTOLOGY_2,
                        Constants.IS_A_CLASS, req.getParameter("comment")));
            else  warning = merge.processClassSuggestion(new testHistory(acceptedSuggestion, null, Constants.ONTOLOGY_1,
                   Constants.IS_A_CLASS, req.getParameter("comment")));
            
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
            //align all the remaining suggestions
        }else if (req.getParameter("remaining") != null) {
            
            merge.mergeRemaining();
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
        }else if(req.getParameter("manualmerge") != null){
            
            //a pair of class that user select to merge
            if((req.getParameter("manualclass1") != null) && (req.getParameter("manualclass2") != null)){
                
                String classname1 = req.getParameter("manualclass1").trim();
                String classname2 = req.getParameter("manualclass2").trim();
                
                testPair pair = new testPair(classname1,classname2);
                
                warning = merge.processClassSuggestion(new testHistory(pair, req.getParameter("newmergename"), Constants.ONTOLOGY_NEW,
                               Constants.ALIGN_CLASS, req.getParameter("comment")));
                
                //When manual mode, if the suggestion contains one or both of elements accepted by user
                if(!Constants.getHoldingPairs(pair, sug.getPairList()).isEmpty())
                    session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            }
            
        }else if((req.getParameter("manualsub") != null) || (req.getParameter("manualsuper") != null)){
            
            //a pair of class that user select to create relation
            if((req.getParameter("manualclass1") != null) && (req.getParameter("manualclass2") != null)){
                
                String classname1 = req.getParameter("manualclass1").trim();
                String classname2 = req.getParameter("manualclass2").trim();
                testMClass class1 = merge.getOntManager().getontology(Constants.ONTOLOGY_1).getMClass(classname1);
                testMClass class2 = merge.getOntManager().getontology(Constants.ONTOLOGY_2).getMClass(classname2);
                
                
                //check whether this pair of class already have is-a relation in the new ontology
                if(!class1.getAlignSupers().containsValue(class2) ||
                        !class2.getAlignSupers().containsValue(class1)){
                    
                    testPair pair = new testPair(classname1, classname2 );
                    
                    if(req.getParameter("manualsub")!= null)
                       warning = merge.processClassSuggestion(new testHistory(pair, null, Constants.ONTOLOGY_2,
                                Constants.IS_A_CLASS, req.getParameter("comment")));
                    else
                       warning = merge.processClassSuggestion(new testHistory(pair, null, Constants.ONTOLOGY_1,
                                Constants.IS_A_CLASS, req.getParameter("comment")));
                    
                    //When manual mode, if the suggestion contains one or both of elements accepted by user
                    if(!Constants.getHoldingPairs(pair, sug.getPairList()).isEmpty())
                        session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
                }
            }
            
        }else if (req.getParameter("undo") != null) {
            
            merge.undoClassMerge();
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
        }else if (req.getParameter("skip") != null) {
            
            warning = merge.processClassSuggestion(new testHistory((testPair) sug.getPairList().get(0), null,
                                     Constants.ONTOLOGY_NEW, Constants.NO));
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));           
            
        }        
        
        
        //search a class
        if(req.getParameter("search") != null){
            
            testMOntology onto = merge.getOntManager().getontology((new Integer(req.getParameter("searchonto"))).intValue());
            for(Integer i : onto.getClasses().keySet()){
                testMClass tmc = onto.getClasses().get(i);
                if(tmc.getLabel().matches(req.getParameter("searchname").trim())){
                    tmc.highlight();
                    display(tmc);
                }
            }     
        }
        
        
        //move to the manual merging
        if (mode.equals(Constants.MODE_MANUAL)){
            
            session.setAttribute("mode", Constants.MODE_MANUAL);
            
            try{
                out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
                out.println(testFormHandler.createManualClassForm(merge, settings, warning));
                out.println(testPageHandler.createFooter() );
                
            } finally {
                out.close();
            }
        }
        
        
        if (req.getParameter("continue") != null){
            Commons.isFinalized = Constants.STEP_CLASS;
            LockSessionServlet lss = new LockSessionServlet();
            //LockSessionServlet.createXmlTree(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml");
            lss.testgetHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_Concepts_HistoryList.xml", merge, settings);

            merge.finalizeClassSuggestions();
            session.removeAttribute("sug");
            //if(req.getParameter("continue").equalsIgnoreCase("Finalize"))
            
            
            try {
                out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
                out.println(testFormHandler.createStartForm(settings, Constants.STEP_CLASS));
                out.println(testFormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                out.println(testPageHandler.createFooter());
            } finally {
                out.close();
            }
            
        }else{
            
            // new class form
            try {
                
                out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
                out.println(testFormHandler.createClassForm(merge, settings, (testSuggestion)session.getAttribute("sug"), warning));
                out.print(testPageHandler.createFooter());
                
            } finally {
                out.close();
                
            }
        }
    }
}
