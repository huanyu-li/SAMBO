/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.jdbc.ResourceManager;
import se.liu.ida.sambo.jdbc.bioportal.mappingTable;
import se.liu.ida.sambo.jdbc.bioportal.parentTable;
import se.liu.ida.sambo.jdbc.bioportal.synonymsTable;
import uk.ac.ebi.ontocat.OntologyService;
import uk.ac.ebi.ontocat.OntologyService.SearchOptions;
import uk.ac.ebi.ontocat.OntologyServiceException;
import uk.ac.ebi.ontocat.OntologyTerm;
import uk.ac.ebi.ontocat.bioportal.BioportalOntologyService;

/**
 * To query bioportal online server.
 * 
 * @author Rajaram
 * @version 1.0
 */
public class BioportalSearch extends Matcher{    
    /**
     * MySQL connection.
     */
    private Connection sqlConn = null;
    /**
     * To store and access synonyms table in the local database.
     */
    private synonymsTable synDB = null;
    /**
     * To store and access parents table in the local database.
     */
    private parentTable parentDB = null;
    /**
     * To store and access mappings table in the local database.
     */
    private mappingTable mapDB = null;
    /**
     * Ticket to access bioportal.
     */
    private OntologyService ticket = new BioportalOntologyService
            ("3a6ded3d-0768-4d32-9608-11fc205f37a2");
    /**
     * To compute the similarity values using swap word matcher.
     */
    private SwapWords swapWords = new SwapWords();
    /**
     * Default constructor.
     */
    public BioportalSearch() {
        try {
            sqlConn = ResourceManager.getConnection();
            synDB = new synonymsTable(sqlConn);
            parentDB = new parentTable(sqlConn);
            mapDB = new mappingTable(sqlConn);
        } catch (SQLException ex) {
            Logger.getLogger(BioportalSearch.class.getName()).
                    log(Level.SEVERE, null, ex);
        }        
    }    
    /**
     * To get parents for the given term.
     * 
     * @param term
     * @return  List of parents for the term. 
     */
    private ArrayList<String> getParents(String term) {
                
        ArrayList<String> parents = new ArrayList<String>();        
        parents = parentDB.selectParents(term);
        
        if (!parents.isEmpty()) {            
            return parents;
        }
        
        try {            
            for (OntologyTerm ot : ticket.searchAll(term,SearchOptions.EXACT)) {
                
                if (ot.getLabel().equalsIgnoreCase(term)) {
                    List<OntologyTerm> relations = ticket.getParents(ot);
                    
                    for (OntologyTerm relation:relations) {
                                                
                        String parent = relation.getLabel().toLowerCase(); 
                        
                        if(parent.contains("owl:thing") || 
                                parent.contains("http://")) {                            
                            parent = "";
                        }
                        parent = NameProcessor.getInstance().advCleanName(parent);
                        if((parents.isEmpty() || !parents.contains(parent)) 
                                && !parent.isEmpty()) {                            
                            parents.add(parent);
                        }
                    }
                }
            }                       
            
            String parentGroup = "";       
        
            for (String parnt:parents) {
                parentGroup = parentGroup.concat(parnt);
                parentGroup = parentGroup.concat("#");
            }
        
            if (!parentGroup.isEmpty()) {
                // To remove last #
                parentGroup = parentGroup.substring(0, 
                        (parentGroup.length()-1));            
                parentDB.insertParents(term, parentGroup);
            }
        } catch (OntologyServiceException ex) {
            Logger.getLogger(BioportalSearch.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return parents;
    }
    
    /**
     * To get synonyms for the given term.
     * 
     * @param term
     * @return  List of synonyms for the term.
     */
    private ArrayList<String> getSynonyms(String term) {
                
        ArrayList<String> synonyms = new ArrayList<String>();         
        synonyms = synDB.selectSynonyms(term);        
        
        if (!synonyms.isEmpty()) {
            return synonyms;
        }
        
        synonyms.add(term);
        
        try {
            for (OntologyTerm ot : ticket.searchAll(term, 
                    SearchOptions.EXACT)) {
                
                if (ot.getLabel().equalsIgnoreCase(term)) {
                    
                    for (String synonym:ticket.getSynonyms(
                            ot.getOntologyAccession(), ot.getAccession())) {  
                        
                        if(synonym.contains("http://")) {
                            synonym = "";
                        }
                        synonym = NameProcessor.getInstance().
                                advCleanName(synonym);
                        if (!synonym.isEmpty() && !synonyms.contains(synonym)) {                            
                            synonyms.add(synonym); 
                        }
                    }
                }
            }
        } catch (OntologyServiceException ex) {
            Logger.getLogger(BioportalSearch.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        String synonymsGroup = "";
        
        for(String synms:synonyms) {
            synonymsGroup = synonymsGroup.concat(synms);
            synonymsGroup = synonymsGroup.concat("#");
        }
        
        if (!synonymsGroup.isEmpty()) {
            // To remove last # 
            synonymsGroup = synonymsGroup.substring(0, 
                    (synonymsGroup.length()-1));            
            synDB.insertSynonyms(term, synonymsGroup);
        }
        return synonyms;
    }
       
    
    /**
     * To get mappings for the given term.
     * 
     * (Note: This method search all the ontologies in the bioportal, so it will
     * take more time for the computation)
     * 
     * @param term
     * @return  List of mappings for the term. 
     */    
    private ArrayList<String> getMappings(String term) {
                
        ArrayList<String> mappings = new ArrayList<String>(); 
        String [] keys = {"Legacy Concept Name","Preferred_Name",
            "rdfs:label","SYNONYM SY","SYNONYM FN","anatomy:synonym",
            "EXACT SYNONYM"};
        /**
         * Since this method takes more time for the computation, we can set
         * limit on maximum number of mappings we would like to gather. 
         */
        int limit = 500;
        
        mappings = mapDB.selectMappings(term);        
        
        if (!mappings.isEmpty()) {
            return mappings;
        }
        mappings.add(term);
        
        try {
            for (OntologyTerm ot : ticket.searchAll(term, 
                    SearchOptions.INCLUDE_PROPERTIES)) {
                
                if (mappings.size() > limit) {
                    break;
                }
                
                Map<String, List<String>> maps = null;
                
                try {
                    maps = ticket.getAnnotations(ot);
                } catch(Exception e) {
                    e.printStackTrace();                
                }
                
                if (maps != null) {                    
                    for (String key:keys) {                        
                        ArrayList<String> colorsList = 
                                (ArrayList) maps.get(key);                        
                        if (colorsList != null) {                            
                            for (Object s:colorsList) {                                
                                String map = s.toString();    
                                map = NameProcessor.getInstance().
                                            advCleanName(map);
                                if (mappings.isEmpty() || 
                                        !mappings.contains(map)) {      
                                    mappings.add(map);
                                }
                            }
                        }
                    }
                }
            }
        } catch (OntologyServiceException ex) {
            Logger.getLogger(BioportalSearch.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        
        String mapsGroup="";
        
        for (String mapp:mappings) {
            mapsGroup = mapsGroup.concat(mapp);
            mapsGroup = mapsGroup.concat("#");
        }
        
        if (!mapsGroup.isEmpty()) {
            // To remove last #
            mapsGroup=mapsGroup.substring(0, (mapsGroup.length()-1));           
            mapDB.insertParents(term, mapsGroup);
        }
        return mappings;
    }
    
    /**
     * To add slash to the strings to be stored in the database.
     * 
     * @param str
     * @return 
     */
    private String addSlashes(String str){
        if(str==null) {
            return "";
        }
        StringBuffer s = new StringBuffer ((String) str);
        
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt (i) == '\'') {
                s.insert (i++, '\\');
            }
        }
        return s.toString();
    }
    /**
     * 
     * Calculate the similarity value between any two strings.
     * 
     * @param term1     The first string.
     * @param term2     The second string.
     *
     * @return          similarity value between two given strings.
     */
    public double getSimValue(String term1, String term2) {
        
        // Check if two given terms are equivalent.
        if (swapWords.getSimValue(term1, term2) > 
                MatcherConstants.MIN_SIMVALUE) {
            return MatcherConstants.MAX_SIMVALUE;
        }
        
        ArrayList<String> mappingTerm1 = getMappings(term1);
        ArrayList<String> mappingTerm2 = getMappings(term2);
        
        if(mappingTerm1.contains(term2)) {
            return MatcherConstants.MAX_SIMVALUE;
        }
        if(mappingTerm2.contains(term1)) {
            return MatcherConstants.MAX_SIMVALUE;
        }
        
        return MatcherConstants.MIN_SIMVALUE;
        
        
    }
    
    public static void main(String[] args) {
        
        BioportalSearch bp = new BioportalSearch();
        
        double simValue = bp.getSimValue("heart", "cardiac");
        
        System.out.println(simValue);
        
    }
}
