<%-- 
    Document   : startSession
    Created on : Apr 27, 2010, 1:03:56 PM
    Author     : mzk
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" import="se.liu.ida.sambo.ui.web.*, se.liu.ida.sambo.session.*"%>

<%
session=request.getSession(true);
    Commons.currentPosition = 0;
    Commons.USER_SESSION_ID = 0;
    Commons.new_session = 1;
    out.println(PageHandler.createHeader(Constants.STEP_START) );

    if (request.getParameter("uname") != null) {
    
        out.println(FormHandler.createFileUploadForm(Constants.UNK, Constants.UNK));
        //Constants.userName = request.getParameter("email").trim();
        
        out.println("Login Information: " + Commons.USER_NAME ); // Added by Me
        // It returns false if File or directory does not exist

    } else if (request.getSession(true).getAttribute("email") != null) {

            //Go to Align and Merge
            out.println(FormHandler.createFileUploadForm(Constants.UNK, Constants.UNK));
    }
    out.println(PageHandler.createFooter() );
%>
