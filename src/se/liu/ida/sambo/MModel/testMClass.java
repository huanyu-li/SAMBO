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
 */
public class testMClass implements testMElement {
    private Integer id;
    private String uri;
    private String name;
    private String label;
    private String alignComment;
    private String alignName;
    private testMElement alignElement;
    private HashMap<Integer, testMClass> supers;
    private HashMap<Integer, testMClass> subs;
    private HashMap<Integer, testMClass> equiv;
    private HashMap<Integer, testMClass> haspart;    
    private HashMap<Integer, testMClass> partof;
    private testMClass alignClass = null;
    private HashMap<Integer, testMClass> alignSupers;
    private HashMap<Integer, testMClass>  alignSubs;
    private boolean display = false;
    private boolean highlight = false;
    
    public testMClass(){}
    public testMClass(String uri){
        this.uri = uri;
        this.name = getLocalName();
        supers = new HashMap<Integer,testMClass>();
        subs = new HashMap<Integer,testMClass>();
        equiv = new HashMap<Integer,testMClass>();
        haspart = new HashMap<Integer,testMClass>(); 
        alignSupers = new HashMap<Integer,testMClass>();
        alignSubs = new HashMap<Integer,testMClass>();
    }
    public void set_id(Integer index){
        this.id = index;
    }
    public void addSuper(int index, testMClass tmc){
    }
    public void addSub(int index, testMClass tmc){
        this.subs.put(index, tmc);
    }
    public void addEquiv(testMClass tmc){
    }
    public String getAlignComment()
    {
        return alignComment;
    }
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
    public String getAlignName()
    {
        return alignName;
    }
    public String getURI(){
        return this.uri;
    }
    public String getURIbyName(String Name){
        if(this.name == Name)
            return this.uri;
        else
            return null;
    }
    public testMElement getAlignElement(){
        return alignElement;
    }

    public boolean isMClass() {
        return true;
    }
    public boolean isMProperty() {
        return false;
    }
    public HashMap<Integer, testMClass> getSuperClasses(){
        return this.supers;
    }
    public HashMap<Integer, testMClass> getSubClasses(){
        return this.subs;
    }
    public boolean isHighlight(){
        return highlight;
    }
    public void closeHighlight() {
        highlight = !highlight;
    }
    public void highlight() {
        highlight = true;
    }
    public HashMap<Integer, testMClass> getParts(){
        return this.haspart;
    }
        public HashMap<Integer, testMClass> getPartOf(){
        return this.partof;
    }
    public String getLabel(){
        return this.label;
    }
    public testMClass getAlignClass(){
        return this.alignClass;
    }

    public HashMap<Integer, testMClass> getAlignSupers(){
        return alignSubs;
    }
    public void removeAlignSub(){
    }
    public HashMap<Integer, testMClass> getAlignSubs(){
        return alignSubs;
    }
    public boolean isDisplay() {
        return display;
    }
    public void turnDisplay() {
        display = !display;
    }
    public void setDisplay(boolean d) {
        display = d;
    }
    public void setAlignName(String align_name){
        this.alignName = align_name;
    }
    public void setAlignClass(testMClass ac){
        alignClass = ac;
    }
    public boolean addAlignSuper(testMClass alignsuperclass){
        if(alignsuperclass == null){
            return false;
        }
        alignSupers.put(alignsuperclass.getId(),alignsuperclass);
        alignsuperclass.addAlignSub(this);
        return true;
    }
    public void addAlignSub(testMClass alignsubclass){
        alignSubs.put(alignsubclass.getId(), alignsubclass);
    }
    public Integer getId(){
        return this.id;
    }
    public boolean addAlignComment(String c) {

        if (c == null) {

            alignComment = OntConstants.ALIGN_COMMENT;
            return false;

        } else {
            alignComment += c;
        }

        return true;
    }
    public void setAlignElement(testMClass n) {

        alignClass = n;
    }
    public void removeAlignSub(testMClass tc){
        alignSubs.remove(tc.getId());
    }
    public void removeAlignSuper(testMClass tc){
        alignSupers.remove(tc.getId());
        tc.removeAlignSub(this);
        
    }
    
}
