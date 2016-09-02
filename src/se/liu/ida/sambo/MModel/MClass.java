/*
 * MClass.java
 */
package se.liu.ida.sambo.MModel;

import se.liu.ida.sambo.MModel.util.OntConstants;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import java.util.Enumeration;
import java.util.List;

import com.objectspace.jgl.Array;
import com.objectspace.jgl.HashMap;

import com.hp.hpl.jena.ontology.OntClass;

/**The interface represents an ontology node characterising a class description.
 *
 * @author He Tan
 */
public class MClass implements MElement {

    //indicate to which this class belongs
    private int num;
    //the corresponding OntClass
    private OntClass ontClass;
    //the pretty label
    private String prettyname = null;
    private Array synonyms;
    private Array prettysyn;
    private String alignComment = null;
    //a list of the classes of which this class are part
    private Array partOf;
    //a list of the classes which are part_of this class
    private Array hasPart;
    //a list of Super Classes
    private Array supers;
    //a list of Sub-Classes
    private Array subs;
    //a list of Equivalent Classes
    private Array equiv;
    //the name if this class is aligned
    private MClass alignClass = null;
    //the name if the class is copied with a new name
    private String alignName = null;
    //a list of Super-Classes included
    private Array alignSupers;
    //a list of Sub-Classes included
    private Array alignSubs;
    //to expand the subclasses of the class
    private boolean display = false;
    //to highlight the class
    private boolean highlight = false;

    /** Creates new MClass
     *
     * @param oc the corresponding OntClass in OntModel
     * @param num the ontology to which the class belong
     */
    public MClass(OntClass oc, int num) {

        this.ontClass = oc;
        this.num = num;
        //if(num != OntConstants.ONTOLOGY_INF)
        // this.prettyname =  OntConstants.clearname(oc.getLocalName().trim());
        //    this.prettyname =  OntConstants.clearname(oc.getLabel(OntConstants.lan));

        if (num != OntConstants.ONTOLOGY_INF) {
            String term;
            if (oc.getLabel(OntConstants.lan) != null) {
                term = oc.getLabel(OntConstants.lan).trim();
            } else if (oc.getLabel(null) != null) {
                term = oc.getLabel(null).trim();
            } else {
                term = oc.getLocalName().trim();
            }
            this.prettyname = NameProcessor.getInstance().advCleanName(term);
            System.out.println(oc.getLocalName() + ";" + prettyname + ";" + oc.getLabel(OntConstants.lan) + ";" + oc.getLabel(null));
        }


        synonyms = new Array();

        if (num != OntConstants.ONTOLOGY_INF) {
            prettysyn = new Array();
        }

        supers = new Array();
        subs = new Array();

        partOf = new Array();
        hasPart = new Array();

        equiv = new Array();

        this.alignComment = OntConstants.ALIGN_COMMENT;

        alignSupers = new Array();
        alignSubs = new Array();
    }

    /*Gets the corresponding OntClass
     *
     * @return OntClass
     */
    public OntClass OntClass() {
        return ontClass;
    }

    /*Returns to which MOntology class belongs
     *
     *@return num
     */
    public int whichMOnto() {
        return num;
    }

    /**
     *Returns the class uri
     *
     *@return the uir
     **/
    public String getURI() {

        return ontClass.getURI();
    }

    /**
     *Returns the name of the class
     *
     *@return the name of the class.
     **/
    public String getId() {

        return ontClass.getLocalName();
    }

    /**
     *Returns the pretty name which
     * is applied in mactching algorithm
     *
     *@return the name of the class.
     **/
    @Override
    public String getPrettyName() {

        return prettyname;
    }

    public Array getPrettySyn() {
        return prettysyn;
    }

    /**
     *Returns the comment about this class
     *
     *@return the comment
     **/
    public String getComment(String lan) {

        return ontClass.getComment(lan);
    }

    /**
     *Returns the comment about this class
     *
     *@return the comment
     **/
    public String getComment() {

        return ontClass.getComment(null);
    }

    /**
     *Returns the comment about this class
     *
     *@return the comment
     **/
    public List listComments() {

        return ontClass.listComments(null).toList();
    }

    /**
     *Returns the label about this class
     *
     *@return the label
     **/
    public String getLabel(String lan) {

        //if(ontClass.getLabel(lan) == null)
        //    return "";
        if (ontClass.getLabel(lan) != null) {
            return ontClass.getLabel(lan);
        } else if (ontClass.getLabel(null) != null) {
            return ontClass.getLabel(null);
        } else {
            return "(no label)";
        }

    }

    /**
     *Returns the label about this class
     *
     *@return the label
     **/
    public String getLabel() {

        //   if(ontClass.getLabel(OntConstants.lan) == null)
        //       return "";

        if (ontClass.getLabel(OntConstants.lan) != null) {
            return ontClass.getLabel(OntConstants.lan);
        } else if (ontClass.getLabel(null) != null) {
            return ontClass.getLabel(null);
        } else {
            return "(no label)";
        }
    }

    /**
     *Returns a list of its synonym OntClasses
     *
     *@return a list of synonym OntClasses
     **/
    public Array getSynonyms() {

        return synonyms;
    }

    public void addSynonym(String s) {

        synonyms.add(s);
        if (num != OntConstants.ONTOLOGY_INF) {
            prettysyn.add(NameProcessor.getInstance().advCleanName(s));
        }
    }

    /**
     *Returns a list of classes those this class is part_of
     *
     *@return a list of classes
     **/
    public Array getPartOf() {

        return partOf;
    }

    /**
     *Returns a list of classes which are part_of this class
     *
     *@return a list of classes
     **/
    public Array getParts() {

        return hasPart;
    }

    /**
     *Returns a list of classes those this class direct is-a
     *
     *@return a list of classes
     **/
    public Array getSuperClasses() {

        return supers;
    }

    /**
     *Returns a list of classes which are direct is-a of this class
     *
     *@return a list of classes
     **/
    public Array getSubClasses() {

        return subs;
    }

    /**
     *Returns a list of classes which are equivlent to this class
     *
     *@return a list of the equivlent classes
     **/
    public boolean addEquivOntClass(OntClass ec) {

        if (ec == null) {
            return false;
        }

        equiv.add(ec);
        return true;
    }

    /**
     * Adds a class which this class is part_of
     *
     * @param c the class
     */
    public boolean addPartOf(MClass c) {

        if (c == null) {
            return false;
        }

        if (partOf.contains(c)) {
            return false;
        }

        partOf.add(c);
        c.addHasPart(this);
        return true;
    }

    /**
     * Adds a class which is part_of this class
     *
     * @param c the class
     */
    void addHasPart(MClass c) {

        hasPart.add(c);
    }

    /**
     * Adds a class which this class directly is-a
     *
     * @param c the class
     */
    public boolean addSuperClass(MClass c) {

        if (c == null) {
            return false;
        }

        if (supers.contains(c)) {
            return false;
        }

        supers.add(c);
        c.addSubClass(this);
        return true;
    }

    /**
     * Removes a class which this class directly is-a
     *
     * @param c the class
     */
    public boolean removeSuperClass(MClass c) {
        if (c != null) {
            supers.remove(c);
            c.removeSubClass(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a class which directly is-a this class
     *
     * @param c the super classes
     */
    void addSubClass(MClass c) {
        subs.add(c);
    }

    /**
     * Removes a class which directly is-a this class
     *
     * @param c the super classes
     */
    void removeSubClass(MClass c) {
        supers.remove(c);
    }

    /**
     *Sets the comment about the class
     *
     *@param c the comment
     **/
    public boolean addAlignComment(String c) {

        if (c == null) {
            alignComment = OntConstants.ALIGN_COMMENT;
            return false;

        } else {
            alignComment += c;
        }

        return true;
    }

    /**
     *Sets the comment about the class
     *
     *@param c the comment
     **/
    public String getAlignComment() {

        return alignComment;
    }

    /**
     * Sets the name of this class in the new ontology,
     *  if it is aligned.
     *
     * @param n the new name
     */
    public void setAlignElement(MClass n) {

        alignClass = n;
    }

    /**
     * Sets the name of this class in the new ontology,
     *  if it is to be copied with a new name
     *
     * @param n the new name
     */
    public void setAlignName(String n) {

        alignName = n;
    }

    /**
     *Returns the name of this class in the new ontology,
     *   if it is to be merged
     *
     *@return name, if the class is to be merged.
     *        null, if black-copy the class.
     **/
    public MElement getAlignElement() {

        return alignClass;
    }

    /**
     * the name of this class in the new ontology,
     *  if it is to be copied
     *
     *@return name, if the class is to be copied.
     *        null, if black-copy the class.
     **/
    public String getAlignName() {

        return alignName;
    }

    /**
     * Add a class which is included as super-class of this class
     *
     * @param c the classes
     */
    public boolean addAlignSuper(MClass c) {

        if (c == null) {
            return false;
        }

        alignSupers.add(c);
        c.addAlignSub(this);

        return true;
    }

    /**
     * Remove a super class from the list
     */
    public void removeAlignSuper() {

        ((MClass) alignSupers.popBack()).removeAlignSub();
    }

    /**
     *Returns a list of classes which are included as super-class of this class
     *
     *@return a list of the classes
     **/
    public Array getAlignSupers() {

        return alignSupers;
    }

    /**
     * Add a class which is included as sub-class of this class
     *
     * @param c the classes
     */
    void addAlignSub(MClass c) {

        alignSubs.add(c);
    }

    /**
     * Remove a sub class from the list
     */
    void removeAlignSub() {

        alignSubs.popBack();
    }

    /**
     *Returns a list of classes which are included as sub-class of this class
     *
     *@return a list of the new subClasses
     **/
    public Array getAlignSubs() {

        return alignSubs;
    }

    /**
     *Returns a list of classes which are equivlent to this class
     *
     *@return a list of the equivlent classes
     **/
    public Array getEquivOnttClasses() {

        return equiv;
    }

    public boolean hasEquivOntClass(OntClass oc) {

        if (equiv.contains(oc)) {
            return true;
        }

        return false;
    }

    /**
     * Returns whether the class is displayed in the tree structure
     *
     *@ return true, display the class in the tree structure
     *         false, hide the class in the tree structure
     */
    public boolean isDisplay() {
        return display;
    }

    /*
     * Change the display status of this class
     */
    public void turnDisplay() {
        display = !display;
    }

    /*
     * Change the display status of this class
     */
    public void setDisplay(boolean d) {
        display = d;
    }

    /**
     * Returns whether the class is highlighted
     *
     *@ return true, highlight
     */
    public boolean isHighlight() {
        return highlight;
    }

    /*
     * Change the hightlight status of this class
     */
    public void closeHighlight() {
        highlight = !highlight;
    }

    /*
     * Change the hightlight status of this class
     */
    public void highlight() {
        highlight = true;
    }

    /**
     * Compares this class to the specified object.
     */
    public boolean equals(Object object) {

        if (object != null && object instanceof MClass) {
            if (((MClass) object).num == num && getURI().equals(((MClass) object).getURI())) {
                return true;
            }
        }

        return false;

    }

    /**Returns a hash code for this element
     *
     *@return a hash code value for this object.
     */
    public int hashCode() {

        return getURI().hashCode();
    }

    /**
     *print this object
     *@return the object
     */
    public String toString() {
//[class:GO_0007610,1]
        return "[class:" + getId() + "," + num + "]";
    }

    /**
     * Check wether the element is a MClass
     *
     *@return true
     */
    public boolean isMClass() {
        return true;
    }

    /**
     * Check wether the element is a MProperty
     *
     *@return false
     */
    public boolean isMProperty() {
        return false;
    }

    @Override
    public void setAlignElement(MElement me) {
        alignClass = (MClass) me;
    }

    @Override
    public boolean isExternal() {
        String ns = ontClass.getNameSpace();
        boolean condition = OntConstants.EXTERNAL_NAMESPACE.contains(ns);
        return condition;
    }
}
