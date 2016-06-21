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
    public double calculateSimilarityValue(int matcher, testPair conceptPair, 
            String ontologyPair, Connection conn) {        
        
        pair = conceptPair;
        ontologiesPairName = ontologyPair;
        sqlConn = conn;
        simValueTable = new SimValueGenerateQuery(ontologiesPairName, sqlConn);
        
        switch(matcher){
            case AlgoConstants.EDIT_DISTANCE:
                getEditDistanceValues();
                break;                
            case AlgoConstants.NGRAM:
                getNGramValues();
                break;                
            case AlgoConstants.LIN_COMBINATION:
                getLinValues(false);
                break;                
            case AlgoConstants.LIN_COMBINATION_PLUS:
                getLinValues(true);
                break;                
            case AlgoConstants.TERM_BASIC:
                getTermWNValues(false);
                break;                
            case AlgoConstants.TERM_WN:
                getTermWNValues(true);
                break;                
            case AlgoConstants.UMLS:
                getUMLSValue();
                break;                
            case AlgoConstants.HIERARCHY:
                getStrValue();
                break;                
            case AlgoConstants.LEARNING:
                getLearningValue();
                break;
            default:
                System.out.println("Invalid matcher");
                finalSimValue = -1;
                        
        }       
    
       return finalSimValue; 
    }

    private void getEditDistanceValues() {
        matchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();        
        perform(AlgoConstants.EDIT_DISTANCE, 
                matchers[AlgoConstants.EDIT_DISTANCE]);
    }

    private void getNGramValues() {
        matchers[AlgoConstants.NGRAM] =  new NGram(3);        
        perform(AlgoConstants.NGRAM, matchers[AlgoConstants.NGRAM]);
    }
    
    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */ 
    private void getLinValues(boolean WordNet_ON) {
        
        if (WordNet_ON) {            
            linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                    new Porter_WordNet(WordNet_ON); 
            linsMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linsMatchers[AlgoConstants.NGRAM] = 
                    new NGram(AlgoConstants.NGRAM_SIZE);         
            perform(AlgoConstants.LIN_COMBINATION_PLUS,
                    linsMatchers, AlgoConstants.WEIGHT_LIN);
        } else {            
            linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                    new Porter_WordNet(WordNet_ON);
            linsMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linsMatchers[AlgoConstants.NGRAM] =
                    new NGram(AlgoConstants.NGRAM_SIZE);
            perform(AlgoConstants.LIN_COMBINATION, linsMatchers,
                    AlgoConstants.WEIGHT_LIN);            
        }
    }

    /** 
     * calculate the linguistic sim value using Porter, 
     * Ngram and EditDistance individually.
     */
    private void getTermWNValues(boolean WordNet_ON) {      
        
        if (WordNet_ON) {         
            linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                    new Porter_WordNet(WordNet_ON); 
            linsMatchers[AlgoConstants.EDIT_DISTANCE] =  new EditDistance();
            linsMatchers[AlgoConstants.NGRAM] = 
                    new NGram(AlgoConstants.NGRAM_SIZE);
            perform(AlgoConstants.TERM_WN, linsMatchers,
                    AlgoConstants.WEIGHT_LIN);         
        } else {
            linsMatchers[AlgoConstants.PORTER_WORDNET] =  
                    new Porter_WordNet(WordNet_ON);                   
            linsMatchers[AlgoConstants.EDIT_DISTANCE] =  
                    new EditDistance();
            linsMatchers[AlgoConstants.NGRAM] = 
                    new NGram(AlgoConstants.NGRAM_SIZE); 
            perform(AlgoConstants.TERM_BASIC, linsMatchers,
                    AlgoConstants.WEIGHT_LIN);            
        }
    }
        
    /**
     * calculate similarity value with help of some dictionary,
     * e.x. a specific biological dictionary UMLS.
     */ 
    private void getUMLSValue() {     
        matchers[AlgoConstants.UMLS] = new UMLSKSearch_V6();         
        perform(AlgoConstants.UMLS, matchers[AlgoConstants.UMLS]);
        
    }
    /**
     * calculate similarity values based a structural propagation algorithms
     */ 
    private void getStrValue() {                
        //(new HierarchySearch(this)).propogateSimValue();
    }
    
    private void getLearningValue(){
               
        AlgoConstants.LEARNING_DIR1="/home/rajka62/"
                 + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                 + "CorpusForTesting/Corpus/100/Mouse";      
        
        AlgoConstants.LEARNING_DIR2="/home/rajka62/"
                + "Dataset_for_testing_rajaram_apr2012/anatomy_2011/"
                + "CorpusForTesting/Corpus/100/Human";
                  
        matchers[AlgoConstants.LEARNING] = new BayesLearning(
                AlgoConstants.LEARNING_DIR1, AlgoConstants.LEARNING_DIR2);        
        perform(AlgoConstants.LEARNING, matchers[AlgoConstants.LEARNING]);        
    }
    
    /**
     * A linguistic matcher calculate similarity.
     */ 
    private void perform(int matcher, Matcher matcher_single) {        
        Matcher[] matcher_list = {matcher_single};
        double[] weight = {1.0};
        perform(matcher, matcher_list, weight);        
    }
    
    /**
     * This method calculate similarity value for the given concept pair
     * and the matcher list.
     * 
     * @param matcher       Matcher 
     * @param matcherList   In matchers like wordnet and wordlist the algorithm
     *                      will use more than one matcher(matching algorithm).
     * @param weight        Used for the matcher's like wordnet and wordlist. 
     */
    private void perform(int matcher, Matcher[] matcherList, double[] weight){
        
        //pretty name
        String concept1Name = source_ontology.getElement(pair.getSource()).getPrettyName();
        String concept2Name = target_ontology.getElement(pair.getTarget()).getPrettyName();
        // Concept 1 synms. 
        String synm1;
        //concept ID
        String concept1ID = source_ontology.getElement(pair.getSource()).getLocalName();
        String concept2ID = target_ontology.getElement(pair.getTarget()).getLocalName();          
        double[] values = new double[weight.length];
        //To store multiple SQL statement.
        ArrayList<String> updateStatement = new ArrayList<String>();
        ArrayList<String> insertStatement = new ArrayList<String>();            
        String matcherColumnName = "matcher"+matcher;            
        boolean isColumnAvailable = simValueTable.isColumnAvailable(
                matcherColumnName);
        boolean isPairFound;
        
        if (!isColumnAvailable) {
            // create new column in the savesimvalue table
            simValueTable.createColumn(matcherColumnName);                
        }
        
        isPairFound=false;                
        /**
         * Accessing DB to find the sim value for the concept pair.
         * 
         * result[0]- Is the concept pair available in the DB.
         * result[1]- Is sim value for the concept pair available. 
         */
        boolean[] resultFromDB = simValueTable.getPairParams(concept1ID, 
                concept2ID, matcherColumnName, sqlConn);
        
        isPairFound=resultFromDB[0];
        // Calculating sim value with pretty name.
        calculate(matcherList, values, concept1Name, concept2Name);                
        //pretty synonyms
        if(source_ontology.getElement(pair.getSource()).isMClass() && target_ontology.getElement(pair.getTarget()).isMClass()){
            for(String symn1 : source_ontology.getMClass(pair.getSource()).getPrettySyn())
            {
                calculate(matcherList, values, symn1, concept2Name);
                for(String symn2 : target_ontology.getMClass(pair.getTarget()).getPrettySyn())
                {
                    calculate(matcherList, values, symn1,symn2);
                }
                for(String symn2 : target_ontology.getMClass(pair.getTarget()).getPrettySyn())
                {
                    calculate(matcherList, values, concept1Name,symn2);
                }
            }
        }
        finalSimValue = Comb.weight(values, weight);               
         /**
          * Creating new row in the database if the concept pair
          * is not found in the DB.
          */                          
        if (!isPairFound) {
            String statement = simValueTable.generateInsertStatement(
                    concept1ID, concept2ID, matcher, finalSimValue);
            insertStatement.add(statement);                
        }
         /**
          * If the concept pair is found in the DB then update 
          * its matcher value in the data base.
          */
        else if (isPairFound) {
            String statement = simValueTable.generateUpdateStatement(
                    concept1ID, concept2ID, matcher, finalSimValue);
            updateStatement.add(statement);
        }
        
        if (insertStatement.size() > 0) {
            simValueTable.executeStatements(insertStatement, sqlConn);
            //delayLine(100);                
        }
        if (updateStatement.size() > 0) {
            simValueTable.executeStatements(updateStatement, sqlConn);
            //delayLine(100);                
        }  
    }
    
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

