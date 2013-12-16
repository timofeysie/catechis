package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
public class AllTestStats
{

	private int number_of_tests;
	private double average_score;
	private TestStats[] test_stats;
	
	private long session_start;
	private long session_end;
	private int number_of_session_tests;

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

	public TestStats[] getTestStats()
	{
		return test_stats;
	}

	public void setTestStats(TestStats[] _test_stats)
	{
		test_stats = _test_stats;
	}

	public TestStats getTestStats(int index)
	{
		return test_stats[index];
	}

	public void setTestStats(int index, TestStats _test_stats)
	{
		test_stats[index] = _test_stats;
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

}
