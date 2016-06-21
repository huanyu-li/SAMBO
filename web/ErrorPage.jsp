<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@page isErrorPage="true" %>
<%@ page language="java" import="se.liu.ida.sambo.ui.web.*"%>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>




<%
out.println(PageHandler.createHeader(Constants.CANCEL));
%>

<TABLE width="70%" class="border_table" align="center"> 

<tr> <td align="center" class='blue'> 

<%= exception.getClass().getName()%>
:&nbsp;&nbsp;
<%= exception.getMessage()%>
</td> </tr>
<tr> <td align="center"><a class='menulink' href="index.jsp">Start Over</a></td> </tr>
 
</TABLE>


<%
out.println(PageHandler.createFooter());
%>
