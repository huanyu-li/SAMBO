/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.PRAalg.util;
import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.util.Pair;

/**
 * @author Qiang Liu
 * Created on Dec 20, 2010, 5:39:55 PM
 */
public class ConsistentChecker {

    private static Logger logger = Logger.getLogger(ConsistentChecker.class.getName());

    OntoGraph ontograph1;
    OntoGraph ontograph2;    

    /**
     *  constructor
     * @param monto1 ontology 1
     * @param monto2 ontology 2
     */
    public ConsistentChecker(MOntology monto1, MOntology monto2) {
        ontograph1 = new OntoGraph(monto1);
        ontograph2 = new OntoGraph(monto2);
    }

    /**
     *  constructor
     * @param ontograph1 the DAG graph for ontology 1
     * @param ontograph2 the DAG graph for ontology 2
     */
    public ConsistentChecker(OntoGraph ontograph1, OntoGraph ontograph2) {
        this.ontograph1 = ontograph1;
        this.ontograph2 = ontograph2;
    }

    /**
     *
     * @return the DAG graph for ontology 1
     */
    public OntoGraph getOntograph1() {
        return ontograph1;
    }

    /**
     *
     * @return the DAG graph for ontology 2
     */
    public OntoGraph getOntograph2() {
        return ontograph2;
    }
    
    /**
     * check if suggestion 1 and 2 are consistent with each other
     * @param sug1 suggestion 1
     * @param sug2 suggestion 2
     * @return true if consistent, otherwise false
     */
    public boolean isConsistent(Pair sug1, Pair sug2) {
        MClass sug1_c1 = (MClass) sug1.getObject1();
        MClass sug1_c2 = (MClass) sug1.getObject2();
        MClass sug2_c1 = (MClass) sug2.getObject1();
        MClass sug2_c2 = (MClass) sug2.getObject2();
        boolean subconsis = ontograph1.hasInferSubRel(sug1_c1, sug2_c1) == ontograph2.hasInferSubRel(sug1_c2, sug2_c2);
        boolean supconsis = ontograph1.hasInferSupRel(sug1_c1, sug2_c1) == ontograph2.hasInferSupRel(sug1_c2, sug2_c2);
        return subconsis && supconsis;
    }

}
