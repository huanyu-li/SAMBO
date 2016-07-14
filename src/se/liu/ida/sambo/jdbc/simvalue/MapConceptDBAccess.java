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
public class MapConceptDBAccess {

    
    public void singleinsert(String sqlStatement,Connection conn) {
         
         Statement stmt = null;
         		
	try {
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
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
    public int getCPairId(String statement,  Connection conn) {
                  
         Statement stmt = null;
	 ResultSet queryResult = null;                
         int cpairid=-1;
		
	 try {
             stmt = conn.createStatement();
	     queryResult = stmt.executeQuery(statement);             
                        
             while (queryResult.next()) {
                 cpairid = queryResult.getInt("mcid");                        
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return cpairid;
     }
    public String getconcepts(String statement,Connection conn){
        Statement stmt = null;
	 ResultSet queryResult = null;                
         String data = "";
		
	 try {
             stmt = conn.createStatement();
	     queryResult = stmt.executeQuery(statement);             
                        
             while (queryResult.next()) {
                 data = data.concat(queryResult.getString("scname")).concat(AlgoConstants.SEPERATOR).concat(queryResult.getString("tcname"));
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return data;
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
}
