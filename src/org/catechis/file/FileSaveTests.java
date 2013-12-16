package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.EncodeString;
import org.catechis.FileStorage;
import org.catechis.JDOMSolution;
import org.catechis.Transformer;
import org.catechis.admin.FileUserOptions;
import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;
import org.catechis.dto.SavedTest;
import org.catechis.dto.SavedTestResult;
import org.catechis.dto.Test;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.dto.WordTestRecordOptions;
import org.catechis.gwangali.RateOfTesting;
import org.catechis.interfaces.SaveTests;
import org.catechis.juksong.FarmingTools;
import org.catechis.wherewithal.DeckCard;
import org.catechis.wherewithal.HouseDeck;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
*This class was made to create file specific methods for the SaveTests interface.
** Using a normal word object provides no place for a list of words that are tested
* outside the system, and then managed with this object.  So the test index for each word
* is put in the reading level.
*<p>We use the test[0] Test object to hold additional info about the saved tests:
* type = testing type, as each individual word in a saved test can have a different type.
* date = WordNExtTestDate file key, a link to the file that the test was chosen from and needs to be updated after the test.
* grade = pass or fail after the test has been scored.
* level = new level after the test has been scored.  depending on the type above, could be reading_level or writing_level.
*<p>This object is not threadsafe. 
*/
public class FileSaveTests implements SaveTests
{
	
	private Vector log;
	private Document doc;
	private String file_name;
	
	/**
	 * This variable is used when loading a saved test profile for a teacher.
	 * Instead of putting the variable in an unused slot of a SavedTest bean,
	 * the calling method (SavedClassTestsAction) can get this method after 
	 * calling the loadClassTests method.
	 */
	private String class_id;
	
	/**
	 * This is the name of a house deck that is set after a user on the Android app
	 * associates a saved test with a particular house deck.
	 */
	private String house_deck_name;
	private String house_deck_id;
	
	/**
	 * This variable is used when loading a saved test profile for a teacher.
	 * Instead of putting the variable in an unused slot of a SavedTest bean,
	 * the calling method (SavedClassTestsAction) can get this method after 
	 * calling the loadClassTests method.
	 */
	public String getClassId()
	{
		return class_id;
	}
	
	public FileSaveTests()
	{
		// no arguments here...
		log = new Vector();
		log.add("FileSaveTests<init>.log -------------- start me up");
	}
	
	public FileSaveTests(String _file_name)
	{
		log.add("FileSaveTests<init>.log -------------- start me up");
		file_name = _file_name;
		File file = new File(file_name);
		EncodeString encoder = new EncodeString();
		doc = null;
		log = new Vector();
		try
		{
			encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add("FileEdit<init> "+j.toString());
		} catch (java.io.IOException i)
		{
			log.add("FileEdit<init> "+i.toString());
		}
	}
		    
	/**
	 * Save a list of test words that can be stored for later scoring.
	 * The first test set in each word contains:
	 * 1. the type of test in the type member.
	 * 2. the <wntd_file_key> reference in the date member.
	 * The test words are coming from CreateIntegratedTestAction which calls on
	 * test_words = wntds.getTestWords(user_id, context_path, action_time, subject, test_type, number_of_words);
	 */
	public void save(UserInfo user_info, Vector words, String test_id)
	{
		//String type = wtm.getType();
		String save_file_name = (test_id+".xml");
		String saved_tests_path = new String(user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS
			+File.separator+Constants.SAVED
			+File.separator+save_file_name);
		File file = new File(saved_tests_path);
		Element root = new Element("test_words");
		doc = new Document(root);
		int size = words.size();
		int i = 0;
		while (i<size)
		{
			Word word = (Word)words.get(i);
			Element e = createTestWordElementWithType(word, i); 
			try
			{
				root.addContent(e);
			} catch (java.lang.NullPointerException npe)
			{
			
			}
			i++;
		}
		
		try
		{
			log.add("FileSavedTests.save: path "+saved_tests_path);
		} catch (java.lang.NullPointerException npe)
		{
			//log.add("FileSavedTests.save: path "+user_info.getRootPath());
			//log.add("FileSavedTests.save: id   "+user_info.getUserId());
		}
		writeDocument(saved_tests_path, user_info.getEncoding());
	}
	
	/**
	 * Save info about a test for an entire class.
	 * files/admin/teachers/teacher_id/saved tests.xml
	 */
	public void saveClassTest(String class_id, String test_id, String test_name,
			String creation_time, UserInfo user_info)
	{
		log.add("FileSavedTests.saveClassTest class_id "+class_id);
		String save_file_name = (test_id+".xml");
		String teacher_folder = user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+Constants.ADMIN
			+File.separator+Constants.TEACHERS
			+File.separator+user_info.getUserId();
		String saved_tests_path = teacher_folder+File.separator+"saved tests.xml";
		File saved_tests_file = new File(saved_tests_path);
		boolean exists = saved_tests_file.exists();
		if (!exists)
		{
			log.add("FileSavedTests.saveClassTest: no file found at "+saved_tests_path);
			// if the file doesn't exist yet.
			FileUserUtilities fut = new FileUserUtilities(user_info.getRootPath());
			fut.createSavedTestsFile(teacher_folder);
			append(fut.getLog());
		}
		loadFile(saved_tests_path);
		Element root = doc.getRootElement();
		Element saved_test_element = bindSavedClassTest(class_id, test_id, test_name, creation_time);
		root.addContent(saved_test_element);
		writeDocument(saved_tests_path, user_info.getEncoding());
	}
	
	/**
	*	<test_word>
	*		<text></text>			so that we dont have to load every word
	*		<index></index>
	*		<word_id></word_id>
	*		<word_category/>
	*		<category></category>
	*		<definition></definition>	when unbinding a test. 
	*		<testing_type> 					kept in test[9].type
	*		<wntd_file_key></wntd_file_key> kept in test[0].date	
	*		<deck_card_name>R1 				kept is test[0].name
	*		<deck_card_word_type>reading 	kept in test[0].level
	**		<grade>							kept in test[0].grade.  We're assuming this is used in scoring funtions.
	*	</test_word>
	*/
	private Element createTestWordElementWithType(Word word, int word_index)
	{
		// create main element
	    Element test_word = new Element("test_word");
	    	// create sub-elements
	    Element text = new Element("text");
	    Element index = new Element("index");
	    Element word_id = new Element("word_id");
	    Element category = new Element("category");
	    Element definition = new Element("definition");
	    Element testing_type = new Element("testing_type");
	    Element wntd_file_key = new Element("wntd_file_key");
	    Element reading_deck_card_name = new Element("reading_deck_card_name");
	    Element writing_deck_card_name = new Element("writing_deck_card_name");
	    	// add content to sub-elements
	    text.addContent(word.getText());
	    index.addContent(Integer.toString(word_index));
	    word_id.addContent(Long.toString(word.getId()));
	    category.addContent(word.getCategory());
	    definition.addContent(word.getDefinition());
	    reading_deck_card_name = addReadingDeckCardName(reading_deck_card_name, word);
	    writing_deck_card_name = addWritingDeckCardName(writing_deck_card_name, word);
	    Test test = new Test();
	    try
	    {
	    	test = word.getTests(0);
	    	testing_type.addContent(test.getType());
	    	wntd_file_key.addContent(test.getDate());
	    } catch (java.lang.NullPointerException npe)
	    {
	    	// this might happen if the user only chooses one type, not both
	    	// in which case these values are not needed.  Actually not sure about the wntd file key
	    }
	    log.add("FileSaveTests.createTestWordElementWithType: type "+test.getType()+" word "+word.getText());
	    	// add sub-elements to main element
	    test_word.addContent(text);
	    test_word.addContent(index);
	    test_word.addContent(word_id);
	    test_word.addContent(category);
	    test_word.addContent(definition);
	    test_word.addContent(testing_type);
	    test_word.addContent(wntd_file_key);
	    test_word.addContent(reading_deck_card_name);
	    test_word.addContent(writing_deck_card_name);
	    return test_word;
	}
	
	/**
	 * The writing_deck_card_name is kept in the test[0].name field.
	 * @param reading_deck_card_name
	 * @param word
	 * @return
	 */
	private Element addReadingDeckCardName(Element reading_deck_card_name, Word word)
	{
		String reading_deck_card_name_string = null;
		Test test = new Test();
	    try
	    {
	    	test = word.getTests(0);
	    	reading_deck_card_name_string = test.getName();
	    	reading_deck_card_name.addContent(reading_deck_card_name_string);
	    } catch (java.lang.NullPointerException npe)
	    {
	    	log.add("FileSavedTests.addDeckCardName: npe getting/setting deck card name");
	    }
	    return reading_deck_card_name;
	}
	
	/**
	 * The writing deck card word type is kept in the test[0]/level.
	 * @param writing_deck_card_name
	 * @param word
	 * @return
	 */
	private Element addWritingDeckCardName(Element writing_deck_card_name, Word word)
	{
		String writing_deck_card_name_string = null;
		Test test = new Test();
	    try
	    {
	    	test = word.getTests(0);
	    	writing_deck_card_name_string = test.getLevel();
	    	writing_deck_card_name.addContent(writing_deck_card_name_string);
	    } catch (java.lang.NullPointerException npe)
	    {
	    	log.add("FileSavedTests.addDeckCardWordType: npe getting/setting deck card word type");
	    }
	    return writing_deck_card_name;
	}
	
	/**
	 * Create an entry in the saved tests.xml file containing info about a list of words saved for a test at a later date.
	 * The str_id is the same as test_id element in the saved_tests object, we're guessing.
	 */
	public void addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id)
	{
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS);
		String saved_files_path = (tests_folder_path+File.separator+"saved tests.xml");
		File saved_files_folder = new File(saved_files_path);
		boolean exists = saved_files_folder.exists();
		if (!exists)
		{
			log.add("FileSavedTests.addToSavedTestsList: no file found at "+saved_files_path);
			// if the file doesn't exist yet.
			FileUserUtilities fut = new FileUserUtilities(user_info.getRootPath());
			fut.createSavedTestsFile(tests_folder_path);
			append(fut.getLog());
		}
		loadFile(saved_files_path);
		Element root = doc.getRootElement();
		Element saved_test_element = bindSavedTest(saved_test, str_id);
		root.addContent(saved_test_element);
		writeDocument(saved_files_path, user_info.getEncoding());
	}
	
	/**
	*
		       test_id;
	private String test_date;
	private String test_name;
	private String test_type;
	private String test_status;
	private String test_format;
	private String creation_time;
	*/
	private Element bindSavedTest(SavedTest saved_test_obj, String str_id)
	{
		// create main element
	    Element saved_test = new Element("saved_test");
	    	// create sub-elements
	    Element test_id       		= new Element("test_id");
	    Element test_date     		= new Element("test_date");
	    Element test_name    		= new Element("test_name");
	    Element test_type    		= new Element("test_type");
	    Element test_status   		= new Element("test_status");
	    Element test_format   		= new Element("test_format");
	    Element creation_time 		= new Element("creation_time");
	    Element score_time    		= new Element("score_time");
	    Element number_of_words    	= new Element("number_of_words");
	    	// add content to sub-elements
	    test_id.addContent(str_id);
	    test_date.addContent(saved_test_obj.getTestDate());
	    test_name.addContent(saved_test_obj.getTestName());
	    test_type.addContent(saved_test_obj.getTestType());
	    test_status.addContent(saved_test_obj.getTestStatus());
	    test_format.addContent(saved_test_obj.getTestFormat());
	    creation_time.addContent(saved_test_obj.getCreationTime());
	    score_time.addContent(saved_test_obj.getTestStatus());
	    number_of_words.addContent(saved_test_obj.getNumberOfWords());
	    log.add("added now "+saved_test_obj.getNumberOfWords());
	    	// add sub-elements to main element
	    saved_test.addContent(test_id);
	    saved_test.addContent(test_date);
	    saved_test.addContent(test_name);
	    saved_test.addContent(test_type);
	    saved_test.addContent(test_status);
	    saved_test.addContent(test_format);
	    saved_test.addContent(creation_time);
	    saved_test.addContent(score_time);
	    saved_test.addContent(number_of_words);
	    log.add("bindSavedTest: bound words "+saved_test_obj.getNumberOfWords());
	    return saved_test;
	}
	
	/**
	*	String class_id
		String test_id;
		String class_name
		String creation_time;
	*/
	private Element bindSavedClassTest(String class_id, String test_id, String test_name, String creation_time)
	{
		// create main element
	    Element saved_test = new Element("saved_test");
	    	// create sub-elements
	    Element class_id_elem       = new Element("class_id");
	    Element test_id_elem        = new Element("test_id");
	    Element test_name_elem      = new Element("test_name");
	    Element creation_time_elem  = new Element("creation_time");
	    	// add content to sub-elements
	    class_id_elem.addContent(class_id);
	    test_id_elem.addContent(test_id);
	    test_name_elem.addContent(test_name);
	    creation_time_elem.addContent(creation_time);
	    	// add sub-elements to main element
	    saved_test.addContent(class_id_elem);
	    saved_test.addContent(test_id_elem);
	    saved_test.addContent(test_name_elem);
	    saved_test.addContent(creation_time_elem);
	    return saved_test;
	}
	
	/**
	*Returns a hash of word objects from the user/subject/tests/saved/test_id.xml file.
	*The key is an index to keep the words in the same order as the test was created in.
	*The index number is also set in the writing level of the word.
	*The testing type, if mixed should be set in the type of the first test.
	* After this call, the house_deck_name and id are available if they exists and can be
	* gotten with their getters.
	* 
	*  		test.setType(testing_type);
	* 	    test.setDate(wntd_file_key);
	* 	   	test.setGrade(grade);
	* 	   	test.setName(reading_deck_card_name);
	* 	   	test.setLevel(writing_deck_card_name);
	* 	    Test [] tests = new Test[1];
	* 	    tests[0] = test;
	    
	*/
	public Hashtable load(UserInfo user_info, String test_id)
	{
		Hashtable saved_tests = new Hashtable();
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS
			+File.separator+Constants.SAVED);
		String saved_files_path = (tests_folder_path+File.separator+test_id+".xml");
	    	loadFile(saved_files_path);
	    File test = new File(saved_files_path);
		log.add("FileSaveTests.load: path "+test.getAbsolutePath());
		if (!test.exists())
		{
			log.add("does not exist");
			return new Hashtable();
		}
		try
	    {
			loadFile(saved_files_path);
	    	Element root = doc.getRootElement();
	    	house_deck_name = root.getChildText("house_deck_name");
	    	house_deck_id = root.getChildText("house_deck_id");
	    	List list = root.getChildren("test_word");
	    	int size = list.size();
	    	int i = 0;
	    	while(i<size)
	    	{
	    		Element saved_test_elem = (Element)list.get(i);
	    		Word test_word = new Word();
	    		String index = (String)saved_test_elem.getChildText("index");
	    		test_word = unbindWord(saved_test_elem);
	    		log.add("FileSavedTests.load: index "+index+" type "+getTestTypeFromSavedWord(test_word));
	    		saved_tests.put(index, test_word);
	    		i++;
	    	}
	    } catch (java.lang.NullPointerException npe)
	    {
	    	log.add("npe");
			return new Hashtable();
	    }
		return saved_tests;
	}
	
	/**
	 * Load a specific SavedTest object.
	 * @param user_info
	 * @param test_id
	 * @return a SavedTest object.
	 */
	public SavedTest loadSavedTest(UserInfo user_info, String test_id)
	{
		Vector tests = getSavedTestsList(user_info);
   		int i = 0;
   		int size = tests.size();
   		log.add("FileSaveTests.loadSavedTest: looking for test "+test_id);
   		while (i<size)
   		{
   			SavedTest saved_test = (SavedTest)tests.get(i);
   			if (saved_test.getTestId().equals(test_id))
   			{
   				log.add(i+" found "+saved_test.getTestName());
   				return saved_test;
   			}	else
   			{
   				log.add("no match "+i+" "+saved_test.getTestId()+" "+saved_test.getTestName()+" - "+Transformer.getDateFromMilliseconds(saved_test.getCreationTime())+" student "+user_info.getUserId());
   			}
   			i++;
   		}
   		return null;
	}
	
	/**
	* Same as the load method above, but returns a hash of word objects from the 
	* user/subject/tests/saved/test_id.xml file but the key in this case is 
	* the word id so passed words can be found easily in the test.
	*/
	public Hashtable loadSavedTestWordsForScorring(UserInfo user_info, String test_id)
	{
		Hashtable saved_tests = new Hashtable();
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS
			+File.separator+Constants.SAVED);
		String saved_files_path = (tests_folder_path+File.separator+test_id+".xml");
	    	loadFile(saved_files_path);
	    File test = new File(saved_files_path);
		log.add("FileSaveTests.load: path "+test.getAbsolutePath());
		if (!test.exists())
		{
			log.add("does not exist");
			return new Hashtable();
		}
		try
	    {
			loadFile(saved_files_path);
	    	Element root = doc.getRootElement();
	    	List list = root.getChildren("test_word");
	    	int size = list.size();
	    	int i = 0;
	    	while(i<size)
	    	{
	    		Element saved_test_elem = (Element)list.get(i);
	    		Word test_word = new Word();
	    		String index = (String)saved_test_elem.getChildText("index");
	    		log.add("FileSavedTests.load: index "+index);
	    		test_word = unbindWord(saved_test_elem);
	    		saved_tests.put(test_word.getId()+"", test_word);
	    		i++;
	    	}
	    } catch (java.lang.NullPointerException npe)
	    {
	    	log.add("npe");
			return new Hashtable();
	    }
		return saved_tests;
	}
	
	/**
	 *Returns the word objects from the user/subject/tests/saved/test_id.xml file.
	 *The key is an index to keep the words in the same order as the test was created in.
	 *This method returns a Vector without the index info in the load method above,
	 *as a convenience when loading the list to then score the test.
	 */
	 public Vector loadSavedTestWordsVector(UserInfo user_info, String test_id)
	 {
		 Vector saved_tests = new Vector();
		 String tests_folder_path = (user_info.getRootPath()
				 +File.separator+Constants.FILES
				 +File.separator+user_info.getUserId()
				 +File.separator+user_info.getSubject()
				 +File.separator+Constants.TESTS
				 +File.separator+Constants.SAVED);
		 String saved_files_path = (tests_folder_path+File.separator+test_id+".xml");
	     loadFile(saved_files_path);
	     File test = new File(saved_files_path);
	     log.add(test.getAbsolutePath());
	     if (!test.exists())
	     {
	    	 log.add("does not exist");
	    	 return new Vector();
	     }
	     try
	     {
	    	 loadFile(saved_files_path);
	    	 Element root = doc.getRootElement();
	    	 List list = root.getChildren("test_word");
	    	 int size = list.size();
	    	 int i = 0;
	    	 while(i<size)
	    	 {
	    		 Element saved_test_elem = (Element)list.get(i);
	    		 Word test_word = new Word();
	    		 String index = (String)saved_test_elem.getChildText("index");
	    		 test_word = unbindWord(saved_test_elem);
	    		 saved_tests.add(test_word);
	    		 i++;
	    	 }
	     } catch (java.lang.NullPointerException npe)
	     {
	    	 log.add("npe");
	    	 return new Vector();
	     }
	     return saved_tests;
	 }
	
	/**
	 * This method was made nine months after the loadSavedTests method below,
	 * and during debugging, we noticed that method, and this one became obsolete.
	*Returns a vector saved_test objects from the user/subject/tests/saved tests.xml file..
	*The file has these elements:
	<saved_test>
		<test_id>-735976319087141914</test_id>
		<test_date>1249668205974</test_date>
		<test_name>testie</test_name>
		<test_type>reading</test_type>
		<test_status>pending</test_status>
		<test_format>DailyTest</test_format>
		<creation_time>1249668205974</creation_time>
	</saved_test>
	*/
	public Vector loadTestList(UserInfo user_info)
	{
		//log.add("FST.loadTestList");
		Vector saved_tests = new Vector();
		String tests_file_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS
			+File.separator+"saved tests.xml");
		File test = new File(tests_file_path);
		//System.err.println("FST.loadTestList: tests file exists? "+test.exists()+" path "+tests_file_path);
	    loadFile(tests_file_path);
		Element root = doc.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size(); 
		//System.err.println("FST.loadTestList: size "+size);
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest saved_test = unbindSavedTest(saved_test_elem);
			saved_tests.add(saved_test);
			i++;
		}
		return saved_tests;
	}
	
	/**
	 *Load a list of teachers saved tests which have the following info:
	<saved_test>
		<test_id>-735976319087141914</test_id>
		<test_name>testie</test_name>
		<creation_time>1249668205974</creation_time>
	</saved_test>
	Returns a Vector of SavedTest objects which only have the above members filled in.
	*/
	public Vector loadClassTestList(UserInfo user_info)
	{
		//log.add("FST.loadTestList"); 
		Vector saved_tests = new Vector();
		String teacher_folder = user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+Constants.ADMIN
			+File.separator+Constants.TEACHERS
			+File.separator+user_info.getUserId();
		//System.err.println("FST.loadClassTestList: tests file exists? "+test.exists()+" path "+tests_file_path);
	    String saved_test_path = teacher_folder+File.separator+Constants.SAVED_TESTS+".xml";
	    File test = new File(saved_test_path);
	    log.add("FST.loadClassTestList: tests file exists? "+test.exists()+" path "+saved_test_path);
		loadFile(saved_test_path);
		Element root = doc.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size(); 
		//System.err.println("FST.loadTestList: size "+size);
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest saved_test = unbindSavedClassTest(saved_test_elem);
			saved_tests.add(saved_test);
			i++;
		}
		return saved_tests;
	}
	
	/**
	 *Load a SavedTest object using the test_id argument.  The class_id is available after this call.
	 *<saved_test>
	*	<test_id>-735976319087141914</test_id>
	*	<test_name>testie</test_name>
	*	<creation_time>1249668205974</creation_time>
	*</saved_test>
	*Returns a Vector of SavedTest objects which only have the above members filled in.
	*/
	public SavedTest loadClassTest(UserInfo user_info, String test_id)
	{
		//log.add("FST.loadTestList"); 
		Vector saved_tests = new Vector();
		String teacher_folder = user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+Constants.ADMIN
			+File.separator+Constants.TEACHERS
			+File.separator+user_info.getUserId();
		//System.err.println("FST.loadClassTestList: tests file exists? "+test.exists()+" path "+tests_file_path);
	    String saved_test_path = teacher_folder+File.separator+Constants.SAVED_TESTS+".xml";
	    File test = new File(saved_test_path);
	    log.add("FST.loadClassTestList: tests file exists? "+test.exists()+" path "+saved_test_path);
		loadFile(saved_test_path);
		Element root = doc.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size(); 
		//System.err.println("FST.loadTestList: size "+size);
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest saved_test = unbindSavedClassTest(saved_test_elem);
			if (saved_test.getTestId().equals(test_id))
			{
				return saved_test;
			}
			i++;
		}
		return null;
	}
	
	/**
	 * Go through a whole class of students and return the status for each student.
	 * @param user_info for the teacher
	 * @param test_id which should be the same for all the students.
	 * @param student_ids duh.
	 * @return Hashtable with the student id as the key, and a status string, like 'pending' or 'complete'.
	 */
	public Hashtable loadClassTestStatus(UserInfo user_info, String test_id, Vector student_ids)
	{
		log.add("loadClassTestStatus");
		log.add("user_id: "+user_info.getUserId()+" test_id: "+test_id+" tests: "+student_ids.size());
		Hashtable test_stati = new Hashtable();
		int i = 0;
		int size = student_ids.size();
		while (i<size)
		{
			String student_id = (String)student_ids.get(i);
			UserInfo student_info = new UserInfo();
			student_info.setEncoding(user_info.getEncoding());
			student_info.setRootPath(user_info.getRootPath());
			student_info.setSubject(user_info.getSubject());
			student_info.setUserId(student_id);
			SavedTest saved_test = loadSavedTest(student_info, test_id);
			log.add("number "+i+" student "+student_id);
			try
			{
				test_stati.put(student_id, saved_test);
			} catch (java.lang.NullPointerException npe)
			{
				log.add("The ooold line 655 error for "+student_id);
			}
			i++;
		}
		return test_stati;
	}
	
	/** UNbind the following type of xml:
	 * <house_decks>
	<house_deck>
		<deck_id>-1111111111111111111</deck_id>
		<name>green</name>
		<deck_card>
			<card_id>-1111111111111111111</card_id>
			<card_name>R1</card_name>
			<index>1</index>
			<type>reading</type>
		</deck_card>
	</house_deck>
</house_decks>
	 * @param teacher_id
	 * @param root_path
	 * @param device_id
	 * @return
	 */
	public Vector loadHouseDecks(String teacher_id, String root_path, String device_id)
    {
        String method = "loadHouseDecks";
        Vector house_decks = new Vector();
        String house_decks_path = root_path
            +File.separator+Constants.FILES
            +File.separator+Constants.ADMIN
            +File.separator+Constants.TEACHERS
            +File.separator+teacher_id
            +File.separator+"house_decks"
            +File.separator+device_id+".xml";
	     loadFile(house_decks_path);
	     File test = new File(house_decks_path);
	     log.add(test.getAbsolutePath());
	     if (!test.exists())
	     {
	    	 log.add("does not exist "+house_decks_path);
	    	 return new Vector();
	     }
	     try
	     {
	    	 loadFile(house_decks_path);
	    	 Element root = doc.getRootElement();
	    	 List list = root.getChildren("house_deck");
	    	 int size = list.size();
	    	 int i = 0;
	    	 while(i<size)
	    	 {
	    		 Element house_deck_elem = (Element)list.get(i);
	    		 HouseDeck house_deck = new HouseDeck();
	    		 String deck_id = (String)house_deck_elem.getChildText("deck_id");
	    		 String deck_name = (String)house_deck_elem.getChildText("deck_name");
	    		 house_deck.setDeckName(deck_name);
	    		 house_deck.setDeckId(deck_id);
	    		 house_deck.setCards(unbindDeckCards(house_deck_elem));
	    		 house_decks.add(house_deck);
	    		 i++;
	    	 }
	     } catch (java.lang.NullPointerException npe)
	     {
	    	 log.add("npe");
	    	 return new Vector();
	     }
	     return house_decks;
	 }
	
	/**
	 * Unbind elements like this:
	 * 
	 * 		<deck_card>
	 * 					<card_id>-1111111111111111111</card_id>
	 * 					<card_name>R1</card_name>
	 * 					<index>1</index>
	 * 					<type>reading</type>
	 * 		</deck_card>
	 * 					
	 * @param house_deck_elem
	 * @return
	 */
	private Hashtable unbindDeckCards(Element house_deck_elem)
	{
		Hashtable deck_cards = new Hashtable();
		List list = house_deck_elem.getChildren("deck_cards");
   	 	int size = list.size();
   	 	int i = 0;	
   	 	while (i<size)
   	 	{
   	 		Element deck_card_elem = (Element)list.get(i);
   	 		String card_id = (String)house_deck_elem.getChildText("card_id");
   	 		String card_name = (String)house_deck_elem.getChildText("card_name");
   	 		String index = (String)house_deck_elem.getChildText("index");
   	 		String type = (String)house_deck_elem.getChildText("type");
   	 		DeckCard deck_card = new DeckCard();
   	 		deck_card.setCardId(card_id);
   	 		deck_card.setCardName(card_name);
   	 		deck_card.setIndex(Integer.parseInt(index));
   	 		deck_card.setType(type);
   	 		deck_cards.put(card_id, deck_card);
   	 	}
		return deck_cards;
	}
	
	/**
	 * The test index is put in the reading level.  The test type, if it exists is put in the first test,
	 * along with the wntd file key in the test date field.  Also, if there is a reading or writing _deck_card_name element
	 * present, these are put in the test[0] name/level fields respectively.
	 * @param saved_test_elem
	 * @return
	 */
	private Word unbindWord(Element saved_test_elem)
	{
		log.add("FileSavedTests.unbindWord");
		Word test_word = new Word();
		String index = (String)saved_test_elem.getChildText("index");
		try
		{
			test_word.setId(Long.parseLong(saved_test_elem.getChildText("word_id")));
		} catch (java.lang.NumberFormatException nfe)
		{
			log.add("FileSavedTests.unbindWord: nfe for "+saved_test_elem.getChildText("word_id"));
			test_word.setId(0);
		}
	    test_word.setCategory(saved_test_elem.getChildText("category"));
	    test_word.setText(saved_test_elem.getChildText("text"));
	    test_word.setDefinition(saved_test_elem.getChildText("definition"));
	    test_word.setReadingLevel(Integer.parseInt(index));
	    Test test = new Test();
	    String wntd_file_key = saved_test_elem.getChildText("wntd_file_key");
	    String testing_type = saved_test_elem.getChildText("testing_type");
	    String grade = saved_test_elem.getChildText("grade");
	    String reading_deck_card_name = getReadingDeckCardName(saved_test_elem);
	    String writing_deck_card_name = getWritingDeckCardName(saved_test_elem);
	   	test.setType(testing_type);
	    test.setDate(wntd_file_key);
	   	test.setGrade(grade);
	   	test.setName(reading_deck_card_name);
	   	test.setLevel(writing_deck_card_name);
	    Test [] tests = new Test[1];
	    tests[0] = test;
	    test_word.setTests(tests);
	    log.add("FileSavedTests.unbindWord: testing type "+testing_type);
	    String is_set = FileSaveTests.getTestTypeFromSavedWord(test_word);
	    log.add("FileSavedTests.unbindWord: testing type saved? "+is_set);
	    return test_word;
	}
	
	/**
	 * The deck card name, for example, R1, or W1, is held in test[1].name.
	 * If may or may not exist depending on the type of test.
	 * If the saved test has been associated with a deck card in the wherewithal Android app,
	 * then it will contain a deck card name.
	 * @param saved_test_elem
	 * @return
	 */
	private String getReadingDeckCardName(Element saved_test_elem)
	{
		String reading_deck_card_name = "n/a";
		try
		{
			reading_deck_card_name = saved_test_elem.getChildText("reading_deck_card_name");
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileSavedTests.getDeckCardName: npe for "+saved_test_elem.getChildText("word_id"));
		}
		return reading_deck_card_name;
	}
	
	/**
	 * The deck card word type is held in test[1].level.
	 * The word type is different from the test type, which could be a reading test, but
	 * each word is can be split into its reading/text, writing/definition parts.
	 * If may or may not exist depending on the type of test.
	 * If the saved test has been associated with a deck card in the wherewithal Android app,
	 * then it will contain a deck card name
	 * @param saved_test_elem
	 * @return
	 */
	private String getWritingDeckCardName(Element saved_test_elem)
	{
		String writing_deck_card_name = "n/a";
		try
		{
			writing_deck_card_name = saved_test_elem.getChildText("writing_deck_card_name");
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileSavedTests.getDeckCardWordType: npe for "+saved_test_elem.getChildText("word_id"));
		}
		return writing_deck_card_name;
	}
	
	/**
	 * This method finds the first test set in the word and retrieves the test type.
	 * This is used when saving a list of test words that are scored at a later date
	 * and is a mix of types.
	 * @param word
	 * @return
	 */
	public static String getTestTypeFromSavedWord(Word word)
	{
		Test test = word.getTests(0);
		String test_type = test.getType();
		return test_type;
	}
	
	/**
	 * This method finds the first test set in the word and retrieves the date.
	 * This is used when saving a list of test words that are scored at a later date
	 * and we must update the words test date files.
	 * @param word
	 * @return
	 */
	public static String getWNTDFileKeyFromSavedWord(Word word)
	{
		Test test = word.getTests(0);
		String wntd_file_key = test.getDate();
		return wntd_file_key;
	}
	
	/**
	 * Replace the tests with one test that has the type set based on the agrument passed in.
	 * @param test_word - Word object.
	 * @param testing_type - r/w/ or r&w
	 * @param wntd_file_key - A link to the file in the wntd *type* folder.
	 * @return Same word object with a single test set with the test type in the type field and wntd key in the date field.
	 */
	public static Word setTestInfoFromSavedWord(Word test_word, String testing_type, String wntd_file_key)
	{	
		Test test = new Test();
	    test.setType(testing_type);
    	Test [] tests = new Test[1];
    	test.setType(testing_type);
    	test.setDate(wntd_file_key);
    	tests[0] = test;
    	test_word.setTests(tests);
    	return test_word;
	}
	
	/**
	 * This method again uses the first test set in the word to add a saved test result.
	 * It adds the new level to level and pass or fail grade to grade.
	 * Rather than use a new object or add properties to the Word class, we hack Test objects.
	 * @param word
	 * @param new_level - an integer
	 * @param grade - Constants.PASS or FAIL.
	 */
	public static Word setTestResult(Word word, String new_level, String grade)
	{
		Test test = word.getTests(0);
		test.setGrade(grade);
		test.setLevel(new_level);
    	Test [] tests = new Test[1];
    	tests[0] = test;
    	word.setTests(tests);
    	return word;
	}
	
	public static String getNewLevelFromTest(Word word)
	{
		Test test = word.getTests(0);
		String new_level = test.getLevel();
		return new_level;
	}
	
	public static String getGradeFromTest(Word word)
	{
		Test test = word.getTests(0);
		String grade = test.getGrade();
		return grade;
	}
    
	/**
	*Returns a vector saved_test objects from the user/subject/tests/saved tests.xml file..
	*/
	public Vector getSavedTestsList(UserInfo user_info)
	{
		log.add("getSavedTestsList");
		Vector saved_tests = new Vector();
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS);
		String saved_files_path = (tests_folder_path+File.separator+"saved tests.xml");
		log.add(saved_files_path+" exists? "+(new File(saved_files_path).exists()));
	    Document document = loadFileInternally(saved_files_path);
	    Element root = document.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size();
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest saved_test = unbindSavedTest(saved_test_elem);
			saved_tests.add(saved_test);
			i++;
		}
		return saved_tests;
	}
	
	/**
	*and delete the saved test words file it points to.
	*@return s true if both jobs are completed,
	* otherwise, the queen on the dark side of the moon flies out of her cave
	* and wrecks havoc with the man on the moon's closet.
	*/
	public boolean deleteSavedTestList(String test_id_to_delete, UserInfo user_info)
	{
		boolean complete = false;
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS);
		String saved_files_path = (tests_folder_path+File.separator+"saved tests.xml");
		log.add("FileSavedTests.deleteSavedTestList: "+saved_files_path+" exists? "+(new File(saved_files_path).exists()));
	    //loadFile(saved_files_path);
		Document doc2 = loadPrivateFile(saved_files_path);
		Element root = doc2.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size();
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			String saved_test_id = (String)saved_test_elem.getChildText("test_id");
			if (saved_test_id.equals(test_id_to_delete))
			{
				list.remove(saved_test_elem);
				writePrivateDocument(doc2, saved_files_path, user_info.getEncoding());
				String test_folder_path = (tests_folder_path
						+File.separator+Constants.SAVED
						+File.separator+test_id_to_delete+".xml");
				File file = new File(test_folder_path);
				complete = file.delete();
			}
			i++;
		}
		return complete;
	}
	
	/**
	 *Delete a test from the teacher's saved_tests.xml file.
	*/
	public boolean deleteSavedClassTestFromList(UserInfo user_info, String test_id)
	{
		Vector saved_tests = new Vector();
		String teacher_folder = user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+Constants.ADMIN
			+File.separator+Constants.TEACHERS
			+File.separator+user_info.getUserId();
		String saved_test_path = teacher_folder+File.separator+Constants.SAVED_TESTS+".xml";
	    File test = new File(saved_test_path);
	    log.add("FST.deleteSavedClassTestFromList: tests file exists? "+test.exists()+" path "+saved_test_path);
		loadFile(saved_test_path);
		Element root = doc.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size(); 
		//System.err.println("FST.loadTestList: size "+size);
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest saved_test = unbindSavedClassTest(saved_test_elem);
			String this_id = saved_test.getTestId();
			if (this_id.equals(test_id))
			{
				list.remove(i);
				writeDocument(saved_test_path, user_info.getEncoding());
				return true;
			}
			i++;
		}
		return false;
	}
	
	/**
	*A SavedTest bean has these members:
	test_id;
	private String test_date;
	private String test_name;
	private String test_type;
	private String test_status;
	private String test_format;
	private String creation_time;
	*/
	private SavedTest unbindSavedTest(Element e)
	{
		SavedTest saved_test = new SavedTest();
		saved_test.setTestId(e.getChildText("test_id"));
		saved_test.setTestDate(e.getChildText("test_date"));
		saved_test.setTestName(e.getChildText("test_name"));
		saved_test.setTestType(e.getChildText("test_type"));
		saved_test.setTestStatus(e.getChildText("test_status"));
		saved_test.setTestFormat(e.getChildText("test_format"));
		saved_test.setCreationTime(e.getChildText("creation_time"));
		saved_test.setScoreTime(e.getChildText("score_time"));
		try
		{
			saved_test.setTestScore(e.getChildText("test_score"));
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FST.unbindSavedTest: no test score element");
		}
		try
		{
			saved_test.setNumberOfWords(e.getChildText("number_of_words"));
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FST.unbindSavedTest: no number of words eloement");
		}
		log.add("FST.unbindSavedTest: create saved_test ---");
		append(Transformer.createTable(saved_test));
		log.add("FST.unbindSavedTest: create saved_test --- end");
		return saved_test;
	}
	
	/**
	*A SavedTest bean has many member variables, but the teacher only uses these:
	test_id;
	test_name;
	creation_time;
	The class id is stored in the global variable class_id which can be got with the
	getter method getClassId.
	*/
	private SavedTest unbindSavedClassTest(Element e)
	{
		SavedTest saved_test = new SavedTest();
		class_id = (String)e.getChildText("class_id");
		log.add("FileSaveTests.unbindSavedClassTest: class_id set to "+class_id);
		saved_test.setTestId(e.getChildText("test_id"));
		saved_test.setTestName(e.getChildText("test_name"));
		saved_test.setCreationTime(e.getChildText("creation_time"));
		log.add("FST.unbindSavedTest: create saved_test ---");
		append(Transformer.createTable(saved_test));
		log.add("FST.unbindSavedTest: create saved_test --- end");
		return saved_test;
	}
	
	/**
	*Go through each test word result, load the word and update its level and other
	* status files based on the result of pass or fail.  Return a Vector of test
	* score objects (SavedTestResult) to notify the user.
	* 
	* Seriously, what of all these variables are needed?  In the wtr, * lines should be left blank?
	* 
	* An AllWordsTest has these members:
	* private String text;
	* private String definition;
	* private String category;
	* private String test_type;
	* private String level;
	* private int daily_test_index;
	* private long id;
	* private String answer;
	*
	* Depending on the type of test, the answer could have the text of an answer entered like a
	* normal online test, or a pass/fail grade checked by the teacher or student themselves.
	*
	* An WordTestResult with the following members will be returned by this method:
	* private String text;					*
	* private String definition;			*
	* private String answer;
	* private String grade;
	* private String level;					*
	* private int id;
	* private String original_text;			*
	* private String original_definition;	*
	* private String original_level;	
	* private String encoding;
	* private String date;
	* private long word_id;					*
	* Vector scoreSavedTests(UserInfo user_info, SavedTest saved_test, Vector test_word_results);
	* 
	* Here is the object for the saved test:
	* 
	* private String test_id;
	* private String test_date;
	* private String test_name;
	* private String test_type;
	* private String test_status;
	* private String test_format;
	* private String creation_time;
	* 
	* A proposed new object could have:
	* 
	* private String text;
	* private String definition;
	* private String category;
	* private long word_id;					
	* private String testing_type;
	* private String original_level;
	* private String new_level;
	* private int test_index;		
	* private String grade;	
	* private String encoding;
	* 
	* Here is a brief of the work this method does:
// Get the Exclude Level Times Vector

// cycle thru all the test words.
// words that are in the test_word_results are passed.  Those that aren't are fails

// get the result of the test by loading the word and adjusting the level
WordTestResult wtr = store.scoreSingleWordTest(awt, user_id);

// update the word file with the new level
String new_level = store.recordWordTestScore(wtr, awt, max_level, test_name, user_id); 

// this updates the words level

// next, add a record in the daily *type* tests.record file. 
// this also removes the word from the new word list if it is a pass test, 
// if the word in on the new <type> words.list

// next add the test results to the test records which will show up on the
// missed words list if the user has that option set.
TestUtility tu = new TestUtility();
tu.addDailyTestRecordIfNeeded(test_type, wtr, user_id, context_path, user_opts, awt.getId(), encoding);		

ftr.updateTestsStatus(user_id, subject);
		
TestTimeMemory ttm = wltd.updateTestDateRecordsAndReturnMemory(user_id, context_path, wtr);		
// this will also save the list files for a possible undo

// we may want to get this info to show to the user, regarding last/next test date
String org_wltd = Transformer.getDateFromMilliseconds(ttm.getOriginalWLTD());
String org_wntd = Transformer.getDateFromMilliseconds(Domartin.getFileWithoutExtension(ttm.getOriginalWNTD()));
String new_wntd = Transformer.getDateFromMilliseconds(ttm.getNewWNTD());
			
// update the rate of testing and maybe get the new values
RateOfTesting rot = new RateOfTesting();			// update ROT and get new values
rot.updateRateOfTesting(test_type, awt.getLevel(), wtr.getGrade(), user_id, subject, context_path);
	*
	*This stuff is left out, as it should happen in the test action.


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

// maybe get the remaining number of tests
files = wltd.getWNTFiles(user_id, context_path);

	*/
	public Vector scoreSavedTests(UserInfo user_info, SavedTest saved_test, 
				Vector test_words, Vector test_word_results)
	{
		Vector test_results = new Vector();
		
		// from IntegratedTestResult
		FileStorage store = new FileStorage();
		String user_id = user_info.getUserId();
		String context_path = user_info.getRootPath();
		Hashtable user_opts = store.getUserOptions(user_id, context_path); 
		String subject = (String)user_opts.get("subject");
		String max_level = (String)user_opts.get("max_level");
		String encoding = (String)user_opts.get("encoding");
		FileTestRecords ftr = new FileTestRecords(context_path);
		Vector elt_vector = new FileUserOptions().getELTVector(user_opts);
		int i = 0;
		int size = test_words.size();
		while (i<size)
		{
			Word word = (Word)test_words.get(i);
			long word_id = word.getId();
			String str_id = Long.toString(word_id);
			String grade = "fail";
			if (test_word_results.contains(str_id))
			{
				grade = "pass";
			}
			Test test = word.getTests(0);
			String test_type = test.getType();
			SavedTestResult saved_test_result = new SavedTestResult(word, test_type, word.getReadingLevel(), grade, user_info.getEncoding());
			saved_test_result.setGrade(grade);
			String new_level = recordWordTestScore(user_info, saved_test, saved_test_result, word, (String)user_opts.get("max_level"));
			saved_test_result.setNewLevel(new_level);
			addDailyTestRecordIfNeeded(saved_test, saved_test_result, user_info, user_opts);		
			ftr.updateTestsStatus(user_id, subject);
			RateOfTesting rot = new RateOfTesting();			// update ROT and get new values
			rot.updateRateOfTesting(test_type, FarmingTools.getAppropriateLevel(word, test_type), grade, user_id, subject, context_path);
			i++;
		}
		return test_results;
	}
	
	/** From TestUtility.
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
	    private void addDailyTestRecordIfNeeded(SavedTest saved_test, SavedTestResult saved_test_result,
	        UserInfo user_info, Hashtable user_opts)
	    {
	        boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	        boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	        String record_exclude_level = (String)user_opts.get("record_exclude_level");
	        //int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	        WordTestRecordOptions wtro = new WordTestRecordOptions();
	        wtro.setType(saved_test_result.getTestingType());
	        wtro.setUserName(user_info.getUserId());
	        wtro.setRootPath(user_info.getRootPath());
	        wtro.setRecordFailedTests(record_failed_tests);
	        wtro.setRecordPassedTests(record_passed_tests);
	        wtro.setRecordExcludeLevel(record_exclude_level);
	        //wtro.setRecordLimit(record_limit);
	        wtro.setWordId(saved_test_result.getWordId());
	        String pass_fail = saved_test_result.getGrade();
	        if (pass_fail.equals("pass"))
	        {
	            FileJDOMWordLists fjdomwl =  new FileJDOMWordLists(user_info.getRootPath(), Constants.READING, user_info.getUserId());
	            try
	            {
	                fjdomwl.removeWordFromNewWordsList(Long.toString(saved_test_result.getWordId()), user_info.getEncoding());
	            } catch (java.lang.NullPointerException npe)
	            {
	                // dont know why this should be null...
	            }
	        }
	        addDailyTestRecord(saved_test, saved_test_result, wtro, user_info);
	    }
	    
	    /**
	     * From FileTestRecords.
	     * 
		*<p>Record a single DailyWordTest from a WordTestMemory object.
		*<p>Basically unbind the object into and element and add it to a
		* file name daily reading/writing tests.record in the /files/user/ folder. 
		*<p>The text object is saved in bytes, and the date is saved as time in
		* a long.  These transformations happen within this method, so that
		* the rest of the system can keep on using encoded text and string dates
		* as it was first designed to use.
		*<p>WordTestResult was replaced by SavedTestResult.
		*<P>THE OBJECT WordTestRecordOptions is used to hold the options to decided what records should be saved.
		  *<p>private String type;
		  *<p>private String user_name;
		  *<p>private String root_path;
		  *<p>private boolean record_failed_tests;
		  *<p>private boolean record_passed_tests;
		  *<p>private String record_exclude_level;
		  *<p>private int record_limit;
		*/  
		public void addDailyTestRecord(SavedTest saved_test, SavedTestResult saved_test_result, WordTestRecordOptions wtro, UserInfo user_info)
		{
			log.add("addDailyTestRecord");
			boolean add_test = addDailyTestRecordIsRequired(saved_test_result, wtro);
			if (add_test == true)
			{
				String wtro_type = wtro.getType();
				String saved_test_type = saved_test.getTestType();
				String saved_test_result_type = saved_test_result.getTestingType();
				//System.err.println("addDailyTestRecord wtro type "+wtro_type);
				//System.err.println("addDailyTestRecord saved_test_type "+saved_test_type);
				//System.err.println("addDailyTestRecord saved_test_result_type "+saved_test_result_type);
				String type = saved_test_type;
				if (type.equals(Constants.READING_AND_WRITING))
				{
					type = Constants.READING;
				}
				String root_path = wtro.getRootPath();
				String user_name = wtro.getUserName();
				String file_name = ("daily "+type+" tests.record");
				String file_path = new String(root_path+File.separator
					+"files"+File.separator+user_name+File.separator+file_name);
				//File file = new File(path_to_test);
				String encoding = user_info.getEncoding();
				loadFile(file_path);
				Element root = doc.getRootElement();
				List list = root.getChildren("test");
				if (list.size() > wtro.getRecordLimit())
				{
					// delete first record before save
					Object obj = list.remove(0);
				}
				Element test = new Element("test");
				// create element tags
				Element date = new Element("date");
				Element text = new Element("text");
				Element def = new Element("definition");
				Element answer = new Element("answer");
				Element grade = new Element("grade");
				Element new_l = new Element("new_level");
				Element org_l = new Element("original_level");
				Element reversed = new Element("reversed");
				Element word_id = new Element("word_id");
				try
				{
					date.addContent(Transformer.getLongDateAsString(saved_test.getTestDate()));	// the only other thing not in wtr
				} catch (java.lang.NullPointerException npe)
				{
					date.addContent(saved_test.getTestDate());
				}
				text.addContent(saved_test_result.getText());				// Transformer.getByteString() worked for a while then started causeing a npe
				def.addContent(saved_test_result.getDefinition());
				answer.addContent("na"); // because if came from a checkbox, not a text field.
				grade.addContent(saved_test_result.getGrade());
				new_l.addContent(saved_test_result.getNewLevel());
				org_l.addContent(saved_test_result.getOriginalLevel());
				reversed.addContent("no");			// used later if the test score is reversed
				word_id.addContent(Long.toString(saved_test_result.getWordId()));
				test.addContent(date);
				test.addContent(text);
				test.addContent(def);
				test.addContent(answer);
				test.addContent(grade);
				test.addContent(new_l);
				test.addContent(org_l);
				test.addContent(reversed);
				test.addContent(word_id);
				root.addContent(test);
				//writeDocument(doc, path_to_test, wtr.getEncoding());
				writeDocument(file_path, user_info.getEncoding()); //
			} else 
			{ 
				/*do test should not be added */ 
				log.add("test not recorded because it failed addDailyTestRecordIsRequired");
			}
		}
		
		/**
		 * From FileTestRecords.  Similar to TestUtility.addDailyTestRecordIfNeeded,
		 * which calls FileTestRecords.addDailyTestRecord(saved_test_result, wtro),
		 * which then calls this method in FileTestRecords, or something like that.
		 * These methods should be simplified, which may or may not happen.
		 * 
		 * Properties in the argument objects:
		*<p>WordTestResult was replaced by SavedTestResult
		*<P>THE OBJECT WordTestRecordOptions is used to hold the options to decided what records should be saved.
		  *<p>private String type;
		  *<p>private String user_name;
		  *<p>private String root_path;
		  *<p>private boolean record_failed_tests;
		  *<p>private boolean record_passed_tests;
		  *<p>private String record_exclude_level;
		  *<p>private int record_limit;
		  */
		private boolean addDailyTestRecordIsRequired(SavedTestResult saved_test_result, WordTestRecordOptions wtro)
		{
			boolean true_if_saved = false;
			String word_level = saved_test_result.getNewLevel();
			String exclude_level = wtro.getRecordExcludeLevel();
			if (word_level.equals(exclude_level))
			{
				// word_level = 0 and exclude_level = 0 (default value)
				log.add("do not record test because of exclude level");
			} else
			{
				String grade = saved_test_result.getGrade();
				boolean record_failed_tests = wtro.getRecordFailedTests();
				boolean record_passed_tests = wtro.getRecordPassedTests();
				if (grade.equals("pass") && record_passed_tests==true)
				{
					true_if_saved = true;
				} else if (grade.equals("fail") && record_failed_tests==true)
				{
					true_if_saved = true;
				}
				log.add("record_failed_tests "+record_failed_tests+" record_passed_tests "+record_passed_tests);
				log.add("save test "+true_if_saved);
			}
			return true_if_saved;
		}
	
	/**
	 * From FileStorage
	 * 
	*We have to take a test result and update file for the words level and the test files record
	*words_jdom.recordWordScore(question, "pass", test_name, date, test_type, max_level);
	
	* This method comes from FileStorage with this api:
	* WordTestResult wtr, AllWordsTest awt, String max_level, String test_name, String user_name
	* 
	WordTestResult was replaced by SavedTestResult.	AllWordsTest ditto.
	*/
	public String recordWordTestScore(UserInfo user_info, SavedTest saved_test, SavedTestResult saved_test_result, Word word, String max_level)
	{ 
		String question = new String();
		String test_type = saved_test_result.getTestingType();
		if (test_type.equals("reading"))
		{
			question = saved_test_result.getText();
		} else if (test_type.equals("writing"))
		{
			question = saved_test_result.getDefinition();
		}
		String grade = saved_test_result.getGrade();
		String date = saved_test.getTestDate();
		String category = saved_test_result.getCategory();
		String org_level = saved_test_result.getOriginalLevel();
		String path_to_words = getPathToFile(category, user_info.getUserId()+"", user_info.getRootPath());
		String encoding = saved_test_result.getEncoding();
		File words_file = new File(path_to_words);
		String file = words_file.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(words_file);
		String test_name = new String("level "+FarmingTools.getAppropriateLevel(word, test_type)+" "+test_type+".test"); // this is sooo 2004
		String new_level = jdom.recordWordScore(question, grade, test_name, date, test_type, max_level, org_level);
		jdom.writeDocument(path_to_words, encoding);
		return new_level;
	}
	
	/**
	 * Change the saved_test file from pending to complete, and add the element for score_time
	 * and score result to indicate when the test was scored and updated.
	 * 
	 * private String test_date;
	private String test_name;
	private String test_type;
	private String test_status;
	private String test_format;
	private String creation_time;
	
	add
		
		score_time
	
	 * @param user_info
	 * @param saved_test
	 * @param saved_test_result
	 */
	public void updateSavedTestStatus(UserInfo user_info, SavedTest new_saved_test)
	{
		log.add("FST.updateSavedTestStatus ###");
		String test_id = new_saved_test.getTestId();
		String new_test_satus = new_saved_test.getTestStatus();
		String tests_file_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS
			+File.separator+"saved tests.xml");
		File test = new File(tests_file_path);
		log.add("+FST.updateSavedTestStatus path "+tests_file_path);
	    Document private_doc = loadPrivateFile(tests_file_path);
	    Element root = private_doc.getRootElement();
		List list = root.getChildren("saved_test");
		int size = list.size();
		log.add("+FST.updateSavedTestStatus size : "+size);
		int i = 0;
		while(i<size)
		{
			Element saved_test_elem = (Element)list.get(i);
			SavedTest this_saved_test = unbindSavedTest(saved_test_elem);
			if (this_saved_test.getTestId().equals(test_id))
			{
				log.add("found "+test_id);
				Element test_status_elem = saved_test_elem.getChild("test_status");
				test_status_elem.setText(new_test_satus);
				Element score_time_elem = saved_test_elem.getChild("score_time");
				if (score_time_elem == null)
				{
					score_time_elem = new Element("score_time"); // in case the elem doesn't exist yet
					score_time_elem.addContent(new_saved_test.getScoreTime());
					saved_test_elem.addContent(score_time_elem);
					log.add("added new ScoreTime");
				} else
				{
					score_time_elem.setText(new_saved_test.getScoreTime());
				}
				Element test_score = saved_test_elem.getChild("test_score");
				if (test_score == null)
				{
					test_score = new Element("test_score"); // in case the elem doesn't exist yet
					test_score.addContent(new_saved_test.getTestScore());
					saved_test_elem.addContent(test_score); 
					log.add("added new TestScore "+new_saved_test.getTestScore());
				} else
				{
					score_time_elem.setText(new_saved_test.getScoreTime());
				}
				writePrivateDocument(private_doc, tests_file_path, user_info.getEncoding());
				break;
			}
			log.add("didn't found "+test_id);
			i++;
		}
	}
	
	/**
	 * Located here for convenience and time saving and whatever.
	 * @param file_name
	 * @param user_name
	 * @return
	 */
	private String getPathToFile(String file_name, String user_name, String root_path)
	{
		String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = file_name;}
		String path_to_file = new String(root_path+File.separator
			+Constants.FILES+File.separator+user_name+File.separator+file_w_o_ext+".xml");
		return path_to_file;
	}
	
	/**
	 * Save a list of words.  If this is an integrated test, 
	 * with type both (reading and writing) then a blank Text object
	 * is created and the testing type is saved per word in xml file
	 * and then unbound at set in the Test [0] object.  Therefore this
	 * information must already be set in the word object Vector passed in.
	 * 
	 * <daily_test>
		<test_id></test_id>
		<test_name></test_name>
		<test_type></test_type>
		<creation_time></creation_time>
		<test_item>
		<index></index>
		<id></id>
		<category></category>
		</test_item>
		</daily_test>
		
		The method Element bindSavedTest(SavedTest saved_test_obj, String str_id)
		creates this element:
	test_id;
	private String test_date;
	private String test_name;
	private String test_type;
	private String test_status;
	private String test_format;
	private String creation_time;
	<p>returns the id which is a long as a string, and represents the filename.
	*
	*Then each word itss created with this method:
	**	<test_word>
	*		<index></index>
	*		<word_id></word_id>
	*		<category></category>
	*		<text></text>			so that we dont have to load every word
	*		<definition></definition>	when unbinding a test. 
	*		<testing_type></testing_type>
	*	</test_word>
	* private Element createTestWordElement(Word word, int word_index)
	*
	*/
	public String saveWordList(String name, String type, Vector words, UserInfo user_info)
	{
		//Transformer.
		long test_id = Domartin.getNewID();
		String test_id_str = Long.toString(test_id);
		String creation_time = Long.toString(new Date().getTime());
		String tests_folder_path = (user_info.getRootPath()
				+File.separator+Constants.FILES
				+File.separator+user_info.getUserId()
				+File.separator+user_info.getSubject()
				+File.separator+Constants.TESTS);
		String saved_file_path = (tests_folder_path+File.separator+test_id_str+".xml");
		File saved_file = new File(saved_file_path);
		if (!saved_file.exists())
		{
			File saved_tests_folder = new File(tests_folder_path);
			saved_tests_folder.mkdir();
		}
		File save_file = new File(saved_file_path);
		Element root = new Element("test_words");
		doc = new Document(root);
		SavedTest saved_test = new SavedTest();
		saved_test.setCreationTime(creation_time);
		saved_test.setTestDate("test_date"); // this will be the actual date the test is taken.
		saved_test.setTestFormat("integrated");
		saved_test.setTestId(test_id_str);
		saved_test.setTestName(name);
		saved_test.setTestStatus("pending");
		saved_test.setTestType(type);
		Element saved_test_e = bindSavedTest(saved_test, test_id_str);
		int i = 0;
		int size = words.size();
		while(i<size)
		{
			Word word = (Word)words.get(i);
			Element word_e = createTestWordElementWithType(word, i);
			saved_test_e.addContent(word_e);
			i++;
		}
		root.addContent(saved_test_e);
		writeDocument(saved_file_path, user_info.getEncoding());
		return test_id_str;
	}
	
	/**
     * After calling load the doc is alive, so we can iterate through the tests and modify the elements.
     * We add the card names and word types to the test words:
     *
    *   <test_word>
    *        <index></index>
    *        <word_id></word_id>
    *        <category></category>
    *        <text></text>            so that we dont have to load every word
    *        <definition></definition>    when unbinding a test.
    *        <testing_type></testing_type>
    *        <wntd_file_key>1377779297903</wntd_file_key>
    * 		 <deck_card_name>W4</deck_card_name>
    * 		 <deck_card_word_type>writing</deck_card_word_type>
    * 		 <reading_deck_card_name>W4</reading_deck_card_name>
    * 		 <writing_deck_card_name>R12</writing_deck_card_name>
    *    </test_word>
    *
     * @param String user_info
     * @param Stringtest_id
     * @param Hashtable word_id_reading_deck_card_names
     * @param Hashtable word_id_writing_deck_card_names
     * @param String _house_deck_name
     * @param String _house_deck_id
     */
    public void addDeckCardAssociations(UserInfo user_info, String test_id, 
    		Hashtable word_id_reading_deck_card_names, Hashtable word_id_writing_deck_card_names,
    		String _house_deck_name, String _house_deck_id)
    {    
        load(user_info, test_id);
        Element root = doc.getRootElement();
        root.removeChildren("house_deck_name");
        root.removeChildren("house_deck_id");
        Element house_deck_name_element = new Element("house_deck_name");
        Element house_deck_id_element = new Element("house_deck_id");
        house_deck_name_element.setText(_house_deck_name);
        house_deck_id_element.setText(_house_deck_id);
        root.addContent(house_deck_name_element);
        root.addContent(house_deck_id_element);
        List list = root.getChildren("test_word");
        int size = list.size();
        int i = 0;
        while(i<size)
        {
            Element saved_test_elem = (Element)list.get(i);
            Word test_word = new Word();
            String index = (String)saved_test_elem.getChildText("index");
            unbindModifyBindSavedTest(saved_test_elem, word_id_reading_deck_card_names, word_id_writing_deck_card_names);
            i++;
        }
        writePrivateDocument(doc, file_name, user_info.getEncoding());
    }
    
    /**
     * Add deck card associations to an existing saved test. 
     * @param saved_test_elem
     * @param word_id_card_names
     * @param word_id_types
     */
    private void unbindModifyBindSavedTest(Element saved_test_elem, Hashtable word_id_reading_deck_card_names, Hashtable word_id_writing_deck_card_names)
    {
            log.add("FileSavedTests.unbindModifyBindSavedTest");
            Word test_word = new Word();
            String index = (String)saved_test_elem.getChildText("index");
            String word_id = saved_test_elem.getChildText("word_id");
            String reading_deck_card_name = (String)word_id_reading_deck_card_names.get(word_id); // get info from hashes.
            String writing_deck_card_name = (String)word_id_writing_deck_card_names.get(word_id);
            log.add("FileSavedTests.unbindModifyBindSavedTest: card_name "+reading_deck_card_name);
            Element reading_deck_card_name_element = saved_test_elem.getChild("reading_deck_card_name");
            Element writing_deck_card_name_element = saved_test_elem.getChild("writing_deck_card_name");
            if (writing_deck_card_name_element == null)
            {
            	log.add("FileSavedTests.unbindModifyBindSavedTest: no deck card name or word type found");
                reading_deck_card_name_element = new Element("reading_deck_card_name");
                writing_deck_card_name_element = new Element("writing_deck_card_name");
                reading_deck_card_name_element.setText(reading_deck_card_name);
                writing_deck_card_name_element.setText(writing_deck_card_name);
                saved_test_elem.addContent(reading_deck_card_name_element);
                saved_test_elem.addContent(writing_deck_card_name_element);
            } else 
            {
            	reading_deck_card_name_element.setText(reading_deck_card_name);
            	writing_deck_card_name_element.setText(writing_deck_card_name);
            }
    }
    
    public void saveHouseDecks(String teacher_id, String root_path, String device_id, Vector house_decks)
    {
        String method = "saveHouseDecks";
        String house_decks_path = root_path
            +File.separator+Constants.FILES
            +File.separator+Constants.ADMIN
            +File.separator+Constants.TEACHERS
            +File.separator+teacher_id
            +File.separator+"house_decks";
        File folder = new File(house_decks_path);
        String house_decks_file = house_decks_path+File.separator+device_id+".xml";
        log.add("FileSavedTests."+method+" opening file "+house_decks_file);
        File file = new File(house_decks_file);
        if (!folder.exists())
        {
        	boolean created_folder = folder.mkdir();
        	System.err.print("FileSavedTests."+method+" created new folder? "+created_folder+" "+house_decks_path);
        	try 
        	{
        		
				boolean created = file.createNewFile();
				log.add("FileSavedTests."+method+" created new file? "+created);
			} catch (java.io.IOException ioe) 
			{
				log.add("FileSavedTests."+method+" Oh shit!");
				//ioe.printStackTrace();
			}
        }
        Element root = new Element("house_decks");
        doc = new Document(root);
        // iterate thru house decks
        for (int i=0; i<house_decks.size(); i++)
        {
            HouseDeck house_deck = (HouseDeck)house_decks.get(i);
            Element house_deck_element = bindHouseDeck(house_deck);
            root.addContent(house_deck_element);
        }
        writeDocument(house_decks_file, doc);
    }
    
    /**
     *  <house_deck>
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
    private Element bindHouseDeck(HouseDeck house_deck)
    {
        Element house_deck_element = new Element("house_deck");
        Element deck_id = new Element("deck_id");
        //Element player_id = new Element("player_id"); // don't need to save associations
        //Element game_id = new Element("game_id");  // ditto
        Element deck_name = new Element("deck_name");
        deck_id.setText(house_deck.getDeckId());
        deck_name.setText(house_deck.getDeckName());
        house_deck_element.addContent(deck_id);
        house_deck_element.addContent(deck_name);
        Hashtable deck_cards = house_deck.getCards();
        Enumeration e = deck_cards.keys();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            DeckCard card = (DeckCard)deck_cards.get(key);
            Element deck_card = bindDeckCard(card);
            house_deck_element.addContent(deck_card);
        }
        return house_deck_element;
    }
    
    /*
     * <card>
         *                 <id/>
         *                 <name/>
         *                 <index/>
         *                 <type/>
     */
    private Element bindDeckCard(DeckCard deck_card)
    {
        Element deck_card_element = new Element("deck_card");
        Element card_id = new Element("card_id");
        Element card_name = new Element("card_name");
        Element index = new Element("index");
        Element type = new Element("type");
        card_id.addContent(deck_card.getCardId());
        card_name.addContent(deck_card.getCardName());
        index.addContent(deck_card.getIndex()+"");
        type.addContent(deck_card.getType());
        deck_card_element.addContent(card_id);
        deck_card_element.addContent(card_name);
        deck_card_element.addContent(index);
        deck_card_element.addContent(type);
        return deck_card_element;
    }

	
	/** load a file
	*/
	public void loadFile(String _file_name)
	{
		file_name = _file_name;
		File file_f = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("FileSavedTests.loadFile<> "+ file_name);
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file_f);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			System.err.println("FileSaveTests.loadFile1: JDOMException "); 
			System.err.println("file_name "+file_name);
			j.printStackTrace();
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add(fnfe.toString());
			System.err.println("FileSaveTests.loadFile1: FNFE");
		} catch (java.io.IOException i)
		{
			System.err.println("FileSaveTests.loadFile1: IOE "+file_name);
			log.add(i.toString());
		}
	}
	
	/** load a file
	*/
	public Document loadFileInternally(String name)
	{
		Document document = null;
		File file_f = new File(name);
		log.add("FileSavedTests.loadFile<> "+ name);
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file_f);
			document = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			System.err.println("FileSaveTests.loadFile1: JDOMException "); 
			System.err.println("file_name "+name);
			j.printStackTrace();
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add(fnfe.toString());
			System.err.println("FileSaveTests.loadFile1: FNFE");
		} catch (java.io.IOException i)
		{
			System.err.println("FileSaveTests.loadFile1: IOE "+name);
			log.add(i.toString());
		}
		return document;
	}
	
	/** load a file
	*/
	public Document loadPrivateFile(String private_file_name)
	{
		File file_f = new File(private_file_name);
		Document private_doc = null;
		//log = new Vector();
		log.add("FileSavedTests.loadPrivateFile path: "+ private_file_name);
		log.add("FileSavedTests.loadPrivateFile: exists? "+ file_f.exists());
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file_f);
			private_doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			System.err.println("FileSaveTests.loadPrivateFile: JDOM Exceptiuon");
			log.add(j.toString());
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add(fnfe.toString());
			System.err.println("FileSaveTests.loadPrivateFile: FNFE");
		} catch (java.io.IOException i)
		{
			System.err.println("FileSaveTests.loadPrivateFile: IOExce[tion ");
			System.err.println("file_name "+file_name);
			log.add(i.toString());
		}
		return private_doc;
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name, Document doc2)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(doc2, writer);
			writer.close();
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add("FileSaveTests.writeDocument1: fnfe for "+file_name);
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
			log.add("FileSaveTests.writeDocument1: ioe for "+file_name);
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
	
	/**
	*<p>Write document with encoding.</p>
	*<p>XMLOutputter(java.lang.String indent, boolean newlines, java.lang.String encoding)</p>
	*/	
	public void writeDocument(String file_name, String encoding)
	{
		//log.add("JDOMSolution.writeDocument: writing "+file_name+" ...");
		if (doc==null)
		{
			//log.add("JDOMSolution.writeDocument: 2 doc is null");
		}
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file_name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			//log.add("JDOMSolution.writeDocument: "+file_name);
			osw.close();
		} catch (java.io.UnsupportedEncodingException uee)
		{
			log.add("FileSaveTests.writeDocument2: euu error "+encoding);
			uee.printStackTrace();
		}catch (java.io.IOException e)
		{
			log.add("FileSaveTests.writeDocument2: io error");
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
	}
	
	/**
	*<p>Write document with encoding.</p>
	*<p>XMLOutputter(java.lang.String indent, boolean newlines, java.lang.String encoding)</p>
	*/	
	public void writePrivateDocument(Document private_doc, String file_name, String encoding)
	{
		log.add("FileSaveTests.writePrivateDocument: writing "+file_name+" ...");
		if (private_doc==null)
		{
			log.add("FileSaveTests.writePrivateDocument: doc is null");
		}
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file_name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(private_doc, osw);
			//log.add("JDOMSolution.writeDocument: "+file_name);
			osw.close();
		} catch (java.io.UnsupportedEncodingException uee)
		{
			log.add("FileSaveTests.writePrivateDocument: euu error "+encoding);
			uee.printStackTrace();
		}catch (java.io.IOException e)
		{
			log.add("FileSaveTests.writePrivateDocument: io error");
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
	}
	
	public String getHouseDeckName()
	{
		return house_deck_name;
	}
	
	public String getHouseDeckId()
	{
		return house_deck_id;
	}
	
	/** deguggin */
	public Vector getLog()
	{
		log.add("FileSaveTests.log -------------- end");
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
	
	public void resetLog()
	{
		log = new Vector();
	}
}
