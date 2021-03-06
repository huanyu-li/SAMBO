/*
 * MatchingAlgos.java
 *
 */

package se.liu.ida.sambo.algos.matching;

import java.util.ArrayList;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.AlignmentConstants;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructor;
import se.liu.ida.sambo.algos.matching.algos.SimValueConstructorMappableGrp;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @authors  He Tan, Rajaram
 * @version 2.0
 */
public class MatchingAlgos {
    /**
     * To access computation class.
     */
    private SimValueConstructor slotSimValues, classSimValues;
    /**
     * To access computation class when mappable groups are chosen.
     */
    private SimValueConstructorMappableGrp classSimValuesMGrp; 
    
    /** 
     * Create new MatchingAlgos. 
     */
    public MatchingAlgos(OntManager ontManager) {
        
        // Initailize sim value list for slots
        System.out.println("Init slot");         
        String step = "Init slot";
        slotSimValues = new SimValueConstructor(step, ontManager);        
        System.out.println("Finish init slot");
        
        // Initialize sim value list for classes
        System.out.println("Init class");        
        step = "Init class";
        classSimValues = new SimValueConstructor(step, ontManager);
        
        // Initialize mappable group
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            classSimValuesMGrp = new SimValueConstructorMappableGrp(step, 
                    ontManager);
        }
        System.out.println("Finish init class");        
        /**
         * To have a copy of OntManager, it will be used in the
         * sim value calculation of a single pair(used in the recommendations).
         */        
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            AlgoConstants.ontManager = ontManager;        
            int onto1Size = (ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getClasses()).size();
            int onto2Size = (ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getClasses()).size();        
            AlgoConstants.NO_OF_PAIRS = onto1Size*onto2Size;
        } else {
            int onto1Size = (ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getClasses()).size();
            int onto2Size = (ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getClasses()).size();        
            RecommendationConstants.NO_OF_PAIRS = onto1Size*onto2Size;
        }
    }    
    
    /**
     * This method sets location of learning matcher's corpus.
     * 
     * @param dir1  Ontology 1 corpus path.  
     * @param dir2  Ontology 2 corpus path.
     */
    public void setLearningDir(String dir1, String dir2){
        AlgoConstants.LEARNING_DIR1 = dir1;
        AlgoConstants.LEARNING_DIR2 = dir2;
    }    
    
    /**
     * To start computation for the slot matching.
     * 
     * @param matcher   Name of the matcher. 
     */
    public void calculateSlotSimValue(int matcher){
        slotSimValues.calculateSimilarityValue(matcher);
    }
    
    /**
     * To start computation for the class matching.
     * 
     * @param matcher   Name of the matcher. 
     */
    public void calculateClassSimValue(int matcher){
        classSimValues.calculateSimilarityValue(matcher);
    }
    
    /**
     * To start computation for the class matching, this process use only 
     * mappable groups.
     * 
     * @param matcher   Name of the matcher. 
     */
    public void calculateClassSimValueMGrp(int matcher){
        classSimValuesMGrp.calculateSimilarityValue(matcher);
    }
    
    /**
     * Used in the recommendation 1 method to get the reference alignment.
     * 
     * @return List of mapping suggestion.
     */ 
    public Vector getClassSugs(double threshould, int matcher){
        return classSimValues.getPairListSingleMatcher(threshould, matcher);
    }
    
    /**
     * To get mapping suggestions of the slot matching process.
     * 
     * @return List of mapping suggestion.
     */ 
    public Vector getSlotSugs(double[] weight, double threshold, 
            String combinationMethod) {                
        return slotSimValues.getPairListSegmentPairs(weight, threshold, 
                combinationMethod);
    }
    
    /**
     * To get mapping suggestions of the class matching process, used in both
     * computation and recommendation session.
     * 
     * @return List of mapping suggestion.
     */ 
    public Vector getClassSugs(double[] weight, double threshold, 
            String combinationMethod) {        
        
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            return classSimValues.getPairList(weight, threshold, 
                    combinationMethod);                
        } else {
            return classSimValues.getPairListSegmentPairs(weight, threshold, 
                    combinationMethod);                
        }    
    }
    
    /**
     * To get mapping suggestions of the class matching process, used in both
     * computation and recommendation session and used for the strategies that
     * has double threshold filtering.
     * 
     * @return List of mapping suggestion.
     */
    public Vector getClassSugs(double[] weight, double upperthreshold, 
            double lowerthreshold, String combinationMethod) {        
        
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            return classSimValues.getPairList(weight, upperthreshold, 
                    lowerthreshold, combinationMethod);                
        } else {
            return classSimValues.getPairListSegmentPairs(weight, 
                    upperthreshold, lowerthreshold, combinationMethod);                
        }
    }
    
    /**
     * To get mapping suggestions of the class matching process, used in 
     * mappable group case and used for the strategies that has double threshold 
     * filtering.
     * 
     * @return List of mapping suggestion.
     */
    public Vector getClassSugsMGBased(double[] weight, double upperthreshold, 
            double lowerthreshold, String combinationMethod) {
        return classSimValuesMGrp.getPairList(weight, upperthreshold, 
                lowerthreshold, combinationMethod);
    }
    
    /**
     * To get suggestions of the class matching process, used in mappable
     * group.
     * 
     * @return List of mapping suggestion.
     */
    public Vector getClassSugsMGBased(double[] weight, double threshold, 
            String combinationMethod) {        
        return classSimValuesMGrp.getPairList(weight, threshold, 
                combinationMethod);        
    }
    
    /**
     * Used in loading saved validation session. this method load un validated
     * suggestions of the slot concepts.
     * 
     * @return  List of un validated suggestions. 
     */
    public Vector loadSlotSugs(){
        return slotSimValues.loadPairList();
    }    
    
    /**
     * Used in loading saved validation session. this method load un validated
     * suggestions of the class concepts.
     * 
     * @return  List of un validated suggestions. 
     */
    public Vector loadClassSugs(){
        return classSimValues.loadPairList();
    }
  
    /**
     * This method set alignment status of a concept pair.
     * 
     * @param pair  Concept pair.
     */
    public void setAlignment(Pair pair){
             
        String concept1 = ((MElement) pair.getObject1()).getId(),
                        concept2 = ((MElement) pair.getObject2()).getId();
        
        AlignmentConstants.IsAligned.add(concept1 + AlgoConstants.SEPERATOR 
                + concept2);        
    }
    
    /**
     * This method used in the recommendation method 2, to get the 
     * user accepted suggestions.
     * 
     * @return  List of user accepted suggestions.
     */
    public ArrayList<String> getAcceptedPairs() {
        return classSimValues.getAcceptedPairs();
    }
    
    /**
     * This method used in the recommendation method 2, to get the 
     * user rejected suggestions.
     * 
     * @return  List of user rejected suggestions.
     */
    public ArrayList<String> getRejectedPairs() {
        return classSimValues.getPRARejection();
    }    
}