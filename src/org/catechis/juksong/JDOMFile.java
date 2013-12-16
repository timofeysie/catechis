package org.catechis.juksong;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.catechis.EncodeString;
import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;
import org.catechis.dto.SavedTest;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.interfaces.SaveTests;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
*This class was made to create file specific methods for the SaveTests interface.
*<p>This object is not threadsafe. 
*
*A WikiWord has these properties:
* long id;
* String text;
* Definition [] definition; see below
* String encoding; of source
* String source; the url it came from
* Category category; 
* 
*A Definition has the following properties:
* String definition;
* String grammar;
* 
* A Category has:
* long id;
* String name;
*/
public class JDOMFile
{
	
	private Vector log;
	private Document doc;
	private String file_name;
	
	
	public JDOMFile()
	{
		// no arguments here...
		log = new Vector();
	}
	
	public JDOMFile(String _file_name)
	{
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
			log.add("JDOMFile<init> "+j.toString());
		} catch (java.io.IOException i)
		{
			log.add("JDOMFile<init> "+i.toString());
		}
	}
		    
	/**
	 * Create the file that a new word will be saved into.
	 * The path is user_id/files/shared/ek or ke/text.xml
	 * @param user_info
	 * @param wiki_word
	 * @param dictionary - Use JuksongConstants for EK/KE (English/Korean) dictionaries.
	 * @return - true if a new word is created, false if the word already exists.
	 */
	public boolean saveWikiWord(UserInfo user_info, WikiWord wiki_word, String dictionary)
	{
		String word_name = "";
    	try
    	{
    		word_name = URLEncoder.encode(wiki_word.getText(), user_info.getEncoding());
    	} catch (UnsupportedEncodingException uee)
    	{
    		log.add("UnsupportedEncodingException");
    	}
		String shared_path = new String(user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+JuksongConstants.SHARED);
		FarmingTools.ifFolderDoesntExistCreateIt(shared_path);
		log.add("UnsupportedEncodingException");
		String dictionary_path = shared_path
			+File.separator+JuksongConstants.DICTIONAY;
		FarmingTools.ifFolderDoesntExistCreateIt(dictionary_path);
		
		String specific_path = dictionary_path
			+File.separator+word_name; 
			
		File file = new File(specific_path);
		if (file.exists())
		{
			return false;
		}
		Element root = new Element("wiki_word");
		doc = new Document(root);
		Element wiki_word_element = createWikiWordElement(wiki_word);
		Element wiki_word_definitions = createWikiWorkDefinition(wiki_word);
		Element wiki_word_categories = createWikiWordCategories(wiki_word);
		wiki_word_element.addContent(wiki_word_definitions);
		wiki_word_element.addContent(wiki_word_categories);
		writeDocument(specific_path, user_info.getEncoding());
		return true;
	}
	
	/**
	*A WikiWord has these properties:
	* long id;
	* String text;
	* Definition [] definition; see below
	* String encoding; of source
	* String source; the url it came from
	* Category category; 	
	*/
	private Element createWikiWordElement(WikiWord wiki_word)
	{
		// create main element
	    Element word = new Element("wiki_word");
	    	// create sub-elements
	    Element id = new Element("id");
	    Element text = new Element("text");
	    Element encoding = new Element("encoding");
	    Element source = new Element("source");
	    Element definitions = createWikiWorkDefinition(wiki_word);
	    Element category = createWikiWordCategories(wiki_word);
	    	// add content to sub-elements
	    id.addContent(Long.toString(wiki_word.getId()));
	    text.addContent(wiki_word.getText());
	    encoding.addContent(wiki_word.getEncoding());
	    source.addContent(wiki_word.getSource());
	    	// add sub-elements to main element
	    word.addContent(id);
	    word.addContent(text);
	    word.addContent(encoding);
	    word.addContent(source);
	    word.addContent(definitions);
	    return word;
	}
	
	/**
	 * A Definition has the following properties:
	 * String definition;
	 * String grammar;
	 * 
	 * @param wiki_word
	 * @return
	 */
	private Element createWikiWorkDefinition(WikiWord wiki_word)
	{
		Element definitions = new Element("definition");
		Definition [] wiki_definitions = wiki_word.getDefinitions();
		int i = 0;
		int len = wiki_definitions.length;
		while (i<len)
		{
			Definition definition = wiki_definitions[i];
			Element def_elem = new Element ("definition");
			Element grammar = new Element("grammar");
			def_elem.addContent(definition.getDefinition());
			grammar.addContent(definition.getGrammar());
			definitions.addContent(def_elem);
			i++;
		}
		return definitions;
	}
	
	/**
	 * 
	 * A Category has:
	 * long id;
	 * String name;
	 * String type
	 * @param wiki_word
	 * @return
	 */
	private Element createWikiWordCategories(WikiWord wiki_word)
	{
		Element categories = new Element("categories");
		Category [] wiki_categories = wiki_word.getCategory();
		int i = 0;
		int len = wiki_categories.length;
		while (i<len)
		{
			Category category = wiki_categories[i];
			Element id = new Element ("id");
			Element name = new Element("name");
			Element category_type = new Element("category_type");
			id.addContent(Long.toString(category.getId()));
			name.addContent(category.getName());
			category_type.addContent(category_type);
			i++;
		}
		return categories;
	}
	
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
	    Element test_id       = new Element("test_id");
	    Element test_date     = new Element("test_date");
	    Element test_name     = new Element("test_name");
	    Element test_type     = new Element("test_type");
	    Element test_status   = new Element("test_status");
	    Element test_format   = new Element("test_format");
	    Element creation_time = new Element("creation_time");
	    	// add content to sub-elements
	    test_id.addContent(str_id);
	    test_date.addContent(saved_test_obj.getTestDate());
	    test_name.addContent(saved_test_obj.getTestName());
	    test_type.addContent(saved_test_obj.getTestType());
	    test_status.addContent(saved_test_obj.getTestStatus());
	    test_format.addContent(saved_test_obj.getTestFormat());
	    creation_time.addContent(saved_test_obj.getCreationTime());
	    	// add sub-elements to main element
	    saved_test.addContent(test_id);
	    saved_test.addContent(test_date);
	    saved_test.addContent(test_name);
	    saved_test.addContent(test_type);
	    saved_test.addContent(test_status);
	    saved_test.addContent(test_format);
	    saved_test.addContent(creation_time);
	    return saved_test;
	}
	
	/**
	*Returns a vector saved_test objects from the user/subject/tests/saved tests.xml file..
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
			saved_tests.put(index, test_word);
			i++;
		}
		return saved_tests;
	}
	
	private Word unbindWord(Element saved_test_elem)
	{
		Word test_word = new Word();
		String index = (String)saved_test_elem.getChildText("index");
		test_word.setId(Long.parseLong(saved_test_elem.getChildText("word_id")));
	    	test_word.setCategory(saved_test_elem.getChildText("category"));
	    	test_word.setText(saved_test_elem.getChildText("text"));
	    	test_word.setDefinition(saved_test_elem.getChildText("definition"));
	    	return test_word;
	}
	
	/**
	*Returns a vector saved_test objects from the user/subject/tests/saved tests.xml file..
	*/
	public Vector getSavedTestsList(UserInfo user_info)
	{
		Vector saved_tests = new Vector();
		String tests_folder_path = (user_info.getRootPath()
			+File.separator+Constants.FILES
			+File.separator+user_info.getUserId()
			+File.separator+user_info.getSubject()
			+File.separator+Constants.TESTS);
		String saved_files_path = (tests_folder_path+File.separator+"saved tests.xml");
	    	loadFile(saved_files_path);
		Element root = doc.getRootElement();
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
		return saved_test;
	}
	
	/**
	*Go through each test word result, load the word and update its level and other
	* status files based on the result of pass or fail.  Return a Vector of test
	* score objects to notify the user.
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
	* Vector scoreSavedTests(UserInfo user_info, SavedTest saved_test, Vector test_word_results);
	*/
	public Vector scoreSavedTests(UserInfo user_info, SavedTest saved_test, Vector test_word_results)
	{
		Vector test_results = new Vector();
		
		return test_results;
	}
	
	/** load a file
	*/
	public void loadFile(String _file_name)
	{
		file_name = _file_name;
		File file_f = new File(file_name);
		doc = null;
		log = new Vector();
		log.add("FileSavedTests.loadFile "+ file_name);
		try
		{
			EncodeString encoder = new EncodeString();
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
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
			log.add("FileCategories.writeDocument: ioe for "+file_name);
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
			log.add("JDOMSolution.writeDocument: euu error "+encoding);
			uee.printStackTrace();
		}catch (java.io.IOException e)
		{
			log.add("JDOMSolution.writeDocument: io error");
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
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
