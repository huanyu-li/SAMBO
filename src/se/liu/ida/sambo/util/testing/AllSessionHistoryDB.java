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
 * Handles queries related to all sessions history.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class AllSessionHistoryDB {
    
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Table Name
     */
    public String tableName = "dbsambo.allhistory";
    /** 
     * SQL insert.
     */
    private final String sqlInsert = "INSERT INTO " + tableName + " ( "
            + "concepts, decision) VALUES ( ?, ?)";
    
    /**
     * Default constructor.
     */
    public AllSessionHistoryDB() {
        try {
            sqlConn = ResourceManager.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Execute multiple sql insert statements.
     * 
     * @param statements 
     */
    public void insert(ArrayList<String> statements) {
        
        Statement stmt = null;
        try {
            
            stmt = sqlConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE);
            sqlConn.setAutoCommit(false);
            
            for (String statement: statements) {
                stmt.addBatch(statement);
            }
            stmt.executeBatch();
            sqlConn.commit();
            sqlConn.setAutoCommit(true);
            stmt.close();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Insert decision into the database.
     * 
     * @param statements 
     */
    public void insert(String suggestion, String decision) {
        
        PreparedStatement stmt;        
        
        try {            
            stmt = sqlConn.prepareStatement(sqlInsert);
            stmt.setString(1, suggestion);
            stmt.setString(2, decision);
            
            stmt.executeUpdate();
            stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * To get accepted or rejected validated mapping suggestions.
     * 
     * @param decision  Either 0 or 1, 0 - rejected and 1 - accepted.
     * 
     * @return  List of validated mapping suggestions.
     */
    public  ArrayList<String> select(String decision) {
        
        ArrayList<String> result = new ArrayList<String>();
        Statement stmt = null;
	ResultSet rs = null;
        String sqlSelect = "SELECT * FROM " + tableName + " where "
                + "decision ='" + decision + "'";
        try {
            
            stmt = sqlConn.createStatement();
            rs = stmt.executeQuery(sqlSelect);
            
            while (rs.next()) {
                result.add(rs.getString("concepts"));
            }
            
            stmt.close();
            rs.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }        
        return result;
    }
    
    /**
     * To select all the validated mapping suggestions.
     * 
     * @return  List of validated suggestions. 
     */
    public  ArrayList<String> selectAll() {
        
        ArrayList<String> result = new ArrayList<String> ();
        Statement stmt = null;
	ResultSet rs = null;
        String sqlSelect = "SELECT * FROM "+tableName;
        
        try {
            
            stmt = sqlConn.createStatement();
            rs = stmt.executeQuery(sqlSelect);
            
            while (rs.next()) {
                result.add(rs.getString("concepts"));
            }
            
            stmt.close();
            rs.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return result;
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
