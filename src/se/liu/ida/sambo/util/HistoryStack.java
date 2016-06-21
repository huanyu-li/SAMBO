package se.liu.ida.sambo.util;

import java.util.*;

/**
 * A specialized stack which keeps track of how many objects were added at
 * one time.  When the stack is "popped" all of the objects which were
 * added at the last "push" are returned.
 *
 * @author Carolyn Manis, modified by He Tan
 * @vertion 1.0
 */
public class HistoryStack extends Stack {

    private Stack depth;

    public HistoryStack() {

	depth = new Stack();
    }

   
    /**
     * The object is added, and 1 added to the depth stack.
     *
     * @param obj an object to add to the stack
     * @return true if the object was added to the stack
     */
    public boolean add(Object obj) {
	
	depth.push(new Integer(1));
	return (push(obj) != null);
    }

    /** 
     * Overrides the super-method addAll from Vector.  The objects in
     * the collection are added to the stack and the number of objects
     * added at one time is saved on the depth stack
     *
     * @param c a collection of objects to be added to the stack
     * @return true if the stack was changed
     */
    public boolean addAll(Collection c) {

	boolean changed = false;
	
	Integer size = new Integer( c.size() );
	// Add the size of collection
	depth.push(size);
	
	Iterator it = c.iterator();
	while (it.hasNext() ) {
	    changed = true;
	    push( it.next() );
	}
	
	return changed;
    }
    
     /**
     * Returns the most recent additions in an vector in the 
     * order they were added to the stack. The number of objects in the
     * vector is determined by the value on top of the depth stack.
     *
     * @return a vector of objects removed from the stack
     */
    public Vector removeMostRecent() {
        
	Vector recentVector = new Vector();
        
	if (! depth.isEmpty() ) {
	    Integer recentDepth = (Integer)depth.pop();
            
            for(int i=0; i < recentDepth.intValue(); i++){
                recentVector.add(pop());
	    }
	}
	return recentVector;
    }
    
    
    /**
     * return the last element in the stack, and update the depth stack.
     *
     * @return the last element in the stack
     */
    public Object remove(){	
        
	int recentDepth = ((Integer) depth.pop()).intValue();              
        if(recentDepth != 1)
            depth.push(new Integer(recentDepth-1));                    
        return pop();
            
    }
    

    /**
     * Add the objects to the stack, but increase the number on the depth
     * stack instead of adding a new object
     * @param additions the objects to be appended to the previous add
     * @return true if the stack was changed
     */
    public boolean updateMostRecent(Collection additions) {
	
	boolean changed = false;
        
        if(!additions.isEmpty()){
            
             changed = true;	
	   
             // Update the number of pairs belonging to the most recent addition
	     Integer recentNum = (Integer)depth.pop();
	     int updatedNum = recentNum.intValue() + additions.size();
	     depth.push(new Integer(updatedNum));
	
	    Iterator it = additions.iterator();
	    while (it.hasNext() ) {	   
	         push( it.next() );
            }
	}
	
	return changed;
	
    }
    
   
    /**
     * Add the object to the stack, but increase the number on the depth
     * stack instead of adding a new object
     * @param obj the object to be appended to the previous add
     * @return true if the stack was changed
     */
    public boolean updateMostRecent(Object obj) {
	
	boolean changed = false;
        
        if(obj != null){
            
             changed = true;	
             
             // Update the number of pairs belonging to the most recent addition
	     Integer recentNum = (Integer)depth.pop();
	     int updatedNum = recentNum.intValue() + 1;
	     depth.push(new Integer(updatedNum));
	     push(obj);            
	}
	
	return changed;
	
    }
    

    /**
     * Clears the stack of all elements
     */
    public void removeAllElements() {
	
	depth.removeAllElements();
	super.removeAllElements();
    }
    

}
