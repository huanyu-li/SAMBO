/*
 * OBO2OWL.java
 *
 * Created on den 9 maj 2006, 10:08
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package se.liu.ida.sambo.util;

/**
 *
 * @author  He Tan
 * @version
 */


import java.io.*;
import java.util.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

public class OBOParser {
    
    
    //OBO 1.0 Format Syntax
    //http://www.geneontology.org/GO.format.obo-1_0.shtml
    static final String term = "[Term]";
    static final String type = "[Typedef]";
    static final String id = "id";
    static final String name = "name";
    
    static final String syn = "synonym";
    static final String syn1 = "related_synonym";
    static final String syn2 = "exact_synonym";
    static final String syn3 = "broad_synonym";
    static final String syn4 = "narrow_synonym";
    
    static final String def = "def";
    
    static final String isa = "is_a";
    
    static final String partof ="relationship: part_of";
    
    
    static final String lang = "en";
    static final String sn_lang = "sn";
    
    static String getTagValue(String line, char token){
        
        return line.substring(line.indexOf(token) + 1).trim();
    }
    
    static String getInference(String str){
        
        return (new StringTokenizer(str, "!")).nextToken();
    }
    
    static String getQuataValue(String str){
        
        return str.substring(str.indexOf('"')+1, str.lastIndexOf('"'));
    }
    
    
    
    //create the ontology from the document
    static void read(BufferedReader reader, OntModel model, String ns){
        
        //add part-of property
        OntProperty partPro = model.createObjectProperty(ns + "part_of");
        partPro.addLabel("Part of", lang);
        
        //read file
        try{
            String line;
            while((line=reader.readLine())!= null){
                
                //process one term
                if(line.startsWith(term)){
                    
                    OntClass clazz = null;
                    
                    while((line=reader.readLine()).length() != 0){
                        
                        if(line.startsWith(id))
                           // clazz = model.createClass(ns + getTagValue(line, ':').replace(':', '_'));
                            clazz = model.createClass(ns + getTagValue(line, ':').replace(':', '_'));
                        
                        else if(line.startsWith(name))
                            clazz.addLabel(getTagValue(line, ':'), lang);
                        
                        else if(line.startsWith(syn) || line.startsWith(syn1) || line.startsWith(syn2)
                        || line.startsWith(syn3) || line.startsWith(syn4))
                            clazz.addLabel(getTagValue(line, ':'), sn_lang);
                        
                        else if(line.startsWith(def))
                            clazz.addComment(getTagValue(line, ':'), lang);
                        
                        else if(line.startsWith(isa))
                            clazz.addSuperClass(model.createClass(ns + getTagValue(line, ':').replace(':', '_')));
                        
                        else if(line.startsWith(partof))
                            clazz.addSuperClass(model.createSomeValuesFromRestriction(null, partPro,
                                    model.createClass(ns + line.substring(partof.length()).trim().replace(':', '_'))));
                        
                    }//end while
                    
                }//end if
            }
            
            reader.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }        
        
    }             
    
   
    
    
    static void writeOWLFile(OntModel model, String file){
        
        RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
        writer.setProperty("showXmlDeclaration","true");
        writer.setProperty("tab","8");
        writer.setProperty("xmlbase", base);
        
        try{
            writer.write(model, new FileOutputStream(file), null);
            
        }catch(java.io.FileNotFoundException e){
            e.printStackTrace();
        }
        
    }
    
    static OntModel readOBOFile(String file){
        
        
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null );
        model.createOntology(ns);
        model.setNsPrefix("", ns);
        
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            OBOParser.read(reader, model, ns);
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return model;
    }
    
    
  //  static final String ns = "http://www.informatics.jax.org/adult_mouse_anatomy_060906.gxd#";
  //  static final String base = "http://www.informatics.jax.org/adult_mouse_anatomy_060906.gxd";
    static final String ns = "http://www.nlm.nih.gov/mesh_ABDEGH.go#";
    static final String base = "http://www.nlm.nih.gov/mesh_ABDEGH.go";
    
    public static void main(String args[]) {
        
        //!!!!! remove " and []  in the obo file before this!!!!!
        
       /* String file = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/adult_mouse_anatomy.obo";
        String owlfile = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/adult_mouse_anatomy.owl";
        
        String file = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/mesh.obo";
          String owlfile = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/mesh.owl";
         */
        
        String file = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/examples/eye_MA.obo";
        String owlfile = "C:/Documents and Settings/He Tan/My Documents/Project/ontologies/eye_MA.owl";
        
        OntModel model = readOBOFile(file);
        writeOWLFile(model, owlfile);        
        
        System.out.println("Done!!!");
        
        
    }
    
    
    
    
}


