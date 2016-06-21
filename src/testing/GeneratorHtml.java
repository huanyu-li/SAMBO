/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.session.Commons;

/**
 *
 * @author Rajaram
 */
public class GeneratorHtml {
    
    
    
    
    public static void HTML(String Type, ArrayList<String> data)
    {
        ArrayList<String> HtmlLine=new ArrayList();
        System.out.println("<HTML><BODY><table border='1'><tr>");
                
        HtmlLine.add("<HTML><BODY><table border='1'><tr>");
        
        
        System.out.println("<td>S.No</td><td>Strategy</td><td>weights</td><td>combination</td><td>threshold</td><td>Precision+</td><td>Recall+</td><td>Precision-</td><td>Recall-</td><td>F-measure</td><td>Execution (E)</td><td>Recommendation Score(Round)</td><td>Recommendation Score(No Round)</td></tr>");
        HtmlLine.add("<td>S.No</td><td>Strategy</td><td>weights</td><td>combination</td><td>threshold</td><td>Precision+</td><td>Recall+</td><td>Precision-</td><td>Recall-</td><td>F-measure</td><td>Execution (E)</td><td>Recommendation Score(Round)</td><td>Recommendation Score(No Round)</td></tr>");
        
        
        
        for(String s:data)
        {
            String[] tabel_data=s.split("\\#");
            System.out.println("<tr>");
            HtmlLine.add("<tr>");
            
            for(String d:tabel_data){
            System.out.println("<td>"+d+"</td>");
            HtmlLine.add("<td>"+d+"</td>");
            }
            
            
            System.out.println("</tr>");
            HtmlLine.add("</tr>");
        }
        
        System.out.println("</table></HTML></BODY>");
        HtmlLine.add("</table></HTML></BODY>");
        
      //String FILE_NAME = "C:/Users/rajka62/Desktop/TESTING/Recommendation_"+Type+".html";
      
      String FILE_NAME = "F:/Recommendation_"+Type+".html";
        
        //String FILE_NAME = "/home/rajka62/RECOMM_USING_PRA/Recommendation_"+Type+".html";
        
        
        try {
            createFile(HtmlLine,FILE_NAME);
        } catch (IOException ex) {
            Logger.getLogger(GeneratorHtml.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    
    
    

// Create PRA_Oracle_File.txt file in same location where UMLS history file stored 
public static void createFile(ArrayList<String> HtmlLine, String FILE) throws IOException
{
    File f = new File(FILE);
    PrintWriter file_writer=null;
    if (f.exists()){
        f.delete();
            
                f.createNewFile();
            
    }
        
   
            file_writer = new PrintWriter(new BufferedWriter(new FileWriter(FILE, true)));
       
 
  
 for(String s:HtmlLine){
     file_writer.println(s);

 }
 file_writer.flush();
 file_writer.close();
    

}
    
    
}
