/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelProgramingtry;

import com.objectspace.jgl.OrderedMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.Comb;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructor;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.Pair;

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
public class SimValueConstructorParallel implements Runnable{
    
    /**
     * For querying the computation results.
     */
    private SimValueGenerateQuery simValueTable;
    /**
     * Acts as a temporary database to store the concepts of an ontology.
     */
    private OrderedMap ontology1Content, ontology2Content;
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
    public SimValueConstructorParallel(OrderedMap uOntology1Content, 
            OrderedMap uOntology2Content, int uMatcher, 
            Matcher[] uMatcherList, double[] uWeight)  {
        
        ontology1Content = uOntology1Content;
        ontology2Content = uOntology2Content;
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
            Logger.getLogger(SimValueConstructor.class.getName()).log(
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
        Object concept1, concept2;
        Pair pair;
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
        
         for(Enumeration e1 = ontology1Content.elements(); 
                 e1.hasMoreElements();) {
             
             concept1 = e1.nextElement();
              for(Enumeration e2 = ontology2Content.elements(); 
                      e2.hasMoreElements();) {
                  concept2 = e2.nextElement();
                  
                  pair = new Pair(concept1,concept2);                  
                  // Getting pretty name
                 name1 = ((MElement) pair.getObject1()).getPrettyName();
                 name2 = ((MElement) pair.getObject2()).getPrettyName();
                 //concept ID
                 concept1ID = ((MElement) pair.getObject1()).getId();
                 concept2ID = ((MElement) pair.getObject2()).getId();                        
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
                    if (((MElement) pair.getObject1()).isMClass() && 
                            ((MElement) pair.getObject2()).isMClass()) {

                        for (Enumeration en1 = ((MClass) pair.getObject1()).
                                getPrettySyn().elements(); 
                                en1.hasMoreElements();) {
                            
                            synm1 = (String) en1.nextElement();
                            calculate(matcherList, values, synm1, name2);

                            for (Enumeration en2 = ((MClass) pair.getObject2()).
                                    getPrettySyn().elements(); 
                                    en2.hasMoreElements();) {
                                calculate(matcherList, values, synm1, 
                                        (String) en2.nextElement());
                            }
                        }
                        
                        for (Enumeration en2 = ((MClass) pair.getObject2()).
                                getPrettySyn().elements(); 
                                en2.hasMoreElements();) {
                            calculate(matcherList, values, name1, 
                                    (String) en2.nextElement());
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
                if (insertStatements.size() > 100000) {
                    simValueTable.executeStatements(insertStatements, 
                            insertConn);
                    /**
                     * Computation of matchers like EditDistance and NGram are
                     * faster so delay will make sure the sim values are stored
                     * in DB.
                     */ 
                    if(matcherList.length<3) {
                        delayLine(50);
                    }
                    insertStatements.clear();
                }
                if (updateStatements.size() > 100000) {
                    simValueTable.executeStatements(updateStatements, 
                            updateConn);
                    if (matcherList.length < 3) {
                        delayLine(50);
                    }
                    updateStatements.clear();
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
            Logger.getLogger(SimValueConstructor.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        
    }
}
