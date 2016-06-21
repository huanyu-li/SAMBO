/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.util;

/**
 * <p>
 * Handles various system settings like user directory, path of
 * configuration files etc.
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public abstract class SystemSettings {

    /**
     * To avoid creating an instance.
     */
    protected SystemSettings() {
    }
    /**
     * User directory.
     */
    protected static final String USER_DIR = System.getProperty("user.dir");
    /**
     * Configuration directory path.
     */
    protected static final String CONFIG = USER_DIR.concat("/config");
}
