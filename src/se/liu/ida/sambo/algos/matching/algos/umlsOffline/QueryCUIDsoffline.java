/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.umlsOffline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.UMLSQuery;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.UMLSQueryConstants;
import se.liu.ida.sambo.util.UMLSOfflineSettings;

/**
 * <p>
 * Handles queries related to UMLS offline server.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class QueryCUIDsoffline extends UMLSQuery {
    /**
     * MySQL connection.
     */
    private Connection sqlConn = null;
    /**
     * Statments for quering CUIDs.
     */
    private PreparedStatement[] queryStatement = new PreparedStatement
            [UMLSQueryConstants.NO_OF_SEARCH_PARMS];
    /**
     * Statment for quering CUIDs relations.
     */
    private PreparedStatement statementRelation = null;
    /**
     * SQL table for the exact search data.
     */
    private final String exactSearchTable =
            UMLSOfflineSettings.UMLS_TABLE_EXACT;
    /**
     * SQL table for the normalized string search data.
     */
    private final String normStringSearchTable = UMLSOfflineSettings.
            UMLS_TABLE_NORMZ_STRING;
    /**
     * SQL table for the normalized word search data.
     */
    private final String normWordSearchTable = UMLSOfflineSettings.
            UMLS_TABLE_NORMZ_WORD;
    /**
     * SQL table for the CUIDs relations data.
     */
    private final String relationTable =
            UMLSOfflineSettings.UMLS_TABLE_RELATION;
    /**
     * SQL statement for the exact search.
     */
    private String exactSearch = "SELECT DISTINCT CUI FROM "
            + exactSearchTable + " WHERE STR= ?";
    /**
     * SQL statement for the normalized string search.
     */
    private String normalizedStringSearch = "SELECT DISTINCT CUI FROM "
             + normStringSearchTable + " WHERE NSTR= ?";
     /**
      * SQL statement for the normalized word search.
      */
    private String normalizedWordSearch = "SELECT DISTINCT CUI FROM "
             + normWordSearchTable + " WHERE NWD= ?";
     /**
      * SQL statement for the relations search.
      */
    private String relationSearch = "SELECT DISTINCT RELA FROM "
             + relationTable + " WHERE CUI1= ? AND CUI2= ?";

    /**
     *<p>
     * This constructor initializes SQL connection.
     * </p>
     *
     * @param conn  SQl connection.
     */
    public QueryCUIDsoffline(final Connection conn) {

        sqlConn = conn;
        try {
            queryStatement[UMLSQueryConstants.EXACT_SEARCH] = sqlConn.
                    prepareStatement(exactSearch);
            queryStatement[UMLSQueryConstants.NORMALIZED_WORDS_SEARCH]
                    = sqlConn.
                    prepareStatement(normalizedWordSearch);
            queryStatement[UMLSQueryConstants.NORMALIZED_STRING_SEARCH]
                    = sqlConn.
                    prepareStatement(normalizedStringSearch);
            statementRelation = sqlConn.prepareStatement(relationSearch);
        } catch (SQLException ex) {
            Logger.getLogger(QueryCUIDsoffline.class.getName()).log(
                    Level.SEVERE , null , ex);
        }
    }

    /**
     * <p>
     * This method queries the database to get CUIDs for the given term.
     * </p>
     *
     * @param term              The term to be searched.
     * @param searchLevel       Search level between 1 and 3.
     *
     * @return  List of CUIDs, empty list is return
     *          if no CUIDs are found.
     */
    public  List<String> getCUIDs(final String term,
            final int searchLevel) {

        ArrayList<String> cuids = new ArrayList();
        ResultSet results = null;
        String cuid = "";
        try {
            queryStatement[searchLevel].setString(1, term);
            results = queryStatement[searchLevel].executeQuery();

            while (results.next()) {
                cuid = results.getString("CUI");
                cuids.add(cuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UMLSOfflineResourceManager.close(results);
        return cuids;
    }

    /**
     * <p>
     * This method queries the database to get the relations between two given
     * CUIDs.
     * </p>
     *
     * @param cuid1     First CUID.
     * @param cuid2     Second CUID.
     *
     * @return  List of relations, empty list is return
     *          if no relations are found.
     */
    public List<String> queryRelation(final String cuid1,
            final String cuid2) {

        ArrayList<String> relations = new ArrayList();
        ResultSet results = null;
        String relation = "";
        try {
            statementRelation.setString(1, cuid1);
            statementRelation.setString(2, cuid2);
            results = statementRelation.executeQuery();

            while (results.next()) {
                relation = results.getString("RELA");
                relations.add(relation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UMLSOfflineResourceManager.close(results);
        return relations;
    }
}
