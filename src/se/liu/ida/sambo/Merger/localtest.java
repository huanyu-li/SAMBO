/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.Merger;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import se.liu.ida.sambo.algos.matching.algos.AlgoConstants;
import se.liu.ida.sambo.session.Commons;

/**
 *
 * @author huali50
 */
public class localtest {
    
    public static void main(String args[]) throws OWLOntologyCreationException {
        testMergerManager mm = new testMergerManager();
        
        mm.loadOntologies("C:\\Users\\huali50\\Desktop\\ontologies\\nose_MA_1.owl","C:\\Users\\huali50\\Desktop\\ontologies\\nose_MeSH_2.owl");
        mm.init();
        Commons.hasProcessStarted = true;
        Commons.isFinalized = 0;
        AlgoConstants.STOPMATACHING_PROCESS = false;
        AlgoConstants.ISRECOMMENDATION_PROCESS = false;
        
        double[] weight = new double[Constants.singleMatchers.length];
        //mm.matching(4, 0);
        weight[Constants.EditDistance] = Double.parseDouble("0.6" + Constants.EditDistance);
        
        mm.getSuggestions(Constants.STEP_CLASS, weight, 0.6, "");
        
    }
}
