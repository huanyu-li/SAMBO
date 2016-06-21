/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.PRAalg;
import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.util.Pair;

/**
 * @author Qiang Liu
 * Created on Dec 21, 2010, 12:11:15 AM
 */
public class fPRA {
    private static Logger logger = Logger.getLogger(fPRA.class.getName());
    // PRA suggestions
    Vector<Pair> PRASugs;

    // ontologies to be aligned
    MOntology monto1, monto2;

      /**
     * Constructor
     * @param monto1 the ontology 1
     * @param monto2 the ontology 2
     * @param PRASugs the suggestions in the PRA
     */
    public fPRA(MOntology monto1, MOntology monto2, Vector<Pair> PRASugs) {
        this.PRASugs = PRASugs;
        this.monto1 = monto1;
        this.monto2 = monto2;
    }
}
