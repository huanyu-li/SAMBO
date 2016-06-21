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
public class AccessSuggBasedRecomm {
    
 Connection SQLconn=null;
 
 public String tableName="dbsambo.recommendationSuggBased";
 
 
 public AccessSuggBasedRecomm()
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
 
 
 
 
 
 
 
 
 
 
 
 
        public boolean select(String s, Connection conn)
	{
		// declare variables
		PreparedStatement stmt = null;
		ResultSet rs = null;
                String SQL="";   
                
                boolean result=false;
                
                
                String Straparams[]=s.split(",");
                    //out.println(_result[i].getId()+" "+_result[i].getThreshold()+" "+_result[i].getMatchers()+" "+_result[i].getWeights()+" "+_result[i].getSubweights());
                    
                    
                
                
                
		
		try {
                    
                    SQL = "SELECT * FROM "+tableName+" WHERE matcher='"+Straparams[0]+"' AND weight='"+Straparams[1]+"' AND combination='"+Straparams[2]
                            +"' AND threshold='"+Straparams[3]+"'";
                            
			
                        

			// prepare statement
			stmt = conn.prepareStatement( SQL );
			//stmt.setMaxRows( maxRows );	
		
			rs = stmt.executeQuery();
                        
                        String data="";
                        
                        while(rs.next())
                        result=true;
                        
                        
		}
                
		catch (Exception _e) {
			_e.printStackTrace();
			
		}
                
		finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
		
		}
		
                return result;
	}
 
 
 
 
 
 
 
 
 
 
 
 public void ClearDB()
    {
           
                PreparedStatement stmt = null;
		ResultSet rs = null;
                
                
                
                
                
                String SQL = "DELETE FROM "+tableName;
                        
		
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
     
   AccessSuggBasedRecomm test= new AccessSuggBasedRecomm();
   
   ArrayList<String> result=new ArrayList();
   
   Connection SQLconn=null;
   
   try {
                    
                    SQLconn = ResourceManager.getConnection();
                    //Updateconn = ResourceManager.getConnection();
             }
        
        catch (Exception ex) 
        {
           System.out.println("Error in creating connection");
        }
   
//   test.ClearDB();
   
//   result.add("INSERT INTO "+test.tableName+" VALUES('a', 'b', 'c', 'd', 1, 1, 1, 1, 1, 1)");
   
//   test.InsertINTODB(result);
   
  // test.ClearDB();
//   test.closeConnection();
     
   
   
   
   
   
   System.out.println(test.select("BayesLearning,1.0,-NA-,0.3", SQLconn));
     
 }
         
 
    
}
