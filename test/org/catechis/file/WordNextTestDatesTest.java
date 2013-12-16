package org.catechis.file;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.ArrayList;
import junit.framework.TestCase;

import java.util.Date;

import org.catechis.dto.SavedTest;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordNextTestDate;
import org.catechis.file.WordNextTestDates;
import org.catechis.dto.WordLastTestDates;
import org.catechis.WordTestDateUtility;
import org.catechis.Storage;
import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.FileStorage;
import org.catechis.file.FileCategories;
import org.catechis.admin.FileUserOptions;
import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;;

public class WordNextTestDatesTest extends TestCase
{

	public WordNextTestDatesTest(String name) 
	{
		super(name);
	}
	
	protected void setUp()
	{

	}
	
	protected void tearDown()
	{
		
	}
	
	/**
	*Testing the wntds.getNextTestWord(user_id, current_dir, time) method.
	*/
	public void testGetNextTestWord()
	{
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("new");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subj);
		//System.out.println("word ---------------- ");
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		String actual = word.getDefinition();
		String expected = "to have talent for arts";
		assertEquals(expected, actual);
	}
	
	/**
	*Testing the wntds.getSpecificNextTestWord(String user_id, String current_dir, String time, String subject, String type)
	*/
	public void testGetSpecificNextTestWord()
	{
		//System.out.println("WordNextTestDatesTest.testGetSpecificNextTestWord: ----------------- ");
		String type = new String("reading"); 
		type = Constants.WRITING;
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("new");
		//String user_id = new String("test_user");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
	    
		//createLists(store, user_id, elt_vector, type);
		
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);					// npe
		Word word = wntds.getSpecificNextTestWord(user_id, current_dir, time, subj, type);
		//System.out.println("word ---------------- "+type);
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		String actual = word.getDefinition();
		String expected = "to be nervous";
		assertEquals(expected, actual);
	}
	
	/**
	*Testing the (String user_id, String current_dir, String time, String subject, String type)
	*
	public void testCreateLists()
	{
		System.out.println("WordNextTestDatesTest.testCreateLists: ----------------- ");
		// first creater a user
		//System.out.println("FileUserUtilities.testAddNewUserFiles ");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		//long user_name_long = Domartin.getNewID();
		//String user_id = Long.toString(user_name_long);
		String user_name = "new guy";
		String password = "new";
		String e_mail = "new@hot.com";
		boolean created = fut.addNewUser(user_name, password, e_mail);
		long long_user_id = fut.getNewUserId();
		String user_id = Long.toString(long_user_id);
		//System.out.println("WordNextTestDatesTest.testCreateLists: created user "+user_id);
		//dumpLog(fut.getLog());
		
		String type = new String("reading"); 
		type = Constants.WRITING;
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		FileStorage store = new FileStorage(current_dir);
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		String category = new String("test cat"); 
		createTestCatAndWords(store, category, current_dir, user_id);
		createLists(store, user_id, elt_vector, type);	// dont need type for this really...
		
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);
		Word word = wntds.getSpecificNextTestWord(user_id, current_dir, time, subj, type);  //
		//System.out.println("type ---------------- "+type);
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		String actual = word.getDefinition();
		String expected = "def3";
		//System.out.println("actual "+actual);
		// clean up
		fut.deleteUser(user_id);
		assertEquals(expected, actual);
	}
	*/
	
	/**
	 * Testing wntds.getNextTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 * @param store
	 * @param category
	 * @param context_path
	 * @param user_id
	 */
	public void testGetNextTestWords()
	{
		//System.out.println("WordNextTestDates.testGetNextTestWords ----------");
		//String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("new");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		//String test_type = Constants.READING_AND_WRITING;
		String test_type = Constants.WRITING;
		//String test_type = Constants.READING;
		String number_of_words = "10";
		Vector words = wntds.getNextTestWords(user_id, current_dir, time, subj, test_type, number_of_words);
		int i = 0;
		int size = words.size();
		while (i<size)
		{
			//Word word = (Word)words.get(i);
			//Test [] tests = word.getTests();
			//Test test = tests[0];
			//String type = test.getType();
			//System.out.println(i+" "+type+" - "+word.getDefinition()+" "+word.getText());
			//printLog(Transformer.createTable(word));
			i++;
		}
		//printLog(wntds.getLog());
		//System.out.println("word ---------------- ");
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		int actual = words.size();
		int expected = 10;
		assertEquals(expected, actual);
	}
	
	/**
	 * Testing wntds.getNextTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 * and it's ability to limit the words to the number of available words.
	 * Set the int expected = ?? to the number of words returned: size.
	 * For the primary user (id -5519451928541341468)
	 * WordNextTestDates.testGetMaximumNumberOfWords: words returned: 1433
	 * WordNextTestDates.testGetMaximumNumberOfWords: words requested: 10000
	 * So to pass the test, the expected variable should be 1433
	 */
	public void testGetMaximumNumberOfWords()
	{
		//System.out.println("WordNextTestDates.testGetMaximumNumberOfWords ----------");
		//String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("new");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.createNewLists(user_id, subj, current_dir);	
		//printLog(fuo.getLog());
		//String test_type = Constants.READING_AND_WRITING;
		String test_type = Constants.WRITING;
		//String test_type = Constants.READING;
		String number_of_words = "10000"; // change this if there is ever more that 10,000 words in this users folder.
		Vector words = wntds.getNextTestWords(user_id, current_dir, time, subj, test_type, number_of_words);
		int i = 0;
		int size = words.size();
		/*
		while (i<size)
		{
			Word word = (Word)words.get(i);
			Test [] tests = word.getTests();
			Test test = tests[0];
			String type = test.getType();
			System.out.println(i+" "+type+" - "+word.getDefinition()+" "+word.getText());
			//printLog(Transformer.createTable(word));
			i++;
		}
		*/
		//printLog(wntds.getLog());
		//System.out.println("WordNextTestDates.testGetMaximumNumberOfWords: words returned: "+size);
		//System.out.println("WordNextTestDates.testGetMaximumNumberOfWords: words requested: "+number_of_words);
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		int actual = words.size();
		int expected = 1433; // this needs to be set to the number of words being returned printed out above
		// because the actual number returned depends of the number of words in the user folder,
		// the last test date of each word and the next test date and the current date.
		assertEquals(expected, actual);
	}
	
	/**
	 * Testing wntds.getNextTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 * and it's ability to limit the words to the number of available words.
	 * Set the int expected = ?? to the number of words returned: size.
	 *
	public void testGetBothReadingAndWritingWords()
	{
		//System.out.println("WordNextTestDates.testGetBothReadingAndWritingWords ----------");
		//String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("7");
		elt_vector.add("15");
		elt_vector.add("30");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_id = new String("-5519451928541341468");
		//String user_id = new String("new");
		// String user_id = new String("new");
		// create new lists (very expensive for this user!)
		//FileUserOptions fuo = new FileUserOptions(current_dir);
		//fuo.createNewLists(user_id, subj, current_dir);		
		
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		//String test_type = Constants.WRITING;
		String test_type = Constants.READING_AND_WRITING;
		//String test_type = Constants.READING;
		String number_of_words = "4000"; 
		Vector words = wntds.getTestWords(user_id, current_dir, time, subj, test_type, number_of_words);
		// test for both reading and writing words
		boolean reading = false;
		boolean writing = false;
		int i = 0;
		int size = words.size();
		while (i<size)
		{
			Word word = (Word)words.get(i);
			Test [] tests = word.getTests();
			String type = "blank";
			try
			{
				Test test = tests[0];
				type = test.getType();
			} catch (java.lang.NullPointerException npe)
			{
				//System.out.println("test is npe "+word.getText());
			}
			//System.out.println(i+" "+type+" - "+word.getDefinition()+" "+word.getText());
			if (type.equals(Constants.READING))
			{
				reading = true;
			}
			if (type.equals(Constants.WRITING))
			{
				writing = true;
			}
			//printLog(Transformer.createTable(word));
			i++;
		}
		
		//printLog(wntds.getLog());
		//System.out.println("WordNextTestDates.testGetBothReadingAndWritingWords: words returned: "+size);
		//System.out.println("WordNextTestDates.testGetBothReadingAndWritingWords: words requested: "+number_of_words);
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		boolean expected = true;
		boolean actual = false;
		if (reading&&writing)
		{
			actual = true;
		}
		assertEquals(expected, actual);
	}
	*/
	
	/**
	*Testing the wntds.getNextTestWord(user_id, current_dir, time) method.
	*/
	public void testGetNextTestWordSameAsGetNextTestWords()
	{
		//System.out.println("WordNextTestDates.testGetNextTestWordSameAsGetNextTestWords");
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("new");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		//System.out.println("Today is "+today.toString());
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.createNewLists(user_id, subj, current_dir);
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);
		int i = 0;
		int size = 10;
		while (i<size)
		{
			Word word = wntds.getNextTestWord(user_id, current_dir, time, subj);
			//System.out.println(i+" "+wntds.getNextTestType()+" "+word.getDefinition()+" "+word.getText());
			// we need to score the word and remove the wntd file to get different words here.
			i++;
		}
		//System.out.println("word ---------------- ");
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		String actual = "";
		String expected = "to have talent for arts";
		assertEquals(expected, actual);
	}
	
	/**
	 * Testing int getLowestCommonDenomenator(Hashtable students, String current_dir, String subject, String type)
	 * The students in the class are:
	 * 	<student>-5519451928541341007</student>
	 *  <student>-5519451928541341407</student>
	 */
	public void testGetLowestCommonDenomenator()
	{
		//System.out.println("testGetLowestCommonDenomenator ------------------------------------------------");
		String encoding = "UTF-8";
		String student_1 = "-5519451928541341468";
		String student_2 = "-4500429194402377875";
		String teacher_id = "0000000000000000001";
		String class_id   = "-226040235952523203";
		String cat = "october1";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable students = fut.getStudents(teacher_id, class_id); // returns stuydent ids-name pairs
		//System.out.println("students - "+students.size());
		// find out the number of waiting tests before adding two words
		WordNextTestDates wntds = new WordNextTestDates();
		Vector waiting_tests1 = wntds.getWaitingTests(current_dir, student_1, Constants.READING, Constants.VOCAB);
		Vector waiting_tests3 = wntds.getWaitingTests(current_dir, student_2, Constants.READING, Constants.VOCAB);
		int waiting_reading_tests_b4_add_1 = waiting_tests1.size();
		int waiting_reading_tests_b4_add_3 = waiting_tests3.size();
		//System.out.println("waiting_reading_tests_b4_add_1 - "+waiting_reading_tests_b4_add_1);
		//System.out.println("waiting_reading_tests_b4_add_3 - "+waiting_reading_tests_b4_add_3);
		FileStorage store = new FileStorage(current_dir);
		Date date = new Date();
		String now = date.toString();
		Word word1 = new Word();
	    word1.setText("t1 - "+now);
	    word1.setDefinition("d1 - "+now);
	    Word word2 = new Word();
	    word2.setText("t2 - "+now);
	    word2.setDefinition("d2 - "+now);
	    store.addWord(word1, cat, student_1, encoding);
	    store.addWord(word2, cat, student_1, encoding);
	    store.addWord(word1, cat, student_2, encoding);
	    store.addWord(word2, cat, student_2, encoding);
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.createNewLists(student_1, "vocab", current_dir);		
		fuo.createNewLists(student_2, "vocab", current_dir);
		int lcd = wntds.getLowestCommonDenomenator(students, current_dir, Constants.VOCAB, Constants.READING);
		//System.out.println("wntds log ----------------------------------------------------------------------");
		//printLog(wntds.getLog());
		int expected = waiting_reading_tests_b4_add_1;
		if (waiting_reading_tests_b4_add_1 > waiting_reading_tests_b4_add_3)
		{
			expected = waiting_reading_tests_b4_add_3;
		}
		expected = expected + 2;
		int actual = lcd;
		//System.out.println("expected - "+lcd);
		//System.out.println("actual - "+actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * Testing int getLowestCommonDenomenator(Hashtable students, String current_dir, String subject, String type)
	 * The students in the class are:
	 * 	<student>-5519451928541341007</student>
	 *  <student>-5519451928541341407</student>
	 */
	public void testGetTestWords()
	{
		System.out.println("testGetTestWords ------------------------------testGetTestWords");
		String encoding = "UTF-8";
		String student_id = "7655807335881695697";
		String teacher_id = "0000000000000000001";
		String class_id   = "0000000000000000001";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		Date date = new Date();
		long now = date.getTime();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable students = fut.getStudents(teacher_id, class_id); // returns stuydent ids-name pairs
		WordNextTestDates wntds = new WordNextTestDates();
		int number_of_words = wntds.getLowestCommonDenomenator(students, current_dir, Constants.VOCAB, Constants.READING);
		Vector test_words = wntds.getTestWords(student_id, current_dir, now+"", Constants.VOCAB, Constants.READING, number_of_words+"");
		String expected = "1";
		SavedTest saved_test = null;
		try
		{
			saved_test = (SavedTest)test_words.get(0);
		} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		{
			saved_test = new SavedTest();
		}
		String actual = saved_test.getNumberOfWords();
		System.out.println("expected - "+expected);
		System.out.println("actual - "+actual);
		printLog(wntds.getLog());
		assertEquals(expected, actual);
	}
	
	public void testPartTestWords()
	{
		//System.out.println("testGetLowestCommonDenomenator ------------------------------------------------");
		String encoding = "UTF-8";
		String student_id = "-5519451928541341468";
		String student_2 = "-4500429194402377875";
		String teacher_id = "0000000000000000001";
		String class_id   = "-226040235952523203";
		String cat = "october1";
		Date date = new Date();
		long now = date.getTime();
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable students = fut.getStudents(teacher_id, class_id); // returns stuydent ids-name pairs
		WordNextTestDates wntds = new WordNextTestDates();
		String number_of_words = "12";
		String subject = new String("vocab"); 
		int total_number_of_words = Integer.parseInt(number_of_words)*2;
		Vector test_words = wntds.getTestWords(student_id, current_dir, now+"", subject, Constants.READING, total_number_of_words+"");
		Vector first_test_words = getPartTestWords(test_words, 0, Integer.parseInt(number_of_words));
		Vector second_test_words = getPartTestWords(test_words, Integer.parseInt(number_of_words), test_words.size()-1);
		int expected = first_test_words.size();
		int actual = second_test_words.size();
		System.out.println("3rd (test_words) - "+test_words.size());
		System.out.println("1st (expected)   - "+expected);
		System.out.println("2nd (actual)     - "+actual);
		assertEquals(expected, actual);
	}
	
	private Vector getPartTestWords(Vector test_words, int start, int finish)
	{
		Vector part_test_words = new Vector();
		for (int i = start; i < finish; i++)	
		{
			part_test_words.add(test_words.get(i));
		}
		return part_test_words;
	}
	
	private void createTestCatAndWords(FileStorage store, String category, String context_path, String user_id)
	{
		FileCategories cats = new FileCategories();
		cats.addCategory(category, context_path, user_id);
		String encoding = "UTF-8";
		Date date = new Date();
		long now = date.getTime();
		Word word1 = new Word();
		String expected_name = new String("word1");
		word1.setText(expected_name);
		word1.setDefinition("def1");
		word1.setWritingLevel(0);
		word1.setReadingLevel(0);
		word1.setRetired(true);
		word1.setDateOfEntry(now);
		word1.setId(Domartin.getNewID());
		Test[] tests = new Test[3];
		Test test1 = new Test();
		test1.setDate("Mon Aug 15 08:09:00 PST 2005");
		test1.setName("level 0 reading.test");
		test1.setGrade("pass");
		tests[0] = test1;
		Test test2 = new Test();
		test2.setDate("Tue Aug 16 08:09:00 PST 2005");
		test2.setName("level 1 reading.test");
		test2.setGrade("fail");
		tests[1] = test2;
		Test test3 = new Test();
		test3.setDate("Wed Aug 17 08:09:00 PST 2005");
		test3.setName("level 1 reading.test");
		test3.setGrade("fail");
		tests[2] = test3;
		word1.setTests(tests);
		
		Word word2 = new Word();
		String expected_name2 = new String("word2");
		word2.setText(expected_name2);
		word2.setDefinition("def2");
		word2.setWritingLevel(0);
		word2.setReadingLevel(0);
		word2.setDateOfEntry(now);
		word2.setId(Domartin.getNewID());
		Test[] tests2 = new Test[3];
		Test test11 = new Test();
		test11.setDate("Mon Aug 15 09:09:00 PST 2005");
		test11.setName("level 0 reading.test");
		test11.setGrade("pass");
		tests2[0] = test11;
		Test test22 = new Test();
		test22.setDate("Tue Aug 16 10:09:00 PST 2005");
		test22.setName("level 1 reading.test");
		test22.setGrade("pass");
		tests2[1] = test22;
		Test test33 = new Test();
		test33.setDate("Wed Aug 17 11:09:00 PST 2005");
		test33.setName("level 2 reading.test");
		test33.setGrade("pass");
		tests2[2] = test33;
		word2.setTests(tests2);
		
		Word word3 = new Word();
		String expected_name3 = new String("word3");
		word3.setText(expected_name3);
		word3.setDefinition("def3");
		word3.setWritingLevel(0);
		word3.setReadingLevel(0);
		word3.setDateOfEntry(now);
		word3.setId(Domartin.getNewID());
		Test[] tests3 = new Test[3];
		Test test111 = new Test();
		test111.setDate("Sat Aug 13 01:09:00 PST 2005");		// this is the earliest (and only) writing test
		test111.setName("level 0 writing.test");
		test111.setGrade("pass");
		tests3[0] = test111;
		Test test222 = new Test();
		test222.setDate("Tue Aug 16 11:09:00 PST 2005");
		test222.setName("level 1 reading.test");
		test222.setGrade("pass");
		tests3[1] = test222;
		Test test333 = new Test();
		test333.setDate("Wed Aug 17 12:09:00 PST 2005");
		test333.setName("level 2 reading.test");
		test333.setGrade("pass");
		tests3[2] = test333;
		word3.setTests(tests3);
		
		store.addWord(word1, category, user_id, encoding);
		store.addWord(word2, category, user_id, encoding);
		store.addWord(word3, category, user_id, encoding);
	}
		
	
	/**
	* one day  = 86400000  milliseconds
	* one week = 604800000 milliseconds
	*/
	
	// if u need to add ids or last/next test records
	private void createLists(FileStorage store, String user_id, Vector elt_vector, String type)
	{
		// create lists
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.setSubject(Constants.VOCAB);
		Vector all_word_categories = store.getWordCategories("exclusive", user_id);
		wltd.createLists(all_word_categories, store, user_id);
	}
	
	/*
	    // ad ids
	    int size = all_word_categories.size();
	    int i = 0;
	    while(i<size)
	    {
		    String cat = (String)all_word_categories.get(i);
		    int ids = store.addIds(cat, user_id); 
		    i++;
	    }
	*/
	
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
