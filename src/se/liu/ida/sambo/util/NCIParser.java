/*
 * NCIParser.java
 *
 * Created on den 15 januari 2007, 12:38
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package se.liu.ida.sambo.util;

import java.io.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.ResourceUtils;

import org.w3c.dom.*;

/**
 *
 * @author hetan
 */
public class NCIParser {
    
    static final String lang = "en";
    static final String sn_lang = "sn";
    
    /** Creates a new instance of NCIParser */
    public NCIParser() {
    }
    
    static void parse(String file, String out){
        
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null );
        
        String ns = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
        String base = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl";
        model.createOntology(ns);
        model.setNsPrefix("", ns);
        
        OntProperty partPro = model.createObjectProperty(ns + "part_of");
        
        Document doc = XMLFileHandler.readXMLFile(file);
        
        NodeList classList = doc.getElementsByTagName("owl:Class");
        
        int len = classList.getLength();
        for(int i=0; i<len; i++){
            
            Node clazz = classList.item(i);
            
            if(clazz.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            //rdf:about or rdf:resource for referred class
            //if(clazz.getAttributes().getNamedItem("rdf:ID") == null)
            //    continue;
            
           // OntClass jenaClass =  model.createClass(ns + clazz.getAttributes().getNamedItem("rdf:ID").getTextContent());
            OntClass jenaClass = model.createClass(ns + getClassName(clazz));
            jenaClass.addLabel(getClassName(clazz), lang);
            
            NodeList list = clazz.getChildNodes();
            
            int listLen = list.getLength();
            for(int j=0; j<listLen; j++){
                
                Node node = list.item(j);
                
                if(node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                
                if(node.getNodeName() == "Synonym")
                    jenaClass.addLabel(node.getFirstChild().getTextContent(), sn_lang);
                
                if(node.getNodeName() == "DEFINITION")
                    jenaClass.addComment(node.getFirstChild().getTextContent(), lang);
                
                if(node.getNodeName() == "rdfs:subClassOf"){
                    
                    if(node.hasAttributes()){
                        jenaClass.addSuperClass(model.createClass(ns + getClassName(node)));
                        continue;
                    }
                    
                    Node superClass = node.getFirstChild();
                    while(superClass.getNodeType() != Node.ELEMENT_NODE){
                        superClass = superClass.getNextSibling();
                    };
                    
                    if(superClass.hasAttributes())
                        jenaClass.addSuperClass(model.createClass(ns + getClassName(superClass)));
                    //part-of
                    else{
                        
                        Node part = superClass.getFirstChild();
                        while(part.getNodeType()!= Node.ELEMENT_NODE || part.getNodeName() == "owl:onProperty"){
                            part = part.getNextSibling();
                        };
                        System.out.println(part.getNodeName());
                        if(part.hasAttributes()){
                            jenaClass.addSuperClass(model.createSomeValuesFromRestriction(null, partPro,
                                    model.createClass(ns + getClassName(part))));
                            continue;
                        }
                        
                        Node partClass = part.getFirstChild();
                        while(partClass.getNodeType() != Node.ELEMENT_NODE){
                            partClass = partClass.getNextSibling();
                        };
                        
                        jenaClass.addSuperClass(model.createSomeValuesFromRestriction(null, partPro,
                                    model.createClass(ns + getClassName(partClass))));
                        
                    }
                    
                }
            }
        }
        
        
        RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
        writer.setProperty("showXmlDeclaration","true");
        writer.setProperty("tab","8");
        writer.setProperty("xmlbase", base);
        
        try{
            writer.write(model, new FileOutputStream(out), null);
            
        }catch(java.io.FileNotFoundException e){
            e.printStackTrace();
        }
        
    }
    
    
    
    static String getClassName(Node node){
        
        if(node.getAttributes().getNamedItem("rdf:ID") != null)
            return  node.getAttributes().getNamedItem("rdf:ID").getTextContent();
        
        else if(node.getAttributes().getNamedItem("rdf:about") != null)
            return  node.getAttributes().getNamedItem("rdf:about").getTextContent().substring(1);
        
        else if(node.getAttributes().getNamedItem("rdf:resource") != null)
            return  node.getAttributes().getNamedItem("rdf:resource").getTextContent().substring(1);
        
        return null;
    }    
    
        
    public static void main(String args[]) {
        
        
        parse("Z:/sambo/data/06VT/ontologies/NCI04-11-17-C/NCI-anatomy-verysimple.owl",
               "Z:/sambo/data/06VT/ontologies/NCI-anatomy.owl" );
    }
    
}
