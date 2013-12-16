package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.lang.Long;
import java.lang.Double;
import java.lang.NumberFormatException;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Enumeration;
import org.apache.commons.beanutils.BeanComparator;

import org.catechis.Domartin;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordTestDates;

// for debuggin
import org.catechis.Transformer;
import java.lang.Throwable;



public class Stats
{
	/** This variable should be coming from some kind of user preference.*/
	private int allowed_number_of_levels = 4;
	/** The last date found in passed in word lists and test lists.*/
	private String last_date;
	/** There is a difference in processing words and tests, so this is a word specific last date*/
	private String words_last_test_date;
	/** There keep a separate tally for reading and writing tests so that the WordTestDates can be set.*/
	private String words_last_test_reading_level;
	private String words_last_test_writing_level;
	private String words_last_test_reading_date;
	private String words_last_test_writing_date;
	private long words_last_test_reading_time;
	private long words_last_test_writing_time;
	
	/** This is the last date in milliseconds for comparison to a new date.*/
	private long last_time;
	/** The milliseconds associated with the words_last_time_date.*/
	private long words_last_test_time;
	private int num_of_words;
	private double avg_reading_level;
	private double avg_writing_level;
	private Vector reading_counts;
	private Vector writing_counts;
	private Hashtable writing_levels;
	private Hashtable reading_levels;
	
	/**These variables needs to come from exclusive categories only.  For instance,
	* the date that phrases are entered into the system: a word should only have one
	* of these categories, and we can create stats of all phrases by keeping track
	* of each words stats as we go thru these kind of exclusive categories.
	*/
	private int total_word_count;
	private int total_test_count;
	private double running_total_of_reading_word_averages;
	private double running_total_of_writing_word_averages;
	private double running_total_of_tests;
	private Vector total_reading_counts;
	private Vector total_writing_counts;
	
	/**These vectors holds the Rate Of Forgetting information. */
	private Vector reading_rof;
	private Vector writing_rof;
	private Vector r_rof_milliseconds;
	private Vector r_rof_running_count;
	private Vector w_rof_milliseconds;
	private Vector w_rof_running_count;
	private ArrayList reading_tests;
	private ArrayList writing_tests;
	private HashMap reading_hash;
	private ArrayList reading_list;
	private HashMap writing_hash;
	private ArrayList writing_list;
	
	private Vector reading_word_test_dates;
	private Vector writing_word_test_dates;
	
	/** This object holds lists of the last test date of each word.*/
	private WordTestDates word_test_dates;
	
	private int retired_words;
	
	
	/** HOMEGROWN DEGUGGING KILLS!*/
	private Vector log;
	
	public Stats()
	{
		total_writing_counts = setupVector();
		total_reading_counts = setupVector();
		total_word_count = 0;
		total_test_count = 0;
		running_total_of_reading_word_averages = 0;
		running_total_of_writing_word_averages = 0;
		running_total_of_tests = 0;
		reading_rof = setupVector();
		r_rof_milliseconds = setupVector();
		r_rof_running_count = setupVector();
		writing_rof = setupVector();
		w_rof_milliseconds = setupVector();
		w_rof_running_count = setupVector();
		
		reading_hash = new HashMap();
		reading_list = new ArrayList();
		writing_hash = new HashMap();
		writing_list = new ArrayList();
		
		word_test_dates = new WordTestDates(allowed_number_of_levels);
		words_last_test_reading_time = 0;
		words_last_test_writing_time = 0;
		words_last_test_reading_level = "0";
		words_last_test_writing_level = "0";
		retired_words = 0;
		
		// debuggin
		log = new Vector();
	}
	
	/**
	*<p>This is the sample xml format:
		<p><word>
			<p><text>바닷가</text>
			<p><definition>beach</definition>
			<p><level>1</level>
			<p><writing-level>6</writing-level>
			<p><reading-level>3</reading-level>
			<p><test>
				<p><date>Sun Jan 30 13:43:01 PST 2005</date>
				<p><file>level 0 reading.test</file>
				<p><grade>pass</grade>
			<p></test>
		<p></word>
	*<p>This method collects the reading and writing levels of words or phrases.
	*<p>It can calculate the average reading and writing levels, as well as counting
	* the number of words at each level, which are stored in a vector.
	*/
	public void wordStats(Vector words)
	{
		words_last_test_date = null;
		num_of_words = words.size();
		log.add("words "+num_of_words);
		int i = 0; Word word;
		int w_run_total = 0; writing_counts = new Vector();
		int r_run_total = 0; reading_counts = new Vector();
		writing_counts = setupVector();
		reading_counts = setupVector();
		while (i<num_of_words)
		{
			word = (Word)words.get(i);
			try
			{
				evaluateWordRateOfForgetting(word);
			} catch (java.lang.ArrayIndexOutOfBoundsException aioob) 
			{
				i++;
				break; 
			}
			if (!isWordRetired(word))
			{
				int w_level = (int)word.getWritingLevel();
				int r_level = (int)word.getReadingLevel();
				if (r_level > (allowed_number_of_levels-1)) {r_level = (allowed_number_of_levels-1);}
				if (w_level > (allowed_number_of_levels-1)) {w_level = (allowed_number_of_levels-1);}
				try
				{
					String writ_level = (String)writing_counts.get(w_level);
					String read_level = (String)reading_counts.get(r_level);
					int w_inc = Integer.parseInt(writ_level);
					int r_inc = Integer.parseInt(read_level);
					w_inc++;r_inc++;
					writing_counts.set(w_level, Integer.toString(w_inc));
					reading_counts.set(r_level, Integer.toString(r_inc));
				} catch (java.lang.ArrayIndexOutOfBoundsException e)
				{/* not sure why we were going out of bounds here */}
				w_run_total = w_run_total+w_level;
				r_run_total = r_run_total+r_level;
				running_total_of_reading_word_averages=running_total_of_reading_word_averages+r_level;
				running_total_of_writing_word_averages=running_total_of_writing_word_averages+w_level;
			}
			total_word_count++;
			i++;
		}
		collectTotalWordCounts(writing_counts, reading_counts);
		double d_num_of_words = new Double(Integer.toString(num_of_words)).doubleValue();
		avg_reading_level = (r_run_total/d_num_of_words);
		avg_writing_level = (w_run_total/d_num_of_words);
	}
	
	/**
	 * Retired words are considered learned and therefore are not included in the word level
	 * stats, and are not usually tested.
	 * @param word
	 * @return
	 */
	private boolean isWordRetired(Word word)
	{
		boolean retired = false;
		try
		{
			retired = word.getRetired();
			if (retired)
			{
				retired_words++;
				log.add("word "+word.getText()+word.getDefinition()+" is retired");
			}
		} catch (java.lang.NullPointerException npe)
		{
			// no info
		}
		return retired;
	}
	
	public int getRetiredWords()
	{
		return retired_words;
	}
	
	/**
	*<p>This method has to cycle through all the tests and evaluate them to determine
	* when a word goes up a level, and how long it takes before it then fails a test and
	* goes back down.  For instance, if a user passes a word on a writing test, and then
	* two weeks later fails on the word, and then another word is failed after four weeks,
	* then the average Rate Of Forgetting would be two three weeks.
	*
	*<p>This means that the user should be tested on words that has gone up a level 
	* (or at least reviewing the word, for instance by getting an e-mail thats lists the words
	* about to pass the date of the R.O.F.) before the R.O.F date has passed.
	*<p>First I think we should create two lists for reading and writing that consist of
	* the date and the score sorted by date.  Then we cycle thru the lists and keep track
	* of the level as it goes up and down, and then create two more lists that contain the
	* average time elapsed before a failed test stored at the failed level.
	* Does that make sense?
	*/
	public void getRateOfForgetting(Word word)
	{
		ArrayList reading_tests = new ArrayList();
		ArrayList writing_tests = new ArrayList();
		Test[] tests = word.getTests();
		int number = tests.length;
		int i = 0;
		while (i<number)
		{
			try
			{
				Vector return_args = unbindTest(i, tests, reading_tests, writing_tests);
				reading_tests = (ArrayList)return_args.get(0);
				writing_tests = (ArrayList)return_args.get(1);
			} catch (java.lang.NullPointerException npe)
			{
				log.add(npe.getMessage());
				// ignore problems but go on to next test
			}
			i++;
		}
		Comparator comparator = new BeanComparator("date");
		Collections.sort(reading_tests, comparator);
		Collections.sort(writing_tests, comparator);
		try
		{
			evaluateTests(reading_tests);
		} catch (java.lang.NullPointerException npe)
		{}
		try
		{
			evaluateTests(writing_tests);
		} catch (java.lang.NullPointerException npe)
		{}
	}
	
	/**
	*<p>Returns a Vector of two ArrayLists.  The first is the reading tests, and the second
	* is the writing tests.  We chose this rout because Java can only return one
	* object, and we thought it better than setting member variables.
	* The test object properties are filled in with the type and level.  This method
	* should be private but was made public for testing purpose.
	*<p>Since the early development team used xml files, the test file names contained
	* the type of test as well as the level tested.  Of course this information was in
	* the test file itself as xml elements.  But when test histories were created,
	* only the file name of the test was recorded, making it necessary to parse the
	* test name to get this information and then set it back as a Vector of test objects
	* with the level and type set.
	*<p>Silly early developers!  Like to play with xml files!  Thank god the wise
	* current developers are going to use a database and a sensible object relation bridge
	* and at such a time this method will not be necessary.
	*<p>(Don't believe him, for he is the very same early developer who talks to himself
	* and has yet to figure out hoe to use this promised ORB mentioned above)
	*/
	public Vector unbindTest(int i, Test[] tests, ArrayList reading_tests, ArrayList writing_tests)
	{
		String str_date = new String();String type = new String();String level = new String();
		Vector return_args = new Vector();
		Test test = tests[i];
		try
		{
			str_date = test.getDate();
			type = getTestType(test.getName());
			level = getTestLevel(test.getName());
		} catch (java.lang.NullPointerException npe)
		{
		}
		if (type==null||str_date==null)
		{
			// it must be an olf beginning, or intermediate test
		}
		else 
		{
			test.setType(type);
			test.setLevel(level);
			if (type.equals("reading"))
			{
				reading_tests.add(test);
			} else
			{
				writing_tests.add(test);
			}
		}
		return_args.add(reading_tests);
		return_args.add(writing_tests);
		return return_args;
	}
	
	
	//---------------------------------------------------------new methods
	public void evaluateWordTests(Vector words)
	{
		int size = words.size();
		int index = 0;
		//log.add("********** evaluateWordTests words "+size);
		while (index<size)
		{
			Word word = (Word)words.get(index);
			evaluateWordRateOfForgetting(word);
			index++;
		}
	}
	
	/**
	*<p>This method has to cycle through all the tests and evaluate them to determine
	* when a word goes up a level, and how long it takes before it then fails a test and
	* goes back down.  For instance, if a user passes a word on a writing test, and then
	* two weeks later fails on the word, and then another word is failed after four weeks,
	* then the average Rate Of Forgetting would be two three weeks.
	*
	*<p>This means that the user should be tested on words that has gone up a level 
	* (or at least reviewing the word, for instance by getting an e-mail thats lists the words
	* about to pass the date of the R.O.F.) before the R.O.F date has passed.
	*<p>First I think we should create two lists for reading and writing that consist of
	* the date and the score sorted by date.  Then we cycle thru the lists and keep track
	* of the level as it goes up and down, and then create two more lists that contain the
	* average time elapsed before a failed test stored at the failed level.
	* Does that make sense?
	*/
	public void evaluateWordRateOfForgetting(Word word)
	{
		Test[] tests = word.getTests();
		reading_hash = new HashMap(); reading_list = new ArrayList();
		writing_hash = new HashMap(); writing_list = new ArrayList();
		int number = 0;
		try
		{
			number = tests.length;
		} catch (java.lang.NullPointerException npe)
		{
			number = 0;
		}
		int i = 0;
		while (i<number)
		{
			try
			{
				parseTestName(tests[i]);
			} catch (java.lang.NullPointerException npe)
			{
			}
			i++;
		}
		Collections.sort(reading_list); Collections.sort(writing_list);
		try
		{
			evaluateROF(reading_list, reading_hash); evaluateROF(writing_list, writing_hash);
		} catch (java.lang.NullPointerException npe)
		{
		} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
		{
		}
	}
	
	/**
	*<p>We have to get the milliseconds from the date so that another list can be created which
	* can then be sorted so that the test objects, which are stored in hashes with the milliseconds
	* as the index can be retrieved in historical order.
	*<p>Here are some silly old notes from the earlier implementations that failed because we
	* tried to sort a list of beans based on the date property which was a string.
	*<p>This method doesn't work.  POssibly if they are stored as Date objects, but I couldn't
	* get that method to work under tests in Domartin either, so we went with list/hashmap method.
	*<p>Returns a Vector of two ArrayLists.  The first is the reading tests, and the second
	* is the writing tests.  We chose this rout because Java can only return one
	* object, and we thought it better than setting member variables.
	* The test object properties are filled in with the type and level.  This method
	* should be private but was made public for testing purpose.
	*<p>Since the early development team used xml files, the test file names contained
	* the type of test as well as the level tested.  Of course this information was in
	* the test file itself as xml elements.  But when test histories were created,
	* only the file name of the test was recorded, making it necessary to parse the
	* test name to get this information and then set it back as a Vector of test objects
	* with the level and type set.
	*<p>Silly early developers!  Like to play with xml files!  Thank god the wise
	* current developers are going to use a database and a sensible object relation bridge
	* and at such a time this method will not be necessary.
	*<p>(Don't believe him, for he is the very same early developer who talks to himself
	* and has yet to figure out hoe to use this promised ORB mentioned above)
	*/
	public void parseTestName(Test test)
	{
		String str_date = new String();
		String type = new String();
		String level = new String();
		long milliseconds = 0;
		Vector return_args = new Vector();
		try
		{
			str_date = test.getDate();
			type = getTestType(test.getName());
			level = getTestLevel(test.getName());
		} catch (java.lang.NullPointerException npe)
		{
			//npe.printStackTrace();
			//log.add("********** test "+test.getName()+" is null");
		}
		if (type==null||str_date==null)
		{
			// it must be an old test name
			//log.add("********** test "+test.getName()+" is invalid");
		} else  
		{
			test.setType(type);
			test.setLevel(level);
			long mill = Domartin.getMilliseconds(str_date);
			test.setMilliseconds(mill);
			String mill_str = new String(Long.toString(mill));
			if (type.equals("reading"))
			{
				reading_hash.put(Long.valueOf(mill_str), test);
				reading_list.add(Long.valueOf(mill_str));
			} else
			{
				writing_hash.put(Long.valueOf(mill_str), test);
				writing_list.add(Long.valueOf(mill_str));
			}
			//log.add("********** parseTestName "+mill_str+" added");
		}
	}
	
	/**
	*This method should be private except for testing.
			<p><test>
				<p><date>Sun Jan 30 13:43:01 PST 2005</date>
				<p><file>level 0 reading.test</file>
				<p><grade>pass</grade>
			<p></test>
	*
	*/
	public void evaluateROF(ArrayList list, HashMap hash)
	{
		boolean pass_flag = false;
		String  pass_date = new String("0");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Long list_long = (Long)list.get(i);
			Test test = (Test)hash.get(list_long);
			String level = (String)test.getLevel();
			String grade = (String)test.getGrade();
			String date  = (String)test.getDate();
			String type  = (String)test.getType();
			try
			{
				int int_level = Integer.parseInt(level);
				if (int_level > allowed_number_of_levels) {int_level = allowed_number_of_levels;}
				if (grade.equals("pass"))
				{
					// the last past level is only one higher, or at max level if it is another pass, the new pass test info is captured.
					pass_flag = true;
					pass_date = date;
					log.add("% "+i+" %% pass_flag true");
				} else if (grade.equals("fail")&&pass_flag==true)
				{
					long date_delta = getDateDelta(date, pass_date);
					if (type.equals("reading"))
					{
						updateReadingROF(date_delta, int_level);
					} else if (type.equals("writing"))
					{
						updateWritingROF(date_delta, int_level);
					}
					pass_flag = false;
				}
			} catch (java.lang.NumberFormatException nfe)
			{
				// There is no word level, so skip this test
			}
			i++;
		}
	}	
	
	//-------------------------------------------------- end new methods
	
	/**
	*This method should be private except for testing.
			<p><test>
				<p><date>Sun Jan 30 13:43:01 PST 2005</date>
				<p><file>level 0 reading.test</file>
				<p><grade>pass</grade>
			<p></test>
	*
	*/
	public void evaluateTests(ArrayList list)
	{
		ArrayList tests = Domartin.sortList(list, "date");
		boolean pass_flag = false;
		String  pass_date = new String("0");
		int size = tests.size();
		int i = 0;
		//log.add("%%%%%%%%%%%%%%%% evalutating "+size+" tests --------------");
		while (i<size)
		{
			Test test = (Test)tests.get(i);
			//log.add("% "+i+" %% "+Transformer.createTable(test));
			String level = (String)test.getLevel();
			//log.add("% "+i+" %%%%%%%%%%%%%%% level "+level);
			if (level == null)
			{
				// skip no conforming test names
				//log.add("% "+i+" %%%%%%%%%%%%%%%% level is null ");
			} else {
				//log.add("%% "+Transformer.createTable(test));
				String grade = (String)test.getGrade();
				String date  = (String)test.getDate();
				String type  = (String)test.getType();
				int int_level = Integer.parseInt(level);
				//log.add("% "+i+" %%%%%%%%%%%%%%% grade "+grade+" type "+type+" level "+level+" date "+date+" pass date "+pass_date);
				if (grade.equals("pass"))
				{
					// the last past level is only one higher, or at max level
					// if it is another pass, the new pass test info is captured.
					pass_flag = true;
					pass_date = date;
					//log.add("% "+i+" %%%%%%%%%%%%%%%%% pass_flag set ");
				} else if (grade.equals("fail")&&pass_flag==true)
				{
					// record date delta
					//log.add("% "+i+" %%%%%%%%%%%%%%%%% pass_date "+pass_date);
					//log.add("% "+i+" %%%%%%%%%%%%%%%%% date "+date);
					long date_delta = getDateDelta(date, pass_date);
					if (type.equals("reading"))
					{
						updateReadingROF(date_delta, int_level);
						//log.add("% "+i+" %%%%%%%%%%%%%-> adding reading at "+int_level+" + "+date_delta);
					} else if (type.equals("writing"))
					{
						updateWritingROF(date_delta, int_level);
						//log.add("% "+i+" %%%%%%%%%%%%-> adding writing at "+int_level+" + "+date_delta);
					}
					//log.add("% "+i+" %%%%%%%%%%%%% pass flag false at delta "+date_delta);
					pass_flag = false;
				} else
				{
					//pass_flag = false;
					//pass_date = new String();
					//log.add("% "+i+" %%%%%%%%%%%%% why are we here??? ");
				}
			}
			//log.add("% "+i+" %%%%%%%%%%%%% end ");
			i++;
		}
	}
	
	/**
	*This method should be private except for testing.
	*/
	public void updateWritingROF(long date_delta, int int_level)
	{
		if (int_level > (allowed_number_of_levels-1)) {int_level = (allowed_number_of_levels-1);}
		String w_rof_m = (String)w_rof_milliseconds.get(int_level);
		if (w_rof_m.equals("0"))
		{
			// THIS SHOULD BE THE FIRST TIME TRHU, SO VALUES ARE 0
			//log.add("--- writing rof set for level "+int_level+" milliseconds "+date_delta);
			w_rof_milliseconds.set(int_level, Long.toString(date_delta));
			w_rof_running_count.set(int_level, "0");
		} else
		{
			try
			{
				long old_milliseconds = Long.decode(w_rof_m).longValue();
				long new_milliseconds = date_delta + old_milliseconds;
				int old_running_count = Integer.parseInt((String)w_rof_running_count.get(int_level));
				int new_running_count = old_running_count+1;
				w_rof_milliseconds.set(int_level, Long.toString(new_milliseconds));
				w_rof_running_count.set(int_level, Integer.toString(new_running_count));
				//log.add("--- writing updated "+w_rof_m+" + "+date_delta+" = "+new_milliseconds+" rc "+new_running_count);
			} catch (java.lang.NumberFormatException nfe)
			{
				// got: Error For input string: "-2177145000" with long old_milliseconds = Long.decode(w_rof_m).longValue();
				//Should really do this in the if statement above
				//log.add("--- writing nfe");
				w_rof_milliseconds.set(int_level, Long.toString(date_delta));
				w_rof_running_count.set(int_level, "0");
			}
		}
	}
	
	/**
	*This method should be private except for testing.
	*/
	public void updateReadingROF(long date_delta, int int_level)
	{
		String r_rof_m = (String)r_rof_milliseconds.get(int_level);
		if (r_rof_m.equals("0"))
		{
			// THIS SHOULD BE THE FIRST TIME TRHU, SO VALUES ARE 0
			//log.add("--- reading rof set for level "+int_level+" milliseconds "+date_delta);
			r_rof_milliseconds.set(int_level, Long.toString(date_delta));
			r_rof_running_count.set(int_level, "0");
		} else
		{
			try
			{
				long old_milliseconds = Long.decode(r_rof_m).longValue();;
				long new_milliseconds = (date_delta + old_milliseconds);
				int old_running_count = Integer.parseInt((String)r_rof_running_count.get(int_level));
				int new_running_count = old_running_count+1;
				r_rof_milliseconds.set(int_level, Long.toString(new_milliseconds));
				r_rof_running_count.set(int_level, Integer.toString(new_running_count));
				//log.add("--- reading updated level "+int_level+" rc "+new_running_count+" - "+r_rof_m+" + "+date_delta+" = "+new_milliseconds);

			} catch (java.lang.NumberFormatException nfe)
			{
				// got: Error For input string: "-2177145000" with long old_milliseconds = Long.decode(w_rof_m).longValue();
				//Should really do this in the if statement above
				//log.add("--- writing nfe ");
				r_rof_milliseconds.set(int_level, Long.toString(date_delta));
				r_rof_running_count.set(int_level, "0");
			}
		}
	}
	
	/**
	* Here we parse the test type.
	* Original tests were like this: basic_writing.test
	* They included level 1 to 3 or something.
	* Therefore we can't use them to establish R.O.F.
	* After that the looked like: level 0 reading.test
	*/
	private String getTestType(String test_file)
	{
		String type = new String();
		String test_file_w_o_ext = Domartin.getFileWithoutExtension(test_file);
		int space = test_file_w_o_ext.lastIndexOf(" ");
		int len = test_file_w_o_ext.length();
		if (len == -1)
		{
			// set non-conforming names to null
			type = null;
		} 
		else
		{
			try
			{
				type = test_file_w_o_ext.substring(space+1, len);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{
				type = null;
			}
		}
		return type;
	}
	
	/**
	* parse level from level 0 reading.test
	*/
	private String getTestLevel(String test_file)
	{
		String level = new String();
		String test_file_w_o_ext = Domartin.getFileWithoutExtension(test_file);
		int space = test_file_w_o_ext.indexOf(" ");
		int len = test_file_w_o_ext.length();
		if (len == -1)
		{
			// set non-conforming names to null
			level = null;
		} 
		else
		{
			try
			{
				level = test_file_w_o_ext.substring(space+1, space+2);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{
				level = null;
			}
			try
			{
				int int_level = Integer.parseInt(level);
			} catch (java.lang.NumberFormatException nfe)
			{
				level = null;
			}
		}
		return level;
	}
	
	/** This method just gets the difference between the two dates
	* in milliseconds.
	*<p>If we re-use the date format and parse position we get a null pointer exception.
	*/
	private long getDateDelta(String later_date, String early_date)
	{
		//log.add("--- later "+later_date+" early "+early_date);
		long delta = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		ParsePosition pp = new ParsePosition(0);
		Date early = sdf.parse(early_date, pp);
		long early_milliseconds = early.getTime();
		SimpleDateFormat lsdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		ParsePosition lpp = new ParsePosition(0);
		Date later = lsdf.parse(later_date, lpp);
		long later_milliseconds = later.getTime();
		delta = (later_milliseconds - early_milliseconds);
		//log.add(later_milliseconds+" - "+early_milliseconds+" = "+delta);
		return delta;
	}
	
	/**This method takes a Hashtable with the test_date-score format.
	* We steps thru the list, adding up the scores and then divides by the number of tests.
	* We also check the dates, hanging on to the latest date for later retrieval in the
	* getLastTest method.
	*/
	public double getAverage(Hashtable tests)
	{
		int number_of_tests = tests.size();
		double running_total = 0.0;
		int i = 0; last_time = 0;
		Enumeration e = tests.keys();
		while (e.hasMoreElements())
		{
			String str_grade = (String)e.nextElement();
			String str_date = (String)tests.get(str_grade);
			Double dbl_score = new Double(str_grade);
			double score = dbl_score.doubleValue();
			running_total = (running_total + score);
			i++;
			compareDates(str_date);
		}
		running_total_of_tests = running_total_of_tests+running_total;
		total_test_count = total_test_count+number_of_tests;
		double average_score = (running_total / number_of_tests);
		return average_score;
	}
	
	public String getLastTestDate()
	{
		return last_date;
	}
	
	public String getLastWordsTestDate()
	{
		return words_last_test_date;
	}
	
	public double getAverageReadingLevel()
	{
		return avg_reading_level;
	}
	
	public double getAverageWritingLevel()
	{
		return avg_writing_level;
	}
	
	public Vector getReadingCounts()
	{
		return reading_counts;
	}
	
	public Vector getWritingCounts()
	{
		return writing_counts;
	}
	
	public int getNumberOfWords()
	{
		return num_of_words;
	}
	
	public int getTotalOfAllWords()
	{
		return total_word_count;
	}
	
	public double getTotalOfReadingWordAverages()
	{
		return (running_total_of_reading_word_averages/total_word_count);
	}
	
	public double getTotalOfWritingWordAverages()
	{
		double long_total_word_count = total_word_count;
		double result = running_total_of_writing_word_averages/long_total_word_count;
		return result;
	}
	
	public Vector getTotalReadingCounts()
	{
		return total_reading_counts;
	}
	
	public Vector getTotalWritingCounts()
	{
		return total_writing_counts;
	}
	
	public int getTotalTestCount()
	{
		return total_test_count;
	}
	
	public double getRunningTotalOfTests()
	{
		return running_total_of_tests;
	}
	
	public ArrayList getReadingList()
	{
		return reading_list;
	}
	
	public ArrayList getWritingList()
	{
		return writing_list;
	}
	
	public HashMap getReadingHash()
	{
		return reading_hash;
	}
	
	public HashMap getWritingHash()
	{
		return writing_hash;
	}
	
	public WordTestDates getWordTestDates()
	{
		return word_test_dates;
	}
	
	/** THis method is for testing only */
	public void setReadingROFMillisecondsAndRC(Vector r_rof_m, Vector r_rof_rc)
	{
		r_rof_milliseconds = r_rof_m;
		r_rof_running_count = r_rof_rc;
	}
	
	/**
	* This method has to take the reading rates of forgetting which span the
	* range of limited levels, right now 0-3, which is saved as milliseconds
	* which have been added to each other by finding the delta between two dates
	* and then divide by the reading running count which is just the number of
	* deltas that have been added, to create the average rate of forgetting.
	*The running count needs to be incremented by one, because they start at 0.
	*/
	public Vector getReadingRateOfForgetting()
	{
		long avg_r_rof = 0;
		long r_rof_m = 0;
		int i = 0;
		while (i<allowed_number_of_levels)
		{
			try
			{
				String str_r_rof_m = (String)r_rof_milliseconds.get(i);
				r_rof_m = Long.parseLong(str_r_rof_m);
			} catch (java.lang.NullPointerException npe)
			{
				r_rof_m = 0;
			}
			int r_rof_rc = Integer.parseInt((String)r_rof_running_count.get(i))+1;
			try
			{
				avg_r_rof = (r_rof_m/r_rof_rc);
			} catch (java.lang.ArithmeticException ae)
			{
				avg_r_rof = 0;
			}
			reading_rof.set(i, Long.toString(avg_r_rof));
			i++;
		}
		return reading_rof;
	}
	
	public long getParticularRateOfForgetting(String type, int index)
	{
		long rof = Long.parseLong("0");
		long level_rof = Long.parseLong("0");
		int level_rc = 0;
		if (type.equals("reading"))
		{
			try
			{
				level_rof = Long.parseLong((String)r_rof_milliseconds.get(index));
			} catch (java.lang.NullPointerException npe)
			{
				level_rof = 0;
			}
			try
			{
				level_rc = Integer.parseInt((String)r_rof_running_count.get(index))+1;
			} catch (java.lang.ArithmeticException ae)
			{
				level_rc = 0;
			}
		} else if (type.equals("writing"))
		{
			try
			{
				level_rof = Long.parseLong((String)w_rof_milliseconds.get(index));
			} catch (java.lang.NullPointerException npe)
			{
				level_rof = 0;
			}
			try
			{
				level_rc = Integer.parseInt((String)w_rof_running_count.get(index))+1;
			} catch (java.lang.ArithmeticException ae)
			{
				level_rc = 0;
			}
		}
		rof = (level_rof/level_rc);
		return rof;
	}
	
	/**
	* Same as getReadingRateOfForgetting().
	*/
	public Vector getWritingRateOfForgetting()
	{
		long w_rof_m = 0;
		long avg_w_rof = 0;
		int i = 0;
		while (i<allowed_number_of_levels)
		{
			try
			{
				String str_w_rof_m = (String)w_rof_milliseconds.get(i);
				w_rof_m = Long.parseLong(str_w_rof_m);
			} catch (java.lang.NullPointerException npe)
			{
				w_rof_m = 0;
			}
			int w_rof_rc = Integer.parseInt((String)w_rof_running_count.get(i))+1;
			try
			{
				avg_w_rof = (w_rof_m/w_rof_rc);
			} catch (java.lang.ArithmeticException ae)
			{
				avg_w_rof = 0;
			}
			writing_rof.set(i, Long.toString(avg_w_rof));
			i++;
		}
		return writing_rof;
	}
	
	/** Debugging method */
	public long rateOfForgetting(int new_running_count, long new_milliseconds)
	{
		long avg_rof = (new_milliseconds/new_running_count);
		return avg_rof;
	}
	
	
	/**We convert the string with DateFormat.parse and get the
	* calendar object and use the after method to determine which
	* is the most recent date, and either return the new date, or
	* the previously set date.  We store both the string date and
	* the number of milliseconds since January 1, 1970, 00:00:00 GMT
	* used in the comparison.
	*<p>Each date was created like this:
	* Date date = new Date();
	* String str_date = date.toString();
	*<p>Using date = df.parse(str_date) w/o the ParsePosition object needs
	* to catch a java.text.ParseException.
	*<p> SAMPLE DATE : Mon Aug 15 08:09:00 PST 2005
	*/
	private void compareDates(String str_date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = sdf.parse(str_date, pp);
		long this_time = date.getTime();
		if (this_time>last_time)
		{
			last_time = this_time;
			last_date = str_date;
		} else 
		{
			if (last_date==null)
			{
				last_time = this_time;
				last_date = str_date;
			} else 
			{
				// last_time should already be set
			}
		}
	}
	
	/**
	*This method goes thru all the writing and reading levels and adds them to the
	* totals for all word categories, which is used when we are trolling categories of
	* words and want to know the totals of the entire group.
	*/
	private void collectTotalWordCounts(Vector writing_counts, Vector reading_counts)
	{
		int i = 0;
		int max = allowed_number_of_levels;
		while (i<max)
		{
			String this_reading_level = (String)reading_counts.get(i);
			String this_writing_level = (String)writing_counts.get(i);
			int r_old = Integer.parseInt((String)total_reading_counts.get(i));
			int w_old = Integer.parseInt((String)total_writing_counts.get(i));
			int r_inc = Integer.parseInt(this_reading_level);
			int w_inc = Integer.parseInt(this_writing_level);
			int new_reading_total = r_old+r_inc;
			int new_writing_total = w_old+w_inc;
			total_reading_counts.set(i, Integer.toString(new_reading_total));
			total_writing_counts.set(i, Integer.toString(new_writing_total));
			i++;
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
	*This is a helper method for word stats.  Each word has a reading and writing
	level, and will also develop speaking and listening levels in the future.
	Each level needs to be initialized so that we can count all the words at each level,
	without getting an OutOfBounds error.
	*/
	private Vector setupVector()
	{
		Vector setup_vector = new Vector();
		int size = allowed_number_of_levels;
		int inde = 0;
		while (inde<size)
		{
			setup_vector.add(inde, "0");
			inde++;
		}
		return setup_vector;
	}	
	
	private Vector setupLongVector()
	{
		Vector setup_vector = new Vector();
		int size = allowed_number_of_levels;
		int inde = 0;
		while (inde<size)
		{
			setup_vector.add(inde, "0");
			inde++;
		}
		return setup_vector;
	}	
	
	/**This helper method goes thru all the Test objects in a Word object
	* to find the most recent test.
	*/
	private void getLastTest(Word word)
	{
		words_last_test_time = 0;
		words_last_test_date = null;
		Test[] tests = word.getTests();
		int number = tests.length;
		int i = 0;
		while (i<number)
		{
			try
			{
				String str_date = tests[i].getDate();
				compareDates(str_date);
			} catch (java.lang.NullPointerException npe)
			{}
			i++;
		}
		String def = word.getDefinition();
		// System.out.println("Stats.getLastTest: word "+def+" has "+number
		//	+" tests with last date "+getLastWordsTestDate());
	}
	
	public void getLastWordTest(Word word)
	{
		Test[] tests = word.getTests();
		int number = 0;
		try
		{
			number = tests.length;
		} catch (java.lang.NullPointerException npe)
		{
			number = 0;
		}
		int i = 0;
		while (i<number)
		{
			try
			{
				String str_date = tests[i].getDate();
				String type = getTestType(tests[i].getName());
				String level = getTestLevel(tests[i].getName());
				if (level==null)
				{
					// old test, we dont know the exact level so skip
				} else
				{
					if (type.equals("reading"))
					{
						compareReadingWordTestDates(str_date, level, word);
					} else if (type.equals("writing"))
					{
						compareWritingWordTestDates(str_date, level, word);
					}
					compareWordDates(str_date);
				}
			} catch (java.lang.NullPointerException npe)
			{}
			i++;
		}
		String def = word.getDefinition();
	}
	
	private void compareReadingWordTestDates(String str_date, String level, Word word)
	{
		if (Integer.parseInt(level) > (allowed_number_of_levels-1)) {level = Integer.toString((allowed_number_of_levels-1));}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = sdf.parse(str_date, pp);
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
	
	/** For debugging only? */
	public Vector getLog()
	{
		return log;
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
	
	public void resetROF()
	{
		reading_rof = setupVector();
		r_rof_milliseconds = setupVector();
		r_rof_running_count = setupVector();
		writing_rof = setupVector();
		w_rof_milliseconds = setupVector();
		w_rof_running_count = setupVector();
		reading_hash = new HashMap();
		reading_list = new ArrayList();
		writing_hash = new HashMap();
		writing_list = new ArrayList();
	}
	
	private void addArrayListToLog(ArrayList list)
	{
		int total = list.size();
		int i = 0;
		while (i<total)
		{
			//log.add(Transformer.createTable(list.get(i)));
			i++;
		}
	}
	
	// debug
	
	private void addErrorToLog(java.lang.NullPointerException npe)
	{
			log.add("toString           : "+npe.toString());
			log.add("getMessage         : "+npe.getMessage());
			log.add("getLocalziedMessage:"+npe.getLocalizedMessage());
			Throwable throwup = npe.getCause();
			Throwable init_cause = npe.initCause(throwup);
			log.add("thowable.msg       :"+init_cause.toString());
			StackTraceElement[] ste = npe.getStackTrace();
			for (int j=0;j<ste.length;j++)
			{
				log.add(j+" - "+ste[j].toString());
				if (j>6)
				{
					log.add("  ...");
					break;
				}
			}
	}

}
