package org.catechis.testing;

import java.util.Vector;
import java.util.Hashtable;
import org.catechis.Storage;
import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.catechis.EncodeString;
import org.catechis.dto.Word;
import org.catechis.dto.Momento;
import org.catechis.dto.UserInfo;
import org.catechis.dto.SavedTest;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordLastTestDates;
import org.catechis.dto.WordTestRecordOptions;
import org.catechis.file.FileTestRecords;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.constants.Constants;
import org.catechis.juksong.TestTimeMemory;


import org.catechis.constants.Loggy;

public class TestUtility extends Loggy
{

	public TestUtility()
	{
		super();
	}
	
	/**
	*<p>AllWordsTest has these member variable:
	*<p>// data kept on xml file
	*<p>private String text;
	*<p>private String definition;
	*<p>private String category;
	*<p>private String test_type;
	*<p>private String level;
	*<p>private int daily_test_index;
	*<p>private long id;
	*<p>// users answers
	*<p>private String answer;
	*/
	public static AllWordsTest setupAllWordsTest(Word word, String type, int test_index)
	{
		AllWordsTest awt_test_word = new AllWordsTest();
		awt_test_word.setText(word.getText());				// really only need on of these as question.
	 	awt_test_word.setDefinition(word.getDefinition());
	 	awt_test_word.setCategory(word.getCategory());  		// dont need this, its in the wntd file
	 	awt_test_word.setTestType(type);
		if (type.equals(Constants.READING))				// dont need it.
		{
			awt_test_word.setLevel(Integer.toString(word.getReadingLevel()));		
		} else if (type.equals(Constants.WRITING)) 
		{
			awt_test_word.setLevel(Integer.toString(word.getWritingLevel()));	
		}
		awt_test_word.setDailyTestIndex(test_index);
		awt_test_word.setId(word.getId());				// word id is in the wntd file 
		return awt_test_word;
	}
	
	/**
	*In this case, the AllWordsTest.answer variable is not a user answer, bu a pass/fail indicator chosen by a student or teacher.
	*@param awt_list is a Vector of test result objects.  The test is already scored with pass/fail info in the awt objects.
	*/
	public Vector scoreSavedTest(UserInfo user_info, SavedTest saved_test, Vector awt_list)
	{
		// test is already scored with pass/fail info in the awt objects
		Vector wtr_list = new Vector();
		for (int i=0; i<awt_list.size();i++)
		{
			AllWordsTest awt = (AllWordsTest)awt_list.get(i);
			WordTestResult wtr = updateWord(user_info, awt, saved_test.getTestDate());
			wtr_list.add(wtr);
		}
	       	return wtr_list;
	}
	    
	    /**
	    *  We have user_info, a test word, and a pass/fail.
	    * the Word object will only have text/def and id.
	    * UserInfo has encoding, root_path, user_id, subject;
	    * SavedTest
		test_id;
		test_date;
		test_name;
		test_type;
		test_status;
		test_format;
		creation_time;
		
	    * the new info we will get here is the new_level
	    * The test info will be saved in FileTestRecords.addDailyTestRecord(wtr, wtro);
	    * in the addDailyTestRecordIfNeeded method.
	    * by way of creating and setting a WordTestRecordOptions.
	    * we also need to keep track of the old level for an undo?
	    * is that necessary?  I suppose there are times when we
	    * would want to reverse a grade even when it's a student
	    * or teacher who has marked a word passed then changed their mind.
	    *
	    * Please note the test for these methods is in FileSaveTestsTest
	    * because this method was created to score a saved test.
	    * Not really a unit test, but a functional test used in development.
	    *
	    * Working notes
	    * In the past we used all this clutter:
	    
	    *<p>AllWordsTest 
	*<p>private String text;
	*<p>private String definition;
	*<p>private String category;
	*<p>private String test_type;
	*<p>private String level;
	*<p>private int daily_test_index;
	*<p>private long id;
	*<p>private String answer;
	
	    *WordTestResult
	* private String text;
	* private String definition;
	* private String answer;
	* private String grade;
	* private String level;
	* private int id;
	* private String original_text;
	* private String original_definition;
	* private String original_level;
	* private String encoding;
	* private String date;
	* private long word_id;
	
		 * AppTestUserDTO has the following attributes:
	 * String user_id;
	 * String current_dir;
	 * String time;             *** not in UserInfo
	 * String subject;
	 * boolean reading;          *** not in UserInfo
	 * boolean writing;          *** not in UserInfo
	
 		TestTimeMemory
	private String original_wltd;
	private String new_wltd;
	private String original_wntd;
	private String new_wntd;
	private String type;
	* This object stores time in milliseconds s representing a words
 * last and next test dates (hence wltd/wntd).  There is already an
 * object in the org.catechis.dto package with WordLastTestDate,
 * so the name of TestTimeMemory is used to differentiate this from that.
 
 * some methods came from com.quaquaverse.testing.TestResultUtility
 *Here are some notes about the difference between this method and the one from
 * quaquaverse, which was taken originally from the indoct DailyTestResultAction:
 		// wtr = store.scoreSingleWordTest(awt, user_id);  this basically get all the info we already have and calls:
		// wtr = scorer.scoreSingleWord(awt, word, user_options); // if there is no file action here...skip it
		// the scorer does both determine pass or fail and update the level in wtr, the latter in this method:
		// wtr = scoreGrade("pass", text, def, user_answer, level, max_level); or fail of course.
		// these methods really only adjust the level, as well as the rigamorole of setting the wtr
		// so those methods are included here 
	    */
	private WordTestResult updateWord(UserInfo user_info, AllWordsTest awt, String test_date)
	{
		
		WordTestResult wtr = new WordTestResult();
		
		String test_type = awt.getTestType();
		String user_id = user_info.getUserId();
		String context_path = user_info.getRootPath();
		FileStorage store = new FileStorage(context_path);		// get from server
		Hashtable user_opts = store.getUserOptions(user_id, context_path);  // get from server
		String subject = user_info.getSubject();
		String max_level = (String)user_opts.get("max_level");
		String encoding = (String)user_opts.get("encoding");		// this is in UserInfo, so same source???
		
		append(Transformer.createTable(awt));
		
		Word word = store.getWordObject("id", Long.toString(awt.getId()), awt.getCategory(), user_info.getUserId());
		String old_level = getAppropriateLevel(word, test_type);
		String test_name = new String("level "+awt.getLevel()+" "+test_type+".test"); // required for store.recordWordTestScore
		wtr.setEncoding(encoding);
		String new_level = store.recordWordTestScore(wtr, awt, max_level, test_name, user_id); // this updates the words level
		wtr.setLevel(new_level);
		
		wtr.setDate(test_date); // this is the date the test was taken,
		addDailyTestRecordIfNeeded(test_type, wtr, user_info, user_opts, awt.getId());	// adds a record in the daily *type* tests.record file. this also removes the word from the new word list if it is a pass test, and of course if the word in on the new <type> words.list	
		FileTestRecords ftr = new FileTestRecords(context_path);
		Momento old_m = ftr.getMomentoObject(user_id, subject); // this is needed to get the time stamp/file name of the wntd (Words Next Test Date) 
		ftr.updateTestsStatus(user_id, subject);
		wtr.setWntdName(old_m.getActionId()+".xml");	// was file[0] in test, now action_id... seems messy to add the xml ext here...
		wtr.setWordId(awt.getId());
		WordLastTestDates wltd = new WordLastTestDates();
		Vector elt_vector = setupELTVector(user_opts);
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(test_type);
		wltd.setLimitOfWords(100);
		TestTimeMemory ttm = wltd.updateTestDateRecordsAndReturnMemory(user_id, context_path, wtr);
		// what do we do with the ttm???
		return wtr;
	}
	
	private String getAppropriateLevel(Word word, String test_type)
	{
		int level = 0; 	
		if (test_type.equals(Constants.READING))
		{
			level = word.getReadingLevel();
		} else if (test_type.equals(Constants.WRITING))
		{
			level = word.getWritingLevel();
		}
		return Integer.toString(level);
			
	}
	
	private WordTestResult scoreGrade(String grade, String text, String def, String answer, int level, String max)
	{
		int max_level = Integer.parseInt(max);
		String org_level = Integer.toString(level);
		if (grade.equals("pass"))
		{
			level = limitLevel((level++), max_level); // check level
		} else
		{
			level = limitLevel((level--), max_level); // check level
		}
		String new_level =  Integer.toString(level);
		WordTestResult wtr = addWordTestResults(text, def, answer, grade, org_level, new_level);
		return wtr;
	}
	
	/**
	*
	* The original comment for this method was:
	* (text, def, answer, "fail", index)
	*/
	private WordTestResult addWordTestResults(String text,String def,String answer,String grade, String org_level, String new_level)
	{
		WordTestResult wtr = new WordTestResult();
		wtr.setText(text);
		wtr.setDefinition(def);
		wtr.setAnswer(answer);
		wtr.setGrade(grade);
		wtr.setLevel(new_level);
		wtr.setOriginalText(text);
		wtr.setOriginalDefinition(def);
		wtr.setOriginalLevel(org_level);
		return wtr;
	}
	
	/**
	* The original method in org.catechis.Soring did not include a 0 level limit, so not sure if that was
	* done somewhere else or not. It must be done somewhere, as a word at level 0 never goes below 0
	* after a failed test.
	* For a long time, the max_level has been 3.  Originally it was 10 if you can believe it.
	* Actually, it's not a bad idea.  We use the concept of a retired word for consecutive passes at level 3
	* lets look at a sample exclude level time vector with a simplified multiple increase:
	* level 0 - 1 day
	* level 1 - 1 week
	* level 2 - 2 weeks
	* level 3 - 1 month
	* level 4 - 2 months
	* level 5 - 4 months
	* level 6 - 6 months
	* level 7 - 1 years
	* level 8 - 2 years
	* level 9 - 4 years
	* level 10- 8 years 
	*
	* Therefore, a word would not reach level 10, (or retired/know status) for 16 years!
	* Obviously this is too much, unless we really are talking about life learning.
	* Even in our native languages, we lose words over time if we don't use them, and
	* maybe that's a good thing.  Our minds change over the years, and what we don't use,
	* eventually we lose.
	* Anyhow, the decision to lime levels to 3 was made when we wanted to look at statistics
	* for the number of words at each level.  The fact that we had both a reading and a writing
	* level for each word meant that our statistics for words in their categories would look like this:
	* Category  	Total  	R.Ave  	W.Ave  	R-0  	W-0  	R-1  	W-1  	R-2  	W-2  	R-3  	W-3
	* All 		1977 	2.69 	2.49 	72 	94 	127 	220 	147 	294 	1631 	1369
	* october1.xml 	37 	2.92 	2.76 	0 	1 	1 	2 	1 	2 	35 	32
	* october2.xml 	36 	2.81 	2.69 	2 	0 	0 	4 	1 	3 	33 	29
	* November.xml 	33 	2.94 	2.67 	0 	1 	1 	2 	0 	4 	32 	26
	* ...
	*
	* For all words we later simplified it to this:
	* Level  	Reading  	Writing
	* 0 		76 		94
	* 1 		122 		217
	* 2 		150 		298
	* 3 		1629 		1368
	* Average 	2.69 		2.49
	* 
	* You can imagine how messy it would get with more levels.
	* Also, we wanted to look at these statistics over time in a way like this:
	* Date  	Tests  	Ave  	Words  	R-Avg  	W-Avg  	R-0  	W-0  	R-1  	W-1  	R-2  	W-2  	R-3  	W-3
	* ...
	* 07/27/09 	14822	49.67 	1977 	2.69 	2.49 	76 	94 	119 	217 	154 	298 	1628 	1368
	* 07/29/09 	15504	49.67 	1977 	2.69 	2.49 	76 	94 	122 	217 	150 	298 	1629 	1368
	* 08/09/09 	15630 	49.67 	1977 	2.69 	2.49 	72 	94 	127 	220 	147 	294 	1631 	1369
	* 
	* More levels would make this a truly monstrous thing.  However, when I see these stats, I feel a sense of
	* satisfaction at seeing my vocabulary learning in a visual way.  And creating a visual view of a persons
	* learning over time, in an animated way could take on a life of it's own especially if we want to 
	* compare a students learning strategies and teachers teaching techniques.
	*
	* So even the most simple method can have the most profound consequences and provoke the longest discussions...
	*/
	private int limitLevel(int level, int max_level)
	{
		if (level>max_level)
		{
			level = max_level;
		}
		if (level < 0)
		{
			level = 0;
		}
		return level;
	}
	
	/**
	*<p>record the test in daily *type* tests.record file
	*this ads an element to the daily *type* tests.record file.
	*<p>The WordTestRecordOption object holds all the on off switches
	* for what should be recorded in the file.  
	*<p>The FileTestRecords object does all the word of checking these
	* options and then saving a record in the appropriate file.
	* While it at it, this method will remove the word from the new words list
	* if the test is passed.
	* Otherwise, all the work is done in FileTestRecords.addDailyTestRecord(wtr, wtro);
	* by way of creating and setting a WordTestRecordOptions.
	*/
	private void addDailyTestRecordIfNeeded(String type, WordTestResult wtr, 
		UserInfo user_info, Hashtable user_opts, long word_id)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_info.getUserId());
	    wtro.setRootPath(user_info.getRootPath());
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    wtro.setWordId(word_id);
	    String pass_fail = wtr.getGrade();
	    if (pass_fail.equals("pass"))
	    {
		    FileJDOMWordLists fjdomwl =  new FileJDOMWordLists(user_info.getRootPath(), Constants.READING, user_info.getUserId());
		    try
		    {
			    fjdomwl.removeWordFromNewWordsList(Long.toString(word_id), user_info.getEncoding());
		    } catch (java.lang.NullPointerException npe)
		    {
			    // dont know why this should be null...
		    }
	    }
	    FileTestRecords ftr = new FileTestRecords(user_info.getRootPath());
	    ftr.setEncoding(user_info.getEncoding());
	    //System.out.println("wtr ---------------------------");
	    //printLog(Transformer.createTable(wtr));
	    //System.out.println("wtr ---------------------------");
	    ftr.addDailyTestRecord(wtr, wtro);
	}
	
	/**
	*What class did we move this method too?  Oh, this was the one we moved.
	*/
	private Vector setupELTVector(Hashtable user_opts)
	{
		Vector elt_vector = new Vector();
		elt_vector.add(user_opts.get("exclude_level0_test"));
		elt_vector.add(user_opts.get("exclude_level1_test"));
		elt_vector.add(user_opts.get("exclude_level2_test"));
		elt_vector.add(user_opts.get("exclude_level3_test"));
		return elt_vector;
	}

	/**
	*
	*<p>Notes from ChangeUpdateScoreAction:
	*<p>Here goes
	WordTestMemory
	private String category;  	***
	private String type;		***
	private String date;		***
	private String score;		SET LATer
	private String index;		*** -1
	private String number_correct;	?
	private String level;		?
	private String test_name;	needed 
	long word_id
	
	<p>WordTestResult
	private String text;
	private String definition;
	private String answer;
	private String grade;
	private String level;
	private int id;
	private String original_text;
	private String original_definition;
	private String original_level;
	private String encoding;
	private String date;
	long word_id
	
	Objects already in the session:
	ArrayList key_list = (ArrayList)session.getAttribute("actual_key_list"); // if this is null then we create it next
	WordLastTestDates wltd = (WordLastTestDates)session.getAttribute("wltd"); // the list of words and their key assosiations
	
	DailyTestAction attributes set:
	session.setAttribute("daily_test_index", "0")
	* session.setAttribute("awt_test_word", awt_test_word);
	//session.setAttribute("actual_key_list", key_list);
	//session.setAttribute("wltd", wltd);
	
	* session.setAttribute("awt_test_word", awt);
	session.setAttribute("actual_key_list", key_list);
	session.setAttribute("wltd", wltd);
	* session.setAttribute("wtr", wtr);
	
	objects actually needed in the daily_test_result.jsp:
	AllWordsTest awt = (AllWordsTest)session.getAttribute("awt_test_word");
	WordTestResult wtr = (WordTestResult)session.getAttribute("wtr");
	(String)session.getAttribute("daily_test_index")
	*/
	public WordTestResult reverseTestResult(WordTestResult word_test_result, AllWordsTest awt, String user_name, String user_id, String context_path)
	{
		Storage store = new FileStorage(context_path);
		Hashtable user_opts = store.getUserOptions(user_name, context_path);
		log.add("TestUtility.updateLevel: user_opts --------"); // dumpHash(user_opts, context);
		String encoding = (String)user_opts.get("encoding");String test_type = awt.getTestType();
		String grade = word_test_result.getGrade();
		WordTestMemory word_test_memory = new WordTestMemory();
		word_test_memory.setType(awt.getTestType()); 
		word_test_memory.setCategory(awt.getCategory());
		word_test_memory.setDate(word_test_result.getDate()); // this is used to locate the test in test.records
		word_test_memory.setIndex("-1"); // what's this for again???
		String test_name = ("level "+word_test_result.getLevel()+" "+awt.getTestType()+".test");
		word_test_memory.setTestName(test_name);	
		log.add("TestUtility.updateLevel: wtm --------");dumpLog(Transformer.createTable(word_test_memory));
		WordTestRecordOptions wtro = new WordTestRecordOptions(test_type, user_name, context_path, 
			user_opts, word_test_result.getWordId());
		String org_text = EncodeString.encodeThis(word_test_result.getOriginalText(), encoding);
		String org_def  = EncodeString.encodeThis(word_test_result.getOriginalDefinition(), encoding);
		String new_word_level = word_test_result.getOriginalLevel();   // set default
		word_test_result = reverseGrade(word_test_result, word_test_memory, 
		store, user_name, grade, encoding);
		word_test_result.setWordId(awt.getId());
		wtro.setWordId(word_test_result.getWordId());
		wtro.setType(test_type);
		wtro.setUserName(user_name);	// not sure why these were comming up null...oh, something is not getting the ids in JDOMSolution...
		updateDailyTestsRecords(context_path, word_test_result, wtro, encoding);
		return word_test_result;  // if there is any result for this method, this is it.
	}
	
	/**
	*A method from ChangeUpdateAction in Indoct
	*/
	private void updateDailyTestsRecords(String context_path, WordTestResult wtr, 
			WordTestRecordOptions wtro, String encoding)
	{
		FileTestRecords ftr = new FileTestRecords(context_path);
		ftr.setEncoding(encoding);
		try
		{
			ftr.reverseTestRecord(wtr, wtro);				// delete or add a daily test.records entry
		} catch (java.lang.NullPointerException npe)
		{
			log.add("TestUtility.updateDailyTestsRecords: FileTestRecords npe --------- log");
			append(ftr.getLog());
		}
	}
	
	/**
	*<p>Another method from ChangeUpdateAction in Indoct.
	*<p>This method finds the pass or fail score of the particular word, and has to change
	* both the words reading or writing level and the overall percentage score which needs
	* to be re-calculated from the number of words tested, and the number of words correct
	* held in the i_score variable.
	*<p>
	*/
	private WordTestResult reverseGrade(WordTestResult word_test_result, WordTestMemory word_test_memory, 
		Storage store, String user_name, String grade, String encoding)
	{
			word_test_memory.setScore("-1");
			log.add("TestUtility.reverseGrade");
			if (grade.equals("pass"))
			{
				word_test_result.setGrade("fail");
			}
			else
			{
				word_test_result.setGrade("pass");
			}
			log.add("TestUtility.reverseGrade 1");
		String new_word_level = store.updateWordLevel(word_test_memory, word_test_result, user_name, encoding);
		log.add("TestUtility.reverseGrade 2");
		word_test_result.setLevel(new_word_level);
		return word_test_result;
	}
	
	/**
	*<p>record the test in daily *type* tests.record file
	*this ads an element to the daily *type* tests.record file.
	*<p>The WordTestRecordOption object holds all the on off switches
	* for what should be recorded in the file.  
	*<p>The FileTestRecords object does all the word of checking these
	* options and then saving a record in the appropriate file.
	*<p>This method was moved from DailyTestResultAction.
	*/
	public void addDailyTestRecordIfNeeded(String type, 
		WordTestResult wtr, String user_id, String root_path, 
		Hashtable user_opts, long id, String encoding)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_id);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    wtro.setWordId(id);
	    String pass_fail = wtr.getGrade();
	    if (pass_fail.equals("pass"))
	    {
		    removeWordFromNewWordsList(root_path, id, type, encoding, user_id);
	    }
	    FileTestRecords ftr = new FileTestRecords(root_path);
	    ftr.setEncoding(encoding);
	    ftr.addDailyTestRecord(wtr, wtro);
	}
	
	/**
	*Helper method for addDailyTestRecordIfNeeded.
	*/
	private void removeWordFromNewWordsList(String context_path, long search_id, String type, 
			String encoding, String user_id)
	{
		if (type.equals(Constants.READING))
		{
			FileJDOMWordLists fjdomwl =  new FileJDOMWordLists(context_path, Constants.READING, user_id, "vocab");
			fjdomwl.removeWordFromNewWordsList(Long.toString(search_id), encoding);
		} else
		{
			FileJDOMWordLists fjdomwl =  new FileJDOMWordLists(context_path, Constants.WRITING, user_id, "vocab");
			fjdomwl.removeWordFromNewWordsList(Long.toString(search_id), encoding);
		}
	}
}
