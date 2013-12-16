package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import org.catechis.constants.Constants;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordTestRecordOptions;
import org.catechis.Domartin;
import org.catechis.Transformer;
import java.io.File;
import org.jdom.Element;
//import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
//import java.text.DateFormat;
import org.catechis.interfaces.TestRecords;

// unchecked pasted-in umports
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.Locale;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import org.catechis.Scoring;
import org.catechis.Domartin;
import org.catechis.dto.Word;
//import org.catechis.Transformer; // repeat
import org.catechis.EncodeString;
import org.catechis.JDOMSolution;
import org.catechis.dto.SessionsReport;
import org.catechis.dto.Test;
import org.catechis.dto.Momento;
import org.catechis.dto.TestStats;
import org.catechis.dto.WordStats;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.RatesOfForgetting;
import org.catechis.dto.WordLastTestDates;
import org.catechis.interfaces.Categories;

/**
<p>This class stores and retrieves info from tests in two files,
 daily reading tests.record, and another one for writing, as well as the Momento
 * object and other items relating to the applications actions being performed.
 * <p>Be careful to pass in the context path to the constructor to use some methods like getMomentoObject.
 * Most methods are designed to take the path as an argument, however.
<p>These lists can also be used to store a list of missed words that are
* for instance above level 0, so that they will not show up on the weekly
* word list, unless they have failed a test recently.
<p>WordTestMemory
<p>private String category;
<p>	private String type;
<p>	private String date;
<p>	private String score;
<p>	private String index;
<p>	private String number_correct;
<p>	private String level;
<p>	private String test_name;
<p>	
*<p>
*/
public class FileTestRecords implements TestRecords
{
	
	private Vector log;
	private String root_path;
	
	/**This is the full path and file name used by loadDocument().*/
	private String name; 
	
	private Document doc;
	/** Not sure if this is necessary, but it was used in JDOMSolution, and we
	* were having problems so went back to the legacy code that worked as a
	* working comparison... */
	private EncodeString encoder;
	private String encoding;
	
	private int grand_index;
	
	private int daily_session_tests;
	
	private long daily_session_start_time;
	
	/** The number of words at each reading and writing level loaded after getAndResetTestsStatus and getTestsStatus. */
	Hashtable reading_levels;
	Hashtable writing_levels;
	Hashtable reading_and_writing_levels;
	
	private SessionsReport sessions_report;
	
	private int total_words;
	
	/**This no args constructor is  to open up the utility methods for use.*/
	public FileTestRecords()
	{
		log = new Vector();
	}
	
	/**This constructor allows the root path to get files from.*/
	public FileTestRecords(String _root_path)
	{
		this.root_path = _root_path;
		log = new Vector();
		log.add("FileTestRecords.<init> _root_path "+_root_path);
		log.add("FileTestRecords.<init>  root_path "+this.root_path);
	}
	
	/** This is the constructor from JDOMSolution */
	public FileTestRecords(File file)
	{
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
			log.add(j.toString());
		} catch (java.io.IOException i)
		{
			log.add(i.toString());
		}
	}

	/**
	*<p>Record a single DailyWordTest from a WordTestMemory object.
	*<p>Basically unbind the object into and element and add it to a
	* file name daily reading/writing tests.test in the /files/user/ folder. 
	*/
	public void addWordTestMemory(WordTestMemory wtm, String user_name, String root_path)
	{
		String type = wtm.getType();
		String file_name = ("daily "+type+" tests.record");
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File file = new File(path_to_test);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element e = createWordTestMemoryElement(wtm); 
		try
		{
			root.addContent(e);
		} catch (java.lang.NullPointerException npe)
		{}
		writeDocument(doc, path_to_test);
	}
	
	private Element createWordTestMemoryElement(WordTestMemory wtm)
	{
	    Element test = new Element("test");
	    Element cat = new Element("category");
	    Element type = new Element("type");
	    Element date = new Element("date");
	    Element score = new Element("score");
	    Element index = new Element("index");
	    Element num_c = new Element("number_correct");
	    //Element level = new Element("level");
	    Element te_na = new Element("test_name");
	    cat.addContent(wtm.getCategory());
	    type.addContent(wtm.getType());
	    date.addContent(wtm.getDate());
	    score.addContent(wtm.getScore());
	    index.addContent(wtm.getIndex());
	    num_c.addContent(wtm.getNumberCorrect());
	    //level.addContent(wtm.getLevel());
	    te_na.addContent(wtm.getTestName());
	    test.addContent(cat);
	    test.addContent(type);
	    test.addContent(date);
	    test.addContent(score);
	    test.addContent(index);
	    test.addContent(num_c);
	    //test.addContent(level);
	    test.addContent(te_na);
	    return test;
	}
	
	/**
	*<p>Record a single DailyWordTest from a WordTestMemory object.
	*<p>Basically unbind the object into an element and add it to a
	* file name daily reading/writing tests.record in the /files/user/ folder. 
	*<p>The text object is saved in bytes, and the date is saved as time in
	* a long.  These transformations happen within this method, so that
	* the rest of the system can keep on using encoded text and string dates
	* as it was first designed to use.
	*<p>WordTestResult (set in DailyTestResultAction)
	  *<p>String text;
	  *<p>String definition;
	  *<p>String answer;
	  *<p>String grade;
	  *<p>String level;		  - not needed
	  *<p>int id;			  - not needed
	  *<p>String original_text;
	  *<p>String original_definition;
	  *<p>String original_level;	  - not needed
	  *<p>String encoding; 		  - not needed 
	  *<p>String date;
	*<P>THE OBJECT WordTestRecordOptions is used to hold the options to decided what records should be saved.
	  *<p>private String type;
	  *<p>private String user_name;
	  *<p>private String root_path;
	  *<p>private boolean record_failed_tests;
	  *<p>private boolean record_passed_tests;
	  *<p>private String record_exclude_level;
	  *<p>private int record_limit;
	*/  
	public void addDailyTestRecord(WordTestResult wtr, WordTestRecordOptions wtro)
	{
		log.add("addDailyTestRecord");
		boolean add_test = addDailyTestRecordIsRequired(wtr, wtro);
		if (add_test == true)
		{
			String type = wtro.getType();
			String root_path = wtro.getRootPath();
			String user_name = wtro.getUserName();
			String file_name = ("daily "+type+" tests.record");
			name = new String(root_path+File.separator
				+"files"+File.separator+user_name+File.separator+file_name);
			log.add("addDailyTestRecord path "+name);
			//File file = new File(path_to_test);
			encoding = wtr.getEncoding();
			loadDocument();
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
				date.addContent(Transformer.getLongDateAsString(wtr.getDate()));	// the only other thing not in wtr
			} catch (java.lang.NullPointerException npe)
			{
				date.addContent(wtr.getDate());
			}
			text.addContent(wtr.getText());				// Transformer.getByteString() worked for a while then started causeing a npe
			def.addContent(wtr.getDefinition());
			if (type.equals("writing"))
			{
				answer.addContent(wtr.getAnswer());
			} else 
			{
				answer.addContent(wtr.getAnswer());
			}
			grade.addContent(wtr.getGrade());
			new_l.addContent(wtr.getLevel());
			org_l.addContent(wtr.getOriginalLevel());
			reversed.addContent("no");			// used later if the test score is reversed
			word_id.addContent(Long.toString(wtr.getWordId()));
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
			writeDocument(); //
		} else 
		{ 
			/*do test should not be added */ 
			log.add("test not recorded because it failed addDailyTestRecordIsRequired");
		}
	}
	
	private void createTestElement()
	{
		
	}
	
	/**Properties in the argument objects:
	*<p>WordTestResult (set in DailyTestResultAction)
	  *<p>String text;
	  *<p>String definition;
	  *<p>String answer;
	  *<p>String grade;
	  *<p>String level;		  - not needed
	  *<p>int id;			  - not needed
	  *<p>String original_text;
	  *<p>String original_definition;
	  *<p>String original_level;	  - not needed
	  *<p>String encoding; 		  - not needed 
	  *<p>String date;
	*<P>THE OBJECT WordTestRecordOptions is used to hold the options to decided what records should be saved.
	  *<p>private String type;
	  *<p>private String user_name;
	  *<p>private String root_path;
	  *<p>private boolean record_failed_tests;
	  *<p>private boolean record_passed_tests;
	  *<p>private String record_exclude_level;
	  *<p>private int record_limit;
	  */
	private boolean addDailyTestRecordIsRequired(WordTestResult wtr, WordTestRecordOptions wtro)
	{
		boolean true_if_saved = false;
		String word_level = wtr.getLevel();
		String exclude_level = wtro.getRecordExcludeLevel();
		if (word_level.equals(exclude_level))
		{
			// word_level = 0 and exclude_level = 0 (default value)
			log.add("do not record test because of exclude level");
		} else
		{
			String grade = wtr.getGrade();
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
	* Create an xml element with the root_child as its name,
	then add its subelemts based on hash keys, with their values as content.
	*/
	public void recordHashAsXML(Hashtable hash, String root_child, String path_to_file, String user_name)
	{
		Document doc = loadDocument(path_to_file);
		Element root = doc.getRootElement();
		Element new_child = new Element(root_child);
		for (Enumeration keys = hash.keys() ; keys.hasMoreElements() ;) 
		{
			String key = (String)keys.nextElement();
			String val = (String)hash.get(key);
			Element new_element = new Element(key);
			new_element.addContent(val);
			new_child.addContent(new_element);
		}
		root.addContent(new_child);
		if (encoding!=null)
		{
			writeDocumentWithEncoding(path_to_file);	// encoding should be set before this is called.
		} else
		{
			writeDocument(path_to_file);
		}
	}
	
	public void setEncoding(String _encoding)
	{
		encoding = _encoding;
	}
	
	/*
	public void returnHashFromXML(Hashtable hash, String root_child, String file_name, String user_name)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren(root_child);
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			if (str_text.equals(search_text))
			{
				word.setText(e.getChildText("text"));
				word.setDefinition(e.getChildText("definition"));
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
			}
			i++;
		}
	}
	*/
	
	/**
	*Load a vector of WordTestResults from a daily *type* tests.record file.
	*<p>A typical entry looks like this:
	*<p><test>
	*<p>	<date>1189742002000</date>
	*<p>	<text>-21-117-92-21-117-120-21-117-92</text>
	*<p>	<definition>To attend</definition>
	*<p>	<answer>to iron</answer>
	*<p>	<grade>fail</grade>
	*<p>	<new_level>2</new_level>
	*<p>	<original_level>3</original_level>
	*<p>	<reversed>no</reversed>
	*<p></test>
	*<p>Note the date is in milliseconds, and the text has been saved in bytes.
	*<p>If it were a writing test, then the answer would also be saved in bytes.
	*<p>These must be converted back into a string date and encoded strings.
	<p>The method populateWordTestResult looks at each test element, and filters out
	* tests that are excluded due to settings in the user options file.
	*<p>If a test is needed, it is then actually unbound into an object in unbindWTR.
	*<p>Properties from the user options hash are:
	*<p>record_failed_tests
	*<p>record_passed_tests
	*/
	public Vector getDailyTestRecords(String user_name, String type, String root_path, Hashtable user_opts)
	{
		Vector tests = new Vector();
		String file_name = ("daily "+type+" tests.record");
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File test_name = new File(path_to_test);
		//boolean pass = Boolean.getBoolean((String)user_opts.get("record_passed_tests"));
		//boolean fail = Boolean.getBoolean((String)user_opts.get("record_failed_tests"));
		//boolean fail = true;
		boolean changed = false;	// this could be put in the users option file later if we want...
		// these are the onse from the WordTestRecordsOptions constructor.
		boolean fail = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
		boolean pass = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    	//String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    	//int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
		
	    	Document doc = loadDocument(path_to_test);
		Element root = new Element("test");
		try
		{
			root = doc.getRootElement();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("no file at "+path_to_test);
		}
		List list = root.getChildren("test");
		int size = list.size();
		int i = 0;
		while(i<size)
		{
			Element record = (Element)list.get(i);
			WordTestResult wtr = populateWordTestResult(record, pass, fail, changed, type);
			tests.add(wtr);
			i++;
		}
		return tests;
	}
	
	/**
	*<p>Note level in the WordTestResult object is saved in the xml file as new_level...
	*<p>Also, text, and possibly answer (fore writing tests) bust me encoded from bytes,
	* and the date must be formatted from a long.
	*<p>We ignore the changed argument right now.  It seems we would need to know
	* if the user wants to record a failed test changed to a pass, or the opposite.
	*<p>Here is the xml record and the bean properties to show the difference:
	<date>1189742002000</date>
	*<p>	<text>-21-117-92-21-117-120-21-117-92</text>
	*<p>	<definition>To attend</definition>
	*<p>	<answer>to iron</answer>
	*<p>	<grade>fail</grade>
	*<p>	<new_level>2</new_level>
	*<p>	<original_level>3</original_level>
	*<p>	<reversed>no</reversed>
	*<p>
	*<p>private int id;
	*<p>private String original_text;
	*<p>private String original_definition;
	*<p>private String original_level;
	*<p>private String encoding;
	*<p>private String date;
	*/
	private WordTestResult populateWordTestResult(Element e, boolean pass, boolean fail, boolean changed, String type)
	{
		WordTestResult wtr = new WordTestResult();
		String grade = (String)e.getChildText("grade");
		boolean test = true;
		//log.add("-- "+pass+fail+changed+type+test);
		boolean reversed = Boolean.getBoolean((String)e.getChildText("reversed"));
		if (grade.equals("pass")&&pass==true)
		{
			// send back pass test if user option record_passed_tests
			// is set to true in the users option file.
			wtr = unbindWTR(e, type);
			return wtr;
		} else if (grade.equals("fail")&&fail==true)
		{
			// send back failed test if user option record_failed_tests
			// is set to true in the users option file.
			wtr = unbindWTR(e, type);
			return wtr;
		} else if (grade.equals("fail")&&changed==true)
		{
			// send back failed test if user option reversed
			// is set to true in the users option file.
			// In this case, a failed test has been reversed to a pass
			wtr = unbindWTR(e, type);
			return wtr;
		}
		return wtr;
	}
	
	
	/**
	*<p>Note level in the WordTestResult object is saved in the xml file as new_level...
	*<p>Also, text, and possibly answer (fore writing tests) bust me encoded from bytes,
	* and the date must be formatted from a long.
	*<p>We ignore the changed argument right now.  It seems we would need to know
	* if the user wants to record a failed test changed to a pass, or the opposite.
	*<p>Here is the xml record and the bean properties to show the difference:
	<date>1189742002000</date>
	*<p>	<text>-21-117-92-21-117-120-21-117-92</text>
	*<p>	<definition>To attend</definition>
	*<p>	<answer>to iron</answer>
	*<p>	<grade>fail</grade>
	*<p>	<new_level>2</new_level>
	*<p>	<original_level>3</original_level>
	*<p>	<reversed>no</reversed>
	*<p>
	*<p>private int id;
	*<p>private String original_text;
	*<p>private String original_definition;
	*<p>private String original_level;
	*<p>private String encoding;
	*<p>private String date;
	*<p>
	*<p>Members of WordTestRecordOptions:
	*<p>String type;
	*<p>String user_name;
	*<p>String root_path;
	*<p>boolean record_failed_tests;
	*<p>boolean record_passed_tests;
	*<p>String record_exclude_level;
	*<p>int record_limit;
	*<p>record_exclude_level  * this one is only useful when set to 0.
	*/
	private WordTestResult populateWordTestResult2(Element e, WordTestRecordOptions wtro)
	{
		WordTestResult wtr = new WordTestResult();
		String grade = (String)e.getChildText("grade");
		boolean rft = wtro.getRecordFailedTests();
		if (grade.equals("pass") && wtro.getRecordPassedTests() == true)
		{
			wtr = unbindWTR(e, wtro.getType());
			return wtr;
		} else if (grade.equals("fail") && wtro.getRecordFailedTests() == true)
		{
			wtr = unbindWTR(e, wtro.getType());
			return wtr;
		}
		return null;
	}
	
	/**
	*<p>text and answer are saved as bytes for writing tests, and the date is saved as a long
	* in string form of course.
	*/
	private WordTestResult unbindWTR(Element e, String type)
	{
		WordTestResult wtr = new WordTestResult();
		if (type.equals("reading"))
		{
			wtr.setAnswer((String)e.getChildText("answer"));
		} else
		{
			wtr.setAnswer((String)e.getChildText("answer"));
		}
		wtr.setText((String)e.getChildText("text"));
		wtr.setDefinition((String)e.getChildText("definition"));
		wtr.setLevel((String)e.getChildText("new_level"));
		wtr.setOriginalLevel((String)e.getChildText("original_level"));
		wtr.setDate(Transformer.getDateFromMilliseconds((String)e.getChildText("date")));
		wtr.setGrade((String)e.getChildText("grade"));
		try
		{
			wtr.setWordId(Long.parseLong((String)e.getChildText("word_id")));
		} catch (java.lang.NumberFormatException nfe)
		{
			log.add((String)e.getChildText("word_id"));
			wtr.setWordId(0);
		}
		return wtr;
	}
	
	/**
	*<p>This method is similar to getDailyTestRecords, except that is returns only the
	* text-definition pairs of WordTestResults that fit a specific criteria, 
	* such as exclude level 0 tests, because the user can get a list of level 0
	* words to study elsewhere, so what they want here are only words that
	* are outside this range.
	*<p>Extra properties can be added to the user_opts hash. In-fact, all non-record test
	* entries can be deleted to save memory, as they are mostly not used.
	*<p>Properties from the user options hash are held in:
	WordTestRecordOptions
	*<p>String type;
	*<p>String user_name;
	*<p>String root_path;
	*<p>boolean record_failed_tests;
	*<p>boolean record_passed_tests;
	*<p>String record_exclude_level;
	*<p>int record_limit;
	*<p>record_exclude_level  * this one is only useful when set to 0.
	* a range might be more useful, and can be added later if it is deemed useful
	* and important.
	*<p>Despite being called *record* as in the verb sense, these are also used for
	* retrieving records.
	*/
	public Hashtable getSpecificDailyTestRecords(WordTestRecordOptions wtro)
	{
		//append(Transformer.createTable(wtro));
		String type = wtro.getType();
		String user_name = wtro.getUserName();
		Hashtable words_defs = new Hashtable(); 
		String file_name = ("daily "+type+" tests.record");
		String path_to_test = new String(wtro.getRootPath()+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		Document doc = loadDocument(path_to_test);
		Element root = doc.getRootElement();           
		List list = root.getChildren("test");
		int size = list.size();
		int i = 0;
		while(i<size)
		{
			Element record = (Element)list.get(i);
			WordTestResult wtr = populateWordTestResult2(record, wtro);
			if (wtr == null) 
			{
				// no passed or failed tests need to be recorded;
				break;
			}
			String level = wtr.getLevel();
			String text = wtr.getText();
			String def = wtr.getDefinition();
			String rel = new String();
			//log.add(text+" "+def+" "+level);
			try
			{
				rel = wtro.getRecordExcludeLevel();
				if (level.equals(rel))
				{ 
					/* exclude record */ 
				} else
				{
					words_defs.put(wtr.getText(), wtr.getDefinition());
				}
			} catch (java.lang.NullPointerException npe)
			{
				log.add("level "+level+" rel "+rel);
			}
			i++;
		}
		return words_defs;
	}
	
	/**
	*<p>This method gets a vector of word test memory beans which are used when
	* a test is modified, or reversed by a user.
	*/
	public Vector getWordTestMemoryRecords(String user_name, String type, String root_path)
	{
		Vector tests = new Vector();
		String file_name = ("daily "+type+" tests.record");
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		//log.add("FileTestRecords.getDailyTestRecords path "+path_to_test);
		File test_name = new File(path_to_test);
		Document doc = loadDocument(path_to_test);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		//log.add("FileTestRecords.getDailyTestRecords list size = "+list.size());
		WordTestMemory wtm = new WordTestMemory();
		Class class_name = wtm.getClass();
		String obj_name = "wtm";
		//tests = Transformer.loadElements(list, obj_name, class_name);
		tests = populateWTM(list);
		//Vector loadElements(List list, String object_name, Class class_name)
		return tests;
	}	
	
	private Vector populateWTM(List list)
	{
		Vector wtm_tests = new Vector();
		int size= list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			WordTestMemory wtm = new WordTestMemory();
			String cat = (String)e.getChildText("category");
			wtm.setCategory(cat);
			wtm.setType((String)e.getChildText("type"));
			wtm.setDate((String)e.getChildText("date"));
			wtm.setScore((String)e.getChildText("score"));
			wtm.setIndex((String)e.getChildText("index"));
			wtm.setNumberCorrect((String)e.getChildText("number_correct"));
			wtm.setTestName((String)e.getChildText("test_name"));
			try
			{
				wtm.setWordId(Long.parseLong((String)e.getChildText("word_id")));
			} catch (java.lang.NumberFormatException nfe)
			{
				wtm.setWordId(0);
			}
			wtm_tests.add(wtm);
			i++;
		}
		return wtm_tests;
	}
	
	public Hashtable getLastUserHistoryHash(String user_name, String current_dir)
	{
		//log.add("FileTestRecords.getLastUserHistoryHash: here we are");
		Hashtable history = new Hashtable();
		//File path_file = new File("");
	        //String current_dir = path_file.getAbsolutePath();
		String file_name = (user_name+".hist");
		String path_to_test = new String(current_dir+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		//log.add("FileTestRecords.getLastUserHistoryHash: path "+path_to_test);
		File file = new File(path_to_test);
		Document doc = loadDocument(file);
		Element last_record = getLastRecord(doc);
		history = Transformer.elementIntoHash(last_record);
		//log.add("FileTestRecords.getLastUserHistoryHash: path again "+path_to_test);
		return history; 
	}
	
	public Element getLastRecord(Document doc)
	{
		Vector record = new Vector();
		Element root = doc.getRootElement();
		List list = root.getChildren();
		int size = list.size();
		//log.add("FileTestRecords.getLastRecord: list size = "+size);
		Element e = (Element)list.get(size-1);
		//record = Transformer.createTable(obj);
		return e; 
	}
	
	/**
	*<p>This method is called in the ChangeUpdateAction(defunct) or ChangeUpdateScoreAction to either delete a test
	* that was stored because the user has reversed the test result to pass, or add
	* a record if the opposite is the case.
	*<p>We need to consider if the test should have been save to determine if there is
	* a record to be deleted, or if a new record should be created.
	<p>WordTestResult
	  String text;
	  String definition;
	  String answer;
	  String grade;
	  String level;
	  int id;
	  String original_text;
	  String original_definition;
	  String original_level;
	  String encoding;
	  String date;
	  long word_id;
	*/
	public void reverseTestRecord(WordTestResult wtr, WordTestRecordOptions wtro)
	{
		long word_id = wtr.getWordId();
		log.add("reverseTestRecord search id "+word_id);
		String type = wtro.getType();
		//String root_path = wtro.getRootPath();
		String user_name = wtro.getUserName();
		String file_name = ("daily "+type+" tests.record");
		name = new String(root_path+File.separator
				+"files"+File.separator+user_name+File.separator+file_name);
		log.add("FileTestRecords.reverseTestRecord "+name);
		loadDocument();
		Element root = doc.getRootElement();
		List list = root.getChildren("test");
		int size = list.size();
		int i = 0;
		log.add("reverseTestRecord size "+size);
		boolean changed = false;
		while (i<size)
		{
			//log.add("reverseTestRecord i "+i);
			Element e = (Element)list.get(i);
			try
			{
				long this_id = Long.parseLong((String)e.getChildText("word_id")); 
				log.add("reverseTestRecord id found "+i+" "+this_id);
				if (word_id==this_id)
				{
					// delete record
					//int j = root.indexOf(e);
					//Element trash = (Element)root.removeContent(i);
					root.removeContent(e);
					log.add("reverseTestRecord id found "+i+" and killed it baby!");
					changed = true;
					break;
				}
			} catch (java.lang.NumberFormatException nfe)
			{
					// 
					log.add("nfe");
			}
			i++;
		}
		if (changed==true)
		{
			writeDocument();
		}
	}
	
	/**
	*<p>This method increments the tests and daily_session_tests elements
	* in the status.xml file.
	*<p>It is used in the DailyTestResultAction/IntegratedTestResultAction
	* and put in the session as daily_test_index.
	*<p>The subject argument represents a folder inside the users main folder.
	*/
	public int updateTestsStatus(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		// update the grand total of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		int index = Integer.parseInt(tests);
		index++;
		tests_e.setText(Integer.toString(index));
		// update the daily session number of tests
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		String daily_session_tests = (String)daily_session_tests_e.getText();
		int daily_session_index = Integer.parseInt(daily_session_tests);
		daily_session_index++;
		daily_session_tests_e.setText(Integer.toString(daily_session_index));
		writeDocument(doc, file);
		return index;
	}
	
	/**
	*<p>This method increments the grand_index, daily_session_tests,
	*word_test_elapsed_time and number_of_tests_in_week elements
	* in the status.record file.
	*<p>Used in IntegratedTestResultAction.
	*<p>The subject argument represents a folder inside the users main folder.
	*<P> calling methods must remember to call writeDocument() after all work is
	*done when using this method.
	*/
	public void updateSessionStatus()
	{
		Element root = doc.getRootElement();
		// update the grand total of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		grand_index = Integer.parseInt(tests);
		grand_index++;
		tests_e.setText(Integer.toString(grand_index));
		// update the daily session number of tests
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		String daily_session_tests = (String)daily_session_tests_e.getText();
		int daily_session_index = Integer.parseInt(daily_session_tests);
		daily_session_index++;
		daily_session_tests_e.setText(Integer.toString(daily_session_index));
		// load the word test start time and add the delta between then and now to the word_test_elapsed_time
		Element word_test_start_e = root.getChild("word_test_start");
		String word_test_start_s = (String)word_test_start_e.getText();
		long word_test_start = Long.parseLong(word_test_start_s);
		Date date = new Date();
		long now = date.getTime();
		long time_taken_for_test = now-word_test_start;
		Element word_test_elapsed_time_e = root.getChild("word_test_elapsed_time");
		long word_test_elapsed_time = Long.parseLong((String)word_test_elapsed_time_e.getText());
		word_test_elapsed_time = word_test_elapsed_time+time_taken_for_test;
		word_test_elapsed_time_e.setText(Long.toString(word_test_elapsed_time));
		Element number_of_tests_in_week_e = root.getChild("number_of_tests_in_week");
		int number_of_tests_in_week = Integer.parseInt((String)number_of_tests_in_week_e.getText());
		number_of_tests_in_week++;
		number_of_tests_in_week_e.setText(Integer.toString(number_of_tests_in_week));
	}
	
	/**
	*<p>This method increments the grand_index, daily_session_tests,
	*word_test_elapsed_time and number_of_tests_in_week elements
	* in the status.record file.
	*<p>Used in IntegratedTestResultAction.
	*<p>The subject argument represents a folder inside the users main folder.
	*<P> calling methods must remember to call writeDocument() after all work is
	*done when using this method.
	*/
	public void updateSessionStatusUsingSharedDoc()
	{
		Element root = doc.getRootElement();
		// update the grand total of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		grand_index = Integer.parseInt(tests);
		grand_index++;
		tests_e.setText(Integer.toString(grand_index));
		// update the daily session number of tests
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		String daily_session_tests = (String)daily_session_tests_e.getText();
		int daily_session_index = Integer.parseInt(daily_session_tests);
		daily_session_index++;
		daily_session_tests_e.setText(Integer.toString(daily_session_index));
		// load the word test start time and add the delta between then and now to the word_test_elapsed_time
		Element word_test_start_e = root.getChild("word_test_start");
		String word_test_start_s = (String)word_test_start_e.getText();
		long word_test_start = Long.parseLong(word_test_start_s);
		Date date = new Date();
		long now = date.getTime();
		long time_taken_for_test = now-word_test_start;
		Element word_test_elapsed_time_e = root.getChild("word_test_elapsed_time");
		long word_test_elapsed_time = Long.parseLong((String)word_test_elapsed_time_e.getText());
		word_test_elapsed_time = word_test_elapsed_time+time_taken_for_test;
		word_test_elapsed_time_e.setText(Long.toString(word_test_elapsed_time));
		Element number_of_tests_in_week_e = root.getChild("number_of_tests_in_week");
		int number_of_tests_in_week = Integer.parseInt((String)number_of_tests_in_week_e.getText());
		number_of_tests_in_week++;
		number_of_tests_in_week_e.setText(Integer.toString(number_of_tests_in_week));
	}
	
	/**
	 * Decrement words at *type* previous level, and increment words at new level.
	 * @param previous_level
	 * @param new_level
	 * @param type
	 */
	public void updateWordLevels(String previous_level, String new_level, String type)
	{
		Element root = doc.getRootElement();
		// decrement
		String decrement_level = "words_at_"+type+"_level_"+previous_level;
		log.add("FileTestRecords.updateWordLevels: decrement_level "+decrement_level);
		Element old_level_e = root.getChild(decrement_level);
		String old_level_s = old_level_e.getText();// npe
		log.add("FileTestRecords.updateWordLevels: from old_level "+old_level_s);
		List l = root.getChildren();
		int old_level = Integer.parseInt(old_level_s);
		old_level--;
		if (old_level < 0)
		{
			old_level = 0; // just in case there are no words at that level
		}
		log.add("FileTestRecords.updateWordLevels: to new level "+old_level);
		old_level_e.setText(Integer.toString(old_level));
		// increment
		String increment_level = "words_at_"+type+"_level_"+new_level;
		log.add("FileTestRecords.updateWordLevels: increment_level "+increment_level);
		Element new_level_e = root.getChild(increment_level);
		String new_level_s = new_level_e.getText();
		log.add("FileTestRecords.updateWordLevels: from new_level "+new_level_s);
		int new_word_level = Integer.parseInt(new_level_s);
		new_word_level++;
		log.add("FileTestRecords.updateWordLevels: to new level "+new_word_level);
		new_level_e.setText(Integer.toString(new_word_level));
		loadWordLevles(root); // refresh the whole set of data
		calculateAverages();
	}
	
	public void incrementRetiredWords(String user_id, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator
			+subject+File.separator+status_file);
		log.add("FileTestRecords.incrementRetiredWords: path "+path_to_file);
		File file = new File(path_to_file);
		log.add("FileTestRecords.incrementRetiredWords: exists? "+file.exists());
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element number_of_retired_words_e = root.getChild("number_of_retired_words");
		String number_of_retired_words_s = number_of_retired_words_e.getText();
		log.add("FileTestRecords.incrementRetiredWords: number_of_retired_words "+number_of_retired_words_s);
		int number_of_retired_words = Integer.parseInt(number_of_retired_words_s);
		number_of_retired_words++;
		number_of_retired_words_e.setText(Integer.toString(number_of_retired_words));
		writeDocument(doc, file);
	}
	
	/**
	 * Calculate the average level of all the words.
	 */
	private void calculateAverages()
	{
		int r_run_total = 0;
		int w_run_total = 0;
		int i = 0;
		while (i<4)
		{
			int reading_words = Integer.parseInt((String)reading_and_writing_levels.get("words_at_reading_level_"+i));
			reading_words = reading_words * i;
			r_run_total = r_run_total + reading_words;
			int writing_words = Integer.parseInt((String)reading_and_writing_levels.get("words_at_writing_level_"+i));
			writing_words = writing_words * i;
			w_run_total = w_run_total + writing_words;
			i++;
		}
		double d_num_of_words = new Double(Integer.toString(total_words)).doubleValue();
		double avg_reading_level = (r_run_total/d_num_of_words);
		double avg_writing_level = (w_run_total/d_num_of_words);
		reading_and_writing_levels.put("reading_average", Double.toString(avg_reading_level));
		reading_and_writing_levels.put("writing_average", Double.toString(avg_writing_level));
	}
	
	public void updateAllWordLevels(Vector all_reading_counts, 
			Vector all_writing_counts, int total_words, int total_retired_words, 
			String user_id, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator
			+subject+File.separator+status_file);
		name = path_to_file;
		log.add("FileTestRecords.updateAllWordLevels: path "+path_to_file);
		File file = new File(path_to_file);
		log.add("FileTestRecords.updateAllWordLevels: exists? "+file.exists());
		doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element words_at_reading_level_0 = root.getChild("words_at_reading_level_0");
		Element words_at_reading_level_1 = root.getChild("words_at_reading_level_1");
		Element words_at_reading_level_2 = root.getChild("words_at_reading_level_2");
		Element words_at_reading_level_3 = root.getChild("words_at_reading_level_3");
		Element words_at_writing_level_0 = root.getChild("words_at_writing_level_0");
		Element words_at_writing_level_1 = root.getChild("words_at_writing_level_1");
		Element words_at_writing_level_2 = root.getChild("words_at_writing_level_2");
		Element words_at_writing_level_3 = root.getChild("words_at_writing_level_3");
		words_at_reading_level_0.setText((String)all_reading_counts.get(0));
		words_at_reading_level_1.setText((String)all_reading_counts.get(1));
		words_at_reading_level_2.setText((String)all_reading_counts.get(2));
		words_at_reading_level_3.setText((String)all_reading_counts.get(3));
		words_at_writing_level_0.setText((String)all_writing_counts.get(0));
		words_at_writing_level_1.setText((String)all_writing_counts.get(1));
		words_at_writing_level_2.setText((String)all_writing_counts.get(2));
		words_at_writing_level_3.setText((String)all_writing_counts.get(3));
		Element number_of_tests = root.getChild("number_of_tests");
		number_of_tests.setText(Integer.toString(total_words));
		writeDocument(doc, "euc-kr");
	}
	
	/**
	 * This method is similar to updateAllWordLevels but adds the total retired words.
	 * 
	 * @param all_reading_counts
	 * @param all_writing_counts
	 * @param total_words
	 * @param retired_words
	 * @param user_id
	 * @param subject
	 */
	public void updateFullWordLevels(AllStatsHistory all_stats_history,
			String user_id, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator
			+subject+File.separator+status_file);
		log.add("FileTestRecords.updateFullWordLevels: "+path_to_file);
		File file = new File(path_to_file);
		name = path_to_file;
		doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element words_at_reading_level_0 = root.getChild("words_at_reading_level_0");
		Element words_at_reading_level_1 = root.getChild("words_at_reading_level_1");
		Element words_at_reading_level_2 = root.getChild("words_at_reading_level_2");
		Element words_at_reading_level_3 = root.getChild("words_at_reading_level_3");
		Element words_at_writing_level_0 = root.getChild("words_at_writing_level_0");
		Element words_at_writing_level_1 = root.getChild("words_at_writing_level_1");
		Element words_at_writing_level_2 = root.getChild("words_at_writing_level_2");
		Element words_at_writing_level_3 = root.getChild("words_at_writing_level_3");
		Element number_of_tests = root.getChild("number_of_tests");
		Element number_of_retired_words = root.getChild("number_of_retired_words");
		Element number_of_words = root.getChild("number_of_words");
		Element date = root.getChild("date");
		Vector reading_levels = all_stats_history.getReadingLevels();
		Vector writing_levels = all_stats_history.getWritingLevels();
		words_at_reading_level_0.setText((String)reading_levels.get(0));
		words_at_reading_level_1.setText((String)reading_levels.get(1));
		words_at_reading_level_2.setText((String)reading_levels.get(2));
		words_at_reading_level_3.setText((String)reading_levels.get(3));
		words_at_writing_level_0.setText((String)writing_levels.get(0));
		words_at_writing_level_1.setText((String)writing_levels.get(1));
		words_at_writing_level_2.setText((String)writing_levels.get(2));
		words_at_writing_level_3.setText((String)writing_levels.get(3));
		number_of_tests.setText(Integer.toString(all_stats_history.getNumberOfTests()));
		number_of_retired_words.setText(Integer.toString(all_stats_history.getNumberOfRetiredWords()));
		number_of_words.setText(Integer.toString(all_stats_history.getNumberOfWords()));
		date.setText((String)all_stats_history.getDate());
		log.add("number_of_tests "+all_stats_history.getNumberOfTests()+" retired "+all_stats_history.getNumberOfRetiredWords());
		writeDocument();
	}
	
	public void setTestStartTime()
	{
		Element root = doc.getRootElement();
		Element word_test_start_e = root.getChild("word_test_start");
		Date date = new Date();
		long now = date.getTime();
		word_test_start_e.setText(Long.toString(now));
	}
	
	
	/**
	*<p>This method resets the daily_session_tests element to zero
	* in the status.record file.
	* <p>It also stores a new login time to start a new session.
	*/
	public void resetDailySessionTestsStatus(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		daily_session_tests_e.setText("0");
		Date date = new Date();
		long now = date.getTime();
		Element daily_session_start_elem = root.getChild("daily_session_start");
		daily_session_start_elem.setText(Long.toString(now));
		writeDocument(doc, file);
	}
	
	/**
	*<p>This method saves the time in milliseconds at login in the *subject*status.record file.
	*/
	public void setDailySessionStartTime(String user_name, String subject, long time)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element daily_session_start_elem = root.getChild("daily_session_start");
		daily_session_start_elem.setText(Long.toString(time));
		writeDocument(doc, file);
	}
	
	/**
	*<p>This method saves the time in milliseconds at login in the *subject*status.record file.
	*/
	public void setDailySessionStartTimeUsingSharedDoc(long time)
	{
		Element root = doc.getRootElement();
		Element daily_session_start_elem = root.getChild("daily_session_start");
		daily_session_start_elem.setText(Long.toString(time));
	}
	
	/**
	*<p>Get the time of the first session of a 24 hour period saved in the status.record file.
	*  The format is a long representing milliseconds from the getTime call on a date.
	*/
	public long getDailySessionStartTime(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element daily_session_start_elem = root.getChild("daily_session_start");
		long time = Long.parseLong(daily_session_start_elem.getText());
		return time;
	}
	
	/**
	*<p>Get the time of the first session of a 24 hour period saved in the status.record file.
	*  The format is a long representing milliseconds from the getTime call on a date.
	*/
	public long getDailySessionStartTimeUsingSharedDoc()
	{
		Element root = doc.getRootElement();
		Element daily_session_start_elem = root.getChild("daily_session_start");
		long time = Long.parseLong(daily_session_start_elem.getText());
		return time;
	}
	
	/**
	*<p>This method saves the current place in the daily tests with i for the point
	* in the list of categories, and j as the point in the list of words in that
	* category.  The format is Mdaily_test_index>i:j</daily_test_index>.
	*<p>The subject arg represents a folder inside the users main folder.
	*/
	public void setDailyTestMark(String user_name, String subject, int i, int j)
	{
		String daily_test_index = Integer.toString(i)+":"+Integer.toString(j);
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element d_t_i = root.getChild("daily_test_index");
		d_t_i.setText(daily_test_index);
		writeDocument(doc, file);
	}
	
	/**
	*<p>This method gets current place in the daily tests with i for the point
	* in the list of categories, and j as the point in the list of words in that
	* category.  The format returned is a string like this i:j
	*<p>The subject arg represents a folder inside the users main folder.
	*<p>This was used for legacy DailyTestActions.
	*/
	public String getDailyTestMark(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element d_t_i = root.getChild("daily_test_index");
		String daily_test_index = d_t_i.getText();
		return daily_test_index;
	}
	
	
	/**
	*This status is the grand total of daily *type* or integrated tests run.
	*The daily_session_tests is also loaded and can get retrieved with the method:
	* getDailySessionTests()
	* <p>Replaced by getStatusRecord.
	*/
	public int getTestsStatus(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		// get the total number of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		int index = Integer.parseInt(tests);
		// get the no of daily session tests
		Element tests_e_daily_session_tests = root.getChild("daily_session_tests");
		String tests_daily_session_tests = (String)tests_e_daily_session_tests.getText();
		daily_session_tests = Integer.parseInt(tests_daily_session_tests);
		// daily_session_start_time
		Element tests_e_daily_session_start_time = root.getChild("daily_session_start");
		String tests_daily_session_start_time = (String)tests_e_daily_session_start_time.getText();
		daily_session_start_time = Long.parseLong(tests_daily_session_start_time);
		loadWordLevles(root);
		// return the total number of tests.
		return index;
	}
	
	/**
	*This status is the grand total of daily *type* or integrated tests run.
	*The daily_session_tests is also loaded and can get retrieved with the method:
	*<p>It also resets the test counter and session start time.
	*/
	public int getAndResetTestsStatus(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		// get the total number of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		int index = Integer.parseInt(tests);
		loadWordLevles(root);
		// reset the session info
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		daily_session_tests_e.setText("0");
		Date date = new Date();
		long now = date.getTime();
		String long_now = Long.toString(now);
		daily_session_tests = 0;
		Element daily_session_start_elem = root.getChild("daily_session_start");
		daily_session_start_elem.setText(long_now);
		daily_session_start_time = now;
		writeDocument(doc, path_to_file);
		// return the total number of tests.
		return index;
	}
	
	/**
	*This status is the grand total of daily *type* or integrated tests run.
	*The daily_session_tests is also loaded and can get retrieved with the method:
	*<p>It also resets the test counter and session start time.
	*/
	public void getAndResetTestsStatusUsingSharedDoc()
	{
		Element root = doc.getRootElement();
		// get the total number of tests
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		grand_index = Integer.parseInt(tests);
		loadWordLevles(root);
		// reset the session info
		Element daily_session_tests_e = root.getChild("daily_session_tests");
		daily_session_tests_e.setText("0");
		Date date = new Date();
		long now = date.getTime();
		String long_now = Long.toString(now);
		daily_session_tests = 0;
		Element daily_session_start_elem = root.getChild("daily_session_start");
		daily_session_start_elem.setText(long_now);
		daily_session_start_time = now;
	}
	
	/**
	*Reset the following members of the status.record:
	<number_of_sessions>0</number_of_sessions>
	<number_of_tests_in_week>355</number_of_tests_in_week>
	<word_test_elapsed_time>4705625</word_test_elapsed_time>
	<week_of_year>0</week_of_year>
	*/
	public void resetWeeklySessionStatus()
	{
		Element root = doc.getRootElement();
		// reset the session info
		Element number_of_sessions_e = root.getChild("number_of_sessions");
		number_of_sessions_e.setText("0");
		Element number_of_tests_in_week_e = root.getChild("number_of_tests_in_week");
		number_of_tests_in_week_e.setText("0");
		Element word_test_elapsed_time_e = root.getChild("word_test_elapsed_time");
		word_test_elapsed_time_e.setText("0");
		Element week_of_year_e = root.getChild("week_of_year");
		Calendar right_now = Calendar.getInstance();
		int week = right_now.get(Calendar.WEEK_OF_YEAR);
		week_of_year_e.setText(Integer.toString(week));
		writeDocument();
	}
	
	/**
	 * Called in updateWordLevels, getTestsStatus, and getAndResetTestsStatus.
	 * The reading_and_writing_levels Hashtable is set from the status.record, along with
	 number_of_words
	 reading_average
	 writing_average
	 date
	 and are then available thru getReadingAndWritingLevels().
	 * 
	 * @param root
	 */
	private void loadWordLevles(Element root)
	{
		reading_levels = new Hashtable();
		reading_levels.put(0, root.getChildText("words_at_reading_level_0"));
		reading_levels.put(1, root.getChildText("words_at_reading_level_1"));
		reading_levels.put(2, root.getChildText("words_at_reading_level_2"));
		reading_levels.put(3, root.getChildText("words_at_reading_level_3"));
		writing_levels = new Hashtable();
		writing_levels.put(0, root.getChildText("words_at_writing_level_0"));
		writing_levels.put(1, root.getChildText("words_at_writing_level_1"));
		writing_levels.put(2, root.getChildText("words_at_writing_level_2"));
		writing_levels.put(3, root.getChildText("words_at_writing_level_3"));
		// legacy
		/*
		reading_and_writing_levels = new Hashtable();
		Element last_record = getLastRecord(doc);
		reading_and_writing_levels = Transformer.elementIntoHash(last_record);
		*/
		reading_and_writing_levels = new Hashtable();
		reading_and_writing_levels.put("words_at_reading_level_0", root.getChildText("words_at_reading_level_0"));
		reading_and_writing_levels.put("words_at_reading_level_1", root.getChildText("words_at_reading_level_1"));
		reading_and_writing_levels.put("words_at_reading_level_2", root.getChildText("words_at_reading_level_2"));
		reading_and_writing_levels.put("words_at_reading_level_3", root.getChildText("words_at_reading_level_3"));
		reading_and_writing_levels.put("words_at_writing_level_0", root.getChildText("words_at_writing_level_0"));
		reading_and_writing_levels.put("words_at_writing_level_1", root.getChildText("words_at_writing_level_1"));
		reading_and_writing_levels.put("words_at_writing_level_2", root.getChildText("words_at_writing_level_2"));
		reading_and_writing_levels.put("words_at_writing_level_3", root.getChildText("words_at_writing_level_3"));
	
		reading_and_writing_levels.put("number_of_words", root.getChildText("number_of_words"));
		reading_and_writing_levels.put("reading_average", root.getChildText("reading_average"));
		reading_and_writing_levels.put("writing_average", root.getChildText("writing_average"));
		reading_and_writing_levels.put("date", root.getChildText("date"));
		reading_and_writing_levels.put("number_of_retired_words", root.getChildText("number_of_retired_words"));
		reading_and_writing_levels.put("week_of_year", root.getChildText("week_of_year"));
	}
	
	public Hashtable getReadingLevels()
	{
		return reading_levels;
	}
	
	public Hashtable getReadingAndWritingLevels()
	{
		return reading_and_writing_levels;
	}
	
	public int getGrandIndex()
	{
		return grand_index;
	}
	
	public Hashtable getWritingLevels()
	{
		return writing_levels;
	}
	
	/**
	 * Must be called after getTestsStatus
	 * @return
	 */
	public int getDailySessionTests()
	{
		return daily_session_tests;
	}
	
	/**
	 * Must be called after getTestsStatus
	 * @param _daily_session_tests
	 */
	public void setDailySessionTests(int _daily_session_tests)
	{
		daily_session_tests = _daily_session_tests;
	}
	
	/**
	 * Must be called after getTestsStatus
	 * @return
	 */
	public long getDailySessionStartTime()
	{
		return daily_session_start_time;
	}
	
	/**
	* The momento has the following properties:
	* action_name, action_time, action_id, action_type;
	* <p>Be careful to pass in the context path in the constructor to use.
	*/
	public Momento getMomentoObject(String user_name, String subject)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Momento m = new Momento();
		m.setActionName((String)root.getChildText("action_name"));
		m.setActionTime((String)root.getChildText("action_time"));
		m.setActionId((String)root.getChildText("action_id"));
		m.setActionType((String)root.getChildText("action_type"));
		loadWordLevles(root);
		unbindTestStatus(root);
		return m;
	}
	
	/**
	*Load the entire status.record file, return the momento and make the other
	* information available via get methods.
	* <p>Be careful to pass in the context path in the constructor to use.
	*/
	public Momento getStatusRecord(String user_name, String subject)
	{
		String status_file = "status.record";
		name = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		File file = new File(name);
		log.add("getStatusRecord: file name "+name+" exists? "+file.exists());
		loadDocument();
		Element root = doc.getRootElement();
		Momento m = unbindMomento(root); // momento 
		unbindTestStatus(root); // grand index, daily_session_tests, daily_session_start_time
		loadWordLevles(root); // reading and writing levels
		unbindSessionsReport(root); // test start time, elapsed time, sessions in a week, week of year, tests in week.
		Element total_words_e = root.getChild("number_of_words");
		String number_of_words_s = (String)total_words_e.getText();
		total_words = Integer.parseInt(number_of_words_s);
		reading_and_writing_levels.put("number_of_words" , number_of_words_s);
		return m;
	}
	
	/**
	 * test start time, elapsed time, sessions in a week, week of year, tests in week.
	 * @param root
	 */
	private void unbindSessionsReport(Element root)
	{
		sessions_report = new SessionsReport();
		sessions_report.setWordTestStart(Long.parseLong(root.getChildText("word_test_start")));
		sessions_report.setWordTestElapsedTime(Long.parseLong(root.getChildText("word_test_elapsed_time")));
		sessions_report.setNumberOfSessions(Integer.parseInt(root.getChildText("number_of_sessions")));
		sessions_report.setWeekOfYear(Integer.parseInt(root.getChildText("week_of_year")));
		sessions_report.setNumberOfTestsInWeek(Integer.parseInt(root.getChildText("number_of_tests_in_week")));
	}
	
	public void incrementSessionCount()
	{
		Element root = doc.getRootElement();
		Element number_of_sessions_e = root.getChild("number_of_sessions");
		String number_of_sessions_s = number_of_sessions_e.getText();
		int number_of_sessions = Integer.parseInt(number_of_sessions_s);
		number_of_sessions++;
		number_of_sessions_e.setText(Integer.toString(number_of_sessions));
		writeDocument();
	}
	
	public SessionsReport getSessionsReport()
	{
		return sessions_report;
	}
	
	private Momento unbindMomento(Element root)
	{
		Momento m = new Momento();
		m.setActionName((String)root.getChildText("action_name"));
		m.setActionTime((String)root.getChildText("action_time"));
		m.setActionId((String)root.getChildText("action_id"));
		m.setActionType((String)root.getChildText("action_type"));
		return m;
	}
	
	private void unbindTestStatus(Element root)
	{
		Element tests_e = root.getChild("tests");
		String tests = (String)tests_e.getText();
		grand_index = Integer.parseInt(tests);
		// get the no of daily session tests
		Element tests_e_daily_session_tests = root.getChild("daily_session_tests");
		String tests_daily_session_tests = (String)tests_e_daily_session_tests.getText();
		daily_session_tests = Integer.parseInt(tests_daily_session_tests);
		// daily_session_start_time
		Element tests_e_daily_session_start_time = root.getChild("daily_session_start");
		String tests_daily_session_start_time = (String)tests_e_daily_session_start_time.getText();
		daily_session_start_time = Long.parseLong(tests_daily_session_start_time);
		loadWordLevles(root);
		
	}
	
	/**
	*The momento object records the status of actions.  It has the following properties:
	action_name, action_time, action_id, action_type;
	* Unlike other methods here, the user does not need to call writeDocument().
	*/
	public void setMomentoObject(String user_name, String subject, Momento m)
	{
		String status_file = "status.record";
		String path_to_file = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator
			+subject+File.separator+status_file);
		//log.add(path_to_file);
		File file = new File(path_to_file);
		Document doc = loadDocument(file);
		Element root = doc.getRootElement();
		Element action_name = root.getChild("action_name");
		Element action_time = root.getChild("action_time");
		Element action_id = root.getChild("action_id");
		Element action_type = root.getChild("action_type");
		action_name.setText(m.getActionName());
		action_time.setText(m.getActionTime());
		action_id.setText(m.getActionId());
		action_type.setText(m.getActionType());
		writeDocument(doc, path_to_file);
	}
	
	/**
	*The momento object records the status of actions.  It has the following properties:
	action_name, action_time, action_id, action_type;
	* Unlike other methods here, the user does not need to call writeDocument().
	* <p>Same as above but the caller must remember to write the doc later.
	*/
	public void setMomentoObjectUsingSharedDoc(Momento m)
	{
		Element root = doc.getRootElement();
		Element action_name = root.getChild("action_name");
		Element action_time = root.getChild("action_time");
		Element action_id = root.getChild("action_id");
		Element action_type = root.getChild("action_type");
		action_name.setText(m.getActionName());
		action_time.setText(m.getActionTime());
		action_id.setText(m.getActionId());
		action_type.setText(m.getActionType());
	}
	
	public void clearTestRecord(String type, String user_name, String root_path)
	{
		String file_name = "daily "+type+" tests.record";
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		//log.add("FileTestRecords.clearTestRecord "+path_to_test);
		//Document doc = loadDocument(path_to_test);
		//Element root = doc.getRootElement();
		//root.removeContent();
		Element new_root = new Element("tests");
		Document doc = new Document(new_root);
		//doc.addContent(new_root);
		writeDocument(doc, path_to_test);
	}
	
	public void loadDocument()
	{
		log.add("FileTestRecords.loadDocument path "+name);
		log.add("FileTestRecords.loadDocument encoding "+encoding);
		File file = new File(name);
		//log = new Vector();
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add("FileTestRecords 1");
			log.add(j.toString());
		} catch (java.io.IOException i)
		{
			log.add("FileTestRecords 2");
			log.add(i.toString());
		}
	}
	
	public Document loadDocument(String path_to_file)
	{
		log.add("FileTestRecords.loadDocument path "+path_to_file);
		File file = new File(path_to_file);
		//log = new Vector();
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add("FileTestRecords 1");
			log.add(j.toString());
		} catch (java.io.IOException i)
		{
			log.add("FileTestRecords 2");
			log.add(i.toString());
		}
		return doc;
	}
	
	public Document loadDocument(File file)
	{
		//log.add("FileTestRecords file "+File.getAbsolutePath());
		Document doc = null;
		//log = new Vector();
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			//log.add("FileTestRecords 3");
			log.add(j.toString());
		} catch (java.io.IOException i)
		{
			//log.add("FileTestRecords 4");
			log.add(file.getAbsolutePath());
			log.add(i.toString());
		}
		return doc;
	}
	
	public void writeDocument(Document doc, String file_name)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			//log.add("FileTestRecords 5");
			e.printStackTrace();
		}
	}
	
	public void writeDocument(Document doc, File file)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file);
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			//log.add("FileTestRecords 6");
			e.printStackTrace();
		}
	}
	
	/**
	*<p>Write document with encoding.</p>
	*<p>XMLOutputter(java.lang.String indent, boolean newlines, java.lang.String encoding)</p>
	*/	
	public void writeDocumentWithEncoding(String file_name)
	{
		if (doc==null)
		{
			log.add("FileTestRecord.writeDocument: 2 doc is null");
		}
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file_name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			osw.close();
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeDocument(Document doc, String file_name, String encoding)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file_name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			osw.close();
		} catch (java.io.IOException e)
		{
			//log.add("FileTestRecords 5");
			e.printStackTrace();
		}
	}
	
	public void writeDocument()
	{
		if (encoding == null)
		{
			encoding = "euc-kr";
		}
		log.add("FileTestRecords.writeDocument: encoding "+encoding);
		log.add("FileTestRecords.writeDocument: name "+name);
		try
		{
			doc.getContentSize();
			log.add("FileTestRecords.writeDocument: doc size "+doc.getContentSize());
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			osw.close();
		} catch (java.io.IOException e)
		{
			log.add("FileTestRecords.writeDocument: encoding "+encoding);
			e.printStackTrace();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileTestRecords.writeDocument: name "+name);
			npe.printStackTrace();
		}
	}
	
	/**
	*Load a xml file of stored word test memory objects and un-serialize them
	into objects and return the list as a vector.
<test>
  <category>cat.xml</category>
  <type>reading</type>
  <date>Wed 04 Jul 2007</date>
  <score>pass</score>
  <index>-1</index>
  <number_correct>1</numer_correct>
  <level>0</level>
  <test_name>daily reading test.test</test_name>
 </test>
 These are the same as the WordTestMemory object. 
 	
	public Vector getDailyTestRecords(String user, String type, String root_path)
	{
		Vector dtr = new Vector();
		//String type = wtm.getType();
		String file_name = ("daily "+type+" tests.record");
		String path_to_test = new String(root_path+File.separator
			+"files"+File.separator+user_name+File.separator+file_name);
		File file = new File(path_to_test);
		Document doc = loadDocument(path_to_test);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		String obejct_name = daily_test_record;
		WordTestMemory wtm = new WordTestMemory();
		Class class_name = wtm.getClass();
		dtr = Transformer.loadElements(list, object_name, class_name);
		return dtr;
	}*/
	
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
	
	private void dumpLog(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" -		"+val);
	    }
    }
    
    public void resetLog()
    {
	    log = new Vector();
    }
    
}
