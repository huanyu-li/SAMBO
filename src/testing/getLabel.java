/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @author rajka62
 */
public class getLabel {
    
    OntManager ontManager = null;
    
    public getLabel(OntManager ont) {
        
        ontManager = ont;
        
    }
    
    
    public void labelPrint(String  ID1, String ID2) {
        
        Object concept1 = ontManager.getMOnt(Constants.ONTOLOGY_1).
                    getElement(ID1);
        
        
        Object concept2 = ontManager.getMOnt(Constants.ONTOLOGY_2).
                    getElement(ID2);
        
        Pair pair = new Pair(concept1, concept2);
        
        String label1 = ((MElement) pair.getObject1()).getLabel();
        
        String label2 = ((MElement) pair.getObject2()).getLabel();
        
        
        System.out.println(label1 +" <---> "+ label2);
        
        
    }
            
            
    
}
