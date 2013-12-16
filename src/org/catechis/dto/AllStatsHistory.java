package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;

/**
*<p>This bean stores the statistics gathered in the AllWordStats and AllTestStats
* objects.  These objects can then be stored and retrieved in a collections to provide
* a history of testing activities.
<p> Here are the following statistics collected:
<p>
<p>AllTestStats properties
<p>Test  		Total  	Average
<p>All Tests 	182 	49.7
<p>
<p>AllWordStats properties
<p>Category  	total  	R-Avg.  	W-Avg. 
<p>All 		913 	0.81 		1.2
<p>R-0  W-0  	R-1  	W-1  	R-2  	W-2  	R-3  	W-3  
<p>317 	201 	474 	485 	99 	137 	22 	57 
*/
public class AllStatsHistory
{
	// date stats copllected
	private String date;

	// properties from all test stats
	private int number_of_tests;
	private double average_score;
	
	// properties from all word stats
	private int number_of_words;
	private double writing_average;
	private double reading_average;
	private Vector reading_levels;
	private Vector writing_levels;
	
	private long session_start;
	private long session_end;
	private int number_of_session_tests;
	private int number_of_retired_words;
	
	public void setDate(String _date)
	{
		date = _date;
	}
	
	public String getDate()
	{
		return date;
	}

	public void setNumberOfTests(int _number_of_tests)
	{
		number_of_tests = _number_of_tests;
	}

	public int getNumberOfTests()
	{
		return number_of_tests;
	}

	public void setAverageScore(double _average_score)
	{
		average_score = _average_score;
	}

	public double getAverageScore()
	{
		return average_score;
	}

	public void setNumberOfWords(int _number_of_words)
	{
		number_of_words = _number_of_words;
	}
	
	public int getNumberOfWords()
	{
		return number_of_words;
	}
	
	public void setWritingAverage(double _writing_average)
	{
		writing_average = _writing_average;
	}
	
	public double getWritingAverage()
	{
		return writing_average;
	}
	
	public void setReadingAverage(double _reading_average)
	{
		reading_average = _reading_average;
	}
	
	public double getReadingAverage()
	{
		return reading_average;
	}

	public void setReadingLevels(Vector _reading_levels)
	{
		reading_levels = _reading_levels;
	}
	public Vector getReadingLevels()
	{
		return reading_levels;
	}
	
	public void setWritingLevels(Vector _writing_levels)
	{
		writing_levels = _writing_levels;
	}
	
	public Vector getWritingLevels()
	{
		return writing_levels;
	}
	
	public void setSessionStart(long _session_start)
	{
		session_start = _session_start;
	}
	
	public long getSessionStart()
	{
		return  session_start;
	}
	
	public void setSessionEnd(long _session_end)
	{
		session_end = _session_end;
	}
	
	public long getSessionEnd()
	{
		return  session_end;
	}
	
	public void setNumberOfSessionTests(int _number_of_session_tests)
	{
		number_of_session_tests = _number_of_session_tests;
	}
	
	public int getNumberOfSessionTests()
	{
		return number_of_session_tests;
	}
	
	public void setNumberOfRetiredWords(int _number_of_retired_words)
	{
		number_of_retired_words = _number_of_retired_words;
	}
	
	public int getNumberOfRetiredWords()
	{
		return number_of_retired_words;
	}
	
}
