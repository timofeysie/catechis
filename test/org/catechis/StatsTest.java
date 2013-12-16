package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Vector;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;
import junit.framework.TestCase;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordTestDates;
import org.catechis.Domartin;
import org.catechis.JDOMSolution;

public class StatsTest extends TestCase
{

	private Stats stats;
	private Hashtable actual_tests;
	
	public StatsTest (String name) 
	{
		super(name);
	}
    
	protected void setUp() throws Exception
	{
    		stats = new Stats();
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		actual_tests = store.getTests("sample", "guest");
	}
	
	/**
	* The sample.test file should have three tests with scores of 70,80 and 90.
	*/
	public void testGetAverage()
	{
		double actual_average = stats.getAverage(actual_tests);
		double expected_average = 80;
		assertEquals(expected_average, actual_average, 0);
		int size = actual_tests.size();
	}
	
	public void testGetLastDate()
	{
		double actual_average = stats.getAverage(actual_tests);
		String expected_date = new String("Mon Aug 15 08:09:00 PST 2005");
		String actual_date = stats.getLastTestDate();
		assertEquals(expected_date, actual_date);
	}
	
	/**
	 * THis method should only test getLastWordTest date, as that is the only method that uses compareWordDates
	 * which modifies words_last_test_date which is returned by getLastWordsTestDate.
	 */
	public void testGetLastWordDate()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector actual_words = store.getWordObjects("date test words", "test_user");
		//System.out.println("StatsTest.testGetLastWordDate: actual words "+actual_words.size());
		//Stats stats = new Stats();
		stats = new Stats(); // the old version used a local copy.  duh!
		int size = actual_words.size();
		int i = 0;
		while (i<size)
		{
			stats.getLastWordTest((Word)actual_words.get(i));
			i++;
		}
		//stats.wordStats(actual_words);
		String expected_last_date = new String("Tue Mar 15 03:33:15 PDT 2005");
		String actual_last_date = stats.getLastWordsTestDate();
		//System.out.println("StatsTest.testGetLastWordDate: actual "+actual_last_date);
		//dumpLog(stats.getLog());
		assertEquals(expected_last_date, actual_last_date);
	}
	
	public void testWordStats()
	{
		/*
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector actual_words = store.getWordObjects("september", "guest");
		*/
		Vector actual_words = new Vector();
		Word word0 = new Word();
		word0.setText("text");
		word0.setDefinition("def");
		word0.setWritingLevel(0);
		word0.setReadingLevel(1);
		Word word1 = new Word();
		word1.setText("text");
		word1.setDefinition("def");
		word1.setWritingLevel(0);
		word1.setReadingLevel(2);
		Word word2 = new Word();
		word2.setText("text");
		word2.setDefinition("def");
		word2.setWritingLevel(0);
		word2.setReadingLevel(3);
		Word word3 = new Word();
		word3.setText("text");
		word3.setDefinition("def");
		word3.setWritingLevel(0);
		word3.setReadingLevel(2);
		actual_words.add(word0);
		actual_words.add(word1);
		actual_words.add(word2);
		actual_words.add(word3);
		Stats stats = new Stats();
		stats.wordStats(actual_words);
		double actual_reading_avg = stats.getAverageReadingLevel();
		double expected_reading_avg = 2;
		double delta = 0;
		assertEquals(expected_reading_avg, actual_reading_avg, delta);
	}
	
	public void testGetReadingCounts()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector words = store.getWordObjects("test words", "guest");
		Stats stats = new Stats();
		stats.wordStats(words);
		Vector reading_counts = stats.getWritingCounts();
		String reading_count = (String)reading_counts.get(0);
		int actual_number_of_words_at_reading_level_0 = Integer.parseInt(reading_count);
		int expected_number_of_words_at_reading_level_0 = 2;
		assertEquals(expected_number_of_words_at_reading_level_0, 
			actual_number_of_words_at_reading_level_0);
	}
	
	public void testGetTotalOfAllWords()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("test words", "guest");
		Vector word_words = store.getWordObjects("word", "guest");
		Stats stats = new Stats();
		stats.wordStats(test_words);
		stats.wordStats(word_words);
		int actual_total_of_all_words = stats.getTotalOfAllWords();
		int expected_total_of_all_words = 5;
		assertEquals(expected_total_of_all_words, actual_total_of_all_words);
	}
	
	public void testGetTotalCounts()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("test words", "guest");
		Vector word_words = store.getWordObjects("word", "guest");
		Stats stats = new Stats();
		stats.wordStats(test_words);
		stats.wordStats(word_words);
		Vector actual_total_reading_counts = stats.getTotalReadingCounts();
		String actual_level_0_reading_count = (String)actual_total_reading_counts.get(0);
		String expected_level_0_reading_count = new String("2");
		assertEquals(expected_level_0_reading_count, actual_level_0_reading_count);
	}
	
	public void testGetNumberOfWords()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector oct_words = store.getWordObjects("october", "test_user");
		Stats stats = new Stats();
		stats.wordStats(oct_words);
		int expected_number_of_words = 73;
		int actual_number_of_words = stats.getNumberOfWords();
		//System.out.println("StatsTest.testGetNumberOfWords: actual number of words in oct "+actual_number_of_words);
		assertEquals(expected_number_of_words, actual_number_of_words);
	}
	
	public void testGetAverageReadingLevel()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector oct_words = store.getWordObjects("october", "test_user");
		Stats stats = new Stats();
		stats.wordStats(oct_words);
		double delta = 0;
		double expected_reading_average = 1.1369863013698631;
		double actual_reading_average = stats.getAverageReadingLevel();
		assertEquals(expected_reading_average, actual_reading_average, delta);
	}
	
	public void testEvaluateWordTests()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("october-test", "word_user");
		Stats stats = new Stats();
		stats.evaluateWordTests(test_words);
		
		
		// debug
		//System.out.println("testEvaluateWordTests////////////////////");
		//Vector log = stats.getLog();
		//printLog(log);
		String expected_reading_rof = new String("386 days 11 hours 55 minutes 50 seconds");
		String actual_reading_rof_mill = Long.toString(stats.getParticularRateOfForgetting("reading", 2));
		String actual_reading_rof = Domartin.getElapsedTime(actual_reading_rof_mill);
		//Vector r_rof = stats.getReadingRateOfForgetting();
		//Vector w_rof = stats.getWritingRateOfForgetting();
		/*
		int total = r_rof.size();
		int i = 0;
		System.out.println("Reading Level of forgetting =================");
		while (i<total)
		{
			System.out.println("Level "+i+" rate of forgetting "+Domartin.getElapsedTime((String)r_rof.get(i)));
			i++;
		}
		System.out.println("Writing Level of forgetting =================");
		total = w_rof.size();
		i = 0;
		while (i<total)
		{
			System.out.println("Level "+i+" rate of forgetting "+Domartin.getElapsedTime((String)w_rof.get(i)));
			i++;
		}
		System.out.println("testEvaluateWordTests////////////////////");
		*/
		assertEquals(expected_reading_rof, actual_reading_rof);
	}
	
	/**
	*	private String date;
		private String name;
		private String grade;
		private String type;
		private String level;
	*/
	public void testEvaluateTests()
	{
	    // void evaluateTests(ArrayList tests)
	    ArrayList list = new ArrayList();
	    
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    test1.setType("reading");
	    test1.setLevel("0");
	    list.add(test1);
	    
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    test2.setType("reading");
	    test2.setLevel("1");
	    list.add(test2);
	    
	    Stats stats = new Stats();
	    stats.evaluateTests(list);
	    
	    Vector r_rof = stats.getReadingRateOfForgetting();
	    String actual_level1_rof = (String)r_rof.get(1);
	    String expected_level1_rof = new String ("86400000");
	    
	    assertEquals(expected_level1_rof, actual_level1_rof);
	}
	
	public void testUnbindTest()
	{
	    ArrayList reading_tests = new ArrayList();
	    
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    reading_tests.add(test1);
	    
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    reading_tests.add(test2);
	    
	    ArrayList writing_tests = new ArrayList();
	    
	    Test tests [] = new Test[2];
	    tests[0] = test1;
	    tests[1] = test2;
	    int i = 0;
	    
	    Stats stats = new Stats();
	    Vector reading_and_writing_tests = stats.unbindTest(i, tests, reading_tests, writing_tests);
	    ArrayList returned_reading_tests = (ArrayList)reading_and_writing_tests.get(0);
	    Test actual_test = (Test)returned_reading_tests.get(0);
	    String actual_type = actual_test.getType();
	    String actual_level = actual_test.getLevel();
	    
	    String expected_type = new String("reading");
	    String expected_level = new String("0");
	    assertEquals(expected_type, actual_type);
	    assertEquals(expected_level, actual_level);
	}
	
	public void testGetReadingRateOfForgetting()
	{
	    ArrayList reading_tests = new ArrayList();
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    reading_tests.add(test1);
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    reading_tests.add(test2);
	    Test test3 = new Test();
	    test3.setDate("Wed Aug 17 08:09:00 PST 2005");
	    test3.setName("level 0 reading.test");
	    test3.setGrade("pass");
	    reading_tests.add(test3);
	    Test test4 = new Test();
	    test4.setDate("Thu Aug 18 08:09:00 PST 2005");
	    test4.setName("level 1 reading.test");
	    test4.setGrade("fail");
	    reading_tests.add(test4);
	    Test test5 = new Test();
	    test5.setDate("Fri Aug 19 08:09:00 PST 2005");
	    test5.setName("level 0 reading.test");
	    test5.setGrade("pass");
	    reading_tests.add(test5);
	    Test test6 = new Test();
	    test6.setDate("Sat Aug 20 08:09:00 PST 2005");
	    test6.setName("level 1 reading.test");
	    test6.setGrade("fail");
	    reading_tests.add(test6);
	    
	    Word word = new Word();
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test tests [] = new Test[6];
	    tests[0] = test1;
	    tests[1] = test2;
	    tests[2] = test3;
	    tests[3] = test4;
	    tests[4] = test5;
	    tests[5] = test6;
	    word.setTests(tests);
	    
	    Stats stats = new Stats();
	    stats.evaluateWordRateOfForgetting(word);
	    
	    Vector r_rof = stats.getReadingRateOfForgetting();
	    String actual_level1_rof = (String)r_rof.get(1);
	    String expected_level1_rof = new String ("86400000");
	    assertEquals(expected_level1_rof, actual_level1_rof);
	}
	
	public void testGetWritingRateOfForgetting()
	{
	    ArrayList writing_tests = new ArrayList();
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 writing.test");
	    test1.setGrade("pass");
	    writing_tests.add(test1);
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 writing.test");
	    test2.setGrade("fail");
	    writing_tests.add(test2);
	    Test test3 = new Test();
	    test3.setDate("Wed Aug 17 08:09:00 PST 2005");
	    test3.setName("level 0 writing.test");
	    test3.setGrade("pass");
	    writing_tests.add(test3);
	    Test test4 = new Test();
	    test4.setDate("Thu Aug 18 08:09:00 PST 2005");
	    test4.setName("level 1 writing.test");
	    test4.setGrade("fail");
	    writing_tests.add(test4);
	    Test test5 = new Test();
	    test5.setDate("Fri Aug 19 08:09:00 PST 2005");
	    test5.setName("level 0 writing.test");
	    test5.setGrade("pass");
	    writing_tests.add(test5);
	    Test test6 = new Test();
	    test6.setDate("Sat Aug 20 08:09:00 PST 2005");
	    test6.setName("level 1 writing.test");
	    test6.setGrade("fail");
	    writing_tests.add(test6);
	    
	    Word word = new Word();
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test tests [] = new Test[6];
	    tests[0] = test1;
	    tests[1] = test2;
	    tests[2] = test3;
	    tests[3] = test4;
	    tests[4] = test5;
	    tests[5] = test6;
	    word.setTests(tests);
	    
	    Stats stats = new Stats();
	    stats.evaluateWordRateOfForgetting(word);
	    
	    Vector w_rof = stats.getWritingRateOfForgetting();
	    String actual_level1_rof = (String)w_rof.get(1);
	    String expected_level1_rof = new String ("86400000");
	    /*
	    // debug
	    System.out.println("testGetWritingRateOfForgetting|||||||||||||||||||||");
	    Vector log = stats.getLog();
	    printLog(log);
	    System.out.println("testGetWritingRateOfForgetting||||||||||||||||||||| actual_level1_rof "+actual_level1_rof);
	    */
	    assertEquals(expected_level1_rof, actual_level1_rof);
	}
	
	public void testGetROFReturnMethod()
	{
		Vector r_rof_m  = new Vector();
		Vector r_rof_rc = new Vector();
		String r_rof_milliseconds = new String ("259200000");
		String r_rof_running_count = new String ("2");
		r_rof_m.add("0");
		r_rof_m.add(r_rof_milliseconds);
		r_rof_m.add("0");
		r_rof_m.add("0");
		r_rof_rc.add("0");
		r_rof_rc.add(r_rof_running_count);
		r_rof_rc.add("0");
		r_rof_rc.add("0");
		Stats stats = new Stats();
	        stats.setReadingROFMillisecondsAndRC(r_rof_m, r_rof_rc);
		Vector actual_r_rof_m_v = stats.getReadingRateOfForgetting();
		String actual_r_rof = (String)actual_r_rof_m_v.get(1);
		String expected_r_rof = new String("86400000");
		//System.out.println("testGetROFReturnMethod **** "+actual_r_rof);
		assertEquals(expected_r_rof, actual_r_rof);
	}
	
	public void testGetROFfromWordStats()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("october-test", "word_user");
		Stats stats = new Stats();
		stats.wordStats(test_words);
		Vector r_rof = stats.getReadingRateOfForgetting();
		Vector w_rof = stats.getWritingRateOfForgetting();
		String expected_reading_rof = new String("33393350000");
		String actual_reading_rof = (String)r_rof.get(2);
		//long actual_reading_rof = Long.parseLong(actual_reading_rof_str);
		//Vector log = stats.getLog();
		//printLog(log);
		//System.out.println("testGetROFfromWordStats Level of forgetting ***********"+actual_reading_rof);
		//System.out.println("stats log ************* !!!");
		//printLog(stats.getLog());
		//System.out.println("stats log ************* end");
		//System.out.println("Writing Level of forgetting =================");
		int total = r_rof.size();
		int i = 0;
		while (i<total)
		{
			//System.out.println("Level "+i+" rate of forgetting "+r_rof.get(i));
			i++;
		}
		//System.out.println("Writing Level of forgetting =================");
		total = w_rof.size();
		i = 0;
		while (i<total)
		{
			//System.out.println("Level "+i+" rate of forgetting "+w_rof.get(i));
			i++;
		}
		
		assertEquals(expected_reading_rof, actual_reading_rof);
	}
	
	
	private Vector setupVector(int allowed_number_of_levels)
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
	
	public void testParseTestName()
	{
		Test expected_test = new Test();
		expected_test.setDate("Mon Aug 15 08:09:00 PST 2005");
		expected_test.setName("level 0 reading.test");
		expected_test.setGrade("pass");
		long expected_mill  = Long.parseLong("1124122140000");
		String expected_type  = new String("reading");
		String expected_level = new String("0");
		Stats stats = new Stats();
		stats.parseTestName(expected_test);
		ArrayList list = stats.getReadingList();
		HashMap hash = stats.getReadingHash();
		Long list_long = (Long)list.get(0);
		Test actual_test = (Test)hash.get(list_long);
		String actual_type  = actual_test.getType();
		String actual_level = actual_test.getLevel();
		long actual_mill  = actual_test.getMilliseconds();
		String expected = new String(actual_type+actual_level+actual_mill);
		String actual   = new String(expected_type+expected_level+expected_mill);
		assertEquals(expected, actual);
	}
	
	public void testRetiredWords()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector oct_words = store.getWordObjects("october", "test_user");
		Stats stats = new Stats();
		stats.wordStats(oct_words);
		int expected_number_of_retired_words = 1;
		int actual_number_of_retired_words = stats.getRetiredWords();
		//System.out.println("StatsTest.testGetNumberOfWords: actual number of words in oct "+actual_number_of_words);
		assertEquals(expected_number_of_retired_words, actual_number_of_retired_words);
	}
	
	/*
	public void testGetWordTestDates()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("october-test", "word_user");
		int number_of_words = test_words.size();
		Stats stats = new Stats();
		stats.wordStats(test_words);
		WordTestDates wtd = stats.getWordTestDates();
		ArrayList sorted_reading_dates = wtd.getSortedTestDatesList(0, "reading");
		ArrayList sorted_writing_dates = wtd.getSortedTestDatesList(0, "writing");
		//System.out.println("StatsTest.testGetWordTestDates ++++++++++++++ "+number_of_words+" number of words");
		Vector log = wtd.getLog();
		printLog(log);
		//System.out.println("StatsTest.testGetWordTestDates sorted reading tests +++");
		printWTDArrayLog(sorted_reading_dates, wtd, "0", "reading");
		//System.out.println("StatsTest.testGetWordTestDates sorted writing tests +++");
		printWTDArrayLog(sorted_writing_dates, wtd, "0", "writing");
		//System.out.println("StatsTest.testGetWordTestDates ++++++++++++++");
	}
	*/
	
	/*
	public void testGetWordTestDatesKnown()
	{
	    ArrayList writing_tests = new ArrayList();
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 writing.test");
	    test1.setGrade("pass");
	    writing_tests.add(test1);
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 writing.test");
	    test2.setGrade("fail");
	    writing_tests.add(test2);
	    Test test3 = new Test();
	    test3.setDate("Wed Aug 17 08:09:00 PST 2005");
	    test3.setName("level 0 writing.test");
	    test3.setGrade("pass");
	    writing_tests.add(test3);
	    Test test4 = new Test();
	    test4.setDate("Thu Aug 18 08:09:00 PST 2005");
	    test4.setName("level 1 writing.test");
	    test4.setGrade("fail");
	    writing_tests.add(test4);
	    Test test5 = new Test();
	    test5.setDate("Fri Aug 19 08:09:00 PST 2005");
	    test5.setName("level 0 writing.test");
	    test5.setGrade("pass");
	    writing_tests.add(test5);
	    Test test6 = new Test();
	    test6.setDate("Sat Aug 20 08:09:00 PST 2005");
	    test6.setName("level 1 writing.test");
	    test6.setGrade("fail");
	    writing_tests.add(test6);
	    
	    Word word = new Word();
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test tests [] = new Test[6];
	    tests[0] = test1;
	    tests[1] = test2;
	    tests[2] = test3;
	    tests[3] = test4;
	    tests[4] = test5;
	    tests[5] = test6;
	    word.setTests(tests);
	    
	    ArrayList reading_tests = new ArrayList();
	    test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    reading_tests.add(test1);
	    test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    reading_tests.add(test2);
	    test3 = new Test();
	    test3.setDate("Wed Aug 17 08:09:00 PST 2005");
	    test3.setName("level 0 reading.test");
	    test3.setGrade("pass");
	    reading_tests.add(test3);
	    test4 = new Test();
	    test4.setDate("Thu Aug 18 08:09:00 PST 2005");
	    test4.setName("level 1 reading.test");
	    test4.setGrade("fail");
	    reading_tests.add(test4);
	    test5 = new Test();
	    test5.setDate("Fri Aug 19 08:09:00 PST 2005");
	    test5.setName("level 0 reading.test");
	    test5.setGrade("pass");
	    reading_tests.add(test5);
	    test6 = new Test();
	    test6.setDate("Sat Aug 20 08:09:00 PST 2005");
	    test6.setName("level 1 reading.test");
	    test6.setGrade("fail");
	    reading_tests.add(test6);
	    
	    Word word2 = new Word();
	    word2.setText("text");
	    word2.setDefinition("def");
	    word2.setWritingLevel(0);
	    word2.setReadingLevel(0);
	    Test tests2 [] = new Test[6];
	    tests2[0] = test1;
	    tests2[1] = test2;
	    tests2[2] = test3;
	    tests2[3] = test4;
	    tests2[4] = test5;
	    tests2[5] = test6;
	    word2.setTests(tests2);
	    
	    Vector words = new Vector();
	    words.add(word);
	    words.add(word2);
	    
	    Stats stats = new Stats();
	    stats.wordStats(words);
	    WordTestDates wtd = stats.getWordTestDates();
	    ArrayList sorted_reading_dates = wtd.getSortedTestDatesList(0, "reading");
	    //ArrayList sorted_writing_dates = wtd.getSortedTestDatesList(0, "writing");
	    //System.out.println("StatsTest.testGetWordTestDates ************** ");
	    Vector log = wtd.getLog();
	   // printLog(log);
	    //System.out.println("StatsTest.testGetWordTestDates sorted reading tests +++");
	    printWTDArrayLog(sorted_reading_dates, wtd, "0", "reading");
	    //System.out.println("StatsTest.testGetWordTestDates sorted writing tests +++");
	    //printWTDArrayLog(sorted_writing_dates, wtd, "0", "writing");
	    //System.out.println("StatsTest.testGetWordTestDates **************");
	}	
	*/
	
	
	/*
	public void testAllTests()
	{
		Stats stats = new Stats();
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector tests = new Vector();
		tests.add("level 0 reading");
		tests.add("level 0 writing");
		tests.add("level 1 writing");
		tests.add("level 1 reading");
		tests.add("level 2 writing");
		tests.add("level 2 reading");
		tests.add("level 3 writing");
		tests.add("level 3 reading");
		int num = tests.size();
		int ind = 0;String test;
		String pattern = new String("###.##");
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		while (ind<num)
		{
			test = (String)tests.get(ind);
			Hashtable actual_tests = store.getTests(test, "guest");
			double average = stats.getAverage(actual_tests);
			int size = actual_tests.size();
			String last_date = stats.getLastTestDate();
			String average_output = myFormatter.format(average);
			System.out.println(test+" number of tests: "+size+" avg: "
				+average_output+" last test: "+last_date); //
			ind++;
		}
		int total_test_count = stats.getTotalTestCount();
		double running_total_of_tests = stats.getRunningTotalOfTests();
		System.out.println("Total number of tests: "+total_test_count);
		System.out.println("Aveage test score    : "
			+running_total_of_tests/total_test_count); //
	}
	*/
	
	/*
	public void testAllWords()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String user_name = ("guest");
		FileStorage store = new FileStorage(current_dir);
		/*Vector word_files = new Vector();
		word_files.add("April");
		word_files.add("December");
		word_files.add("February");
		word_files.add("Ganada21to25");
		word_files.add("Ganada26to30");
		word_files.add("January");
		word_files.add("March");
		word_files.add("May");
		word_files.add("November");
		word_files.add("october");
		word_files.add("random words");
		word_files.add("september");
		Vector word_files = store.getWordCategories("primary", user_name);
		dumpLog(word_files);
		int num = word_files.size();
		int ind = 0;String word_file;
		Stats stats = new Stats();
		while (ind<num)
		{
			word_file = (String)word_files.get(ind);
			//System.out.println("JDOMSoltion.testAllWords : "+word_file);
			Vector actual_words = store.getWordObjects(word_file, "guest");
			stats.wordStats(actual_words);
			double actual_writing_avg = stats.getAverageWritingLevel();
			double actual_reading_avg = stats.getAverageReadingLevel();
			Vector r_counts = stats.getReadingCounts();
			Vector w_counts = stats.getWritingCounts();
			String last_date = stats.getLastTestDate();
			int number_of_words = stats.getNumberOfWords();
			/*System.out.println(word_file
				+" words "+number_of_words
				+" r avg "+actual_reading_avg
				+" w avg "+actual_writing_avg
				+" r0-"+r_counts.get(0)+" w0-"+w_counts.get(0)
				+" r1-"+r_counts.get(1)+" w1-"+w_counts.get(1)
				+" r2-"+r_counts.get(2)+" w2-"+w_counts.get(2)
				+" r3-"+r_counts.get(3)+" w3-"+w_counts.get(3)
				+" last test "+last_date); // 	END COMMENT
				
			ind++;
		}
		String pattern = new String("###.##");
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		double towwa = stats.getTotalOfWritingWordAverages();
		double torwa = stats.getTotalOfReadingWordAverages();
		String towwa_output = myFormatter.format(towwa);
		String torwa_output = myFormatter.format(torwa);
		System.out.println("total words "+stats.getTotalOfAllWords());
		System.out.println("words avgerage writing level "
			+towwa_output);
		System.out.println("words avgerage reading level "
			+torwa_output); //	END COMMENT
		Vector all_writing_counts = stats.getTotalWritingCounts();
		Vector all_reading_counts = stats.getTotalReadingCounts();
		int total = all_writing_counts.size();
		int i = 0;
		
		while (i<total)
		{
			System.out.println("Level "+i
				+" number of reading "+all_reading_counts.get(i)
				+" number of writing "+all_writing_counts.get(i));
			i++;
		}
			//END COMMENT
	}
	*/
	
	/**This method is in response to the following error:
	java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 4
	at java.util.Vector.get(Vector.java:709)
	at org.catechis.Stats.updateReadingROF(Stats.java:601)
	at org.catechis.Stats.evaluateROF(Stats.java:474)
	at org.catechis.Stats.evaluateWordRateOfForgetting(Stats.java:360)
	at org.catechis.Stats.wordStats(Stats.java:164)
	at org.catechis.FileStorage.getWordStats(FileStorage.java:424)
	at com.businessglue.struts.LoginAction.loginUser(Unknown Source)
	*
	public void textUpdateReadingROF()
	{
		
	}
	*/
	
	/*
	public void testAllROF()
	{
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		
		/* From a historical point of view, looking back from late 2009, this were the categories in use at the time,
		 * and are a testimony as to how old these tests were when we tried to resurrect the rates of forgetting (how ironic!)
		Vector word_files = new Vector();
		word_files.add("April1");
		word_files.add("April2");
		word_files.add("August");
		word_files.add("December");
		word_files.add("February");
		word_files.add("Ganada21to25-1");
		word_files.add("Ganada21to25-2");
		word_files.add("Ganada26to30");
		word_files.add("Ganada Intermediate 11-15");
		word_files.add("Ganada Intermediate 3-5");
		word_files.add("Ganada Intermediate 6-10");
		word_files.add("January");
		word_files.add("March");
		word_files.add("May");
		word_files.add("November");
		word_files.add("october");
		word_files.add("Passives and Causatives");
		word_files.add("random words 1");
		word_files.add("random words 2");
		word_files.add("september1");
		word_files.add("September 2005");
		word_files.add("september2");
		*
		String user_id = new String("-5519451928541341468");
		//Vector categories = store.getCategories(user_id);
		Vector categories = store.getWordCategories("vocab", user_id);
		int num = categories.size();
		System.out.println("Stats.Test.testAllROF "+num);
		int ind = 0;
		String word_file;
		Stats stats = new Stats();
		while (ind<num)
		{
			word_file = (String)categories.get(ind);
			System.out.println("category "+word_file);
			Vector words = store.getWordObjects(word_file, user_id);
			stats.evaluateWordTests(words);
			ind++;
		}
		Vector r_rof = stats.getReadingRateOfForgetting();
		Vector w_rof = stats.getWritingRateOfForgetting();
		int total = r_rof.size();
		int i = 0;
		System.out.println("Reading Level of forgetting -----------------");
		while (i<total)
		{
			System.out.println("Level "+i+" rate of forgetting "+Domartin.getElapsedTime((String)r_rof.get(i)));
			i++;
		}
		System.out.println("Writing Level of forgetting -----------------");
		total = w_rof.size();
		i = 0;
		while (i<total)
		{
			System.out.println("Level "+i+" rate of forgetting "+Domartin.getElapsedTime((String)w_rof.get(i)));
			i++;
		}
		
	}
	*/
	
	public int filterReadingNull(String num)
	{
		if (num=="null")
			return 0;
		else 
			return Integer.parseInt(num);
	}
	
	
	/** UTILITY 
	public void testCleaning()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String word_file = new String("Ganada26to30.xml");
		Vector words = store.getWordObjects(word_file, "cleaner");
		int tests_cleaned = store.cleanTests(words, word_file);
		System.out.println("StatsTest.testCleaning: cleaned "+tests_cleaned+" from "+word_file);
	}
	
	public void testLimtLevels()
	{
		int levels_corrected = 0;
		int max_level = 3;
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector files = store.getWordCategories("primary", "cleaner");
		int total = files.size();
		int i = 0;
		while (i<total)
		{
			String word_file = (String)files.get(i);
			String path_to_cleaning_file = new String(current_dir+File.separator
				+"files"+File.separator+"cleaner"+File.separator+word_file);
			File clean_file = new File(path_to_cleaning_file);
			JDOMSolution jdom = new JDOMSolution(clean_file);
			int levels_adjusted = jdom.cleanLevels(max_level);
			levels_corrected = levels_corrected+levels_adjusted;
			jdom.writeDocument(path_to_cleaning_file);
			i++;
		}
		System.out.println("StatsTest.testLimitLevels: levels corrected "+levels_corrected);
	}
	*/
	
	private void dumpLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
	}
	
	private void printLog(Vector log)
	{
		/*
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
		*/
	}
	
	private void printArrayLog(ArrayList log)
	{
		/*
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
		*/
	}
	
	private void printWTDArrayLog(ArrayList log, WordTestDates wtd, String level, String type)
	{
		/*
		String def = new String();
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			String date_key = (String)log.get(i);
			Word word = wtd.getSpecificWord(Integer.parseInt(level), type, date_key);
			try
			{
				def = word.getDefinition();
			} catch (java.lang.NullPointerException npe)
			{
				def = new String("null");
			}
			System.out.println("log- "+log.get(i)+" "+def);
			i++;
		}
		*/
	}
	
	public static void main(String args[]) 
	{
        	junit.textui.TestRunner.run(StatsTest.class);
    }
}
