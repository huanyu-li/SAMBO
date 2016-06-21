package se.liu.ida.sambo.util;


/**
 * The class is designed to encapsulate the info concerning a merging action.
 */  

public class History {
    
    // the new name chosen by the user to be used in the new ontology
    private String name;
    
      
    // of which class the name will be changed.
    private int num;
    
    // the actual node pair
    private Pair pair;
    
    // the action performed to the pair of elements, merge or create relation.
    // the defualt value is 0, no action.
    private int action;
    
    //Constant.UNIQUE = -1
    private int warning  = -1;
    
    // the comment of this action
    private String comment = "Alignment Comment.";    
    
       
    
    /**
     * Construct a history in class step 
     * for those pair to do nothing
     */
    public History(Pair p) {
        pair = p;
    }
    
   /**
     * Constructor 
     */
    public History(Pair p, String name, int num, int action) {
        pair = p;
        this.name = name;
        this.num = num;
        this.action = action;
    }
    
    
    /**
     * Constructor 
     */
    public History(Pair p, String name, int num, int action, String comment) {
        pair = p;
        this.name = name;
        this.num = num;
        this.action = action;
        this.comment = comment;
    }
    
    
   /**
     * set the action
     *@param a the action
     */
    public void setAction(int a) {
	action = a;
    }
    
    /**
     * set the node pair
     *@param p the pair
     */
    public void setPair(Pair p) {
	pair = p;
    }
	
    /**
     * set the name to be used in the new ontology
     * @param n the name
     */
    public void setName(String n) {
	name = n;
    }
        
    /**
     * set of which node to be the superclass
     *  if create is-a relation between the pair of node
     *
     * or set of which node to have a new name,
     *  if is copied to the new ontology
     *
     * @param n the number
     */
    public void setNum(int n) {
	num = n;
    }
    
    
    /**
     * set the comment
     *
     * @param c the comment
     */
    public void setComment(String c) {
	comment = c;
    }
 
    
    /**
     * set the comment
     *
     * @param c the comment
     */
    public int setWarning(int w) {
	warning = w;
        return w;
    }   
    
    /**
     * extract the action
     * @return the action
     */
    public int getAction() {
	return action;
    }
    
    /**
     * extract the node pair from the object
     * @return the pair of nodes which have been suggested by
     * Ontology Merge 
     */
    public Pair getPair() {
	return pair;
    }
	
    /**
     * extract the name to be used in the new ontology
     * @return the name to be used for the node in the new ontology
     */
    public String getName() {
	return name;
    }
    
        
    /**
     * extract of which node the name be changed
     * @return the number of the node
     */
    public int getNum() {
	return num;
    }
    
    /**
     * get the comment
     * @return the comment
     */
    public String getComment() {
	return comment;
    }
    
    public int getWarning(){        
        return warning;
    }
    
    /**
     * print the object
     * @return the object
     */
    public String toString(){
        
        return "[" + pair.toString() + ", " + action + "]"; 
    }
    

}
