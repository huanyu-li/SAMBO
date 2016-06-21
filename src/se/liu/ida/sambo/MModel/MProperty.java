/*
 * MProperty.java
 */
package se.liu.ida.sambo.MModel;

import se.liu.ida.sambo.MModel.util.OntConstants;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import com.hp.hpl.jena.ontology.OntProperty;
import java.util.List;

/** The Class represents an ontology node characterising a property description.
 *
 * @author  He Tan
 * @version 
 */
public class MProperty implements MElement {

    //indicates to which the property belongs
    private int num;
    //the corresponding OntProperty in OntModel
    private OntProperty ontPro;
    //the pretty name
    private String prettyname = null;
    private String alignComment = null;
    //the name if this property is to be merged
    private MProperty alignPro = null;
    //the name if there is when the property is copied to
    // the new ontology
    private String alignName = null;

    /** Creates new MProperty
     *
     * @param op the corresponding ontProperty in OntModel
     * @param name the name of the property
     * @param num which model the property belong to
     */
    public MProperty(OntProperty op, int num) {

        this.ontPro = op;
        this.num = num;
        this.alignComment = OntConstants.ALIGN_COMMENT;

//        if(num != OntConstants.ONTOLOGY_INF)
//            prettyname = OntConstants.clearname(op.getLabel(OntConstants.lan).trim());

        if (op.getLabel(null) != null) {
            this.prettyname = NameProcessor.getInstance().basicCleanName(op.getLabel(null).trim());
        } else {
            this.prettyname = NameProcessor.getInstance().basicCleanName(op.getLocalName().trim());
        }
        System.out.println(op.getLocalName() + ";" + prettyname + ";" + op.getLabel(OntConstants.lan) + ";" + op.getLabel(null));
    }

    /*Returns to which MOntology the property belongs
     *
     *@return the num
     */
    public int whichMOnto() {
        return num;
    }

    /**
     *Returns the property uri
     *
     *@return the uir
     **/
    public String getURI() {

        return ontPro.getURI();
    }

    /**
     *Returns the property id
     *
     *@return the id
     **/
    public String getId() {

        return ontPro.getLocalName();
    }

    /**
     *Returns the pretty name of the property
     *
     *@return the pretty name
     **/
    public String getPrettyName() {

        return prettyname;
    }


    /*Gets the corresponding OntProperty
     *   Creates such links since it takes time to
     *    get the OntProperty in OntModel each time.
     */
    public OntProperty OntProperty() {
        return ontPro;
    }

    /**
     *Returns the label about this class
     *
     *@return the label
     **/
    public String getLabel(String lan) {

        if (ontPro.getLabel(lan) == null) {
            return "";
        }

        return ontPro.getLabel(lan);
    }

    /**
     *Returns the label about this class
     *
     *@return the label
     **/
    public String getLabel() {

        if (ontPro.getLabel(OntConstants.lan) == null) {
            return "";
        }

        return ontPro.getLabel(OntConstants.lan);
    }

    /**
     *Returns the comment about the property
     *
     *@return the comment
     **/
    public String getComment(String lan) {

        return ontPro.getComment(lan);
    }

    /**
     *Returns the comment about the property
     *
     *@return the comment
     **/
    public String getComment() {

        return ontPro.getComment(null);
    }

    /**
     *Returns the comment about the property
     *
     *@return the comment
     **/
    public List listComments() {

        return ontPro.listComments(null).toList();
    }

    /**
     * gets the alignment comment      
     *
     * @return comment
     */
    public String getAlignComment() {

        return alignComment;
    }

    /**
     *Returns whether this property is functional
     *
     *@return whether this property is functional
     */
    public boolean isFunctional() {
        return ontPro.isFunctionalProperty();
    }

    /**
     *Returns the type of the property
     *
     *@return the type
     **/
    public String getType() {

        return ontPro.getRDFType().getLocalName();
    }

    /**
     *Returns a list of its ranges
     *
     *@return a list of ranges
     **/
    public List getRanges() {

        return ontPro.listRange().toList();
    }

    /**
     *Returns a list of its domains
     *
     *@return a list of domains
     **/
    public List getDomains() {

        return ontPro.listDomain().toList();
    }

    /**
     * Sets the alignment comment
     *   if it is to be merged
     *
     * @param n the name
     */
    public boolean addAlignComment(String c) {

        if (c == null) {

            alignComment = OntConstants.ALIGN_COMMENT;
            return false;

        } else {
            alignComment += "\n" + c;
        }

        return true;
    }

    /**
     * Sets the name of this property in the new ontology
     *   if it is to be merged
     *
     * @param n the name
     */
    public boolean setAlignElement(MProperty n) {

        if (n == null) {
            return false;
        } else {
            alignPro = n;
        }

        return true;
    }

    /**
     *Returns the name of the property in the new ontology
     *    if it is to be merged
     *
     *@return the name of the property.
     **/
    public MElement getAlignElement() {

        return alignPro;
    }

    /**
     * Sets the name of the property in the new ontology
     *  if it is to be copied.
     *
     * @param n the name
     */
    public void setAlignName(String n) {

        alignName = n;
    }

    /**
     *Returns the name of the property in the new ontology
     *  if it is applicable.
     *
     * @return name, if the property is to be merged.
     *        null, if black-copy the property.
     */
    public String getAlignName() {

        return alignName;
    }

    /**
     * Check wether the element is a MClass 
     *
     *@return false
     */
    public boolean isMClass() {
        return false;
    }

    /**
     * Check wether the element is a MProperty
     *
     *@return true
     */
    public boolean isMProperty() {
        return true;
    }

    /**
     * Compares this property to the specified object.
     */
    public boolean equals(Object object) {

        if (object instanceof MProperty) {
            if ((((MProperty) object).num == num) && getURI().equals(((MProperty) object).getURI())) {
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
    @Override
    public String toString() {

        return "[" + getType() + ":" + getId() + "," + num + "]";
    }

    @Override
    public void setAlignElement(MElement me) {
        alignPro = (MProperty) me;
    }

    @Override
    public boolean isExternal() {
        String ns = ontPro.getNameSpace();
        boolean condition = OntConstants.EXTERNAL_NAMESPACE.contains(ns);
        return condition;
    }
}
