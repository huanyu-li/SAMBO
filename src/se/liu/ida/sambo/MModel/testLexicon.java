/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.MModel;

/**
 * This is the class store information like rdfs:label
 * @author huali50
 */
public class testLexicon {
    //lexicon type
    private String type;
    //language type such "en" or "sn" 
    private String language;
    //The lexicon content
    private String name;
    public testLexicon(){}
    public testLexicon(String type, String language, String name){
        this.type=type;
        this.language=language;
        this.name=name;
    }
    /**
    * Get language type
    * @author huali50
    * @return this.language
    */
    public String getlanguage(){
        return this.language;
    }
    /**
    * Get laxicon content
    * @author huali50
    * @return this.name
    */
    public String getname(){
        return this.name;
    }
}
