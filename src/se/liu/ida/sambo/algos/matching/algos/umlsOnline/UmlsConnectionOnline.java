/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.umlsOnline;


import UtsSecurity.UtsWsSecurityController;
import UtsSecurity.UtsWsSecurityControllerImplService;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.util.UMLSOnlineSettings;

/**
 * <p>
 * Establish a connection with the UMLS online server for
 * the purpose of querying.
 * You need a UMLS License to access its online server(
 * <a href="https://uts.nlm.nih.gov//license.html">Available here</a>).
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public class UmlsConnectionOnline {

    private final String serviceName = UMLSOnlineSettings.UMLS_SERVICE;
    // UMLS username to access its online server.
    private final String username = UMLSOnlineSettings.UMLS_USERNAME;
    // UMLS password to access its online server.
    private final String password = UMLSOnlineSettings.UMLS_PASSWORD;
    private UtsWsSecurityController securityService;
    private String ticketGrantingTicket;

    /**
     *<p>
     * This constructor initializes the UMLS service and generates a ticket to
     * access the online UMLS server.
     * </p>
     */
    public UmlsConnectionOnline() {

        //initialize the security service.
        securityService = (new UtsWsSecurityControllerImplService()
                ).getUtsWsSecurityControllerImplPort();

        try {
            //get the Proxy Grant Ticket.
            ticketGrantingTicket = securityService.getProxyGrantTicket(
                    username, password);
            System.out.println("Connection established.");
        } catch (UtsSecurity.UtsFault_Exception ex) {
            Logger.getLogger(UmlsConnectionOnline.class.getName()).log(
                    Level.SEVERE, null, ex);
            }
    }

    /**
     *<p>
     * This methods gets a proxy ticket to access online UMLS server.
     * </p>
     *
     * @return      Ticket (String), returns null if the ticket is not granted.
     *
     */
    public final String getProxyTicket() {
        try {
            return securityService.getProxyTicket(ticketGrantingTicket,
                    serviceName);
        } catch (UtsSecurity.UtsFault_Exception ex) {
            Logger.getLogger(UmlsConnectionOnline.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    return null;
    }
}
