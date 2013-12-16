package org.catechis.file;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import junit.framework.TestCase;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.catechis.dto.SimpleWord;
import org.catechis.dto.WeeklyReport;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordCategory;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordLastTestDates;
import org.catechis.file.WordNextTestDates;
import org.catechis.juksong.TestDateEvaluator;
import org.catechis.lists.Sarray;
import org.catechis.lists.Sortable;
import org.catechis.JDOMSolution;
import org.catechis.Transformer;
import org.catechis.WordTestDateUtility;
import org.catechis.Domartin;
import org.catechis.Storage;
import org.catechis.FileStorage;
import org.catechis.admin.JDOMFiles;
import org.catechis.constants.Constants;;

public class FileWordCategoriesManagerTest extends TestCase
{
	public FileWordCategoriesManagerTest(String name) 
	{
		super(name);
	}
	
	public void testAddWordCategory()
	{
		//System.out.println("FileWordCategoriesManagerTest.AddWordCategory");
		String user_id = new String("new");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		WordCategory word_cat = new WordCategory();
		word_cat.setCategoryType(Constants.EXCLUSIVE);
		word_cat.setName("Test");
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		long cat_id = fwcm.addWordCategory(word_cat, user_id, subject, current_dir, encoding);
		WordCategory cat = fwcm.getWordCategory(cat_id, user_id, subject, current_dir);
		long expected_id = cat_id;
		long actual_id = cat.getId();
		boolean done = fwcm.deleteCategory(actual_id, user_id, subject, current_dir, encoding);
		//System.out.println("done? "+done);
		//printLog(fwcm.getLog());
		assertEquals(expected_id, actual_id);
	}
	
	public void testAddWordCategoryWithWords()
	{
		//System.out.println("FileWordCategoriesManagerTest.testAddWordCategoryWithWords");
		String user_id = new String("new");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		WordCategory word_cat = new WordCategory();
		word_cat.setCategoryType(Constants.EXCLUSIVE);
		word_cat.setName("Test");
		Vector category_words = new Vector();
		SimpleWord simple_word1 = new SimpleWord();
		simple_word1.setDefinition("def1");
		simple_word1.setText("text1");
		simple_word1.setWordId(1);
		simple_word1.setWordPath("test.xml");
		category_words.add(simple_word1); // add word 1
		SimpleWord simple_word2 = new SimpleWord();
		simple_word2.setDefinition("def2");
		simple_word2.setText("text2");
		simple_word2.setWordId(2);
		simple_word2.setWordPath("test.xml");
		category_words.add(simple_word2); // add word 2
		word_cat.setCategoryWords(category_words);
		//System.out.println("calculated number of words: "+word_cat.calculateTotalWords());
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		long cat_id = fwcm.addWordCategory(word_cat, user_id, subject, current_dir, encoding);
		WordCategory returned_cat = fwcm.getWordCategory(cat_id, user_id, subject, current_dir);
		long expected_number_of_words = +word_cat.calculateTotalWords();
		long actual_number_of_words = returned_cat.calculateTotalWords();
		boolean done = fwcm.deleteCategory(cat_id, user_id, subject, current_dir, encoding);
		//System.out.println("done? "+done);
		//printLog(fwcm.getLog());
		assertEquals(expected_number_of_words, actual_number_of_words);
	}
	
	public void testGetWordCategoryNames()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategoryNames");
		String user_id = new String("new");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		Vector word_cats = fwcm.getWordCategoryNames(user_id, subject, current_dir);
		int expected_number_of_cats = 3;
		int actual_number_of_cats = word_cats.size();
		//System.out.println("categories --------------------------");
		//printCategories(word_cats);
		//System.out.println("log ---------------------------------");
		//printLog(fwcm.getLog());
		assertEquals(expected_number_of_cats, actual_number_of_cats);
	}
	
	public void testGetExclusiveWordCategoryNames()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategoryNames");
		String user_id = new String("new");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		Vector word_cats = fwcm.getAllExclusiveCategories(user_id, subject, current_dir);
		int expected_number_of_cats = 2;
		int actual_number_of_cats = word_cats.size();
		//System.out.println("categories --------------------------");
		//printCategories(word_cats);
		//System.out.println("log ---------------------------------");
		//printLog(fwcm.getLog());
		assertEquals(expected_number_of_cats, actual_number_of_cats);
	}
	
	public void testGetExclusiveWordCategoryNames2()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategoryNames");
		String user_id = new String("-5519451928541341469");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		Vector word_cats = fwcm.getAllExclusiveCategories(user_id, subject, current_dir);
		int expected_number_of_cats = 2;
		int actual_number_of_cats = word_cats.size();
		//System.out.println("categories --------------------------");
		//printCategories(word_cats);
		//System.out.println("log ---------------------------------");
		printLog(fwcm.getLog());
		assertEquals(expected_number_of_cats, actual_number_of_cats);
	}
	
	public void testGetWordCategory()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategory -=-=-");
		String user_id = new String("-5519451928541341469");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		long cat_id = Long.parseLong("4716676107208947544");
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		WordCategory word_cat = fwcm.getWordCategory(cat_id, user_id, subject, current_dir);
		Vector words = word_cat.getCategoryWords();
		//System.out.println("words -=-=-");
		////printWords(words);
		//System.out.println("words -=-=-");
		int expected = 14;
		int actual = words.size();
		printLog(fwcm.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	 * Utility to generate category index file entries and WordCategory files from word files.
	 *
	public void testGenerateCateogiresFromFilesUtility()
	{
		System.out.println("testGenerateCateogiresFromFilesUtility");
		String user_id = new String("prime");
		//String user_id = new String("new");
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileCategories cats = new FileCategories(current_dir);
		//Hashtable files = cats.getSortedWordCategories(user_id);
		Hashtable files = cats.getSortedWordCategories2(user_id, encoding);
		System.out.println("cats log --- start");
		printLog(cats.getLog());
		System.out.println("cats log --- end");
		FileStorage fs = new FileStorage(current_dir);
		for (Enumeration e = files.elements() ; e.hasMoreElements() ;) 
		{
			String key = (String)e.nextElement();
			String val = (String)files.get(key); // this is null and we couldn't even tell you what it should be, except maybe type...
			Vector words = fs.getWordObjects(key, user_id);
			WordCategory word_cat = new WordCategory();
			word_cat.setCategoryType(Constants.EXCLUSIVE);
			word_cat.setName(key);
			Word first_word = null;
			try
			{
				first_word = (Word)words.get(0);
				long doe = first_word.getDateOfEntry();
				if (doe==0)
				{
					TestDateEvaluator tde = new TestDateEvaluator();
					FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
					String first_date = fjdomwl.getEarliestDate(first_word);
					doe = Domartin.getMilliseconds(first_date);
				}
				word_cat.setCreationTime(doe);
				word_cat.setCategoryWords(words);
				FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
				long cat_id = fwcm.addWordCategory(word_cat, user_id, subject, current_dir, encoding);
				System.out.println(key+" - "+words.size()+" doe "+Transformer.getDateFromMilliseconds(doe+""));
				printLog(fwcm.getLog());
			} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
			{
				System.out.println(key+" - "+words.size()+" doe iioobe");
			}
		}
		int expected = 1;
		int actual = 0;
		assertEquals(expected, actual);
	}
	*/
	
	/**
	 * Utility made 21013 to take all the word files in a folder and splice each word into it's own
	 * file, and create a category entry for it.
	 * There was a bug initially in ileCategories.getSortedWordCategories2 that was using a hash
	 * and trying to put repeat keys into the list, so a series of files were being ignored.
	 * That;s what the convertSkippedFiles methods below were all about.
	 */
	public void testGenerateSeparateWordFilesUtility()
	{
		System.out.println("testGenerateSeparateWordFilesUtility ======-=======");
		String user_id = new String("-5519451928541341469");
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		//encoding = "UTF-8";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String files_path = current_dir+File.separator+"files";
		FileCategories cats = new FileCategories(current_dir);
		Hashtable files = cats.getSortedWordCategories2(user_id, encoding);
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		FileStorage fs = new FileStorage(current_dir);
		for (Enumeration e = files.elements() ; e.hasMoreElements() ;) 
		{
			String key = (String)e.nextElement();
			String val = (String)files.get(key); // this is null and we couldn't even tell you what it should be, except maybe type...
			//System.out.println("key "+key+" val "+val);
			Vector words = fs.getWordObjects(key, user_id);
			Vector simple_words = new Vector();
			WordCategory exclusive_cat = new WordCategory();
			exclusive_cat.setCategoryType(Constants.EXCLUSIVE);
			exclusive_cat.setName(key);
			for (int i = 0; i < words.size(); i++)
			{
				Word word = (Word)words.get(i);
				long doe = word.getDateOfEntry();
				if (doe==0)
				{
					TestDateEvaluator tde = new TestDateEvaluator();
					FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
					String first_date = fjdomwl.getEarliestDate(word);
					doe = Domartin.getMilliseconds(first_date);
				}
				exclusive_cat.setCreationTime(doe);
				Vector single_word = new Vector();
				single_word.add(word);
				String word_id = Long.toString(word.getId());
				if (Transformer.getDateFromMilliseconds(doe+"").equals("01/01/70"))
				{
					WordTestDateUtility wtdu = new WordTestDateUtility();
					String result = wtdu.evaluateWordsFirstTestDate(word);
					if (result.equals("no tests"))
					{
						result = Long.toString(new Date().getTime());
					}
					doe = Long.parseLong(result);
				}
				SimpleWord simple_word = new SimpleWord(word.getId(), word_id+".xml", word.getText(), word.getDefinition());
				System.out.println(word.getId()+" "+word_id+".xml "+word.getText()+" "+word.getDefinition());
				simple_words.add(simple_word);
				String new_file_path = files_path+File.separator+user_id+File.separator+word_id+".xml";
				File file = new File(new_file_path);
				try 
				{
					boolean created = file.createNewFile();
				} catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					//System.out.println("ioe: File not created");
					e1.printStackTrace();
				}
				JDOMSolution jdom = new JDOMSolution(file); // the file is actually not used.
				try
				{
					jdom.addSingleWord(word, encoding, files_path, user_id);
					fs.addWord(word, word_id+".xml", user_id, encoding);
				} catch (Exception ex)
				{
					System.out.println("add word, add single word npe "+key+" - "+word.getDefinition()+" doe "+Transformer.getDateFromMilliseconds(doe+""));
					printLog(jdom.getLog());
					ex.printStackTrace();
					break;
				}
				//System.out.println(key+" - "+word.getDefinition()+" doe "+Transformer.getDateFromMilliseconds(doe+""));
			}
			exclusive_cat.setCategoryWords(simple_words);
			fwcm = new FileWordCategoriesManager();
			long cat_id = fwcm.addWordCategory(exclusive_cat, user_id, subject, current_dir, encoding);
			if (cat_id == -1)
			{
				// cat exists, do nothing.
			}
			printLog(fwcm.getLog());
			fwcm.resetLog();
			String path_to_old_cat = files_path+File.separator+user_id+File.separator+key; 
			File file_to = new File(path_to_old_cat);
			boolean exists = file_to.exists();
			boolean deleted = file_to.delete();
			if (!deleted||!exists)
			{
				System.out.println("not deleted or not exists: file "+path_to_old_cat+" deleted "+key+"? "+deleted+" exists? "+exists);
			}
		}
		
		// convert missed files
		convertSkippedFiles();
		
		fwcm = new FileWordCategoriesManager();
		
		Vector new_cats = fwcm.getAllExclusiveCategories(user_id, subject, current_dir);
		
		//System.out.println("new_cats --- start");
		//printCategories(new_cats);
		//System.out.println("new_cats --- end");
		
		//Sarray scats = fwcm.getSortedCategories(user_id, subject, current_dir, Constants.EXCLUSIVE);
		//System.out.println("new_cats --- start");
		//printSarray(scats);
		//System.out.println("new_cats --- end");
		
		int expected = 1;
		int actual = 0;
		assertEquals(expected, actual);
	}
	
	
	
	private Vector convertSkippedFiles()
	{
		Vector skipped_files = new Vector();
		skipped_files.add("JanuaryB-7.xml");
		skipped_files.add("JanuaryB-7.xml");
		skipped_files.add("JanuaryB-17.xml");
		skipped_files.add("JanuaryB-17.xml");
		skipped_files.add("JanuaryB-30.xml ");
		skipped_files.add("JanuaryB-30.xml");
		skipped_files.add("JanuaryB-3.xml");
		skipped_files.add("JanuaryB-3.xml");
		skipped_files.add("random words 2 I-29.xml");
		skipped_files.add("random words 2 I-29.xml");
		skipped_files.add("random words 2 I-27.xml");
		skipped_files.add("random words 2 I-27.xml");
		skipped_files.add("random words 2 II.xml");
		skipped_files.add("random words 2 I-25.xml");
		skipped_files.add("random words 2 I-25.xml");
		skipped_files.add("random words 1.xml");
		skipped_files.add("random words 2 II.xml");
		skipped_files.add("random words 2 I-1.xml");
		skipped_files.add("random words 2 I-1.xml");
		skipped_files.add("random words 2 I-6.xml");
		skipped_files.add("random words 2 I-6.xml");
		skipped_files.add("random words 2 I-9.xml");
		skipped_files.add("random words 2 I-9.xml");
		skipped_files.add("random words 2 I-16.xml");
		skipped_files.add("random words 2 I-16.xml");
		skipped_files.add("random words 2 I-21.xml");
		skipped_files.add("random words 2 I-21.xml");
		skipped_files.add("random words 2 I-24.xml");
		skipped_files.add("random words 2 I-24.xml");
		return skipped_files;
	}
	
	private Hashtable convertSkippedFilesHash(Hashtable skipped_files)
	{
		String now = Long.toString(new Date().getTime());
		skipped_files.put(now, "JanuaryB-7.xml");
		skipped_files.put(now, "JanuaryB-7.xml");
		skipped_files.put(now, "JanuaryB-17.xml");
		skipped_files.put(now, "JanuaryB-17.xml");
		skipped_files.put(now, "JanuaryB-30.xml");
		skipped_files.put(now, "JanuaryB-30.xml");
		skipped_files.put(now, "JanuaryB-3.xml");
		skipped_files.put(now, "JanuaryB-3.xml");
		skipped_files.put(now, "random words 2 I-29.xml");
		skipped_files.put(now, "random words 2 I-29.xml");
		skipped_files.put(now, "random words 2 I-27.xml");
		skipped_files.put(now, "random words 2 I-27.xml");
		skipped_files.put(now, "random words 2 II.xml");
		skipped_files.put(now, "random words 2 I-25.xml");
		skipped_files.put(now, "random words 2 I-25.xml");
		skipped_files.put(now, "random words 1.xml");
		skipped_files.put(now, "random words 2 II.xml");
		skipped_files.put(now, "random words 2 I-1.xml");
		skipped_files.put(now, "random words 2 I-1.xml");
		skipped_files.put(now, "random words 2 I-6.xml");
		skipped_files.put(now, "random words 2 I-6.xml");
		skipped_files.put(now, "random words 2 I-9.xml");
		skipped_files.put(now, "random words 2 I-9.xml");
		skipped_files.put(now, "random words 2 I-16.xml");
		skipped_files.put(now, "random words 2 I-16.xml");
		skipped_files.put(now, "random words 2 I-21.xml");
		skipped_files.put(now, "random words 2 I-21.xml");
		skipped_files.put(now, "random words 2 I-24.xml");
		skipped_files.put(now, "random words 2 I-24.xml");
		return skipped_files;
	}
	
	
	public void testGetSortedCategoryNames()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategoryNames");
		//String user_id = new String("-5519451928541341468");
		String user_id = new String("prime");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		Sarray word_catsarray = new Sarray();
		try
		{
			word_catsarray = fwcm.getSortedCategories(user_id, subject, current_dir, Constants.EXCLUSIVE);
		} catch (java.lang.NullPointerException npe)
		{
			//System.out.println("npe +++++++++++++++++");
			//printLog(fwcm.getLog());
		}
		int expected_number_of_cats = 117;
		int actual_number_of_cats = word_catsarray.size();
        for (int i = 0; i < word_catsarray.size(); i++)
        {
            Sortable s = (Sortable)word_catsarray.elementAt(i);
            String object_key = (String)s.getKey();
            WordCategory word_cat = (WordCategory)s.getObject();
           // System.out.println(Transformer.getKoreanDateFromMilliseconds(Long.toString(word_cat.getCreationTime()))
            //		+" 	"+word_cat.getName()
			//		+"	 	 	 	"+word_cat.getTotalWordCount());
        }
		assertEquals(expected_number_of_cats, actual_number_of_cats);
	}

	/*
	public void testGallery()
	{
		//System.out.println("FileWordCategoriesManagerTest.testGetWordCategoryNames");
		String user_id = new String("-5519451928541341468");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		Hashtable eyem_files = getGraphicsFiles("oneminpasteight", "ompe-");
		int expected_number_of_cats = 84;
		int actual_number_of_cats = eyem_files.size();

		assertEquals(expected_number_of_cats, actual_number_of_cats);
	}
	*/
	
	/**
	 * This is for the gallery app which has no tests.
	 */
	private Hashtable getGraphicsFiles(String file_name, String root_name)
	{
		String bin_path = System.getProperty("user.dir");
		System.out.println("bin_path "+bin_path);
		Properties properties = System.getProperties();
		String sep = new String(properties.getProperty("file.separator"));
		int last_slash = bin_path.lastIndexOf(sep);
		System.out.println("last_slash "+last_slash);
		String app_path = bin_path.substring(0, last_slash);
		System.out.println("app_path "+app_path);
		//String path = new String(app_path+sep+"webapps"+sep+"gallery"+sep+"sketch"+sep+file_name);
		String path = new String(app_path+sep+"catechis"+sep+"sketch"+sep+file_name);
		System.out.println("Gallery-"+file_name+".getGraphicsFiles: Looking in "+path);
		File user_files_dir = new File(path);
		String[] files = user_files_dir.list();
		Hashtable user_files = new Hashtable();
		//System.out.println("Gallery-"+file_name+".getGraphicsFiles: "+files.length+" files");
		for (int i = 0; i < files.length; i++) 
		{
			try
			{
				StringBuffer index_buffer = new StringBuffer();
				String file = files[i];
				int name_end = file.indexOf(root_name)+root_name.length();
				file = Domartin.getFileWithoutExtension(file); // ompe-0lw
				int end_index = (file.length()-2);
				String substring = file.substring(name_end, end_index);
				String index_string = new String(index_buffer);
				user_files.put(substring, file);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{ 
				//System.out.println("Gallery-aioob at "+i);
				break;
			}
		}
		return user_files;
	}
	
	public void testAddWordsToCategory()
	{
		//System.out.println("testAddWordsToCategory =-=-=-=-=-=-=-=-=");
		String user_id = new String("prime");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		Sarray word_catsarray = fwcm.getSortedCategories(user_id, subject, current_dir, Constants.EXCLUSIVE);
		Sortable s = (Sortable)word_catsarray.elementAt(0);
        String object_key = (String)s.getKey();
        WordCategory word_cat = (WordCategory)s.getObject();
        //System.out.println("name of cat to add word to: "+word_cat.getName()+" id "+word_cat.getId());
        long cat_id = word_cat.getId();
        int expected = word_cat.getTotalWordCount()+1;
        word_cat = fwcm.getWordCategory(cat_id, user_id, subject, current_dir);
        Vector words = word_cat.getCategoryWords();
		int expected_words = words.size()+1;
		Date today = new Date();
		String name = today.toString();
		long word_id = today.getTime();
		Vector category_words = new Vector();
		Word simple_word1 = new Word();
		simple_word1.setDefinition("def1");
		simple_word1.setText("text1");
		simple_word1.setId(word_id);
		simple_word1.setCategory(word_cat.getName());
		category_words.add(simple_word1);
		fwcm.addWordsToCategory(user_id, word_cat.getId(), subject, current_dir, category_words, encoding);
		//printLog(fwcm.getLog());
        word_cat = fwcm.getWordCategory(cat_id, user_id, subject, current_dir);
        words = word_cat.getCategoryWords();
        int actual_words = words.size();
		assertEquals(expected, actual_words);
	}
	
	public void testGetCategoryId()
	{
		//System.out.println("testGetCategoryId");
		String user_id = new String("prime");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		String category_name = "october1-01.xml";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		long actual = fwcm.getCategoryId(category_name, user_id, subject, current_dir);
		long expected = Long.parseLong("-5368021119795338553");
		assertEquals(expected, actual);
	}
	
	/**
	 * We had a problem getting a number format exception from this methods result.
	 */
	public void testGetCategoryId2()
	{
		//System.out.println("testGetCategoryId");
		String user_id = new String("-5519451928541341469");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String action_time = Long.toString(new Date().getTime());
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		String category_name = "seminar.xml";
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		long actual = fwcm.getCategoryId(category_name, user_id, subject, current_dir);
		//System.out.println("testGetCategoryId actual: "+actual);
		long expected = Long.parseLong("-5519451928541341469");
		assertEquals(expected, actual);
	}
	
	public void testUpdateCategory()
	{
		System.out.println("FileWordCategoriesManagerTest.testUpdateCategory");
		String user_id = new String("new");
		user_id = "prime";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		long now = new Date().getTime();
		String action_time = Long.toString(now);
		String subject = Constants.VOCAB;
		String encoding = "euc-kr";
		WordCategory word_cat1 = new WordCategory();
		word_cat1.setCategoryType(Constants.EXCLUSIVE);
		word_cat1.setName("Test1 "+action_time);
		word_cat1.setCreationTime(now);
		Vector category_words = new Vector();
		SimpleWord simple_word1 = new SimpleWord();
		simple_word1.setDefinition("def1");
		simple_word1.setText("text1");
		simple_word1.setWordId(1);
		simple_word1.setWordPath("test.xml");
		category_words.add(simple_word1); // add word 1
		word_cat1.setCategoryWords(category_words);
		
		WordCategory word_cat2 = new WordCategory();
		word_cat2.setCategoryType(Constants.EXCLUSIVE);
		word_cat2.setName("Test2 "+action_time);
		word_cat2.setCreationTime(now+1);
		SimpleWord simple_word2 = new SimpleWord();
		simple_word2.setDefinition("def2");
		simple_word2.setText("text2");
		simple_word2.setWordId(2);
		simple_word2.setWordPath("test.xml");
		category_words.add(simple_word2); // add word 2
		word_cat2.setCategoryWords(category_words);
		
		//System.out.println("calculated number of words: "+word_cat.calculateTotalWords());
		FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
		long cat_id1 = fwcm.addWordCategory(word_cat1, user_id, subject, current_dir, encoding);
		long cat_id2 = fwcm.addWordCategory(word_cat2, user_id, subject, current_dir, encoding);
		System.out.println("cat_id1 "+cat_id1);
		System.out.println("cat_id2 "+cat_id2);
		
		WordCategory returned_cat1 = fwcm.getWordCategory(cat_id1, user_id, subject, current_dir);
		WordCategory returned_cat2 = fwcm.getWordCategory(cat_id2, user_id, subject, current_dir);
		
		category_words.add(simple_word1); // add word 1
		category_words.add(simple_word2); // add word 2
		word_cat2.setCategoryWords(category_words);
		System.out.println("category_words "+category_words.size());
		System.out.println("word_cat2.calculateTotalWords() "+word_cat2.calculateTotalWords());
		fwcm.updateCategory(word_cat2, user_id, subject, current_dir, encoding);
		long expected_number_of_words = category_words.size();
		WordCategory returned_cat2_after = fwcm.getWordCategory(cat_id2, user_id, subject, current_dir);
		System.out.println("fwcm log ----=");
		printLog(fwcm.getLog());
		System.out.println("fwcm log ----=");
		// recalculate new number of words
		returned_cat2 = fwcm.getWordCategory(cat_id2, user_id, subject, current_dir);
		long actual_number_of_words = returned_cat2.calculateTotalWords();
		category_words = returned_cat2.getCategoryWords();
		//System.out.println("category_words after update "+category_words.size());
		System.out.println("word_cat2.calculateTotalWords() "+word_cat2.calculateTotalWords());
		
		boolean done1 = fwcm.deleteCategory(cat_id1, user_id, subject, current_dir, encoding);
		boolean done2 = fwcm.deleteCategory(cat_id2, user_id, subject, current_dir, encoding);
		//System.out.println("done? "+done);
		//printLog(fwcm.getLog());
		assertEquals(expected_number_of_words, actual_number_of_words);
	}
	
	private void printCategories(Vector log)
	{
		System.out.println("name 	type		date	id				words");
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			WordCategory word_cat = (WordCategory)log.get(i);
			System.out.println(word_cat.getName()
					+"	 "+word_cat.getCategoryType()
					+Transformer.getDateFromMilliseconds(Long.toString(word_cat.getCreationTime()))
					+"	"+word_cat.getId()
					+"	"+word_cat.getTotalWordCount());
			i++;
		}
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
	
	private void printSarray(Sarray word_catsarray)
	{
		for (int i = 0; i < word_catsarray.size(); i++)
        {
            Sortable s = (Sortable)word_catsarray.elementAt(i);
            String object_key = (String)s.getKey();
            WordCategory word_cat = (WordCategory)s.getObject();
            System.out.println(i+" 	"
            		+Transformer.getKoreanDateFromMilliseconds(Long.toString(word_cat.getCreationTime()))
            		+"		"+word_cat.getName()+" 		"
            		+" 		"+word_cat.getTotalWordCount());
        }
	}
	
	private void printWords(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			SimpleWord word = (SimpleWord)log.get(i);
			System.out.println(word.getText()+" "+word.getDefinition());
			i++;
		}
	}
	

}
