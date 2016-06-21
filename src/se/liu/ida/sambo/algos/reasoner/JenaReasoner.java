/*
 * JenaReasoner.java
 */
package se.liu.ida.sambo.algos.reasoner;

/**
 *
 * @author  He Tan
 * @version
 */
import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

import java.util.*;

import java.util.logging.Logger;
import se.liu.ida.sambo.util.Pair;

public class JenaReasoner {
    private static Logger logger = Logger.getLogger("");
    /**Get the inconsistant classes
     *
     *
     *@return a vector of inconsistant classes
     */
    public static Vector getIncon(OntModel model) {

        Vector list = new Vector();

        StmtIterator it = model.listStatements(null, OWL.equivalentClass, OWL.Nothing);
        while (it.hasNext()) {
            list.add(it.nextStatement().getSubject());
        }

        //close the iterator and free the resource.s
        it.close();

        //OWL.Nothing is equivalent to OWL.Nothing
        list.remove(OWL.Nothing);

        return list;
    }

    /** Get cycles
     *
     *@return a vector of cycles
     */
    public static Vector getCycles(OntModel reasonModel, OntModel ontModel) {

        Vector cycles = new Vector();
        Vector equiVector = new Vector();

        //Equivalent classes defined in the models
        for (Iterator it = ontModel.listClasses(); it.hasNext();) {

            OntClass c = (OntClass) it.next();

            if (c.isAnon() || c.equals(ontModel.getProfile().NOTHING()) || c.equals(ontModel.getProfile().THING())) {
                continue;
            }

            Set set = c.listEquivalentClasses().toSet();
            //the equivClass list does not contain the class itself in OWL_MEM.
            set.add(c);
            //not only itself
            if (set.size() > 1) {
                equiVector.add(set);
            }
        }


        //Equivalent classes
        for (Iterator it = reasonModel.listClasses(); it.hasNext();) {

            OntClass c = (OntClass) it.next();

            if (c.isAnon() || c.equals(reasonModel.getProfile().NOTHING()) || c.equals(reasonModel.getProfile().THING())) {
                continue;
            }

            Set set = c.listEquivalentClasses().toSet();

            //1. not only itself
            //2. not in cycles yet
            //3. not the defined equivlanet classes
            //4. not equivalent to NOTHING.
            if ((set.size() > 1) && !cycles.contains(set) && !equiVector.contains(set) && !set.contains(reasonModel.getProfile().NOTHING())) {
                cycles.add(set);
            }

        }

        return cycles;
    }

    /** Get subsumptions between classes
     *
     *@return a vector of pairs of classes with subsumption
     */
    public static Vector getSubsumption(OntModel reasonModel, OntModel ontModel) {

        Vector subsumptions = new Vector();
        Hashtable ht = new Hashtable();

        //are equivalent classes the defined subclasses
        //  boolean equiDefine = false;
        boolean subclass = false;

        // 1. query the ontModel, store every class with its all equivalentClasses
        for (Iterator it = ontModel.listClasses(); it.hasNext();) {
            OntClass clazz = (OntClass) it.next();
            ht.put(clazz, clazz.listEquivalentClasses().toSet());
        }

        // 2.1 get each class A
        for (Enumeration e = ht.keys(); e.hasMoreElements();) {
            OntClass clazz = (OntClass) e.nextElement();
            // 2.2 get every of its equivalent classes eqvA
            for (Iterator it = ((Set) ht.get(clazz)).iterator(); it.hasNext();) {
                OntClass cls = (OntClass) it.next();
                // 2.3 add A to eqvA's eqvs
                ((Set) ht.get(cls)).add(clazz);
            }
        }

        for (Iterator it = reasonModel.listClasses(); it.hasNext();) {
            // 3.1 get the class in reason model
            OntClass c = (OntClass) it.next();
            if (c.isAnon() || c.equals(reasonModel.getProfile().NOTHING()) || c.equals(reasonModel.getProfile().THING())) {
                continue;
            }
            //System.out.println("This is updated version");
            //if (c.getLocalName().equalsIgnoreCase("NCI_C25769"))
            //    System.out.println();
            //System.out.println(c.getLocalName());
            // 3.2 get the corresponding class in non-reason model
            OntClass oc = ontModel.getOntClass(c.getURI());
            // 3.2 get every of its direct subclasses in non-reason model
            for (Iterator i = oc.listSubClasses(false); i.hasNext();) {
                 OntClass osc = (OntClass) i.next();
                 if (osc.isAnon() || osc.equals(reasonModel.getProfile().NOTHING())) {
                    continue;
                 }
//                OntClass sc = (OntClass) i.next();
//                if (sc.getLocalName().equalsIgnoreCase("NCI_C13044"))
//                    System.out.println();
//                System.out.println("subinclude" + sc.getLocalName());
//                if (sc.isAnon() || sc.equals(reasonModel.getProfile().NOTHING())) {
//                    continue;
//                }
//                // get corresponding class in non-reason model
//                OntClass osc = ontModel.getOntClass(sc.getURI());

                //check whether it is a defined subclass
                if (!oc.hasSubClass(osc, true)) {

                    //equivalent superclass
                    for (Iterator ite = ((Set) ht.get(oc)).iterator(); ite.hasNext();) {

                        OntClass eq = (OntClass) ite.next();

                        if (eq.hasSubClass(osc, false)) {
                            subclass = true;
                            break;
                        }
                    }

                    //equivalent subclasses
                    for (Iterator ite1 = ((Set) ht.get(osc)).iterator(); ite1.hasNext();) {

                        OntClass eqs = (OntClass) ite1.next();

                        if (oc.hasSubClass(eqs, false)) {
                            subclass = true;
                            break;
                        }

                        for (Iterator ite2 = ((Set) ht.get(oc)).iterator(); ite2.hasNext();) {

                            if (((OntClass) ite2.next()).hasSubClass(eqs, false)) {
                                subclass = true;
                                break;
                            }
                        }
                    }


                    if (!subclass) {
                        subsumptions.add(new Pair(c, osc));
                    }

                    subclass = false;
                }
            }
        }

        return subsumptions;
    }

    /*Classify the ontology, i.e. remove the redundant is-a relations
     *
     *@param file to which write the classified ontology
     */
    public static OntModel classify(OntModel reasonModel, OntModel ontModel) {
        return ontModel;
    }


}
