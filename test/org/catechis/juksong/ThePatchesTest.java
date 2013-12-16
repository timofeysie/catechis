package org.catechis.juksong;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.catechis.dto.Word;
import org.catechis.dto.WordTestResult;
import org.catechis.file.WordNextTestDates;
import org.catechis.gwangali.GwangaliLogi;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThePatchesTest extends GwangaliLogi
{
	/**
	 *TestTimeMemory revrseUpdateTestDateRecords(String user_id, String category, 
			String subject, TestTimeMemory ttm, WordTestResult wtr, String current_dir,
			Vector elt_vector)
	 */
	@Test
	public void testRevrseUpdateTestDateRecords()
	{

		//String user_id = new String("-5519451928541341468");
		String user_id = "2960548470376610112"; 
		String category = "???"; 
		TestTimeMemory ttm = new TestTimeMemory(); // get this from creating a test
		WordTestResult wtr = new WordTestResult();
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String type = "reading"; 
		String subject = new String("vocab"); 
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		FileStorage store = new FileStorage(current_dir);
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		WordNextTestDates wntds = new WordNextTestDates();
		//wntds.setSubject(subj);
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subject);
		TestTimeMemory old_ttm = new TestTimeMemory();
		ThePatches tp = new ThePatches();
		TestTimeMemory new_ttm = tp.revrseUpdateTestDateRecords(user_id, category, 
				subject, old_ttm, wtr, current_dir, elt_vector);
		System.out.println("word ---------------- "+word.getDefinition());
		printLog(Transformer.createTable(word));
		System.out.println("log -----------------");
		printLog(wntds.getLog());
		System.out.println("test type "+wntds.getNextTestType());
		String actual = word.getDefinition();
		String expected = "to have talent for arts";
		assertEquals(expected, actual);
		
	}

}
