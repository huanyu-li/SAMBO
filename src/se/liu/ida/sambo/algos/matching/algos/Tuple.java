/*
 * Tuple.java
 */

package se.liu.ida.sambo.algos.matching.algos;

/** The class groups two string and their similarity score
 * @author He Tan
 */
public class Tuple {
    
    private Object objectOne;
    private Object objectTwo;
    private double score;

    /** Constructs <CODE>Tuple</CODE>
     * @param w1 the word one
     * @param w2 the word two
     * @param ss the similarity score
     */
    public Tuple(Object w1, Object w2, double ss) {
        
        this.objectOne = w1;
        this.objectTwo = w2;
        this.score = ss;
    }

    /** get the first word of the tuple
     * @return the word one
     */
    public Object getObjectOne(){
        return objectOne;
    }
    
    /** get the second word of the tuple
     * @return the word two
     */
    public Object getObjectTwo(){
        return objectTwo;
    }
    
    
    /** get their similarityScore
     * @return the similarity score
     */
    public double getScore(){
        return score;
    }
    
}

