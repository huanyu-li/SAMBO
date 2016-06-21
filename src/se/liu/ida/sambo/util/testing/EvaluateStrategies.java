/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util.testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.jdbc.ResourceManager;

/**
 * <p>
 * Evaluate alignment strategies using reference alignment file and save the
 * evaluation results in the database.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class EvaluateStrategies {
    
    /**
      * Parameters to calculate the distance.
      *
      *           |      real
      *           | 
      *           |  cor   wrg    
      *       ----|-------------
      *  a   cor  |   A     B
      *  l        |
      *  g   wrg  |   C     D  
      */
    private double paramA = 0, paramB = 0, paramC = 0, paramD = 0;
    /**
     * Evaluation parameters.
     */
    private double precisionPlus = 0, recallPlus = 0, precisionMinus = 0, 
            recallMinus = 0, score1 = 0, score2 = 0;
    /**
     * To extract reference alignment.
     */
    private ExtractReferenceAlignmentFile refAlign = 
                new ExtractReferenceAlignmentFile();
    /**
     * Reference alignment.
     */
    private ArrayList<String> acceptedMapping = refAlign
            .getReferenceAlignment();
    /**
     * SQL server connection.
     */
    private Connection sqlConn = createConnection();
    /**
     * To calculate/access simvalue for the concept pairs.
     */
    private EvaluateStrategiesSimAccess simValueConstructor = new 
            EvaluateStrategiesSimAccess();
    /**
     * Evaluation results table name.
     */
    private final String tableName="dbsambo.evaluatestrategies";
    /**
     * SQL insert statement.
     */
    private final String sqlInsert = "INSERT INTO " + tableName + " "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * SQL select statement.
     */
    private final String sqlSelect = "SELECT * FROM " + tableName + " WHERE "
            + "matcher= ? AND weight= ? AND combination= ? AND threshold= ?";
    /**
     * Insert statement.
     */
    private PreparedStatement insertStmt;
    /**
     * Select statement.
     */
    private PreparedStatement selectStmt;
    
    /**
     * 
     */
    public EvaluateStrategies() {
        try {
            insertStmt = sqlConn.prepareStatement(sqlInsert);
            selectStmt = sqlConn.prepareStatement(sqlSelect);
        } catch (SQLException ex) {
            Logger.getLogger(EvaluateStrategies.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
    }
        
        
    /**
     * This method will start the evaluation process.
     */
    public void startEvaluation() {
        
        ArrayList<String> evaluationResult = new ArrayList();    
        GenerateAlignmentStrategies gAlgnStrag = 
                new GenerateAlignmentStrategies();
        evaluationResult = gAlgnStrag.getStrategies();        
        int i = 0;
        
        for (String s:evaluationResult) {            
            i++;
            System.out.println("S.No"+(i));
            System.out.println("Strategy "+s);
                    
            String strParams[] = s.split(",");
            
            if (!isStrategyEvaluated(s)) {
                
                double[] scoreParam = getParams(
                        strParams[0], strParams[1], strParams[2], strParams[3]);
                
                precisionPlus = (scoreParam[0]/(scoreParam[0]+scoreParam[1]));
                recallPlus = (scoreParam[0]/(scoreParam[0]+scoreParam[2]));
                precisionMinus = (scoreParam[3]/(scoreParam[2]+scoreParam[3]));
                recallMinus = (scoreParam[3]/(scoreParam[1]+scoreParam[3]));
                score1 = (scoreParam[0] + scoreParam[3])/
                        (scoreParam[0] + scoreParam[1] + scoreParam[2] 
                        + scoreParam[3]);
                score2 = scoreParam[0]/(scoreParam[0] + scoreParam[1] 
                        + scoreParam[2]);
                
                double[] evalParams = {paramA, paramB, paramC, paramD, 
                    recallPlus, precisionPlus, recallMinus, precisionMinus,
                    score1, score2};
                
                insert(s, evalParams);
            }
        }
        closeConnection();
    }
    
    /**
     * To evaluate single alignment strategy.
     * 
     * @param strategy      Single alignment strategy.
     */
    public void evaluateStrategy(String strategy) {        
        
        
        System.out.println("Strategy " + strategy);
        String strParams[]=strategy .split(",");
        
        if(!isStrategyEvaluated(strategy)) {
            double[] scoreParam = getParams(
                    strParams[0], strParams[1], strParams[2], strParams[3]);
            
            precisionPlus = (scoreParam[0]/(scoreParam[0]+scoreParam[1]));
            recallPlus = (scoreParam[0]/(scoreParam[0]+scoreParam[2]));
            precisionMinus = (scoreParam[3]/(scoreParam[2]+scoreParam[3]));
            recallMinus = (scoreParam[3]/(scoreParam[1]+scoreParam[3]));
            score1 = (scoreParam[0] + scoreParam[3])/
                    (scoreParam[0] + scoreParam[1] + scoreParam[2]
                    + scoreParam[3]);
            score2 = scoreParam[0]/(scoreParam[0] + scoreParam[1]
                    + scoreParam[2]);
        }
        closeConnection();
    }
    
    
     /**
     * This method returns parameters A, B, C, D for a given alignment 
     * strategy.
     * 
     * @param matchers      Matchers in the alignment strategy.
     * @param weights       Weights for the matchers.
     * @param combination   Combination type(weighted/maximum)
     * @param thresholds    Thresholds in the alignment strategy.
     * 
     * @return      scoreParam which will be used in the recommendation score
     *              calculation.
     */
    private double[] getParams(String matchers, String weights, 
            String combination, String thresholds) {
        //Split matchers from a strategy
        String[] matcher = matchers.split("\\;");
        //Split matchers weights from a strategy
        String[] weightStr = weights.split("\\;");
        //Split thresholds from a strategy
        String[] thresholdstr = thresholds.split("\\;"); 
        // Mapping suggestions.
        ArrayList<String> suggestions = new ArrayList<String>(); 
        boolean isSingleThreshold;
        double singleThreshold = 0, upperThreshold = 0, lowerThreshold = 0;
        double[] weight = new double[weightStr.length];
        double[] finalWeights = new double[Constants.singleMatchers.length];
        
        if(thresholdstr.length > 1) {
            isSingleThreshold = false;                
            lowerThreshold = Double.valueOf(thresholdstr[0]).doubleValue();
            upperThreshold = Double.valueOf(thresholdstr[1]).doubleValue();
            } else {
            isSingleThreshold = true;
            singleThreshold = Double.valueOf(thresholdstr[0]).doubleValue();
        }
        // Converting weight values from string to double format.
        for (int i = 0; i < weightStr.length; i++) {
            weight[i]=Double.valueOf(weightStr[i]).doubleValue();
        } 
        //Converting weights format compartable for querying. 
        for (int i = 0; i < matcher.length; i++) {
            for(int j = 0;j < Constants.singleMatchers.length; j++) {
                if (matcher[i].equalsIgnoreCase(
                        Constants.singleMatchers[j])) {                    
                    finalWeights[j] = weight[i];
                 }             
            }
        }
        
        if (isSingleThreshold) {
            suggestions = simValueConstructor.getSuggestions(finalWeights, 
                    singleThreshold, combination);
        } else {
            suggestions = simValueConstructor.getSuggestions(finalWeights, 
                    upperThreshold, lowerThreshold, combination);
        }
        
        calculateParams(suggestions);
        double[] scoreParam = {paramA,paramB,paramC,paramD};
        
        return scoreParam;    
    }
    
    /**
     * Calculate score parameters with respect to the mapping suggestions.
     * 
     * @param suggestions   List of mapping suggestions.
     * @return 
     */
    private void calculateParams(ArrayList<String> suggestions) {
              
        paramA = 0;
        paramB = 0;
        paramC = 0;
        paramD = 0;
        
        for (String suggestion:suggestions)  {
            if(acceptedMapping.contains(suggestion)) {
                paramA++;
            } else {
                paramB++;
            }
        }       
        
        for (String accept:acceptedMapping) {
            if(!suggestions.contains(accept)) {
                paramC++;
            }
        }        
        paramD = (AlgoConstants.NO_OF_PAIRS)-(paramA + paramB + paramC);
    }
    
    /**
     * Create a new SQL server connection.
     */
    private Connection createConnection() {
        Connection conn = null;
        try {
            conn = ResourceManager.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }
    
    /**
     * Close SQL server connection created by the instance of this class.
     */
    private void closeConnection() {        
        try {
            ResourceManager.close(insertStmt);
            ResourceManager.close(selectStmt);
            ResourceManager.close(sqlConn);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Insert strategy evaluation result into the database.
     * 
     * @param Strategy  Alignment Strategy.
     */
    private void insert(String Strategy, double[] evalParams) {        
        
        String[] straParams=Strategy.split(",");        
        try {
            int index = 1;
            
            for(int i = 0; i < straParams.length; i++) {
                insertStmt.setString(index, straParams[i]);
                index++;
            }
            for(int i = 0; i < evalParams.length; i++) {
                insertStmt.setString(index, Double.toString(evalParams[i]));
                index++;
            }
            
            insertStmt.execute();
        } catch (Exception _e) {
            _e.printStackTrace();
        }
    }
    
    /**
     * Check if the strategy is evaluated by checking its availability in the
     * database.
     * 
     * @param Strategy  Alignment Strategy.
     * 
     * @return  Returns true if the strategy found in the database.
     */
    private  boolean isStrategyEvaluated(String Strategy) {        
        
	ResultSet queryResult = null;
        boolean resultAvailablity = false;
        String[] straParams = Strategy.split(",");
        
        try {            
            int index = 1; 
            
            for (int i = 0; i < straParams.length; i++) {
                selectStmt.setString(index, straParams[i]);
                index++;
            }
            
            queryResult = selectStmt.executeQuery();
            
            while (queryResult.next()) {
                resultAvailablity = true;
            }
            queryResult.close();            
        } catch (Exception _e) {
            _e.printStackTrace();
        }
        return resultAvailablity; 
    }
}
