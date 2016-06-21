/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.jdbc.ResourceManager;



/**
 * <p>
 * Generate SQL query statement for querying the database where similarity 
 * values are stored.
 * </p>
 * @author Rajaram
 * @version 1.0
 */
public class SimValueGenerateQuery {
    
    /**
     * To query sim value table.
     */
    private SaveSimValuesDBAccess simValueDao = new SaveSimValuesDBAccess();
    /**
     * Ontologies pair name.
     */
    private String ontologiesName;
    /**
     * SQL connection.
     */
    private java.sql.Connection selectConn = null;
    /**
     * <p>
     * This constructor initialize a connection to access the SQL server,
     * initialize ontology pair name.
     * </p>
     * @param ontologies    Name of the ontology pair.
     * @param sqlConn       SQL server connection.
     */
    public SimValueGenerateQuery(String ontologies, 
            java.sql.Connection sqlConn) {
        ontologiesName = ontologies;
        selectConn = sqlConn;
        
        
    }
    /**
     * This method will create a new column in the simValue table. 
     * Usually used when a new matcher is added to the database.
     * 
     * @param columnName    Name of the matcher( eg. matcher0, matcher1 etc).
     */
    public void createColumn(String columnName) {
        
                  
        String statement="ALTER TABLE "+ simValueDao.getTableName()+
                " ADD "+columnName+" double default -1";
        simValueDao.addColumn(statement);
    }
    /**
     * To execute list of MySQL statement. Usually used for multiple update and
     * insert statements.
     * 
     * @param statements    List of MySQL statements
     * @param conn          SQL server connection(Using separate connection will 
     *                      increase the system performance). 
     */
    public void executeStatements(ArrayList<String> statements,
            java.sql.Connection conn) {
        simValueDao.multipleUpdate(statements, conn);
    }
    
    /**
     * This method query's the simvalue table to get various concept pair params 
     * like, if the row for the selected pair is available in the database, 
     * if the simvalue for the selected pair is available in the database.
     * 
     * @param concept1      Concept1 ID.
     * @param concept2      Concept2 ID.
     * @param colname       Column name of the matcher in the simvalue table. 
     * @param selectConn    SQL server connection(Using seperate connection will 
     *                      increases system performance).
     * 
     * @return pairParams   Various parameters for the pair.
     */    
    public boolean[] getPairParams(String concept1, String concept2, 
            String colname, java.sql.Connection selectConn) {
        /**
         * [0] Is a row for the concept pair available in the database.
         * [1] Is the simvalue for the concept pair available in the database.
         */ 
        boolean[] pairParams = {false, false};            
        String statement;
        statement = "select concept1 , "+colname+" from "+ simValueDao.
                getTableName() +" where ontologies='"+ontologiesName+"' and "
                + "concept1='"+ concept1 +"' and concept2='"
                + concept2 +"' limit 0,1";                      
        pairParams =simValueDao.getParams(statement, colname, selectConn);
        return pairParams;
    }
    
    /**
     * This method query's the simvalue table to get sim value for a particular
     * concept pair, return -1 if the simvalue is not found in the database.
     *      
     * @param concept1      Concept1 ID.
     * @param concept2      Concept2 ID.
     * @param colname       Column name of the matcher in the simValue table. 
     * @param selectConn    SQL server connection.
     * 
     * @return simValue   Sim value for the pair.
     */
    public double getSimValue(String concept1, String concept2, String colname
            ) {
                             
        double simValue = -1;
        String statement;   
            
        statement = "select "+colname+" from "+simValueDao.getTableName()+
                    " where ontologies='"+ ontologiesName +"' and concept1='"
                    + concept1 +"' and concept2='"+ concept2 +"' limit 0,1";            
                      
       simValue = simValueDao.getSimValue(statement, colname, selectConn);      
        
       return simValue;
    }
    /**
     * This method generates insert statement to insert a new row in the 
     * sim value table.  
     *
     * @param concept1      Concept1 ID.
     * @param concept2      Concept2 ID.
     * @param matcher       Column name of the matcher in the simValue table.
     * @param simvalue      Similarity value for the concept pair.
     * 
     * @return statement    Insert statement.
     */
    public String generateInsertStatement(String concept1, String concept2, 
            int matcher, double simvalue) {
        
        String statement="";               
        statement="INSERT INTO "+ simValueDao.getTableName() +
                "(ontologies, concept1, concept2, ";                   
        statement=statement+"matcher"+matcher+") VALUES";                  
        statement=statement.concat("('"+ontologiesName+"', "
                + "'"+concept1+"'," + " '"+concept2+"', "+simvalue+")");
        return statement;
    }
    /**
     * This method generates update statement to update exiting row in the 
     * sim value table. 
     * 
     * @param concept1      Concept1 ID.
     * @param concept2      Concept2 ID.
     * @param matcher       Column name of the matcher in the simValue table.
     * @param simvalue      Similarity value for the concept pair.
     * 
     * @return statement    Update statement.
     */
    public String generateUpdateStatement(String concept1, String concept2, 
            int matcher, double simvalue) {
        
        String statement;
        statement = "UPDATE " + simValueDao.getTableName() + " SET "
                + "matcher"+ matcher +" = " + simvalue + " WHERE "
                + "ontologies ='"+ ontologiesName + "' and concept1 ='"
                + concept1 +"' and concept2 ='" + concept2 + "'";
        return statement;
    }
    
    /**
     * Availability of a column for a particular matcher in
     * the simValue table.
     * 
     * @param colname   Column name(eg. matcher0,matcher1 etc)
     * 
     * 
     * @return  Availability of column.
     */       
    public boolean isColumnAvailable(String colname) {
        
        String statement = "SHOW COLUMNS FROM " +simValueDao.getTableName()+ 
                " WHERE FIELD='"+colname+"'";        
        boolean availability = simValueDao.isColumnAvailable(statement, 
                selectConn);
        return availability;
    }
    
    /**
     * 
     * @param weight        weight for each matcher.
     * @param thershold     Filter level.
     * 
     * @return              List of suggestions. 
     */
    public ArrayList<String> getSuggestionsWeightedBased(double[] weight,
            double thershold) {
             
       ArrayList<String> suggestions = null;       
       String sqlMatchers = "((";        
       String sqlWeight = "(";
       String statement = "";
       int matchers = 0;        
       boolean hierarchyMatcherON = false;
       //To avoid combining sim values in HierarchyMatcher
       if (weight[AlgoConstants.HIERARCHY] !=0) {
           hierarchyMatcherON = true;
           weight[AlgoConstants.HIERARCHY] = 0;
        }
        
       /**
        * Generate part of a SQL Query 
        * Eg: sqlMatchers = (((matcher2*0.5) +(matcher3*0.5))
        *     sqlWeight = (0.5+0.5)).
        */       
       for (int i=0; i <weight.length; i++) {
           if (weight[i] != 0) {               
               if (matchers > 0) {
                   sqlMatchers = sqlMatchers+" + ";
                   sqlWeight = sqlWeight+" + ";
               }               
               sqlMatchers = sqlMatchers+"(matcher"+i+"*"+weight[i]+")";
               sqlWeight = sqlWeight+weight[i];
               matchers++;               
           }
       }
       sqlMatchers=sqlMatchers+")/";
       sqlWeight=sqlWeight+"))";
       //Adding HierarchyMatcher value to final sim value
       if (hierarchyMatcherON) {
           sqlWeight=sqlWeight+"+matcher"+AlgoConstants.HIERARCHY;                
       }
       statement = "SELECT concept1, concept2, "+sqlMatchers+sqlWeight+""
               + " as simvalue from "+ simValueDao.getTableName() +" WHERE"
               + " ontologies='"+ontologiesName+"' AND "+ sqlMatchers + 
               sqlWeight +">="+thershold;
       
       suggestions=simValueDao.getSimValueByCombination(statement, selectConn);
       return suggestions;    
    }
    
    /**
     * 
     * @param weight        weight for each matcher.
     * @param thershold     Filter level.
     * 
     * @return  List of suggestions.
     */
    public ArrayList getSuggestionsMaximumBased(double[] weight,
            double thershold) {
        

       
       ArrayList suggestions = null;        
       String sqlMatcher = "(GREATEST(";
       String statement;
       int matcher = 0;
       int noOFMatcher = 0;
       boolean HierarchyMatcherON = false;
       // The weight for matcher should be 1
       for (int i = 0; i < weight.length; i++) {
           if (weight[i]!= 0 && weight[i] != 1) {
               System.out.println("Invalid weight for matcher" + i);
               return null;
           }
           if (weight[i]!= 0 && i != AlgoConstants.HIERARCHY) {
               noOFMatcher++;
           }
       }
       //In case if the user select only one matcher then do WeightedBased.
       if (noOFMatcher == 1) {
           System.out.println("MaximumBased is not applicable for single matcher"
                   + "...So the system returning suggestions for "
                   + "WeightedBased");
           return getSuggestionsWeightedBased(weight, thershold);           
       }       
       //To avoid combining sim values in HierarchyMatcher
       if (weight[AlgoConstants.HIERARCHY] != 0) {
           HierarchyMatcherON = true;
           weight[AlgoConstants.HIERARCHY] = 0;
        }        
       /**
        * Generate part of a SQL Query
        * 
        * Eg.(GREATEST(matcher0, matcher1, matcher2))
        */ 
       
       for (int i = 0;i < weight.length; i++) {
           if (weight[i]!= 0) {
               if (matcher == 0) {
                   sqlMatcher = sqlMatcher + "matcher" + i;
               } else {
                   sqlMatcher = sqlMatcher + ", matcher" + i;
               }               
               matcher++;
           }
       }       
       sqlMatcher = sqlMatcher+")";       
       //Adding HierarchyMatcher value to final sim value
       if (HierarchyMatcherON) {
           sqlMatcher = sqlMatcher+"+matcher"+AlgoConstants.HIERARCHY+")";                
       } else  {
           sqlMatcher = sqlMatcher+")";                
       }
       /**
        * The final statement will looks like
        * 
        * "SELECT concept1, concept2, (GREATEST(matcher0, matcher1, matcher2)) 
        * as simvalue from dbsambo.savesimvalues WHERE 
        * ontologies='eye_MA_1#eye_MeSH_2' AND 
        * (GREATEST(matcher0, matcher1, matcher2))>=0.6"
        */     
       statement = "SELECT concept1, concept2, "+sqlMatcher+" as simvalue"
               + " from " + simValueDao.getTableName() +" WHERE ontologies"
               + "='"+ontologiesName+"' AND "+ sqlMatcher +">=" + thershold;        
           
        
       suggestions = simValueDao.getSimValueByCombination(statement, selectConn);
        
       
        return suggestions;
    }
}
    
