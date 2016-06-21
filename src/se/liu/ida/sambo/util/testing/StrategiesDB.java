/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util.testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import se.liu.ida.sambo.jdbc.ResourceManager;

/**
 * <p>
 * To query the strategies database.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class StrategiesDB {
    
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Strategies table name in the database.
     */
    private String tableName = "dbsambo.strategies";
    
    /**
     * Default constructor which will initialize SQL server connection.
     */
    public StrategiesDB() {
        try {
            sqlConn = ResourceManager.getConnection();            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }
    
    /**
     * Insert list of strategies into the database.
     * 
     * @param strategies    List of alignment strategies. 
     */
    public void insertStrategies(ArrayList<String> strategies) {
        
        Statement stmt = null;
        try {
            stmt = sqlConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE);
            sqlConn.setAutoCommit(false);
            // Executing mutiple statement.
            for (String strategy: strategies) {
                stmt.addBatch("INSERT INTO " + tableName + " VALUES('"
                        + strategy +"')");
            }
            stmt.executeBatch();
            sqlConn.commit();
            sqlConn.setAutoCommit(true);
            stmt.close();            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Query database to get saved alignment strategies.
     * 
     * @return  List of alignment strategies. 
     */
    public ArrayList<String> getStrategies() {
        
        ArrayList<String> result =new ArrayList();
        PreparedStatement stmt = null;
	ResultSet queryResult = null;
        String sqlSelect = "SELECT * FROM " + tableName;
        try {
            stmt = sqlConn.prepareStatement(sqlSelect);
            queryResult = stmt.executeQuery();
            
            while(queryResult.next()) {
                result.add(queryResult.getString("strategy"));
            }
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        ResourceManager.close(queryResult);
	ResourceManager.close(stmt);
                
        return result;
    }
    
    /**
     * Delete strategies saved in the database.
     */
    public void clearTable() {
        
        PreparedStatement stmt = null;
	
        String sqlSelect = "DELETE FROM " + tableName;
        try {
            stmt = sqlConn.prepareStatement(sqlSelect);
            stmt.executeUpdate();            
            
        } catch (Exception _e) {
            _e.printStackTrace();
        }        
	ResourceManager.close(stmt);
    }
    
    /**
     * Close SQL server connection created by the instance of this class.
     */
    public void closeConnection() {
        
        try {
            ResourceManager.close(sqlConn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
