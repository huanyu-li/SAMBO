package se.liu.ida.sambo.util.testing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.PRAalg.dtfPRA;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.Pair;

/**
 * To access the database to get mapping suggestions for the alignment 
 * strategy.
 * 
 * (Note:) The complete computation results for all the matchers in the strategy
 * should be available in the database.
 * 
 * @author  Rajaram
 * @version 1.0
 */
public class EvaluateStrategiesSimAccess {
    /**
     * To manage the ontology contents.
     */
    private OntManager ontManager = AlgoConstants.ontManager;
    /**
     * Ontologies pair name.
     */
    private String ontologiesName;     
    /**
     * For querying the computation results of the matchers.
     */
    private SimValueGenerateQuery simValueTable;     
    /**
     * SQL server connection.
     */
    private Connection selectConn, updateConn;
    
    /**
     * <p>
     * This constructor establishes a connection to access the SQL server,
     * initialize the ontology contents etc.
     * </p>
     *    
     */
    public EvaluateStrategiesSimAccess() {
        
        selectConn = makeConnection();
        updateConn = makeConnection(); 
        ontologiesName = AlgoConstants.settingsInfo.getName(
                Constants.ONTOLOGY_1).concat("#").concat(
                AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
        simValueTable = new SimValueGenerateQuery(ontologiesName, selectConn);       
        
    }
    
    /**
     * This method create a new SQL server connection.
     *
     * @return conn     SQL server connection.
     */    
    private Connection makeConnection() {
        Connection conn = null;
        try {       
            conn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(EvaluateStrategiesSimAccess.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
                
        return conn;
    }


    
    /** 
     * To get mapping suggestions if more than one matcher is selected.     
     *
     * @param threshold     Single threshold value for filtering mapping
     *                      suggestions.
     * @param weight        weights of the matchers, if the matcher is not 
     *                      selected then its weight should be 0.
     * @param combination   combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */  

    private Vector getPairList(double[] weight, double threshold,
            String combination){
        // List of the final mapping suggestions to be return.       
        Vector suggestions = new Vector();        
        Object concept1 = "";
        Object concept2 = "";                
        String concept1ID = "";
        String concept2ID = "";        
        Connection selectConn = null;
        double finalSimValue = 0;       
        ArrayList<String> queryResult=null;        
        // Querying database.
        if (combination.equalsIgnoreCase("maximum")) {
            queryResult = simValueTable.getSuggestionsMaximumBased(
                    weight, threshold);                
        } else {
            queryResult = simValueTable.getSuggestionsWeightedBased(
                    weight, threshold);                
        }
        /**
         * The above query will return results in form of array list,
         * so in this step we will convert these results into concept pairs.
         */
        for (String data:queryResult) {
            String[] resultParams = data.split("#");
            concept1ID = resultParams[0];
            concept2ID = resultParams[1];
            finalSimValue = Double.valueOf(resultParams[2]).doubleValue();                    
            concept1 = null;
            concept2 = null;            
            // Quering ontoManager with conceptIDs.
            concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(concept1ID);
            concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(concept2ID);
            // Generating concept pairs and adding to suggestion list.
            if (concept1 != null && concept2 != null) {
                Pair pair = new Pair(concept1, concept2);                
                pair.setSim(finalSimValue);    
                suggestions.add(pair);
            }
        }   
        return suggestions;
    }
    
    
    /** 
     * To get mapping suggestions for the double threshold filtering, 
     * if more than one matcher is selected.     
     *
     * @param weight            weights of matchers, if the matcher is not 
     *                          selected then its weight should be 0.
     * @param upperthreshold    Higher threshold.
     * @param lowerthreshold    Lower threshold.
     * @param combination       combination method(weighted/maxBased).
     *
     * @return   List of pairs based on the combination of similarity 
     *           and threshold.
     */
    private Vector getPairList(double[] weight, double upperthreshold,
            double lowerthreshold, String combination) {
        // List of final mapping suggestions.        
        Vector suggestion = new Vector();
        // List of mapping suggestions between upper and lower threshold.
        Vector unprocessedSuggestions = new Vector();  
        // Getting suggestions above or equal to lower threshold.
        Iterator pairs = getPairList(weight, lowerthreshold,
                combination).iterator();
        /**
         * Separating suggestions.
         * (i.e) The suggestions with simvalue greater than or equals to the 
         * upper threshold will be added to the final suggestions list, 
         * the suggestions between upper and lower threshold will be added to 
         * the unprocessed suggestions list which will be further filtered 
         * by the dtf algorithm.
         */
        while (pairs.hasNext()) {            
            Pair pair = (Pair) pairs.next();            
            if (pair.getSim() >= upperthreshold) {
                suggestion.add(pair);
            } else {
                unprocessedSuggestions.add(pair);
            }            
        }
        /**
         * We don't have enough computation power.
         */
        if (suggestion.size() > 6000) {
            Vector emptyList = new Vector();
            
            return emptyList;
        }
        /**
         * We need suggestions greater than or equals to the upper threshold 
         * to get consistent group and of course the suggestions between 
         * upper and lower threshold to apply the dtf algorithm.
         */
        Iterator dftResult = null;
        if (suggestion.size() > 0 && unprocessedSuggestions.size() > 0) {
            dtfPRA dtf = new dtfPRA(ontManager.getMOnt(1),
                    ontManager.getMOnt(2), suggestion);
            dftResult = dtf.getResults(unprocessedSuggestions,
                    upperthreshold, lowerthreshold).iterator();         
        } else if (unprocessedSuggestions.size() > 0){
            dftResult = unprocessedSuggestions.iterator();
            
        }
        while (dftResult != null && dftResult.hasNext()) {
                suggestion.add(dftResult.next());
            }

        return suggestion;
    
        }
    
    
    /**
     * This method returns mapping suggestions for the user specified alignment
     * strategies.
     * 
     * Note : This method is used for the strategies that has double threshold
     * filtering.   
     * 
     * @param weight            Weights for matchers.
     * @param upperthreshold    Highest threshold level.
     * @param lowerthreshold    Lowest threshold level.
     * @param combination       Combination type(weighted/maximumBased).
     * 
     * @return  List of mapping suggestions(ArrayList).
     */        
    public  ArrayList<String> getSuggestions(double[] weight,
            double upperthreshold, double lowerthreshold, String combination) {
        
        Vector pairList = getPairList(weight, upperthreshold, lowerthreshold, 
                combination);
        
        ArrayList<String> suggestions = new ArrayList<String>();        
        Iterator itrPairs = pairList.iterator();     
        
        while (itrPairs.hasNext()) {
            Pair pair=(Pair)itrPairs.next();            
            String CID1 = null;
            String CID2 = null;
            CID1 = ((MElement) pair.getObject1()).getId();            
            CID2 = ((MElement) pair.getObject2()).getId();
            if(CID1 != null && CID2 != null) {
                suggestions.add(CID1+AlgoConstants.SEPERATOR+CID2);
            }
        }                
        return suggestions;        
    }
    
    /** 
     * This method returns mapping suggestions for the user specified alignment
     * strategies.     
     * 
     * @param weight        weights of matchers, if the matcher is not selected
     *                      then its weight should be 0.
     * @param threshold     Single threshold value for filtering mapping
     *                      suggestions.
     * @param combination   combination method(weighted/maxBased).
     *
     * @return   List of mapping suggestions.
     */
    public  ArrayList<String> getSuggestions(double[] weight, double threshold,
            String combination) {
        Vector pairList = getPairList(weight, threshold, combination);         
        
        ArrayList<String> suggestions = new ArrayList<String>();        
        Iterator itrPairs = pairList.iterator();     
        
        while (itrPairs.hasNext()) {
            Pair pair = (Pair)itrPairs.next();            
            String CID1 = null;
            String CID2 = null;
            CID1 = ((MElement) pair.getObject1()).getId();            
            CID2 = ((MElement) pair.getObject2()).getId();
            if (CID1 != null && CID2 != null) {
                suggestions.add(CID1+AlgoConstants.SEPERATOR+CID2);
            }            
        }                
        return suggestions;
    }
    /**
     * Close all SQL server connections created by instance of this class.
     */
    public void closeAllConnections() {
        ResourceManager.close(selectConn);
        ResourceManager.close(updateConn);
    }
}