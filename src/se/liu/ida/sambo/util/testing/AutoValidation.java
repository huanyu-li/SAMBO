/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util.testing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Pair;

/**
 * <p>
 * Auto validation, used for the testing purpose.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class AutoValidation { 
    
    /**
     * This method automatically validate mapping suggestions.
     * 
     * Note: This method is designed for the testing purpose.
     * 
     * @param merge         Instance of merge manager(Main.java merge manager
     *                      instance) 
     * @param maxValidation Number of mapping suggestions to be validate.
     */
    public void validate (MergeManager  merge, int maxValidation) {
        
        int warning = Constants.UNIQUE;
        Vector suggestions = merge.getGeneralSuggestionVector();
        final int totalSugg = suggestions.size();
        ExtractReferenceAlignmentFile refAlign = 
                new ExtractReferenceAlignmentFile();
        ArrayList<String> acceptedSuggestions = refAlign
                .getReferenceAlignment();
        ArrayList<String> mappingSuggestions = convertPairToString(suggestions);
        int currSuggLength;
        String[] concepts;
        Object concept1 = null ,concept2 = null;
        
        int paramA = 0 , paramB = 0;
        
        while (mappingSuggestions.size() > 0) {
            currSuggLength = merge.getGeneralSuggestionVector().size();
            
            if ((totalSugg-currSuggLength) >= maxValidation) {
                break;
            }
            concepts = mappingSuggestions.get(0).split(AlgoConstants.SEPERATOR);
            concept1 = null;
            concept2 = null;
            
            concept1 = AlgoConstants.ontManager.getMOnt(se.liu.ida.sambo.Merger.
                    Constants.ONTOLOGY_1).getElement(concepts[0]);
            concept2 = AlgoConstants.ontManager.getMOnt(se.liu.ida.sambo.Merger.
                    Constants.ONTOLOGY_2).getElement(concepts[1]);
            
            Pair pair = new Pair(concept1, concept2);
            
            if(concept1 != null && concept2 != null) {
                
                if(acceptedSuggestions.contains(mappingSuggestions.get(0))) {
                    warning = merge.processClassSuggestion(new History(pair, "",
                            Constants.ONTOLOGY_NEW, Constants.ALIGN_CLASS, ""));
                    paramA++;
                }
                else {
                    warning = merge.processClassSuggestion(new History(pair, 
                            null,Constants.ONTOLOGY_NEW, Constants.NO));
                    paramB++;
                }
            }
            mappingSuggestions = convertPairToString(suggestions);
        }
        
        System.out.println(" A = "+paramA +" B = "+paramB);
    }
    
    /**
     * To convert mapping suggestions from "Pair" object format to "String" 
     * object format.
     * 
     * @param suggestions   Mapping suggestions.
     * 
     * @return      List of mapping suggestions(ArrayList<String>) 
     */
    private ArrayList<String> convertPairToString (Vector suggestions) {
        
        ArrayList<String> mappingSuggestions = new ArrayList();        
        Iterator itr = suggestions.iterator();
        
        while (itr.hasNext()) {
            
            Pair pair = (Pair) itr.next();
            String concept1 = ((MElement) pair.getObject1()).getId();
            String concept2 = ((MElement) pair.getObject2()).getId();
            
            mappingSuggestions.add((concept1+AlgoConstants.SEPERATOR+concept2));
        }
        return mappingSuggestions;
    }
}
