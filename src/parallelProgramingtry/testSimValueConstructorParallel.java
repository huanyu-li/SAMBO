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
        
        perform();
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
    private void perform() {
        
         
        //To store multiple update statement
        ArrayList<String> updateStatements = new ArrayList<String>();
        //To store multiple insert statement
        ArrayList<String> insertStatements = new ArrayList<String>();
        String matcherColumnName = "matcher" + matcher;
        String concept1, concept2;
        testPair pair;
        double[] values;
        boolean simValueFound, isPairFound;
        //Final sim Value
        double simValues;
        //pretty name
        String name1 , name2;
        //conceptID
        String concept1ID , concept2ID;                
        //Synms for concept1
        String synm1;
        testMOntology source_ontology = ontmanager.getontology(Constants.ONTOLOGY_1);
        testMOntology target_ontology = ontmanager.getontology(Constants.ONTOLOGY_2);
        for(Integer i : source_content)
        {
            for(Integer j : target_content)
            {
                concept1 = source_ontology.getURITable().getURI(i);
                concept2 = target_ontology.getURITable().getURI(j);
                pair = new testPair(concept1,concept2);   
                name1 = source_ontology.getElement(concept1).getPrettyName();
                name2 = target_ontology.getElement(concept2).getPrettyName();
                concept1ID = source_ontology.getElement(concept1).getLocalName();
                concept2ID = source_ontology.getElement(concept2).getLocalName();
                values = new double[weight.length];                
                
                simValueFound = false; 
                isPairFound = false;
                /**
                 * Accessing DB to find the sim value for the concept pair.
                 * 
                 * result[0]- Is the concept pair available in the DB.
                 * result[1]- Is sim value for the conceptPair available. 
                 */  
                boolean[] resultFromDB = simValueTable.getPairParams(concept1ID,
                        concept2ID, matcherColumnName, selectConn);
                isPairFound = resultFromDB[0];
                simValueFound = resultFromDB[1];
                if (!simValueFound) {
                    calculate(matcherList, values, name1, name2);
                    
                    //pretty synonyms
                    if(source_ontology.getElement(concept1).isMClass() && target_ontology.getElement(concept2).isMClass()){
                        for(String symn1 : source_ontology.getMClass(concept1).getPrettySyn())
                        {
                            calculate(matcherList, values, symn1, name2);
                            for(String symn2 : target_ontology.getMClass(concept2).getPrettySyn())
                            {
                                calculate(matcherList, values, symn1, symn2);
                            }
                            for(String symn2 : target_ontology.getMClass(concept2).getPrettySyn())
                            {
                                calculate(matcherList, values, name1, symn2);
                            }
                        }
                    }
                    simValues = Comb.weight(values, weight);
                     /**
                       * Creating new row in the data base if the concept pair
                       * was not found in the DB.
                       */
                    if(!isPairFound) {
                        String statement = simValueTable.generateInsertStatement
                                (concept1ID, concept2ID, matcher, simValues);
                        insertStatements.add(statement);
                    }
                    /**
                     * If the concept pair is found in the DB then update 
                     * its matcher value in the data base.
                     */
                    else if (isPairFound) {
                        String statement = simValueTable.generateUpdateStatement
                                (concept1ID, concept2ID, matcher, simValues);
                        updateStatements.add(statement);
                    }
                }
            }
        }

        //If we have atleast one insert statement.
        if (insertStatements.size() > 0) {
            simValueTable.executeStatements(insertStatements, insertConn);
            if(matcherList.length < 3) {
                delayLine(50);
            }
        }
        //If we have atleast one update statement.
        if (updateStatements.size() > 0) {
            simValueTable.executeStatements(updateStatements, updateConn);
            if (matcherList.length < 3) {
                delayLine(50);
            }
        }
    }
    
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

