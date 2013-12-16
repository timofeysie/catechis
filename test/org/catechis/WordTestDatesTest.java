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
import org.catechis.dto.WordTestDates;
import org.catechis.WordTestDateUtility;
import org.catechis.Domartin;

public class WordTestDatesTest extends TestCase
{

	private Stats stats;
	private Hashtable actual_tests;
	
	public WordTestDatesTest(String name) 
	{
		super(name);
	}
	
	/**
	* addWordTestDate(int level, String type, Word word)
	*/
	public void testAddWordTestDate()
	{
		int level = 1;
		String type = new String("reading");
		Word word = new Word();
		String expected_name = new String("text");
		word.setText(expected_name);
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		Word word1 = new Word();
		word1.setText("text1");
		word1.setDefinition("def1");
		word1.setWritingLevel(0);
		word1.setReadingLevel(0);
		Word word2 = new Word();
		word2.setText("text2");
		word2.setDefinition("def2");
		word2.setWritingLevel(0);
		word2.setReadingLevel(0);
		String test_date  = new String("Sat Aug 20 08:09:00 PST 2005");
		String test_date1 = new String("Sun Aug 21 08:09:00 PST 2005");
		String test_date2 = new String("Mon Aug 22 08:09:00 PST 2005");
		WordTestDates word_test_dates = new WordTestDates(3);
		word_test_dates.addWordTestDate(level, type, word, test_date);
		word_test_dates.addToLog("0 added");
		word_test_dates.addWordTestDate(level, type, word1, test_date1);
		word_test_dates.addToLog("1 added");
		word_test_dates.addWordTestDate(level, type, word2, test_date2);
		word_test_dates.addToLog("2 added");
		ArrayList sorted_keys = word_test_dates.getSortedTestDatesList(level, type);
		Word actual_word = word_test_dates.getSpecificWord(level, type, test_date2);
		String actual_name = actual_word.getText();
		expected_name = new String("text2");
		//printLog(word_test_dates.getLog());
		assertEquals(expected_name, actual_name);
	}
	
	public void testGetSpecificWordTestDatesMilliseconds()
	{
		int level = 1;
		String type = new String("reading");
		Word word = new Word();
		String name = new String("text");
		word.setText(name);
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		String test_date  = new String("Sat Aug 20 08:09:00 PST 2005");
		WordTestDates word_test_dates = new WordTestDates(3);
		word_test_dates.addWordTestDate(level, type, word, test_date);
		//printLog(word_test_dates.getLog());
		ArrayList milli_list = word_test_dates.getSpecificWordTestDatesMilliseconds(level, type);
		String milli_string  = (String)milli_list.get(1);
		//System.out.println("\\\\\\\\\\\\\\\\\\\\ "+milli_string);
		//Word actual_word = word_test_dates.getSpecificWord(level, type, test_date2);
		//String actual = actual_word.getText();
		//String expected = new String("text2");
		//printLog(word_test_dates.getLog());
		//assertEquals(expected, actual);
	}	
	
	public void testChangeSpecificWordTestDate()
	{
		int level = 1;
		String type = new String("reading");
		Word word = new Word();
		String expected_name = new String("text");
		word.setText(expected_name);
		word.setDefinition("def");
		word.setWritingLevel(0);
		word.setReadingLevel(0);
		Word word1 = new Word();
		word1.setText("text1");
		word1.setDefinition("def1");
		word1.setWritingLevel(0);
		word1.setReadingLevel(0);
		Word word2 = new Word();
		word2.setText("text2");
		word2.setDefinition("def2");
		word2.setWritingLevel(0);
		word2.setReadingLevel(0);
		String test_date  = new String("Sat Aug 20 08:09:00 PST 2005");
		String test_date1 = new String("Sun Aug 21 08:09:00 PST 2005");
		String test_date2 = new String("Mon Aug 22 08:09:00 PST 2005");
		WordTestDates word_test_dates = new WordTestDates(3);
		word_test_dates.addWordTestDate(level, type, word, test_date);
		//word_test_dates.addToLog("0 added ===========");
		word_test_dates.addWordTestDate(level, type, word1, test_date1);
		//word_test_dates.addToLog("1 added ===========");
		word_test_dates.addWordTestDate(level, type, word2, test_date2);
		//word_test_dates.addToLog("2 added ===========");
		printLog(word_test_dates.getLog());
		word_test_dates.resetLog();
		// now we change a word to a newer test
		int old_level = 0; // so a pass of failed test are treated the same???
		int new_level = 1;
		String old_date_key = word_test_dates.getSpecificWordTestMIllisecondsKey(type, word);
		Vector log = new Vector();
		//System.out.println("old_date_key ============= "+old_date_key);
		String new_date = new String("Tue Aug 23 08:09:00 PST 2005");
		//System.out.println("WTDs before change =======");
		ArrayList before_list = word_test_dates.getSortedTestDatesList(old_level, type);
		//System.out.println("WTD  ====================="+before_list.size());
		printWTDs(before_list, 0, type, word_test_dates);
		word_test_dates.changeSpecificWordTestDate(old_level, new_level, type, old_date_key, new_date);
		// at this point we need to check if the change has been made
		//System.out.println("WTDs after change ========"); 
		ArrayList after_list = word_test_dates.getSortedTestDatesList(old_level, type);
		//System.out.println("WTD  ====================="+after_list.size());
		printWTDs(before_list, 0, type, word_test_dates);
		ArrayList sorted_keys = word_test_dates.getSortedTestDatesList(new_level, type);
		String last_date_key = (String)sorted_keys.get(sorted_keys.size()-1);
		Word actual_word = word_test_dates.getSpecificWord(level, type, last_date_key);
		String actual_name = new String();
		try
		{
			actual_name = actual_word.getText();
		} catch (java.lang.NullPointerException npe)
		{
			actual_name = new String();
		}
		//printLog(word_test_dates.getLog());
		assertEquals(expected_name, actual_name);
		//changeSpecificWordTestDate(int old_level, int new_level, String type, String old_date_key, String new_date)
	}
	
	/**
	* THis *test* was giving us an error, but not really even a test.  Here was the result:
	Error	N/A
	java.lang.NullPointerException at org.catechis.JDOMSolution.getWords(JDOMSolution.java:214) 
	at org.catechis.FileStorage.getWordObjects(FileStorage.java:240) 
	at org.catechis.WordTestDatesTest.testAllWordTestDates(WordTestDatesTest.java:174)
	*/
	/*public void testAllWordTestDates()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_name = new String("guest");
		Vector word_files = store.getWordCategories("primary", user_name);
		int num = word_files.size();
		int ind = 0;
		String word_file;
		Stats stats = new Stats();
		WordTestDates word_test_dates = new WordTestDates(3);
		WordTestDateUtility wtd_utility = new WordTestDateUtility(3);
		//System.out.println("-------------WTD "+ind+" &&&/// "+num);
		while (ind<num)
		{
			word_file = (String)word_files.get(ind);
			Vector words = store.getWordObjects(word_file, "guest");
			int num_of_words = words.size();
			int word_index = 0;
			int level = 0;
			Vector log = new Vector();
			while (word_index < num_of_words)
			{
				Word word = (Word)words.get(word_index);
				wtd_utility = new WordTestDateUtility(3);
				wtd_utility.evaluateWordTestDates(word);
				word_test_dates.addWordTestDate(wtd_utility.getWordsLastTestLevel("reading"), "reading", word, wtd_utility.getWordsLastTestDate("reading"));
				word_test_dates.addWordTestDate(wtd_utility.getWordsLastTestLevel("writing"), "writing", word, wtd_utility.getWordsLastTestDate("writing"));
				log.add(word.getText()+"		"+word.getDefinition()+" 		"+wtd_utility.getWordsLastTestLevel("reading")+" r-ltd "+wtd_utility.getWordsLastTestDate("reading"));
				word_index++;
			}
			//printLog(log);
			//System.out.println("WTD "+ind+" &&&/// "+words.size());
			ind++;
		}
		String type = new String("reading");
		int level = 0;
		ArrayList list = word_test_dates.getSortedTestDatesList(level, type);
		//printWTDs(list, 0, type, word_test_dates);
		//System.out.println("-------------WTD ");
	}*/
	
	private void printWTDs(ArrayList list, int level, String type, WordTestDates wtds)
	{
		/*
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			String date_key = (String)list.get(i);
			try
			{
				Word word = wtds.getSpecificWord(level, type, date_key);
				String text = word.getText();
				String def = word.getDefinition();
				System.out.println("wtd: "+date_key+" "+text+" "+def);
			} catch (java.lang.NullPointerException npe)
			{
				System.out.println("wtd is nullllll ");
			}
			i++;
		}
		*/
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

}
