/*
 * similarityScore.java
 *
 */

package se.liu.ida.sambo.algos.matching;

import se.liu.ida.sambo.MModel.MElement;

/**
 * The interface for linguistic matchers
 *
 * @author He Tan
 * @version 
 */

public abstract class Matcher {
    
    
    /**
     * Calculates the similarity (probability of match) between
     * any two strings.
     * @param input1 The first string.
     * @param input2 The second string.
     *
     * @return similariy value between these two 
     */

    public abstract double getSimValue(String input1, String input2);

     /**
     * Calculates the similarity (probability of match) between
     * any two MElements.
     * @param input1 The first MElement.
     * @param input2 The second MElement.
     *
     * @return similariy value between these two
     */
    public double getSimValue(MElement me1, MElement me2){
        return this.getSimValue(me1.getPrettyName(), me2.getPrettyName());
    };
    
}
