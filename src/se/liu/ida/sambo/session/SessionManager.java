/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.sambo.session;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.Vector;
import java.util.Enumeration;

import se.liu.ida.sambo.Merger.*;
import se.liu.ida.sambo.MModel.*;
import se.liu.ida.sambo.algos.matching.MatchingAlgos;
import se.liu.ida.sambo.util.Pair;
import se.liu.ida.sambo.util.History;
import se.liu.ida.sambo.util.HistoryStack;


/**
 *
 * @author mzk
 */
public class SessionManager {

    private MergeManager merman = new MergeManager();;
    
    Vector historyStack = new Vector();
    

    /**
     *
     * @param filePath
     */
    public void loadSuggestionsFromXML(String filePath){
        
        Commons.XMLSuggestionVector = new Vector();

         try{
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db =dbf.newDocumentBuilder();
            Document doc=db.parse(filePath);

            NodeList sugList = doc.getElementsByTagName("Pair");
            int sugListSize = sugList.getLength();

            for (int i = sugListSize - 1; i >= 0; i--) {
                Element pairElement = (Element) sugList.item(i);
                String pairValue=pairElement.getChildNodes().item(0).getNodeValue();

                //Pair pair = (Pair)pairValue;

                Commons.XMLSuggestionVector.addElement(pairValue);
            }//end for
        }catch (Exception e){
            System.out.println(e);
        }

        //return Commons.XMLSuggestionVector;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public Vector loadProcessedSuggestionsFromXML(String filePath){

        Commons.XMLProcessedSuggestionVector = new Vector();
        
         try{
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db =dbf.newDocumentBuilder();
            Document doc=db.parse(filePath);

             //Document doc = XMLFileHandler.readXMLFile(filePath);
             //Generate the NodeList;
            NodeList sugList = doc.getElementsByTagName("Suggestion");
            //check whether the user exist.
            int size = sugList.getLength();
            Commons.strProcessedSuggestionsPair = new String[size];
            Commons.strProcessedSuggestionsName = new String[size];
            Commons.strProcessedSuggestionsNum = new String[size];
            Commons.strProcessedSuggestionsComment = new String[size];
            Commons.strProcessedSuggestionsAction = new String[size];

            for (int i = 0; i <size; i++) {
                Element pairElement = (Element) sugList.item(i);

                String pairValue = pairElement.getAttribute("pair");

                String nameValue = pairElement.getAttribute("name");

                String numValue = pairElement.getAttribute("num");

                String commentValue = pairElement.getAttribute("comment");

                String actionValue = pairElement.getAttribute("action");

                Commons.XMLProcessedSuggestionVector.addElement(pairValue);
                Commons.strProcessedSuggestionsPair[i] = pairValue;
                if(nameValue.equals("null")){
                    nameValue = "";
                    Commons.strProcessedSuggestionsName[i] = nameValue;
                }else{
                    Commons.strProcessedSuggestionsName[i] = nameValue;
                }
                Commons.strProcessedSuggestionsNum[i] = numValue;
                Commons.strProcessedSuggestionsComment[i] = commentValue;
                Commons.strProcessedSuggestionsAction[i] = actionValue;
            }//end for
        }catch (Exception e){
            System.out.println(e);
        }

        return Commons.XMLProcessedSuggestionVector;
    }



    /**
     *
     * @param filename
     */
    public void getSuggestionsXML(String filename){
        Commons.vList = new Vector();
        
        try{
            Commons.vList = merman.getGeneralSuggestionVector();
            
            int no = Commons.vList.size();
            Commons.strings =new Object[no];
            int i =0;
            for(Object str : Commons.vList) {
                Commons.strings[i++] = str;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.newDocument();

            Element root = document.createElement("Suggestions");
            document.appendChild(root);

            for (int j = 0; j < no; j++){
                Element Rootchild = document.createElement("Pair");
                root.appendChild(Rootchild);
                Text text = document.createTextNode(Commons.strings[j].toString());
                Rootchild.appendChild(text);
            }

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            String xmlString = sw.toString();

            File file = new File(filename);
            BufferedWriter bw = new BufferedWriter
                          (new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(xmlString);
            bw.flush();
            bw.close();

        }catch(Exception e){
                System.out.println(e);
        }
    }

    //ADDED BY ME
    /**
     *
     * @param filename
     */
    public void getHistoryXML(String filename){
        
        try{
            historyStack = merman.getCurrentHistory();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.newDocument();

            Element root = document.createElement("Suggestions");
            document.appendChild(root);

            int i = 0;
            for (Enumeration e = historyStack.elements() ; e.hasMoreElements() ;) {
                Element Rootchild = document.createElement("Pair");
                root.appendChild(Rootchild);
                Text text = document.createTextNode(e.nextElement().toString());
                Rootchild.appendChild(text);
//                System.out.println(e.nextElement());
                i++;
            }


        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        String xmlString = sw.toString();

        File file = new File(filename);
        BufferedWriter bw = new BufferedWriter
                      (new OutputStreamWriter(new FileOutputStream(file)));
        bw.write(xmlString);
        bw.flush();
        bw.close();

        }catch(Exception e)
        {
            System.out.println(e);
        }

    }



    /**
     *
     * @param step
     */
    public void loadRecommendationsFromXML(int step){

        String filePath = new String();
        
        if(step==Constants.STEP_SLOT){
            filePath = Commons.DATA_PATH + "RelationRecommendations.xml";
        }else if(step==Constants.STEP_CLASS){
            filePath = Commons.DATA_PATH + "ConceptRecommendations.xml";
        }
            
            Commons.strRecommendedMatchers.clear();
            Commons.strRecommendedWeight.clear();
            Commons.RecommendedThresholdValue = new String();

         try{
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
            DocumentBuilder db =dbf.newDocumentBuilder();
            Document doc=db.parse(filePath);

            NodeList recList = doc.getElementsByTagName("Recommendation");
            for (int i = recList.getLength() - 1; i >= 0; i--) {
                Element element = (Element) recList.item(i);

                for(int j=0; j<Commons.Matchers_Available.length; j++){
                    String strMN = element.getAttribute("matchername"+j);
                    if(!strMN.trim().isEmpty()){
                        Commons.strRecommendedMatchers.add(strMN);
                    }
                }

                for(int j=0; j<Commons.Matchers_Available.length; j++){
                    String strWV = element.getAttribute("weightvalue"+j);
                    if(!strWV.trim().isEmpty()){
                        Commons.strRecommendedWeight.add(strWV);
                    }
                }//end inner FOR

                Commons.RecommendedThresholdValue = element.getAttribute("thresholdvalue").toString();
            }//end for
        }catch (Exception e){
            System.out.println(e);
        }

    }

}//End Class
