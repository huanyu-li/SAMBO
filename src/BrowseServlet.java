
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.net.URL;

import com.hp.hpl.jena.ontology.OntModel;

import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.ui.web.*;
import se.liu.ida.sambo.Merger.OntManager;


public class BrowseServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest req,
            HttpServletResponse res)
            throws ServletException, IOException {
        
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        HttpSession session = req.getSession(true);
        
        if(req.getParameter("new") != null){
            
            session.setAttribute("display", "false");
            session.setAttribute("file_link",  "ontologies" + File.separator + req.getParameter("ontofile"));
            
            //load the ontology                       
            session.setAttribute("browse_ont",
                    OntManager.loadOntology((new File(Constants.FILEHOME + req.getParameter("ontofile"))).toURL(), true));
            
        }
        
        
        MOntology onto = (MOntology) session.getAttribute("browse_ont");
        
        //To browse the ontology tree structure
        //change the display status of the class
        String classname =req.getParameter("classname");
        if(classname != null)
            onto.getClass(classname).turnDisplay();
        
        
        //showAll or hideAll
        if(req.getParameter("all") != null){
            
            boolean display = true;
            if(((String)session.getAttribute("display")).equals("true")){
                display = false;
                session.setAttribute("display", "false");
            }else{
                session.setAttribute("display", "true");
            }
            
            for(Enumeration e = onto.getClasses().elements(); e.hasMoreElements();)
                ((MClass) e.nextElement()).setDisplay(display);
        }
        
        
        out.println(PageHandler.createHeader(Constants.WELCOME) );
        
        out.println("<p> <a class='menulink' href=\"" + (String) session.getAttribute("file_link") + "\"> Ontology Source File </a> </p>" );
        out.println(PageHandler.createClassTree(onto));
        
        out.println(PageHandler.createFooter());
        
    }
}
