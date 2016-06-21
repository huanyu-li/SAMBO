<%@ page language="java" import="se.liu.ida.sambo.ui.web.*, java.io.*"%>

<%
        session = request.getSession(true);
%>


<%	
	out.println(PageHandler.createHeader(Constants.REASON) );
	
	out.println(FormHandler.createReasonUploadForm(Constants.UNK, null, null) );

	out.println(PageHandler.createFooter() );

%>