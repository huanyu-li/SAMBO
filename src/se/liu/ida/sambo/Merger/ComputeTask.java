/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.RecursiveTask;
import se.liu.ida.sambo.MModel.testLexicon;

/**
 *
 * @author huali50
 */
public class ComputeTask extends RecursiveTask<Integer>  {
    private static final int Threshold = 100;
    private int start;
    private int end;
    private HashSet<testLexicon> sourcelexicon;
    private HashSet<testLexicon> targetlexicon;
    private ArrayList<Task> tasklist;
    public ComputeTask(int start, int end){
        this.start = start;
        this.end = end;
        this.sourcelexicon = sourcelexicon;
        this.targetlexicon = targetlexicon;
    }
    public void settasklist(ArrayList<Task> tasklist){
        this.tasklist = tasklist;
    }
    protected Integer compute(){
        boolean cancompute = (end-start)<=Threshold;
        if(cancompute){
            for(int i =start;i<end;i++)
            {
                //tasklist.get(i).compute_sim();
            }
        }
        else{
            int middle = (start+end)/2;
            ComputeTask leftTask = new ComputeTask(start,middle);
            leftTask.settasklist(tasklist);
            ComputeTask rightTask = new ComputeTask(middle+1,end);
            rightTask.settasklist(tasklist);
            leftTask.fork();
            rightTask.fork();
            //int leftresult = leftTask.join();
            //int rightresult = rightTask.join();
        }   
        return 0;
    }
}
