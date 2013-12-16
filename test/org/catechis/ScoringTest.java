package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;

import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import junit.framework.TestCase;

import org.catechis.dto.Word;
import org.catechis.dto.TestStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.Transformer;

public class ScoringTest extends TestCase
{
	
	private Scoring scorer;
	
    public ScoringTest (String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
	    scorer = new Scoring();
    }
    
    // Testing: public String getRatioString(int index, int score)
    public void testGetRatioString()
    {
	    int index = 10;
	    int score = 7;
	    String expected = new String("7/10");
	    String actual = scorer.getRatioString(index, score);
	    assertEquals(expected, actual);
    }
    
    // Testing: String getPercentageScore(int index, int score)
    public void testGetPercentageScore()
    {
	    double index = 10;
	    double score = 7;
	    String expected = new String("70");
	    String actual = scorer.getPercentageScore(index, score);
	    assertEquals(expected, actual);
    }    
    
    /**
    * usage:
    Word word = getWordObject(search_property, search_value, category, user_name);
    Scoring scorer = new Scoring();
    Hashtable user_options = getOptions(user_name);
    wtr = scorer.scoreSingleWord(awt, word, user_options);
    */
    public void testScoreSingleWord()
    {
	    String category = ("text-def");
	    AllWordsTest awt = new AllWordsTest();
	    awt.setText("text");
	    awt.setDefinition("definition");
	    awt.setCategory(category);
	    awt.setTestType("reading");
	    awt.setAnswer("definition_fail");
	    
	    String user_name = new String("guest");
	    String file_name = new String("text-def");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileStorage store = new FileStorage(current_dir);
	    Hashtable user_opts = store.getUserOptions(user_name, current_dir);
	    String search_property = new String("definition");
	    String search_value = new String("text");
	    Word word = store.getWordObject(search_property, search_value, category, user_name);
	    //dumpLog(store.getLog());
	    Scoring scorer = new Scoring();
	    WordTestResult wtr = scorer.scoreSingleWord(awt, word, user_opts);
	    //dumpLog(scorer.getLog());
	    String expected = new String("fail");
	    String actual = wtr.getGrade();
	    assertEquals(expected, actual);
    }
    
    /**
    *String excludeArea(String exclude_area_begin, String exclude_area_end, String text)
    */
        public void testApplyOptions()
	{
		Hashtable user_options = new Hashtable();
		user_options.put("grade_whitespace","false");
		user_options.put("exclude_chars",",");
		user_options.put("exclude_area","true");
		user_options.put("exclude_area_begin_char","[");
		user_options.put("exclude_area_end_char","]");
		user_options.put(" alternate_answer_separator","/");
		String text = ("name[pronunciation]");
		Scoring scorer = new Scoring();
		String actual = scorer.applyOptions(user_options, text);
		String expected = ("name");
		//System.out.println(actual);
		assertEquals(expected, actual);
	}
    /**/
    
        /**
         * Testing emoveEcludeChars(String text, String exclude_chars)
         */
        public void testRemoveEcludeChars()
        {
        	String exclude_chars = ",~!@#$%^*()_+-=?';:-";
        	String text = "hello"+exclude_chars;
        	Scoring scorer = new Scoring();
    		String actual = scorer.removeExcludeChars(text, exclude_chars);
    		String expected = "hello";
    		assertEquals(expected, actual);
        }
        
        /**
         * Testing emoveEcludeChars(String text, String exclude_chars)
         */
        public void testRemoveEcludeChars0()
        {
        	String exclude_chars = ",~!@#$%^*()_+-=?';:-";
        	String text = "-policy*";
        	Scoring scorer = new Scoring();
    		String actual = scorer.removeExcludeChars(text, exclude_chars);
    		dumpLog(scorer.getLog());
    		String expected = "policy";
    		assertEquals(expected, actual);
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

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ScoringTest.class);
    }

}

