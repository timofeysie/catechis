package org.catechis.filter;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.File;

import junit.framework.TestCase;
import org.catechis.dto.Word;
import org.catechis.FileStorage;
import org.catechis.filter.WordListFilter;


import org.apache.struts.action.DynaActionForm;

public class WordListFilterTest extends TestCase
{

	public WordListFilterTest (String name)
	{
		super(name);
	}
    
	protected void setUp() throws Exception
	{
	}
	
	/**
	<p>This tests the two methods:
	*<p>getLevelZeroWords(String user_name, String test_type, Storage store)
	*<p>getFilteredLevelZeroWords(String user_name, String test_type, Vector previous_words, Storage store)
	*/
	public void testFilteredGetLevelZeroWords1()
	{
		String user_name = new String("WLTD test");
		String reading = new String("reading");
		String writing = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		WordListFilter wlf = new WordListFilter();
		Word word = new Word();
		word.setText("text");
		word.setDefinition("def");
		word.setReadingLevel(0);
		word.setWritingLevel(0);
		Vector reading_list = new Vector();
		reading_list.add(word);
		Vector original_writing_list = new Vector();
		original_writing_list.add(word);
		Vector new_writing_list = wlf.getFilteredLevelZeroWords(user_name, reading, reading_list, store);
		//String expected_date = new String("08/16/05");
		int original_writing_list_size = original_writing_list.size();
		int new_writing_list_size = new_writing_list.size();
		//printLog(store.getLog());
		int expected_size = 0;
		int actual_size = new_writing_list_size;
		//printLog(store.getLog());
		//System.out.println("WordListFilterTest.testGetLevelZeroWords1: original_writing_list_size "+original_writing_list_size);
		//System.out.println("WordListFilterTest.testGetLevelZeroWords1: new_writing_list_size "+new_writing_list_size);
		//System.out.println("WordListFilterTest.testGetLevelZeroWords1: reading_list_size "+reading_list.size());
		assertEquals(expected_size, actual_size);
	}
	
	/**
	<p>This tests the two methods:
	*<p>getLevelZeroWords(String user_name, String test_type, Storage store)
	*<p>getFilteredLevelZeroWords(String user_name, String test_type, Vector previous_words, Storage store)
	*/
	public void testFilteredGetLevelZeroWords()
	{
		String user_name = new String("guest");
		String reading = new String("reading");
		String writing = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		WordListFilter wlf = new WordListFilter();
		Vector reading_list = wlf.getLevelZeroWords(user_name, reading, store);
		Vector original_writing_list = wlf.getLevelZeroWords(user_name, writing, store);
		Vector new_writing_list = wlf.getFilteredLevelZeroWords(user_name, reading, reading_list, store);
		//String expected_date = new String("08/16/05");
		int original_writing_list_size = original_writing_list.size();
		int new_writing_list_size = new_writing_list.size();
		//printLog(store.getLog());
		int expected_size = 60;
		int actual_size = new_writing_list_size;
		//System.out.println("WordListFilterTest.testGetLevelZeroWords: original_writing_list_size "+original_writing_list_size);
		//System.out.println("WordListFilterTest.testGetLevelZeroWords: new_writing_list_size "+new_writing_list_size);
		assertEquals(expected_size, actual_size);
	}
	
	/**
	*Hashtable getMissedWords(String type, String root_path, String user_name, Hashtable user_opts)
	*/
	public void testGetMissedWords()
	{
		String user_name = new String("guest");
		String reading = new String("reading");
		String writing = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Hashtable user_opts = store.getUserOptions(user_name, current_dir);
		WordListFilter wlf = new WordListFilter();
		Hashtable missed_words = wlf.getMissedWords(reading, current_dir, user_name, user_opts);
		int actual_size = missed_words.size();
		int expected_size = 26;
		//System.out.println("WordListFilterTest.testGetMissedWords size "+actual_size);
		assertEquals(expected_size, actual_size);
	}
	
	/**
	*Hashtable getMissedWords(String type, String root_path, String user_name, Hashtable user_opts, Hashtable previous_missed_words)
	*/
	public void testFilteredGetMissedWords()
	{
		String user_name = new String("guest");
		String reading = new String("reading");
		String writing = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Hashtable user_opts = store.getUserOptions(user_name, current_dir);
		WordListFilter wlf = new WordListFilter();
		Hashtable missed_words = wlf.getMissedWords(reading, current_dir, user_name, user_opts);
		Hashtable new_missed_words = wlf.getFilteredMissedWords(writing, current_dir, user_name, user_opts, missed_words);
		int new_actual_size = new_missed_words.size();
		int actual_size = missed_words.size();
		int expected_size = 24;
		//System.out.println("WordListFilterTest.testFilteredGetMissedWords size "+actual_size);
		//System.out.println("WordListFilterTest.testFilteredGetMissedWords new size "+new_actual_size);
		assertEquals(expected_size, new_actual_size);
	}
	
	/**
	*
	*Hashtable getNoRepeatsMissedWords1(String type, String root_path, String user_name, 
		Hashtable user_opts, Hashtable previous_missed_words, Vector previous_words)
	*/ /* this takes far to long
	public void testGetNoRepeatsMissedWords1()
	{
		String user_name = new String("guest");
		String reading = new String("reading");
		String writing = new String("writing");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Hashtable user_opts = store.getUserOptions(user_name, current_dir);
		WordListFilter wlf = new WordListFilter();
		// the way to get a list including repeats...
		String type = new String("reading"); 
		Vector r_all_words =  wlf.getLevelZeroWords(user_name, type, store);
		Hashtable r_words_defs = wlf.getMissedWords(type, current_dir, user_name, user_opts);
		type = new String("writing"); 
		Vector w_all_words =  wlf.getLevelZeroWords(user_name, type, store);
		Hashtable w_words_defs = wlf.getMissedWords(type, current_dir, user_name, user_opts);
		int r_all_words_size = r_all_words.size();
		int w_all_words_size = w_all_words.size();
		//int r_words_defs_size = r_words_defs.size();
		//int w_words_defs_size = w_words_defs.size();
		//int total_words = r_all_words_size + w_all_words_size + r_words_defs_size + w_words_defs_size;
		int total_words = r_all_words_size + w_all_words_size;
		// the way to exclude all repeats
		Vector reading_list = wlf.getLevelZeroWords(user_name, reading, store);
		Vector writing_list = wlf.getFilteredLevelZeroWords(user_name, reading, reading_list, store);
		printLog(wlf.getLog());
		Vector all_words = new Vector();
		all_words.addAll(reading_list);
		all_words.addAll(writing_list);
		Hashtable blank_missed_words = new Hashtable();	// first time thru pass in only a blank list
		/*Hashtable missed_reading_words = 
			wlf.getNoRepeatsMissedWords1(reading, current_dir, user_name, 
			user_opts, blank_missed_words, all_words);
		Hashtable missed_writing_words = 
			wlf.getNoRepeatsMissedWords1(writing, current_dir, user_name, 
			user_opts, missed_reading_words, all_words);	// 2nd time thru, use the previous list to exclude.
		//dumpLog(missed_reading_words, missed_writing_words);
		int reading_list_size = reading_list.size();
		int writing_list_size = writing_list.size();
		//int missed_reading_words_size = missed_reading_words.size();
		//int missed_writing_words_size = missed_reading_words.size();
		//int total_non_redundant_words = reading_list_size + writing_list_size + missed_reading_words_size + missed_writing_words_size;
		//int new_actual_size = new_missed_words.size();
		int total_non_redundant_words = reading_list_size + writing_list_size;
		int actual_size = total_non_redundant_words;
		int expected_size = 172;
		//printLog(wlf.getLog());
		System.out.println("WordListFilterTest.testFilteredGetMissedWords total words "+total_words);
		System.out.println("WordListFilterTest.testFilteredGetMissedWords with no repeats "+total_non_redundant_words);
		assertEquals(expected_size, actual_size);
	}*/
	
	/** debuggin only!  don't try this at home!*/
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
	
    private void dumpLog(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" -		"+val);
	    }
    }
    
    private void dumpLog(Hashtable log1, Hashtable log2)
    {
	    for (Enumeration e = log1.keys() ; e.hasMoreElements() ;) 
	    {
		    String key1 = (String)e.nextElement();
		    String val1 = (String)log1.get(key1);
		    String val2 = (String)log2.get(key1);
		    System.out.println(key1+" -		"+val1+" :	"+val2);
	    }
    }

}
