/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.PRA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MOntology;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import se.liu.ida.PRAalg.dtfPRA;
import se.liu.ida.PRAalg.mgPRA;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.exceptions.UserSessionsDaoException;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.util.SuggestionXmlFileParser;

/**
 *
 * @author Shahab
 */
public class PRA {

    public MOntology monto1 = null, monto2 = null;
    public Vector praSugg = new Vector();
    Vector filteredSugg = new Vector();
    String praSuggestions = "";
    String filteredRemainingSuggestions = "";
    //Properties configFile = new Properties();

    public PRA() throws FileNotFoundException, IOException, UserSessionsDaoException {

        monto1 = Commons.monto1;
        monto2 = Commons.monto2;
        // Suggestions that are in an equivalence relation (=)
        praSugg = GetProcessedSuggestions();
    }

    /*
     *  Filter with PRA (fPRA)
     */
    public void fPRA() {
        praSuggestions = "";
        if (praSugg.size() > 0) {
            Iterator itr_rem = Commons.remainingSuggestionVector.iterator();
            while (itr_rem.hasNext()) {
                String remainingSugg = itr_rem.next().toString();
                Iterator itr_pra = praSugg.iterator();
                while (itr_pra.hasNext()) {
                    String praSugg1 = itr_pra.next().toString();
                    Pattern pattern = Pattern.compile("\\[\\[class:(.*),1\\], \\[class:(.*),2\\], (.*)\\]");
                    Matcher matcher = pattern.matcher(praSugg1);
                    String term1 = "";
                    String term2 = "";
                    if (matcher.find()) {
                        // Parse out term1 from PRA suggestions
                        term1 = matcher.group(1).trim();
                        // Parse out term2 from PRA suggestions
                        term2 = matcher.group(2).trim();
                    }
                    
                    
                    Matcher matcherRemSugg = pattern.matcher(remainingSugg);
                    String term1RemSugg = "";
                    String term2RemSugg = "";
                    if (matcherRemSugg.find()) {
                        // Parse out term1 from PRA suggestions
                        term1RemSugg = matcherRemSugg.group(1).trim();
                        // Parse out term2 from PRA suggestions
                        term2RemSugg = matcherRemSugg.group(2).trim();
                    }
                    //System.out.println("term pair :-"+ term1 + " " +term2);
                    //if (remainingSugg.indexOf(term1) != -1 || remainingSugg.indexOf(term2) != -1) {
                    if (term1RemSugg.equalsIgnoreCase(term1) || 
                            term2RemSugg.equalsIgnoreCase(term2)) {
                        praSuggestions += "<Pair>";
                        praSuggestions += remainingSugg;
                        praSuggestions += "</Pair>";
                    }
                }
            }
        }  

        File file = new File(Commons.SEGMENT + "RA");
        if (!file.exists()) {
            file.mkdir();
        }
        // Generate Filtered Suggestions
        GenerateRemainingSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml", praSuggestions);
        // Filtered PRA suggestion after applying fPRA algorithm.
        
        String remSuggestion = GetRemainingSuggestions();
        if (!remSuggestion.isEmpty()) {
        filteredRemainingSuggestions = RemovePRASuggestionsFromRemainingSuggestions(praSuggestions, remSuggestion);
        // Update Suggestion List in the database
        UpdateUserSuggestions();
        }
    }

    /*
     *  Double Threshold Filter with PRA (dtfPRA)
     */
    public void dtfPRA() {
        //praSuggestions = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>";
        praSuggestions = "";
        Iterator itr_pra;
        if (praSugg.size() > 0) {
            dtfPRA calSugg = new dtfPRA(monto1, monto2, praSugg);
            itr_pra = calSugg.getResults(Commons.remainingSuggestionVector, 0.6, 0.4).iterator();
        } else {
            itr_pra = Commons.remainingSuggestionVector.iterator();
        }
        while (itr_pra.hasNext()) {
            praSuggestions += "<Pair>";
            praSuggestions += itr_pra.next().toString();
            praSuggestions += "</Pair>";
        }

        File file = new File(Commons.SEGMENT + "RA");
        if (!file.exists()) {
            file.mkdir();
        }
        //GenerateRemainingSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml",praSuggestions);
        // Filtered commons between PRA suggestions and Remaining suggestions and save them in database.
        filteredRemainingSuggestions = CommonPRASuggestionsRemainingSuggestions(praSuggestions, GetRemainingSuggestions());
        // Update Suggestion List in the database
        UpdateUserSuggestions();
        // Filter No PRA based suggestions and displayed to the user
        
        String nonPRASuggs=RemoveNonPRASuggestionsFromRemainingSuggestions(praSuggestions, GetRemainingSuggestions());
        
        
//        String nonPRASuggestions = RemoveNonPRASuggestionsFromRemainingSuggestions(praSuggestions, GetRemainingSuggestions()).substring(1);
        
         String nonPRASuggestions="";
         
         if(nonPRASuggs.length()>1)
             nonPRASuggestions=nonPRASuggs.substring(1);
         
         
         
         
        
        GenerateRemainingSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml", nonPRASuggestions);
    }

    /*
     *  Mappable groups and fixing with PRA (mgPRA)
     */
    public void mgPRA() {
        //praSuggestions = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>";
        praSuggestions = "";
        Iterator itr_pra;
        if (praSugg.size() > 0) {
            mgPRA calSugg = new mgPRA(monto1, monto2, praSugg);
            itr_pra = calSugg.getResults(Commons.remainingSuggestionVector).iterator();
        } else {
            itr_pra = Commons.remainingSuggestionVector.iterator();
        }
        while (itr_pra.hasNext()) {
            praSuggestions += "<Pair>";
            praSuggestions += itr_pra.next().toString();
            praSuggestions += "</Pair>";
        }

        File file = new File(Commons.SEGMENT + "RA");
        if (!file.exists()) {
            file.mkdir();
        }
        //GenerateRemainingSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml",praSuggestions);
        // Filtered commons between PRA suggestions and Remaining suggestions and save them in database.
        filteredRemainingSuggestions = CommonPRASuggestionsRemainingSuggestions(praSuggestions, GetRemainingSuggestions());
        // Update Suggestion List in the database
        UpdateUserSuggestions();
        // Filter No PRA based suggestions and displayed to the user
        String remSuggestion = GetRemainingSuggestions();
        
       if (!remSuggestion.isEmpty()) {
        String nonPRASuggestions = RemoveNonPRASuggestionsFromRemainingSuggestions(praSuggestions, remSuggestion).substring(1);
        GenerateRemainingSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml", nonPRASuggestions);
       }
    }

    // Generic function for generating automatic header and footer information for remaining suggestion
    private String GenerateRemainingSuggestionsXML(String filename, String xml) {
        BufferedWriter bufferedWriter = null;
        xml = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>" + xml;
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            //Start writing to the output stream
            if (xml.substring(xml.length() - 12, xml.length()).lastIndexOf("</Pair>") == -1) {
                if (xml.startsWith("<Pair>", 51 /* offset for starting <Pair> tag in xml file*/)) {
                    xml += "</Pair>";
                }
            }
            xml += "</Suggestions>";
            bufferedWriter.write((xml == null ? "" : xml));
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
            }
        }
        return xml;
    }
//        private String GenerateRemainingSuggestionsXML(String filename,String xml)
//    {
//        BufferedWriter bufferedWriter = null;
//        xml = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>" + xml;
//        try {
//            //Construct the BufferedWriter object
//            bufferedWriter = new BufferedWriter(new FileWriter(filename));
//            //Start writing to the output stream
//            xml += "</Suggestions>";
//            bufferedWriter.write((xml == null? "":xml));
//        } catch (FileNotFoundException ex) {
//        } catch (IOException ex) {
//        } finally {
//            //Close the BufferedWriter
//            try {
//                if (bufferedWriter != null) {
//                    bufferedWriter.flush();
//                    bufferedWriter.close();
//                }
//            } catch (IOException ex) {
//            }
//        }
//        return xml;
//    }

    // Update PRA filtered remaining suggestion to database
    private void UpdateUserSuggestions() {
        try {
            UserSessionsDao _dao = UserSessionsDaoFactory.create();
            UserSessionsPk _dpk = new UserSessionsPk(Commons.S_ID);
            UserSessions _dto = _dao.findByPrimaryKey(_dpk);
            _dto.setUserSuggestionsListXml(filteredRemainingSuggestions);
            _dao.update(_dpk, _dto);
        } catch (Exception _e) {
        }
    }

    // Removing PRA suggestions from remaining suggestions to get non PRA suggestions only
    public String RemovePRASuggestionsFromRemainingSuggestions(String praSugg, String remainingSugg) {
        String remain_sugg = "";
        String tempremainingSugg = "";
        try {
            String[] praSugglist = praSugg.split("</Pair>");
            String[] remainingSugglist = remainingSugg.split("</Pair>");
            for (String pra_sugg : praSugglist) {
                for (String remaining_sugg : remainingSugglist) {
                    if (remaining_sugg.trim().equals(pra_sugg.trim())) {
                        continue;
                    }
                    tempremainingSugg += remaining_sugg + "</Pair>";
                }
                remainingSugglist = tempremainingSugg.split("</Pair>");
                remain_sugg = tempremainingSugg;
                tempremainingSugg = "";
            }
        } catch (Exception _e) {
        }

        // Prepare remaining suggestions xml to be saved in the database
        remain_sugg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Suggestions>" + remain_sugg.substring(1, remain_sugg.length() - 7) + "</Suggestions>";

        // LOC: remain_sugg.substring(1,remain_sugg.length()-7)
        // LOC Description: filtered out remaining suggestions that doesnot contain PRA suggestions

        return remain_sugg;
    }

    // Removing Non PRA suggestions from remaining suggestions to get non PRA suggestions only
    public String RemoveNonPRASuggestionsFromRemainingSuggestions(String praSugg, String remainingSugg) {
        String remain_sugg = "";
        
        
        
        
        
        
//        String tempremainingSugg = "";  
//        try {
//            String[] praSugglist = praSugg.split("</Pair>");
//            String[] remainingSugglist = remainingSugg.split("</Pair>");
//            
//            
//            
//            for (String pra_sugg : praSugglist) {
//                for (String remaining_sugg : remainingSugglist) {
//                    if (remaining_sugg.trim().equals(pra_sugg.trim())) {
//                        continue;
//                    }
//                    tempremainingSugg += remaining_sugg + "</Pair>";
//
//                }
//                // Filtered remaining suggestions
//                remainingSugglist = tempremainingSugg.split("</Pair>");
//                remain_sugg = tempremainingSugg;
//                tempremainingSugg = "";
//            }
//            
//            
//            
//        } catch (Exception _e) {
//        }
//        
        
        
        
        
        
        
        
        
        
        ArrayList<String> SuggToBeAdd=new ArrayList();
        
                try {
            String[] praSugglist = praSugg.split("</Pair>");
            String[] remainingSugglist = remainingSugg.split("</Pair>");
            
            
             for(String s:praSugglist)            
                 SuggToBeAdd.add(s);
             
                 
             
             
             for(String s:remainingSugglist)
             {
                 String s2=s.replaceAll("\n", "");
                 s2=s2.replaceAll("\r", "");
                 
                 if(!SuggToBeAdd.contains(s2) && !s2.isEmpty())                  
                 remain_sugg += s + "</Pair>";
                              
             }
            
            
        } catch (Exception _e) {
        }
        
        
        
        
        

        // Prepare remaining suggestions xml to be saved in the database
                
//        if(remain_sugg.substring(remain_sugg.length() - 7, remain_sugg.length()).equalsIgnoreCase(""))        
//        remain_sugg = remain_sugg.substring(1, remain_sugg.length() - 7);

        // LOC: remain_sugg.substring(1,remain_sugg.length()-7)
        // LOC Description: filtered out remaining suggestions that doesnot contain PRA suggestions

        return remain_sugg;
    }
    
    
    

    // Common PRA suggestions and remaining suggestions to get non PRA suggestions only
    public String CommonPRASuggestionsRemainingSuggestions(String praSugg, String remainingSugg) {
        String remain_sugg = "";
        String tempremainingSugg = "";
        
//        try {
//            String[] praSugglist = praSugg.split("</Pair>");
//            String[] remainingSugglist = remainingSugg.split("</Pair>");
//            for (String pra_sugg : praSugglist) {
//                for (String remaining_sugg : remainingSugglist) {
//                    if (remaining_sugg.trim().equals(pra_sugg.trim())) {
//                        tempremainingSugg += remaining_sugg + "</Pair>";
//                        continue;
//                    }
//                }
//            }
//        } catch (Exception _e) {
//        }
        
        
        
        
//        ArrayList<String> SuggToBeAdd=new ArrayList();
        
//        ArrayList<String> RemainingSugg=new ArrayList();
        
        
        
        try {
            String[] praSugglist = praSugg.split("</Pair>");
//            String[] remainingSugglist = remainingSugg.split("</Pair>");
            
            
             for(String s:praSugglist)
             {
                 s="\r\n"+s;
                 tempremainingSugg += s + "</Pair>";
//                 SuggToBeAdd.add(s);
                 
             }
                 
             
             
//             for(String s:remainingSugglist)
//             {
//                 String s2=s.replaceAll("\n", "");
//                 s2=s2.replaceAll("\r", "");
//                 
//                 if(SuggToBeAdd.contains(s2))                  
//                 tempremainingSugg += s + "</Pair>";
//                              
//             }
            
            
        } catch (Exception _e) {
        }
        
        
       
        
        
        
        
        

        // Prepare remaining suggestions xml to be saved in the database
        if (tempremainingSugg.length() <= 9) {
            remain_sugg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Suggestions></Suggestions>";
        } else {
            remain_sugg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Suggestions>" + tempremainingSugg.substring(2) + "</Suggestions>";
        }

        // LOC: remain_sugg.substring(1,remain_sugg.length()-7)
        // LOC Description: filtered out remaining suggestions that doesnot contain PRA suggestions

        return remain_sugg;
    }

    // Get remaining suggestion by read the remaining suggestion xml file
    public String GetRemainingSuggestions() {
        StringWriter stw = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            InputStream inputStream = new FileInputStream(new File(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml"));
            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
            stw = new StringWriter();
            javax.xml.transform.Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));
        } catch (Exception e) {
        }
        if (stw != null && stw.toString().contains("<Suggestions>")) {
        return stw.toString().substring(51, stw.toString().length() - 14);
        }
        
        String emptyList = "";
        
        return emptyList;
    }

    // Read processed suggestions from the database based on the usersession id
    public Vector GetProcessedSuggestions() {
        Vector processedSuggs = null;
        try {
            processedSuggs = new Vector();
            UserSessionsDao _dao = UserSessionsDaoFactory.create();
            UserSessionsPk _dpk = new UserSessionsPk(Commons.S_ID);
            UserSessions _dto = _dao.findByPrimaryKey(_dpk);
            processedSuggs = SuggestionXmlFileParser.getProcessedSuggestions(_dto.getUserHistorylistXml());
        } catch (Exception _e) {
        }
        return processedSuggs;
    }
}
