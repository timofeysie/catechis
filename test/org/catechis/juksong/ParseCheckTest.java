package org.catechis.juksong;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.catechis.Transformer;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;

import junit.framework.TestCase;

public class ParseCheckTest extends TestCase
{
	/**
	* We want to test that the Transform object transforms 
	* a long date in this format: "EEE MMM dd HH:mm:ss zzz yyyy"
	* to a short date of this format: "MM/dd/yy"
	*<p>The expected date is the 16th I presume, due to the time zone difference. 
	*/
	public void testCheck()
	{
		//String file = new String("March II.xml");
		//String file = new String("random words 2 I.xml");
		String file = new String("Ganada I2 Ch07-08 b.xml");
        String user = new String("-5519451928541341468");
        ParseCheck ps = new ParseCheck();
		String actual = ps.check(user, file);
		File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        String path = new String(current_dir+File.separator+Constants.FILES
	    		+File.separator+user+File.separator+file);
		String expected = path+" is well-formed!";
		//printCollection(ps.getLog());
		//System.out.println("TransformerTest.testSimplifyDate: expected "+expected_date);
		//System.out.println("ParseCheckTest.testCheck: actual   "+  actual);
		assertEquals(expected, actual);
	}
	
	/**
	* We want to test that the Transform objevt transforms 
	* a long date in this format: "EEE MMM dd HH:mm:ss zzz yyyy"
	* to a short date of this format: "MM/dd/yy"
	*<p>The expected date is the 16th I presume, due to the time zone difference. 
	*/
	public void testWordCheck()
	{
		String file = new String("one_word.xml");
        ParseCheck ps = new ParseCheck();
		File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        System.out.println("testWordCheck "+current_dir);
        String file_path = new String(current_dir+File.separator+"test"
        		+File.separator+"org"+File.separator+"catechis"
        		+File.separator+"juksong"+File.separator
        		+file);
        String user = "-5519451928541341468";
        String word_id = "-3457751923227770362"; 
        //String file = "Ganada I2 Ch07-08 b.xml";
        String subject = "vocab";
        Word word = ps.unbindWord(word_id, user, file_path, subject);
        printLog(Transformer.createTable(word));
        System.out.println("testWordCheck path "+file_path);
        //C:\Tims Folder\programming\catechis\test\org\catechis\juksong
        //C:\Tims Folder\programming\catechis\test\org\catechis\juksong\one_word.xml
		//String expected = path+" is well-formed!";
		printCollection(ps.getLog());
		//System.out.println("TransformerTest.testSimplifyDate: expected "+expected_date
		boolean expected = false;
		boolean actual = true;
		assertEquals(expected, actual);
	}
	
	void printCollection(Collection<?> c) 
	{
	    for (Object e : c) {
	        System.out.println(e);
	    }
	}
	
	private void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println((String)log.get(i));
			i++;
		}
	}


}
