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
public class testLexicon {
    private String type;
    private String language;
    private String name;
    public testLexicon(){}
    public testLexicon(String type, String language, String name){
        this.type=type;
        this.language=language;
        this.name=name;
    }
    public String getlanguage(){
        return this.language;
    }
    public String getname(){
        return this.name;
    }
}
