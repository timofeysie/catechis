package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import junit.framework.TestCase;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordLastTestDates;
import org.catechis.WordTestDateUtility;
import org.catechis.Domartin;

public class WordTestDatesUtilityTest extends TestCase
{

	public WordTestDatesUtilityTest(String name) 
	{
		super(name);
	}

	/**
	* 
	*/
	public void testEvaluateWordTestDates()
	{
		int level = 1;
		String type = new String("reading");
		Word word = new Word();
		String expected_name = new String("text");
		word.setText(expected_name);
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		Test[] tests = new Test[3];
		Test test1 = new Test();
		test1.setDate("Mon Aug 15 08:09:00 PST 2005");
		test1.setName("level 0 reading.test");
		test1.setGrade("pass");
		tests[0] = test1;
		Test test2 = new Test();
		String expected_date = new String("Tue Aug 16 08:09:00 PST 2005");
		test2.setDate(expected_date);
		test2.setName("level 1 reading.test");
		test2.setGrade("fail");
		tests[1] = test2;
		Test test3 = new Test();
		String expected_date2 = new String("Wed Aug 17 08:09:00 PST 2005");
		test3.setDate(expected_date2);
		test3.setName("level 1 reading.test");
		test3.setGrade("fail");
		tests[2] = test3;
		word.setTests(tests);

		
		
		//assertEquals(expected_date2, actual_date);
	}
	
	/**
	* 
	*/
	public void testEvaluateListOfWTD()
	{
		String type = new String("reading");
		
		Word word = new Word();
		String expected_name = new String("text");
		word.setText(expected_name);
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		Test[] tests = new Test[3];
		Test test1 = new Test();
		test1.setDate("Mon Aug 15 08:09:00 PST 2005");
		test1.setName("level 0 reading.test");
		test1.setGrade("pass");
		tests[0] = test1;
		Test test2 = new Test();
		String expected_date = new String("Tue Aug 16 08:09:00 PST 2005");
		test2.setDate(expected_date);
		test2.setName("level 1 reading.test");
		test2.setGrade("fail");
		tests[1] = test2;
		Test test3 = new Test();
		String expected_date2 = new String("Wed Aug 17 08:09:00 PST 2005");
		test3.setDate(expected_date2);
		test3.setName("level 1 reading.test");
		test3.setGrade("fail");
		tests[2] = test3;
		word.setTests(tests);
		
		Word word2 = new Word();
		String expected_name2 = new String("text2");
		word2.setText(expected_name2);
		word2.setDefinition("def2");
		word2.setWritingLevel(0);
		word2.setReadingLevel(0);
		Test[] tests2 = new Test[3];
		Test test11 = new Test();
		test11.setDate("Mon Aug 15 08:09:00 PST 2005");
		test11.setName("level 0 reading.test");
		test11.setGrade("pass");
		tests2[0] = test11;
		Test test22 = new Test();
		String expected_date3 = new String("Tue Aug 16 08:09:00 PST 2005");
		test22.setDate(expected_date3);
		test22.setName("level 1 reading.test");
		test22.setGrade("pass");
		tests2[1] = test22;
		Test test33 = new Test();
		String expected_date4 = new String("Wed Aug 17 08:09:00 PST 2005");
		test33.setDate(expected_date4);
		test33.setName("level 2 reading.test");
		test33.setGrade("pass");
		tests2[2] = test33;
		word2.setTests(tests2);
		
		Vector words = new Vector();
		words.add(word);
		words.add(word2);
		WordTestDateUtility wtd_utility = new WordTestDateUtility(3);
		wtd_utility.evaluateListOfWTD(words, type);
		WordLastTestDates ltd = wtd_utility.getWordLastTestDates(type);
		ArrayList actual_key_list = ltd.getSortedWLTDKeys();
		int size = actual_key_list.size();
		Long long_key = new Long("0");
		try
		{
			long_key = (Long)actual_key_list.get(size);
		} catch (java.lang.IndexOutOfBoundsException ioobe)
		{
			//System.out.println("WTDUT_____size_______"+size); 	// 1
		}
		String string_key = new String(long_key.toString());
		Word actual_word = new Word();
		String actual_text = new String();
		try
		{
			actual_word = ltd.getWord(string_key);
			actual_text = actual_word.getText();
		} catch (java.lang.NullPointerException npe)
		{}
		//System.out.println("WTDUT_________________________________");
		//System.out.println("WTDUT: "+actual_text+" "+string_key);	// 0
		//System.out.println("WTDUT_________________________________");
		int inde = 0;
		while (inde<size)
		{
			Long l_key = (Long)actual_key_list.get(inde);
			long key = l_key.longValue();
			String str_key = new String(Long.toString(key));
			Word test_word = ltd.getWord(str_key);
			try
			{
				//System.out.println(key+" "+test_word.getText());
			} catch (java.lang.NullPointerException npe)
			{
				//System.out.println(key+" is null");		// 1124294940000 is null
			}
			inde++;
		}
		//System.out.println("WTDUT_________________________________");
		String actual_date = wtd_utility.getWordsLastTestDate(type);
		assertEquals(expected_date2, actual_date);
	}
	
	/*
	public void testGetWordObjects()
	{
		String type = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Vector test_words = store.getWordObjects("october-test", "word_user");
		WordTestDateUtility wtd_utility = new WordTestDateUtility(3);
		
		wtd_utility.evaluateListOfWTD(test_words, type);
		WordLastTestDates ltd = wtd_utility.getWordLastTestDates(type);
		ArrayList key_list = ltd.getSortedWLTDKeys();
		//System.out.println("testGetWordObjects ---------- "+type+" words "+test_words.size()+" keys "+key_list.size());
		int size = key_list.size();
		int inde = 0;
		while (inde<size)
		{
			Long l_key = (Long)key_list.get(inde);
			long key = l_key.longValue();
			String str_key = new String(Long.toString(key));
			Word test_word = ltd.getWord(str_key);
			try
			{
				//System.out.println(key+" "+test_word.getText());
			} catch (java.lang.NullPointerException npe)
			{
				//System.out.println(key+" is null");
			}
			inde++;
		}	   
		Vector log = wtd_utility.getLog();
		printLog(log);
		//System.out.println("testGetWordObjects ---------- end");
		//assertEquals(expected_writing_level, actual_writing_level);
	}*/
	
	public void testEvaluateWordsFirstTestDate()
	{
		
		Word word = new Word();
		String expected_date = new String("Mon Aug 15 08:09:00 PST 2005");
		word.setText("text");
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		Test[] tests = new Test[3];
		Test test1 = new Test();
		test1.setDate(expected_date);
		test1.setName("level 0 reading.test");
		test1.setGrade("pass");
		tests[0] = test1;
		Test test2 = new Test();
		String expected_date1 = new String("Tue Aug 16 08:09:00 PST 2005");
		test2.setDate(expected_date1);
		test2.setName("level 1 reading.test");
		test2.setGrade("fail");
		tests[1] = test2;
		Test test3 = new Test();
		String expected_date2 = new String("Wed Aug 17 08:09:00 PST 2005");
		test3.setDate(expected_date2);
		test3.setName("level 1 reading.test");
		test3.setGrade("fail");
		tests[2] = test3;
		word.setTests(tests);
		WordTestDateUtility wtd_utility = new WordTestDateUtility();
		String actual_date = wtd_utility.evaluateWordsFirstTestDate(word);
		//printLog(wtd_utility.getLog());
		//System.out.println("fucl "+actual_date);
		assertEquals(expected_date, actual_date);
	}
	
	public void testEvaluateWordsFirstTestDate2()
	{
		//System.err.println("WTDU.testEvaluateWordsFirstTestDate2 ====");
		File path_file = new File("");
	    	String current_dir = path_file.getAbsolutePath();
	    	String user_name = new String("guest");
	    	//String category = new String("october1.xml");
		String category = new String("september1.xml");
		//String search_text = "e����";
		//String search_text = "Got wet";
		String search_text = "sometimes";
		String search_element = "definition";
		String expected_date = "Sat Nov 13 10:09:27 PST 2004";
		FileStorage fs = new FileStorage(current_dir);
		Word word = fs.getWordObject(search_element, search_text, category, user_name);
		//dumpLog(Transformer.createTable(word));
		WordTestDateUtility wtd_utility = new WordTestDateUtility();
		String actual_date = wtd_utility.evaluateWordsFirstTestDate(word);
		//System.err.println("testEvaluateWordsFirstTestDate2: definition "+word.getDefinition());
		//System.err.println("testEvaluateWordsFirstTestDate2: first date "+actual_date);
		//printLog(wtd_utility.getLog());
		assertEquals(expected_date, actual_date);
	}
	
	public void testGetWordTestTimes()
	{
		System.err.println("WTDU.testGetWordTestTimes ====");
		File path_file = new File("");
	    	String current_dir = path_file.getAbsolutePath();
	    	String user_name = new String("guest");
	    	//String category = new String("october1.xml");
		String category = new String("september1.xml");
		//String search_text = "e����";
		//String search_text = "Got wet";
		String search_text = "sometimes";
		String search_element = "definition";
		//String search_element = "text";
		FileStorage fs = new FileStorage(current_dir);
		Word word = fs.getWordObject(search_element, search_text, category, user_name);
		dumpLog(Transformer.createTable(word));
		WordTestDateUtility wtd_utility = new WordTestDateUtility();
		wtd_utility.setWordTestType("writing");
		String actual_date = wtd_utility.evaluateWordsFirstTestDate(word);
		Vector actual_times = wtd_utility.getWordTestTimes();
		String actual_time = (String)actual_times.get(1);
		String expected_time = "1246498000";
		printLog(actual_times);
		
		//printLog(wtd_utility.getLog());
		//assertEquals(expected_time, actual_time);
	}
	
	/*
	public void testGetTestTimes()
	{
		System.err.println("WTDU.testGetTestTimes ====");
		File path_file = new File("");
	    	String current_dir = path_file.getAbsolutePath();
	    	String user_name = new String("guest");
	    	//String category = new String("october1.xml");
		String category = new String("september1.xml");
		//String search_text = "e����";
		//String search_text = "Got wet";
		String search_text = "sometimes";
		String search_element = "definition";
		//String search_element = "text";
		FileStorage fs = new FileStorage(current_dir);
		Word word = fs.getWordObject(search_element, search_text, category, user_name);
		System.err.println("word --------------------------");
		dumpLog(Transformer.createTable(word));
		WordTestDateUtility wtd_utility = new WordTestDateUtility();
		//wtd_utility.setWordTestType("reading");
		wtd_utility.setWordTestType("writing");
		Vector actual_times = wtd_utility.getTestTimes(word);
		String actual_time = new String();
		try
		{
			actual_time = (String)actual_times.get(1);
		} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		{
			System.err.println("npe - no times!");
		}
		
		long first_time = wtd_utility.getWordsFirstTestTime();
		String first_kiss = Transformer.simplifyDate(Long.toString(first_time));
		System.err.println("first test date = "+first_kiss);
		String expected_time = "1246498000";
		System.err.println("test times --------------------------");
		printLog(actual_times);
		System.err.println("-------------------------------------");
		printLog(wtd_utility.getLog());
		//printLog(wtd_utility.getLog());
		//assertEquals(expected_time, actual_time);
	
	}
	*/
	
	private void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.err.println(i+" "+log.get(i));
			i++;
		}
	}
	
    private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

    }

}
