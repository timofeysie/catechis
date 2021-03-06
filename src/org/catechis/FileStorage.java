package org.catechis;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Vector;
import java.util.Locale;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.servlet.ServletContext;

import org.catechis.Scoring;
import org.catechis.Domartin;
import org.catechis.dto.Word;
import org.catechis.Transformer;
import org.catechis.JDOMSolution;
import org.catechis.file.FileEdit;
import org.catechis.file.FileTestRecords;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.constants.Constants;
import org.catechis.dto.TestStats;
import org.catechis.dto.WordStats;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.RatesOfForgetting;
import org.catechis.dto.WordLastTestDates;

/**
*This class should be a link class only.  It should be able to take user info
and then parlay with other objects, like JDOMSoultion, to get the requested info
and then return it.
*<p>The early methods break this ideal, because we didn't quite understand the role
* that the Storage object should play.  The later methods, like the update* methods
* Don't do any processing, but send out to other objects to do this.  Again, those
* other objects should only do what they specialize in, such as JDOMSolution doing only
* xml specific things, where as helper methods should be elsewhere so that they can
* be shared by different Storage methods, such as MySQL or Hibernate, etc.
*/
public class FileStorage implements Storage
{

	private String root_path;
	private ServletContext context;
	private String content_type;
	private String encoding;
	private Locale locale;
	private String user_folder;
	private Vector files;
	private Vector tests;
	private Vector words;
	private Vector other; 
	private String user_role;
	
	/**
	 * This member is used during create files so that test list keys can be save in the word
	 * while the DOM is in memory.
	 */
	private JDOMSolution this_jdom;
	
	/**
	 * Used along with this_jadom in getWordObjects so that the 
	 * saveWTDKeys method can write the document.
	 */
	private String path_to_words;
	
	// for debuggin
	private String path_to_files;
	private Vector log;
	
	/**This no args constructor is  to open up the utility methods for use.*/
	public FileStorage()
	{
		log = new Vector();
	}

	/**
	*<p>The two arguments for this constructor come from these calls in the
	* index servlet:
	*<p>ServletContext context = getServlet().getServletContext();
	*<p>String context_path = context.getRealPath("/WEB-INF/");
	*/
	public FileStorage(String _root_path, ServletContext _context)
	{
		this.root_path = _root_path;
		this.context = _context;
		log = new Vector();
	}
	
	/**
	*<p>This method is for testing.
	*/
	public FileStorage(String _root_path)
	{
		this.root_path = _root_path;
		log = new Vector();
	}

	public Hashtable findUsers()
	{
		String passwords_path = (root_path+File.separator+"files"
			+File.separator+"user.passes");
		File passwords_file = new File (passwords_path);
		JDOMSolution jdom = new JDOMSolution(passwords_file, this);
		Hashtable passwords_hash = jdom.getWhateverHash("user","name","password");
		return passwords_hash;
	}
	
	public void login(String user_name)
	{
		Hashtable user_opts = getOptions(user_name);
		encoding = (String)user_opts.get("encoding");
		setLocale((String)user_opts.get("locale"));
		user_role = (String)user_opts.get("role");
		if (user_role.equals("student"))
		{
			loginStudent();
		}
		else if (user_role.equals("teacher"))
		{
			loginTeacher();
		}
	}
	
	private void loginStudent()
	{
		//loggit("FileStorage.loginStudent:");
		File user_files_dir = new File(user_folder);
		String[] files_array = user_files_dir.list();
		files = new Vector();
		EncodeString encoder = new EncodeString();
		for (int i = 0; i < files_array.length; i++) 
		{
			String file = encoder.encodeThis(files_array[i]);
			files.add(file);
		}
	}
	
	private void loginTeacher()
	{
		//loggit("FileStorage.loginTeacher:");
		String files_path = (root_path+File.separator+"files"
			+File.separator);
		File user_files_dir = new File(files_path);
		String[] files_and_folders_array = user_files_dir.list();
		files = new Vector();
		EncodeString encoder = new EncodeString();
		for (int i = 0; i < files_and_folders_array.length; i++) 
		{
			String file = encoder.encodeThis(files_and_folders_array[i]);
			files.add(file);
		}
	}
	
	public void loggit(String msg)
	{
		try
		{
			System.out.println(msg);
		}
		catch (java.lang.NullPointerException e)
		{
			System.out.println(msg);
		}
	}

	private Hashtable getOptions(String user_name)
	{
		user_folder = (root_path+File.separator+"files"
			+File.separator+user_name);
		String file_name = new String(user_name+".options");
		File file_chosen = Domartin.createFileFromUserNPath(file_name, user_folder);
		String file_path = file_chosen.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(file_chosen, this);
		Hashtable options = jdom.getOptionsHash();
		return options;
	}
	
	private void setLocale(String full_locale)
	{
		int underscore = full_locale.lastIndexOf("_");
		String language = full_locale.substring(0,underscore);
		String country = full_locale.substring((underscore+1),full_locale.length());
		//loggit("FileStorage.setLocale: Encoding set to: Language - "+language+" Country - "+country);
		locale = new Locale(language, country);
		content_type = new String("text/html;charset="+encoding);
	}
	
	public String getType(String file_name)
	{
		String extension = Domartin.getFileExtension(file_name);
		return extension;
	}
	
	/**
	*<p>How do we get the path here?  If we store the path in the session,
	* how do we substitute a db specific object that works for both?  
	* Remember, the session
	* layer should not know what kind of storage is being used.  Are we going 
	* to keep a register of storage objects that contain this info?
	*/
	public Hashtable getWordsDefs(String file_name, String user_name)
	{
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File file_chosen = new File(path_to_file);
		String file_path = file_chosen.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(file_chosen, this);
		Hashtable words_defs = jdom.getWhateverHash("word","text","definition");
		//append(jdom.getLog());
		return words_defs;
	}

	/**
	*<p>If the filename has no extension, Domartin returns a -1, so then we just
	* use the original name passed in.
	*/
	public Hashtable getTests(String file_name, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".test");
		File test_chosen = new File(path_to_test);
		String test_path = test_chosen.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(test_chosen, this);
		Hashtable tests = jdom.getWhateverKeyValHash("score","grade","date");
		int sizer = tests.size();
		return tests;
	}
	
	/**
	* JDOMsolution is passed a copy of this FileStorage object for context logging only.
	*<p>As in getTests, first we strip off any file extension, then add it again.
	*This method seemed quicker than searching the string and evaluating the extension.
	*/
	public Vector getWordObjects(String file_name, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		path_to_words = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		// loggit("FileStorage.getWordObjects: path to words "+path_to_words);
		File words_file = new File(path_to_words);
		String test_path = words_file.getAbsolutePath();
		this_jdom = new JDOMSolution(words_file, this);
		Vector words = new Vector();
		try
		{
			words = this_jdom.getWords();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FIleStorage.getWordObjects: npe for :"+path_to_words);
		}
		//log.add(jdom.getLog());
		return words;
	}
	
	/**
	* This method is the same as above but it attaches the file name
	* to that the word file can be altered later, in a mixed file environment,
	*/
	public Vector getWordObjectsForTest(String file_name, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_words = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		//loggit("FileStorage.getWordObjectsForTest: path to words "+path_to_words);
		File words_file = new File(path_to_words);
		String test_path = words_file.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(words_file, this);
		Vector words = jdom.getWordsForTest(file_name);
		try
		{
			appendWords(words);
		} catch (java.lang.NullPointerException npe)
		{
			loggit("FileStorage.getWordObjects: appendWords npe for "+file_name);
		}
		append(jdom.getLog());
		return words;
	}
	
	/**
	* The search_property is either text, definition, of id.  The search value is the desired text or def value,
	* category is the file name, and user-name speaks for itself. 
	* @param search_property is either text, definition, of id.
	* @param search_value is the desired text or def value,
	* @param category is the file name.  It can have the xml file extension or not.  We check.
	* @param user-name speaks for itself.
	* @return The word object with all elements.
	*/
	public Word getWordObject(String search_property, String search_value, String category, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(category); 
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = category;}
		String path_to_word = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		log.add("FileStorage.getWordObject: path to word "+path_to_word);
		File file = new File(path_to_word);
		JDOMSolution jdom = new JDOMSolution(file);
		Word word = new Word();
		if (search_property.equals("text"))
		{
			word = jdom.findWord(search_value);
			log.add("FileStorage.getWordObject: using text "+search_value);
		} else if (search_property.equals("definition"))
		{
			word = jdom.findWordFromDefinition(search_value);
			log.add("FileStorage.getWordObject: using definition "+search_value);
		}  else if (search_property.equals("id"))
		{
			log.add("FileStorage.getWordObject: using id "+search_value);
			word = jdom.findWordFromId(search_value); 
		} else
		{
			log.add("FileStorage.getWordObject: somethings wrong: not search property "+search_property);
		}
		log.add("FileStorage.getWordObject: jdom log @@@@@@@@@@" );
		append(jdom.getLog());
		log.add("FileStorage.getWordObject: jdom log end @@@@@@" );
		return word;
	}
	
	
	/**
	*Get all files in the users folder.
	*/
	public Vector getCategories(String user_name)
	{
		String path_to_tests = new String(root_path+File.separator
			+"files"+File.separator+user_name);
		loggit("FileStorage.getCateogires: path to tests "+path_to_tests);
		boolean exists = new File(path_to_tests).exists();
		loggit("FileStorage.getCateogires: exists? "+exists);
		path_to_files = path_to_tests;
		File user_files_dir = new File(path_to_tests);
		String[] files_array = user_files_dir.list();
		Vector files = new Vector();
		EncodeString encoder = new EncodeString();
		for (int i = 0; i < files_array.length; i++) 
		{
			String file = encoder.encodeThis(files_array[i]);
			String ext = Domartin.getFileExtension(file);
			//if (ext.equals(".xml"))
			//{
				files.add(file);
			//}
		}
		return files;
	}
	
	/**
	*Get all files with the .test extension in the files/user/ folder.
	*/
	public Vector getTestCategories(String user_name)
	{
		Vector all_files = getCategories(user_name);
		Vector tests = new Vector();
		for (int i = 0; i < all_files.size(); i++)
		{
			String file = (String)all_files.get(i);
			//log.add(i+" "+file);
			String ext = Domartin.getFileExtension(file);
			if (ext.equals(".test"))
			{
				tests.add(file);
			}
		}
		return tests;
	}
	
	/**
	*The first argument can be used to distinguish between exclusive and non-exclusive
	* categories.
	*<p>
	*/
	public Vector getWordCategories(String category, String user_name)
	{
		Vector all_files = getCategories(user_name);
		Vector tests = new Vector();
		for (int i = 0; i < all_files.size(); i++)
		{
			String file = (String)all_files.get(i);
			String ext = Domartin.getFileExtension(file);
			if (ext.equals(".xml"))
			{
				tests.add(file);
			}
		}
		return tests;
	}	
	
	/**
	*<p>Narrow down a list with a filter object (which has properties like
	* a min and max reading or writing levels, start position, etc).
	*<p>Min and man levels determine what range of words can be returned,
	*<p> and it only looks for words of a certain type, that is 
	* reading or writing.
	*<p>The JDOMSolution object method getFilteredWords is used.  The first
	* argument length can be -all- which filters the entire list.
	*/
	public Vector getFilteredWordObjects(WordFilter word_filter, String user_name)
	{
		File category_file = addXMLExtension(word_filter.getCategory(), user_name);
		JDOMSolution jdom = new JDOMSolution(category_file, this);
		Vector filtered_words = new Vector();
		try
		{
			filtered_words = jdom.getFilteredWords(word_filter);
		} catch (java.lang.NullPointerException npe)
		{
			//log.add("FileStorage.getFilteredWordObjects: possible java.io.UTFDataFormatException: Invalid byte 3 of 3-byte UTF-8 sequence.");
			//log.add(category_file.getPath());
			//append(jdom.getLog());
		}
		return filtered_words;
	}
	
	/**
	* We create a bean containing all the statistics for a users word tests.
	*<p>An array of blank TestStat objects with the appropriate size needs to
	* be created first as set so that each TestStat can be reset with the real
	* thing during a while loop.
	*<p>The average test score is calculated here, but should really be done
	* in stats.  But something has to take the running total of tests and
	* divide it by the total test count, and then set the result in the 
	* AllTestStats object, so then again this isn't such a bad place to do it.
	* Really there needs to be another object that takes tasks like this away
	* from FileStorage so that the future HibernateStorage can use it also.
	*/
	public AllTestStats getTestStats(Vector tests, String user_name)
	{
		Stats stats = new Stats();
		AllTestStats all_test_stats = new AllTestStats();
		int number_of_tests = tests.size();
		int index = 0;
		TestStats [] ts;
		ts = new TestStats [number_of_tests];
		all_test_stats.setTestStats(ts);
		String test;
		while (index<number_of_tests)
		{
			test = (String)tests.get(index);
			Hashtable test_hash = getTests(test, user_name);
			double average = stats.getAverage(test_hash);
			int size = test_hash.size();
			String last_date = stats.getLastTestDate();
			TestStats test_stats = new TestStats();
			test_stats.setName(test);
			test_stats.setTotalTests(size);
			test_stats.setAverageScore(average); 
			test_stats.setLastDate(last_date);
			all_test_stats.setTestStats(index, test_stats);
			index++;
		}
		int total_test_count = stats.getTotalTestCount();
		double running_total_of_tests = stats.getRunningTotalOfTests();
		double average_score = (running_total_of_tests/total_test_count);
		all_test_stats.setNumberOfTests(total_test_count);
		all_test_stats.setAverageScore(average_score);
		return all_test_stats;
	}
	
	/**
	*<p>This method collects all the data from a WordStats array and returns
	* it in a AllWordStats object.  Pretty self-explanatory, no?
	* 
	*/
	public AllWordStats getWordStats(Vector words, String user_id)
	{
		Stats stats = new Stats();
		AllWordStats all_word_stats = new AllWordStats();
		int number_of_word_files = words.size();
		int index = 0;
		WordStats [] ws = new WordStats [number_of_word_files];
		all_word_stats.setWordStats(ws);
		String word_file;
		while (index<number_of_word_files)
		{
			word_file = (String)words.get(index);
			Vector actual_words = getWordObjects(word_file, user_id);
			stats.wordStats(actual_words);
			double actual_reading_average = stats.getAverageReadingLevel();
			double actual_writing_average = stats.getAverageWritingLevel();
			Vector r_counts = stats.getReadingCounts();
			Vector w_counts = stats.getWritingCounts();
			String last_date = stats.getLastWordsTestDate();
			int number_of_words = stats.getNumberOfWords();
			int number_of_retired_words = stats.getRetiredWords();
			WordStats word_stats = new WordStats();
			word_stats.setName(word_file);
			word_stats.setAverageReadingScore(actual_reading_average); 
			word_stats.setAverageWritingScore(actual_writing_average);
			word_stats.setReadingLevels(r_counts);
			word_stats.setWritingLevels(w_counts);
			word_stats.setLastDate(last_date);
			word_stats.setNumberOfWords(number_of_words);
			all_word_stats.setWordStats(index, word_stats);
			index++;
		}
		double torwa = stats.getTotalOfReadingWordAverages();
		double towwa = stats.getTotalOfWritingWordAverages();
		Vector all_reading_counts = stats.getTotalReadingCounts();
		Vector all_writing_counts = stats.getTotalWritingCounts();
		int total_words = stats.getTotalOfAllWords();
		int retired_words = stats.getRetiredWords();
		updateStatusRecordLevel(all_reading_counts, all_writing_counts, total_words, 
				retired_words, user_id);
		RatesOfForgetting rofs = new RatesOfForgetting();
		rofs.setReadingRateOfForgetting(stats.getReadingRateOfForgetting());
		rofs.setWritingRateOfForgetting(stats.getWritingRateOfForgetting());
		all_word_stats.setRatesOfForgetting(rofs);
		all_word_stats.setNumberOfWords(total_words);
		all_word_stats.setReadingAverage(torwa);
		all_word_stats.setWritingAverage(towwa);
		all_word_stats.setReadingLevels(all_reading_counts);
		all_word_stats.setWritingLevels(all_writing_counts);
		all_word_stats.setNumberOfRetiredWords(retired_words);
		return all_word_stats;
	}
	
	private void updateStatusRecordLevel(Vector all_reading_counts, 
			Vector all_writing_counts, int total_words, int retired_words,
			String user_id)
	{ 
		FileTestRecords ftr = new FileTestRecords(root_path);
		ftr.updateAllWordLevels(all_reading_counts, 
				all_writing_counts, total_words, retired_words,
				user_id, Constants.VOCAB);
		ftr.writeDocument();
	}
	
	
	public void addAllStatsHistory(AllStatsHistory all_stats_history, String user_name)
	{
		String path_to_history_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+user_name+".hist");
		File history_file = new File(path_to_history_file);
		JDOMSolution jdom = new JDOMSolution(history_file, this);
		jdom.addHistory(all_stats_history);
		jdom.writeDocument(history_file.getAbsolutePath());
	}
	
	public Vector getAllStatsHistory(String user_name)
	{
		Vector all_stats_history = new Vector();
		String path_to_history_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+user_name+".hist");
		File history_file = new File(path_to_history_file);
		JDOMSolution jdom = new JDOMSolution(history_file, this);
		all_stats_history = jdom.getHistory();
		return all_stats_history;
	}
	
	public long updateHistory(AllTestStats all_test_stats, AllWordStats all_word_stats, 
			String user_id)
	{
		log.add("store.updateHistory");
		Date date = new Date();
		AllStatsHistory all_stats_history = new AllStatsHistory();
		long time = date.getTime();
		all_stats_history.setDate(Long.toString(time));
		all_stats_history.setNumberOfTests(all_test_stats.getNumberOfTests());
		all_stats_history.setAverageScore(all_test_stats.getAverageScore());
		all_stats_history.setNumberOfWords(all_word_stats.getNumberOfWords());
		all_stats_history.setReadingAverage(all_word_stats.getReadingAverage());
		all_stats_history.setWritingAverage(all_word_stats.getWritingAverage());
		all_stats_history.setReadingLevels(all_word_stats.getReadingLevels());
		all_stats_history.setWritingLevels(all_word_stats.getWritingLevels());
		all_stats_history.setNumberOfRetiredWords(all_word_stats.getNumberOfRetiredWords());
		all_stats_history.setSessionStart(all_test_stats.getSessionStart());
		all_stats_history.setSessionEnd(all_test_stats.getSessionEnd());
		all_stats_history.setNumberOfSessionTests(all_test_stats.getNumberOfSessionTests());
		log.add("store.updateHistory: all_word_stats.getNumberOfRetiredWords() = "+all_word_stats.getNumberOfRetiredWords());
		//addAllStatsHistory(all_stats_history, user_id);			     // adds a history record.
		/**
		String path_to_history_file = new String(root_path+File.separator
				+"files"+File.separator+user_id+File.separator+user_id+".hist");
		File history_file = new File(path_to_history_file);
		JDOMSolution jdom = new JDOMSolution(history_file, this);
		jdom.addSessionHistory(all_stats_history);
		jdom.writeDocument(history_file.getAbsolutePath());
		*/
		FileTestRecords ftr = new FileTestRecords(root_path);
		try
		{
			ftr.updateFullWordLevels(all_stats_history,
				user_id, Constants.VOCAB);
		} catch (java.lang.NoClassDefFoundError ncdfe)
		{
			log.add("store.updateHistory: ncdfe");
			append(ftr.getLog());
		}
		//ftr.writeDocument();
		return time;
	}
	
	/**
	 * Save a users history objects and session information. 
	 * @param all_test_stats
	 * @param all_word_stats
	 * @param user_id
	 */
	public void saveSessionHistory(AllTestStats all_test_stats, AllWordStats all_word_stats, 
			String user_id)
	{
		Date date = new Date();
		AllStatsHistory all_stats_history = new AllStatsHistory();
		all_stats_history.setDate(date.toString());
		all_stats_history.setNumberOfTests(all_test_stats.getNumberOfTests());
		all_stats_history.setAverageScore(all_test_stats.getAverageScore());
		all_stats_history.setNumberOfWords(all_word_stats.getNumberOfWords());
		all_stats_history.setReadingAverage(all_word_stats.getReadingAverage());
		all_stats_history.setWritingAverage(all_word_stats.getWritingAverage());
		all_stats_history.setReadingLevels(all_word_stats.getReadingLevels());
		all_stats_history.setWritingLevels(all_word_stats.getWritingLevels());
		// values!
		all_stats_history.setSessionStart(all_test_stats.getSessionStart());
		all_stats_history.setSessionEnd(all_test_stats.getSessionEnd());
		all_stats_history.setNumberOfSessionTests(all_test_stats.getNumberOfSessionTests());
			String path_to_history_file = new String(root_path+File.separator
				+"files"+File.separator+user_id+File.separator+user_id+".hist");
			File history_file = new File(path_to_history_file);
			JDOMSolution jdom = new JDOMSolution(history_file, this);
			jdom.addSessionHistory(all_stats_history);
			jdom.writeDocument(history_file.getAbsolutePath());
	}
	
	/**
	* This method takes all the test and word info in the two object arguments
	* and replaces the old values with them, whether they have all been changed or not.
	* <p>It is assumed that the calling method has confirmed that at least one
	* thing has changed, so while we're at it, we update all values, so that we dont;t
	* have to do each one separately later.
	*/
	public void updateWord(WordTestMemory wtm, WordTestResult wtr, String user_name, String encoding)
	{
		String file_name = wtm.getCategory();
		String new_text = wtr.getText();
		String new_def = wtr.getDefinition();
		String original_text = wtr.getOriginalText();
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File file_chosen = new File(path_to_file);
		JDOMSolution jdom = new JDOMSolution(file_chosen, this);
		jdom.replaceWord(original_text, new_text, new_def);
		jdom.writeDocument(path_to_file, encoding);
	}
	
	/**
	* This method takes all the test and word info in the two object arguments
	* and finds the word writing/reading level and increments or decrements it
	* based on the pass/fail attribute set in the wtr.grade..
	* <p>The string returned represents the new word reading or writing level 
	* that is adjusted in the JDOMSoulution method replaceWordAndChangeTest.
	*/
	public String updateWordLevel(WordTestMemory wtm, WordTestResult wtr, String user_name, String encoding)
	{
		String file_name = wtm.getCategory();
		String new_text = wtr.getText();
		String new_def = wtr.getDefinition();
		String original_text = wtr.getOriginalText();
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File file_chosen = new File(path_to_file);
		JDOMSolution jdom = new JDOMSolution(file_chosen, this);
		Hashtable user_options = getOptions(user_name);
		String max_level = (String)user_options.get("max_level");
		String new_word_level = jdom.changeWordLevel(wtm, wtr, max_level); 
		// jdom.replaceWord(original_text, new_text, new_def);
		//dumpLog(jdom.getLog());
		jdom.writeDocument(path_to_file, encoding);
		//loggit("FileStorage.upadteWordLevel: new_word_level ====== "+new_word_level);  
		return new_word_level;
	}
	
	/**
	* THis method updates the test score for a words test history, as well as 
	* changing the test file history to reflect the changed total percentage score
	* in the test file.  The new score should be recalculated elsewhere.
	*<p>  <score>
	*      <grade>70</grade>
	*      <date>Mon Nov 01 22:26:22 PST 2004</date>
	*     </score>
	*/
	public void updateTest(WordTestMemory wtm, String new_score, String user_name)
	{
		String category = wtm.getCategory();
		String date = wtm.getDate();
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+category);
		File file_chosen = new File(path_to_file);
		JDOMSolution jdom = new JDOMSolution(file_chosen, this); 
		jdom.changeScore(date, new_score);
		jdom.writeDocument(path_to_file);
	}
	
	/**
	*This method only returns the text and definition properties.  The reason is to
	* exclude the test histories, which are not needed here.
	*/
	public Word getWordWithoutTests(String text, String category, String user_name)
	{
		Word word = new Word();
		String file_w_o_ext = Domartin.getFileWithoutExtension(category);
		if (file_w_o_ext.equals("-1"))
		{file_w_o_ext = category;}
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		//String path_to_file = new String(root_path+File.separator 
		//	+"files"+File.separator+user_name+File.separator+category);
		File file = new File(path_to_file);
		JDOMSolution jdom = new JDOMSolution(file);
		word = jdom.findWordWithoutTests(text);
		return word;
	}
	
	/**
	*Here we just add a word to a file.
	*<p>The FileJDOMWordLists takes the user folder as a constructor,
	and adds reading.list and writing.list to add the words id and filename
	* to each of these files inside the users vocab/lists/ folder.
	*<p>We also have to add a words last and next test date record (wltd/wntd), 
	* with writing being one rate of testing delta (rotd) value higher than reading.
	*/
	public void addWord(Word word, String file_name, String user_name, String encoding)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String user_folder = root_path+File.separator+"files"+File.separator+user_name;
		String path_to_words = new String(user_folder+File.separator+file_w_o_ext+".xml");
		//loggit("FileStorage.getWordObjects: path to words "+path_to_words);
		File words_file = new File(path_to_words);
		String test_path = words_file.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(words_file, this);
		//loggit("created jdoms");
		jdom.addWord(word);
		//loggit("added word ");
		jdom.writeDocument(path_to_words, encoding);
		//loggit("wrote doc");
		jdom = null;
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
		fjdomwl.addToNewWordsList(word, path_to_words, Constants.READING, encoding);
		fjdomwl.addToNewWordsList(word, path_to_words, Constants.WRITING, encoding);
		//loggit("added word to new word lists");
		//append(jdom.getLog());	this gave us out of memory and null pointer exceptions.  go figure...
	}
	
	/**
	*Here we update a word to in a file.
	*/
	public void editWord(Word old_word, Word new_word, String file_name, String user_name, String encoding)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_words = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		//loggit("FileStorage.editWord: path to words "+path_to_words);
		File words_file = new File(path_to_words);
		String test_path = words_file.getAbsolutePath();
		FileEdit edit = new FileEdit(path_to_words);
	        edit.editWord(old_word, new_word);
	        edit.writeDocument(path_to_words, encoding);
	}
	
	public void deleteWord(Word word, String category, String user_id, String encoding)
	{
		String path_to_words = getPathToFile(category, user_id);
		File words_file = new File(path_to_words);
		String test_path = words_file.getAbsolutePath();
		log.add("FileStorage.deleteWord: path "+path_to_words);
		FileEdit edit = new FileEdit(path_to_words);
		try
		{
			edit.deleteWord(word, encoding, root_path, user_id, category); 
			edit.writeDocument(path_to_words, encoding);
			log.add("FileStorage.deleteWord: edit log ---");
			append(edit.getLog());
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileStorage.deleteWord: npe!.  Here's the edit log ---");
			append(edit.getLog());
		}
	}
	
	/**
	*Get exclusive categories.
	*Load the daily test marks (i = category in list, j = word in the category).
	*Create a list of words within the WLTD object.
	*Set the new daily test marks.
	*return the key list to access the words.
	 described as a hash of Word objects using a test date as a key in the WLTD docs.
	* @ param user_name is now the users id.
	*/
	public ArrayList getWordLastTestDatesList(WordLastTestDates wltd, String user_name, String subject)
	{
		Vector all_word_categories = getWordCategories("exclusive", user_name);
		FileTestRecords ftr = new FileTestRecords(root_path);
		String d_t_i = ftr.getDailyTestMark(user_name, subject);
		int i = Domartin.getDailyTestIndexMark(d_t_i, "i");
		int j = Domartin.getDailyTestIndexMark(d_t_i, "j");
		wltd.setLimitI(i);
		wltd.setLimitJ(j);
		int expected_words = wltd.getList(all_word_categories, this, user_name);
		//append(wltd.getLog());
		i = wltd.getLimitI();
		j = wltd.getLimitJ();
		ftr.setDailyTestMark(user_name, subject, i, j);
		ArrayList actual_key_list = wltd.getSortedWLTDKeys();
		//append(wltd.getLog());
		return actual_key_list;
	}
	
	/** This method gets a list of words with recently tested word (according to an
	* exclude level test vector which indicates the period of time that tested
	* words should be excluded).
	*/
	public ArrayList getAdjustedWordLastTestDatesList(WordLastTestDates wltd, String user_name)
	{
		Vector all_word_categories = getWordCategories("exclusive", user_name);
		int expected_words = wltd.getAdjustedList(all_word_categories, this, user_name);
		ArrayList actual_key_list = wltd.getSortedWLTDKeys();
		return actual_key_list; 
	}
	
	private String getPathToFile(String file_name, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		return path_to_file;
	}
	
	public String getContentType()
	{
		return content_type;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public String getUserFolder()
	{
		return user_folder;
	}
	
	public Vector getUserLists()
	{
		return files;
	}
	
	public void setTests(Vector _tests)
	{
		tests = _tests;
	}
	
	public Vector getTests()
	{
		return tests;
	}
	
	public void setWords(Vector _words)
	{
		words = _words;
	}
	
	public Vector getWords()
	{
		return words;
	}
	
	public String getRole()
	{
		return user_role;
	}
	
	public Vector getOther()
	{
		return other;
	}
	
	public void setOther(Vector _other)
	{
		other = _other;
	}
	
	public String getPathToFiles()
	{
		return path_to_files;
	}
	
	public void copyFile(File in, File out)
	{
		try
		{
			FileInputStream fis  = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i=fis.read(buf))!=-1) 
			{
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
		} catch (java.io.FileNotFoundException fnfe)
		{
			
		} catch (java.io.IOException ioe)
		{
			
		}
	}
	
	/**
	*This helper method just adds the .xml extension if necessary,
	* because sometimes we get a filename in w/o it, but usually we pass
	* in the full filename with the extension.
	*/
	private File addXMLExtension(String file_name, String user_name)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_words = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		File file = new File(path_to_words);
		return file;
	}
	
	// new methods, supposingly using a more mature architecture.
	// Action methods create a Storage object and send it the users name
	// so that it can access the users data.
	// The storage object then using the data to access the logic methods
	// contained in other object.
	/**
	AllWordsTest // data kept on xml file
	private String text;
	private String definition;
	private String category;
	private String test_type;
	*<p>First we set the search criteria.  If it is a reading test, that means the user has seen the
	* word in a foreign language (the text element), and they have typed in the meaning in their own
	* language (the definition element), so we look for the original text element with the value from 
	* the AllWordsTest object.
	*<p>Then we have to match the searched for text/definition, for a reading test, we want to mathc
	* the returned definition with the field0 property of the test_form, which the user has typed in
	* to score the test as pass or fail.  
	*/
	public WordTestResult scoreSingleWordTest(AllWordsTest awt, String user_name)
	{
		if (awt==null)
		{
			return null;
		}
		String search_property = new String();
		String search_value = new String();
		if (awt.getTestType().equals("reading"))
		{
			search_property = new String("text");
			search_value = awt.getText();
		} else if (awt.getTestType().equals("writing"))
		{
			search_property = new String("definition");
			search_value = awt.getDefinition();
		}
		String category = awt.getCategory();
		WordTestResult wtr = new WordTestResult();
		try
		{
			//log.add("got to try");
			Word word = getWordObject(search_property, search_value, category, user_name);
			//log.add(word.getText()+""+word.getDefinition());
			Scoring scorer = new Scoring();
			//log.add("created scorer");
			Hashtable user_options = getOptions(user_name);
			//log.add("back from getOptions");
			wtr = scorer.scoreSingleWord(awt, word, user_options);	// no more coomets after here...Scorer tests all pass
			log.add("Scorer log -----");
			dumpLog(scorer.getLog());
			//log.add("WordTestResult.scoreSingleWordTest.scorer.scoreSingleWord: Scoring log -=-=-=-=-=-= ");
			//append(scorer.getLog());
		} catch (java.lang.NullPointerException npe)
		{}
		return wtr;
	}
	
	/**
	*We have to take a test result and update file for the words level and the test files record
	*words_jdom.recordWordScore(question, "pass", test_name, date, test_type, max_level);
	WordTestResult
	private String text;
	private String definition;
	private String answer;
	private String grade;
	private String level;
	private int id;
	private String original_text;
	private String original_definition;
	private String original_level;
	
	AllWordsTest // data kept on xml file
	private String text;
	private String definition;
	private String category;
	private String test_type;
	*/
	public String recordWordTestScore(WordTestResult wtr, AllWordsTest awt, String max_level, String test_name, String user_name)
	{
		String question = new String();String test_type = awt.getTestType();
		if (test_type.equals("reading"))
		{
			question = wtr.getText();
		} else if (test_type.equals("writing"))
		{
			question = wtr.getDefinition();
		}
		String grade = wtr.getGrade();String date = new Date().toString();
		String category = awt.getCategory();String org_level = wtr.getOriginalLevel();
		String path_to_words = getPathToFile(category, user_name);String encoding = wtr.getEncoding();
		File words_file = new File(path_to_words);String file = words_file.getAbsolutePath();
		printWordTestScore(grade, date, category,
				 org_level, path_to_words, encoding, file,
				 question,  test_type); // for loggin only so far
		JDOMSolution jdom = new JDOMSolution(words_file, this);
		String new_level = jdom.recordWordScore(question, grade, test_name, date, test_type, max_level, org_level);
		jdom.writeDocument(path_to_words, encoding);
		log.add("FileStorage.recordWordTestScore: jdom log ~~~~~~~~~~~ start");
		append(jdom.getLog());
		log.add("FileStorage.recordWordTestScore: jdom log ~~~~~~~~~~~ end");
		return new_level;
	}
	
	private void printWordTestScore(String grade,String date,String category,
			String org_level,String path_to_words,String encoding,String file,
			String question, String test_type)
	{
		log.add("FileStorage.recordWordTestScore: question = "+question);
		log.add("FileStorage.recordWordTestScore: test_type = "+test_type);
		log.add("FileStorage.recordWordTestScore: grade = "+grade);
		log.add("FileStorage.recordWordTestScore: date = "+ date);
		log.add("FileStorage.recordWordTestScore: category = "+category);
		log.add("FileStorage.recordWordTestScore: org_level = "+org_level);
		log.add("FileStorage.recordWordTestScore: path_to_words = "+path_to_words);
		log.add("FileStorage.recordWordTestScore: encodingt = "+encoding);
		log.add("FileStorage.recordWordTestScore: file = "+file);
	}
	
	/**
	*We have to take a test result and update file for the words level and the test files record
	*words_jdom.recordWordScoreSearchById(id, question, "pass", test_name, date, test_type, max_level);
	*/
	public String recordWordTestScoreUsingId(WordTestResult wtr, AllWordsTest awt, String max_level, String test_name, String user_name)
	{
		String question = new String();
		String test_type = awt.getTestType();
		if (test_type.equals("reading"))
		{
			question = wtr.getText();
		} else if (test_type.equals("writing"))
		{
			question = wtr.getDefinition();
		}
		String grade = wtr.getGrade();
		String date = new Date().toString();
		String category = awt.getCategory();
		String org_level = wtr.getOriginalLevel();
		String path_to_words = getPathToFile(category, user_name);
		String encoding = wtr.getEncoding();
		long id = awt.getId();
		File words_file = new File(path_to_words);
		String file = words_file.getAbsolutePath();
		log.add("FileStorage.recordWordTestScore: question = "+question);
		log.add("FileStorage.recordWordTestScore: test_type = "+test_type);
		log.add("FileStorage.recordWordTestScore: grade = "+grade);
		log.add("FileStorage.recordWordTestScore: date = "+ date);
		log.add("FileStorage.recordWordTestScore: category = "+category);
		log.add("FileStorage.recordWordTestScore: org_level = "+org_level);
		log.add("FileStorage.recordWordTestScore: path_to_words = "+path_to_words);
		log.add("FileStorage.recordWordTestScore: encodingt = "+encoding);
		log.add("FileStorage.recordWordTestScore: file = "+file);
		JDOMSolution jdom = new JDOMSolution(words_file, this);
		String new_level = jdom.recordWordScoreSearchById(id, question, grade, test_name, date, test_type, 
				max_level, org_level);
		jdom.writeDocument(path_to_words, encoding);
		log.add("FileStorage.recordWordTestScore: jdom log ~~~~~~~~~~~ start");
		append(jdom.getLog());
		log.add("FileStorage.recordWordTestScore: jdom log ~~~~~~~~~~~ end");
		return new_level;
	}
	
	/**
	*/
	public Hashtable getUserOptions(String user_name, String context_path)
	{
		// from LoginAction.loginUser method
		String user_folder = (context_path+File.separator+"files"+File.separator+user_name);
		String file_name = new String(user_name+".options");
		File file_chosen = new File(user_folder+File.separator+file_name);
		log.add("FileStorage.getUserOptions: file_chosen (exists? "+file_chosen.exists()+") = "+file_chosen.getAbsolutePath());
		String file_path = file_chosen.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(file_chosen);
		Hashtable options = null;
		try
		{
			options = jdom.getOptionsHash();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileStorage.getUserOptions: NPE:            ++++++++++++");
			log.add("FileStorage.getUserOptions: what jdom says: ++++++++++++");
			append(jdom.getLog());
			log.add("FileStorage.getUserOptions: what jdom says: ++++++++++++");
		}
		return options;
	}
	
	/**
	 * Teacher options file in: /files/admin/teachers/teacher_id/teacher_id.options
	*/
	public Hashtable getTeacherOptions(String teacher_id, String context_path)
	{
		// from LoginAction.loginUser method
		String file_path = (context_path+File.separator+"files"
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id
				+File.separator+teacher_id+".options");
		String file_name = new String(file_path);
		File file_chosen = new File(file_name);
		log.add("FileStorage.getUserOptions: file_chosen (exists? "+file_chosen.exists()+") = "+file_chosen.getAbsolutePath());
		JDOMSolution jdom = new JDOMSolution(file_chosen);
		Hashtable options = jdom.getOptionsHash();
		log.add("FileStorage.getUserOptions: what jdom says: ++++++++++++");
		append(jdom.getLog());
		log.add("FileStorage.getUserOptions: what jdom says: ++++++++++++");
		return options;
	}
	
	public Vector getNewWordsList(String type, String user_name)
	{
		String user_folder = root_path+File.separator+"files"+File.separator+user_name;
		Vector new_words = new Vector();
		try
		{
			FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
			new_words = fjdomwl.getNewWordsList(type);
			dumpLog(fjdomwl.getLog());
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileStorage.getNewWordsList ----------- npe");
			log.add(user_folder);
		}
		return new_words;
	}
	
	public Vector getNewWordsList(String type, String user_name, String subject)
	{
		String user_folder = root_path+File.separator+"files"+File.separator+user_name;
		Vector new_words = new Vector();
		try
		{
			FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder, type, subject);
			new_words = fjdomwl.getNewWordsList(type);
			dumpLog(fjdomwl.getLog());
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileStorage.getNewWordsList ----------- npe");
			log.add(user_folder);
		}
		return new_words;
	}
	
	public Vector getNoRepeatsNewWordsList(String type, Vector previous_list, String user_name)
	{
		String user_folder = root_path+File.separator+"files"+File.separator+user_name;
		Vector new_words = new Vector();
		try
		{
			FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
			new_words = fjdomwl.getNewWordsList(type);
		} catch (java.lang.NullPointerException npe)
		{
			//log.add("FileStorage.getNoRepeatsNewWordsList -----------");
			//log.add(user_folder);
		}
		return new_words;
	}
	
	/**
	*Setup to retire the word in FileJDOMWordLists.retireWord(), and add word to user_folder/vocab/lists/retired words.list.
	*/
	public void retireWord(Word word, String category, String user_id, String encoding)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(category);
		if (file_w_o_ext.equals("-1")) {file_w_o_ext = category;}
		String user_folder = root_path+File.separator+"files"+File.separator+user_id;
		String path_to_words = new String(user_folder+File.separator+file_w_o_ext+".xml");
		String file_path = new String(user_folder+File.separator+category);
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists(file_path);
		fjdomwl.retireWord(Long.toString(word.getId()), encoding);
		String lists_folder_path = new String(user_folder+File.separator+"vocab"+File.separator+"lists");
		String retired_words_file_path = new String(lists_folder_path+File.separator+"retired words.list");
		fjdomwl = new FileJDOMWordLists(retired_words_file_path);
		fjdomwl.addToRetiredWordsList(word, category, encoding);
		FileTestRecords ftr = new FileTestRecords(root_path);
		ftr.incrementRetiredWords(user_id, Constants.VOCAB);
		//dumpLog(fjdomwl.getLog());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.catechis.Storage#saveWTDKeys(java.lang.String, java.lang.String, long)
	 */
	public void saveWTDKeys(String wntd_file_key_r, String wntd_file_key_w, long word_id)
	{
		this_jdom.addWTDKeys(wntd_file_key_r, wntd_file_key_w, word_id);
	}
	
	public void writeJDOMDocument(String file_name)
	{
		this_jdom.writeDocument(file_name, encoding);
	}
	
	/**CLEANING UTILITY*/
	public int cleanTests(Vector words, String file_name)
	{
		String path_to_cleaning_file = new String(root_path+File.separator
			+"files"+File.separator+"cleaner"+File.separator+file_name);
		String path_to_clean_file = new String(root_path+File.separator
			+"files"+File.separator+"cleaner"+File.separator+"new_"+file_name);
		log.add(path_to_cleaning_file);
		File clean_file = new File(path_to_cleaning_file);
		JDOMSolution jdom = new JDOMSolution(clean_file, this);
		int tests_cleaned = jdom.cleanAllTests();
		int words_cleaned = jdom.cleanLevels();
		jdom.writeDocument(path_to_clean_file);
		return tests_cleaned;
	}
	
	/**CLEANING UTILITY*/
	public int cleanAndBAckupTests(Vector words, String file_name)
	{
		String path_to_cleaning_file = new String(root_path+File.separator
			+"files"+File.separator+"cleaner"+File.separator+file_name);
		String path_to_clean_file = new String(root_path+File.separator
			+"files"+File.separator+"cleaner"+File.separator+"new_"+file_name);
		File clean_file = new File(path_to_cleaning_file);
		JDOMSolution jdom = new JDOMSolution(clean_file, this);
		String user_id = "blank";
		String category_id = "blank";
		int tests_cleaned = jdom.cleanAndBackupAllTests(user_id, category_id);
		jdom.writeDocument(path_to_clean_file);
		return tests_cleaned;
	}
	
	/**utility to ad ids to old words*/
	public int addIds(String _file_name)
	{
		String file_name = new String(root_path+File.separator
			+"files"+File.separator+"cleaner"+File.separator+_file_name);
		File file = new File(file_name);
		JDOMSolution jdom = new JDOMSolution(file, this);
		//log.add("testAddIds:file name : "+file_name+" adding");
		int added = 0;
		try
		{
			    added = jdom.addIds();
		} catch (java.lang.NullPointerException npe)
		{
			    log.add("npe file name "+file_name);
		}
		if (added>0)
		{
			    jdom.writeDocument(file_name);
		}
		//append(jdom.getLog());
		//log.add(file_name+" "+added);
		return added;
	}
	
	/**utility to ad ids to old words*/
	public int addIds(String _file_name, String user_name)
	{
		String file_name = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+_file_name);
		File file = new File(file_name);
		JDOMSolution jdom = new JDOMSolution(file, this);
		//log.add("testAddIds:file name : "+file_name+" adding");
		int added = 0;
		try
		{
			    added = jdom.addIds();
		} catch (java.lang.NullPointerException npe)
		{
			    log.add("npe file name "+file_name);
		}
		if (added>0)
		{
			    jdom.writeDocument(file_name);
		}
		//append(jdom.getLog());
		//log.add(file_name+" "+added);
		return added;
	}
	
	/** deguggin */
	
	private void dumpLog(Vector log)
	{
	    int i = 0;
	    while (i<log.size())
	    {
		    context.log("FileStorage.log "+i+" "+log.get(i));
		    i++;
	    }
	}
	
	public Vector getLog()
	{
		return log;
	}
	
	public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add(v.get(i));
			i++;
		}
	}
	
	public void appendWords(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			Word word = (Word)v.get(i);
			log.add("fs.word: "+word.getDefinition()+" id "+word.getId());
			i++;
		}
	}
	
	public void resetLog()
	{
		log = new Vector();
	}

}
