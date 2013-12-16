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

//import com.businessglue.util.CreateJDOMList;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.Domartin;
import org.catechis.legacy.CreateJDOMList;

// for debugging
import org.catechis.Transformer;
public class ExcludeWordTest extends TestCase
{

	 Vector elt_vector;
	
    public ExcludeWordTest (String name) 
    {
        super(name);
    }
    
    /**
    * The setup
    */
    protected void setUp() throws Exception
    {
	    elt_vector = new Vector();
	    elt_vector.add("2");
	    elt_vector.add("7");
	    elt_vector.add("14");
	    elt_vector.add("30");
    }
    
    public void testExcludeWord()
    {
	    String type = new String("reading");
	    int level = 0;
	    Date date = new Date();
	    String now = date.toString();
	    // create current date to force exlude.
	
	    Word word = new Word();
	    String expected_name = new String("text");
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test[] tests = new Test[3];
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    tests[0] = test1;
	    Test test2 = new Test();
	    String expected_date = new String("Tue Aug 16 08:09:00 PST 2005");
	    test2.setDate(expected_date);
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    tests[1] = test2;
	    Test test3 = new Test();
	    String expected_date2 = new String(now);
	    test3.setDate(expected_date2);
	    test3.setName("level 0 reading.test");
	    test3.setGrade("fail");
	    tests[2] = test3;
	    word.setTests(tests);
	    
	    Vector this_elt_vector = new Vector();
	    this_elt_vector.add("4");
	    this_elt_vector.add("8");
	    this_elt_vector.add("14");
	    this_elt_vector.add("30");
	    
	    ExcludeWord ex_word = new ExcludeWord();
	    boolean expected_result = true;
	    boolean actual_result = ex_word.checkExclusionDate(this_elt_vector, level, word, type);
	    printLog(ex_word.getLog());
	    assertEquals(expected_result, actual_result);
    }
    
     public void testExcludeWord1()
    {
	    String type = new String("reading");
	    int level = 0;
	    Date date = new Date();
	    String now = date.toString();
	    // create current date to force exlude.
	
	    Word word = new Word();
	    String expected_name = new String("text");
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test[] tests = new Test[3];
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    tests[0] = test1;
	    Test test2 = new Test();
	    String expected_date = new String("Tue Aug 16 08:09:00 PST 2005");
	    test2.setDate(expected_date);
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    tests[1] = test2;
	    Test test3 = new Test();
	    String expected_date2 = new String(now);
	    test3.setDate(expected_date2);
	    test3.setName("level 0 reading.test");
	    test3.setGrade("fail");
	    tests[2] = test3;
	    word.setTests(tests);
	    
	    ExcludeWord ex_word = new ExcludeWord();
	    boolean expected_result = true;
	    boolean actual_result = ex_word.checkExclusionDate(elt_vector, level, word, type);;
	    printLog(ex_word.getLog());
	    assertEquals(expected_result, actual_result);
    }
    
    public void testIncludeWord()
    {
	    String type = new String("reading");
	    int level = 0;
	    Date date = new Date();
	    String now = date.toString();
	
	    Word word = new Word();
	    String expected_name = new String("text");
	    word.setText("text");
	    word.setDefinition("def");
	    word.setWritingLevel(0);
	    word.setReadingLevel(0);
	    Test[] tests = new Test[3];
	    Test test1 = new Test();
	    test1.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test1.setName("level 0 reading.test");
	    test1.setGrade("pass");
	    tests[0] = test1;
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    tests[1] = test2;
	    Test test3 = new Test();
	    test3.setDate("Tue Aug 16 11:11:11 PST 2005");	// last test date
	    test3.setName("level 0 reading.test");
	    test3.setGrade("fail");
	    tests[2] = test3;
	    word.setTests(tests);
	    
	    ExcludeWord ex_word = new ExcludeWord();
	    boolean expected_result = false;
	    boolean actual_result = ex_word.checkExclusionDate(elt_vector, level, word, type);
	    printLog(ex_word.getLog());
	    assertEquals(expected_result, actual_result);
    }
    
    /**
    * This method gave the following output:
    	ExcludeWordTest.testExcludeFile: path = /home/timmy/projects/catechis/files/test_user/october.xml
    	ExcludeWordTest.testExcludeFile: exists true
    * and produced the following error:
	java.lang.NullPointerException at org.catechis.legacy.CreateJDOMList.<init>(CreateJDOMList.java:76) at org.catechis.ExcludeWordTest.testExcludeFile(ExcludeWordTest.java:184)
    *
    public void testExcludeFile()
    {
	    String file_name = new String("october.xml");
	    String user_name = new String("test_user");
	    	    // setup word file
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user_name+File.separator+file_name);
	    File test_file = new File(path);
	    System.out.println("ExcludeWordTest.testExcludeFile: path = "+test_file.getPath());
	    boolean exists = test_file.exists();
	    System.out.println("ExcludeWordTest.testExcludeFile: exists "+exists);
	    CreateJDOMList jdom = new CreateJDOMList(test_file);
	    int test_level = 0;
	    String test_levels = new String("0");
	    String test_index = new String("1");
	    String test_length = new String("10");
	    String test_type = new String("reading");
	    Word test_word = new Word();
	    Hashtable un_shuffled_words_defs = jdom.getFilteredTextDefHash2(test_length, test_index, test_levels, test_type, elt_vector);
	    int actual_count = 0;
	    ExcludeWord ex_word = new ExcludeWord();
	    for (Enumeration e = un_shuffled_words_defs.elements() ; e.hasMoreElements() ;) 
	    {
		    test_word = (Word)e.nextElement();	// (elt_vector, level, word, type)
		    if (ex_word.checkExclusionDate(elt_vector, test_level, test_word, test_type))
		    {
			    actual_count++;
		    }
	    }
	    printLog(jdom.getLog());
	    int expected_count = 20;
	    assertEquals(expected_count, actual_count);
    }
    */
    
    public void TestExcludeWordObjects()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    Vector actual_words = store.getWordObjects("word", "test_user");
	    Word first_word = (Word)actual_words.get(0);
	    String actual_definition = first_word.getDefinition();
	    String expected_definition = new String("beach");
	    int expected_writing_level = 6;
	    int actual_writing_level = first_word.getWritingLevel();
	    int actual_reading_level = first_word.getReadingLevel();
	    assertEquals(expected_definition, actual_definition);
	    assertEquals(expected_writing_level, actual_writing_level);
    }

    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(ScoringTest.class);
    }
    
    private void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println(" "+log.get(i));
			i++;
		}
	}

}

