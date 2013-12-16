package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.Locale;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.Scoring;
import org.catechis.Domartin;
import org.catechis.dto.Word;
import org.catechis.Transformer;
import org.catechis.EncodeString;
import org.catechis.JDOMSolution;
import org.catechis.dto.Test;
import org.catechis.dto.TestStats;
import org.catechis.dto.WordStats;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.RatesOfForgetting;
import org.catechis.dto.WordLastTestDates;
import org.catechis.interfaces.Categories;

/**
 * * Replaced by FileWordCategoriesManager *
*This class was created to create file specific methods to used categories or words.
*<p>This object is not threadsafe.
*/
public class FileCategories implements Categories
{
	
	private Vector log;
	private String root_path;
	
	/**This no args constructor is  to open up the utility methods for use.*/
	public FileCategories()
	{
		log = new Vector();
	}
	
	/**This constructor allows the root path to get files from.*/
	public FileCategories(String _root_path)
	{
		this.root_path = _root_path;
		log = new Vector();
	}

	/**
	*The first argument can be used to distinguish between exclusive and non-exclusive
	* categories.  Exclusive categories means that each word has only one category it can 
	* belong to.  Non exclusive categories can overlap with each other.
	*<p>
	*/
	public Hashtable getSortedWordCategories(String user_name)
	{
		Hashtable cat_test_dates = new Hashtable();
		String file_type = (".xml");
		String folder = ("files");
		Vector all_files = getFilteredFiles(user_name, file_type, folder);
		for (int i = 0; i < all_files.size(); i++)
		{
			Vector first_dates = new Vector();
			String file = (String)all_files.get(i);
			//log.add("getSortedWordCategories: file "+file);
			Vector words = getWords(file, folder, user_name);
			if (words == null)
				log.add("null words: file "+file);
			String now = Long.toString(new Date().getTime());
			try
			{
				int j = 0; 
				int size = words.size(); 
				while (j<size)
				{
					Word word = (Word)words.get(j);
					String first_date = getFirstDate(word);
					first_dates.add(first_date);		// sort thru all the words tests and return the earliest
					j++;
				}
				int num_of_first_dates = first_dates.size();
				//log.add("getSortedWordCategories: num_of_first_dates "+i+" "+num_of_first_dates+" for "+file);
				try
				{
					String cats_last_date = (String)first_dates.get(num_of_first_dates-1);
					cat_test_dates.put(cats_last_date, file);
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{
					log.add("getSortedWordCategories: aioob for file "+file);
				}
				if (size==0)
				{
					//String now = Long.toString(new Date().getTime());
					cat_test_dates.put(now, file);		// add files that have no words yet.
				}
			} catch (java.lang.NullPointerException npe)
			{
				cat_test_dates.put(now, "npe "+file);		// file was null, a mysterious parse error
				log.add("getSortedWordCategories: file "+file+" words causes npe");
				npe.printStackTrace();
			}
		}
		return cat_test_dates;
	}
	
	/**
	*Returns a Hashtable with the keys as the first test date found by searching all the words
	* for the earliest date of entry.  The file name has the .xml extension attached.
	*<p>
	*/
	public Hashtable getSortedWordCategories2(String user_name, String encoding)
	{
		Vector used_keys = new Vector();
		Hashtable cat_test_dates = new Hashtable();
		String file_type = (".xml");
		String folder = ("files");
		Vector all_files = getFilteredFiles2(user_name, file_type, folder, encoding);
		for (int i = 0; i < all_files.size(); i++)
		{
			Vector first_dates = new Vector();
			String file = (String)all_files.get(i);
			Vector words = getWords(file, folder, user_name);
			String now = Long.toString(new Date().getTime());
			try
			{
				int j = 0; 
				int size = words.size(); 
				while (j<size)
				{
					Word word = (Word)words.get(j);
					String first_date = getFirstDate(word);
					first_dates.add(first_date);		// sort thru all the words tests and return the earliest
					j++;
				}
				int num_of_first_dates = first_dates.size();
				//log.add("getSortedWordCategories: num_of_first_dates "+i+" "+num_of_first_dates+" for "+file);
				try
				{
					String cats_last_date = (String)first_dates.get(num_of_first_dates-1);
					while(used_keys.contains(cats_last_date))
					{
						cats_last_date = Long.getLong(cats_last_date)+1+"";
					}
					used_keys.add(cats_last_date);
					cat_test_dates.put(cats_last_date, file);
					log.add("getSortedWordCategories: put "+cats_last_date+" file "+file);
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{
					log.add("getSortedWordCategories: aioob in put! ");
				}
				if (size==0)
				{
					//String now = Long.toString(new Date().getTime());
					cat_test_dates.put(now, file);		// add files that have no words yet.
					log.add("getSortedWordCategories: put "+now+" file "+file+" has no words yet");
				}
			} catch (java.lang.NullPointerException npe)
			{
				// cat_test_dates.put(now, "npe "+file);
				cat_test_dates.put(now, file);		// file was null, a mysterious parse error
				log.add("getSortedWordCategories: put "+now+" file "+file+" file was null!!");
			}
		}
		return cat_test_dates;
	}
	
	/**
	*<p>Returns a vector of Word objects gotten from JDOMSolution
	*The folder argument was included when we were considering creating a suv-folder
	* for words, but we haven't got there yet.
	*/
	public Vector getWords(String file_name, String folder, String user_name)
	{
		String path_to_file = new String(root_path+File.separator
			+folder+File.separator+user_name+File.separator+file_name);
		File file_chosen = new File(path_to_file);
		String file_path = file_chosen.getAbsolutePath();
		JDOMSolution jdom = new JDOMSolution(file_chosen);
		Vector words_defs = new Vector();
		try
		{
			words_defs = jdom.getWordsForTest(file_name);
		} catch (java.lang.NullPointerException npe)
		{
			log.add("getWords npe for file "+file_name);
		}
		return words_defs;
	}
	
	/**
	*<p>This method takes a user name, a file extension type, such as xml or test, 
	* and a subfolder name, such as ../files/...
	*<p>This leaves open the possibility of sub-folders.
	*/
	public Vector getFilteredFiles(String user_name, String file_type, String folder)
	{
		String path_to_tests = new String(root_path+File.separator+folder+File.separator+user_name);
		log.add("getFilteredFiles: path "+path_to_tests);
		log.add("getFilteredFiles: looking fro file type "+file_type);
		File user_files_dir = new File(path_to_tests);
		String[] files_array = user_files_dir.list();
		Vector files = new Vector();
		EncodeString encoder = new EncodeString();
		for (int i = 0; i < files_array.length; i++) 
		{
			String file = encoder.encodeThis(files_array[i]);
			String ext = Domartin.getFileExtension(file);
			if (ext.equals(file_type))
			{
				files.add(file);
			} else
			{
				log.add("getFilteredFiles: excluded "+file);
			}
		}
		return files;
	}
	
	/**
	*<p>This method takes a user name, a file extension type, such as xml or test, 
	* and a subfolder name, such as ../files/...
	*<p>This leaves open the possibility of sub-folders.
	*/
	public Vector getFilteredFiles2(String user_name, String file_type, String folder, String encoding)
	{
		String path_to_tests = new String(root_path+File.separator+folder+File.separator+user_name);
		File user_files_dir = new File(path_to_tests);
		boolean exists = user_files_dir.exists();
		log.add("getFilteredFiles2: path "+path_to_tests+" exists? "+exists);
		String[] files_array = user_files_dir.list();
		Vector files = new Vector();
		EncodeString encoder = new EncodeString();
		try
		{
			for (int i = 0; i < files_array.length; i++) 
			{
				//String file = encoder.encodeThis(files_array[i]);
				String file = encoder.encodeThis(files_array[i], encoding);
				String ext = Domartin.getFileExtension(file);
				if (ext.equals(file_type))
				{
					files.add(file);
				}	
			}
		} catch (java.lang.NullPointerException npe)
		{
			log.add("getFilteredFiles2:files_array is null");
		}
		return files;
	}
	
	public String getFirstDate(Word word)
	{
		Test [] tests = word.getTests();
		Date now = new Date();
		long words_first_test_time = now.getTime();
		int num_of_tests = tests.length;
		for (int i = 0; i < tests.length; i++) 
		{
			Test test = tests[i];
			String str_date = test.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			Date date = null;
			ParsePosition pp = new ParsePosition(0);
			date = sdf.parse(str_date, pp);
			long this_time = 0;
			try
			{
				this_time = date.getTime();
				if (this_time < words_first_test_time)
				{
					words_first_test_time = this_time;
				}
			} catch (java.lang.NullPointerException npe)
			{
				//log.add("getFirstDate.npe : "+Transformer.getDateFromMilliseconds(Long.toString(this_time)));
			}
		}
		return Long.toString(words_first_test_time);
	}
	
	public ArrayList getSortedKeys(Hashtable hash)
	{
		// ArrayList getSortedTestDatesList(int level, String type)
		Set set = hash.keySet();
		// ArrayList sortList(ArrayList list)
		ArrayList unsorted_keys = new ArrayList(set);
		//ArrayList unsortedKeys = hash.keySet();
		ArrayList sortedKeys = Domartin.sortList(unsorted_keys);
		return sortedKeys;
	}
	
	/**
	*<p>A calling method has to know not to put the .xml on the end of the new
	* file name, as in the future, we hope such things will be done in a db.
	*<p>Previously we have kept JDOM specific methods out of the interface-based
	*objects, but JDOMSolution has become huge, so it seemed like a better thing to
	* do memory wise was just do some of the work here.  When we create, say
	*<p> a HibernateCategories object, it can implement addCategories any way it wants.
	*/
	public void addCategory(String category, String root_path, String user_name)
	{
		String folder = ("files");
		String type = (".xml");
		String path_to_file = new String(root_path+File.separator
			+folder+File.separator+user_name+File.separator+category+type);
		Document doc = new Document();
		Element root = new Element("vocab");
		doc.addContent(root);
		log.add("FileCategories.addCategory: created "+path_to_file);
		writeDocument(path_to_file, doc);
		File test = new File(path_to_file);
		log.add("FileCategories.addCategory: exists? "+test.exists());
	}
	
	public void deleteCategory(String category, String root_path, String user_name)
	{
		String folder = ("files");
		String path_to_file = new String(root_path+File.separator
			+folder+File.separator+user_name+File.separator+category);
		File file = new File(path_to_file);
		file.delete();
	}
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name, Document doc)
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
			log.add("FileCategories.writeDocument: ioe for "+file_name);
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
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
