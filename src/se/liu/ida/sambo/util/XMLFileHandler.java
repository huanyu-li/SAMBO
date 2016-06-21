/*
 * XMLFileHandler.java
 *
 */

package se.liu.ida.sambo.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import org.w3c.dom.*;


/**
 *
 * @author hetan
 */
public class XMLFileHandler {
    
    
    public static Document readXMLFile(String file){
        
        Document document = null;
        
        DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        //factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse( new File(file) );
            
        } catch (SAXException sxe) {
            
            sxe.printStackTrace();
            
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
        }        
        
        return document;
    }
    
    
    public static void writeXMLFile(Document doc, String filename){
        
       try {
    
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            
            String filePath = System.getProperty("user.dir").concat(
                    "/test.xml");

            xformer.transform(new DOMSource(doc), new StreamResult(new File(filePath).toURI().getPath()));
            xformer.transform(new DOMSource(doc), new StreamResult(new File(filename).toURI().getPath()));
            System.out.println(filename);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }   
   
}
