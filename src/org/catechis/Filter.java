package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.Transformer;

import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Filter
{
	/** Global filter bean object.*/
	private WordFilter word_filter;
	/** This global Word object is re-used instead off passing it around as
	* and argument during filtering*/
	private Word word;
	
	/** Minimum level to match a word at, used by all words associated with 
	* the filter passed to the constructor.*/
	private int min_level;
	/** Maximum level to match a word at, used by all words associated with 
	* the filter passed to the constructor.*/
	private int max_level;
	
	/** Used to find a certain test.*/
	private Hashtable tests;
	
	// for debugging
	private Vector log;
	
	/** Constructor of Filter object with a specific WordFilter bean.
	*/
	public Filter(WordFilter _word_filter)
	{
		this.word_filter = _word_filter;
	}
	
	public Filter(Hashtable _tests)
	{
		this.tests = _tests;
	}

	/**
	*The actual filtering of word objects happens here.
	*<p>Here are some of the properties we set in the WordFilter object.
	    <p>word_filter.setStartIndex(0);
	    <p>word_filter.setMinMaxRange("0-0");
	    <p>word_filter.setType("reading");
	    <p>word_filter.setCategory("october.xml");
	*<p>If properties have not been set in the filter object, then they are
	* skipped by using a try/catch block within each filter method.
	*/
	public boolean filterWord(Word _word, int index)
	{
		log = new Vector();
		log.add("------------------------");
		this.word = _word;
		setMinMaxLevels();
		boolean index_pass = indexFilter(index);
		boolean word_level_pass = wordLevelFilter();
		boolean et_pass = true; excludeTimeFilter();
		if (word_level_pass==true&&index_pass==true&&et_pass==true)
		{
			return true;
		}
		return false;
	}
	
	/**This method is necessary because we want all the filter properties to
	be shared with each word tested instead of getting them each time, and it
	saves space in the
	*/
	private void setMinMaxLevels()
	{
		try
		{
			min_level  = getLevelMin(word_filter.getMinMaxRange());
			max_level  = getLevelMax(word_filter.getMinMaxRange());
		}
		catch (java.lang.NullPointerException nt)
		{
			//min max levels not set
		}
	}
	
	/**
	*This method checks to see if the word integration number is lower
	* than the start index so that a user can test only words after a certain
	* level.
	*<p>This method wasn't working, so we went back to testing the words by reading and
	* writing separately.
	*/
	private boolean indexFilter(int index)
	{
		boolean pass_index = false;
		try
		{
			if (index>=word_filter.getStartIndex())
			{
				pass_index = true;
				log.add("filter: pass_index = true");
			} else
			{
				pass_index = false;
				log.add("filter: pass_index = false");
			}
		} catch (java.lang.NullPointerException nt)
		{
			// no index set
			pass_index = true;
		}
		return pass_index;
	}
	
	private boolean wordLevelFilter()
	{
		boolean pass_level = false;
		int word_level=0;
		try
		{
			if (word_filter.getType().equals("writing"))
			{
				word_level = word.getWritingLevel();
				log.add("Filter:writing level "+word_level);
			} 
			else if (word_filter.getType().equals("reading"))
			{
				word_level = word.getReadingLevel();
				log.add("Filter:reading level "+word_level);
			}

			if (word_level>=min_level&&word_level<=max_level)
			{
				pass_level = true;
				log.add("Filter:pass = true ");
			}
		} catch (java.lang.NullPointerException npe)
		{
			// no type set
			pass_level = true;
		}
		return pass_level;
	}
	
	/**We are assuming the exclude time is entered in milliseconds.
	*The flag et_pass starts out as true, and if a date more current
	* than the exclude time is found, then it is set to false and the
	* filter fails.
	*/
	private boolean excludeTimeFilter()
	{
		boolean et_pass = true;
		try
		{
			long exclude_time = parseMilliseconds(word_filter.getExcludeTime());
			Test tests[] = word.getTests();
			int length = tests.length;
			int index = 0;
			while (index<length)
			{
				Test test = tests[index];
				String str_date = test.getDate();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				Date date = null; // merge
				ParsePosition pp = new ParsePosition(0);
				date = sdf.parse(str_date, pp);
				long this_time = date.getTime();
				if (this_time>=exclude_time)
				{
					et_pass = false;
				}
				index++;
			}
		} catch (java.lang.NullPointerException npe)
		{
			// no exclude time set
			et_pass = true;
		}
		return et_pass;
	}
	
	/**
	*<p>Helper method used in getFilteredWords.
	*/
	private int getLevelMin(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_min = levels.substring(0,slash);
		int min = Integer.parseInt(str_min);
		return min;
	}
	
	/**
	*<p>Helper method used in getFilteredWords.
	*/
	private int getLevelMax(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_max = levels.substring(slash+1, levels.length());
		int max = Integer.parseInt(str_max);
		return max;
	}
	
	private long parseMilliseconds(String time)
	{
		long long_time = 0;
		try
		{
			long_time = new Long(time).longValue();
		}
		catch (NumberFormatException nfe)
		{
			//- if the String does not contain a parsable long.
		}
		return long_time;
	}	
	
	public void findTest()
	{
		
	}
	
	public Vector getLog()
	{
		return log;
	}

}
