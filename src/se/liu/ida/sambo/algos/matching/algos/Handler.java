/*
 * Handler.java
 *
 */

package se.liu.ida.sambo.algos.matching.algos;


import java.util.Vector;


/**The class handles some general problem in the matching algorithms
 *
 * @author  He Tan
 * @version 
 */
class Handler {

    /** Creates new Handler */
    Handler() {
    }
   
   
    
    /**
     * Merge sort algorithm. To sort similarityList on the similarity score of the tuples.
     *
     * @param a the similarityList
     * @param a
     */
    static void mergesort(Vector a, int low, int high)  { 
       if(low == high)  
            return; 
       int length = high-low+1; 
       int pivot = (low+high) / 2; 
       mergesort(a, low, pivot); 
       mergesort(a, pivot+1, high); 
       Vector working = new Vector(); 
       for(int i = 0; i < length; i++)  
           working.add(i, a.get(low+i)); 
       int m1 = 0;  
       int m2 = pivot-low+1; 
       for(int i = 0; i < length; i++) { 
         if(m2 <= high-low){  
             if(m1 <= pivot-low){ 
                 if(((Tuple)working.get(m1)).getScore() < 
                        ((Tuple) working.get(m2)).getScore()){                              
                            a.remove(i+low);
                            a.add((i+low), working.get(m2++));                               
                 }else{                     
                     a.remove(i+low);
                     a.add((i+low), working.get(m1++)); 
                 }    
             }else{
                 a.remove(i+low);
                 a.add((i+low), working.get(m2++));           
             }
         }else{
             a.remove(i+low);
             a.add((i+low), working.get(m1++)); 
         }
       } 
       
     
     }
    
     
     
     /** get the bigger integer
      *
      * @param s1 the first integer
      * @param s2 the second integer
      * @return the bigger one
      */     
     static int max(int s1, int s2){
         if(s1 >= s2) return s1;
         else return s2;
     }
     
     
     /** get the smaller integer
      *
      * @param s1 the first integer
      * @param s2 the second integer
      * @return the smaller one
      */   
     static int min(int s1, int s2){
         
         if(s1 <= s2) return s1;
         else return s2;
     }


}
