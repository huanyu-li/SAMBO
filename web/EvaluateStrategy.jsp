<%-- 
    Document   : EvaluateStrategy
    Created on : May 14, 2012, 6:51:17 PM
    Author     : Rajaram
--%>

<%@page import="se.liu.ida.sambo.util.testing.EvaluateStrategies"%>
<%@page import="org.springframework.web.servlet.tags.EscapeBodyTag"%>
<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; 
              charset=windows-1252">
        <title>Evaluate alignment strategies</title>
    </head>
    <body>
        <%!
        EvaluateStrategies es= new EvaluateStrategies();
        %>
        
        <%        
        es.startEvaluation();
        out.println("Evaluation done!!!!!");
        %>
    </body>
</html>
