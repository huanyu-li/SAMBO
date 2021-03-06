/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.testMergerManager;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.AlignmentConstants;
import se.liu.ida.sambo.algos.matching.algos.testSimValueConstructor;
import se.liu.ida.sambo.algos.matching.algos.testSimValueConstructorMappableGrp;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.testPair;
/**
 *
 * @author huali50
 */


public class testMatchingAlgos {
    /**
     * To access computation class.
     */
    private testSimValueConstructor slotSimValues, classSimValues;
    /**
     * To access computation class when mappable groups are chosen.
     */
    private testSimValueConstructorMappableGrp classSimValuesMGrp; 
    private testMergerManager merge;
    
    /** 
     * Create new MatchingAlgos. 
     */
    public testMatchingAlgos(testOntManager ontManager,testMergerManager merge) {
        this.merge = merge;
        
        // Initailize sim value list for slots
        System.out.println("Init slot");         
        String step = "Init slot";
        slotSimValues = new testSimValueConstructor(step, ontManager,merge);        
        System.out.println("Finish init slot");
        
        // Initialize sim value list for classes
        System.out.println("Init class");        
        step = "Init class";
        classSimValues = new testSimValueConstructor(step, ontManager,merge);
        
        // Initialize mappable group
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            classSimValuesMGrp = new testSimValueConstructorMappableGrp(step, 
                    ontManager);
        }
        System.out.println("Finish init class");        
        /**
         * To have a copy of OntManager, it will be used in the
         * sim value calculation of a single pair(used in the recommendations).
         */        
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            AlgoConstants.testontManager = ontManager;
            int onto1Size = ontManager.getontology(Constants.ONTOLOGY_1).getMClasses().size();
            int onto2Size = ontManager.getontology(Constants.ONTOLOGY_2).getMClasses().size();
            AlgoConstants.NO_OF_PAIRS = onto1Size*onto2Size;
        } else {
            int onto1Size = ontManager.getontology(Constants.ONTOLOGY_1).getMClasses().size();
            int onto2Size = ontManager.getontology(Constants.ONTOLOGY_2).getMClasses().size();      
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
    public void calculateSlotSimValue(HashSet<Integer> matcherlist){
        slotSimValues.calculate_concept_sim(matcherlist,merge,Constants.STEP_SLOT);
    }
    
    /**
     * To start computation for the class matching.
     * 
     * @param matcher   Name of the matcher. 
     */
    public void calculateClassSimValue(HashSet<Integer> matcherlist){

        classSimValues.calculate_concept_sim(matcherlist,merge,Constants.STEP_CLASS);
    }
    public void calculateclasssim(HashSet<Integer> matcher,testMergerManager merge)
    {
        classSimValues.calculate_concept_sim(matcher,merge,Constants.STEP_CLASS);
    }
    /**
     * To start computation for the class matching, this process use only 
     * mappable groups.
     * 
     * @param matcher   Name of the matcher. 
     */
    public void calculateClassSimValueMGrp(int matcher){
        //classSimValuesMGrp.calculateSimilarityValue(matcher);
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
    public Vector getSlotSugs(double[] weight, double threshold, String combinationMethod,int step) {                
        return slotSimValues.getPropertyList(weight, threshold,combinationMethod,step,merge.getMoid());
    }
    
    /**
     * To get mapping suggestions of the class matching process, used in both
     * computation and recommendation session.
     * 
     * @return List of mapping suggestion.
     */ 
    public Vector getClassSugs(double[] weight, double threshold, String combinationMethod, int step) {        
        merge.setWeight(weight);
        if (combinationMethod.equalsIgnoreCase("maximum")){
            merge.setCombination(Constants.MAXBASED);
        }
        else{
            merge.setCombination(Constants.WEIGHTBASED);
        }
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            if(merge.getIsDatabase()== false){
                if(merge.getIsLarge() == true){
                    return merge.generate_tasklist_match(step, threshold, 0);
                }
                else{
                    return classSimValues.getPairListLocal(weight, threshold, combinationMethod, step);
                }
            }
            else{
                
                return classSimValues.getPairList(weight, threshold, combinationMethod, step, merge.getMoid());
            }                
        } else {
            return classSimValues.getPairListSegmentPairs(weight, threshold,combinationMethod);                
        }    
    }
    
    /**
     * To get mapping suggestions of the class matching process, used in both
     * computation and recommendation session and used for the strategies that
     * has double threshold filtering.
     * 
     * @return List of mapping suggestion.
     */
    public Vector getClassSugs(double[] weight, double upperthreshold, double lowerthreshold, String combinationMethod,int step) {        
        merge.setWeight(weight);
        if (combinationMethod.equalsIgnoreCase("maximum")){
            merge.setCombination(Constants.MAXBASED);
        }
        else{
            merge.setCombination(Constants.WEIGHTBASED);
        }
        if(!AlgoConstants.ISRECOMMENDATION_PROCESS) {
            if(merge.getIsDatabase()== false){
                 if(merge.getIsLarge() == true){
                    return merge.generate_tasklist_match(step,lowerthreshold,upperthreshold);
                }
                 else{
                return classSimValues.getPairListLocal(weight, upperthreshold, lowerthreshold, combinationMethod, step);}
            }
            else{
                return classSimValues.getPairList(weight, upperthreshold, lowerthreshold, combinationMethod,step,merge.getMoid());    
            }            
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
    public void setAlignment(testPair pair){
        String concept1 = merge.getLocalName(pair.getSource());
        String concept2 = merge.getLocalName(pair.getTarget());
       
        AlignmentConstants.IsAligned.add(concept1 + AlgoConstants.SEPERATOR + concept2); 
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