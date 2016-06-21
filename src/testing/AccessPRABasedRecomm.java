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
public class AccessPRABasedRecomm {
    
 Connection SQLconn=null;
 
 public String tableName="dbsambo.recommendationPRABased";
 public String tableNameABCD="dbsambo.recommendationPRABasedABCD";
 
 
 public AccessPRABasedRecomm()
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
 
 
 
 
 
 
 
 public void ClearDB()
    {
           
                PreparedStatement stmt = null;
		ResultSet rs = null;
                
                
                
                
                
                
                        
		
		try {
			String SQL = "DELETE FROM "+tableName;
                        
			// prepare statement
			stmt = SQLconn.prepareStatement( SQL );
			
		       stmt.executeUpdate();
                       
                       
                       SQL = "DELETE FROM "+tableNameABCD;
                        
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
     
   AccessPRABasedRecomm test= new AccessPRABasedRecomm();
   
   ArrayList<String> result=new ArrayList();
   
   test.ClearDB();
   
   result.add("INSERT INTO "+test.tableName+" VALUES('a', 'b', 'c', 'd', 1, 1, 1, 1, 1, 1)");
   
   test.InsertINTODB(result);
   
  // test.ClearDB();
   test.closeConnection();
     
     
 }
         
 
    
}
