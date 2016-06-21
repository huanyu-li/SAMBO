/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.PRAalg;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Shahab
 */
public class Test {

    public static void main(String args[]) throws Exception {

       DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
       DocumentBuilder db =dbf.newDocumentBuilder();
       Document doc=db.parse("file:///D:/Thesis/src/backup/SAMBO-WebApp-Session/build/web/segment/RA/RemainingSuggestions.xml");
       doc.getDocumentElement().normalize();

       // Display Remaining Suggestions List

       System.out.println("Root element " + doc.getDocumentElement().getNodeName());
       NodeList nodeLst = doc.getElementsByTagName("Suggestions");
       System.out.println("Display Remaining Suggestions");


       for (int s = 0; s < nodeLst.getLength(); s++)
       {
            Node pairNode = nodeLst.item(s);
            Element pairElmnt = (Element) pairNode;
            NodeList pairElmntLst = pairElmnt.getElementsByTagName("Pair");
            Element pairNmElmnt = (Element) pairElmntLst.item(0);
            for(int p = 0; p < pairElmntLst.getLength(); p++)
            {
                if(p > 0)
                    pairNmElmnt = getNextElement(pairNmElmnt);
                NodeList pairNm = pairNmElmnt.getChildNodes();
                System.out.println("Suggestion "+ (p+1) +": "  + ((Node) pairNm.item(0)).getNodeValue());
             }
       }
    }

    public static Element getNextElement(Element el)
    {
        Node nd = el.getNextSibling();
        while (nd != null) {
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)nd;
            }
            nd = nd.getNextSibling();
        }
        return null;
    }

}
