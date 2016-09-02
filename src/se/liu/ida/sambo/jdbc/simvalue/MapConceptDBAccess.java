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

    /**
     * Singel insert
     * @author huali50
     * @param sqlStatement
     * @param conn 
     */
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
    /**
     * Get concept pair id
     * @author huali50
     * @param statement
     * @param conn
     * @return 
     */
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
    /**
     * Get concepts
     * @author huali50
     * @param statement
     * @param conn
     * @return 
     */
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
    /**
     * Update in batch
     * @author huali50
     * @param sqlStatements
     * @param conn 
     */
    public void multipleUpdate(ArrayList<String> sqlStatements,
             Connection conn) {
         
         Statement stmt = null;
         		
	try {
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            // It prevents statement to execute immediately.
            conn.setAutoCommit(false);
            // All statements in the list will be executed in a single batch.
            int i=0;
            int size = sqlStatements.size();
            int batches_size = size/100000;
            int last_batch_size = size%100000;
            int block_flag = 0;
            for(String statement: sqlStatements) {
                stmt.addBatch(statement);
                i++;
                if(i == 100000){
                    stmt.executeBatch();
                    stmt.clearBatch();
                    conn.commit();
                    conn.setAutoCommit(false);
                    block_flag++;
                    i=0;
                    System.out.println("block_id"+block_flag);
                }
                if((block_flag == batches_size) &&(i==last_batch_size)){
                    stmt.executeBatch();
                    stmt.clearBatch();
                    conn.commit();
                }
                             
            }
            stmt.close();		
        } catch (Exception _e) {
            _e.printStackTrace();		
        }
     }
}
