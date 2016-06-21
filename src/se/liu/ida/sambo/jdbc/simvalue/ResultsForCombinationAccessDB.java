/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Rajaram
 * @version 1.0
 */
public class ResultsForCombinationAccessDB {    

    /**
     * Name of the matcher info table.
     */
    private String tableName = "dbsambo.resultsforcombination";	
    /** 
     * SQL select.
     */
    private final String sqlSelect = "SELECT isresultavailable FROM " + 
            tableName + " WHERE ontologies = ? AND "
            + "columnnameinsimvaluetable = ?";
    /** 
     * SQL insert.
     */
    private final String sqlInsert = "INSERT INTO " + tableName + " ( "
            + "ontologies, combination, columnnameinsimvaluetable, "
            + "isresultavailable ) VALUES ( ?, ?, ?, ? )";
    /** 
     * SQL update.
     */
    private final String sqlUpdate = "UPDATE " + tableName + " SET"
            + " isresultavailable = ? WHERE ontologies = ? AND "
            + "columnnameinsimvaluetable = ?";
    /**
     * SQL server connection.
     */
    private java.sql.Connection selectConn = null;
    /**
     * 
     * @param sqlConn 
     */
    public ResultsForCombinationAccessDB(java.sql.Connection sqlConn) {
        selectConn = sqlConn;
        
    }
    
    /**
     * This method query's the database to get the information about matchers, 
     * like status of the matcher's computation result etc.
     * 
     * @param ontologyPair  Name of the ontology pair.
     * @param matcherName   Name of matcher in the simvalue table.
     * @param sqlConn       SQL server connection.
     * 
     * @return      Matcher's status. 
     */
    public boolean[] getMatcherInfo(String ontologyPair,
            String matcherName, Connection sqlConn) {
        
        /**
         * [0] Is a row for the selected matcher exists in the simvalue
         * table.
         * [1] Is complete computation results for the selected matcher is
         * available in the simvalue table.
         */
        boolean [] result = {false, false};        
        PreparedStatement stmt;
        ResultSet queryResult;
        
        try {
            
            stmt = sqlConn.prepareStatement(sqlSelect);
            stmt.setString(1, ontologyPair);
            stmt.setString(2, matcherName);
            
            queryResult = stmt.executeQuery();
                                   
                        
            while (queryResult.next()) {                            
                result[0] = true;                            
                            
                if(queryResult.getBoolean(1)) {                                
                    result[1] = true;                            
                }       
            }
            stmt.close();
            queryResult.close();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
               
        return result;
        
    }    
    
    /**
     * This method will insert new row in the matcher info table.
     * 
     * @param ontologies    Name of the ontology pair.
     * @param note          Simple note about computation(Note this arg can be 
     *                      empty).
     * @param colName       Matcher name in the simvalue table.
     */
    public void insertNewRow(String ontologies, String note, String colName) {
        
        PreparedStatement stmt;        
        
        try {            
            stmt = selectConn.prepareStatement(sqlInsert);
            stmt.setString(1, ontologies);
            stmt.setString(2, note);
            stmt.setString(3, colName);
            stmt.setString(4, "false");
            
            stmt.executeUpdate();
            stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method set the status of a matcher's computation result as 
     * available.
     * 
     * @param ontologies    Name of the ontology pair.
     * @param matcherName   Matcher name in the simvalue table.
     */
    public void setResultIsAvailable(String ontologies, String matcherName) {
        
        PreparedStatement stmt;        
        
        try {            
            stmt = selectConn.prepareStatement(sqlUpdate);
            stmt.setString(1, "true");
            stmt.setString(2, ontologies);
            stmt.setString(3, matcherName);            
            
            stmt.executeUpdate();
            stmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
