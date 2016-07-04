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
    public String generateInsertStatement(int id, double simvalue,int matcher) {
        
        String statement="";               
        statement="INSERT INTO "+ getTableName(matcher) +
                "(id, similarity) VALUES";                  
        statement=statement.concat("('"+id+"', "+simvalue+")");
        return statement;
    }
    
    public String generateUpdateStatement(int id, int matcher, double simvalue) {
        
        String statement;
        statement = "UPDATE " + getTableName(matcher) + " SET "
                + "similarity = " + simvalue + " WHERE "
                + "id ='"+ id;
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
    public void singleinsert(String statement){
        simvalueDao.singleinsert(statement, Conn);
    }
    public void executeStatements(ArrayList<String> statements,java.sql.Connection conn) {
        simvalueDao.multipleUpdate(statements, conn);
    }
}
