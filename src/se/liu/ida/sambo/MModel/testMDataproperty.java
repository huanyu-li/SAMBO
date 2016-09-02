/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

/**
 *
 * @author huali50
 * Data Property in Ontology
 */
public class testMDataproperty extends testMProperty {
    private String uri;
    private String name;
    public testMDataproperty(){
    }
    public testMDataproperty(String uri){
        this.uri = uri;
        this.name = getdatapropertyname();
    }
    public String getdatapropertyname(){
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
    public String getURI(){
        return this.uri;
    }
    public String getName(){
        return this.name;
    }
}
