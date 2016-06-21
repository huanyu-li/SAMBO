/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import se.liu.ida.sambo.algos.matching.Matcher;

/**
 *
 * @author Qiang Liu
 */
public class UMLSKSearch_V6 extends Matcher {

    private final String umlsRelease = "2009AA";
    private final String language = "ENG";
    private final double umlsSimValue = 0.9;
    private UmlsTermQuerier termQuerier;
    private UmlsContextQuerier contextQuerier;


    public static void main(String[] args) {
        //  for (int i = 0; i < 18; i++) {
        UMLSKSearch_V6 test = new UMLSKSearch_V6();
        System.out.println(test.getSimValue("fundus oculi", "fundus oculi"));
        System.out.println(test.getSimValue("fundus oculi", "fundus oculi"));
        System.out.println("\n");
        // }
    }

    public UMLSKSearch_V6() {

        System.out.println("[DEBUG MSG] Trying to connect UMLS server......");
        UmlsConnector connector = new UmlsConnector();
        connector.doConnect();
        System.out.println("[DEBUG MSG] Connection established.");
        termQuerier = new UmlsTermQuerier(umlsRelease, language, connector);
        contextQuerier = new UmlsContextQuerier(umlsRelease, language, connector);

    }

    // This function Search for UMLS concept for two given terms if the UMLS concept is same for two terms then it returs true 
    public boolean querySimRelation(String term1, String term2) {
       
         
        
        String cuiList1="",cuiList2="";
        System.out.println("[DEBUG MSG] In UMLSKS. Query the relation : " + term1 + "==" + term2);
        cuiList1 = this.termQuerier.getTerm(term1, 2);
        cuiList2 = this.termQuerier.getTerm(term2, 2);
       
        
        
        
        
        
        if (cuiList1.isEmpty() || cuiList2.isEmpty()) {
            System.out.println("    ---> False");
            return false;
        }    
        
        
        
        String[] cuis1 = cuiList1.split(";");
        for (int i = 0; i < cuis1.length; i++) {
            if (cuiList2.contains(cuis1[i])) {
//                System.out.println("[DEBUG MSG] Query the relation : " + term1 + "==" + term2);
                System.out.println("    ---> True");
                return true;
            }
        }
        System.out.println("    ---> False");
        return false;
    }

    
     // This function is not in USE
    public boolean querySubRelation(String cTerm, String pTerm) {
        System.out.println("[DEBUG MSG] In UMLSKS. Query the relation : " + cTerm + "-->" + pTerm);
        String pList = this.termQuerier.getTerm(pTerm, 1);
        String cList = this.termQuerier.getTerm(cTerm, 1);
        if (pList.isEmpty() || cList.isEmpty()) {
            return false;
        }
        String[] pCuis = pList.split(";");
        String[] cCuis = cList.split(";");

        // 1. check if they are similar
        for (int i = 0; i < cCuis.length; i++) {
            for (int j = 0; j < pCuis.length; j++) {
                if (cCuis[i].equalsIgnoreCase(pCuis[j])) {
                    return true;
                }
            }
        }
        // 2.1 check if pCui appears in ancestor of cCuis
        // 2.2 check if cCui appears in descendants of pCuis
        if (pCuis.length < cCuis.length) {
            for (int i = 0; i < cCuis.length; i++) {
                String cAncList = this.contextQuerier.getAncContext(cCuis[i]);
                for (int j = 0; j < pCuis.length; j++) {
                    if (cAncList.contains(pCuis[j])) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < pCuis.length; i++) {
                String pChdList = this.contextQuerier.getChdContext(pCuis[i]);
                for (int j = 0; j < cCuis.length; j++) {
                    if (pChdList.contains(cCuis[j])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public double getSimValue(String input1, String input2) {
        if (this.querySimRelation(input1, input2)) {
            return this.umlsSimValue;
        } else {
            return 0;
        }
    }
}
