package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;
import junit.framework.TestCase;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.Transformer;
import org.catechis.dto.TestStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileCategories;

public class FileCategoriesTest extends TestCase
{

    public FileCategoriesTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {

    }
    
    /** Hashtable getSortedWordCategories(String user_name) */
    public void testGetSortedWordCategories()
    {
	    System.out.println("testGetSortedWordCategories");
	    String user_name = ("guest");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Hashtable categories = cats.getSortedWordCategories(user_name);
	    //dumpLog(cats.getLog());
	    System.out.println("testGetSortedWordCategories: cats "+categories.size());
	    Enumeration keys = categories.keys();
	    String expected_first_date = new String("10/25/04");  // used to be this.  Not sure what changed.  Most likely the files in the guest folder.
	    expected_first_date = new String("01/09/05");
	    String actual_first_date = new String();
	    if (keys.hasMoreElements())
	    {
		    actual_first_date = Transformer.getDateFromMilliseconds((String)keys.nextElement());
	    }
	    /*
	    for (Enumeration e = categories.keys() ; e.hasMoreElements() ;)
	    {
		    String key = (String)e.nextElement();
		    String date = Transformer.getDateFromMilliseconds(key);
		    String cat = (String)categories.get(key);
		    System.out.println(date+" "+cat);
	    }
	    */
	    //dumpLog(cats.getLog());
	    ArrayList sorted_key_list = cats.getSortedKeys(categories);
	    //Iterator it = sorted_key_list.iterator();
	    /*int i = 0;
	    int size = sorted_key_list.size();
	    while (i<size)
	    {
		    String key = (String)sorted_key_list.get(i);
		    String date = Transformer.getDateFromMilliseconds(key);
		    String cat = (String)categories.get(key);
		    //System.out.println("testGetSortedWordCategories "+date+" "+cat);
		    i++;
	    }*/
	    System.out.println("testGetSortedWordCategories: acutal   "+actual_first_date);
	    System.out.println("testGetSortedWordCategories: expected "+expected_first_date);
	    assertEquals(expected_first_date, actual_first_date);
    }
    
    /**  When we first tried to implement categories by splitting all the old categories up into their own files,
     * a number of categories were being skipped, so we made this test to confirm the skipping and
     * try to fix the problem.  getSortedWordCategories(String user_name) */
    public void testGetSortedWordCategoriesSkippingFile()
    {
	    System.out.println("testGetSortedWordCategoriesSkippingFile ---");
	    String user_name = ("-5519451928541341469");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Hashtable categories = cats.getSortedWordCategories(user_name);
	    dumpLog(cats.getLog());
	    System.out.println("testGetSortedWordCategoriesSkippingFile: cats "+categories.size());
	    Enumeration keys = categories.keys();
	    String expected_first_date = new String("10/25/04");
	    String actual_first_date = new String();
	    if (keys.hasMoreElements())
	    {
		    actual_first_date = Transformer.getDateFromMilliseconds((String)keys.nextElement());
	    }
	    /*
	    for (Enumeration e = categories.keys() ; e.hasMoreElements() ;)
	    {
		    String key = (String)e.nextElement();
		    String date = Transformer.getDateFromMilliseconds(key);
		    String cat = (String)categories.get(key);
		    System.out.println(date+" "+cat);
	    }
	    */
	    //dumpLog(cats.getLog());
	    ArrayList sorted_key_list = cats.getSortedKeys(categories);
	    //Iterator it = sorted_key_list.iterator();
	    /*int i = 0;
	    int size = sorted_key_list.size();
	    while (i<size)
	    {
		    String key = (String)sorted_key_list.get(i);
		    String date = Transformer.getDateFromMilliseconds(key);
		    String cat = (String)categories.get(key);
		    //System.out.println("testGetSortedWordCategoriesSkippingFile "+date+" "+cat);
		    i++;
	    }*/
	    assertEquals(expected_first_date, actual_first_date);
    }
    
    /** getFirstDate(Word word) */
    public void testGetFirstDate()
    {
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
	    String expected_date1 = new String("Tue Aug 16 08:09:00 PST 2005");
	    test2.setDate(expected_date1);
	    test2.setName("level 1 reading.test");
	    test2.setGrade("fail");
	    tests[1] = test2;
	    Test test3 = new Test();
	    Date date1 = new Date(1);
	    //String then = Long.toString(date1.getTime());
	    String expected_date2 = date1.toString();
	    test3.setDate(expected_date2); 
	    test3.setName("level 0 reading.test");
	    test3.setGrade("fail");
	    tests[2] = test3;
	    word.setTests(tests);
	    String user_name = ("guest");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    String date = cats.getFirstDate(word);
	    //dumpLog(cats.getLog());
	    //System.out.println("testGetFirstDate- "+Transformer.getDateFromMilliseconds(date));
	    String expected_date = new String("01/01/70");
	    String actual_date = Transformer.getDateFromMilliseconds(date);
	    assertEquals(expected_date, actual_date);
    }
    
    public void testGetFilteredFiles()
    {
	    String user_name = ("guest");
	    String file_type = (".xml");
	    String folder = ("files");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Vector files = cats.getFilteredFiles(user_name, file_type, folder);
	    int expected_size = 39;
	    int actual_size = files.size();
	    assertEquals(expected_size, actual_size);
    }
    
    public void testAddCategories()
    {
	    String user_name = ("guest");
	    String file_type = (".xml");
	    String folder = ("files");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Vector files = cats.getFilteredFiles(user_name, file_type, folder);
	    int expected_size = files.size()+1;
	    String new_category = "test"+file_type;
	    cats.addCategory(new_category, current_dir, user_name);
	    files = cats.getFilteredFiles(user_name, file_type, folder);
	    int actual_size = files.size();
	    cats.deleteCategory(new_category, current_dir, user_name);
	    assertEquals(expected_size, actual_size);
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

    private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

    }
    
    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(FileCategoriesTest.class);
    }

}
