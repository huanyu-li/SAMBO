/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc;

/**
 * <p>
 * Execute the queries related to mappable group.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class MappableConceptPairsDB {
    
    /**
     * Database name.
     */
    private String dataBase = "dbsambo";
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    
    /**
     * Default constructor.
     */
    public MappableConceptPairsDB(Connection conn) {        
            sqlConn = conn;        
    }
    
    /**
     * Method 'getTableName'
     * 
     * @return Name of table that contains mappable suggestions.
     */
    public String getTableName() {
        return dataBase+".mappablesuggestions";	
    }
    
    /**
     * To execute multiple insert statements.
     * 
     * @param statements 
     */    
    public void multipleInsert(ArrayList<String> statements) {
        
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
       } catch (Exception _e) {
           _e.printStackTrace();
       }
    }
    
    /**
     * To get mappable concept pairs for given ontology pairs.
     * 
     * @param ontologyName  Ontology pair name.
     * 
     * @return  List of mappable concept pairs.
     * 
     */
    public ArrayList getConceptPairs(String ontologyName) {
        
        Statement stmt = null;
	ResultSet queryResult = null;
        String sqlSelect ="";
        ArrayList<String> mappableConceptPairs = new ArrayList<String>();
        try {
            sqlSelect = "SELECT suggestion FROM " + getTableName() + " WHERE"
                    + " ontologies='" + ontologyName + "'";
            stmt = sqlConn.createStatement();
            queryResult = stmt.executeQuery(sqlSelect);
            
            while(queryResult.next()) {
                mappableConceptPairs.add(queryResult.getString("suggestion"));
            }
            
            stmt.close();
            queryResult.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        
        return mappableConceptPairs;
    }
    
    /**
     * Check the database if mappable concept pairs for a pair of ontology is 
     * available in the database.
     * 
     * @param ontologyName      Ontology pair name.
     * 
     * @return  True/False.
     */
    public boolean isConceptPairsAvailable(String ontologyName) {
        
        boolean availability = false;		
        Statement stmt = null;
        ResultSet queryResult = null;
        String sqlSelect = "";
        
        try {
                 
            sqlSelect = "SELECT suggestion FROM " + getTableName() + " WHERE "
                    + "ontologies='" + ontologyName + "' limit 0,10";
            stmt = sqlConn.createStatement();
            queryResult = stmt.executeQuery(sqlSelect);
            
            while(queryResult.next()) {
                availability = true;
                break;
            }
            
            stmt.close();
            queryResult.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }        
        return availability;		
    }
    
    /**
     * This method clears the database.
     * 
     * @param ontologyName Ontology pair name.
     */
    public void clearTable(String ontologyName) {
        
        Statement stmt = null;	
        String sqlDelete = "";
        try {
            sqlDelete = "DELETE FROM " + getTableName() + " WHERE "
                    + "ontologies='" + ontologyName + "'";
            stmt = sqlConn.createStatement();
            stmt.executeUpdate(sqlDelete);
            stmt.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
}
