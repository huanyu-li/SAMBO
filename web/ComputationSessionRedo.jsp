<%-- 
Code Added by Rajaram
--%>

<%@page import="java.util.Date"%>
<%@page import="java.text.DateFormat"%>
<%@page import="se.liu.ida.sambo.Merger.MergeManager"%>
<%@page import="se.liu.ida.sambo.segPairSelAlgs.SubG"%>
<%@page import="se.liu.ida.sambo.factory.UserSessionsDaoFactory"%>
<%@page import="se.liu.ida.sambo.dao.UserSessionsDao"%>
<%@page import="se.liu.ida.sambo.dto.UserSessions"%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page language="java" import="javax.mail.*, javax.mail.internet.*, se.liu.ida.sambo.util.XMLFileHandler, se.liu.ida.sambo.ui.SettingsInfo, se.liu.ida.sambo.ui.web.*, org.w3c.dom.*, java.io.*"%>
<%@page import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.session.*"%>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>

<%@ page isThreadSafe="false" %>

<%-- 
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<%
out.println(PageHandler.createHeader(Constants.WELCOME));
out.println("<form METHOD=\"POST\" ACTION='Class'>");
out.println("<center><table>");
out.println("<tr>");
out.println("<td><input type='submit' name='ValdORComp' "
        + "value='computation'/></td></tr></br>");
out.println("</table></center>");
out.println("</form>");
%>