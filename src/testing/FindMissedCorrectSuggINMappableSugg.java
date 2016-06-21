/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import testing.DBmanager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.util.testing.ExtractReferenceAlignmentFile;

/**
 *
 * @author rajka62
 */
public class FindMissedCorrectSuggINMappableSugg {
    
    
   public Connection SQLconn=null;
    
    
    
    
    public FindMissedCorrectSuggINMappableSugg()
    {
        try {
            SQLconn=DBmanager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(FindMissedCorrectSuggINMappableSugg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    public boolean SelectFromMappableGrp(String tablename, String sugg)
	{
            
            boolean result=false;
		// declare variables
		PreparedStatement stmt = null;
		ResultSet rs = null;
                String SQL="";                
               
		
		try {
			
			SQL = "SELECT * FROM "+tablename+" WHERE suggestion='"+sugg+"'";
                        
                        
			// prepare statement
			stmt = SQLconn.prepareStatement( SQL );
				
		
			rs = stmt.executeQuery();
                        
                        
                        while(rs.next())
                        result=true;
        
                        
		}
                
		catch (Exception _e) {
			_e.printStackTrace();
			
		}
                
		finally {
			DBmanager.close(rs);
			DBmanager.close(stmt);
		
		}
                
                
                return result;
		
	}
    
    
    
    public ArrayList<String> SelectRA(String tablename)
	{
            
            ArrayList<String> RA=new ArrayList();
		// declare variables
		PreparedStatement stmt = null;
		ResultSet rs = null;
                String SQL="";                
               
		
		try {
			
			SQL = "SELECT * FROM "+tablename;
                        
                        
			// prepare statement
			stmt = SQLconn.prepareStatement( SQL );
				
		
			rs = stmt.executeQuery();
                        
                        
                        while(rs.next())
                        RA.add(rs.getString("conceptID1")+"#"+rs.getString("conceptID2"));
        
                        
		}
                
		catch (Exception _e) {
			_e.printStackTrace();
			
		}
                
		finally {
			DBmanager.close(rs);
			DBmanager.close(stmt);
		
		}
                
                
                return RA;
		
	}
    
    
    
    
    public static void main(String args[])
    {
        ExtractReferenceAlignmentFile refAlign = new 
                ExtractReferenceAlignmentFile();      
        ArrayList<String> RA=refAlign.getReferenceAlignment();
        
        
        FindMissedCorrectSuggINMappableSugg mapp=new FindMissedCorrectSuggINMappableSugg();
        
        String tablename="mappablesuggestions_2300";
        
        int missedSugg=0;
        
        
        for(String s:RA)
        {
            s=s.replaceAll("\\;", "\\#");
            
            if(!mapp.SelectFromMappableGrp(tablename, s))
            {
                System.out.println(s);
                missedSugg++;
            }
            
        }
        
        
        System.out.println("Total missed correct suggestion : "+missedSugg);
        
        DBmanager.close(mapp.SQLconn);


    }
    
}
