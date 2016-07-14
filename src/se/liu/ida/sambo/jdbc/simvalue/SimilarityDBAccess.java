/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;

/**
 *
 * @author huali50
 */
public class SimilarityDBAccess {  
     public void singleinsert(String sqlStatement,Connection conn) {
         
         Statement stmt = null;
         		
	try {
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            // It prevents statement to execute immediately.
            conn.setAutoCommit(false);
            stmt.addBatch(sqlStatement);                
            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            stmt.close();		
        } catch (Exception _e) {
            _e.printStackTrace();		
        }
     }
     public void multipleUpdate(ArrayList<String> sqlStatements,
             Connection conn) {
         
         Statement stmt = null;
         		
	try {
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            // It prevents statement to execute immediately.
            conn.setAutoCommit(false);
            // All statements in the list will be executed in a single batch.
            for(String statement: sqlStatements) {
                stmt.addBatch(statement);                
            }
            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            stmt.close();		
        } catch (Exception _e) {
            _e.printStackTrace();		
        }
     }
     public int getSimvalueId(String statement,Connection conn) {
                  
         Statement stmt = null;
	 ResultSet queryResult = null;                
         int opairid=-1;
		
	 try {
             stmt = conn.createStatement();
	     queryResult = stmt.executeQuery(statement);             
                        
             while (queryResult.next()) {
                 opairid = queryResult.getInt("id");                        
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return opairid;
     }
    public ArrayList getSimvalueViewIdandValue(String statement,Connection conn){
        ArrayList<String> mappable_concepts = new ArrayList<String>();
        Statement stmt = null;
	ResultSet queryResult = null;                              
        String data = null;
        boolean sqlreturn = false;
        try {
            stmt = conn.createStatement();
            sqlreturn = stmt.execute(statement);
            if(sqlreturn == true){
                queryResult = stmt.getResultSet();
                while (queryResult.next()) {
                    data = "";
                    data = data.concat(queryResult.getString("id")).concat(AlgoConstants.SEPERATOR).concat(queryResult.getString("simvalue"));
                    mappable_concepts.add(data);                        
                }
            }        
            
            stmt.close();
            queryResult.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        } 
        return mappable_concepts;
    }
}
