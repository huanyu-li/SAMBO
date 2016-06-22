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

/**
 *
 * @author huali50
 */
public class testMClass implements testMElement {
    private String uri;
    private String name;
    private String alignComment;
    private String alignName;
    private testMElement alignElement;
    private HashMap<Integer, testMClass> supers;
    private HashMap<Integer, testMClass> subs;
    private HashMap<Integer, testMClass> equiv;
    public testMClass(){}
    public testMClass(String uri){
        this.uri = uri;
        this.name = getLocalName();
        supers = new HashMap<Integer,testMClass>();
        subs = new HashMap<Integer,testMClass>();
        equiv = new HashMap<Integer,testMClass>();
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
}
