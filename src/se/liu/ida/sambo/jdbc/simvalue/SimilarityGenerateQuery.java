/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.util.ArrayList;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;

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
    public String generateInsertStatement(int id, int matcher,double simvalue) {
        
        String statement="";               
        statement="INSERT INTO "+ getTableName(matcher) + "(id, "+ getColumnName(matcher) +") VALUES";                  
        statement=statement.concat("('"+id+"', "+simvalue+")");
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
    public void generateWeightedBasedSql(double[] weight, double thershold){
        boolean hierarchyMatcherON = false;
        String[] matcher = null;
        String condition = "";
        //To avoid combining sim values in HierarchyMatcher
        if (weight[AlgoConstants.HIERARCHY] !=0) {
           hierarchyMatcherON = true;
           weight[AlgoConstants.HIERARCHY] = 0;
        }
        int count = 0;
        for(int i = 0; i < weight.length; i++){
            if(weight[i] != 0){
                matcher[count] = getColumnName(i) + "*" + weight[i];
                count++;
            }
        }
        for(int j = 0; j < count; j++){
        
        }
    } 
}
