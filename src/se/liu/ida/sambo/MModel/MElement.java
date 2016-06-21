/*
 * MElement.java
 *
 */
package se.liu.ida.sambo.MModel;

/**  uer interface that represents an ontology component, e.g. concept and relation.
 *
 * @author  He Tan
 * @version 
 */
import java.util.List;

public interface MElement {

    public boolean isExternal();

    public void setAlignElement(MElement me);

    /**Returns the uri of this element.
     *
     *@return the uri.
     **/
    public String getURI();

    /**Returns the id of this element.
     *
     *@return the id.
     **/
    public String getId();

    /**Returns the label of this element.
     *
     *@return the label.
     **/
    public String getLabel(String lan);

    /**Returns the label of this element.
     *
     *@return the label.
     **/
    public String getLabel();

    /**
     *Returns the comment about this element
     *
     *@return the comment
     **/
    public String getComment(String lan);

    /**
     *Returns the comment about this element
     *
     *@return the comment
     **/
    public String getComment();

    /**
     *Returns the comment about this element
     *
     *@return the comment
     **/
    public List listComments();

    /**Returns the pretty name of this element.
     *
     *the pretty name is in lowercase, and only include letters and digits
     *
     *@return the name.
     **/
    public String getPrettyName();

    /*Returns the MOntology to which the element belongs.
     *
     *1, the first ontology
     *2, the second ontology
     *3, the new ontology
     *
     *@return the num
     */
    public int whichMOnto();

    /**Returns the alignment comment.
     *
     *@return the comment.
     **/
    public String getAlignComment();

    /**Gets the name of the element will be in the new ontology,
     *  if it is aligned to another element.
     *
     *@return name, if this element is aligned to another element.
     *        null, if black-copy the element.
     *        
     */
    public MElement getAlignElement();

    /**Gets the name of the element will be in the new ontology, 
     *   if it has a new name in the new ontology.
     *
     *@return name, if the element has a new name
     *        null, if black-copy the element
     */
    public String getAlignName();

    /**Check whether the element is a concpet
     *
     *@return true, if it is a concept
     */
    public boolean isMClass();

    /**Check whether the element is a relation
     *
     *@return true, if it is a relation
     */
    public boolean isMProperty();

    /**Compares this element to the specifed object 
     *
     *@return true, if they are equal
     */
    public boolean equals(Object object);

    /**Returns a hash code for this element
     *
     *@return a hash code value for this object.
     */
    public int hashCode();

    /**Returns a string representation of this element.
     *
     *@return a string representation
     */
    public String toString();
}

