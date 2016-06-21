package se.liu.ida.sambo.text.classifier;

import java.io.*;
import java.util.*;

import se.liu.ida.sambo.text.classifier.util.*;

/**
 * Creates a list of instances from a directory where 
 * each subdirectory contains a category of files
 *
 * @author   Ray Mooney , and modified by He Tan
 * 
 */


public class DirectoryInstancesConstructor extends InstancesConstructor
{
    /** Name of the directory where the instance files are stored. */
    protected String dirName;

    /** Type of document (text or HTML) */
    protected short docType;

    /** Flag set to stem words to their root forms */
    protected boolean stem;
    
    /** Array of categories (classes) in the data */
    protected String[] categories;
    
    //int NUM = 20;

    /** Construct an InstancesConstructor for the given directory and category labels */
  /*  public DirectoryInstancesConstructor(String dirName, String[] categories, short docType, boolean stem) {
	this.categories = categories;
	this.dirName = dirName;
	this.docType = docType;
	this.stem = stem;
    }*/
    
    /** Construct an InstancesConstructor for the given directory*/
    public DirectoryInstancesConstructor(String dirName, short docType, boolean stem){
        
        File[] subDirs = (new File(dirName)).listFiles();
        categories = new String[subDirs.length];
        for(int i= subDirs.length-1; i>=0; i--)
            if(subDirs[i].isDirectory()&&!subDirs[i].isHidden())
                this.categories[i] = subDirs[i].getName();
        
        this.dirName = dirName;
        this.docType = docType;
	this.stem = stem;
    }

    /** Construct an InstancesConstructor for the given directory and category labels */
 /*   public DirectoryInstancesConstructor(String dirName, String[] categories) {
	this(dirName, categories, DocumentIterator.TYPE_HTML, false);
    }*/

    /** Construct an InstancesConstructor for the given directory */
    public DirectoryInstancesConstructor(String dirName) {
        this(dirName, DocumentIterator.TYPE_TEXT, true);
    }
  
  /** Get the instances from the directory, process them into HashMapVector's and
     * label them with the correct category label */
    public List getInstances() {
	ArrayList instances = new ArrayList();
	/*DocumentIterator docIter = new DocumentIterator(new File(dirName), docType, stem);
	while(docIter.hasMoreDocuments()) { //read in all documents
	    FileDocument doc = docIter.nextDocument();
	    int category = findClassID(doc.file.getName()); // find category of document
	    Instance instance = new Instance (doc.featureVector(), category, doc.file.getName(), doc);
	    instances.add(instance);
	}*/
       // int n;
        for(int i = categories.length -1; i>=0; i--){// find category of document
          //  n=0;
            DocumentIterator docIter = new DocumentIterator(new File(dirName + File.separator + categories[i]), docType, stem);
           // while(docIter.hasMoreDocuments() && n <= NUM) { //read in all documents
            while(docIter.hasMoreDocuments()) { //read in all documents
                FileDocument doc = docIter.nextDocument();
                Instance instance = new Instance (doc.featureVector(), i, doc.file.getName(), doc);
                instances.add(instance);
               // n++;
            }
	}
            
	return instances;
    }
    
    /** Get the documents descibing the specified target
     *   this method is used when testing
     *
     *@param target
     */
    public DocumentIterator getInstances(String target){
        
        return new DocumentIterator(new File(dirName + File.separator + target), docType, stem);
    }

    /** Finds the class ID from the name of the document file.
     * Assumes file name contains the category name as a substring */
 /*   public int findClassID (String name) {
	for (int i=0; i < categories.length; i++) {
	    if (name.indexOf(categories[i]) != -1)
		return i;
	}
        return -1;
    }*/
    
    /** Get the categories from the directory*/
    public String[] getCategories(){
        return categories;
    }

    
    public String getDirName(){
        
        return this.dirName;
    }
    /** Test loading a sample directory of instances */
   /* public static void main(String[] args) {
	/*String dirName =  "/u/mooney/ir-code/corpora/yahoo-science/";
	String[] classes = {"bio","chem","phys"};
        DirectoryInstancesConstructor con = new DirectoryInstancesConstructor(dirName, classes);*/
        
     //   String dirName = "/home/hetan/sambo/text/nose/MA";    
     //   DirectoryInstancesConstructor con = new DirectoryInstancesConstructor(dirName);
        
	/*List instances = con.getInstances();
	System.out.println("Number Instances: " + instances.size() + "\n" + instances);*/
      //  String[] categories = con.getCategories();
       // for(int i = categories.length -1; i>=0; i--)
       //     System.out.println(i + ": " + categories[i]);
   // }

}
