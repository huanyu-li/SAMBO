/*
 * Constants.java
 */

package se.liu.ida.sambo.Merger;

import java.util.Vector;
import java.util.Enumeration;
import java.io.File;

import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.testPair;

/**The Constants involved in a merging process
 *
 * @author  He Tan
 * @version 
 */
public class Constants extends se.liu.ida.sambo.MModel.util.OntConstants {

     /////////////////////////////////
    // the ontology language        //
    ///////////////////////////////// 
    /**
     * constant value indicating the DAML+OIL ontology language
     */     
    public static final int DAML = 1;     
    
    /**
     * constant value indicating the OWL language
     */     
    public static final int OWL = 2;
    
    
    /////////////////////////////////////////////
    // ontologies and information              //
    /////////////////////////////////////////////
    /**
     * constant value indicating the ontology one
     */     
    public static final int ONTOLOGY_1 = 1; 
    
    /**
     * constant value indicating the ontology two
     */     
    public static final int ONTOLOGY_2 = 2; 
    
     /**
     * constant value indicating the new ontology
     */     
    public static final int ONTOLOGY_NEW = 0;         
   
    
    /////////////////////////////////
    // the aligning actions        //
    /////////////////////////////////  
    
    /** indicate element with no action */
    public static final int NO = 0;
    
     /** indicate merging slot */
    public static final int ALIGN_SLOT = 1;    
    
    /** indicate merging class */
    public static final int ALIGN_CLASS = 2;    
    
    /** indicate is-a relation */
    public static final int IS_A_CLASS = 3;         
    
    
    /////////////////////////////////////////////
    // Constants for matching algorithm        //
    ///////////////////////////////////////////// 
    /**
     * a list of single matchers
     */
    public static final String[] singleMatchers = {
                "EditDistance",
                "NGram",
                "WL",
                "WN",
                "TermBasic",
                "TermWN",
                "UMLSKSearch",
                "Hierarchy",
                "BayesLearning"
    }; 
        

    /**
     * a matcher based on edit distance*/
     public static final int EditDistance  = 0;

     /** a matcher matcher based on n-gram */
     public static final int NGram = 1;

     /** a matcher based on WL */
     public static final int WL = 2;

     /* a matcher based on WN */
     public static final int WN = 3;

     /* a matcher based on terms*/
     public static final int Terminology  = 4;
     
     /** a matcher looking up WordNet */
     public static final int WordNet_Plus = 5;
   
     /** a matcher looking up UMLS */
     public static final int UMLS = 6;
  
     /* a matcher based is-a hierarchy */
     public static final int Hierarchy = 7;
     
     /** a matcher utilizing Bayes Learning */
     public static final int Bayes = 8;
  
      
    /** the defaultReasoner 
     */
     public static String defaultReasoner;
     
    //////////////////////////
    // the merging steps    //
    //////////////////////////   
    /** Indicates the slot merging step in the merge process  */
    public static final int STEP_SLOT = 3;

    /** Indicates the class merging step in the merge process*/
    public static final int STEP_CLASS = 4;
    
    
    //indicate the unique label 
    public static final int UNIQUE = -1;

      
    
    // Gets all pairs that contains the specified object 
    //  from a vector.
    public static Vector getHoldingPairs(Object object, Vector vector) {
        
        Vector holdingList = new Vector();
        for (Enumeration e = vector.elements(); e.hasMoreElements();) {
            Pair pair = (Pair)e.nextElement();
            if (pair.contains(object)) {
                holdingList.add(pair);
            }
        }
        return holdingList;
    }  
        public static Vector testgetHoldingPairs(testPair object, Vector vector) {
        
        Vector holdingList = new Vector();
        for (Enumeration e = vector.elements(); e.hasMoreElements();) {
            testPair pair = (testPair)e.nextElement();
            if (pair.contains(object.getSource())) {
                holdingList.add(pair);
            }
        }
        return holdingList;
    }  
    
    
    
    //Gets all pairs that contains one of the objects 
    // in the specified pair from ta vector.
    public static Vector getHoldingPairs(Pair pair, Vector vector) {
        
        Vector holdingVector = new Vector();
        Object obj1 = pair.getObject1();
        Object obj2 = pair.getObject2();        
        for (Enumeration e = vector.elements(); e.hasMoreElements();) {
            Pair p = (Pair)e.nextElement();
            //System.out.println(p);
            if (p.contains(obj1) || p.contains(obj2)) 
                holdingVector.add(p);            
        }     
        return holdingVector;
    } 
}