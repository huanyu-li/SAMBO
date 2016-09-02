/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.MModel.util.OntConstants;

/**
 *
 * @author huali50
 * Class in Ontology
 */
public class testMClass implements testMElement {
    //Integer ID
    private Integer id;
    //Class URI
    private String uri;
    //Class Local name 
    private String name;
    //Class name based on one language
    private String label;
    //Comment about alignment
    private String alignComment;
    //Name for alignment
    private String alignName;
    //Super class of current class
    private HashMap<Integer, testMClass> supers;
    //Sub class of current class
    private HashMap<Integer, testMClass> subs;
    //Equivalent class of current class
    private HashMap<Integer, testMClass> equiv;
    //haspart relationship
    private HashMap<Integer, testMClass> haspart;
    //partof relationship
    private HashMap<Integer, testMClass> partof;
    //Aligned class
    private testMClass alignClass = null;
    //Aligned Super relationship
    private HashMap<Integer, testMClass> alignSupers;
    //Aligned Sub relationship
    private HashMap<Integer, testMClass>  alignSubs;
    private String Comment;
    private boolean display = false;
    private boolean highlight = false;
    private boolean is_root = true;
    private boolean is_Thing = false;
    
    public testMClass(){}
    public testMClass(Integer Id, String uri, String class_label){
        this.id = Id;
        this.uri = uri;
        this.label = class_label;
        this.name = getLocalName();
        supers = new HashMap<Integer,testMClass>();
        subs = new HashMap<Integer,testMClass>();
        equiv = new HashMap<Integer,testMClass>();
        haspart = new HashMap<Integer,testMClass>(); 
        alignSupers = new HashMap<Integer,testMClass>();
        alignSubs = new HashMap<Integer,testMClass>();
        this.alignComment = OntConstants.ALIGN_COMMENT;
    }
    /**
     * Set class id
     * @author huali50
     * @param index 
     */
    public void set_id(Integer index){
        this.id = index;
    }
    /**
     * Add subs
     * @author huali50
     * @param index
     * @param tmc 
     */
    public void addSub(int index, testMClass tmc){
        this.subs.put(index, tmc);
    }
    /**
     * Add supers
     * @author huali50
     * @param index
     * @param tmc 
     */
    public void addSuper(int index, testMClass tmc){
        this.supers.put(index, tmc);
    }
    public void addEquiv(testMClass tmc){
    }
    /**
     * Get align comment
     * @author huali50
     * @return alignComment
     */
    public String getAlignComment()
    {
        return alignComment;
    }
    /**
     * Get class local name
     * @author huali50
     * @return local name
     */
    public String getLocalName(){
        if(this.uri==null){
            return null;
        }
        else
        {
            int i = this.uri.indexOf("#") + 1;
            if(i == 0){
                i = this.uri.lastIndexOf("/") + 1;
            }
            return this.uri.substring(i);
        }
    }
    /**
     * Get aligned name
     * @author huali50
     * @return alignName
     */
    public String getAlignName()
    {
        return alignName;
    }
    /**
     * Get URI
     * @author huali50
     * @return this.uri
     */
    public String getURI(){
        return this.uri;
    }
    /**
     * Get aligned element
     * @author huali50
     * @return alignClass
     */
    public testMClass getAlignElement(){
        return alignClass;
    }
    public boolean isMClass() {
        return true;
    }
    public boolean isMProperty() {
        return false;
    }
    /**
     * Get super classes
     * @author huali50
     * @return this.supers
     */
    public HashMap<Integer, testMClass> getSuperClasses(){
        return this.supers;
    }
    /**
     * Get sub classes
     * @author huali50
     * @return this.subs
     */
    public HashMap<Integer, testMClass> getSubClasses(){
        return this.subs;
    }
    public boolean isHighlight(){
        return highlight;
    }
    public void closeHighlight() {
        highlight = !highlight;
    }
    /**
     * Sethighlight
     */
    public void highlight() {
        highlight = true;
    }
    /**
     * Get parts
     * @author huali50
     * @return this.haspart
     */
    public HashMap<Integer, testMClass> getParts(){
        return this.haspart;
    }
    /**
     * Get partof
     * @author huali50
     * @return  this.partof
     */
    public HashMap<Integer, testMClass> getPartOf(){
        return this.partof;
    }
    /**
     * Get class label
     * @author huali50
     * @return 
     */
    public String getLabel(){
        return this.label;
    }
    /**
     * Get aligned class
     * @author huali50
     * @return 
     */
    public testMClass getAlignClass(){
        return this.alignClass;
    }
    /**
     * Get aligned supers
     * @author huali50
     * @return 
     */
    public HashMap<Integer, testMClass> getAlignSupers(){
        return alignSupers;
    }
    /**
     * Get aligned subs
     * @author huali50
     * @return 
     */
    public HashMap<Integer, testMClass> getAlignSubs(){
        return alignSubs;
    }
    /**
     * Check display
     * @return 
     */
    public boolean isDisplay() {
        return display;
    }
    /**
     * Turn display flag
     */
    public void turnDisplay() {
        display = !display;
    }
    /**
     * Set display flag
     * @param d 
     */
    public void setDisplay(boolean d) {
        display = d;
    }
    /**
     * Set aligned name
     * @author huali50
     * @param align_name 
     */
    public void setAlignName(String align_name){
        this.alignName = align_name;
    }
    /**
     * Set aligned class
     * @author huali50
     * @param ac 
     */
    public void setAlignClass(testMClass ac){
        alignClass = ac;
    }
    /**
     * Ad aligned super
     * @author huali50
     * @param alignsuperclass
     * @return 
     */
    public boolean addAlignSuper(testMClass alignsuperclass){
        if(alignsuperclass == null){
            return false;
        }
        alignSupers.put(alignsuperclass.getId(),alignsuperclass);
        alignsuperclass.addAlignSub(this);
        return true;
    }
    /**
     * Add aligned sub
     * @author huali50
     * @param alignsubclass 
     */
    public void addAlignSub(testMClass alignsubclass){
        alignSubs.put(alignsubclass.getId(), alignsubclass);
    }
    /**
     * Get class Integer id
     * @author huali50
     * @return 
     */
    public Integer getId(){
        return this.id;
    }
    /**
     * Add aligned comment
     * @author huali50
     * @param c
     * @return 
     */
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
     * Set align element
     * @author huali50
     * @param n 
     */
    public void setAlignElement(testMClass n) {

        alignClass = n;
    }
    /**
     * Remove aligned sub if undo
     * @author huali50
     * @param tc 
     */
    public void removeAlignSub(testMClass tc){
        alignSubs.remove(tc.getId());
    }
    /**
     * Remove aligned super if undo
     * @author huali50
     * @param tc 
     */
    public void removeAlignSuper(testMClass tc){
        alignSupers.remove(tc.getId());
        tc.removeAlignSub(this);
    }
    /**
     * Get Comment
     * @author huali50
     * @return this.Comment
     */
    public String getComment(){
        return this.Comment;
    }
    /**
     * Set is_root
     * @author huali50
     * @param is_root 
     */
    public void setRoot(boolean is_root){
        this.is_root = is_root;
    }
    /**
     * Check if it is root
     * @author huali50
     * @return this.is_root
     */
    public boolean IsRoot(){
        return this.is_root;
    }
    /**
     * Set is_Thing
     * @author huali50
     */
    public void setThing(){
        this.is_Thing = true;
    }
    /**
     * Check if it is owl:Thing
     * @author huali50
     * @return this.is_Thing
     */
    public boolean isOWLTHING(){
        return this.is_Thing;
    }
}
