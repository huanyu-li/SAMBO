/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.PRAalg;

import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.PRAalg.util.ConsistentChecker;
import se.liu.ida.PRAalg.util.MappableChecker;
import java.util.Enumeration;
import java.util.logging.Level;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.PRA.PRA;
import se.liu.ida.sambo.component.loader.PRALoader;
import se.liu.ida.sambo.sysconfig.SysLogger;
import se.liu.ida.sambo.util.Pair;

/**
 * @author Qiang Liu
 * Created on Dec 20, 2010, 7:07:21 PM
 */
public class mgPRA {

    private static Logger logger = Logger.getLogger(mgPRA.class.getName());
    // PRA suggestions
    Vector<Pair> PRASugs;
    // mappable checker
    MappableChecker mappableChecker;
    // ontologies to be aligned
    MOntology monto1, monto2;

    /**
     *
     * @param args
     * @throws java.lang.Exception
     */
    /*
    public static void main(String args[]) throws Exception {
    SysLogger.openConsoleLogger(Level.FINE);
    MOntology monto1 = OntManager.loadOntology("file:///" +"D:/Thesis/src/SAMBO-WebApp/build/web/ontologies/OWL/eye_MA.owl", false);
    MOntology monto2 = OntManager.loadOntology("file:///" +"D:/Thesis/src/SAMBO-WebApp/build/web/ontologies/OWL/eye_MeSH.owl", false);
    String praFile = "D:/Thesis/src/SAMBO-WebApp/build/web/ontologies/PRA/eye_RA.rdf";

    Vector<Pair> prasugs = PRALoader.importPRAtoVector(monto1, monto2, praFile);
    pmPRA test = new pmPRA(monto1, monto2, prasugs);
    for (Pair p : test.getResults()) {
    System.out.print(((MClass) p.getObject1()).OntClass().getLocalName());
    System.out.print(" -- ");
    System.out.print(((MClass) p.getObject2()).OntClass().getLocalName());
    System.out.println();
    }
    }
     */
    public static void main(String args[]) throws Exception {
        run();
    }

    public static void run() {
        SysLogger.openConsoleLogger(Level.FINE);
        MOntology monto1 = OntManager.loadOntology("file:///" + "F:/IDAJOB/Ontologies/eye/eye_MA_1.owl", false);
        MOntology monto2 = OntManager.loadOntology("file:///" + "F:/IDAJOB/Ontologies/eye/eye_MeSH_2.owl", false);
        String praFile = "F:/IDAJOB/Ontologies/eye/eye_RA.rdf";

        Vector<Pair> prasugs = PRALoader.importPRAtoVector(monto1, monto2, praFile);
        mgPRA test = new mgPRA(monto1, monto2, prasugs);
        for (Pair p : test.getResults()) {
            System.out.println(((MClass) p.getObject1()).OntClass().getLocalName());
            System.out.println(" -- ");
            System.out.println(((MClass) p.getObject2()).OntClass().getLocalName());
            System.out.println();
        }
    }

    /**
     * Constructor
     * @param monto1 the ontology 1
     * @param monto2 the ontology 2
     * @param PRASugs the suggestions in the PRA
     */
    public mgPRA(MOntology monto1, MOntology monto2, Vector<Pair> PRASugs) {
        this.PRASugs = PRASugs;
        this.mappableChecker = new MappableChecker(new ConsistentChecker(monto1, monto2), PRASugs);
        this.monto1 = monto1;
        this.monto2 = monto2;
    }

    /**
     *  get mappable pairs of classes using mgPRA method
     * @return a set of concept pairs, which are mappable classes
     */
    public Vector<Pair> getResults(Vector<Pair> remainingSuggestions) {
        Vector<Pair> result = new Vector<Pair>();
        // 1. initialize the mappable checker
        mappableChecker.initiate();
        for (Pair sug : remainingSuggestions) {
            MClass c1 = (MClass) sug.getObject1();
            MClass c2 = (MClass) sug.getObject2();
            if (mappableChecker.ifMappable(c1, c2)) {
                result.add(sug);
                logger.info("[mgPRA] One suggestion passed :" + sug.toString());
            } else {
               logger.info("[mgPRA] One suggestion failed :" + sug.toString());
            }
        }
        return result;}


    public Vector<Pair> getResults(){
        Vector<Pair> result = new Vector<Pair>();
        
        
        Vector<Pair> result2 = new Vector<Pair>();
        // 1. initialize the mappable checker
        mappableChecker.initiate();
        // 2. generate the mappable pairs of classes 
        for (Enumeration e1 = monto1.getClasses().elements(); e1.hasMoreElements();) {
            MClass c1 = (MClass) e1.nextElement();
            for (Enumeration e2 = monto2.getClasses().elements(); e2.hasMoreElements();) {
                MClass c2 = (MClass) e2.nextElement();
                Pair sug = new Pair(c1, c2);
                if (mappableChecker.ifMappable(c1, c2)) {
                    result.add(sug);
                   logger.info("[mgPRA] Mappable suggestion :" + sug.toString());
                } else {
                    result2.add(sug);
                    logger.info("[mgPRA] Non-mappable suggestion :" + sug.toString());
                }
            }
        }
        
        //return result2;
        return result;
    }
}
