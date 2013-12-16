package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import org.catechis.dto.RatesOfForgetting;

public class AllWordStats
{

	private int number_of_words;
	private double writing_average;
	private double reading_average;
	private Vector reading_levels;
	private Vector writing_levels;
	private WordStats[] word_stats;
	private RatesOfForgetting rates_of_forgetting;
	private int number_of_retired_words;
	
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
	
	public WordStats[] getWordStats()
	{
		return word_stats;
	}

	public void setWordStats(WordStats[] _word_stats)
	{
		word_stats = _word_stats;
	}

	public WordStats getWordStats(int index)
	{
		return word_stats[index];
	}

	public void setWordStats(int index, WordStats _word_stats)
	{
		word_stats[index] = _word_stats;
	}
	
	public void setRatesOfForgetting(RatesOfForgetting _rates_of_forgetting)
	{
		rates_of_forgetting = _rates_of_forgetting;
	}

	public RatesOfForgetting getRatesOfForgetting()
	{
		return rates_of_forgetting;
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
