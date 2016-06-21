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
 * To access bioportal parent table.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class parentTable {    
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Parent table name.
     */
    private String tablename = "dbsambo.parents";
    /**
     * SQL select.
     */
    private String sqlSelect = "SELECT parents FROM "+tablename
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
    public parentTable(Connection conn) {     
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
     * Select parents from database.
     * 
     * @param term
     * @return  List of parents for the term. 
     */
    public ArrayList<String> selectParents(String term) {
        
        ArrayList<String> parents = new ArrayList<String>();        
	ResultSet rs = null;
        
        try {
            
            selectStatement.setString(1, term);
            rs = selectStatement.executeQuery();
            
            while (rs.next()) {
                
                String parentGroup = rs.getString("parents");
                String []parentStr = parentGroup.split("#");
                
                for (String parent:parentStr) {
                    parents.add(parent);
                }
            }
            rs.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return parents;
    }
    
    /**
     * Insert parents into database.
     * 
     * @param term
     * @param parents 
     */
    public void insertParents(String term, String parents) {
        
        try {
            insertStatement.setString(1, term);
            insertStatement.setString(2, parents);
            insertStatement.executeUpdate();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
}
