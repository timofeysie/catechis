package org.catechis.file;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.Stats;
import org.catechis.StatsTest;
import org.catechis.gwangali.ROTConstants;
import org.catechis.dto.Test;

import junit.framework.TestCase;

public class ROTFileStorageTest extends TestCase 
{

	/**
	 * /**
	 * This test will pass assuming the value for the rot level 0 reading vector is not changed in another test.
	 * These values are millisecond equivalents.
	 * seconds- 10000
	 * minutes- 60000
	 * hours  - 3600000
	 * days   - 86400000
	 */
	public void testGetROTReading()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("reading"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		//System.out.println("ROTFileStorageTest.testGetROTReading: original rot -----------");
		//dumpLog(rot_vector);
		String expected_string = "3840000";
		//String expected_string = "3600000";
		String actual_string   = (String)rot_vector.get(0);
		long expected_long = Long.parseLong(expected_string);
		long actual_long = Long.parseLong(actual_string);
		boolean actual = expected_long>3600000;
		boolean expected = true;
		assertEquals(expected_string, actual_string);
	}
	
	/**
	 * This test will pass assuming the value for the rot level 2 writing vector is not changed in another test.
	 */
	public void testGetROTWriting()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("writing"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		//System.out.println("ROTFileStorageTest.testGetROTWriting: original rot -----------");
		//dumpLog(rot_vector);
		String expected_string = "100800000";
		String actual_string   = (String)rot_vector.get(2);
		long expected_long = Long.parseLong(expected_string);
		long actual_long = Long.parseLong(actual_string);
		boolean actual = expected_long>3600000;
		boolean expected = true;
		assertEquals(expected_string, actual_string);
	}
	
	public void testGetROTDReading()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("reading"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		//dumpLog(rotd_vector);
		String expected = "120000";
		String actual   = (String)rotd_vector.get(0);
		assertEquals(expected, actual);
	}
	
	public void testGetROTDWriting()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("writing"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		//dumpLog(rotd_vector);
		String expected = "120000";
		String actual   = (String)rotd_vector.get(0);
		assertEquals(expected, actual);
	}
	
	/**
	 * Let's test this method:
	 * editROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir)
	 * by incrementing the level 0 writing rot value.
	 */
	public void testEditROT()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("writing"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String option_name = ROTConstants.ROT;
		String rot_level = "0";
		String grade = ROTConstants.PASS;
		String subject = "vocab";
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector original_rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String level_zero_rotd = (String)rotd_vector.get(0);
		rotfs.editROT(type, option_name, rot_level, grade, user_id, subject, current_dir);
		//System.out.println("ROTFileStorageTest.testEditROT: original rot -----------");
		//dumpLog(original_rot_vector);
		Vector new_rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		//System.out.println("ROTFileStorageTest.testEditROT: new rot ----------------");
		//dumpLog(new_rot_vector);
		String original_level_zero_rot = (String)original_rot_vector.get(0);
		int expected_int = Integer.parseInt(original_level_zero_rot)+Integer.parseInt(level_zero_rotd);
		String expected = Integer.toString(expected_int);
		String actual = (String)new_rot_vector.get(0);
		assertEquals(expected, actual);
	}
	
	/**
	 * Now let's undo the change we made in the past methods.
	 * reverseEditROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir)
	*/
	public void testReverseEditROT()
	{
		String user_id = new String("-5519451928541341468");
		String type = new String("writing"); 
		String subj = new String("vocab");
		File path_file = new File("");
		String option_name = ROTConstants.ROT;
		String rot_level = "0";
		String grade = ROTConstants.FAIL;
		String subject = "vocab";
		String current_dir = path_file.getAbsolutePath();
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector original_rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String level_zero_rotd = (String)rotd_vector.get(0);
		rotfs.reverseEditROT(type, option_name, rot_level, grade, user_id, subject, current_dir);
		System.out.println("ROTFileStorageTest.testEditROT: original rot -----------");
		dumpLog(original_rot_vector);
		Vector new_rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		System.out.println("ROTFileStorageTest.testEditROT: new rot ----------------");
		dumpLog(new_rot_vector);
		String original_level_zero_rot = (String)original_rot_vector.get(0);
		int expected_int = Integer.parseInt(original_level_zero_rot)-Integer.parseInt(level_zero_rotd)-Integer.parseInt(level_zero_rotd);
		String expected = Integer.toString(expected_int);
		String actual = (String)new_rot_vector.get(0);
		assertEquals(expected, actual);
	}
	
	private void dumpLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
	}
	
	public static void main(String args[]) 
	{
        	junit.textui.TestRunner.run(ROTFileStorageTest.class);
    }

}
