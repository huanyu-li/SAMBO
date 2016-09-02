/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.util.ArrayList;

/**
 *
 * @author huali50
 */
public class MapConceptGenerateQuery {
    private MapConceptDBAccess mcdbDao = new MapConceptDBAccess();
    private java.sql.Connection Conn = null;
    protected String dataBaseName="dbsambo";
    public MapConceptGenerateQuery(java.sql.Connection sqlConn) {

        Conn = sqlConn; 
    }
    /**
     * Get Table name
     * @author huali50
     * @return table name
     */
    public String getTableName() {
        return dataBaseName+".mappable_concepts";	
    }  
    /**
     * Generate insert statement
     * @author huali50
     * @param moid
     * @param concept1
     * @param concept2
     * @param step
     * @return statement
     */
    public String generateInsertStatement(int moid, String concept1, String concept2, int step) {
        
        String statement="";               
        statement="INSERT INTO "+ getTableName() +
                "(moid, scname, tcname, type) VALUES";                  
        statement=statement.concat("("+moid+","+"'"+concept1+"',"+"'"+concept2+"', "+ step +")");
        return statement;
    }
    public void singleinsert(String statement){
        mcdbDao.singleinsert(statement, Conn);
    }
    /**
     * Get Concept pair Id
     * @author huali50
     * @param moid
     * @param concept1
     * @param concept2
     * @return cpairId
     */
     public int getCPairId(int moid,String concept1, String concept2) {
        int cpairId;                     
        String statement;   
            
        statement = "select mcid from "+getTableName()+
                    " where moid ="+moid+" and scname='"+ concept1 +"' and tcname='"
                    + concept2 +"'";            
                      
       cpairId = mcdbDao.getCPairId(statement, Conn);      
        
       return cpairId;
    }
     /**
      * Execute statements insert
      * @author huali50
      * @param statements 
      */
    public void executeStatements(ArrayList<String> statements) {
        mcdbDao.multipleUpdate(statements, Conn);
    }
    /**
     * Get Concepts
     * @author huali50
     * @param mcid
     * @return souce names and target names
     */
    public String getconcepts(int mcid){
        String statement = "Select scname, tcname from " + getTableName() + " where mcid = "+ mcid;
        return mcdbDao.getconcepts(statement, Conn);
    }
}
