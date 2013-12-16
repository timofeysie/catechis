package org.catechis.juksong;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.catechis.admin.FileUserOptions;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;
import org.catechis.dto.WordLastTestDates;
import org.catechis.file.WordNextTestDates;
import org.junit.Test;

public class TestDateEvaluatorTest 
{
	/**
	 * 
	 * Words first test:
	 * <test><date>Fri Nov 11 01:34:22 KST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
	 */
	@Test
	public void testGetFirstTime()
	{
		//System.out.println("TestDateEvaluatorTest.testGetFirstTime: -------------------- +++");
		String user_id = new String("WLTD test 2");
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		String search_value = "dining together2";
		String category = "two words.xml";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Word word = store.getWordObject("definition", search_value, category, user_id);
		//FileUserOptions fuo = new FileUserOptions(current_dir);
		//cleanFolders(user_id, current_dir);
		//Vector categories = store.getWordCategories(subj, user_id);
		printLog(Transformer.createTable(word));
		TestDateEvaluator tde = new TestDateEvaluator();
		tde.evaluteSingleLastTestDate(word);
		long first_time = tde.getFirstTime();
		long reading_time = tde.getReadingTime();
		long time = tde.getTime();
		String tde_type = tde.getType();
		long writing_time = tde.getWritingTime();
		String expected = "Fri Nov 11 01:34:22 KST 2005";
		String actual = Transformer.getLongDateFromMilliseconds(""+first_time);
		//System.out.println("first_time "+first_time+Transformer.getLongDateFromMilliseconds(""+first_time));
		//System.out.println("reading_time "+reading_time);
		//System.out.println("time "+time);
		//System.out.println("tde_type "+tde_type);
		//System.out.println("writing_time "+writing_time);
		//System.out.println("TestDateEvaluatorTest.testGetFirstTime: -------------------- +++");
		assertEquals(expected, actual);
	}
	
	/**
	 * 
	 * Words last test
	 * <test><date>Thu Nov 09 05:05:50 KST 2006</date><file>level 1 reading.test</file><grade>fail</grade></test>
	 */
	@Test
	public void testEvaluteSingleLastTestDate()
	{
		//System.out.println("TestDateEvaluatorTest.testEvaluteSingleLastTestDate: -------------------- +++");
		String user_id = new String("WLTD test 2");
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		String search_value = "dining together2";
		String category = "two words.xml";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Word word = store.getWordObject("definition", search_value, category, user_id);
		//FileUserOptions fuo = new FileUserOptions(current_dir);
		//cleanFolders(user_id, current_dir);
		//Vector categories = store.getWordCategories(subj, user_id);
		printLog(Transformer.createTable(word));
		TestDateEvaluator tde = new TestDateEvaluator();
		tde.evaluteSingleLastTestDate(word);
		long reading_time = tde.getReadingTime();
		long time = tde.getTime();
		String tde_type = tde.getType();
		long writing_time = tde.getWritingTime();
		String expected = "Thu Nov 09 05:05:50 KST 2006";
		//System.out.println("reading date "+Transformer.getLongDateFromMilliseconds(""+reading_time));
		//System.out.println("reading_time "+reading_time);
		String actual = Transformer.getLongDateFromMilliseconds(""+reading_time);
		//System.out.println("actual "+actual);
		//System.out.println("TestDateEvaluatorTest.testEvaluteSingleLastTestDate: -------------------- +++");
		assertEquals(expected, actual);
	}
	
	/**
	 * 
	 * Word
	 * <word><text>화장1</text><definition>chairperson1</definition><writing-level>2</writing-level><reading-level>3</reading-level>
<id>111</id>
<test><date>Fri Nov 11 22:34:22 KST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
<test><date>Sat Nov 12 11:03:24 KST 2005</date><file>level 0 reading.test</file><grade>pass</grade></test>
</word>
	 */
	@Test
	public void testGetWritngTime()
	{
		System.out.println("TestDateEvaluatorTest.testGetWritngTime: -------------------- +++");
		String user_id = new String("WLTD test 2");
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		String search_value = "chairperson1";
		String category = "two words.xml";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		Word word = store.getWordObject("definition", search_value, category, user_id);
		//FileUserOptions fuo = new FileUserOptions(current_dir);
		//cleanFolders(user_id, current_dir);
		//Vector categories = store.getWordCategories(subj, user_id);
		printLog(Transformer.createTable(word));
		TestDateEvaluator tde = new TestDateEvaluator();
		tde.evaluteSingleLastTestDate(word);
		long time = tde.getTime();
		String tde_type = tde.getType();
		long writing_time = tde.getWritingTime();
		String expected = "Fri Nov 11 22:34:22 KST 2005";
		//System.out.println("writing date "+Transformer.getLongDateFromMilliseconds(""+writing_time));
		//System.out.println("writing_time "+writing_time);
		String actual = Transformer.getLongDateFromMilliseconds(""+writing_time);
		//System.out.println("actual "+actual);
		//System.out.println("TestDateEvaluatorTest.testGetWritngTime: -------------------- +++");
		assertEquals(expected, actual);
	}
	
	/**
	 * chairperson 		ltd = Wed Jul 12 20:37:09 KST 2006
	 * dining together 	ltd = Thu Nov 09 05:05:50 KST 2006
	 */
	@Test
	public void testGetNextTestWord()
	{
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//String user_id = new String("-5519451928541341468");
		//String user_id = new String("2960548470376610112");
		String user_id = new String("WLTD test");
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		
		cleanFolders(user_id, current_dir);
		Vector categories = store.getWordCategories(subj, user_id);
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.createNewLists(categories, store, user_id);
		
		
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subj);
		//System.out.println("word ---------------- "+word.getDefinition());
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		String actual = word.getDefinition();
		String expected = "chairperson";
		assertEquals(expected, actual);
	}
	
	/**
	 * All the previous tests are passing, meaning that the TestDateEvaluator is working as
	 * expected.  This test is then a functional test which is focusing on 
	 * WordLastTestDates.createNewLists(categories, store, user_id)
	 * and
	 * WordNextTestDates.getNextTestWord(user_id, current_dir, time, subj)
	 * 
	 * For reading tests:
	 * dining together2 last test date = Thu Nov 09 05:05:50 KST 2006
	 * chairperson1     last test date = Sat Nov 12 11:03:24 KST 2005
	 * 
	 * So the word with the oldest test is chairerspmn1, therefore that should be the word returned by getNextTestWord
	 * if new test lists are created, but they are different levels
	 * 
	 * Here are the two words in the WLTD test 2 user file:
	 * 
<word><text>화장1</text><definition>chairperson1</definition><writing-level>2</writing-level><reading-level>3</reading-level>
<id>111</id>
<test><date>Fri Nov 11 22:34:22 KST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
<test><date>Sat Nov 12 11:03:24 KST 2005</date><file>level 0 reading.test</file><grade>pass</grade></test>
</word>

<word><text>회식2</text><definition>dining together2</definition><writing-level>2</writing-level><reading-level>2</reading-level>
<id>222</id>
<test><date>Fri Nov 11 01:34:22 KST 2005</date><file>level 0 writing.test</file><grade>pass</grade></test>
<test><date>Sat Nov 12 11:03:24 KST 2005</date><file>level 0 reading.test</file><grade>pass</grade></test>
<test><date>Sun Apr 23 12:48:18 KST 2006</date><file>level 1 reading.test</file><grade>fail</grade></test>
<test><date>Tue May 16 18:52:16 KST 2006</date><file>level 0 reading.test</file><grade>fail</grade></test>
<test><date>Fri Jul 07 16:24:45 KST 2006</date><file>level 1 reading.test</file><grade>pass</grade></test>
<test><date>Wed Jul 12 20:37:09 KST 2006</date><file>level 2 reading.test</file><grade>fail</grade></test>
<test><date>Thu Nov 09 05:05:50 KST 2006</date><file>level 1 reading.test</file><grade>fail</grade></test>
</word>
	*
	* The log output below is troublesome:
waiting tests ---------------- 
npe word 1134232462000
npe word 1134308062000
waiting tests ---------------- 
waiting tests 2 ---------------- 
1134232462000	Sun Dec 11 01:34:22 KST 2005 dining together2 222
1134308062000	Sun Dec 11 22:34:22 KST 2005 dining together2 222
waiting tests 2 ---------------- 
	*
	* Is wltd really creating files with identical words?
	 */
	@Test
	public void testCreateNewLists()
	{
		System.out.println("TestDateEvaluatorTest.testCreateNewLists: -------------------- ");
		String type = new String("reading"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//System.out.println("TestDateEvaluatorTest.testCreateNewLists #########");
		String user_id = "WLTD test 2";
		FileUserOptions fuo = new FileUserOptions(current_dir);
		cleanFolders(user_id, current_dir);
		Vector categories = store.getWordCategories(subj, user_id);
		
		WordLastTestDates wltd = new WordLastTestDates();
		
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.createNewLists(categories, store, user_id);
		System.out.println("wltd log ---------------- ");
		printLog(wltd.getLog());
		System.out.println("wltd log ---------------- ");
		
		Vector waiting_tests = wltd.getWaitingTests(user_id, current_dir);
		System.out.println("waiting tests ---------------- ");
		int total = waiting_tests.size();
		int i = 0;
		while (i<total)
		{
			String milliseconds_key = (String)waiting_tests.get(i);
			Word word = wltd.getWord(milliseconds_key);
			try
			{
				System.out.println(milliseconds_key+"	"+Transformer.getDateFromMilliseconds(milliseconds_key)+" "+word.getDefinition());
				//wltd.getWNTFiles(user_id, current_dir);
			} catch (java.lang.NullPointerException npe)
			{
				System.out.println("npe word "+milliseconds_key);
			}
			i++;
		}
		System.out.println("waiting tests ---------------- ");
		
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subj);
		waiting_tests = wltd.getWaitingTests(user_id, current_dir);
		System.out.println("waiting tests 2 ---------------- ");
		total = waiting_tests.size();
		i = 0;
		while (i<total)
		{
			String milliseconds_key = (String)waiting_tests.get(i);
			Word word2 = wntds.getSpecificNextTestWord(user_id, current_dir, milliseconds_key, subj, type);
			try
			{
				System.out.println(milliseconds_key+"	"+Transformer.getLongDateFromMilliseconds(milliseconds_key)+" "+word.getDefinition()+" "+word.getId());
				//wltd.getWNTFiles(user_id, current_dir);
			} catch (java.lang.NullPointerException npe)
			{
				System.out.println("npe word "+milliseconds_key);
			}
			i++;
		}
		System.out.println("waiting tests 2 ---------------- ");
		
		System.out.println("word ---------------- "+word.getDefinition());
		printLog(Transformer.createTable(word));
		System.out.println("log -----------------");
		printLog(wntds.getLog());
		System.out.println("test type "+wntds.getNextTestType());
		System.out.println("TestDateEvaluatorTest.testCreateNewLists: -------------------- ");
		String actual = word.getDefinition();
		String expected = "chairperson1";
		assertEquals(expected, actual);
	}
	
	/**
	 * For writing tests:
	 * dining together2 last test date = Fri Nov 11 01:34:22 KST 2005
	 * chairperson1     last test date = Sat Nov 12 11:03:24 KST 2005
	 * 
	 * dining together2 is older by 10 hours, since they have the same level.
	 */
	@Test
	public void testWritingLists()
	{
		String type = new String("writing"); 
		String subj = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		//System.out.println("TestDateEvaluatorTest.testCreateNewLists #########");
		String user_id = "WLTD test 2";
		
		/*
		cleanFolders(user_id, current_dir);
		Vector categories = store.getWordCategories(subj, user_id);
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		wltd.createNewLists(categories, store, user_id);
		*/
		
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subj);
		
		//System.out.println("word ---------------- "+word.getDefinition());
		//printLog(Transformer.createTable(word));
		//System.out.println("log -----------------");
		//printLog(wntds.getLog());
		//System.out.println("test type "+wntds.getNextTestType());
		
		String actual = word.getDefinition();
		String expected = "chairperson1";
		assertEquals(expected, actual);
	}
	
	private void cleanFolders(String user_id, String current_dir)
	{
		// delete reading wltd
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setType(Constants.READING);
		String ptf = wltd.getPathToFile(user_id, current_dir, "wltd");
		File file = new File(ptf);
		boolean exist = file.exists();
		boolean write =	file.canWrite();
		System.out.println("exists? "+exist+" write? "+write);
		boolean deleted = deleteDir(file);
		file.mkdir();
		System.out.println(ptf+" deleted? "+deleted);
		// delete reading wntd
		ptf = wltd.getPathToFile(user_id, current_dir, "wntd");
		file = new File(ptf);
		deleted = deleteDir(file);
		file.mkdir();
		System.out.println(ptf+" deleted? "+deleted);
		// delete writing wltd
		wltd.setType(Constants.WRITING);
		ptf = wltd.getPathToFile(user_id, current_dir, "wltd");
		file = new File(ptf);
		deleted = deleteDir(file);
		file.mkdir();
		System.out.println(ptf+" deleted? "+deleted);
		// delete writing wntd
		ptf = wltd.getPathToFile(user_id, current_dir, "wntd");
		file = new File(ptf);
		deleted = deleteDir(file);
		file.mkdir();
		System.out.println(ptf+" deleted? "+deleted);
	}
	
	// Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) 
    {
        if (dir.isDirectory()) 
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) 
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    } 
	
	private void printLog(Vector log)
	   {
			int total = log.size();
			int i = 0;
			while (i<total)
			{
				System.out.println(i+"	"+log.get(i));
				i++;
			}
	   }

}
