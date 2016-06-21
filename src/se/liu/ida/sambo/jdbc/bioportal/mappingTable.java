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
 * To access bioportal mapping table.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class mappingTable {    
    /**
     * SQL server connection.
     */
    private Connection sqlConn = null;
    /**
     * Parent table name.
     */
    private String tablename = "dbsambo.mappings";
    /**
     * SQL select.
     */
    private String sqlSelect = "SELECT mappings FROM "+tablename
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
    public mappingTable(Connection conn) {     
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
     * Select mappings from database.
     * 
     * @param term
     * @return  List of parents for the term. 
     */
    public ArrayList<String> selectMappings(String term) {
        
        ArrayList<String> mappings = new ArrayList<String>();        
	ResultSet rs = null;
        
        try {
            
            selectStatement.setString(1, term);
            rs = selectStatement.executeQuery();
            
            while (rs.next()) {
                
                String mapGroup = rs.getString("mappings");
                String []maps = mapGroup.split("#");
                
                for (String map:maps) {
                    mappings.add(map);
                }
            }
            rs.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return mappings;
    }
    
    /**
     * Insert mappings into database.
     * 
     * @param term
     * @param mappings 
     */
    public void insertParents(String term, String mappings) {
        
        try {
            insertStatement.setString(1, term);
            insertStatement.setString(2, mappings);
            insertStatement.executeUpdate();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
}
