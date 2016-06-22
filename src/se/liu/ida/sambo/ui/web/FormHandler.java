package se.liu.ida.sambo.ui.web;

import com.hp.hpl.jena.rdf.model.Resource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.MModel.MProperty;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.dao.PredefinedStrategiesDao;
import se.liu.ida.sambo.dto.PredefinedStrategies;
import se.liu.ida.sambo.factory.PredefinedStrategiesDaoFactory;
import se.liu.ida.sambo.jdbc.MappableConceptPairsDB;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.session.SessionManager;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.Suggestion;

/**
 * Creates various forms for the Request-Response interaction
 */

public class FormHandler{
    
    
//
//            UPLOAD FILE
//
//    
    
    /**
     * creates a HTML form for uploading files
     *
     * @param int type1 the upload type for the first ontology
     * @param int type2 the upload type for the second ontology
     *
     * @return a string containing the HTML representation of the form
     */

    private static String SessionID = "";
    public static String createFileUploadForm(int type1, int type2) {
        
        String 	fileform = "<FORM METHOD=POST ACTION=\"LoadFile\" ENCTYPE=\"multipart/form-data\">";
        fileform +=  "<TABLE width=\"90%\" border=\"0\" class=\"border_table\" align=\"center\">";
        
        //ontology1 file upload form
        fileform += uploadFileForm(type1, Constants.ONTOLOGY_1);
        
        //two blank rows
        fileform += "<tr></tr><tr></tr>";
        
        //ontology2 file upload form
        fileform += uploadFileForm(type2, Constants.ONTOLOGY_2);
        
        //row4: if two upload formats have been chosen
        //print the form for the new ontology file
        //and the button to upload files
        if ((type1 != Constants.UNK) && (type2 != Constants.UNK)){
            
            //two blank rows
            fileform += "<tr></tr><tr></tr>";
            
            fileform += "<tr> <td width=\"25%\" align=\"center\">"
                    + "<font class='classname'>New Ontology: </font></td>";
            
            fileform += "<td><TABLE><tr>";
            fileform += "<td > <INPUT TYPE=\"TEXT\" NAME=\"name3\" value=\"New Ontology\" size=\"20\"> </td>"
                    + "<td>&nbsp;&nbsp;" + makeButton("upload", "upload", "submit", "Upload") + "</td>";
            fileform += "</tr></TABLE></td>";
            
            fileform += "</tr>";
        }
        
        
        fileform += "</TABLE></FORM>";
        
        return fileform;
        
    }
    
    
//create the file-uploading form
    private static String uploadFileForm(int type, int onto){
        
        String fileform = "<tr><td width=\"25%\" align=\"center\">"
                + "<font class='classname'>Ontology "+  onto + " : </font></td>";
        
        fileform += "<td align=\"left\">"
                + "<TABLE><tr>" + uploadFileOption(type, onto) + "</tr>";
        
        // Print more options when an upload type has been chosen:
        if (type != Constants.UNK){
            
            fileform += "<tr><td width=\"25%\">Color:</td>";
            fileform += "<td>" + colorSelect("color"+onto, onto-1) + "</td></tr>";
        }
        
        return  fileform += "</TABLE></td>" + "</tr>";
    }
    
    
//first, select the uploading type.
    private static String uploadFileOption(int type, int filenum) {
        
        String filename = "FILE" + filenum;
        String str = "";
        
        if (type!=Constants.UNK){
            str += "<td><INPUT type=\"hidden\" name=\"type" + filenum + "\" value=\""+ type + "\">";
            str += "File :</td>";
        }
        
        str += "<td align=\"center\">";
        
        if (type == Constants.URL) {
            str += "<INPUT TYPE=\"text\" name=\""+filename+"\" size=\"50\">";
        } else if (type == Constants.ON_SERVER){
            str += createServerFileList(filenum)+"</td>";
        } else if (type == Constants.FILE){
            str += "<INPUT TYPE=\"file\" name=\""+filename+"\" SIZE=\"50\">";
        } else {
            // No upload type has been given
            str += createUploadTypeForm(filenum);
        }
        
        return str + "</td>";
    }
    
    
//create the upload type form
    private static String createUploadTypeForm(int filenum) {
        
        String typeStr = "type" + filenum;
        
        String formStr = "<TABLE width=\"100%\"><tr><td valign=\"top\">";
        
        formStr += "<INPUT type=\"hidden\" name=\"" + typeStr + "\" value=\"" +Constants.UNK + "\">";
        formStr += "<INPUT type=\"radio\" name=\"" + typeStr + "\" value=\"" + Constants.URL + "\">"
                + "Web Address</br>";
        formStr += "<INPUT type=\"radio\" name=\""+ typeStr + "\" value=\"" + Constants.FILE + "\">"
                + "Upload from Disk</br>";
        formStr += "<INPUT type=\"radio\" name=\"" + typeStr + "\" value=\"" + Constants.ON_SERVER + "\" checked>"
                +  "On the Server";
        
        formStr += "</td><td>&nbsp;&nbsp;&nbsp;"
                + makeButton("choose" + typeStr, "choose" + typeStr, "submit", "Select Upload Type");
        formStr += "</td></tr> </TABLE>";
        
        return formStr;
    }
    
    
    
    // get a list of the files in ontologies on the server
    private static String createServerFileList(int filenum) {
        
        File file_dir = new File( Constants.FILEHOME + Constants.languages[Constants.OWL]);
        File[] fileList = file_dir.listFiles();
        
        String fileStr = "FILE" + filenum;
        String selectStr = "<SELECT name=\"" + fileStr + "\">";
        
        
        for(int i = 0; i < fileList.length; i++) {
            File f = fileList[i];
            if(f.isFile() && !f.isHidden())
                selectStr += "<OPTION value=\"" + f.getName() + "\">" + f.getName();
        }
        
        selectStr += "</SELECT>";
        
        return selectStr;
    }
    
    
//?//
//            SLOT FORM
//
//    
    /**
     * creates a HTML form for presenting slot suggestions
     * @param pairSlot a pair of slot suggestions
     * @param settings general settings for the pages
     * @param sameSlotName indicate whether the pair of slots have the same name
     * @param sameSlotType indicate whether the pair of slots have the same type
     * @return a string containing the HTML representation of the form
     */
    public static String createSlotForm( Suggestion sug, SettingsInfo settings, String SessionId){
        
        
        String color1 = settings.getColor(Constants.ONTOLOGY_1);
        String color2 = settings.getColor(Constants.ONTOLOGY_2);
        
        String formStr = "<center> Mapping Candidate Details </center> <br>";
        
        // Outer table
        formStr += "<TABLE width=\"85%\" class=\"border_table\" align=\"center\">"
                + "<tr><td valign=\"top\" align=\"center\">";
        
        // Form table
        formStr += "<FORM method=POST action=\"Slot?sid="+SessionId+"\">"
                + "<TABLE width=\"80%\" align=\"center\">"
                + "<tr><td width=\"100%\" valign=\"top\" align=\"center\">";
        
        SessionID = SessionId;
        formStr += Constants.JavaScript_OpenWindow;

        //Added by MZK
        Commons.OWL_1 = settings.getName(Constants.ONTOLOGY_1);
        Commons.OWL_2 = settings.getName(Constants.ONTOLOGY_2);
        
        // While there are suggestions left, print them out as a suggestions table.
        if(!sug.getPair().isEmptyPair()){
            
            formStr += "<TABLE width=\"100%\" align=\"center\"> ";
            
            MProperty p1 = (MProperty) sug.getPair().getObject1();
            MProperty p2 = (MProperty) sug.getPair().getObject2();


            //row1: MOntology Names
            formStr += "<tr><td colspan = \"2\" align=\"center\"> <font class=\"classname\">"
                    + settings.getName(Constants.ONTOLOGY_1) + "</font></td>";
            formStr += "<td colspan=\"2\" align=\"center\"> <font class=\"classname\">"
                    + settings.getName(Constants.ONTOLOGY_2) + "</font></td> </tr>";
            
            //row2, row3: type and name
            formStr += "<tr><td width=\"20%\" bgcolor=\"#222222\" align=\"center\">"
                    + "<font color=\"#eeeeee\"> Type </font></td>";
            formStr += "<td width=\"30%\" bgcolor=\"#222222\" align=\"center\">"
                    +"<font color=\"#eeeeee\"> Name </font></td> ";
            
            formStr += "<td width=\"20%\" bgcolor=\"#222222\" align=\"center\">"
                    +"<font color=\"#eeeeee\"> Type </font></td>";
            formStr += "<td width=\"30%\" bgcolor=\"#222222\" align=\"center\">"
                    +"<font color=\"#eeeeee\"> Name </font></td></tr>";
            
            formStr += "<tr><td align=\"center\">" + Constants.fontify(p1.getType(), color1) + "</td>"
                    + "<td align=\"center\">" + Constants.fontify(p1.getLabel(), color1) + "</td>" ;
            
            formStr += "<td walign=\"center\">" +  Constants.fontify(p2.getType(), color2) + "</td>"
                    + "<td align=\"center\">" + Constants.fontify(p2.getLabel(), color2) + "</td></tr>";
            
            formStr += "</TABLE></td></tr>";
            
            
            //input a new name for the merged class
            if(p1.getType().equalsIgnoreCase(p2.getType()))
                formStr += " <tr><td>" + Constants.commentStr +  "</td></tr>";
            
            formStr += "<tr> <td align=\"center\"><br>";
            
            //if different type, block the "equiv" button
            if(!p1.getType().equalsIgnoreCase(p2.getType()))
                formStr += "<INPUT name=\"merge\" value=\"Equiv. Relations\" type=\"submit\" disabled>";
            else  formStr += makeButton("merge", "merge", "submit", "Accept an Equivalence Relation");
            
            formStr += "&nbsp;&nbsp;&nbsp;"
                    
                    + makeButton("skip", "skip", "submit", "Reject") + "<br>"
                    + "<hr noshade width=\"100%\">"
                    + makeButton("manual", "manual", "submit", "Align manually")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + makeButton("undo", "undo", "submit", "Undo") + "&nbsp;" + "<br><br> ";
            
            //the small windows to show the remaining suggestions and previously processed classes
            formStr += "<img src=\"img/suggest.gif\" border=\"0\">&nbsp;&nbsp;"
                    +"<span class=\"menulink\" onClick=\"openwindow('remaining_sugs.jsp', 'blank');\">"
                    + sug.getRemainingSug() + " Remaining Suggestions, " +  "</span> &nbsp;&nbsp;&nbsp;&nbsp;";
            
            //If there are no suggestions, only show the finish button
        } else {
            formStr += "<p> No Remaining Suggestions </p> ";
            
            formStr += makeButton("undo", "undo", "submit", "Undo") + "&nbsp;&nbsp;"
                    + makeButton("finalize", "finalize", "submit", "Finalize") + "<br>"
                    + "<hr noshade width=\"70%\">"
                    + makeButton("manual", "manual", "submit", "Align manually") + "<br><br>";
        }
        
        formStr += "<img src=\"img/history.gif\" border=\"0\">"
            + "<span class=\"menulink\" onClick=\"openwindow('history.jsp', 'blank');\"> History  </span> </center>";
        
        
        // Close table and form
        formStr += "</td></tr> </TABLE></FORM> ";
        
        // Close outer table
        formStr += "</td></tr></TABLE>";

        //Lock session here
        //formStr += createLockSessionForm();

        return formStr;
    }
    
    
    /**
     * creates a HTML form for presenting slot suggestions
     * @param MOntology ontology-1
     * @param MOntology  ontology-2
     * @param settings general settings for the pages
     * @param typeStr the information of the pair of slot to be merge with different type
     * @return a string containing the HTML representation of the form
     */
    public static String  createManualSlotForm(MOntology onto1, MOntology onto2,
            Suggestion sug,  SettingsInfo settings,String session_id){
        
        String color1 = settings.getColor(Constants.ONTOLOGY_1);
        String color2 = settings.getColor(Constants.ONTOLOGY_2);
        
        
        // Outer table
        String formStr = "<TABLE width=\"85%\" class=\"border_table\" align=\"center\">"
                +   "<tr><td width=\"100%\" valign=\"top\" align=\"center\">";
        
        //form and table
        formStr += "<FORM method=POST action=\"Slot?sid="+session_id+"\">" +
                "<TABLE width=\"90%\" align=\"center\">";
        
        //There are two cells in each one row in the suggestion table.
        // the left one is slot list from ontology-1
        // the right one is slot list from ontology-2

        //Added by MZK
        Commons.OWL_1 = settings.getName(Constants.ONTOLOGY_1);
        Commons.OWL_2 = settings.getName(Constants.ONTOLOGY_2);

        // row1: MOntology Names
        formStr += "<tr><td width=\"50%\" align=\"center\"> <font class=\"classname\">"
                + settings.getName(Constants.ONTOLOGY_1) + "</font></td>";
        formStr += "<td width=\"50%\" align=\"center\"> <font class=\"classname\">"
                + settings.getName(Constants.ONTOLOGY_2) + "</font></td> </tr>";
        
        
        //row2: print slot list
        formStr += "<tr><td width=\"50%\" align=\"center\"  valign = \"top\">"
                + createSlotList(onto1, Constants.ONTOLOGY_1, settings.getColor(Constants.ONTOLOGY_1)) + "</td>";
        formStr += "<td width=\"50%\" align=\"center\"  valign = \"top\">"
                + createSlotList(onto2, Constants.ONTOLOGY_2, settings.getColor(Constants.ONTOLOGY_2))+ "</td></tr>";
        
        
        //row3: different slot type
        if(sug.reset())
            formStr += "<tr><td  class=\"classname\" colspan=\"2\" ><br> They are Different Type Properties! </td></tr>";
        
        
        //input a new name for the merged class
        formStr += " <tr><td colspan=\"2\">" + Constants.commentStr + "</td></tr>";
        
        //row4: buttons
        formStr += "<tr> <td  colspan=\"2\"  align=\"center\"><br>"
                + makeButton("undo", "undo", "submit", "&lt&lt Undo") + "&nbsp&nbsp&nbsp"
                + makeButton("manualmerge", "manualmerge", "submit", "&equiv; Equiv. Relations")
                + "<hr noshade width=\"70%\">"
                + makeButton("suggestion", "suggestion", "submit", "Suggestion Align") + "<br><br>";
        
        formStr += Constants.JavaScript_OpenWindow;
        formStr += "<img src=\"img/history.gif\" border=\"0\">"
            + "<span class=\"menulink\" onClick=\"openwindow('history.jsp', 'blank');\"> History  </span></center>";
        
        formStr += "</td> </tr> ";
        
        // Close button table and form
        formStr += "</TABLE></FORM> ";
        
        
        // Close outer table
        formStr += "</td></tr> </TABLE>";
        return formStr;
    }
    
    
    private static String createSlotList(MOntology onto, int ontonum, String color){
        
        String formStr = "<div class=\"tableContainer\">";
        //print the slot list
        formStr += "<TABLE class=\"tree_table\">" ;
        
        for(Enumeration e = onto.getProperties().elements(); e.hasMoreElements();){
            
            MProperty slot = (MProperty) e.nextElement();
            
            formStr += "<tr><td nowrap class=\"tree_td\">";
            
            if (slot.getAlignElement() == null)
                formStr += "<INPUT name=\"manualslot" + ontonum + "\" type=\"radio\"" + "value=\""+ slot.getId() +"\">"
                        + "<INPUT name=\"manualslot" + ontonum + slot.getId() +  "type\" type=\"hidden\"" + "value=\""+ slot.getType() +"\">";
            
            else formStr += "&nbsp;<img src=\"img/icon_merge.jpg\" border=\"0\">&nbsp;";
            
            formStr += Constants.fontify(slot.getType() + ":" + slot.getLabel(), color);
            if(slot.getAlignName() != null)
                formStr +=  " (" + slot.getAlignName() + ")";
            
            formStr += "</td></tr>";
        }
        
        return formStr += "</TABLE>";
    }

    /**
     * Method 'getPredefinedstrategiesDao'
     *
     * @return PredefinedstrategiesDao
     */
    public static PredefinedStrategiesDao getPredefinedstrategiesDao()
    {
            return PredefinedStrategiesDaoFactory.create();
    }

    public static String createPredefinedStrategiesForm()
    {
            String formStr = "<center> <strong>List of Predefined Strategies <strong></center>";

            //out table
            formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                    + "<tr><td valign=\"top\">";

            //form and table
            formStr += "<FORM method=POST action=\"Class\">"
                    + "<TABLE border=\"0\" width=\"100%\">"
                    + "<tr><td> <strong>s.no </strong></td><td><strong> matchers </strong> </td><td><strong> weights </strong></td><td><strong>threshold</strong></td></tr>";

            formStr += Constants.JavaScript_OpenWindow;


            try {
                    PredefinedStrategiesDao _dao = getPredefinedstrategiesDao();
                    PredefinedStrategies _result[] = _dao.findAll();
                    for (int i=0; i<_result.length; i++ ) {
                        formStr +=  "<tr>" +
                                    "<td>" + _result[i].getId() + "</td>" +
                                    "<td>" + _result[i].getMatchers().trim() + "</td>" +
                                    "<td>(" + _result[i].getWeights().trim() + ")</td>" +
                                    "<td>" + _result[i].getThreshold() + "</td>"+
                                    "</tr>";
                    }
		}
		catch (Exception _e) {
			_e.printStackTrace();
		}
            /*if(sug.getPairList().size() > 0){

                //row1: the table with details about the classes
                formStr += "<tr><td width=\"100%\" valign=\"top\">"
                        + createClassInfo(sug.getPairList(),  color1, color2, settings) + "</td></tr>";

                //row2:
                formStr += "<tr><td valign=\"top\">"
                        + "<TABLE border=\"0\" width =\"80%\">";

                //comment and newname
                formStr += " <tr><td>"+ Constants.commentStr + "</td></tr> ";

                formStr += "</TABLE></td></tr>";

                //row3: the buttons and remaining suggestions
                formStr += "<tr> <td align=\"center\"><br>"
                        + makeButton("merge", "merge", "submit", "&equiv; Equiv. Concepts")
                        + makeButton("subclass", "subclass", "submit", "&le; Sub-Concept")
                        + makeButton("superclass", "superclass", "submit", "&ge; Super-Concept") + "&nbsp;&nbsp;&nbsp;"
                        + makeButton("undo", "undo", "submit", "&lt&lt Undo")
                        + makeButton("skip", "skip", "submit", "&gt&gt Skip to Next")

                        + "<br><hr noshade width=\"80%\">"

                        //the small windows to show the remaining suggestions
                        + "<img src=\"img/suggest.gif\" border=\"0\">&nbsp;"
                        + "<span class=\"menulink\" onClick=\"openwindow('remaining_sugs.jsp', 'blank');\">"
                        + sug.getRemainingSug() + " Remaining Suggestions " + "</span>&nbsp;"

                        + makeButton("remaining", "remaining", "submit", "Align Remaining")
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + makeButton("manual", "manual", "submit", "Align manually") + "</td></tr>";



            } else {
                formStr += "<tr><td align=\"center\"><p> No Remaining Suggestions </p>"
                        + makeButton("undo", "undo", "submit", "Undo") + "&nbsp;&nbsp;"
                        + makeButton("continue", "continue", "submit", "Finalize")+ "<br>"
                        + "<hr noshade width=\"70%\">"
                        + makeButton("manual", "manual", "submit", "Align manually") + "</td></tr>";
            }
            

            //the small windows to show the previously processed classes
            formStr += "<tr><td><br><table width=\"56%\" align=\"center\"><tr>"
                    //history link
                    + "<td width=\"22%\"><img src=\"img/history.gif\" border=\"0\">"
                    + "<span class=\"menulink\" onClick=\"openwindow('history.jsp', 'blank');\"> History  </span></td>"
                    //warning label
                    + "<td align=\"left\">"
                    + "<table width=\"100%\" class=\"border_table\">"
                    + "<tr><td class=\"warn\"> warning  ";

            if(warning != Constants.UNIQUE)
                formStr += ":&nbsp;&nbsp;the last action introduces a name conflict!";
            */
            formStr += " </table>"
                    + "</tr></table></td> </tr>";

            formStr += "<tr><td align=right><center>" + makeButton("recommendations", "recommendations", "submit", "Generate Recommendations") +"</center></td></tr>";

            // Close table and form
            formStr += "</TABLE></FORM>";

           
            // Close outer table
            formStr += "</td></tr></TABLE>";

           

            return formStr;
    }
    
//?//
//           CLASS FORM
//
//    
    /**
     * creates a HTML form for presenting class suggestions
     * @param classVector  a list of class suggestions
     * @param settings general settings for the pages
     * @param samenameStr the same name string when skip suggetions without new name
     * @param newname the new name for the merged class or one of skipped class
     * @param ontonum the ontology where the new name already exists
     * @return a string containing the HTML representation of the form
     */
    public static String createClassForm( SettingsInfo settings, Suggestion sug, int warning){
        
        String color1 = settings.getColor(Constants.ONTOLOGY_1);
        String color2 = settings.getColor(Constants.ONTOLOGY_2);
        
        String formStr = "<center> Mapping Candidate Details </center>";
        
        //out table
        formStr += "<br><TABLE  width=\"85%\" class =\"border_table\" align=\"center\">"
                + "<tr><td valign=\"top\">";
        
        //form and table
        formStr += "<FORM method=POST action=\"Class\">"
                + "<TABLE border=\"0\" width=\"100%\">";
        
        formStr += Constants.JavaScript_OpenWindow;
        
        if(sug.getPairList().size() > 0){
            
            //row1: the table with details about the classes
            formStr += "<tr><td width=\"100%\" valign=\"top\">"
                    + createClassInfo(sug.getPairList(),color1,color2,settings) + "</td></tr>";
            
            //row2:
            formStr += "<tr><td valign=\"top\">"
                    + "<TABLE border=\"0\" width =\"80%\">";
            
            //comment and newname
            formStr += " <tr><td>"+ Constants.commentStr + "</td></tr> ";
            
            formStr += "</TABLE></td></tr>";
            
            //row3: the buttons and remaining suggestions
            formStr += "<tr> <td align=\"center\"><br>"
                    + makeButton("merge", "merge", "submit", "Accept an Equivalence Relation")
                    + makeButton("subclass", "subclass", "submit", "Accept an Sub-Concept Relation")
                    + makeButton("superclass", "superclass", "submit", "Accept an Super-Concept Relation") + "&nbsp;&nbsp;&nbsp;"
                    
                    + makeButton("skip", "skip", "submit", "Reject")
                    
                    + "</tr><tr> <td width=\"100%\"><hr noshade=\"\" width=\"100%\"></td> <td width=\"100%\">"
                    + "<hr noshade=\"\" width=\"100%\"></td></tr>"
                    + "<tr><td align=\"center\">"
                    
                    //the small windows to show the remaining suggestions
                    + "<img src=\"img/suggest.gif\" border=\"0\">&nbsp;"
                    + "<span class=\"menulink\" onClick=\"openwindow('remaining_sugs.jsp', 'blank');\">"
                    + sug.getRemainingSug() + " Remaining Suggestions " + "</span>&nbsp;"
                    
                    + makeButton("remaining", "remaining", "submit", "Align Remaining")
                    + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + makeButton("manual", "manual", "submit", "Align manually") + "</td><td align=\"center\">"                 
                    + makeButton("undo", "undo", "submit", "Undo")
                    + "</td></tr>";
            
            
            
        } else {
            formStr += "<tr><td align=\"center\"><p> No Remaining Suggestions </p>"
                    + makeButton("undo", "undo", "submit", "Undo") + "&nbsp;&nbsp;"
                    + makeButton("continue", "continue", "submit", "Finalize")+ "<br>"
                    + "<hr noshade width=\"100%\">"
                    + makeButton("manual", "manual", "submit", "Align manually") + "</td></tr>";
        }
        
        
        //the small windows to show the previously processed classes        
        formStr += "<tr><td><br><table width=\"56%\" align=\"center\"><tr>"
                //history link
                + "<td width=\"22%\"><img src=\"img/history.gif\" border=\"0\">"
                + "<span class=\"menulink\" onClick=\"openwindow('history.jsp', 'blank');\"> History  </span></td>"
                //warning label
                + "<td align=\"left\">"
                + "<table width=\"100%\" class=\"border_table\">"
                + "<tr><td class=\"warn\"> warning  ";
        
        if(warning != Constants.UNIQUE)
            formStr += ":&nbsp;&nbsp;the last action introduces a name conflict!";
        
        formStr += " </td></tr></table>"
                + "</tr></table></td> </tr>";
        
        // Close table and form
        formStr += "</TABLE></FORM>";
        
        // Close outer table
        formStr += "</td></tr></TABLE>";
        
        //Lock session here
        //formStr += createLockSessionForm();
        return formStr;
    }
    
    
    
    private static String createClassInfo(Vector classNodes,
            String color1,
            String color2,
            SettingsInfo settings) {
        
        int width = 50;
        if(classNodes.size() > 1)  width = 48;
        
        String infoStr = "<TABLE width=\"100%\"  align=\"center\">";
        
        //row1: MOntology Names
        infoStr += "<tr>";
        
        if(classNodes.size() > 1)
            infoStr += "<td width=\"4%\"></td>";
        
        infoStr += "<td width=\"" + width + "%\"align=\"center\"> <font class=\"classname\">"
                + settings.getName(Constants.ONTOLOGY_1) + "</font></td>"
                + "<td width=\"" + width + "%\" align=\"center\"> <font class=\"classname\">"
                + settings.getName(Constants.ONTOLOGY_2) + "</font></td> </tr>";
        
        
        for(int i=0; i < classNodes.size(); i++){
            
            infoStr += "<tr>";
            
            if(classNodes.size() >1)
                infoStr +=  "<td width=\"4%\"><INPUT name=\"classPair\" type=\"radio\"" + "value=\"" + i + "\"></td>";
            
            infoStr += "<td width=\"" + width + "%\" valign=\"top\">"
                    + createClassTable((MClass) ((Pair) classNodes.get(i)).getObject1(), color1) + "</td>"
                    +  "<td width=\"" + width + "%\" valign=\"top\">"
                    + createClassTable((MClass) ((Pair) classNodes.get(i)).getObject2(), color2) + "</td></tr>";
        }
        
        
        return infoStr + "</Table>";
    }
    
    
    
    private static String createClassTable(MClass c, String color) {
        
        String tableStr = "<TABLE width=\"100%\" class=\"border_table\" valign=\"top\">";
        
        // Header for the table- the class name
        tableStr += "<tr><td colspan=\"2\"><font color=\"" + color
                + "\" class=\"classname\">" + c.getLabel() + "</td></tr>";
        
        tableStr += "<tr><td width=\"20%\" align=\"right\"class=\"classname\">"
                +"Id: </td><td align=\"left\">&nbsp;&nbsp;"
                + c.getId() + "</td></tr>";
        
        // Print definition
        tableStr += "<tr><td align=\"right\" class='classname'>"
                + "definition: </td><td align=\"left\">&nbsp;&nbsp;";
        if(c.getComment() != null)
            tableStr +=  c.getComment();
        tableStr += "</td> </tr>";
        
        //print synonyms
        tableStr += "<tr><td  align=\"right\" class='classname'>"
                + "Synonym: </font></td>";
        tableStr +="<td align=\"left\">" ;
        
        for(Enumeration e = c.getSynonyms().elements(); e.hasMoreElements();)
            tableStr += "&nbsp;&nbsp;" + (String)e.nextElement() + "<br>";
        tableStr += "</td></tr>";
        
        //print part-of
        tableStr += "<tr><td  align=\"right\" class='classname'>"
                + "Part of: </font></td>";
        tableStr +="<td align=\"left\">" ;
        for(Enumeration e = c.getPartOf().elements(); e.hasMoreElements();)
            tableStr += "&nbsp;&nbsp;" + ((MClass)e.nextElement()).getLabel() + "<br>";
        tableStr += "</td></tr>";
        
        tableStr += "</TABLE>";
        
        
        return tableStr;
    }
    
    /**
     * creates a HTML form for presenting class suggestions
     * @param onto1 the ontology-1
     * @param onto2 the ontology-2
     * @param settings general settings for the pages
     * @return a string containing the HTML representation of the form
     */
    public static String createManualClassForm(MOntology onto1, MOntology onto2, SettingsInfo settings, int warning){
        
        String servlet = "Class";
        
        String color1 = settings.getColor(Constants.ONTOLOGY_1);
        String color2 = settings.getColor(Constants.ONTOLOGY_2);
        
        String formStr = "<center> Mapping Candidate Details </center>";
        
        //out table
        formStr += "<br><TABLE  width=\"90%\" class =\"border_table\" align=\"center\">" +
                "<tr><td valign=\"top\">";
        //form and table
        formStr += "<FORM method=POST action=\"Class\">"
                + "<TABLE width=\"100%\">";
        
        //There are two cells in each one row in the suggestion table.
        // the left one is slot list from ontology-1
        // the right one is slot list from ontology-2
        
        // row1: MOntology Names
        formStr += "<tr><td width=\"50%\" align=\"center\">"
                + "<a href='" + servlet + "?allOne' class='menulink'>"
                + settings.getName(Constants.ONTOLOGY_1) + "</a></td>";
        formStr += "<td width=\"50%\" align=\"center\">"
                + "<a href='" + servlet + "?allTwo' class='menulink'>"
                + settings.getName(Constants.ONTOLOGY_2) + "</a></td> </tr>";
        
        
        //row2: print class tree
        formStr += "<tr><td width=\"50%\" align=\"center\" valign=\"top\">"
                + createClassTree(onto1, Constants.ONTOLOGY_1, servlet,
                settings.getColor(Constants.ONTOLOGY_1), settings.getColor(Constants.ONTOLOGY_2)) + "</td>";
        formStr += "<td width=\"50%\" align=\"center\" valign=\"top\">"
                + createClassTree(onto2, Constants.ONTOLOGY_2, servlet,
                settings.getColor(Constants.ONTOLOGY_2), settings.getColor(Constants.ONTOLOGY_1))+ "</td></tr>";
        
        
        //row5: mapping comment and searching
        formStr += "<tr><td colspan=\"2\" width=\"90%\" align=\"center\"><table>";
        
        formStr += "<tr><td width=\"40%\"><font size=2>comment on the mapping<br>"
                + "<TEXTAREA NAME=\"comment\" COLS=30 ROWS=2></TEXTAREA></td>";
        
        formStr += "<td align=\"left\" valign=\"bottom\"><font size=2>Concept Name:&nbsp;&nbsp;"
                + "<INPUT type=text size=20 name=\"searchname\">" + "&nbsp;&nbsp;&nbsp;"
                + "in &nbsp;&nbsp;"
                + "<SELECT name=\"searchonto\">"
                + "<OPTION value=\"" + Constants.ONTOLOGY_1 + "\">" + settings.getName(Constants.ONTOLOGY_1)
                + "<OPTION value=\"" + Constants.ONTOLOGY_2 + "\">" + settings.getName(Constants.ONTOLOGY_2)
                + "</SELECT>&nbsp;&nbsp;"
                + makeButton("search", "search", "submit", "search")
                + "</font></td></tr>";
        
        formStr += "</table></td></tr> ";
        
        //row6: buttons
        formStr += "<tr> <td  colspan=\"2\"  align=\"center\"><br>";
        formStr += makeButton("undo", "undo", "submit", "&lt&lt Undo") + "&nbsp;&nbsp;&nbsp;"
                + makeButton("manualmerge", "manualmerge", "submit", "&equiv; Equiv. Concepts")
                + makeButton("manualsub", "manualsub", "submit", "&le; Sub-Concept")
                + makeButton("manualsuper", "manualsuper", "submit", "&ge; Super-Concept") + "<br>"
                + "<hr noshade width=\"70%\">"
                + makeButton("suggestion", "suggestion", "submit", "Suggestion Align")+ "</td></tr>";
        
        formStr += Constants.JavaScript_OpenWindow;
        
        //row7: history and warning
        formStr += "<tr><td colspan=\"2\"><table width=\"56%\" align=\"center\"><tr>"
                //history link
                + "<td width=\"22%\"><img src=\"img/history.gif\" border=\"0\">"
                + "<span class=\"menulink\" onClick=\"openwindow('history.jsp', 'blank');\"> History  </span></td>"
                //warning label
                + "<td align=\"left\">"
                + "<table width=\"100%\" class=\"border_table\">"
                + "<tr><td class=\"warn\"> warning  ";
        
        if(warning != Constants.UNIQUE)
            formStr += ":&nbsp;&nbsp;the last action introduces a name conflict!";
        
        formStr += " </td></tr></table>"
                + "</tr></table></td> </tr>";        
                
        // Close button table and form
        formStr += "</TABLE></FORM> ";
        
        // Close outer table
        formStr += "</td></tr> </TABLE>";
        return formStr;
    }
    
    
    private static String createClassTree(MOntology onto, int ontonum, String servlet, String color, String highcolor){
        
        String tableStr = "<div class=\"tableContainer\">";
        tableStr += "<TABLE class=\"tree_table\">";
        String thiscolor = color;
        
        String blank = "&nbsp;&nbsp;&nbsp;&nbsp;";
        for(Enumeration e = onto.roots().elements(); e.hasMoreElements();){
            
            MClass root = (MClass) e.nextElement();
            tableStr += "<tr><td nowrap class=\"tree_td\">" + blank;
            
            if(root.isHighlight()){
                thiscolor = highcolor;
                root.closeHighlight();
            }
            
            if(root.getAlignElement() == null)
                tableStr += "<INPUT name=\"manualclass" + ontonum + "\" type=\"radio\"" + "value=\""+ root.getId()  +"\">";
            else
                tableStr += "&nbsp;<img src=\"img/icon_merge.jpg\" border=\"0\">&nbsp;";
            
            
            if(root.getSubClasses().isEmpty() && root.getParts().isEmpty())
                tableStr += "<a title=\"" + createManualClassInfo(root) + "\" >"
                        + Constants.setNameWithIcon(root, thiscolor) + "</a>";
            else{
                tableStr += "<a href='" + servlet + "?classname" + ontonum + "=" + root.getId()
                + "' class='treelink' title=\"" + createManualClassInfo(root) + "\" >" + Constants.setNameWithIcon(root, thiscolor) + "</a>";
                if(root.isDisplay())
                    tableStr += classTreeRecursion(onto, ontonum, root, servlet, blank, color, highcolor);
            }
            
            
        }
        
        tableStr += "</TABLE>";
        return tableStr + "</div>";
    }
    
    
    private static String createManualClassInfo(MClass c){
        
        String Str = " ID     :  "  + c.getId()
        + "\n Label :  "  + c.getLabel() ;
        
        Str += "\n Synonym :  ";
        
        for(Enumeration e = c.getSynonyms().elements(); e.hasMoreElements();){
            
            Str += (String)e.nextElement() + "; ";
            if(e.hasMoreElements())
                Str += "\n &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        
        Str += "\n Definition :  ";
        
        if(c.getComment() != null)
            Str += c.getComment();
        
        
        return Str;
    }
    
    private static String classTreeRecursion(MOntology onto, int ontonum, MClass pred, String servlet, String blank, String color, String highcolor){
        
        String str = "";
        blank += "&nbsp;&nbsp;&nbsp;&nbsp;";
        String thiscolor;
        for( Enumeration e = pred.getSubClasses().elements(); e.hasMoreElements(); ){
            
            str +=  "<tr><td nowrap class=\"tree_td\">" + blank + "<span class='isa'>i-</span>";
            
            MClass child = (MClass) e.nextElement();
            
            thiscolor = color;
            if(child.isHighlight()){
                thiscolor = highcolor;
                child.closeHighlight();
            }
            
            if(child.getAlignElement() == null)
                str += "<INPUT name=\"manualclass" + ontonum + "\" type=\"radio\"" + "value=\""+ child.getId()  +"\">";
            else str += "&nbsp;<img src=\"img/icon_merge.jpg\" border=\"0\">&nbsp;";
            
            if(child.getSubClasses().isEmpty() && child.getParts().isEmpty())
                str +=  "<a title=\"" + createManualClassInfo(child) + "\" >"
                        + Constants.setNameWithIcon(child, thiscolor) + "</a>";
            else{
                str += "<a href='" + servlet + "?classname" + ontonum + "=" + child.getId()
                + "' class='treelink' title=\"" + createManualClassInfo(child) + "\">" + Constants.setNameWithIcon(child, thiscolor) + "</a>";
                if(child.isDisplay())
                    str += classTreeRecursion(onto, ontonum, child, servlet, blank, color, highcolor);
            }
        }
        
        for( Enumeration e = pred.getParts().elements(); e.hasMoreElements(); ){
            
            str += "<tr><td nowrap class=\"tree_td\">" + blank + "<span class= 'part'>p-</span>";
            
            MClass child = (MClass) e.nextElement();
            
            thiscolor = color;
            if(child.isHighlight()){
                thiscolor = highcolor;
                child.closeHighlight();
            }
            
            if(child.getAlignElement() == null)
                str += "<INPUT name=\"manualclass" + ontonum + "\" type=\"radio\"" + "value=\""+ child.getId()  +"\">";
            else str += "&nbsp;<img src=\"img/icon_merge.jpg\" border=\"0\">&nbsp;";
            
            if(child.getSubClasses().isEmpty() && child.getParts().isEmpty())
                str += "<a title=\"" + createManualClassInfo(child) + "\" >"
                        + Constants.setNameWithIcon(child, thiscolor) + "</a>";
            else{
                str += "<a href='" + servlet + "?classname" + ontonum + "=" + child.getId()
                + "' class='treelink' title=\"" + createManualClassInfo(child) + "\">" + Constants.setNameWithIcon(child, thiscolor) + "</a>";
                if(child.isDisplay())
                    str += classTreeRecursion(onto, ontonum, child, servlet, blank, color, highcolor);
            }
        }
        
        return str;
        
    }
    
    
    
    
    /**
     * creates a HTML form for starting the merge with the correct type
     * (Suggestion Merge or Manual Merge)
     * @param settings general settings for the merge
     * @return a string containing the HTML representation of the form
     */
    public static String createStartForm(SettingsInfo settings, int step) {
        
        String startform = Constants.JavaScript_OpenWindow; // Added by Rajaram
                
                startform += "<FORM method=GET action=\"Main\">" ;
                
                //
        if(step == Constants.STEP_SLOT)
        {
        
        startform += "<TABLE width=\"80%\" border=\"0\" class=\"border_table\" align=\"center\">" +
                "<tr><td align=\"center\"> Align ";
        
        if(step == Constants.STEP_SLOT){
            startform += "Relation";
            Commons.SESSION_TYPE = "Computation";//Added by MZK
        }else if(step == Constants.STEP_CLASS){
            startform += "Concept";
            Commons.SESSION_TYPE = "Validation";//Added by MZK
        }
            

        startform += " in " + Constants.fontify(settings.getName(Constants.ONTOLOGY_1),
                settings.getColor(Constants.ONTOLOGY_1))
                + " and " +
                Constants.fontify(settings.getName(Constants.ONTOLOGY_2),
                settings.getColor(Constants.ONTOLOGY_2));
        
        startform += "<tr><td><INPUT type=\"hidden\" name=\"step\"  value=\"" + step + "\">";
        
        
        
        startform += "<tr><td align=\"center\">";
        
        startform += "<table><tr>";
        startform += "<td><font class='classname'>matchers:&nbsp;</font>";
        startform += "<td>"+createMatcherForm(step);
        
        Commons.STEP_VALUE = step;//Added by MZK

        startform += "<td><font class='classname'>&nbsp;&nbsp;threshold:&nbsp;&nbsp;</font>"
                + "&nbsp;&nbsp;<INPUT type=\"text\" name=\"threshold\"  value=\"0.6\" size=\"2\" >";
        
        }
        
        
        
//        startform += "&nbsp;&nbsp;|&nbsp;&nbsp;<a href='#' onclick=\"javascript:window.open('EvaluateStrategy.jsp','EvaluateStrategy','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=900, height=300');\">Evaluate Strategy</a>";
        
        //startform += "<td>&nbsp;&nbsp;" + makeButton("start", "start", "submit", "Start");
        
        if(step == Constants.STEP_CLASS)        {
            //startform += "<br><br>&nbsp;&nbsp;" + makeButton("finish", "finish", "submit", "Finish");
            
            
            String ontname="Align Concept"+" in " + Constants.fontify(settings.getName(Constants.ONTOLOGY_1),
                settings.getColor(Constants.ONTOLOGY_1))
                + " and " +
                Constants.fontify(settings.getName(Constants.ONTOLOGY_2),
                settings.getColor(Constants.ONTOLOGY_2));
            
            
            
            startform += "<TABLE align=\"center\"><td>"+ontname+"</td></table>";
            
            
            
            startform += "<TABLE width=\"80%\" border=\"0\" class=\"border_table\" align=\"center\">" +
                "<tr><td align=\"center\">";
        
        
//            startform += "Concept";
            Commons.SESSION_TYPE = "Validation";//Added by MZK
        
            

//        startform += " in " + Constants.fontify(settings.getName(Constants.ONTOLOGY_1),
//                settings.getColor(Constants.ONTOLOGY_1))
//                + " and " +
//                Constants.fontify(settings.getName(Constants.ONTOLOGY_2),
//                settings.getColor(Constants.ONTOLOGY_2))+"</td></tr>";
//        
            startform += "<tr><td><INPUT type=\"hidden\" name=\"step\"  value=\"" + step + "\">"+"</td></tr>";
            
            
            startform += "<tr>";
            startform += "<td><table><tr><td align=\"center\"><table><tr><td><font class=\"classname\">matchers:&nbsp;</font></td>";
            startform += "<td nowrap>"+createMatcherForm(step)+"</td></tr></table></td></tr></table></td>";
        
        
            Commons.STEP_VALUE = step;//Added by MZK
        

startform +="<td nowrap align=\"center\" style=\"BORDER-LEFT: white solid\"><table><tr><td><font class=\"classname\">single threshold:</font></td>";

startform +="<td></td><td></td><td></td>";


startform +="<td><input type=\"text\" name=\"threshold\" value=\"0.6\" size=\"2\"></td>";

startform +="<td><input type=\"radio\" name=\"threshold_flag\" value=\"single\" checked=\"\"></td></tr><tr>";


startform +="<td nowrap><font class=\"classname\">double threshold:</font></td><td>upper</td>";
startform +="<td><input type=\"text\" name=\"double_threshold_upper\" value=\"0.6\" size=\"2\"></td><td>lower</td>";
startform +="<td><input type=\"text\" name=\"double_threshold_lower\" value=\"0.4\" size=\"2\"></td>";
startform +="<td><input type=\"radio\" name=\"threshold_flag\" value=\"double\"></td></tr></table></td>";

startform +="<td align=\"center\" style=\"BORDER-LEFT: white solid\"><table><tr>";
startform +="<td nowrap><font class=\"classname\">weighted-sum combination</font></td>";
startform +="<td><input type=\"radio\" name=\"combination\" value=\"weighted\" checked=\"\"></td></tr>";




startform +="<tr><td nowrap><font class=\"classname\">maximum-based combination</font></td>";
startform += "<td><input type=\"radio\" name=\"combination\" value=\"maximum\"></td></tr></table></td>";




startform +="<td align=\"center\" style=\"BORDER-LEFT: white solid\"><table><tr>"
        + "<td nowrap><font class=\"classname\">use preprocessed data</font></td>";



String ontologies=settings.getName(Constants.ONTOLOGY_1)+"#"+settings.getName(Constants.ONTOLOGY_2);
            
           
           if(IsMappableGrpAvailable(ontologies))
               startform += "<td><INPUT type='checkbox' name='mappableGrp' value='true'></td>";
           else
               startform += "<td><INPUT type='checkbox' name='mappableGrp' value='false' DISABLED></td>";

startform +="</tr></table></td></tr>";


startform +="<tr></tr><tr></tr>";



startform +="<tr><table  class=\"border_table\" align=\"center\">";


startform +="<td>&nbsp;&nbsp;<input name=\"start\" value=\"Start Computation    \" type=\"submit\"> </td>";


startform +="<td>&nbsp;&nbsp;<input name=\"finish\" value=\"Finish Computation   \" type=\"submit\" > </td>";


startform +="<td>&nbsp;&nbsp;<input type=\"button\"name=\"interrupt\" value=\"Interrupt Computation\" onClick=\"openwindow('Interrupt.jsp', 'blank');\"></td>";


startform +="<td></td><td></td><td></td>";
        
        
        
        
   String title="Enter at which pair you want to interrupt computation process(NOTE: No.of pairs generated = "+AlgoConstants.NO_OF_PAIRS+" )"; 
           
                
        
        

startform +="<td><font class=\"classname\">interrupt at:</font></td>";

startform +="<td><input type=\"text\" name=\"interuppt_at\" value=\"1000\" size=\"2\" title='"+title+"'></td>";

startform +="<td><input type=\"checkbox\" name=\"enable_interrupt\" value=\"true\"></td></table></tr>";
            
            
    
           
           
           
           
           
           
//           startform += "<br><br><font class='classname'>&nbsp;&nbsp;Do auto Pairs:&nbsp;</font>";

//           startform += "<INPUT type=\"text\" name=\"auto_align\" value=\"0\" size=\"2\" title='"+title+"'>";
           
           
            //
           
           
//            startform += "<br><br>&nbsp;&nbsp;" + "<input name='interrupt' type='button' content='Interrupt' onClick='openwindow('Interrupt.jsp', 'blank');' />";         
            
            
          
             
             //
              
            
        }
        
        
        
        //Added By Rajaram
        if(step == Constants.STEP_SLOT){
            startform += "<td>&nbsp;&nbsp;" + makeButton("start", "start", "submit", "Start");
            startform += "</table></TABLE> </FORM>";  
        }
        else if(step == Constants.STEP_CLASS){
            startform += "<td>&nbsp;&nbsp;" + makeButton("finish-1", "finish", "submit", "Finish");
            startform += "</table></TABLE> </FORM>"; 
        }
        else
            startform += "</table></FORM>";  
        /*
        if(step == Constants.STEP_CLASS)
        {
            startform += "<br><br>&nbsp;&nbsp;" + makeButton("finish-1", "finish", "submit", "Finish");
            startform += "<br><br>&nbsp;&nbsp;<span class=\"menulink\" onClick=\"openwindow('Interrupt.jsp', 'blank');\">"
                    +" <font color='red'>Interrupt Computation </font>" + "</span>&nbsp;";
        }
        */

        
        
        
        
        
        
       return startform;
    }
    
    
    
    
    
    
    public static boolean IsMappableGrpAvailable(String ontologies) 
    {
        boolean availablity=false;
        try {
            Connection conn=ResourceManager.getConnection();            
            MappableConceptPairsDB db = new MappableConceptPairsDB(conn);
            availablity = db.isConceptPairsAvailable(ontologies);
            ResourceManager.close(conn);
        } catch (SQLException ex) {
            Logger.getLogger(FormHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        return availablity;
    }
    
    

    /**
     * creates a HTML form for starting the merge with the correct type
     * (Suggestion Merge or Manual Merge)
     * @param settings general settings for the merge
     * @return a string containing the HTML representation of the form
     */
    public static String createRecommendationForm(SettingsInfo settings, int step) {
        String startform = "<FORM method=GET action=\"Main\">" ;

        startform += "<TABLE width=\"80%\" border=\"0\" class=\"border_table\" align=\"center\">";

        if(step == Constants.STEP_SLOT){
            File file = new File(Commons.DATA_PATH+"RelationRecommendations.xml");
            boolean exists = file.exists();
            if (!exists) {
                startform += "<tr><td align=\"center\"> No Recommendations!</td></tr>";
            }else{

                startform += "<tr><td align=\"center\"> <b>Computation Recommendations</b>";
                startform += "<tr><td align=\"center\"> Align Relation";
                Commons.SESSION_TYPE = "Computation";

                startform += " in " + Constants.fontify(settings.getName(Constants.ONTOLOGY_1),
                settings.getColor(Constants.ONTOLOGY_1))
                + " and " +
                Constants.fontify(settings.getName(Constants.ONTOLOGY_2),
                settings.getColor(Constants.ONTOLOGY_2));

                startform += "<tr><td><INPUT type=\"hidden\" name=\"step\"  value=\"" + step + "\">";
                startform += "<tr><td align=\"center\">";
                startform += "<table><tr>";
                startform += "<td><font class='classname'>matchers:&nbsp;</font>";
                startform += "<td>"+ createRecommendationMatcherForm(step);
                Commons.STEP_VALUE = step;//Added by MZK
                startform += "<td><font class='classname'>&nbsp;&nbsp;threshold:&nbsp;&nbsp;</font>"
                + "<INPUT type=\"text\" name=\"threshold\"  value=\""+Commons.RecommendedThresholdValue.trim()+"\" size=\"2\" >";

                startform += "<td>&nbsp;&nbsp;" + makeButton("start", "start", "submit", "Start");
                startform += "</table>";
            }
        }else if(step == Constants.STEP_CLASS){
            File file = new File(Commons.DATA_PATH+"ConceptRecommendations.xml");
            boolean exists = file.exists();
            if (!exists) {
                //startform += "<tr><td align=\"center\"> No Recommendations!</td></tr></table>";
                startform += "<tr><td align=\"center\"><input type=button name='predefinedStrategies' value='Use recommendations from predefined strategies' onclick=\"javascript:window.open('strategies.jsp','Predefined Alignment Strategies','toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=600, height=400');\" /></td></tr></table>";
            }else{
                startform += "<tr><td align=\"center\"> <b>Computation Recommendations</b>";
                startform += "<tr><td align=\"center\"> Align Concept";
                Commons.SESSION_TYPE = "Validation";//Added by MZK

                startform += " in " + Constants.fontify(settings.getName(Constants.ONTOLOGY_1),
                settings.getColor(Constants.ONTOLOGY_1))
                + " and " +
                Constants.fontify(settings.getName(Constants.ONTOLOGY_2),
                settings.getColor(Constants.ONTOLOGY_2));

                startform += "<tr><td><INPUT type=\"hidden\" name=\"step\"  value=\"" + step + "\">";
                startform += "<tr><td align=\"center\">";
                startform += "<table><tr>";
                startform += "<td><font class='classname'>matchers:&nbsp;</font>";
                startform += "<td>"+ createRecommendationMatcherForm(step);
                Commons.STEP_VALUE = step;//Added by MZK
                startform += "<td><font class='classname'>&nbsp;&nbsp;threshold:&nbsp;&nbsp;</font>"
                + "<INPUT type=\"text\" name=\"threshold\"  value=\""+Commons.RecommendedThresholdValue.trim()+"\" size=\"2\" >";

                startform += "<td>&nbsp;&nbsp;" + makeButton("start", "start", "submit", "Start");
                startform += "<br><br>&nbsp;&nbsp;" + makeButton("finish", "finish", "submit", "Finish");
                startform += "</table>";
            }
        }

        startform += "</TABLE> </FORM>";
        return startform;
    }

    
    /**
     * creates a HTML form for uploading a file to reasoning
     *
     * @param type the upload type for the ontology
     * @param ontofile the ontology file
     * @param reasonServer the reason server
     *
     * @return a string containing the HTML representation of the form
     */
    public static String createReasonUploadForm(int type, String ontofile, String reasoner) {
        
        String fileform =
                "<FORM METHOD=POST ACTION=\"JenaReason\" ENCTYPE=\"multipart/form-data\">";
        fileform +=  "<TABLE width=\"90%\" border=\"0\" class=\"border_table\" align=\"center\">";
        
        // two blank rows
        fileform += "<tr></tr><tr></tr>";
        
        // file form
        fileform += "<tr><td width=\"25%\" align=\"right\"><font class='classname'>Ontology File: </font></td>";
        fileform += "<td>";
        
        fileform += "<TABLE><tr>";
        fileform += "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>";
        fileform += uploadFileOption(type, Constants.ONTOLOGY_NEW);
        
        //if type is known, to get file
        if (type != Constants.UNK){
            
            fileform += "<td >&nbsp;&nbsp;";
            fileform += makeButton("reason", "reason", "submit", "Reasoning");
            fileform+="</td>";
        }
        fileform += "</tr></TABLE>";
        fileform += "</td></tr>";
        
        
        // reasoner form
        fileform +=  "<tr><td width=\"25%\" align=\"right\"><font class='classname'>Reasoner: </font></td>";
        fileform +=  "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        
        if(reasoner !=null){
            
            fileform +=  "<INPUT type=\"hidden\" name=\"reasoner\""
                    + "value=\"" + reasoner + "\" size=\"30\">"
                    + reasoner ;
        }else{
            
            fileform += "<INPUT type=\"text\" name=\"reasoner\""
                    + "value=\"" +Constants.defaultReasoner+ "\" size=\"30\">";
        }
        fileform += "</td></tr>";
        
        fileform += "</TABLE></FORM>";
        
        return fileform;
        
    }
    
    
    
    /**print the reasoning result
     * @param cycles the cycles found in the ontology
     * @param unSatisfiable the unsatisfiable class found in the ontology
     * @param subsumption the computed subsumption created by the reasoner
     * @param classifiedFile the url of the classsified ontology file
     */
    public static String reasonResultForm(Vector incon, Vector cycles, Vector subsumption){
        
        String formStr ="<TABLE width=\"90%\" border=\"0\" class=\"border_table\" align=\"center\">";
        //  formStr += "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;<B>Reasoning Report</B></td></tr>";
        
        formStr +="<tr><td><TABLE width=\"90%\" align=\"center\">";
        
        formStr += "<tr><td width=\"15%\" align=\"right\" class='classname'>Cycle :</td>";
        
        if(cycles.isEmpty()){
            formStr += "<td>&nbsp;&nbsp; No </td></tr>";
            
        }else{
            
            formStr +="<td><Table>";
            
            for(Enumeration e1 = cycles.elements(); e1.hasMoreElements();){
                formStr +="<tr><td>&nbsp;&nbsp;( ";
                for(Iterator it = ((Set)e1.nextElement()).iterator(); it.hasNext();)
                    formStr +=  ((Resource)it.next()).getURI() + "; \n &nbsp;&nbsp;";
                formStr+=" )</td></tr>";
            }
            
            formStr+="</Table></td></tr>";
            
        }
        
        
        formStr += "<tr></tr>";
        
        formStr += "<tr><td class='classname' align=\"right\" >Inconsistence :</td>";
        
        if(incon.isEmpty()){
            formStr += "<td>&nbsp;&nbsp; No </td></tr>";
            
        }else{
            
            formStr +="<td><Table>";
            
            for(Enumeration e = incon.elements(); e.hasMoreElements();)
                formStr +="<tr><td>&nbsp;&nbsp;"
                        +  ((Resource)e.nextElement()).getURI() + "; " + "</td></tr>";
            formStr+="</TABLE></td></tr>";
            
            
        }
        
        formStr += "<tr><td class='classname' align=\"right\" >Subsumption :</td>";
        
        if(subsumption.isEmpty()){
            formStr += "<td>&nbsp;&nbsp; No </td></tr>";
            
        }else{
            
            formStr +="<td><Table>";
            
            for(Enumeration e = subsumption.elements(); e.hasMoreElements();){
                Pair p = (Pair) e.nextElement();
                formStr += "<tr><td>&nbsp;&nbsp;"
                        + ((Resource) p.getObject2()).getURI() +  "<br> &nbsp;&nbsp;&nbsp;&nbsp; -> "
                        + ((Resource) p.getObject1()).getURI()
                        + "</td></tr>";
            }
            
            
            formStr+="</TABLE></td></tr>";
            
            
        }
        
        formStr += "</TABLE></td></tr>";
        
        formStr += "</TABLE><tr><td>";
        
        return formStr;
        
    }
    
    
    
    private static String createMatcherForm(int step) {

        String selectStr = "";

        // Get a list of matcher provided by the system
        if(step == Constants.STEP_SLOT){
        
            selectStr = "<input type=\"text\" name=\"weight4\" value=\"1.0\" size=\"2\">"
                + "<input type=checkbox name =\"" + Constants.singleMatchers[4]
                + "\" checked>" + Constants.singleMatchers[4];
        }
        
        
        
        
        //int num = Constants.singleMatchers.length-1;
        
        //To unblock Learning Matcher
        
        int num = Constants.singleMatchers.length;
        
        if(step == Constants.STEP_CLASS){
            
            
            
            
            
            for(int i = 0; i < num; i++) {
                
                
                if(i==AlgoConstants.HIERARCHY)
                    {
                    selectStr += "<br><input type=\"text\" name=\"weight" + i + "\" value=\"1.0\" size=\"2\" DISABLED>"
                        + "<input type=checkbox name =\"" + Constants.singleMatchers[i]
                        + "\" >" + Constants.singleMatchers[i];
                    }
                
                    else
                    {
                selectStr += "<br><input type=\"text\" name=\"weight" + i + "\" value=\"1.0\" size=\"2\">"
                        + "<input type=checkbox name =\"" + Constants.singleMatchers[i]
                        + "\" >" + Constants.singleMatchers[i];
                    }
                
            }
            
//            //block the learning matcher
//            selectStr += "<br><input type=\"text\" name=\"weight" + num
//                    + "\" value=\"1.0\" size=\"2\" DISABLED >"
//                    + "<input type=checkbox name =\"" + Constants.singleMatchers[num]
//                    + "\"  DISABLED >" + Constants.singleMatchers[num];
            
            
        }
        
        return selectStr;
    }

    //Added By ME
    private static String createRecommendationMatcherForm(int step) {
        SessionManager sesman = new SessionManager();

        String selectStr = new String();
        if(step == Constants.STEP_SLOT){
            sesman.loadRecommendationsFromXML(Constants.STEP_SLOT);
            selectStr = "<input type=\"text\" name=\"weight0\" value=\"1.0\" size=\"2\">"
                + "<input type=checkbox name =\"" + Constants.singleMatchers[4]
                + "\" checked>" + Constants.singleMatchers[4];
        }else if(step == Constants.STEP_CLASS){
            sesman.loadRecommendationsFromXML(Constants.STEP_CLASS);
            int num = Constants.singleMatchers.length-1;
            int len = Commons.strRecommendedMatchers.size();
            int wet = Commons.strRecommendedWeight.size();

            for(int i = 0; i < num; i++) {
                if(i<wet){
                    selectStr += "<br><input type=\"text\" name=\"weight" + i + "\" value=\""+ Commons.strRecommendedWeight.get(i) +"\" size=\"2\">";
                }
                if(i<len){
                    selectStr += "<input type=checkbox name =\"" + Commons.strRecommendedMatchers.get(i)
                        + "\" checked>" + Commons.strRecommendedMatchers.get(i);
                }else{
                    selectStr += "<input type=checkbox name =\"" + Commons.Matchers_Available[i]
                        + "\" >" + Commons.Matchers_Available[i];
                }
                    
            }

            //block the learning matcher
            selectStr += "<br><input type=\"text\" name=\"weight" + num
                    + "\" value=\"1.0\" size=\"2\" DISABLED >"
                    + "<input type=checkbox name =\"" + Constants.singleMatchers[num]
                    + "\"  DISABLED >" + Constants.singleMatchers[num];

        }

        return selectStr;
    }
    
// make a button - content can be a name or an image for a button
    private static String makeButton(String name,
            String value,
            String type,
            String content) {
        
        return "<INPUT name=\"" + name + "\" value=\"" +  content + "\" type=\""+ type + "\">";
        
        // Works only with Mozilla and IE
        //return "<BUTTON name=\"" + name + "\" value=\"" +
        //    value + "\" type=\""+ type + "\">" + content +
        //    "</BUTTON>";
    }
    
    
    
    
    private static String colorSelect(String fieldname, int ontoNum) {
        
        
        String selectStr = "<TABLE><tr><td>";
        
        
        for (int i = 0; i < Constants.colorList.length; i++) {
            
            if (i == ontoNum) {
                
                selectStr += "<INPUT type=\"radio\" name=\""+fieldname+"\"" +
                        " checked value=\"" + Constants.colorList[i][0] +"\">";
            } else {
                selectStr += "<INPUT type=\"radio\" name=\""+fieldname+"\"" +
                        " value=\"" + Constants.colorList[i][0] +"\">";
            }
            
            // Color box
            selectStr +=  "<img src=\"img/" + Constants.colorList[i][1] + "\" " +  "border=\"0\">";
        }
        
        selectStr += "</td></tr></TABLE>";
        
        return selectStr;
        
    }

    private static String createTypeForm(String t1, String t2) {
        
        String formStr = "<tr><td align=\"left\"><TABLE width=\"80%\"><tr> <td width=\"40%\" align=\"center\">"+
                "Choose the type for aligning:</td> <td  align=\"left\">";
        formStr += "<INPUT type=\"radio\" name=\"type\" value=\"" + Constants.ONTOLOGY_1 + "\">" + t1 + " </br>";
        formStr += "<INPUT type=\"radio\" name=\"type\" value=\"" + Constants.ONTOLOGY_2 + "\">" + t2 ;
        formStr += "</td></tr> </TABLE></td></tr>";
        
        return formStr;
    }

}
