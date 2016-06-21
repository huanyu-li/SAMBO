/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.bioportal;




import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * To access bioportal synonyms table.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class synonymsTable {    
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Synonyms table name.
     */
    private String tablename = "dbsambo.synonyms";
    /**
     * SQL select.
     */
    private String sqlSelect = "SELECT synonyms FROM "+tablename
            +" WHERE term = ?";
    /**
     * SQL insert.
     */
    private String sqlInsert = "INSERT INTO "+ tablename +" VALUES(?, ?)";
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
     * @param conn 
     */
    public synonymsTable(Connection conn) {     
        try {
            sqlConn = conn;
            insertStatement = sqlConn.prepareStatement(sqlInsert);
            selectStatement = sqlConn.prepareStatement(sqlSelect);
        } catch (SQLException ex) {
            Logger.getLogger(synonymsTable.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Select synonyms from database.
     * 
     * @param term
     * @return  List of synonyms for the term. 
     */
    public ArrayList<String> selectSynonyms(String term) {
        
        ArrayList<String> synms = new ArrayList<String>();        
	ResultSet rs = null;
        
        try {
            
            selectStatement.setString(1, term);
            rs = selectStatement.executeQuery();
            
            while (rs.next()) {
                
                String synGRP = rs.getString("synonyms");
                String []syn = synGRP.split("#");
                
                for (String s:syn) {
                    synms.add(s);
                }
            }
            rs.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return synms;
    }
    
    /**
     * Insert synonyms into database.
     * 
     * @param term
     * @param synonyms 
     */
    public void insertSynonyms(String term, String synonyms) {
        
        try {
            insertStatement.setString(1, term);
            insertStatement.setString(2, synonyms);
            insertStatement.executeUpdate();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
}
