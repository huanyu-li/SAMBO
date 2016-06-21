/*
 * HierarchySearch.java
 *
 */

package se.liu.ida.sambo.algos.matching.algos;


import com.objectspace.jgl.OrderedMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.simvalue.SaveSimValuesDBAccess;
import se.liu.ida.sambo.jdbc.simvalue.SimValueGenerateQuery;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @author  He Tan
 * @version
 */
public class HierarchySearch{
    
    //the propagation coefficient for parents
    private final double WEIGHT_PARENT = 0.2;
    //the propagation coefficient for children
    private final double WEIGHT_CHILD  = 0.3;
    //the maximal length of paths
    private final int MAXIMAL_LENGTH = 3;
    
    //private SimValueConstructor simValueConstructor;
    
    private Vector list;
    
    private OrderedMap Onto1, Onto2;
    
    private Object c1="", c2="";
    
    private ArrayList<String> UPDATE_STATEMENT=new ArrayList<String>();
    
    private String concept1 ="",  concept2="";
    
    private String ontologiesPairName=AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_1).concat("#").concat(AlgoConstants.settingsInfo.getName(Constants.ONTOLOGY_2));
           
    // To get table name from SaveSimValuesDBAccess class
    SaveSimValuesDBAccess simvalu = new SaveSimValuesDBAccess();
    private String simValueTablename=simvalu.getTableName();//"dbsambo.savesimvalues";
    //*
    
    
    SimValueGenerateQuery simValueTable;
    
    
    Connection sqlConn=null;
    
    
//    /** Creates new HierarchySearch */
//    public HierarchySearch(SimValueConstructor simValueConstructor){
//        
//        this.simValueConstructor = simValueConstructor;
//        this.list = new Vector();
//    }
    
    
    
     /** Creates new HierarchySearch */
    public HierarchySearch(OrderedMap Onto1, OrderedMap Onto2){
        
        this.Onto1=Onto1;
        this.Onto2=Onto2;
        //this.simValueConstructor = simValueConstructor;
        this.list = new Vector();
        
        
        
        try {
            sqlConn=ResourceManager.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(HierarchySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         simValueTable = new SimValueGenerateQuery(ontologiesPairName, sqlConn);
        
        
                    String colName4Matcher="matcher"+AlgoConstants.HIERARCHY;           
            
                    boolean IsColumnavailable=simValueTable.isColumnAvailable(colName4Matcher);
            
            if(!IsColumnavailable)
                {
                    // create new column in savesimvalue table
                simValueTable.createColumn(colName4Matcher);
                }
        
        
    }
    
    
    /**
     * Propogate the similarity (probability of match) value from the identified matchings.
     *
     * @param matchSet the set of pairs of concepts already identified
     *
     */
    
   
    public void propogateSimValue(){
        
        for(Enumeration e1 = Onto1.elements(); e1.hasMoreElements();){          
                    c1 = e1.nextElement();
                    
                                   
                
                for(Enumeration e2 = Onto2.elements(); e2.hasMoreElements();)
                {
                
                    c2 = e2.nextElement();
                
                    Pair pair=new Pair(c1,c2); 
            
            //Pair pair = (Pair) e.nextElement();
                    
                        concept1 = ((MElement) pair.getObject1()).getId();
                        concept2 = ((MElement) pair.getObject2()).getId();
        
              
                    
            //if(simValueConstructor.get(pair)[AlgoConstants.FINAL_VALUE] == AlgoConstants.ALIGNMENT){                
                if(AlignmentConstants.IsAligned!=null && AlignmentConstants.IsAligned.contains(concept1+AlgoConstants.SEPERATOR+concept2)){
                list.clear();
                parentPropogation((MClass) pair.getObject1(), (MClass) pair.getObject2(), 1, 1);
                list.clear();
                childPropogation((MClass) pair.getObject1(), (MClass) pair.getObject2(), 1, 1);
            }
            
            //** This else is ADDED by Rajaram IF CONCEPTS are not Aligned then they should get sim value as 0    
            else
               UPDATE_STATEMENT.add("UPDATE "+simValueTablename+" SET matcher"+AlgoConstants.HIERARCHY+" =0  WHERE ontologies ='"+ontologiesPairName+"' and concept1 ='"+concept1+"' and concept2 ='"+concept2+"'");
            
        }
                
                if(UPDATE_STATEMENT.size()>100000){
                simValueTable.executeStatements(UPDATE_STATEMENT,sqlConn);
                delayLine(50);
                UPDATE_STATEMENT.clear();
            }
    }
     
        
        if(UPDATE_STATEMENT.size()>0){
            simValueTable.executeStatements(UPDATE_STATEMENT,sqlConn);
            
            delayLine(50);
            }
        
        
        ResourceManager.close(sqlConn);
        
    }
    
    
    //propogate to parents
    private void parentPropogation(MClass c1, MClass c2, int length1, int length2){
        //System.out.println(" --- PP : " + c1.toString() + ";  "  + c2.toString());
        
        if(length1 <= MAXIMAL_LENGTH && length2 <= MAXIMAL_LENGTH){
            
         //   for(Enumeration e1 = c1.getSuperParts().elements(); e1.hasMoreElements();){
            for(Enumeration e1 = c1.getSuperClasses().elements(); e1.hasMoreElements();){
                
                parentPropogation((MClass) e1.nextElement(), c2, length1 + 1, length2);
                // MClass sup1 = (MClass) e1.nextElement();
                
                //for(Enumeration e2 = c2.getSuperParts().elements(); e2.hasMoreElements();){
                for(Enumeration e2 = c2.getSuperClasses().elements(); e2.hasMoreElements();){
                    
                    parentPropogation(c1, (MClass) e2.nextElement(), length1, length2 + 1 );
                }
            }
        }
        
        calculate(new Pair(c1, c2), WEIGHT_PARENT, length1, length2);
    }
    
    //propogate to children
    private void childPropogation(MClass c1, MClass c2, int length1, int length2){
        
        if(length1 <= MAXIMAL_LENGTH && length2 <= MAXIMAL_LENGTH){
            
           // for(Enumeration e1 = c1.getSubParts().elements(); e1.hasMoreElements();){
            for(Enumeration e1 = c1.getSubClasses().elements(); e1.hasMoreElements();){
                
                childPropogation((MClass) e1.nextElement(), c2, length1 + 1, length2);
                
                //for(Enumeration e2 = c2.getSubParts().elements(); e2.hasMoreElements();){
                for(Enumeration e2 = c2.getSubClasses().elements(); e2.hasMoreElements();){
                    
                    childPropogation(c1, (MClass) e2.nextElement(), length1, length2 + 1 );
                }
            }
        }
        
        calculate(new Pair(c1, c2), WEIGHT_CHILD, length1, length2);
    }
    
    
    //calculate the propogation value and set it to simValueConstructor
    private void calculate(Pair pair, double weight, int l1, int l2){
        
        if(!list.contains(pair)){
            
            concept1 = ((MElement) pair.getObject1()).getId();
            concept2 = ((MElement) pair.getObject2()).getId();
            
            double simvalue=(weight/l1)*(weight/l2);
            UPDATE_STATEMENT.add("UPDATE "+simValueTablename+" SET matcher"+AlgoConstants.HIERARCHY+" = "+simvalue+" WHERE ontologies ='"+ontologiesPairName+"' and concept1 ='"+concept1+"' and concept2 ='"+concept2+"'");
            //simValueConstructor.setPairValue(pair, (weight/l1)*(weight/l2), AlgoConstants.HIERARCHY);
            list.add(pair);
        }
    }
    


private void delayLine(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(HierarchySearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }


}
