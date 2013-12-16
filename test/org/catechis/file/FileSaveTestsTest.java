package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.apache.struts.action.ActionMapping;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.UserInfo;
import org.catechis.dto.SavedTest;
import org.catechis.Domartin;
import org.catechis.I18NWebUtility;
import org.catechis.Storage;
import org.catechis.Transformer;
import org.catechis.FileStorage;
import org.catechis.dto.TestStats;
import org.catechis.JDOMSolution;
import org.catechis.dto.SavedTest;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.SavedTestResult;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordLastTestDates;
import org.catechis.file.FileEdit;
import org.catechis.file.FileSaveTests;
import org.catechis.file.FileCategories;
import org.catechis.testing.SaveTest;
import org.catechis.admin.FileUserOptions;
import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;
import org.catechis.testing.TestUtility;
import org.catechis.wherewithal.DeckCard;
import org.catechis.wherewithal.HouseDeck;

public class FileSaveTestsTest extends TestCase
{

    public FileSaveTestsTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {

    }
    
    /**
    * fromWLTDsToFile(String user_id, String subj, WordLastTestDates wltd, ArrayList key_list, SavedTest saved_test)
    *
    public void testFromWLTDsToFile()
    {
	    String user_id = new String("test_user");
	    String subject = new String("vocab");
	    //String encoding = new String("euc-kr");
	    String encoding = new String("UTF-8");
	    File path_file = new File("");
	    String root_path = path_file.getAbsolutePath();
	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
	    //dumpLog(Transformer.createTable(user_info));
	    // original file
	    FileSaveTests f_save_tests = new FileSaveTests();
	    SaveTest save_test = new SaveTest(f_save_tests);
	    
	    Test[] tests = new Test[1];
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    tests[0] = test1;
	    Word word1 = new Word();
	    word1.setText("text1");
	    word1.setDefinition("def1");
	    word1.setId(Long.parseLong("1111111111111111111"));
	    word1.setTests(tests);
	    word1.setCategory("some file1.xml");
	    Word word2 = new Word();
	    word2.setText("text2");
	    word2.setDefinition("def2");
	    word2.setId(Long.parseLong("2222222222222222222"));
	    word2.setTests(tests);
	    word2.setCategory("some file2.xml");
	    String type = new String("reading"); 
	    Vector elt_vector = new Vector(); // exclude level time in days
	    elt_vector.add("1");
	    elt_vector.add("14");
	    elt_vector.add("30");
	    elt_vector.add("90");
	    Date today = new Date();
	    long now = today.getTime();
	    String today_now = Long.toString(now);
	    WordLastTestDates wltd = new WordLastTestDates();
	    wltd.setExcludeLevelTimes(elt_vector);
	    wltd.setType(type);
	    wltd.setLimitOfWords(100);
	    wltd.setSubject(Constants.VOCAB);
	    wltd.addWord(word1, today_now);
	    wltd.addWord(word2, today_now);
	    
	    ArrayList key_list = wltd.getSortedWLTDKeys();; 
	    SavedTest saved_test = new SavedTest();
	    saved_test.setTestId("1111111111111111111");
		saved_test.setTestDate(today_now);
		saved_test.setTestName("testie");
		saved_test.setTestType("reading");
		saved_test.setTestStatus("pending");
		saved_test.setTestFormat("DailyTest");
		saved_test.setCreationTime(today_now);
	    save_test.fromWLTDsToFile(user_info, wltd, key_list, saved_test);
	    Vector saved_tests_list = save_test.loadSavedTests(user_info);
	    //dumpLog(save_test.getLog());
	    //dumpSavedTestsList(saved_tests_list);
	    //String actual = actual_word.getText();
	    //String expected = new String("sexy");
	    //assertEquals(expected, actual);
    }
    */
    
    /**
     * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
     */
     public void testAddToSavedTestsList()
     {
 	    String user_id = new String("test_user");
 	    String subject = new String("vocab");
 	    //String encoding = new String("euc-kr");
 	    String encoding = new String("UTF-8");
 	    File path_file = new File("");
 	    String root_path = path_file.getAbsolutePath();
 	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
 	    //dumpLog(Transformer.createTable(user_info));
 	    // original file
 	    String str_id = Domartin.getNewID()+"";
 	    //System.out.println("FileSaveTestsTest.testAddToSavedTestsList: new id "+str_id);
 	    FileSaveTests f_save_tests = new FileSaveTests();
 	    String today_now = new Date().getTime()+"";
 	    SavedTest saved_test = new SavedTest();
 	    saved_test.setTestId(str_id);
 		saved_test.setTestDate(today_now);
 		saved_test.setTestName("testie");
 		saved_test.setTestType("reading");
 		saved_test.setTestStatus("pending");
 		saved_test.setTestFormat("DailyTest");
 		saved_test.setCreationTime(today_now);
 	    f_save_tests.addToSavedTestsList(user_info, saved_test, str_id);
 	    // now check it the test was saved
 	    Vector saved_tests = f_save_tests.getSavedTestsList(user_info);
 	    boolean actual = false;
 	    int i = 0;
 	    int size = saved_tests.size();
 	    while (i<size)
 	    {
 	    	SavedTest this_saved_test = (SavedTest)saved_tests.get(i);
 	    	String this_id = this_saved_test.getTestId();
 	    	if (this_id.equals(str_id))
 	    	{
 	    		actual = true;
 	    	}
 	    	i++;
 	    }
 	    boolean expected = true;
 	    // clean up
 	    f_save_tests.deleteSavedTestList(str_id, user_info);
 	    assertEquals(expected, actual);
     }
    
     /**
      * Testing scoreSavedTests(UserInfo user_info, SavedTest saved_test, Vector test_words, Vector test_word_results)
      * Go through each test word result, load the word and update its level and other
      * 
      * If we put word 1's id in the test results vector, then only that word will be incremented.
      * 
      * -5882280617916499657
      * 
  * status files based on the result of pass or fail.  Return a Vector of test
  * score objects (SavedTestResult) to notify the user.   
  *
  * SavedTestResults:
  private String text;
  private String definition;
  private String category;
  private long word_id;     
  private String testing_type;
  private String original_level;
  private String new_level;
  private int test_index;  
  private String grade; 
  private String encoding;
  *
     * This method needs to support:
     * Hashtable user_opts = store.getUserOptions(user_id, context_path);
     * Word word = store.getWordObject("id", Long.toString(awt.getId()), awt.getCategory(), user_info.getId());
     * Momento old_m = ftr.getMomentoObject(user_id, subject);
  * FileJDOMWordLists fjdomwl =  new FileJDOMWordLists(root_path, Constants.READING, user_id);
  * fjdomwl.removeWordFromNewWordsList(Long.toString(word_id), encoding);
  * FileTestRecords ftr = new FileTestRecords(root_path);
  * ftr.addDailyTestRecord(wtr, wtro);
  * WordLastTestDates wltd = new WordLastTestDates();
  * TestTimeMemory ttm = wltd.updateTestDateRecordsAndReturnMemory(user_id, context_path, wtr);
  * Momento new_m = new Momento("IntegratedTestResultAction", Long.toString(new Date().getTime()), "default", "default");
  * ftr.setMomentoObject(user_id, subject, new_m);
 <id>-4647875113887576714</id></word>
 <word><text>gorueda</text><definition>To choose</definition><writing-level>3</writing-level>
 <test><date>Sat Jan 08 18:51:32 PST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
 <test><date>Fri Feb 25 11:12:51 PST 2005</date><file>level 0 writing.test</file><grade>fail</grade></test>
 <test><date>Fri Feb 25 11:14:22 PST 2005</date><file>level 0 writing.test</file><grade>fail</grade></test>
 <test><date>Mon Feb 28 23:26:42 PST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
 <reading-level>1</reading-level>
 // data kept on xml file
  private String text;
  private String definition;
  private String category;
  private String test_type;
  private String level;
  private int daily_test_index;
  private long id;
  private String answer;
  
  
     *
     public void testScoreSavedTest()
     {
    	 //System.out.println("FileSaveTestsTest.testScoreSavedTest");
    	 String user_id = new String("test_user");
    	 String subject = new String("vocab");
    	 String encoding = new String("UTF-8");
    	 //String encoding = new String("UTF-8");
   	 	 String category = ("word1.xml");
   	 	 File path_file = new File("");
   	 	 String root_path = path_file.getAbsolutePath();
     
   	 	 UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
   	 	 FileSaveTests f_save_tests = new FileSaveTests();
   	 	 SaveTest save_test = new SaveTest();
   	 	 Date today = new Date();
   	 	 long now = today.getTime();
   	 	 String today_now = Long.toString(now);
   	 	 String test_id = "063";
   	 	 SavedTest saved_test = new SavedTest();
   	 	 saved_test.setTestId(test_id);
   	 	 saved_test.setTestDate(today_now);
   	 	 saved_test.setTestName("testie");
   	 	 saved_test.setTestType(Constants.READING_AND_WRITING);
   	 	 saved_test.setTestStatus("pending");
   	 	 saved_test.setTestFormat("IntegratedTest");
   	 	 saved_test.setCreationTime(today_now);
   	 	 String word1_id = "-58822806179164996574";
   	 	 String word2_id = "-58822806179164996575";
   	 	 
   	 	 Vector test_words_vector = f_save_tests.loadSavedTestWordsVector(user_info, test_id);
   	 	 Vector test_word_results = new Vector();
   	 	 test_word_results.add(word1_id);
   	 	 //test_word_results.add(word2_id);
   	 	 TestUtility test_utility = new TestUtility();
   	 	 Vector results = f_save_tests.scoreSavedTests(user_info, saved_test, test_words_vector, test_word_results);
   	 	 // SavedTestResult
   	 	 int i = 0;
   	 	 while (i<results.size())
   	 	 {
   	 		 SavedTestResult str = (SavedTestResult)results.get(i);
   	 		 //dumpLog(Transformer.createTable(str));
   	 		 i++;
   	 	 }
   	 	 //System.out.println("FileSaveTests --- log");
   	 	 //dumpLog(f_save_tests.getLog());
   	 	 //Vector wltds_list = test_utility.scoreSavedTest(user_info, saved_test, awt_list);
   	 	 //dumpLog(test_utility.getLog());
      
     }*/
    
    /**
     * Testing Hashtable load(UserInfo user_info, String test_id)
     *
    public void testLoad()
    {
    	//System.out.println("FileSaveTestsTest.testLoad");
    	String user_id = new String("test_user");
	    String subject = new String("vocab");
	    String encoding = new String("UTF-8");
	    File path_file = new File("");
	    String root_path = path_file.getAbsolutePath();
	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
	    String test_id = "-735976319087141914";
	    FileSaveTests f_save_tests = new FileSaveTests();
    	Hashtable words = f_save_tests.load(user_info, test_id);
    	Enumeration keys = words.keys();
    	while (keys.hasMoreElements())
    	{
    		String key = (String)keys.nextElement();
    		Word word = (Word)words.get(key);
    		//System.out.println(key+" "+word.getDefinition());
    	}
    }
    */
    
    /**
     * Vector loadTestList(UserInfo user_info)
     * <test_id>-735976319087141914</test_id>
		<test_date>1249668205974</test_date>
		<test_name>testie</test_name>
		<test_type>reading</test_type>
		<test_status>pending</test_status>
		<test_format>DailyTest</test_format>
		<creation_time>1249668205974</creation_time>
     * @param tests_list
     *
    public void testLoadTestList()
    {
    	//System.out.println("FileSaveTestsTest.testLoadTestList");
    	String user_id = new String("test_user");
	    String subject = new String("vocab");
	    String encoding = new String("UTF-8");
	    File path_file = new File("");
	    String root_path = path_file.getAbsolutePath();
	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
	    String test_id = "-735976319087141914";
	    FileSaveTests f_save_tests = new FileSaveTests();
    	Vector tests = f_save_tests.getSavedTestsList(user_info);
    	int i = 0;
    	int size = tests.size();
    	while (i<size)
    	{
    		SavedTest saved_test = (SavedTest)tests.get(i);
    		//System.out.println(saved_test.getTestId()+" "+saved_test.getTestName()+" - "+Transformer.getDateFromMilliseconds(saved_test.getCreationTime()));
    		i++;
    	}
    }
    */
    
    /**
     * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
     */
     public void testSaveIndexAndType()
     {
 	    String user_id = new String("test_user");
 	    String subject = new String("vocab");
 	    //String encoding = new String("euc-kr");
 	    String encoding = new String("UTF-8");
 	    File path_file = new File("");
 	    String root_path = path_file.getAbsolutePath();
 	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
 	    String str_id = Domartin.getNewID()+"";
 	    //System.out.println("FileSaveTestsTest.testSaveIndexAndType");
 	    FileSaveTests f_save_tests = new FileSaveTests();
 	    String today_now = new Date().getTime()+"";
 	    SavedTest saved_test = new SavedTest();
 	    saved_test.setTestId(str_id);
 		saved_test.setTestDate(today_now);
 		saved_test.setTestName(str_id);
 		saved_test.setTestType(Constants.READING_AND_WRITING);
 		saved_test.setTestStatus("pending");
 		saved_test.setTestFormat("IntegratedTest");
 		saved_test.setCreationTime(today_now);
 	    f_save_tests.addToSavedTestsList(user_info, saved_test, str_id);
 	    // create word list
 	    Word word1 = new Word();
	    word1.setText("text1");
	    word1.setDefinition("def1");
	    word1.setId(Long.parseLong("1111111111111111111"));
	    word1.setCategory("some file1.xml");
	    Word word2 = new Word();
	    word2.setText("text2");
	    word2.setDefinition("def2");
	    word2.setId(Long.parseLong("2222222222222222222"));
	    word2.setCategory("some file2.xml");
	    Test[] tests = new Test[1];
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    test1.setType(Constants.READING_AND_WRITING);
	    tests[0] = test1;
	    word1.setTests(tests);
	    word2.setTests(tests);
	    Vector words = new Vector();
	    words.add(word1);
	    words.add(word2);
	    f_save_tests.save(user_info, words, str_id);
	   //System.out.println("FileSaveTestsTest.testSaveType: str_id "+str_id+" %%%");
	    // now check it the test was saved
 	    Vector saved_tests = f_save_tests.getSavedTestsList(user_info);
 	    Hashtable saved_words = f_save_tests.load(user_info, str_id);
 	    //dumpWords(saved_words);
 	    Word word = (Word)saved_words.get("0");
 	    Test test = word.getTests(0); 	    
 	    String actual_type = test.getType();
 	    String expected_type = Constants.READING_AND_WRITING;
 	    // claeanup
 	    f_save_tests.deleteSavedTestList(str_id, user_info);
 	    assertEquals(expected_type, actual_type);
     }
     
     /**
      * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
      */
      public void testSavedType()
      {
  	    String user_id = new String("test_user");
  	    String subject = new String("vocab");
  	    //String encoding = new String("euc-kr");
  	    String encoding = new String("UTF-8");
  	    File path_file = new File("");
  	    String root_path = path_file.getAbsolutePath();
  	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
  	    String str_id = Domartin.getNewID()+"";
  	    //System.out.println("FileSaveTestsTest.testSavedType str_id "+ str_id);
  	    FileSaveTests f_save_tests = new FileSaveTests();
  	    String today_now = new Date().getTime()+"";
  	    SavedTest saved_test = new SavedTest();
  	    saved_test.setTestId(str_id);
  		saved_test.setTestDate(today_now);
  		saved_test.setTestName("rw");
  		saved_test.setTestType(Constants.READING_AND_WRITING);
  		saved_test.setTestStatus("pending");
  		saved_test.setTestFormat("IntegratedTest");
  		saved_test.setCreationTime(today_now);
  		saved_test.setNumberOfWords("2");
  		//System.out.println("number of words before save "+saved_test.getNumberOfWords());
  	    f_save_tests.addToSavedTestsList(user_info, saved_test, str_id);
  	  //System.out.println("FileSaveTestsTest.testSaveType: end -------------------- log");
	    //dumpLog(f_save_tests.getLog());
  	    // create word list
  	    Word word1 = new Word();
 	    word1.setText("text1");
 	    word1.setDefinition("def1");
 	    word1.setId(Long.parseLong("1111111111111111111"));
 	    word1.setCategory("some file1.xml");
 	    Word word2 = new Word();
 	    word2.setText("text2");
 	    word2.setDefinition("def2");
 	    word2.setId(Long.parseLong("2222222222222222222"));
 	    word2.setCategory("some file2.xml");
 	    Test[] tests = new Test[1];
 	    Test test1 = new Test();
 	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
 	    test1.setName("level 0 reading.test");
 	    test1.setGrade("pass");
 	    test1.setType(Constants.READING);
 	    tests[0] = test1;
 	     word1.setTests(tests);
	    Test test2 = new Test();
	    test2.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test2.setName("level 0 reading.test");
	    test2.setGrade("pass");
	    test2.setType(Constants.READING);
	    tests[0] = test2;
	    word2.setTests(tests);
 	    Vector words = new Vector();
 	    words.add(word1);
 	    words.add(word2);
 	    f_save_tests.save(user_info, words, str_id);
  	    // now check it the test was saved
  	    Vector saved_tests = f_save_tests.getSavedTestsList(user_info);
  	    //SavedTest saved_test_after = getSavedTest(str_id, saved_tests);
  	    //System.out.println("FileSaveTestsTest.testSaveType: end -------------------- saved this test ");
	    //System.out.println("test name "+saved_test_after.getTestName());
	    //System.out.println("number of words "+saved_test_after.getNumberOfWords());
	    //System.out.println("FileSaveTestsTest.testSaveType: end -------------------- log");
	    //dumpLog(f_save_tests.getLog());
  	    Hashtable saved_words = f_save_tests.load(user_info, str_id);
  	    //System.out.println("FileSaveTestsTest.testSaveType: str_id "+str_id+" %%%");
  	   // dumpWords(saved_words);
  	    //System.out.println("FileSaveTestsTest.testSaveType: end --------------------");
  	    Word word = (Word)saved_words.get("0");
  	    Test test = word.getTests(0); 	    
  	    String actual_type = test.getType();
  	    String expected_type = Constants.READING;
  	    // cleanup
  	    f_save_tests.deleteSavedTestList(str_id, user_info);
  	    assertEquals(expected_type, actual_type);
      }
      
      private SavedTest getSavedTest(String str_id, Vector saved_tests)
      {
    	  SavedTest saved_test = new SavedTest();
    	  for (int i=0;i<saved_tests.size();i++)
    	  {
    		 saved_test = (SavedTest)saved_tests.get(i);
    		 String this_id = saved_test.getTestId();
    		 if (this_id.equals(str_id))
    		 {
    			 return saved_test;
    		 }
    	  }
    	  return saved_test;
      }
      
      /**
       * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
       */
       public void testDeleteSavedTest()
       {
   	    String user_id = new String("test_user");
   	    String subject = new String("vocab");
   	    //String encoding = new String("euc-kr");
   	    String encoding = new String("UTF-8");
   	    File path_file = new File("");
   	    String root_path = path_file.getAbsolutePath();
   	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
   	    String str_id = Domartin.getNewID()+"";
   	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting");
   	    FileSaveTests f_save_tests = new FileSaveTests();
   	    String today_now = new Date().getTime()+"";
   	    SavedTest saved_test = new SavedTest();
   	    saved_test.setTestId(str_id);
   		saved_test.setTestDate(today_now);
   		saved_test.setTestName("rw");
   		saved_test.setTestType(Constants.READING_AND_WRITING);
   		saved_test.setTestStatus("pending");
   		saved_test.setTestFormat("IntegratedTest");
   		saved_test.setCreationTime(today_now);
   	    f_save_tests.addToSavedTestsList(user_info, saved_test, str_id);
   	    // create word list
   	 
   	    Word word1 = new Word();
  	    word1.setText("READING text1");
  	    word1.setDefinition("def1");
  	    word1.setId(Long.parseLong("1111111111111111111"));
  	    word1.setCategory("some file1.xml");
  	    Test[] tests1 = new Test[1];
  	    Test test1 = new Test();
	    test1.setType(Constants.READING);
	    tests1[0] = test1;
	    word1.setTests(tests1);
	    
  	    Word word2 = new Word();
  	    word2.setText("WRITING text2");
  	    word2.setDefinition("def2");
  	    word2.setId(Long.parseLong("2222222222222222222"));
  	    word2.setCategory("some file2.xml");
  	    Test[] tests2 = new Test[1];
 	    Test test2 = new Test();
 	    test2.setType(Constants.WRITING);
 	    tests2[0] = test2;
 	    word2.setTests(tests2);
 	    
  	    Vector words = new Vector();
  	    words.add(word1);
  	    words.add(word2);
  	    f_save_tests.save(user_info, words, str_id);
  	  
  	    // now try to delete what we just made.
  	    f_save_tests.resetLog();
   	    boolean actual = f_save_tests.deleteSavedTestList(str_id, user_info);
   	    boolean expected = true;
   	    assertEquals(expected, actual);
       }
      
      /**
       * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
       */
       public void testSavedTypeWriting()
       {
   	    String user_id = new String("test_user");
   	    String subject = new String("vocab");
   	    //String encoding = new String("euc-kr");
   	    String encoding = new String("UTF-8");
   	    File path_file = new File("");
   	    String root_path = path_file.getAbsolutePath();
   	    UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
   	    String str_id = Domartin.getNewID()+"";
   	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting");
   	    FileSaveTests f_save_tests = new FileSaveTests();
   	    String today_now = new Date().getTime()+"";
   	    SavedTest saved_test = new SavedTest();
   	    saved_test.setTestId(str_id);
   		saved_test.setTestDate(today_now);
   		saved_test.setTestName("rw");
   		saved_test.setTestType(Constants.READING_AND_WRITING);
   		saved_test.setTestStatus("pending");
   		saved_test.setTestFormat("IntegratedTest");
   		saved_test.setCreationTime(today_now);
   	    f_save_tests.addToSavedTestsList(user_info, saved_test, str_id);
   	    // create word list
   	 
   	    Word word1 = new Word();
  	    word1.setText("READING text1");
  	    word1.setDefinition("def1");
  	    word1.setId(Long.parseLong("1111111111111111111"));
  	    word1.setCategory("some file1.xml");
  	    Test[] tests1 = new Test[1];
  	    Test test1 = new Test();
	    test1.setType(Constants.READING);
	    tests1[0] = test1;
	    word1.setTests(tests1);
	    
  	    Word word2 = new Word();
  	    word2.setText("WRITING text2");
  	    word2.setDefinition("def2");
  	    word2.setId(Long.parseLong("2222222222222222222"));
  	    word2.setCategory("some file2.xml");
  	    Test[] tests2 = new Test[1];
 	    Test test2 = new Test();
 	    test2.setType(Constants.WRITING);
 	    tests2[0] = test2;
 	    word2.setTests(tests2);
 	    
  	    Vector words = new Vector();
  	    words.add(word1);
  	    words.add(word2);
  	    f_save_tests.save(user_info, words, str_id);
  	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting --- save log");
  	    //dumpLog(f_save_tests.getLog());
   	    // now check it the test was saved
  	    f_save_tests.resetLog();
   	    Vector saved_tests = f_save_tests.getSavedTestsList(user_info);
   	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting --- getSavedTestsList log");
   	    //dumpLog(f_save_tests.getLog());
   	    f_save_tests.resetLog();
   	    Hashtable saved_words = f_save_tests.load(user_info, str_id);
   	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting --- load log");
   	    //dumpLog(f_save_tests.getLog());
   	    //System.out.println("FileSaveTestsTest.testSavedTypeWriting --- words");
   	    //dumpWords(saved_words);
   	    
   	    String actual_type = null;
   	    Word word = (Word)saved_words.get("0");
   	    if(word.getId() == Long.parseLong("2222222222222222222"))
   	    {
   	    	Test test = word.getTests(0); 	    
   	    	actual_type = test.getType();
   	    } else 
   	    {
   	    	word = (Word)saved_words.get("1");
   	    	Test test = word.getTests(0); 	    
   	    	actual_type = test.getType();
   	    }
   	    String expected_type = Constants.WRITING;
   	    // cleanup
   	    f_save_tests.deleteSavedTestList(str_id, user_info);
   	    assertEquals(expected_type, actual_type);
       }
       
       /**
        * Vector loadTestList(UserInfo user_info)
        * <test_id>-735976319087141914</test_id>
   		<test_date>1249668205974</test_date>
   		<test_name>testie</test_name>
   		<test_type>reading</test_type>
   		<test_status>pending</test_status>
   		<test_format>DailyTest</test_format>
   		<creation_time>1249668205974</creation_time>
        * @param tests_list
        */
       public void testUpdateSavedTestStatus()
       {
    	   
    	    //System.out.println("FileSaveTestsTest.testUpdateSavedTestStatus ++++++++++++");
       		String user_id = new String("test_user");
       		String subject = new String("vocab");
       		String encoding = new String("UTF-8");
       		File path_file = new File("");
       		String root_path = path_file.getAbsolutePath();
       		UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
       		String test_id = "063";
       		FileSaveTests f_save_tests = new FileSaveTests();
       		Vector tests = f_save_tests.getSavedTestsList(user_info);
       		f_save_tests.resetLog();
       		Date date = new Date();
       		long time = date.getTime();
       		int i = 0;
       		int size = tests.size();
       		//System.out.println("FileSaveTestsTest.testUpdateSavedTestStatus: size "+size);
       		while (i<size)
       		{
       			SavedTest saved_test = (SavedTest)tests.get(i);
       			if (saved_test.getTestId().equals(test_id))
       			{
       				// change test status and score date
       				saved_test.setTestStatus(time+"");
       				saved_test.setScoreTime(time+"");
       				f_save_tests = new FileSaveTests();
       				f_save_tests.updateSavedTestStatus(user_info, saved_test);
       				//System.out.println("fst log ---------------------------------");
       				//dumpLog(f_save_tests.getLog());
       				//System.out.println("log end ---------------------------------");
       				break;
       			}	else
       			{
       				//System.out.println("FileSaveTestsTest.testUpdateSavedTestStatus "+i+" "+saved_test.getTestId()+" "+saved_test.getTestName()+" - "+Transformer.getDateFromMilliseconds(saved_test.getCreationTime()));
       			}
       			i++;
       		}	
       		
       		
       	
       		String actual = null;
       		// now load the test list and find the changed test to make sure it was changed, isn't it?
       		tests = f_save_tests.getSavedTestsList(user_info);
       		i = 0;
       		size = tests.size();
       		while (i<size)
       		{
       			SavedTest saved_test = (SavedTest)tests.get(i);
       			actual = saved_test.getTestStatus();
       			if (saved_test.getTestId().equals(test_id))
       			{
       				//System.out.println("FileSaveTestsTest.testUpdateSavedTestStatus: new saved_test ===");
       				//dumpLog(Transformer.createTable(saved_test));
       				//System.out.println("FileSaveTestsTest.testUpdateSavedTestStatus: new saved_test === end");
       				break;
       			}
       			i++;
       		}
       		String expected = time+"";
       		assertEquals(expected, actual);
       }
       
       /**
        * Vector loadTestList(UserInfo user_info)
        * <test_id>-735976319087141914</test_id>
   		<test_date>1249668205974</test_date>
   		<test_name>testie</test_name>
   		<test_type>reading</test_type>
   		<test_status>pending</test_status>
   		<test_format>DailyTest</test_format>
   		<creation_time>1249668205974</creation_time>
        * @param tests_list
        */
       public void testTestingType()
       {
    	   
       		//System.out.println("FileSaveTestsTest.testTetingType __________");
       		String user_id = new String("test_user");
       		String subject = new String("vocab");
       		String encoding = new String("UTF-8");
       		File path_file = new File("");
       		String root_path = path_file.getAbsolutePath();
       		UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
       		String test_id = "063";
       		String actual = null;
       		String writing_word_id = ("5882280617916499656");
      		String expected = Constants.WRITING;
       		FileSaveTests fst = new FileSaveTests();
       		Hashtable test_words = fst.load(user_info, test_id);
       		//System.out.println("fst log __________");
       		//dumpLog(fst.getLog());
       		//System.out.println("fst log __________");
       		//dumpTestWordHash(test_words, fst);
       		
       		try
    		{
    			Enumeration keys = test_words.keys();
    			while (keys.hasMoreElements())
    			{
    				String key = (String)keys.nextElement();
    				Word word = (Word)test_words.get(key);
    				String testing_type = fst.getTestTypeFromSavedWord(word);
    				if (word.getDefinition().equals("def2"))
    				{
    					actual = testing_type;
    					//System.out.println("type found : "+actual);
    					break;
    				}
    			}
    		} catch (java.lang.NullPointerException npe)
    		{
    			
    		}
    		
  		assertEquals(expected, actual);
  }
       
       /*
       public void testTestingType2()
       {
    	   
       		System.out.println("FileSaveTestsTest.testTetingType2 __________");
       		String user_id = new String("-5519451928541341468");
       		String category = "october1";
       		String subject = new String("vocab");
	    String second_word_id = "-2672985807382824350";
	    String encoding = new String("UTF-8");
   		File path_file = new File("");
   		String root_path = path_file.getAbsolutePath();
   		UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
   		String test_id = "-1";
   		String actual = null;
  		String expected = Constants.WRITING;
   		FileSaveTests fst = new FileSaveTests();
   		Hashtable test_words = fst.load(user_info, test_id);
   		//System.out.println("fst log __________");
   		//dumpLog(fst.getLog());
   		//System.out.println("fst log __________");
   		//dumpTestWordHash(test_words, fst);
   		
   		try
		{
			Enumeration keys = test_words.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				Word word = (Word)test_words.get(key);
				String testing_type = fst.getTestTypeFromSavedWord(word);
				System.out.println(word.getDefinition()+" type "+testing_type);
				dumpLog(Transformer.createTable(word));
				System.out.println("Test [0] info ================================= ");
				System.out.println("grade "+fst.getGradeFromTest(word));
				System.out.println("new level "+fst.getNewLevelFromTest(word));
				System.out.println("type "+fst.getTestTypeFromSavedWord(word));
		    	System.out.println("wntd key "+fst.getWNTDFileKeyFromSavedWord(word));
		    	System.out.println("Test [0] info ================================= end");
				if (word.getDefinition().equals("recommend"))
				{
					actual = testing_type;
					System.out.println("type found : "+actual);
					break;
				}
			}
		} catch (java.lang.NullPointerException npe)
		{
			
		}
		
		assertEquals(expected, actual);
}
       */
       
       /**
   	 * Load the saved tests list for a particular user.
   	 * For the primary user this should be:
   	 * 
   	  	<saved_test>
   		<test_id>-1</test_id>
   		<test_date>1275197364509</test_date>
   		<test_name>omg</test_name>
   		<test_type>reading_and_writing</test_type>
   		<test_status>pending</test_status>
   		<test_format>IntegratedTest</test_format>
   		<creation_time>1275197364509</creation_time>
   		</saved_test>
   	 */
   	public void testLoadSavedTests()
   	{
   		File path_file = new File("");
   	    String current_dir = path_file.getAbsolutePath();
   	    String user_id = new String("-5519451928541341468");
   	    String subject = Constants.VOCAB;
   	    String encoding = new String("euc-kr");
   	    UserInfo user_info = new UserInfo(encoding, current_dir, user_id, subject);
   	    // load test words
   	    FileSaveTests fst = new FileSaveTests();
   	    Vector saved_tests = fst.loadTestList(user_info);
   	    int size = saved_tests.size();
   	     // System.out.println("FileSaveTestsTest.testLoadSavedTests: saved tests "+size+" --------- ");
   	    //dumpSavedTestsList(saved_tests);
   	    //dumpLog(fst.getLog());
   	    
   	    String actual_test_id = null;
   	    try
   	    {
   	    	SavedTest saved_test = (SavedTest)saved_tests.get(0);
   	    	actual_test_id = saved_test.getTestId();
   	    } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
   	    {
   	    	//System.out.println("FileSaveTestsTest.testLoadSavedTests: aioobe");
   	    }
   	    //System.out.println("actual "+actual_test_id);
   	    String expected_test_id = "-1279361629089452921";
   	    //System.out.println("FileSaveTestsTest.testLoadSavedTests: saved tests ----------------- log");
   	    assertEquals(expected_test_id, actual_test_id);	
   	}
   	
   	/**
     * addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
     */
     public void testGetTestTypeFromSavedWord()
     {
 	    Word word1 = new Word();
	    word1.setText("READING text1");
	    word1.setDefinition("def1");
	    word1.setId(Long.parseLong("1111111111111111111"));
	    word1.setCategory("some file1.xml");
	    Test[] tests1 = new Test[1];
	    Test test1 = new Test();
	    test1.setType(Constants.READING);
	    tests1[0] = test1;
	    word1.setTests(tests1);
	    String actual_type = FileSaveTests.getTestTypeFromSavedWord(word1);
 	    String expected_type = Constants.READING;
 	    assertEquals(expected_type, actual_type);
     }
     
       /**
      * Testing void saveClassTest(String test_id, String test_name, String teacher_id, 
			String creation_time, String encoding, String path)
			Save info about tests that were added to each student in a class.
      */
      public void testSavedClassTest()
      {
    	  
  	    String teacher_id = new String("0000000000000000001");
  	    String subject = new String("vocab");
  	    String encoding = new String("euc-kr");
  	    //String encoding = new String("UTF-8");
  	    File path_file = new File("");
  	    String root_path = path_file.getAbsolutePath();
  	    String str_id = Domartin.getNewID()+"";
  	    //System.out.println("FileSaveTestsTest.testSaveIndexAndType");
  	    FileSaveTests f_save_tests = new FileSaveTests();
  	    String today_now = new Date().getTime()+"";
  	    //SavedTest saved_test = new SavedTest();
  		//saved_test.setCreationTime(today_now);
  		String test_id = str_id; 
  		String test_name = "test";
  		String creation_time = today_now; 
  		String class_id = "fake_class_id";
  		UserInfo user_info = new UserInfo(encoding, root_path, teacher_id, subject);
  	    f_save_tests.saveClassTest(class_id, test_id, test_name, creation_time, user_info);
  	    // load the test list.
  	    Vector saved_class_tests = f_save_tests.loadClassTestList(user_info);

  	    // find the test again:
  	    SavedTest saved_test = null;
  	    int i=0;
  	    int size = saved_class_tests.size();
  	    while (i<size)
  	    {
  	    	saved_test = (SavedTest)saved_class_tests.get(i);
  	    	if (saved_test.getTestId().equals(test_id))
  	    	{
  	    		//System.out.println("loaded test "+test_id+" - "+saved_test.getTestName());
  	    		break;
  	    	}
  	    	i++;
  	    }
  	    //System.out.println("FileSaveTestsTest.testSavedClassTest saved_class_tests "+saved_class_tests.size());
  	    //dumpSavedTestsList(saved_class_tests);
  	    //System.out.println("log ----------- start");
  	    //dumpLog(f_save_tests.getLog());
  	    //System.out.println("log ----------- end");
  	    saved_test = (SavedTest)saved_class_tests.get(0);
  	    //String actual_date = saved_test.getCreationTime();
  	    //String expected_date = today_now;
  	    String expected_class_id = class_id;
  	    String actual_class_id = f_save_tests.getClassId();
  	    // cleanup
  	    boolean deleted = f_save_tests.deleteSavedClassTestFromList(user_info, test_id);
  	    //System.out.println("cleaned up? "+deleted);
  	    //assertEquals(expected_date, actual_date);
  	    assertEquals(expected_class_id, actual_class_id);
      }
      
      /** tHIS is our formula:
       * passed_words_param_map.size()/test_words.size()*100
       * We finally found out that you have to use doubles for everything.
       */
      public void testScoreResult()
      {
    	  double passed_words = 5;
    	  double test_words = 10;
    	  //System.out.println("testScoreResult: "+passed_words+" / "+test_words);
    	  double div = passed_words / test_words;
    	  //System.out.println("testScoreResult: = "+div);
    	  double fl_score = (div*100); 
    	  DecimalFormat df = new DecimalFormat("##");
  		  String actual = df.format(fl_score);
  		  String expected = "50";
  		  //ystem.out.println("testScoreResult: actual "+actual);
  		  assertEquals(expected, actual);
      }
      
      public void testLoadClassTestStatus()
      {
    	  String subject = new String("vocab");
    	  String encoding = new String("euc-kr");
    	  File path_file = new File("");
    	  String root_path = path_file.getAbsolutePath();
    	  FileSaveTests f_save_tests = new FileSaveTests();
    	  String today_now = new Date().getTime()+"";
    	  String teacher_id = new String("0000000000000000001");
    	  String test_id = "-638067619651304653"; 
    	  String test_name = "test";
    	  String creation_time = today_now; 
    	  String class_id = "0000000000000000001";
    	  UserInfo user_info = new UserInfo(encoding, root_path, teacher_id, subject);
    	  FileUserUtilities fut = new FileUserUtilities(root_path);
    	  Vector student_ids = fut.getStudentIds(teacher_id, class_id);
    	  //System.out.println("testLoadClassTestStatus() student_ids "+student_ids.size());
    	  Hashtable class_tests = f_save_tests.loadClassTestStatus(user_info, test_id, student_ids);
    	  //System.out.println("log 0000000000000000");
    	  //dumpLog(f_save_tests.getLog());
    	  //System.out.println("log 0000000000000000 end");
    	  SavedTest saved_test = (SavedTest)class_tests.get("-5760154295424797770");
    	  String actual = saved_test.getTestId();
  		  String expected = test_id;
  		  //System.out.println("testLoadClassTestStatus() class tests "+class_tests.size());
  		  //System.out.println("log 0000000000000000 dump class_tests");
  		  //dumpSavedClassTests(class_tests);
  		  //System.out.println("log 0000000000000000 end dump"); 
		  assertEquals(expected, actual);
      }
   	
      /**
       * Testing add separateLCDTests (lowest common denomenator) for a whole class.
       * Producces this output: <saved_test><test_id>2836020544745840863</test_id><test_date>1379213013328</test_date><test_name>Hell ain't a bad place to be._reading</test_name><test_type>reading</test_type><test_status>pending</test_status><test_format>CardGame</test_format><creation_time>1379213013328</creation_time><score_time>pending</score_time><number_of_words>6</number_of_words></saved_test><saved_test><test_id>6008921350576488801</test_id><test_date>1379213013912</test_date><test_name>Hell ain't a bad place to be._writing</test_name><test_type>writing</test_type><test_status>pending</test_status><test_format>CardGame</test_format><creation_time>1379213013912</creation_time><score_time>pending</score_time><number_of_words>6</number_of_words></saved_test><saved_test><test_id>6026051993789241091</test_id><test_date>1379213014637</test_date><test_name>Hell ain't a bad place to be.</test_name><test_type>separate</test_type><test_status>pending</test_status><test_format>IntegratedTest</test_format><creation_time>1379213014637</creation_time><score_time>pending</score_time><number_of_words>6</number_of_words></saved_test>
       *
      public void testSeparateLCDTestsAKATestHell()
      {
    	  String test_name = "Hell ain't a bad place to be.";
    	  String subject = new String("vocab");
    	  String encoding = new String("euc-kr");
    	  File path_file = new File("");
    	  String context_path = path_file.getAbsolutePath();
    	  FileSaveTests f_save_tests = new FileSaveTests();
    	  String class_id = "0000000000000000001";
    	  String teacher_id = new String("0000000000000000001");
    	  String test_id = Domartin.getNewID()+""; // test_ids should all be the same
    	  String test_type = "separate";
    	  String user1_id = "-5519451928541341468";
    	  String user2_id = "-5760154295424797770";
    	  FileUserOptions fuo = new FileUserOptions(context_path);
    	  fuo.createNewLists(user1_id, subject, context_path);	
    	  fuo.createNewLists(user2_id, subject, context_path);
    	  FileUserUtilities fut = new FileUserUtilities(context_path);
    	  Hashtable students = fut.getStudents(teacher_id, class_id); // returns stuydent ids-name pairs
    	  Storage store = new FileStorage(context_path);
  		  Hashtable teacher_opts = store.getTeacherOptions(teacher_id, context_path);
  		  String reading_id = Domartin.getNewID()+"";
		  String writing_id = Domartin.getNewID()+"";
		  String number_of_words = "";
    	  for (Enumeration e = students.keys() ; e.hasMoreElements() ;) 
    	  {
    		  String student_id = (String)e.nextElement();
  			  String student_name = (String)students.get(student_id);
  			  Hashtable student_names = new Hashtable();
  			  student_names.put(student_id, student_name); // create a test for the student and save it, maybe return the number of words?
  			  String action_time = Long.toString(new Date().getTime());
    	      separateLCDTests(students, student_id, encoding, test_name, teacher_id, class_id, 
					teacher_opts, Constants.READING, reading_id, context_path);    	      
			  separateLCDTests(students, student_id, encoding, test_name, teacher_id, class_id, 
					teacher_opts,  Constants.WRITING, writing_id, context_path);
    	      WordNextTestDates wntds = new WordNextTestDates();
    	      Hashtable student_test_totals = new Hashtable(); // this will hold student ids - number of words in their test
			  Hashtable student_test_ids = new Hashtable(); 
		      number_of_words = wntds.getLowestCommonDenomenator(students, context_path, subject, Constants.READING)+"";
			  Vector test_words = wntds.getTestWords(student_id, context_path, action_time, subject, test_type, number_of_words);
			  UserInfo user_info = new UserInfo(encoding, context_path, student_id, subject);
			  f_save_tests.save(user_info, test_words, test_id); // save the test words
			  student_test_totals.put(student_id, Integer.toString(test_words.size()));
			  student_test_ids.put(student_id, test_id);
			  SavedTest  saved_test = createSavedTest(test_type, test_name, test_id, "IntegratedTest", number_of_words);
			  f_save_tests.addToSavedTestsList(user_info, saved_test, test_id); // create the entry & load all the saved tests and set them in the session so the jsp can display them
    	  }
  		  String expected = number_of_words;
  		  UserInfo user_info = new UserInfo("euc-kr", context_path, teacher_id, subject);
  		  Vector student_ids = fut.getStudentIds(teacher_id, class_id);
  		  Hashtable class_tests = f_save_tests.loadClassTestStatus(user_info, test_id, student_ids);
  		  SavedTest saved_test = (SavedTest)class_tests.get("-5519451928541341468");
  		  String actual = saved_test.getNumberOfWords();
		  assertEquals(expected, actual);
      }*/
      
      /**
       * Testing addDeckCardAssociations(UserInfo user_info, String test_id, Hashtable word_id_card_names, Hashtable word_id_types)
       */
      public void testLoadDeckCardAssociations()
      {
    	  
  	    String user_id = new String("-5519451928541341468");
  	    String subject = new String("vocab");
  	    String encoding = new String("euc-kr");
  	    File path_file = new File("");
  	    String root_path = path_file.getAbsolutePath();
  	    //System.out.println("FileSaveTestsTest.testAddDeckCardAssociations");
  	    FileSaveTests f_save_tests = new FileSaveTests();
  		String test_id = "-1111111111111111111"; 
  		String house_deck_name = "green";
  		String house_deck_id = test_id;
  		UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
  		Hashtable word_id_reading_deck_card_names = setupWordIdReadingDeckCardNames();
  		Hashtable word_id_writing_deck_card_names = setupWordIdWritingDeckCardNames();
  		try
  		{
  			f_save_tests.addDeckCardAssociations(user_info, test_id, word_id_reading_deck_card_names, word_id_writing_deck_card_names,
  					house_deck_name, house_deck_id);
  		} catch (java.lang.NullPointerException npe)
  		{
  			System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: npe");
  			npe.printStackTrace();
  			dumpLog(f_save_tests.getLog());
  		}
  		// load test and check results
  		Hashtable test_words = f_save_tests.load(user_info, test_id);
  		//System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: house_deck_name "+f_save_tests.getHouseDeckName());
  		//System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: house_deck_id "+f_save_tests.getHouseDeckId());
  		String expected_deck_card_name = "R1";
  		String actual = "none";
  	    Enumeration e = test_words.keys();
  	    while (e.hasMoreElements())
  	    {
  	    	String key = (String)e.nextElement();
  	    	Word test_word = (Word)test_words.get(key);
  	    	//System.out.println("test_word "+test_word.getDefinition());
  	    	Test test = null;
  	    	try
  	    	{
  	    		test = test_word.getTests(0);
  	    		String testing_type = test.getType();
  	    		String wntd_file_key = test.getDate();
  	    		String grade = test.getGrade();
  	    		String reading_deck_card_name = test.getName();
  	    		String writing_deck_card_name = test.getLevel();
  	    		//System.out.println("testing_type 			"+testing_type);
  	    		//System.out.println("wntd_file_key 			"+wntd_file_key);
  	    		//System.out.println("grade 					"+grade);
  	    		//System.out.println("reading_deck_card_name 	"+reading_deck_card_name);
  	    		////System.out.println("writing_deck_card_name 	"+writing_deck_card_name);
  	    		actual = reading_deck_card_name;
  	    	} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
  	    	{
  	    		//System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: aioobe");
  	    		aioobe.printStackTrace();
  	    	}
  	    }	
  	    assertEquals(expected_deck_card_name, actual);
      }
      
      public void testGetHouseDeckName()
      {
    	  
  	    String user_id = new String("-5519451928541341468");
  	    String subject = new String("vocab");
  	    String encoding = new String("euc-kr");
  	    File path_file = new File("");
  	    String root_path = path_file.getAbsolutePath();
  	    //System.out.println("FileSaveTestsTest.testAddDeckCardAssociations");
  	    FileSaveTests f_save_tests = new FileSaveTests();
  		String test_id = "-1111111111111111111"; 
  		String house_deck_name = "green";
  		String house_deck_id = test_id;
  		UserInfo user_info = new UserInfo(encoding, root_path, user_id, subject);
  		// load test and check results
  		Hashtable test_words = f_save_tests.load(user_info, test_id);
  		//System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: house_deck_name "+f_save_tests.getHouseDeckName());
  		//System.out.println("FileSaveTestsTest.testAddDeckCardAssociations: house_deck_id "+f_save_tests.getHouseDeckId());
  		String expected = house_deck_name;
  		String actual = f_save_tests.getHouseDeckName();
  	    assertEquals(expected, actual);
      }
      
      /**
       * Saving this info:
       * 
       *<house_deck>
 *         <deck_id/>
 *         <player_id/>
 *         <game_id/>
 *         <name>¡±Player A¡±</name>
 *         <size>24</size>
 *             <card>
 *                 <id/>
 *                 <name/>
 *                 <index/>
 *                 <type/>
 *            </card>
 *             ...
 *    </house_deck>
       */
      public void testSaveAndLoadHouseDecks()
      {
    	  String class_id = "0000000000000000001";
    	  String teacher_id = new String("0000000000000000001");  
  	      String user_id = new String("-5519451928541341468");
  	      String subject = new String("vocab");
  	      String encoding = new String("euc-kr");
  	      File path_file = new File("");
  	      String root_path = path_file.getAbsolutePath();
  	      System.out.println("FileSaveTestsTest.testSaveHouseDecks");
  	      FileSaveTests f_save_tests = new FileSaveTests();
  	      String device_id = "test_device_name";
  	      Vector house_decks = new Vector();
  	      HouseDeck house_deck = new HouseDeck();
  	    String test_id = "-1111111111111111111"; 
  		String house_deck_name = "green";
  		String house_deck_id = test_id;
  		house_deck.setDeckName(house_deck_name);
  		house_deck.setDeckId(house_deck_id);
  		house_deck.setPlayerId(user_id);
  		house_deck.setGameId(test_id);
  		DeckCard deck_card = new DeckCard();
  		deck_card.setCardId(test_id);
  		deck_card.setCardName("R1");
  		deck_card.setIndex(1);
  		deck_card.setStatus("testing");
  		deck_card.setType(Constants.READING);
  		Hashtable deck_cards = new Hashtable ();
  		deck_cards.put(test_id, deck_card);
  		house_deck.setCards(deck_cards);
  		house_decks.add(house_deck);
  		try
  		{
  			f_save_tests.saveHouseDecks(teacher_id, root_path, device_id, house_decks);
  		} catch (java.lang.Exception e)
  		{
  			System.out.println("FileSaveTestsTest.testSaveHouseDecks: e");
  			e.printStackTrace();
  			dumpLog(f_save_tests.getLog());
  		}
  		Vector loaded_house_decks = f_save_tests.loadHouseDecks(teacher_id, root_path, device_id);
  		HouseDeck actual_house_deck = (HouseDeck) loaded_house_decks.get(0);
  		String actual = actual_house_deck.getDeckName();
  		dumpLog(f_save_tests.getLog());
  		String expected = house_deck_name;
  	    assertEquals(expected, actual);
      }
      
      /*
      public void testHouseDecksRemoteCall()
      {
    	  String class_id = "0000000000000000001";
    	  String teacher_id = new String("0000000000000000001");  
  	      String user_id = new String("-5519451928541341468");
  	      String subject = new String("vocab");
  	      String encoding = new String("euc-kr");
  	      File path_file = new File("");
  	      String root_path = path_file.getAbsolutePath();
  	      System.out.println("FileSaveTestsTest.testSaveHouseDecks");
  	      FileSaveTests f_save_tests = new FileSaveTests();
  	      String device_id = "test_device_name";
  	      Vector house_decks = new Vector();
  	      HouseDeck house_deck = setupHouseDeck(user_id);
  	      house_decks.add(house_deck);
  	      house_deck = setupHouseDeck(user_id);
  	      house_decks.add(house_deck);
  	      house_deck = setupHouseDeck(user_id);
  	      house_decks.add(house_deck);
  	      try
  	      {	
  	    	  f_save_tests.saveHouseDecks(teacher_id, root_path, device_id, house_decks);
  	      } catch (java.lang.Exception e)
  	      {
  	    	  System.out.println("FileSaveTestsTest.testSaveHouseDecks: e");
  	    	  e.printStackTrace();
  	    	  dumpLog(f_save_tests.getLog());
  	      }
  	      Vector loaded_house_decks = f_save_tests.loadHouseDecks(teacher_id, root_path, device_id);
  	      HouseDeck actual_house_deck = (HouseDeck) loaded_house_decks.get(0);
  	      String actual = actual_house_deck.getDeckName();
  	      dumpLog(f_save_tests.getLog());
  	      String expected = house_deck_name;
  	      assertEquals(expected, actual);
      }
      */
      
      private HouseDeck setupHouseDeck(String user_id)
      {
    	  HouseDeck house_deck = new HouseDeck();
    	    String test_id = "-1111111111111111111"; 
    	    Date date = new Date();
    	    String time = date.toString();
    		String house_deck_name = time;
    		String house_deck_id = date.getTime()+"";
    		house_deck.setDeckName(house_deck_name);
    		house_deck.setDeckId(house_deck_id);
    		house_deck.setPlayerId(user_id);
    		house_deck.setGameId(test_id);
    		DeckCard deck_card = new DeckCard();
    		deck_card.setCardId(test_id);
    		deck_card.setCardName("R1");
    		deck_card.setIndex(1);
    		deck_card.setStatus("testing");
    		deck_card.setType(Constants.READING);
    		Hashtable deck_cards = new Hashtable ();
    		deck_cards.put(test_id, deck_card);
    		house_deck.setCards(deck_cards);
    		return house_deck;
      }
      
      /**
       * Support method for testAddDeckCardAssociations
       * @return
       */
      private Hashtable setupWordIdReadingDeckCardNames()
      {
    	  Hashtable word_id_reading_deck_card_names = new Hashtable();
    	  word_id_reading_deck_card_names.put("-974372450394015960", "R1");
    	  word_id_reading_deck_card_names.put("-7747983981330287976", "R2");
    	  word_id_reading_deck_card_names.put("-2390231914073158751", "R3");
    	  return word_id_reading_deck_card_names;
      }
      
      /**
       * Support method for testAddDeckCardAssociations
       * @return
       */
      private Hashtable setupWordIdWritingDeckCardNames()
      {
    	  Hashtable word_id_writing_deck_card_names = new Hashtable();
    	  word_id_writing_deck_card_names.put("-974372450394015960", "W3");
    	  word_id_writing_deck_card_names.put("-7747983981330287976", "W2");
    	  word_id_writing_deck_card_names.put("-2390231914073158751", "W1");
    	  return word_id_writing_deck_card_names;
      }
    	  
    	  
      private String separateLCDTests(Hashtable students, String student_id, 
    			  String encoding, String test_name, 
    				String teacher_id, String class_id,
    				Hashtable teacher_opts, String type, String test_id,
    				String context_path)
    		{
    			Hashtable student_test_totals = new Hashtable(); // this will hold student ids - number of words in their test
    			Hashtable student_test_ids = new Hashtable(); // this will hold ids - test_id pairs.
    			Hashtable student_names = new Hashtable();

    			String subject = Constants.VOCAB;
    			String action_time = Long.toString(new Date().getTime());
    			WordNextTestDates wntds = new WordNextTestDates();
    			int number_of_words = wntds.getLowestCommonDenomenator(students, context_path, subject, type);
    			Vector test_words = new Vector();
    			try
    			{
    				test_words = wntds.getTestWords(student_id, context_path, action_time, subject, type, number_of_words+"");
    				//System.out.println("wntds.getTestWords wntds log -=- ");
    				//dumpLog(wntds.getLog());
    			} catch (java.lang.NullPointerException npe)
    			{
    				//System.out.println("wntds.getTestWords NPE ");
    				//dumpLog(wntds.getLog());
    			}
    			FileSaveTests f_save_tests = new FileSaveTests();
    			UserInfo user_info = new UserInfo(encoding, context_path, student_id, subject);
    			f_save_tests.save(user_info, test_words, test_id); // save the test words
    			SavedTest saved_test = createSavedTest(type, test_name+"_"+type, test_id, "CardGame", number_of_words+"");
    			student_test_totals.put(student_id, Integer.toString(test_words.size()));
    			student_test_ids.put(student_id, test_id);
    			f_save_tests.addToSavedTestsList(user_info, saved_test, test_id);
    			return test_id;
    		}
   	
    	  private SavedTest createSavedTest(String test_type, String test_name, String test_id, 
    				String test_format, String number_of_words)
    		{
    	 	    String today_now = new Date().getTime()+"";
    			SavedTest saved_test = new SavedTest();
    			saved_test.setTestId(test_id);
    			saved_test.setTestDate(today_now);
    			saved_test.setTestName(test_name);
    			saved_test.setTestType(test_type);
    			saved_test.setTestStatus("pending");
    			saved_test.setTestFormat(test_format);
    			saved_test.setCreationTime(today_now);
    			saved_test.setNumberOfWords(number_of_words);
    			return saved_test;
    		}
    
    private void dumpSavedTestsList(Vector tests_list)
    {
	    int i = 0;
	    while (i<tests_list.size())
	    {
		    SavedTest saved_test = (SavedTest)tests_list.get(i);
		    System.out.println("------------ "+i);
		    dumpLog(Transformer.createTable(saved_test));
		    System.out.println("------------ "+i);
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
    
    private void dumpWords(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    Word word = (Word)log.get(key);
		    Test test0 = word.getTests(0);
		    System.out.println(key+"-----"+test0.getType());
		    dumpLog(Transformer.createTable(word));
	    }
    }
    
    private void dumpSavedClassTests(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    SavedTest saved_test = (SavedTest)log.get(key);
		    System.out.println(key+" saved test ----- "+saved_test.getTestName());
		    dumpLog(Transformer.createTable(saved_test));
	    }
    }


    private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

    }
    
    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(FileEditTest.class);
    }
    
    private void dumpTestWordHash(Hashtable hash, FileSaveTests fst)
	{
		try
		{
			Enumeration keys = hash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				Word word = (Word)hash.get(key);
				System.out.println("this word ===================================== ");
				dumpLog(Transformer.createTable(word));
				System.out.println("Test [0] info ================================= ");
				System.out.println("grade "+fst.getGradeFromTest(word));
				System.out.println("new level "+fst.getNewLevelFromTest(word));
				System.out.println("type "+fst.getTestTypeFromSavedWord(word));
		    	System.out.println("wntd key "+fst.getWNTDFileKeyFromSavedWord(word));
		    	System.out.println("Test [0] info ================================= end");
			}
		} catch (java.lang.NullPointerException npe)
		{
			
		}
	}

}
