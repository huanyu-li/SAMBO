/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.umlsOffline;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.UMLSQueryConstants;

/**
 * Check the relations between the two given terms.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class UMLSRelationCheck {
    
    /**
     * To query the UMLS offline server.
     */
    private QueryCUIDsoffline umlsAccess = null;
    /**
     * SQL connection.
     */
    private Connection sqlConn = null;
    
    public UMLSRelationCheck() {
        try {
            
            sqlConn = UMLSOfflineResourceManager.getConnection();
            umlsAccess = new QueryCUIDsoffline(sqlConn);
        } catch (SQLException ex) {
            Logger.getLogger(UMLSRelationCheck.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Check if the two given terms are in a relation.
     * 
     * @param term1
     * @param term2
     * @return 
     */
    public boolean hasRelation(String term1, String term2) {
        
        List<String> term1CUIs = umlsAccess.getCUIDs(term1, 
                 UMLSQueryConstants.NORMALIZED_STRING_SEARCH);
        
        List<String> term2CUIs = umlsAccess.getCUIDs(term2, 
                UMLSQueryConstants.NORMALIZED_STRING_SEARCH);         
         
         if(term1CUIs.isEmpty() || term2CUIs.isEmpty()) {
             return false;
         }
         else {
             
             for (String CUID1:term1CUIs) {
                 
                 for(String CUID2:term2CUIs) {
                     
                     if (!CUID1.equalsIgnoreCase(CUID2)) {
                         
                         List<String> relation = umlsAccess.queryRelation(CUID1, 
                                 CUID2);                         
                         if(hasPartOf(relation)) {
                             return true;
                         }
                     }
                 }
             } 
             return false;
         }
    }
    
    /**
     * Checks if the two given terms are in a part of relation.
     * 
     * @param relation  List of relations between the two given terms.
     * 
     * @return          True/False. 
     */
    private boolean hasPartOf(List<String> relation) {
        
        for(String rel:relation) {
            
            if(rel !=null) {
                
                if(rel.contains("part")) {
                    return true;
                }
            }
        }        
        return false;
    }
    
    public static void main(String args[]) {
        
        UMLSRelationCheck test = new UMLSRelationCheck();
        System.out.println(test.hasRelation("pelvic girdle bone", 
                "pelvic bone"));
    }
}
