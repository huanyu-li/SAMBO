/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.umlsOnline;

import UtsMetathesaurusFinder.UiLabel;
import UtsMetathesaurusFinder.UtsFault_Exception;
import UtsMetathesaurusFinder.UtsWsFinderController;
import UtsMetathesaurusFinder.UtsWsFinderControllerImplService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.UMLSQuery;
import se.liu.ida.sambo.algos.matching.algos.newRajaram.UMLSQueryConstants;
import se.liu.ida.sambo.util.UMLSOnlineSettings;

/**
 * <p>
 * Handles queries for finding UMLS CUIDs , uses UMLS online server.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class QueryCUIDsonline extends UMLSQuery {
    // Path of the UMLS history file.
    private final String historyFile = UMLSOnlineSettings.UMLS_SEARCH_HISTORY;
    // To seperate terms from CUIDs when writing in the history file.
    private final String seperator = "#";
    // UMLS release version that the user wants to use.
    private final String umlsRelease = UMLSOnlineSettings.UMLS_RELEASE;
    // UMLS language that the user wants to use.
    private final String language = UMLSOnlineSettings.UMLS_LANGUAGE;
    private PrintWriter recordWriter = null;
    // Acts as a temporary database and increases performance.
    private HashMap<String, String> history = new HashMap<String, String>();
    // UMLS connection for accessing the server.
    private UmlsConnectionOnline connection = null;
    // To check if the temporary database is needed.
    private final boolean useHistory = true;
    private final boolean useHistoryFile = true;
    private final boolean saveHistoryFile = true;
    private UtsWsFinderController utsFinderService =
            (new UtsWsFinderControllerImplService()
            ).getUtsWsFinderControllerImplPort();
    UtsMetathesaurusFinder.Psf psfMetathesaurusFinder =
            new UtsMetathesaurusFinder.Psf();

    /**
     *<p>
     * This constructor sets the UMLS language to be used, imports data from the
     * history file and stores it in the temporary database (HashMap variable).
     * </p>
     *
     * @param uConnection       UMLS server connection.
     */
    public QueryCUIDsonline(final UmlsConnectionOnline uConnection) {

        connection = uConnection;
        psfMetathesaurusFinder.setIncludedLanguage(language);

        if (useHistoryFile) {
            this.importHistory();
        }

        if (saveHistoryFile) {
            this.setRecordWriter();
        }
    }

    /**
     * <p>
     * This method gets the CUIDs for the given term.
     * </p>
     *
     * @param term              The term to be searched.
     * @param searchLevel       Search levels between 1 and 3.
     *
     * @return  List that contains CUIDs, empty list is returned
     *          if no CUIDs are found.
     */
    public List<String> getCUIDs(final String term,
            final int searchLevel) {

        List<String> cuids = new ArrayList<String>();
        String historyData = "";

        // Looking at the previous query history.
        if (useHistory && history.containsKey(term)) {
            String[] historyCUIDs = history.get(term).split(";");
            cuids = Arrays.asList(historyCUIDs);
            return cuids;
        }

        /**
         * If the term was not searched previously then make a new query to the
         * UMLS server.
         */
        List<UiLabel> uiLabels = queryTerm(term, searchLevel);

        // If the query returns CUIDs
        if (!uiLabels.isEmpty()) {
            for (UiLabel uiLabel:uiLabels) {
                String cuid = uiLabel.getUi();
                cuids.add(cuid);
                historyData += cuid + ";";
            }

            // Writing to the history file
            if (saveHistoryFile && recordWriter != null) {
                recordWriter.println(term + this.seperator + historyData);
                recordWriter.flush();
            }

            // Storing in the local database
            if (useHistory) {
                history.put(term, historyData);
            }
        }

        return cuids;
    }

    /**
     * <p>
     * This method sets a hierarchy for the query.<br>
     *
     * (i.e) If searchLevel 1 returns no CUIDs then the query is repeated with
     * searchLevel 2 and so on upto to maximum searchLevel.
     * </p>
     *
     * @param term              The term to be searched.
     * @param searchLevel       Search searchLevel between 1 and 3.
     *
     * @return uiLabels     List that contains CUIDs, empty List
     *                      is returned if no CUIDs are found.
     */
    private List<UiLabel> queryTerm(final String term, final int searchLevel) {

        List<UiLabel> uiLabels = new ArrayList<UiLabel>();

        // Level 1: querying with the exact string given by the user.
        uiLabels = getUiLabel(term, UMLSQueryConstants.EXACT_SEARCH);

        // Level 2: querying with normalized words of the given term.
        if (uiLabels.isEmpty() && (searchLevel
                > UMLSQueryConstants.EXACT_SEARCH)) {
            uiLabels = getUiLabel(term,
                    UMLSQueryConstants.NORMALIZED_WORDS_SEARCH);
        }

        // Level 3: querying with normalized strings of the given term
        if (uiLabels.isEmpty() && (searchLevel
                > UMLSQueryConstants.NORMALIZED_WORDS_SEARCH)) {
            uiLabels = getUiLabel(term,
                    UMLSQueryConstants.NORMALIZED_STRING_SEARCH);
        }

        return uiLabels;
    }

    /**
     * <p>
     * This method uses UMLS API (UTS) to get CUIDs.
     * </p>
     *
     * @param term              The term to be searched.
     * @param searchLevel       Search searchLevel between 1 and 3.
     *
     * @return uiLabels         List that contains CUIDs, empty List
     *                          is returned if no CUIDs are found.
     */
    private List<UiLabel> getUiLabel(final String term, final int searchLevel) {

        List<UiLabel> uiLabels = new ArrayList<UiLabel>();


        try {
            uiLabels = utsFinderService.findConcepts(
                    connection.getProxyTicket(), umlsRelease, "atom",
                    term, UMLSQueryConstants.SEARCH_TYPE[searchLevel],
                    psfMetathesaurusFinder);
            } catch (UtsFault_Exception ex) {
            Logger.getLogger(QueryCUIDsonline.class.getName()).log(Level.SEVERE,
                    null, ex);
            }

        return uiLabels;
    }

    /**
     *<p>
     * This method imports data from the history file and fills
     * history (HashMap variable) which acts as a temporary database.
     * </p>
     */

    private void importHistory() {

        System.out.println("Importing the result from file : " + historyFile);
        File f = new File(historyFile);
        String line;

        // If no history exists.
        if (!f.exists()) {
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new
                    FileReader(historyFile));
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] keyAndValue = line.split(seperator);
                // Consider the history data which has CUIDs
                if (keyAndValue.length == 2) {
                    history.put(keyAndValue[0], keyAndValue[1]);
                }
            }
            reader.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *<p>
     * This method initializes the history file to write data in it.
     * </p>
     */

    private void setRecordWriter() {

        try {
            String fileDir = historyFile.substring(0,
                    historyFile.lastIndexOf("/"));
            File file = new File(fileDir);
            if (!file.exists()) {
                file.createNewFile();
            }
            this.recordWriter = new PrintWriter(new
                    BufferedWriter(new FileWriter(historyFile, true)));
        } catch (IOException ex) {
            Logger.getLogger(QueryCUIDsonline.class.getName()).log(Level.SEVERE,
                    null, ex);
            recordWriter.close();
        }
    }

    @Override
    public void finalize() {

        if (recordWriter != null) {
            this.recordWriter.close();
        }
    }
}
