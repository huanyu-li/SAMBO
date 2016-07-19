/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import se.liu.ida.sambo.MModel.testLexicon;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.EditDistance;

/**
 *
 * @author huali50
 */
public class Task {
    private Integer source_id;
    private Integer target_id;
    private HashSet<testLexicon> sourcelexicon;
    private HashSet<testLexicon> targetlexicon;
    private double value;
    public Task(Integer sid, Integer tid, HashSet<testLexicon> sourcelexicon, HashSet<testLexicon> targetlexicon){
        this.source_id = sid;
        this.target_id = tid;
        this.sourcelexicon = sourcelexicon;
        this.targetlexicon = targetlexicon;
    }
    public void compute_sim(Matcher matcher){
        double max_sim = 0;
        
            for(testLexicon stl : sourcelexicon){
                for(testLexicon ttl : targetlexicon){
                    if(stl.getlanguage().equals(ttl.getlanguage())){
                        double similarity = matcher.getSimValue(stl.getname(),ttl.getname());
                        if(similarity > max_sim)
                            max_sim = similarity;
                    }
                }
            }
            
            value = max_sim;
    }
    public double getsimilarity(){
        return value;
    }
    public HashSet<testLexicon> getsource(){
        return this.sourcelexicon;
    }
    public HashSet<testLexicon> gettarget(){
        return this.targetlexicon;
    }
    public Integer getsourceid(){
        return this.source_id;
    }
    public Integer gettargetid(){
        return this.target_id;
    }
}
