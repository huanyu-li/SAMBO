/*
 * PubMedFetcher.java
 
 */

package se.liu.ida.sambo.text.query;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
 * Retrive abstracts from Medline given a list of PMIDs
 *
 * @author  He Tan 
 * @version
 *
 */
public class PubMedFetcher{    
  
  private static final int MAXPMIDS = 20;  // Max PMIDs can be sent via a URL.
  private String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed";
  
  private static final int RET_TYPE_ABSTRACT =2;  
    
  private DocumentBuilder dBuilder;                                      //XML document builder
  private Document result;						// Query result.
  private NodeList text;							// List of hits.
  private NodeList notFound;                                             //Error or Warning message.
   
  //private int bufferSize = 2000000;
  private PubMedTimer timer = PubMedTimer.getInstance();
  
 
  public PubMedFetcher() throws ParserConfigurationException{
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      dBuilder = factory.newDocumentBuilder();
  }   
  
  
  /** Create a new instance of PubMedFetcher with a tool name and email address.
   * They are useful for PubMed's statistics, have no effect on fetching abstracts. 
   * @param tool applicaiton name
   * @param email email address
   */
  public PubMedFetcher(String tool, String email)throws ParserConfigurationException{
    this();
    if(tool != null && tool.length() > 0) 
      baseUrl += "&tool=" + tool;
    if(email != null && email.length() > 0)
      baseUrl += "&email=" + email;
  }
  
 
    
   private String prepareQueryURL(Vector PubMedIds){
      
      StringBuffer query = new StringBuffer(baseUrl);
      if(PubMedIds != null && PubMedIds.size()> 0){
          query.append("&id=");
          for(Iterator it = PubMedIds.iterator();it.hasNext();)
              query.append(it.next()).append(",");
          query.deleteCharAt(query.length() - 1); // remove last ","
      }
      
      query.append("&retmode=").append(PubMedConstants.retMode[PubMedConstants.RET_MODE_XML]);
      query.append("&rettype=").append(PubMedConstants.retType[RET_TYPE_ABSTRACT]);
      
      return query.toString();
   }
   
   
   private int doQuery(String queryURL){
       
         synchronized(timer){
          timer.waitForMyTurn();    // Wait for enough interval between 2 trials.
	  try{ // Send the query to and parse response from PubMed
              long sendQueryTime = System.currentTimeMillis();
              result = dBuilder.parse(queryURL);
                            
              if(result != null)
                  timer.setLastQueryTime(sendQueryTime);  // Set new query time.
              }catch(Exception e){
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
      else{// If successful                                    
          NodeList list = result.getElementsByTagName("AbstractText");
	  if(list != null && list.getLength() > 0){
              text = list;
              return PubMedConstants.QUERY_SUCCESS;
          }
          
          return PubMedConstants.QUERY_FAIL;
      }
            
   }
   
   /**Send a query to PubMed.
   * @param queryTerm
   * @return status of the query
   */
  public int sendQuery(Vector PubMedIds){ 
      
      return doQuery(prepareQueryURL(PubMedIds));
  }
  

   public Vector getAbstractText(){
        
      Vector v = new Vector(); 
      
      for(int i = text.getLength(); i> 0; i--){
          NodeList children = text.item(i-1).getChildNodes();
          for(int j = children.getLength(); j> 0; j--)
              if (children.item(j-1).getNodeType() == Node.TEXT_NODE)
                  v.add(children.item(j-1).getNodeValue());
      }            
      return v;
   }
  
 
  
}