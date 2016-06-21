/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * Parses specific XML files in which elements occur only one time.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public class XmlFileParser {

    private Document document=null;

    /**
     *<p>
     * This constructor configures the XML parser.
     *</p>
     *
     * @param filePath      Path of XML file.
     *
     */
     public XmlFileParser(final String filePath) {

         try {
             File xmlFile = new File(filePath);
             DocumentBuilderFactory dbFactory =
                     DocumentBuilderFactory.newInstance();
             DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
             document = dBuilder.parse(xmlFile);
             document.getDocumentElement().normalize();
            } catch (Exception e) {
                e.printStackTrace();
            }
     }

     /**
      * <p>
      * This method gets the value of a particular XML element.
      * </p>
      *
      * @param element      The XML element.
      *
      * @return         Value of the element (String), empty string
      *                 is returned if the element is empty.
      */
     public final String getValueOf(final String element) {

        String elementValue = "";
        NodeList nList = document.getElementsByTagName(
                document.getDocumentElement().getNodeName());
        Node nNode = nList.item(0);
        Element eElement = (Element) nNode;

        NodeList cList = eElement.getElementsByTagName(element).
                item(0).getChildNodes();
         Node nValue = (Node) cList.item(0);
        elementValue = nValue.getNodeValue();

        return elementValue;
     }
}
