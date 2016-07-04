/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.jdbc.simvalue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructor;
import se.liu.ida.sambo.jdbc.ResourceManager;

/**
 *
 * @author huali50
 */
public class localtest {
    public static void main(String args[]){
        String statement = null;
        Connection sqlConn = null;
        MapOntologyGenerateQuery mapontologyTable;
        MapConceptGenerateQuery mapconceptTable;
        SimilarityGenerateQuery similarityTable;
        sqlConn = makeConnection();
        mapontologyTable = new MapOntologyGenerateQuery(sqlConn );
        mapconceptTable = new MapConceptGenerateQuery(sqlConn );
        similarityTable = new SimilarityGenerateQuery(sqlConn);
        int moid = mapontologyTable.getOPairId("ontology1", "ontology2");
        //mapconceptTable.singleinsert(mapconceptTable.generateInsertStatement(moid, "o1_ear", "o2_ear"));
        int mcid = mapconceptTable.getCPairId(moid, "o1_ear", "o2_ear");
        System.out.println(mcid);
        similarityTable.singleinsert(similarityTable.generateInsertStatement(mcid, 0.32, 0));
        similarityTable.singleinsert(similarityTable.generateInsertStatement(mcid, 0.56, 1));
        
        //statement = mapontologyTable.generateInsertStatement("ontology1", "ontology2");
        //mapontologyTable.execute(statement);
        //statement = mapontologyTable.generateInsertStatement("ontology3", "ontology4");
        //mapontologyTable.execute(statement);
        
        
        
        
    }
    
    private static Connection makeConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return conn;
    }
}
