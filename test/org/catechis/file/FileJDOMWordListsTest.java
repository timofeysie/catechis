package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.awt.Color;
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
import org.catechis.FileStorage;
import org.catechis.Domartin;
import org.catechis.dto.TestStats;
import org.catechis.JDOMSolution;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileEdit;
import org.catechis.file.FileCategories;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.juksong.FarmingTools;
import org.catechis.constants.Constants;

public class FileJDOMWordListsTest extends TestCase
{
	private String test_str_id;
	private long id;

    public FileJDOMWordListsTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
	    id = Domartin.getNewID();
	    test_str_id = Long.toString(id);
	     //System.out.println("id "+test_str_id);
    }
    
    /*
    public void testConstructor()
    {
	    //System.out.println("TEST 0");
	    Word word = new Word();
	    word.setText("text");
	    word.setDefinition("def");
	    word.setId(id);
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    String path_to_fake_word_file = "doesnt exist";
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_name;
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
	    Vector vlog = fjdomwl.getLog();
	    //System.out.println("log size "+vlog.size());
	    //dumpLog(vlog);
	    //int actual = list1.size()+1;
	    //int expected = list2.size();
	    //assertEquals(expected, actual);
    }*/
    
    public void testAddToNewWordsList()
    {
	   //System.out.println("FileJDOMWordListsTest.testAddToNewWordsList ----");
	    String encoding = new String("UTF-8");
	    Word word = new Word();
	    word.setText("text");
	    word.setDefinition("def");
	    word.setId(id);
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    String path_to_fake_word_file = "doesnt exist";
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_name;
	    FileJDOMWordLists fjdomwl1 = new FileJDOMWordLists(user_folder);
	    fjdomwl1.setSubject(Constants.VOCAB);
	    //System.out.println(user_folder);
	    //dumpLog(fjdomwl1.getLog());
	    Vector list1 = fjdomwl1.getNewWordsList(type);
	    ////System.out.println("list 1");
	    //dumpList1(list1);
	    //System.out.println("end ---- list 1");
	    
	    FileJDOMWordLists fjdomwl2 = new FileJDOMWordLists(user_folder);
	    fjdomwl2.setSubject(Constants.VOCAB);
	    fjdomwl2.addToNewWordsList(word, path_to_fake_word_file, type, encoding);
	    Vector list2 = fjdomwl2.getNewWordsList(type);
	    //System.out.println("list 2");
	    //dumpList1(list2);
	    //System.out.println("end ---- list 2");
	    
	    fjdomwl2.removeWordFromNewWordsList(test_str_id, encoding);
	    //dumpLog(fjdomwl2.getLog());
	    int actual = list1.size()+1;
	    int expected = list2.size();
	    assertEquals(expected, actual);
    }
    
    public void testGetNewWordsList()
    {
	    //System.out.println("TEST 2");
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_name;
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
	    fjdomwl.setSubject(Constants.VOCAB);
	    Vector list = fjdomwl.getNewWordsList(type);
	    //dumpList(list);
	    //dumpLog(fjdomwl.getLog());
	    /*this doenst really prove anything */
	    boolean actual = list.isEmpty();
	    boolean expected = false;
	    assertEquals(expected, actual);
    }
    
    public void testGetNoRepeatsNewWordsList()
    {
	    //System.out.println("TEST 3");
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_name;
	    //System.out.println("user_folder  "+user_folder);
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
	    fjdomwl.setSubject(Constants.VOCAB);
	    Vector list1 = fjdomwl.getNewWordsList(type);
	    Vector list2 = fjdomwl.getNoRepeatsNewWordsList(type, list1);
	    //System.out.println("list 1  "+list1.size());
	    //System.out.println("list 2  "+list2.size());
	    //dumpLog(fjdomwl.getLog());
	    boolean actual = list2.isEmpty();
	    boolean expected = true;
	    assertEquals(expected, actual);
    }
    
    /**
    *Testing removeWordFromNewWordsList(String search_id, String type, String user_name, String encoding);
    */
    public void testRemoveWordFromNewWordsList()
    {
	    //System.out.println("FileJDOMWordListsTest.testRemoveWordFromNewWordsList ---");
	    String encoding = new String("UTF-8");
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    String subject = Constants.VOCAB;
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    // first, add a test word
	    long new_id = Domartin.getNewID();
	    String new_test_str_id = Long.toString(new_id);
	    Word word = new Word();
	    word.setText("text1");
	    word.setDefinition("def1");
	    word.setId(new_id);
	    String path_to_fake_word_file = "doesnt exist";
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_name;
	    //System.out.println("user_folder "+user_folder);
	    String file_name = new String(user_folder+File.separator+"files"+File.separator+user_name+File.separator+subject
			+File.separator+"lists"+File.separator+"new words "+type+".list");
	    //System.out.println("fjdomwl will user "+file_name);
	    FileJDOMWordLists fjdomwl0 = new FileJDOMWordLists(current_dir, type, user_name, Constants.VOCAB);
	    
	    Vector list0 = fjdomwl0.getNewWordsList2();
	    //System.out.println("list 0");
	   //dumpList1(list0);
	    //System.out.println("end ---- list 0");
	    // add a word and get the updated list to compare its size later
	    fjdomwl0.addToNewWordsList(word, path_to_fake_word_file, type, encoding);
	    //System.out.println("added "+new_id);
	    FileJDOMWordLists fjdomwl1 = new FileJDOMWordLists(current_dir, type, user_name, Constants.VOCAB);
	    Vector list1 = fjdomwl1.getNewWordsList2();
	    //System.out.println("list 1");
	    //dumpList1(list1);
	    //System.out.println("end ---- list 1");
	    // and now the money shot, remove the test word
	    fjdomwl1.removeWordFromNewWordsList(new_test_str_id, encoding);
	    FileJDOMWordLists fjdomwl2 = new FileJDOMWordLists(current_dir, type, user_name, Constants.VOCAB);
	    fjdomwl2.setSubject(Constants.VOCAB);
	    Vector list2 = fjdomwl2.getNewWordsList2();
	    //System.out.println("list 2 deleted "+new_test_str_id);
	    //System.out.println("list 2");
	    //dumpList1(list2);
	    //System.out.println("end ---- list 2");
	    int actual = list2.size();
	    int expected = list1.size()-1; 
	    assertEquals(expected, actual); 
    }
    
    /*
    This was an attempt to transform all text/definition pairs into bytes,
    * but nedit would show that the text was being multiplied each time the file was saved.
    * later, after much agonizing, it turned out that this was just a flaw with the editor,
    * as the multiplied text fields looked normal when used in the program.
    * BUt by this time we had given up on encoding all the fields.
    *So if there is a problem later, then all these methods will have to be changed again.
    *OK, word to the not-so-wise: if you pass in the file using the File constructor, for some reason
    * encoded items get multiplied.  So just pass in the path, and let the object that does the writing
    * create the file and save it.
    public void testGetStringFromBytesString()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String enc = new String("UTF-8");
		String user_name = new String("guest");
		String category = new String("very much");
		String search_value = new String("Very much");
		String search_property = new String("definition");
		FileStorage store = new FileStorage(current_dir);
		Word word = store.getWordObject(search_property, search_value, category, user_name);
		System.out.println("Word"); 
		dumpLog(Transformer.createTable(word));
		String actual = word.getText();
		//String actual = word.getDefinition();
		String bytes = Transformer.getByteString(actual);
		String bytes_we = Transformer.getByteStringWithEncoding(actual, enc);
		String expected = Transformer.getStringFromBytesString(bytes);
		System.out.println("Transformer.testGetStringFromBytesString"); 
		System.out.println("bytes 	"+bytes);
		System.out.println("bytes we 	"+bytes_we);
		System.out.println("actual: 	"+actual);
		System.out.println("expected:	"+expected);	// returns nothing
		assertEquals(expected, actual);
	}
	*/
	
    /**
    *Add a tag <retired>true</retired> to a word, then also check the isRetired method.
    */
    public void testRetireWord()
    {
	    // copy a word file
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("2960548470376610112");
	    String search_id = new String("-7998125076748455789");
	    String original = new String("random words 14.xml");
	    String copy_cat = new String("copy.xml");
	    String original_path = new String(current_dir+File.separator+"files"+File.separator+user+File.separator+original);
	    File original_file = new File(original_path);
	    String duplicate_path = new String(current_dir+File.separator+"files"+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // test retire method
	    String encoding = new String("euc-kr");
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(duplicate_path);
	    fjdomwl.retireWord(search_id, encoding);
	    fjdomwl.setSubject(Constants.VOCAB);
	    FileJDOMWordLists fjdomwl1 = new FileJDOMWordLists(duplicate_path);
	    fjdomwl1.setSubject(Constants.VOCAB);
	    boolean expected = true;
	    boolean actual = fjdomwl1.isRetired(search_id);
	    duplicate_file.delete();
	    assertEquals(expected, actual);
    }
    
    /**
    *This method checks the first word in the file which has been pre-set with a retired tag.
    */
    public void testIsRetired()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("2960548470376610112");
	    String search_id = new String("-7920458335499530699");
	    String original = new String("random words 14.xml");
	    String original_path = new String(current_dir+File.separator+"files"+File.separator+user+File.separator+original);
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(original_path);
	    fjdomwl.setSubject(Constants.VOCAB);
	    boolean actual = fjdomwl.isRetired(search_id);
	    boolean expected = true; 
	    assertEquals(expected, actual);
    }
    
    
    /**
     * Testing:
     * Word word = fjdomwl.getSpecificWord(word_id+"", category, user_id, context_path);
     */
    public void testGetSpecificWord()
    {
	    System.out.println("testGetSpecificWord:  52 tests");
	    String user_name = "guest";
	    String type = Constants.WRITING;
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String category ="october2.xml";
	    String user_id = "-5519451928541341468";
	    String context_path = current_dir;
	    String user_folder = current_dir+File.separator+"files"+File.separator+user_id;
	    //FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists();
	    //long word_id = Long.getLong("7304045571594794468");
	    String word_id = ("7304045571594794468");
		Word word = fjdomwl.getSpecificWord(word_id, category, user_id, context_path);
		Test tests[] = word.getTests();
		for (int i = 0;i<tests.length;i++)
		{
			Test test = tests[i];
			dumpLog(Transformer.createTable(test));
		}
		
		
		FarmingTools farming = new FarmingTools();
		Color color = farming.ratePerformancebyLightColor(word, type);
		int red = color.getRed();
		int gre = color.getGreen();
		int blu = color.getBlue();
		String color_string = "rgb("+red+","+gre+","+blu+")";
		System.out.println("Log -------");
		dumpLog(farming.getLog());
	    boolean actual = true;
	    boolean expected = false;
	    assertEquals(expected, actual);
    }
    
    // add link and action to use these methods.
    // add retired words.list
    // show total of list on homepage?
    // allow words to be removed, or tested on some schedule?
    // retire separate reading and writing? no!
    // only singel retired words.xml file.
    
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
		    System.out.println(i+" - "+log.get(i));
		    i++;
	    }

    }
    
    private void dumpList(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    Word word = (Word)log.get(i);
		    String text = Transformer.getStringFromBytesString(word.getText());
		    String def = Transformer.getStringFromBytesString(word.getDefinition());
		    System.out.println(text+" - "+def);
		    i++;
	    }

    }
    
    private void dumpList1(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    Word word = (Word)log.get(i);
		    //String text = Transformer.getStringFromBytesString(word.getText());
		    //String def = Transformer.getStringFromBytesString(word.getDefinition());
		    System.out.println(i+" "+word.getId());
		    i++;
	    }

    }
    
    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(FileEditTest.class);
    }

}
