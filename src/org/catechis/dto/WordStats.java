package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;

public class WordStats
{

	private String name;
	private String definition;
	private double average_reading_score;
	private double average_writing_score;
	private Vector reading_levels;
	private Vector writing_levels;
	private String last_date;
	private int number_of_words;
	
	public void setName(String _name)
	{
		name = _name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setDefinition(String _definition)
	{
		name = _definition;
	}
	
	public String getDefinition()
	{
		return name;
	}	
	
	public void setAverageReadingScore(double _average_reading_score)
	{
		average_reading_score = _average_reading_score;
	}
	
	public double getAverageReadingScore()
	{
		return average_reading_score;
	}
	
	public void setAverageWritingScore(double _average_writing_score)
	{
		average_writing_score = _average_writing_score;
	}
	
	public double getAverageWritingScore()
	{
		return average_writing_score;
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
	
	public void setLastDate(String _last_date)
	{
		last_date = _last_date;
	}
	
	public String getLastDate()
	{
		return last_date;
	}
	
	public void setNumberOfWords(int _number_of_words)
	{
		number_of_words = _number_of_words;
	}
	
	public int getNumberOfWords()
	{
		return number_of_words;
	}

}
