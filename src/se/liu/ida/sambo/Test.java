

/**
 *
 * @author hetan
 */

package se.liu.ida.sambo;

import java.net.URL;

import com.hp.hpl.jena.ontology.*;

import com.hp.hpl.jena.rdf.model.ModelFactory;


import java.util.Enumeration;
import java.net.MalformedURLException;


import se.liu.ida.sambo.MModel.*;

public class Test {
    
    /** Creates a new instance of ISWCTest */
    public Test() {
    }


    /**Loads an ontology.
     *
     *@param url the url of the ontology file
     *
     *@throws MalformedURLException
     *
     *@return the MOntology of the ontology
     */
    public static MOntology loadOntology(String url){
        
        try{
             OntModel ontModel = null;
       
             ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );        
             ontModel.read((new URL(url)).toString());
        
             return new MOntology(10, ontModel); 

        }catch(MalformedURLException e){
            throw (RuntimeException) e.fillInStackTrace();
        }
    }
    

    static void printDAG(String prefix, MClass clazz, boolean part){
        
	if(part)
	    System.out.println(prefix + " p- " + clazz.getLabel());
	else  System.out.println(prefix + clazz.getLabel());
        
        for( Enumeration e = clazz.getSubClasses().elements(); e.hasMoreElements(); )
            printDAG("  " + prefix, (MClass) e.nextElement(), false);
        
        for( Enumeration e = clazz.getParts().elements(); e.hasMoreElements(); )
            printDAG("  " + prefix, (MClass) e.nextElement(), true);
    }
    
  
 
    public static void main(String args[]) throws Exception {


        MOntology ontOne = loadOntology("file:///D:/appl/SAMBO/SAMBO-WebApp/web/ontologies/OWL/behavior_GO.owl");
        MOntology ontTwo = loadOntology("file:///D:/appl/SAMBO/SAMBO-WebApp/web/ontologies/OWL/behavior_SO.owl");
        
        System.out.println("behavior GO has " + ontOne.getClasses().size() + " concepts");
        System.out.println("behavior SO has " + ontTwo.getClasses().size() + " concepts");
        

        for(Enumeration e1 = ontOne.roots().elements(); e1.hasMoreElements();)
            printDAG("",  (MClass) e1.nextElement(), false);


	System.out.println("------------------------------------------------");

        for(Enumeration e2 = ontTwo.roots().elements(); e2.hasMoreElements();)
            printDAG("",  (MClass) e2.nextElement(), false);

         
    }
}
