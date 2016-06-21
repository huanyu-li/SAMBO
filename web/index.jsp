<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page language="java" import="se.liu.ida.sambo.ui.web.*, java.io.*"%>

<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>


<%
//If the user log out, invalidate her session.
if(request.getParameter("out")!=null && session != null){
    //System.out.println("session.invalidate");
    session.invalidate();
    
//else remove all the session attributes related to alignment
}else if(session != null){
    for(java.util.Enumeration e = session.getAttributeNames(); e.hasMoreElements();){        
        String attr = (String) e.nextElement();
        if(!attr.equals("email") && !attr.equals("pwd")){
                
               session.removeAttribute(attr);
        }else{
            System.out.println(attr+ ":" +request.getParameter("attr"));

        }
    }
}
%>

<%
out.println(PageHandler.createHeader(Constants.WELCOME));
%>


<TABLE class="light_table" width="100%" cellbuffer="5">
    <tr> 

    <td width="25%" valign= "top"  align= "center" >

        <%
        // Menu table
        out.println(createMenu());
        %>

    </td>



    <td align= "center">

        <TABLE width="90%" align= "center" class="light_table">
            <tr><td valign= "top">
                <%                
                String incpage = "inc/welcome.inc";
                
                String to_page = request.getParameter("page");
                if (to_page != null) incpage="inc/" + to_page + ".inc";
                %>
                
                <jsp:include page="<%=incpage%>" flush="true" />
            </td></tr>
        </TABLE>

    </td> </tr>

</TABLE>


<%
out.println(PageHandler.createFooter() );
%>


<%! 
/**
    * Creates the menu for index page
    * @exception java.io.IOException if the menuitems.inc cannot be found
    */
synchronized String createMenu() throws java.io.IOException {
    
    BufferedReader in = null;
    String menuStr = "<TABLE width=\"90%\" class=\"border_table\" cellspacing=\"3\">";
    
    
    try {
        
        File menu = new File(getServletContext().getRealPath("inc/menuitems.inc"));
        in = new BufferedReader(new FileReader(menu));
        
        String line;
        
        while ( (line = in.readLine()) != null) {
            
            if ( line.equals("--break--") ) {
                menuStr += "<tr><td><br></td></tr>";
            } else {
                java.util.StringTokenizer st = new java.util.StringTokenizer(line, ";");
                String link = st.nextToken();
                
                //point to websites outside SAMBO
                if (link.startsWith("http")) {
                    
                    menuStr += "<tr><td>" + "&nbsp;&nbsp;&nbsp;"
                            + "<a class='menulink' href=\"" + link + "\" target=new>"
                            + st.nextToken() + "</a></td> </tr>";
                    
                }else {
                    
                    menuStr += "<tr><td>" + "&nbsp;&nbsp;"
                            + " <a class='menulink' href=\"" + link + "\">"
                            + st.nextToken() + "</a></td> </tr>";
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    menuStr += "</TABLE>";
    if(in!=null)
    {
        in.close();
    }
    return menuStr;
    
}
%>
