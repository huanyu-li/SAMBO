/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.Matcher;
import se.liu.ida.sambo.algos.matching.algos.umlsOffline.QueryCUIDsoffline;
import se.liu.ida.sambo.algos.matching.algos.umlsOffline.UMLSOfflineResourceManager;
import se.liu.ida.sambo.algos.matching.algos.umlsOnline.QueryCUIDsonline;
import se.liu.ida.sambo.algos.matching.algos.umlsOnline.UmlsConnectionOnline;

/**
 * <p>
 * Find similarity (probability of match) between the strings by using the UMLS
 * Metathesaurus.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public class Umls extends Matcher {

    private UMLSQuery termQuery;

    /**
     *<p>
     * This constructor establishes a connection to access the UMLS service.
     * </p>
     *
     * @param serverType    Which UMLS server(online/offine) user want to used.
     */
    public Umls(final int serverType) {

        switch(serverType) {

            case UMLSQueryConstants.ONLINE_SERVER:
                System.out.println("Trying to connect UMLS"
                        + " online server......");
                try {
                    UmlsConnectionOnline connector = new UmlsConnectionOnline();
                    termQuery = new QueryCUIDsonline(connector);
                } catch (Exception ex) {
                    Logger.getLogger(Umls.class.getName()).log(Level.SEVERE,
                        null, ex);
                    System.out.println("Problem with connecting UMLS online"
                        + " server!");
                }
                break;
            case UMLSQueryConstants.OFFLINE_SERVER:
                System.out.println("Trying to connect UMLS"
                        + " offine server......");
                Connection sqlConn = null;
                try {
                    sqlConn = UMLSOfflineResourceManager.getConnection();
                    termQuery = new QueryCUIDsoffline(sqlConn);
                } catch (SQLException ex) {
                    Logger.getLogger(Umls.class.getName()).log(Level.SEVERE,
                        null, ex);
                    System.out.println("Problem with connecting UMLS offine"
                        + " server!");
                }
                break;
            default:
                System.out.println("Invalid server type!");
        }
    }

    /**
     * <p>
     * Search for the UMLS CUID of two given terms, if the terms
     * share a CUID then these terms are likely to be an equivalent.
     * </p>
     *
     * @param term1        The first string.
     * @param term2        The second string.
     *
     * @return true if term1 and term2 share a CUID, else return false.
     */
    public final boolean querySimRelation(final String term1,
            final String term2) {

        List<String> cuidsTerm1 = new ArrayList<String>();
        List<String> cuidsTerm2 = new ArrayList<String>();


        // Get list of CUIDs for the input terms
        cuidsTerm1 = termQuery.getCUIDs(term1,
                UMLSQueryConstants.NORMALIZED_STRING_SEARCH);
        cuidsTerm2 = termQuery.getCUIDs(term2,
                UMLSQueryConstants.NORMALIZED_STRING_SEARCH);

        /** 
         * If no CUIDs are found then the terms are not mapped in the UMLS 
         * so return false in that case.
         */
        if (cuidsTerm1.isEmpty() || cuidsTerm2.isEmpty()) {
            return false;
        }

        // Comparing CUIDs list of the terms.
        for (String cuid1:cuidsTerm1) {
            if (cuidsTerm2.contains(cuid1)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>
     * This method calculates the similarity between the two given terms,
     * by using the UMLS Metathesaurus.
     * </p>
     *
     * @param term1     The first string.
     * @param term2     The second string.
     *
     * @return similarity value between the two given strings.
     */
    @Override
    public final double getSimValue(final String term1, final String term2) {
        if (querySimRelation(term1, term2)) {
            return MatcherConstants.MAX_SIMVALUE;
        } else {
            return MatcherConstants.MIN_SIMVALUE;
        }
    }

}
