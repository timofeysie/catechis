package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;

import java.io.File;
import java.util.Vector;

import org.catechis.Storage;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.Transformer;


public class FilterTest extends TestCase
{
	Vector words;
	FileStorage store;
	WordFilter word_filter;
	
    public FilterTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
	    word_filter = new WordFilter();
	    word_filter.setStartIndex(0);
	    word_filter.setMinMaxRange("2-2");
	    word_filter.setType("writing");
	    word_filter.setCategory("test words.xml");
	    word_filter.setExcludeTime("1000000");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    store = new FileStorage(current_dir);
	    words = store.getWordObjects("test words.xml", "test_user");
    }
    
    public void testFilterWord()
    {
	    int size = words.size();
	    int index = 0;
	    int expected_size = 1;
	    int actual_size = 0;
	    String min_max = word_filter.getMinMaxRange();
	    Filter filter = new Filter(word_filter);
	    while (index<size)
	    {
		    Word word = new Word();
		    word = (Word)words.get(index);
		    boolean pass = filter.filterWord(word, 0);
		    if (pass == true)
		    {
			    actual_size++;
		    }
		    pass = false;
		    index++;
	    }
	    assertEquals(expected_size, actual_size);
    }
    
    /**Setting the filter levels to 3 should return no results*/
    public void testFilterWordNoResult()
    {
	    int size = words.size();
	    int index = 0;
	    int expected_size = 0;
	    int actual_size = 0;
	    String min_max = new String("3-3");
	    word_filter.setMinMaxRange(min_max);
	    Filter filter = new Filter(word_filter);
	    while (index<size)
	    {
		    Word word = new Word();
		    word = (Word)words.get(index);
		    boolean pass = filter.filterWord(word, 0);
		    if (pass == true)
		    {
			    actual_size++;
		    }
		    index++;
	    }
	    assertEquals(expected_size, actual_size);
    }
    
    /** no comment today */
    public void testFilterExculdeTime()
    {
	    int expected_size = 1;
	    int actual_size = 0;
	    Filter filter = new Filter(word_filter);
	    int size = words.size();
	    int index = 0;
	    while (index<size)
	    {
		    Word word = new Word();
		    word = (Word)words.get(index);
		    boolean pass = filter.filterWord(word, 0);
		    if (pass == true)
		    {
			    actual_size++;
		    }
		    index++;
	    }
	    assertEquals(expected_size, actual_size);
    }
    
    /** no comment tomorrow either 
    *<p>OK, so  it looks like we have to set the levels.  the try catch block,
    * combined with the if (a&&b&&c) method breaks everything, and there isnt time
    * to figure out a better solution, as it really is time to leave this prototype
    * behind.  The Hibernate Criteria API does all this filtering for free.
    *<p>So this app will have to function as is from today, November 6th, 2005,
    * after over a year in development.  Therefore this quaint no comment has turned into
    * a farewell, unexpectedly on this day of other farewells...
    *<p>OK, so I spoke too soon.  I put in a few more cycles and got all the tests to
    * pass.  So it may not be the end, tho it should be.  It turns out separating the
    * JSPs, the Action objects, the Storage interface and the Catechis package 
    * from knowing what kind of storage is being used, filesystem or Hibern8 is
    * more trouble than its worth, and starting from scratch using Hibern8 along,
    * because of its mature features is a better idea.  Now if only I could get that
    * rpm for mysql to work...
    */
    public void testFilterNothingSetSoReturnAll()
    {
	    int size = words.size();
	    int expected_size = 4;
	    int actual_size = 0;
	    WordFilter blank_filter = new WordFilter();
	    Filter filter = new Filter(blank_filter);
	    int index = 0;
	    while (index<size)
	    {
		    Word word = new Word();
		    word = (Word)words.get(index);
		    int w_level = word.getWritingLevel();
		    boolean pass = filter.filterWord(word, 0);
		    if (pass == true)
		    {
			    actual_size++;
		    }
		    index++;
	    }
	    printObject(blank_filter);
	    assertEquals(expected_size, actual_size);
    }
    
    
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
