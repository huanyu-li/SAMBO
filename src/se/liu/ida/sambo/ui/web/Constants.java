/*
 * Constants.java
 */

package se.liu.ida.sambo.ui.web;

import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.testMClass;
/**The constants involve in creating web-based user interface
 *
 * @author  He Tan
 * @version
 */
public class Constants extends se.liu.ida.sambo.Merger.Constants{
    
    //the absolute pathname of the directory where ontologies are located.
    public static String FILEHOME;
    public static String SESSIONS;
    public static String SEGMENT;
    public static String SEGMENT_RA;
    public static String SEGMENT_SUGGESTIONS;
    public static String SESSIONS_SUGGESTIONS;
    public static String host = "ida.liu.se";
    public static String mailAddr = "sambo@ida.liu.se";

    
    /////////////////////////////////////////
    // type of the uploaded file           //
    /////////////////////////////////////////
    /**
     * constant value for a URL type upload
     */
    public static final int URL = -1;
    
    /**
     * constant value for file type upload
     */
    public static final int FILE = -2;
    
    /**
     * constant value indicating a file on the local server
     */
    public static final int ON_SERVER = -3;
    
    /**
     * constant value indicating a file on the local server
     */
    public static final int TEMP_ON_SERVER = -4;
    
    /**
     * constant value for unknown upload type or ontology format
     */
    public static final int UNK = 0;

    /*
     * constant value for generating no of segment pairs
     */
    public static final int NoOfSegmentPairs = 5;


    /*
     *List of ontology languages supported by the system
     *@see sambo.Merger.Constants
     **/
    public static final String[] languages =
    { null, "DAML", "OWL"};
    
    
    /**
     * session error message
     */
    public static final String SESSION_ERROR = "The Session Invalid, Please Restart.";

    
    /////////////////////////////////////////
    // Merging Mode                        //
    /////////////////////////////////////////
    /**
     * Indicates the manual mode
     */
    public static final String MODE_MANUAL = "m";
    
    /**
     * Indicates the suggetsion mode
     */
    public static final String MODE_SUGGESTION = "s";
    
    
    
    //////////////////////////
    // the merging steps    //
    //////////////////////////
    /**  Indicates the welcome page
     *@see sambo.Merger.Constants
     */
    public static final int WELCOME = 0;
    
    /** Indicates the start step in the merge process
     *@see sambo.Merger.Constants
     */
    public static final int STEP_UPLOAD = 1;
    
    /** Indicates the start page
     *@see sambo.Merger.Constants
     */
    public static final int STEP_START = 2;
    
    /** Indicates the slot merging step in the merge process
     *@see sambo.Merger.Constants
     */
    public static final int STEP_SLOT = 3;
    
    /** Indicates the class merging step in the merge process
     *@see sambo.Merger.Constants
     */
    public static final int STEP_CLASS = 4;
    
    
    /**Indicates the class merging step in the merge process
     *@see sambo.Merger.Constants
     */
    public static final int STEP_FINISH = 5;
    
    /**  Indicates the welcome page
     *@see sambo.Merger.Constants
     */
    public static final int REASON = 6;
    
    public static final int BROWSE = 7;
    
    /** Indicates the cancel page
     *@see sambo.Merger.Constants
     */
    public static final int CANCEL = 8;
    
    public static final int HELP = 9;
    
    
    public static final String[] headers = {
        "WELCOME",
                "UPLOAD ONTOLOGIES",
                "START",
                "ALIGNING SLOT",
                "ALIGNING CLASS",
                "FINISH",
                "REASONING",
                "ONTOLOGIES",
                "ERROR PAGE",
                "HELP"
    };
    
    /*
     *List of available colors for the ontologies
     **/
    public static final String[][] colorList =
    {
        {"#990000", "red.gif"},
        {"#000066", "dkblue.gif"},
        {"#006699", "ltblue.gif"},
        {"#FF9900", "orange.gif"}
    };
    
    
    public static String commentStr = "<table width=\"60%\" align=\"left\"><tr><br></tr><tr>"
            //comment
            + "<td><font size=2>comment on the mapping<br></font>"
            + "<TEXTAREA NAME=\"comment\" COLS=30 ROWS=2></TEXTAREA></td>"
            //newname
            + "<td align=\"right\" vlign=\"bottom\"><font size=2><br>new name for the mapping<br></font>"
            + "<INPUT type=text size=28 name=\"newmergename\" value=\"\"></td>"
            + "</tr></table>";    
     
    
    //JavaScript for opeing a new window
    public static final String JavaScript_OpenWindow = 
            "\n  <script language=\"JavaScript\"> \n"
            + "     function openwindow(webpage, title) {       \n"
            + "       window.open(webpage, title, 'scrollbars=yes, resizable=yes, left=200, top=200, width=700, height=300') \n"
            + "     } \n"
            + "  </script> \n";
 
    
    
    /**Shows a class in an ontology with its merging information
     *
     * @param MClass c the class
     * @param String color its color
     */
    public static String setNameWithIcon(MClass c, String color){
        
        String str = fontify(c.getLabel() , color);
        
        //set icon to present the node status
        if (c.getAlignElement() != null){
            
            if(c.getAlignName() != null)
                str += "&nbsp;(" + c.getAlignName() + ")";            
            else  str += "&nbsp;(" + c.getAlignElement().getLabel() + ")";
        }
        
        if(!c.getAlignSupers().isEmpty() || !c.getAlignSubs().isEmpty())
            str += "&nbsp;<img src=\"img/icon_relation.jpg\" valign=\"top\" border=\"0\">";
        
        
        return str;
    }
        public static String testsetNameWithIcon(testMClass c, String color){
        
        String str = fontify(c.getLabel() , color);
        
        //set icon to present the node status
        if (c.getAlignElement() != null){
            
            if(c.getAlignName() != null)
                str += "&nbsp;(" + c.getAlignName() + ")";            
            else  str += "&nbsp;(" + c.getAlignClass().getLabel() + ")";
        }
        
        if(!c.getAlignSupers().isEmpty() || !c.getAlignSubs().isEmpty())
            str += "&nbsp;<img src=\"img/icon_relation.jpg\" valign=\"top\" border=\"0\">";
        
        
        return str;
    }
    
    /** Creates font tags to change the text color around a message
     * @param String message the message
     * @param String color the color
     */
    public static String fontify(String message, String color) {
        
        return "<font color=\"" + color +"\">" + message + "</font>";
        
    }
    
    
    /** Creates a legend with the chosen ontology colors and which ontology
     * is represented by which color
     *
     *@param String ontoName1 the name of ontology-1
     *@param String ontoName2 the name of ontology-2
     *@param String color1 the color for the ontology-1
     *@param String color2 the color for the ontology-2
     */
    public static String createColorLegend(String ontoName1,
            String ontoName2,
            String color1,
            String color2) {
        
        String legendStr = "<TABLE  class=\"border_table\" align=\"center\"> <tr> <td>";
        
        String pic1 = "";
        String pic2 = "";
        for(int i = 0; i < Constants.colorList.length; i++) {
            
            if (color1.equals(Constants.colorList[i][0])) {
                pic1 = Constants.colorList[i][1];
            } else if (color2.equals(Constants.colorList[i][0])) {
                pic2 = Constants.colorList[i][1];
            }
        }
        
        return legendStr + "<img src=\"img/" + pic1 + "\" border=\"0\">&nbsp;"  + ontoName1
                + "</td> <td>" + "<img src=\"img/" +  pic2 +  "\" border=\"0\">&nbsp;" + ontoName2
                + "</td> </tr> </TABLE>";
    }
    
    
}
