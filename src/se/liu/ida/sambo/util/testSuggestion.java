/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

import java.util.Vector;

/**
 *
 * @author huali50
 */
public class testSuggestion {
    testPair pair;
    Vector list;
    int remainingSugs = 0;
    
    
    //reset new name or/and new type;
    boolean reset = false;  
    
    int reset_num = 100;
    String reset_value;
    
    /** Creates a new instance of Suggestion */
    public testSuggestion() {
    }
    
    public testSuggestion(testPair pair) {
        this.pair = pair;
    }
    
    public testSuggestion(Vector list){
        this.list = list;
    }
    
    public testSuggestion(testPair pair, int rs) {
        this.pair = pair;
        this.remainingSugs = rs;
    }
    
    public testSuggestion(Vector list, int rs){
        this.list = list;
        this.remainingSugs = rs;
    }    
  
    
    public testPair getPair(){
        return pair;
    }
    
    public Vector getPairList(){
        return list;
    }
    
    public int getRemainingSug(){
        return remainingSugs;
    }
    
    public boolean reset(){
        return reset;
    }  
    
      
    public void reset(boolean r){
        reset = r;
    } 
}
