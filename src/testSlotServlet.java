/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import se.liu.ida.sambo.MModel.testMProperty;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.testFormHandler;
import se.liu.ida.sambo.ui.web.testPageHandler;
import se.liu.ida.sambo.util.QueryStringHandler;
import se.liu.ida.sambo.util.testHistory;
import se.liu.ida.sambo.util.testPair;
import se.liu.ida.sambo.util.testSuggestion;

/**
 *
 * @author huali50
 */
public class testSlotServlet extends HttpServlet {
    public void doPost(HttpServletRequest req,  HttpServletResponse res)throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
         // Added by Shahab
        String sid = QueryStringHandler.ParseSessionId(req.getQueryString());
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 3;
        testMergerManager merge = (testMergerManager)session.getAttribute(session.getId());
        SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
        testSuggestion sug = (testSuggestion) session.getAttribute("sug");
                //set the mode
        if (req.getParameter("suggestion") != null)
            session.setAttribute("mode", Constants.MODE_SUGGESTION);
        
        if (req.getParameter("manual") != null)
            session.setAttribute("mode", Constants.MODE_MANUAL);
        
        String mode = (String) session.getAttribute("mode");
        
        if (req.getParameter("merge") != null){
            
            merge.processSlotSuggestion(new testHistory(sug.getPair(), req.getParameter("newmergename"),
                    Constants.ONTOLOGY_NEW, Constants.ALIGN_SLOT, req.getParameter("comment") ));
            
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
            //merge a pair of slots manually
        }else if(req.getParameter("manualmerge") != null){
            //a pair of slot that user select to merge
            if(req.getParameter("manualslot1") != null && req.getParameter("manualslot2") != null){
                String manualslot1 = req.getParameter("manualslot1").trim();
                String manualslot2 = req.getParameter("manualslot2").trim();
                testMProperty p1 = merge.getOntManager().getontology(Constants.ONTOLOGY_1).getProperty(manualslot1);
                testMProperty p2 = merge.getOntManager().getontology(Constants.ONTOLOGY_2).getProperty(manualslot2);
                testPair pair = new testPair(manualslot1,manualslot2);
                
                String newname = req.getParameter("newmergename");
                //handle the selected slot merge pair with different type
                if(!p1.getType().equalsIgnoreCase(p2.getType())){
                    
                    //tempotary manual suggestion :)
                    sug = new testSuggestion(pair);
                    sug.reset(true);
                    
                    //check whether get a pair of slots
                }else{
                    
                    merge.processSlotSuggestion(new testHistory(pair, req.getParameter("newmergename"),
                            Constants.ONTOLOGY_NEW, Constants.ALIGN_SLOT, req.getParameter("comment") ));
                    
                    //When manual mode, if the suggestion contains one or both of elements accepted by user
                    if(sug.getPair().contains(pair.getSource()) || sug.getPair().contains(pair.getTarget()))
                        session.setAttribute("sug", new testSuggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
                }
            }
            
            
        }else if(req.getParameter("undo") != null) {
            
            merge.undoSlotMerge();
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
            //the user skip this suggestion
        } else if (req.getParameter("skip") != null) {
            
            merge.processSlotSuggestion(new testHistory(sug.getPair(), "", Constants.ONTOLOGY_NEW, Constants.NO));
            session.setAttribute("sug", new testSuggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
            
        }
        //move to the manual merging
        if (mode.equals(Constants.MODE_MANUAL)){
            try{
                out.println(testPageHandler.createHeader(Constants.STEP_SLOT));
                out.println(testFormHandler.createManualSlotForm(merge, sug, settings,sid));
                out.println(testPageHandler.createFooter());
                
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
                out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
                out.println(testFormHandler.createStartForm(settings, Constants.STEP_CLASS));
                out.println(testFormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                out.println(testPageHandler.createFooter());
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
                //lss.getHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_Relations_HistoryList.xml", merge, settings);
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
                    out.println(testPageHandler.createHeader(Constants.STEP_CLASS));
                    
                    
                    out.println(testFormHandler.createStartForm(settings, Constants.STEP_CLASS));
                    out.println(testFormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
                    out.println(testPageHandler.createFooter());
                } finally {
                    out.close();
                }
                
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(SlotServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }else{
            
            //Print slot suggestion form
            try {
                out.println(testPageHandler.createHeader(Constants.STEP_SLOT));
                out.println(testFormHandler.createSlotForm(merge, (testSuggestion) session.getAttribute("sug"), settings,sid));
                out.println(testPageHandler.createFooter() );
                
            } finally {
                out.close();
            }
        }
        
        
    }
}
