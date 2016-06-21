/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import gov.nih.nlm.kss.models.meta.concept.*;
import gov.nih.nlm.kss.models.meta.context.*;
import gov.nih.nlm.kss.query.meta.ConceptRequest;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class UmlsContextQuerier {
    private String umlsRelease;
    private String language;
    private UmlsConnector connector;
    private HashMap<String, String> map_anc;
    private HashMap<String, String> map_chd;

    public UmlsContextQuerier(String umlsRelease, String language,  UmlsConnector connector) {
        this.umlsRelease = umlsRelease;
        this.language = language;
        this.connector = connector;
        this.map_anc = new HashMap<String, String>();
        this.map_chd = new HashMap<String, String>();
    }

    public String getAncContext(String cui){
        System.out.println("    [DEBUG MSG] Query the ancestor of CUI: " + cui );
        if (map_anc.containsKey(cui))
            return map_anc.get(cui);
        String result = this.getContext(cui, true);
        map_anc.put(cui, result);
        System.out.println("     " + result);
        return result;
    }

    public String getChdContext(String cui){
        System.out.println("    [DEBUG MSG] Query the children of CUI: " + cui);
        if (map_chd.containsKey(cui))
            return map_chd.get(cui);
        String result = this.getContext(cui, false);
        map_chd.put(cui, result);
        System.out.println("     " + result);
        return result;
    }

    private String getContext(String cui, boolean ifAnc){
        String result = "";
        ContextGroup cxtGp = this.queryContext(cui, ifAnc);
        if (cxtGp == null)
            return result;
        Object[] cxtContents = cxtGp.getContents();
        //System.out.println("    cxtContents.length:  " + cxtContents.length);
        for (int i=0; i<cxtContents.length; i++) {
            Context cxt = (Context) cxtContents[i];
            StringCxt[] strCxt = cxt.getCXT();
            //System.out.println("        strCxt.length:  " + strCxt.length);
            for (int j=0; j<strCxt.length; j++)
            {
                SourceCxt[] srcCxt = strCxt[j].getCXT();
                //System.out.println("            srcCxt.length:  " + srcCxt.length);
                for (int k=0; k<srcCxt.length; k++){
                    if (ifAnc) {
                        CxtMember[] anc = srcCxt[k].getANC();
                        //System.out.println("                anc.length:  " + anc.length);
                        for (int m = 0; m < anc.length; m++) {
                            String strAnc = anc[m].getCUI2();
                            if (!result.contains(strAnc)) {
                                result += strAnc + ";";
                            }
                        }
                    } else {
                        CxtMember[] chd = srcCxt[k].getCHD();
                        //System.out.println("                chd.length:  " + chd.length);
                        for (int m = 0; m < chd.length; m++) {
                            String strChd = chd[m].getCUI2();
                            if (!result.contains(strChd)) {
                                result += strChd + ";";
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public ContextGroup queryContext(String cui, boolean ifAnc) {
        try {
            // query the server to get the concept group
            ConceptRequest req = new ConceptRequest();
            this.configReq(req, ifAnc);
            req.setCUI(cui);
            ConceptGroup cptgp = this.connector.getUmlsksService().getConceptProperties(req);
            // get the context and return
            Object[] cptContents = cptgp.getContents();
            if (cptContents.length != 1) {
                System.err.println("[DEBUG MSG] Something wrong with the cptContents returned from server.");
                System.err.println("            The length of cptContents : " + cptContents.length);
                System.exit(0);
            }
            Concept cpt = (Concept) cptContents[0];
            ContextGroup cxts = cpt.getCXTs();
            return cxts;
        } catch (RemoteException ex) {
            Logger.getLogger(UMLSKSearch_V6.class.getName()).log(Level.SEVERE, null, ex);
            this.connector.doConnect();
            return null;
        }
    }

    public void printInfor(ContextGroup cxtGroup) {
        System.out.println("=== ConceptGroup Infor =====");
        if (cxtGroup == null) {
            System.out.println("(null).");
            return;
        }
        Object[] cxtContents = cxtGroup.getContents();
        for (int i=0; i<cxtContents.length; i++) {
            Context cxt = (Context) cxtContents[i];
            StringCxt[] strCxt = cxt.getCXT();
            for (int j=0; j<strCxt.length; j++)
            {
                SourceCxt[] srcCxt = strCxt[j].getCXT();
                for (int k=0; k<srcCxt.length; k++){
                    CxtMember[] anc = srcCxt[k].getANC();
                    for (int m=0; m<anc.length; m++){
                       //System.out.println("     ====== ANC Infor =========");
                        System.out.println("<ANC> [CUI]" + anc[m].getCUI2() + "; [CXS]" + anc[m].getCXS() + "; [RANK]" + anc[m].getRank());
                        //System.out.println("     ==========================");
                    }
                    System.out.println("==========================");
                    CxtMember[] chd = srcCxt[k].getCHD();
                    for (int m=0; m<chd.length; m++){
                        //System.out.println("     ====== CHD Infor =========");
                        System.out.println("<CHD> [CUI]" + chd[m].getCUI2() + "; [CXS]" + chd[m].getCXS() + "; [RANK]" + chd[m].getRank());
                        //System.out.println("     ==========================");
                    }
                    System.out.println("==========================");
                }
            }
        }
        System.out.println("============================");
    }

    private void configReq(ConceptRequest req, boolean ifAnc) {
        req.setCasTicket(this.connector.getProxyTicket());
        req.setRelease(umlsRelease);
        req.setLanguage(language);
        req.setIncludeContexts(true);
        if (ifAnc)
            req.setContextTypes(new String[]{"ANC"});
        else
            req.setContextTypes(new String[]{"CHD"});
        //req.setContextTypes(new String[]{"ANC", "CHD"});
        //req.setIncludeRelations(true);
        //req.setIncludeConceptAttrs(true);
        //req.setIncludeTerminology(true);
        //req.setIncludeRelationshipAttrs(true);
        //req.setIncludeDefinitions(true);
        //req.setIncludeSemanticGroups(true);
        //req.setIncludeCooccurrences(true);
    }
}
