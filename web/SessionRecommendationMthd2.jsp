<%-- 
    Document   : SessionRecommendationMthd2
    Created on : Feb 6, 2012, 5:41:54 PM
    Author     : Rajaram
--%>

<%@page import="se.liu.ida.sambo.PRA.oracle.GeneratorOracleFromHisory"%>
<%@page import="se.liu.ida.sambo.Recommendation.RecommendationConstants"%>
<%@page import="se.liu.ida.sambo.ui.SettingsInfo"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ page import="se.liu.ida.sambo.ui.web.Constants, se.liu.ida.sambo.algos.matching.algos.AlgoConstants" %>


<%
RecommendationConstants.DO_RECOMMENDATION_MTH2 = true;
session = request.getSession(false);
MergeManager merge = (MergeManager)session.getAttribute(session.getId());
SettingsInfo settings = (SettingsInfo)session.getAttribute("settings");
GeneratorOracleFromHisory oracle = new GeneratorOracleFromHisory
        (merge,settings);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; 
              charset=windows-1252">
        <title>Recommendation Method 1</title>
        <script language="JavaScript">
         function redirect() {              
             window.open("strategies.jsp", "recommendation strategies", 
             'scrollbars=yes, resizable=yes, left=200, top=200, width=700, height=300');
             window.close();
         }
        </script> 
    </head>
    <body onload="redirect()">
   
    </body>
</html>
