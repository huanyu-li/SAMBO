package se.liu.ida.sambo.text.classifier;

import java.util.*;
import java.io.File;

import se.liu.ida.sambo.text.classifier.util.*;

/**
 * Wrapper class to test NaiveBayes classifier using 10-fold CV.
 * Running it with -debug option gives very detailed output
 *
 * @author       Sugato Basu
 */

public class TestNaiveBayes {
    /** A driver method for testing the NaiveBayes classifier using
    * 10-fold cross validation.  
    * @param args a list of command-line arguments.  Specifying "-debug"
    * will provide detailed output
    */
    public static void main(String args[]) throws Exception
    {
        String dirName = "/home/hetan/sambo/text/nose";       
        
	System.out.println("Loading instances from " + dirName + "...");
	//List instances = new DirectoryInstancesConstructor(dirName, categories).getInstances();
        DirectoryInstancesConstructor dic = new DirectoryInstancesConstructor(dirName + File.separator + "train");
       // List instances = new DirectoryInstancesConstructor(dirName).getInstances();
	System.out.println("Initializing Naive Bayes classifier...");
	NaiveBayes BC;
	boolean debug;
	// setting debug flag gives very detailed output, suitable for debugging
	/*if (args.length==1 && args[0].equals("-debug"))
	    debug = true;
	else
	    debug = false;
	BC = new NaiveBayes(categories, debug);*/
        BC = new NaiveBayes(dic.getCategories(), true);        
        System.out.println("Training Naive Bayes classifier...");        
        BC.train(dic.getInstances());        
        System.out.println("Load new instances from " + dirName + "...");
        DocumentIterator docIter = new DocumentIterator(new File(dirName + File.separator + "test"));  
        Vector instances = new Vector();
        while(docIter.hasMoreDocuments()) { //read in all documents
            FileDocument doc = docIter.nextDocument();
            Instance instance = new Instance (doc.featureVector(), doc.file.getName(), doc);
            instances.add(instance);
        }
        System.out.println("Testing new instances...");
        for(Enumeration e = instances.elements(); e.hasMoreElements();)
            BC.test((Instance) e.nextElement());
        System.out.println("finish");
        

	// Perform 10-fold cross validation to generate learning curve
	//CVLearningCurve cvCurve = new CVLearningCurve(BC,instances);
       /* CVLearningCurve cvCurve = new CVLearningCurve(BC,dic.getInstances());
	cvCurve.run();*/
    }
}
