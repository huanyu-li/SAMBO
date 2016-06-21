/*
 * Clust.java
 *
 * Created on den 11 maj 2007, 10:20
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package se.liu.ida.sambo.segPairSelAlgs;

/**
 *
 * @author hetan
 */
import java.util.*;
import java.io.*;

import org._3pq.jgrapht.*;
import org._3pq.jgrapht.graph.*;
import org._3pq.jgrapht.alg.*;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;

import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.sambo.util.Pair;

public class Clust {
    ArrayList clusterOne,  clusterTwo;
    MOntology monto1, monto2;
    int segPairNum;
    final int LeastSize = 5;

    public Clust(String ontoUri1, String ontoUri2, int segPairNum){
        this.monto1 =  OntManager.loadOntology(ontoUri1, false);
        this.monto2 =  OntManager.loadOntology(ontoUri2, false);
        this.segPairNum = segPairNum;
        clusterOne = new ArrayList();
        clusterTwo = new ArrayList();
    }


       public static void main(String args[]) throws Exception {
        String location = System.getProperty("user.dir") ;
        String ontoLoc =  location + "/ontology/";
        String segLoc = location + "/segment/clust/";
        String ontoUri1 = "file:///" + ontoLoc + "NCI-anatomy.owl";
        String ontoUri2 = "file:///" + ontoLoc + "mesh-anatomy-A.owl";
        SubG sg = new SubG(ontoUri1, ontoUri2, 5);
        sg.run(segLoc);
       }
       
       public void run(String segLoc){
        Random random = new Random();

        for (int s = 0; s < segPairNum; s++) {
            System.out.println("random number: " + random.nextInt(151));
        }

        System.out.println("NCI anatomy has " + monto1.getClasses().size() + " concepts");
        System.out.println("MeSH anatomy has " + monto2.getClasses().size() + " concepts");

        //Clustering

        //Creates a new simple weighted graph for each root in the ontology
        SimpleWeightedGraph[] graphListOne = new SimpleWeightedGraph[monto1.roots().size()];
        SimpleWeightedGraph[] graphListTwo = new SimpleWeightedGraph[monto2.roots().size()];

        int i = 0;

        for (Enumeration e = monto1.roots().elements(); e.hasMoreElements();) {
            graphListOne[i] = new SimpleWeightedGraph();
            generateGraph(graphListOne[i], (MClass) e.nextElement());
            i++;
        }

        i = 0;
        for (Enumeration e = monto2.roots().elements(); e.hasMoreElements();) {
            graphListTwo[i] = new SimpleWeightedGraph();
            generateGraph(graphListTwo[i], (MClass) e.nextElement());
            i++;
        }


        // for each graph generated from different root
        // 1. find the maximal spanning tree of a weighted graph
        // 2. cluster the ontology concepts
        for (int m = 0; m < graphListOne.length; m++) {
            Clustering(spanning(graphListOne[m]), LeastSize, clusterOne);
        }


        for (int m = 0; m < graphListTwo.length; m++) {
            Clustering(spanning(graphListTwo[m]), LeastSize, clusterTwo);
        }


        ArrayList eq = new ArrayList();

        boolean cont = false;
        for (Iterator it1 = clusterOne.iterator(); it1.hasNext();) {
            // find one cluster from ontology 1
            SimpleWeightedGraph graphOne = (SimpleWeightedGraph) it1.next();
            startover:
            {
                if (cont) {
                    continue;
                }
                for (Iterator it2 = clusterTwo.iterator(); it2.hasNext();) {
                    // find one cluster from ontology 2
                    SimpleWeightedGraph graphTwo = (SimpleWeightedGraph) it2.next();

                    for (Iterator it3 = graphOne.vertexSet().iterator(); it3.hasNext();) {
                        // get claszz in cluster 1
                        MClass clazz1 = (MClass) it3.next();
                        for (Iterator it4 = graphTwo.vertexSet().iterator(); it4.hasNext();) {
                            // get clazz in cluster 2
                            MClass clazz2 = (MClass) it4.next();
                            // if clazz1 and clazz2 has the same label
                            if (clazz1.getLabel().equalsIgnoreCase(clazz2.getLabel())) {
                                // add a new segment pair
                                eq.add(new Pair(graphOne, graphTwo));
                                // remove the added cluster Two
                                clusterTwo.remove(graphTwo);
                                cont = true;
                                break startover;
                            }//if there exists matching concepts

                        }
                    }//for: search matching concepts in a pair of cluster
                }
            }//for: search matching clusters
            cont = false;
        }


        //Write ontologies to OWL files
        int j = 1;
        for (Iterator it = eq.iterator(); it.hasNext();) {
            Pair pair = (Pair) it.next();
            createOWLFile((SimpleWeightedGraph) pair.getObject1(), segLoc + "NCI-" + j + ".owl");
            createOWLFile((SimpleWeightedGraph) pair.getObject2(), segLoc + "MeSH-" + j + ".owl");
            j++;
        }
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

    /**
     * this function used to find the maximal spanning tree of a weighted graph.
     * this function return is also a weighted graph.
     *
     * @param	g a weighted graph
     * @return	a weighted graph
     */
    private SimpleWeightedGraph spanning(SimpleWeightedGraph g) {
        Set V = g.vertexSet();
        System.out.println("there are " + V.size() + " nodes in graph.");
        Set E = g.edgeSet();
        System.out.println("there are " + E.size() + " edges in graph.");

        SimpleWeightedGraph newg = new SimpleWeightedGraph();
        Iterator iter = V.iterator();
        while (iter.hasNext()) {
            newg.addVertex(iter.next());
        }

        //for every time, the maximal edge is selected.
        ConnectivityInspector inspector = new ConnectivityInspector(newg);

        while (!E.isEmpty() && !inspector.isGraphConnected()) {
            iter = E.iterator();
            double max = Double.MIN_VALUE;
            Edge maxEdge = null;
            // for all edges, find the maxEdge
            while (iter.hasNext()) {
                Edge ee = (Edge) iter.next();
                if (max < ee.getWeight()) {
                    max = ee.getWeight();
                    maxEdge = ee;
                }
            }
            //set the weight of the current maximal edge to 0
            maxEdge.setWeight(0);
            //if the two vertexes of the selected edge is already connected by other edges which have been
            //selected in the maximal spanning tree, the edge can not be included in the maximal spanning tree
            if (!inspector.pathExists(maxEdge.getSource(), maxEdge.getTarget())) {
                newg.addEdge(maxEdge);
            }

            inspector = new ConnectivityInspector(newg);

        }

        return newg;
    }

    /**
     * this function used to cluster the ontology. For each time, breaking the edge with the minimal weight to partition the
     * connected parts of the maximal spanning tree into two parts. If the partitioned parts are too small, they should be
     * incorporated into its neighbour part.
     *
     * @param g	the maximal spanning tree
     * @param low	the low limit for the concept number of a cluster
     */
    private void Clustering(SimpleWeightedGraph g, int low, ArrayList cluster) //low <= size <=
    {

        Set E = g.edgeSet();
        Iterator iter = E.iterator();
        double min = Double.MAX_VALUE;
        Edge minEdge = null;
        // find the minEdge
        while (iter.hasNext()) {
            Edge ee = (Edge) iter.next();
            if (min > ee.getWeight()) {
                min = ee.getWeight();
                minEdge = ee;
            }
        }
        // if min==1, it connects a leaf node, return
        if (min == 1) {
            cluster.add(g);
            return;
        }

        g.removeEdge(minEdge.getSource(), minEdge.getTarget());

        ConnectivityInspector inspector = new ConnectivityInspector(g);
        List glist = inspector.connectedSets();
        iter = glist.iterator();
        boolean needseperate = true;
        while (iter.hasNext()) {
            Set temp_s = (Set) iter.next();
            if (temp_s.size() < low) {
                needseperate = false;
            }
        }
        // if not need seperate, set the minEdge with a big weight
        // and start find new minEdge
        if (needseperate == false) {
            Edge temp_e = g.addEdge(minEdge.getSource(), minEdge.getTarget());
            temp_e.setWeight(1); //maxvalue
            Clustering(g, low, cluster);
        } else {
            iter = glist.iterator();
            while (iter.hasNext()) {
                // 1. get a connected set
                Set temp_s = (Set) iter.next();
                // 2. initiate a new graph
                SimpleWeightedGraph new_g = (SimpleWeightedGraph) g.clone();

                Iterator temp_iter = temp_s.iterator();
                while (temp_iter.hasNext()) {
                    // 3. remove vertexses in the connected set
                    new_g.removeVertex(temp_iter.next());
                }
                Clustering(new_g, low, cluster);

            }

        }
    }

    private void generateGraph(SimpleWeightedGraph graph, MClass root) {
        graph.addVertex(root);
        // get the number of children including classes of sub and partOf
        int childnum = root.getSubClasses().size() + root.getParts().size();
        for (Enumeration e = root.getSubClasses().elements(); e.hasMoreElements();) {
            MClass subclass = (MClass) e.nextElement();
            graph.addVertex(subclass);
            // edge relates root with its subclass
            Edge edge = graph.addEdge(root, subclass);
            //the graph must not contain any edge e2 such that e2.equals(e).
            //If such e2 is found then the newly created edge e is abandoned,
            //the method leaves this graph unchanged returns null.
            if (edge != null) {
                // every edge has weight 1.0/childnum
                edge.setWeight(1.0 / childnum); //System.out.println(edge.getWeight());
            }
            // recursively do it for every subclass
            generateGraph(graph, subclass);
        }

        for (Enumeration e = root.getParts().elements(); e.hasMoreElements();) {
            MClass subclass = (MClass) e.nextElement();
            // System.out.println(clazz.getLabel() + " --> " + subclass.getLabel());
            graph.addVertex(subclass);
            Edge edge = graph.addEdge(root, subclass);
            //  System.out.println(edge);
            if (edge != null) {
                edge.setWeight(1.0 / childnum); //System.out.println(edge.getWeight());
            }
            generateGraph(graph, subclass);
        }
    }

    static void createOWLFile(SimpleWeightedGraph graph, String out) {

        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

        String ns = "http://segment.com#";
        String base = "http://segment.com";
        model.createOntology(ns);
        model.setNsPrefix("", ns);

        //create the OntClass for the root

        RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
        writer.setProperty("showXmlDeclaration", "true");
        writer.setProperty("tab", "8");
        writer.setProperty("xmlbase", base);

        createOntClass(model, graph, ns);

        try {
            writer.write(model, new FileOutputStream(out), null);

        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //WARNING: owl file only contains the list of concepts
    static void createOntClass(OntModel model, SimpleWeightedGraph graph, String ns) {

        //System.out.println("\n" + graph);

        for (Iterator it = graph.vertexSet().iterator(); it.hasNext();) {

            MClass clazz = (MClass) it.next();
            OntClass jenaClass = model.createClass(ns + clazz.getId());

            jenaClass.addLabel(clazz.getLabel(), "en");

            for (Enumeration e = clazz.getSynonyms().elements(); e.hasMoreElements();) {
                jenaClass.addLabel((String) e.nextElement(), "sn");
            }
        }
    }

 
}
