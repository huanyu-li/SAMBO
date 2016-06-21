<%--
Added by Rajaram
To stop the matching process and obtain partial result
--%>

<%@page import="se.liu.ida.sambo.algos.matching.algos.AlgoConstants"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; 
              charset=windows-1252">
        <title>Computation interrupt</title>
    </head>
    <body>
        <h1>Interrupt!!!!</h1>
        <%
        AlgoConstants.STOPMATACHING_PROCESS=true;
        String show="Computation is done for "
                +AlgoConstants.USER_INTERRUPT_AT+" concept pairs";
        out.println(show);
        %>
    </body>
</html>
