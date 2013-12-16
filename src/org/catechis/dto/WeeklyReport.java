package org.catechis.dto;

import java.util.Vector;

/**
 * Hold info from year-month-week.xml weekly session history reports in the
 * root_path/user_id/subject/history folder.
<weekly_report>
	<week_of_year></week_of_year>
	<number_of_sessions></number_of_sessions> * This is the number of all stat histories saved during the week.  
	<average_time_spent_testing></average_time_spent_testing> * word_test_elapsed_time/number_of_session_tests
	<number_of_tests_in_week></number_of_tests_in_week>
	<number_of_words>2156</number_of_words>
	<writing_average>2.6154916512059367</writing_average>
	<reading_average>2.771799628942486</reading_average>
	<words_at_reading_level_0>21</words_at_reading_level_0>
	<words_at_reading_level_1>137</words_at_reading_level_1>
	<words_at_reading_level_2>155</words_at_reading_level_2>
	<words_at_reading_level_3>1843</words_at_reading_level_3>
	<words_at_writing_level_0>44</words_at_writing_level_0>
	<words_at_writing_level_1>227</words_at_writing_level_1>
	<words_at_writing_level_2>243</words_at_writing_level_2>
	<words_at_writing_level_3>1642</words_at_writing_level_3>
<w/eekly_report>
 * @author timmy
 *
 */
public class WeeklyReport 
{
	
	private String week_of_year;
	private int number_of_sessions;
	private long average_time_spent_testing;
	private int number_of_tests_in_week;
	private int number_of_words;
	private double writing_average;
	private double reading_average;
	private Vector reading_levels;
	private Vector writing_levels;
	
	public void setWeekOfYear(String _week_of_year)
	{
		week_of_year = _week_of_year;
	}
	
	public String getWeekOfYear()
	{
		return week_of_year;
	}
	
	public void setNumberOfSessions(int _number_of_sessions)
	{
		number_of_sessions = _number_of_sessions;
	}
	
	public int getNumberOfSessions()
	{
		return number_of_sessions;
	}
	
	public void setAverageTimeSpentTesting(long _average_time_spent_testing)
	{
		average_time_spent_testing = _average_time_spent_testing;
	}
	
	public long getAverageTimeSpentTesting()
	{
		return average_time_spent_testing;
	}
	
	public void setNumberOfTestsInWeek(int _number_of_tests_in_week)
	{
		number_of_tests_in_week = _number_of_tests_in_week;
	}
	
	public int getNumberOfTestsInWeek()
	{
		return number_of_tests_in_week;
	}
	
	
	public int setNumberOfTestsInWeek()
	{
		return number_of_tests_in_week;
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

}
