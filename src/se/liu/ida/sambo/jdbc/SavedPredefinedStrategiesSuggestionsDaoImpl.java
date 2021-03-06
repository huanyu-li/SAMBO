/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package se.liu.ida.sambo.jdbc;

import se.liu.ida.sambo.dao.*;
import se.liu.ida.sambo.factory.*;
import se.liu.ida.sambo.dto.*;
import se.liu.ida.sambo.exceptions.*;
import java.sql.Connection;
import java.util.Collection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class SavedPredefinedStrategiesSuggestionsDaoImpl extends AbstractDAO implements SavedPredefinedStrategiesSuggestionsDao
{
	/** 
	 * The factory class for this DAO has two versions of the create() method - one that
takes no arguments and one that takes a Connection argument. If the Connection version
is chosen then the connection will be stored in this attribute and will be used by all
calls to this DAO, otherwise a new Connection will be allocated for each operation.
	 */
	protected java.sql.Connection userConn;

	/** 
	 * All finder methods in this class use this SELECT constant to build their queries
	 */
	protected final String SQL_SELECT = "SELECT id, savedpredefinedstrategiesid, suggestionsXML, suggestionsVector FROM " + getTableName() + "";

	/** 
	 * Finder methods will pass this value to the JDBC setMaxRows method
	 */
	protected int maxRows;

	/** 
	 * SQL INSERT statement for this table
	 */
	protected final String SQL_INSERT = "INSERT INTO " + getTableName() + " ( id, savedpredefinedstrategiesid, suggestionsXML, suggestionsVector ) VALUES ( ?, ?, ?, ? )";

	/** 
	 * SQL UPDATE statement for this table
	 */
	protected final String SQL_UPDATE = "UPDATE " + getTableName() + " SET id = ?, savedpredefinedstrategiesid = ?, suggestionsXML = ?, suggestionsVector = ? WHERE id = ?";

	/** 
	 * SQL DELETE statement for this table
	 */
	protected final String SQL_DELETE = "DELETE FROM " + getTableName() + " WHERE id = ?";

	/** 
	 * Index of column id
	 */
	protected static final int COLUMN_ID = 1;

	/** 
	 * Index of column savedpredefinedstrategiesid
	 */
	protected static final int COLUMN_SAVEDPREDEFINEDSTRATEGIESID = 2;

	/** 
	 * Index of column suggestionsXML
	 */
	protected static final int COLUMN_SUGGESTIONS_X_M_L = 3;

	/** 
	 * Index of column suggestionsVector
	 */
	protected static final int COLUMN_SUGGESTIONS_VECTOR = 4;

	/** 
	 * Number of columns
	 */
	protected static final int NUMBER_OF_COLUMNS = 4;

	/** 
	 * Index of primary-key column id
	 */
	protected static final int PK_COLUMN_ID = 1;

	/** 
	 * Inserts a new row in the savedpredefinedstrategiessuggestions table.
	 */
	public SavedPredefinedStrategiesSuggestionsPk insert(SavedPredefinedStrategiesSuggestions dto) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		long t1 = System.currentTimeMillis();
		// declare variables
		final boolean isConnSupplied = (userConn != null);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			conn = isConnSupplied ? userConn : ResourceManager.getConnection();
		
			stmt = conn.prepareStatement( SQL_INSERT, Statement.RETURN_GENERATED_KEYS );
			int index = 1;
			stmt.setInt( index++, dto.getId() );
			stmt.setInt( index++, dto.getSavedpredefinedstrategiesid() );
			stmt.setString( index++, dto.getSuggestionsXML() );
			stmt.setString( index++, dto.getSuggestionsVector() );
			System.out.println( "Executing " + SQL_INSERT + " with DTO: " + dto );
			int rows = stmt.executeUpdate();
			long t2 = System.currentTimeMillis();
			System.out.println( rows + " rows affected (" + (t2-t1) + " ms)" );
		
			// retrieve values from auto-increment columns
			rs = stmt.getGeneratedKeys();
			if (rs != null && rs.next()) {
				dto.setId( rs.getInt( 1 ) );
			}
		
			reset(dto);
			return dto.createPk();
		}
		catch (Exception _e) {
			_e.printStackTrace();
			throw new SavedPredefinedStrategiesSuggestionsDaoException( "Exception: " + _e.getMessage(), _e );
		}
		finally {
			ResourceManager.close(stmt);
			if (!isConnSupplied) {
				ResourceManager.close(conn);
			}
		
		}
		
	}

	/** 
	 * Updates a single row in the savedpredefinedstrategiessuggestions table.
	 */
	public void update(SavedPredefinedStrategiesSuggestionsPk pk, SavedPredefinedStrategiesSuggestions dto) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		long t1 = System.currentTimeMillis();
		// declare variables
		final boolean isConnSupplied = (userConn != null);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			conn = isConnSupplied ? userConn : ResourceManager.getConnection();
		
			System.out.println( "Executing " + SQL_UPDATE + " with DTO: " + dto );
			stmt = conn.prepareStatement( SQL_UPDATE );
			int index=1;
			stmt.setInt( index++, dto.getId() );
			stmt.setInt( index++, dto.getSavedpredefinedstrategiesid() );
			stmt.setString( index++, dto.getSuggestionsXML() );
			stmt.setString( index++, dto.getSuggestionsVector() );
			stmt.setInt( 5, pk.getId() );
			int rows = stmt.executeUpdate();
			reset(dto);
			long t2 = System.currentTimeMillis();
			System.out.println( rows + " rows affected (" + (t2-t1) + " ms)" );
		}
		catch (Exception _e) {
			_e.printStackTrace();
			throw new SavedPredefinedStrategiesSuggestionsDaoException( "Exception: " + _e.getMessage(), _e );
		}
		finally {
			ResourceManager.close(stmt);
			if (!isConnSupplied) {
				ResourceManager.close(conn);
			}
		
		}
		
	}

	/** 
	 * Deletes a single row in the savedpredefinedstrategiessuggestions table.
	 */
	public void delete(SavedPredefinedStrategiesSuggestionsPk pk) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		long t1 = System.currentTimeMillis();
		// declare variables
		final boolean isConnSupplied = (userConn != null);
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			conn = isConnSupplied ? userConn : ResourceManager.getConnection();
		
			System.out.println( "Executing " + SQL_DELETE + " with PK: " + pk );
			stmt = conn.prepareStatement( SQL_DELETE );
			stmt.setInt( 1, pk.getId() );
			int rows = stmt.executeUpdate();
			long t2 = System.currentTimeMillis();
			System.out.println( rows + " rows affected (" + (t2-t1) + " ms)" );
		}
		catch (Exception _e) {
			_e.printStackTrace();
			throw new SavedPredefinedStrategiesSuggestionsDaoException( "Exception: " + _e.getMessage(), _e );
		}
		finally {
			ResourceManager.close(stmt);
			if (!isConnSupplied) {
				ResourceManager.close(conn);
			}
		
		}
		
	}

	/** 
	 * Returns the rows from the savedpredefinedstrategiessuggestions table that matches the specified primary-key value.
	 */
	public SavedPredefinedStrategiesSuggestions findByPrimaryKey(SavedPredefinedStrategiesSuggestionsPk pk) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByPrimaryKey( pk.getId() );
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria 'id = :id'.
	 */
	public SavedPredefinedStrategiesSuggestions findByPrimaryKey(int id) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		SavedPredefinedStrategiesSuggestions ret[] = findByDynamicSelect( SQL_SELECT + " WHERE id = ?", new Object[] {  new Integer(id) } );
		return ret.length==0 ? null : ret[0];
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria ''.
	 */
	public SavedPredefinedStrategiesSuggestions[] findAll() throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByDynamicSelect( SQL_SELECT + " ORDER BY id", null );
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria 'id = :id'.
	 */
	public SavedPredefinedStrategiesSuggestions[] findWhereIdEquals(int id) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByDynamicSelect( SQL_SELECT + " WHERE id = ? ORDER BY id", new Object[] {  new Integer(id) } );
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria 'savedpredefinedstrategiesid = :savedpredefinedstrategiesid'.
	 */
	public SavedPredefinedStrategiesSuggestions[] findWhereSavedpredefinedstrategiesidEquals(int savedpredefinedstrategiesid) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByDynamicSelect( SQL_SELECT + " WHERE savedpredefinedstrategiesid = ? ORDER BY savedpredefinedstrategiesid", new Object[] {  new Integer(savedpredefinedstrategiesid) } );
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria 'suggestionsXML = :suggestionsXML'.
	 */
	public SavedPredefinedStrategiesSuggestions[] findWhereSuggestionsXMLEquals(String suggestionsXML) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByDynamicSelect( SQL_SELECT + " WHERE suggestionsXML = ? ORDER BY suggestionsXML", new Object[] { suggestionsXML } );
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the criteria 'suggestionsVector = :suggestionsVector'.
	 */
	public SavedPredefinedStrategiesSuggestions[] findWhereSuggestionsVectorEquals(String suggestionsVector) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		return findByDynamicSelect( SQL_SELECT + " WHERE suggestionsVector = ? ORDER BY suggestionsVector", new Object[] { suggestionsVector } );
	}

	/**
	 * Method 'SavedPredefinedStrategiesSuggestionsDaoImpl'
	 * 
	 */
	public SavedPredefinedStrategiesSuggestionsDaoImpl()
	{
	}

	/**
	 * Method 'SavedPredefinedStrategiesSuggestionsDaoImpl'
	 * 
	 * @param userConn
	 */
	public SavedPredefinedStrategiesSuggestionsDaoImpl(final java.sql.Connection userConn)
	{
		this.userConn = userConn;
	}

	/** 
	 * Sets the value of maxRows
	 */
	public void setMaxRows(int maxRows)
	{
		this.maxRows = maxRows;
	}

	/** 
	 * Gets the value of maxRows
	 */
	public int getMaxRows()
	{
		return maxRows;
	}

	/**
	 * Method 'getTableName'
	 * 
	 * @return String
	 */
	public String getTableName()
	{
		return "dbsambo.savedpredefinedstrategiessuggestions";
	}

	/** 
	 * Fetches a single row from the result set
	 */
	protected SavedPredefinedStrategiesSuggestions fetchSingleResult(ResultSet rs) throws SQLException
	{
		if (rs.next()) {
			SavedPredefinedStrategiesSuggestions dto = new SavedPredefinedStrategiesSuggestions();
			populateDto( dto, rs);
			return dto;
		} else {
			return null;
		}
		
	}

	/** 
	 * Fetches multiple rows from the result set
	 */
	protected SavedPredefinedStrategiesSuggestions[] fetchMultiResults(ResultSet rs) throws SQLException
	{
		Collection resultList = new ArrayList();
		while (rs.next()) {
			SavedPredefinedStrategiesSuggestions dto = new SavedPredefinedStrategiesSuggestions();
			populateDto( dto, rs);
			resultList.add( dto );
		}
		
		SavedPredefinedStrategiesSuggestions ret[] = new SavedPredefinedStrategiesSuggestions[ resultList.size() ];
		resultList.toArray( ret );
		return ret;
	}

	/** 
	 * Populates a DTO with data from a ResultSet
	 */
	protected void populateDto(SavedPredefinedStrategiesSuggestions dto, ResultSet rs) throws SQLException
	{
		dto.setId( rs.getInt( COLUMN_ID ) );
		dto.setSavedpredefinedstrategiesid( rs.getInt( COLUMN_SAVEDPREDEFINEDSTRATEGIESID ) );
		dto.setSuggestionsXML( rs.getString( COLUMN_SUGGESTIONS_X_M_L ) );
		dto.setSuggestionsVector( rs.getString( COLUMN_SUGGESTIONS_VECTOR ) );
	}

	/** 
	 * Resets the modified attributes in the DTO
	 */
	protected void reset(SavedPredefinedStrategiesSuggestions dto)
	{
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the specified arbitrary SQL statement
	 */
	public SavedPredefinedStrategiesSuggestions[] findByDynamicSelect(String sql, Object[] sqlParams) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		// declare variables
		final boolean isConnSupplied = (userConn != null);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			conn = isConnSupplied ? userConn : ResourceManager.getConnection();
		
			// construct the SQL statement
			final String SQL = sql;
		
		
			System.out.println( "Executing " + SQL );
			// prepare statement
			stmt = conn.prepareStatement( SQL );
			stmt.setMaxRows( maxRows );
		
			// bind parameters
			for (int i=0; sqlParams!=null && i<sqlParams.length; i++ ) {
				stmt.setObject( i+1, sqlParams[i] );
			}
		
		
			rs = stmt.executeQuery();
		
			// fetch the results
			return fetchMultiResults(rs);
		}
		catch (Exception _e) {
			_e.printStackTrace();
			throw new SavedPredefinedStrategiesSuggestionsDaoException( "Exception: " + _e.getMessage(), _e );
		}
		finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			if (!isConnSupplied) {
				ResourceManager.close(conn);
			}
		
		}
		
	}

	/** 
	 * Returns all rows from the savedpredefinedstrategiessuggestions table that match the specified arbitrary SQL statement
	 */
	public SavedPredefinedStrategiesSuggestions[] findByDynamicWhere(String sql, Object[] sqlParams) throws SavedPredefinedStrategiesSuggestionsDaoException
	{
		// declare variables
		final boolean isConnSupplied = (userConn != null);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			// get the user-specified connection or get a connection from the ResourceManager
			conn = isConnSupplied ? userConn : ResourceManager.getConnection();
		
			// construct the SQL statement
			final String SQL = SQL_SELECT + " WHERE " + sql;
		
		
			System.out.println( "Executing " + SQL );
			// prepare statement
			stmt = conn.prepareStatement( SQL );
			stmt.setMaxRows( maxRows );
		
			// bind parameters
			for (int i=0; sqlParams!=null && i<sqlParams.length; i++ ) {
				stmt.setObject( i+1, sqlParams[i] );
			}
		
		
			rs = stmt.executeQuery();
		
			// fetch the results
			return fetchMultiResults(rs);
		}
		catch (Exception _e) {
			_e.printStackTrace();
			throw new SavedPredefinedStrategiesSuggestionsDaoException( "Exception: " + _e.getMessage(), _e );
		}
		finally {
			ResourceManager.close(rs);
			ResourceManager.close(stmt);
			if (!isConnSupplied) {
				ResourceManager.close(conn);
			}
		
		}
		
	}

}
