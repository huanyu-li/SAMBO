import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.exceptions.UserSessionsDaoException;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.FormHandler;
import se.liu.ida.sambo.ui.web.PageHandler;
import se.liu.ida.sambo.util.QueryStringHandler;
import se.liu.ida.sambo.util.Suggestion;
import se.liu.ida.sambo.util.testing.AutoValidation;

// The session control the align and merge process.
public class MainServlet extends HttpServlet {
    
    /**
     * Combination method.
     */
    private String combinationMethod = "";
    /**
     * Threshold type (Single/Double).
     */
    private String thresholdType = "";
    
    @Override
    public void doGet(HttpServletRequest req,  HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 2;
        // Add merge object to the session
        MergeManager  merge = (MergeManager)session.
                getAttribute(session.getId());
        SettingsInfo  settings = (SettingsInfo)session.getAttribute("settings");
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        double upperThreshold = 0, lowerThreshold = 0;
        double threshold = 0;
        int step;
        
        /**
         * To stop the matching process at particular concept pair and obtain 
         * partial result.
         */ 
        if (req.getParameter("enable_interrupt") != null && 
                req.getParameter("interuppt_at") != null) {
            AlgoConstants.SET_INTERRUPT_ON = true;
            AlgoConstants.STOP_COMPUTATION_AT = Integer.parseInt(
                    req.getParameter("interuppt_at"));
        } else {
            AlgoConstants.SET_INTERRUPT_ON = false;
        }
        
        if (req.getParameter("combination") != null) {
                        
            combinationMethod = req.getParameter("combination");           
            System.out.println("Use combination Technique " 
                    + combinationMethod);
        }
        if (req.getParameter("threshold_flag") != null) {
                        
            thresholdType = req.getParameter("threshold_flag");           
            System.out.println("Threshold type " + thresholdType);
            if(thresholdType.equalsIgnoreCase("double")) {
                
                upperThreshold = (new Double(req.getParameter
                        ("double_threshold_upper"))).doubleValue();
                lowerThreshold = (new Double(req.getParameter
                        ("double_threshold_lower"))).doubleValue();
                System.out.println("Upper threshold = " + upperThreshold + 
                        " , Lower threshold = " + lowerThreshold);
            }
        }
        
        Commons.SESSION_ID = session.getId();//Added by MZK
        if (req.getParameter("start") != null) {
            step = (new Integer(req.getParameter("step"))).intValue();                                
            //Get the threshold           
            threshold = (new Double(req.getParameter("threshold"))
                    ).doubleValue();
            //If user want to use only mappable Grp
            boolean useMappableGrp = false;
            
            if (req.getParameter("mappableGrp") != null && 
                    req.getParameter("mappableGrp").equalsIgnoreCase("true")) {
                useMappableGrp = true;
            }
            
            getAllParameters(req);            
            //Start to merge slots
            if (step == Constants.STEP_SLOT){
                
                System.out.println("STEP_SLOT");
                merge.getSuggestions(Constants.STEP_SLOT, getWeight(step, 
                        merge, req), threshold, "weighted");
                merge.getSuggestionsXML(Commons.DATA_PATH + Commons.USER_NAME + 
                        "_SuggestionList.xml");
                session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(), merge.suggestionsRemaining()));
                //the default mode is suggestion mode
                session.setAttribute("mode", Constants.MODE_SUGGESTION);
                try {
                    out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                    String sid = QueryStringHandler.ParseSessionId(
                            req.getQueryString());
                    out.println(FormHandler.createSlotForm((Suggestion) 
                            session.getAttribute("sug"), settings, sid));
                    out.println(PageHandler.createFooter());

                } finally {
                    out.close();
                }
            //Start to merge class
            }else if (step == Constants.STEP_CLASS) {
                long t1 = System.currentTimeMillis();                
                Commons.hasProcessStarted = true;
                Commons.isFinalized = 0;
                System.out.println("STEP_CLASS");
                AlgoConstants.STOPMATACHING_PROCESS = false;
                AlgoConstants.ISRECOMMENDATION_PROCESS = false;
                System.out.println("Interrupt is enable--------------- "
                        + ""+AlgoConstants.STOPMATACHING_PROCESS);
                System.out.println("Interrupt at PAIR enable--------------- "
                        + ""+AlgoConstants.SET_INTERRUPT_ON);
                System.out.println("Is Recommendation Process--------------- "
                        + ""+AlgoConstants.ISRECOMMENDATION_PROCESS);
                
                // Normal matching
                if(!useMappableGrp && thresholdType.
                        equalsIgnoreCase("double")) {
                    merge.getSuggestions(getWeight(step, merge, req), 
                            upperThreshold, lowerThreshold, combinationMethod);
                }                    
                else if(!useMappableGrp && thresholdType.
                        equalsIgnoreCase("single")) {
                    merge.getSuggestions(Constants.STEP_CLASS, getWeight(step, 
                            merge, req), threshold, combinationMethod);                    
                }
                // Mappable group matching
                else if(useMappableGrp && thresholdType.
                        equalsIgnoreCase("double")) {
                     merge.getSuggestionsMGBased(getWeight(merge, req), 
                             upperThreshold, lowerThreshold, combinationMethod);
                }                   
                else if(useMappableGrp && thresholdType.
                        equalsIgnoreCase("single")) {
                    merge.getSuggestionsMGBased(getWeight(merge, req), 
                            threshold, combinationMethod);
                }
                
                merge.getSuggestionsXML(Commons.DATA_PATH + Commons.USER_NAME + 
                        "_SuggestionList.xml");        
                session.setAttribute("sug", new Suggestion(merge.
                        getNextSuggestionList(), merge.suggestionsRemaining()));
                session.setAttribute("threshold", threshold);              
                //do not display the hierarchy when first visit "user" model
                session.setAttribute("display1", Boolean.FALSE);
                session.setAttribute("display2", Boolean.FALSE);               
                long t2 = System.currentTimeMillis();      
            
                System.out.println( "Time Taken to display suggestions " + 
                        (t2-t1) + " ms" );
                
//                //Auto Validation
//                int maxValidation = 500000;
//                AutoValidation auto = new AutoValidation();
//                auto.validate(merge, maxValidation);
                
                try {
                    out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                    out.println(FormHandler.createClassForm(settings, 
                            (Suggestion) session.getAttribute("sug"), 
                            Constants.UNIQUE));
                    out.println(PageHandler.createFooter());

                } finally {
                    out.close();
                }
            }
        } else if(req.getParameter("finish-1") != null) {
            // Delete files code goes here
            File userFile=new File(Commons.DATA_PATH + Commons.USER_NAME+".xml");
            userFile.delete();

            File rel_sugListFile = new File(Commons.DATA_PATH + 
                    Commons.USER_NAME + "_SuggestionList.xml");
            rel_sugListFile.delete();

            File rel_hisListFile = new File(Commons.DATA_PATH + 
                    Commons.USER_NAME + "_HistoryList.xml");
            rel_hisListFile.delete();

            File con_tempListFile = new File(Commons.DATA_PATH + 
                    Commons.USER_NAME + "_Concepts_HistoryList.xml");
            con_tempListFile.delete();

            File rel_tempListFile = new File(Commons.DATA_PATH + 
                    Commons.USER_NAME + "_Relations_HistoryList.xml");
            rel_tempListFile.delete();

            File usr_tempFile = new File(Commons.DATA_PATH + 
                    Commons.USER_NAME + "_temp.xml");
            usr_tempFile.delete();

	    // alignment and merged ontology's name
	    String alignfile = settings.getName(Constants.ONTOLOGY_NEW) + "_" + 
                    session.getId() + "_alignment.owl";	    
            String mergefile = settings.getName(Constants.ONTOLOGY_NEW) + "_" + 
                    session.getId() + ".owl";
            
            merge.finalize(Constants.FILEHOME + alignfile, Constants.FILEHOME + 
                    mergefile);
            
            //set the new ontology for browsing
            session.setAttribute("file_link", "ontologies/" + mergefile);         
            session.setAttribute("display", "false");
            
            //"OWL" ************?
	    out.println(PageHandler.createHeader(Constants.STEP_FINISH));
            out.println(PageHandler.createFinished(alignfile, mergefile, 
                    Constants.OWL));
	    out.println(PageHandler.createFooter());
           /*
            try {
                this.RemoveUserSessionFromDb();
            } catch (UserSessionsDaoException ex) {
                Logger.getLogger(MainServlet.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            */
        }
    }
    
    private void getAllParameters(HttpServletRequest req){
                        //======Added by Me--------///
            Commons.usedMatchersList.clear();
            String usedMatcherValues[] = new String[9];
            Commons.usedWeightValuesList.clear();
            // Get Parameter names and their values
            Enumeration paramN=req.getParameterNames();
                int k=0;
                while( paramN.hasMoreElements() ) {
                    String name=(String)paramN.nextElement();
                    String all[]=req.getParameterValues(name);
                    int count=0;
                    for( int i=0; i<all.length; i++ ) {
                        String val = all[i].toString();
                        //out.print( all[i] );
                        if(name.equals("threshold")){
                            Commons.THRESHOLD_VALUE = all[i];
                        }
                        for(int m=0; m<Commons.Matchers_Available.length; m++){
                            if(name.equals(Commons.Matchers_Available[m])){
                                if (count==0){
                                Commons.usedMatchersList.add(name);
                                count = count + 1;
                                }
                                int j = Commons.usedMatchersList.indexOf(name);
                                usedMatcherValues[j] = all[i];
                            }

                        }

                        if(name.startsWith("weight")){
                            String usedWV = all[i].trim();
                            Commons.usedWeightValuesList.add(usedWV);
                        }
                    }
                    if(name.startsWith("weight")){
                     k = k + 1;
                    }
         	}
                
                if(req.getParameter("threshold_flag")!=null && 
                        req.getParameter("threshold_flag").
                        equalsIgnoreCase("double")) {
                    
                    Commons.THRESHOLD_VALUE = req.getParameter
                            ("double_threshold_lower") + " to " + 
                            req.getParameter("double_threshold_upper");                        
                }
    }
    
    private double[] getWeight(int step, MergeManager  merge, 
            HttpServletRequest req) {
          
          //Set the weight for each matcher
            double[] weight = new double[Constants.singleMatchers.length]; 
            
            if (req.getParameter(Constants.singleMatchers
                    [Constants.EditDistance]) != null){
                merge.matching(step, Constants.EditDistance);
                weight[Constants.EditDistance] = Double.parseDouble(
                        req.getParameter("weight" + Constants.EditDistance));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.NGram]) != null){
                merge.matching(step, Constants.NGram);
                weight[Constants.NGram] = Double.parseDouble(
                        req.getParameter("weight" + Constants.NGram));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.WL]) != null){
                merge.matching(step, Constants.WL);
                weight[Constants.WL] = Double.parseDouble(
                        req.getParameter("weight" + Constants.WL));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.WN]) != null){
                merge.matching(step, Constants.WN);
                weight[Constants.WN] = Double.parseDouble(
                        req.getParameter("weight" + Constants.WN));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.Terminology]) != null){
                merge.matching(step, Constants.Terminology);
                weight[Constants.Terminology] = Double.parseDouble(
                        req.getParameter("weight" + Constants.Terminology));
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.WordNet_Plus]) != null){
                merge.matching(step, Constants.WordNet_Plus);
                weight[Constants.WordNet_Plus] = (new Double(
                        req.getParameter("weight" + Constants.WordNet_Plus))).
                        doubleValue();                
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.UMLS]) != null){
                merge.matching(step, Constants.UMLS);
                weight[Constants.UMLS] = (new Double(
                        req.getParameter("weight" + Constants.UMLS))).
                        doubleValue();
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.Bayes]) != null){
                merge.matching(step, Constants.Bayes);
                weight[Constants.Bayes] = (new Double(
                        req.getParameter("weight" + Constants.Bayes))).
                        doubleValue();
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.Hierarchy]) != null){
                merge.matching(step, Constants.Hierarchy);
                /** 
                 * Right now weight for Hierarchy is disabled so this line 
                 * wont work.
                 */ 
//                weight[Constants.Hierarchy] = (new Double(req.getParameter("weight" + Constants.Hierarchy))).doubleValue();
                
                weight[Constants.Hierarchy] = 1.0;            
            }
            return weight;
    }
    
    private double[] getWeight(MergeManager  merge, HttpServletRequest req){
          
          //Set the weight for each matcher
            double[] weight = new double[Constants.singleMatchers.length]; 
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.EditDistance]) != null){
                merge.matching(Constants.EditDistance);
                weight[Constants.EditDistance] = Double.parseDouble(
                        req.getParameter("weight" + Constants.EditDistance));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.NGram]) != null){
                merge.matching(Constants.NGram);
                weight[Constants.NGram] = Double.parseDouble(
                        req.getParameter("weight" + Constants.NGram));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.WL]) != null){
                merge.matching(Constants.WL);
                weight[Constants.WL] = Double.parseDouble(
                        req.getParameter("weight" + Constants.WL));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.WN]) != null){
                merge.matching(Constants.WN);
                weight[Constants.WN] = Double.parseDouble(
                        req.getParameter("weight" + Constants.WN));
            }

            if(req.getParameter(Constants.singleMatchers
                    [Constants.Terminology]) != null){
                merge.matching(Constants.Terminology);
                weight[Constants.Terminology] = Double.parseDouble(
                        req.getParameter("weight" + Constants.Terminology));
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.WordNet_Plus]) != null){
                merge.matching(Constants.WordNet_Plus);
                weight[Constants.WordNet_Plus] = (new Double(
                        req.getParameter("weight" + Constants.WordNet_Plus))).
                        doubleValue();                
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.UMLS]) != null){
                merge.matching(Constants.UMLS);
                weight[Constants.UMLS] = (new Double(
                        req.getParameter("weight" + Constants.UMLS))).
                        doubleValue();
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.Bayes]) != null){
                merge.matching(Constants.Bayes);
                weight[Constants.Bayes] = (new Double(
                        req.getParameter("weight" + Constants.Bayes))).
                        doubleValue();
            }
            
            if(req.getParameter(Constants.singleMatchers
                    [Constants.Hierarchy]) != null){
                merge.matching(Constants.Hierarchy);
                
                
                /**
                 * Right now weight for Hierarchy is disabled so this line 
                 * wont work
                 */ 
                //weight[Constants.Hierarchy] = (new Double(req.getParameter("weight" + Constants.Hierarchy))).doubleValue();
                
                weight[Constants.Hierarchy] = 1.0;
            }
            return weight;
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
    
    void RemoveUserSessionFromDb() throws UserSessionsDaoException
    {
        UserSessionsDao _daoU = getUserSessionsDao();
        if(_daoU.findByPrimaryKey(Commons.USER_SESSION_ID).getId() > 0)
        {
            UserSessionsPk _pk = new UserSessionsPk(_daoU.findByPrimaryKey(
                    Commons.USER_SESSION_ID).getId());
            _daoU.delete(_pk);
        }
       
    }
    
    
    public Connection makeConnection()
    {
        Connection conn=null;
        try {
            conn=ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(MainServlet.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
       
        
        return conn;
    }
}
