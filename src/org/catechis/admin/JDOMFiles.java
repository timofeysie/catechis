package org.catechis.admin;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
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
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;

/**
*<p>This class does all the work to add a user, such as creating files and folders
* as well as confirming if the user already exists, of the invitation code is correct.
*<p>This class does not do everything, only JDOM and file related activities.  
* FileUserUtilities would create the lists of options, for instance that would go
* into the new users option file.
*/
public class JDOMFiles
{
	private Document doc;
	private EncodeString encoder;
	private Vector log;
	
	public JDOMFiles()
	{
		// used by FileEdit
	}
	
	/**
	*This constructor was created for testing purposes.  We need to create a
	* test document by hand so that we can test the respective methods.
	*/
	public JDOMFiles(Document _doc)
	{
		this.doc = _doc;
		log = new Vector();
	}

	/**
	*If the caller only wants to use createNewXMLFile then they must ignore the
	java.io.FileNotFoundException error thrown by the constructor, even tho
	the file will be created properly.
	*/
	public JDOMFiles(String file_name)
	{
		File file = new File(file_name);
		doc = null;
		log = new Vector();
		try
		{
			encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add("JDOMFiles.<init> FileNotFoundException: "+fnfe.toString());
			// this is expecxted if a caller only wants to create a new file
		} catch (java.io.IOException i)
		{
			log.add(i.toString());
		} catch (org.jdom.JDOMException j)
		{
			log.add(j.toString());
		}
	}

	/**
	* 
	*/
	public Hashtable getLoginEntries()
	{
		Element root = doc.getRootElement();
		List entries = root.getChildren("login");
		int size = entries.size();
		log.add("JDOMFiles.getLoginEntries: list size "+size);
		Hashtable entries_hash = new Hashtable();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)entries.get(i);
			String user_name = e.getChildText("user");
			String date  = e.getChildText("date");
			entries_hash.put(date, user_name);
			log.add("added "+date+" "+ user_name);
			i++;
		}
		return entries_hash;
	}
	
	/**
	*
	*/
	public void addLoginEntry(String user_name, String current_date)
	{
		Element root = doc.getRootElement();
		Element entry = new Element("login");
		Element user = new Element("user");
		Element date = new Element("date");
		user.addContent(user_name);
		date.addContent(current_date);
		entry.addContent(user);
		entry.addContent(date);
		root.addContent(entry);
	}
	
	/**
	*Add an entry in the user.passes file as such:
	*<user>
		<id>##########</id>
		<name>steve</name>
		<password>steve</password>
		<e_mail>ste@steve.com</steve>
	</user>
	*/
	public void addUserPass(String user_name, String password, String e_mail, long long_id)
	{
		Element root = doc.getRootElement();
		Element id = new Element("id");
		Element name = new Element("name");
		Element pass = new Element("password");
		Element email = new Element("e_mail");
		id.addContent(Long.toString(long_id));
		name.addContent(user_name);
		pass.addContent(password);
		email.addContent(e_mail);
		Element user = new Element("user");
		user.addContent(id);
		user.addContent(name);
		user.addContent(pass);
		user.addContent(email);
		root.addContent(user);
	}
	
	/**
	*Create file to hold state info of user.
	*<status>
		<daily_test_index>0:0</daily_test_index>
		<daily_test_file>0</daily_test_file>
		<tests>0</tests>
		<passed_tests>0</passed_tests>
		<failed_tests>0</failed_tests>
		<reversed_tests>0</reversed_tests>
	</status>
	*/
	public void createStatusRecord()
	{
		Element root =  new Element("status");
		
		Element daily_test_index = new Element("daily_test_index");
		Element daily_test_file = new Element("daily_test_file");
		Element tests = new Element("tests");
		Element passed_tests = new Element("passed_tests");
		Element failed_tests = new Element("failed_tests");
		Element reversed_tests = new Element("reversed_tests");
		Element action_name = new Element("action_name");
		Element action_time = new Element("action_time");
		Element action_id = new Element("action_id");
		Element action_type = new Element("action_type");
		Element daily_session_tests = new Element("daily_session_tests");
		Element daily_session_start = new Element("daily_session_start");
		Element number_of_tests = new Element("number_of_tests");
		Element average_score = new Element("average_score");
		Element number_of_words = new Element("number_of_words");
		Element writing_average = new Element("writing_average");
		Element reading_average = new Element("reading_average");
		Element words_at_reading_level_0 = new Element("words_at_reading_level_0");
		Element words_at_reading_level_1 = new Element("words_at_reading_level_1");
		Element words_at_reading_level_2 = new Element("words_at_reading_level_2");
		Element words_at_reading_level_3 = new Element("words_at_reading_level_3");
		Element words_at_writing_level_0 = new Element("words_at_writing_level_0");
		Element words_at_writing_level_1 = new Element("words_at_writing_level_1");
		Element words_at_writing_level_2 = new Element("words_at_writing_level_2");
		Element words_at_writing_level_3 = new Element("words_at_writing_level_3");
		Element word_test_start = new Element("word_test_start");
		Element word_test_elapsed_time = new Element("word_test_elapsed_time");
		Element number_of_sessions = new Element("number_of_sessions");
		Element week_of_year = new Element("week_of_year");
		Element number_of_tests_in_week = new Element("number_of_tests_in_week");
		Element number_of_retired_words = new Element("number_of_retired_words");
		Element date = new Element("date");
		
		daily_test_index.addContent("0:0");
		daily_test_file.addContent("0");
		tests.addContent("0");
		passed_tests.addContent("0");
		failed_tests.addContent("0");
		reversed_tests.addContent("0");
		action_name.addContent("default");
		action_time.addContent("default");
		action_id.addContent("0");
		action_type.addContent("default");
		daily_session_tests.addContent("0");
		daily_session_start.addContent("0");
		number_of_tests.addContent("0");
		average_score.addContent("0");
		number_of_words.addContent("0");
		writing_average.addContent("0");
		reading_average.addContent("0");
		words_at_reading_level_0.addContent("0");
		words_at_reading_level_1.addContent("0");
		words_at_reading_level_2.addContent("0");
		words_at_reading_level_3.addContent("0");
		words_at_writing_level_0.addContent("0");
		words_at_writing_level_1.addContent("0");
		words_at_writing_level_2.addContent("0");
		words_at_writing_level_3.addContent("0");
		word_test_start.addContent("0");
		word_test_elapsed_time.addContent("0");
		number_of_sessions.addContent("0");
		week_of_year.addContent(Integer.toString(Calendar.WEEK_OF_YEAR));
		number_of_tests_in_week.addContent("0");
		number_of_retired_words.addContent("0");
		date.addContent("0");
		
		root.addContent(daily_test_index);
		root.addContent(daily_test_file);
		root.addContent(tests);
		root.addContent(passed_tests);
		root.addContent(failed_tests);
		root.addContent(reversed_tests);
		root.addContent(action_name);
		root.addContent(action_time);
		root.addContent(action_id);
		root.addContent(action_type);
		root.addContent(daily_session_tests);
		root.addContent(daily_session_start);
		root.addContent(number_of_tests);
		root.addContent(average_score);
		root.addContent(number_of_words);
		root.addContent(writing_average);
		root.addContent(reading_average);
		root.addContent(words_at_reading_level_0);
		root.addContent(words_at_reading_level_1);
		root.addContent(words_at_reading_level_2);
		root.addContent(words_at_reading_level_3);
		root.addContent(words_at_writing_level_0);
		root.addContent(words_at_writing_level_1);
		root.addContent(words_at_writing_level_2);
		root.addContent(words_at_writing_level_3);
		root.addContent(word_test_start);
		root.addContent(word_test_elapsed_time);
		root.addContent(number_of_sessions);
		root.addContent(week_of_year);
		root.addContent(number_of_tests_in_week);
		root.addContent(number_of_retired_words);
		root.addContent(date);
		doc = new Document(root);
	}
	
	/**
	* A daily <type> test.record simply has an empty <tests /> tag.
	*/
	public void createHistoryRecord()
	{
		Element root =  new Element("tests");
		doc = new Document(root);
	}
	
	/**
	* A daily <type> test.record simply has an empty <tests /> tag.
	*/
	public void createBlankRecord(String root_name)
	{
		Element root =  new Element(root_name);
		doc = new Document(root);
	}
	
	/**
	*These level # type.test files look like this:
	<test>
  <option>
    <name>description</name>
    <value>This is a simple 10 word English to Korean text.</value>
  </option>
  <option>
    <name>type</name>
    <value>reading</value>
  </option>
  <option>
    <name>length</name>
    <value>10</value>
  </option>
  <option>
    <name>levels</name>
    <value>2-2</value>
  </option>
  <option>
    <name>index</name>
    <value>29</value>
  </option>
  <option>
    <name>shuffle</name>
    <value>yes</value>
  </option>
<score><grade>50</grade><date>Sun Oct 16 10:39:54 PST 2005</date></score>
</test>
	*/
	
	public void createLevelTestFiles(int i, String type, Hashtable name_values)
	{
		Element root =  new Element("test");
		int j = 0;
		Enumeration e = name_values.keys();
		while (e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			String val = (String)name_values.get(key);
			Element option = new Element("option");
			Element name = new Element("name");
			name.addContent(key);
			Element value = new Element("value");
			value.addContent(val);
			option.addContent(name);
			option.addContent(value);
			root.addContent(option);
		}
		doc = new Document(root);
	}
	
	public void createOptions(Hashtable name_values)
	{
		Element root =  new Element("user_options");
		int j = 0;
		Enumeration e = name_values.keys();
		while (e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			String val = (String)name_values.get(key);
			Element option = new Element("option");
			Element name = new Element("name");
			name.addContent(key);
			Element value = new Element("value");
			value.addContent(val);
			option.addContent(name);
			option.addContent(value);
			root.addContent(option);
		}
		doc = new Document(root);
	}
	
	public void createNewXMLFile(String root_name)
	{
		Element root =  new Element(root_name);
		doc = new Document(root);
	}
	
	public void deleteUserPass(String user_name)
	{
		Element root = doc.getRootElement();
		List users = root.getChildren("user");
		int size = users.size();
		int i = 0;
		while (i<size)
		{
			try
			{
				Element e = (Element)users.get(i);
				String user = e.getChildText("name");
				if (user.equals(user_name))
				{
					root.removeContent(e);
				}
			} catch (java.lang.IndexOutOfBoundsException iioob)
			{
				log.add(iioob.toString());
			}
			i++;
		}
	}
	
	public String getInvitationCode()
	{
		Element root = doc.getRootElement();
		Element i_c = root.getChild("invitation_code");
		String code = i_c.getText();
		return code;
	}
	
	public void resetLoginEntries()
	{
		Element root = new Element("entries");
		doc = new Document(root);
	}
	
	/**
	*THis method search es the user.passes file and finds the users name and returns the id for that user.
	*/
	public String getUserId(String user_name)
	{
		String user_id = new String();
		Element root = doc.getRootElement();
		List entries = root.getChildren("user");
		int size = entries.size();
		log.add("JDOMFiles.getUserId: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)entries.get(i);
			String this_user_name = e.getChildText("name");
			log.add("found "+ this_user_name);
			if (user_name.equals(this_user_name))
			{
				user_id = e.getChildText("id");
				log.add("matches "+user_id+" "+ this_user_name);
			}
			i++;
		}
		return user_id;
	}
	
	/**
	*This method gets id-name pairs from the user.passes file.
	*/
	public Hashtable getUserIds()
	{
		Hashtable user_ids = new Hashtable();
		Element root = doc.getRootElement();
		List entries = root.getChildren("user");
		int size = entries.size();
		log.add("JDOMFiles.getUserIds: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)entries.get(i);
			String user_name = e.getChildText("name");
			String user_id = e.getChildText("id");
			log.add(user_name+" "+user_id);
			try
			{
				user_ids.put(user_id, user_name);
			} catch (java.lang.NullPointerException npe)
			{
				log.add("JDOMFiles.getUserIds: npe "+i);
			}
			i++;
		}
		return user_ids;
	}
	
	/**
	* Looking for a property to set a new value.
	* <option>
	*	<name>property</name>
	*	<value>value</value>
	* </option>
	*/
	public void changeOption(String property, String new_value)
	{
		Element root = doc.getRootElement();
		List options = root.getChildren("option");
		int size = options.size();
		log.add("JDOMFiles.changeOption: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)options.get(i);
			String this_option = (String)e.getChildText("name");
			log.add("found "+ this_option);
			if (property.equals(this_option))
			{
				String old_value = (String)e.getChildText("value");
				log.add("found "+property+" with value "+ old_value);
				Element value  = e.getChild("value");
				value.setText(new_value);
				break;
			}
			i++;
		}
	}
	
	/**
	*Returns the list of teacher ids in the files/admin/teachers.xml.
	*/
	public Vector getTeacherNames()
	{
		Vector teachers = new Vector();
		Element root = doc.getRootElement();
		List options = root.getChildren("teacher");
		int size = options.size();
		log.add("JDOMFiles.getTeachers: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)options.get(i);
			String id = (String)e.getText();
			teachers.add(id);
			i++;
		}
		return teachers;
	}
	
	/**
	*In the teacher_id's folder is a file classes.xml with the following format:
	* <classes>
	* <class>
	*	<class_id>0000000000000000001</class_id>
	*	<class_name>jukseong5<class_name> 
	* </class>
	* </classes>
	*The class_id element represents a file inside the classes folder which is
	* just a list of student id's for students in each class.
	*This method will return the class_id-class_name pairs as a Hashtable.
	*The full path to the file must be passed in to a constructor.
	*/
	public Hashtable getClassNames()
	{
		Hashtable classes = new Hashtable();
		Element root = doc.getRootElement();
		List options = root.getChildren("class");
		int size = options.size();
		log.add("JDOMFiles.getClassNames: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)options.get(i);
			String class_id = (String)e.getChildText("class_id");
			String class_name = (String)e.getChildText("class_name");
			classes.put(class_id, class_name);
			i++;
		}
		return classes;
	}
	
	/**
	*Return a  Vector student ids from the file
	* /home/timmy/Desktop/tomcat WEB-INF/files/admin/teachers/<teacher_id>/classes/<class_id>.xml file
	* which has the format:
	*<class>
	*  <student>-7028961644117975960</student>
	*	...
	*/
	public Vector getClassIds()
	{
		Vector students = new Vector();
		Element root = doc.getRootElement();
		List options = root.getChildren("student");
		int size = options.size();
		log.add("JDOMFiles.getStudentNames: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)options.get(i);
			String student_name = (String)e.getText();
			students.add(student_name);
			i++;
		}
		return students;
	}
	
	/**
	 * Create an entry in file/admin/teachers/teacher_id/classes.xml
<?xml version="1.0" encoding="UTF-8"?>
<classes>
	<class>
		<class_id>0000000000000000001</class_id>
		<class_name>jukseong5</class_name>
	</class>
	...
</classes>
	 * @param class_name
	 * @param class_id
	 */
	public void createClass(String class_name, long class_id, String file_name, String encoding)
	{
		Element root = doc.getRootElement(); // this is the classes element.
		Element new_class = new Element("class");
		Element class_name_elem = new Element("class_name");
		Element class_id_elem = new Element("class_id");
		class_name_elem.addContent(class_name);
		class_id_elem.addContent(class_id+"");
		new_class.addContent(class_name_elem);
		new_class.addContent(class_id_elem);
		root.addContent(new_class);
		writeDocument(file_name, encoding);
		// now create a xml file with a </classes> root.
	}
	
	/**
	 * Not yet implemented.
	 * @param list_name
	 * @param list_id
	 * @param file_name
	 * @param encoding
	 */
	public void addToList(String list_name, long list_id, String file_name, String encoding)
	{
		Element root = doc.getRootElement(); // this is the classes element.
		Element new_class = new Element("list");
		Element class_name_elem = new Element("list_name");
		Element class_id_elem = new Element("list_id");
		class_name_elem.addContent(list_name);
		class_id_elem.addContent(list_id+"");
		new_class.addContent(class_name_elem);
		new_class.addContent(class_id_elem);
		root.addContent(new_class);
		writeDocument(file_name, encoding);
	}
	
	public void createClassFile(long class_id, String file_name)
	{
		Document new_document = new Document();
		new_document.addContent(new Element("class"));
		writeDocument(file_name, new_document);
	}
	
	public void createNewDocument(String file_name, String root_name)
	{
		Document new_document = new Document();
		new_document.addContent(new Element(root_name));
		writeDocument(file_name, new_document);
	}
	
	/**
	 * Delete the entry with the id passed in.
	 * file/admin/teachers/teacher_id/classes.xml
		<?xml version="1.0" encoding="UTF-8"?>
	<classes>
	<class>
		<class_id>0000000000000000001</class_id>
		<class_name>jukseong5</class_name>
	</class>
	...
	</classes>
	 * @param class_id_to_delete
	 */
	public void deleteClass(long class_id_to_delete, String file_name)
	{
		Element root = doc.getRootElement();
		List options = root.getChildren("class");
		int size = options.size();
		log.add("JDOMFiles.getClassNames: list size "+size);
		int i = 0;
		while (i<size)
		{
			Element e = (Element)options.get(i);
			String class_id = (String)e.getChildText("class_id");
			String class_id_to_delete_string = class_id_to_delete+"";
			if (class_id.equals(class_id_to_delete_string))
			{
				root.removeContent(i);
				break;
			}
			i++;
		}
		writeDocument(file_name);
	}
	
	/**
	 * in file/admin/teachers/teacher_id/classes/class_id.xml
<?xml version="1.0" encoding="UTF-8"?>
<class>
	<student>-7028961644117975960</student>
	...
	<student>4201001381186235795</student>
</class>
	 * @param student_id
	 * @param class_file
	 */
	public  void addToClass(String student_id, String class_file)
	{
		Element root = doc.getRootElement();
		Element student = new Element("student");
		student.addContent(student_id);
		root.addContent(class_file); // there, that was easy.
		writeDocument(class_file);
	}
	
	/**
	 * Like addTocClass above, this adds a list of ids to the same class.
	 * @param student_ids
	 * @param class_file
	 */
	public  void addToClass(Vector student_ids, String class_file)
	{
		Element root = doc.getRootElement();
		int i = 0;
		int size = student_ids.size();
		while (i<size)
		{
			Element student = new Element("student");
			String student_id = (String)student_ids.get(i);
			log.add("JDOMFiles.addToClass: student_id "+student_id);
			student.addContent(student_id);
			root.addContent(student); // there, that was easy.
			i++;
		}
		writeDocument(class_file);
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
			log.add("JDOMFiles.writeDocument1: IOE "+file_name);
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
			log.add("JDOMFiles.writeDocument2: IOE "+file_name);
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name, Document new_doc)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(new_doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			log.add("JDOMFiles.writeDocument3: IOE "+file_name);
			e.printStackTrace();
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
    
    public Vector getLog()
    {
	    return log;
    }

}
