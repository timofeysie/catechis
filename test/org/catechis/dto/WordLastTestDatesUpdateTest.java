package org.catechis.dto;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.junit.Test;

public class WordLastTestDatesUpdateTest 
{
	@Test
	public void testUpdateTestDateRecords()
	{
		String type = new String("reading"); 
		
		Vector elt_vector = new Vector(); // exclude level time in days
		elt_vector.add("1");
		elt_vector.add("14");
		elt_vector.add("30");
		elt_vector.add("90");
		
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_id = new String("2960548470376610112");
		//String user_name = new String("-5519451928541341468");
		//Vector all_word_categories = store.getWordCategories("exclusive", user_name);
		//System.out.println("categories - "+all_word_categories.size());
		WordLastTestDates wltd = new WordLastTestDates();
		wltd.setExcludeLevelTimes(elt_vector);
		wltd.setType(type);
		wltd.setLimitOfWords(100);
		// create a test test to test (yeah I had to do that...)
		String [] files = wltd.getWNTFiles(user_id, current_dir);
		String file = files[1];
		String file_w_ext = file;
		String file_wo_ext = Domartin.getFileWithoutExtension(file_w_ext);
		String file_date = Transformer.getLongDateFromMilliseconds(file_wo_ext);
		//System.out.println("--- file "+file);
		//System.out.println("--- date "+file_date);
		//printFiles(files);
		// create a new test
		Date today = new Date();
		String now = today.toString();
		//long word_id = Long.parseLong("-7920458335499530699"); // this word is retired
		long word_id = Long.parseLong("-7983252723503331340");
		//System.out.println("new test date - "+now);
		//System.out.println("     toady    - "+today);
		//System.out.println("--- word_id "+word_id);
		//System.out.println("--- file_id "+file_wo_ext);
		Date fwoe = new Date(Long.parseLong(file_wo_ext));
		//System.out.println("--- file_date "+fwoe.toString());
		WordTestResult wtr = new WordTestResult();
		wtr.setWntdName(file);
		wtr.setDate(now);
		wtr.setWordId(word_id);
		wtr.setLevel("0");
		wltd.updateTestDateRecords(user_id, current_dir, wtr);
		files = wltd.getWNTFiles(user_id, current_dir);
		///printFiles(files);
		//System.out.println("-------------- log ");
		//printLog(wltd.getLog());
		String user_folder_path = (current_dir+File.separator+user_id);
		String vocab_folder_path = (user_folder_path+File.separator+"vocab");
		String lists_folder_path = (vocab_folder_path+File.separator+"lists");
		String wntd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
		// Now we gotta add a day to the current date, cause the word at level 0 will create a new test date 1 day later.
		long this_instant = today.getTime();
		long day = 86400000;
		//int differential = 362;		// please tell my why we need this?
		//long expected_wnt_time = this_instant + day + differential;
		long expected_wnt_time = this_instant + day;
		//if (expected_wnt_time>expected_wnt_time
		String expected_file_name = Long.toString(expected_wnt_time);
		String wntd_file_path = (wntd_folder_path+File.separator+expected_file_name+".xml");
		//System.out.println("old test date - "+this_instant);
		//System.out.println("new test date - "+expected_wnt_time);
		//System.out.println("path - "+wntd_file_path);
		File expected_file = new File(wntd_file_path);
		boolean actual = expected_file.exists();
		boolean expected = true;
		assertEquals(expected, actual);
	}

}
