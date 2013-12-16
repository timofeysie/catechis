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
import org.catechis.FileStorage;
import org.catechis.dto.TestStats;
import org.catechis.JDOMSolution;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileEdit;
import org.catechis.file.FileCategories;

public class FileEditTest extends TestCase
{

    public FileEditTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {

    }
    
    public void testEditWord()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"word1.xml");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("word1 copy1.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    Word old_word = new Word();
	    old_word.setText("text");
	    //old_word.setDefinition("\ud3b8\uc9d1\ud558\ub2e4");
	    Word new_word = new Word();
	    new_word.setText("sexy");
	    new_word.setDefinition("sexxxy");
	    // run test cleanup and check
	    String encoding = new String("euc-kr");
	    FileEdit edit = new FileEdit(duplicate_path);
	    edit.editWord(old_word, new_word);
	    edit.writeDocument(duplicate_path, encoding);
	    //dumpLog(edit.getLog());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word actual_word = jdom2.findWordWithoutTests("sexy");
	    String actual = actual_word.getText();
	    String expected = new String("sexy");
	    duplicate_file.delete();
	    assertEquals(expected, actual);
    }
    
    public void testDeleteWord()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+"word1.xml");
	    File original_file = new File(path);
	    // duplicate file
	    String copy_cat = new String("word1 copy.xml");
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    Word old_word = new Word();
	    old_word.setText("text");
	    old_word.setDefinition("def");
	    String encoding = new String("euc-kr");
	    FileEdit edit = new FileEdit(duplicate_path);
	    edit.deleteWord(old_word);
	    edit.writeDocument(duplicate_path, encoding);
	    //dumpLog(edit.getLog());
	    JDOMSolution jdom2 = new JDOMSolution(duplicate_file);
	    Word actual_word = jdom2.findWordWithoutTests("text");
	    boolean actual = true;
	    String test = actual_word.getText();
	    if (test == null)
	    {
		    actual = false;
	    }
	    boolean expected = false;
	    //duplicate_file.delete();
	    assertEquals(expected, actual);
    }
    
    public void testEditWord2()
    {
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String user = new String("test_user");
	    String search_id = "-4583428238372124649";
	    String category = "copy of october.xml";
	    String user_id = "test_user";
	    //String encoding = new String("euc-kr");
	    String encoding = new String("UTF-8");
	    // original file
	    String path = new String(current_dir+File.separator+"files"
		+File.separator+user+File.separator+category);
	    File original_file = new File(path);
	    // get original text
	    String user_folder = current_dir+File.separator+"files"+File.separator+user;
	    FileJDOMWordLists fjdomwl = new FileJDOMWordLists(user_folder);
	    Word original_word = fjdomwl.getSpecificWord(search_id, category, user_id, current_dir);
	    String expected_text = original_word.getText();
	    // duplicate file
	    
	    String copy_cat = "copy of "+category;
	    String duplicate_path = new String(current_dir+File.separator+"files"
	    	+File.separator+user+File.separator+copy_cat);
	    File duplicate_file = new File(duplicate_path);
	    // copy original to duplicate file
	    FileStorage fs = new FileStorage();
	    fs.copyFile(original_file, duplicate_file);
	    // setup for test
	    FileEdit edit = new FileEdit();
	    //edit.editWord2(original_word, duplicate_path);
	    edit.editWord2(original_word, duplicate_path, encoding);
	    FileJDOMWordLists fjdomwl1 = new FileJDOMWordLists(user_folder);
	    Word actual_word = fjdomwl1.getSpecificWord(search_id, copy_cat, user_id, current_dir);
	    //Word actual_word = fjdomwl1.getSpecificWord(search_id, category, user_id, current_dir);
	    String actual_text = actual_word.getText();
	    //duplicate_file.delete();
	    System.out.println(expected_text);
	    System.out.println(actual_text);
	    assertEquals(expected_text, actual_text);
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
        junit.textui.TestRunner.run(FileEditTest.class);
    }

}
