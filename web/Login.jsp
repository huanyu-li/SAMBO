<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page import="se.liu.ida.sambo.segPairSelAlgs.SubG"%>
<%@page import="se.liu.ida.sambo.factory.UserSessionsDaoFactory"%>
<%@page import="se.liu.ida.sambo.dao.UserSessionsDao"%>
<%@page import="se.liu.ida.sambo.dto.UserSessions"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page language="java" import="javax.mail.*, javax.mail.internet.*, se.liu.ida.sambo.util.XMLFileHandler, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.ui.web.*, org.w3c.dom.*, java.io.*"%>
<%@page import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.session.*"%>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="se.liu.ida.sambo.ui.web.Constants" %>

<%@ page isThreadSafe="false" %>

<%-- 
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<%
        out.println(PageHandler.createHeader(Constants.WELCOME));
%>

<%
//Log In
        if (request.getParameter("login") != null) {

            if (isValidUser(request.getParameter("email").trim(), request.getParameter("pwd").trim(), request)) //Go to Align and Merge
            {
                //Commons.DATA_PATH = getDataPath(request.getParameter("email").trim(), request);
                Commons.DATA_PATH = Constants.SESSIONS;
                Commons.SEGMENT = Constants.SEGMENT;
                Commons.USER_NAME = request.getParameter("email").trim();
                //out.println("Path :" +Commons.DATA_PATH+Commons.USER_NAME+".xml");
                boolean exist = checkSavedSession();
                if (!exist) {
                    out.println("No previous session found!");
                    Commons.new_session = 1;
                    out.println(FormHandler.createFileUploadForm(Constants.UNK, Constants.UNK));
                }else{
                    //out.println(Commons.DATA_PATH+Commons.USER_NAME+".xml");
                    //out.println(LoadAllSessions(Commons.USER_NAME,Commons.DATA_PATH+Commons.USER_NAME+".xml", request)); // Added by Shahab
                    
                    
                    out.println(LoadSession(Commons.USER_NAME,Commons.DATA_PATH+Commons.USER_NAME+".xml", request));
                }

                //out.println(FormHandler.createFileUploadForm(Constants.UNK, Constants.UNK));
            } else {
                out.println(loginForm("Invalid email address or/and password"));
                request.removeAttribute("email");
            }


//Register
        } else if (request.getParameter("register") != null) {

            String reg = register(request.getParameter("email").trim(), request);
            if (reg == null) {
                out.println(loginForm("Thanks for your registration. <br><br> Please check your email for password."));
                request.removeAttribute("email");
            } else {
                out.println(loginForm("Registartion failed. " + reg));
                request.removeAttribute("email");
            }


//In a session
        } /*else

            if (request.getSession(true).getAttribute("email") != null) {

            //Go to Align and Merge
            out.println(FormHandler.createFileUploadForm(Constants.UNK, Constants.UNK));

        } */ else {
            out.println(loginForm("Please login || register first <br><br> For registering, please input an email address at User Account, and a password will be sent."));

        }
%>


<%
        out.println(PageHandler.createFooter());
%>

<%!
    synchronized boolean checkSavedSession(){

        //File file = new File(Commons.DATA_PATH+Commons.USER_NAME+".xml");
        UserSessionsDao _dao = UserSessionsDaoFactory.create();
        boolean IsSessionSaved = true;
        try
        {
            if(_dao.findWhereEmailEquals(Commons.USER_NAME).length == 0)
                IsSessionSaved = false;
        }
        catch(Exception _ex)
        {
            _ex.printStackTrace();
        }
        return IsSessionSaved;
   }
%>

<%!    synchronized String getDataPath(String email, HttpServletRequest req) {
        String datapath = new String();
        Document doc = XMLFileHandler.readXMLFile(getServletContext().getRealPath("/xml/users.xml"));

        //Generate the NodeList;
        NodeList nodeList = doc.getElementsByTagName("user");

        //Search for User's Record
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {

            Element user = (Element) nodeList.item(i);

            if (email.equals(user.getAttribute("email"))) {
                datapath = user.getAttribute("datapath");
            } //end if
        }//end for

        return datapath;
    }

%>


<%!
    synchronized boolean isValidEmailAddr(String email) {

        if (email.length() <= 0) {
            return false;
        }

        String[] tokens = email.split("@");

        return tokens.length == 2 && tokens[0].length() > 0 && tokens[1].length() > 0;
    }

%>

<%!    synchronized boolean isValidUser(String email, String pwd, HttpServletRequest req) {

        //if (!isValidEmailAddr(email) || pwd.length() < 0) {
        if (pwd.length() < 0) {
            return false;
        }

        Document doc = XMLFileHandler.readXMLFile(getServletContext().getRealPath("/xml/users.xml"));

        //Generate the NodeList;
        NodeList nodeList = doc.getElementsByTagName("user");

        //Search for User's Record
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {

            Element user = (Element) nodeList.item(i);

            if (email.equals(user.getAttribute("email")) && pwd.equals(user.getAttribute("pwd"))) {

                //increment the times of the user visiting SAMBO
                user.setAttribute("times", Integer.toString(Integer.parseInt(user.getAttribute("times")) + 1));
                XMLFileHandler.writeXMLFile(doc, getServletContext().getRealPath("/xml/users.xml"));

                req.getSession(true).setAttribute("email", email);

                return true;
            } //end if
        }//end for

        return false;
    }

%>

<%!    synchronized String register(String email, HttpServletRequest req) {

        if (!isValidEmailAddr(email)) {
            return "Invalid Email Address";
        }

        Document doc = XMLFileHandler.readXMLFile(getServletContext().getRealPath("/xml/users.xml"));
        //Generate the NodeList;
        NodeList nodeList = doc.getElementsByTagName("user");

        Element user = null;
        //check whether the user exist.
        for (int i = nodeList.getLength() - 1; i >= 0; i--) {
            Element node = (Element) nodeList.item(i);

            if (email.equals(node.getAttribute("email"))) {
                user = node;
                break;
            } //end if
        }//end for

        //if the user does not exist, create one.
        if (user == null) {
            user = doc.createElement("user");
            user.setAttribute("email", email);
            user.setAttribute("times", Integer.toString(0));
            user.setAttribute("addr", req.getRemoteAddr());
        }

        //generate a pwd for the user
        java.util.Random random = new java.util.Random();
        String pwd = String.valueOf(random.nextInt(10) * 1000 + random.nextInt(10) * 100 + random.nextInt(10) * 10 + random.nextInt(10));


        // Send password to the user
        java.util.Properties props = new java.util.Properties();

        props.put("mail.smtp.host", Constants.host);
        Session mailsession = Session.getDefaultInstance(props, null);
        mailsession.setDebug(false);

        try {
            // Instantiatee a message
            MimeMessage msg = new MimeMessage(mailsession);

            //Set message attributes
            msg.setFrom(new InternetAddress(Constants.mailAddr));
            InternetAddress[] address = {new InternetAddress(email)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Welcome to SAMBO ");
            msg.setSentDate(new java.util.Date());

            // Set message content
            msg.setText("Thanks for your registration. \n" + "Your PASSWORD is " + pwd + ".");

            //Send the message
            Transport.send(msg, address);

        } catch (MessagingException mex) {
            return mex.getMessage();
        }

        user.setAttribute("pwd", pwd);

        //anyway append the user to the doc.
        doc.getDocumentElement().appendChild(user);
        XMLFileHandler.writeXMLFile(doc, getServletContext().getRealPath("/xml/users.xml"));
        System.out.println(getServletContext().getRealPath("/xml/users.xml"));
        return null;
    }

%>


<%!    /**
     * Creates login form
     * @exception java.io.IOException if the menuitems.inc cannot be found
     */
    synchronized String loginForm(String str) {

        return "<form method=\"POST\" action=\"Login.jsp\" name=\"login\">" + "<center>" + "<table class='border_table' width=\"80%\">" + "<tr><td></td> <td class=\"blue\"><br>" + str + "<br><br></td>" + "<tr>" + "<td td width=\"20%\" align=\"right\">User Account</td>" + "<td align=\"left\"><input type=\"text\" name=\"email\"></td>" + "</tr>" + "<tr>" + "<td align=\"right\">Password</td>" + "<td align=\"left\"><input type=\"password\" name=\"pwd\">" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "<input type=\"submit\" value=\"login\" name=\"login\" >" + "&nbsp;&nbsp;<input type=\"submit\" value=\"register\" name=\"register\" >" + "</td>" + "</tr></table></center></form>";

    }

%>

<%!
    synchronized String LoadSession(String email, String filePath, HttpServletRequest req){
        /*String str = new String();
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


        str = "<FORM METHOD=\"POST\" ACTION=\"LoadSession\" ENCTYPE=\"multipart/form-data\">"
                + "<Table align=center>"
                + "<tr><td><b>System has found your</b></td><td><b>stored session!</b><br></td></tr>"
                +   "<tr>"
                +       "<td>User Name :</td><td><input type=\"text\" value="+Commons.USER_NAME+" readonly=\"readonly\" name=\"uName\"</td>"
                +   "</tr>"
                +   "<tr>"
                +       "<td>Ontology Component :</td><td><input type=\"text\" value="+ontology_component+" readonly=\"readonly\" name=\"stype\" </td>"
                +   "</tr>"
                +   "<tr>"
                +       "<td>Ontology 1 :</td><td><input type=\"text\" value="+Commons.OWL_1+" readonly=\"readonly\" name=\"ont1\"</td>"
                +   "</tr>"
                +   "<tr>"
                +       "<td>Ontology 2 :</td><td><input type=\"text\" value="+Commons.OWL_2+" readonly=\"readonly\" name=\"ont2\"</td>"
                +   "</tr>";
                
                for(int i=0; i<Commons.usedMatchersList.size(); i++){
                str = str + "<tr>"
                +       "<td>Matcher"+i+" Name :</td><td><input type=\"text\" value="+usedMatchers[i].toString()+" readonly=\"readonly\" name=\"matchername"+i+"\"</td>"
                +   "</tr>"
                +   "<tr>"
                +       "<td>Matcher"+i+" Value :</td><td><input type=\"text\" value="+mValue+" readonly=\"readonly\" name=\"matchervalue"+i+"\"</td>"
                +   "</tr>";
                }
                for(int i=0; i<Commons.usedWeightValuesList.size(); i++){
                str = str + "<tr>"
                +       "<td>Weight "+i+" Value :</td><td><input type=\"text\" value="+strWeightList[i]+" readonly=\"readonly\" name=\"weightvalue"+i+"\"</td>"
                +   "</tr>";
                }
                
                str = str + "<tr>"
                +       "<td>Threshold Value :</td><td><input type=\"text\" value="+Commons.THRESHOLD_VALUE+" readonly=\"readonly\" name=\"thresholdValue\"</td>"
                +   "</tr>"
                +   "<tr>"
                +       "<td>Step :</td><td><input type=\"text\" value="+Commons.STEP_VALUE+" readonly=\"readonly\" name=\"stepValue\"</td>"
                +   "</tr>"
                +    "<tr>"
                +         "<td><INPUT TYPE=SUBMIT value=\"Load Session\"></FORM></td>"                
                +         "<td><FORM METHOD=\"POST\" ACTION=\"startSession.jsp\">"
                +           "<INPUT TYPE=HIDDEN value="+Commons.USER_NAME+" name=\"uname\">"
                +           "<INPUT TYPE=SUBMIT value=\"Start New Session\"></td>"
                +    "</tr>"
                + "</Table>"
                +" </FORM>>";

        //str = "Done";

        }catch (Exception e){
            System.out.println(e);
        }
        return str;*/
        int count = 0;
        String str = new String();
        String strTable = new String();;

        try {
                UserSessionsDao _dao = getUserSessionsDao();
                UserSessions _result[] = _dao.findAll();
                
                
                strTable= "<div style='width:178%'>"
                       + "<FORM METHOD=\"POST\" ACTION=\"startSession.jsp\">"
                           +"<INPUT TYPE=HIDDEN value="+Commons.USER_NAME+" name=\"uname\">"
                           +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                           + "<INPUT TYPE=SUBMIT value=\"Start New Session\">"
                       + "</FORM>"
                    + "</div>";
                
                strTable +=
                   "<table align=center style='width:90%'>"
                    + "<tr>"
                        + "<td>"
                           + "<b>Session(s) stored in the system!</b><br><br />"
                        + "</td>"
                    + "</tr>"
                   + "</table>"
                   + Constants.JavaScript_OpenWindow;
                
                for (int i=0; i<_result.length; i++ ) {
                            
                   strTable +=
                        "<table align=center style='width:90%'>"
                              +"<tr>"
                                    +"<th align='left'><strong>Session "+ Integer.toString(++count) + "</strong></th>"
                              +"</tr>"
                              + "<tr>"
                                    +"<td>User Name :</td><td><input type=\"text\" value="+ _result[i].getEmail()+" readonly=\"readonly\" name=\"uName\" /></td>"

                                   // +"<td>Ontology Component :</td><td><input type=\"text\" value="+ontology_component+" readonly=\"readonly\" name=\"stype\" </td>"

                                    +"<td>Ontology 1 :</td><td><input type=\"text\" value="+ _result[i].getOntology1() +" readonly=\"readonly\" name=\"ont1\" /></td>"

                                    +"<td>Ontology 2 :</td><td><input type=\"text\" value="+ _result[i].getOntology2() +" readonly=\"readonly\" name=\"ont2\" /></td>"

                                    + "</tr>";
                   
                   
                   String savedAt="";
                   
                   if(_result[i].getStep()== Constants.STEP_UPLOAD)
                       savedAt="\"File upload\"";
                   else if(_result[i].getStep()== Constants.STEP_START)
                       savedAt="\"Start page\"";
                   else if(_result[i].getStep()== Constants.STEP_SLOT)
                       savedAt="\"Slot validation\"";
                   else if(_result[i].getStep()== Constants.STEP_CLASS)
                       savedAt="\"Class validation\"";
                   else if(_result[i].getStep()== Constants.STEP_FINISH)
                       savedAt="\"Class validation done\"";

                    strTable += "<tr>"
                                    
                                    +"<td>Step :</td><td><input type=\"text\" value="+savedAt+" readonly=\"readonly\" name=\"stepValue\"</td>"

                                    +"<td>Last Accessed :</td><td><input type=\"text\" value='"+ DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.LONG).format(_result[i].getLastAccessedTime())  +"' readonly=\"readonly\" name=\"lastAccessed\" /></td>";
                          
                          strTable +="<td><FORM METHOD=\"POST\" ACTION='LoadSession?sid="+_result[i].getId()+"' ENCTYPE='multipart/form-data'><input type='hidden' name='sid' value="+_result[i].getSid()+"><INPUT TYPE=SUBMIT value=\"Load Session\"></FORM></FORM></td>"
                                    + "<td>"
                           + "</tr>";
                      strTable += "</table>";

                      Commons.USER_SESSION_ID = _result[i].getId();
                     
                }

                /*str = "<FORM METHOD='POST' ACTION='LoadSession' ENCTYPE='multipart/form-data'>"
                    + strTable 
                    + "</form><br />";*/
                str    = strTable;
                 

        }
        catch (Exception _e) {
                _e.printStackTrace();
        }
        return str;
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

    /*synchronized String LoadSession(String email, String filePath, HttpServletRequest req)
    {
        return "";
    }*/

    // Added by Shahab
    /*synchronized String LoadAllSessions(String email, String filePath, HttpServletRequest req){
        String str = new String();
        String strTable = new String(); // Added by Shahab
        String mName = new String();
        String mValue = new String();
        String isFinalized = new String();
        String usedMatchers[] = new String[Commons.Matchers_Available.length];
        String strWeightList[] = new String[5];
        int countSession = 0;

        Commons.usedMatchersList.clear();
        Commons.usedWeightValuesList.clear();

        String ontology_component = new String();

        try{
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db =dbf.newDocumentBuilder();
            Document doc=db.parse(filePath);

            NodeList nodeList = doc.getElementsByTagName("userSession");
            strTable =
                   "<table align=center style='width:90%'>"
                    + "<tr>"
                        + "<td>"
                           + "<b>Session(s) stored in the system!</b><br><br />"
                        + "</td>"
                    + "</tr>"
                  + "</table>";
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

                    strTable +=
                        "<table align=center style='width:90%'>"
                              +"<tr>"
                                    +"<th align='left'><strong>Session "+ Integer.toString(++countSession) + "</strong></th>"
                              +"</tr>"
                              + "<tr>"
                                    +"<td>User Name :</td><td><input type=\"text\" value="+Commons.USER_NAME+" readonly=\"readonly\" name=\"uName\"</td>"
                              
                                   // +"<td>Ontology Component :</td><td><input type=\"text\" value="+ontology_component+" readonly=\"readonly\" name=\"stype\" </td>"
                              
                                    +"<td>Ontology 1 :</td><td><input type=\"text\" value="+Commons.OWL_1+" readonly=\"readonly\" name=\"ont1\"</td>"
                              
                                    +"<td>Ontology 2 :</td><td><input type=\"text\" value="+Commons.OWL_2+" readonly=\"readonly\" name=\"ont2\"</td>"
                              + "</tr>";
                           
                    strTable += "<tr>"
                                    +"<td>Threshold Value :</td><td><input type=\"text\" value="+Commons.THRESHOLD_VALUE+" readonly=\"readonly\" name=\"thresholdValue\"</td>"
                              
                                    +"<td>Step :</td><td><input type=\"text\" value="+Commons.STEP_VALUE+" readonly=\"readonly\" name=\"stepValue\"</td>";
                             
                          strTable +="<td><INPUT TYPE=SUBMIT value=\"Load Session\"></FORM></td>"
                                    +"<td>"
                           + "</tr>";
                      strTable += "</table>";          

                } //end if
            }//end for
           
       str = "<FORM METHOD='POST' ACTION='LoadSession' ENCTYPE='multipart/form-data'>"
               + strTable +
               "<br /><div align='center' style='width:178%'><table align='right' style='width:90%;border:1px solid black'>"
                   + "<FORM METHOD=\"POST\" ACTION=\"startSession.jsp\">"
                       +"<INPUT TYPE=HIDDEN value="+Commons.USER_NAME+" name=\"uname\">"
                       +"<INPUT TYPE=SUBMIT value=\"Start New Session\">"
                   + "</FORM>"
               + "</table></div>";

        }
        catch (Exception e){
            System.out.println(e);
        }
        
        return str;
    }*/

    public static java.util.Date toDate(java.sql.Timestamp timestamp) {
    long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
    return new java.util.Date(milliseconds);
    }
%>

