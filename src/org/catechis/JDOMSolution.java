package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import java.lang.NumberFormatException;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;

public class JDOMSolution
{

	private Storage store;
	private Document doc;
	private EncodeString encoder;
	private Reader reader;
	private Vector words;
	
	private Hashtable text_def_hash;
	private Hashtable sub_elements;
	private Hashtable word_levels;
	
	private String last_date;
	private String last_word;
	private HashSet last_words;
	
	private int index;
	private String level;
	private String org_level;
	
	private String file_name;
	
	
	/**When we adjust the word level, we need to retrieve the new level to
	* change the word grade hashtable used in the test result jsp.
	private String new_level;
	*We had a problem with the global variable for some reason.
	*/
	
	/**This member saves the last statistics object to check it with the next
	* one so that we don't put repeats in the histories list.  But if by chance the
	* stats look the same in the future, then that will be ok, which is why we
	* do it on the get side, not the set side.
	*/
	private AllStatsHistory last_all_stats_history;
	
	/**
	*This is our homegrown logging system, because system outs don't come thru
	* the JUnit and Ant setup.  Should look into that...not tonight!
	*/
	private Vector log;
	
	/**
	*This constructor was created for testing purposes.  We need to create a
	* test document by hand so that we can test the respective methods.
	*/
	public JDOMSolution(Document _doc, Storage _store)
	{
		this.doc = _doc;
		this.store = _store;
		log = new Vector();
	}

	public JDOMSolution(File file)
	{
		doc = null;
		log = new Vector();
		try
		{
			encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
			log.add("JDOMSolution<init>: built file? "+file.exists());
		} catch (org.jdom.JDOMException j)
		{
			log.add(j.toString());
		} catch (java.io.IOException i)
		{
			log.add(i.toString());
		}
	}
	
	public JDOMSolution(File _file, Storage _store)
	{
		store = _store;
		File file = _file;
		file_name = file.getName();
		doc = null;
		log = new Vector();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(file);
		} catch (org.jdom.JDOMException j)
		{
			store.loggit(j.toString());
		} catch (java.io.IOException i)
		{
			store.loggit(i.toString());
		}
	}
	
	/**
	*<p>This method retrieves a list of option style elements from an xml file with
	* the following format:</p>
	*<p>elements ("word")</p>
	*<p>  names content /names("text")</p>
	*<p>  values content /values("definition")</p>
	*<p>/elements</p>
	*<p>THen is creates a Hash like this keys=names hash(key)=value.</p>
	*/
	public Hashtable getWhateverHash(String elements, String names, String values)
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren(elements);
		int size = option_list.size();
		Hashtable options = new Hashtable();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText(names);
			String value  = e.getChildText(values);
			if (value==null) {value=new String("null");}
			String enc_name = encoder.encodeThis(name);
			String enc_value  = encoder.encodeThis(value);
			options.put(enc_name, enc_value);
			i++;
		}
		return options;
	}

	/**
	* This method reads a xml file with the format:
	* option name=xxx value=xxx and returns these in a hastable.
	*/
	public Hashtable getOptionsHash()
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren("option");
		int size = option_list.size();
		log.add("JDOMSolution.getOptionHash: list size "+size);
		Hashtable options = new Hashtable();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText("name");
			String value  = e.getChildText("value");
			options.put(name, value);
			i++;
		}
		return options;
	}
	
	/**
	*This method takes an element name, two sub-elements names from which it
	* constructs the key-value pairs stored in a hashtable.
	*/
	public Hashtable getWhateverKeyValHash(String option, String _name, String _value)
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren(option);
		int size = option_list.size();
		Hashtable options = new Hashtable();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText(_name);
			String value  = e.getChildText(_value);
			options.put(name, value);
			i++;
		}
		return options;
	}
	
	/** This methods retrieve the following from a Word object
	*	private String text;
		private String definition;
		private int writing_level;
		private int reading_level;
		date_of_entry
		test []
	*<p>We had a problem when there was no reading or writing level present for
	* some words, hence the two separate try blocks. This is a workaround, as
	* the system should add these attributes, or make sure they are there in some
	* other way.
	*/
	public Vector getWords()
	{
		Vector words = new Vector();
		Element root = doc.getRootElement();
		List all_words = root.getChildren("word");
		int size = all_words.size();
		log.add("JDOMSolution.getWords.size: "+size);
		String line = new String();
		int i = 0;
		while (i<size) 
		{
			Element e = (Element)all_words.get(i);
			Word word = new Word();
			try
			{
				word.setText(e.getChildText("text"));
				word.setDefinition(e.getChildText("definition"));
			} catch (java.lang.NullPointerException npe)
			{
				word.setText("null text");
				word.setDefinition("null definition");
			}
			try
			{
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
			} catch (java.lang.NumberFormatException nfe)
			{
				word.setWritingLevel(0);
			}
			try
			{
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
			} catch (java.lang.NumberFormatException nfe)
			{
				word.setReadingLevel(0);
			}
			try
			{
				word.setDateOfEntry(Long.parseLong(e.getChildText("date-of-entry")));
			} catch (java.lang.NumberFormatException nfe)
			{
				word.setDateOfEntry(0);
			}
			try
			{
				word.setId(Long.parseLong((String)e.getChildText("id")));
				//log.add("jdom.getWordsForTest: "+word.getDefinition()+" orig: "+e.getChildText("id"));
			} catch (java.lang.NumberFormatException nfe)
			{
				word.setId(0);
				log.add("jdom.getWords.nfe: "+word.getDefinition()+" ID IS NULL ");
			}
			List tests = e.getChildren("test");
			int number_of_tests = tests.size();
			int j = 0;  Test all_tests[] = new Test[number_of_tests];
			while (j<number_of_tests)
			{
				try
				{
					Element t = (Element)tests.get(j);
					Test test = new Test();
					test.setDate(t.getChildText("date"));
					test.setName(t.getChildText("file"));
					test.setGrade(t.getChildText("grade"));
					all_tests[j]=test;
				}
				catch (java.lang.IndexOutOfBoundsException ioobe)
				{
					break;
				}
				j++;
			}
			word = isRetired(word, e);
			word = getTestingOptions(word, e);
			word.setTests(all_tests);
			word.setCategory(file_name);
			words.add(word);
			i++;
		}
		return words;
	}
	
	private Word isRetired(Word word, Element e)
	{
		String retired_string = "false";
		try
		{
			retired_string = e.getChildText("retired");
			boolean retired = Boolean.parseBoolean(retired_string);
			word.setRetired(retired);
		} catch (java.lang.NumberFormatException nfe)
		{
			
		} catch (java.lang.NullPointerException npe)
		{
			
		}
		return word;
	}
	
	private Word getTestingOptions(Word word, Element e)
	{
		List <Element> testing_options_list = e.getChildren("testing_options");
		int size = testing_options_list.size();
		String [] testing_options = new String[size];
		try
		{
			for (int i=0;i<size;i++) 
			{
				Element option = (Element)testing_options_list.get(i);
				String testing_option = (String)option.getText();
				testing_options[i] = testing_option;
				
			}
		} catch (java.lang.NumberFormatException nfe)
		{
			
		} catch (java.lang.NullPointerException npe)
		{
			
		}
		word.setTestingOptions(testing_options);
		return word;
	}
	
	/**
	*Same as above but with category/file name set.
	*/
	public Vector getWordsForTest(String category)
	{
		log.add("JDOMSolution.getWordsForTest: ");
		Vector words = new Vector();
		try
		{
			Element root = doc.getRootElement();
			List all_words = root.getChildren("word");
			int size = all_words.size();
			String line = new String();
			int i = 0;
			while (i<size) 
			{
				Element e = (Element)all_words.get(i);
				Word word = new Word();
				try
				{
					word.setText(e.getChildText("text"));
					word.setDefinition(e.getChildText("definition"));
					log.add(i+" "+e.getChildText("text")+" "+e.getChildText("definition"));
				} catch (java.lang.NullPointerException npe)
				{
					word.setText("null text");
					word.setDefinition("null definition");
					log.add(i+" null");
				}
				try
				{
					word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
				} catch (java.lang.NumberFormatException nfe)
				{
					word.setWritingLevel(0);
				}
				try
				{
					word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				} catch (java.lang.NumberFormatException nfe)
				{
					word.setReadingLevel(0);
				}
				try
				{
					word.setDateOfEntry(Long.parseLong(e.getChildText("date-of-entry")));
				} catch (java.lang.NumberFormatException nfe)
				{
					word.setDateOfEntry(0);
				}
				try
				{
					word.setId(Long.parseLong((String)e.getChildText("id")));
					//log.add("jdom.getWordsForTest: "+word.getDefinition()+" orig: "+e.getChildText("id"));
				} catch (java.lang.NumberFormatException nfe)
				{
					word.setId(0);
					log.add("jdom.getWordsForTest.nfe: "+category+" "+word.getDefinition()+" ID IS NULL");
				}
				word.setRetired(new Boolean((String)e.getChildText("retired")).booleanValue()); 
				word = getTestingOptions(word, e);
				word.setCategory(category);
				List tests = e.getChildren("test");
				int number_of_tests = tests.size();
				int j = 0;  Test all_tests[] = new Test[number_of_tests];
				while (j<number_of_tests)
				{
					try
					{
						Element t = (Element)tests.get(j);
						Test test = new Test();
						test.setDate(t.getChildText("date"));
						test.setName(t.getChildText("file"));
						test.setGrade(t.getChildText("grade"));
						all_tests[j]=test;
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						log.add("JDOMSOolution.getWordsForTest: ioobe at test "+j+" tests "+number_of_tests);
						break;
					}
					j++;
				}
				word.setTests(all_tests);
				
				//log.add("jdom.word "+word.getDefinition()+" id "+word.getId());
				
				words.add(word);
				i++;
			}
		} catch (java.lang.NullPointerException npe)
		{
			words = null;
			log.add("npe "+file_name);
		}
		return words;
	}
	
	/**
	* This method is used in the getWordsForTest() method.  There may or may not be a retired
	* element so a null pointer error means retired = false;
	*<p>I remember reading that using errors as a design feature was not a good idea.
	* So should we add retired elements to every single word file when for a long time a word wont need it?
	* Not sure about the performance trade offs when this class is bloated and horrible to begin with...
	*/
	private boolean getRetiredElement(Element e)
	{
		boolean retired = false;
		try
		{
			retired = Boolean.getBoolean((String)e.getChildText("retired"));
		} catch (java.lang.NullPointerException npe)
		{
			retired = false;
		}
		return retired;
	}
	
	/**
	*<p>Helper method used in getFilteredWords.
	*/
	private int getLevelMin(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_min = levels.substring(0,slash);
		int min = Integer.parseInt(str_min);
		return min;
	}
	
	/**
	*<p>Helper method used in getFilteredWords.
	*/
	private int getLevelMax(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_max = levels.substring(slash+1, levels.length());
		int max = Integer.parseInt(str_max);
		return max;
	}
	
	/** This method, like its brother, gets the following from a Word object
	* filtered/screened according to properties set in the WordFilter object
	* passed in.
	*<p>IF any of the Word properties are null, then that property is set with
	* a string saying null instead of risking a null object, or 0 if it is an int.
	*<p>If filter properties are null, then it is assumed to be a default non-filtered
	* property..
	*/
	public Vector getFilteredWords(WordFilter word_filter)
	{
		Vector words = new Vector();
		Element root = doc.getRootElement();
		List all_words = root.getChildren("word");
		int size = all_words.size();
		String line = new String();
		int i = 0;
		while (i<size) 
		{
			Element e = (Element)all_words.get(i);
			Word word = new Word();
			word = checkForNull(e);
			List tests = e.getChildren("test");
			int number_of_tests = tests.size();
			int j = 0;  Test all_tests[] = new Test[number_of_tests];
			while (i<number_of_tests)
			{
				try
				{
					Element t = (Element)tests.get(j);
					Test test = new Test();
					test.setDate(t.getChildText("date"));
					test.setName(t.getChildText("file"));
					test.setGrade(t.getChildText("grade"));
					all_tests[j]=test;
				}
				catch (java.lang.IndexOutOfBoundsException ioobe)
				{
					break;
				}
				j++;
			}
			word.setTests(all_tests);
			if (filterWord(word, word_filter, i))
			{
				// word passes all tests if returns true
				words.add(word);
				i++;
			} else
			{
				i++;
			}
		}
		return words;
	}
	
	/**
	*The actual filtering of word objects happens here.
	*<p>Here are some of the properties we set in the WordFilter object.
	    <p>word_filter.setStartIndex(0);
	    <p>word_filter.setMinMaxRange("0-0");
	    <p>word_filter.setType("reading");
	    <p>word_filter.setCategory("october.xml");
	*<p>If properties have not been set in the filter object, then they are
	* skipped by using a try/catch block.
	*/
	private boolean filterWord(Word word, WordFilter word_filter, int index)
	{
		boolean pass = false;
		// Start Index
		try
		{
			if (index>word_filter.getStartIndex())
			{
				pass = true;
			} else
			{
				pass = false;
			}
		} catch (java.lang.NullPointerException nt)
		{}
		
		// Writing Level
		try
		{
			if (word_filter.getType().equals("writing"))
			{
				int word_level = word.getWritingLevel();
				int min_level  = getLevelMin(word_filter.getMinMaxRange());
				int max_level  = getLevelMax(word_filter.getMinMaxRange());
				if (word_level>=min_level&&word_level<=max_level)
				{
					pass = true;
					return pass;
				}
			} else 
			{
				pass = false;
			}
		} catch (java.lang.NullPointerException nt)
		{}
		
		// Reading Level
		try
		{
			if (word_filter.getType().equals("reading"))
			{
				int word_level = word.getReadingLevel();
				int min_level  = getLevelMin(word_filter.getMinMaxRange());
				int max_level  = getLevelMax(word_filter.getMinMaxRange());
				if (word_level>=min_level&&word_level<=max_level)
				{
					pass = true;
					//System.out.println("JDOMSolution.filterWord: reading level "+word_level+" passed min "+min_level+" mix "+max_level);
				}
			} else 
			{
				pass = false;
			}
		} catch (java.lang.NullPointerException nt)
		{}
		return pass;
	}
	
	private Word checkForNull(Element e)
	{
		Word word = new Word();
		// --- filter null text
		try
		{
			word.setText(e.getChildText("text"));
		} catch (java.lang.NullPointerException nt)
		{
			word.setText("null");
		}
		// --- filter null definition
		try
		{
			word.setDefinition(e.getChildText("definition"));
		} catch (java.lang.NullPointerException nt)
		{
			word.setDefinition("null");
		}
		// --- filter null writing level
		try
		{
			word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
		} catch (java.lang.NumberFormatException nf)
		{
			word.setWritingLevel(0);
		}
		// --- filter null reading level
		try
		{
			word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
		} catch (java.lang.NumberFormatException nf)
		{
			word.setReadingLevel(0);
		}
		return word;
	}
	
	/**
	*<p>We create a history element (in the most redundant way possbile)
	* based on this xml schema:
	*<p><all_stats_history>
	*<p>	<date>Thu Oct 13 19:10:24 KST 2005</date>
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
	*<p>	<words_at_writing_level_3>57</words_at_writing_level_3>
	*added:
	*long session_start;
long session_end;
int number_of_session_tests;

	*<p></all_stats_history>
	*<p>This old version deosnt contain the last 24 hour period session information.
	*/
	public void addHistory(AllStatsHistory all_stats_history)
	{
		Element root = doc.getRootElement();
		// create parent element
		Element all_stats_history_elem = 	new Element("all_stats_history");
		// create child elements
		Element date = 						new Element("date");
		Element number_of_tests = 			new Element("number_of_tests");
		Element average_score = 			new Element("average_score");
		Element number_of_words = 			new Element("number_of_words");
		Element writing_average = 			new Element("writing_average");
		Element reading_average = 			new Element("reading_average");
		Element words_at_reading_level_0 = 	new Element("words_at_reading_level_0");
		Element words_at_reading_level_1 = 	new Element("words_at_reading_level_1");
		Element words_at_reading_level_2 = 	new Element("words_at_reading_level_2");
		Element words_at_reading_level_3 = 	new Element("words_at_reading_level_3");
		Element words_at_writing_level_0 = 	new Element("words_at_writing_level_0");
		Element words_at_writing_level_1 = 	new Element("words_at_writing_level_1");
		Element words_at_writing_level_2 = 	new Element("words_at_writing_level_2");
		Element words_at_writing_level_3 = 	new Element("words_at_writing_level_3");
		Element number_of_retired_words = 	new Element("number_of_retired_words");
		// fill child elements with data from the the object passed in
		date.addContent(all_stats_history.getDate());
		number_of_tests.addContent(Integer.toString(all_stats_history.getNumberOfTests()));
		average_score.addContent(Double.toString(all_stats_history.getAverageScore()));
		number_of_words.addContent(Integer.toString(all_stats_history.getNumberOfWords()));
		writing_average.addContent(Double.toString(all_stats_history.getWritingAverage()));
		reading_average.addContent(Double.toString(all_stats_history.getReadingAverage()));
		Vector reading_levels = all_stats_history.getReadingLevels();
		Vector writing_levels = all_stats_history.getWritingLevels();
		words_at_reading_level_0.addContent((String)reading_levels.get(0));
		words_at_reading_level_1.addContent((String)reading_levels.get(1));
		words_at_reading_level_2.addContent((String)reading_levels.get(2));
		words_at_reading_level_3.addContent((String)reading_levels.get(3));
		words_at_writing_level_0.addContent((String)writing_levels.get(0));
		words_at_writing_level_1.addContent((String)writing_levels.get(1));
		words_at_writing_level_2.addContent((String)writing_levels.get(2));
		words_at_writing_level_3.addContent((String)writing_levels.get(3));
		number_of_retired_words.addContent(Integer.toString(all_stats_history.getNumberOfRetiredWords()));
		// add chile elements to the parent
		all_stats_history_elem.addContent(date);
		all_stats_history_elem.addContent(number_of_tests);
		all_stats_history_elem.addContent(average_score);
		all_stats_history_elem.addContent(number_of_words);
		all_stats_history_elem.addContent(writing_average);
		all_stats_history_elem.addContent(reading_average);
		all_stats_history_elem.addContent(words_at_reading_level_0);
		all_stats_history_elem.addContent(words_at_reading_level_1);
		all_stats_history_elem.addContent(words_at_reading_level_2);
		all_stats_history_elem.addContent(words_at_reading_level_3);
		all_stats_history_elem.addContent(words_at_writing_level_0);
		all_stats_history_elem.addContent(words_at_writing_level_1);
		all_stats_history_elem.addContent(words_at_writing_level_2);
		all_stats_history_elem.addContent(words_at_writing_level_3);
		all_stats_history_elem.addContent(number_of_retired_words);
		// add the parent to the root element
		root.addContent(all_stats_history_elem);
	}
	
	/**
	 * 
	 * @param all_stats_history
	 */
	public void addSessionHistory(AllStatsHistory all_stats_history)
	{
		Element root = doc.getRootElement();
		// create parent element
		Element all_stats_history_elem = 	new Element("all_stats_history");
		// create child elements
		Element date = 						new Element("date");
		Element number_of_tests = 			new Element("number_of_tests");
		Element average_score = 			new Element("average_score");
		Element number_of_words = 			new Element("number_of_words");
		Element writing_average = 			new Element("writing_average");
		Element reading_average = 			new Element("reading_average");
		Element words_at_reading_level_0 = 	new Element("words_at_reading_level_0");
		Element words_at_reading_level_1 = 	new Element("words_at_reading_level_1");
		Element words_at_reading_level_2 = 	new Element("words_at_reading_level_2");
		Element words_at_reading_level_3 = 	new Element("words_at_reading_level_3");
		Element words_at_writing_level_0 = 	new Element("words_at_writing_level_0");
		Element words_at_writing_level_1 = 	new Element("words_at_writing_level_1");
		Element words_at_writing_level_2 = 	new Element("words_at_writing_level_2");
		Element words_at_writing_level_3 = 	new Element("words_at_writing_level_3");
		Element session_start = 			new Element("session_start");
		Element session_end = 				new Element("session_end");
		Element number_of_session_tests = 	new Element("number_of_session_tests");
		// fill child elements with data from the the object passed in
		date.addContent(all_stats_history.getDate());
		number_of_tests.addContent(Integer.toString(all_stats_history.getNumberOfTests()));
		average_score.addContent(Double.toString(all_stats_history.getAverageScore()));
		number_of_words.addContent(Integer.toString(all_stats_history.getNumberOfWords()));
		writing_average.addContent(Double.toString(all_stats_history.getWritingAverage()));
		reading_average.addContent(Double.toString(all_stats_history.getReadingAverage()));
		Vector reading_levels = all_stats_history.getReadingLevels();
		Vector writing_levels = all_stats_history.getWritingLevels();
		words_at_reading_level_0.addContent((String)reading_levels.get(0));
		words_at_reading_level_1.addContent((String)reading_levels.get(1));
		words_at_reading_level_2.addContent((String)reading_levels.get(2));
		words_at_reading_level_3.addContent((String)reading_levels.get(3));
		words_at_writing_level_0.addContent((String)writing_levels.get(0));
		words_at_writing_level_1.addContent((String)writing_levels.get(1));
		words_at_writing_level_2.addContent((String)writing_levels.get(2));
		words_at_writing_level_3.addContent((String)writing_levels.get(3));
		session_start.addContent(Long.toString(all_stats_history.getSessionStart()));
		session_end.addContent(Long.toString(all_stats_history.getSessionEnd()));
		number_of_session_tests.addContent(Integer.toString(all_stats_history.getNumberOfSessionTests()));
		// add chile elements to the parent
		all_stats_history_elem.addContent(date);
		all_stats_history_elem.addContent(number_of_tests);
		all_stats_history_elem.addContent(average_score);
		all_stats_history_elem.addContent(number_of_words);
		all_stats_history_elem.addContent(writing_average);
		all_stats_history_elem.addContent(reading_average);
		all_stats_history_elem.addContent(words_at_reading_level_0);
		all_stats_history_elem.addContent(words_at_reading_level_1);
		all_stats_history_elem.addContent(words_at_reading_level_2);
		all_stats_history_elem.addContent(words_at_reading_level_3);
		all_stats_history_elem.addContent(words_at_writing_level_0);
		all_stats_history_elem.addContent(words_at_writing_level_1);
		all_stats_history_elem.addContent(words_at_writing_level_2);
		all_stats_history_elem.addContent(words_at_writing_level_3);
		all_stats_history_elem.addContent(session_start);
		all_stats_history_elem.addContent(session_end);
		all_stats_history_elem.addContent(number_of_session_tests);
		// add the parent to the root element
		root.addContent(all_stats_history_elem);
	}

	/**
	*<p>Un-marshall, unbind, get all the statistics history from an xml.hist file.
	*/
	public Vector getHistory()
	{
		Vector all_stats_histories = new Vector();
		Element root = doc.getRootElement();
		List all_stats_history_elements = root.getChildren("all_stats_history");
		int size = all_stats_history_elements.size();
		int i = 0;
		while (i<size) 
		{
			Element e = (Element)all_stats_history_elements.get(i);
			AllStatsHistory all_stats_history = new AllStatsHistory();
			all_stats_history.setDate(e.getChildText("date"));
			all_stats_history.setNumberOfTests(Integer.parseInt(e.getChildText("number_of_tests")));
			all_stats_history.setAverageScore(Double.parseDouble(e.getChildText("average_score")));
			all_stats_history.setNumberOfWords(Integer.parseInt(e.getChildText("number_of_words")));
			all_stats_history.setWritingAverage(Double.parseDouble(e.getChildText("writing_average")));
			all_stats_history.setReadingAverage(Double.parseDouble(e.getChildText("reading_average")));
			Vector reading_levels = new Vector();
			Vector writing_levels = new Vector();
			reading_levels.add(0, e.getChildText("words_at_reading_level_0"));
			reading_levels.add(1, e.getChildText("words_at_reading_level_1"));
			reading_levels.add(2, e.getChildText("words_at_reading_level_2"));
			reading_levels.add(3, e.getChildText("words_at_reading_level_3"));
			writing_levels.add(0, e.getChildText("words_at_writing_level_0"));
			writing_levels.add(1, e.getChildText("words_at_writing_level_1"));
			writing_levels.add(2, e.getChildText("words_at_writing_level_2"));
			writing_levels.add(3, e.getChildText("words_at_writing_level_3"));
			try
			{
				all_stats_history.setSessionStart(Long.parseLong(e.getChildText("session_start")));
				all_stats_history.setSessionEnd(Long.parseLong(e.getChildText("session_end")));
				all_stats_history.setNumberOfSessionTests(Integer.parseInt(e.getChildText("number_of_session_tests")));
			} catch (java.lang.NumberFormatException nfe)
			{
				all_stats_history.setSessionStart(Long.parseLong("0"));
				all_stats_history.setSessionEnd(Long.parseLong("0"));
				all_stats_history.setNumberOfSessionTests(Integer.parseInt("0"));
				// old records don't have these elements
			}
			all_stats_history.setReadingLevels(reading_levels);
			all_stats_history.setWritingLevels(writing_levels);
			if (isUniqueHistory(all_stats_history))
			{
				all_stats_histories.add(all_stats_history);
			}
			i++;
			last_all_stats_history = all_stats_history;
		}
		return all_stats_histories;
	}
	
	/**
	*<p>Unmarshall, unbind, get all the statistics history from an xml.hist file.
	*/
	public Vector getSessionHistory()
	{
		Vector all_stats_histories = new Vector();
		Element root = doc.getRootElement();
		List all_stats_history_elements = root.getChildren("all_stats_history");
		int size = all_stats_history_elements.size();
		int i = 0;
		while (i<size) 
		{
			Element e = (Element)all_stats_history_elements.get(i);
			AllStatsHistory all_stats_history = new AllStatsHistory();
			all_stats_history.setDate(e.getChildText("date"));
			all_stats_history.setNumberOfTests(Integer.parseInt(e.getChildText("number_of_tests")));
			all_stats_history.setAverageScore(Double.parseDouble(e.getChildText("average_score")));
			all_stats_history.setNumberOfWords(Integer.parseInt(e.getChildText("number_of_words")));
			all_stats_history.setWritingAverage(Double.parseDouble(e.getChildText("writing_average")));
			all_stats_history.setReadingAverage(Double.parseDouble(e.getChildText("reading_average")));
			Vector reading_levels = new Vector();
			Vector writing_levels = new Vector();
			reading_levels.add(0, e.getChildText("words_at_reading_level_0"));
			reading_levels.add(1, e.getChildText("words_at_reading_level_1"));
			reading_levels.add(2, e.getChildText("words_at_reading_level_2"));
			reading_levels.add(3, e.getChildText("words_at_reading_level_3"));
			writing_levels.add(0, e.getChildText("words_at_writing_level_0"));
			writing_levels.add(1, e.getChildText("words_at_writing_level_1"));
			writing_levels.add(2, e.getChildText("words_at_writing_level_2"));
			writing_levels.add(3, e.getChildText("words_at_writing_level_3"));
			all_stats_history.setReadingLevels(reading_levels);
			all_stats_history.setWritingLevels(writing_levels);
			all_stats_history.setSessionStart(Long.parseLong(e.getChildText("session_start")));
			all_stats_history.setSessionEnd(Long.parseLong(e.getChildText("session_end")));
			all_stats_history.setNumberOfSessionTests(Integer.parseInt(e.getChildText("number_of_session_tests")));
			i++;
			last_all_stats_history = all_stats_history;
		}
		return all_stats_histories;
	}

	/**
	*<p>Replace word method replaces both word and definition properties.
	*<P>Formerly we encoded the text and definitions here, but after commenting
	* them out, it seemed to have no effect.  Here is the code we used
	* after the if (text.equals(original_text)) block:
	* //String enc_name   = encoder.encodeThis( name);
	* //String enc_value  = encoder.encodeThis(value);
	*
	*/
	public void replaceWord(String original_text, String new_text, String new_def)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String text = e.getChildText("text");
			if (text.equals(original_text))
			{
				Element child_text = e.getChild("text");
				Element child_definition = e.getChild("definition");
				child_text = child_text.setText(new_text);
				child_definition = child_definition.setText(new_def);
			}
			i++;
		}
	}
	
	/**
	*<p>Replace word method replaces both word and definition properties, 
	* and changes the reading/writing level either up or down.
	*<p>Just in case there are two different test with the same date and time
	* (What are the chances of that, really?  if the clock stops...?)
	* we should really make sure the test name and not only the date match.
	*<p>We have to enclose the test element checks in a try/catch block
	* because the word may have no tests yet.
	*<p>This method is not used right now, as we replace the word info and
	* change the level in different methods, so that the user can choose to
	* change the level, but not replace the text-definition, and vice versa.
	*/
	public void replaceWordAndChangeTest(WordTestMemory wtm, WordTestResult wtr)
	{
		Element root         = doc.getRootElement();
		List list            = root.getChildren("word");
		int i                = 0;
		while (i<list.size())
		{
			Element e = (Element)list.get(i);
			String text = e.getChildText("text");
			if (text.equals(wtr.getOriginalText()))
			{
				Element child_text = e.getChild("text");
				Element child_definition = e.getChild("definition");
				child_text.setText(wtr.getText());
				child_definition.setText(wtr.getDefinition());
				List tests = e.getChildren("test");
				int j = 0;
				while (i<tests.size())
				{
					try
					{
						Element t = (Element)tests.get(j);
						if (t.getChildText("date").equals(wtm.getDate()))
						{
							Element grade = t.getChild("grade");
							grade.setText(wtr.getGrade());
							adjustLevel(e, wtm.getType(), wtr.getGrade());
						}
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						break;
					}
					j++;
				}
			}
			i++;
		}
	}	
	
	/**
	*<p>This method changes the reading/writing level either up or down.
	*<p>The word test result must be changed from fail to pass to increment, and
	* the opposite for decrements.
	*/
	public String changeWordLevel(WordTestMemory wtm, WordTestResult wtr, String max_level)
	{
		String new_level = new String();
		String test_level= Domartin.getTestLevel(wtm.getTestName());
		Element root         = doc.getRootElement();
		List list            = root.getChildren("word");
		int i                = 0;
		while (i<list.size())
		{
			Element e = (Element)list.get(i);
			String text = e.getChildText("text");
			if (text.equals(wtr.getOriginalText()))
			{
				//log.add("JDOMSolution.changeWordtLevel: matched, looking for test "+wtm.getDate());
				Element child_text = e.getChild("text");
				Element child_definition = e.getChild("definition");
				new_level = getAdjustLevel(e, wtm.getType(), wtr.getGrade(), test_level, max_level, wtr.getOriginalLevel());
				List tests = e.getChildren("test"); 
				int j = 0;
				while (i<tests.size())
				{
					//log.add("JDOMSolution.changeWordtLevel: tests,size "+tests.size());
					try
					{
						Element t = (Element)tests.get(j);
						//log.add("JDOMSolution.changeWordtLevel: t.getChildText(date) "+t.getChildText("date"));
						if (t.getChildText("date").equals(wtm.getDate()))
						{
							Element grade = t.getChild("grade");
							grade.setText(wtr.getGrade());
							//new_level = getAdjustLevel(e, wtm.getType(), wtr.getGrade());
							replaceWordLevel(e, new_level, wtm.getType());
							//log.add("JDOMSolution.changeWordtLevel: got new level "+new_level);
							return new_level;
						}
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						log.add("JDOMSolution.changeWordtLevel: ioobe "+j);
						break;
					}
					j++;
				}
			}
			i++;
		}
		return new_level;
	}	
	
	private void replaceWordLevel(Element word, String level, String type)
	{
		String full_type = new String(type+"-level");
		Element level_elem = word.getChild(full_type);
		level_elem.setText(level);
	}
	
	
	/**
	* This is the third iteration of this method.  We get the test_level from
	* the test type (or category), then increment or decrement based on the test
	* result.
	*<p>If the test is at level 0, it should only be incremented once.
	*<p>If the test is 1 or higher, it gets incremented twice, once to 
	* bring it back to its pre-test level, and another to simulate a passed test.
	*/
	private String getAdjustLevel(Element word, String type, String grade, String test_level, String max_level, String original_level)
	{
		log.add("JDOMSoultion.getAdjusteLevel: -------------------- ");
		log.add("type grade test_level max_level");
		log.add(type+" "+grade+" "+test_level+" "+max_level);
		
		String level_type = new String(type+"-level");
		String new_level = new String();
		Element level_elem = word.getChild(level_type);
		String current_level = level_elem.getText();
		if (grade.equals("pass"))
		{
			//if (test_level.equals("0"))
			//{
			//	new_level = Domartin.incrementWordLevelLimited(test_level, max_level);
			//} else
			//{
			//	new_level = Domartin.incrementWordLevelLimited(test_level, max_level);
			//	new_level = Domartin.incrementWordLevelLimited(new_level, max_level);
			//}
			new_level = Domartin.incrementWordLevelLimited(original_level, max_level);
		} 
		else
		{
			new_level = Domartin.decrementWordLevel(test_level);
		}
		level_elem.setText(new_level);
		return new_level;
	}
	
	
	
	/**
	*<p>
	* This method increments or decrements (using grade) the reading 
	* or writing level (using type) of a word element.
	*<p>The level is incremented or decremented twice because if the user failed
	* a test for a word at level 1, then the word is now at level 0, so to reverse the
	* test we need to increment it up 2 to level 2.  However, if the original level
	* is only 0, then we only want to add 1, because words aren't allowed to go below 0.
	*<p>If the original level was 0 or 1, then the current level would be the same: 0.
	*
	*/
	private void adjustLevel(Element word, String type, String grade)
	{
		String level_type = new String(type+"-level");
		String new_level = new String();
		Element level_elem = word.getChild(level_type);
		String current_level = level_elem.getText();
		if (grade.equals("pass"))
		{
			new_level = Domartin.incrementWordLevel(current_level);
			if (current_level.equals("1"))
			{ 
				new_level = Domartin.incrementWordLevel(new_level);
			} else
			{
				// if the original level was 1 (thats after the test
			}
		} 
		else
		{
			new_level = Domartin.decrementWordLevel(current_level);
			new_level = Domartin.decrementWordLevel(new_level);
		}
		level_elem.setText(new_level);
		//level_elem.setText(new_level);
		//log.add("JDOMSolution.adjustLevel: current level "+current_level);
		//log.add("JDOMSolution.adjustLevel: new level "+new_level);
		//return new_level;
	}
	
	/**
	* Copy of previous method except it returns the new level value
	*/
	private String getAdjustLevel(Element word, String type, String grade)
	{
		String level_type = new String(type+"-level");
		String new_level = new String();
		Element level_elem = word.getChild(level_type);
		String current_level = level_elem.getText();
		if (grade.equals("pass"))
		{
			new_level = Domartin.incrementWordLevel(current_level);
			if (current_level.equals("0"))
			{ 
				// words at level 0 should only go up one level
			} else
			{
				new_level = Domartin.incrementWordLevel(new_level);
				//log.add("JDOMSolution.getAdjustLevel: inc2 "+new_level);
			}
		} 
		else
		{
			new_level = Domartin.decrementWordLevel(current_level);
			new_level = Domartin.decrementWordLevel(new_level);
			//log.add("JDOMSolution.getAdjustLevel: dec "+new_level);
		}
		level_elem.setText(new_level);
		//log.add("JDOMSolution.getAdjustLevel: current level "+current_level);
		//log.add("JDOMSolution.getAdjustLevel: new level "+new_level);
		return new_level;
	}
	
	/* We had a problem with this way of doing it.
	public String getNewWordLevel()
	{
		return new_level;
	}
	*/
	
	/**
	*<p>Normal find word method.  The argument is the text child element of a word
	* element.
	*/
	public Word findWord(String search_text)
	{
		boolean find_doe = false;
		Word word = new Word();
		Element root = new Element("null");
		try
		{
			root = doc.getRootElement();
		} catch (java.lang.NullPointerException npe)
		{
			log.add(npe.getMessage());
		}
		List list = root.getChildren("word");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			if (str_text.equals(search_text))
			{
				log.add("JDOMSolution.found: "+search_text);
				word.setText(e.getChildText("text"));
				word.setDefinition(e.getChildText("definition"));
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
				try
				{
					word.setDateOfEntry(Long.parseLong(e.getChildText("date-of-entry")));
				} catch (java.lang.NumberFormatException nfe)
				{
					log.add("JDOMSolution: doe nfe");
					find_doe = true;
				}
				List tests = e.getChildren("test");
				int t_s = tests.size();
				Test all_tests[] = new Test[t_s];
				log.add("test: "+t_s);
				int j = 0;
				while (j<tests.size())
				{
					log.add("JDOMSolution j "+j);
					String test_file = new String();
					String test_grade = new String();
					Test test = new Test();
					try
					{
						Element t = (Element)tests.get(j);
						test_file = (String)t.getChildText("file");
						test_grade = (String)t.getChildText("grade");
						test.setDate(t.getChildText("date"));
						test.setName(test_file);
						test.setGrade(test_grade);
						log.add("JDOMSolution.findWord: added test "+test_file+" grade "+test_grade);
						all_tests[j]=test;
						if (find_doe==true)
						{
							word.setDateOfEntry(Long.parseLong(getEarliestDate(t.getChildText("date"))));
						}
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						log.add("JDOMSolution.findWord: npe with test "+test_file+" grade "+test_grade);
						//break;
					}
					log.add("J "+j);
					append(Transformer.createTable(test));
					j++;
				}
				log.add("finito ");
				word.setTests(all_tests);
			}
			i++;
		}
		return word;
	}
	
/**
	*<p>Normal find word method.  The argument is the text child element of a word
	* element.
	*/
	public Element findWordElement(String search_text)
	{
		boolean find_doe = false;
		Element word_elem = null;
		Word word = new Word();
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			if (str_text.equals(search_text))
			{
				word_elem = e;
				i=size;
			}
			i++;
		}
		return word_elem;
	}	
	
	/**
	*This method will return the date of the first test.  We take one day off this to simulate the
	* date of first entry, as the early developer failed to capture this info from the start.
	*/
	public String getEarliestDate(String date)
	{
		return (new String("0"));
	}
	
	/**
	*<p>Normal find word method.  The argument is the text child element of a word
	* element.
	*/
	public Word findWordFromDefinition(String search_text)
	{
		Word word = new Word();
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("definition");
			if (str_text.equals(search_text))
			{
				word.setText(e.getChildText("text"));
				word.setDefinition(e.getChildText("definition"));
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
				try
				{
					word.setDateOfEntry(Long.parseLong(e.getChildText("date-of-entry")));
				} catch (java.lang.NumberFormatException nfe) 
				{
					Date date = new Date();
					word.setDateOfEntry(date.getTime());
				}
				List tests = e.getChildren("test");
				Test all_tests[] = new Test[tests.size()];
				int j = 0;
				while (j<tests.size())
				{
					try
					{
						Element t = (Element)tests.get(j);
						Test test = new Test();
						test.setDate(t.getChildText("date"));
						test.setName(t.getChildText("file"));
						test.setGrade(t.getChildText("grade"));
						all_tests[j]=test;
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						break;
					}
					j++;
				}
				word.setTests(all_tests);
			}
			i++;
		}
		return word;
	}
	
	/**
	*<p>Find word by id.
	*/
	public Word findWordFromId(String search_id)
	{
		Word word = new Word();
		Element root = doc.getRootElement(); 
		List list = root.getChildren("word");
		log.add("JDOMSolution.findWordFromId: list size "+list.size());
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_id = e.getChildText("id");
			if (str_id.equals(search_id))
			{
				log.add("JDOMSolution.findWordFromId: match "+search_id+" "+str_id);
				word.setText(e.getChildText("text"));
				word.setDefinition(e.getChildText("definition"));
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
				word.setId(Long.parseLong(e.getChildText("id")));
				try
				{
					word.setDateOfEntry(Long.parseLong(e.getChildText("date-of-entry")));
				} catch (java.lang.NumberFormatException nfe) 
				{
					Date date = new Date();
					word.setDateOfEntry(date.getTime());
				}
				List tests = e.getChildren("test");
				Test all_tests[] = new Test[tests.size()];
				int j = 0;
				while (j<tests.size())
				{
					try
					{
						Element t = (Element)tests.get(j);
						Test test = new Test();
						test.setDate(t.getChildText("date"));
						test.setName(t.getChildText("file"));
						test.setGrade(t.getChildText("grade"));
						all_tests[j]=test;
					}
					catch (java.lang.IndexOutOfBoundsException ioobe)
					{
						break;
					}
					j++;
				}
				word.setTests(all_tests);
				word = getTestingOptions(word, e);
				break;
			} else
			{
				log.add("JDOMSolution.findWordFromId: NO match "+search_id+" "+str_id);
			}
			i++;
		}
		return word;
	}
	
	/**
	*<p>Normal find word method.  The argument is the text child element of a word
	* element.
	*/
	public Word findWordWithoutTests(String search_text)
	{
		Word word = new Word();
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
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
		return word;
	}
	
	/**
	*Here we look in a test file to match the test date and then
	* replace it with the new test score.
	*<p>Were looking to change an element like this one:
	*<p>  <score>
	*      <grade>70</grade>
	*      <date>Mon Nov 01 22:26:22 PST 2004</date>
	*     </score>
	*<p>Note the responsibility for creating the new test score happens
	* in the Struts Action class ChangeUpdateAction, where it knows how many
	* words are in the test, and what the pass/fail of all the other words is.
	*/
	public void changeScore(String date, String new_score)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("score");
		int i = 0;
		//log.add("JDOMSolution.changeScore: search for date "+date);
		//log.add("JDOMSolution.changeScore: number of tests "+list.size());
		while (i<list.size())
		{
			Element e = (Element)list.get(i);
			if (e.getChildText("date").equals(date))
			{
				//log.add("JDOMSolution.changeScore: test date "+e.getChildText("date"));
				//log.add("JDOMSolution.changeScore: changing "+e.getChildText("grade")+" to "+new_score);
				Element score = e.getChild("grade");
				score.setText(new_score);
			}
			i++;
		}
	}
	
	public void addSingleWord(Word word, String encoding, String path, String user_id)
	{
		Element words = new Element("words");	
		doc = new Document(words);
		String id_file_name = path+File.separator+user_id+File.separator+word.getId()+".xml";
		writeDocument(id_file_name, encoding);
		log.add("addSingleWord: created new doc for path "+id_file_name);
		addWord(word);
		writeDocument(id_file_name, encoding);
	}
	
	/**
	*We need to create word object to add to the file passed in at creation time.
	*<p>The reading and writing levels are fixed at 0, but it would be easy to let the user
	* dictate other levels, as well as other elements.
	*/
	public void addWord(Word word_obj)
	{
		Element root = doc.getRootElement();
		Element word = new Element("word");
		Element text = new Element("text");
		Element def = new Element("definition");
		Element doe = new Element("date-of-entry");
		Element rl = new Element("reading-level");
		Element wl = new Element("writing-level");
		Element id = new Element("id");
		text.addContent(word_obj.getText());
		def.addContent(word_obj.getDefinition());
		doe.addContent(Long.toString(word_obj.getDateOfEntry()));
		rl.addContent("0");
		wl.addContent("0");
		id.addContent(Long.toString(word_obj.getId()));
		word.addContent(text);
		word.addContent(def);
		word.addContent(doe);
		word.addContent(rl);
		word.addContent(wl);
		word.addContent(id);
		word = addTests(word, word_obj);
		root.addContent(word);
		log.add("jdom.addWord: added "+word_obj.getDefinition());
	}
	
	/**
	// <test><date>Mon Apr 11 09:33:15 PDT 2005</date><file>level 1 reading.test</file><grade>pass</grade></test>
			// tests can have date,name,grade, type, level, milliseconds;
			// WordNextTestDatesTest only tests date, name, grade
	*/
	private Element addTests(Element word, Word word_obj)
	{
		try
		{
			Test tests [] = word_obj.getTests();
			int len = tests.length;
			int i = 0;
			while (i<len)
			{
				Test test = tests[i];
				String date = test.getDate();
				String name = test.getName();
				String grade = test.getGrade();
				Element test_element = new Element("test");
				Element date_element = new Element("date");
				Element name_element = new Element("name");
				Element grade_element = new Element("grade");
				date_element.addContent(date);
				name_element.addContent(name);
				grade_element.addContent(grade);
				test_element.addContent(date_element);
				test_element.addContent(name_element);
				test_element.addContent(grade_element);
				word.addContent(test_element);
				i++;
			}
		} catch(java.lang.NullPointerException npe)
		{
				// no tests to add
		}
		return word;
	}
	

	/**
	*<p>Change a word object.
	*<p>This has been a persistently annoying error.  When we first made this
	* method over a year ago, if didn't work.  Because content management is
	* not the purpose of this package, we let is slide.  But being able to edit
	* words when it is most needed, such as after a test seems too necessary to avoid.
	*<p>So we took another look and got the test in the FileStorageTest working.
	*<p>However, the JDOMSolution test continued to fail.
	*<p>Thats why the second method was created, to experiment with recording
	* the document within the edit method.
	*/
	public void editWord(Word old_word_obj, Word new_word_obj)
	{
		log.add("JDOMSolution.editWord");
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = old_word_obj.getText();
		String search_def  = old_word_obj.getDefinition();
		int size = list.size();
		log.add("list size "+size);
		log.add("search text "+search_text);
		log.add("search def "+search_def);
		log.add("new text "+new_word_obj.getText());
		log.add("new def "+new_word_obj.getDefinition());
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				log.add("JDOMSolution.editWord: found word at "+i);
				Element text = e.getChild("text");
				Element def  = e.getChild("definition");
				text.setText(new_word_obj.getText());
				def.setText(new_word_obj.getDefinition());
				//boolean removed_text = e.removeChild("text");
				//e.removeChild("definition");
				//Element added_text = (Element)e.addContent(text);	//returns parent
				//e.addContent(text);
				//e.addContent(def);
				//int index = root.indexOf(e);
				//root.removeContent(index);
				//root.setContent(e);
				//log.add("childe removed "+removed_text);
				//log.add("added text "+added_text.toString());
				break;
			}
			i++;
		}
	}
	
	
	/**
	*<p>Change a word object.
	*<p>See editWord for more notes.
	*/
	public void editWord2(Word old_word_obj, Word new_word_obj, String path, String encoding)
	{
		log.add("JDOMSolution.editWord");
		Element root = doc.getRootElement();
		Attribute root_encoding = root.getAttribute("encoding");
		String encoding_name = root_encoding.getValue();
		log.add("encoding "+root_encoding);
		List list = root.getChildren("word");
		String search_text = old_word_obj.getText();
		String search_def  = old_word_obj.getDefinition();
		int size = list.size();
		log.add("list size "+size);
		log.add("search text "+search_text);
		log.add("search def "+search_def);
		log.add("new text "+new_word_obj.getText());
		log.add("new def "+new_word_obj.getDefinition());
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				log.add("JDOMSolution.editWord: found word at "+i);
				//root.removeContent(e);
				Element text = e.getChild("text");
				Element def  = e.getChild("definition");
				Element changed_text = text.setText(new_word_obj.getText());
				Element changed_def = def.setText(new_word_obj.getDefinition());
				List kids = changed_text.getChildren();
				log.add("changed text has kids size "+kids.size());
				log.add("changed text "+changed_text.getText());
				boolean removed_text = e.removeChild("text");
				boolean removed_def = e.removeChild("definition");
				log.add("removed text "+changed_text.getText());
				log.add("removed def "+changed_text.getText());
				e.addContent(changed_text);
				e.addContent(changed_def);
				//boolean removed_text = e.removeChild("text");
				//e.removeChild("definition");
				//Element added_text = (Element)e.addContent(text);	//returns parent
				//e.setContent(text);
				//e.setContent(def);
				//int index = list.indexOf(e);
				//list.remove(index);
				//list.add(e);
				//log.add("childe removed "+removed_text);
				//log.add("added text "+added_text.toString());
				
				//Element new_text = e.getChild("text");
				//Element new_def  = e.getChild("definition");
				/*
				Element new_text = new Element("text");
				Element new_def  = new Element("definition");
				Element rl = e.getChild("reading-level");
				Element wl = e.getChild("writing-level");
				Element doe = e.getChild("date-of-entry");
				List tests = e.getChildren("tests");
				Element new_word = new Element("word");
				new_text.addContent(new_word_obj.getText());
				new_def.addContent(new_word_obj.getDefinition());
				new_word.addContent(new_text);
				new_word.addContent(new_def);
				try
				{
					new_word.addContent(rl);
				} catch (org.jdom.IllegalAddException iae)
				{
					log.add("iae rl");
				}
				try
				{
					new_word.addContent(wl);
				} catch (org.jdom.IllegalAddException iae)
				{
					log.add("iae wl");
				}
				try
				{
					new_word.addContent(doe);
				} catch (org.jdom.IllegalAddException iae)
				{
					log.add("iae doe");
				}
				try
				{
					new_word.addContent(tests);
				} catch (org.jdom.IllegalAddException iae)
				{
					log.add("iae tests");
				}
				Element new_vocab = new Element("vocab");
				//try
				//{
					new_vocab.addContent(new_word);
				//} catch (org.jdom.IllegalAddException iae)
				//{}
				//List words = root.getChildren("word");
				//vocab.add
				Document new_doc = new Document(new_vocab);
				doc = null;
				doc = new_doc;
				log.add(new_doc.toString());
				//text.setText(new_word_obj.getText());
				//def.setText(new_word_obj.getDefinition());
				*/
				break;
			}
			i++;
		}
		writeDocument(path, encoding);
	}
	
	/**
	*<p>Delete word object
	*/
	public void deleteWord1(Word word_obj)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = word_obj.getText();
		String search_def  = word_obj.getDefinition();
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			//log.add("Element "+str_text+" "+str_def);
			if (str_text.equals(search_text)&&str_def.equals(search_def))
			{
				//boolean lr = list.remove(i);
				//boolean erc = e.removeContent();
				//boolean rrc = root.removeContent(i);
				//boolean erct = e.removeChild("text");
				//boolean ercd = e.removeChild("definition");
				//boolean ercs = e.removeChildren("test");
				//e.removeChild("test");
				//log.add("Element index "+i+"  erct-"+erct+" ercd-"+ercd+" ercs-"+ercs);
				//Element c = e.getChild("text");
				//c.getParent().removeContent(c);
				List l = e.removeContent();
				int s = l.size();
				//log.add("Element index "+i+"  removed "+s);
				i=size;
			}
			i++;
		}
		//root.detach();
		//Document doc2 = new Document(root);
		//doc = doc2;
	}	
	
	/**
	*<p>Delete word object
	*/
	public void deleteWord(Word word_obj)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = word_obj.getText();
		String search_def  = word_obj.getDefinition();
		Iterator it = list.iterator();
		int i = 0;
		while (it.hasNext())
		{
			Element e = (Element)it.next();
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			//log.add("Element "+str_text+" "+str_def);
			if (str_text.equals(search_text)&&str_def.equals(search_def))
			{
				//int j = root.indexOf(e);
				//Content c = root.removeContent(i);
				//boolean b = root.removeContent(e);
				list.remove(e);
				//it.remove();
				//Element ce = (Element)c;
				//log.add("Element index "+i+"  removed "+b);
				//log.add("Element index "+i+"  removed "+ce.toString());
				break;
			}
			i++;
		}
	}
	
	/**
	<p>(Note, This method came from the original CreateJDOMList in the 
	* com.businessglue.util. package.)
	*<p>Here we create a new "test" element with grade/file/date sub-elements.</p>
	*<p>We also update each individual word level dependant on type.<p>
	*<p>Note: We cyclce thru the word list again here.  It would be nice to merge this
	* with the other previous cycle when the words are graded.  Maybe this is not possible
	* because what we use is a hashmap, no a reference to the xml file.  Maybe we could
	* bind an XPath reference to the key, or use and array...?</p>
	*/
	public String recordWordScore(String word, String grade, String test_file, String date, String type, String max_level, String org_level)
	{
		String new_level = new String();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int size = word_list.size();
		log.add("recordWordScore: looking for "+word+" in "+size+" number of words");
		if (word_levels == null)
		{word_levels = new Hashtable();}
		int i = 0;
		while (i<size)
		{
			Element e = (Element)word_list.get(i);
			String text = new String();
			String defi = new String();
			if (type.equals("reading"))
			{
				text = e.getChildText("text");
				defi = e.getChildText("definition");
				log.add("JDOMSolution.recordWordScore: type: "+type+" text : "+text+" def : "+defi); // def
			} else
			{
				text = e.getChildText("definition");
				defi = e.getChildText("text");
				log.add("JDOMSolution.recordWordScore: type: "+type+" text : "+text+" def : "+defi);
			}
			if (text.equals(word))
			{
				new_level = updateWordLevel(e, grade, text, date, type, max_level, org_level);
				log.add("*********************** got word *********************");
				log.add("recordWordScore: new_level "+new_level+" updated "+grade+" type "+type+" org_level "+org_level);
				Element new_test_elem = new Element("test");
				sub_elements = new Hashtable();
				sub_elements.put("grade", grade);
				sub_elements.put("file", test_file);
				sub_elements.put("date", date);
				Element added_elements_elem = addHoweverManyToElement(new_test_elem, sub_elements);
				e.addContent(added_elements_elem);
				break;
			}
			i++;
		}
		return new_level;
	}
	
	/**
	*<p>Create a new "test" element with grade/file/date sub-elements.</p>
	*<p>We also update each individual word level dependant on type.<p>
	*<p>Note: We cycle thru the word list again here.  It would be nice to merge this
	* with the other previous cycles when the words are graded.  Maybe this is not possible
	* because what we use is a hashmap, no a reference to the xml file.  Maybe we could
	* bind an XPath reference to the key, or use and array...?</p>
	*/
	public String recordWordScoreSearchById(long word_id, String word, String grade, String test_file, 
			String date, String type, String max_level, String org_level)
	{
		String new_level = new String();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int size = word_list.size();
		log.add("recordWordScoreSearchById: looking for "+word+" in "+size+" number of words");
		if (word_levels == null)
		{word_levels = new Hashtable();}
		int i = 0;
		while (i<size)
		{
			Element e = (Element)word_list.get(i);
			String id_str = e.getChildText("id");
			String text = new String();
			String defi = new String();
			if (type.equals("reading"))
			{
				text = e.getChildText("text");
				defi = e.getChildText("definition");
				log.add("JDOMSolution.recordWordScoreSearchById: type: "+type+" text : "+text+" def : "+defi); // def
			} else
			{
				text = e.getChildText("definition");
				defi = e.getChildText("text");
				log.add("JDOMSolution.recordWordScoreSearchById: type: "+type+" text : "+text+" def : "+defi);
			}
			log.add("JDOMSolution.recordWordScoreSearchById: id_str "+id_str+" = word_id "+word_id+" ?");
			if (id_str.equals(Long.toString(word_id)))
			{
				new_level = updateWordLevel(e, grade, text, date, type, max_level, org_level);
				log.add("*********************** got word *********************");
				log.add("recordWordScoreSearchById: new_level "+new_level+" updated "+grade+" type "+type+" org_level "+org_level);
				Element new_test_elem = new Element("test");
				sub_elements = new Hashtable();
				sub_elements.put("grade", grade);
				sub_elements.put("file", test_file);
				sub_elements.put("date", date);
				Element added_elements_elem = addHoweverManyToElement(new_test_elem, sub_elements);
				e.addContent(added_elements_elem);
				break;
			}
			i++;
		}
		return new_level;
	}
	
	/**utility method to ad ids to old words */
	public int addIds()
	{
		Element root = doc.getRootElement();
		List words = root.getChildren("word");
		int size = words.size();
		log.add("FileJDOMWordLists.addIds: list size "+size);
		int i = 0;
		int added = 0;
		while (i<size)
		{
			Element e = (Element)words.get(i);
			String id = (String)e.getChildText("id");
			//Element text_e = e.getChild("text");
			//String text = (String)e.getChildText("text");
			//log.add("text "+text);
			//String def = (String)e.getChildText("definition");
			if (id == null)
			{
				Element id_e = new Element("id");
				long long_id = Domartin.getNewID();
				id_e.addContent(Long.toString(long_id));
				e.addContent(id_e);
				added++;
				//Element text_e = e.getChild("text");
				//Element def_e = e.getChild("definition");
				//Element new_text = new Element("new_text");
				//new_text.setText(text);
				//e.addContent(new_text);
				//def_e.setText(def);
				// add id
			}
			i++;
		}
		return added;
	}
	
	/**
	<p>(Note, This method also comes from the original CreateJDOMList in the 
	* com.businessglue.util. package.)
	*<p>This method changes the 'level' element in the words file and adds the result to the
	* word_levels Hashmap for later retrieval.</p>
	*<p>Without the updateWordLevel method, we had the levels updated an arbitrary amount of
	* times per test, when only once was desired.  It's because the test results come in as
	* a Hastable, and we cycle trhu that hashtable looking for those words in the xml file.
	* So for each word, we have to cycle again thru the xml file looking for that word.</p>
	*<p>There must be a better way of doing this, so the updateWordLevel method is a workaround
	* to get things started with correct behaviour.</p>
	*/
	private String updateWordLevel(Element word, String pass_fail, String word_text, String date, String type, String max_level, String org_level)
	{
		String new_level = new String();
		String level_type = new String(type+"-level");
		if (wordIsAlreadyUpdated(word_text))
		{log.add("updateWordLevel: wordIsAlreadyUpdated");/* do nothing*/}
		else
		{
			Element level_elem = word.getChild(level_type); // npe
			if (level_elem == null)
			{
				level_elem = new Element(level_type);		// if there is no level tage yet...
				level_elem.addContent("0");
				word.addContent(level_elem);
				log.add("CreateJDOMList.updateWordLevel: added "+level_type+" element");
			}
			String current_level = level_elem.getText();log.add("updateWordLevel: current level "+current_level);
			org_level = current_level;
			if (pass_fail.equals("pass"))
			{	new_level = Domartin.incrementWordLevel(current_level); log.add("updateWordLevel: new level "+new_level);}
			else
			{	new_level = Domartin.decrementWordLevel(current_level);	log.add("updateWordLevel: new level "+new_level);}			
			int max_int = Integer.valueOf(max_level).intValue();
			int new_int = Integer.valueOf(new_level).intValue();
			if (new_int>max_int)
			{
				new_level = current_level;log.add("updateWordLevel: limited to "+max_int);
			}
			else
			{
				level = new_level;log.add("updateWordLevel: set ");
			}
			level_elem.setText(new_level);
			word_levels.put(word_text, new_level);
			//context.log("CreateJDOMList.updateWordLevel: put "+word_text+" at new level "+new_level+" for "+level_type);
		}
		return new_level;
	}
	
	
	/**
	<p>(Note, This method also comes from the original CreateJDOMList in the 
	* com.businessglue.util. package.)
	*<p>Because we had the same words being updated numerous times, this method uses a
	* Hashset whith the method.contains(word) to exclude further updates.</p>
	*<p>THis method should be considerd a workaround, as there is definately a better way.</p>
	*/
	private boolean wordIsAlreadyUpdated(String word_text)
	{
		boolean return_flag = false;
		if (last_words == null)
		{
			last_words = new HashSet();
			last_words.add(word_text);
		}
		else
		{
			if (last_words.contains(word_text))
			{
				return_flag = true;
			}
			else
			{
				last_words.add(word_text);
			}
		}
		return return_flag;
	}	
	
	/**
	<p>(Note, This method also comes from the original CreateJDOMList in the 
	* com.businessglue.util. package.)
	*<p>This method adds an element with an arbitrary number of sub elements
	* which are passed in as a hashtable or key-value pairs</p>
	*/
	public Element addHoweverManyToElement(Element elem, Hashtable sub_elems)
	{
		Enumeration keys = sub_elems.keys(); 
		while (keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			String value = (String)sub_elems.get(key);
			Element child = new Element(key);
			child.addContent(value);
			elem.addContent(child);
		}
		return elem;
	}	
	
	/**
	 * Used to save a backup to test files for deletion later.
	 * @param wntd_file_key_r Word's Next Test Date file key in tests/lists directory.
	 * @param wntd_file_key_w
	 * @param word_id
	 */
	public void addWTDKeys(String wntd_file_key_r, String wntd_file_key_w, long word_id)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String id_str = e.getChildText("id");
			if (id_str.equals(Long.toString(word_id)))
			{
				Element wntd_file_key_r_element = new Element("wntd_file_key_r");
				Element wntd_file_key_w_element = new Element("wntd_file_key_w");
				wntd_file_key_r_element.addContent(wntd_file_key_r);
				wntd_file_key_w_element.addContent(wntd_file_key_w);
				e.addContent(wntd_file_key_r_element);
				e.addContent(wntd_file_key_w_element);
				break;
			}
			i++;
		}
	}
	
	/**
	 * Add an array of options to the testing_options elment.
	 * If the element doesn't exists, create it.
	 * @param word_id
	 * @param path
	 * @param encoding
	 * @param testing_options
	 */
	public void addTestingOptiuons(String word_id, String path, String encoding, String [] testing_options)
	{
		log.add("JDOMSolution.addTestingOptiuons");
		Element root = doc.getRootElement();
		List all_words = root.getChildren("word");
		Element testing_options_element = null;
		int size = all_words.size();
		int words = 0;
		while (words<size) 
		{
			Element e = (Element)all_words.get(words);
			String this_id = (String)e.getChildText("id");
			if (word_id.equals(this_id))
			{
				log.add("JDOMSolution.addTestingOptiuons - found id");
				testing_options_element = e.getChild("testing_options");
				if (testing_options_element == null)
				{
					log.add("JDOMSolution.addTestingOptiuons - adding new element");
					testing_options_element = new Element("testing_options");
					e.setContent(testing_options_element);
				}
				for (int i = 0; i < testing_options.length; i++)
				{
					String testing_option = testing_options[i];
					Element option = new Element("testing_option");
					option.setText(testing_option);
					testing_options_element.addContent(option);
					log.add("JDOMSolution.addTestingOptiuons - set content "+testing_option);
				}
				writeDocument(path, encoding);
				break;
			}
			words++;
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
		} catch (java.io.IOException e)
		{
			//log.add("JDOMSolution.writeDocument: io error");
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
	}


    /**CLEANING UTILITY*/
    public int cleanAllTests()
    {
	    int number_of_tests_cleaned = 0;
	    Element root = doc.getRootElement();
	    List all_words = root.getChildren("word");
	    int size = all_words.size();
	    int i = 0;
	    while (i<size) 
	    {
		    Element e = (Element)all_words.get(i);
		    boolean removed = e.removeChildren("test");
		    if (removed)
		    {
			    number_of_tests_cleaned++;
		    }
		    i++;
	    }
	    return number_of_tests_cleaned;
    }
    
    /**
    *Clean tests from words and back them up in individual word files.
    */
    public int cleanAndBackupAllTests(String user_id, String category_id)
    {
	    int number_of_tests_cleaned = 0;
	    Element root = doc.getRootElement();
	    List all_words = root.getChildren("word");
	    int size = all_words.size();
	    int i = 0;
	    while (i<size) 
	    {
		    Element e = (Element)all_words.get(i);
		    String word_id = e.getChildText("id");
		    List all_tests = e.getChildren("test");
		    backupTests(all_tests, word_id, user_id, category_id);
		    boolean removed = e.removeChildren("test");
		    if (removed)
		    {
			    number_of_tests_cleaned++;
		    }
		    i++;
	    }
	    return number_of_tests_cleaned;
    }
    
    private void backupTests(List all_tests, String word_id, String user_id, String category_id)
    {
	    
    }
    
    /**CLEANING UTILITY*/
    public int cleanLevels()
    {
	    int number_of_levels_cleaned = 0;
	    Element root = doc.getRootElement();
	    List all_words = root.getChildren("word");
	    int size = all_words.size();
	    int i = 0;
	    while (i<size) 
	    {
		    Element e = (Element)all_words.get(i);
		    Element w = e.getChild("writing-level");
		    Element r = e.getChild("reading-level");
		    w.setText("0");
		    r.setText("0");
		    number_of_levels_cleaned++;
		    i++;
	    }
	    return number_of_levels_cleaned;
    }
    
    /**CLEANING UTILITY*/
    public int cleanLevels(int max_level)
    {
	    int number_of_levels_cleaned = 0;
	    Element root = doc.getRootElement();
	    List all_words = root.getChildren("word");
	    int size = all_words.size();
	    int i = 0;
	    while (i<size) 
	    {
		    Element e = (Element)all_words.get(i);
		    Element w = e.getChild("writing-level");
		    Element r = e.getChild("reading-level");
		    String w_level = w.getText();
		    String r_level = r.getText();
		    int w_l_int = Integer.parseInt(w_level);
		    int r_l_int = Integer.parseInt(r_level);
		    if (w_l_int>max_level)
		    {
			    w.setText(Integer.toString(max_level));
			    number_of_levels_cleaned++;
		    }
		    if (r_l_int>max_level)
		    {
			    r.setText(Integer.toString(max_level));
			    number_of_levels_cleaned++;
		    }
		    i++;
	    }
	    return number_of_levels_cleaned;
    }
    
    private boolean isUniqueHistory(AllStatsHistory all_stats_history)
    {
	    boolean not_the_same = false;
	    if (last_all_stats_history==null)
	    {
		    return true;
	    }
	    if (all_stats_history.getNumberOfTests()!=last_all_stats_history.getNumberOfTests())
	    {
		    not_the_same = true;
		    return not_the_same;
	    }
	    if (all_stats_history.getAverageScore()!=last_all_stats_history.getAverageScore())
	    {
		    not_the_same = true;
		    return not_the_same;
	    }
	    if (all_stats_history.getNumberOfWords()!=last_all_stats_history.getNumberOfWords())
	    {
		    not_the_same = true;
		    return not_the_same;
	    }
	    if (all_stats_history.getWritingAverage()!=last_all_stats_history.getWritingAverage())
	    {
		    not_the_same = true;
		    return not_the_same;
	    }
	    if (all_stats_history.getReadingAverage()!=last_all_stats_history.getReadingAverage())
	    {
		    not_the_same = true;
		    return not_the_same;
	    }

	    return not_the_same;
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
    
    public Vector getLog()
    {
	    return log;
    }

}
