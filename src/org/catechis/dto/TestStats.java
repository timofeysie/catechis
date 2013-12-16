package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*This object is used to hold the properties of natural language testing results.
*<p>(Not to be confused with the StatsTest object which tests the Stats object)
*/
public class TestStats
{

	private String name;
	private int total_tests;
	private double average_score;
	private String last_date;
	
	public void setName(String _name)
	{
		name = _name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setTotalTests(int _total_tests)
	{
		total_tests = _total_tests;
	}
	
	public int getTotalTests()
	{
		return total_tests;
	}
	
	public void setAverageScore(double _average_score)
	{
		average_score = _average_score;
	}
	
	public double getAverageScore()
	{
		return average_score;
	}
	
	public void setLastDate(String _last_date)
	{
		last_date = _last_date;
	}
	
	public String getLastDate()
	{
		return last_date;
	}

}
