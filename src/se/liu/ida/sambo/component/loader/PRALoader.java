/*
 * PRALoader.java
 *
 * Created on 2008
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package se.liu.ida.sambo.component.loader;

import java.io.*;
import org.w3c.dom.*;

import java.util.*;

import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @author Qiang Liu
 */
public class PRALoader {

    private static Logger logger = Logger.getLogger(PRALoader.class.getName());


    /**
     * Import the suggestions from PRA file using Vector,
     * which ignore conflicting suggestions
     *
     * @param onto1 the first MOntology
     * @param onto2 the second MOntology
     * @param praFile the reference file
     * @param userSim the user specified similarity value
     * @return the SuggestionKeeper
     */
    public static Vector<Pair> importPRAtoVector(MOntology onto1, MOntology onto2,
            String praFile) {
        //logger.info("Import PRA suggestions from : " + praFile);
        PRAContainer praInfor = readPRAFile(praFile);
        Vector<Pair> praSugs = praInfor.getPRASugs(onto1, onto2);
        //logger.info("Number of imported PRA suggestions : " + praSugs.size());
        return praSugs;
    }

    private static PRAContainer readPRAFile(String referFile) {
        PRAContainer praInfor = new PRAContainer();
        File f = new File(referFile);
        if (!f.exists() || !f.isFile()) {
            logger.severe(" There is no reference file at " + referFile);
            return null;
        }
        String strURI1 = null, strURI2 = null, strValue = null, relation = null;
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc = dombuilder.parse(new FileInputStream(referFile));
            Element root = doc.getDocumentElement();
            NodeList rdfNodes = root.getChildNodes();
            // retrieve <Suggestion> in the file
            if (rdfNodes != null) {
                for (int i = 0; i < rdfNodes.getLength(); i++) {
                    if (rdfNodes.item(i).getNodeName().equals("Alignment")) {
                        // retrieve the content under <Suggestion>
                        Node align = rdfNodes.item(i);
                        NodeList mapNodes = align.getChildNodes();
                        if (mapNodes != null) {
                            for (int j = 0; j < mapNodes.getLength(); j++) {
                                if (mapNodes.item(j).getNodeName().equals("onto1")) {
                                    praInfor.setOntoUri1(mapNodes.item(j).getTextContent());
                                }
                                if (mapNodes.item(j).getNodeName().equals("onto2")) {
                                    praInfor.setOntoUri2(mapNodes.item(j).getTextContent());
                                }
                                if (mapNodes.item(j).getNodeName().equals("map")) {
                                    // retrieve the content of <map> under <Suggestion>
                                    Node map = mapNodes.item(j);
                                    NodeList cellNodes = map.getChildNodes();
                                    if (cellNodes != null) {
                                        for (int k = 0; k < cellNodes.getLength(); k++) {
                                            if (cellNodes.item(k).getNodeName().equals("Cell")) {
                                                // retrieve the content of <cell> under <map> under <Suggestion>
                                                Node cell = cellNodes.item(k);
                                                for (Node alignment = cell.getFirstChild(); alignment != null; alignment = alignment.getNextSibling()) {
                                                    if (alignment.getNodeName().equals("entity1")) {
                                                        strURI1 = alignment.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                                                    }
                                                    if (alignment.getNodeName().equals("entity2")) {
                                                        strURI2 = alignment.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                                                    }
                                                    if (alignment.getNodeName().equals("measure")) {
                                                        strValue = alignment.getTextContent();
                                                    }
                                                    if (alignment.getNodeName().equals("relation")) {
                                                        relation = alignment.getTextContent();
                                                    }
                                                }
                                                praInfor.addPRA(strURI1, strURI2, strValue, relation);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return praInfor;
    }
}

// a simple class to transfer alignment from PRA file to program
class PRAContainer {

    String ontoUri1;
    String ontoUri2;
    Vector<String> uriList1;
    Vector<String> uriList2;
    Vector<String> simList;
    Vector<String> relList;

    protected PRAContainer() {
        uriList1 = new Vector<String>();
        uriList2 = new Vector<String>();
        simList = new Vector<String>();
        relList = new Vector<String>();
    }

    protected void addPRA(String uri1, String uri2, String sim, String relation) {
        uriList1.add(uri1);
        uriList2.add(uri2);
        simList.add(sim);
        relList.add(relation);
    }

    protected Vector<Pair> getPRASugs(MOntology onto1, MOntology onto2) {
        // check if the uri of PRA and ontologies match
        boolean ifreverse;
        String onturi1 = onto1.getURI();
        String onturi2 = onto2.getURI();
        if (onturi1.equalsIgnoreCase(ontoUri1) && onturi2.equalsIgnoreCase(ontoUri2)) {
            ifreverse = false;
        } else if (onturi1.equalsIgnoreCase(ontoUri2) && onturi2.equalsIgnoreCase(ontoUri1)) {
            ifreverse = true;
        } else {
            String msg = "The PRA is not for these two ontologies ! \n  " +
                    "PRA ontoURI1 : " + ontoUri1 + "\n" +
                    "PRA ontoURI2 : " + ontoUri2 + "\n" +
                    "Ontology1 uri : " + onturi1 + "\n" +
                    "Ontology2 uri : " + onturi2 + "\n";
            Logger.getLogger(PRALoader.class.getName()).severe(msg);
            return null;
        }
        // build the result
        Vector<Pair> result = new Vector<Pair>();
        for (int i = 0; i < this.getSize(); i++) {
            MClass me1, me2;
            String uri1 = this.uriList1.elementAt(i);
            String uri2 = this.uriList2.elementAt(i);
            String lname1 = uri1.substring(uri1.indexOf("#")+1);
            String lname2 = uri2.substring(uri2.indexOf("#")+1);
            if (ifreverse){
                me1 = onto1.getClass(lname2);
                me2 = onto2.getClass(lname1);
            }else{
                me1 = onto1.getClass(lname1);
                me2 = onto2.getClass(lname2);
            }
            result.add(new Pair(me1, me2));
        }
        return result;
    }

    protected int getSize() {
        return simList.size();
    }

    /**
     * @return the ontoUri1
     */
    protected String getOntoUri1() {
        return ontoUri1;
    }

    /**
     * @param ontoUri1 the ontoUri1 to set
     */
    protected void setOntoUri1(String ontoUri1) {
        this.ontoUri1 = ontoUri1;
    }

    /**
     * @return the ontoUri2
     */
    protected String getOntoUri2() {
        return ontoUri2;
    }

    /**
     * @param ontoUri2 the ontoUri2 to set
     */
    protected void setOntoUri2(String ontoUri2) {
        this.ontoUri2 = ontoUri2;
    }
}