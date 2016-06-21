/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

/**
 *
 * @author huali50
 */
public  class testMProperty implements testMElement {
    private String uri;
    private String name;
    private String prettyname;
    private String alignName;
    private String alignComment;
    private testMElement alignElement;
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
    public String getAlignComment()
    {
        return alignComment;
    }
    public String getPrettyName()
    {
        return prettyname;
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
    public testMElement getAlignElement(){
        return alignElement;
    }
    public boolean isMClass() {
        return false;
    }
    public boolean isMProperty() {
        return true;
    }
}
