/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.util.HashSet;
import se.liu.ida.sambo.MModel.testLexicon;
import se.liu.ida.sambo.algos.matching.algos.EditDistance;

/**
 *
 * @author huali50
 */
public class testMapping  {
    private HashSet<testLexicon> sourcelexicon;
    private HashSet<testLexicon> targetlexicon;
    private double sim;
    public testMapping(HashSet<testLexicon> sourcelexicon,HashSet<testLexicon> targetlexicon){
        this.sourcelexicon = sourcelexicon;
        this.targetlexicon = targetlexicon;
    }
    public void compute_sim(){
        double max_sim = 0;
        EditDistance test = new EditDistance();
        for(testLexicon stl : sourcelexicon)
        {
            for(testLexicon ttl : targetlexicon)
            {
                if(stl.getlanguage().equals(ttl.getlanguage())){
                    double similarity = test.getSimValue(stl.getname(),ttl.getname());
                    System.out.println("source: "+stl.getname()+" target: "+ttl.getname()+" similarity = "+similarity);
                    if(similarity > max_sim)
                        max_sim = similarity;
                }
            }
        }
        this.sim = max_sim;
    }
    public double getsimilarity(){
        return sim;
    }
}
