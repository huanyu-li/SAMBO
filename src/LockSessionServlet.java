/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import se.liu.ida.sambo.ui.web.*;//Constants;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.w3c.dom.Document;
import java.text.DateFormat;

import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.util.*;



import se.liu.ida.sambo.session.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dao.UsersDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.dto.Users;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.factory.UsersDaoFactory;
/**
 *
 * @author mzk
 */
public class LockSessionServlet extends HttpServlet {

   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, ParserConfigurationException, SAXException {
        HttpSession session = request.getSession(false);

        response.setContentType("text/html;charset=UTF-8");
        
        String message = "Session has been saved successfully.";
        String title = "Confirmation Required";

        if(Commons.currentPosition == 0 || Commons.currentPosition == 1 || Commons.isFinished == true){
            session.invalidate();
            getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
        }else{
            PrintWriter out = response.getWriter();
            try {
                MergeManager merge = (MergeManager)session.getAttribute(session.getId());
                // MOntology :  monto1 , monto2
                Commons.monto1 = merge.getOntology(Constants.ONTOLOGY_1);
                Commons.monto2 = merge.getOntology(Constants.ONTOLOGY_2);
                SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
                Commons.colorOnt1 = settings.getColor(Constants.ONTOLOGY_1);
                Commons.colorOnt2 = settings.getColor(Constants.ONTOLOGY_2);
                Suggestion sug = (Suggestion) session.getAttribute("sug");

                SessionManager sesman = new SessionManager();

                out.println("<HTML><HEAD><TITLE>SessionTimer</TITLE>"
                        
                        +"<script type=\"text/javascript\">function submitform(){document.forms[\"restart\"].submit();}</script>"                        
                        + "</HEAD>");
                if (Commons.STEP_VALUE == Constants.STEP_SLOT){
                    out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                }else if (Commons.STEP_VALUE == Constants.STEP_CLASS){
                    out.println(PageHandler.createHeader(Constants.STEP_CLASS));
                }
                out.println("<BODY>");
                LockSessionServlet lss = new LockSessionServlet();
                if (Commons.STEP_VALUE == Constants.STEP_CLASS){
                    if(Commons.isFinalized == Constants.STEP_SLOT && Commons.hasProcessStarted == false){
                        File file=new File(Commons.DATA_PATH+Commons.USER_NAME+".xml");
                        boolean exists = file.exists();
                        if (exists) {
                            file.delete();
                        }
                        File f = new File(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml");
                        f.renameTo(new File(Commons.DATA_PATH + Commons.USER_NAME + ".xml"));
                        merge.getSuggestionsXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");

                    }
                    else
                    {
                        lss.createXmlTree(Commons.DATA_PATH + Commons.USER_NAME + ".xml");
                        merge.getSuggestionsXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");
                        //sesman.getHistoryXML(Commons.DATA_PATH + "HistoryListVector.xml");
                        lss.getHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_HistoryList.xml", merge, settings);
                        Commons.URL_OWL_1 = settings.getURL(Constants.ONTOLOGY_1);
                        Commons.URL_OWL_2 = settings.getURL(Constants.ONTOLOGY_2);
                       
                    }
                }else if(Commons.STEP_VALUE == Constants.STEP_SLOT){
                     lss.createXmlTree(Commons.DATA_PATH + Commons.USER_NAME + ".xml");
                     merge.getSuggestionsXML(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml");
                     //sesman.getHistoryXML(Commons.DATA_PATH + "HistoryListVector.xml");
                     lss.getHistoryXML(Commons.DATA_PATH + Commons.USER_NAME + "_HistoryList.xml", merge, settings);
                }else{
                    out.println("Error Occured!");
                }

                //Fortmat Date values
                Commons.CTime = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.SHORT).format(new Date(session.getCreationTime()));

                Commons.LATime = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.SHORT).format(new Date(session.getLastAccessedTime()));
                //END Fortmat Date values

                out.println("<b>Session has been locked Successfully!</b><br>");
                out.println("<H2 align=center>Session is locked by : " + Commons.USER_NAME + "</H2>");
                out.println("<Table align=center>");
                out.println("<tr><td> Ontology File 1 </td><td> : " + Commons.OWL_1+ "</td></tr>");
                out.println("<tr><td> Ontology File 2 </td><td> : " + Commons.OWL_2 + "</td></tr>");
                out.println("<tr><td> Step </td><td> : " +Commons.STEP_VALUE+ "</td></tr>");

                for (int i = 0; i < Commons.usedMatchersList.size(); i++) {
                    out.println("<tr><td> Matchers Used </td><td> : " +Commons.usedMatchersList.get(i)+ "</td></tr>");
                    out.println("<tr><td> Matchers Value </td><td> : on </td></tr>");
                    if(!Commons.usedWeightValuesList.get(i).isEmpty()){
                            out.println("<tr><td> Weight </td><td> : " +Commons.usedWeightValuesList.get(i)+ "</td></tr>");
                    }
                }
                out.println("<tr><td> Threshold Value </td><td> : " + Commons.THRESHOLD_VALUE + "</td></tr>");
                out.println("<tr><td> Creation Time </td><td> : " + Commons.CTime+ "</td></tr>");
                out.println("<tr><td>Last Accessed Time </td><td> : " + Commons.LATime+ "</td></tr>");
                out.println("<tr><td> Session Name </td><td> : " + Commons.SESSION_TYPE + "</td></tr>");
                out.println("</Table><BR>");
                out.println("<center><img src=\"img/history.gif\" border=\"0\">");
                out.println("<a href=\"history.jsp\" target=\"_blank\"> History</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick=\"javascript:window.open('pras_alignment.jsp','Recommendations','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=900, height=300');\">Align using PRA's</a>"
                        + "&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick=\"javascript:window.open('SessionRecommendationMthd2.jsp','Recommendations method 2','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=900, height=300');\">Generate recommendation 2</a>"
                        +"&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick=\"javascript:window.open('SessionRecommendationMthd3.jsp','Recommendations method 3','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=900, height=300');\">Generate recommendation 3</a>"
                        +"&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick=\"javascript:window.open('FindMappableConceptPairs.jsp','Mappable concept pairs','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=900, height=300');\">Find mappable group using PRA's</a>"
                        +"&nbsp;&nbsp;|&nbsp;&nbsp;<FORM id = \"restart\" method=POST action=\"Slot?sid=\"> <INPUT name=\"restart_CS\" value=\"restartCS\" type=\"hidden\"></INPUT><a href=\"javascript: submitform()\">Restart computation</a></FORM>"
                        
                        + "</center>");
                out.println("</BODY></HTML>");
                SaveUserSessionToDb();
                out.print(PageHandler.createFooter());
                Commons.remainingSuggestionVector = merge.getRemainingSuggestions();
//                session.invalidate();
//                getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
                out.close();
            } finally {
//                session.invalidate();
//                out.close();
                //javax.swing.JOptionPane.getRootFrame().setAlwaysOnTop(true);
                //JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
//                getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
            }    
        }
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
        try {
            try {
                processRequest(request, response);
            } catch (SAXException ex) {
                Logger.getLogger(LockSessionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LockSessionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            try {
                processRequest(request, response);
            } catch (SAXException ex) {
                Logger.getLogger(LockSessionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LockSessionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public enum Matcher
    {
        EditDistance, NGram, WL, WN, TermBasic, TermWN, UMLSKSearch, Hierarchy, BayesLearning, NOVALUE;

        public static Matcher toMatcher(String str)
        {
            try {
                return valueOf(str);
            }
            catch (Exception ex) {
                return NOVALUE;
            }
        }
    }


    // Save User session to Database
    public String SaveUserSessionToDb() throws ParserConfigurationException, IOException, SAXException{

        int countWeightValue = 0;
        
        try {
            UserSessionsDao _dao = getUserSessionsDao();
            UserSessions _dto = new UserSessions();
            UsersDao _daoU = getUsersDao();
            Users[] _dtoU = _daoU.findWhereEmailEquals(Commons.USER_NAME);
            if(Commons.new_session == 1)
            {
                //if(_dao.findByDynamicWhere("email ='"+Commons.USER_NAME+"' AND ontology1 ='"+Commons.OWL_1+"' AND ontology2 ='"+Commons.OWL_2+"'", null).length == 0)
                {
                        _dto.setUserid(_dtoU[0].getId());
                        _dto.setEmail(Commons.USER_NAME);
                        _dto.setSid(Commons.SESSION_ID);
                        _dto.setOntology1(Commons.OWL_1);
                        _dto.setOntology2(Commons.OWL_2);
                        _dto.setColor1(Commons.colorOnt1);
                        _dto.setColor2(Commons.colorOnt2);
                        for(String usedMatcher : Commons.usedMatchersList)
                        {
                            switch(Matcher.toMatcher(usedMatcher))
                            {
                                case EditDistance:
                                    _dto.setMatchername0(usedMatcher);
                                    _dto.setMatchervalue0("on");
                                    break;
                                case NGram:
                                    _dto.setMatchername1(usedMatcher);
                                    _dto.setMatchervalue1("on");
                                    break;
                                case WL:
                                    _dto.setMatchername2(usedMatcher);
                                    _dto.setMatchervalue2("on");
                                    break;
                                case WN:
                                    _dto.setMatchername3(usedMatcher);
                                    _dto.setMatchervalue3("on");
                                    break;
                                case TermBasic:
                                    _dto.setMatchername4(usedMatcher);
                                    _dto.setMatchervalue4("on");
                                    break;
                                case TermWN:
                                    _dto.setMatchername5(usedMatcher);
                                    _dto.setMatchervalue6("on");
                                    break;
                                case UMLSKSearch:
                                    _dto.setMatchername6(usedMatcher);
                                    _dto.setMatchervalue6("on");
                                    break;
                                case Hierarchy:
                                    _dto.setMatchername7(usedMatcher);
                                    _dto.setMatchervalue7("on");
                                    break;
                            }
                        }

                        for(String usedWeight : Commons.usedWeightValuesList)
                        {
                            switch(countWeightValue)
                            {
                                case 0:
                                    _dto.setWeightvalue0(Double.parseDouble(usedWeight));
                                    break;

                                case 1:
                                    _dto.setWeightvalue1(Double.parseDouble(usedWeight));
                                    break;

                                case 2:
                                    _dto.setWeightvalue2(Double.parseDouble(usedWeight));
                                    break;

                                case 3:
                                    _dto.setWeightvalue3(Double.parseDouble(usedWeight));
                                    break;

                                case 4:
                                    _dto.setWeightvalue4(Double.parseDouble(usedWeight));
                                    break;

                                case 5:
                                    _dto.setWeightvalue5(Double.parseDouble(usedWeight));
                                    break;

                                case 6:
                                    _dto.setWeightvalue6(Double.parseDouble(usedWeight));
                                    break;

                                case 7:
                                    _dto.setWeightvalue7(Double.parseDouble(usedWeight));
                                    break;
                            }
                            countWeightValue++;
                        }
                        
                        
//                        String[] thresholds = Commons.THRESHOLD_VALUE.split("\\;");
                        
                        //if(thresholds.length==1)
                       // _dto.setThresholdvalue(Double.parseDouble(thresholds[0]));
                        
                        
                        _dto.setSessionType(Commons.SESSION_TYPE);
                        _dto.setStep(Short.parseShort(Integer.toString(Commons.STEP_VALUE)));
                        _dto.setIsFinalized(Commons.isFinalized);
                        //_dto.setCreationTime(Commons.CTime);
                        //_dto.setLastAccessedTime(Commons.LATime);
                        _dto.setCreationTime(new Date());
                        _dto.setLastAccessedTime(new Date());
                        _dto.setUserXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + ".xml"));
                        _dto.setUserHistorylistXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_HistoryList.xml"));
                        _dto.setUserSuggestionsListXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml"));
                        _dto.setUserTempXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml"));
                        _dto.setUserRelationsHistorylistXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_Relations_HistoryList.xml"));
                        _dao.insert(_dto);

                        /*UsersDao _daoU = getUsersDao();
                        Users[] _dtoU = _daoU.findWhereEmailEquals(Commons.USER_NAME);

                        UsersUserSessionDao _daoUS = getUsersUserSessionDao();
                        UsersUserSession _dtoUS = new UsersUserSession();
                        _dtoUS.setUserId(_dtoU[0].getId());
                        _dtoUS.setUserSessionId(_dto.getId());
                        _daoUS.insert(_dtoUS);*/
                }
            }
            else
            {
                    UserSessionsPk _dpk = new UserSessionsPk(Commons.S_ID);
                    _dto.setId(Commons.S_ID);
                    _dto.setUserid(_dtoU[0].getId());
                    _dto.setEmail(Commons.USER_NAME);
                    _dto.setSid(Commons.SESSION_ID);
                    _dto.setOntology1(Commons.OWL_1);
                    _dto.setOntology2(Commons.OWL_2);
                    _dto.setColor1(Commons.colorOnt1);
                    _dto.setColor2(Commons.colorOnt2);
                    for(String usedMatcher : Commons.usedMatchersList)
                    {
                        switch(Matcher.toMatcher(usedMatcher))
                        {
                            case EditDistance:
                                _dto.setMatchername0(usedMatcher);
                                _dto.setMatchervalue0("on");
                                break;
                            case NGram:
                                _dto.setMatchername1(usedMatcher);
                                _dto.setMatchervalue1("on");
                                break;
                            case WL:
                                _dto.setMatchername2(usedMatcher);
                                _dto.setMatchervalue2("on");
                                break;
                            case WN:
                                _dto.setMatchername3(usedMatcher);
                                _dto.setMatchervalue3("on");
                                break;
                            case TermBasic:
                                _dto.setMatchername4(usedMatcher);
                                _dto.setMatchervalue4("on");
                                break;
                            case TermWN:
                                _dto.setMatchername5(usedMatcher);
                                _dto.setMatchervalue5("on");
                                break;
                            case UMLSKSearch:
                                _dto.setMatchername6(usedMatcher);
                                _dto.setMatchervalue6("on");
                                break;
                            case Hierarchy:
                                _dto.setMatchername7(usedMatcher);
                                _dto.setMatchervalue7("on");
                                break;
                        }
                    }

                    for(String usedWeight : Commons.usedWeightValuesList)
                    {
                        switch(countWeightValue)
                        {
                            case 0:
                                _dto.setWeightvalue0(Double.parseDouble(usedWeight));
                                break;

                            case 1:
                                _dto.setWeightvalue1(Double.parseDouble(usedWeight));
                                break;

                            case 2:
                                _dto.setWeightvalue2(Double.parseDouble(usedWeight));
                                break;

                            case 3:
                                _dto.setWeightvalue3(Double.parseDouble(usedWeight));
                                break;

                            case 4:
                                _dto.setWeightvalue4(Double.parseDouble(usedWeight));
                                break;

                            case 5:
                                _dto.setWeightvalue5(Double.parseDouble(usedWeight));
                                break;

                            case 6:
                                _dto.setWeightvalue6(Double.parseDouble(usedWeight));
                                break;

                            case 7:
                                _dto.setWeightvalue7(Double.parseDouble(usedWeight));
                                break;
                        }
                        countWeightValue++;
                    }

                   // String[] thresholds = Commons.THRESHOLD_VALUE.split("\\;");
                        
                   //     if(thresholds.length==1)
                  //      _dto.setThresholdvalue(Double.parseDouble(thresholds[0]));
                        
                        
                    _dto.setSessionType(Commons.SESSION_TYPE);
                    _dto.setStep(Short.parseShort(Integer.toString(Commons.STEP_VALUE)));
                    _dto.setIsFinalized(Commons.isFinalized);
                    //_dto.setCreationTime(Commons.CTime);
                    //_dto.setLastAccessedTime(Commons.LATime);
                    _dto.setCreationTime(new Date());
                    _dto.setLastAccessedTime(new Date());
                    _dto.setUserXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + ".xml"));
                    _dto.setUserHistorylistXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_HistoryList.xml"));
                    _dto.setUserSuggestionsListXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml"));
                    _dto.setUserTempXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_temp.xml"));
                    _dto.setUserRelationsHistorylistXml(SaveXmlToBuffer(Commons.DATA_PATH + Commons.USER_NAME + "_Relations_HistoryList.xml"));
                    _dao.update(_dpk, _dto);
            }

            // Get Session ID from database
            Commons.S_ID = _dto.getId();
        }
        catch (Exception _e) {
                _e.printStackTrace();
        }
        return "";
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

     /**
     * Method 'getUsersDao'
     *
     * @return UsersDao
     */

     public static UsersDao getUsersDao()
     {
        return UsersDaoFactory.create();
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
    public void testgetHistoryXML(String filename, testMergerManager merge, SettingsInfo settings){}
    public void getHistoryXML(String filename, MergeManager merge, SettingsInfo settings){
        BufferedWriter bufferedWriter = null;
        //filename = "D:/appl/xml_files/users.xml";
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));

            //Start writing to the output stream
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bufferedWriter.newLine();
            bufferedWriter.write("<ProcessedSuggestions>");
            bufferedWriter.newLine();

            for(Enumeration e = merge.getCurrentHistory().elements(); e.hasMoreElements();){
                getHistoryInfo((History) e.nextElement(), settings, bufferedWriter);
            }

            bufferedWriter.newLine();
            bufferedWriter.write("</ProcessedSuggestions>");

        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            }
        }

    }

    public static void getHistoryInfo(History history, SettingsInfo settings, BufferedWriter bufferedWriter){
        Pair p = (Pair) history.getPair();
        try{
            String skipped = new String();
            bufferedWriter.write("<Suggestion ");
            bufferedWriter.write(" pair=\""+p.toString()+"\"");

            skipped = history.getName();
            if(skipped == null){
                bufferedWriter.write(" name=\"null\"");
            }else{
                bufferedWriter.write(" name=\""+history.getName()+"\"");
            }
            bufferedWriter.write(" num=\""+history.getNum()+"\"");
            bufferedWriter.write(" comment=\""+history.getComment()+"\"");
            bufferedWriter.write(" action=\""+history.getAction()+"\"");
            bufferedWriter.write("/>");
            bufferedWriter.newLine();
        }catch(Exception e){
                System.out.println(e);
        }
    }

    public void createXmlTree(String filename) throws ParserConfigurationException
    {
        BufferedWriter bufferedWriter = null;
        try
        {  
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            //Start writing to the output stream
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bufferedWriter.newLine();
            bufferedWriter.write("<Sessions>");
            bufferedWriter.newLine();
            bufferedWriter.write("<userSession ");
            bufferedWriter.write(" email=\""+Commons.USER_NAME+"\"");
            bufferedWriter.write(" sid=\""+Commons.SESSION_ID+"\"");
            bufferedWriter.write(" sessiontype=\""+Commons.SESSION_TYPE+"\"");
            bufferedWriter.write(" ontology1=\""+Commons.OWL_1+"\"");
            bufferedWriter.write(" ontology2=\""+Commons.OWL_2+"\"");
            bufferedWriter.write(" color1=\""+Commons.colorOnt1+"\"");
            bufferedWriter.write(" color2=\""+Commons.colorOnt2+"\"");
            bufferedWriter.write(" thresholdvalue=\""+Commons.THRESHOLD_VALUE+"\"");
            bufferedWriter.write(" step=\""+Commons.STEP_VALUE+"\"");

            if(Commons.SESSION_TYPE.equals("Computation")){
                bufferedWriter.write(" matchername=\"");
                bufferedWriter.write(Commons.usedMatchersList.get(0)+"\"");
                bufferedWriter.write(" matchervalue=\"");
                bufferedWriter.write("on\"");
                bufferedWriter.write(" weightvalue=\"");
                bufferedWriter.write(Commons.usedWeightValuesList.get(0)+"\"");
            }else{
                for (int i = 0; i < Commons.usedMatchersList.size(); i++) {
                    bufferedWriter.write(" matchername"+i+"=\"");
                    bufferedWriter.write(Commons.usedMatchersList.get(i)+"\"");
                    bufferedWriter.write(" matchervalue"+i+"=\"");
                    bufferedWriter.write("on\"");
                }
                for (int i = 0; i < Commons.usedWeightValuesList.size(); i++) {
                    bufferedWriter.write(" weightvalue"+i+"=\"");
                    if(!Commons.usedWeightValuesList.get(i).trim().isEmpty()){
                        bufferedWriter.write(Commons.usedWeightValuesList.get(i)+"\"");
                    }
                }//End FOR
                Commons.NO = Commons.usedMatchersList.size();
                bufferedWriter.write(" no=\""+Commons.NO+"\"");
            }// End Else
            bufferedWriter.write(" isFinalized=\""+Commons.isFinalized+"\"");
            bufferedWriter.write("/>");
            bufferedWriter.newLine();
            bufferedWriter.write("</Sessions>");
       }
       catch (FileNotFoundException e) {
       }
       catch (IOException e) {
       } 
       finally
       {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            }
            catch (IOException e) {
            }
       }
  }

   /**
     *  Writes the Document object into an XML file.
     */
    public void createXMLFile(Document doc, File fileName)
    {
        try
        {
            org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
            format.setIndenting(false);
            format.setLineSeparator("\n");
            org.apache.xml.serialize.XMLSerializer output = new org.apache.xml.serialize.XMLSerializer(new FileOutputStream(fileName), format);
            output.serialize(doc);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getAllParameters(HttpServletRequest req){
                        //======Added by Me--------///
            Commons.usedMatchersList.clear();
            String usedMatcherValues[] = new String[5];
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
                ///////////////////////////////////////
    }

    private void setRequestedAttributes(int step, HttpServletRequest request){
        switch(step){
            case 3:
                request.setAttribute("TermBasic", "on");
                double weight = Double.valueOf(Commons.usedWeightValuesList.get(0).trim()).doubleValue();
                request.setAttribute("weight4", weight);
                break;
            case 4:
                for(int i=0; i<Commons.usedMatchersList.size(); i++){
                    request.setAttribute(Commons.usedMatchersList.get(i), "on");
                }
                for(int i=0; i<Commons.usedWeightValuesList.size(); i++){
                    request.setAttribute("weight"+i, Commons.usedWeightValuesList.get(i));
                }
                break;
        }
    }
}
