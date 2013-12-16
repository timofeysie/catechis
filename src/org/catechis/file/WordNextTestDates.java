package org.catechis.file;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;

import org.catechis.FileStorage;
import org.catechis.Storage;
import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.WordTestDateUtility;
import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordNextTestDate;
import org.catechis.dto.WordLastTestDates;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.constants.Constants;

/**
*A rather important class to get the next word that needs to be tested.
*/
public class WordNextTestDates
{

	private String next_test_type;
	
	private WordNextTestDate wntd;
	
	private int number_of_waiting_reading_tests;
	
	private int number_of_waiting_writing_tests;
	
	private String words_next_test_date_file_name;

	private String word_category;
	
	public WordNextTestDates()
	{
		log = new Vector();
		log.add("WordNextTestDates");
		next_test_type = new String();
	}

	/**
	*Return the next word either reading or writing based on the the current time.
	*<p>This should be the most distant time represented by a file named after its date in milliseconds.
	*<p>First this method must get the most distant test file, then it must find
	* the word object and return that.
	* <p>If the most distant date more recent than the current time, that means there are no words to 
	* be tested yet, and the text and definition of the word returned is filled with Constants.NA,
	* and the date of entry field is filled with the most distant date minus the current time in 
	* milliseconds so that the user can be informed of how much time is remaining before they can
	* run the next test.
	*/
	public Word getNextTestWord(String user_id, String current_dir, String time, String subject)
	{
		//log.add("WordNextTestDates.getNextTestWord: log ********");
		//log.add("time "+time);
		String path_to_reading_files = getPathToFiles(user_id, current_dir, "wntd", Constants.READING, subject);
		String path_to_writing_files = getPathToFiles(user_id, current_dir, "wntd", Constants.WRITING, subject);
		File reading_files = new File(path_to_reading_files);
		File writing_files = new File(path_to_writing_files);
		String [] reading_file_names = reading_files.list();
		Arrays.sort(reading_file_names);
		String [] writing_file_names = writing_files.list();
		Arrays.sort(writing_file_names);
		//log.add("reading_file_names "+reading_file_names.length+" writing_file_names "+writing_file_names.length);
		// do we need to sort these? test it!
		String first_reading_file = Domartin.getFileWithoutExtension(reading_file_names [0]);
		String first_writing_file = Domartin.getFileWithoutExtension(writing_file_names [0]);
		//log.add("1st reading file "+first_reading_file+" 1st writing file "+first_writing_file);
		String and_the_winner_is = WordTestDateUtility.getEarlierTime(first_reading_file, first_writing_file);
		//log.add("remote date "+Transformer.getLongDateFromMilliseconds(and_the_winner_is));
		next_test_type = Constants.READING; 
		if (and_the_winner_is.equals(first_writing_file))
		{
			next_test_type = Constants.WRITING;
		}
		Word next_word = new Word();
		if (Long.parseLong(and_the_winner_is) > Long.parseLong(time))
		{
			next_word.setText(Constants.NA); // no words to be tested yet.
			next_word.setDefinition(Constants.NA);
			next_word.setDateOfEntry(Long.parseLong(and_the_winner_is) - Long.parseLong(time));
			return next_word;
		} else
		{
			words_next_test_date_file_name = and_the_winner_is;
			//log.add("type "+next_test_type);
			//log.add("and_the_winner_is "+and_the_winner_is);		// file id without the .xml, which is added in fjdomwl
			//log.add("FileJDOMWordLists log -------- ");
			FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
			String path_to_file = "";
			String full_path = "";
			try
			{
				path_to_file = getPathToFiles(user_id, current_dir, "wntd", next_test_type, subject);
				full_path = addFileNameWithExtension(path_to_file, and_the_winner_is, ".xml");
				//log.add("path - "+full_path);
				wntd = fjdomwl.getWordNextTestDateObject(full_path);
				//append(fjdomwl.getLog());
				//log.add("WordNextTestDate object -------- ");
				//append(Transformer.createTable(wntd));				//search_id null- category null
				String wltd_file_key = wntd.getWltdFileKey();
				word_category = wntd.getCategory();
				String word_id = wntd.getWordId();
				next_word = fjdomwl.getSpecificWord(word_id, word_category, user_id, current_dir);
			} catch (java.lang.NullPointerException npe)
			{
				//log.add("WordNextTestDates.getNextTestWord: npe!");
				//log.add("path_to_file "+path_to_file);
				//log.add("full_path "+full_path);
				append(fjdomwl.getLog());
			}
		}
		return next_word;
	}
	
	public WordNextTestDate getSpecificWordNextTestDate(String user_id, String current_dir, String wntd_file_key, String subject)
	{
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
		WordNextTestDate wntd = new WordNextTestDate();
		String path_to_file = "";
		String full_path = "";
		path_to_file = getPathToFiles(user_id, current_dir, "wntd", next_test_type, subject);
		full_path = addFileNameWithExtension(path_to_file, wntd_file_key, ".xml");
		//log.add("path - "+full_path);
		File file    = new File(full_path);
		//log.add("exits? - "+file.exists());
		wntd = fjdomwl.getWordNextTestDateObject(full_path);
		return wntd;
	}
	
	/**
	 * Get a list of words from wntd files.  Based on the method above which only gets the next word.
	 *<p>If the test in reading and writing,
	 * In order to tell what kind of test needs to be performed, we are setting the type in
	 * tests[0] which will replace all the other tests to save memory.
	 * In order to tell what file in the wntd folder the word came from, so the test files can be
	 * updated after the test is scored, the wntd file name is stored in the test[0] date member.
	 * getNextTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 *Return the next word either reading or writing based on the the current time.
	*<p>This should be the most distant time represented by a file named after its date in milliseconds.
	*<p>First this method must get the most distant test file, then it must find
	* the word object and return that.
	* @param s_number_of_words The number of words you want returned.  Use Constants.ALL for the maximum.
	* @return A Vector of word objects to be tested.  The amount returned will either be the same as
	* the last argument for this method, or limited the number to those forward of the current time
	* if there are less than that amount.
	*/
	public Vector getNextTestWords(String user_id, String current_dir, String time, String subject, String test_type, String s_number_of_words)
	{
		log.add("WordNextTestDates.getNextTestWords");
		String path_to_reading_files = getPathToFiles(user_id, current_dir, "wntd", Constants.READING, subject);
		String path_to_writing_files = getPathToFiles(user_id, current_dir, "wntd", Constants.WRITING, subject);
		File reading_files = new File(path_to_reading_files);
		File writing_files = new File(path_to_writing_files);
		String [] reading_file_names = reading_files.list();
		String [] writing_file_names = writing_files.list();
		//log.add("reading_file_names "+reading_file_names.length+" writing_file_names "+writing_file_names.length);
		Arrays.sort(reading_file_names);
		Arrays.sort(writing_file_names);
		//log.add("WordNextTestDates.getNextTestWords: reading file names ---------");
		//append(reading_file_names);
		//log.add("WordNextTestDates.getNextTestWords: writing file names ---------");
		//append(writing_file_names);
		String now = new Date().getTime()+"";
		Word next_word = new Word();
		Vector words = new Vector();
		int number_of_words = checkForMaxNumberOfWords(s_number_of_words, reading_file_names);
		int w = 0;
		while (w<number_of_words)
		{
			String first_reading_file = now; // make the latest test date and catch times
			String first_writing_file = now; // in case one list is longer than the other.
			try 
			{	
				first_reading_file = Domartin.getFileWithoutExtension(reading_file_names [w]);
			} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
			{
				first_reading_file = now;
			}
			try
			{
				first_writing_file = Domartin.getFileWithoutExtension(writing_file_names [w]);
			} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
			{
				first_writing_file = now;
			}
			if (test_type.equals(Constants.READING_AND_WRITING))
			{
				String and_the_winner_is = WordTestDateUtility.getEarlierTime(first_reading_file, first_writing_file);
				next_test_type = Constants.READING; // if type is 'both' set to reading unless it's writing
				log.add("between "+first_reading_file+" and "+first_writing_file+" the winner is:");
				log.add("        "+and_the_winner_is);
				if (and_the_winner_is.equals(first_writing_file))
				{
					next_test_type = Constants.WRITING;
					log.add("writing: "+and_the_winner_is+" is equal to "+first_writing_file);
				} else
				{
					log.add("reading: "+and_the_winner_is+" is not equal to "+first_writing_file);
				}
				words_next_test_date_file_name = and_the_winner_is;
			} else if (test_type.equals(Constants.READING))
			{
				words_next_test_date_file_name = first_reading_file;
				next_test_type = Constants.READING;
			} else if (test_type.equals(Constants.WRITING))
			{
				words_next_test_date_file_name = first_writing_file;
				next_test_type = Constants.WRITING;
			}
			if (Long.parseLong(words_next_test_date_file_name) > Long.parseLong(time))
			{
				return words;
			} else
			{
				FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
				String path_to_file = "";
				String full_path = "";
				try
				{
					path_to_file = getPathToFiles(user_id, current_dir, "wntd", next_test_type, subject);
					full_path = addFileNameWithExtension(path_to_file, words_next_test_date_file_name, ".xml");
					wntd = fjdomwl.getWordNextTestDateObject(full_path);
					//append(fjdomwl.getLog());
					//log.add("WordNextTestDate object -------- ");
					//append(Transformer.createTable(wntd));				//search_id null- category null
					String wltd_file_key = wntd.getWltdFileKey();
					word_category = wntd.getCategory();
					String word_id = wntd.getWordId();
					next_word = fjdomwl.getSpecificWord(word_id, word_category, user_id, current_dir);
					Test test = new Test();
					//log.add("WordNextTestDate type "+next_test_type);
					test.setType(next_test_type);
					test.setDate(words_next_test_date_file_name);
					Test [] tests = new Test[1];
					tests[0] = test;
					next_word.setTests(tests);
				} catch (java.lang.NullPointerException npe)
				{
					log.add("WordNextTestDates.getNextTestWord: npe! last word reached?");
					//log.add("path_to_file "+path_to_file);
					//log.add("full_path "+full_path);
					//append(fjdomwl.getLog());
					return words;
				}
			}
			words.add(next_word);
			w++;
		}
		return words;
	}
	
	/**
	 * Get a list of words from wntd files.  Based on the method above which only gets the next word.
	 *<p>If the test in reading and writing,
	 * In order to tell what kind of test needs to be performed, we are setting the type in
	 * tests[0] which will replace all the other tests to save memory.
	 * In order to tell what file in the wntd folder the word came from, so the test files can be
	 * updated after the test is scored, the wntd file name is stored in the test[0] date member.
	 * getNextTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 *Return the next word either reading or writing based on the the current time.
	*<p>This should be the most distant time represented by a file named after its date in milliseconds.
	*<p>First this method must get the most distant test file, then it must find
	* the word object and return that.
	* @param s_number_of_words The number of words you want returned.  Use Constants.ALL for the maximum.
	* @return A Vector of word objects to be tested.  The amount returned will either be the same as
	* the last argument for this method, or limited the number to those forward of the current time
	* if there are less than that amount.
	*/
	public Vector getTestWords(String user_id, String current_dir, String time, String subject, String test_type, String s_number_of_words)
	{
		log.add("WordNextTestDates.getNextTestWords"); 
		String [] reading_file_names = setupFileNames(Constants.READING, user_id, current_dir, subject);
		String [] writing_file_names = setupFileNames(Constants.WRITING, user_id, current_dir, subject);
		if (reading_file_names == null)
		{
			reading_file_names = new String [0];
			log.add("reading_file_names is null");
		}
		if (writing_file_names == null)
		{
			writing_file_names = new String [0];
			log.add("writing_file_names is null");
		}
		Word next_word = new Word(); Vector words = new Vector();
		log.add("reading_file_names "+reading_file_names.length+" writing_file_names "+writing_file_names.length);
		int number_of_words = checkForMaxNumberOfWords(s_number_of_words, reading_file_names);
		int w = 1;
		Hashtable all_words = combineLists(reading_file_names, writing_file_names);
		int number_of_all_words = checkForMaxNumberOfWords(s_number_of_words, reading_file_names);
		log.add("all_words "+all_words.size());
		log.add("number_of_all_words chosen "+number_of_all_words);
		Vector v = new Vector(all_words.keySet());
	    Collections.sort(v);
	    Iterator it = v.iterator();
	    while (it.hasNext()) 
	    {
	        String file_name =  (String)it.next();
	        String type = (String)all_words.get(file_name);
			String first_file = Domartin.getFileWithoutExtension(file_name);
			
			//log.add("iteration: file_name "+file_name+" type "+type);
			words_next_test_date_file_name = first_file;
			next_test_type = test_type;
			String path_to_file = getPathToFiles(user_id, current_dir, "wntd", type, subject);
			String full_path = addFileNameWithExtension(path_to_file, words_next_test_date_file_name, ".xml");
			log.add("full_path "+full_path);
			FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
			wntd = fjdomwl.getWordNextTestDateObject(full_path);
			append(Transformer.createTable(getNextWord(user_id, current_dir, subject)));
			if (test_type.equals(Constants.READING_AND_WRITING))
			{
				words_next_test_date_file_name = first_file;
				next_test_type = type;
				//log.add("test_type.equals(Constants.READING_AND_WRITING) "+first_file);
			} else if (test_type.equals(Constants.READING)&&type.equals(Constants.READING))
			{
				words_next_test_date_file_name = first_file;
				next_test_type = Constants.READING;
				//log.add("test_type.equals(Constants.READING)&&type.equals(Constants.READING) "+first_file);
			} else if (test_type.equals(Constants.WRITING)&&type.equals(Constants.WRITING))
			{
				words_next_test_date_file_name = first_file;
				next_test_type = Constants.WRITING;
				//log.add("test_type.equals(Constants.WRITING)&&type.equals(Constants.WRITING)"+first_file);
			} else
			{
				//log.add("next");
				it.next();
			}
			try
			{
				if (Long.parseLong(words_next_test_date_file_name) > Long.parseLong(time))
				{
					return words;
				} else
				{
					//try
					//{
						next_word = getNextWord(user_id, current_dir, subject);
						Transformer.createTable(next_word);
					//} catch (java.lang.NullPointerException npe)
					//{
						//log.add("WordNextTestDates.getNextTestWord: npe! last word reached?");
						//log.add(npe.);
						//return words;
					//}
				}
			} catch (java.lang.NumberFormatException nfe)
			{
				log.add("WordNextTestDates.getNextTestWord: nfe for words_next_test_date_file_name "+words_next_test_date_file_name);
			}
			words.add(next_word);
			w++;
			if (w>number_of_words)
			{
				return words;
			}
		}
		return words;
	}
	
	private String [] setupFileNames(String type, String user_id, String current_dir, String subject)
	{
		//log.add("WordNextTestDates.setupFileNames");
		String path_to_files = getPathToFiles(user_id, current_dir, "wntd", type, subject);
		File files = new File(path_to_files);
		String [] file_names = files.list();
		return file_names;
	}
	
	private Word getNextWord(String user_id, String current_dir, String subject)
	{
		Word next_word = null;
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
		String path_to_file = getPathToFiles(user_id, current_dir, "wntd", next_test_type, subject);
		String full_path = addFileNameWithExtension(path_to_file, words_next_test_date_file_name, ".xml");
		//log.add("WordNextTestDates.getNextTestWord: path to file "+full_path);
		boolean exists = new File(full_path).exists();
		//log.add("exists? "+exists);
		//log.add("WordNextTestDate object type -------- ");
		append(Transformer.createTable(wntd));	
		String wltd_file_key = wntd.getWltdFileKey();
		word_category = wntd.getCategory();
		String word_id = wntd.getWordId();
		try
		{
			next_word = fjdomwl.getSpecificWord(word_id, word_category, user_id, current_dir);
		} catch (java.lang.NullPointerException npe)
		{
			next_word = new Word();
			next_word.setId(Long.parseLong(word_id));
			next_word.setText("npe");
			next_word.setDefinition("NOE");
		}
		Test test = new Test();
		//log.add("WordNextTestDate type "+next_test_type+" (after getSpecificWord)");
		//append(fjdomwl.getLog());
		test.setType(next_test_type);
		test.setDate(words_next_test_date_file_name);
		Test [] tests = new Test[1];
		tests[0] = test;
		next_word.setTests(tests);
		return next_word;
	}
	
	private Hashtable combineLists(String [] reading_file_names, String [] writing_file_names)
	{
		Hashtable lists = new Hashtable();
		int size = reading_file_names.length;log.add("WordNextTestDates.combineLists: reading size "+size);
		int i = 0;
		while (i<size)
		{
			lists.put(reading_file_names[i], Constants.READING);
			i++;
		}
		size = writing_file_names.length;log.add("WordNextTestDates.combineLists: writing size "+size);
		i = 0;
		while (i<size)
		{
			lists.put(writing_file_names[i], Constants.WRITING);
			i++;
		}
		//log.add("WordNextTestDates.combineLists: total size "+lists.size());
		return lists;
	}
	
	/**
	 * This method limits the number of words to go through to the total number of files in the
	 * wntd reading folder if the calling method has set the number of words to 'all'.
	 * @param s_number_of_words same as from the getNextTestWords argument.
	 * @param reading_file_names in the wntd folder..
	 * @return either an integer version of the s_number_of_words or the max number of words available,
	 * even though the getNextTestWords method above will limit the number to those forward of the current time.
	 */
	private int checkForMaxNumberOfWords(String s_number_of_words, String [] reading_file_names)
	{
		int number_of_words = 0;
		if (s_number_of_words.equals(Constants.ALL))
		{
			number_of_words =  reading_file_names.length;
		} else
		{
			number_of_words = Integer.parseInt(s_number_of_words);
		}
		return number_of_words;
	}
	
	/**
	*Return the next word either reading or writing based on the the current time.
	*<p>This should be the most distant time represented by a file named after its date in milliseconds.
	*<p>First this method must get the most distant test file, then it must find
	* the word object and return that.
	*/
	public Word getSpecificNextTestWord(String user_id, String current_dir, String time, String subject, String type)
	{
		log.add("WordNextTestDates.getSpecificNextTestWord -------- ");
		//log.add("time "+time);
		String path_to_files = getPathToFiles(user_id, current_dir, "wntd", type, subject);
		File files = new File(path_to_files);
		String [] file_names = files.list();
		//printFiles(file_names);
		// do we need to sort the list?
		//ArrayList list = new ArrayList();
		//ArrayList sorted_list = Domartin.sortList(list);
		words_next_test_date_file_name = Domartin.getFileWithoutExtension(file_names [file_names.length-1]);
		//log.add("remote date "+Transformer.getLongDateFromMilliseconds(words_next_test_date_file_name));
		next_test_type = type;
		//log.add("type "+next_test_type);
		//log.add("and_the_winner_is "+words_next_test_date_file_name);		// file id without the .xml, which is added in fjdomwl
		//log.add("FileJDOMWordLists log -------- ");
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
		String full_path = addFileNameWithExtension(path_to_files, words_next_test_date_file_name, ".xml");
		wntd = fjdomwl.getWordNextTestDateObject(full_path);		// this memeber obj can be retrieved if needed.
		//append(fjdomwl.getLog());
		//log.add("WordNextTestDate object -------- ");
		//append(Transformer.createTable(wntd));				//search_id null- category null
		String wltd_file_key = wntd.getWltdFileKey();
		//word_category = wntd.getCategory();
		String word_id = wntd.getWordId();
		//log.add("WordNextTestDate cat -------- "+word_category);
		Word next_word = fjdomwl.getSpecificWord(word_id, word_category, user_id, current_dir);
		Vector waiting_tests = getWaitingTests(current_dir, user_id, type, subject);
		if (type.equals(Constants.READING))
		{
			number_of_waiting_reading_tests = waiting_tests.size();
		} else
		{
			number_of_waiting_writing_tests = waiting_tests.size();
		}
		return next_word;
	}
	
	/**
	*Return the path to a directory like user_id/vocab/lists/wntd or wltd.
	*/
	public String getPathToFiles(String user_id, String current_dir, String last_dir_name, String type, String subject)
	{
		String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
		String subj_folder_path = (user_folder_path+File.separator+subject);
		String lists_folder_path = (subj_folder_path+File.separator+"lists");
		String wtd_folder_path = (lists_folder_path+File.separator+last_dir_name+" "+type);
		return wtd_folder_path;
	}
	
	/**
	*Return the path to a directory like user_id/vocab/lists/wntd or wltd.
	*/
	public String addFileNameWithExtension(String path, String file_name, String ext)
	{
		String complete_path = (path+File.separator+file_name+ext);
		return complete_path;
	}
	
	/**
	*Returns the file names in the wntd folder minus the ones with a date in milliseconds after the current date.
	*/
	public Vector getWaitingTests(String current_dir, String user_id, String type, String subject)
	{
		String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
		String subj_folder_path = (user_folder_path+File.separator+subject);
		String lists_folder_path = (subj_folder_path+File.separator+"lists");
		String wtd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
		//log.add("WordNextTestDates.getWaitingTests: path "+wtd_folder_path);
		File folder = new File(wtd_folder_path);
		String [] files = folder.list();
		Date now = new Date();
		long this_instant = now.getTime();
		Vector results = new Vector();
		int i = 0;
		int l = 0;
		try
		{
			l = files.length;
		} catch (java.lang.NullPointerException npe)
		{
			// l stays at 0
		}
		//log.add("WordNextTestDates.getWaitingTests: length "+l);
		while (i<l)
		{
			String file_w_ext = files [i];
			String file = Domartin.getFileWithoutExtension(file_w_ext);
			long file_long = Long.parseLong(file);
			long diff = file_long-this_instant;
			String str_diff = Domartin.getElapsedTime(Long.toString(diff));
			//log.add("diff "+diff+" - "+str_diff);
			if (file_long<this_instant)
			{
				results.add(file);
				//log.add("diff "+diff+" - "+str_diff);
			}
			i++;
		}
		return results;
	}
	
	public String getNextTestType()
	{
		return next_test_type;
	}
	
	/**
	*This method arrange with FileJDOMWordLists to get the WordNextTestDate object when the caller knows the file name
	* and the type of the test.
	*/
	public WordNextTestDate getWordNextTestDate(String current_dir, String user_id, String next_test_type, String next_test_time, String subject)
	{
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
		String path_to_file = getPathToFiles(user_id, current_dir, "wntd", next_test_type, subject);
		String full_path = addFileNameWithExtension(path_to_file, next_test_time, ".xml");
		WordNextTestDate wntd = fjdomwl.getWordNextTestDateObject(full_path);
		return wntd;
	}
	
	/**
	 * Get the waiting tests for this type for each student and find the lowest amount of words
	 * waiting to be tested amunst the group and return that.
	 * @param students id-name pairs
	 * @param current_dir
	 * @param subject vocab
	 * @param type reading or writing. 
	 * @return
	 */
	public int getLowestCommonDenomenator(Hashtable students, String current_dir, String subject, String type)
	{
		log.add("WordNextTestDates.getLowestCommonDenomenator");
		boolean first_time = true;
		int lcd_soundsystem = 0;
		for (Enumeration e = students.keys() ; e.hasMoreElements() ;) 
		{
			String student_id = (String)e.nextElement();
			Vector waiting_tests = getWaitingTests(current_dir, student_id, type, subject);
			int waiting_words = waiting_tests.size();
			log.add(student_id+" waiting_words "+waiting_words);
			if (first_time)
			{
				lcd_soundsystem = waiting_words;
				first_time = false;
			} else
			{
				if (waiting_words < lcd_soundsystem)
				{
					lcd_soundsystem = waiting_words;
				}
			}
		}
		return lcd_soundsystem;
	}
	
	/** This object holds the words id, and the id of the words last test date.*/
	public WordNextTestDate getWordNextTestDate()
	{
		return wntd;
	}
	
	public int getNumberOfWaitingReadingTests()
	{
		return number_of_waiting_reading_tests;
	}
	
	public int getNumberOfWaitingWritingTests()
	{
		return number_of_waiting_writing_tests;
	}
	
	public String getWordsNextTextDateFileName()
	{
		return words_next_test_date_file_name;
	}
	
	public String getWordCategory()
	{
		return word_category;
	}
	
	/** DEGUGGING */
	private Vector log;
	
	public Vector getLog()
	{
		return log;
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
	
	public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add("WordNextTestDate.log: "+v.get(i));
			i++;
		}
	}
	
	public void append(String [] s)
	{
		int size = s.length;
		int i = 0;
		while (i<size)
		{
			log.add(i+" "+s[i]);
			i++;
		}
	}
	
	private void printTests(Word word)
	{
		Test all_tests[] = word.getTests();
		int size = all_tests.length;
		int i = 0;
		while (i<size)
		{
			Test test = all_tests[i];
			log.add(" "+i+" "+test.getName()+" "+test.getDate()+" "+test.getGrade());
			i++;
		}
	}
	
	private void printFiles(String[] file_names)
	{
		int len = file_names.length;
		int i = 0;
		while (i<len)
		{
			String file = Domartin.getFileWithoutExtension(file_names [i]);
			log.add("file date "+i+" "+Transformer.getLongDateFromMilliseconds(file));
			i++;
		}
	}

}
