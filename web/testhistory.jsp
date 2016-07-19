<%-- 
    Document   : testhistory
    Created on : Jul 19, 2016, 9:40:44 AM
    Author     : huali50
--%>

<%@page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.Merger.testMergerManager,  se.liu.ida.sambo.util.*,  java.util.Vector, java.util.Enumeration" %>
<%@page import="se.liu.ida.sambo.session.Commons" %>
<% 
session = request.getSession(false);

testMergerManager merge = (testMergerManager)session.getAttribute(session.getId());
SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");

String color1 = settings.getColor(Constants.ONTOLOGY_1);
String color2 = settings.getColor(Constants.ONTOLOGY_2);




%>

<%! 
//createPRAFile PRAFile=null;
%>


<html>
    <head>
        <title>History</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <link rel="stylesheet" href="stylesheet.css" type="text/css">
    </head>
    <body>

        <TABLE width="95%" class="light_table" align="center">
        <tr><td>
        <tr><td align="center"> 
 
        <%
        
        out.println(Constants.createColorLegend(settings.getName(Constants.ONTOLOGY_1),
                settings.getName(Constants.ONTOLOGY_2), color1, color2 ));
        
        //Added by Rajaram
        //PRAFile=new createPRAFile();
        %>

        <tr><td><TABLE width="90%" class="border_table" align="center">

            <%            
            
            for(Enumeration e = merge.getHistory().elements(); e.hasMoreElements();){
                out.println(getHistoryInfo((testHistory) e.nextElement(), settings, color1, color2 ));
                               }
        
        for(Enumeration e = merge.getCurrentHistory().elements(); e.hasMoreElements();){
            out.println(getHistoryInfo((testHistory) e.nextElement(), settings, color1, color2 )); 
        }                  
           // To create PRA oracle file
           //PRAFile.createFile();
            %>

        </TABLE>
        <tr><td></TABLE>

    </body>
</html>


<%!
public int PRA_Serial=0;
synchronized String getHistoryInfo(testHistory history, SettingsInfo settings, String color1, String color2){
    
    String str ="";
    //String C1="",C2="";
    
    
    testPair p = (testPair) history.getPair();    
    
    
    switch (history.getAction()){
        
        case Constants.ALIGN_SLOT:
        case Constants.ALIGN_CLASS:
            
            str += "<tr><td width=\"5%\"><td align=\"left\">"
                    +"<a title='" + history.getComment() + "'>"
                    + "<img src=\"img/icon_merge.jpg\" border=\"0\">&nbsp;&nbsp;";
            
            if(history.getName() != null && history.getName().trim().length() > 0)
                str += history.getName() + "&nbsp;" ;
            
            str += "(" + Constants.fontify(p.getLocalName(p.getSource()), color1)
            + ", " + Constants.fontify(p.getLocalName(p.getTarget()), color2) + ")";
            
            //Added by Rajaram
           //To create unique serial number for PRA pair            
            //C1=(String)((MElement) p.getObject1()).getLabel();
           // C2=(String)((MElement) p.getObject2()).getLabel();
           // PRAFile.generatePRASerial(C1,C2);
                     
            
            
            
            if(history.getWarning() != Constants.UNIQUE){
                
                str += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<span class= 'warn'> warning:&nbsp;name conflict on \"" ;
                
                if(history.getName() != null && history.getName().trim().length() > 0)
                    str +=  history.getName();
                
                else if(history.getWarning() == Constants.ONTOLOGY_1)
                    str += p.getLocalName(p.getSource());
                
                else if(history.getWarning() == Constants.ONTOLOGY_2)
                    str += p.getLocalName(p.getTarget());
                
                str  +=  "\"</span>";
            }
            
            str += "</a></td></tr>";
            
            break;
            
        case Constants.IS_A_CLASS:
            
            str +="<tr><td width=\"5%\"><td align=\"left\">"
                    +"<a title='" + history.getComment() + "'>"
                    + "<img src=\"img/icon_relation.jpg\" border=\"0\">&nbsp;&nbsp;";
            
            if(history.getNum() == Constants.ONTOLOGY_1){
                str += Constants.fontify(p.getLocalName(p.getSource()), color1) + "<br> "
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<img src=\"img/pointer_relation.jpg\" border=\"0\">"
                        + Constants.fontify(p.getLocalName(p.getTarget()), color2);
                
                
            //Added by Rajaram
            //C1=(String)((MElement) p.getObject1()).getLabel();
            //C2=(String)((MElement) p.getObject2()).getLabel();
            //PRAFile.generatePRASerial(C1,C2);
            }
            
            if(history.getNum() == Constants.ONTOLOGY_2){
                str += Constants.fontify(p.getLocalName(p.getTarget()), color1) + "<br> "
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<img src=\"img/pointer_relation.jpg\" border=\"0\">"
                        + Constants.fontify(p.getLocalName(p.getSource()), color2);
                
                
            //Added by Rajaram
            //C1=(String)((MElement) p.getObject1()).getLabel();
            //C2=(String)((MElement) p.getObject2()).getLabel();
            //PRAFile.generatePRASerial(C1,C2);
            
            }
            
            if(history.getWarning() != Constants.UNIQUE)
                str +=  "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<span class= 'warn'> warning:&nbsp;"
                        + " name conflict between \"" + p.getLocalName(p.getSource())
                        + "\" and \"" + p.getLocalName(p.getTarget()) + "\"</span>";
            
            
            str += "</a></td></tr>";
            break;
            
        case Constants.NO:
            
            str += "<tr><td width=\"5%\"><td align=\"left\">"
                    +"<a title='" + history.getComment() + "'>"
                    + "<img src=\"img/icon_reject.gif\" border=\"0\">"+"&nbsp;&nbsp;( ";
            
            str += Constants.fontify(p.getLocalName(p.getSource()), color1) +  ", "
                   + Constants.fontify(p.getLocalName(p.getTarget()), color2) + ")";
            
            
            
            
            //Added by Rajaram
           // System.out.println("C1: "+((MElement) p.getObject1()).getLabel()+" C2 "+((MElement) p.getObject2()).getLabel()+" are rejected");
            
            
            if(history.getWarning() != Constants.UNIQUE)
                str +=  "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<span class= 'warn'> warning:&nbsp;"
                        + " name conflict between \"" + p.getLocalName(p.getSource())
                        + "\" and \"" + p.getLocalName(p.getTarget()) + "\"</span>";
            
            str += "</a></td></tr>";
            break;
            
    }
    
    return str;
}


%>
