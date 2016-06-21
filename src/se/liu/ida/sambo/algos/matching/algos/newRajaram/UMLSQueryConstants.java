/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.newRajaram;

/**
 *
 * @author rajka62
 */
public final class UMLSQueryConstants {
    /**
     * To avoid creating an instance.
     */
    private UMLSQueryConstants() {
    }
    /**
     * Unique integer value for the online server.
     **/
    public static final int ONLINE_SERVER = 0;
    /**
     * Unique integer value for the offline server.
     **/
    public static final int OFFLINE_SERVER = 1;
    /**
     * Unique integer value for the EXACT search for UMLS matcher.
     **/
    public static final int EXACT_SEARCH = 0;
    /**
     * Unique integer value for the NORMALIZED WORDS search for UMLS matcher.
     **/
    public static final int NORMALIZED_WORDS_SEARCH = 1;
    /**
     * Unique integer value for the NORMALIZED STRING search for UMLS matcher.
     **/
    public static final int NORMALIZED_STRING_SEARCH = 2;
    /**
     * No.of available search parameters.
     **/
    public static final int NO_OF_SEARCH_PARMS = 3;
    /**
     * Searching parameter for UMLS server (Note: More parameters are
     * available, see the UMLS page for more information).
     */
    public static final String[] SEARCH_TYPE = {"exact" , "normalizedWords" ,
            "normalizedString"};
}
