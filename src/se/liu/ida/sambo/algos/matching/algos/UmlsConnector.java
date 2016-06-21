/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos;

import gov.nih.nlm.kss.UMLSKSServiceLocator;
import gov.nih.nlm.kss.UMLSKSServicePortType;
import gov.nih.nlm.umlsks.authorization.AuthorizationPortType;
import gov.nih.nlm.umlsks.authorization.AuthorizationPortTypeServiceLocator;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 * @author Qiang Liu
 */
public class UmlsConnector {

    private static Logger logger = Logger.getLogger(UmlsConnector.class.getName());
    private final String ksHost = "http://umlsks.nlm.nih.gov";
    //private final String username = "alex_liu05";
    //private final String password = "blackman";
    private final String username = "samboSB";
    private final String password = "sambo2013!";
    private UMLSKSServicePortType umlsksService;
    private String proxyTicket;
    private int connCount;

    public static void main(String[] args) {
        UmlsConnector c = new UmlsConnector();
    }

    /**
     *
     */
    public UmlsConnector() {
        connCount = 1;
        this.doConnect();
    }

    String getProxyTicket() {
        try {
// 1.1  Locate the authentication web service
            URL authURL = new URL("https://uts-ws.nlm.nih.gov/authorization/services/AuthorizationPort");
            AuthorizationPortType authPortType =
                    new AuthorizationPortTypeServiceLocator().getAuthorizationPort(authURL);

            // 1.2  Obtain a proxy granting ticket
            String pgt = authPortType.getProxyGrantTicket(username, password);

            // 1.3  Obtain a single-use ticket
            proxyTicket = authPortType.getProxyTicket(pgt, ksHost);
            return this.proxyTicket;
        } catch (Exception ex) {
            logger.warning(ex.toString());
            return null;
        }
    }

    UMLSKSServicePortType getUmlsksService() {
        return this.umlsksService;
    }

    /**
     *
     */
    public void doConnect() {
        try {
            logger.finer("Connect to UMLS Server. Time : " + this.connCount++);
            //this.getProxyTicket();

            // 2.1  Properties for UMLSKS web service
            String ksURI = "https://uts-ws.nlm.nih.gov/UMLSKS/services/UMLSKSService";

            // 2.2  Locate the UMLSKS web service
            URL ksURL = new URL(ksURI);
            umlsksService = new UMLSKSServiceLocator().getUMLSKSServicePort(ksURL);
            logger.finer("UMLS Connection established.");
        } catch (Exception ex) {
            logger.warning(ex.toString());
            this.doConnect();
        }
    }
}
