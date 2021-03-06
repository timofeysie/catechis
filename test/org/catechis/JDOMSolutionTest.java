package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;
import java.util.Hashtable;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import java.lang.Integer;
//import java.lang.NullPointerException;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordFilter;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.JDOMSolution;



// for debugging
import org.catechis.Transformer;
public class JDOMSolutionTest extends TestCase
{
	
	private Document doc;
	private Document doc2;
	private MockStorage store;
	
    public JDOMSolutionTest (String name) 
    {
        super(name);
    }
    
    /**
    * The setup method creates the following document:
    test
    	element
		sub_element1=test1
		sub_element2=test2
			
			
	test
		score
			grade ...
			date ...
		score
			grade ...
			date ...
		score
			grade ...
			date ...
    */
    protected void setUp() throws Exception
    {
	    	doc = new Document();
		Element root = new Element("test");
		Element element = new Element("element");
		Element sub_element1 = new Element("sub_element1");
		Element sub_element2 = new Element("sub_element2");
		sub_element1.addContent("test1");
		sub_element2.addContent("test2");
		element.addContent(sub_element1);
		element.addContent(sub_element2);
		root.addContent(element);
		doc.addContent(root);
		store = new MockStorage();
		
		// doc two test mock
		doc2 = new Document();
		Element test = new Element("test");
		Element score0 = new Element("score");
		Element score1 = new Element("score");
		Element score2 = new Element("score");
		Element grade0 = new Element("grade");
		Element date0 = new Element("date");
		Element grade1 = new Element("grade");
		Element date1 = new Element("date");
		Element grade2 = new Element("grade");
		Element date2 = new Element("date");
		grade0.addContent("70");
		date0.addContent("Mon Nov 01 22:26:22 PST 2004");
		grade1.addContent("80");
		date1.addContent("Mon Nov 01 22:26:22 PST 2004");
		grade2.addContent("90");
		date2.addContent("Mon Nov 01 22:26:22 PST 2004");
		score0.addContent(grade0);
		score0.addContent(date0);
		score1.addContent(grade1);
		score1.addContent(date1);
		score2.addContent(grade2);
		score2.addContent(date2);
		test.addContent(score0);
		test.addContent(score1);
		test.addContent(score2);
		doc2.addContent(test); 
    }
    
    // Testing: Get Whatever Hash
    public void testGetWhateverHash()
    {
	    JDOMSolution jdom = new JDOMSolution(doc, store);
	    Hashtable test_hash = jdom.getWhateverHash("element","sub_element1","sub_element2");
	    String expected = new String("test2");
	    String actual = (String)test_hash.get("test1");
	    assertEquals(expected, actual);
    }
    
    // Testing: Number of elements returned.  Had a problem with the Stats returning only 1 test.
    public void testGetWhateverKeyValHashSize()
    {
	    JDOMSolution jdom = new JDOMSolution(doc2, store);
	    Hashtable test_hash = jdom.getWhateverKeyValHash("score","grade","date");
	    int expected_size = 3;
	    int actual_size = test_hash.size();
	    assertEquals(expected_size, actual_size);
    }

    // Testing: when there is no sub_element present
    public void testGetWhateverHashNoResult()
    {
	    JDOMSolution jdom = new JDOMSolution(doc, store);
	    Hashtable test_hash = jdom.getWhateverHash("element","sub_element1","sub_element4");
	    String actual = (String)test_hash.get("test1");
	    if (actual==null)
	    {
		    assertNull("There is no child element, so return null", actual);
	    } else
	    {
		    assertEquals(actual, actual);
		    //fail("testGetWhateverHash should be null.  This is a failure");
	    }
    }
    
    public void testGetOptionsHash()
    {
	    doc = new Document();
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String options_path = new String(current_dir+File.separator+"test.options");
	    File test_file = new File(options_path);
	    JDOMSolution jdom = new JDOMSolution(test_file, store);
	    Hashtable test_options = jdom.getOptionsHash();
	    String expected = new String("value");
	    String actual = (String)test_options.get("test");
	    assertEquals(expected, actual);
    }
    
    public void testGetFilteredReadingWords()
    {
	    String test_length = new String("");
	    String text_index = new String("all");
	    String user_name = new String("test_user");
	    File path_file = new File("");
	    WordFilter word_filter = new WordFilter();
	    word_filter.setStartIndex(0);
	    word_filter.setMinMaxRange("0-0");
	    word_filter.setType("reading");
	    word_filter.setCategory("test words.xml");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    Vector actual_filtered_words = store.getFilteredWordObjects(word_filter, user_name);
	    int expected_size = 2;
	    int actual_size = actual_filtered_words.size();
	    //System.out.println("JDOMSolutionTest.testGetFilteredWords: actual size "+actual_size);
	   assertEquals(expected_size, actual_size);
    }
    
    public void testGetFilteredWritingWords()
    {
	    String test_length = new String("");
	    String text_index = new String("all");
	    String user_name = new String("test_user");
	    File path_file = new File("");
	    WordFilter word_filter = new WordFilter();
	    word_filter.setStartIndex(0);
	    word_filter.setMinMaxRange("0-0");
	    word_filter.setType("writing");
	    word_filter.setCategory("test words.xml");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    Vector actual_filtered_words = store.getFilteredWordObjects(word_filter, user_name);
	    int expected_size = 2;
	    int actual_size = actual_filtered_words.size();
	    //System.out.println("JDOMSolutionTest.testGetFilteredWords: actual size "+actual_size);
	   assertEquals(expected_size, actual_size);
    }
    
    public void testGetWordsForTest()
    {
	    String test_length = new String("");
	    //String file_name = new String("october1.xml");
	    //String file_name = new String("Ganada Intermediate 16-20.xml");
	    String file_name = new String("August.xml");
	    String user_name = new String("WLTD files");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
	    if (file_w_o_ext.equals("-1"))
	    {
	    	file_w_o_ext = file_name;
	    }
	    String path_to_words = new String(current_dir+File.separator+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
	    File words_file = new File(path_to_words);
	    String test_path = words_file.getAbsolutePath();
	    JDOMSolution jdom = new JDOMSolution(words_file);
	    Vector words = jdom.getWordsForTest(file_name);
	    Word first_word = (Word)words.get(0);
	    Test tests [] = first_word.getTests();
	    int actual_size = tests.length;
	    //System.out.println("JDOMSolutionTest.testGetWordsForTest: tests "+actual_size);
	    int i = 0;
	    while (i<actual_size)
	    {
		    Test test = tests[i];
		    //printLog(Transformer.createTable(test));
		    //printLog(jdom.getLog());
		    i++;
	    }
	    int expected_size = 31;
	    //System.out.println("JDOMSolutionTest.testGetWordsForTest: tests "+expected_size);
	    //dumpLog(jdom.getLog());
	    assertEquals(expected_size, actual_size);
    }
    
    /**
    *We create a AllStatsHistory object, then add it to the test_user.hist file,
    * but we save it as test_user_temp.hist file so that we can delete it after the test.
    *	*<p>	<date>Thu Oct 13 19:10:24 KST 2005</date>
	*<p>	<number_of_tests>182</number_of_tests>
	*<p>	<average_score>49.7</average_score>
	*<p>	<number_of_words>913</number_of_words>
	*<p>	<writing_average>1.2</writing_average>
	*<p>	<reading_average>0.81</reading_average>
	*<p>	<words_at_reading_level_0>317</words_at_reading_level_0>
	*<p>	<words_at_reading_level_1>474</words_at_reading_level_1>
	*<p>	<words_at_reading_level_2>99</words_at_reading_level_2>
	*<p>	<words_at_reading_level_3>22</words_at_reading_level_3>
	*<p>	<words_at_writing_level_0>201</words_at_writing_level_0>
	*<p>	<words_at_writing_level_1>485</words_at_writing_level_1>
	*<p>	<words_at_writing_level_2>137</words_at_writing_level_2>
	*<p>	<words_at_writing_level_3>57</words_at_writing_level_3>*/
    public void testPutHistory()
    {
	File path_file = new File("");
	String current_dir = path_file.getAbsolutePath();
	String history_path = new String(current_dir+File.separator+"files"
		+File.separator+"test_user"+File.separator+"test_user.hist");
	File history_file = new File(history_path);
	String test_history_path = new String(current_dir+File.separator+"files"
		+File.separator+"test_user"+File.separator+"test_user_temp.hist");
	File test_history_file = new File(test_history_path);
	JDOMSolution jdom = new JDOMSolution(history_file, store);
	AllStatsHistory all_stats_history = new AllStatsHistory();
	all_stats_history.setDate("Thu Oct 13 19:10:24 KST 2004");
	all_stats_history.setNumberOfTests(1);
	all_stats_history.setAverageScore(100);
	all_stats_history.setNumberOfWords(100);
	all_stats_history.setWritingAverage(1.0);
	all_stats_history.setReadingAverage(1.0);
	Vector writing_levels = new Vector();
	Vector reading_levels = new Vector();
	int i = 0;
	while(i<4)
	{
		reading_levels.add(i, Integer.toString(i));
		writing_levels.add(i, Integer.toString(i));
		i++;
	}
	all_stats_history.setReadingLevels(reading_levels);
	all_stats_history.setWritingLevels(writing_levels);
	jdom.addHistory(all_stats_history);
	jdom.writeDocument(test_history_file.getAbsolutePath());
	// now read the test file
	jdom = new JDOMSolution(test_history_file, store);
	Vector actual_histories = jdom.getHistory();
	int expected_size = 2;
	int actual_size = actual_histories.size();
	test_history_file.delete();
	assertEquals(expected_size, actual_size);
    }
    
    public void testReplaceWord()
    {
	    // setup word file
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+"test_user"+File.separator+"test words.xml");
	    File test_file = new File(path);
	    JDOMSolution jdom = new JDOMSolution(test_file);
	    // setup word to replace
	    String original_text = new String("바닷가");
	    String new_text = new String("바닷가");
	    String new_def = new String("test");
	    jdom.replaceWord(original_text, new_text, new_def);
	    // now find the word and test the replacement
	    Word word = jdom.findWordWithoutTests(original_text);
	    String actual_def = word.getDefinition();
	    String expected_def = new_def;
	    assertEquals(expected_def, actual_def);
    }
    
    /**
    * We try to change a test from the test user.xml file with the last test:
    * <test><date>Mon Jun 28 16:19:48 PDT 2005</date><file>level 1 writing.test</file><grade>fail</grade></test>
    */
    public void testReplaceWordAndChangeTest()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"test words.xml");
	    File test_file = new File(path);
	    String copy_cat = new String("test words copy.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    FileStorage fs = new FileStorage();
	    fs.copyFile(test_file, duplicate_file);
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    Word original_word = jdom.findWord("바닷가");
	    WordTestMemory wtm = new WordTestMemory();
	    wtm.setCategory(copy_cat);
	    wtm.setType("writing");
	    wtm.setDate("Mon Jun 28 16:19:48 PDT 2005");
	    WordTestResult wtr = new WordTestResult();
	    wtr.setText("바닷가");
	    wtr.setDefinition(original_word.getDefinition());
	    wtr.setAnswer(original_word.getDefinition());
	    wtr.setGrade("pass");
	    wtr.setId(0);
	    wtr.setOriginalText(original_word.getText());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    jdom2.replaceWordAndChangeTest(wtm, wtr);
	    jdom2.writeDocument(copy_cat);
	    Word altered_word = jdom2.findWord("바닷가");
	    // duplicate_file.delete();
	    String actual_level = Integer.toString(altered_word.getWritingLevel());
	    // dumpLog(jdom2.getLog());
	    /*Vector log = jdom2.getLog();
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
	    */
	    String expected_level = new String("1");
	    duplicate_file.delete();
	    assertEquals(expected_level, actual_level);
    }
    
    public void testChangeScore()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"sample.test");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("sample copy.test");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    //System.out.println("JDOMSolution.testChangeScore: path "+duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    String date = new String("Mon Nov 01 22:26:22 PST 2004");
	    String expected_score = new String("100");
	    // run test cleanup and check
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    jdom.changeScore(date, expected_score);
	    jdom.writeDocument(copy_cat);
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Hashtable tests = jdom.getWhateverHash("score", "date", "grade");
	    //dumpHash(tests);
	    String actual_score = (String)tests.get(date);
	    duplicate_file.delete();
	    assertEquals(expected_score, actual_score);
    }
    
    public void testDeleteWord()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("word_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"october-test.xml");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("october-test2 copy.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    //System.out.println("JDOMSolution.testDeleteWord: path "+duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    Word word = new Word();
	    word.setText("texty");
	    word.setDefinition("texty");
	    // run test cleanup and check
	    String encoding = new String("euc-kr");
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    jdom.deleteWord(word);
	    jdom.writeDocument(copy_cat, encoding);
	    //dumpLog(jdom.getLog());
	    //printLog(jdom.getLog());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word actual_word = jdom2.findWordWithoutTests("texty");
	    //dumpHash(tests);
	    String actual = word.getText();
	    String expected = null;
	    //duplicate_file.delete();
	    assertEquals(expected, actual);
    }
    
    public void testEditWord()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("word_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"october-test.xml");
	    File original_file = new File(path);
	    System.err.println("file: "+original_file.getAbsolutePath()+" exists? "+original_file.exists());
	    // duplicate file
	    String copy_cat = new String("october-test copy.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    System.err.println("dupl: "+duplicate_file.getAbsolutePath()+" exists? "+duplicate_file.exists());
	    //System.out.println("JDOMSolution.testChangeScore: path "+duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    Word old_word = new Word();
	    old_word.setText("texty");
	    old_word.setDefinition("texty");
	    Word new_word = new Word();
	    new_word.setText("sexy");
	    new_word.setDefinition("sexxxy");
	    // run test cleanup and check
	    String encoding = new String("euc-kr");
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    jdom.editWord2(old_word, new_word, copy_cat, encoding);
	    //jdom.writeDocument(copy_cat, encoding);
	    dumpLog(jdom.getLog());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word actual_word = jdom2.findWordWithoutTests("sexy");
	    //dumpHash(tests);
	    String actual = actual_word.getText();
	    String expected = new String("sexy");;
	    duplicate_file.delete();
	    assertEquals(expected, actual);
    }
    
    public void testEditWordIllegalDataException()
    {
	    //System.out.println("JDOMSolutionTest: 13");
	    File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String user_name = new String("guest");
		String category = new String("very much");
		String search_value = new String("Very much");
		String search_property = new String("definition");
		FileStorage store = new FileStorage(current_dir);
		Word old_word = store.getWordObject(search_property, search_value, category, user_name);
		String text = old_word.getText();
		
	// original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user_name+File.separator+category+".xml");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("very much copy.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user_name+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    
	    Word new_word = new Word();
	    new_word.setText("pok");
	    new_word.setDefinition("very much");
	    // run test cleanup and check
	    String encoding = new String("euc-kr");
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    jdom.editWord2(old_word, new_word, copy_cat, encoding);
	    //jdom.writeDocument(copy_cat, encoding);
	    //dumpLog(jdom.getLog());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word actual_word = jdom2.findWordWithoutTests("Very much");
	    //dumpHash(tests);
	    String actual = actual_word.getText();
	    String expected = new String("pok");;
	    //duplicate_file.delete();
	    assertEquals(expected, actual);
	    // org.jdom.IllegalDataException: The data " ??" is not legal for a JDOM character content: 0x0 is not a legal XML character.
    }
    
    /**
    *Methods used in the jdom method:
    * wtm.getType()
    * wtm.getDate()
    * wtr.getOriginalText()
    * wtr.getGrade()
    * We are going to change this test:
    * <test><date>Thu May 05 15:43:13 PDT 2005</date><file>level 1 writing.test</file><grade>fail</grade></test>
    * <writing-level>0</writing-level>
    */
    public void testChangeWordLevelUp()
    {
	    File path_file = new File("");
	    String user = new String("test_user");
	    String encoding = new String("euc-kr");
	    String base_path = new String(path_file.getAbsolutePath()+File.separator+"files"
		+File.separator+user+File.separator);
	    // original file and duplicates
	    File original_file = new File(base_path+"test words.xml");
	    String duplicate_path = new String(base_path+"level test words.xml");
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    WordTestMemory wtm = new WordTestMemory();
	    wtm.setType("reading");
	    wtm.setDate("Thu May 05 15:43:13 PDT 2005");
	    wtm.setTestName("level 1 writing.test");
	    WordTestResult wtr = new WordTestResult();
	    wtr.setText("\ubc14\ub2f7\uac00");
	    wtr.setOriginalText("\ubc14\ub2f7\uac00");
	    wtr.setGrade("pass");
	    wtr.setOriginalLevel("1");
	    String max_level = "3";
	    // run test and cleanup
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    String actual_word_level = jdom2.changeWordLevel(wtm, wtr, max_level);
	    jdom2.writeDocument(duplicate_path, encoding);
	    String expected_word_level = new String("2");
	    duplicate_file.delete();
	    assertEquals(expected_word_level, actual_word_level);
    }
    
    /**
    *Methods used in the jdom method:
    * wtm.getType()
    * wtm.getDate()
    * wtr.getOriginalText()
    * wtr.getGrade()
    * We are going to change this test:
    * <test><date>Thu May 05 15:43:13 PDT 2005</date><file>level 1 writing.test</file><grade>fail</grade></test>
    * <writing-level>0</writing-level>
    */
    public void testChangeWordLevelDown()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"test words.xml");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("test words copy.test");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    WordTestMemory wtm = new WordTestMemory();
	    wtm.setType("writing");
	    wtm.setDate("Mon Jun 28 16:19:48 PDT 2005");
	    wtm.setTestName("level 1 writing.test");
	    WordTestResult wtr = new WordTestResult();
	    wtr.setText("\ubc14");
	    wtr.setOriginalText("\ubc14");
	    wtr.setGrade("fail");
	    // run test and cleanup
	    String max_level = "3";
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    String actual_word_level = jdom2.changeWordLevel(wtm, wtr, max_level);
	    String expected_word_level = new String("0");
	    duplicate_file.delete();
	    assertEquals(expected_word_level, actual_word_level);
    }
    
    
    /**
    *
    */
    public void testChangeWordLevelMoreThan0()
    {
	    File path_file = new File("");
	    String user = new String("test_user");
	    String encoding = new String("euc-kr");
	    String base_path = new String(path_file.getAbsolutePath()+File.separator+"files"
		+File.separator+user+File.separator);
	    // original file and duplicates
	    File original_file = new File(base_path+"test words.xml");
	    String duplicate_path = new String(base_path+"level test words.xml");
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    WordTestMemory wtm = new WordTestMemory();
	    wtm.setType("reading");
	    wtm.setDate("Thu May 05 15:43:13 PDT 2005");
	    wtm.setTestName("level 2 reading.test");
	    WordTestResult wtr = new WordTestResult();
	    wtr.setText("\ubc14");
	    wtr.setOriginalText("\ubc14");
	    wtr.setGrade("pass");
	    wtr.setOriginalLevel("2");
	    // run test and cleanup
	    String max_level = "3";
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    String actual_word_level = jdom2.changeWordLevel(wtm, wtr, max_level);
	    jdom2.writeDocument(duplicate_path, encoding);
	    String expected_word_level = new String("3");
	    duplicate_file.delete();
	    assertEquals(expected_word_level, actual_word_level);
    }
    
    /**
    Change the first test_user/word2 which has the following word:
    <text>text</text>
		<definition>def</definition>
		<level>1</level>
		<writing-level>1</writing-level>
		<reading-level>1</reading-level>
		<test><date>Mon Apr 11 09:33:15 PDT 2005</date><file>level 0 reading.test</file><grade>pass</grade></test>
		<test><date>Thu May 05 15:43:13 PDT 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
		   /*
	WordTestMemory
		   private String category;
		   private String type;
		   private String date;
		   private String score;
		   private String index;
		   private String number_correct;
		   private String level;
		   private String test_name;
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
		
	AllWordsTest
		// data kept on xml file
		private String text;
		private String definition;
		private String category;
		private String test_type;
		private String level;
		// users answers
		private String answer;
	*/
    public void testUpdateWordLevel()
    {
	    File path_file = new File("");
	    String user = new String("test_user");
	    String encoding = new String("euc-kr");
	    String base_path = new String(path_file.getAbsolutePath()+File.separator+"files"
		+File.separator+user+File.separator);
	    // original file and duplicates
	    File original_file = new File(base_path+"word1.xml");
	    String duplicate_path = new String(base_path+"word1copy.xml");
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    String text = new String("text"); // this is because it is a reading test
	    String grade = new String("pass");
	    String test_file = new String("level 1 reading.test");
	    String type = new String("reading");
	    String max_level = new String("3");
	    //String org_level = new String("1");
	    WordTestMemory wtm = new WordTestMemory();
	    wtm.setType("reading");
	    wtm.setDate("Thu May 05 15:43:13 PDT 2005");
	    wtm.setTestName("level 1 reading.test");
	    String score = new String("pass");
	    String index = new String("-1");
	    String number_correct = new String("1");
	    //String level = new String("2");
	    wtm.setScore(score);
	    wtm.setIndex(index);
	    wtm.setNumberCorrect(number_correct);
	    //wtm.setLevel(level);
	    
	    WordTestResult wtr = new WordTestResult();
	    wtr.setText(text);
	    wtr.setDefinition("def");
	    wtr.setOriginalText("text");
	    wtr.setGrade("pass");
	    String answer = new String("def");
	    //String grade = new String("pass");
	    //String level = new String("2");
	    int id = -1;
	    String original_definition = new String("def");
	    String original_level = new String("1");
	    wtr.setAnswer(answer);
	    wtr.setGrade(grade);
	    wtr.setId(id);
	    wtr.setOriginalDefinition(original_definition);
	    wtr.setOriginalLevel(original_level);

	    //System.out.println("----------------------------");
	    //System.out.println("---testUpdateWordLevel------");
	    //System.out.println("----------------------------");
	    // run test
	    JDOMSolution jdom = new JDOMSolution(duplicate_file);
	    Word word = jdom.findWord("text");
	    String org_level = Integer.toString(word.getReadingLevel());
	    String date = new Date().toString();
	    //dumpLog(Transformer.createTable(word));
// public String recordWordScore(word, grade, test_file, date, type, max_level, org_level)
	    jdom.recordWordScore(text, grade, test_file, date, type, max_level, org_level);
	    jdom.writeDocument(duplicate_path, encoding);
	    //System.out.println("----------------------------");
	    //dumpLog(jdom.getLog());
	    // check results
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word word2 = jdom.findWord("text");
	    //System.out.println("----------------------------");
	    //dumpLog(Transformer.createTable(word2));
	    String actual_word_level = Integer.toString(word2.getReadingLevel());
	    String expected_word_level = new String("2");
	    duplicate_file.delete();
	    assertEquals(expected_word_level, actual_word_level);
    }
    
    public void testParseFile()
    {
	    // setup word file
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+"test_user"+File.separator+"struts-config.xml");
	    File test_file = new File(path);
	    JDOMSolution jdom = new JDOMSolution(test_file);
	    // print out any problems
	    //dumpLog((jdom.getLog()));
	    //assertEquals(expected_def, actual_def);
    }
    
    public void testGetWordObjects()
    {
    	String user_id = new String("test_user");
	    String category = "october1";
	    //String search_id = "-4583428238372124649";
	    String search_id = "-2672985807382824350";
	    String expected = "recommend";
	    File path_file = new File("");
	    String root_path = path_file.getAbsolutePath();
	    
	    String file_w_o_ext = Domartin.getFileWithoutExtension(category); 
		if (file_w_o_ext.equals("-1"))
			{file_w_o_ext = category;}
		String path_to_word = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator+file_w_o_ext+".xml");
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator+category);
		File file = new File(path_to_word);
		//System.out.println("JDOMSolutionTest.testGetWordObjects: path "+path_to_word);
		//System.out.println("JDOMSolutionTest.testGetWordObjects: exists? "+file.exists());
		JDOMSolution jdom = new JDOMSolution(file);
		Word word = jdom.findWordFromId(search_id);
		//System.out.println("JDOMSolution log -----------");
		//dumpLog(jdom.getLog());
		//System.out.println("JDOMSolution log -----------");
		String actual = word.getDefinition();
		long actual_id = word.getId();
		long expected_id = Long.parseLong("-2672985807382824350");
		assertEquals(expected_id, actual_id);
    }
    
    public void testAddTestingOptiuons()
    {
    	System.out.println("testAddTestingOptiuons");
	    String file_name = new String("August.xml");
	    String user_name = new String("WLTD files");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String file_w_o_ext = Domartin.getFileWithoutExtension(file_name);
	    if (file_w_o_ext.equals("-1"))
	    {
	    	file_w_o_ext = file_name;
	    }
	    String path_to_words = new String(current_dir+File.separator+"files"+File.separator+user_name+File.separator+file_w_o_ext+".xml");
	    File words_file = new File(path_to_words);
	    String [] testing_options = new String [1];
	    long now = new Date().getTime();
	    String expected = ("fake_option-"+now);
	    String encoding = "euc-kr";
	    String word_id = "-4422046309217876055";
	    String actual = "";
	    testing_options[0] = expected;
	    JDOMSolution jdom = new JDOMSolution(words_file);
	    jdom.addTestingOptiuons(word_id, path_to_words, encoding, testing_options);
	    // now get the word again and see if the option was added.
	    jdom = new JDOMSolution(words_file);
	    Word changed_word = new Word();
	    try
	    {
	    	changed_word = jdom.findWordFromId(word_id);
	    	 String [] options = changed_word.getTestingOptions();
	 	    for (int i = 0; i < options.length; i++)
	 	    {
	 	    	String option = options[i];
	 	    	System.out.println("option "+i+" "+option);
	 	    	if (option.equals(expected))
	 	    	{
	 	    		actual = option;
	 	    	}
	 	    }
	    } catch (java.lang.NullPointerException npe)
	    {
	    	System.out.println("NBPE");
	    	npe.printStackTrace();
	    }
	    System.out.println("JDOMSolution log -----------");
	    dumpLog(jdom.getLog());
	    assertEquals(expected, actual);
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
    
    private void printLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
    }
    
    private void dumpHash(Hashtable log)
    {
	    /*
	    Enumeration keys = log.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" - "+val);
	    }
	    */

    }

    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(ScoringTest.class);
    }

}

