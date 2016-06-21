/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelProgramingtry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.testOntManager;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.util.CompareKey;
/**
 *
 * @author huali50
 */

public class testSplitSimValueCalculationTask {
    
    /**
     * Acts as a temporary database to store the concepts of the ontology.
     */

    /**
     * To manage the ontology contents.
     */
    private testOntManager ontManager;    
    private Set<Integer> source_content;
    private Set<Integer> target_content;
    /**
     * Matcher name.
     */    
    private int matcher;
    /**
     * For TermBasic and TermWN matchers.
     */
    private Matcher[] matcherList;
    /**
     * Weight for matcher.
     */
    private double[] weight;
    
                
    public testSplitSimValueCalculationTask(testOntManager uOntManager,int uMatcher, 
            Matcher[] uMatcher_list, double[] uWeight) {
        
        ontManager = uOntManager;  
        matcher = uMatcher;
        matcherList = uMatcher_list;
        weight = uWeight;
        source_content = ontManager.getontology(Constants.ONTOLOGY_1).getMClasses();
        target_content = ontManager.getontology(Constants.ONTOLOGY_2).getMClasses();

    }    
    
    /**
     * Create new thread for each calculation.
     * 
     * @param noOfThreads  Number of threads the user want to create. 
     */
    public void runTask(int noOfThreads) {
        
        List<Thread> threads = new ArrayList<Thread>();
        int onto2size = target_content.size();
        int concept2PerThread = onto2size/noOfThreads;
        
        int concept2 = 0;
        int threadsCount = 0;
        //OrderedMap ontologySeg2 = null;        
        Set<Integer> ontologySeg2 =null;
        for(Integer i : target_content)
        {
            concept2++;
            onto2size--;
            if(concept2 == 1){
                ontologySeg2 =new HashSet<Integer>();
                threadsCount++;
            }
            ontologySeg2.add(i);
            /**
                 * It is impossible to divide the concepts equally for 
                 * all threads.
                 * 
                 * Eg. If the ontology2 has 10 concepts if we use 3 threads then 
                 * each thread will get 3 concepts, but the 10th concept will be 
                 * leave out, so in those case the thread which is created 
                 * last will take the 10th concept into account.
                 */
                if (concept2 == concept2PerThread && onto2size < 
                        concept2PerThread) {                    
                    concept2PerThread = concept2PerThread + onto2size;
                }
                if (concept2 == concept2PerThread) {
                    concept2 = 0;                    
                    Runnable task = new testSimValueConstructorParallel(this.ontManager,
                            source_content, ontologySeg2, matcher, 
                            matcherList, weight);                        
		    Thread worker = new Thread(task);
		    // We can set name of the thread
                    worker.setName(String.valueOf(threadsCount));
                    // Start the thread
                    worker.start();
                    System.out.println("Thread "+threadsCount+" Started");
                    // Remember the thread for later usage
                    threads.add(worker);
                }
        }
        int running = 0;
        do {
            running = 0;
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    running++;
                }
            }
            System.out.println("We have " + running + " running threads. ");
            delayCurrentThread(1000*running);                
        } while (running > 0);
    }    
    
    /**
     * Delay the thread that called this class.
     * 
     * @param delay     Delay time in milli seconds.
     */
    private void delayCurrentThread(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger(SplitSimValueCalculationTask.class.getName())
                    .log(Level.SEVERE, null, ex);
        }        
    }
}

