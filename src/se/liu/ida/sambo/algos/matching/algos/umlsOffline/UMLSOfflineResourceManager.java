/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.liu.ida.sambo.algos.matching.algos.umlsOffline;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;
import com.mysql.jdbc.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * <p>
 * Establish a connection with the UMLS offine server for
 * the purpose of querying.
 * You can download the UMLS data from its online server(
 * <a href="https://uts.nlm.nih.gov">Available here</a>).
 * </p>
 *
 * @author Rajaram
 * @version 1.0
 */
public final class UMLSOfflineResourceManager {
    /**
     * To avoid creating an instance.
     */
    private UMLSOfflineResourceManager() {
    }

    private static final String JDBC_DRIVER   = "com.mysql.jdbc.Driver";
    /**
     * URL of the SQL server.
     */
    private static final String JDBC_URL      = "jdbc:mysql://localhost:3306/";
    /**
     * UserName for the SQL server.
     */
    private static final String JDBC_USER = "root";
    /**
     * Password for the SQL server.
     */
    private static final String JDBC_PASSWORD = "";

    private static Driver driver = null;

    /**
      * <p>
      * Establish a connection with the SQL server.
      * </p>
      *
      * @return                    SQL connection to access the database.
      * @throws SQLException       if the connection parameters are wrong.
      */
    public static synchronized java.sql.Connection getConnection()
            throws SQLException {
        if (driver == null) {
            try {
                Class jdbcDriverClass = Class.forName(JDBC_DRIVER);
                driver = (Driver) jdbcDriverClass.newInstance();
                DriverManager.registerDriver(driver);
            } catch (Exception e) {
                System.out.println("Failed to initialise JDBC driver");
                e.printStackTrace();
            }
        }

        return DriverManager.getConnection(
                JDBC_URL,
                JDBC_USER,
                JDBC_PASSWORD
        );
    }

    /**
      * <p>
      * This method will terminate the connection with the SQL server.
      * </p>
      *
      * @param conn     SQL connection to be closed.
      */
    public static void close(final Connection conn) {
        try {
            if (!conn.isClosed() && conn != null) {
                conn.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
      * <p>
      * This method will close the SQl statement.
      * </p>
      *
      * @param stmt     SQL Statement to be closed.
      */
    public static void close(final PreparedStatement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
    }

    /**
      * <p>
      * This method will close the SQl result set.
      * </p>
      *
      * @param rs     ResultSet to be closed.
      */
    public static void close(final ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
