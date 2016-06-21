/*
 * DBOperator.java
 *
 * Created on den 31 januari 2006, 16:50
 */

package se.liu.ida.sambo.util;

/**
 *
 * @author hetan
 */

import java.sql.*;

public class MySQL {
    
    Connection conn = null;
    
    /** Creates a new instance of MySQL
     *   and create the connection to the database.
     */
    public MySQL() {
        
        this("hetan", "sambo", "jdbc:mysql://localhost:3306/sambo");
    }
    
    
    
    /** Creates a new instance of MySQL
     *   and create the connection to the database.
     */
    public MySQL(String username, String password) {
        
        this(username, password, "jdbc:mysql://localhost:3306/sambo");
    }
    
    /** Creates a new instance of MySQL
     *   and create the connection to the database.
     */
    public MySQL(String username, String password, String url) {
        
        try {
            
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();;
            this.conn = DriverManager.getConnection(url, username, password);
            
            System.out.println("Database connection established");
            
        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
            
        }
    }
    
    /** terminate the connection to database
     *   and finalize the instance
     */
    public void finalize(){
        
        try {
            
            if(this.conn != null)
                this.conn.close();
            
            System.out.println("Database connection terminated");
            
            super.finalize();
            
        } catch ( Exception e) {
            e.printStackTrace();
        } catch ( Throwable t){
            t.printStackTrace();
        }
        
    }
    
    /** execute the update SQL statement and update the database
     *
     *@param SQL the SQL statement
     *@param warning the error is seen as a warning
     */
    public void executeUpdate(String SQL, int warning){
        
        Statement stmt;
        
        try {
            
            stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
            
            stmt.close();
            System.out.println(SQL);
            
        } catch(SQLException ex) {
            
            System.err.println("SQLException: " + ex.getMessage());
            
            //It is allowed to continue, if the specific error occures
            if(ex.getErrorCode() != warning)
                finalize();
        }
    }
    
    /** execute the update SQL statement and update the database
     *
     *@param SQL the SQL statement
     */
    public void executeUpdate(String SQL){
        
        Statement stmt;
        
        try {
            
            stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
            
            stmt.close();
            System.out.println(SQL);
            
        } catch(SQLException ex) {
            
            System.err.println("SQLException: " + ex.getMessage());
            finalize();
        }
    }
    
    /** execute the query SQL statement and update the database
     *
     *@param SQL the SQL statement
     */
    public ResultSet executeQuery(String SQL){
        
        Statement stmt;
        ResultSet resultSet=null;
        
        try {
            
            stmt = this.conn.createStatement();
            resultSet = stmt.executeQuery(SQL);
            
            // stmt.close();
         //   System.out.println(SQL);
            
            return resultSet;
            
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            finalize();
        }
        
        return resultSet;
    }
    
    
    //Prepare a statement
    public PreparedStatement prepareStmt(String stmt){
        
        try{
            return this.conn.prepareStatement(stmt);
            
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            finalize();
        }
        
        return null;
    }
    
    /**close the prepared statement
     */
    public void closeStmt(PreparedStatement stmt){
        
        try{
            
            System.out.println("\nclose prepared statement");
            stmt.close();
            
        }catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            finalize();
        }
    }
    
    
    /* drop the table
     *
     *@param name the name of table
     */
    public void dropTable(String name){
        
        executeUpdate( "drop table " + name);
    }
}


