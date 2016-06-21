<%@ page language="java" import="se.liu.ida.sambo.ui.web.*, java.io.*"%>

<%
session = request.getSession(true);
out.println(PageHandler.createHeader(Constants.BROWSE));
%>
        
        
        
<TABLE width="30%" class="border_table"><tr><td><br>

    <%
    File[] server_files = (new File(Constants.FILEHOME + File.separator + Constants.languages[Constants.OWL])).listFiles();
    
    out.println("<ul>");
    for (int i = 0; i < server_files.length; i++) {
        
        File file = server_files[i];
        if(file.isFile() && !file.isHidden()){
            String link = "<li><a class='menulink' target=\"_blank\""
                    + " href=\"Browse?ontofile=OWL"  + File.separator + file.getName()
                    +  "&new\">"  +  file.getName()  + "</a></li>";
            out.println(link);
        }
        
    }
    out.println("</ul>");
    %>
        
</td></tr></TABLE>

<%

out.println(PageHandler.createFooter());
%>