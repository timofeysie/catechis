package org.catechis.file;
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

import org.jdom.Element;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.EncodeString;
import org.catechis.Domartin;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordNextTestDate;
import org.catechis.Transformer;
import org.catechis.interfaces.WordLists;
import org.catechis.constants.Constants;
import org.catechis.WordTestDateUtility;

/**
*This class can be used to add words to lists of new words so that a list of new words
* which have not been tested can be viewed as priority on the list.
*<p>When putting a new word, it will be added to reading and writing lists,
* but when getting lists, the caller must specify which type of list they want.
*<p>The files are in the <user_name>/subject/lists/new words reading.list and writing.list
*<p><new_words>
*<p>	<new_word>
*<p>		<word_id>1234567890</word_id>
*<p>		<primary_category>word file name.xml</primary_category>
*<p>		<text>(text as bytes)</text>
*<p>		<definition>(def as bytes)</definition>
*<p>	</new_word>
*<p></new_words>
*<p>The primary category is the file name in this case.
*/
public class FileJDOMWordLists implements WordLists
{
	private String file_name;
	private String path;
	private String subject;
	private Document doc;
	private EncodeString encoder;
	private Vector log;
	
	public FileJDOMWordLists()
	{
		log = new Vector();
	}
	
	public FileJDOMWordLists(String _path)
	{
		this.path = _path;
		log = new Vector();
		log.add("path "+path);
	}

	/**
	*We had a problem where the text elements content was 
	* being multiplied each time a doc was saved, but this
	* seemed to be due to the editor nedit showing a compounded
	* text, but the text appeared fine in the program.
	*/
	public FileJDOMWordLists(String path, String type, String subject)
	{
		file_name = new String(path+File.separator+subject
			+File.separator+"lists"+File.separator+"new words "+type+".list");
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("a file name "+ file_name);
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
	*This is the method used in the web app in the DailyTestResults.java.
	*/
	public FileJDOMWordLists(String path, String type, String user_name, String subject)
	{
		file_name = new String(path+File.separator+"files"+File.separator+user_name+File.separator+subject
			+File.separator+"lists"+File.separator+"new words "+type+".list");
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("a file name "+ file_name);
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
	
	public FileJDOMWordLists(File file)
	{
		doc = null;
		file_name = file.getAbsolutePath();
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
	
	/** load a normal load list crating the path from  the root
	*/
	public void loadWordList(String _file_name)
	{
		file_name = _file_name;
		File file_f = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("b file name "+ file_name);
		try
		{
			encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file_f);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add(j.toString());
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add(fnfe.toString());
		} catch (java.io.IOException i)
		{
			log.add(i.toString());
		}
	}
	
	/**
	*This version is used for testing.  Should really be called loadNewWordsList.  Live and learn...
	*/
	public void loadList(String type, String subject)
	{
		file_name = new String(path+File.separator+subject
			+File.separator+"lists"+File.separator+"new words "+type+".list");
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("c file name "+ file_name);
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
		} catch (java.lang.NullPointerException npe)
		{
			log.add(npe.toString());
		}
	}
	
	/**
	*THis version is used in the webapp.
	*/
	public void loadList(String type, String user_name, String subject)
	{
		file_name = new String(path+File.separator+subject+user_name+File.separator
			+File.separator+"lists"+File.separator+"new words "+type+".list");
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("c file name "+ file_name);
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
		} catch (java.lang.NullPointerException npe)
		{
			log.add(npe.toString());
		}
	}
	
	/**
	*THis version is used in the webapp.
	*/
	public void loadFile()
	{
		File file = new File(path);
		doc = null;
		log = new Vector();
		log.add("no args file name "+ file_name);
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
		} catch (java.lang.NullPointerException npe)
		{
			log.add(npe.toString());
		}
	}
	
	/**
	*Find a word from an category.  If the category has no .xml file extension, add it.
	*/
	public void loadWordList(String category, String user_id, String current_dir)
	{
		file_name = new String(current_dir+File.separator+user_id+File.separator+category);
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("c file name "+ file_name);
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
		} catch (java.lang.NullPointerException npe)
		{
			log.add(npe.toString());
		}
	}
	
	/**
	* Trying to turn strings into bytes works with Korean text. but not with English.
	*/
	public void addToNewWordsList(Word word, String path_to_word_file, String type, String encoding)
	{
		log.add("d path "+path_to_word_file);
		long long_id = word.getId();
		//String str_text = Transformer.getByteString(word.getText());
		//String str_def = Transformer.getByteString(word.getDefinition());
		String str_text = word.getText();
		String str_def = word.getDefinition();
		Element new_word = new Element("new_word");
		Element id = new Element("id");
		Element primary_path = new Element("primary_path");
		Element text = new Element("text");
		Element def = new Element("definition");
		id.addContent(Long.toString(long_id));
		primary_path.addContent(path_to_word_file);
		text.addContent(str_text);
		def.addContent(str_def);
		new_word.addContent(id);
		new_word.addContent(primary_path);
		new_word.addContent(text);
		new_word.addContent(def);
		loadList(type, Constants.VOCAB);
		try
		{
			Element root = doc.getRootElement();
			root.addContent(new_word);
			writeDocument(encoding);
		} catch (java.lang.NullPointerException npe)
		{
			npe.toString();
		}
	}
	
	/**
	*Unbind the new words list.  The calling method should know that only a partial word is returned.
	*<p><new_words>
	*<p>	<new_word>
	*<p>		<word_id>1234567890</word_id>
	*<p>		<primary_category>word file name.xml</primary_category>
	*<p>		<text>(text as bytes)</text>
	*<p>		<definition>(def as bytes)</definition>
	*<p>	</new_word>
	*<p></new_words>
	*/
	public Vector getNewWordsList(String type)
	{
		Element root = null;
		try
		{
			if (subject.equals(null)) {subject = Constants.VOCAB;}
			loadList(type, subject);
		} catch (java.lang.NullPointerException npe)
		{
			root = new Element("new_words");
		}
		root = doc.getRootElement();
		List new_words = root.getChildren("new_word");
		int size = new_words.size();
		//log.add("FileJDOMWordLists.: list size "+size);
		Vector words = new Vector();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)new_words.get(i);
			long id = Long.parseLong(e.getChildText("id"));
			String pc  = (String)e.getChildText("primary_category");
			String text = (String)e.getChildText("text");
			String def = (String)e.getChildText("definition");
			Word word = new Word();
			word.setId(id);
			word.setCategory(pc);
			word.setText(text);
			word.setDefinition(def);
			words.add(word);
			//log.add("text "+text+" "+Transformer.getStringFromBytesString(text));
			//log.add("def  "+def+" "+Transformer.getStringFromBytesString(def));
			i++;
		}
		return words;
	}
	
	/**
	*<p>This method assumes the file is already loaded in the constructor.
	*<p>Unbind the new words list with these attributes.
	*<p><new_words>
	*<p>	<new_word>
	*<p>		<word_id>1234567890</word_id>
	*<p>		<primary_category>word file name.xml</primary_category>
	*<p>		<text>(text as bytes)</text>
	*<p>		<definition>(def as bytes)</definition>
	*<p>	</new_word>
	*<p></new_words>
	*/
	public Vector getNewWordsList2()
	{
		Element root = doc.getRootElement();
		List new_words = root.getChildren("new_word");
		int size = new_words.size();
		//log.add("FileJDOMWordLists.: list size "+size);
		Vector words = new Vector();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)new_words.get(i);
			long id = Long.parseLong(e.getChildText("id"));
			String pc  = (String)e.getChildText("primary_category");
			String text = (String)e.getChildText("text");
			String def = (String)e.getChildText("definition");
			Word word = new Word();
			word.setId(id);
			word.setCategory(pc);
			word.setText(text);
			word.setDefinition(def);
			words.add(word);
			//log.add("text "+text+" "+Transformer.getStringFromBytesString(text));
			//log.add("def  "+def+" "+Transformer.getStringFromBytesString(def));
			i++;
		}
		return words;
	}
	
	/*
	*Exclude any word objects already contained in the other_new_words Vector passed in as an arg.
	*/
	public Vector getNoRepeatsNewWordsList(String type, Vector other_new_words)
	{
		if (subject.equals(null)) {subject = Constants.VOCAB;}
		loadList(type, subject);
		Hashtable keys = getKeysList(other_new_words);
		Element root = doc.getRootElement();
		List new_words = root.getChildren("new_word");
		int size = new_words.size();
		log.add("FileJDOMWordLists.getNoRepeatsNewWordsList: list size "+size);
		Vector words = new Vector();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)new_words.get(i);
			String str_id = (String)e.getChildText("id");
			long id = Long.parseLong(str_id);
			String pc  = (String)e.getChildText("primary_category");
			String text = (String)e.getChildText("text");
			String def = (String)e.getChildText("definition");
			Word word = new Word();
			word.setId(id);
			word.setCategory(pc);
			word.setText(text);
			word.setDefinition(def);
			String contains = (String)keys.get(str_id);
			//log.add("str_id   "+str_id+" "+word.getText()+"		"+word.getDefinition()+".");
			if (contains.equals(str_id))
			{
				//log.add("pass "+i);
				i++;
			} else
			{
				words.add(word);
			}
			i++;
		}
		return words;
	}
	
	private Hashtable getKeysList(Vector other_new_words)
	{
		Hashtable word_ids = new Hashtable();
		int i = 0;
		while (i<other_new_words.size())
		{
			Word word = (Word)other_new_words.get(i);
			long id = word.getId();
			String str_id = Long.toString(id);
			word_ids.put(str_id, str_id);
			i++;
		}
		return word_ids;
	}
	
	/**
	*Remove a word from the new words list
	*/
	public void removeWordFromNewWordsList(String search_id, String encoding)
	{
		// loadList(type, user_name);
		Element root = doc.getRootElement();
		List new_words = root.getChildren("new_word");
		int size = new_words.size();
		//log.add("*** FileJDOMWordLists.: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)new_words.get(i);
			String id = (String)e.getChildText("id");
			//log.add("search_id "+search_id+" = "+id+" ?");
			if (id.equals(search_id))
			{
				// remove found word
				root.removeContent(e);
				//log.add("yes!!! took long enough");
				writeDocument(encoding);
				break;
			}
			i++;
		}
	}
	
	public int addIds(String extension, String encoding)
	{
		log.add("FileJDOMWordLists.addIds: path "+path+extension);
		//loadWordList(path+extension); // not needed with File constructor
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
			Element text_e = e.getChild("text");
			String text = (String)e.getChildText("text");
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
				Element new_text = new Element("new_text");
				new_text.setText(text);
				e.addContent(new_text);
				//def_e.setText(def);
				// add id
			}
			i++;
		}
		log.add("FileJDOMWordLists.addIds: writing "+path+extension);
		//writeDocument(path+extension, encoding);
		return added;
	}
	
	/**
	*Add an element <retired> to a word and set it to true.
	* if the word has a retired element that is set to false.
	* if not, it adds the element with a value of true.
	*/
	public void retireWord(String search_id, String encoding)
	{
		file_name = path;
		loadWordList(path);
		log.add("FileJDOMWordLists.retireWords: looking for "+search_id);
		Element root = doc.getRootElement();
		List words = root.getChildren("word");
		int size = words.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)words.get(i);
			String id = (String)e.getChildText("id");
			String text = (String)e.getChildText("text");String definition = (String)e.getChildText("definition");log.add(id+" "+definition+"		 "+text);
			if (id.equals(search_id))
			{
				//log.add("FileJDOMWordLists.retireWords: found "+id);
				String is_retired = e.getChildText("retired");
				if (is_retired==null)
				{
					// word has no retired element, so create is a set it to true
					Element r = new Element("retired");
					r.addContent("true");
					e.addContent(r);
					break;
				}
				boolean retired_boolean = Boolean.getBoolean(is_retired);
				if (retired_boolean==true)
				{
					// word is already retired
					break;
				} else
				{
					// retired element is set as false, so set it to true
					Element r = e.getChild("retired");
					r.setText("true");
				}
			}
			i++;
		}
		writeDocument(encoding);
	}
	
	/**
	*Return true if the word has a <retired>true</retired> element.
	*<p>Attachment leads to jealousy.  The path to the dark side that is.
	*/
	public boolean isRetired(String search_id)
	{
		file_name = path;
		boolean retired = false;
		loadWordList(path);
		log.add("FileJDOMWordLists.isRetired: looking for "+search_id);
		Element root = doc.getRootElement();
		List words = root.getChildren("word");
		int size = words.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)words.get(i);
			String id = (String)e.getChildText("id");
			String text = (String)e.getChildText("text");String definition = (String)e.getChildText("definition");log.add(id+" "+definition+"		 "+text);
			if (id.equals(search_id))
			{
				//log.add("FileJDOMWordLists.isRetire: found "+id);
				String is_retired = e.getChildText("retired");
				if (is_retired==null)
				{
					// word has no retired element
					//log.add("FileJDOMWordLists.isRetire: returning false ");
					return false;
				}
				//log.add("FileJDOMWordLists.isRetire: not null "+is_retired);
				return new Boolean(is_retired).booleanValue();
			}
			i++;
		}
		return retired;
	}
	
	/**
	*<p>Fear leads to anger, anger leads to hate, hate leads to suffering.  I sense much fear in you...
	*/
	public void addToRetiredWordsList(Word word, String category, String encoding)
	{
		log.add("FilJDOMWordLists.addToRetiredWordsList path "+path);
		file_name = path;
		loadWordList(path);
		long long_id = word.getId();
		String str_text = word.getText();
		String str_def = word.getDefinition();
		log.add("FilJDOMWordLists.addToRetiredWordsList retired "+str_def+" "+str_text);
		Element new_word = new Element("retired_word");
		Element id = new Element("id");
		Element primary_path = new Element("primary_path");
		Element text = new Element("text");
		Element def = new Element("definition");
		id.addContent(Long.toString(long_id));
		primary_path.addContent(category);
		text.addContent(str_text);
		def.addContent(str_def);
		new_word.addContent(id);
		new_word.addContent(primary_path);
		new_word.addContent(text);
		new_word.addContent(def);
		loadWordList(path);
		Element root = doc.getRootElement();
		root.addContent(new_word);
		writeDocument(encoding);
	}
	
	/**
	*Create a file in either wltd reading/writing folder named after the last test date.
	*/
	public void createLastTestDateFile(long long_id, String str_category, String test_time)
	{
		//log.add("d path "+path_to_word_file);
		// root element
		Element test = new Element("test");	
		// create sub elements
		Element word_id = new Element("word_id");
		Element category = new Element("category");
		// add content to sub elements
		word_id.addContent(Long.toString(long_id));
		category.addContent(str_category);
		// add sub elements to root
		test.addContent(word_id);
		test.addContent(category);
		// create document with root
		doc = new Document(test);
		file_name = path+File.separator+test_time+".xml";
		writeDocument();
	}
	
	/**
	*Create a file in either wntd reading/writing folder named after the next test date.
	*/
	public void createNextTestDateFile(long long_id, String str_category, String test_time, String _wltd_file_key)
	{
		//log.add("d path "+path_to_word_file);
		// root element
		Element test = new Element("test");	
		// create sub elements
		Element word_id = new Element("word_id");
		Element category = new Element("category");
		Element wltd_file_key = new Element("wltd_file_key");
		// add content to sub elements
		word_id.addContent(Long.toString(long_id));
		category.addContent(str_category);
		wltd_file_key.addContent(_wltd_file_key);
		// add sub elements to root
		test.addContent(word_id);
		test.addContent(category);
		test.addContent(wltd_file_key);
		// create document with root
		doc = new Document(test);
		file_name = path+File.separator+test_time+".xml";
		writeDocument();
	}
	
	/**
	*Load the xml file and unbind the object here.
	*<p><test>
	*<p><word_id>-7983252723503331340</word_id>
	*<p><category>random words 14.xml</category>
	*<p><wltd_file_key>1224386366000</wltd_file_key>
	*<p></test>
	*<p>Drop it like its hot.
	*/
	public WordNextTestDate getWordNextTestDateObject(String full_path_to_file)
	{
		//file_name =full_path_to_file;
		//log.add("FileJDOMWordLIsts.getWordNextTestDateObject:file_name "+full_path_to_file);
		loadWordList(full_path_to_file);
		Element test = new Element("word");
		try
		{
			test = doc.getRootElement();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("npe "+file_name);
		}
		String word_id = (String)test.getChildText("word_id");
		String category = (String)test.getChildText("category");
		String wltd_file_key = (String)test.getChildText("wltd_file_key");
		WordNextTestDate wntd = new WordNextTestDate();
		wntd.setWordId(word_id);
		wntd.setCategory(category);
		wntd.setWltdFileKey(wltd_file_key); 
		return wntd;
	}
	
	/**
	*Returns a basic word with text, definition, category and id, enough for a test.
	*<p>Returns a blank word if not found.
	*/
	public Word getSpecificWord(String search_id, String category, String user_id, String current_dir)
	{
		String full_category_path = current_dir+File.separator+"files"+File.separator+user_id+File.separator+category;
		//System.err.println("full_category_path "+full_category_path);
		loadWordList(full_category_path);
		Element root = doc.getRootElement();
		List words = root.getChildren("word");
		int size = words.size();
		//log.add("words in this cat: "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)words.get(i);
			log.add("fjdom.getSpecificWord: e "+e);
			String word_id = (String)e.getChildText("id");
			log.add("fjdom.getSpecificWord: word_id "+word_id);
			//String text = (String)e.getChildText("text");String definition = (String)e.getChildText("definition");log.add(id+" "+definition+"		 "+text);
			
			try
			{
			
			if (word_id.equals(search_id))
			{
				String text = (String)e.getChildText("text");
				String def = (String)e.getChildText("definition");
				log.add("fjdom.getSpecificWord:word_id.equals(search_id) "+text+" "+def+" cat "+category);
				Word word = new Word();
				word.setId(Long.parseLong(word_id));
				word.setCategory(category);
				word.setCategoryId(getOrFindCategoryId(e, category, user_id, current_dir));
				word.setText(text);
				word.setDefinition(def);
				word.setDateOfEntry(getDateOfEntry(e, word));
				word.setWritingLevel(Integer.parseInt(e.getChildText("writing-level")));
				word.setReadingLevel(Integer.parseInt(e.getChildText("reading-level")));
				word.setRetired(new Boolean((String)e.getChildText("retired")).booleanValue()); 
				List tests = e.getChildren("test");
				Test all_tests[] = new Test[tests.size()];
				int j = 0;
				while (i<tests.size())
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
				return word;
			}
			
			} catch (java.lang.NullPointerException npe)
			{
				log.add(" npe ");
				npe.printStackTrace();
			}
			
			i++;
		}
		return new Word();
	}
	
	/**
	 * This method is used in word.getSpecificWord method above to set the category id:
	 * setCategoryId(getOrFindCategoryId(e, category, user_id, current_dir));
	 * @param e
	 * @param category_name
	 * @param user_id
	 * @param context_path
	 * @return
	 */
	private long getOrFindCategoryId(Element e, String category_name, String user_id, String context_path)
	{
		long category_id;
		try
		{
			String category_id_string = (String)e.getChildText("category_id");
			category_id = Long.parseLong(category_id_string);
			log.add("getOrFindCategoryId category_id_string "+category_id_string+" for category name "+category_name);
		} catch (java.lang.NullPointerException npe)
		{
			log.add("getOrFindCategoryId  npe #1 in getting id for "+category_name);
			FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
			try
			{	
				category_id = fwcm.getCategoryId(category_name, user_id, subject, context_path);
				log.add("getOrFindCategoryId category_id "+category_id);
			} catch (java.lang.NullPointerException npe2)
			{
				category_id = 0;
				log.add("getOrFindCategoryId  npe #2 in FileWordCategoryManager getting id for "+category_name);
				log.add("fwcm log -=-=-=-=-=- start");
				append(fwcm.getLog());
				log.add("fwcm log -=-=-=-=-=- end");
			} catch (java.lang.NumberFormatException nfe)
			{
				category_id = 0;
				log.add("getOrFindCategoryId: nfe in FileWordCategoryManager getting id for category "+category_name);
				log.add("fwcm log -=-=-=-=-=- start");
				append(fwcm.getLog());
				log.add("fwcm log -=-=-=-=-=- end");
			}
			log.add("getOrFindCategoryId category_id_string set to 0");
			append(fwcm.getLog());
		} catch (java.lang.NumberFormatException npe)
		{
			log.add("getOrFindCategoryId  nfe #1 in getting id for "+category_name);
			FileWordCategoriesManager fwcm = new FileWordCategoriesManager();
			try
			{	
				category_id = fwcm.getCategoryId(category_name, user_id, subject, context_path);
				log.add("getOrFindCategoryId category_id "+category_id);
			} catch (java.lang.NullPointerException npe2)
			{
				category_id = 0;
				log.add("getOrFindCategoryId  npe #2 in FileWordCategoryManager getting id for "+category_name);
				log.add("fwcm log -=-=-=-=-=- start");
				append(fwcm.getLog());
				log.add("fwcm log -=-=-=-=-=- end");
			} catch (java.lang.NumberFormatException nfe)
			{
				category_id = 0;
				log.add("getOrFindCategoryId: nfe #1 in FileWordCategoryManager getting id for category "+category_name);
				log.add("fwcm log -=-=-=-=-=- start");
				append(fwcm.getLog());
				log.add("fwcm log -=-=-=-=-=- end");
			}
			log.add("getOrFindCategoryId category_id_string set to 0");
			append(fwcm.getLog());
		}
		return category_id;
	}
	
	private long getDateOfEntry(Element e, Word word)
	{
		long doe = 0;
		try
		{
			doe = Long.parseLong(e.getChildText("date-of-entry"));
		} catch (java.lang.NumberFormatException npe)
		{
			word.setDateOfEntry(Long.parseLong(getEarliestDate(word)));
		}
		return doe;
	}
	
	/**
	*This method will return the date of the first test.  We take one day off this to simulate the
	* date of first entry, as the early developer failed to capture this info from the start.
	<p>If there is no date of entry, and the word has no tests (it must be like a weird old word
	* lost in space) then we use the current date.
	*/
	public String getEarliestDate(Word word)
	{
		WordTestDateUtility wtdu = new WordTestDateUtility();
		String result = wtdu.evaluateWordsFirstTestDate(word);
		if (result.equals("no tests"))
		{
			result = Long.toString(new Date().getTime());
		}
		return result;
	}
	
	public void createWordFile(Word word, String encoding)
	{
		//log.add("d path "+path_to_word_file);
		// root element
		Element test = new Element("test");	
		// create sub elements
		Element word_id = new Element("word_id");
		Element category = new Element("category");
		// add content to sub elements
		// add sub elements to root
		test.addContent(word_id);
		test.addContent(category);
		// create document with root
		doc = new Document(test);
		file_name = path+File.separator+word.getId()+".xml";
		writeDocument();
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument()
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
	*/
	public void writeDocument(String encoding)
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
			e.printStackTrace();
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
	
	/**
	*<p>Write document with path and encoding.</p>
	*/
	public void writeDocument(String file, String encoding)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			osw.close();
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
	
	public void setSubject(String _subject)
	{
		subject = _subject;
	}
	
	
    /** Deguggin --------------------------------------------------------- */
    public Vector getLog()
    {
	    return log;
    }
    
    public void resetLog()
    {
	    log = new Vector();
    }
    
    private void append(Vector add)
    {
    	for (int i = 0; i < add.size(); i++)
    	{
    		log.add(add.get(i));
    	}
    }

}
