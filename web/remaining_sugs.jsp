<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.Merger.MergeManager, se.liu.ida.sambo.util.Pair, se.liu.ida.sambo.MModel.MElement, java.util.Vector, java.util.Enumeration" %>

<% 
session = request.getSession(false);

MergeManager merge = (MergeManager)session.getAttribute(session.getId());
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
                    Pair pair = (Pair) sugs.get(i);
                    String n1 = ((MElement)pair.getObject1()).getLabel();
                    String n2 = ((MElement)pair.getObject2()).getLabel();
                    out.println("<td width=\"2%\">");
                    out.println("<td>("  + Constants.fontify(n1, color1)
                    +", " + Constants.fontify(n2, color2)+")");
                }
                %>
            </TABLE><tr><td>
        </TABLE>

    </body>
</html>
