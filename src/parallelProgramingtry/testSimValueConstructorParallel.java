/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelProgramingtry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.testMClass;
import se.liu.ida.sambo.MModel.testMElement;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.Comb;
import se.liu.ida.sambo.algos.matching.algos.testSimValueConstructor;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.testPair;
/**
 *
 * @author huali50
 */




/**
 * <p>
 * To calculate the similarity value by parallel computation.
 * 
 * Note : This is a test class, it calculate the similarity value for 
 * all the concept pairs of two given ontology.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class testSimValueConstructorParallel implements Runnable{
    
    /**
     * For querying the computation results.
     */
    private SimValueGenerateQuery simValueTable;
    /**
     * Acts as a temporary database to store the concepts of an ontology.
     */
    private testOntManager ontmanager;
    private Set<Integer> source_content;
    private Set<Integer> target_content;
    /**
     * Ontologies pair name.
     */
    private String ontologiesName;   
    /**
     * Matcher name.
     */
    private int matcher;
    /**
     * For TermBasic and TermWN matchers.
     */
    private Matcher[] matcherList;
    /**
     * Weight of the matchers.
     */
    private double[] weight;
    /**
     * SQL connections.
     */
    private Connection insertConn, updateConn, selectConn;
    
    /**
     * This constructor initialize parameters for the matching process.
     * 
     * @param uOntology1Content
     * @param uOntology2Content
     * @param uMatcher
     * @param uMatcherList
     * @param uWeight 
     */
    public testSimValueConstructorParallel(testOntManager ontmanager,Set<Integer> uOntology1Content, 
            Set<Integer> uOntology2Content, int uMatcher, 
            Matcher[] uMatcherList, double[] uWeight)  {
        this.ontmanager = ontmanager;
        this.source_content = uOntology1Content;
        this.target_content = uOntology2Content;
        // Name of the ontology conceptPair. 
        ontologiesName = AlgoConstants.settingsInfo.getName(
                Constants.ONTOLOGY_1).concat(AlgoConstants.SEPERATOR).concat(
                AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));        
        matcher = uMatcher;
        matcherList = uMatcherList;
        weight = uWeight;
        insertConn = makeConnection();
        updateConn = makeConnection();
        selectConn = makeConnection();
        // To query the sim value table.
        simValueTable = new SimValueGenerateQuery(ontologiesName, selectConn);
    }
    
    @Override
    public void run() {
        
        //perform();
    }    
    
    
    /**
     * Create SQL server connection.
     *
     * @return conn     SQL server connection.
     */
    private Connection makeConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(testSimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    /**
     * Calculate similarity value for the two given ontology.
     */
    
   
    
    private void calculate(Matcher[] matcher_list, double[] values,
            String str1, String str2) {
        
        double value;        
        for (int i = 0; i < matcher_list.length; i++) {
            value = matcher_list[i].getSimValue(str1, str2);                        
            if (values[i] < value) {
                values[i] = value;
            }
        }
    }
    /**
     * This method create delay in program.
     * 
     * @param delayTime Delay time in milli second. 
     */
    private void delayLine(int delayTime) {
        try {        
            Thread.sleep(delayTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(testSimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        
    }
}

