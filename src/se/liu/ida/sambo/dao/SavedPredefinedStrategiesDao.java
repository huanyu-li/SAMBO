/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package se.liu.ida.sambo.dao;

import se.liu.ida.sambo.dto.*;
import se.liu.ida.sambo.exceptions.*;

public interface SavedPredefinedStrategiesDao
{
	/** 
	 * Inserts a new row in the savedpredefinedstrategies table.
	 */
	public SavedPredefinedStrategiesPk insert(SavedPredefinedStrategies dto) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Updates a single row in the savedpredefinedstrategies table.
	 */
	public void update(SavedPredefinedStrategiesPk pk, SavedPredefinedStrategies dto) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Deletes a single row in the savedpredefinedstrategies table.
	 */
	public void delete(SavedPredefinedStrategiesPk pk) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns the rows from the savedpredefinedstrategies table that matches the specified primary-key value.
	 */
	public SavedPredefinedStrategies findByPrimaryKey(SavedPredefinedStrategiesPk pk) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'id = :id'.
	 */
	public SavedPredefinedStrategies findByPrimaryKey(int id) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria ''.
	 */
	public SavedPredefinedStrategies[] findAll() throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'id = :id'.
	 */
	public SavedPredefinedStrategies[] findWhereIdEquals(int id) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'predefinedstrategyid = :predefinedstrategyid'.
	 */
	public SavedPredefinedStrategies[] findWherePredefinedstrategyidEquals(int predefinedstrategyid) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'ontology1 = :ontology1'.
	 */
	public SavedPredefinedStrategies[] findWhereOntology1Equals(String ontology1) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'ontology2 = :ontology2'.
	 */
	public SavedPredefinedStrategies[] findWhereOntology2Equals(String ontology2) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'matchers = :matchers'.
	 */
	public SavedPredefinedStrategies[] findWhereMatchersEquals(String matchers) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'weights = :weights'.
	 */
	public SavedPredefinedStrategies[] findWhereWeightsEquals(String weights) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'threshold = :threshold'.
	 */
	public SavedPredefinedStrategies[] findWhereThresholdEquals(double threshold) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'fmeasure = :fmeasure'.
	 */
	public SavedPredefinedStrategies[] findWhereFmeasureEquals(double fmeasure) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'quality = :quality'.
	 */
	public SavedPredefinedStrategies[] findWhereQualityEquals(double quality) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'precision1 = :precision1'.
	 */
	public SavedPredefinedStrategies[] findWherePrecision1Equals(double precision1) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the criteria 'recall = :recall'.
	 */
	public SavedPredefinedStrategies[] findWhereRecallEquals(double recall) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Sets the value of maxRows
	 */
	public void setMaxRows(int maxRows);

	/** 
	 * Gets the value of maxRows
	 */
	public int getMaxRows();

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the specified arbitrary SQL statement
	 */
	public SavedPredefinedStrategies[] findByDynamicSelect(String sql, Object[] sqlParams) throws SavedPredefinedStrategiesDaoException;

	/** 
	 * Returns all rows from the savedpredefinedstrategies table that match the specified arbitrary SQL statement
	 */
	public SavedPredefinedStrategies[] findByDynamicWhere(String sql, Object[] sqlParams) throws SavedPredefinedStrategiesDaoException;
        
        
        public void updateStatement(float fmeasure,double quality,float precision,float recall, int id) throws SavedPredefinedStrategiesDaoException;
	
        public void insertStatement(int pid, String ontology1, String ontology2, String matchers, String weights, String combination, String thresholds, SavedPredefinedStrategies dto) throws SavedPredefinedStrategiesDaoException;
	
}
