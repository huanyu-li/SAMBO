/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.testPair;

/**
 *
 * @author huali50
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class testSinglePairComputation {
    
    /**
     * Matchers.
     */
    private Matcher[] matchers = new Matcher[AlgoConstants.NO_OF_MATCHERS];
    /**
     * Linguistic Matchers.
     */
    private Matcher[] linsMatchers = new Matcher[AlgoConstants.LIN_MATCHER_NUM];
    /**
     * Concept pair whose similarity value need to be calculate.
     */
    private testPair pair;
    private testOntManager ontmanager;
    private testMOntology source_ontology;
    private testMOntology target_ontology;
    /**
     * Ontologies pair name.
     */
    private String ontologiesPairName;    
    /**
     * For querying the computation results.
     */
    private SimValueGenerateQuery simValueTable;        
    /**
     * SQL connection.
     */
    private Connection sqlConn;
    /**
     * Final similarity value for the concept pair.
     */ 
    private double finalSimValue = 0;
    public testSinglePairComputation(testOntManager ontmanager){
        this.ontmanager = ontmanager;
        source_ontology = this.ontmanager.getontology(Constants.ONTOLOGY_1);
        target_ontology = this.ontmanager.getontology(Constants.ONTOLOGY_2);
    }

    /**
     * This method calculates sim value for a single concept pair.
     * 
     * @param matcher       Matcher whose sim value is needed.
     * @param conceptPair   Concept pair.
     * @param ontologyPair  Name of the ontology pair.
     * @param conn          MySQL connection to access the database.
     * 
     * @return SimValue    Sim Value for the concept pair.            
     */

 
    
    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */ 


    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */
   
        
    /**
     * calculate similarity value with help of some dictionary,
     * e.x. a specific biological dictionary UMLS.
     */ 
 
    /**
     * calculate similarity values based a structural propagation algorithms
     */ 
    private void getStrValue() {                
        //(new HierarchySearch(this)).propogateSimValue();
    }
    

    
    /**
     * A linguistic matcher calculate similarity.
     */ 
   
    
    /**
     * This method calculate similarity value for the given concept pair
     * and the matcher list.
     * 
     * @param matcher       Matcher 
     * @param matcherList   In matchers like wordnet and wordlist the algorithm
     *                      will use more than one matcher(matching algorithm).
     * @param weight        Used for the matcher's like wordnet and wordlist. 
     */
    
    
    private void calculate(Matcher[] matcher_list, double[] values,
            String str1, String str2) {
                
        double value;        
        for (int i = 0; i < matcher_list.length; i++){   
            value = matcher_list[i].getSimValue(str1, str2);
            if(values[i] < value) {
                values[i] = value;
            }
        }
    }
    
    /**
     * This method create delay in a program.
     * 
     * @param delayTime Delay time in milli second. 
     */    
    private void delayLine(int i) {
        try {        
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SinglePairComputation.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
    }
    
}

