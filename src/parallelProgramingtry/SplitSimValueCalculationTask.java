/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelProgramingtry;

import com.objectspace.jgl.OrderedMap;
import com.objectspace.jgl.OrderedMapIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.util.CompareKey;

/**
 * <p>
 * Split the similarity value calculation task and run them in parallel.
 * </p>
 * 
 * @author Rajaram
 * @version 1.0
 */
public class SplitSimValueCalculationTask {
    
    /**
     * Acts as a temporary database to store the concepts of the ontology.
     */
    private OrderedMap ontology1Content, ontology2Content;
    /**
     * To manage the ontology contents.
     */
    private OntManager ontManager;    
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
    
                
    public SplitSimValueCalculationTask(OntManager uOntManager,int uMatcher, 
            Matcher[] uMatcher_list, double[] uWeight) {
        
        ontManager = uOntManager;  
        matcher = uMatcher;
        matcherList = uMatcher_list;
        weight = uWeight;
        ontology1Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_1).getClasses();
        ontology2Content = ontManager.getMOnt(
                    Constants.ONTOLOGY_2).getClasses();
    }    
    
    /**
     * Create new thread for each calculation.
     * 
     * @param noOfThreads  Number of threads the user want to create. 
     */
    public void runTask(int noOfThreads) {
        
        List<Thread> threads = new ArrayList<Thread>();
        int onto2size = (ontManager.getMOnt(Constants.ONTOLOGY_2).
                getClasses()).size();
        int concept2PerThread = onto2size/noOfThreads;
        
        int concept2 = 0;
        int threadsCount = 0;
        OrderedMap ontologySeg2 = null;        
        
        for (OrderedMapIterator i = ontology2Content.begin(); 
                !i.equals( ontology2Content.end() ); i.advance()) {
                concept2++;
                onto2size--;
                if (concept2 == 1) {
                    ontologySeg2 = new OrderedMap(new CompareKey());
                    threadsCount++;
                }
                
                ontologySeg2.add(i.key(), i.value());
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
                    Runnable task = new SimValueConstructorParallel(
                            ontology1Content, ontologySeg2, matcher, 
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
