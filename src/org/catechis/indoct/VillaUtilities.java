package org.catechis.indoct;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.catechis.admin.FileUserOptions;
import org.catechis.constants.Constants;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.Momento;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordLastTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileSaveTests;
import org.catechis.file.FileTestRecords;
import org.catechis.file.WordNextTestDates;
import org.catechis.filter.WordListFilter;
import org.catechis.gwangali.RateOfTesting;
import org.catechis.juksong.TestTimeMemory;
import org.catechis.testing.TestUtility;

public class VillaUtilities
{
	
	private Vector log;
	
	public VillaUtilities()
	{
		log = new Vector();
	}
	
	/**
	 * This method goes through all the words marked as 'pass' in the passed_words_param argument
	 * loads the words in the test_words Hashtable, and updates the word levels, adds a test record 
	 * in the word file, and other things such as removing the word from the new words list,
	 * and updating the word next test date and word last test date lists.
	 * @param passed_words_params - usually comes from request.getParameterNames();
	 * @param test_words with the id as the key and a Word object as the value
	 * @param user_id
	 * @param subject like vocal or grammar, etc.
	 * @param context_path
	 * @return the same Hashtable as test_words, but  updating the test[0] Test object which now has:
	 * type = testing type, as each individual word in a saved test can have a different type.
	 * date = WordNExtTestDate file key, a link to the file that the test was chosen from and needs to be updated after the test.
	 * grade = pass or fail after the test has been scored.
	 * level = new level after the test has been scored.  depending on the type above, could be reading_level or writing_level.
	 * The type and date were set previously when the saved test was loaded in FileSaveTests.
	 */
	public Hashtable updateScoreAndWords(Map passed_words_params, Hashtable test_words, 
			String user_id, String category, String context_path)
	{
		Hashtable new_test_words = new Hashtable(); // to hold the test words after they have been scored, with the score info in the test[0] object.
		Enumeration keys = test_words.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    Word this_word = (Word)test_words.get(key);
		    long this_word_id = this_word.getId();
		    String this_word_obj = this_word_id+"";
		    log.add("Checking "+this_word_obj+" against map");
		    if (actuallyContains(passed_words_params, this_word_id+""))
		    {
		    	log.add("VillaUtilities.updateScoreAndWords: passed word_id_param "+this_word_id+" word ##########");
		    	append(Transformer.createTable(this_word));
		    	log.add("VillaUtilities.updateScoreAndWords: end word #############################################");
		    	String new_level = "0";
		    	new_level = updateWord(this_word, Constants.PASS, user_id, context_path); 
		    	this_word = FileSaveTests.setTestResult(this_word, new_level, Constants.PASS);
		    	new_test_words.put(this_word_id, this_word);
		    	log.add("VillaUtilities.updateScoreAndWords: passed "+this_word.getText()+" new_level "+new_level);
        } else
        {
        		String testing_type = FileSaveTests.getTestTypeFromSavedWord(this_word);
        		//String grade = FileSaveTests.getGradeFromTest(this_word);
        		String wntd_file_key = FileSaveTests.getWNTDFileKeyFromSavedWord(this_word);
        		log.add("VillaUtilities.updateScoreAndWords: this_word -------))) type -"+testing_type+" grade - of course fail,  wntd key -"+wntd_file_key);
        		append(Transformer.createTable(this_word));
        		this_word = FileSaveTests.setTestInfoFromSavedWord(this_word, testing_type, wntd_file_key);
        		String new_level = "0";
        		try
        		{
        			new_level = updateWord(this_word, Constants.FAIL, user_id, context_path); 
        			this_word = FileSaveTests.setTestResult(this_word, new_level, Constants.FAIL);
        			log.add("VillaUtilities.updateScoreAndWords: fst log %%%%%%%%%%%%%");
        			new_test_words.put(this_word_id, this_word);
        			log.add("VillaUtilities.updateScoreAndWords: failed "+this_word.getText()+" new_level "+new_level);
        		} catch (java.lang.NullPointerException npe)
        		{
        			log.add("VillaUtilities.updateScoreAndWords: word_id "+this_word_id+" caused a npe in fail round");
        			npe.printStackTrace();
        			log.add("VillaUtilities.updateScoreAndWords: vu log |||||||| ");
        			append(getLog());
        		}
        	}
        }
        return new_test_words;
	}
	
	private Vector mapToVector(Map map)
	{
		Vector vector = new Vector();
		Iterator it = map.entrySet().iterator();
		 while (it.hasNext())
		 {
			 Map.Entry pairs = (Map.Entry)it.next();
			 vector.add(pairs.getKey());
			 log.add("pairs.getKey() "+pairs.getKey());
		 }
		return vector;
	}
	
	private boolean actuallyContains(Map map, String look_for)
	{
		Iterator it = map.entrySet().iterator();
		 while (it.hasNext())
		 {
			 Map.Entry pairs = (Map.Entry)it.next();
			 String this_time = (String)pairs.getKey();
			 if (this_time.equals(look_for))
			 {
				 log.add("YES!");
				 log.add("this_time "+this_time);
				 log.add("look_for  "+look_for);
				 return true;
			 }
			 log.add("NO!");
			 log.add("this_time "+this_time);
			 log.add("look_for  "+look_for);
		 }
		return false;
	}

	/**
	 * Score a word passed in, updating the words level, adding a test element,
	 * adding the word to the record file if such properties are set, and
	 * removing the word from the new words file if there.
	 * This method was based on IntegratedTestResultAction.
	 * 

need to send: 

awt which has:
test_type = awt.getTestType();
awt.getLevel
answer (and check where awt.getAnswer is used.  Maybe we should set this to SavedIntegratedTest or some Constants)
grand_index

And return what to go into the session?

	 * @param this_word
	 *  @param grade either pass or fail.
	 *   @param user_id
	 *    @param context_path
	 *    @return the new level (be it reading or writing) as the value.
	 * 
	 * Things to return
	 * 
	 * reading_level
	 * writing_level * only need on sine
	 * testing_type already known by the caller
	 * 
	 * How about a Hashtable, with the words test index as the key, and the new level
	 * (be it reading or writing) as the value
	 * 
	 */
	public String updateWord(Word this_word, String grade, String user_id, 
			String context_path)
	{
		FileStorage store = new FileStorage(context_path);	
		Hashtable user_opts = store.getUserOptions(user_id, context_path);
		String subject = (String)user_opts.get("subject");
		String max_level = (String)user_opts.get("max_level");
		String encoding = (String)user_opts.get("encoding");
		FileTestRecords ftr = new FileTestRecords(context_path);
		
		log.add("ve.updatedWord: this_word -------");
		append(Transformer.createTable(this_word));
		log.add("ve.updatedWord: this_word -------");
		
		String test_type = FileSaveTests.getTestTypeFromSavedWord(this_word);
		String wntd_file_key = FileSaveTests.getWNTDFileKeyFromSavedWord(this_word);
		String answer = getSavedTestAnswer(test_type, this_word);
		
		Word archetype_word = store.getWordObject("id", this_word.getId()+"", this_word.getCategory(), user_id);
		AllWordsTest awt = setupAWTWithMerge(test_type, answer, this_word, archetype_word);
		log.add("awt ----------------------------");
		append(Transformer.createTable(awt));
		log.add("arch_word ----------------------");
		append(Transformer.createTable(archetype_word));
		String test_name = new String("level "+awt.getLevel()+" "+test_type+".test");
		// update the last/next test date lists
		FileUserOptions fuo = new FileUserOptions();
		Vector elt_vector = fuo.getELTVector(user_opts);		
		// exclude-level-times eliminate recently tested words from the list
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(test_type);
		wltd.setLimitOfWords(100); // probably don't need this for integrated tests.
		// get first word to test
		String [] files = wltd.getWNTFiles(user_id, context_path);
		String file = files[0];
		String file_w_ext = file;
		String file_wo_ext = Domartin.getFileWithoutExtension(file_w_ext);
		String file_date = Transformer.getLongDateFromMilliseconds(file_wo_ext);
		String word_level = getWordLevel(test_type, this_word);
		
		System.err.println("test_type "+test_type);
		System.err.println("reading_level "+this_word.getReadingLevel());
		System.err.println("writing_level "+this_word.getReadingLevel());
		System.err.println("word_level "+word_level);
		
		// no need to do this as we already know if the word passed or failed
		//WordTestResult wtr = store.scoreSingleWordTest(awt, user_id);
		WordTestResult wtr = new WordTestResult();
		//log.add("wtr - before ------- word_level "+word_level);
		//append(Transformer.createTable(wtr));
		//log.add("wtr - --------------");
		wtr.setAnswer(answer);
		wtr.setEncoding(encoding);
		wtr.setGrade(grade);
		wtr.setLevel(word_level);
		wtr.setOriginalLevel(word_level);
		wtr.setWordId(this_word.getId());
		wtr.setText(this_word.getText());
		wtr.setOriginalText(this_word.getText());
		wtr.setDefinition(this_word.getDefinition());
		wtr.setOriginalDefinition(this_word.getDefinition());
		String new_level = store.recordWordTestScoreUsingId(wtr, awt, max_level, test_name, user_id); // this updates the words level
		log.add("VillaUtilities.updateWord: log after store.recordWordTestScore -------------- start");
		append(store.getLog());
		log.add("VillaUtilities.updateWord: log after store.recordWordTestScore -------------- end");
		wtr.setLevel(new_level);
		Date date = new Date();
		long time = date.getTime();
		String str_date = date.toString();
		wtr.setDate(str_date);
		// next, add a record in the daily *type* tests.record file. 
		// this also removes the word from the new word list if it is a pass test, 
		// and of course if the word in on the new <type> words.list
		TestUtility tu = new TestUtility();
		tu.addDailyTestRecordIfNeeded(test_type, wtr, user_id, context_path, user_opts, awt.getId(), encoding);		
		ftr.updateTestsStatus(user_id, subject);
		//WordTestResult wtr = new WordTestResult();
		//wtr = new WordTestResult();
		wtr.setWntdName(wntd_file_key+".xml");			
		// was file[0] in test, now action_id... seems messy to add the xml ext here...
		wtr.setWordId(awt.getId());
		//log.add("wtr - AFTER -------");
		//append(Transformer.createTable(wtr));
		//log.add("wtr - --------------");
		TestTimeMemory ttm = new TestTimeMemory();
		try
		{
			ttm = wltd.updateTestDateRecordsAndReturnMemory(user_id, context_path, wtr);		
		// this will also save the list files for a possible undo
		String org_wltd = Transformer.getDateFromMilliseconds(ttm.getOriginalWLTD());
		String org_wntd = Transformer.getDateFromMilliseconds(Domartin.getFileWithoutExtension(ttm.getOriginalWNTD()));
		String new_wntd = Transformer.getDateFromMilliseconds(ttm.getNewWNTD());
		files = wltd.getWNTFiles(user_id, context_path);
		RateOfTesting rot = new RateOfTesting();			// update ROT and get new values
		rot.updateRateOfTesting(test_type, awt.getLevel(), wtr.getGrade(), user_id, subject, context_path);
		Vector rot_vector = rot.getROTVector(test_type, user_id, subject, context_path);
		String rot_name = "rot_"+test_type+"_vector";
		WordNextTestDates wntd = new WordNextTestDates();
		int waiting_reading_tests = wntd.getWaitingTests(context_path, user_id, Constants.READING, subject).size();
		int waiting_writing_tests = wntd.getWaitingTests(context_path, user_id, Constants.WRITING, subject).size();
		updateMissedWords(context_path, user_id, test_type);  
		
		} catch (java.lang.NumberFormatException nfe)
		{
			log.add("VillaUtilities.updateWord: nufie at 567");
			nfe.printStackTrace();
		}
		return new_level;
	}
	
	public String getWordLevel(String test_type, Word this_word)
	{
		String word_level = null;
		if (test_type.equals(Constants.READING))
		{
			word_level = Integer.toString(this_word.getReadingLevel());
		} else
		{
			word_level = Integer.toString(this_word.getWritingLevel());
		}
		return word_level;
	}
	
	/**
	 * Since a saved test is either pass or fail, without an answer,
	 * we use the text or definition of the word to fill the space
	 * so that some parts of the system don't break.
	 * @param test_type
	 * @param this_word
	 * @return
	 */
	public String getSavedTestAnswer(String test_type, Word this_word)
	{
		String answer = null;
		if (test_type.equals(Constants.READING))
		{
			answer = this_word.getDefinition();
		} else
		{
			answer = this_word.getText();
		}
		return answer;
	}
	
	public AllWordsTest setupAWT(String test_type, String answer, Word this_word)
	{
		AllWordsTest awt = new AllWordsTest();
		awt.setTestType(test_type);
		awt.setAnswer(answer);
		awt.setCategory(this_word.getCategory());
		awt.setDefinition(this_word.getDefinition());
		awt.setId(this_word.getId());
		if (test_type.equals(Constants.READING))
		{
			awt.setLevel(this_word.getReadingLevel()+"");
		} else
		{
			awt.setLevel(this_word.getWritingLevel()+"");
		}
		awt.setText(this_word.getText());
		return awt;
	}
	
	public AllWordsTest setupAWTWithMerge(String test_type, String answer, Word this_word, Word archetype_word)
	{
		AllWordsTest awt = new AllWordsTest();
		awt.setTestType(test_type);
		awt.setAnswer(answer);
		awt.setCategory(this_word.getCategory());
		awt.setDefinition(this_word.getDefinition());
		awt.setId(this_word.getId());
		if (test_type.equals(Constants.READING))
		{
			awt.setLevel(archetype_word.getReadingLevel()+"");
		} else
		{
			awt.setLevel(archetype_word.getWritingLevel()+"");
		}
		awt.setText(this_word.getText());
		return awt;
	}
	
	
	/**
	 * This method adds words to the history file that can be filtered for only missed words, hence the name.
	 * Same as the above method, except the missed words list are not set into the session, since this method
	 * is used to score a saved test of multiple words, so the user is only interested in seeing an update of
	 * the words in the test.
	*The jsp_options hold on/off values, such as printon print missed words by type.
	*/
	private void updateMissedWords(String context_path, 
		String user_id, String test_type)
	{
		Hashtable jsp_options = getJSPOptions(user_id, "daily_test_result", "vocab", context_path); 
		String format_print_missed_reading = (String)jsp_options.get("format_print_missed_reading");
		String format_print_missed_writing = (String)jsp_options.get("format_print_missed_writing");
		Hashtable r_words_defs = new Hashtable();
		Hashtable w_words_defs = new Hashtable();
		WordListFilter wlf = new WordListFilter();
		log.add("vu.updateMissedWords: test_type "+test_type+" fpmr "+format_print_missed_reading+" fpmw "+format_print_missed_writing);
		try
		{
			if (test_type.equals(Constants.READING) && format_print_missed_reading.equals("true"))
			{
				r_words_defs = wlf.getMissedWords(Constants.READING, context_path, user_id, jsp_options);
			}
		} catch (java.lang.NullPointerException npe)
		{
			log.add("npe in if (test_type.equals(Constants.READING) && format_print_missed_reading.equals(true))");
		}
		try
		{	
			if (test_type.equals(Constants.WRITING) && format_print_missed_writing.equals("true"))
			{
				w_words_defs = wlf.getMissedWords(Constants.WRITING, context_path, user_id, jsp_options);
			}
		} catch (java.lang.NullPointerException npe)
		{
			log.add("npe in if (test_type.equals(Constants.WRITING) && format_print_missed_writing.equals(true))");
		}
	}
	
	/**
	*This method should be moved into the interface for all actions, as all pages should let the user set options
	*It appears here and in DailyTestResultAction, and where else?
	*/
	private Hashtable getJSPOptions(String guest_id, String jsp_name, String subject, String current_dir)
	{
		Hashtable options = new Hashtable();
		FileUserOptions fuo = new FileUserOptions(current_dir);
		try
		{
			options = fuo.getJSPOptions(guest_id, jsp_name, subject);
			log.add("VillaUtilities.getJSPOptions: options size "+options.size());
		} catch (java.lang.NullPointerException npe)
		{
			log.add("VillaUtilities.getJSPOptions: npe, using defaults");
			options.put("format_print_missed_reading", "true");
			options.put("format_print_missed_writing", "true");
			options.put("record_failed_tests", "true");
			options.put("record_passed_tests", "true");
			options.put("record_exclude_level", "0");
			options.put("record_limit", "17");
			options.put("word_lookup_for_pass", "false");
			options.put("word_lookup_for_fail", "false");
		}
		//printLog(fuo.getLog(), context);
		return options;
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
	
	private void append(Hashtable hash)
	{
	    Enumeration keys = hash.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)hash.get(key);
		    System.out.println(key+" - "+val);
	    }
	}
	
	public void dumpErrorLog(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			System.err.println("VU - "+v.get(i));
			i++;
		}
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
	
}
