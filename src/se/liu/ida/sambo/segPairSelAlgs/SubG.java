package se.liu.ida.sambo.segPairSelAlgs;

import java.net.URL;
import java.util.*;
import java.io.*;


import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.util.Pair;

public class SubG {

    MOntology monto1, monto2;
    int segPairNum;

    public SubG(String ontoUri1, String ontoUri2, int segPairNum) {
        this.monto1 = OntManager.loadOntology(ontoUri1, false);
        this.monto2 = OntManager.loadOntology(ontoUri2, false);
        this.segPairNum = segPairNum;
    }

    /*public static void main(String args[]) throws Exception {
    String location = System.getProperty("user.dir");
    String ontoLoc = location + "/ontology/";
    String segLoc = location + "/segment/SubG/";
    String ontoUri1 = "file:///" + ontoLoc + "NCI-anatomy.owl";
    String ontoUri2 = "file:///" + ontoLoc + "mesh-anatomy-A.owl";
    //String ontoUri1 = "file:///" + ontoLoc + "eye_MeSH.owl";
    //String ontoUri2 = "file:///" + ontoLoc + "eye_MA.owl";
    SubG sg = new SubG(ontoUri1, ontoUri2);
    sg.run(segLoc);
    }*/
    public int run(String segLoc) {
        System.out.println("Run SubG ...");

        System.out.println("Onto1 has " + monto1.getClasses().size() + " concepts");
        System.out.println("Onto2 has " + monto2.getClasses().size() + " concepts");

        ArrayList candidate = new ArrayList();

        //FileWriter writer = openOut(location + "AlignDAG-anatomy-NCI-MeSH.txt");

        for (Enumeration e1 = monto1.getClasses().elements(); e1.hasMoreElements();) {

            MClass mc1 = (MClass) e1.nextElement();

            for (Enumeration e2 = monto2.getClasses().elements(); e2.hasMoreElements();) {

                MClass mc2 = (MClass) e2.nextElement();

                // if(test.alignment(mc1.getLabel(), mc2.getLabel()))
                //    writeOut("NCI: " + mc1.getLabel() + ", MeSH: " + mc2.getLabel());

                // if (mc1.getLabel().equalsIgnoreCase(mc2.getLabel())) {
                if (mc1.getPrettyName().equalsIgnoreCase(mc2.getPrettyName())) {
                    ArrayList list1 = new ArrayList(), list2 = new ArrayList();
                    countDAG(mc1, list1);
                    countDAG(mc2, list2);

                    //set the minimum and maximum number of concepts in a segment
                    if (list1.size() > 1 && list1.size() < 60 && list2.size() > 1 && list2.size() < 60) {

                        /*  writeOut(writer, list1.size() + ", "  + list2.size());

                        writeOut(writer, mc1.getLabel());
                        printDAG("  - ", mc1, writer);

                        writeOut(writer, "------------------------------");

                        writeOut(writer, mc2.getLabel());
                        printDAG("  - ", mc2, writer);

                        writeOut(writer, "\n================================================\n");
                         **/

                        //the first element of list is always the root of the subgraph
                        candidate.add(new Pair(list1, list2));
                    }
                }
            }
        }//end for candidates

        // closeOut(writer);

        System.out.println("Get all pairwise disjoint segment pairs.");

        // get all segments which are pairwise disjoint
        ArrayList<Pair> allSegmPairs = getAllSegments(candidate);
        int i = 0;
        for (Pair pair : allSegmPairs) {
            //output the segments
            /*FileWriter writer = openOut(location + segLoc + "segment" + i + ".txt");

            for(Iterator it1 = ((ArrayList)pair.getObject1()).iterator(); it1.hasNext();)
            writeOut(writer, ((MClass)it1.next()).getLabel());

            writeOut(writer, "\n -----------------  \n");

            for(Iterator it2 = ((ArrayList)pair.getObject2()).iterator(); it2.hasNext();)
            writeOut(writer, ((MClass)it2.next()).getLabel());

            closeOut(writer);
             **/
            createOWLFile((MClass) ((ArrayList) pair.getObject1()).get(0), segLoc + "Onto1-segment-" + i + ".owl");
            createOWLFile((MClass) ((ArrayList) pair.getObject2()).get(0), segLoc + "Onto2-segment-" + i + ".owl");
            i++;
        }
        System.out.println("Total number of exported segment pairs :" + allSegmPairs.size());
        return allSegmPairs.size();
    }

    static void printDAG(String prefix, MClass clazz) {
        System.out.println(prefix + clazz.getLabel());
        for (Enumeration e = clazz.getSubClasses().elements(); e.hasMoreElements();) {
            printDAG("  i- " + prefix, (MClass) e.nextElement());
        }
        for (Enumeration e = clazz.getParts().elements(); e.hasMoreElements();) {
            printDAG("  p- " + prefix, (MClass) e.nextElement());
        }
    }

    static void printDAG(String prefix, MClass clazz, FileWriter writer) {

        writeOut(writer, prefix + clazz.getLabel());

        for (Enumeration e = clazz.getSubClasses().elements(); e.hasMoreElements();) {

            MClass sub = (MClass) e.nextElement();
            printDAG("  i- " + prefix, sub, writer);
        }

        for (Enumeration e = clazz.getParts().elements(); e.hasMoreElements();) {

            MClass sub = (MClass) e.nextElement();
            printDAG("  p- " + prefix, sub, writer);
        }

    }

    static void countDAG(MClass clazz, ArrayList list) {

        if (!list.contains(clazz)) {
            list.add(clazz);
        }

        for (Enumeration e = clazz.getSubClasses().elements(); e.hasMoreElements();) {
            countDAG((MClass) e.nextElement(), list);
        }

        for (Enumeration e = clazz.getParts().elements(); e.hasMoreElements();) {
            countDAG((MClass) e.nextElement(), list);
        }
    }

    static void createOWLFile(MClass root, String out) {

        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

        String ns = "http://segment.com#";
        String base = "http://segment.com";
        model.createOntology(ns);
        model.setNsPrefix("", ns);

        OntProperty partPro = model.createObjectProperty(ns + "part_of");

        //create the OntClass for the root
        createOntClass(model, partPro, ns, root);

        RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
        writer.setProperty("showXmlDeclaration", "true");
        writer.setProperty("tab", "8");
        writer.setProperty("xmlbase", base);

        try {
            writer.write(model, new FileOutputStream(out), null);

        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    static void createOntClass(OntModel model, OntProperty partPro, String ns, MClass clazz) {

        OntClass jenaClass = model.createClass(ns + clazz.getId());

        jenaClass.addLabel(clazz.getLabel(), "en");

        for (Enumeration e = clazz.getSynonyms().elements(); e.hasMoreElements();) {
            jenaClass.addLabel((String) e.nextElement(), "sn");
        }

        for (Enumeration e = clazz.getSubClasses().elements(); e.hasMoreElements();) {

            MClass sub = (MClass) e.nextElement();
            createOntClass(model, partPro, ns, sub);
            jenaClass.addSubClass(model.getOntClass(ns + sub.getId()));
        }

        for (Enumeration e = clazz.getParts().elements(); e.hasMoreElements();) {

            MClass part = (MClass) e.nextElement();
            createOntClass(model, partPro, ns, part);
            jenaClass.addSubClass(model.createSomeValuesFromRestriction(null, partPro, model.getOntClass(ns + part.getId())));
        }

        /*  for(Enumeration e = clazz.getSuperClasses().elements(); e.hasMoreElements();)
        //jenaClass.addSuperClass(((MClass) e.nextElement()).OntClass());
        jenaClass.addSuperClass(model.createClass(ns + ((MClass) e.nextElement()).getId()));

        for(Enumeration e = clazz.getPartOf().elements(); e.hasMoreElements();)
        //jenaClass.addSuperClass(model.createSomeValuesFromRestriction(null, partPro, ((MClass) e.nextElement()).OntClass()));
        jenaClass.addSuperClass(model.createSomeValuesFromRestriction(null, partPro, model.createClass(ns +((MClass) e.nextElement()).getId())));
         **/
    }

    public static void writeOut(FileWriter out, String s) {
        try {
            out.write(s + "\n");
            out.flush();
        } catch (Exception e) {
            System.err.println("java.io.IOException" + e.getMessage());
        }

    }

    // open a new file to output intermediate results
    public static FileWriter openOut(
            String fileName) {
        try {
            return new FileWriter(fileName);
        } catch (Exception e) {

            System.err.println("java.io.IOException" + e.getMessage());
        }

        return null;
    }

    public static void closeOut(FileWriter out) {
        try {
            out.close();
        } catch (Exception e) {
            System.err.println("java.io.IOException" + e.getMessage());
        }
    }

    private ArrayList<Pair> getAllSegments(ArrayList candidates) {
        Collections.shuffle(candidates);
        ArrayList<Pair> segPairs = new ArrayList<Pair>();
        for (Iterator it = candidates.iterator(); it.hasNext();) {
            boolean pairwiseDisjoint = true;
            Pair segPairCandidate = (Pair) it.next();
            // check whether the segments are pairwise disjoint
            for (Pair segPair : segPairs) {
                if (((ArrayList) segPairCandidate.getObject1()).containsAll((ArrayList) segPair.getObject1())
                        || ((ArrayList) segPair.getObject1()).containsAll((ArrayList) segPairCandidate.getObject1())
                        || ((ArrayList) segPairCandidate.getObject2()).containsAll((ArrayList) segPair.getObject2())
                        || ((ArrayList) segPair.getObject2()).containsAll((ArrayList) segPairCandidate.getObject2())) {
                    pairwiseDisjoint = false;
                    break;
                }
            }
            if (pairwiseDisjoint) {
                segPairs.add(segPairCandidate);
            }
        }
        return segPairs;
    }

    // select an segment pair and  ensure the selected segments are pairwise disjoint
    static Pair getSegment(Random random, ArrayList candidate, ArrayList segment) {

        int i = random.nextInt(candidate.size());
        // System.out.println("random number: " + i);
        // choose a candidate
        Pair segPairCandidate = (Pair) candidate.get(i);

        boolean pairwise = true;

        // check whether the segments are pairwise
        for (Iterator it = segment.iterator(); it.hasNext();) {
            Pair segPair = (Pair) it.next();
            if (((ArrayList) segPairCandidate.getObject1()).containsAll((ArrayList) segPair.getObject1())
                    || ((ArrayList) segPair.getObject1()).containsAll((ArrayList) segPairCandidate.getObject1())
                    || ((ArrayList) segPairCandidate.getObject2()).containsAll((ArrayList) segPair.getObject2())
                    || ((ArrayList) segPair.getObject2()).containsAll((ArrayList) segPairCandidate.getObject2())) {
                pairwise = false;
                break;
            }
        }

        if (pairwise) {
            return segPairCandidate;
        } else {
            getSegment(random, candidate, segment);
        }
        return null;
    }
}
