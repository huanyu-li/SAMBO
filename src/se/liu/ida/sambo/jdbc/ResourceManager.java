package se.liu.ida.sambo.jdbc;

import java.sql.*;

public class ResourceManager
{
    private static String JDBC_DRIVER   = "com.mysql.jdbc.Driver";
    
    
    private static String JDBC_URL      = "jdbc:mysql://localhost:3306/";
    
    
    private static String JDBC_USER     = "root";
    private static String JDBC_PASSWORD = "zxcvbn";
    
//    private static String JDBC_URL      = "jdbc:mysql://botanix.ida.liu.se:3306/dbsambo";
//    
//    private static String JDBC_USER     = "rajka62";
//    private static String JDBC_PASSWORD = "rajka62";



    private static Driver driver = null;

    public static synchronized Connection getConnection()
	throws SQLException
    {
        if (driver == null)
        {
            try
            {
                Class jdbcDriverClass = Class.forName( JDBC_DRIVER );
                driver = (Driver) jdbcDriverClass.newInstance();
                DriverManager.registerDriver( driver );
            }
            catch (Exception e)
            {
                System.out.println( "Failed to initialise JDBC driver" );
                e.printStackTrace();
            }
        }

        return DriverManager.getConnection(
                JDBC_URL,
                JDBC_USER,
                JDBC_PASSWORD
        );
    }


	public static void close(Connection conn)
	{
		try {
			if (!conn.isClosed() && conn != null) conn.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}

	public static void close(PreparedStatement stmt)
	{
		try {
			if (stmt != null) stmt.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}

	public static void close(ResultSet rs)
	{
		try {
			if (rs != null) rs.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

	}

}
