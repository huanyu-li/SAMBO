/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import se.liu.ida.sambo.jdbc.ResourceManager;

/**
 *
 * @author Rajaram
 */
public class AccessPRABasedFilter {
    
 Connection SQLconn=null;
 
 public String tableName="dbsambo.filterPRABased";
 
 
 public AccessPRABasedFilter()
 {
     try {
                    
                    SQLconn = ResourceManager.getConnection();
                    //Updateconn = ResourceManager.getConnection();
             }
        
        catch (Exception ex) 
        {
           System.out.println("Error in creating connection");
        }
 }
 
 
 
 
 
 public void InsertINTODB(ArrayList<String> MULTIPLE_STATEMENTS)
	{
		                
                Statement stmt = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			//conn = isConnSupplied ? userConn : ResourceManager.getConnection();
                        
                                                
                        stmt = SQLconn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        
                        
                            SQLconn.setAutoCommit(false);
    
                             for(String statement: MULTIPLE_STATEMENTS)
                             {
                                 stmt.addBatch(statement);

                                    }
  
                             stmt.executeBatch();
                             SQLconn.commit();
                             SQLconn.setAutoCommit(true);
                             stmt.close();
            } 
                
                catch (Exception ex) {
                System.out.println("Error in SQL ");
                
            }

		
		
		
	}
 
 
 
 
 public void InsertINTODB(String statement)
	{
		                
                PreparedStatement stmt = null;
		
		try {
			
                                 
                            // prepare statement
			stmt = SQLconn.prepareStatement(statement);
			
		       stmt.executeUpdate();
            } 
                
                catch (Exception ex) {
                System.out.println("Error in    : "+statement);
                
            }

		
		
		
	}
 
 
 
 
 
 
 
 public void ClearDB(String filter)
    {
           
                PreparedStatement stmt = null;
		ResultSet rs = null;
                
                
                
                
                
                String SQL = "DELETE FROM "+tableName+" WHERE filter='"+filter+"'";
                        
		
		try {
			
                        
			// prepare statement
			stmt = SQLconn.prepareStatement( SQL );
			
		       stmt.executeUpdate();
                       
		}
		catch (Exception _e) {
			_e.printStackTrace();
			
		}
		finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);

		
		}
           
           
       }
 
 
 
 
 
 
 
 
 public void closeConnection()
                       {
        
        try {
               
		//ResourceManager.close(Updateconn);
                ResourceManager.close(SQLconn);
                
			
               }
        
        catch (Exception ex) 
        {
           System.out.println("Error in creating connection");
        }
        
    }
 
 
 
 
 public static void main(String args[])
 {
     
   AccessPRABasedFilter test= new AccessPRABasedFilter();
   
   ArrayList<String> result=new ArrayList();
   
   test.ClearDB("dtf");
   
   result.add("INSERT INTO "+test.tableName+" VALUES('a', 'b', 'c', 'd', 'dtf')");
   
   test.InsertINTODB(result);
   
  // test.ClearDB();
   test.closeConnection();
     
     
 }
         
 
    
}
