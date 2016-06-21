/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.oreilly.servlet.MultipartRequest;

import java.util.*;
import java.io.*;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.*;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.Merger.*;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.session.*;
import se.liu.ida.sambo.util.*;

/**
 *
 * @author mzk
 */
public class LoadSessionServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        
        
        
        Map params = request.getParameterMap();
        
        Iterator i = params.keySet().iterator();
        
//        while(i.hasNext())
//        {
//            String Key = (String) i.next();
//            
//            String value=((String []) params.get(Key))[0];
//            
//            System.out.println(Key+" "+value);
//        }
        
        
        

        // Added by Shahab
        String sid = QueryStringHandler.ParseSessionId(request.getQueryString());
        GenerateUserSessionFromDb(Integer.parseInt(sid));
        Commons.new_session = 0;
        LoadCommon(Commons.DATA_PATH + Commons.USER_NAME +".xml");
        ////////////////////////////////////////////
        Commons.isLoadedSession = true;
        
            MultipartRequest multi = new MultipartRequest(request, Constants.FILEHOME, 1024*1024*1024);
        
            int type1 = -3;//(new Integer(multi.getParameter("type1"))).intValue(),
            int type2 = -3;//(new Integer(multi.getParameter("type2"))).intValue();
            Commons.S_ID = Integer.valueOf(request.getParameter("sid"));
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            session.setAttribute("settings", acquireResources(multi, out, type1, type2));

        
        //if the source files are set
        if(session.getAttribute("settings")  != null){
            SettingsInfo settings = (SettingsInfo) session.getAttribute("settings");
            
            AlgoConstants.settingsInfo=settings;
            
            MergeManager merge = new MergeManager();
            merge.loadOntologies(settings.getURL(Constants.ONTOLOGY_1), settings.getURL(Constants.ONTOLOGY_2));
            merge.init();
            SessionManager sesman = new SessionManager();

            session.setAttribute(session.getId(), merge);
            settings.setStep(Commons.STEP_VALUE);

            int step = Commons.STEP_VALUE;
            //double threshold = Double.valueOf(Commons.THRESHOLD_VALUE.trim()).doubleValue();

            
            
            /////////////////////////////////////////
            switch(step){
                case Constants.STEP_SLOT:
                    System.out.println("STEP_SLOT");
                    setRequestedAttributes(step, request);

                    sesman.loadSuggestionsFromXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");

                    //double[] usedSlotWeight = getWeight(step, merge, request);

                    if(Commons.isFinalized == Constants.STEP_SLOT){
                        sesman.loadProcessedSuggestionsFromXML(Commons.DATA_PATH +
                                Commons.USER_NAME + "_Relations_HistoryList.xml");

                        //merge.loadSuggestions(Constants.STEP_SLOT, usedSlotWeight, threshold);
                        //merge.loadProcessedSuggestions(Constants.STEP_SLOT, usedSlotWeight, threshold);
                        
                        
                        //Added by Rajaram TO SOLVE PROBLEM IN SESSION LOADING
                        merge.loadSuggestions(Constants.STEP_SLOT);
                        merge.loadProcessedSuggestions(Constants.STEP_SLOT);
                        

                        session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(),
                                merge.suggestionsRemaining()));

                        session.setAttribute("mode", Constants.MODE_SUGGESTION);

                        merge.finalizeSlotSuggestions();
                        session.removeAttribute("sug");

                        session.setAttribute("mode", Constants.MODE_SUGGESTION);
                        settings.setStep(Constants.STEP_CLASS);
                        displayClassStarterForm(out,settings);
                    }else{
                        sesman.loadProcessedSuggestionsFromXML(Commons.DATA_PATH +
                                Commons.USER_NAME + "_HistoryList.xml");

                       // merge.loadSuggestions(Constants.STEP_SLOT, usedSlotWeight, threshold);
                        //merge.loadProcessedSuggestions(Constants.STEP_SLOT, usedSlotWeight, threshold);
                        
                        
                        
                        //Added by Rajaram TO SOLVE PROBLEM IN SESSION LOADING
                        merge.loadSuggestions(Constants.STEP_SLOT);
                        merge.loadProcessedSuggestions(Constants.STEP_SLOT);

                        session.setAttribute("sug", new Suggestion(merge.getNextSuggestion(),
                                merge.suggestionsRemaining()));

                        session.setAttribute("mode", Constants.MODE_SUGGESTION);
                        try {
                            out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                            out.println(FormHandler.createSlotForm((Suggestion) session.getAttribute("sug"), settings,sid ));
                            out.println(PageHandler.createFooter());
                        } finally {
                            out.close();
                        }
                    }
                    break;
                case Constants.STEP_CLASS:
                    System.out.println("STEP_CLASS");
                    setRequestedAttributes(Constants.STEP_SLOT, request);

                    sesman.loadProcessedSuggestionsFromXML(Commons.DATA_PATH +
                                Commons.USER_NAME + "_Relations_HistoryList.xml");
                    merge.loadProcessedSuggestions(Constants.STEP_SLOT, getWeight(Constants.STEP_SLOT, merge, request));
                    session.setAttribute("mode", Constants.MODE_SUGGESTION);

                    merge.reFinalizeSlotSuggestions();
                    session.removeAttribute("sug");

                    if(Commons.isFinalized == Constants.STEP_CLASS){
                        setRequestedAttributes(step, request);
                        session.setAttribute("mode", Constants.MODE_SUGGESTION);
                        // Remaining Suggestions loaded from XML that are in the suggestionslist.xml
                        sesman.loadSuggestionsFromXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");
                     
                        //double[] usedClassWeight = getWeight(step, merge, request);
                        // Processed Suggestion will be placed in the history list
                        sesman.loadProcessedSuggestionsFromXML(Commons.DATA_PATH + Commons.USER_NAME + "_Concepts_HistoryList.xml");
                        // Remaining Suggestions loaded from memory
                       // merge.loadSuggestions(Constants.STEP_CLASS, usedClassWeight, threshold);
                        // Processed Suggestions loaded from memory
                        //merge.loadProcessedSuggestions(Constants.STEP_CLASS, usedClassWeight, threshold);
                        
                        
                        //Added by Rajaram TO SOLVE PROBLEM IN SESSION LOADING
                        merge.loadSuggestions(Constants.STEP_CLASS);
                        merge.loadProcessedSuggestions(Constants.STEP_CLASS);
                        //

                        Vector currentSuggestionVector1 = merge.getNextSuggestionList();
                        int remsug = merge.suggestionsRemaining();
                        session.setAttribute("sug", new Suggestion(currentSuggestionVector1, remsug));
                        session.setAttribute("display1", Boolean.FALSE);
                        session.setAttribute("display2", Boolean.FALSE);

                        merge.finalizeClassSuggestions();
                        session.removeAttribute("sug");
                        displayClassStarterForm(out,settings);
                        
                    }
                    
                    //**null point problem here
                    
                    else{
                        setRequestedAttributes(step, request);
                        session.setAttribute("mode", Constants.MODE_SUGGESTION);
                        sesman.loadSuggestionsFromXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");
                        
                        //double[] usedClassWeight = getWeight(step, merge, request);

                        sesman.loadProcessedSuggestionsFromXML(Commons.DATA_PATH + Commons.USER_NAME + "_HistoryList.xml");

                        //merge.loadSuggestions(Constants.STEP_CLASS, usedClassWeight, threshold);
                       // merge.loadProcessedSuggestions(Constants.STEP_CLASS, usedClassWeight, threshold);
                        
                        
                        //Added by Rajaram TO SOLVE PROBLEM IN SESSION LOADING
                        merge.loadSuggestions(Constants.STEP_CLASS);                        
                        merge.loadProcessedSuggestions(Constants.STEP_CLASS);

                        Vector currentSuggestionVector1 = merge.getNextSuggestionList();
                        int remsug = merge.suggestionsRemaining();
                        session.setAttribute("sug", new Suggestion(currentSuggestionVector1, remsug));
                        session.setAttribute("display1", Boolean.FALSE);
                        session.setAttribute("display2", Boolean.FALSE);

                        Commons.currentPosition = 4;
                        try {
                            out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                            out.println(FormHandler.createClassForm(settings, (Suggestion) session.getAttribute("sug"), Constants.UNIQUE));
                            
                            out.println(PageHandler.createFooter());
                        } finally {
                            out.close();
                        }
                    }
                      break;
            }

            ////////////////////////////////////////

            //End
        }
        
       

    }


public void LoadCommon(String filePath){
    String str = new String();
    String mName = new String();
    String mValue = new String();
    String isFinalized = new String();
    String usedMatchers[] = new String[Commons.Matchers_Available.length];
    String strWeightList[] = new String[5];

    Commons.usedMatchersList.clear();
    Commons.usedWeightValuesList.clear();

    String ontology_component = new String();

    try{
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db =dbf.newDocumentBuilder();
        Document doc=db.parse(filePath);

        NodeList nodeList = doc.getElementsByTagName("userSession");

        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Element user = (Element) nodeList.item(i);
            if (user.getAttribute("email").equals(Commons.USER_NAME)){
                Commons.SESSION_TYPE = user.getAttribute("sessiontype");
                if (Commons.SESSION_TYPE.equals("Computation"))
                    ontology_component = "Relations";
                else if (Commons.SESSION_TYPE.equals("Computation"))
                    ontology_component = "Concepts";
                Commons.OWL_1 = user.getAttribute("ontology1");
                Commons.OWL_2 = user.getAttribute("ontology2");
                Commons.colorOnt1 = user.getAttribute("color1");
                Commons.colorOnt2 = user.getAttribute("color2");
                Commons.THRESHOLD_VALUE = user.getAttribute("thresholdvalue");
                isFinalized = user.getAttribute("isFinalized");
                Commons.isFinalized = Integer.parseInt(isFinalized);

                //Define loop to get values for both sessionTypes here
                if(Commons.SESSION_TYPE.equals("Validation")){
                    String strNO = user.getAttribute("no");
                    Commons.NO = Integer.parseInt(strNO);

                    for(int j=0; j<Commons.NO; j++){
                        int count = 0;
                        mName= user.getAttribute("matchername"+j);
                        mValue = user.getAttribute("matchervalue"+j);
                        for(int m=0; m<Commons.Matchers_Available.length; m++){
                            if(mName.equals(Commons.Matchers_Available[m])){
                                if (count==0){
                                    Commons.usedMatchersList.add(mName);
                                    usedMatchers[m] = user.getAttribute("matchername"+j);
                                    count = count + 1;
                                }
                            }
                        }
                    }
                    for(int j=0; j<strWeightList.length; j++){
                        String strWV = user.getAttribute("weightvalue"+j).toString();
                        if(!strWV.trim().isEmpty()){
                            Commons.usedWeightValuesList.add(strWV);
                            strWeightList[j] = user.getAttribute("weightvalue"+j);
                        }
                    }
                }else{
                    mName= user.getAttribute("matchername");
                    usedMatchers[0] = mName.trim();
                    Commons.usedMatchersList.add(mName.trim());
                    mValue = user.getAttribute("matchervalue");
                    String strWV = user.getAttribute("weightvalue").toString();
                        if(!strWV.trim().isEmpty()){
                            Commons.usedWeightValuesList.add(strWV);
                            strWeightList[0] = user.getAttribute("weightvalue");
                        }
                }
                String strStep = user.getAttribute("step");
                Commons.STEP_VALUE = Integer.parseInt(strStep);
                Commons.SESSION_ID = user.getAttribute("sid");

            } //end if
        }//end for
    }
    catch(Exception _ex)
    {
        _ex.printStackTrace();
    }
}

public void GenerateUserSessionFromDb(int id){
    UserSessionsDao _dao = getUserSessionsDao();
    try{
        UserSessions[] _oUserSession = _dao.findWhereIdEquals(id);
        GenerateXmlFileFromDb(Commons.DATA_PATH + Commons.USER_NAME +".xml",_oUserSession[0].getUserXml());
        GenerateXmlFileFromDb(Commons.DATA_PATH + Commons.USER_NAME +"_HistoryList.xml",_oUserSession[0].getUserHistorylistXml());
        GenerateXmlFileFromDb(Commons.DATA_PATH + Commons.USER_NAME +"_SuggestionList.xml",_oUserSession[0].getUserSuggestionsListXml());
        GenerateXmlFileFromDb(Commons.DATA_PATH + Commons.USER_NAME +"_Relations_HistoryList.xml",_oUserSession[0].getUserRelationsHistorylistXml());
        GenerateXmlFileFromDb(Commons.DATA_PATH + Commons.USER_NAME +"_temp.xml",_oUserSession[0].getUserTempXml());
    }
    catch(Exception ex){
        ex.printStackTrace();
    }
    finally{
        _dao = null;
    }
}

public static UserSessionsDao getUserSessionsDao(){
    return UserSessionsDaoFactory.create();
}

public void GenerateXmlFileFromDb(String filename,String xml){
    BufferedWriter bufferedWriter = null;
    try {
        //Construct the BufferedWriter object
        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        //Start writing to the output stream
        bufferedWriter.write((xml == null? "":xml));
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    } finally {
        //Close the BufferedWriter
        try {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

private void setRequestedAttributes(int step, HttpServletRequest request){
    switch(step){
        case 3:
            request.setAttribute("TermBasic", "on");
            double weight = Double.valueOf(Commons.usedWeightValuesList.get(0).trim()).doubleValue();
            request.setAttribute("weight4", weight);
            break;
        case 4:
            request.setAttribute("TermBasic", null);
            for(int i=0; i<Commons.usedMatchersList.size(); i++){
                request.setAttribute(Commons.usedMatchersList.get(i), "on");
            }
            for(int i=0; i<Commons.usedWeightValuesList.size(); i++){
                request.setAttribute("weight"+i, Commons.usedWeightValuesList.get(i));
            }
            break;
    }
}

private void displayClassStarterForm(PrintWriter out, SettingsInfo settings){
    try {
        out.println(PageHandler.createHeader(Constants.STEP_CLASS));
        out.println(FormHandler.createStartForm(settings, Constants.STEP_CLASS));
        out.println(FormHandler.createRecommendationForm(settings, Constants.STEP_CLASS));
        out.println(PageHandler.createFooter());
    } finally {
        out.close();
    }
}
    // Aquire the files, but try to avoid overwriting files with the same name
    private synchronized SettingsInfo acquireResources(MultipartRequest multi, PrintWriter out,
            int type1, int type2) throws IOException{


//        Testing Input Values
//        String nf = new String();
//        Enumeration en = multi.getParameterNames();
//        while(en.hasMoreElements()){
//            String str = (String)en.nextElement();
//
//            if (str.equals("FILE1")){
//                String all[]=multi.getParameterValues(str);
//                for(int i=0; i<all.length;i++){
//                    nf = all[i];
//                    System.out.println(nf);
//                }
//            }else if (str.equals("FILE2")){
//                String all[]=multi.getParameterValues(str);
//                for(int i=0; i<all.length;i++){
//                    nf = all[i];
//                    System.out.println(nf);
//                }
//            }
//        }

        URL url1, url2;
        try{
            // configure the settings of the session
            url1 = getURL(multi, type1, Commons.OWL_1+".owl");
            url2 = getURL(multi, type2, Commons.OWL_2+".owl");

            //MalformedURLException, or NullPointException
        }catch(Exception e){
            //if malformed url exception occur, allow user to restart loading ontologies
            printUpload(out, type1, type2);
            return null;
        }

        SettingsInfo settings = new SettingsInfo();
        settings.setURLs(url1, url2 );
        settings.setNames(filename(url1), filename(url2), multi.getParameter("name3"));
        //settings.setColors(multi.getParameter("color1"), multi.getParameter("color2"));
        settings.setColors(Commons.colorOnt1, Commons.colorOnt2);

        return settings;
    }


    //print upload page
    private void printUpload(PrintWriter out, int type1, int type2){

        try {
            out.print(PageHandler.createHeader(Constants.STEP_UPLOAD));
            out.print(FormHandler.createFileUploadForm(type1, type2));
            out.print(PageHandler.createFooter());

        } finally {
            out.close();
        }
    }


    //get source ontology's URL
    private URL getURL(MultipartRequest multi, int type, String file) throws Exception{

        if (type==Constants.URL)
            return new URL(file);

        else if (type==Constants.FILE)
            return multi.getFile(file).toURL();

        //type is Constants.ON_SERVER
        return (new File( Constants.FILEHOME + Constants.languages[Constants.OWL]
                + File.separator + file)).toURL();

    }


    //get filename, ex.
    // file:///.../behavior_GO.owl  --> behavior_GO
    private String filename(URL url){

        return filename((new File(url.getFile())).getName());
    }


    private String filename(String name){

        if(name.lastIndexOf('.') != -1)
            return name.substring(0, name.lastIndexOf('.'));

        return name;
    }

    private double[] getWeight(int step, MergeManager  merge, HttpServletRequest req){

            double[] weight = new double[Constants.singleMatchers.length];
            
            
            //for(int i=0;i<9;i++)System.out.println("matcher"+i+" weight "+req.getAttribute("weight").toString());
            

            if(req.getAttribute(Constants.singleMatchers[Constants.EditDistance]) != null){
                merge.matching(step, Constants.EditDistance);
                weight[Constants.EditDistance] = Double.parseDouble(req.getAttribute("weight" + Constants.EditDistance).toString());
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.NGram]) != null){
                merge.matching(step, Constants.NGram);
                weight[Constants.NGram] = Double.parseDouble(req.getAttribute("weight" + Constants.NGram).toString());
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.WL]) != null){
                merge.matching(step, Constants.WL);
                weight[Constants.WL] = Double.parseDouble(req.getAttribute("weight" + Constants.WL).toString());
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.WN]) != null){
                merge.matching(step, Constants.WN);
                weight[Constants.WN] = Double.parseDouble(req.getAttribute("weight" + Constants.WN).toString());
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.Terminology]) != null)
            {
                merge.matching(step, Constants.Terminology);
                weight[Constants.Terminology] = Double.parseDouble(req.getAttribute("weight" + Constants.Terminology).toString());
            }

            //** null error here
            if(req.getAttribute(Constants.singleMatchers[Constants.WordNet_Plus]) != null){
                merge.matching(step, Constants.WordNet_Plus);
                
                //weight[Constants.WordNet_Plus] = (new Double(req.getAttribute("weight" + Constants.WordNet_Plus).toString())).doubleValue();
           
                
                weight[Constants.WordNet_Plus] = Double.parseDouble(req.getAttribute("weight" + Constants.WordNet_Plus).toString());
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.UMLS]) != null){
                merge.matching(step, Constants.UMLS);
                weight[Constants.UMLS] = (new Double(req.getAttribute("weight" + Constants.UMLS).toString())).doubleValue();
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.Bayes]) != null){
                merge.matching(step, Constants.Bayes);
                weight[Constants.Bayes] = (new Double(req.getAttribute("weight" + Constants.Bayes).toString())).doubleValue();
            }

            if(req.getAttribute(Constants.singleMatchers[Constants.Hierarchy]) != null){
                merge.matching(step, Constants.Hierarchy);
                weight[Constants.Hierarchy] = (new Double(req.getAttribute("weight" + Constants.Hierarchy).toString())).doubleValue();
            }
            
            return weight;
    }




    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
