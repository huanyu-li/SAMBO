import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.FormHandler;
import se.liu.ida.sambo.ui.web.PageHandler;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.Suggestion;

// Step 3 in the Ontology Merge

// Process an accepted class suggestion
public class ClassServlet extends HttpServlet {
    
    //browse the two source ontologies
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 4;
        // Add merge object to the session
        MergeManager merge = (MergeManager)session.getAttribute(session.getId());
        SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
        
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
        
        
        try{
            
            out.println(PageHandler.createHeader(Constants.STEP_CLASS));
            out.println(FormHandler.createManualClassForm(merge.getOntology(Constants.ONTOLOGY_1),
                    merge.getOntology(Constants.ONTOLOGY_2), settings, Constants.UNIQUE));
            out.println(PageHandler.createFooter() );
            
        } finally {
            out.close();
        }
        
    }
    
    //show/hide all hierarchy
    private void showAll(HttpSession session, MOntology onto, String whichOnto){
        
        boolean display = !((Boolean)session.getAttribute(whichOnto)).booleanValue();
        session.setAttribute(whichOnto,  new Boolean(display));
        
        
        for(Enumeration e = onto.getClasses().elements(); e.hasMoreElements();)
            ((MClass) e.nextElement()).setDisplay(display);
    }
    
    
    //diplay the searched class
    private void display(MClass clazz){
        
        clazz.setDisplay(true);
        for(Enumeration e = clazz.getSuperClasses().elements(); e.hasMoreElements();)
            display((MClass) e.nextElement());
        for(Enumeration e = clazz.getPartOf().elements(); e.hasMoreElements();)
            display((MClass) e.nextElement());
    }
    
    
    //extract the indicated pair of class from the list
    //the default one is the first pair in the list
    private Pair acceptedSug(HttpServletRequest req, HttpSession session, Vector sug){
        
        // Extract the parameter sent from the selection form
        String acceptedPairnum = req.getParameter("classPair");
        if (acceptedPairnum != null) {
            int choice = Integer.parseInt(acceptedPairnum);
            return (Pair)sug.get(choice);
        } else{
            return (Pair)sug.get(0);
        }
    }
    
    
    //handle the actions
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        
        // get objects attached to the session
        MergeManager  merge = (MergeManager) session.getAttribute(session.getId());
        SettingsInfo  settings = (SettingsInfo) session.getAttribute("settings");
        Suggestion sug = ((Suggestion) session.getAttribute("sug"));        
        
        //set the mode
        if (req.getParameter("suggestion") != null)
            session.setAttribute("mode", Constants.MODE_SUGGESTION);
        
        if (req.getParameter("manual") != null)
            session.setAttribute("mode", Constants.MODE_MANUAL);
        
        String mode = (String) session.getAttribute("mode");
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();        
        
        int warning = Constants.UNIQUE;
        
        // merge one pair of class suggestion
        if (req.getParameter("merge") != null) {
            
            Pair acceptedSuggestion = acceptedSug(req, session, sug.getPairList());
            
            warning = merge.processClassSuggestion(new History(acceptedSuggestion, req.getParameter("newmergename"),
                              Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS, req.getParameter("comment")));
            
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
            
        }else if ((req.getParameter("subclass")!=null) || req.getParameter("superclass")!=null){
            
            Pair acceptedSuggestion = acceptedSug(req, session, sug.getPairList());
            
            if(req.getParameter("subclass")!=null)
                warning = merge.processClassSuggestion(new History(acceptedSuggestion, null, Constants.ONTOLOGY_2,
                        Constants.IS_A_CLASS, req.getParameter("comment")));
            else  warning = merge.processClassSuggestion(new History(acceptedSuggestion, null, Constants.ONTOLOGY_1,
                   Constants.IS_A_CLASS, req.getParameter("comment")));
            
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
            //align all the remaining suggestions
        }else if (req.getParameter("remaining") != null) {
            
            merge.mergeRemaining();
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
        }else if(req.getParameter("manualmerge") != null){
            
            //a pair of class that user select to merge
            if((req.getParameter("manualclass1") != null) && (req.getParameter("manualclass2") != null)){
                
                String classname1 = req.getParameter("manualclass1").trim();
                String classname2 = req.getParameter("manualclass2").trim();
                
                Pair pair = new Pair(merge.getOntology(Constants.ONTOLOGY_1).getClass(classname1),
                        merge.getOntology(Constants.ONTOLOGY_2).getClass(classname2));
                
                warning = merge.processClassSuggestion(new History(pair, req.getParameter("newmergename"), Constants.ONTOLOGY_NEW,
                               Constants.ALIGN_CLASS, req.getParameter("comment")));
                
                //When manual mode, if the suggestion contains one or both of elements accepted by user
                if(!Constants.getHoldingPairs(pair, sug.getPairList()).isEmpty())
                    session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            }
            
        }else if((req.getParameter("manualsub") != null) || (req.getParameter("manualsuper") != null)){
            
            //a pair of class that user select to create relation
            if((req.getParameter("manualclass1") != null) && (req.getParameter("manualclass2") != null)){
                
                String classname1 = req.getParameter("manualclass1").trim();
                String classname2 = req.getParameter("manualclass2").trim();
                
                MClass class1 = (MClass) merge.getOntology(Constants.ONTOLOGY_1).getClass(classname1);
                MClass class2 = (MClass) merge.getOntology(Constants.ONTOLOGY_2).getClass(classname2);
                
                
                //check whether this pair of class already have is-a relation in the new ontology
                if(!class1.getAlignSupers().contains(class2) ||
                        !class2.getAlignSupers().contains(class1)){
                    
                    Pair pair = new Pair(class1, class2 );
                    
                    if(req.getParameter("manualsub")!= null)
                       warning = merge.processClassSuggestion(new History(pair, null, Constants.ONTOLOGY_2,
                                Constants.IS_A_CLASS, req.getParameter("comment")));
                    else
                       warning = merge.processClassSuggestion(new History(pair, null, Constants.ONTOLOGY_1,
                                Constants.IS_A_CLASS, req.getParameter("comment")));
                    
                    //When manual mode, if the suggestion contains one or both of elements accepted by user
                    if(!Constants.getHoldingPairs(pair, sug.getPairList()).isEmpty())
                        session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
                }
            }
            
        }else if (req.getParameter("undo") != null) {
            
            merge.undoClassMerge();
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));
            
        }else if (req.getParameter("skip") != null) {
            
            warning = merge.processClassSuggestion(new History((Pair) sug.getPairList().get(0), null,
                                     Constants.ONTOLOGY_NEW, Constants.NO));
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestionList(), merge.suggestionsRemaining()));           
            
        }        
        
        
        //search a class
        if(req.getParameter("search") != null){
            
            MOntology onto = merge.getOntology((new Integer(req.getParameter("searchonto"))).intValue());
            
            for(Enumeration e = onto.getClasses().elements(); e.hasMoreElements();){
                
                MClass clazz = (MClass) e.nextElement();
                if(clazz.getLabel().matches(req.getParameter("searchname").trim())){
                     clazz.highlight();
                     display(clazz);
                }
            }           
        }
        
        
        //move to the manual merging
        if (mode.equals(Constants.MODE_MANUAL)){
            
            session.setAttribute("mode", Constants.MODE_MANUAL);
            
            try{
                out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                out.println(FormHandler.createManualClassForm(merge.getOntology(Constants.ONTOLOGY_1),
                        merge.getOntology(Constants.ONTOLOGY_2), settings, warning));
                out.println(PageHandler.createFooter() );
                
            } finally {
                out.close();
            }
        }
        
        
        if (req.getParameter("continue") != null){
            Commons.isFinalized = Constants.STEP_CLASS;
            LockSessionServlet lss = new LockSessionServlet();
            //LockSessionServlet.createXmlTree(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml");
            lss.getHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_Concepts_HistoryList.xml", merge, settings);

            merge.finalizeClassSuggestions();
            session.removeAttribute("sug");
            //if(req.getParameter("continue").equalsIgnoreCase("Finalize"))
            
            
            try {
                out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                out.println(FormHandler.createStartForm(settings, Constants.STEP_CLASS));
                out.println(FormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                out.println(PageHandler.createFooter());
            } finally {
                out.close();
            }
            
        }else{
            
            // new class form
            try {
                
                out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                out.println(FormHandler.createClassForm( settings, (Suggestion)session.getAttribute("sug"), warning));
                out.print(PageHandler.createFooter());
                
            } finally {
                out.close();
                
            }
        }
    }
}

