/*
 * MModel.java
 *
 */
package se.liu.ida.sambo.MModel;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.objectspace.jgl.Array;
import com.objectspace.jgl.OrderedMap;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import se.liu.ida.sambo.MModel.util.OntConstants;
import se.liu.ida.sambo.util.CompareKey;

/** User interface to represent an ontology
 *
 * @author  He Tan
 * @version
 */
public class MOntology {

    OntModel ontModel;
    private OrderedMap classes;
    private OrderedMap properties;
    //an unique indentifier for the ontology
    private int num;
    Resource THING, NOTHING;

    /** Creates new MOntology
     *
     * @param num indicates which the ontology is, the first one or the second one.
     */
    public MOntology(int num, OntModel ontModel) {

        this.num = num;
        classes = new OrderedMap(new CompareKey());
        properties = new OrderedMap(new CompareKey());
        this.ontModel = ontModel;
        buildFromOntModel(ontModel);
        
    }

    /** Creates new MOntology
     *
     */
    public MOntology(OntModel ontModel) {
        classes = new OrderedMap(new CompareKey());
        properties = new OrderedMap(new CompareKey());

        this.num = OntConstants.ONTOLOGY_INF;
        buildFromOntModel(ontModel);
        
    }

    /**
     * return the URI of the ontology
     * @return ontology's URI
     * 
     */
    public String getURI() {
        Ontology ont = (Ontology) ontModel.listOntologies().next();
        return ont.getURI();
    }

    /**Returns a hash code for this element
     *
     *@return a hash code value for this object.
     */
    public int hashCode() {

        return num;
    }

    /**
     * add a class to the ontology
     * The class name is the key
     *
     *@param c the class
     */
    public void addClass(MClass c) {

        classes.add(c.getId(), c);
    }

    /**
     *add a property to the ontology
     *
     *@param p the property
     *
     */
    public void addProperty(MProperty p) {
        properties.add(p.getId(), p);
        /**
         * Added by Rajaram.
         * To create differences between class and slot add REL_ to prefix of
         * slot id.
         */
//        String newSlotID = "REL_".concat(p.getId());
//        properties.add(newSlotID, p);
        
    }

    /**
     * get the number of the ontology
     * @return the number
     *        1, the ontology 1
     *        2, the ontology 2
     *        0, the new ontology
     */
    public int Num() {

        return num;
    }

    /**
     * gets all the classes in the ontology
     * @return all the classes
     */
    public OrderedMap getClasses() {

        return classes;
    }

    /**
     * gets all the properties in the ontology
     * @return all properties
     */
    public OrderedMap getProperties() {

        return properties;
    }

    /**
     * gets a class in the ontology
     *
     * @param uri
     * @param name the name of the class
     *
     * @return the class
     */
    public MClass getClass(String uri) {

        return (MClass) classes.get(uri);
    }

    public MClass getEquivMClass(OntClass oc) {

        for (Enumeration e = getClasses().elements(); e.hasMoreElements();) {

            MClass c = (MClass) e.nextElement();
            if (c.hasEquivOntClass(oc)) {
                return c;
            }
        }

        return null;
    }

    /**
     * gets a property in the ontology
     *
     * @param name the name of property
     * @return the property
     */
    public MProperty getProperty(String name) {

        return (MProperty) properties.get(name);
    }

    /**
     * gets an element (class or property) in the ontology
     *
     * @param name the name of element
     * @return the element
     */
    public MElement getElement(String name) {

        MProperty me = (MProperty) properties.get(name);
        if (me == null) {
            return (MClass) classes.get(name);
        } else {
            return me;
        }
    }

    /** Finds those classes which have no super-class
     *   or super-class is Thing.
     *
     * @return the roots
     */
    public Array roots() {

        Array roots = new Array();

        for (Enumeration e = classes.elements(); e.hasMoreElements();) {

            MClass c = (MClass) e.nextElement();
            if (c.getSuperClasses().isEmpty() && c.getPartOf().isEmpty()) {
                roots.add(c);
            }
        }
        return roots;
    }

    /** Finds those classes which have no super-class
     *   (only consider the is-a relation)  or super-class is Thing.
     *
     * @return the roots
     */
    public Array isaRoots() {

        Array roots = new Array();

        for (Enumeration e = classes.elements(); e.hasMoreElements();) {

            MClass c = (MClass) e.nextElement();
            if (c.getSuperClasses().isEmpty()) {
                roots.add(c);
            }
        }
        return roots;
    }

    /** Finds those classes which have no sub-class
     *
     * @return the leaves
     */
    public Array leaves() {

        Array leaves = new Array();

        for (Enumeration e = classes.elements(); e.hasMoreElements();) {

            MClass c = (MClass) e.nextElement();
            if (c.getSubClasses().isEmpty()) {
                leaves.add(c);
            }
        }
        return leaves;
    }

    /**
     *print this object
     *@return the object
     */
    public String toString() {

        return "[Ontology " + Num() + "]";
    }

    /** The description (contruction) of classes (and properties)
     *  are read from the OntModel.
     *
     * @param m the OntModel
     */
    public void buildFromOntModel(OntModel m) {

        THING = m.getProfile().THING();
        NOTHING = m.getProfile().NOTHING();

        //   OntConstants.loadStopWords();

        renderProperties(m);

        for (Iterator it = m.listClasses(); it.hasNext();) {
            renderClassDescription((Resource) it.next());
        }
    }

    /******************
     *build properties
     **********************/
    private void renderProperties(OntModel m) {

        //object properties
        for (Iterator i = m.listObjectProperties(); i.hasNext();) {

            OntProperty p = (OntProperty) i.next();
            if (p.getLocalName().equals("disjointWith")
                    || p.getLocalName().equals("sameAs")
                    || p.getLocalName().equals("differentFrom")) {
                return;
            } else {
                addProperty(new MProperty(p, num));
            }
        }

        //Datatype properties
        for (Iterator i = m.listDatatypeProperties(); i.hasNext();) {
            addProperty(new MProperty((OntProperty) i.next(), num));
        }


    }

    /*******************************************
     * read superclasses, and part_of classes
     ******************************************************************/
    private MClass renderClassDescription(Resource rs) {

        //filter out the anonymous and embeded class
        if (rs.isAnon() || rs.equals(NOTHING) || rs.equals(THING)) {
            return null;
        }

        if (num == OntConstants.ONTOLOGY_INF && !((OntClass) rs).hasSuperClass(THING)) {
            return null;
        }

        if (getClass(rs.getLocalName()) != null) {
            return getClass(rs.getLocalName());
        }

        // If it is external class, skip
        if (OntConstants.EXTERNAL_NAMESPACE.contains(rs.getNameSpace())) {
            return null;
        }

        MClass clazz = null;

        //check whether MClass exist for the OntClass
        //OBS: no inference.
        for (Iterator it1 = ((OntClass) rs).listEquivalentClasses(); it1.hasNext();) {

            OntClass eq = (OntClass) it1.next();
            if (getClass(eq.getLocalName()) != null) {
                clazz = getClass(eq.getLocalName());
                break;
            }
        }

        if (clazz == null) {
            clazz = getEquivMClass((OntClass) rs);
        }

        if (clazz == null) {
            clazz = new MClass((OntClass) rs, num);
            addClass(clazz);

        } else if (!clazz.hasEquivOntClass((OntClass) rs)) //}else
        {
            clazz.addEquivOntClass((OntClass) rs);
        }


        for (Iterator it2 = ((OntClass) rs).listLabels(OntConstants.sn_lan); it2.hasNext();) {
            String term = ((Literal) it2.next()).getString(); 
            if (clazz.getPrettyName()==null || !clazz.getPrettyName().equalsIgnoreCase(term)) {
                clazz.addSynonym(term);
            }
        }


        //only list the direct superclasses (named and anonymous)
        for (Iterator i = ((OntClass) rs).listSuperClasses(true); i.hasNext();) {

            Resource c = (Resource) i.next();

            if (!c.isAnon()) {

                clazz.addSuperClass(renderClassDescription(c));

            } else if (((OntClass) c).isRestriction()) {

                Restriction r = ((OntClass) c).asRestriction();
                //part_of
                if (r.getOnProperty().getLocalName().equals(OntConstants.part)) {
                    clazz.addPartOf(renderClassDescription(r.asSomeValuesFromRestriction().getSomeValuesFrom()));
                }

            }//close restriction
        }

        return clazz;
    }
}
