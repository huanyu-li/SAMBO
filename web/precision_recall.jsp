<%-- 
    Document   : precision_recall
    Created on : Jan 13, 2011, 10:42:13 PM
    Author     : Shahab
--%>

<%@page import="se.liu.ida.sambo.session.Commons"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%
Commons.StrategyId = request.getParameter("id");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Precision & Recall</title>
    </head>
    <body>
        <table border="0" align="center" width="80%" class="border_table">
            <tbody>
                <tr>
                    <td align="center">
                    <% out.print(getPrecisionAndRecall()); %>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>

<%!

    synchronized String getPrecisionAndRecall(){

            String formStr = "<center> <strong>Precision and Recall for segment pair ontologies<strong></center>";

            //out table
            formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                    + "<tr><td valign=\"top\">";

            //form and table
            formStr += "<FORM method=POST action=\"Class\">"
                    + "<TABLE border=\"0\" width=\"100%\">"
                    + "<tr><td> <strong>S.no </strong></td><td><strong>Precision</strong></td><td> <strong>Recall </strong></td></tr>";
            try {

                    ArrayList resultPrecision = (ArrayList) Commons.precisionList.get(Integer.parseInt(Commons.StrategyId));
                    ArrayList resultRecall = (ArrayList) Commons.recallList.get(Integer.parseInt(Commons.StrategyId));
                    for (int i=0; i< resultPrecision.size(); i++ ) {
                        formStr +=  "<tr>" +
                                        "<td>"+
                                        (i+1)+
                                        "</td>"+
                                        "<td>"+
                                        resultPrecision.get(i)+
                                        "</td>"+
                                         "<td>"+
                                        resultRecall.get(i)+
                                        "</td>"+
                                    "</tr>";
                    }
		}
		catch (Exception _e) {
			_e.printStackTrace();
		}

            formStr += " </table>"
                    + "</tr></table></td> </tr>";


            // Close table and form
            formStr += "</TABLE></FORM>";


            // Close outer table
            formStr += "</td></tr></TABLE>";

            return formStr;
    }
%>
