/*
 * ontoSearch.java
 */

package se.liu.ida.sambo.algos.matching.algos;

import com.objectspace.jgl.Array;
import java.util.Enumeration;
import java.util.Vector;

import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;

import se.liu.ida.sambo.MModel.*;

import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.Pair;



/** The class implement the matching based on the logical structure of the ontologies
 *
 * @author He Tan
 * @vertion 
 */
public class GraphSearch {   
    
    
   private MOntology ontoOne;
   private MOntology ontoTwo;
    
   /**
    * Construct a onto search.
    * @param ontoOne the MOntology-1.
    * @param ontoTwo the MOntology-2
    */    
   public GraphSearch(MOntology ontoOne, 
                      MOntology ontoTwo)
   {
       this.ontoOne = ontoOne;
       this.ontoTwo = ontoTwo;  
   }
   
   
  /**To compare the parents and children of a pairs of class, if most of them are merge,
   * we consider they are similiar or there is relation betwee them
   */    
  public Vector createPCSugVector(){
      
      Vector sugVector = new Vector();
      
      for(Enumeration e1 = ontoOne.getClasses().elements(); e1.hasMoreElements();){
           MClass c1 = (MClass) e1.nextElement();  
           //if the class has not been merged, continue.          
           if(c1.getAlignElement() != null) continue; 
           
           Array subOne = c1.getSubClasses();
           Array superOne = c1.getSuperClasses();
           
           for(Enumeration e2 = ontoTwo.getClasses().elements(); e2.hasMoreElements();){
                MClass c2 = (MClass) e2.nextElement();
                //if the class has not been merged, continue the current iteration.          
                if(c2.getAlignElement() != null) continue; 
                //if there is no is-a relation between these two class, continue the current iteration.
                if( c1.getAlignSupers().contains(c2) 
                     || c2.getAlignSupers().contains(c1)) continue;
                
                Array subTwo = c2.getSubClasses();
                Array superTwo = c2.getSuperClasses();
                
                double sameParents = 0;
                double sameChildren = 0;
                //to count the number of the same childrens             
                for(Enumeration es1 = subOne.elements(); es1.hasMoreElements();){
                    MClass subClassOne = (MClass) es1.nextElement();
                    for(Enumeration es2 = subTwo.elements(); es2.hasMoreElements();){
                        MClass subClassTwo = (MClass) es2.nextElement();
                        if((subClassOne.getAlignElement() != null) && (subClassTwo.getAlignElement() != null) 
                             && subClassOne.getAlignElement().equals(subClassTwo.getAlignElement())){
                            sameChildren = sameChildren + 1.0;
                            break;
                        }
                    }
                }
                //to count the number of the same parents       
                for(Enumeration es1 = superOne.elements(); es1.hasMoreElements();){
                    MClass superClassOne = (MClass) es1.nextElement();
                    for(Enumeration es2 = superTwo.elements(); es2.hasMoreElements();){
                        MClass superClassTwo = (MClass) es2.nextElement();
                        if((superClassOne.getAlignElement() != null) && (superClassTwo.getAlignElement() != null) 
                             && superClassOne.getAlignElement().equals(superClassTwo.getAlignElement())){
                            sameParents = sameParents + 1.0;
                            break;
                        }
                    }
                }
               
                if( (sameParents/Handler.min(superOne.size(), superTwo.size()) >= AlgoConstants.PARENTS_CHILDREN) 
                     && (sameChildren/Handler.min(subOne.size(), subTwo.size()) >= AlgoConstants.PARENTS_CHILDREN) )
                    sugVector.add(new Pair(c1, c2));
           }           
      }     
      
      return sugVector;
  }
    

   /** Creates a vector of pairs of paths that maybe contain nodes candidate for merging or creating relation.
    *
    * @param anchors The vector of pairs of classes have been accepted by user as anchors.
    * @param length  The max path length, i.e. the number of edges on the path.
    * @return A vector of pairs of the paths.
    */
    public Vector createPathSugVector(Vector anchors, int length){
        
        Vector  suggestions = new Vector();                 
        
        //get the paths between each pair of suggestion.
        for(Enumeration e = anchors.elements(); e.hasMoreElements();){
            
            Pair pair1 = (Pair)((History) e.nextElement()).getPair();
            // node from ontology-1 in the first pair
            MClass node1one = (MClass) pair1.getObject1();
            // node from ontology-2 in the second pair
            MClass node1two = (MClass) pair1.getObject2();
            
           for(Enumeration en = anchors.elements(); en.hasMoreElements();){
               
                Pair pair2 = (Pair) ((History)en.nextElement()).getPair();
               
                if(pair1.equals(pair2))
                    continue;
                  
                // node from ontology-1 in the second pair
                MClass node2one = (MClass) pair2.getObject1();
                // node from onotology-2 in the second pair
                MClass node2two = (MClass) pair2.getObject2();
                
                // paths between the nodes from ontology-1
              //  Vector path1to2one = ontoOne.findClassPaths(node1one, node2one, length);
                Vector path1to2one = findClassPaths(node1one, node2one, length);
                
                // paths between the nodes from ontology-2
              //  Vector path1to2two = ontoTwo.findClassPaths(node1two, node2two, length);
                Vector path1to2two = findClassPaths(node1two, node2two, length);
            
                if(path1to2one.isEmpty() || path1to2two.isEmpty())
                    continue;
                
                //there is no other nodes between the pair of paths
                if(((path1to2one.size()==1) && ((Vector)path1to2one.firstElement()).size() <= 2)
                    || ((path1to2two.size()==1) && ((Vector)path1to2two.firstElement()).size() <= 2))
                    continue;
                
                //remove the path, in which all nodes are already merged                
                boolean cleanode = false;
                //check the paths from ontology-1
                for(Enumeration e1 = path1to2one.elements(); e1.hasMoreElements();){
                    Vector vp1 = (Vector)e1.nextElement();
                    for(Enumeration en1 = vp1.elements(); en1.hasMoreElements();){
                        MClass c = (MClass) en1.nextElement();
                        if(c.getAlignElement() == null){                            
                            cleanode = true;
                            break;
                        }
                    }
                    if(cleanode) break;
                }
                //check the paths from ontology-2
                if(cleanode){
                    cleanode = false;
                    for(Enumeration e2 = path1to2two.elements(); e2.hasMoreElements();){
                        Vector vp2 = (Vector)e2.nextElement();
                        for(Enumeration en2 = vp2.elements(); en2.hasMoreElements();){
                            MClass c = (MClass) en2.nextElement();
                            if(c.getAlignElement() == null){
                               cleanode = true;
                               break;                            
                            }
                        }
                        if(cleanode) break;
                    }                                                       
                }                      
                
                if(!cleanode)  continue;
                
                Pair pair = new Pair(path1to2one, path1to2two);
                suggestions.add(pair);                
           }
        }                  
                    
        return suggestions;
    }
    
    
      
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // this part is moved from MOntology, to check the modified places.
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    
  /**
   * Finds all the paths between two classes.
   *
   * @param start The start class.
   * @param end The end class.
   * @param MAX_PATH_LENGTH the max length of the paths
   *         
   * @return The vector of all paths.
   * 
   */  
  public Vector findClassPaths(MClass start, MClass end, int MAX_PATH_LENGTH){
      
      Vector path = new Vector();
      //paths = new Vector();
      Vector paths = new Vector();
       
      if(!start.equals(end))
         //findClassPathsRecursion(start, end, path,MAX_PATH_LENGTH);
           findClassPathsRecursion(start, end, path, MAX_PATH_LENGTH, paths);
      
      return paths;
  }
     
 
 // private Vector findClassPathsRecursion(MClass start, MClass end, Vector path, int MAX_PATH_LENGTH){  
    private Vector findClassPathsRecursion(MClass start, MClass end, Vector path, int MAX_PATH_LENGTH, Vector paths){
        
        Vector thispath = new Vector(path);  
        thispath.add(start);
        
        if (start.equals(end))
            return thispath;  
       
        if ((thispath.size()-1) >= MAX_PATH_LENGTH)
            return thispath;      
              
        for( Enumeration it = start.getSubClasses().elements(); it.hasMoreElements(); ){
            
            MClass node = (MClass)it.nextElement(); 
            
            if (!thispath.contains(node)){               
             //   Vector newpath = findClassPathsRecursion(node, end, thispath, MAX_PATH_LENGTH);
                Vector newpath = findClassPathsRecursion(node, end, thispath, MAX_PATH_LENGTH, paths);
                if((newpath.size()>=2) && ((MClass)newpath.lastElement()).equals(end))
                    paths.add(newpath);  
            }            
        }
        
        for( Enumeration it = start.getParts().elements(); it.hasMoreElements(); ){
            
            MClass node = (MClass)it.nextElement(); 
            
            if (!thispath.contains(node)){               
               // Vector newpath = findClassPathsRecursion(node, end, thispath, MAX_PATH_LENGTH);
                Vector newpath = findClassPathsRecursion(node, end, thispath, MAX_PATH_LENGTH, paths);
                if((newpath.size()>=2) && ((MClass)newpath.lastElement()).equals(end))
                    paths.add(newpath);  
            }            
        }
        
        
        return new Vector();
  }
  
 
    

}

