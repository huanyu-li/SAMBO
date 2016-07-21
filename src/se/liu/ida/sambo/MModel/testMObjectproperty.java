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
public class testMObjectproperty extends testMProperty {
    private String uri;
    private String name;
    public testMObjectproperty(){
    }
    public testMObjectproperty(String uri){
        this.uri = uri;
        this.name = getobjectpropertyname();
    }
    public String getobjectpropertyname(){
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
