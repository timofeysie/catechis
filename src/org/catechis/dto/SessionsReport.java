package org.catechis.dto;

/**
 * This is a session report object from info that is held in the status.record file.
 * It will be collected and stored in a history/date-month-week.xml file upon login after a week has passed.
<word_test_start></word_test_start>
<word_test_elapsed_time></word_test_elapsed_time>
<number_of_sessions></number_of_sessions>
<week_of_year></week_of_year>
<number_of_tests_in_week></number_of_tests_in_week>

 * @author timmy
 *
 */
public class SessionsReport 
{

	private long word_test_start;
	
	private long word_test_elapsed_time;
	
	private int number_of_sessions;
	
	private int week_of_year;
	
	private int number_of_tests_in_week;
	
	public void setWordTestStart(long _word_test_start)
	{
		word_test_start = _word_test_start;
	}
	
	public long getWordTestStart()
	{
		return word_test_start;
	}
	
	public void setWordTestElapsedTime(long _word_test_elapsed_time)
	{
		word_test_elapsed_time = _word_test_elapsed_time;
	}
	
	public long getWordTestElapsedTime()
	{
		return word_test_elapsed_time;
	}
	
	public void setNumberOfSessions(int _number_of_sessions)
	{
		number_of_sessions = _number_of_sessions;
	}
	
	public int getNumberOfSessions()
	{
		return number_of_sessions;
	}
	
	public void setWeekOfYear(int _week_of_year)
	{
		week_of_year = _week_of_year;
	}
	
	public int getWeekOfYear()
	{
		return week_of_year;
	}
	
	public void setNumberOfTestsInWeek(int _number_of_tests_in_week)
	{
		number_of_tests_in_week = _number_of_tests_in_week;
	}
	
	public int getNumberOfTestsInWeek()
	{
		return number_of_tests_in_week;
	}
	
	
}
