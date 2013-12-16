package org.catechis.juksong;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.catechis.FileStorage;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;
import org.catechis.file.FileCategories;
import org.junit.Test;
//import org.junit.TestCase;


public class FarmingToolsTest 
{

	@Test
	public void testRatePerformance() 
	{
	    String user_name = ("-5519451928541341468");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Hashtable categories = cats.getSortedWordCategories(user_name);
	    //System.out.println("cats "+categories.size());
	    Enumeration keys = categories.keys();
	    //FileJDOMWordLists fjdomwl = new FileJDOMWordLists(current_dir);
	    String encoding = new String("UTF-8");
	    while(keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String cat = (String)categories.get(key);
		    //System.out.println("key: "+key+" cat "+cat);
		    FileStorage store = new FileStorage(current_dir);
		    Vector words = store.getWordObjects(cat, user_name);
		    //System.out.println("words: "+words.size());
		    try
		    {
		    	for (int i=0; i<words.size();i++)
		    	{
		    		Word word = (Word)words.get(i);
		    		String def = word.getDefinition();
		    		String text = word.getText();
		    		FarmingTools ft = new FarmingTools();
		    		int reading_score = ft.ratePerformance(word, Constants.READING);
		    		int writing_score = ft.ratePerformance(word, Constants.WRITING);
		    		org.catechis.dto.Test tests[] = word.getTests();
		    		System.out.println(text+ "	"+def+"	reading	"+reading_score+" writing "+writing_score+" tests: "+tests.length);
		    	}
		    	//printLog(ft.getLog());
		    } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		    {
		    	System.out.println("aioobe: ");
		    }
		    break;
	    }
		assertEquals(true,false);
	}
	
	@Test
	public void testRatePerformancebyWhiteColor() 
	{
	    String user_name = ("-5519451928541341468");
	    File path_file = new File("");
	    String current_dir = path_file.getAbsolutePath();
	    FileCategories cats = new FileCategories(current_dir);
	    Hashtable categories = cats.getSortedWordCategories(user_name);
	    //System.out.println("cats "+categories.size());
	    Enumeration keys = categories.keys();
	    //FileJDOMWordLists fjdomwl = new FileJDOMWordLists(current_dir);
	    String encoding = new String("UTF-8");
	    while(keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String cat = (String)categories.get(key);
		    //System.out.println("key: "+key+" cat "+cat);
		    FileStorage store = new FileStorage(current_dir);
		    Vector words = store.getWordObjects(cat, user_name);
		    //System.out.println("words: "+words.size());
		    try
		    {
		    	for (int i=0; i<words.size();i++)
		    	{
		    		Word word = (Word)words.get(i);
		    		String def = word.getDefinition();
		    		String text = word.getText();
		    		FarmingTools ft = new FarmingTools();
		    		Color reading_score = ft.ratePerformancebyLightColor(word, Constants.READING);
		    		Color writing_score = ft.ratePerformancebyLightColor(word, Constants.WRITING);
		    		org.catechis.dto.Test tests[] = word.getTests();
		    		//System.out.println(text+ "	"+def+"	reading	"+reading_score.getRed()+" tests: "+tests.length);
		    		//System.out.println(text+ "	"+def+" writing "+writing_score.getRed()+" tests: "+tests.length);
		    		int red = reading_score.getRed();
		    		int gre = reading_score.getGreen();
		    		int blu = reading_score.getBlue();
		    		String color_string = "reading_score: rgb("+red+","+gre+","+blu+")";
		    		int red1 = writing_score.getRed();
		    		int gre1 = writing_score.getGreen();
		    		int blu1 = writing_score.getBlue();
		    		String color_string1 = "reading_score: rgb("+red1+","+gre1+","+blu1+")";
		    		System.out.println(color_string+" "+color_string1);
		    		//printLog(ft.getLog());
		    	}
		    } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		    {
		    	System.out.println("aioobe: ");
		    }
		    break;
	    }
		assertEquals(true,false);
	}
	
	public static void main(String args[]) 
    {
        //junit.textui.TestRunner.run(FarmingTools.class);
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
}
