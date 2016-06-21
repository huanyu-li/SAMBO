/*
 * PubMedConstants.java
 *
 */

package se.liu.ida.sambo.text.query;

/**
 *
 * @author He Tan
 */
public class PubMedConstants {
    
    /* Query result definition */
    public static final int NOT_FOUND = 0;
    public static final int QUERY_FAIL = -2;
    public static final int CONNECT_FAIL = -3;
    public static final int QUERY_SUCCESS = 1;
    
    public static final int XML_EXCEPTION = -10;
    
    
    public static final boolean ignoreError = false;		// Ignore errors in results.
    public static final boolean ignoreWarning = false;	// Ignore warning in results.
    
    
    
    public static final int RET_MODE_XML =0;         // Retrieval Mode
    public static final String[] retMode = {"xml", "html", "text", "asn.1"};    
    public static final String[] retType = {"count", "uilist", "abstract", "citation", "medline", "full"};
    
}
