/*
 * NGram2.java
 *
 */

package se.liu.ida.sambo.algos.matching.algos;

import java.util.*;
import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.algos.matching.Matcher;

/**
 *
 * @author  He Tan
 * @version
 */


public class NGram extends Matcher{
    
    //the size of N-gram
    public int ng;

    public NGram() {
        this.ng = 2;
    }
    
    public NGram(int size){
        
        this.ng = size;
    }
    
    private void increment(Hashtable table, String gr, int i){
        
        if(table.containsKey(gr)){
            ((int[]) table.get(gr))[i] ++;
        }else{
            int[] occurs = new int[2];
            occurs[i] = 1;
            occurs[1-i] = 0;
            table.put(gr, occurs);
        }
    }
    
    
    @Override
    public double getSimValue(String str1, String str2) {
        
        StringBuffer computeStringDiff = new StringBuffer("");
        StringBuffer computeStringSum = new StringBuffer("");
        
        Hashtable gramList = new Hashtable();
        int len = str1.length();
        for (int i = 0; i <= (len - ng); i++)
            increment(gramList, str1.substring(i, i+ng), 0);
        
        len = str2.length();
        for (int i = 0; i <= (len - ng); i++)
            increment(gramList, str2.substring(i, i+ng), 1);
        
        
        double diff = 0;
        double sum = 0;
        
        for(Enumeration e = gramList.elements(); e.hasMoreElements();){
            
            int[] occurs = (int[]) e.nextElement();
            
            //  sum += (double) Math.pow(occurs[0], 2) + Math.pow(occurs[1], 2);
            //  diff += Math.pow((double)(occurs[0]-occurs[1]), 2);
            
            sum += (double)(occurs[0] + occurs[1]);
            diff += (double) Math.abs(occurs[0]-occurs[1]);
        }
        
        return (sum-diff)/sum;
    }
    
    
    
    public static void main(String[] args) {
        //  for (int i = 0; i < 18; i++) {
        int i = 3;
        System.out.println("NGram Window Size: " + i);
        NGram test = new NGram(i);
        System.out.println(test.getSimValue("nose", "nose"));
           System.out.println(test.getSimValue("heart ventricle", "heart ventricle"));
           System.out.println(test.getSimValue("heart left ventricle", "heart ventricle"));
           System.out.println(test.getSimValue("heart right ventricle", "heart ventricle"));
           System.out.println(test.getSimValue("heart right ventricle", "heart left ventricle"));
            System.out.println(test.getSimValue("nasal cavity epithelium", "nasal cavity"));
        System.out.println("\n");
        // }
    }
    
}