/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.ui.web.Constants;

/**
 *
 * @author huali50
 */
public class SimilarityGenerateQuery {
    private SimilarityDBAccess simvalueDao = new SimilarityDBAccess();
    private java.sql.Connection Conn = null;
    protected String dataBaseName="dbsambo";
    public SimilarityGenerateQuery(java.sql.Connection sqlConn) {

        Conn = sqlConn; 
    }
    public String getTableName(int matcher) {
        String tablename=null;
        switch(matcher){
            case AlgoConstants.EDIT_DISTANCE:
                tablename = dataBaseName+".simvalue_ed";
                break;
            case AlgoConstants.NGRAM:
                tablename = dataBaseName+".simvalue_ng";
                break;
        }
        return tablename;
    }  
    public String getColumnName(int matcher){
        String column_name = null;
        switch(matcher){
            case AlgoConstants.EDIT_DISTANCE:
                column_name = "simvalue_ed";
                break;
            case AlgoConstants.NGRAM:
                column_name = "simvalue_ng";
                break;
        }
        return column_name;
    }
    public String generateInsertStatement(int id,int moid, int matcher,double simvalue) {
        
        String statement="";               
        statement="INSERT INTO "+ getTableName(matcher) + "(id, moid "+ getColumnName(matcher) +") VALUES";                  
        statement=statement.concat("('"+id+"', '"+moid+"', "+simvalue+")");
        return statement;
    }
    
    public String generateUpdateStatement(int id, int matcher, double simvalue) {
        
        String statement;
        statement = "UPDATE " + getTableName(matcher) + " SET " +getColumnName(matcher) + "= " + simvalue + " WHERE "
                + "id ="+ id;
        return statement;
    }
    public String generateSelectMulti(int id, int[] matcher, double[] weight){
        String statement = null;
        
        return statement;
    }
    public String generateMultiSelect(int id, int[] matcher, int[] weight){
        String statement = "";
        
        return statement;
    }
    public int getSimvalueId(int id,int matcher){
        int sId;                     
        String statement;   
            
        statement = "select id from "+getTableName(matcher)+" where id='"+ id +"'";            
                      
        sId = simvalueDao.getSimvalueId(statement, Conn);      
        
        return sId;
    }
    public void singleinsert(String statement){
        simvalueDao.singleinsert(statement, Conn);
    }
    public void executeStatements(ArrayList<String> statements) {
        simvalueDao.multipleUpdate(statements, Conn);
    }
    public ArrayList<String> generateWeightedBasedSql(double[] weight, double thershold, int step, int moid){
        
        ArrayList<String> mappable_concepts = null;
        boolean hierarchyMatcherON = false;
        String[] matcher = null;
        String condition = "";
        String sqlMatchers = "((";        
        String sqlWeight = "(";
        String statement = "";
        int matchers = 0;
        
        //To avoid combining sim values in HierarchyMatcher
        if (weight[AlgoConstants.HIERARCHY] !=0) {
           hierarchyMatcherON = true;
           weight[AlgoConstants.HIERARCHY] = 0;
        }
        
        for (int i=0; i <weight.length; i++) {
           if (weight[i] != 0) {               
               if (matchers > 0) {
                   sqlMatchers = sqlMatchers+" + ";
                   sqlWeight = sqlWeight+" + ";
               }               
               sqlMatchers = sqlMatchers+"("+ getColumnName(i) +"*"+weight[i]+")";
               sqlWeight = sqlWeight+weight[i];
               matchers++;               
           }
        }
        sqlMatchers=sqlMatchers+")/";
        sqlWeight=sqlWeight+"))";
        if (hierarchyMatcherON) {
           sqlWeight=sqlWeight+"+matcher"+AlgoConstants.HIERARCHY;                
        }
        String viewname= null;
        if(step == Constants.STEP_SLOT)
            viewname = "dbsambo.similarity_view_property";
        else if(step == Constants.STEP_CLASS)
            viewname = "dbsambo.similarity_view_class";
        statement = "SELECT id, " + sqlMatchers+sqlWeight+ " as simvalue from " +viewname +" where moid= "+ moid +" and type =" + step + " and " + sqlMatchers+sqlWeight+ ">="+thershold;
        mappable_concepts = simvalueDao.getSimvalueViewIdandValue(statement, Conn);
        return mappable_concepts;
    } 
    public ArrayList<String> generateMaximumBasedSql(double[] weight, double thershold, int step, int moid){
        ArrayList<String> mappable_concepts = null;
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
            return generateWeightedBasedSql(weight, thershold,step, moid);           
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
                    sqlMatcher = sqlMatcher + getColumnName(i);
                } else {
                    sqlMatcher = sqlMatcher + ", " + getColumnName(i);
                }               
                matcher++;
            }
        }       
        sqlMatcher = sqlMatcher+")";       
       //Adding HierarchyMatcher value to final sim value
        if (HierarchyMatcherON) {
           //sqlMatcher = sqlMatcher+"+matcher"+AlgoConstants.HIERARCHY+")";                
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
        statement = "SELECT id, "+sqlMatcher+" as simvalue from dbsambo.similarity_view WHERE "+ sqlMatcher +">=" + thershold; 
        mappable_concepts = simvalueDao.getSimvalueViewIdandValue(statement, Conn);
        return mappable_concepts;
    }
}
