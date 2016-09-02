/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import se.liu.ida.sambo.MModel.testLexicon;
import se.liu.ida.sambo.algos.matching.Matcher;

/**
 *
 * @author huali50
 */
public class ComputeTask extends RecursiveAction  {
    private static final int Threshold = 1000;
    private int start;
    private int end;
    private HashSet<testLexicon> sourcelexicon;
    private HashSet<testLexicon> targetlexicon;
    private HashMap<Integer, Task> tasklist;
    private Matcher matcher;
    public ComputeTask(int start, int end,Matcher matcher){
        this.start = start;
        this.end = end;
        this.sourcelexicon = sourcelexicon;
        this.targetlexicon = targetlexicon;
        this.matcher = matcher;
    }
    public void settasklist(HashMap<Integer, Task> tasklist){
        this.tasklist = tasklist;
    }
    protected void compute(){
        boolean cancompute = (end-start)<=Threshold;
        if(cancompute){
            for(int i =start;i<end;i++)
            {
                tasklist.get(i).compute_sim(matcher);
                
            }
        }
        else{
            int middle = (start+end)/2;
            //System.out.println("Fork " + middle);
            ComputeTask leftTask = new ComputeTask(start,middle,matcher);
            leftTask.settasklist(tasklist);
            ComputeTask rightTask = new ComputeTask(middle+1,end,matcher);
            rightTask.settasklist(tasklist);
            leftTask.fork();
            rightTask.fork();
            
        }   

    }
}
