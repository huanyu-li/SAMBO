/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.PRA.oracle;


import java.util.ArrayList;
import java.util.Enumeration;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.Recommendation.RecommendationConstants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.ui.web.Constants;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.testing.AllSessionHistoryDB;

/**
 * Generate an oracle from session history which can be used in the
 * recommendation process to evaluate an alignment strategy.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class GeneratorOracleFromHisory {
    
    /**
     * To manage ontology content.
     */   
    private MergeManager merge;
    /**
     * Session settings
     */
    private SettingsInfo settings;
    /**
     * Used for the loop recommendation.
     */
    private boolean saveDecisionOnDB = false;
    /**
     * Used for the loop recommendation.
     */
    private boolean useSavedDecision = false;
    /**
     * 
     */
    private AllSessionHistoryDB allDecisionDB = new AllSessionHistoryDB();
    /**
     * This constructor automatically generate an oracle when an instance is 
     * created for this class.
     * 
     * @param merge
     * @param settings
     */
    public GeneratorOracleFromHisory(MergeManager currMerge, SettingsInfo 
            currSettings) {          
          merge = currMerge;
          settings = currSettings;
          // Initializing orcale variables.
          RecommendationConstants.VALIDATED_SUGGESTIONS = new ArrayList 
                  <String>();
          RecommendationConstants.ACCEPTED_SUGGESTIONS = new ArrayList 
                  <String>();
          RecommendationConstants.REJECTED_SUGGESTIONS = new ArrayList
                  <String>();
          
          for (Enumeration e = merge.getHistory().elements(); 
                  e.hasMoreElements();) {
              extractOracleInfo((History) e.nextElement());                               
          }
          for(Enumeration e = merge.getCurrentHistory().elements(); 
                  e.hasMoreElements();) {
              extractOracleInfo((History) e.nextElement());
          }
          
          if (saveDecisionOnDB) {
              for (String suggCorrect:RecommendationConstants.
                      ACCEPTED_SUGGESTIONS) {
                  allDecisionDB.insert(suggCorrect, "1");
              }
              for (String suggCorrect:RecommendationConstants.
                      REJECTED_SUGGESTIONS) {
                  allDecisionDB.insert(suggCorrect, "0");
              }
          }
          if (useSavedDecision) {
              RecommendationConstants.ACCEPTED_SUGGESTIONS = 
                      allDecisionDB.select("1");
              RecommendationConstants.REJECTED_SUGGESTIONS = 
                      allDecisionDB.select("0");
              RecommendationConstants.VALIDATED_SUGGESTIONS = 
                      allDecisionDB.selectAll();
          }
          
          allDecisionDB.closeConnection();
    }
    
    /**
     * This method extract oracle information from the history.
     * 
     * @param history      Mapping decision history of a concept pair. 
     */
    private void extractOracleInfo(History history) {
           
        Pair p = (Pair) history.getPair();
        String concept1ID=(String)((MElement) p.getObject1()).getId();
        String concept2ID=(String)((MElement) p.getObject2()).getId();
        
        switch (history.getAction()) {
            //This case for equalivent mappings.
            case Constants.ALIGN_CLASS:
                           
                RecommendationConstants.VALIDATED_SUGGESTIONS.add
                        (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                RecommendationConstants.ACCEPTED_SUGGESTIONS.add
                        (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                break;
            //This case for relation mappings.    
            case Constants.IS_A_CLASS:
                if (history.getNum() == Constants.ONTOLOGY_1) {
                    
                    RecommendationConstants.VALIDATED_SUGGESTIONS.add
                            (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                    RecommendationConstants.ACCEPTED_SUGGESTIONS.add
                            (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                }
                if(history.getNum() == Constants.ONTOLOGY_2) {
                    
                    RecommendationConstants.VALIDATED_SUGGESTIONS.add
                            (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                    RecommendationConstants.ACCEPTED_SUGGESTIONS.add
                            (concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                }
                break;
            //This case for rejected mapping suggestions.    
            case Constants.NO:
                RecommendationConstants.VALIDATED_SUGGESTIONS.
                        add(concept1ID+AlgoConstants.SEPERATOR+concept2ID);                                  
                RecommendationConstants.REJECTED_SUGGESTIONS.
                        add(concept1ID+AlgoConstants.SEPERATOR+concept2ID);
                break;                
          
            default:
                System.out.println("Invalid history event!!!");
                    
        }
    }
}
