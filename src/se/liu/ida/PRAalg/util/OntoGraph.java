/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.PRAalg.util;

import java.util.Enumeration;
import java.util.logging.Logger;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org._3pq.jgrapht.edge.DirectedEdge;
import org._3pq.jgrapht.graph.SimpleDirectedGraph;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.Merger.OntManager;

/**
 * @author Qiang Liu
 * Created on Dec 20, 2010, 5:25:05 PM
 */
public class OntoGraph {

    private static Logger logger = Logger.getLogger(OntoGraph.class.getName());
    SimpleDirectedGraph ontograph;


    /**
     *
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception{
        MOntology monto = OntManager.loadOntology("file:///c:/onto1.owl", false);
        OntoGraph og = new OntoGraph(monto);
        for (Enumeration e1 = monto.getClasses().elements(); e1.hasMoreElements();) {
            MClass c1 = (MClass) e1.nextElement();
            System.out.println(c1.getURI());
//            for (Enumeration e2 = monto.getClasses().elements(); e2.hasMoreElements();) {
//                MClass c2 = (MClass) e2.nextElement();
//                System.out.print(c1.OntClass().getLocalName() + "->" + c2.OntClass().getLocalName() + ": ");
//                System.out.println(og.hasInferSupRel(c1, c2));
//            }
        }
    }

    /**
     *  constructor
     * @param monto the ontology
     */
    public OntoGraph(MOntology monto) {
        logger.info("Build ontograph.");
        //construct a directed graph according to the structure of the ontology
        ontograph = new SimpleDirectedGraph();
        // fill the graph with concepts
        for (Enumeration e = monto.getClasses().elements(); e.hasMoreElements();) {
            MClass c = (MClass) e.nextElement();
            ontograph.addVertex(c);
        }
        // fill the graph with relations
        for (Enumeration clsEnu = monto.getClasses().elements(); clsEnu.hasMoreElements();) {
            MClass cls = (MClass) clsEnu.nextElement();
            for (Enumeration subEnu = cls.getSubClasses().elements(); subEnu.hasMoreElements();) {
                MClass subcls = (MClass) subEnu.nextElement();
                ontograph.addEdge(new DirectedEdge(subcls, cls));
            }
            for (Enumeration equEnu = cls.getEquivOnttClasses().elements(); equEnu.hasMoreElements();) {
                MClass equcls = (MClass) equEnu.nextElement();
                ontograph.addEdge(new DirectedEdge(equcls, cls));
                ontograph.addEdge(new DirectedEdge(cls, equcls));
            }
            
        }
    }

    /**
     * check if class c1 has c2 as a subclass
     * @param c1 class 1
     * @param c2 class 2
     * @return true, if c1 has subclass c2; false otherwise
     */
    public boolean hasInferSubRel(MClass c1, MClass c2){
        return c1.equals(c2) || DijkstraShortestPath.findPathBetween(ontograph,c2,c1)!=null;
    }

    /**
     *check if class c1 has c2 has a superclass
     * @param c1 class 1
     * @param c2 class 2
     * @return true if c1 has superclass c2; false otherwise
     */
    public boolean hasInferSupRel(MClass c1, MClass c2){
        return c1.equals(c2) || DijkstraShortestPath.findPathBetween(ontograph,c1,c2)!=null;
    }

}
