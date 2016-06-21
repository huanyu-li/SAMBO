/*
 * PubMedTimer.java
 *
 * Copyright (c) 2003 Jing Ding, All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * JING DING MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. JING DING
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Created on 2003 7:21
 */

package se.liu.ida.sambo.text.query;

/**
 * Keep track of time intervals between two consective PubMed inqueries.
 * After a successful query, don't forget setLastQueryTime.
 * @author  Jing Ding
 */
public class PubMedTimer {
  private long interval = 3000;   // Min interval between consective queries (ms).
  private long lastQueryTime = 0;    // last query time.
  private boolean verbose = false;
  private static PubMedTimer pmt = new PubMedTimer();  // Singleton instance.
  
  /** Constructor disabled */
  private PubMedTimer() {}
  
  /** Get an instance of PubMedTimer.
   * @return The singleton PubMedTimer
   */  
  public static PubMedTimer getInstance(){ return pmt; }
  
  /** Set min interval between 2 consective queries.
   * @param i min interval between 2 consective queries in ms.
   */  
  public void setInterval(long i){ interval = i; }
  
  /** Set the time of last successful query.
   * @param i last query time
   */  
  public void setLastQueryTime(long i){ lastQueryTime = i; }
  
  /** Set verbose mode.
   * @param v verbose mode
   */  
  public void setVerbose(boolean v){ verbose = v; }

  /** Wait for enough interval between two PubMed queries. Calling
   * method must be synchronized.
   */  
  public void waitForMyTurn(){
    while(true){   // Wait until enough interval between 2 queries
      long timeNow = System.currentTimeMillis();
      long passed = timeNow - lastQueryTime; 
      if(passed >= interval){
        if(verbose)
          System.out.println("Interval: " + passed);
        break;
      }else{
        try{
          Thread.sleep(interval - passed);
        }catch(InterruptedException ie){}
      }
    }
  }  
}