/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.PRAalg;

import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.PRAalg.util.ConsistentChecker;
import se.liu.ida.PRAalg.util.ConsistentGroupFinder;
import java.util.logging.Level;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.component.loader.PRALoader;
import se.liu.ida.sambo.sysconfig.SysLogger;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.enumOnto;

/**
 * @author Qiang Liu
 * Created on Sep 5, 2009, 11:12:04 AM
 */
public class dtfPRA {

    private static Logger logger = Logger.getLogger(dtfPRA.class.getName());
    // PRA suggestions
    Vector<Pair> PRASugs;
    // the ConsistentChecker
    ConsistentChecker consisChecker;
    // the consistent part of PRA suggestions
    Vector<Pair> consisPRASugs;

    /**
     *
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        //System.out.println( Recommendation.class.getResource("/") );
        System.out.println("Path to web directory"+ System.getProperty("user.dir"));
        SysLogger.openConsoleLogger(Level.FINE);
        MOntology monto1 = OntManager.loadOntology(enumOnto.Eye_GO.getUrl(), false);
        MOntology monto2 = OntManager.loadOntology(enumOnto.Eye_SO.getUrl(), false);
        String praFile = enumOnto.Eye_PRA_C.getUrl();
        Vector<Pair> prasugs = PRALoader.importPRAtoVector(monto1, monto2, praFile);
        dtfPRA test = new dtfPRA(monto1, monto2, prasugs);
        test.genConsistentSugs();
    }

    /**
     *  Constructor
     *
     * @param monto1 the ontology 1
     * @param monto2 the ontology 2
     * @param PRASugs suggestions in the PRA
     */
    public dtfPRA(MOntology monto1, MOntology monto2, Vector<Pair> PRASugs) {
        this.PRASugs = PRASugs;
        consisChecker = new ConsistentChecker(monto1, monto2);
        this.consisPRASugs = new Vector<Pair>();
    }

    /**
     * Run the dtfPRA algorithm to get filtered result
     *
     * @param remainingSuggestions
     * @param highThreshold high Threshold
     * @param lowThreshold low Threshold
     * @return The filted results by dtfPRA
     */
    public Vector<Pair> getResults(Vector<Pair> remainingSuggestions, double highThreshold,
            double lowThreshold) {
        Vector<Pair> remainingSugsAboveLow = new Vector<Pair>();
        Vector<Pair> remainingSugsAboveHigh = new Vector<Pair>();
        Vector<Pair> result = new Vector<Pair>();
        
        for (Pair p : remainingSuggestions) {
            // 1. use lowThreshold to get suggestions above lowThreshold
            
            if (p.getSim() >= lowThreshold) {
                if (p.getSim() >= highThreshold) // 2. use highThreshold to get suggestions above highThreshold
                {
                    remainingSugsAboveHigh.add(p);
                    
                    if(!result.contains(p))
                    result.add(p);
                }
                remainingSugsAboveLow.add(p);
            }
        }

        logger.info("Number of suggestions above low threshold : " + remainingSugsAboveLow.size());
        logger.info("Number of suggestions above high threshold : " + remainingSugsAboveHigh.size());
        // 3. find a group of consistent PRA suggestions
        this.genConsistentSugs();

        if (consisPRASugs.isEmpty()) {
            logger.info("The set of consistent suggestions is empty!");
        }

        // 4. use the consistent PRA suggestions to filter suggestions between two thresholds
        
        for (Pair sug : remainingSugsAboveLow) {
            
            if (remainingSugsAboveHigh.contains(sug) || ifPassConsisCheck(sug)) {
                if(!result.contains(sug))
                result.add(sug);
                logger.info("[dtfPRA] One suggestion passed :" + sug.toString());
            } else {
                logger.info("[dtfPRA] One suggestion failed :" + sug.toString());
            }
        }
        return result;
    }

    public void genConsistentSugs() {
        ConsistentGroupFinder consisFinder = new ConsistentGroupFinder(consisChecker, PRASugs);
        consisPRASugs = consisFinder.getConsistentGroup();
        logger.info(debug_ConsisSugsInfor());
    }

    private boolean ifPassConsisCheck(Pair sug) {
        for (Pair consisSug : consisPRASugs) {            
            if (!consisChecker.isConsistent(sug, consisSug)) {
                return false;
            }
        }
        return true;
    }

    private String debug_ConsisSugsInfor() {
        String rt = "The consistent suggestions include : \n";
        for (Pair s : consisPRASugs) {
            rt += s.toString() + "\n";
        }
        return rt;
    }
}
