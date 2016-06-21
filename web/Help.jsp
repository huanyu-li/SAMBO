<%@ page language="java" import="se.liu.ida.sambo.ui.web.*"%>

<%
out.println(PageHandler.createHeader(Constants.HELP));
%>

<jsp:include page="inc/help.inc" flush="true" />

<%

out.println(PageHandler.createFooter());
%>