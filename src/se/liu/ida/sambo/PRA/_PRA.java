/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.sambo.PRA;

import com.hp.hpl.jena.sparql.algebra.Transformer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Vector;
import se.liu.ida.sambo.MModel.MOntology;
import se.liu.ida.sambo.Merger.OntManager;
import se.liu.ida.PRAalg.util.ConsistentChecker;
import se.liu.ida.PRAalg.util.MappableChecker;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import se.liu.ida.PRAalg.dtfPRA;
import se.liu.ida.PRAalg.fPRA;
import se.liu.ida.PRAalg.mgPRA;
import se.liu.ida.sambo.Merger.Constants;
import se.liu.ida.sambo.MModel.MClass;
import se.liu.ida.sambo.Merger.MergeManager;
import se.liu.ida.sambo.algos.matching.MatchingAlgos;
import se.liu.ida.sambo.component.loader.PRALoader;
import se.liu.ida.sambo.dao.UserSessionsDao;
import se.liu.ida.sambo.dto.UserSessions;
import se.liu.ida.sambo.dto.UserSessionsPk;
import se.liu.ida.sambo.exceptions.UserSessionsDaoException;
import se.liu.ida.sambo.factory.UserSessionsDaoFactory;
import se.liu.ida.sambo.session.Commons;
import se.liu.ida.sambo.ui.SettingsInfo;
import se.liu.ida.sambo.util.Pair;

/**
 *
 * @author Shahab
 */
public class _PRA {
    MOntology monto1=null,monto2=null;
    Vector praSugg = new Vector();
    Vector filteredSugg = new Vector();
    String PRASuggestionsInString = "";
    String filteredRemainingSuggestions = "";
    //Properties configFile = new Properties();
    
    public _PRA() throws FileNotFoundException, IOException, UserSessionsDaoException{
       
        monto1 = Commons.monto1;
        monto2 = Commons.monto2;
        // Suggestions that are in an equivalence relation (=)
        praSugg = GetProcessedSuggestions();
    }

    /*
     *  Filter with PRA (fPRA)
     */
    public void fPRA()
    {
        PRASuggestionsInString = "";
        if(praSugg.size() > 0)
        {
            Iterator itr_rem = Commons.remainingSuggestionVector.iterator();
            while(itr_rem.hasNext())
            {
                String remainingSugg = itr_rem.next().toString();
                Iterator itr_pra = praSugg.iterator();
                while(itr_pra.hasNext())
                {
                    String praSugg = itr_pra.next().toString();
                    // Parse out term 1 from PRA suggestions
                    //String term1 = praSugg.substring(8,18);
                    // Parse out term 2 from PRA suggestions
                    //String term2 = praSugg.substring(30,praSugg.indexOf("2],"));
                    String term1 = praSugg.substring(8,praSugg.indexOf(",1]"));
                    String term2 = praSugg.substring(praSugg.indexOf(", [class:")+9,praSugg.indexOf(",2],"));

                    // Filter out the suggestions from remaining suggestions either
                    // term 1 matches within the termpair of remaining suggestions or
                    // term 2 matches within the termpair of remaining suggestions
                    System.out.println("term pair :-"+ term1 + " " +term2);
                    if(remainingSugg.indexOf(term1) != -1 || remainingSugg.indexOf(term2) != -1)
                    {
                        PRASuggestionsInString += "<Pair>";
                        PRASuggestionsInString += remainingSugg;
                        PRASuggestionsInString += "</Pair>";
                        System.out.println("testing"+remainingSugg);
                        break;
                    }

                }
            }
        }

        File file = new File(Commons.SEGMENT +"RA");
            if(!file.exists())
                file.mkdir();
        // Generate Filtered Suggestions
        GenerateSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml",PRASuggestionsInString);
            //GenerateXmlFileFromDb(Commons.SEGMENT + "RA/RemainingSuggestions.xml",_dto.getUserSuggestionsListXml());
        
        // Filtered PRA suggestion after applying fPRA algorithm.
        filteredRemainingSuggestions = FilterFromRemainingSuggestions(PRASuggestionsInString,ConvertXMLFileToString());
        // Update Suggestion List in the database
        UpdateUserSuggestions();
    }

    private void GenerateSuggestionsXML(String filename,String xml){
        BufferedWriter bufferedWriter = null;
        xml = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>" + xml;
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            //Start writing to the output stream
            xml += "</Suggestions>";
            bufferedWriter.write((xml == null? "":xml));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void GenerateSuggestionsXMLNoTag(String filename,String xml){
        BufferedWriter bufferedWriter = null;

        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            //Start writing to the output stream
            
            bufferedWriter.write((xml == null? "":xml));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void UpdateUserSuggestions()
    {
        try
        {
            UserSessionsDao _dao = getUserSessionsDao();
            UserSessionsPk _dpk = new UserSessionsPk(Commons.S_ID);
            UserSessions _dto = _dao.findByPrimaryKey(_dpk);
            _dto.setUserSuggestionsListXml(filteredRemainingSuggestions);
            _dao.update(_dpk, _dto);
        }
        catch(Exception _e)
        {
            _e.printStackTrace();
        }
    }

    /*
     *  Double Threshold Filter with PRA (dtfPRA)
     */
    public void dtfPRA()
    {
        //PRASuggestionsInString = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>";
        PRASuggestionsInString = "";
        if(praSugg.size() > 0)
        {
            dtfPRA calSugg = new dtfPRA(monto1, monto2, praSugg);
            //Iterator itr_rem = Commons.remainingSuggestionVector.iterator();
            //while(itr_rem.hasNext())
            //for(Object r: Commons.remainingSuggestionVector)
            {
               // String remainingSugg = itr_rem.next().toString();
                Iterator itr_pra = calSugg.getResults(Commons.remainingSuggestionVector, 0.6, 0.4).iterator();
                while(itr_pra.hasNext()){
                    PRASuggestionsInString += "<Pair>";
                    PRASuggestionsInString += itr_pra.next().toString();
                    PRASuggestionsInString += "</Pair>";
                }
            }
        }

        File file = new File(Commons.SEGMENT +"RA");
            if(!file.exists())
                file.mkdir();
        GenerateSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml",PRASuggestionsInString);
        // Filtered PRA suggestion after applying dtfPRA algorithm.
        filteredRemainingSuggestions = FilterFromRemainingSuggestions(PRASuggestionsInString,ConvertXMLFileToString());
        // Update Suggestion List in the database
        UpdateUserSuggestions();
    }

    /*
     *  Mappable groups and fixing with PRA (mgPRA)
     */
    public void mgPRA()
    {
        //PRASuggestionsInString = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>";
        PRASuggestionsInString = "";
        if(praSugg.size() > 0)
        {
            mgPRA calSugg = new mgPRA(monto1, monto2, praSugg);
            {
                Iterator itr_pra = calSugg.getResults().iterator();
                while(itr_pra.hasNext())
                {
                  
                    PRASuggestionsInString += "<Pair>";
                    PRASuggestionsInString += itr_pra.next().toString();
                    PRASuggestionsInString += "</Pair>";
                }
            }
        }

        File file = new File(Commons.SEGMENT +"RA");
            if(!file.exists())
                file.mkdir();
        GenerateSuggestionsXML(Commons.SEGMENT + "RA/RemainingSuggestions.xml",PRASuggestionsInString);
        // Filtered PRA suggestion after applying mgPRA algorithm.
        filteredRemainingSuggestions = FilterFromRemainingSuggestions(PRASuggestionsInString,ConvertXMLFileToString());
        // Update Suggestion List in the database
        UpdateUserSuggestions();
    }


    public void RemoveLineFromContent(String lineContent)
    {
        try
        {
            File inputFile = new File("myFile.txt");
            File tempFile = new File("myTempFile.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String lineToRemove = "bbb";
            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine);
            }

            boolean successful = tempFile.renameTo(inputFile);

        }
        catch(Exception _e)
        {
            _e.printStackTrace();
        }
    }

    public String FilterFromRemainingSuggestions(String PRASugsInString,String remainingSugsXMLFile)
    {
        try
        {
            
            String[] sets = PRASugsInString.split("</Pair>");
            //input file
            GenerateSuggestionsXMLNoTag(Commons.SEGMENT + "RA/PRARemainingSuggestions.xml",remainingSugsXMLFile);
            // temp file which is empty
            GenerateSuggestionsXML(Commons.SEGMENT + "RA/TempRemainingSuggestions.xml","");
            if(!sets[0].toString().equals(""))
            {
                for (int n = 0; n < sets.length; n++)
                {
                   int start = remainingSugsXMLFile.indexOf(sets[n] + "</Pair>");
                   int end = start + sets[n].length() + 7;

                   System.out.println(start + " --- " + end);

                   String lineToRemove = remainingSugsXMLFile.substring(start, end);
                   String currentLine;
                   File inputFile = new File(Commons.SEGMENT + "RA/PRARemainingSuggestions.xml");
                   File tempFile = new File(Commons.SEGMENT + "RA/TempRemainingSuggestions.xml");
                   BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                   BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                   while((currentLine = reader.readLine()) != null)
                   {
                        // trim newline when comparing with lineToRemove
                        String trimmedLine = currentLine.trim();
                        if(trimmedLine.equals(lineToRemove))
                            continue;
                        writer.write(currentLine);
                        writer.newLine();
                    }
                    writer.close();
                    reader.close();
                    //GenerateXmlFileFromString(Commons.SEGMENT + "RA/PRARemainingSuggestions.xml",ConvertXMLFileToString(Commons.SEGMENT + "RA/TempRemainingSuggestions.xml"));
                    inputFile.delete();
                    boolean successful = tempFile.renameTo(inputFile);

                   // Generate an XML that filtered out the terms in termpair of remaining suggestions.
                   //xml = replace(xml, xml.substring(start, end),start,end, "");

                }

            }
            
        }
        catch(Exception _e)
        {
            _e.printStackTrace();
        }
        
        //xml = "<?xml version='1.0' encoding='UTF-8'?><Suggestions>" + xml;
        //return xml;

        return ConvertXMLFileToString(Commons.SEGMENT + "RA/PRARemainingSuggestions.xml");


    }

    public void GenerateXmlFileFromString(String filename,String xml){
    BufferedWriter bufferedWriter = null;
    try {
        //Construct the BufferedWriter object
        bufferedWriter = new BufferedWriter(new FileWriter(filename));
        //Start writing to the output stream
        bufferedWriter.write((xml == null? "":xml));
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    } finally {
        //Close the BufferedWriter
        try {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

    static String replace(String str, String pattern,int s,int e, String replace)
    {
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }


    public String ConvertXMLFileToString(String filename)
    {
        StringWriter stw = null;
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            InputStream inputStream = new FileInputStream(new File(filename));
            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
            stw = new StringWriter();
            javax.xml.transform.Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stw.toString();
    }

    public String ConvertXMLFileToString()
    {
        StringWriter stw = null;
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            InputStream inputStream = new FileInputStream(new File(Commons.DATA_PATH + Commons.USER_NAME + "_SuggestionList.xml"));
            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
            stw = new StringWriter();
            javax.xml.transform.Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return stw.toString();
    }

   
    public Vector GetProcessedSuggestions()
    {
        Vector praSugg = null;
        try
        {
            praSugg = new Vector();
            UserSessionsDao _dao = getUserSessionsDao();
            UserSessionsPk _dpk = new UserSessionsPk(Commons.S_ID);
            UserSessions _dto = _dao.findByPrimaryKey(_dpk);
            praSugg = ParseProcessedSuggestion(_dto.getUserHistorylistXml());
        }
        catch(Exception _e)
        {
            _e.printStackTrace();
        }
        return praSugg;
    }
    
    private Vector ParseProcessedSuggestion(String stringToSearch){
        Vector praSugg = new Vector();
        int intIndex = stringToSearch.indexOf("<Suggestion");
        if(intIndex == - 1){
            System.out.println("Suggestion not found");
        }
        else
        {
            int lastIndex = stringToSearch.lastIndexOf("</ProcessedSuggestions>");
            stringToSearch = stringToSearch.substring(intIndex, lastIndex);// == null?"":stringToSearch.substring(intIndex, lastIndex);
            String[] subString = stringToSearch.split("/>");
            for(String s: subString){
                /*action = 0 : means rejected suggestions*/
                if(Integer.parseInt(s.substring(s.length()-2,s.length()-1)) != 0)
                {
                    s = s.substring(s.indexOf("[["), s.indexOf("]\""))+"]]";
                    String[] object = s.split("], ");
                    object[0] = object[0].toString().substring(1, object[0].toString().length()) + "]";
                    object[0] = object[0].toString().substring(7, object[0].toString().length() - 3);

                    object[1] = object[1].toString().substring(0, object[1].toString().length()-1);
                    object[1] = object[1].toString().substring(7, object[1].toString().length() - 1);

                    praSugg.add(new Pair(monto1.getClass(object[0]),monto2.getClass(object[1])));
                }
            }
        }
        return praSugg;
    }

    /**
     * Method 'getUserSessionsDao'
     *
     * @return UserSessionsDao
     */

     public static UserSessionsDao getUserSessionsDao()
     {
        return UserSessionsDaoFactory.create();
     }

    public void GenerateXmlFileFromDb(String filename,String xml){
        BufferedWriter bufferedWriter = null;
        try {
            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(filename));
            //Start writing to the output stream
            bufferedWriter.write((xml == null? "":xml));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
