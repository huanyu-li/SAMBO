/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

/**
 * <p>
 * Handles various UMLS online server settings like username, password,
 * configuration files etc.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class UMLSOnlineSettings extends SystemSettings {

    /**
     * To avoid creating an instance.
     */
    private UMLSOnlineSettings() {
    }
    /**
     * UMLS history file path.
     */
    public static final String UMLS_SEARCH_HISTORY = USER_DIR.concat(
            "/data/umlsSearchHistory.txt");
    /**
     * UMLS online Configuration file path.
     */
    private static final String UMLS_ONLINE_CONFIG = CONFIG.concat(
            "/umlsOnline.xml");
    /**
     * To parse XML file.
     */
    private static XmlFileParser xmlInfo = new XmlFileParser(
            UMLS_ONLINE_CONFIG);
    /**
     * Username for accessing the UMLS service.
     */
    public static final String UMLS_USERNAME = xmlInfo.getValueOf(
            "umls_username");
    /**
     * Password for accessing the UMLS service.
     */
    public static final String UMLS_PASSWORD = xmlInfo.getValueOf(
            "umls_password");
    /**
     * UMLS Release.
     */
    public static final String UMLS_RELEASE = xmlInfo.getValueOf(
            "umls_release");
    /**
     * UMLS language.
     */
    public static final String UMLS_LANGUAGE = xmlInfo.getValueOf(
            "umls_language");
    /**
     * UMLS service address.
     */
    public static final String UMLS_SERVICE = xmlInfo.getValueOf(
            "umls_serviceName");
}
