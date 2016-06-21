/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import checkconsistency.suggestions.ClusterBasedFilter;
import java.util.Enumeration;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.algos.matching.algos.umlsOffline.UMLSRelationCheck;
import se.liu.ida.sambo.util.Pair;

/**
 * <p>
 * To remove suggestions that are in relation.
 * </p>
 * 
 * @author  Rajaram
 * @version 1.0
 */
public class RemoveConceptsInRelation extends ClusterBasedFilter{
    
    UMLSRelationCheck umlsRelationFinder = new UMLSRelationCheck();
    
    /**
     * Removes suggestions that are in relation.
     * 
     * @param fullList  Final suggestion list.
     * 
     * @return          Final suggestions without any relation. 
     */
    public Vector removeRelationByUMLS(Vector fullList) {        
        
        Vector list = new Vector();
        
        for (int i = 0; i < fullList.size(); i++) {
            
            Pair pair = (Pair)fullList.elementAt(i);            
            
            if(super.hasSameTextualDescrp(pair)) {
                list.add(pair);
            }
            else if(!hasRelation(pair)) {
                list.add(pair);
            }
        }
        return list;
    }
    
    /**
     * Checks if the suggestions has any relation.
     * 
     * @param pair
     * @return 
     */
    private boolean hasRelation(Pair pair) {
        
        String name1 = ((MElement) pair.getObject1()).getPrettyName();
        String name2 = ((MElement) pair.getObject2()).getPrettyName();
        String symns1 = "";
        
        if(umlsRelationFinder.hasRelation(name1, name2)) {
            return true;
        }
        else if(umlsRelationFinder.hasRelation(addApos(name1), 
                addApos(name2))) {
            return true;
        }
        
        //pretty synonyms
        if (((MElement) pair.getObject1()).isMClass() && 
                ((MElement) pair.getObject2()).isMClass()) {
            
            for (Enumeration en1 = ((MClass) pair.getObject1()).
                    getPrettySyn().elements(); en1.hasMoreElements();) {
                        
                symns1 = (String) en1.nextElement();
                
                if (umlsRelationFinder.hasRelation(symns1, name2)) {                    
                    return true;
                }
                else if (umlsRelationFinder.hasRelation(addApos(symns1), 
                        addApos(name2))) {                    
                    return true;
                }
                for (Enumeration en2 = ((MClass) pair.getObject2()).
                        getPrettySyn().elements(); en2.hasMoreElements();) {
                    
                    if (umlsRelationFinder.hasRelation(symns1, 
                            (String) en2.nextElement())) {
                        return true;
                    }
                    else if (umlsRelationFinder.hasRelation(addApos(symns1), 
                            addApos((String) en2.nextElement()))) {
                        return true;
                    }
                }
            }
            
            for (Enumeration en2 = ((MClass) pair.getObject2()).
                    getPrettySyn().elements(); en2.hasMoreElements();) {
                
                if(umlsRelationFinder.hasRelation(name1, 
                        (String) en2.nextElement())) {                    
                    return true;
                }
                else if(umlsRelationFinder.hasRelation(addApos(name1), 
                        addApos((String) en2.nextElement()))) {                    
                    return true;
                }
            }
        }
        return false;
     }
    
    /**
     * Add apostrophe single quote between character s.
     * 
     * @param inputString
     * @return 
     */
    private String addApos(String inputString) {
        inputString = inputString.toLowerCase();
        
        if(inputString.contains(" s ")){
            inputString = inputString.replace(" s ", "'s ");
        }        
        return inputString;
    }
    
}
