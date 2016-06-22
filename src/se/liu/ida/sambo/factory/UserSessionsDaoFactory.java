/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package se.liu.ida.sambo.factory;

import java.sql.Connection;
import se.liu.ida.sambo.dao.*;
import se.liu.ida.sambo.jdbc.*;

public class UserSessionsDaoFactory
{
	/**
	 * Method 'create'
	 * 
	 * @return UserSessionsDao
	 */
	public static UserSessionsDao create()
	{
		return new UserSessionsDaoImpl();
	}

	/**
	 * Method 'create'
	 * 
	 * @param conn
	 * @return UserSessionsDao
	 */
	public static UserSessionsDao create(Connection conn)
	{
		return new UserSessionsDaoImpl( conn );
	}

}