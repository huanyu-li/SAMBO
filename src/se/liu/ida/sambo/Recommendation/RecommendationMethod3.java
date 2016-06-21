/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Recommendation;

import java.util.ArrayList;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructorUserListPair;


/**
 * <p>
 * To compute recommendation method 3, the one which uses decision on mapping
 * suggestions alone.
 * </p>
 * 
 * @author  Rajaram
 * @version 1.0
 */
public class RecommendationMethod3 {
    
    
    /**
     * To calculate/access simvalue for the concept pairs.
     */
    private SimValueConstructorUserListPair simValueConstructor =new 
            SimValueConstructorUserListPair(RecommendationConstants.
            VALIDATED_SUGGESTIONS);
    /**
     * To calculate/access simvalue for the extra wrong suggestions.
     */
    private SimValueConstructorUserListPair simValueConstructorAdditionalB = 
            null;    
    /**
     * Use extra wrong mapping suggestions.
     */
    private boolean useExtraB = false;
    /**
     * If no extra wrong mapping suggestions are needed.
     */
    public RecommendationMethod3() {        
    }
    
    /**
     * If extra wrong mapping suggestions are needed.
     * 
     * @param maxWrongSuggestions   Maximum wrong suggestions the user want to 
     *                              generate. 
     */
    public RecommendationMethod3(double maxWrongSuggestions) {
        
        useExtraB = true;
        // Generating extra wrong mapping suggestions.
        GenerateWrongSuggestions generateB = new 
                GenerateWrongSuggestions(maxWrongSuggestions, 0.8, 0.3);
        
        generateB.editDistanceMethod();
        
               
        simValueConstructorAdditionalB = new 
            SimValueConstructorUserListPair(RecommendationConstants.
            GENERATED_REJECTED_SUGGESTIONS);
    }
            
   /**
     * This method return parameters A, B, C, D for an alignment strategy.
     * 
     * @param matchers      Matchers in an alignment strategy.
     * @param weights       Weight of the matchers.
     * @param combination   Combination type(weighted/maximum)
     * @param thresholds    Thresholds in an alignment strategy.
     * 
     * @return scoreParam   Which can be use to calculate recommendation score.
     */
    public double[] getParams(String matchers, String weights, 
            String combination, String thresholds) {
        //Split matchers from a strategy
        String[] matcher=matchers.split("\\;");
        //Split matchers weight from a strategy
        String[] weightStr=weights.split("\\;");
        //Spliting thresholds in strategy
        String[] thresholdstr=thresholds.split("\\;");            
        boolean isSingleThreshold;
        double singleThreshold = 0, upperThreshold = 0, lowerThreshold = 0;
        double[] weight=new double[weightStr.length];
        double[] scoreParam;
        
        if(thresholdstr.length > 1) {
            isSingleThreshold = false;                
            lowerThreshold = Double.valueOf(thresholdstr[0]).doubleValue();
            upperThreshold = Double.valueOf(thresholdstr[1]).doubleValue();
            } else {
            isSingleThreshold = true;
            singleThreshold = Double.valueOf(thresholdstr[0]).doubleValue();
        }
        // Convert weight values from string to double format.
        for (int i = 0; i < weightStr.length; i++) {
            weight[i]=Double.valueOf(weightStr[i]).doubleValue();
        }
        
        if (isSingleThreshold) {
            scoreParam = calculateParams(matcher, weight, 
                    singleThreshold, combination);
        } else {
            scoreParam = calculateParams(matcher, weight, 
                    upperThreshold, lowerThreshold, combination);
        }
        
        if (isSingleThreshold && useExtraB) {
            int extraB = calculateParamsAdditionalB(matcher, weight, 
                    singleThreshold, combination);
                       
            scoreParam[1] = scoreParam[1] + extraB;
        } else if(useExtraB){
            int extraB = calculateParamsAdditionalB(matcher, weight, 
                    upperThreshold, lowerThreshold, combination);
                       
            scoreParam[1] = scoreParam[1] + extraB;
        }
        
        return scoreParam;    
    }
    
    /**
     * 
     * @param strMatcher
     * @param weight
     * @param upperthreshold
     * @param lowerthreshold
     * @param combination
     * @return 
     */
    private double [] calculateParams(String[] strMatcher, double[] weight,
            double upperthreshold, double lowerthreshold, String combination) {
        
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
        double paramA = 0;
        double paramB = 0;
        double paramC = 0;
        double paramD = 0;        
        int[] matchers = new int[strMatcher.length];       
        double[] matcherWeights = new double[Constants.singleMatchers.length];
        
        //Convert matchers and their weight compartable for querying. 
        for (int i = 0; i < strMatcher.length; i++) {
            for(int j = 0;j < Constants.singleMatchers.length; j++) {
                if (strMatcher[i].equalsIgnoreCase(
                        Constants.singleMatchers[j])) {
                    matchers[i] = j;
                    matcherWeights[j] = weight[i];
                 }             
            }
        }        
        // Querying database for suggestions.
        ArrayList<String> suggestions = simValueConstructor.getSuggestions(
                matchers, combination, matcherWeights, upperthreshold,
                lowerthreshold);
        ArrayList<String> acceptedMappings = RecommendationConstants.
                ACCEPTED_SUGGESTIONS;
        ArrayList<String> rejectedMapping = RecommendationConstants.
                REJECTED_SUGGESTIONS;
        // Counting paramA and paramB
        for(String suggestion:suggestions) {
            if(acceptedMappings.contains(suggestion)) {
                paramA++;
            }
            else if(rejectedMapping.contains(suggestion)) {
                paramB++;
            }
        }
        // Counting paramC
        for(String accepted:acceptedMappings) {
            if(!suggestions.contains(accepted)) {
                paramC++;
            }
        }
        
        paramD = RecommendationConstants.VALIDATED_SUGGESTIONS.size()-
                (paramA+paramB+paramC);        
        double[] scoreParam = {paramA,paramB,paramC,paramD};        
        return scoreParam;
    }
    
    /**
     * 
     * @param strMatcher
     * @param weight
     * @param threshold
     * @param combination
     * @return 
     */
    private double[] calculateParams(String[] strMatcher, double[] weight,
            double threshold, String combination) {
             
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
        double paramA = 0;
        double paramB = 0;
        double paramC = 0;
        double paramD = 0;
        int[] matchers = new int[strMatcher.length];       
        double[] matcherWeights = new double[Constants.singleMatchers.length];
        
        //Convert matchers and their weight compartable for querying. 
        for (int i = 0; i < strMatcher.length; i++) {
            for(int j = 0;j < Constants.singleMatchers.length; j++) {
                if (strMatcher[i].equalsIgnoreCase(
                        Constants.singleMatchers[j])) {
                    matchers[i] = j;
                    matcherWeights[j] = weight[i];
                 }             
            }
        }        
        // Querying database for suggestions.
        ArrayList<String> suggestions = simValueConstructor.getSuggestions
                (matchers, combination, matcherWeights, threshold);
        ArrayList<String> acceptedMapping = RecommendationConstants
                .ACCEPTED_SUGGESTIONS;
        ArrayList<String> rejectedMapping = RecommendationConstants
                .REJECTED_SUGGESTIONS;
        // Counting paramA and paramB
        for (String suggestion:suggestions) {
            if(acceptedMapping.contains(suggestion)) {
                paramA++;
            }
            else if(rejectedMapping.contains(suggestion)) {
                paramB++;
            }
        }
        // Counting paramC
        for(String accepted:acceptedMapping) {
            if(!suggestions.contains(accepted)) {
                paramC++;
            }
        }        
        paramD = RecommendationConstants.VALIDATED_SUGGESTIONS.size()-
                (paramA+paramB+paramC);        
        double[] scoreParam = {paramA,paramB,paramC,paramD};         
        return scoreParam;    
        
    }
    
    /**
     * 
     * @param strMatcher
     * @param weight
     * @param upperthreshold
     * @param lowerthreshold
     * @param combination
     * @return 
     */
    private int calculateParamsAdditionalB(String[] strMatcher, 
            double[] weight, double upperthreshold, double lowerthreshold, 
            String combination) {
        
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
        int paramB = 0;        
        int paramD = 0;        
        int[] matchers = new int[strMatcher.length];       
        double[] matcherWeights = new double[Constants.singleMatchers.length];
        
        //Convert matchers and their weight compartable for querying. 
        for (int i = 0; i < strMatcher.length; i++) {
            for(int j = 0;j < Constants.singleMatchers.length; j++) {
                if (strMatcher[i].equalsIgnoreCase(
                        Constants.singleMatchers[j])) {
                    matchers[i] = j;
                    matcherWeights[j] = weight[i];
                 }             
            }
        }        
        // Querying database for suggestions.
        ArrayList<String> suggestions = simValueConstructorAdditionalB.
                getSuggestions(matchers, combination, matcherWeights, 
                upperthreshold, lowerthreshold);
        
        ArrayList<String> rejectedMapping = RecommendationConstants.
                GENERATED_REJECTED_SUGGESTIONS;
        // Counting paramA and paramB
        for(String suggestion:rejectedMapping) {
            if(suggestion.contains(suggestion)) {
                paramB++;
            }
            else  {
                paramD++;
            }
        }
        
        return paramB;
    }
    
    /**
     * 
     * @param strMatcher
     * @param weight
     * @param threshold
     * @param combination
     * @return 
     */
    private int calculateParamsAdditionalB(String[] strMatcher, 
            double[] weight, double threshold, String combination) {
             
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
        int paramB = 0;        
        int paramD = 0;
        int[] matchers = new int[strMatcher.length];       
        double[] matcherWeights = new double[Constants.singleMatchers.length];
        
        //Convert matchers and their weight compartable for querying. 
        for (int i = 0; i < strMatcher.length; i++) {
            for (int j = 0;j < Constants.singleMatchers.length; j++) {
                if (strMatcher[i].equalsIgnoreCase(
                        Constants.singleMatchers[j])) {
                    matchers[i] = j;
                    matcherWeights[j] = weight[i];
                 }             
            }
        }        
        // Querying database for suggestions.
        ArrayList<String> suggestions = simValueConstructorAdditionalB.
                getSuggestions(matchers, combination, matcherWeights, 
                threshold);
        
        ArrayList<String> rejectedMapping = RecommendationConstants.
                GENERATED_REJECTED_SUGGESTIONS;
        // Counting paramA and paramB
        for (String suggestion:rejectedMapping) {
            if (suggestions.contains(suggestion)) {
                paramB++;
            }
            else  {
                paramD++;
            }
        }
        
        return paramB;
        
    }
}
