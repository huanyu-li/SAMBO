/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.PRAalg.util;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.util.Pair;

/**
 * @author Qiang Liu
 * Created on Dec 21, 2010, 12:45:22 AM
 */
public class MappableChecker {

    private static Logger logger = Logger.getLogger(MappableChecker.class.getName());
    ConsistentChecker consisChecker;
    // suggestions of the PRA
    Vector<Pair> PRASugs;
    // classes of ontology 1 in the suggestions of the PRA
    Vector<MClass> PRAClss1;
    // classes of ontology 2 in the suggestions of the PRA
    Vector<MClass> PRAClss2;
    // consistent suggestions of the PRA
    Vector<Pair> consisPRASugs;
    // classes of ontology 1 in the consistent suggestions of the PRA
    Vector<MClass> consisPRAClss1;
    // classes of ontology 2 in the consistent suggestions of the PRA
    Vector<MClass> consisPRAClss2;
    // cache the calculated relation code
    HashMap<MClass, String> relCodeMap;

    /**
     *
     * @param consisChecker
     * @param PRASugs
     */
    public MappableChecker(ConsistentChecker consisChecker, Vector<Pair> PRASugs) {
        this.consisChecker = consisChecker;
        this.relCodeMap = new HashMap<MClass, String>();
        this.PRASugs = PRASugs;
        this.PRAClss1 = new Vector<MClass>();
        this.PRAClss2 = new Vector<MClass>();
        this.consisPRAClss1 = new Vector<MClass>();
        this.consisPRAClss2 = new Vector<MClass>();
    }

    /**
     * Check if given two classes are mappable
     * @param c1 a class in ontology 1
     * @param c2 a class in ontology 2
     * @return true if c1 and c2 are mappable, otherwise false
     */
    public boolean ifMappable(MClass c1, MClass c2) {
        if (PRAClss1.contains(c1) || PRAClss2.contains(c2))
            return false;
        String code1 = getRelationCode(c1, consisPRAClss1, consisChecker.getOntograph1());
        String code2 = getRelationCode(c2, consisPRAClss2, consisChecker.getOntograph2());
        logger.info("The code of is :" + code1 + " for " + c1.toString() +
                "\nThe code is :" + code2 + " for " + c2.toString());
        return code1.equals(code2);
    }

    /**
     *  Initialize the mappable checker
     */
    public void initiate() {
        // Find a group of consistent suggestions
        this.genConsistentSugs();
        if (consisPRASugs.isEmpty()) {
            logger.info("The set of consistent suggestions is empty!");
        }

        parseSugs(consisPRAClss1, consisPRAClss2, consisPRASugs);
        parseSugs(PRAClss1, PRAClss2, PRASugs);
    }


    private void genConsistentSugs() {
        System.out.println("All PRA suggestions : " + PRASugs.size());
        ConsistentGroupFinder consisFinder = new ConsistentGroupFinder(consisChecker, PRASugs);
        consisPRASugs = consisFinder.getConsistentGroup();
        System.out.println("Consistent PRA suggestions : " + consisPRASugs.size());
        System.out.println(debug_ConsisSugsInfor());
    }

    private String getRelationCode(MClass sourceCls, Vector<MClass> targetClss, OntoGraph ontograph) {
        String result = relCodeMap.get(sourceCls);
        // If there is backup, then use the backup
        if (result != null) {
            return result;
        }
        result = "";
        for (MClass targetCls : targetClss) {
            if (ontograph.hasInferSubRel(targetCls, sourceCls)) {
                result += "1";
            } else {
                result += "0";
            }
        }
        return result;
    }
    
    private String debug_ConsisSugsInfor() {
        String rt = "The consistent suggestions include : \n";
        for (Pair s : consisPRASugs) {
            rt += s.toString() + "\n";
        }
        return rt;
    }

    public Vector<Pair> getConsisPRASugs() {
        return consisPRASugs;
    }

    private void parseSugs(Vector<MClass> clss1, Vector<MClass> clss2, Vector<Pair> sugs) {
        for (Pair sug : sugs) {
            if (!clss1.contains(sug.getObject1())) {
                clss1.add((MClass) sug.getObject1());
            }
            if (!clss2.contains(sug.getObject2())) {
                clss2.add((MClass)sug.getObject2());
            }
        }
    }
}
