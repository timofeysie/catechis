package org.catechis.filter;

import org.catechis.Filter;
import org.catechis.Storage;
import org.catechis.file.FileTestRecords;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.WordTestRecordOptions;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

public class WordListFilter
{

	private Vector log;
	
	public WordListFilter()
	{
		log = new Vector();
	}

	/**
	*<p>This class seemed like a good place to Get a list of words with 
	*levels of a certain type at level 0.
	*<p>These methods were originally in the WeeklyWordListAction.
	*<p>It takes a bit of work to get a list of words at level zero and words
	* that failed a test above level zero that contains no repeats.
	*<p>Yes we could have made the level zero lists as hastables and done this
	* possibly in one pass in one method, but we are leaving open the option of
	* that in the future while currently using the lists in their natural habitats.
	*<p>This is after all just a demonstration application of how foreign language vocabulary
	* can be learned with a thorough implementation of the idea of intermittent reinforcement.
	*<p>See the notes for getNoRepeatsMissedWords1 to see how to make this list.
	*/
	public Vector getLevelZeroWords(String user_name, String test_type, Storage store)
	{
		String min_max = new String("0-0");
		String category_name = new String("all");
		WordFilter word_filter = new WordFilter();
		Vector all_words =  new Vector();
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		word_filter.setStartIndex(0);
		word_filter.setMinMaxRange(min_max);
		word_filter.setType(test_type);
		word_filter.setCategory(category_name);
		for (int i = 0; i < all_word_categories.size(); i++) 
		{
			String category = (String)all_word_categories.get(i);
			word_filter.setCategory(category);  // The filter needs a new name.
			Vector category_words = store.getFilteredWordObjects(word_filter, user_name);
			all_words.addAll(category_words);
		}
		return all_words;
	}
	
	/**
	*<p>Same as getLevelZeroWords, but this method filters out any words already in a previous verctor.
	*/
	public Vector getFilteredLevelZeroWords(String user_name, String test_type, Vector previous_words, Storage store)
	{
		log.add("WordListFilter.getFilteredLevelZeroWords: ");
		Hashtable previous_words_hash = turnVectorIntoHash(previous_words);
		String min_max = new String("0-0");
		String category_name = new String("all");
		WordFilter word_filter = new WordFilter();
		Vector all_words =  new Vector();
		Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		word_filter.setStartIndex(0);
		word_filter.setMinMaxRange(min_max);
		word_filter.setType(test_type);
		word_filter.setCategory(category_name);
		for (int i = 0; i < all_word_categories.size(); i++) 
		{
			String category = (String)all_word_categories.get(i);
			word_filter.setCategory(category);  // The filter needs a new name.
			Vector category_words = store.getFilteredWordObjects(word_filter, user_name);
			for (int j = 0; j < category_words.size(); j++)
			{
				Word this_word = (Word)category_words.get(j);
				String text = this_word.getText();
				String def = this_word.getDefinition();
				if (previous_words_hash.containsKey(text)||previous_words_hash.contains(def))
				{
					// dont add word
					log.add(i+" "+j+" didnt add "+this_word.getDefinition());
				} else
				{
					all_words.add(this_word);
					log.add(i+" "+j+" added "+this_word.getDefinition());
				}
			}
			//all_words.addAll(category_words);
		}
		return all_words;
	}
	
	private Hashtable turnVectorIntoHash(Vector previous_words)
	{
		Hashtable results = new Hashtable();
		int size = results.size();
		int i = 0;
		while (i<size)
		{
			Word word = (Word)previous_words.get(i);
			String text = word.getText();
			String def = word.getDefinition();
			results.put(text, def);
			i++;
		}
		return results;
	}
	
	/**
	*<p>Get words that have been tested and found wanting.
	*<p>This is controlled by settings in the users .option file in their folder.
	*<p>Typically settings would be for only words at a level above 0 that 
	* have been failed recently.
	*/
	public Hashtable getMissedWords(String type, String root_path, String user_name, Hashtable user_opts)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_name);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    FileTestRecords ftr = new FileTestRecords();
	    Hashtable words_defs = new Hashtable();
	    try
	    {
		    words_defs = ftr.getSpecificDailyTestRecords(wtro);
	    } catch (java.lang.NullPointerException npe)
	    {}
	    return words_defs;
	}
	
	/**
	*<p>Second verse, same as the first, just a little be slimmer and a little bit worse.
	*/
	public Hashtable getFilteredMissedWords(String type, String root_path, String user_name, Hashtable user_opts, Hashtable previous_missed_words)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_name);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    FileTestRecords ftr = new FileTestRecords();
	    Hashtable words_defs = ftr.getSpecificDailyTestRecords(wtro);
	    //for (int i = 0; i < words_defs.size(); i++)
	    for (Enumeration e = words_defs.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)words_defs.get(key);
		    if (previous_missed_words.containsKey(key))
		    {
			    words_defs.remove(key);
		    }
	    }
	    return words_defs;
	}
	
	/**
	*<p>Third verse, same as the first, just a little be slimmer and a little bit worse.
	*<p>No, seriously, to use this method, you have to first construct a Vector of
	* concatenated level 0 words to exclude missed words that are already in those lists.
	*<p>This is done by first using the getLevelZeroWords method, usually to retrieve
	* a list of words that have a level zero reading level.
	*<p>Then we pass that list to the getFilteredLevelZeroWords with type writing 
	* to return a list of words at writing level zero that excludes words already in the first list.
	*<p>Then we get to this method, where the first time thru we pass it type = reading, and
	* the previous Vectors concatenated (done externally) list or reading and writing words at level 0,
	* and blank Hastable of previous_missed_words because we don't have any yet.
	*<p>Then we pass this method type=writing and the Hastable or missed words from the last time thru
	* to create a Hashtable that excludes words at level zero, and missed reading words.
	*<p>It therefore takes a bit of work to get a list of words at level zero and words
	* that failed a test above level zero that contains no repeats.
	*<p>Yes we could have made the level zero lists as hastables and done this
	* possibly in one pass in one method, but we are leaving open the option of
	* that in the future while currently using the lists in their natural habitats.
	*<p>This is after all just a demonstration application of how foreign language vocabulary
	* can be learned with a thorough implementation of the idea of intermittent reinforcement.
	*/
	public Hashtable getNoRepeatsMissedWords1(String type, String root_path, String user_name, 
		Hashtable user_opts, Hashtable previous_missed_words, Vector previous_words)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_name);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    FileTestRecords ftr = new FileTestRecords();
	    Hashtable words_defs = ftr.getSpecificDailyTestRecords(wtro);
	    for (Enumeration e = words_defs.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)words_defs.get(key);
		    if (previous_missed_words.containsKey(key))
		    {
			    words_defs.remove(key);
		    }
	    }
	    for (int j = 0; j < previous_words.size(); j++)
	    {
		    Word this_word = (Word)previous_words.get(j);
		    String text = this_word.getText();
		    String def = this_word.getDefinition();
		    if (words_defs.contains(text))
		    {
			    words_defs.remove(text);
		    }
	    }	
	    return words_defs;
	}
	
	/** deguggin */
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

}
