/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.jdbc.ResourceManager;


/**
 * <p>
 * Execute the queries related to the simvalues table.
 * </p>
 * 
 * @author  Rajaram
 * @version 1.0     
 */
public class SaveSimValuesDBAccess {
    
    /**
     * Name of the SQL data base
     */
    protected String dataBaseName="dbsambo";
    /**
     * The name of the table where the similarity values are stored.
     * 
     * @return simvalue table name. 
     */
    public String getTableName() {
        return dataBaseName+".savesimvalues";	
    }   
    /**
     * Add a new column to the existing SQL Table.
     * 
     * @param statement     Complete SQL statement to add new column.
     */
    public void addColumn(String statement) {
        
        Connection conn = null;
	Statement stmt = null; 
		
	try {
            conn = ResourceManager.getConnection();                       
            stmt = conn.createStatement();                        
            stmt.execute(statement);
            stmt.close();
            conn.close();
        } catch (Exception _e) {
            _e.printStackTrace();		
        }
    }
    
    /**
     * This method execute multiple SQL statements.
     * 
     * (Note:) This method is implemented to increase the performance of the
     * system.
     * 
     * @param sqlStatements     List of SQL statements.
     * @param conn              Connection to SQL server. 
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
     
     /**
      * This method returns the similarity value for a single concept pair. 
      *    
      * @param matcherName      Matcher name in the database.
      * @param conn             SQL server connection.
      * 
      * @return simvalue    Sim value for the concept pair, if not found 
      *                     return -1.      
      */
     public float getSimValue(String statement, String matcherName,
             Connection conn) {
                  
         Statement stmt = null;
	 ResultSet queryResult = null;                
         float simvalue=-1;
		
	 try {
             stmt = conn.createStatement();
	     queryResult = stmt.executeQuery(statement);             
                        
             while (queryResult.next()) {
                 simvalue = queryResult.getFloat(matcherName);                        
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return simvalue;
     }
     
     /**
      * This method query's the simvalue table to get various concept pair 
      * parameters like, if the row for the selected concept pair is available 
      * in the data base, if the simvalue for the concept pair is available 
      * in the database.
      * 
      * @param statement       Complete SQL statement to query the simvalue 
      *                        table.
      * @param matcherName     Name of the matcher.
      * @param conn            SQL server connection.
      */
     public boolean[] getParams(String statement, String matcherName,
             Connection conn) {
         Statement stmt = null;
	 ResultSet queryResult = null;                
         /**
          * [0] Is a row for the concept pair available in the database.
          * [1] Is the simvalue for the concept pair available in the database.
          */
         boolean [] result={false,false};
		
	 try {
             stmt = conn.createStatement();
	     queryResult = stmt.executeQuery(statement);             
             
             while(queryResult.next()) {                            
                 result[0] = true;
                 double simValue = queryResult.getFloat(matcherName);
                 if(simValue > -1) {
                     result[1] = true;                   
                 }
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return result;
     }
     
     /**
      * This method query's the database to get suggestions for a particular 
      * alignment strategy.
      * 
      * @param statement    Complete SQL statement.
      * @param conn         SQL server connection.
      * @return 
      */
     public ArrayList getSimValueByCombination(String statement, 
             Connection conn) {
         
         Statement stmt = null;
	 ResultSet queryResult = null;                              
         ArrayList<String> suggestions = new ArrayList<String>();
         String data;
         
         try {
             
             stmt = conn.createStatement();
             queryResult = stmt.executeQuery(statement);             
             
             while (queryResult.next()) {
                 data = "";                            
                 data = data.concat(queryResult.getString("concept1")).concat(
                         AlgoConstants.SEPERATOR).concat(queryResult.
                         getString("concept2")).concat(AlgoConstants.SEPERATOR).
                         concat(queryResult.getString("simvalue"));
                 
                 suggestions.add( data );                        
             }
             stmt.close();
             queryResult.close();
         } catch (Exception _e) {
             _e.printStackTrace();
         }
         
         return suggestions;
     }
     
     /**
      * Availability of the column for a particular matcher in
      * the simvalue table.
      * 
      * @param statement      Complete SQL statement.
      * @param conn           SQL server connection.
      * 
      * @return availability    Returns true if the column available.
      */
     public boolean isColumnAvailable(String statement, Connection conn) {
         
         boolean availability=false;		
	 Statement stmt = null;	 
         int row = 0;
	 	
	 try {
             
             stmt = conn.createStatement();
             // Will be 0 if the column is not found.
             row = stmt.executeUpdate(statement);             
             
             if(row != 0) {
                 availability=true;
             }
             stmt.close();
         } catch (Exception _e) {
             _e.printStackTrace();		
         }
         
         return availability;
     }

}
