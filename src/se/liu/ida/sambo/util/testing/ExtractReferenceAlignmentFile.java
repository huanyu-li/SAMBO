/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util.testing;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;

/**
 * <p>
 * Extract mappings from the OAEI's reference alignment RDF file.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class ExtractReferenceAlignmentFile {
    /**
     * Mappings.
     */
    private ArrayList<String> mappings = new ArrayList<String>();
    /**
     * Reference alignment (RDF file) path.
     */
    private static final String filePath = "C:/Users/rajka62/Desktop/RA.rdf";
//    private static final String filePath = "/data/sambo/RA.rdf";   
    
    /**
     * Extract reference alignment from the RDF File.
     * 
     * @return List of mappings.
     */
    public ArrayList<String> getReferenceAlignment() {
        openAndExactFile();
        return mappings;
    }
    
    /**
     * Open the RDF file and extract mappings.
     */
    private void openAndExactFile() {
        try {
            FileInputStream raFile = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(raFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
	    String [] entity = new String[2];
            
            //Read File Line By Line
	    while ((strLine = br.readLine()) != null)   {
                // Onto1 concept
                if (strLine.contains("<entity1")) {
                    //System.out.println (strLine);
	            entity[0] = strLine;
		}
                // Onto2 concept
                else if (strLine.contains("<entity2")) {
                    //System.out.println (strLine);
		    entity[1] = strLine;
                    extractDataFromXMLTag(entity);				
                }
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {
            e.printStackTrace();		
        }
    }
    
    /**
     * Extract concept ID from the XML element.
     * 
     * @param lines      XML lines. 
     */
    private void extractDataFromXMLTag(String[] lines) {
        String entity1,entity2;
        String[] splitData;
        
        // Extracting concept1 ID
        splitData = lines[0].split("#");
	entity1 = splitData[1];		
	splitData = entity1.split("\"");		
	entity1 = splitData[0];
        
        // Extracting concept2 ID
	splitData = lines[1].split("#");		
	entity2 = splitData[1];		
	splitData = entity2.split("\"");				
	entity2 = splitData[0];
        
    mappings.add(entity1+AlgoConstants.SEPERATOR+entity2);
    System.out.println(entity1+AlgoConstants.SEPERATOR+entity2);
    }    
}
