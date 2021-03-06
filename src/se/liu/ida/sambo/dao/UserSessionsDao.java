/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package se.liu.ida.sambo.dao;

import java.util.Date;
import se.liu.ida.sambo.dto.*;
import se.liu.ida.sambo.exceptions.*;

public interface UserSessionsDao
{
	/** 
	 * Inserts a new row in the usersessions table.
	 */
	public UserSessionsPk insert(UserSessions dto) throws UserSessionsDaoException;

	/** 
	 * Updates a single row in the usersessions table.
	 */
	public void update(UserSessionsPk pk, UserSessions dto) throws UserSessionsDaoException;

	/** 
	 * Deletes a single row in the usersessions table.
	 */
	public void delete(UserSessionsPk pk) throws UserSessionsDaoException;

	/** 
	 * Returns the rows from the usersessions table that matches the specified primary-key value.
	 */
	public UserSessions findByPrimaryKey(UserSessionsPk pk) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'id = :id'.
	 */
	public UserSessions findByPrimaryKey(int id) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria ''.
	 */
	public UserSessions[] findAll() throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'id = :id'.
	 */
	public UserSessions[] findWhereIdEquals(int id) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'userid = :userid'.
	 */
	public UserSessions[] findWhereUseridEquals(int userid) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'email = :email'.
	 */
	public UserSessions[] findWhereEmailEquals(String email) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'ontology1 = :ontology1'.
	 */
	public UserSessions[] findWhereOntology1Equals(String ontology1) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'ontology2 = :ontology2'.
	 */
	public UserSessions[] findWhereOntology2Equals(String ontology2) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'color1 = :color1'.
	 */
	public UserSessions[] findWhereColor1Equals(String color1) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'color2 = :color2'.
	 */
	public UserSessions[] findWhereColor2Equals(String color2) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername0 = :matchername0'.
	 */
	public UserSessions[] findWhereMatchername0Equals(String matchername0) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue0 = :matchervalue0'.
	 */
	public UserSessions[] findWhereMatchervalue0Equals(String matchervalue0) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue0 = :weightvalue0'.
	 */
	public UserSessions[] findWhereWeightvalue0Equals(double weightvalue0) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername1 = :matchername1'.
	 */
	public UserSessions[] findWhereMatchername1Equals(String matchername1) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue1 = :matchervalue1'.
	 */
	public UserSessions[] findWhereMatchervalue1Equals(String matchervalue1) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue1 = :weightvalue1'.
	 */
	public UserSessions[] findWhereWeightvalue1Equals(double weightvalue1) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername2 = :matchername2'.
	 */
	public UserSessions[] findWhereMatchername2Equals(String matchername2) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue2 = :matchervalue2'.
	 */
	public UserSessions[] findWhereMatchervalue2Equals(String matchervalue2) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue2 = :weightvalue2'.
	 */
	public UserSessions[] findWhereWeightvalue2Equals(double weightvalue2) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername3 = :matchername3'.
	 */
	public UserSessions[] findWhereMatchername3Equals(String matchername3) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue3 = :matchervalue3'.
	 */
	public UserSessions[] findWhereMatchervalue3Equals(String matchervalue3) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue3 = :weightvalue3'.
	 */
	public UserSessions[] findWhereWeightvalue3Equals(double weightvalue3) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername4 = :matchername4'.
	 */
	public UserSessions[] findWhereMatchername4Equals(String matchername4) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue4 = :matchervalue4'.
	 */
	public UserSessions[] findWhereMatchervalue4Equals(String matchervalue4) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue4 = :weightvalue4'.
	 */
	public UserSessions[] findWhereWeightvalue4Equals(double weightvalue4) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername5 = :matchername5'.
	 */
	public UserSessions[] findWhereMatchername5Equals(String matchername5) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue5 = :matchervalue5'.
	 */
	public UserSessions[] findWhereMatchervalue5Equals(String matchervalue5) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue5 = :weightvalue5'.
	 */
	public UserSessions[] findWhereWeightvalue5Equals(double weightvalue5) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername6 = :matchername6'.
	 */
	public UserSessions[] findWhereMatchername6Equals(String matchername6) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue6 = :matchervalue6'.
	 */
	public UserSessions[] findWhereMatchervalue6Equals(String matchervalue6) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue6 = :weightvalue6'.
	 */
	public UserSessions[] findWhereWeightvalue6Equals(double weightvalue6) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername7 = :matchername7'.
	 */
	public UserSessions[] findWhereMatchername7Equals(String matchername7) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue7 = :matchervalue7'.
	 */
	public UserSessions[] findWhereMatchervalue7Equals(String matchervalue7) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue7 = :weightvalue7'.
	 */
	public UserSessions[] findWhereWeightvalue7Equals(double weightvalue7) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername8 = :matchername8'.
	 */
	public UserSessions[] findWhereMatchername8Equals(String matchername8) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue8 = :matchervalue8'.
	 */
	public UserSessions[] findWhereMatchervalue8Equals(String matchervalue8) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue8 = :weightvalue8'.
	 */
	public UserSessions[] findWhereWeightvalue8Equals(double weightvalue8) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchername9 = :matchername9'.
	 */
	public UserSessions[] findWhereMatchername9Equals(String matchername9) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'matchervalue9 = :matchervalue9'.
	 */
	public UserSessions[] findWhereMatchervalue9Equals(String matchervalue9) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'weightvalue9 = :weightvalue9'.
	 */
	public UserSessions[] findWhereWeightvalue9Equals(double weightvalue9) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'thresholdvalue = :thresholdvalue'.
	 */
	public UserSessions[] findWhereThresholdvalueEquals(double thresholdvalue) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'session_type = :sessionType'.
	 */
	public UserSessions[] findWhereSessionTypeEquals(String sessionType) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'sid = :sid'.
	 */
	public UserSessions[] findWhereSidEquals(String sid) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'step = :step'.
	 */
	public UserSessions[] findWhereStepEquals(short step) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'is_finalized = :isFinalized'.
	 */
	public UserSessions[] findWhereIsFinalizedEquals(int isFinalized) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'user_xml = :userXml'.
	 */
	public UserSessions[] findWhereUserXmlEquals(String userXml) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'user_historylist_xml = :userHistorylistXml'.
	 */
	public UserSessions[] findWhereUserHistorylistXmlEquals(String userHistorylistXml) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'user_relations_historylist_xml = :userRelationsHistorylistXml'.
	 */
	public UserSessions[] findWhereUserRelationsHistorylistXmlEquals(String userRelationsHistorylistXml) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'user_suggestions_list_xml = :userSuggestionsListXml'.
	 */
	public UserSessions[] findWhereUserSuggestionsListXmlEquals(String userSuggestionsListXml) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'user_temp_xml = :userTempXml'.
	 */
	public UserSessions[] findWhereUserTempXmlEquals(String userTempXml) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'creation_time = :creationTime'.
	 */
	public UserSessions[] findWhereCreationTimeEquals(Date creationTime) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the criteria 'last_accessed_time = :lastAccessedTime'.
	 */
	public UserSessions[] findWhereLastAccessedTimeEquals(Date lastAccessedTime) throws UserSessionsDaoException;

	/** 
	 * Sets the value of maxRows
	 */
	public void setMaxRows(int maxRows);

	/** 
	 * Gets the value of maxRows
	 */
	public int getMaxRows();

	/** 
	 * Returns all rows from the usersessions table that match the specified arbitrary SQL statement
	 */
	public UserSessions[] findByDynamicSelect(String sql, Object[] sqlParams) throws UserSessionsDaoException;

	/** 
	 * Returns all rows from the usersessions table that match the specified arbitrary SQL statement
	 */
	public UserSessions[] findByDynamicWhere(String sql, Object[] sqlParams) throws UserSessionsDaoException;

}
