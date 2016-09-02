/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

import se.liu.ida.sambo.MModel.util.OntConstants;

/**
 *
 * @author huali50
 */
public  class testMProperty implements testMElement {
    private String uri;
    private String name;
    private String prettyname;
    private String alignName;
    private String alignComment = OntConstants.ALIGN_COMMENT;;
    private testMElement alignElement;
    private String propertyType;
    /**
     * Get Align Name
     * @huali50
     * @return 
     */
    public String getAlignName()
    {
        return alignName;
    }
    /**
     * Set Align Name
     * @author huali50
     * @param name 
     */
    public void setAlignName(String name){
        this.alignName = name;
    }
    /**
     * Set Align Element
     * @author huali50
     * @param property 
     */
    public void setAlignElement(testMProperty property){
        this.alignElement = property;
    }
    /**
     * Get URI
     * @author huali50
     * @return uri
     */
    public String getURI(){
        return this.uri;
    }
    /**
     * Get URI by Name
     * @author huali50
     * @param Name
     * @return uri
     */
    public String getURIbyName(String Name){
        if(this.name == Name)
            return this.uri;
        else
            return null;
    }
    /**
     * Get Align Comment
     * @author huali50
     * @return 
     */
    public String getAlignComment()
    {
        return alignComment;
    }
    /**
     * Get pretty name
     * @author huali50
     * @return 
     */
    public String getPrettyName()
    {
        return prettyname;
    }
    /**
     * Get Local Name
     * @author huali50
     * @return 
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
     * Get Align Element
     * @author huali50
     * @return alignElement
     */
    public testMElement getAlignElement(){
        return alignElement;
    }
    /**
     * Check if this is class
     * @author huali50
     * @return 
     */
    public boolean isMClass() {
        return false;
    }
    /**
     * Check if this is property
     * @author huali50
     * @return 
     */
    public boolean isMProperty() {
        return true;
    }
    /**
     * Get Name of property
     * @author huali50
     * @return name
     */
    public String getName(){
        return this.name;
    }
    /**
     * Set type of property
     * @author huali50
     * @param type 
     */
    public void setType(String type){
        if(type.equals("DATA"))
            this.propertyType = "DataProperty";
        else if(type.equals("OBJECT"))
            this.propertyType = "ObjectProperty";
    }
    /**
     * Get type of property
     * @author huali50
     * @return propertyType
     */
    public String getType(){
        return this.propertyType;
    }
    /**
     * Add align Comment
     * @author huali50
     * @param c
     * @return 
     */
    public boolean addAlignComment(String c){
        if (c == null) {

            alignComment = OntConstants.ALIGN_COMMENT;
            return false;

        } else {
            alignComment += "\n" + c;
        }

        return true;
    }
}
