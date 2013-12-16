package org.catechis.gwangali;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Vector;

import org.catechis.constants.Constants;
import org.catechis.file.ROTFileStorage;
import org.junit.Test;

public class RateOfTestingTest extends GwangaliLogi
{

	/**
	 * 3600000 - 120000 = 3480000
	 */
	@Test
	public final void testUpdateRateOfTestingIncrementReading() 
	{
		String user_id = new String("-5519451928541341468");
		String subj = Constants.VOCAB;
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String type = Constants.READING;
		// get rot vector before modification
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String current_level_0_rot = (String)rot_vector.get(0);
		String current_level_0_rotd = (String)rotd_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+current_level_0_rot);
		// 3480000
		String level = "0";
		String test_result = ROTConstants.PASS;
		RateOfTesting rot = new RateOfTesting();
		rot.updateRateOfTesting(type, level, test_result, user_id, subj, current_dir);
		//updateRateOfTesting(String rot_level, String grade, String user_id, String subject, String current_dir)
		rotfs = new ROTFileStorage();
		rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		String actual = (String)rot_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+actual);
		long current_rot = Long.parseLong(current_level_0_rot);
		long current_rotd = Long.parseLong(current_level_0_rotd);
		long expected_long = current_rot + current_rotd;
		//System.out.println("expected_long = current_rot + current_rotd "+expected_long+" = "+current_rot+" + "+current_rotd);
		//System.out.println("expected = "+expected_long);
		//System.out.println("  actual = "+actual);
		String expected  = ""+expected_long;
		//System.out.println("rotfs log -------- ");
		//dumpLog(rotfs.getLog());
		//System.out.println("rot log ---------- ");
		//dumpLog(rot.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	 * 3600000 - 120000 = 3480000
	 */
	@Test
	public final void testUpdateRateOfDecrementReading() 
	{
		String user_id = new String("-5519451928541341468");
		String subj = Constants.VOCAB;
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String type = Constants.READING;
		// get rot vector before modification
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String current_level_0_rot = (String)rot_vector.get(0);
		String current_level_0_rotd = (String)rotd_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+current_level_0_rot);
		// 3480000
		String level = "0";
		String test_result = ROTConstants.FAIL;
		RateOfTesting rot = new RateOfTesting();
		rot.updateRateOfTesting(type, level, test_result, user_id, subj, current_dir);
		//updateRateOfTesting(String rot_level, String grade, String user_id, String subject, String current_dir)
		rotfs = new ROTFileStorage();
		rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		String actual = (String)rot_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+actual);
		long current_rot = Long.parseLong(current_level_0_rot);
		long current_rotd = Long.parseLong(current_level_0_rotd);
		long expected_long = current_rot - current_rotd;
		//System.out.println("expected_long = current_rot + current_rotd "+expected_long+" = "+current_rot+" + "+current_rotd);
		//System.out.println("expected = "+expected_long);
		//System.out.println("  actual = "+actual);
		String expected  = ""+expected_long;
		//System.out.println("rotfs log -------- ");
		//dumpLog(rotfs.getLog());
		//System.out.println("rot log ---------- ");
		//dumpLog(rot.getLog());
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testUpdateRateOfDecrementWriting() 
	{
		String user_id = new String("-5519451928541341468");
		String subj = Constants.VOCAB;
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String type = Constants.WRITING;
		// get rot vector before modification
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String current_level_0_rot = (String)rot_vector.get(0);
		String current_level_0_rotd = (String)rotd_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+current_level_0_rot);
		// 3480000
		String level = "0";
		String test_result = ROTConstants.FAIL;
		RateOfTesting rot = new RateOfTesting();
		rot.updateRateOfTesting(type, level, test_result, user_id, subj, current_dir);
		//updateRateOfTesting(String rot_level, String grade, String user_id, String subject, String current_dir)
		rotfs = new ROTFileStorage();
		rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		String actual = (String)rot_vector.get(0);
		System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+actual);
		long current_rot = Long.parseLong(current_level_0_rot);
		long current_rotd = Long.parseLong(current_level_0_rotd);
		long expected_long = current_rot - current_rotd;
		//System.out.println("expected_long = current_rot + current_rotd "+expected_long+" = "+current_rot+" + "+current_rotd);
		//System.out.println("expected = "+expected_long);
		//System.out.println("  actual = "+actual);
		String expected  = ""+expected_long;
		//System.out.println("rotfs log -------- ");
		//dumpLog(rotfs.getLog());
		System.out.println("rot log ---------- ");
		dumpLog(rot.getLog());
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testUpdateRateOfIncrementWriting() 
	{
		String user_id = new String("-5519451928541341468");
		String subj = Constants.VOCAB;
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String type = Constants.WRITING;
		// get rot vector before modification
		ROTFileStorage rotfs = new ROTFileStorage();
		Vector rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		Vector rotd_vector = rotfs.getROTDVector();
		String current_level_0_rot = (String)rot_vector.get(0);
		String current_level_0_rotd = (String)rotd_vector.get(0);
		//System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+current_level_0_rot);
		// 3480000
		String level = "0";
		String test_result = ROTConstants.PASS;
		RateOfTesting rot = new RateOfTesting();
		rot.updateRateOfTesting(type, level, test_result, user_id, subj, current_dir);
		//updateRateOfTesting(String rot_level, String grade, String user_id, String subject, String current_dir)
		rotfs = new ROTFileStorage();
		rot_vector = rotfs.getROTVector(type, user_id, subj, current_dir);
		String actual = (String)rot_vector.get(0);
		System.out.println("RateOfTestingTest.testUpdateRateOfTesting: current_level_0_rot "+actual);
		long current_rot = Long.parseLong(current_level_0_rot);
		long current_rotd = Long.parseLong(current_level_0_rotd);
		long expected_long = current_rot + current_rotd;
		//System.out.println("expected_long = current_rot + current_rotd "+expected_long+" = "+current_rot+" + "+current_rotd);
		//System.out.println("expected = "+expected_long);
		//System.out.println("  actual = "+actual);
		String expected  = ""+expected_long;
		//System.out.println("rotfs log -------- ");
		//dumpLog(rotfs.getLog());
		System.out.println("rot log ---------- ");
		dumpLog(rot.getLog());
		assertEquals(expected, actual);
	}


}
