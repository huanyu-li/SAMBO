package se.liu.ida.sambo.text.query;

/*
 * PubMedQuerier.java
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
 * Get a PMID list  from PubMed.
 *
 * @author  He Tan
 * @version
 *
 */
public class PubMedQuerier{
    
    
  //  private String QUERY_FIELD = "tiab";  //the query field includes Title/Abstract
   // private static final String QUERY_FIELD = "ecno";  //the query field include EC/RN number.
    private static final String QUERY_FIELD = "";  //without query field
    
    private int FETCH_LIMIT = 100;   // Limit of PMIDs per fetch.
    
    private DocumentBuilder dBuilder;                                      //XML document builder
    private Document result;						// Query result.
    private NodeList ids;							// Number of hits.
    private NodeList notFound;                                                  //error or warning message.
    
    private StringBuffer queryBuffer;
    private PubMedTimer timer = PubMedTimer.getInstance();
    private String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed";
    
    
    public PubMedQuerier() throws ParserConfigurationException{
        
        System.out.println("create a PubMed querier");
        
        System.out.println("query from "  + QUERY_FIELD);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        dBuilder = factory.newDocumentBuilder();
    }
    
    
    
    public PubMedQuerier(String tool, String email) throws ParserConfigurationException{
        this();
        if(tool != null && tool.length() > 0)
            baseUrl += "&tool=" + tool;
        if(email != null && email.length() > 0)
            baseUrl += "&email=" + email;
    }
    
  /* Prepare query URL: append all limits.
   *
   *@param queryTerm the query term
   *@param start the sequential number of the first record retrieved
   *@param limit limits of PUMID per fetch
   *
   *@return the prepared query URL
   */
    private String prepareQueryURL(String queryTerm, int start, int limit){
        
        queryBuffer = new StringBuffer(baseUrl);
        
        queryBuffer.append("&term=").append(queryTerm.replace(' ', '+'));
        
        queryBuffer.append("&field=").append(QUERY_FIELD);
        queryBuffer.append("&retstart=").append(start);
        queryBuffer.append("&retmax=").append(limit);
        queryBuffer.append("&retmode=").append(PubMedConstants.retMode[PubMedConstants.RET_MODE_XML]);
        return queryBuffer.toString();
    }
    
    
        
  /* Prepare query URL: append all limits.
   *
   *@param queryTerm the query term
   *@param start the sequential number of the first record retrieved
   *@param limit limits of PUMID per fetch
   *@param field the query field
   *
   *@return the prepared query URL
   */
    private String prepareQueryURL(String queryTerm, String field, int start, int limit){
        
        queryBuffer = new StringBuffer(baseUrl);
        
        queryBuffer.append("&term=").append(queryTerm.replace(' ', '+'));
        
        queryBuffer.append("&field=").append(field);
        queryBuffer.append("&retstart=").append(start);
        queryBuffer.append("&retmax=").append(limit);
        queryBuffer.append("&retmode=").append(PubMedConstants.retMode[PubMedConstants.RET_MODE_XML]);
        return queryBuffer.toString();
    }
    
    
  /* Prepare query URL: append all limits.
   *
   *@param queryTerm the query term
   *@param queryField the query field
   *
   *@return the prepared query URL
   */
    private String prepareQueryURL(String queryTerm){
        
        queryBuffer = new StringBuffer(baseUrl);
        
        queryBuffer.append("&term=").append(queryTerm.replace(" ", "+AND+"));
        queryBuffer.append("&field=").append(QUERY_FIELD);
        
        return queryBuffer.toString();
    }
    
    
  /* Prepare query URL: append all limits.
   *
   *@param queryTerm the query term
   *
   *@return the prepared query URL
   */
    private String prepareQueryURL(String queryTerm, String field){
        
        queryBuffer = new StringBuffer(baseUrl);
        
        queryBuffer.append("&term=").append(queryTerm.replace(" ", "+AND+"));
        queryBuffer.append("&field=").append(field);
        
        return queryBuffer.toString();
    }
    
    
    //perform the query    
    private int doQuery(String queryURL){
        
      // System.out.println(queryURL);
        
        synchronized(timer){
            timer.waitForMyTurn();    // Wait for enough interval between 2 trials.
            try{ // Send the query to and parse response from PubMed
                long sendQueryTime = System.currentTimeMillis();
                result = dBuilder.parse(queryURL);
                if(result != null)
                    timer.setLastQueryTime(sendQueryTime);  // Set new query time.
                
            }catch (Exception e) {
                e.printStackTrace();
                return PubMedConstants.CONNECT_FAIL;
            }
            
            
        }
        
        // Check whether query successful
        notFound = null;
        if(!PubMedConstants.ignoreError)
            notFound = result.getElementsByTagName("ErrorList");
        if(notFound == null && !PubMedConstants.ignoreWarning)
            notFound = result.getElementsByTagName("WarningList");
        if(notFound != null && notFound.getLength() > 0)
            return PubMedConstants.NOT_FOUND;
        
        else{	// If successful
            NodeList list = result.getElementsByTagName("Id");
            if(list != null && list.getLength()>0){
                ids = list;
                return PubMedConstants.QUERY_SUCCESS;
            }
            
            return PubMedConstants.QUERY_FAIL;
        }
        
    }
    
    
    /**Send a query to PubMed.
     * @param queryTerm
     * @return # of hits if successful.
     */
    public int sendQuery(String queryTerm){
        return doQuery(prepareQueryURL(queryTerm));
    }
    
    
    /**Send a query to PubMed.
     * @param queryTerm
     *@param field the query field
     * @return # of hits if successful.
     */
    public int sendQuery(String queryTerm, String field){
        return doQuery(prepareQueryURL(queryTerm, field));
    }
    
    
    /**Send a query to PubMed.
     * @param queryTerm
     * @return # of hits if successful.
     */
    public int sendQuery(String queryTerm, int start, int limit){
        return doQuery(prepareQueryURL(queryTerm, start, limit));
        
        
    }
    
    
    /**Send a query to PubMed.
     * @param queryTerm  
     *@param field the query field
     * @return # of hits if successful.
     */
    public int sendQuery(String queryTerm, String field, int start, int limit){
        return doQuery(prepareQueryURL(queryTerm, field, start, limit));
        
        
    }
    
    
    /**Get the list of ID extractec from PubMed.
     * @return  the vector
     */
    public Vector getPMIDList(){
        
        Vector v = new Vector();
        
        for(int i = ids.getLength(); i> 0; i--){
            NodeList children = ids.item(i-1).getChildNodes();
            for(int j = children.getLength(); j> 0; j--)
                if (children.item(j-1).getNodeType() == Node.TEXT_NODE)
                    v.add(children.item(j-1).getNodeValue());
        }
        return v;
    }
    
    /**Get the hits of the query
     *
     *@return the hits
     */
    public double getHits(){        
        System.out.println(result);
        NodeList list = result.getElementsByTagName("Count");
        
        if(list != null && list.getLength()>0){
            
            for(int i = list.getLength(); i> 0; i--){
                
                Node count = list.item(i-1);
                if(count.getParentNode().getNodeName().equalsIgnoreCase("eSearchResult")){
                    
                    NodeList text = count.getChildNodes();
                    for(int j = text.getLength(); j> 0; j--)
                        if (text.item(j-1).getNodeType() == Node.TEXT_NODE)
                            return Double.parseDouble(text.item(j-1).getNodeValue());
                            
                }
            }
        }
        
        return (new Integer(PubMedConstants.XML_EXCEPTION)).doubleValue();
    }
    
    
    /** For test.
     * @param args the command line arguments
     */
    public static void main(String[] args){
        try {
            //    PubMedQuerier pq = new PubMedQuerier(tool, email);
            PubMedQuerier pq = new PubMedQuerier();
      /*      pq.sendQuery("1.1.1.174", "ecno");
            System.out.println("1.1.1.174: " + pq.getPMIDList().size());
        */    
            pq.sendQuery("external naris");
            System.out.println("external naris: " + pq.getHits());
            
       /*     pq.sendQuery("nose external naris");
            System.out.println("sambo, external naris: " + pq.getHits());*/
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    
}