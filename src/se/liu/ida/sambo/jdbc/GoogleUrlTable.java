/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc;

import java.sql.*;
import java.util.ArrayList;



/**
 * <p>
 * To store and query google Urls.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class GoogleUrlTable {
        
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Google Url table name.
     */
    private String tableName = "dbsambo.googleurl";
    /**
     * SQL select.
     */
    private String sqlSelect = "SELECT url from "+tableName+" WHERE term =?";
    /**
     * SQL insert.
     */
    private String sqlInsert = "INSERT INTO "+tableName+" VALUES(?, ?)";
    /**
     * Insert statement.
     */
    private PreparedStatement insertStatement = null;
    /**
     * Select statement.
     */
    private PreparedStatement selectStatement = null;
    
    /**
     * Default constructor.
     */
    public GoogleUrlTable() {
        try {
            sqlConn = ResourceManager.getConnection();            
            insertStatement = sqlConn.prepareStatement(sqlInsert);
            selectStatement = sqlConn.prepareStatement(sqlSelect);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Close SQL server connection.
     */
    public void closeConnection() {
        try {
            ResourceManager.close(sqlConn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Selects google urls from database.
     * 
     * @param term
     * @return Returns list of urls.
     */
    public ArrayList<String> selectUrls(String term) {
        
        ArrayList<String> urls = new ArrayList<String>();        
	ResultSet rs = null;
        
        try {            
            selectStatement.setString(1, term);
            rs = selectStatement.executeQuery();
            
            while(rs.next()) {
                String urlGroup = rs.getString("url");
                String []url = urlGroup.split("#");
                
                for(String s:url) {
                    urls.add(s);
                }
            }
            ResourceManager.close(rs);
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        
        return urls;
    }
    
    /**
     * To insert google urls into local database.
     * 
     * @param term      Term
     * @param urls      Urls for the term.
     */
    public void insertUrls(String term , String urls) 	{
        
        try {            
            insertStatement.setString(1, term);
            insertStatement.setString(2, urls);
            insertStatement.executeUpdate();
        } catch (Exception _e) {
            _e.printStackTrace();
        }	
    }
}
