/*
 * UnOrderVector.java
 *
 *
 */

package se.liu.ida.sambo.util;

import java.util.Vector;
import java.util.Enumeration;

/** This class overrides the method equals() in Vector.  
 *
 *Two Lists are defined to be equal if they contain the same elements.
 * But the elements can be in different order. 
 *
 * @author hetan
 */
public class UnOrderVector extends Vector{
    
   public boolean equals(Object obj){
       
        if (!(obj instanceof Vector)) 
            return false;
        
        Vector vector = (Vector)obj;
        
        if(this.size() != vector.size())
            return false;
        
        for(Enumeration e = vector.elements(); e.hasMoreElements();)            
            if(!this.contains(e.nextElement())) 
                return false;        
        
        return true;
   }
    
}
