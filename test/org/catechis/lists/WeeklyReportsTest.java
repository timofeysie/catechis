package org.catechis.lists;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.FileStorage;
import org.catechis.Transformer;
import org.catechis.constants.Constants;
import org.catechis.dto.Momento;
import org.catechis.dto.SessionsReport;
import org.catechis.dto.WeeklyReport;
import org.catechis.dto.WordLastTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileTestRecords;
import org.junit.Test;

public class WeeklyReportsTest 
{
	@Test
	public void testUpdateTestDateRecords()
	{
		Long atst = Long.parseLong("13308");
		String a = Domartin.getElapsedTime(Long.toString(atst));
		//System.out.println("WeeklyReportsTest.testUpdateTestDateRecords "+a);
		String type = new String("reading"); 
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_id = new String("-5519451928541341468");
		FileTestRecords ftr = new FileTestRecords(current_dir);
		Momento old_m = ftr.getStatusRecord(user_id, Constants.VOCAB);
		Hashtable last_record = ftr.getReadingAndWritingLevels();
		SessionsReport sr = ftr.getSessionsReport();
		dumpLog(last_record);
		int expected = Integer.parseInt((String)last_record.get("number_of_words"));
		String subject = Constants.VOCAB;
		WeeklyReports wr = new WeeklyReports();
		boolean wrote = wr.saveWeeklyReport(user_id, subject, current_dir, last_record, sr);
		//System.out.println("WeeklyReportsTest.testUpdateTestDateRecords: wrote?: "+wrote);
		dumpLog(wr.getLog());
		Vector weekly_reports = wr.getListOfWeeklyReports(user_id, subject, current_dir);
		String report_name = (String)weekly_reports.get(0);
		WeeklyReport weekly_report = wr.getWeeklyReport(user_id, subject, current_dir, report_name);
		int actual = weekly_report.getNumberOfWords();
		assertEquals(expected, actual); 
	}
	
	@Test
	public void testGetSortedWeeklyRecords()
	{
		System.out.println("WeeklyReportsTest.testGetSortedWeeklyRecords ------------");
		String type = new String("reading"); 
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileStorage store = new FileStorage(current_dir);
		String user_id = new String("-5519451928541341468");
		String subject = Constants.VOCAB;
		String expected = "2012-17";
		WeeklyReports wrs = new WeeklyReports();
		String actual_week = "0"; // will the the last reports' week number
		
		Sarray sarry = new Sarray();
		try
		{
			sarry = wrs.getSortedListOfWeeklyReports(user_id, subject, current_dir);
		} catch (java.lang.NullPointerException npe)
		{
			System.out.println("npe=== ");
			npe.printStackTrace();
		}
		System.out.println("wrs log 000000000000000 sarry.size() "+sarry.size());
		dumpLog(wrs.getLog());
		String actual = null;
        for ( int i = 0 ; i < sarry.size() ; i++ )
        {
            Sortable s =(Sortable)sarry.elementAt( i );
            String object_key = (String)s.getKey();
            WeeklyReport object = (WeeklyReport)s.getObject();
            System.out.println ("Key = " + object_key + " , Value = " + object.getWeekOfYear());
            actual = object.getWeekOfYear();
        }
        assertEquals(expected, actual); 
	}
	
    private void dumpLog(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" -		"+val);
	    }
    }

	private void dumpLog(Vector log)
	{
	    int i = 0;
	    while (i<log.size())
	    {
	    	System.out.println(i+" "+log.get(i));
		    i++;
	    }
	}    
	
	private void dumpReports(Vector weekly_reports_list, WeeklyReports wrs, String user_id,
			String context_path, String subject)
	{
	    int i = 0;
	    while (i<weekly_reports_list.size())
	    {
	    	String report_name = (String)weekly_reports_list.get(i);
			WeeklyReport wr = wrs.getWeeklyReport(user_id, subject, context_path, report_name);
	    	System.out.println(i+" "+report_name);
		    i++;
	    }
	}   
	
	private void dumpReports(Hashtable weekly_reports, WeeklyReports wrs, String user_id,
			String context_path, String subject)
	{
		System.out.println("Weeks");
		for (Enumeration e = weekly_reports.keys() ; e.hasMoreElements() ;) 
		{
			String key = (String)e.nextElement();
			WeeklyReport wr = (WeeklyReport)weekly_reports.get(key);
	    	System.out.println(wr.getWeekOfYear());
	    }
	}   

}

