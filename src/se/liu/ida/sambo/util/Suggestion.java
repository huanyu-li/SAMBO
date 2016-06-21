/*
 * Suggestion.java
 *
 */

package se.liu.ida.sambo.util;


/**
 *
 * @author hetan
 */

import java.util.Vector;

public class Suggestion {
    
    Pair pair;
    Vector list;
    int remainingSugs = 0;
    
    
    //reset new name or/and new type;
    boolean reset = false;  
    
    int reset_num = 100;
    String reset_value;
    
    /** Creates a new instance of Suggestion */
    public Suggestion() {
    }
    
    public Suggestion(Pair pair) {
        this.pair = pair;
    }
    
    public Suggestion(Vector list){
        this.list = list;
    }
    
    public Suggestion(Pair pair, int rs) {
        this.pair = pair;
        this.remainingSugs = rs;
    }
    
    public Suggestion(Vector list, int rs){
        this.list = list;
        this.remainingSugs = rs;
    }    
  
    
    public Pair getPair(){
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
