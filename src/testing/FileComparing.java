/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author rajka62
 */
public class FileComparing {
    
    
    public ArrayList<String> openFile(String filename)
	{
            ArrayList<String> lines=new ArrayList();
		try {
                        //FileInputStream file= new FileInputStream("F:/RA.rdf");
			
                        FileInputStream file= new FileInputStream(filename);
			
			DataInputStream in = new DataInputStream(file);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  String [] entity=new String[2];
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
			  
                              lines.add(strLine);
				}
			in.close();
			
			  }
                 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
                return lines;
		
	}
    
    
    
    
    public static void main(String args[])
    {
        FileComparing test= new FileComparing();
        
        
        String file1="C:/Users/rajka62/Desktop/ra2011.rdf";
        
        String file2="C:/Users/rajka62/Desktop/ra2012b.rdf";
        
        
        ArrayList<String> input1=test.openFile(file2);
        ArrayList<String> input2=test.openFile(file1);
        
         ArrayList<String> mapping = null;
        
        
        boolean addLine = false;
        for(String s:input1)
        {
            if(s.contains("<map")) {                
                mapping = new ArrayList<String>();                
                addLine = true;                                
            }
            
            if(addLine) {
                mapping.add(s);
            }
            
            if(s.contains("</map")) { 
                addLine = false;
                
                for (String mapp:mapping) {
                    if(!input2.contains(mapp)) {
                        
                        
                        for (String print:mapping) {
                            System.out.println(print);
                        }
                        
                        break;
                    }
                }
                
                
            }
            
        }
        
        
        System.out.println("Done");
    }
    
}
