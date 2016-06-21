/*
 * BayesLearning.java
 *
 */

package se.liu.ida.sambo.algos.matching.algos;

import java.util.Vector;
import java.util.Enumeration;

import com.objectspace.jgl.HashMap;

import se.liu.ida.sambo.MModel.MElement;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.text.classifier.*;
import se.liu.ida.sambo.text.classifier.util.DocumentIterator;
import se.liu.ida.sambo.text.classifier.util.FileDocument;

/**
 *
 * @author  He Tan
 * @version
 */
public class BayesLearning  extends Matcher{
    
    PredictValuesConstructor predictValues1, predictValues2;
    HashMap cateList1, cateList2;
    
    /** Creates new BayesLearning
     *
     *@param the dir in which the documents related to the ontology-1 are stored
     *@param the dir in which the documents related to the ontology-2 are stored
     */
    public BayesLearning(String dir1, String dir2){
        
        try{
            System.out.println("Loading instances from " + dir1 + " ...");
            DirectoryInstancesConstructor dic1 = new DirectoryInstancesConstructor(dir1);
            System.out.println("Loading instances from " + dir2 + " ...");
            DirectoryInstancesConstructor dic2 = new DirectoryInstancesConstructor(dir2);
            boolean debug = false;
            
            //OBS: this predictValueConstructor is for onto-2
            //now onto-1 is training ontology, onto-2 is the testing ontology whose
            //testing result will be set to this predictValueConstructor.
            predictValues2 = test(dic1, dic2, debug);
            predictValues1 = test(dic2, dic1, debug);
            
            cateList1 = reverse(dic1.getCategories());
            cateList2 = reverse(dic2.getCategories());
            
        }catch(OutOfMemoryError e){
            throw (RuntimeException) e.fillInStackTrace();
        }
    }
    
    public HashMap getCategories1(){
        return cateList1;
    }
    
    public HashMap getCategories2(){
        return cateList2;
    } 
        
    
    private PredictValuesConstructor test(DirectoryInstancesConstructor trainDic, DirectoryInstancesConstructor testDic, boolean debug){
        
        System.out.println("Initializing Naive Bayes classifier ...");
        NaiveBayes BC = new NaiveBayes(trainDic.getCategories(), debug);
        System.out.println("Training Naive Bayes classifier ...");
        BC.train(trainDic.getInstances());
        
        System.out.println("Testing new instances...");
        //for each concept in ontology-2, a predictValue is created.
        PredictValuesConstructor pvc = new PredictValuesConstructor(testDic.getCategories().length, trainDic.getCategories().length);
        for(int i = testDic.getCategories().length-1; i>=0; i--){
            //get all instances describing this concept of ontology-2
            DocumentIterator docIter = testDic.getInstances(testDic.getCategories()[i]);
            while(docIter.hasMoreDocuments()) { //read in all documents
                FileDocument doc = docIter.nextDocument();
                //this instance is predicted to a concept from ontology-1
                //!!!! OBS: test1 or test2 
                pvc.increment(i, BC.test1(new Instance(doc.featureVector(), doc.file.getName(), doc))); 
            }
        }
        
        System.out.println("finish this test ..." );
  
        return pvc;
    }
    
    
    @Override
    public double getSimValue(String input1, String input2){
        
        try{
        int catList1=((Integer)cateList1.get(input1)).intValue();
        int catList2=((Integer)cateList2.get(input2)).intValue();
        
       
         return PredictValuesConstructor.getSimValue(predictValues1, predictValues2,catList1 ,catList2 );
        }
        catch(Exception e)
        {
            return 0.0;
        }
        }
    
    private HashMap reverse(String[] categories){
        
        HashMap set = new HashMap();
        for(int i = categories.length -1; i >= 0; i--)
            set.add(categories[i], new Integer(i));
        return set;
    }
    
}
