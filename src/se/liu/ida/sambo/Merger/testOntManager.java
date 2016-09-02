/*
 * MergeManager.java
 */

package se.liu.ida.sambo.Merger;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.testMOntology;
import se.liu.ida.sambo.MModel.MProperty;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;


/**The class manages the ontologies involved in an alignment and merging process
 *
 * ontology 1, ontology 2, and the new ontology.
 *
 * @author  huali50
 * @version
 */
public class testOntManager {
    //Source Ontology
    private testMOntology sourceontology;
    //Target Ontology
    private testMOntology targetontology;
    
    /**
     * Creates new OntManager
     */
    public testOntManager(){
        sourceontology=new testMOntology();
        targetontology=new testMOntology();
    }
    /**
     * Load ontologies based on String paths
     * @author huali50
     * @param sourcepath
     * @param targetpath
     * @throws OWLOntologyCreationException 
     */
    public void loadOntologies(String sourcepath,String targetpath) throws OWLOntologyCreationException {
        sourceontology.loadMOntology(sourcepath);
        targetontology.loadMOntology(targetpath);
    }
    /**
     * Load ontologies based on URL paths
     * @author huali50
     * @param sourcepath
     * @param targetpath
     * @throws OWLOntologyCreationException 
     */
    public void loadOntologies(URL sourcepath,URL targetpath) throws OWLOntologyCreationException {
        sourceontology.loadMOntology(sourcepath);
        targetontology.loadMOntology(targetpath);
    }
    /**
     * Load ontologies based on URI paths
     * @author huali50
     * @param sourcepath
     * @param targetpath
     * @throws OWLOntologyCreationException 
     */
    public void loadOntologies(URI sourcepath,URI targetpath) throws OWLOntologyCreationException {
        sourceontology.loadMOntology(sourcepath);
        targetontology.loadMOntology(targetpath);
    }
    /**
     * Get ontology based on integer flag
     * @param ontology
     * @return 
     */
    public testMOntology getontology(int ontology){
        if(ontology == Constants.ONTOLOGY_1){
            return this.sourceontology;
        }
        else if(ontology == Constants.ONTOLOGY_2){
            return this.targetontology;
        }
        else{
            return null;
        }
    }
}
