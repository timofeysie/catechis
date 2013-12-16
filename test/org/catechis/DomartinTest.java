package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.ArrayList;
import org.catechis.dto.Test;
//import org.catechis.dto.Word;
import org.catechis.Transformer;
import junit.framework.TestCase;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Date;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.io.File;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.beanutils.BeanComparator;
public class DomartinTest extends TestCase
{
	
    public DomartinTest (String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {

    }
    
    public void testGetFileExtension()
    {
	    String test_name = new String("test.file");
	    String expected = new String(".file");
	    String actual = Domartin.getFileExtension(test_name);
	    assertEquals(expected, actual);
    }  
    
    public void testGetFileWithoutExtension()
    {
	    String test_name = new String("test.file");
	    String expected = new String("test");
	    String actual = Domartin.getFileWithoutExtension(test_name);
	    assertEquals(expected, actual);
    }
    
    public void testGetTestLevel()
    {
	    // test for level 1 reading.test
	    String test_name = new String("level 1 reading.test");
	    String expected = new String("1");
	    String actual = Domartin.getTestLevel(test_name);
	    assertEquals(expected, actual);
    }
    
    public void testGetTestLevelNot()
    {
	    // test for null result of basic_ten_word.test
	    String test_name = new String("basic_ten_word.test");
	    String expected = null;
	    String actual = Domartin.getTestLevel(test_name);
	    assertEquals(expected, actual);
    }
    
    public void testGetMilliseconds()
    {
	    String date = new String("Thu Aug 14 08:09:00 PST 2005");
	    long actual_milliseconds = getMilliseconds(date);
	    long expected_milliseconds = Long.parseLong("1124035740000");
	    assertEquals(expected_milliseconds, actual_milliseconds);
    }
    
    	private long getMilliseconds(String str_date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		date = sdf.parse(str_date, pp);
		long time = date.getTime();
		return time;
	}
    
    public void testHashMapWithMillisecondIndex()
    {
	    Comparator comparator = new BeanComparator("date");
	    // Collections.sort(list, comparator);
	    TreeSet list = new TreeSet(comparator);
	    
	    Test test0 = new Test();
	    test0.setDate("Thu Aug 18 08:09:00 PST 2005");
	    test0.setMilliseconds(Domartin.getMilliseconds("Thu Aug 18 08:09:00 PST 2005"));
	    test0.setName("4");
	    list.add(test0);
	    
	    Test test1 = new Test();
	    test1.setDate("Wed Aug 17 08:09:00 PST 2005");
	    test1.setMilliseconds(Domartin.getMilliseconds("Wed Aug 17 08:09:00 PST 2005"));
	    test1.setName("3");
	    list.add(test1);
	    
	    Test test2 = new Test();
	    test2.setDate("Tue Aug 16 08:09:00 PST 2005");
	    test2.setMilliseconds(Domartin.getMilliseconds("Tue Aug 16 08:09:00 PST 2005"));
	    test2.setName("2");
	    list.add(test2);
	    
	    Test test3 = new Test();
	    test3.setDate("Mon Aug 15 08:09:00 PST 2005");
	    test3.setMilliseconds(Domartin.getMilliseconds("Mon Aug 15 08:09:00 PST 2005"));
	    test3.setName("1");
	    list.add(test3);
	    
	    Test test4 = new Test();
	    test4.setDate("Sun Aug 14 08:09:00 PST 2005");
	    test4.setMilliseconds(Domartin.getMilliseconds("Sun Aug 14 08:09:00 PST 2005"));
	    test4.setName("0");
	    list.add(test4);

	    //System.out.println("Hashmap with milliseconds keys -----------");
	    HashMap hash = new HashMap();
	    ArrayList new_list = new ArrayList();
	    Iterator it = list.iterator();
	    while (it.hasNext())
	    {
		    Test test = (Test)it.next();
		    long mill = test.getMilliseconds();
		    String mill_str = new String(Long.toString(mill));
		    hash.put(Long.valueOf(mill_str), test);
		    new_list.add(Long.valueOf(mill_str));
	    }
	    Collections.sort(new_list);
	    StringBuffer name_buffy = new StringBuffer();
	    for (int i=0;i<new_list.size();i++)
	    {
		    Long new_list_long = (Long)new_list.get(i);
		    Test this_test = (Test)hash.get(new_list_long);
		    //System.out.println(i+" "+new_list_long+" "+this_test.getDate());
		    name_buffy.append(this_test.getName());
	    }
	    String actual_names = new String(name_buffy);
	    String expected_names = new String("01234");
	    assertEquals(expected_names, actual_names);
    }
    
    public void testGetElapsedTime()
    {
	    String milliseconds = new String ("86404600");
	    String actual_elapsed_time = Domartin.getElapsedTime(milliseconds);
	    String expected_elapsed_time = new String("1 day 0 hours 0 minutes 4 seconds");
	    //System.out.println("elapsed time "+actual_elapsed_time);
	    assertEquals(expected_elapsed_time, actual_elapsed_time);
    }

    /*
    public void testGetNewID()
    {
	   long id = Domartin.getNewID();
	   System.out.println("new id "+id);
    }*/
    
    public void testCheckOldUser()
    {
	    String user_name = "guest2";
	    String user_id = "2960548470376610112";
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    String search_id = Domartin.checkOldUser( user_name, user_id, current_dir);
	    //System.out.println("id "+search_id);
	    String actual_id = search_id;
	    String expected_id = user_id;
	    assertEquals(expected_id, actual_id);
    }
    
    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(DomartinTest.class);
    }
    
    private void printLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
    }

}
