/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

/**
 *
 * @author huali50
 */
public class testPair {

    private String sourceURI = null;
    private String targetURI = null;
    private String comment = null;
    private int type = -1;
    //private Integer sid;
    //private Integer tid;
    private double sim = 0;
    /**
     * To store subset similarity value.
     */
    private double wlSim = 0;
    /**
     * To store UMLS matcher's similarity value.
     */
    private double umlsSim = 0;
    /**
     * To check if the pair shares same textual description.
     */
    private boolean hasSameLabel = false;

    private boolean empty = false;

    private boolean isAligned = false;

    /**
     * Creates the empty pair
     */
    public testPair() {
        empty = true;
    }

    /**
     * Groups two related objects together
     *
     * @param object1 object1
     * @param object2 object2
     */
    public testPair(String sourceURI, String targetURI) {
        empty = false;
        this.sourceURI = sourceURI;
        this.targetURI = targetURI;
    }
    public testPair(String sourceURI, String targetURI,double sim) {
        empty = false;
        this.sourceURI = sourceURI;
        this.targetURI = targetURI;
        this.sim = sim;
    }

    /**
     * Gets the first object in a pair.
     *
     * @return The first object.
     */
    public String getSource() {
        return this.sourceURI;
    }

    public String getLocalName(String uri) {
        if (uri == null) {
            return null;
        } else {
            int i = uri.indexOf("#") + 1;
            if (i == 0) {
                i = uri.lastIndexOf("/") + 1;
            }
            return uri.substring(i);
        }
    }

    /**
     * Gets the second object in a pair.
     *
     * @return The second object.
     */
    public String getTarget() {
        return this.targetURI;
    }

    public double getSim() {
        return sim;
    }

    public void setSim(double sim) {
        this.sim = sim;
    }

    public void resetSim() {
        this.sim = 0;
    }

    /**
     * Chechs if a pair contains a given object.
     *
     * @param object The object that shoul be checked.
     * @return True if the object is in the pair, otherwise false.
     */
    public boolean contains(String uri) {
        // return ((object == getObject1()) || (object == getObject2()));
        return (this.sourceURI.equals(uri) || this.targetURI.equals(uri));
    }

    /**
     * Gets the other object in the pair.
     *
     * @param object The reference object. That is the object in the pair that
     * you have.
     * @return The other object in the pair, that is not the one that is given
     * as input parameter.
     */
    public String getOtherindex(String uri) {
        // if (object == getObject1()) return getObject2();
        // if (object == getObject2()) return getObject1();

        if (this.sourceURI.equals(uri)) {
            return getTarget();
        }
        if (this.targetURI.equals(uri)) {
            return getSource();
        }

        return null;
    }

    /**
     * Checks if two pairs are equal, that is if they contains the same two
     * objects. The order of the objects is not relevant.
     *
     * @param obj object
     * @return True if the pairs are equal, otherwise false.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof testPair)) {
            return false;
        }
        testPair pair = (testPair) obj;
        return (pair.contains(getSource()) && pair.contains(getTarget()));
    }

    public int hashCode() {

        return sourceURI.hashCode() + targetURI.hashCode();
    }

    /**
     * Puts the pair in a nice format for printing.
     *
     * @return The pair as a string in a nice format.
     */
    public String toString() {
        return "[" + sourceURI + ", " + targetURI + ", " + sim + "]";
    }

    /**
     * Switches the order of the objects in the pair
     */
    public void switchPair() {

        String temp = sourceURI;
        sourceURI = targetURI;
        targetURI = temp;

    }

//    public Pair getStringPair(String p){
//
//        this.pairStr = p;
//        this.pairObj = (Object)this.pairStr;
//        Pair pr = (Pair)this.pairStr;
//        return pr;
//    }
    /**
     * Checks if this pair is empty
     *
     * @return true if the pair is empty
     */
    public boolean isEmptyPair() {
        return empty;
    }

    /**
     * To set if the pair is aligned are not.
     *
     * @param Aligned
     */
    public void setAlignment(boolean Aligned) {
        isAligned = Aligned;
    }

    /**
     * Check if the pair is aligned are not.
     *
     * @returns true if the pair is aligned.
     */
    public boolean getAlignment() {
        return isAligned;
    }

    /**
     * To set WordList matcher similarity value, will be useful in some filters.
     *
     * @param sim Similarity value.
     */
    public void setWLSim(double sim) {
        wlSim = sim;
    }

    /**
     * To set UMLS matcher similarity value, will be useful in some filters.
     *
     * @param sim Similarity value.
     */
    public void setUMLSSim(double sim) {
        umlsSim = sim;
    }

    /**
     * To set label information, will be useful in some filters.
     *
     * @param sim Similarity value.
     */
    public void setSameLabelInfo(boolean info) {
        hasSameLabel = info;
    }

    /**
     * To get WordList matcher similarity value, will be useful in some filters.
     * @return Similarity value.
     */
    public double getWLSim() {
        return wlSim;
    }

    /**
     * To get umls matcher similarity value, will be useful in some filters.
     *
     * @return Similarity value.
     */
    public double getUMLSSim() {
        return umlsSim;
    }
    /**
     * To get label info, will be useful in some filters.
     *
     * @return true if the concepts in pair share a label, else false.
     */
    public boolean getSameLabelInfo() {
        return hasSameLabel;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public String getComment(){
        return this.comment;
    }
}
