/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.recommendation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.jdbc.ResourceManager;

/**
 * <p>
 * Handles queries related to the recommendation database.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class AccessRecommendationDB {
    
    /**
     * SQL connection to access server.
     */    
    private Connection SQLconn=null;
    /**
     * Name of the table where the recommendations info are stored.
     */ 
    private String tableName;
    
    /**
     * This constructor create a new SQL connection.
     * 
     * @param recommMethod      The recommendation method.
     */
    public AccessRecommendationDB(int recommMethod) {
        
        switch (recommMethod) {
            case RecommendationConstants.RECOMMENDATION_METHOD1:
                tableName = RecommendationConstants.RECOM_METHD1_TABLE;
                break;
            case RecommendationConstants.RECOMMENDATION_METHOD2:
                tableName = RecommendationConstants.RECOM_METHD2_TABLE;
                break;
            case RecommendationConstants.RECOMMENDATION_METHOD3:
                tableName = RecommendationConstants.RECOM_METHD3_TABLE;
                break;
            default:
                System.out.println("Invalid recommendation method");
                break;
        }
        
        try {
            SQLconn = ResourceManager.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();           
        }
    }
    
    /**
     * This method execute multiple insert statements.
     * 
     * @param statements    List of MySQL insert statements.
     */
    public void insert(ArrayList<String> statements) {
        
        Statement stmt = null;
		
	try {
            
            stmt = SQLconn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            SQLconn.setAutoCommit(false);
    
            for (String statement: statements) {
                stmt.addBatch(statement);
            }
            
            stmt.executeBatch();
            SQLconn.commit();
            SQLconn.setAutoCommit(true);
            stmt.close();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * This method clears the database.
     * 
     * @param ontologies    Name of the ontology pair. 
     */    
    public void clearTable(String ontologies) {
        
        Statement stmt = null;	
        String statement;
        try {
            
            statement = "DELETE FROM "+tableName+" WHERE ontologies "
                    + "='"+ontologies+"'";
            stmt = SQLconn.createStatement();			
            stmt.executeUpdate(statement);
            stmt.close();
        } catch (Exception _e) {
            _e.printStackTrace();           
        }
    }
    
    /**
     * This method close server connections created by an instance of this 
     * class.
     *
     */
    public void closeConnection() {                
        try {
            ResourceManager.close(SQLconn);
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }    
  
    
    /**
     * This method will get recommendations info from the database.
     * 
     * @param ontologies        Name of the ontology pair.
     * @param displayParams     Recommendation parameters.
     * @return 
     */
    public  ArrayList<HashMap> select(String ontologies, 
            String[] displayParams) {

        ArrayList<HashMap> result =new ArrayList();
        Statement stmt = null;
        ResultSet queryResult = null;
        String statement = "SELECT * FROM "+tableName+" WHERE ontologies ="
                + "'"+ontologies+"'";
        try {
            
            stmt = SQLconn.createStatement();			
            queryResult = stmt.executeQuery(statement);
                       
            while (queryResult.next()) {
                /**
                 * To hold the information of a single alignment strategy.
                 */
                HashMap<String, String> recommInfo = new 
                        HashMap<String, String>();
                
                for (String param:displayParams) {
                    recommInfo.put(param, queryResult.getString(param));
                }
                result.add(recommInfo);
            }
            stmt.close();
            queryResult.close();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return result;
    }
    
    /**
     * This method generate SQL insert statement for the recommendation.
     * 
     * @param ontologies        Name of the ontology pair.
     * @param stragParams       Alignment strategy.
     * @param scoreParam        Score parameters.
     * @param recommParams      Recommendation parameters.
     * 
     * @return  Returns SQL insert statement. 
     */
    public  final String generateInsertStatement(String ontologies, String[]
            stragParams,  double[] scoreParam, float [] recommParams) {
        
         String statement = "INSERT INTO " + tableName + " VALUES('"+ 
                 ontologies +"', '"+ stragParams[0] +"', '"+ stragParams[1] 
                 +"', '"+ stragParams[2] +"', '"+ stragParams[3] +"', ";
         
         for(int i = 0; i < scoreParam.length; i++) {
             statement = statement+ "'"+ scoreParam[i] +"', ";
         }
         for(int i = 0; i < recommParams.length; i++) {
             statement = statement+ "'"+ recommParams[i] +"', ";
         }
         // To remove last two characters ", "
         statement = statement.substring(0, statement.length() - 2);
         statement = statement + ")";
                
         
        return statement;

    }
}
