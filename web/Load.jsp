<%@ page language="java" import="se.liu.ida.sambo.ui.web.*"%>

<%  
	
	out.println(PageHandler.createHeader(Constants.STEP_START) );
        
        
        out.println(FormHandler.createLoadMergeForm());
	
        out.println(PageHandler.createFooter() );
%>
