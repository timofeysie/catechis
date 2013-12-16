package org.catechis.legacy;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.xpath.XPath;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import javax.servlet.ServletContext;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.io.FileOutputStream;


//import com.businessglue.util.EncodeString;
import org.catechis.EncodeString;
//import com.businessglue.indoct.TestBean;
import java.io.UnsupportedEncodingException;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.ExcludeWord;
import org.catechis.Domartin;

/**
<p>The Indcot web app uses the File, Context constructor so that it can easily log comments.</p>
<p>The constructor creates a document from the File, and then it's up to different methods
* to perform functions on the document.</p>
*/
public class CreateJDOMList
{

	private Document doc;
	private EncodeString encoder;
	private ServletContext context;
	private Reader reader;
	private String msg;
	
	private Hashtable text_def_hash;
	private Hashtable sub_elements;
	private Hashtable word_levels;
	
	private String last_date;
	private String last_word;
	private HashSet last_words;
	
	private int index;
	private String level;
	private String org_level;
	
	Vector log;
	
	/**<p>Takes a file and creates a JDOM document from it.</p>
	<p>The user then calls getElementNamed with the name of the element
	to retrieve a java.util.List based on the document loaded.</p>
	*/
	public CreateJDOMList(File file)
	{
		doc = null;
		log = new Vector();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			j.printStackTrace();
		} catch (java.io.IOException i)
		{
			i.printStackTrace();
		} catch (java.lang.NullPointerException npe)
		{
			System.err.println("CreateJDOMList.<init>: file npe "+file.getAbsolutePath());
			npe.printStackTrace();
		}
	}
	
	/**<p>Takes a file and creates a JDOM document from it.</p>
	<p>The user then calls getElementNamed with the name of the element
	to retrieve a java.util.List based on the document loaded.</p>
	*/
	public CreateJDOMList(File file, String encoding)
	{
		OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
		System.err.println("CreateJDOMList.<init>: default file encoding "+out.getEncoding());
		try
		{
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			String defaultEncoding = isr.getEncoding();
			System.err.println("CreateJDOMList.<init>: reader encoding "+defaultEncoding);
		} catch (java.io.FileNotFoundException fnfe)
		{
			
		} catch (java.io.UnsupportedEncodingException uee)
		{
			
		}
		doc = null;
		log = new Vector();
		encoder = new EncodeString();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file, encoding);
			doc = builder.build(reader);
			//doc = builder.build(file);
		} catch (org.jdom.JDOMException j)
		{
			j.printStackTrace();
		} catch (java.io.IOException i)
		{
			i.printStackTrace();
		} catch (java.lang.NullPointerException npe)
		{
			System.err.println("CreateJDOMList.<init>: file npe "+file.getAbsolutePath());
			npe.printStackTrace();
		}
	}
	
	/*
	public void writeOutput(String str) 
	{

	    try {
		FileOutputStream fos = new FileOutputStream("test.txt");
		Writer out = new OutputStreamWriter(fos, "UTF8");
		out.write(str);
		out.close();
	    } catch (IOException e) {
		e.printStackTrace();
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
	*/

	
	/**
	<p>This constrcutor is provided for servlet environments to enable logging.</p>
	<p>Logging notes:
			context = _context;
		msg = file.getAbsolutePath();
		context.log("CreateJDOMList<init> file path : "+msg);</p>
	*/
	public CreateJDOMList(File _file, ServletContext _context)
	{
		context = _context;
		File file = _file;
		doc = null;
		log = new Vector();
		try
		{
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(file);
			context.log("CreateJDOMList<init> got file "+file.getAbsolutePath());
		} catch (org.jdom.JDOMException j)
		{
			context.log(j.toString());
		} catch (java.io.IOException i)
		{
			context.log(i.toString());
		}
	}	

	/** 
	*<p>Returns a List of whatever elements retrieved from the file with the passed
	* in string name.</p>*/
	public java.util.List getElementsNamed(String element_to_get)
	{
		Element root = doc.getRootElement();
		List namedChildren = root.getChildren(element_to_get);
		Element e = (Element)namedChildren.get(0);
		return namedChildren;
	}
	
	public Vector listIntoVector(java.util.List list)
	{ 
		int j = 0;
		Vector result = new Vector();
		while (j<list.size())
		{
			Element e = (Element)list.get(j);
			result.add((String)e.getText());
			j++;
		}
		return result;
	}
	
	/**
	*<p>Get a list of text-definition strings which are children of a 'text' element
	* from a list of all word elements.</p>
	*/
	public Vector getTextDefVector()
	{
		OutputStreamWriter out = new OutputStreamWriter(new ByteArrayOutputStream());
		System.err.println("CreateJDOMList.getTextDefVector: default file encoding "+out.getEncoding());
		text_def_hash = new Hashtable();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int size = word_list.size();
		Vector text_def = new Vector();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)word_list.get(i);
			String text = e.getChildText("text");
			String def  = e.getChildText("definition");
			String enc_text = encoder.encodeThis(text);
			String enc_def  = encoder.encodeThis(def);
			text_def_hash.put(enc_text, enc_def);
			line = new String(enc_text+" - "+enc_def);
			text_def.add(line);
			System.out.println(i+": "+line);
			i++;
		}
		return text_def;
	}
	
	/**
	<p>This method assumes that the two lists are of equal length,
	 as there should be an equal number of word-definition pairs in the file.</p>
	 <p>The problem is, the text-def elements are children of <word> elements.</p>
	*/
	public Vector mergeWordsAndDefs(Vector list1, Vector list2)
	{
		int i = 0;
		String line = new String();
		Vector result = new Vector();
		while (i<list1.size())
		{
			line = new String((String)list1.elementAt(i)+" - "+(String)list2.elementAt(i));
			result.add(line);
			i++;
		}
		return result;
	}
	
	public Hashtable getTextDefHash()
	{
		if (text_def_hash!=null)
		{
			return text_def_hash;
		} else
		{
			text_def_hash = new Hashtable();
			Element root = doc.getRootElement();
			List word_list = root.getChildren("word");
			int size = word_list.size();
			String line = new String();
			int i = 0;
			while (i<size)
			{
				Element e = (Element)word_list.get(i);
				String text = e.getChildText("text");
				String def  = e.getChildText("definition");
				String enc_text = encoder.encodeThis(text);
				String enc_def  = encoder.encodeThis(def);
				text_def_hash.put(enc_text, enc_def);
				i++;
			}
			return text_def_hash;
		}
	}
	
	/**
	*<p>This method is similar to getTextDefHash, except that it filters the results
	* for the desired range of words.</p>
	<p>We need the test_name so that we can reset the index in the test file.
	*context.log("CreateJDOMList.getFilteredTextDefHash: len-"+length+" ind-"+index);
	*<p>The recordNewTestIndex method call never word.  It retrieved 0 options.  So we moved the
	* index into the session scope so that the TestResultAction object, which also adds to and saves
	* the 'name'.test file, which takes less trips to the disk anyway.  The String test_name, String user_path, String encoding
	* arguments were removed.
	*/
	public Hashtable getFilteredTextDefHash(String test_length, String test_index, String test_levels, String test_type)
	{
		context.log("CreateJDOMList.getFilteredTextDefHash: type-"+test_type);
		index = Integer.parseInt(test_index);
		int level_min = getLevelMin(test_levels);
		int level_max = getLevelMax(test_levels);
		text_def_hash = new Hashtable();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int i = 0;
		int word_count = 0;
		int size = word_list.size();
		int length = 10;	// set default size but it's overridden
		if (test_length.equals("all"))
			length = size-1;
		else
			length = Integer.parseInt(test_length);
		boolean flag = false;
		int infinate = 0;
		while (flag==false) 
		{
			if (i<index)
			{
				// index is set higher thana 0, and we must step up to it.
				i++;
			}
			else 
			{
				if (i>=size)
				{
					// loop back to 0 to look for mathing words.
					i = 0;index=0;
					if (infinate>2)
					{
						flag = true;
					}
					infinate++;
				}
				Element e = (Element)word_list.get(i);
				int level = getLevel(e, test_type);
				if (level>=level_min&&level<=level_max) 
				{
					addTextDef(e, level, index, i, word_count);
					word_count++;
					if (word_count>length) 
					{
						flag=true;
						break;
					}
				}
				else
				{
					// has level-"+level+" index-"+index+" i-"+i+" not added");
				}
			}
			i++;
			index++;
		}
		return text_def_hash;
	}
	
	/**
	*<p>This is a temporary method.  CreateJDOMList is a legacy object that is currently being
	* replaced by org.catechis.JDOMSolution.
	<p>I'm just adding an exclude mechanism to that words that have been tested recently can be excluded
	* from this list.
	*<p>In doing so much work these days setting up moodle which I want to integrate with,
	* but this involves MySQL and all the messy permissions I've been able to ignore so far.
	*<p>I have to leave the safe haven of tomcat, and struts for Apache and php....
	*<p>I guess this is an admission that that this method sucks, and I know this, but the new
	* one, in the DailyTestAction, although threadsafe, is not scoring the tests properly now,
	* and I just need a quick fix so that I can use the program effectively while
	* I work in permissions hell on the other fedora core 6 box.
	*<p>Its painful to even look at this method, but here goes...
	*/
	public Hashtable getFilteredTextDefHash2(String test_length, String test_index, String test_levels, String test_type, Vector elt_vector)
	{
		context.log("CreateJDOMList.getFilteredTextDefHash2: type-"+test_type);
		ExcludeWord ex_time = new ExcludeWord();
		Word word = new Word();
		index = Integer.parseInt(test_index);
		int level_min = getLevelMin(test_levels);
		int level_max = getLevelMax(test_levels);
		text_def_hash = new Hashtable();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int i = 0;
		int word_count = 0;
		int size = word_list.size();
		context.log("CreateJDOMList.getFilteredTextDefHash2: words-"+size);
		int length = 10;	// set default size but it's overridden
		if (test_length.equals("all"))
			length = size-1;
		else
			length = Integer.parseInt(test_length);
		boolean flag = false;
		int infinate = 0;
		while (flag==false) 
		{
			if (i<index)
			{
				// index is set higher thana 0, and we must step up to it.
				i++;
			}
			else 
			{
				if (i>=size)
				{
					// loop back to 0 to look for mathing words.
					i = 0;index=0;
					if (infinate>2)
					{
						flag = true;
					}
					infinate++;
				}
				Element e = (Element)word_list.get(i);
				int level = getLevel(e, test_type);
				if (level>=level_min&&level<=level_max) 
				{
					word = unbindWord(e, test_type);
					//try
					//{
						boolean exclude = ex_time.checkExclusionDate(elt_vector, level, word, test_type);
						context.log("CreateJDOMList.getFilteredTextDefHash2: exclude-"+exclude);
						if (!ex_time.checkExclusionDate(elt_vector, level, word, test_type))
						{
							log.add(ex_time.getLog());
							addTextDef(e, level, index, i, word_count);
							word_count++;
							if (word_count>length) 
							{
								flag=true;
								break;
							}
						}
					//} catch (java.lang.NullPointerException npe)
					//{ /* do nothing */}
				}
				else
				{
					// has level-"+level+" index-"+index+" i-"+i+" not added");
				}
			}
			i++;
			index++;
		}
		return text_def_hash;
	}
	
	/**
	*<p>This is based on the illustrious and long suffering getFilteredTextDefHash
	* which returned a Hashtable of words.  This version requires only one word object,
	* which it must first find in an xml file passed to the constructor.
	*<p>At this point the developers are wishing they would have stored each
	* word individually, but then there would be less to do the next monsoon season...
	*<p> I already have a method to do this
	public Word getFilteredTextDefHash3(String test_length, String test_index, String test_levels, String test_type, Vector elt_vector)
	{
		context.log("CreateJDOMList.getFilteredTextDefHash3: type-"+test_type);
		ExcludeWord ex_time = new ExcludeWord();
		Word word = new Word();
		index = Integer.parseInt(test_index);
		int level_min = getLevelMin(test_levels);
		int level_max = getLevelMax(test_levels);
		text_def_hash = new Hashtable();
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int i = 0;
		int word_count = 0;
		int size = word_list.size();
		context.log("CreateJDOMList.getFilteredTextDefHash2: words-"+size);
		int length = 10;	// set default size but it's overridden
		if (test_length.equals("all"))
			length = size-1;
		else
			length = Integer.parseInt(test_length);
		boolean flag = false;
		int infinate = 0;
		while (flag==false) 
		{
			if (i<index)
			{
				// index is set higher thana 0, and we must step up to it.
				i++;
			}
			else 
			{
				if (i>=size)
				{
					// loop back to 0 to look for mathing words.
					i = 0;index=0;
					if (infinate>2)
					{
						flag = true;
					}
					infinate++;
				}
				Element e = (Element)word_list.get(i);
				int level = getLevel(e, test_type);
				if (level>=level_min&&level<=level_max) 
				{
					word = unbindWord(e, test_type);
					//try
					//{
						boolean exclude = ex_time.checkExclusionDate(elt_vector, level, word, test_type);
						context.log("CreateJDOMList.getFilteredTextDefHash2: exclude-"+exclude);
						if (!ex_time.checkExclusionDate(elt_vector, level, word, test_type))
						{
							log.add(ex_time.getLog());
							addTextDef(e, level, index, i, word_count);
							word_count++;
							if (word_count>length) 
							{
								flag=true;
								return word;
							}
						}
					//} catch (java.lang.NullPointerException npe)
					//{ }
				}
				else
				{
					// has level-"+level+" index-"+index+" i-"+i+" not added");
				}
			}
			i++;
			index++;
		}
		return word;
	}	
	*/
	
	
	private Word unbindWord(Element e, String category)
	{
		//Element e = (Element)all_words.get(i);
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
				//log.add("JDOMSOolution.getWordsForTest: ioobe at test "+j+" tests "+number_of_tests);
				break;
			}
			j++;
		}
		word.setTests(all_tests);
		return word;
	}
	
	private void addTextDef(Element e, int _level, int _index, int _i, int _word_count)
	{
		String text = e.getChildText("text");
		String def  = e.getChildText("definition");
		String enc_text = encoder.encodeThis(text);
		String enc_def  = encoder.encodeThis(def);
		text_def_hash.put(enc_text, enc_def);
		// context.log("CreateJDOMList.addTextDef: added "+def+" level "+_level+" index "+_index+" i "+_i+" word_count "+_word_count);
	}
	
	private int getTestLevel(Element e)
	{
		Element level_child = e.getChild("level");
		String str_level = level_child.getText();
		if (str_level=="")
		{
			str_level="0";
		}
		int level = Integer.parseInt(str_level);
		return level;
	}
	
	private int getLevel(Element e, String test_type)
	{
		String level_type = new String(test_type+"-level");
		String str_level = new String();
		List kids = e.getChildren();
		int num = kids.size();
		int ind = 0;
		int level = 0;
		//context.log("CreateJDOMList.getLevel: size "+num);
		while (ind<num)
		{
			try
			{
				Element kid = (Element)kids.get(ind);
				String kids_value = (String)kid.getText();
				String kids_name = (String)kid.getName();
				//context.log("CreateJDOMList.getLevel: value "+kids_value+" name "+kids_name);
				if (kids_name.equals(level_type))
				{
					str_level = kids_value;
					//context.log("CreateJDOMList.getLevel: adding "+str_level);
					break;
				}
			}
			catch (java.lang.NullPointerException n)
			{}
			ind++;
		}
		try
		{
			level = Integer.parseInt(str_level);
		}
		catch (java.lang.NumberFormatException nfe)
		{
			context.log("CreateJDOMList.getLevel: nfe");
			level = 0;
		}
		return level;
	}
	
	public int getLevelMin(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_min = levels.substring(0,slash);
		//context.log("CreateJDOMList.getLevelMin: str_min = "+str_min);
		int min = Integer.parseInt(str_min);
		return min;
	}
	
	public int getLevelMax(String levels)
	{
		int slash = levels.lastIndexOf("-");
		String str_max = levels.substring(slash+1, levels.length());
		//context.log("CreateJDOMList.getLevelMax: str_max = "+str_max);
		int max = Integer.parseInt(str_max);
		return max;
	}
	
	public String getNewIndex()
	{
		String str_index = Integer.toString(index);
		return str_index;
	}
	
	/**
	*<p>THis method was replaced because it didn't work.  now the index is placed in the session
	* scope, and added in the TestResultAction object.
	*/
	public void recordNewTestIndex(String test_name, String user_path, int int_index, String encoding)
	{
		File test_file = Domartin.createFileFromUserNPath(test_name, user_path);
		String index = Integer.toString(int_index);
		CreateJDOMList cjdoml = new CreateJDOMList(test_file, context);
		Element root = doc.getRootElement();
		List option_list = root.getChildren("option");
		int size = option_list.size();
		//context.log("CreateJDOMList.recordNewTestIndex: got "+size+" options");
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText("name");
			String value  = e.getChildText("value");
			//context.log("CreateJDOMList.recordNewTestIndex: found option "+name);
			if (name.equals("index"))
			{
				Element index_e = e.getChild("value");
				index_e.setText(index);
				//context.log("CreateJDOMList.recordNewTestIndex: setText "+index);
				break;
			}
			i++;
		}
		String absolute_path = test_file.getAbsolutePath();
		//context.log("!!!!!!!!!!!!!!!CreateJDOMList.recordNewTestIndex: path="+absolute_path);
		cjdoml.writeDocument(absolute_path);
	}
	
	public Hashtable getOptionsHash()
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren("option");
		int size = option_list.size();
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
	*<p>THis method retrieves a list of option style elements from an xml file with
	* the following format:</p>
	*<p>elements ("word")</p>
	*<p>  names content /names("text")</p>
	*<p>  values content /values("definition")</p>
	*<p>/elements</p>
	*<p>THen is creates a Hash like this keys=names hash(key)=value.</p>
	*/
	public Hashtable getWhateverHash(String elements, String names, String values)
	{
		//context.log("CreateJDOMList.getWhateverHash: looking for elements "+elements+" with names "+names+" and values "+values);
		Element root = doc.getRootElement();
		List option_list = root.getChildren(elements);
		int size = option_list.size();
		//context.log("CreateJDOMList.getWhateverHash: number of "+elements+" is "+size);
		Hashtable options = new Hashtable();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText(names);
			String value  = e.getChildText(values);
			if (value==null)
				value=new String("null");
			//context.log("CreateJDOMList.getWhateverHash: "+elements+" "+i+" name-"+name+" value-"+value);
			String enc_name = encoder.encodeThis(name);
			String enc_value  = encoder.encodeThis(value);
			options.put(enc_name, enc_value);
			i++;
		}
		return options;
	}
	
	public List getWhateverElementsList(String element_name)
	{
		Element root = doc.getRootElement();
		List elems = root.getChildren(element_name);
		return elems;
	}

	/**
	*<p>THis method retrieves a list of option style elements from an xml file with
	* the following format:</p>
	*<p>elements</p>
	*<p>  names content /names</p>
	*<p>  values content /values</p>
	*<p>/elements</p>
	*<p>THen is creates a vector like this "names values"</p>
	*/
	public Vector getWhateverVector(String elements, String names, String values)
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren(elements);
		int size = option_list.size();
		Vector options = new Vector();
		String line = new String();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String name = e.getChildText(names);
			String value  = e.getChildText(values);
			line = new String(name+" "+value);
			options.add(line);
			i++;
		}
		return options;
	}	
	
	/** 
	*<p>When this method gets called by the TestResultAction to record the score of a test,
	* the following arguments are used:("percent", "date", date, "grade", score);<p>
	*/
	public void addElement(String element, String name, String name_content, String value, String value_content)
	{
		Element elem = new Element(element);
		Element child_name = new Element(name);
		child_name.addContent(name_content);
		Element child_value = new Element(value);
		child_value.addContent(value_content);
		elem.addContent(child_name);
		elem.addContent(child_value);
		Element root = doc.getRootElement();
		root.addContent(elem);
		//context.log("CreateJDOMList.addElement: added "+name_content+" "+value_content);
	}
	
	/**
	*<p>This method is used to replace the value of a child element.  It's used like this:
	*<p>TestResultAction.score_jdom.setElement(("option", "name", "index", "value", new_index)
	*<p>It resets the index option in the user.options file so thaT the next test will start
	* picking words where it left of the last time.  Of course, this is only important
	* with level 0 words, because higher levels either promote or demote words.  This method
	* seems pretty hopeless now, in hindsight, but remember, I was just learning about making
	* web apps when I wrote that.  When you aren't using a db, recreating primary keys cant be
	* confusing.
	*/
	public void setElement(String element, String name, String name_content, String value, String value_content)
	{
		Element root = doc.getRootElement();
		List option_list = root.getChildren("option");
		int size = option_list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)option_list.get(i);
			String sub_name = e.getChildText(name);
			if (sub_name.equals(name_content))
			{
				Element value_element = e.getChild(value);
				value_element.setText(value_content);
				break;
			}
			i++;
		}
	}
	/**
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
		//context.log("CreateJDOMList.writeDocument: 2 "+file_name+" encoding "+encoding);
		if (doc==null)
			context.log("CreateJDOMList.writeDocument: 2 doc is null");
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
	*<p>Here we create a new "test" element with grade/file/date sub-elements.</p>
	*<p>We also update each individual word level defendant on type.<p>
	*<p>Note: We cycle thru the word list again here.  It would be nice to merge this
	* with the other previous cycle when the words are graded.  Maybe this is not possible
	* because what we use is a hashmap, no a reference to the xml file.  Maybe we could
	* bind an XPath reference to the key, or use and array...?</p>
	*/
	public void recordWordScore(String word, String grade, String test_file, String date, String type, String max_level)
	{
		Element root = doc.getRootElement();
		List word_list = root.getChildren("word");
		int size = word_list.size();
		if (word_levels == null)
		{word_levels = new Hashtable();}
		int i = 0;
		while (i<size){
			Element e = (Element)word_list.get(i);
			String text = new String();
			String defi = new String();
			if (type.equals("reading"))
			{
				text = e.getChildText("definition");
				defi = e.getChildText("text");
			} else
			{
				text = e.getChildText("text");
				defi = e.getChildText("definition");
			}
			if (text.equals(word)){
				updateWordLevel(e, grade, text, date, type, max_level);
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
	}
	
	/**
	*<p>This method changes the 'level' element in the words file and adds the result to the
	* word_levels Hashmap for later retrieval.</p>
	*<p>Without the updateWordLevel method, we had the levels updated an arbitrary amount of
	* times per test, when only once was desired.  It's because the test results come in as
	* a Hastable, and we cycle thru that hashtable looking for those words in the xml file.
	* So for each word, we have to cycle again thru the xml file looking for that word.</p>
	*<p>There must be a better way of doing this, so the updateWordLevel method is a workaround
	* to get things started with correct behavior.</p>
	*/
	private void updateWordLevel(Element word, String pass_fail, String word_text, String date, String type, String max_level)
	{
		String level_type = new String(type+"-level");
		if (wordIsAlreadyUpdated(word_text))
		{/* do nothing*/}
		else
		{
			Element level_elem = word.getChild(level_type);
			if (level_elem == null)
			{
				level_elem = new Element(level_type);
				level_elem.addContent("0");
				word.addContent(level_elem);
				//context.log("CreateJDOMList.updateWordLevel: added "+level_type+" element");
			}
			String current_level = level_elem.getText();
			org_level = current_level;
			String new_level = new String();
			if (pass_fail.equals("pass"))
			{	new_level = Domartin.incrementWordLevel(current_level);}
			else
			{	new_level = Domartin.decrementWordLevel(current_level);}
			int max_int = Integer.valueOf(max_level).intValue();
			int new_int = Integer.valueOf(new_level).intValue();
			if (new_int>max_int)
			{new_level = current_level;}
			else
			{level = new_level;}
			level_elem.setText(new_level);
			word_levels.put(word_text, new_level);
			//context.log("CreateJDOMList.updateWordLevel: put "+word_text+" at new level "+new_level+" for "+level_type);
		}
	}
	
	/**
	*<p>Because we had the same words being updated numerous times, this method uses a
	* Hashset with the method.contains(word) to exclude further updates.</p>
	*<p>THis method should be considered a workaround, as there is definitely a better way.</p>
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
	
	public Hashtable getWordLevels()
	{
		return word_levels;
	}
	
	/**
	*<p>This method returns the value of the argument 'name' child element if it exists,
	* and the argument 'value' after it creates the new child element if it doesn't.  Savvy?</p>
	*/
	public String ifXDontExistCreateIt(Element word, String name, String value)
	{
		Element new_child = word.getChild(name);
		String  new_content = new String();
		if (new_child!=null)
		{
			new_content = word.getChildText(name);
		}
		else
		{
			new_child = new Element(name);
			new_child.addContent(value);
			word.addContent(new_child);
			new_content = value;
		}
		return new_content;
	}
	
	/**
	*This method was created to get a list of the tests for a specified word.
	*It is called in StatsWord with these args:
	* "word", "def", word_chosen, "test"
	*The 1st arg is the element after the root to look for.
	*The 2nd arg is the sub-element, and the 3rd is the sub-element content
	*The last arg is name of the specified sub-elements children.
	*For StatsWord we returns a Vector of test elements like this:
	* 	<test>
	*		<date>Thu Mar 03 11:21:44 PST 2005</date>
	*		<file>level 0 writing.test</file>
	*		<grade>fail</grade>
	*	</test>
	*/
	public Vector getListOfSpecificElementsChildren(String elements, String child_name, String value, String grand_child)
	{
		Element root = doc.getRootElement();
		List element_list = root.getChildren(elements);
		Vector child_list = new Vector();
		int size = element_list.size();
		//context.log("CreateJDOMList.getListOfSpecificElementChildren: number of "+elements+" is "+size);
		//context.log("CreateJDOMList.getListOfSpecificElementChildren: looking for "+value+" in "+child_name+" elements "+grand_child);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)element_list.get(i);
			String possible_name_match = e.getChildText(child_name);
			if (possible_name_match.equals(value))
			{
				List c_list = e.getChildren(grand_child);
				int children_size = c_list.size();
				child_list = new Vector(c_list);
				//context.log("CreateJDOMList.getListOfSpecificElementChildren: found "+value+" with "+children_size+" children.");
				break;
			}
			i++;
		}
		return child_list;
	}
	
	/**
	*This is an StatsWordAction specific method to return a hashtable of specific tests.
	* We get a List of tests, and return a Hastable with the date as the key,
		<test>
			<date>Thu Mar 03 11:21:44 PST 2005</date>
			<file>level 0 writing.test</file>
			<grade>fail</grade>
		</test>
	* For instance, if we are looking for writing tests, then this element
	* will become: 
	* key-Thu Mar 03 11:21:44 PST 2005 value-fail.
	*/
	public Hashtable getGetDatedTestsHash(String type, Vector word_test_elems)
	{
		int size = word_test_elems.size();
		//context.log("CreateJDOMList.getGetDatedTestsHash: looking at "+size+" tests for "+type+" tests.");
		Hashtable found_tests = new Hashtable();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)word_test_elems.get(i);
			String file_name = e.getChildText("file");
			//context.log("CreateJDOMList.getGetDatedTestsHash: file "+file_name);
			int match = file_name.indexOf(type);
			//context.log("CreateJDOMList.getGetDatedTestsHash: int of indexOf(type) is "+match);
			if (match!=-1)
			{
				String date = e.getChildText("date");
				String grade = e.getChildText("grade");
				context.log("CreateJDOMList.getGetDatedTestsHash: added test from "+date+" with grade "+grade);
				found_tests.put(date, grade);
			}
			i++;
		}
		return found_tests;
	}
	
	public String getOriginalLevel()
	{
		return level;
	}
	
	public String getNewLevel()
	{
		return org_level;
	}
	
	public void xPathTest(Element e, String def)
	{
		// org.jdom.xpath.XPath is abstract, cannot be instantiated.
		//XPath x_path = new XPath("/vocab/word[1]");
		
		// this outputs:
		// 2004-10-10 20:40:24 StandardContext[/indoct]CreateJDOMList.xPathTest: 
		// 1 - /child::vocab/child::word[1.0]
		// I think I'll use List.get(index) instead.
		//
		try
		{
			XPath x_path = XPath.newInstance("/vocab/word[1]");
			String text = x_path.getXPath();
			//context.log("CreateJDOMList.xPathTest: 1 - "+text);
		}
		catch (org.jdom.JDOMException je)
		{
			//context.log("CreateJDOMList.xPathText: "+je.printStackTrace());
		}
		//Element first_word = x_path.selectNode();
		//String text = first_word.getChildText("definition");
		//String path = x_path.valueOf(e);
		//String path = e.getValue();
		//String path = x_path.getXPath();
	}
	
	/**
	*<p>This method is for
	* for debugging purposes.</p>
	*/
	public String encodeTest(String value)
	{
		//context.log("CreateJDOMList.encodeTest: "+value);
		try
		{
			String original = value;
			byte[] utf8Bytes = original.getBytes("UTF8");
			dumpBytes(utf8Bytes);
			String tempString = new String(utf8Bytes, "UTF8");
			return tempString;
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Unencoded";
		} catch (java.lang.NullPointerException n)
		{
			n.printStackTrace();
			return "Null";
		}
	}
	
	private void dumpBytes(byte[] bytes)
	{
		int j = 0;
		StringBuffer buff = new StringBuffer();
		while (j<bytes.length)
		{
			Byte b = new Byte(bytes[j]);
			String what = b.toString();
			buff.append(what+" ");
			j++;
		}
		String shit = new String(buff);
		context.log(shit);
	}
	
	public Vector getLog()
	{
		return log;
	}

}
