/*
 * This source file was generated by FireStorm/DAO.
 * 
 * If you purchase a full license for FireStorm/DAO you can customize this header file.
 * 
 * For more information please visit http://www.codefutures.com/products/firestorm
 */

package se.liu.ida.sambo.dto;

import se.liu.ida.sambo.dao.*;
import se.liu.ida.sambo.factory.*;
import se.liu.ida.sambo.exceptions.*;
import java.io.Serializable;
import java.util.*;

public class SavedPredefinedStrategies implements Serializable
{
	/** 
	 * This attribute maps to the column id in the savedpredefinedstrategies table.
	 */
	protected int id;

	/** 
	 * This attribute maps to the column predefinedstrategyid in the savedpredefinedstrategies table.
	 */
	protected int predefinedstrategyid;

	/** 
	 * This attribute maps to the column ontology1 in the savedpredefinedstrategies table.
	 */
	protected String ontology1;

	/** 
	 * This attribute maps to the column ontology2 in the savedpredefinedstrategies table.
	 */
	protected String ontology2;

	/** 
	 * This attribute maps to the column matchers in the savedpredefinedstrategies table.
	 */
	protected String matchers;

	/** 
	 * This attribute maps to the column weights in the savedpredefinedstrategies table.
	 */
	protected String weights;
        
        /** 
	 * This attribute maps to the column combination in the savedpredefinedstrategies table.
	 */
        protected String combination;

	/** 
	 * This attribute maps to the column threshold in the savedpredefinedstrategies table.
	 */
	protected String threshold;

	/** 
	 * This attribute maps to the column fmeasure in the savedpredefinedstrategies table.
	 */
	protected double fmeasure;

	/** 
	 * This attribute maps to the column quality in the savedpredefinedstrategies table.
	 */
	protected double quality;

	/** 
	 * This attribute maps to the column precision1 in the savedpredefinedstrategies table.
	 */
	protected double precision1;

	/** 
	 * This attribute maps to the column recall in the savedpredefinedstrategies table.
	 */
	protected double recall;

	/**
	 * Method 'SavedPredefinedStrategies'
	 * 
	 */
	public SavedPredefinedStrategies()
	{
	}

	/**
	 * Method 'getId'
	 * 
	 * @return int
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Method 'setId'
	 * 
	 * @param id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * Method 'getPredefinedstrategyid'
	 * 
	 * @return int
	 */
	public int getPredefinedstrategyid()
	{
		return predefinedstrategyid;
	}

	/**
	 * Method 'setPredefinedstrategyid'
	 * 
	 * @param predefinedstrategyid
	 */
	public void setPredefinedstrategyid(int predefinedstrategyid)
	{
		this.predefinedstrategyid = predefinedstrategyid;
	}

	/**
	 * Method 'getOntology1'
	 * 
	 * @return String
	 */
	public String getOntology1()
	{
		return ontology1;
	}

	/**
	 * Method 'setOntology1'
	 * 
	 * @param ontology1
	 */
	public void setOntology1(String ontology1)
	{
		this.ontology1 = ontology1;
	}

	/**
	 * Method 'getOntology2'
	 * 
	 * @return String
	 */
	public String getOntology2()
	{
		return ontology2;
	}

	/**
	 * Method 'setOntology2'
	 * 
	 * @param ontology2
	 */
	public void setOntology2(String ontology2)
	{
		this.ontology2 = ontology2;
	}

	/**
	 * Method 'getMatchers'
	 * 
	 * @return String
	 */
	public String getMatchers()
	{
		return matchers;
	}

	/**
	 * Method 'setMatchers'
	 * 
	 * @param matchers
	 */
	public void setMatchers(String matchers)
	{
		this.matchers = matchers;
	}

	/**
	 * Method 'getWeights'
	 * 
	 * @return String
	 */
	public String getWeights()
	{
		return weights;
	}

	/**
	 * Method 'setWeights'
	 * 
	 * @param weights
	 */
	public void setWeights(String weights)
	{
		this.weights = weights;
	}

	/**
	 * Method 'getThreshold'
	 * 
	 * @return String
	 */
	public String getThreshold()
	{
		return threshold;
	}

	/**
	 * Method 'setThreshold'
	 * 
	 * @param threshold
	 */
	public void setThreshold(String threshold)
	{
		this.threshold = threshold;
	}
        
        
        /**
	 * Method 'getCombination'
	 * 
	 * @return String
	 */
	public String getCombination()
	{
		return combination;
	}

	/**
	 * Method 'setThreshold'
	 * 
	 * @param threshold
	 */
	public void setCombination(String combination)
	{
		this.combination = combination;
	}
        

	/**
	 * Method 'getFmeasure'
	 * 
	 * @return double
	 */
	public double getFmeasure()
	{
		return fmeasure;
	}

	/**
	 * Method 'setFmeasure'
	 * 
	 * @param fmeasure
	 */
	public void setFmeasure(double fmeasure)
	{
		this.fmeasure = fmeasure;
	}

	/**
	 * Method 'getQuality'
	 * 
	 * @return double
	 */
	public double getQuality()
	{
		return quality;
	}

	/**
	 * Method 'setQuality'
	 * 
	 * @param quality
	 */
	public void setQuality(double quality)
	{
		this.quality = quality;
	}

	/**
	 * Method 'getPrecision1'
	 * 
	 * @return double
	 */
	public double getPrecision1()
	{
		return precision1;
	}

	/**
	 * Method 'setPrecision1'
	 * 
	 * @param precision1
	 */
	public void setPrecision1(double precision1)
	{
		this.precision1 = precision1;
	}

	/**
	 * Method 'getRecall'
	 * 
	 * @return double
	 */
	public double getRecall()
	{
		return recall;
	}

	/**
	 * Method 'setRecall'
	 * 
	 * @param recall
	 */
	public void setRecall(double recall)
	{
		this.recall = recall;
	}

	/**
	 * Method 'equals'
	 * 
	 * @param _other
	 * @return boolean
	 */
	public boolean equals(Object _other)
	{
		if (_other == null) {
			return false;
		}
		
		if (_other == this) {
			return true;
		}
		
		if (!(_other instanceof SavedPredefinedStrategies)) {
			return false;
		}
		
		final SavedPredefinedStrategies _cast = (SavedPredefinedStrategies) _other;
		if (id != _cast.id) {
			return false;
		}
		
		if (predefinedstrategyid != _cast.predefinedstrategyid) {
			return false;
		}
		
		if (ontology1 == null ? _cast.ontology1 != ontology1 : !ontology1.equals( _cast.ontology1 )) {
			return false;
		}
		
		if (ontology2 == null ? _cast.ontology2 != ontology2 : !ontology2.equals( _cast.ontology2 )) {
			return false;
		}
		
		if (matchers == null ? _cast.matchers != matchers : !matchers.equals( _cast.matchers )) {
			return false;
		}
		
		if (weights == null ? _cast.weights != weights : !weights.equals( _cast.weights )) {
			return false;
		}
		
		if (threshold != _cast.threshold) {
			return false;
		}
		
		if (fmeasure != _cast.fmeasure) {
			return false;
		}
		
		if (quality != _cast.quality) {
			return false;
		}
		
		if (precision1 != _cast.precision1) {
			return false;
		}
		
		if (recall != _cast.recall) {
			return false;
		}
		
		return true;
	}

	/**
	 * Method 'hashCode'
	 * 
	 * @return int
	 */
	public int hashCode()
	{
		int _hashCode = 0;
		_hashCode = 29 * _hashCode + id;
		_hashCode = 29 * _hashCode + predefinedstrategyid;
		if (ontology1 != null) {
			_hashCode = 29 * _hashCode + ontology1.hashCode();
		}
		
		if (ontology2 != null) {
			_hashCode = 29 * _hashCode + ontology2.hashCode();
		}
		
		if (matchers != null) {
			_hashCode = 29 * _hashCode + matchers.hashCode();
		}
		
		if (weights != null) {
			_hashCode = 29 * _hashCode + weights.hashCode();
		}
                
                
                if (threshold != null) {
			_hashCode = 29 * _hashCode + threshold.hashCode();
		}
                
		long temp_fmeasure = Double.doubleToLongBits(fmeasure);
		_hashCode = 29 * _hashCode + (int) (temp_fmeasure ^ (temp_fmeasure >>> 32));
		long temp_quality = Double.doubleToLongBits(quality);
		_hashCode = 29 * _hashCode + (int) (temp_quality ^ (temp_quality >>> 32));
		long temp_precision1 = Double.doubleToLongBits(precision1);
		_hashCode = 29 * _hashCode + (int) (temp_precision1 ^ (temp_precision1 >>> 32));
		long temp_recall = Double.doubleToLongBits(recall);
		_hashCode = 29 * _hashCode + (int) (temp_recall ^ (temp_recall >>> 32));
		return _hashCode;
	}

	/**
	 * Method 'createPk'
	 * 
	 * @return SavedPredefinedStrategiesPk
	 */
	public SavedPredefinedStrategiesPk createPk()
	{
		return new SavedPredefinedStrategiesPk(id);
	}

	/**
	 * Method 'toString'
	 * 
	 * @return String
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "se.liu.ida.sambo.dto.SavedPredefinedStrategies: " );
		ret.append( "id=" + id );
		ret.append( ", predefinedstrategyid=" + predefinedstrategyid );
		ret.append( ", ontology1=" + ontology1 );
		ret.append( ", ontology2=" + ontology2 );
		ret.append( ", matchers=" + matchers );
		ret.append( ", weights=" + weights );
		ret.append( ", threshold=" + threshold );
		ret.append( ", fmeasure=" + fmeasure );
		ret.append( ", quality=" + quality );
		ret.append( ", precision1=" + precision1 );
		ret.append( ", recall=" + recall );
		return ret.toString();
	}

}