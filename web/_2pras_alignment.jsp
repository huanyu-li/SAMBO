<%-- 
    Document   : pras_alignment
    Created on : Jan 6, 2011, 5:44:41 PM
    Author     : Shahab
--%>



<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="se.liu.ida.sambo.MModel.util.NameProcessor"%>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.util.Properties"%>
<%@ page import="se.liu.ida.sambo.PRA.PRA"%>
<%@ page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.Merger.MergeManager, se.liu.ida.sambo.util.Pair, se.liu.ida.sambo.MModel.MElement, java.util.Vector, java.util.Enumeration" %>

<%
    
%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>PRA's Alignment</title>
        <script>
            function alignUsingPRA()
            {
                for(i=0; i < 3; i++)
                {
                   if(document.alignments.group[i].checked)
                    document.alignments.action = "http://"+ window.location.hostname + ":"+ window.location.port + Commons.CONTEXT_PATH + "/pras_alignment.jsp?id="+ document.alignments.group[i].value;
                }
                
            }
        </script>
        <script type="text/javascript">
            function reloadSession()
            {
                window.opener.location.href="http://"+ window.location.hostname + ":"+ window.location.port + Commons.CONTEXT_PATH + "/LoadSession?sid=64";
                self.close();
            }

        </script>
    </head>
    <body>
        <table border="0" align="center" width="80%" class="border_table">
            <tbody>
                <tr>
                    <td align="center">
                    <%
                       //Commons.PROPERTIES_NAME = request.getRealPath( "/" ).replace("\\build", "")+"WEB-INF\\lib\\sambo.properties";
                       //Properties configFile = new Properties();
                       //FileInputStream file = new FileInputStream( Commons.PROPERTIES_NAME );
                       //configFile.load(file);
                       if(request.getParameter("id") != null)
                        //   out.print(getPRAsAlignment());
                       //else
                       {
                           PRA op = new PRA();
                           if(request.getParameter("id").equals("fPRA"))
                               op.fPRA();
                           else if(request.getParameter("id").equals("dtfPRA"))
                               op.dtfPRA();
                           else if(request.getParameter("id").equals("mgPRA"))
                               op.mgPRA();
                           out.println("<form enctype=\"multipart/form-data\" method=POST>");
                           out.println("pra alignment for "+request.getParameter("id")+" has been done successfully");
                           out.println("<br />");

                           // Read XML file

                           DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                           DocumentBuilder db =dbf.newDocumentBuilder();
                           System.out.println("Path: "+ Commons.SEGMENT + "/RA/RemainingSuggestions.xml");
                           Document doc=db.parse(Commons.SEGMENT + "/RA/RemainingSuggestions.xml");
                           doc.getDocumentElement().normalize();

                           // Display Remaining Suggestions List
                           NodeList nodeLst = doc.getElementsByTagName("Suggestions");
                           out.println("<br />");
                           out.println("Mapping suggestions being filtered off are as follows");
                           out.println("<br />");
                           out.println("<table class='light_table' width='100%'>");
                           for (int s = 0; s < nodeLst.getLength(); s++)
                           {
                                Node pairNode = nodeLst.item(s);
                                Element pairElmnt = (Element) pairNode;
                                NodeList pairElmntLst = pairElmnt.getElementsByTagName("Pair");
                                Element pairNmElmnt = (Element) pairElmntLst.item(0);
                                for(int p = 0; p < pairElmntLst.getLength(); p++)
                                {
                                    if(p > 0)
                                        pairNmElmnt = getNextElement(pairNmElmnt);
                                    NodeList pairNm = pairNmElmnt.getChildNodes();
                                    out.println("<tr><td align ='left'>");
                                    String removedSugg = ((Node) pairNm.item(0)).getNodeValue();
                                    /*String term1 = removedSugg.substring(8,removedSugg.indexOf(",1]"));
                                    String term2 = removedSugg.substring(removedSugg.indexOf(", [class:")+9,removedSugg.indexOf("2],") -1);
                                    */
                                    Pattern pattern = Pattern.compile("\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]");
                                    Matcher matcher1 = pattern.matcher(removedSugg);
                                    String term1 = "";
                                    String term2 = "";
                                    if(matcher1.find())
                                    {
                                       // Parse out term1 from PRA suggestions
                                       term1 = matcher1.group(1).trim();
                                       // Parse out term2 from PRA suggestions
                                       term2 = matcher1.group(2).trim();
                                    }
                                    out.println("Suggestion "+ (p+1) +": "  + Commons.monto1.getClass(term1).getLabel() + "   ---   "+ Commons.monto2.getClass(term2).getLabel());
                                    out.println("<br />");
                                    out.println("</td></tr>");
                                }
                           }
                           out.println("</table>");

                           //out.println("Please click on the link below to see the remaining suggestions that are updated");
                           //out.println("<br />");
                           //out.println("<a href='file:///"+configFile.getProperty("ROOT")+"RemainingSuggestions.xml'>Remaining Suggestions</a>");
                           //out.println("<a href='file:///"+Commons.SEGMENT + "RA/" +"RemainingSuggestions.xml'>Remaining Suggestions</a>");
                           //out.println("<a href='file:///"+getServletContext().getRealPath("/segment/RA/RemainingSuggestions.xml")+"'>Remaining Suggestions</a>");
                           /*out.println("<br />");
                           out.println("<input type=\"button\" name=\"continueToAlignment\" value=\"Continue to Alignment\" onclick=\"javascript:reloadSession();\" />");
                           out.println("</form>");*/
                       }
                    %>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>

<%!

synchronized Element getNextElement(Element el)
{
    Node nd = el.getNextSibling();
    while (nd != null) {
        if (nd.getNodeType() == Node.ELEMENT_NODE) {
            return (Element)nd;
        }
        nd = nd.getNextSibling();
    }
    return null;
}

/* synchronized String getPRAsAlignment(){
    String formStr = "<center> <strong>PRA Alignment Algorithms <strong></center>";

    //form and table
            formStr += "<form enctype=\"multipart/form-data\" name='alignments' method=POST action=\"pras_completion.jsp\">"
                    +  "<div align=center><br>"
                    +  "<table border=\"0\" width=\"100%\">"
                    +  "<tr><td><input type=radio name=group value=fPRA> Filter with PRA (fPRA) <td></tr>"
                    +  "<tr><td><input type=radio name=group value=dtfPRA checked> Double Threshold Filter with PRA (dtfPRA)<td></tr>"
                    +  "<tr><td><input type=radio name=group value=mgPRA> Mappable Groups with PRA (mgPRA)<td></tr>"
                    +  "<tr><td>&nbsp;<td></tr>"
                    +  "<tr><td align=center><input type=submit name=PRA value=Align onclick='alignUsingPRA();return;'/><td></tr>"
                    +  "</table>"
                    +  "</div>"
                    +  "</form>";
    
    return formStr;
}*/
%>
