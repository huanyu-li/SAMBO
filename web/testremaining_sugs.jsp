<%-- 
    Document   : testremaining_sugs
    Created on : Jul 19, 2016, 9:56:29 AM
    Author     : huali50
--%>
<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.Merger.testMergerManager, se.liu.ida.sambo.util.testPair,  java.util.Vector, java.util.Enumeration" %>

<% 
session = request.getSession(false);

testMergerManager merge = (testMergerManager)session.getAttribute(session.getId());
SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
String color1 = settings.getColor(Constants.ONTOLOGY_1);
String color2 = settings.getColor(Constants.ONTOLOGY_2);
Vector sugs = merge.getRemainingSuggestions();
Commons.remainingSuggestionVector = merge.getRemainingSuggestions();
%>


<html>
    <head>
        <title>Remaining Suggestions</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <link rel="stylesheet" href="stylesheet.css" type="text/css">
    </head>
    <body>

        <TABLE width="95%" class="light_table" align="center">
            <tr><td>
            <tr><td align="center">
 
            <%
            
            out.println(Constants.createColorLegend(settings.getName(Constants.ONTOLOGY_1),
                    settings.getName(Constants.ONTOLOGY_2),
                    color1, color2 ));
            %>

            <tr><td><TABLE width="90%" class="border_table" align="center">

                <%
                
                
                int size = sugs.size();
                for(int i=1; i<size; i++){
                    out.println("<tr>");
                    testPair pair = (testPair) sugs.get(i);
                    String n1 = pair.getLocalName(pair.getSource());
                    String n2 = pair.getLocalName(pair.getTarget());
                    out.println("<td width=\"2%\">");
                    out.println("<td>("  + Constants.fontify(n1, color1)
                    +", " + Constants.fontify(n2, color2)+")");
                }
                %>
            </TABLE><tr><td>
        </TABLE>

    </body>
</html>
