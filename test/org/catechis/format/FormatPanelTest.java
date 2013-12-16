package org.catechis.format;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;

import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import org.catechis.FileStorage;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.Transformer;
import org.catechis.format.FormatPanel;

public class FormatPanelTest extends TestCase
{

    public FormatPanelTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {

    }
    
    public void testCalculateVectorWidths()
    {
	    System.out.println("testCalculateVectorWidths ---");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    String file_name = "october1.xml";
	    String user_name = "guest";
	    String type = "reading";
	    FormatPanel fp = new FormatPanel();
	    Vector r_all_words = store.getWordObjectsForTest(file_name, user_name);
	    System.out.println("r_all_words lengths "+r_all_words.size());  // 37
	    System.out.println("getWordObjectsForTest log ---------------");
	    //printLog(store.getLog());
	    
	    Vector w_all_words = r_all_words; //store.getWordObjectsForTest(file_name, user_name);
	    store.resetLog();
	    Hashtable r_words_defs = store.getWordsDefs(file_name, user_name);
	    Hashtable w_words_defs = r_words_defs; //store.getWordsDefs(file_name, user_name);
	    System.out.println("words_defs lengths "+r_words_defs.size());// 32
	    System.out.println("getWordsDefs log ---------------=========");
	    //printLog(store.getLog());
	    
	    fp.setupForAllWidths(r_all_words, w_all_words, r_words_defs, w_words_defs);
	    fp.setVisible(true);
	    Vector r_increments = fp.getReadingIncrements();
	    System.out.println("r increments "+r_increments.size());   // 
	    Vector r_all_words_widths = fp.getVectorWidths(type);
	    System.out.println("r lengths "+r_all_words_widths.size());   // 0   aioob !!!
	    Vector w_all_words_widths = fp.getVectorWidths("writing");
	    fp.dispose();
	    System.out.println("w lengths "+r_all_words_widths.size());   // 0
	    //printWordsVector(w_all_words_widths);
	    //printLog(fp.getLog());
	    Hashtable words_defs = store.getWordsDefs(file_name, user_name);
	    String expected = "61"; 		//		movie buff 	61
	    Word first_word = (Word)r_all_words_widths.get(0);		// aioob
	    String actual = first_word.getDefinition();
	    System.out.print("expected "+expected+"   	actual "+actual);
	    assertEquals(expected, actual);
    }
    
    public void testCalculateVectorWidths2()
    {
	    System.out.println("testCalculateVectorWidths2 --- ");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    String file_name = "format.xml";
	    String user_name = "guest";
	    String type = "reading";
	    FormatPanel fp = new FormatPanel(61);
	    Vector r_all_words = store.getWordObjectsForTest(file_name, user_name);
	    Vector w_all_words = r_all_words; //store.getWordObjectsForTest(file_name, user_name);
	    Hashtable r_words_defs = store.getWordsDefs(file_name, user_name);
	    Hashtable w_words_defs = r_words_defs; //store.getWordsDefs(file_name, user_name);
	    System.out.println("all_words lengths "+r_all_words.size());
	    System.out.println("words_defs lengths "+r_words_defs.size());
	    fp.setupForAllWidths(r_all_words, w_all_words, r_words_defs, w_words_defs);
	    fp.setVisible(true); // this calls the print() method which performs all the work.
	    System.out.println("fp.getLog() -------- ");
	    Vector r_increments = fp.getReadingIncrements();
	    System.out.println("-------------------- ");
	    
	    /*why is the length 15 when the output lists 29 size
	    * and 37 in words_defs
	    */
	    
	    System.out.println("r increments "+r_increments.size());
	    //printWordsVector(r_increments);	// class casrt exception
	    printLog(fp.getLog());
	    fp.dispose();
	    int expected = 29; 		//		movie buff 	61
	    //String actual = (String)r_increments.get(0);	// Array index out of range: 0
	    int actual = r_increments.size();
	    System.out.print("expected "+expected+"   	actual "+actual);
	    assertEquals(expected, actual);
    }
    
    /*
    public void testBoolean()
    {
	    String format_print_reading = ("true");		// These options turn on or off the lists that will be printed on the jsp page.
	    String format_print_writing = ("false");
            String format_print_missed_reading = ("true");
	    String format_print_missed_writing = ("false");
	    boolean format_print_reading1 = Boolean.getBoolean(format_print_reading);
	    boolean format_print_writing1 = Boolean.getBoolean(format_print_writing);
	    boolean format_print_missed_reading1 = Boolean.getBoolean(format_print_missed_reading);
	    boolean format_print_missed_writing1 = Boolean.getBoolean(format_print_missed_writing);
	    System.out.println(format_print_reading1+" "+format_print_writing1);
	    System.out.println(format_print_missed_reading1+" "+format_print_missed_writing1);
	    
	    String format_print_readingA = ("true");		// These options turn on or off the lists that will be printed on the jsp page.
	    String format_print_writingA = ("false");
            String format_print_missed_readingA = ("true");
	    String format_print_missed_writingA = ("false");
	    boolean format_print_reading2 = new Boolean(format_print_readingA).booleanValue();
	    boolean format_print_writing2 = new Boolean(format_print_writingA).booleanValue();
	    boolean format_print_missed_reading2 = new Boolean(format_print_missed_readingA).booleanValue();
	    boolean format_print_missed_writing2 = new Boolean(format_print_missed_writingA).booleanValue();
	    System.out.println(format_print_reading2+" "+format_print_writing2);
	    System.out.println(format_print_missed_reading2+" "+format_print_missed_writing2);
	    boolean expected = "61"; 		//		movie buff 	61
	    boolean actual = (String)r_increments.get(0);
	    //System.out.print("expected "+expected+"   	actual "+actual);
	    assertEquals(expected, actual);
    }
    */
    
    private void printObject(Object obj)
    {
	    Vector vect = Transformer.createTable(obj);
	    int size = vect.size();
	    int index = 0;
	    while (index<size)
	    {
		    index++;
	    }
    }
    
    /** debuggin only!  dont try this at home!*/
	private void printWordsVector(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			Word word = (Word)log.get(i);
			System.out.println(word.getText()+" "+word.getDefinition());
			i++;
		}
	}
    
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
    
    /* code graveyard
    		    Vector word_log = filter.getLog();
		    int s = word_log.size();
		    int i = 0;
		    while (i<s)
		    {
			    System.out.println("FilterTest.testFilterWord: "+word_log.get(i));
			    i++;
		    }
		    
		    //printObject(word);
     */

}
