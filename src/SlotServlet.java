import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.ui.web.*;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Suggestion;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.exceptions.UserSessionsDaoException;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;

import se.liu.ida.sambo.session.*;
import se.liu.ida.sambo.util.QueryStringHandler;

// Step 2 in the Ontology Merge
// Take the accepted slot suggestions and add them to the new ontology
// If the submission was to finalize the suggestions, move on
// to the class merge, otherwise write a new slot suggestion
// form with the non-merged suggestions
public class SlotServlet  extends HttpServlet{
    
    
    public void doPost(HttpServletRequest req,  HttpServletResponse res)
    throws ServletException, IOException {
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
         // Added by Shahab
        String sid = QueryStringHandler.ParseSessionId(req.getQueryString());
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 3;
        // Add merge object to the session
        MergeManager   merge = (MergeManager)session.getAttribute(session.getId());
        SettingsInfo  settings = (SettingsInfo)session.getAttribute("settings");
        Suggestion sug = (Suggestion) session.getAttribute("sug");
        
        
        //set the mode
        if (req.getParameter("suggestion") != null)
            session.setAttribute("mode", Constants.MODE_SUGGESTION);
        
        if (req.getParameter("manual") != null)
            session.setAttribute("mode", Constants.MODE_MANUAL);
        
        String mode = (String) session.getAttribute("mode");
        
        // merge the slot merge suggestion
        if (req.getParameter("merge") != null){
            
            merge.processSlotSuggestion(new History(sug.getPair(), req.getParameter("newmergename"),
                    Constants.ONTOLOGY_NEW, Constants.ALIGN_SLOT, req.getParameter("comment") ));
            
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
            //merge a pair of slots manually
        }else if (req.getParameter("manualmerge") != null){
            
            //a pair of slot that user select to merge
            if(req.getParameter("manualslot1") != null && req.getParameter("manualslot2") != null){
                
                String manualslot1 = req.getParameter("manualslot1").trim();
                String manualslot2 = req.getParameter("manualslot2").trim();
                
                Pair pair = new Pair( merge.getOntology(Constants.ONTOLOGY_1).getProperty(manualslot1),
                        merge.getOntology(Constants.ONTOLOGY_2).getProperty(manualslot2));
                
                String newname = req.getParameter("newmergename");
                
                //handle the selected slot merge pair with different type
                if(!((MProperty) pair.getObject1()).getType().equalsIgnoreCase(((MProperty) pair.getObject2()).getType())){
                    
                    //tempotary manual suggestion :)
                    sug = new Suggestion(pair);
                    sug.reset(true);
                    
                    //check whether get a pair of slots
                }else{
                    
                    merge.processSlotSuggestion(new History(pair, req.getParameter("newmergename"),
                            Constants.ONTOLOGY_NEW, Constants.ALIGN_SLOT, req.getParameter("comment") ));
                    
                    //When manual mode, if the suggestion contains one or both of elements accepted by user
                    if(sug.getPair().contains(pair.getObject1()) || sug.getPair().contains(pair.getObject2()))
                        session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
                }
            }
            
            //the user undo the previous action
        }else if (req.getParameter("undo") != null) {
            
            merge.undoSlotMerge();
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
            //the user skip this suggestion
        } else if (req.getParameter("skip") != null) {
            
            merge.processSlotSuggestion(new History(sug.getPair(), "", Constants.ONTOLOGY_NEW, Constants.NO));
            session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
        }
        
        
        //move to the manual merging
        if (mode.equals(Constants.MODE_MANUAL)){
            try{
                out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                out.println(FormHandler.createManualSlotForm(merge.getOntology(Constants.ONTOLOGY_1),
                        merge.getOntology(Constants.ONTOLOGY_2), sug, settings,sid));
                out.println(PageHandler.createFooter());
                
            } finally {
                out.close();
            }
        }
        
        
        
        
        
        
        
        
        
        
        //To restart CS at session lock
        if (req.getParameter("restart_CS") != null) {
            
                //reset mode to "suggestion" mode
                session.setAttribute("mode", Constants.MODE_SUGGESTION);
                settings.setStep(Constants.STEP_CLASS);
                try {
                    out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                    out.println(FormHandler.createStartForm(settings, Constants.STEP_CLASS));
                    out.println(FormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                    out.println(PageHandler.createFooter());
                } finally {
                    out.close();
                }
        }
        
        
        
        
        
        
        
        
        
        
        //Finalize slots merge and move on to the class merge
        if (req.getParameter("finalize") != null) {
            try {
                Commons.isFinalized = Constants.STEP_SLOT;
                LockSessionServlet lss = new LockSessionServlet();
                lss.createXmlTree(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml");
                lss.getHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_Relations_HistoryList.xml", merge, settings);
                //if(sid != "")
                  //  UpdateUserSessionToDb(Integer.parseInt(sid));
                merge.finalizeSlotSuggestions();
                // We're now done with slot merging, remove the slot suggestions,
                session.removeAttribute("sug");
                //RemoveUserSessionFromDb();
                //reset mode to "suggestion" mode
                session.setAttribute("mode", Constants.MODE_SUGGESTION);
                settings.setStep(Constants.STEP_CLASS);
                try {
                    out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                    
                    
                    out.println(FormHandler.createStartForm(settings, Constants.STEP_CLASS));
                    out.println(FormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                    out.println(PageHandler.createFooter());
                } finally {
                    out.close();
                }
                
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(SlotServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }else{
            
            //Print slot suggestion form
            try {
                out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                out.println(FormHandler.createSlotForm((Suggestion) session.getAttribute("sug"), settings,sid));
                out.println(PageHandler.createFooter() );
                
            } finally {
                out.close();
            }
        }
        
    }

    public void UpdateUserSessionToDb(int sid){
        try
        {
            UserSessionsDao _dao = getUserSessionsDao();
            UserSessions _dto = new UserSessions();
            UserSessionsPk _dpk = new UserSessionsPk(sid);
            _dto.setUserTempXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml"));
            _dto.setUserRelationsHistorylistXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_Relations_HistoryList.xml"));
            _dao.update(_dpk, _dto);
        }
        catch(Exception _ex)
        {
            _ex.printStackTrace();
        }
    }

    // Save Xml files in Buffer
    public String SaveXmlToBuffer(String filename) throws ParserConfigurationException, IOException, SAXException
    {
        String strLine = new String();
        String strXml = new String();
        try{
            Reader in = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(in);
            while((strLine = br.readLine()) != null){
                strXml += strLine;
            }
            br.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return strXml;
    }

    /**
     * Method 'getUserSessionsDao'
     *
     * @return UserSessionsDao
     */
    public static UserSessionsDao getUserSessionsDao()
    {
            return UserSessionsDaoFactory.create();
    }

    

}
