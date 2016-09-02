/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.ui.web;

import java.io.File;
import java.util.Enumeration;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;

/**
 *
 * @author huali50
 */
public class testPageHandler {
    // merging steps
    private static final String[] steps = {"welcome", "start", "start", "slot", "class", "finish"};   
  
    
    /**
     * Creates the header for an HTML page
     *
     * @param pagename The title for the page
     * @param step Which step in the merge process this page represents
     */
    
    public static String createHeader(int step) {
        
        //html head part
        String headerStr = "<html>";
        
        //can change stylesheet here
        headerStr += "<head><title>SAMBO | "+  Constants.headers[step] + "</title>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
                "<link rel=\"stylesheet\" href=\"stylesheet.css\" type=\"text/css\">";
        
        
        headerStr +="</head>";
        
        //html body
        headerStr += "<body>";
        //body table:
        headerStr += "<table width=\"96%\" border=\"0\" align=\"center\" class=\"light_table\">";
        
        //the header row:
        headerStr += "<tr><td > <table width=\"100%\" class=\"light_table\" >";
        
        //Inside header table:
        headerStr += "<tr><th rowspan=\"2\" width=\"90%\"  align=\"left\" > <a href=\"index.jsp\">" +
                "<img src=\"img/sambo.jpg\" border=\"0\" alt=\"homepage\"> </a></th>";
        
        
        if ( (step > Constants.WELCOME) && (step <= Constants.STEP_FINISH) ) {
            
            //question button
            headerStr += "<td valign=\"bottom\">"
                   // + "<a href=\"Help.jsp#" + steps[step] + "\" target=\"new\">"
                    + "<a href=\"Help.jsp#" + "\" target=\"new\">"
                    + "<img src=\"img/help.gif\"  border=\"0\" alt=\"help\"></a>"
                    + "</td></tr>";
            
            //cancel button
            headerStr += "<tr><td width=\"10%\" valign=\"top\">"
                    + "<FORM method=GET action=\"LockSessionServlet\">"
                    + "<INPUT TYPE=\"image\" SRC=\"img/exit.gif\" name=\"Lock\" value=\"Lock\" type=\"submit\">"
                    + "</FORM></td></tr>";

//            //cancel button
//            headerStr += "<tr><td width=\"10%\" valign=\"top\">"
//                    + "<a href=\"index.jsp?out\">"
//                    + "<img src=\"img/exit.gif\"  border=\"0\" alt=\"exit\"> </a>"
//                    + "</td></tr>";
        
            
            //step pic
            headerStr += "<tr> <td colspan=2  align=\"center\">" +
                    // Which merge-step-pic to include
                    "<img src=\"img/step_" + steps[step] +".gif\" alt=\"" + steps[step] + "\" border=\"0\">";
            
        }else {
            
            headerStr += "</tr>";
            
        }
        
        //close the header table;
        headerStr += "</td> </tr> </table> </td></tr>";
        
        
        
        
        
        //the merge row:
        headerStr += "<tr><td valign=\"top\" style='height:100%' >";
        
        return headerStr;
        
    }
    
    
    
    
    /**Creates the footer for an HTML page
     */
    public static String createFooter() {
        
        // Finish the body row, start footer table row
        String footerStr = "</td></tr> <tr><td align=\"center\"> <TABLE width=\"60%\" " +
                "class=\"light_table\" align=\"center\">";
        
        // row2: email address
        footerStr += "<tr><td align=\"center\"><br>";
        
        footerStr += "<font size=\"small\"> comments to <a class='menulink' href=\"mailto:sambo@ida.liu.se\">"+
                "sambo@ida.liu.se </a></font></td> </tr>";
        
        // close the table
        footerStr += "</TABLE></td></tr>";
        
        
        // close the HTML document
        footerStr += "</body></html>";
        
        return footerStr;
    }
    
    
    
    /**
     * Presents the merged ontology
     *
     * @param file The name of the new ontology
     * @param language the num indicating the ontology language
     */
    public static String createFinished(String alignfile, String mergefile, int language)  {

       // Remove User Session From Database
       //RemoveUserSessionFromDb();

       String message = "<TABLE class=\"border_table\" width=\"45%\" align=\"center\" ><tr><td>";
         
       //the ontology file
        message += "<ul><li><a class='menulink' target=\"_blank\" href=\"ontologies/" + alignfile + "\">"+
                "The Alignment in OWL file </a><br>&nbsp;";
        
        //the ontology file
        /*
        message += "<li><a class='menulink' target=\"_blank\" href=\"ontologies/" + mergefile + "\">"+
                "The Merged Ontology in OWL file </a><br>&nbsp;";
         */
        //browse the ontology
        message += "<li><a  class='menulink' target=\"_blank\" href=\"Browse?ontofile="
                + mergefile + "&new\">Browse the Merged Ontology</a><br>&nbsp;";
      
        //reasoning the ontology
        message += "<li><a class='menulink' target=\"_blank\" href=\"JenaReason?&ontofile=" + mergefile 
                + "\">Reasoning the Merged Ontology</a>";        
         
        message += "</ul>";
        message += "</td></tr></TABLE>";
        
        //the file will be deleted after the web server is shut down.
        try {
            (new File(Constants.FILEHOME + alignfile)).deleteOnExit();
            (new File(Constants.FILEHOME + mergefile)).deleteOnExit();
        } catch (Exception e) {
        }
        
        
        return message;
    }

    /** To visulaize the ontology in a tree structure
     *
     * @param MOntology MOnto the ontology
     */
    public static String createClassTree(MOntology MOnto){
        
        String tableStr = "<TABLE width=\"90%\" class=\"light_table\">";
        tableStr += "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;<A href='Browse?all'>"
                + "<img src=\"img/search.gif\" border=\"0\" alt=\"open tree\">";
        
        String blank = "&nbsp;&nbsp;&nbsp;&nbsp;";
        for(Enumeration e = MOnto.roots().elements(); e.hasMoreElements();){
            
            MClass root = (MClass) e.nextElement(); 
            
            tableStr += "<tr><td>" + blank;
            if(root.getSubClasses().isEmpty() && root.getParts().isEmpty())
                tableStr += root.getLabel();
            else{ 
                tableStr += "<A href='Browse?classname="  + root.getId()
                + "' class='menulink'>" + root.getLabel();
                if(root.isDisplay())
                    tableStr += classTreeRecursion(MOnto, root, blank);
            }
        }
        
        tableStr += "</TABLE>";
        return tableStr;
        
    }
    
    //recursively create the tree structure
    private static String classTreeRecursion(MOntology MOnto, MClass pred, String blank){
        
        String str = "";
        blank += "&nbsp;&nbsp;&nbsp;&nbsp;";
        for( Enumeration e = pred.getSubClasses().elements(); e.hasMoreElements(); ){
            str += "<tr><td>" + blank + "<span class='isa'>i-</span>";
            
            MClass child = (MClass) e.nextElement();
            
            if(child.getSubClasses().isEmpty() && child.getParts().isEmpty())
                str += child.getLabel();
            else{
                str += "<a href='Browse?classname=" + child.getId()
                + "' class='menulink'>" + child.getLabel();
                if(child.isDisplay())
                    str += classTreeRecursion(MOnto, child, blank);
            }
        }
        
        for( Enumeration e = pred.getParts().elements(); e.hasMoreElements(); ){
            str += "<tr><td>" + blank + "<span class= 'part'>p-</span>";
            
            MClass child = (MClass) e.nextElement();
            
            if(child.getSubClasses().isEmpty() && child.getParts().isEmpty())
                str += child.getLabel();
            else{
                str += "<a href='Browse?classname="  + child.getId()
                + "' class='menulink'>" + child.getLabel();
                if(child.isDisplay())
                    str += classTreeRecursion(MOnto, child, blank);
            }
        }
        
        return str;
    }
   
    /**
     * Deletes uploaded files from XML
     * @param name filename of the ontology file
     */
    private static void deleteOldFile(String name) {
        File f;
        
        try {
            if (name != null) {
                f = new File(name);
                f.delete();
            }
            
        } catch (Exception e) {}
        
    }
}
