package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import org.catechis.Domartin;
import org.catechis.dto.WordLastTestDates;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Vector;
import java.lang.Integer;

/**
*This utility class takes a Word object and records the last type-specific test
*and what level the word is at after/before??? the test.
*
*/
public class WordTestDateUtility
{
	
	private int allowed_number_of_levels;
	private String words_last_test_date;
	private long words_last_test_time;
	
	/** There keep a separate tally for reading and writing tests so that the WordTestDates can be set.*/
	private String words_last_test_reading_level;
	private String words_last_test_writing_level;
	
	private String words_last_test_reading_date;
	private String words_last_test_writing_date;
	
	private long words_last_test_reading_time;
	private long words_last_test_writing_time;
	
	private WordLastTestDates last_reading_test_dates;
	private WordLastTestDates last_writing_test_dates;

	private String words_first_test_date;
	private long words_first_test_time;
	private Vector word_times;
	private String word_test_type;


	/**This object first the first date a word was tested, or finds the last date
	* a wrd was tested depending on the type of test desired.
	*/
	public WordTestDateUtility(int _allowed_number_of_levels)
	{
		this.allowed_number_of_levels = _allowed_number_of_levels;
		words_last_test_time = 0;
		words_last_test_date = null;
		words_last_test_reading_time = 0;
		words_last_test_writing_time = 0;
		words_last_test_reading_level = "0";
		words_last_test_writing_level = "0";
		words_last_test_reading_date = "0";
		words_last_test_reading_date = "0";
		
		/* degubbing */
		log = new Vector();
	}
	
	/**
	*This no args constructor is used when the caller wants the first test date.
	*/
	public WordTestDateUtility()
	{
		this.words_first_test_date ="no tests";			
		this.words_first_test_time =   Long.MAX_VALUE;
		this.word_times = new Vector();
		//Date date = new Date(words_first_test_time);
		//words_first_test_date = date.toString();
		word_test_type = "reading";
		/* degubbing */
		log = new Vector();
	}
	
	/**THis method takes a Vector of words and looks at their tests to determin the
	* last date a word was tested, depending on the type desired.
	*/
	public void evaluateListOfWTD(Vector words, String type)
	{
		checkForNullWordLastTestDates();
		int num_of_words = words.size();
		int word_index = 0;
		while (word_index < num_of_words)
		{
			Word word = (Word)words.get(word_index);
			evaluateWordTestDates(word);
			if (type.equals("reading"))
			{
				String last_test_date = words_last_test_reading_date;
				last_reading_test_dates.addWord(word, last_test_date);
				// debuggin
				//log.add("WTDU r adding "+word.getText()+" 	"+word.getDefinition());
				Vector v = last_reading_test_dates.getLog();
				try
				{
					String line = (String)v.get(0);
					//log.add(line);
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{}
				last_reading_test_dates.resetLog();
			} else if (type.equals("writing"))
			{
				String last_test_date = words_last_test_writing_date;
				last_writing_test_dates.addWord(word, last_test_date);
				// debuggin
				//log.add("WTDU w adding "+word.getText()+" 	"+word.getDefinition());
				Vector v = last_reading_test_dates.getLog();
				try
				{
					String line = (String)v.get(0);
					//log.add(line);
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{}
				last_reading_test_dates.resetLog();
			}
			word_index++;
		}
	}
	
	private void checkForNullWordLastTestDates()
	{
		if (last_reading_test_dates==null)
		{
			last_reading_test_dates = new WordLastTestDates();
		}
		if (last_writing_test_dates==null)
		{
			last_writing_test_dates = new WordLastTestDates();
		}	
	}
	
	public void evaluateWordTestDates(Word word)
	{
		Test[] tests = word.getTests();
		int number = 0;
		try
		{
			number = tests.length;
			int i = 0;
			while (i<number)
			{
				String str_date = tests[i].getDate();
				String type  = Domartin.getTestType(tests[i].getName());
				String level = Domartin.getTestLevel(tests[i].getName());
				if (level==null)
				{
					// old test, we dont know the exact level so skip
					//log.add("wtdu level == null type = "+type);
				} else
				{
					if (type.equals("reading"))
					{
						compareReadingWordTestDates(str_date, level, word);
					} else if (type.equals("writing"))
					{
						compareWritingWordTestDates(str_date, level, word);
					}
					//compareWordDates(str_date); // why is this here???
				}
				//log.add("wltd "+words_last_test_reading_date);
				i++;
			}
			//log.add("final wltd "+words_last_test_reading_date);
			if (words_last_test_reading_date.equals("0"))
			{
				words_last_test_reading_time = word.getDateOfEntry();
				words_last_test_reading_date = Transformer.getLongDateFromMilliseconds(Long.toString(words_last_test_writing_time));
			
			}
			if (words_last_test_writing_date.equals("0"))
			{
				words_last_test_writing_time = word.getDateOfEntry();
				words_last_test_writing_date = Transformer.getLongDateFromMilliseconds(Long.toString(words_last_test_writing_time));
			}
		} catch (java.lang.NullPointerException npe)
		{
			number = 0;						// there are no tests, so use the words date of entry.
			words_last_test_writing_time = word.getDateOfEntry();
			words_last_test_writing_date = Transformer.getLongDateFromMilliseconds(Long.toString(words_last_test_writing_time));
			words_last_test_writing_level = "0";
			words_last_test_reading_time = word.getDateOfEntry();
			words_last_test_reading_date = Transformer.getLongDateFromMilliseconds(Long.toString(words_last_test_writing_time));
			words_last_test_reading_level = "0";
			log.add("npe so use doe "+words_last_test_reading_date);
		}
	}
	
	public WordLastTestDates getWordLastTestDates(String type)
	{
		WordLastTestDates ltd = new WordLastTestDates();
		if (type.equals("reading"))
		{
			ltd = last_reading_test_dates;
		} else if (type.equals("writing"))
		{
			ltd = last_writing_test_dates;
		}
		return ltd;
	}
	
	public int getWordsLastTestLevel(String type)
	{
		String words_last_test_level = new String();
		if (type.equals("reading"))
		{
			words_last_test_level = words_last_test_reading_level;
		} else if (type.equals("writing"))
		{
			words_last_test_level = words_last_test_writing_level;
		}
		return Integer.parseInt(words_last_test_level);
	}
	
	public String getWordsLastTestDate(String type)
	{
		String words_last_test_date = new String();
		if (type.equals("reading")) 
		{
			words_last_test_date = words_last_test_reading_date;
		} else if (type.equals("writing"))
		{
			words_last_test_date = words_last_test_writing_date;
		}
		return words_last_test_date;
	}
	
	/**
	*Return the lower number.
	*/
	public static String getEarlierTime(String time_one, String time_two)
	{
		long one = Long.parseLong(time_one);
		long two = Long.parseLong(time_two);
		if (one<two)
		{
			return Long.toString(one);
		}
		return Long.toString(two);
	}

	// need to create a new object to handle this, or move all this shit into WordTestDates???
	private void compareReadingWordTestDates(String str_date, String level, Word word)
	{
		if (Integer.parseInt(level) > (allowed_number_of_levels-1)) {level = Integer.toString((allowed_number_of_levels-1));}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		ParsePosition pp = new ParsePosition(0);
		Date date = sdf.parse(str_date, pp);
		long this_time = date.getTime();
		if (this_time>words_last_test_reading_time)
		{
			words_last_test_reading_time = this_time;
			words_last_test_reading_date = str_date;
			words_last_test_reading_level = level;
		} else 
		{
			if (words_last_test_date==null)
			{
				words_last_test_reading_time = this_time;
				words_last_test_reading_date = str_date;
				words_last_test_reading_level = level;
			} else 
			{
				// last_time should already be set
			}
		}
	}
	
	private void compareWritingWordTestDates(String str_date, String level, Word word)
	{
		if (Integer.parseInt(level) > (allowed_number_of_levels-1)) {level = Integer.toString((allowed_number_of_levels-1));}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = sdf.parse(str_date, pp);
		long this_time = date.getTime();
		if (this_time>words_last_test_writing_time)
		{
			words_last_test_writing_time = this_time;
			words_last_test_writing_date = str_date;
			words_last_test_writing_level = level;
		} else 
		{
			if (words_last_test_date==null)
			{
				words_last_test_writing_time = this_time;
				words_last_test_writing_date = str_date;
				words_last_test_writing_level = level;
			} else 
			{
				// last_time should already be set
			}
		}
	}

	private void compareWordDates(String str_date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = sdf.parse(str_date, pp);
		long this_time = date.getTime();
		if (this_time>words_last_test_time)
		{
			words_last_test_time = this_time;
			words_last_test_date = str_date;
		} else 
		{
			if (words_last_test_date==null)
			{
				words_last_test_time = this_time;
				words_last_test_date = str_date;
			} else 
			{
				// last_time should already be set
			}
		}
	}
	
	/**
	*Helper method for evaluateWordsFirstTestDate
	*/
	private void findEarlierTestDate(String str_date, String type)
	{
		log.add("looking at "+str_date);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		ParsePosition pp = new ParsePosition(0);
		Date date = sdf.parse(str_date, pp);
		long this_time = date.getTime();
		log.add("WTDU.findEarlierTestDate: type : "+type);
		if (type.equals(word_test_type))
		{
			word_times.add(Long.toString(this_time));
		}
		if (this_time<words_first_test_time)
		{
			words_first_test_time = this_time;
			words_first_test_date = str_date;
			log.add("this_time < test_time");
			log.add("this_time "+this_time);
			log.add("test_time "+words_first_test_time);
			log.add("test_date "+words_first_test_date);
		}
	}
	
	/**
	*This method finds the first date a word was tested.
	*<p>reading or writing tests are both considered.
	*<p>Test files were named like this:
	*<p>level 0 reading.test
	*<p>Daily tests created a name like this for backwards compatibilty.
	*<p>Really old tests were vague about the level they were testing,
	* but those dates are still important.  THey look like this:
	*<p>basic_writing.test
	*<p>or
	*<p>basic_ten_word.test
	*<p>In these cases, we just add the date of the test.
	*/
	public String evaluateWordsFirstTestDate(Word word)
	{
		Test [] tests = word.getTests();
		int number = 0;
		try
		{
			number = tests.length;
			log.add(number+" of tests (not npe)");
		} catch (java.lang.NullPointerException npe)
		{
			number = 0;
			log.add("npe: no tests!");
		}
		int i = 0;
		while (i<number)
		{
			try
			{
				String str_date = tests[i].getDate();
				String type = tests[i].getType();
				if (type!="reading"&&type!="writing")
				{
					String test_file = tests[i].getName();
					type = Domartin.getTestType(test_file);
				}
				/*
				if (type!="reading"&&type!="writing")
				{
					String test_file = tests[i].getName();
					type = Domartin.getOldTestType(test_file);
				}
				*/
				findEarlierTestDate(str_date, type);
				log.add(str_date+" found");
			} catch (java.lang.NullPointerException npe)
			{
				// maybe there are no tests yet?
				log.add("npe at "+i);
			}
			i++;
		}
		if (words_first_test_time == Long.MAX_VALUE)
		{
			words_first_test_date = "no tests";
		}
		return words_first_test_date;
	}
	
	public long getWordsFirstTestTime()
	{
		return words_first_test_time;
	}
	
	/**
	*THis method collects the test dates in a long number format,
	* subtracts each one from the words_first_test_time, and
	* returns a Vector of the times so that a Y axis plot of the
	* tests will be a number of milliseconds from the first date
	* of entry or the first test date.
	*/
	public Vector getWordTestTimes()
	{
		Vector new_list = new Vector();
		int total = word_times.size();
		int i = 0;
		while (i<total)
		{
			String milliseconds_str = (String)word_times.get(i);
			Long long_time = new Long(milliseconds_str);
			long this_test_time = long_time.longValue();
			long diff = (this_test_time - words_first_test_time);
			int days = Domartin.getDays(Long.toString(diff));
			
			long long_div = Long.parseLong("100000000");
			int neg = 11000;
			long diff2 = (this_test_time / long_div);
			int i_diff = (new Long(diff2).intValue()) - neg;
			//new_list.add(Long.toString(i_diff));
			new_list.add(Integer.toString(days));
			i++;
		}
		return new_list;
	}
	
	/**
	*This value is set to filter the list of test times by type,
	* because when you call getWordTestTimes() it is already created.
	*/
	public void setWordTestType(String _type)
	{
		word_test_type = _type;
	}
	
	
	/**
	*THis method gets a simple list of raw test times by type.
	*/
	public Vector getTestTimes(Word word)
	{
		Vector list = new Vector();
		Test [] tests = word.getTests();
		int number = getSafeNumberOfTests(tests);
		int i = 0;
		while (i<number)
		{
			try
			{
				String str_date = tests[i].getDate();
				String type = tests[i].getType();
				String name = tests[i].getName();
				log.add(name+" "+type);
				if (type==null)
				{
					type = Domartin.getTestType(name);
					log.add("type not set - Domartin says : "+type);
					if (!type.equals("reading")||!type.equals("writing"))
					{
						
					} else 
					{
						type = Domartin.getOldTestType(name);
						log.add("type still not set - Olde Domartin says :"+type);
					}
				}
				if (word_test_type.equals(type))
				{
					String new_date = Transformer.simplifyDate(str_date);
					log.add("added "+type+" "+new_date);
					list.add(type+" "+new_date);
				}
				//list.add(Long.toString(Domartin.getMilliseconds(str_date)));
			} catch (java.lang.NullPointerException npe)
			{
				// maybe there are no tests yet?
				log.add("npe at "+i);
			}
			i++;
		}
		if (words_first_test_time == Long.MAX_VALUE)
		{
			words_first_test_date = "no tests";
		}
		return list;
	}
	
	/**
	*This gets the length of an array, checked for null first.
	*/
	private int getSafeNumberOfTests(Test [] tests)
	{
		int number = 0;
		try
		{
			number = tests.length;
			log.add(number+" of tests (not npe)");
		} catch (java.lang.NullPointerException npe)
		{
			number = 0;
			log.add("npe: no tests!");
		}
		return number;
	}
	
	
	/** DEGUGGING */
	private Vector log;
	
	public Vector getLog()
	{
		return log;
	}
	
	public void addToLog(String s)
	{
		log.add(s);
	}
	
	public void resetLog()
	{
		log = new Vector();
	}

}
