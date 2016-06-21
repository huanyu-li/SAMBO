<%-- 
    Document   : strategies
    Created on : Jan 5, 2011, 7:10:02 PM
    Author     : Shahab
--%>

<%@page import="se.liu.ida.sambo.factory.PredefinedStrategiesDaoFactory"%>
<%@page import="se.liu.ida.sambo.dto.PredefinedStrategies"%>
<%@page import="se.liu.ida.sambo.dao.PredefinedStrategiesDao"%>
<%@page import="se.liu.ida.sambo.Merger.Constants"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
       
        <title>Recommendations</title>
    </head>
    <body>
        <table border="0" align="center" width="80%" class="border_table">
            <tbody>
                <tr>
                    <td align="center">
                    <% out.print(getPredefinedStrategies()); %>
                    </td>
                </tr>
            </tbody>
        </table>
    </body>
</html>

<%!

synchronized String getPredefinedStrategies(){

    String formStr = "<center> <strong>List of Predefined Strategies <strong></center>";

            //out table
            formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                    + "<tr><td valign=\"top\">";

            //form and table
            formStr += "<FORM method=POST action=\"Class\">"
                    + "<TABLE border=\"0\" width=\"100%\">"
                    + "<tr><td> <strong>s.no </strong></td><td><strong> strategy </strong> </td><td><strong> weights </strong></td><td><strong>threshold</strong></td></tr>";
            try {
                    PredefinedStrategiesDao _dao = getPredefinedstrategiesDao();
                    PredefinedStrategies _result[] = _dao.findAll();
                    for (int i=0; i<_result.length; i++ ) {
                        formStr +=  "<tr>" +
                                    "<td>" + (i+1) + "</td>" +
                                    "<td>" + _result[i].getMatchers().trim() + "</td>" +
                                    "<td>(" + _result[i].getWeights().trim() + ")</td>" +
                                    "<td>" + _result[i].getThreshold() + "</td>"+
                                    "</tr>";
                    }
		}
		catch (Exception _e) {
			_e.printStackTrace();
		}
            
            formStr += " </table>"
                    + "</tr></table></td> </tr>";

            formStr += "<tr><td align=right><center><input type='button' name='Recommendations' value='Generate Recommendations' onclick=\"javascript:window.open('recommendationsMethod1.jsp','_self','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=1200, height=400');\" /></center></td></tr>";

            // Close table and form
            formStr += "</TABLE></FORM>";


            // Close outer table
            formStr += "</td></tr></TABLE>";



            return formStr;
}

 /**
     * Method 'getPredefinedstrategiesDao'
     *
     * @return PredefinedstrategiesDao
     */
    public static PredefinedStrategiesDao getPredefinedstrategiesDao()
    {
            return PredefinedStrategiesDaoFactory.create();
    }
%>