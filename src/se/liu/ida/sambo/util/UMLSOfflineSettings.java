/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

/**
 * <p>
 * Handles various MySQL and UMLS local server settings like username, password,
 * configuration files etc.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class UMLSOfflineSettings extends SystemSettings {

    /**
     * To avoid creating an instance.
     */
    private UMLSOfflineSettings() {
    }
    /**
     * UMLS offline Configuration file path.
     */
    private static final String UMLS_OFFINE_CONFIG = CONFIG.concat(
            "/umlsOffline.xml");
    /**
     * To parse XML file.
     */
    private static XmlFileParser xmlInfo = new XmlFileParser(
            UMLS_OFFINE_CONFIG);
    /**
     * Username for accessing the Mysql server.
     */
    public static final String MYSQL_USERNAME = xmlInfo.getValueOf(
            "mysql_username");
    /**
     * Password for accessing the Mysql server.
     */
    public static final String MYSQL_PASSWORD = xmlInfo.getValueOf(
            "mysql_password");
    /**
     * URL of Mysql server.
     */
    public static final String MYSQL_SERVER = xmlInfo.getValueOf(
            "mysql_serverUrl");
    /**
     * UMLS Data Base name in MySQL server.
     */
    private static final String UMLS_DBNAME = xmlInfo.getValueOf(
            "umls_DBName");
    /**
     * MySQL table which contains exact search's data.
     */
    public static final String UMLS_TABLE_EXACT = UMLS_DBNAME.concat(".")
            .concat(xmlInfo.getValueOf("umls_exact"));
    /**
     * MySQL table which contains normalized string search's data.
     */
    public static final String UMLS_TABLE_NORMZ_STRING = UMLS_DBNAME.concat(".")
            .concat(xmlInfo.getValueOf("umls_normString"));
    /**
     * MySQL table which contains normalized word search's data.
     */
    public static final String UMLS_TABLE_NORMZ_WORD = UMLS_DBNAME.concat(".")
            .concat(xmlInfo.getValueOf("umls_normWord"));
    /**
     * MySQL table which contains CUIDS relation data.
     */
    public static final String UMLS_TABLE_RELATION = UMLS_DBNAME.concat(".")
            .concat(xmlInfo.getValueOf("umls_relation"));
}
