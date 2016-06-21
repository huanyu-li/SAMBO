package se.liu.ida.sambo.text.classifier;

import java.util.*;

import se.liu.ida.sambo.text.classifier.util.*;

/**
 *  An object to hold training or test instances for categorization. 
 *  Stores the name, category and FeatureVector representation of
 *  the instance.
 *
 * @author       Sugato Basu and Prem Melville
 */

public class Instance {

  /** Name of the instance */
  protected String name;

  /** Category index of the instance */
  protected int category;

  /** Representation of the instance as a vector of (feature -> weight) mappings */
  protected FeatureVector hashVector;

  /** fileDocument object for the instance */
  protected FileDocument document;

  public Instance(FeatureVector input, int cat, String id, FileDocument doc){
    hashVector = input;
    category = cat;
    name = id;
    document = doc;
  }
  
    public Instance(FeatureVector input, String id, FileDocument doc){
    hashVector = input;
    category = -1;
    name = id;
    document = doc;
  }
  
  /** Sets the name of the instance */
  public void setName(String id){
    name = id;
  }
  
  /** Returns the name of the instance */
  public String getName(){
    return name;
  }
  
  /** Sets the category of the instance */
  public void setCategory(int cat){
    category = cat;
  }
  
  /** Returns the category of the instance */
  public int getCategory(){
    return category;
  }
  
  /** Sets the hashVector of the instance */
  public void setFeatureVector(FeatureVector v) {
    hashVector = v;
  }
  
  /** Returns the hashVector of the instance */
  public FeatureVector getFeatureVector(){
    return hashVector;
  }

  /** Sets the document of the instance */
  public void setDocument(FileDocument doc) {
    document = doc;
  }
  
  /** Returns the document of the instance */
  public FileDocument getDocument(){
    return document;
  }
  
  /** Returns the String representation of the instance object */
  public String toString(){
    String str;
    str = "Name:" + name + ", Category: "+category + "\n";
    return str;
  }
}
