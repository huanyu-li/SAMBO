/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import gov.nih.nlm.kss.models.meta.concept.*;
import gov.nih.nlm.kss.query.meta.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.*;

/**
 *
 * @author Administrator
 */
public class UmlsTermQuerier {

    private final String RECORD_FILE = System.getProperty("user.dir") + "/UMLS_Record_File.txt";
    private final String SEPERATOR = "#";
    private PrintWriter record_writer = null;
    private String umlsRelease;
    private String language;
    private HashMap<String, String> map_term;
    private UmlsConnector connector;
    final private boolean useHistory = true;
    final private boolean useSavedHistory = true;
    final private boolean savedHistory = true;

    UmlsTermQuerier(String umlsRelease, String language, UmlsConnector connector) {
        
//        AlgoConstants.UMLS_RECORD=RECORD_FILE;
        this.umlsRelease = umlsRelease;
        this.language = language;
        this.connector = connector;
        this.map_term = new HashMap<String, String>();
        if (useSavedHistory) {
            this.ImportResultFromFile();
        }
        if(savedHistory)
            this.setRecordWriter();
    }

    public String getTerm(String term, int level) {
        System.out.println("    [DEBUG MSG] The CUIs for term: " + term);
        if (useHistory && map_term.containsKey(term)) {
            System.out.println("    [DEBUG MSG] From history: " + map_term.get(term));
            return map_term.get(term);
        }
        String result = "";
        
        
        
        // If concepts not in history this part will be execuate
        
        ConceptIdGroup gp = this.queryTerm(term, level);
        if (gp != null) {
            Object[] contents = gp.getContents();
            for (int i = 0; i < contents.length; i++) {
                ConceptId cid = (ConceptId) contents[i];
                result += cid.getCUI() + ";";
            }
        }
        
        //
        
        
        if (savedHistory && record_writer != null) {
            record_writer.println(term + this.SEPERATOR + result);
            record_writer.flush();
        }
        if (useHistory) {
            map_term.put(term, result); 
        }
        System.out.println("        " + result);
        return result;
    }

    public ConceptIdGroup queryTerm(String term, int level) {
        try {
            //if (map_detail.keySet().contains(term))
            //    return map_detail.get(term);
            ConceptIdGroup group = this.searchByExact(term);
            if ((group == null || group.getContents().length == 0) && (level > 0)) {
                group = this.searchByNormString(term);
            }
            if ((group == null || group.getContents().length == 0) && (level > 1)) {
                group = this.searchByNormWord(term);
            }
            //this.map_detail.put(term, group);
            return group;
        } catch (Exception ex) {
            Logger.getLogger(UmlsTermQuerier.class.getName()).log(Level.SEVERE, null, "UMLS Connection Error");
            this.connector.doConnect();
            return null;
        }
    }

    private ConceptIdGroup searchByExact(String str) throws RemoteException {
        System.out.println("    [DEBUG MSG] searchByExact.");
        ConceptIdExactRequest req = new ConceptIdExactRequest();
        this.configReq(req);
        req.setSearchString(str);
        ConceptIdGroup group = this.connector.getUmlsksService().findCUIByExact(req);
        return group;
    }

    private ConceptIdGroup searchByNormString(String str) throws RemoteException {
        System.out.println("    [DEBUG MSG] searchByNormString.");
        ConceptIdNormStringRequest req = new ConceptIdNormStringRequest();
        this.configReq(req);
        req.setSearchString(str);
        ConceptIdGroup group = this.connector.getUmlsksService().findCUIByNormString(req);
        return group;
    }

    private ConceptIdGroup searchByNormWord(String str) throws RemoteException {
        System.out.println("    [DEBUG MSG] searchByNormWord.");
        ConceptIdNormWordRequest req = new ConceptIdNormWordRequest();
        this.configReq(req);
        req.setSearchString(str);
        ConceptIdGroup group = this.connector.getUmlsksService().findCUIByNormWord(req);
        return group;
    }

    public void printInfor(ConceptIdGroup cidGp) {
        System.out.println("==== ConceptIdGroup Infor ===");
        if (cidGp == null) {
            System.out.println("(null).");
            return;
        }
        Object[] cidContents = cidGp.getContents();
        for (int i = 0; i < cidContents.length; i++) {
            ConceptId cid = (ConceptId) cidContents[i];
            System.out.println("    [CUI]" + cid.getCUI() + "; [CN]" + cid.getCN() + "");
        }
        System.out.println("============================");
    }

    private void configReq(RestrictedSearchStringRequest req) {
        req.setCasTicket(this.connector.getProxyTicket());
        req.setRelease(umlsRelease);
        req.setLanguage(language);
    }

    private void ImportResultFromFile() {
        System.out.println("[DEBUG MSG] In UMLSKS: Import the result from file : " + RECORD_FILE);
        File f = new File(RECORD_FILE);
        if (!f.exists()) {
            System.out.println("[DEBUG MSG] There is no UMLS record file " + RECORD_FILE);
            return;
        }
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(RECORD_FILE));
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] keyAndValue = line.split(SEPERATOR);
                if (keyAndValue.length == 2) {
                    map_term.put(keyAndValue[0], keyAndValue[1]);
                }
            }
            reader.close();
            
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void setRecordWriter() {
        try {
            String fileDir = RECORD_FILE.substring(0, RECORD_FILE.lastIndexOf("/"));
            File dir = new File(fileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            this.record_writer = new PrintWriter(new BufferedWriter(new FileWriter(RECORD_FILE, true)));
        } catch (IOException ex) {
            Logger.getLogger(UmlsTermQuerier.class.getName()).log(Level.SEVERE, null, ex);
            record_writer.close();
        }
    }

    @Override
    public void finalize() {
        //System.out.println("In finalize()");
        if (record_writer != null) {
            this.record_writer.close();
        }
    }
}
