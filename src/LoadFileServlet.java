import javax.servlet.*;
import javax.servlet.http.*;
import com.oreilly.servlet.MultipartRequest;

import java.io.*;
import java.net.URL;

import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.ui.web.*;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.Merger.*;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.session.*;

public class LoadFileServlet  extends HttpServlet {
    
   
    public void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException{
        
        HttpSession session = req.getSession(false);
        Commons.currentPosition = 1;

        // Upload max 1Mb
        //MultipartRequest multi = new MultipartRequest(req, Constants.FILEHOME);

        // Upload max 10MB
        MultipartRequest multi = new MultipartRequest(req, Constants.FILEHOME, 1024*1024*1024);

        // get the upload type of ontologies
        int type1 = (new Integer(multi.getParameter("type1"))).intValue(),
            type2 = (new Integer(multi.getParameter("type2"))).intValue();
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        
        // start to get the source files' info
        if ((type1 != Constants.UNK)  && (type2 != Constants.UNK) && (multi.getParameter("upload") != null) )
            //upload the files
            session.setAttribute("settings", acquireResources(multi, out, type1, type2));
        else
            printUpload(out, type1, type2);
        
        //if the source files are set
        if(session.getAttribute("settings")  != null){
            
            SettingsInfo settings = (SettingsInfo) session.getAttribute("settings");
            
             AlgoConstants.settingsInfo=settings;
            

            //try to upload ontologies
            //and prepare for the alignment and merging task
            try
            {
                long t1 = System.currentTimeMillis();
                
                MergeManager merge = new MergeManager();
                out.println(PageHandler.createHeader(Constants.STEP_SLOT));
                merge.loadOntologies(settings.getURL(Constants.ONTOLOGY_1), settings.getURL(Constants.ONTOLOGY_2));
                merge.init();
                session.setAttribute(session.getId(), merge);
                settings.setStep(Constants.STEP_SLOT);
                out.println(FormHandler.createStartForm(settings, Constants.STEP_SLOT));  // This displays form which has filters
                out.println(FormHandler.createRecommendationForm(settings, Constants.STEP_SLOT));
                out.println(PageHandler.createFooter());
                merge.clearMerge();
                
            
            long t2 = System.currentTimeMillis();
                        
            System.out.println( "Time Taken to LOAD FILE " + (t2-t1) + " ms" );
                
                
            }
            finally
            {
                out.close();
            }
        }
    }    
    
    // Aquire the files, but try to avoid overwriting files with the same name
    private synchronized SettingsInfo acquireResources(MultipartRequest multi, PrintWriter out,
            int type1, int type2) throws IOException{

        URL url1, url2;
        try{
            // configure the settings of the session
            url1 = getURL(multi, type1, "FILE1");
            url2 = getURL(multi, type2, "FILE2");
            
            //MalformedURLException, or NullPointException
        }catch(Exception e){
            //if malformed url exception occur, allow user to restart loading ontologies
            printUpload(out, type1, type2);
            return null;
        }        
        
        SettingsInfo settings = new SettingsInfo();
        settings.setURLs(url1, url2 );
        settings.setNames(filename(url1), filename(url2), multi.getParameter("name3"));
        settings.setColors(multi.getParameter("color1"), multi.getParameter("color2"));
        
        return settings;
    }
    
    
    //print upload page
    private void printUpload(PrintWriter out, int type1, int type2){
        
        try {
            out.print(PageHandler.createHeader(Constants.STEP_UPLOAD));
            out.print(FormHandler.createFileUploadForm(type1, type2));
            out.print(PageHandler.createFooter());
            
        } finally {
            out.close();
        }
    }
    
    
    //get source ontology's URL
    private URL getURL(MultipartRequest multi, int type, String file) throws Exception{
        
        if (type==Constants.URL)
            return new URL(multi.getParameter(file));
        
        else if (type==Constants.FILE)
            return multi.getFile(file).toURL();
                
        //type is Constants.ON_SERVER
        return (new File( Constants.FILEHOME + Constants.languages[Constants.OWL]
                + File.separator + multi.getParameter(file))).toURL();
        
    }
    
    
    //get filename, ex.
    // file:///.../behavior_GO.owl  --> behavior_GO
    private String filename(URL url){
        
        return filename((new File(url.getFile())).getName());
    }
    
    
    private String filename(String name){
        
        if(name.lastIndexOf('.') != -1)
            return name.substring(0, name.lastIndexOf('.'));
        
        return name;
    }
    
}



