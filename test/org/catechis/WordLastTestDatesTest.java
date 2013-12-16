package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import junit.framework.TestCase;

import java.util.Date;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordLastTestDates;
import org.catechis.file.FileCategories;
import org.catechis.file.WordNextTestDates;
import org.catechis.WordTestDateUtility;
import org.catechis.Domartin;
import org.catechis.Storage;
import org.catechis.FileStorage;
import org.catechis.constants.Constants;;

public class WordLastTestDatesTest extends TestCase
{
	
	private String type;
	private int level;
	private Word word1;
	private Word word2;
	private Word word3;
	public WordLastTestDatesTest(String name) 
	{
		super(name);
	}
	
	protected void setUp()
	{
		level = 1;
		type = new String("reading");
		
		word1 = new Word();
		String expected_name = new String("word1");
		word1.setText(expected_name);
		word1.setDefinition("def");
		word1.setWritingLevel(0);
		word1.setReadingLevel(0);
		word1.setRetired(true);
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
		word1.setTests(tests);
		
		word2 = new Word();
		String expected_name2 = new String("word2");
		word2.setText(expected_name2);
		word2.setDefinition("def2");
		word2.setWritingLevel(0);
		word2.setReadingLevel(0);
		Test[] tests2 = new Test[3];
		Test test11 = new Test();
		test11.setDate("Mon Aug 15 09:09:00 PST 2005");
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
		String expected_date4 = new String("Wed Aug 17 09:09:00 PST 2005");
		test33.setDate(expected_date4);
		test33.setName("level 2 reading.test");
		test33.setGrade("pass");
		tests2[2] = test33;
		word2.setTests(tests2);
		
		word3 = new Word();
		String expected_name3 = new String("word3");
		word3.setText(expected_name3);
		word3.setDefinition("def2");
		word3.setWritingLevel(0);
		word3.setReadingLevel(0);
		Test[] tests3 = new Test[3];
		Test test111 = new Test();
		test111.setDate("Mon Aug 15 10:09:00 PST 2005");
		test111.setName("level 0 reading.test");
		test111.setGrade("pass");
		tests3[0] = test111;
		Test test222 = new Test();
		String expected_date33 = new String("Tue Aug 16 11:09:00 PST 2005");
		test222.setDate(expected_date33);
		test222.setName("level 1 reading.test");
		test222.setGrade("pass");
		tests3[1] = test222;
		Test test333 = new Test();
		String expected_date44 = new String("Wed Aug 17 12:09:00 PST 2005");
		test333.setDate(expected_date44);
		test333.setName("level 2 reading.test");
		test333.setGrade("pass");
		tests3[2] = test333;
		word3.setTests(tests3);
		
		/*
		WordLastTestDates wltd = new WordLastTestDates();
		//WordTestDateUtility wtdu = new WordTestDateUtility(3);
		//wtdu.evaluateWordTestDates(word);
		//String last_date = wtdu.getWordsLastTestDate(type);
		wltd.addWord(word1, "Wed Aug 17 08:09:00 PST 2005");
		wltd.addWord(word2, "Wed Aug 17 09:09:00 PST 2005");
		wltd.addWord(word3, "Wed Aug 17 10:09:00 PST 2005");
		
		ArrayList keys_al = wltd.getSortedWLTDKeys();
		Long actual_key = (Long)keys_al.get(0);
		String actual_str_key = actual_key.toString(); 
		Word actual_word = wltd.getWord(actual_str_key);
		String actual_text = actual_word.getText();
		String expected_text = new String("word1");
		assertEquals(expected_text, actual_text);
		*/
	}
	
	protected void tearDown()
	{
		
	}

	/**
	* 
	*/
	public void testAddWord()
	{
		WordLastTestDates wltd = new WordLastTestDates();
		
		String last_test_date11 = new String("Wed Aug 17 08:09:00 PST 2005");
		wltd.addWord(word1, last_test_date11);
		
		String last_test_date22 = new String("Wed Aug 17 09:09:00 PST 2005");
		wltd.addWord(word2, last_test_date22);
						
		String last_test_date33 = new String("Wed Aug 17 10:09:00 PST 2005");
		wltd.addWord(word3, last_test_date33);
		
		ArrayList al = wltd.getSortedWLTDKeys();
		Long actual_key = (Long)al.get(0);
		String actual_str_key = actual_key.toString(); 
		Word actual_word = wltd.getWord(actual_str_key);
		// the text for this word should be word3
		String actual_text = actual_word.getText();
		String expected_text = new String("word1");
		assertEquals(expected_text, actual_text);
	}
	
	/**
	* 
	*/
	public void testGetSortedWLTDKeys()
	{
		System.out.println("WordLastTestDatesTest.testGetSortedWLTDKeys ---");
		String type = new String("reading"); 
		WordLastTestDates wltd = new WordLastTestDates();
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("WLTD test");
		ArrayList actual_key_list = new ArrayList(); 
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		printLog(all_word_categories.size()+" categories");
		//printLog(words_in_this_cat+" words in "+category+" = subtotal "+expected_words);
		int expected_words = 0;
		for (int i = 0; i < all_word_categories.size(); i++) 
		{
			String category = (String)all_word_categories.get(i);
			Vector category_words = store.getWordObjectsForTest(category, user_name);
			int words_in_this_cat = category_words.size();
			//printLog(words_in_this_cat+" words in "+category);
			expected_words = expected_words + words_in_this_cat;
			//printLog(" expected_words "+expected_words);
			int j = 0;
			while (j<words_in_this_cat)
			{
				Word word = (Word)category_words.get(j);
				WordTestDateUtility wtdu = new WordTestDateUtility(3);
				wtdu.evaluateWordTestDates(word);
				String last_date = wtdu.getWordsLastTestDate(type);
				//printLog(word.getText()+" "+word.getDefinition()+" "+j);
				//printLog(wtdu.getLog());
				wltd.addWord(word, last_date);
				j++;
			}
		}
		actual_key_list = wltd.getSortedWLTDKeys();
		int actual_words = (actual_key_list.size()+1); // expeted words starts the cound at 1, but the index here starts at 0
		//printLog(" actual_words "+actual_words);
		Long actual_key = (Long)actual_key_list.get(0);
		String actual_str_key = actual_key.toString();
		//printLog("WLTD.testGetSortedWLTDKeys : expected "+expected_words+" actual "+actual_words);
		assertEquals(expected_words, actual_words);
	}
	
	public void testGetWordObjects()
	{
		String type = new String("reading"); 
		WordLastTestDates wltd = new WordLastTestDates();
		// makle sure the exclude level times are small enough to include all the words
		Vector elt_vector = new Vector();
		elt_vector.add("1");
		elt_vector.add("1");
		elt_vector.add("3");
		elt_vector.add("9");
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("WLTD files");
		ArrayList actual_key_list = new ArrayList(); 
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		//printLog(all_word_categories.size()+" categories");
		int expected_words = 0;
		for (int i = 0; i < all_word_categories.size(); i++) 
		{
			String category = (String)all_word_categories.get(i);
			Vector category_words = store.getWordObjectsForTest(category, user_name);
			int words_in_this_cat = category_words.size();
			//printLog(words_in_this_cat+" words in "+category+" = subtotal "+expected_words);
			expected_words = expected_words + words_in_this_cat;
			int j = 0;
			while (j<words_in_this_cat)
			{
				Word word = (Word)category_words.get(j);
				WordTestDateUtility wtdu = new WordTestDateUtility(3);
				wtdu.evaluateWordTestDates(word);
				String last_date = wtdu.getWordsLastTestDate(type);
				wltd.addWord(word, last_date);
				//printLog(word.getText()+" "+word.getDefinition()+" "+last_date);
				//printLog(last_date+" "+wltd.getLog()+word.getText()+" "+word.getDefinition());wltd.resetLog();
				//printLog(wltd.getLog());wltd.resetLog();
				j++;
			}
		}
		actual_key_list = wltd.getSortedWLTDKeys();
		int actual_words = actual_key_list.size();
		//printLog("WLTDT.testGetObjects : ================================");
		//printLog(actual_words+" total keys for "+expected_words+" words in "+all_word_categories.size()+" categories");
		//printLog(wltd.getLog());
		Long actual_key = (Long)actual_key_list.get(0);
		String actual_str_key = actual_key.toString();
		//printLog(wltd.getLog());
		assertEquals(expected_words, actual_words);
	}
	
	public void testGetList()
	{
		String type = new String("reading"); 
		
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("1");
		elt_vector.add("3");
		elt_vector.add("9");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("WLTD files");
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		int expected_words = wltd.getList(all_word_categories, store, user_name);
		//printLog("WLTDT.testGetList : wltd.getLog ================================");
		//printLog(wltd.getLog());wltd.resetLog();
		ArrayList actual_key_list = wltd.getSortedWLTDKeys();
		int actual_words = actual_key_list.size(); // aioob
		//printLog("WLTDT.testGetList : "+actual_words+" total keys for "+expected_words+" words in "+all_word_categories.size()+" categories");
		//printLog("WLTDT.testGetList : wltd.getLog after getSortedKeys ============");
		//printLog(wltd.getLog());
		Long actual_key = (Long)actual_key_list.get(0);
		String actual_str_key = actual_key.toString();
		//printLog(wltd.getLog());
		assertEquals(expected_words, actual_words);
	}
	
	/**
	*There should be no *attire* defninition in the list.
	*  I dont know what this means either...
	*/
	public void testExcludeRetiredWords()
	{
		String type = new String("reading"); 
		
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("2960548470376610112");
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		//System.out.println("categories - "+all_word_categories.size());
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		int list_words = wltd.getList(all_word_categories, store, user_name);
		//System.out.println("after wltd.getList "+list_words);
		//System.out.println("WLTDT.testGetList : wltd.getLog ================================");
		//printLog(wltd.getLog());wltd.resetLog();
		ArrayList actual_key_list = wltd.getSortedWLTDKeys();
		int actual_words = actual_key_list.size(); // aioob
		//printLog("WLTDT.testGetList : "+actual_words+" total keys for "+expected_words+" words in "+all_word_categories.size()+" categories");
		//printLog("WLTDT.testGetList : wltd.getLog after getSortedKeys ============");
		//System.out.println("actual_key_list - "+actual_key_list.size());
		//printLog(wltd.getLog());
		//Long actual_key = (Long)actual_key_list.get(0);
		//String actual_str_key = actual_key.toString();
		String cat = (String)all_word_categories.get(0);
		Vector all_words = store.getWordObjects(cat, user_name);
		int expected_words = all_words.size()-1;
		//System.out.println("WLTDT.testGetList : store.getLog ================================");
		//printLog(store.getLog());
		//<retired>true</retired>
		// expected 77.
		assertEquals(expected_words, actual_words);
	}
	
	/**
	*Utility to create a folder of files named after the words last test date in milliseconds.
	* There are 3 words so the expected_words = 2. 
	* wltd.createLists(all_word_categories, store, user_name); IS THE UTILITY.
	*/
	public void testGetWNTFiles()
	{
		String type = new String("reading"); 
		
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("2960548470376610112");
		//String user_name = new String("-5519451928541341468");
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		//System.out.println("categories - "+all_word_categories.size());
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		//int list_words = wltd.getList(all_word_categories, store, user_name);
		//System.out.println("after wltd.getList "+list_words);
		//System.out.println("WLTDT.testGetList : wltd.getLog ================================");
		//printLog(wltd.getLog());wltd.resetLog();
		//ArrayList actual_key_list = wltd.getSortedWLTDKeys();
		//int actual_words = actual_key_list.size(); // aioob
		
		//wltd.createLists(all_word_categories, store, user_name);	// THIS BE THE UTILITY
		String [] files = wltd.getWNTFiles(user_name, current_dir);
		int i = 0;
		printLog(wltd.getLog());
		
		/*
		int l = files.length;
		while (i<l)
		{
			String file_w_ext = files [i];
			String file = Transformer.getLongDateFromMilliseconds(Domartin.getFileWithoutExtension(file_w_ext));
			System.out.println(i+" "+file);
			i++;
		}
		*/
		Vector results = wltd.getWaitingTests(user_name, current_dir);
		int tests = results.size();
		int retired = wltd.getNumberOfRetiredWords();
		System.out.println("number of "+type+" tests waiting "+tests);
		System.out.println("retired words "+retired+" + tests = total "+(tests+retired));
		System.out.println("actual total "+wltd.getNumberOfTotalWords());
			//printLog("WLTDT.testGetList : "+actual_words+" total keys for "+expected_words+" words in "+all_word_categories.size()+" categories");
		//printLog("WLTDT.testGetList : wltd.getLog after getSortedKeys ============");
		//System.out.println("actual_key_list - "+actual_key_list.size());
		//printLog(wltd.getLog());
		//Long actual_key = (Long)actual_key_list.get(0);
		int actual_words = files.length;
		//String cat = (String)all_word_categories.get(0);
		//Vector all_words = store.getWordObjects(cat, user_name);
		int expected_words = 2;		// one word is retired
		//System.out.println("WLTDT.testGetList : store.getLog ================================");
		//printLog(store.getLog());
		assertEquals(expected_words, actual_words);
	}
	
	/**
	*Not sure why this fails, except that there is a differential between the file times and
	* the calculated file times.
	*/
	public void testUpdateTestDateRecords()
	{
		String type = new String("reading"); 
		
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_id = new String("2960548470376610112");
		//String user_name = new String("-5519451928541341468");
		//Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		//System.out.println("categories - "+all_word_categories.size());
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		// create a test test to test (yeah I had to do that...)
		String [] files = wltd.getWNTFiles(user_id, current_dir);
		String file = files[1];
		String file_w_ext = file;
		String file_wo_ext = Domartin.getFileWithoutExtension(file_w_ext);
		String file_date = Transformer.getLongDateFromMilliseconds(file_wo_ext);
		//System.out.println("--- file "+file);
		//System.out.println("--- date "+file_date);
		//printFiles(files);
		// create a new test
		Date today = new Date();
		String now = today.toString();
		//long word_id = Long.parseLong("-7920458335499530699"); // this word is retired
		long word_id = Long.parseLong("-7983252723503331340");
		//System.out.println("new test date - "+now);
		//System.out.println("     toady    - "+today);
		//System.out.println("--- word_id "+word_id);
		//System.out.println("--- file_id "+file_wo_ext);
		Date fwoe = new Date(Long.parseLong(file_wo_ext));
		//System.out.println("--- file_date "+fwoe.toString());
		WordTestResult wtr = new WordTestResult();
		wtr.setWntdName(file);
		wtr.setDate(now);
		wtr.setWordId(word_id);
		wtr.setLevel("0");
		wltd.updateTestDateRecords(user_id, current_dir, wtr);
		files = wltd.getWNTFiles(user_id, current_dir);
		///printFiles(files);
		//System.out.println("-------------- log ");
		//printLog(wltd.getLog());
		String user_folder_path = (current_dir+File.separator+user_id);
		String vocab_folder_path = (user_folder_path+File.separator+"vocab");
		String lists_folder_path = (vocab_folder_path+File.separator+"lists");
		String wntd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
		// Now we gotta add a day to the current date, cause the word at level 0 will create a new test date 1 day later.
		long this_instant = today.getTime();
		long day = 86400000;
		//int differential = 362;		// please tell my why we need this?
		//long expected_wnt_time = this_instant + day + differential;
		long expected_wnt_time = this_instant + day;
		//if (expected_wnt_time>expected_wnt_time
		String expected_file_name = Long.toString(expected_wnt_time);
		String wntd_file_path = (wntd_folder_path+File.separator+expected_file_name+".xml");
		//System.out.println("old test date - "+this_instant);
		//System.out.println("new test date - "+expected_wnt_time);
		//System.out.println("path - "+wntd_file_path);
		File expected_file = new File(wntd_file_path);
		boolean actual = expected_file.exists();
		boolean expected = true;
		assertEquals(expected, actual);
	}
	
	/**
	* one day  = 86400000  milliseconds
	* one week = 604800000 milliseconds
	
	public void testExcludeFilter()
	{
		String b1 = new String("Wed Aug 17 08:09:00 PST 2005");
		String b2 = new String("Thu Aug 18 08:09:00 PST 2005");
		//String getElapsedTime(String milliseconds_string)
		long l1 = Domartin.getMilliseconds(b1);
		long l2 = Domartin.getMilliseconds(b2);
		long delta = l1 - l2;
		Date date = new Date();
		long l_time = date.getTime();
		long n_time = l_time + 604800000;
		Date new_date = new Date(n_time);
		printLog("MIlliseconds between "+date.toString()+" and "+new_date.toString()+" is 604800000");
		printLog("MIlliseconds between "+b1+" and "+b2+" is "+delta);
	}
	*/
	
	public void testExcludeFilter()
	{
		WordLastTestDates wltd = new WordLastTestDates();
		
		Test[] tests = new Test[4];
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
		
		Date today = new Date();
		long now = today.getTime();
		String today_now = today.toString();
		Test test4 = new Test();
		test4.setDate(today_now);
		test4.setName("level 2 reading.test");
		test4.setGrade("pass");
		tests[3] = test4;
		
		Vector elt_vector = new Vector();
		elt_vector.add("1");
		elt_vector.add("7");
		elt_vector.add("14");
		elt_vector.add("30");
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setSubject(Constants.VOCAB);
		word1.setTests(tests);
		wltd.addWord(word1, today_now);
		
		String last_test_date22 = new String("Wed Aug 17 09:09:00 PST 2005");
		wltd.addWord(word2, last_test_date22);
						
		String last_test_date33 = new String("Wed Aug 17 10:09:00 PST 2005");
		wltd.addWord(word3, last_test_date33);
		
		ArrayList al = wltd.getSortedWLTDKeys();
		int actual_words = al.size();
		//printLog(wltd.getLog());
		//printLog(actual_words+" total keys for 3 words");
		//printLog(al);
		int expected_size = 1;
		int actual_size = al.size();
		assertEquals(expected_size, actual_size);			// test1 should have been excluded.
	}
	
	/**
	* 
	*/
	public void testAddOrExcludeWord()
	{
		WordLastTestDates wltd = new WordLastTestDates();
		
		String last_test_date11 = new String("Wed Aug 17 08:09:00 PST 2005");
		wltd.addWordOrExclude(word1, last_test_date11);
		
		String last_test_date22 = new String("Wed Aug 17 09:09:00 PST 2005");
		wltd.addWordOrExclude(word2, last_test_date22);
						
		Date today = new Date();
		long now = today.getTime();
		String today_now = today.toString();
		//String last_test_date33 = new String("Wed Aug 17 10:09:00 PST 2005");
		wltd.addWordOrExclude(word3, today_now);
		
		ArrayList al = wltd.getSortedWLTDKeys();
		//Long actual_key = (Long)al.get(0);
		//String actual_str_key = actual_key.toString(); 
		//Word actual_word = wltd.getWord(actual_str_key);
		// the text for this word should be word3   addWordOrExclude(Word word, String last_test_date)
		//String actual_text = actual_word.getText();
		//String expected_text = new String("word1");
		int expected_size = 1;
		int actual_size = al.size();
		assertEquals(expected_size, actual_size);			// test1 should have been excluded.
	}
	
	/**
	*Utility to create a folder of files named after the words last test date in milliseconds.
	* wltd.createLists(all_word_categories, store, user_name); IS THE UTILITY.
	*/
	public void testCreateNewWordList()
	{
		Word new_word = new Word();
		String expected_name = new String("new_word");
		new_word.setText(expected_name);
		new_word.setDefinition("def");
		new_word.setWritingLevel(0);
		new_word.setReadingLevel(0);
		Date today_is_the_greatest_day_i_had_ever_known = new Date();
		long expected_now = today_is_the_greatest_day_i_had_ever_known.getTime();
		new_word.setDateOfEntry(expected_now);
		long expected_id = Domartin.getNewID(); 
		new_word.setId(expected_id);
		
		String type = new String("reading"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("2960548470376610112");
		String file_name = new String("random words 14.xml");
		String encoding = new String("UTF-8");
		//String user_name = new String("-5519451928541341468");
		store.addWord(new_word, file_name, user_name, encoding);
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		//wltd.createLists(all_word_categories, store, user_name);
		wltd.createNewWordList(new_word, store, user_name, current_dir);
		
		String [] files = wltd.getWNTFiles(user_name, current_dir);
		int i = 0;
		printLog(wltd.getLog());
		String most_recent = files[1];
		String most_recent_time = Domartin.getFileWithoutExtension(most_recent);
		String most_recent_date = Transformer.getLongDateFromMilliseconds(most_recent_time);
		System.out.println("most recent date "+most_recent_date);
		//String search_property = most_recent_time; // this is stupid
		//String search_property = "date_of_entry";
		String search_property = "id";
		// String search_value = Long.toString(expected_now);
		String search_value = Long.toString(expected_id);
		//String category = "vocab";
		String category = file_name;
		System.out.println(search_property+" search "+search_value);
		Word results_word = new Word();
		long actual_now = Long.parseLong("0");
		try
		{
			results_word = store.getWordObject(search_property, search_value, category, user_name); 
			String actual_name = results_word.getText();
			actual_now = results_word.getDateOfEntry();
		} catch (java.lang.NullPointerException npe)
		{
			System.out.println("FileStorage log))))))))))))))))))))))))))");
			printLog(store.getLog());
		}
		// clean up
		store.deleteWord(results_word, category, user_name, encoding);
		/*
		int l = files.length;
		while (i<l)
		{
			String file_w_ext = files [i];
			String file = Transformer.getLongDateFromMilliseconds(Domartin.getFileWithoutExtension(file_w_ext));
			System.out.println(i+" "+file);
			i++;
		}
		
		/*
		Vector results = wltd.getWaitingTests(user_name, current_dir);
		int tests = results.size();
		int retired = wltd.getNumberOfRetiredWords();
		System.out.println("number of "+type+" tests waiting "+tests);
		System.out.println("retired words "+retired+" + tests = total "+(tests+retired));
		System.out.println("actual total "+wltd.getNumberOfTotalWords());
		*/
		//printLog("WLTDT.testGetList : "+actual_words+" total keys for "+expected_words+" words in "+all_word_categories.size()+" categories");
		//printLog("WLTDT.testGetList : wltd.getLog after getSortedKeys ============");
		//System.out.println("actual_key_list - "+actual_key_list.size());
		//printLog(wltd.getLog());
		//Long actual_key = (Long)actual_key_list.get(0);
		//int actual_words = files.length;
		//String cat = (String)all_word_categories.get(0);
		//Vector all_words = store.getWordObjects(cat, user_name);
		//int expected_words = 1;		// one word is retired
		//System.out.println("WLTDT.testGetList : store.getLog ================================");
		//printLog(store.getLog());
		assertEquals(expected_now, actual_now);
	}
	
	public void testGetTestWords()
	{
		//System.out.println("testGetTestWords==============");
		String user_id = new String("-5519451928541341468");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String test_type = Constants.READING_AND_WRITING;
		String number_of_words = "5";
		WordNextTestDates wntds = new WordNextTestDates();
		Vector test_words = wntds.getTestWords(user_id, current_dir, action_time,
					subject, test_type, number_of_words);
		//printLog(test_words);
		//printLog(wntds.getLog());
		int expected = 5;
		int actual = test_words.size();
		assertEquals(expected, actual);
	}
	
	private void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("vector - "+log.get(i));
			i++;
		}
	}
	
	private void printLog(ArrayList al)
	{
		int total = al.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("ArrayList - "+al.get(i));
			i++;
		}
	}
	
	private void printLog(String loggy)
	{
		System.out.println("log- "+loggy);

	}
	
	private void printFiles(String [] files)
	{
		int i = 0;
		int l = files.length;
		while (i<l)
		{
			String file_w_ext = files [i];
			String file = Transformer.getLongDateFromMilliseconds(Domartin.getFileWithoutExtension(file_w_ext));
			System.out.println(i+" "+file);
			i++;
		}
	}

}
