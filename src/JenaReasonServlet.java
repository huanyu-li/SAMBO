
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.Vector;

import com.oreilly.servlet.MultipartRequest;

import com.hp.hpl.jena.ontology.OntModel;

import se.liu.ida.sambo.algos.reasoner.*;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.Merger.*;
import se.liu.ida.sambo.ui.web.FormHandler;
import se.liu.ida.sambo.ui.web.PageHandler;


/** The servlet handles the reasoning request
 * @author He Tan
 */
public class JenaReasonServlet extends HttpServlet {

    //reason the merged ontology
    public void doGet(HttpServletRequest req,
            HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        System.out.println(se.liu.ida.sambo.ui.web.Constants.defaultReasoner);
        OntModel rm = OntManager.createReasonOntModel(OntManager.createJenaDIGReasoner(se.liu.ida.sambo.ui.web.Constants.defaultReasoner),
                          (new File(Constants.FILEHOME + req.getParameter("ontofile"))).toURI().toURL().toString()),
                  m = ((MergeManager) session.getAttribute(session.getId())).getOntManager().getOntModel(Constants.ONTOLOGY_NEW);

        reasoning(JenaReasoner.getIncon(rm), JenaReasoner.getCycles(rm, m),JenaReasoner.getSubsumption(rm, m), res, res.getWriter());

    }

    //reason an uploaded ontology
    public void doPost(HttpServletRequest req,
            HttpServletResponse res)
            throws ServletException, IOException{


        HttpSession session = req.getSession(false);
        session.removeAttribute("file");

        // Upload max 2Mb
        MultipartRequest multi = new MultipartRequest(req, Constants.FILEHOME);

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        com.hp.hpl.jena.reasoner.dig.DIGReasoner reasoner = null;
        if(multi.getParameter("reasoner") != null)
            reasoner = OntManager.createJenaDIGReasoner(multi.getParameter("reasoner"));

        se.liu.ida.sambo.ui.web.Constants.defaultReasoner = multi.getParameter("reasoner");
        System.out.println("reasoner");
        //upload file to reasoning
        int type = (new Integer(multi.getParameter("type0"))).intValue();

        if ( type != Constants.UNK && multi.getParameter("reason") != null){

            String file = multi.getParameter("FILE0");

           // if (type==Constants.URL){

            if (type==Constants.FILE && multi.getFile("FILE0") != null){
                file = (new File(multi.getFile("FILE0").getPath())).toURL().toString();

            }else if (type==Constants.ON_SERVER && file != null && file.length() >0 ){
                file = (new File(Constants.FILEHOME + Constants.languages[Constants.OWL] + "/" + file)).toURL().toString();
            }

            if(file != null & file.length() >0)
                session.setAttribute("file", file);
        }

        //upload file successfully
        if(session.getAttribute("file") !=null){

            OntModel rm = OntManager.createReasonOntModel( reasoner, (String) session.getAttribute("file")),
                      m = OntManager.createOntModel((String) session.getAttribute("file"), false);

            reasoning(JenaReasoner.getIncon(rm), JenaReasoner.getCycles(rm, m),JenaReasoner.getSubsumption(rm, m), res, out);

        }else {

            try {
                out.print(PageHandler.createHeader(Constants.REASON));
                out.print(FormHandler.createReasonUploadForm(type, null, reasoner.getReasonerURL()));
                out.print(PageHandler.createFooter());

            } finally {
                out.close();
            }

        }


    }


    //  private void reasoning(JenaReasoner reason, HttpSession session, HttpServletResponse res, PrintWriter out)
    private void reasoning(Vector incon, Vector cyc, Vector sub, HttpServletResponse res, PrintWriter out){


        try {
            out.print(PageHandler.createHeader(Constants.REASON));
            out.print(FormHandler.reasonResultForm(incon, cyc, sub));
            out.print(PageHandler.createFooter());
        } finally {
            out.close();
        }


    }


}




