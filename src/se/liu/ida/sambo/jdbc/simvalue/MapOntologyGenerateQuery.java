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
public class MapOntologyGenerateQuery {
    private MapOntologyDBAccess modbDao = new MapOntologyDBAccess();
    private java.sql.Connection Conn = null;
    protected String dataBaseName="dbsambo";
    public String getTableName() {
        return dataBaseName+".mappable_ontologies";	
    } 
    public MapOntologyGenerateQuery(java.sql.Connection sqlConn) {

        Conn = sqlConn; 
    }
    public String generateInsertStatement(String ontology1, String ontology2) {
        
        String statement="";               
        statement="INSERT INTO "+ getTableName() +
                "(sname, tname) VALUES";                  
        statement=statement.concat("('"+ontology1+"', '"+ontology2+"')");
        return statement;
    }
     public int getOPairId(String ontology1, String ontology2) {
        int opairId;                     
        String statement;   
            
        statement = "select moid from "+getTableName()+
                    " where sname='"+ ontology1 +"' and tname='"
                    + ontology2 +"'";            
                      
       opairId = modbDao.getOPairId(statement, Conn);      
        
       return opairId;
    }
     public void execute(String statement){
         modbDao.mapontologyinsert(statement, Conn);
     }
}
