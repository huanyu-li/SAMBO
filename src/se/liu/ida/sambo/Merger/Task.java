/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.util.HashSet;
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
    private Matcher[] matcher_list;
    private double[] values;
    public Task(Integer sid, Integer tid, HashSet<testLexicon> sourcelexicon, HashSet<testLexicon> targetlexicon, Matcher[] matcherlist){
        this.source_id = sid;
        this.target_id = tid;
        this.sourcelexicon = sourcelexicon;
        this.targetlexicon = targetlexicon;
        this.matcher_list = matcherlist;
    }
    public void compute_sim(){
        double max_sim = 0;
        for (int i = 0; i < matcher_list.length; i++) {
            for(testLexicon stl : sourcelexicon){
                for(testLexicon ttl : targetlexicon){
                    double similarity = matcher_list[i].getSimValue(stl.getname(),ttl.getname());
                    if(similarity > max_sim)
                        max_sim = similarity;
                }
            }
            values[i] = max_sim;
        }
    }
    public double[] getsimilarity(){
        return values;
    }
    public HashSet<testLexicon> getsource(){
        return this.sourcelexicon;
    }
    public HashSet<testLexicon> gettarget(){
        return this.targetlexicon;
    }
}
