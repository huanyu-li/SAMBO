/*
 * predictValues.java
 *
 */

package se.liu.ida.sambo.text.classifier;

import java.util.Vector;
import java.util.Enumeration;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @author  He Tan
 * @version 
 */
public class PredictValuesConstructor {
    
    private int[][] values;  
    //the total number of instances desribing the concept
    private int[] total;
    
    private int categories;
        
    /** Creates new predictValues 
     *
     * @param s1 the number of the concepts in testing ontolgy
     * @param s2 the number of the concepts in training ontology
     */
    public PredictValuesConstructor(int s1, int s2) {       
        
        this.values = new int[s1][s2];        
        this.total = new int[s1];
        this.categories = s2;
        
        for(int i=0; i<s1; i++){
            total[i] = 0;
            for(int j=0; j<s2; j++)
                values[i][j] = 0;
        }
    }
    
    /* Increment the probability the predicted concept for
     * the training concept
     *
     *@param c1 indicates to which concept of testing ontology the instance belongs
     *@param c2 indicates to which concept of training ontology the instance is classified
     **/
    public void increment(int c1, int c2){
        if(c2 != -1)
            values[c1][c2] ++;
        total[c1]++;
    }
    
    public void increment(int c1, boolean[] c2){
        
        for(int i = 0; i < categories; i++)
            if(c2[i])
            values[c1][i] ++;
        
        total[c1]++;
    }
    

    //return the number of instances describing the concept
    int getInstanceNum(int c){
        return total[c];
    }
    
    
    //return the number of instances related c1 classified to c2
    int getPredictNum(int c1, int c2){
        return values[c1][c2];
    }
    
    
    int length(){
        return values.length;
    }
    
    /* return a similarity value
     */
    public static double getSimValue(PredictValuesConstructor pv1, PredictValuesConstructor pv2, int c1, int c2){
              
        return ((double)(pv1.getPredictNum(c1,c2) + pv2.getPredictNum(c2,c1)))/(pv1.getInstanceNum(c1) + pv2.getInstanceNum(c2));        
    }
    
    
    public static Vector getPairList(PredictValuesConstructor pv1, PredictValuesConstructor pv2, double threshold){
          
          Vector vector = new Vector();
           
          for(int i = pv1.length()-1; i >= 0; i--){                                  
              int numInstances1 = pv1.getInstanceNum(i);
              
              for(int j = pv2.length()-1; j >= 0; j--){                    
                  int numInstances = numInstances1 + pv2.getInstanceNum(j);
                  double result = (double)(pv1.getPredictNum(i,j) + pv2.getPredictNum(j,i))/numInstances;
                  if(result > threshold)  
                      vector.add(new Pair(new Integer(i), new Integer(j)));
              }
          }
          return vector;       
    }
    
}
