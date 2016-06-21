/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;

/**
 *
 * @author Rajaram
 */
public class ConvertIDAlignmentToLabelAlignment {
    
    private final String RECORD_FILE = "C:/RefAlign.txt";
    
    ArrayList rdfLines=new ArrayList();
    
    
    
    
    public void loadRefAlignment()
    {
        BufferedReader reader = null;
        try {
            
            
            
            
            String line;
            reader = new BufferedReader(new FileReader(RECORD_FILE));
            while ((line = reader.readLine()) != null) {
                
                
                if(line.contains("<entity1"))
                {
                
                for(Enumeration e1 = TestingConstants.ontManager.getMOnt(Constants.ONTOLOGY_1).getClasses().elements(); e1.hasMoreElements();){
            
                    Object c1 = e1.nextElement();
                    
                    
                    
                    String ID=((MElement)c1).getId();
                            
                            
                    if(line.contains(ID))
                    {
                        String label=((MElement)c1).getLabel();
                        line= "<entity1 "+label+" />";
                        System.out.println(line);
                        break;
                    }
                    
                    
                }
                
                }
                
                
                
                
                
                
                
                else if(line.contains("<entity2"))
                {
                
                for(Enumeration e2 = TestingConstants.ontManager.getMOnt(Constants.ONTOLOGY_2).getClasses().elements(); e2.hasMoreElements();){
            
                    Object c2 = e2.nextElement();
                    
                    
                    
                    String ID=((MElement)c2).getId();
                            
                            
                    if(line.contains(ID))
                    {
                        String label=((MElement)c2).getLabel();
                        line= "<entity2 "+label+" />";
                        System.out.println(line);
                        break;
                    }
                    
                    
                }            
                
                    
                
                }
                
                
                
                
                else if(line.contains("<relation"))
                {
                    System.out.println(line);
                    System.out.println();
                }
                
                
                
                
                
                
                
                //System.out.println(line);
            }
            reader.close();
            
            
            
         
            
            
        } 
        
        
        
        
        catch (IOException ex) {
            Logger.getLogger(ConvertIDAlignmentToLabelAlignment.class.getName()).log(Level.SEVERE, null, ex);
        }  finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ConvertIDAlignmentToLabelAlignment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
    public static void main(String args[])
    {
        
        
        ConvertIDAlignmentToLabelAlignment test= new ConvertIDAlignmentToLabelAlignment();
        
        test.loadRefAlignment();
        
    }
    
}
