package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;
import java.util.Date;
import java.util.Set;
import java.io.File;

import junit.framework.TestCase;

import org.catechis.JDOMSolution;
import org.catechis.Transformer;
import org.catechis.dto.Word;
import org.catechis.dto.Test;

import org.apache.commons.beanutils.*;
import org.apache.commons.collections.FastHashMap;

import org.jdom.Element;

import org.apache.struts.action.DynaActionForm;

public class TransformerTest extends TestCase
{

	public TransformerTest (String name)
	{
		super(name);
	}
    
	protected void setUp() throws Exception
	{
	}
	
	/**
	* We want to test that the Transform objevt transforms 
	* a long date in this format: "EEE MMM dd HH:mm:ss zzz yyyy"
	* to a short date of this format: "MM/dd/yy"
	*<p>The expected date is the 16th I presume, due to the time zone difference. 
	*/
	public void testSimplifyDate()
	{
		String date = new String("Mon Aug 15 08:09:00 PST 2005"); // 09/28/05 ?
		String actual_date = Transformer.simplifyDate(date);
		String expected_date = new String("08/16/05");
		//System.out.println("TransformerTest.testSimplifyDate: expected "+expected_date);
		//System.out.println("TransformerTest.testSimplifyDate: actual   "+  actual_date);
		assertEquals(expected_date, actual_date);
	}
	
	public void testDumpBean()
	{
	    Word word = new Word();
	    word.setText("anyoung");
	    word.setDefinition("hello");
	    word.setWritingLevel(1);
	    word.setReadingLevel(2);
	    SimpleDateFormat df = new SimpleDateFormat();
	    Date date = new Date();
	    try
	    {
		    date = df.parse("Mon Aug 15 08:09:00 PST 2005");
	    } catch (java.text.ParseException pe)
	    {}
	    long date_long = date.getTime();
	    word.setDateOfEntry(date_long);
	    Vector v_properties = Transformer.createTable(word);
	    int v_size = v_properties.size();
	    int index = 0;
	    while (index<v_size)
	    {
		    //System.out.println("TransformerTest.testDumpBean: "+v_properties.get(index));
		    index++;
	    }
	    
	    
	    int actual_size = v_properties.size();
	    int expected_size = 5;
	    assertEquals(expected_size, actual_size);
	}
	
	public void testGetWordTestBean()
	{
		int number_of_words_to_test = 5;
		DynaBean test = Transformer.getWordTestBean(number_of_words_to_test);
		//printLog(Transformer.createTable(test));
	}

	/**
	*<element>
		<sub_element1>test1</sub_element1>
		<sub_element2>test2</sub_element2>
	</element>
	*/
	public void testElementIntoHash()
	{
		Element element = new Element("element");
		Element sub_element1 = new Element("sub_element1");
		Element sub_element2 = new Element("sub_element2");
		sub_element1.addContent("test1");
		sub_element2.addContent("test2");
		element.addContent(sub_element1);
		element.addContent(sub_element2);
		Transformer trans = new Transformer();
		Hashtable results = trans.elementIntoHash(element);
		int size = results.size();
		//System.out.println("Transformer.testElementIntoHash "+size);
		//printLog(trans.getLog());
		//dumpLog(results);
		Enumeration keys = results.keys();
		String actual = (String)results.get(keys.nextElement());
	        String expected = new String("test2");
		assertEquals(expected, actual);
	}
	
	public void testGetStringFromBytesString()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String user_name = new String("guest");
		String category = new String("very much");
		String search_value = new String("Very much");
		String search_property = new String("definition");
		FileStorage store = new FileStorage(current_dir);
		Word word = store.getWordObject(search_property, search_value, category, user_name);
		//String text = word.getText(); // test for encoded korean
		String text = word.getDefinition();
		String text_bytes = Transformer.getByteString(text);
		// String str_frm_bytes = Transformer.getStringFromBytesString(word.getText()); // idtto
		String str_frm_bytes = Transformer.getStringFromBytesString(word.getDefinition());
		//System.out.println("Transformer.testGetStringFromBytesString");
		//System.out.println("text bytes "+text_bytes);
		//System.out.println("str frm bytes "+str_frm_bytes);	// returns nothing
		String actual = text;
	        String expected = str_frm_bytes;
		assertEquals(expected, actual);
	}
	
    public void testWordTestsIntoTypeData()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    //String user = new String("test_user");
	    String user = new String("-5519451928541341468");
	    //String path = new String(current_dir+File.separator+"files"
	    //		+File.separator+user+File.separator+"october.xml");
	    String path = new String(current_dir+File.separator+"files"
	    		+File.separator+user+File.separator+"March II.xml");
	    File file = new File(path);
	    JDOMSolution jdom = new JDOMSolution(file);
	    Word word = jdom.findWord("schedule");
	    System.out.println("Transformer.testWordTestsIntoTypeData word "+word.getText());
	    printLog(jdom.getLog());
	    Test [] tests = word.getTests();
	    int num = tests.length;
	    System.out.println("tests "+num);
	    int i = 0;
	    while (i<num)
	    {
		    Test test = tests[i];
		    System.out.println(i);
		    printLog(Transformer.createTable(test));
		    i++;
	    }
	    String get_type = "reading";
	    String max_level = "3";
	    Vector data = Transformer.wordTestsIntoTypeData(word, get_type, max_level);
	    System.out.println("datum "+data.size());
	    printLog(data);
    }
	
	
	/* Test creation for the poeople:
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("october");
	    test1.setGrade("pass");
	    Test test2 = new Test();
	    test2.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test2.setName("october");
	    test2.setGrade("pass");
	    Test[] tests = new Test[2];
	    tests[0] = test1;
	    tests[1] = test2;
	*/
	
	/** debuggin only!  dont try this at home!*/
	private void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println(i+" "+log.get(i));
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

}
