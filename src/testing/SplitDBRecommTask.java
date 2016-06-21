/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.Recommendation.RecommendationMethod3;
import se.liu.ida.sambo.jdbc.ResourceManager;
import testing.AccessSuggBasedRecomm;
import se.liu.ida.sambo.util.testing.GenerateAlignmentStrategies;

/**
 *
 * @author rajka62
 */
public class SplitDBRecommTask {
    
   ArrayList<String> result=new ArrayList();
   
   AccessSuggBasedRecomm storeTODB=new AccessSuggBasedRecomm();  
   
   
   RecommendationMethod3 calculateScore=new RecommendationMethod3();
   
   
   Connection Selectconn=null;
   
   Connection Updateconn=null;
   
//   int size=0;
   
   
    
                
    public SplitDBRecommTask()
    {
        
        
        GenerateAlignmentStrategies test=new GenerateAlignmentStrategies();
        result=test.getStrategies();
//        size=result.size();
        
        
        try {
            Selectconn = ResourceManager.getConnection();
            
            Updateconn = ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(SplitDBRecommTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
//        storeTODBstoreTODB.ClearDB(Selectconn);
    }
    
    
    public void runTask()
    {
               List<Thread> threads = new ArrayList<Thread>();
               
               int splitSize=690;
               
               int j=result.size()/splitSize;
               
               
               if(j<1)
                   j=1;
               
               int sugStart=0,sugEnd=splitSize;
               
               if(result.size()<splitSize)
                   sugEnd=result.size();               
                   
           
		// We will create j threads
		for (int i = 0; i < j; i++) {
                    
                    if(i==(j-1))
                        sugEnd=result.size();
                    
                    List<String> StrategySplit=result.subList(sugStart, sugEnd);
                    
			
//			s[i]=new Sum();
			Runnable task = new DBRecomm (Updateconn,Selectconn ,StrategySplit);
			Thread worker = new Thread(task);
			// We can set the name of the thread
			worker.setName(String.valueOf(i));
			// Start the thread, never call method run() direct
			worker.start();
                        
                        //delayCurrentThread(10);
                        
                        //worker.run();
                        
                        System.out.println("Thread "+i+" Started");
			// Remember the thread for later usage
			threads.add(worker);
                        sugStart=sugEnd;
                        
                        sugEnd=sugEnd+splitSize;
                        
                        if(result.size()<sugEnd)
                            sugEnd=result.size();
                        
			
			
		}
                
                int running = 0;
		do {
			running = 0;
			
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					running++;
				}
				
//				else
//				{
//					int i=Integer.parseInt(thread.getName());
//					sum=sum+s[i].getSum();
//				}
			}
			System.out.println("We have " + running + " running threads. ");
                        
                        
                        delayCurrentThread(1000*running);
                        
                        
		} while (running > 0);
           
    }
    
    
    
    private void delayCurrentThread(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(SplitDBRecommTask.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            
        
        
    }
}