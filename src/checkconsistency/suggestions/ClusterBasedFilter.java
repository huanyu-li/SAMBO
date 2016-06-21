/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checkconsistency.suggestions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.algos.matching.algos.SinglePairComputation;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.SwapWords;
import se.liu.ida.sambo.util.Pair;



/**
 * <p>
 * To improve the quality of proposed mapping suggestions. 
 * 
 * (Note) Using methods in this class we can improve the precision of an 
 * alignment strategy, the recall of an alignment strategy may decrease.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class ClusterBasedFilter {      
    /**
     * SQL server connection.
     */    
    private Connection sqlConn = null;
    /**
     * Ontology pair name.
     */    
    private String ontologies = AlgoConstants.settingsInfo.getName
            (Constants.ONTOLOGY_1).concat("#").concat
            (AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
    /**
     * To compute the similarity values using swap word matcher.
     */
    private SwapWords swapWords = new SwapWords();
    /**
     * To compute the similarity value for a single pair. 
     */
    private SinglePairComputation singlePairCompuation = new 
            SinglePairComputation();
    /**
     * Final mapping suggestions.
     */
    private Vector finalSuggestions;
    /**
     * Default constructor.
     * 
     * @param conn      SQL server connection.
     */
    public ClusterBasedFilter(Connection conn) {        
        sqlConn = conn;
    }
    /**
     * Empty constructor.
     */
    public ClusterBasedFilter() {       
        
    }
    
    
    
    /**
     * This method retain suggestions that are likely to be equivalent.
     * 
     * 
     * @param fullList         Full suggestion list(After single/dtf filtering).
     * @param ontologiesNumber Ontology 1/ontology 2. 
     * 
     * @return          List of final mapping suggestions.
     */
    public Vector keepCloseMatch(Vector fullList, int ontologiesNumber) {        
        
        finalSuggestions = new Vector();
        ArrayList<String> processedIDs = new ArrayList<String>();
        
        for (int ij = 0; ij < fullList.size(); ij++) {
            
            Pair pair =(Pair)fullList.elementAt(ij);
            /**
             * To create a subset of suggestions with same concept1 or concept2 
             * ids.
             */ 
            String idToProcess = null;
            
            if (ontologiesNumber == 1) {
                idToProcess = ((MElement) pair.getObject1()).getId();
            }
            else if (ontologiesNumber == 2) {
                idToProcess = ((MElement) pair.getObject2()).getId();
            }
            
            if (processedIDs.isEmpty() || !processedIDs.contains(idToProcess)) { 
                
                Vector suggestionsWithSameID = createSubSet(fullList, 
                        idToProcess, ontologiesNumber);                
                processedIDs.add(idToProcess); 
                boolean noSameLabel = true;
                
                for (int i = 0; i < suggestionsWithSameID.size(); i++) {
                    
                    Pair pairSameID =(Pair)suggestionsWithSameID.elementAt(i);
                    
                    if (hasSameTextualDescrp(pairSameID)) {
                        pairSameID.setSameLabelInfo(true);                        
                        noSameLabel = false;
                    }
                    else {
                        pairSameID.setSameLabelInfo(false);
                    }
                }
                
              /**
                removeSuggestionsBasedonLabel(suggestionsWithSameID, 
                noSameLabel);
               */ 
                
                removeBasedOnSimValue(suggestionsWithSameID, noSameLabel);
            }
        }
        return finalSuggestions;
    }
    
    /**
     * To remove space in between characters.
     * 
     * @param inputString
     * @return 
     */
    private String removeSpace(String inputString) {        
        inputString = inputString.replaceAll(" ", "");
        return inputString.toLowerCase();
    }
    
    /**
     * To create a subset of suggestions based on concept1/concept2.
     * 
     * @param fullList
     * @param idToProcess
     * @param ontologyNumber
     * @return 
     */
    private Vector createSubSet(Vector fullList, String conceptID, 
            int ontologyNumber) {
        
        Vector subSet = new Vector();
        
        for (int i = 0; i < fullList.size(); i++) {
            
            Pair pair = (Pair)fullList.elementAt(i);            
            String ids = null;
            
            if (ontologyNumber == 1) {
                ids = ((MElement) pair.getObject1()).getId();
            }
            else if (ontologyNumber == 2) {
                ids = ((MElement) pair.getObject2()).getId();
            }
            
            if (ids.equalsIgnoreCase(conceptID)) {
                subSet.add(pair);
            }
        }
        
        return subSet;     
    }
    
    /**
     * Check if the concept's in a suggestion has same textual description. 
     * 
     * @param suggestions
     * @return 
     */
    protected boolean hasSameTextualDescrp(Pair suggestions) {
        
        String name1 = ((MElement) suggestions.getObject1()).getPrettyName();
        String name2 = ((MElement) suggestions.getObject2()).getPrettyName();
        String symns1 = "";
        
        if (removeSpace(name1).equalsIgnoreCase(removeSpace(name2))) {
            return true;
        }
        else if (swapWords.getSimValue(name1, name2)>0) {
            return true;
        }
        
        //pretty synonyms
        if (((MElement) suggestions.getObject1()).isMClass() && 
                ((MElement) suggestions.getObject2()).isMClass()) {
            
            for (Enumeration en1 = ((MClass) suggestions.getObject1()).
                    getPrettySyn().elements(); en1.hasMoreElements();) {
                
                symns1 = (String) en1.nextElement();
                
                if (removeSpace(symns1).equalsIgnoreCase(removeSpace(name2))) {
                    return true;                        
                }
                else if (swapWords.getSimValue(symns1, name2) > 0) {
                    return true;
                }
                
                for (Enumeration en2 = ((MClass) suggestions.getObject2()).
                        getPrettySyn().elements(); en2.hasMoreElements();) {
                    
                    String symn2=(String) en2.nextElement();
                    if (removeSpace(symns1).equalsIgnoreCase
                            (removeSpace(symn2))) {
                                
                        return true;                            
                    }
                    else if (swapWords.getSimValue(symns1, symn2) > 0) {                                
                        return true;                            
                    }
                }
            }
            
            for (Enumeration en2 = ((MClass) suggestions.getObject2()).
                    getPrettySyn().elements(); en2.hasMoreElements();) {
                
                String symns2 = (String) en2.nextElement();
                
                if (removeSpace(name1).equalsIgnoreCase(removeSpace(symns2))) {                            
                    return true;
                }
                else if(swapWords.getSimValue(name1, symns2) > 0) {                            
                    return true;                        
                } 
            }
        }
        return false;
   }
    
    /**
     * In a subset retain suggestions which has same textual description, and
     * discard other.
     * 
     * @param suggestionsWithSameID
     * @param hasSameLabel
     * @param noSameLabel 
     */
    private  void removeSuggestionsBasedonLabel(Vector suggestionsWithSameID, 
            boolean [] hasSameLabel, boolean noSameLabel) {
        
        for (int i = 0; i < suggestionsWithSameID.size(); i++) {
            
            Pair pair = (Pair)suggestionsWithSameID.elementAt(i);
            /**
             * If all suggestions in a subset has different label then retain
             * the entire subset.
             */ 
            if(noSameLabel || hasSameLabel[i]) {
                finalSuggestions.add(pair);
            }
        }
    }    
    
    
    /**
     * Retain suggestions in subset based on their WordList matcher's similarity
     * value.
     * 
     * (i.e) The suggestions with high WL similarity value will be retained
     * other are discarded.
     * 
     * @param suggestionSubSet
     * @param noSameLabel 
     */
    private void removeBasedOnWordList(Vector suggestionSubSet, 
            boolean noSameLabel) {
        
        double greaterWLValue = 0;
        
        for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
            
            if (pair.getSameLabelInfo() || suggestionSubSet.size() == 1) {
                finalSuggestions.add(pair);
            }
            else if (noSameLabel && suggestionSubSet.size() > 1) {
                
                double value = singlePairCompuation.
                            calculateSimilarityValue(AlgoConstants.TERM_WN, 
                            pair, ontologies, sqlConn);                
                pair.setWLSim(value);
                
                if (value > greaterWLValue) {
                    greaterWLValue = value;
                }
                
                value = singlePairCompuation.
                            calculateSimilarityValue(AlgoConstants.UMLS, 
                            pair, ontologies, sqlConn);
                pair.setUMLSSim(value);
                
            }
        }
        
        if (noSameLabel && suggestionSubSet.size() > 1) {
            
            for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
                
                if (pair.getWLSim() == greaterWLValue) {
                    finalSuggestions.add(pair);
                }
            }
        }
    }
    
    
        
    
    
    /**
     * Retain suggestions in a subset based on their UMLS similarity value.
     * 
     * (i.e) The suggestions with high UMLS similarity value will be retained
     * other suggestions are discarded. 
     * 
     * @param suggestionSubSet
     * @param noSameLabel 
     */
    private void removeBasedOnUMLS(Vector suggestionSubSet, 
            boolean noSameLabel) {
        
        double greaterUMLSValue = 0;
        
        for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
            
            if (pair.getSameLabelInfo() || suggestionSubSet.size() == 1) {
                finalSuggestions.add(pair);
            }
            else if (noSameLabel && suggestionSubSet.size() > 1) {
                
                double value = singlePairCompuation.
                            calculateSimilarityValue(AlgoConstants.TERM_WN, 
                            pair, ontologies, sqlConn);                
                pair.setWLSim(value);                
                
                
                value = singlePairCompuation.
                            calculateSimilarityValue(AlgoConstants.UMLS, 
                            pair, ontologies, sqlConn);
                pair.setUMLSSim(value);
                
                if (value > greaterUMLSValue) {
                    greaterUMLSValue = value;
                }
                
            }
        }
        
        if (noSameLabel && suggestionSubSet.size() > 1) {
            
            for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
                
                if (pair.getUMLSSim() == greaterUMLSValue) {
                    finalSuggestions.add(pair);
                }
            }
        }
    }
    
    
    /**
     *
     * Retain suggestions in a subset based on their similarity value.
     * 
     * (i.e) The suggestions with high similarity value will be retained
     * other suggestions are discarded.
     * 
     * @param suggestionSubSet
     * @param noSameLabel 
     */
    private void removeBasedOnSimValue(Vector suggestionSubSet, 
            boolean noSameLabel) {
        
        double greaterWLValue = 0;
        
        for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
            
            if (pair.getSameLabelInfo() || suggestionSubSet.size() == 1) {
                finalSuggestions.add(pair);
            }
            else if (noSameLabel && suggestionSubSet.size() > 1) {
                
                double value = pair.getSim();                
                pair.setWLSim(value);
                
                if (value > greaterWLValue) {
                    greaterWLValue = value;
                }
                
                value = singlePairCompuation.
                            calculateSimilarityValue(AlgoConstants.UMLS, 
                            pair, ontologies, sqlConn);
                pair.setUMLSSim(value);
                
            }
        }
        if (noSameLabel && suggestionSubSet.size() > 1) {
            
            for (Iterator it = suggestionSubSet.iterator(); 
                    it.hasNext();) {
                Pair pair = (Pair) it.next();
                
                if (pair.getWLSim() == greaterWLValue) {
                    finalSuggestions.add(pair);
                }
            }
        }
    }
}
