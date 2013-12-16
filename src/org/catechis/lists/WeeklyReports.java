package org.catechis.lists;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.EncodeString;
import org.catechis.constants.Constants;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.RatesOfForgetting;
import org.catechis.dto.SessionsReport;
import org.catechis.dto.TestStats;
import org.catechis.dto.WeeklyReport;
import org.catechis.dto.Word;
import org.catechis.dto.WordStats;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Work with year-month-week.xml weekly session history reports in the
 * root_path/user_id/subject/history folder.
<weekly_report>
	<week_of_year></week_of_year>
	<number_of_sessions></number_of_sessions> * This is the number of all stat histories saved during the week.  
	<average_time_spent_testing></average_time_spent_testing> * word_test_elapsed_time/number_of_session_tests
	<number_of_tests_in_week></number_of_tests_in_week>
	<number_of_words>2156</number_of_words>
	<writing_average>2.6154916512059367</writing_average>
	<reading_average>2.771799628942486</reading_average>
	<words_at_reading_level_0>21</words_at_reading_level_0>
	<words_at_reading_level_1>137</words_at_reading_level_1>
	<words_at_reading_level_2>155</words_at_reading_level_2>
	<words_at_reading_level_3>1843</words_at_reading_level_3>
	<words_at_writing_level_0>44</words_at_writing_level_0>
	<words_at_writing_level_1>227</words_at_writing_level_1>
	<words_at_writing_level_2>243</words_at_writing_level_2>
	<words_at_writing_level_3>1642</words_at_writing_level_3>
<w/eekly_report>
 * @author timmy
 *
 */
public class WeeklyReports 
{
	
	private Vector log;
	
	public WeeklyReports()
	{
		log = new Vector();
		log.add("WeeklyReports.<init>");
	}

	
	/**
	 * Take testing info in the last_record hash and store it in a weekly_record xml file.
	 * @param user_id
	 * @param subject
	 * @param root_path
	 * @param last_record
	 */
	public boolean saveWeeklyReport(String user_id, String subject, String root_path,
			Hashtable last_record, SessionsReport sr)
	{
		Calendar right_now = Calendar.getInstance();
		int year = right_now.get(Calendar.YEAR);
		int week = right_now.get(Calendar.WEEK_OF_YEAR);
		String file_name = (year+"-"+week+".xml");
		String path_to_report = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator+Constants.VOCAB+
			File.separator+Constants.HISTORY+File.separator+file_name);
		log.add(path_to_report);
		File file = new File(path_to_report);
		if (file.exists())
		{
			return false;
		}
		try
		{
			file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			// whoa Nelly!
		}
		Document doc = new Document();
		Element root = createRoot(last_record, sr);
		doc.addContent(root);
		writeDocument(path_to_report, doc);
		// load file
		return true;
	}
	
	/**
	 * Return a Vector of file names minus the xml extension from
	 * year-month-week.xml weekly session history reports in the
	 * root_path/user_id/subject/history folder.
	 * @param user_id
	 * @param subject
	 * @param root_path
	 * @return
	 */
	public Vector getListOfWeeklyReports(String user_id, String subject, String root_path)
	{
		Vector reports = new Vector();
		String path_to_reports = new String(root_path+File.separator
				+"files"+File.separator+user_id+File.separator+Constants.VOCAB+
				File.separator+Constants.HISTORY);
		File file = new File(path_to_reports);
		String[] files_array = file.list();
		for (int i = 0; i < files_array.length; i++) 
		{
			String report_file = files_array[i];
			String report = Domartin.getFileWithoutExtension(report_file);
			reports.add(report);
		}
		return reports;
	}
	
	/**
	 * Return a Vector of file names minus the xml extension from
	 * year-month-week.xml weekly session history reports in the
	 * root_path/user_id/subject/history folder.
	 * @param user_id
	 * @param subject
	 * @param root_path
	 * @return
	 */
	public Sarray getSortedListOfWeeklyReports(String user_id, String subject, String root_path)
	{
		String path_to_reports = new String(root_path+File.separator
				+"files"+File.separator+user_id+File.separator+Constants.VOCAB+
				File.separator+Constants.HISTORY);
		File file = new File(path_to_reports);
		//log.add("WeeklyReports.getSortedListOfWeeklyReports: path "+path_to_reports);
		//log.add("WeeklyReports.getSortedListOfWeeklyReports exists? "+file.exists());
		Sarray  sarry = new Sarray();
		String[] files_array = file.list();
		log.add("WeeklyReports.getSortedListOfWeeklyReports files_array.length "+files_array.length);
		for (int i = 0; i < files_array.length; i++) 
		{
			String report_file = files_array[i];
			log.add(report_file);
			String report = Domartin.getFileWithoutExtension(report_file);
			int dash = report.indexOf("-");
			String year = report.substring(0,dash);
			String week = report.substring(dash+1,report.length());
			Calendar  c = Calendar.getInstance();
			try
			{
				c.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
				c.set(Calendar.YEAR, Integer.parseInt(year));
				long time = c.getTimeInMillis();
				WeeklyReport wr = getWeeklyReport(user_id, subject, root_path, report);
				Sortable s = new Sortable (time+"" , wr);
            	sarry.add(s);
			} catch (java.lang.NullPointerException npe)
			{
				log.add("npe for year "+report_file);
			}
		}
        Collections.sort(sarry);
		return sarry;
	}
	
	/**
	 * Unbind and return the WeeklyReport object from the file with the report_name
	 * passed in minus the xml file extension (year-week).
	 * @param user_id
	 * @param subject
	 * @param root_path
	 * @param report_name
	 * @return
	 */
	public WeeklyReport getWeeklyReport(String user_id, String subject, String root_path,
			String report_name)
	{
		String file_name = (report_name+".xml");
		String path_to_report = new String(root_path+File.separator
			+"files"+File.separator+user_id+File.separator+Constants.VOCAB+
			File.separator+Constants.HISTORY+File.separator+file_name);
		
		File file = new File(path_to_report);
		log.add("WeeklyReports.getSortedListOfWeeklyReports: path "+path_to_report);
		log.add("WeeklyReports.getSortedListOfWeeklyReports exists? "+file.exists());
		Document doc = loadDocument(path_to_report);
		WeeklyReport weekly_report = unbindWeeklyReport(doc);
		weekly_report.setWeekOfYear(report_name);
		return weekly_report;
	}
	
	public Document loadDocument(String path_to_report)
	{
		File file = new File(path_to_report);
		Document doc = null;
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			//log.add(j.toString());
		} catch (java.io.IOException i)
		{
			//log.add(i.toString());
		} catch (java.lang.NullPointerException npe)
		{
			//log.add(npe.toString());
		}
		return doc;
	}
	
	private WeeklyReport unbindWeeklyReport(Document doc)
	{
		WeeklyReport weekly_report = new WeeklyReport();
		Element root = doc.getRootElement();
		weekly_report.setNumberOfTestsInWeek(Integer.parseInt(root.getChildText("number_of_tests_in_week")));
		weekly_report.setNumberOfSessions(Integer.parseInt(root.getChildText("number_of_sessions")));
		weekly_report.setAverageTimeSpentTesting(Long.parseLong(root.getChildText("average_time_spent_testing")));
		weekly_report.setNumberOfWords(Integer.parseInt(root.getChildText("number_of_words")));
		weekly_report.setReadingAverage(Double.parseDouble(root.getChildText("reading_average")));
		weekly_report.setWritingAverage(Double.parseDouble(root.getChildText("writing_average")));
		Vector reading_levels = new Vector();
		reading_levels.add(0, (String)root.getChildText("words_at_reading_level_0"));
		reading_levels.add(1, (String)root.getChildText("words_at_reading_level_1"));
		reading_levels.add(2, (String)root.getChildText("words_at_reading_level_2"));
		reading_levels.add(3, (String)root.getChildText("words_at_reading_level_3"));
		Vector writing_levels = new Vector();
		writing_levels.add(0, (String)root.getChildText("words_at_writing_level_0"));
		writing_levels.add(1, (String)root.getChildText("words_at_writing_level_1"));
		writing_levels.add(2, (String)root.getChildText("words_at_writing_level_2"));
		writing_levels.add(3, (String)root.getChildText("words_at_writing_level_3"));
		weekly_report.setReadingLevels(reading_levels);
		weekly_report.setWritingLevels(writing_levels);
		return weekly_report;
	}
	
	private Element createRoot(Hashtable last_record, SessionsReport sr)
	{
		Element root = new Element("weekly_report");
		Element number_of_sessions = new Element("number_of_sessions");
		Element average_time_spent_testing = new Element("average_time_spent_testing");
		Element number_of_tests_in_week = new Element("number_of_tests_in_week");
		Element number_of_words = new Element("number_of_words");
		Element writing_average = new Element("writing_average");
		Element reading_average = new Element("reading_average");
		Element words_at_reading_level_0 = new Element("words_at_reading_level_0");
		Element words_at_reading_level_1 = new Element("words_at_reading_level_1");
		Element words_at_reading_level_2 = new Element("words_at_reading_level_2");
		Element words_at_reading_level_3 = new Element("words_at_reading_level_3");
		Element words_at_writing_level_0 = new Element("words_at_writing_level_0");
		Element words_at_writing_level_1 = new Element("words_at_writing_level_1");
		Element words_at_writing_level_2 = new Element("words_at_writing_level_2");
		Element words_at_writing_level_3 = new Element("words_at_writing_level_3");
		number_of_sessions.setText(Integer.toString(sr.getNumberOfSessions()));
		int number_of_tests_in_week_int = sr.getNumberOfTestsInWeek();
		number_of_tests_in_week.setText(Integer.toString(number_of_tests_in_week_int));
		long word_test_elapsed_time = sr.getWordTestElapsedTime();
		long average_time_spent_testing_long = 0;
		try
		{
			average_time_spent_testing_long = (word_test_elapsed_time/number_of_tests_in_week_int);
		} catch (java.lang.ArithmeticException ae)
		{
			// divide by 0.
		}
		average_time_spent_testing.setText(Long.toString(average_time_spent_testing_long));
		number_of_words.setText((String)last_record.get("number_of_words"));
		reading_average.setText((String)last_record.get("reading_average"));
		writing_average.setText((String)last_record.get("writing_average"));
		words_at_reading_level_0.setText((String)last_record.get("words_at_reading_level_0"));
		words_at_reading_level_1.setText((String)last_record.get("words_at_reading_level_1"));
		words_at_reading_level_2.setText((String)last_record.get("words_at_reading_level_2"));
		words_at_reading_level_3.setText((String)last_record.get("words_at_reading_level_3"));
		words_at_writing_level_0.setText((String)last_record.get("words_at_writing_level_0"));
		words_at_writing_level_1.setText((String)last_record.get("words_at_writing_level_1"));
		words_at_writing_level_2.setText((String)last_record.get("words_at_writing_level_2"));
		words_at_writing_level_3.setText((String)last_record.get("words_at_writing_level_3"));
		root.addContent(number_of_sessions);
		root.addContent(average_time_spent_testing);
		root.addContent(number_of_tests_in_week);
		root.addContent(number_of_words);
		root.addContent(reading_average);
		root.addContent(writing_average);
		root.addContent(words_at_reading_level_0);
		root.addContent(words_at_reading_level_1);
		root.addContent(words_at_reading_level_2);
		root.addContent(words_at_reading_level_3);
		root.addContent(words_at_writing_level_0);
		root.addContent(words_at_writing_level_1);
		root.addContent(words_at_writing_level_2);
		root.addContent(words_at_writing_level_3);		
		return root;
	}
	
	/**
	 * Usually we go string from strings in the last_record hash to elements.
	 * This method might be used if we start to use the weekly report bean to pass info.
	 *  number_of_sessions.setText(Integer.toString(weekly_report.setNumberOfTestsInWeek()));
		average_time_spent_testing.setText(Long.toString(weekly_report.getAverageTimeSpentTesting()));
		number_of_tests_in_week.setText(Integer.toString(weekly_report.getNumberOfTestsInWeek()));

AllWordStats
	private int number_of_words;
	private double writing_average;
	private double reading_average;
	private Vector reading_levels;
	private Vector writing_levels;
	private WordStats[] word_stats;
	private RatesOfForgetting rates_of_forgetting;
	private int number_of_retired_words; 
last_record
	<daily_test_index>0:0</daily_test_index>
	<daily_test_file>0</daily_test_file>
	<tests>31774</tests>
	<passed_tests>0</passed_tests>
	<failed_tests>0</failed_tests>
	<reversed_tests>0</reversed_tests>
	<action_name>IntegratedTestResultAction</action_name>
	<action_time>1321081670285</action_time>
	<action_id>IntegratedTestResultAction</action_id>
	<action_type>default</action_type>
	<daily_session_tests>12</daily_session_tests>
	<daily_session_start>1321074191068</daily_session_start>
	<number_of_tests>168</number_of_tests>
	<average_score>49.67261904761905</average_score>
	<number_of_words>2185</number_of_words>
	<writing_average>2.5929531757070006</writing_average>
	<reading_average>2.7644877144181734</reading_average>
	<words_at_reading_level_0>53</words_at_reading_level_0>
	<words_at_reading_level_1>82</words_at_reading_level_1>
	<words_at_reading_level_2>203</words_at_reading_level_2>
	<words_at_reading_level_3>1777</words_at_reading_level_3>
	<words_at_writing_level_0>65</words_at_writing_level_0>
	<words_at_writing_level_1>192</words_at_writing_level_1>
	<words_at_writing_level_2>297</words_at_writing_level_2>
	<words_at_writing_level_3>1561</words_at_writing_level_3>
	<word_test_start>1321081656025</word_test_start>
	<word_test_elapsed_time>4705625</word_test_elapsed_time>
	<number_of_sessions>0</number_of_sessions>
	<week_of_year>0</week_of_year>
	<number_of_tests_in_week>355</number_of_tests_in_week>
	<number_of_retired_words>71</number_of_retired_words>
	<date>1321443300580</date>
	 * @param all_test_stats
	 * @param all_word_stats
	 * @return
	 */
	private WeeklyReport fillInReport(AllWordStats all_word_stats, Hashtable last_record)
	{
		WeeklyReport weekly_report = new WeeklyReport();
		int number_of_tests_in_week = Integer.parseInt((String)last_record.get("number_of_tests_in_week"));
		weekly_report.setNumberOfTestsInWeek(number_of_tests_in_week);
		//private int week_of_year;
		String week_of_year = (String)last_record.get("week_of_year");
		weekly_report.setWeekOfYear(week_of_year);
		//private int number_of_sessions;
		int number_of_sessions = Integer.parseInt((String)last_record.get("number_of_sessions"));
		weekly_report.setNumberOfSessions(number_of_sessions);
		//private long average_time_spent_testing;
		long word_test_elapsed_time = Long.parseLong((String)last_record.get("word_test_elapsed_time"));
		long average_time_spent_testing = (word_test_elapsed_time/number_of_tests_in_week);
		weekly_report.setAverageTimeSpentTesting(average_time_spent_testing);
		//private int number_of_words;
		int number_of_words = Integer.parseInt((String)last_record.get("number_of_words"));
		weekly_report.setNumberOfWords(number_of_words);
		//private double reading and writing_average;
		double reading_average = Double.parseDouble((String)last_record.get("reading_average"));
		weekly_report.setWritingAverage(reading_average);
		double writing_average = Double.parseDouble((String)last_record.get("writing_average"));
		weekly_report.setWritingAverage(writing_average);
		//private Vector reading_levels;
		weekly_report.setReadingLevels(all_word_stats.getReadingLevels());
		//private Vector writing_levels;
		weekly_report.setWritingLevels(all_word_stats.getWritingLevels());
		return weekly_report;
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name, Document new_doc)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(new_doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			//log.add("JDOMFiles.writeDocument3: IOE "+file_name);
			e.printStackTrace();
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
	
	/** deguggin */
	
	private void dumpLog(Vector log)
	{
	    int i = 0;
	    while (i<log.size())
	    {
		    log.add("FileStorage.log "+i+" "+log.get(i));
		    i++;
	    }
	}
	
	public Vector getLog()
	{
		return log;
	}
	
	public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add(v.get(i));
			i++;
		}
	}
	
	public void appendWords(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			Word word = (Word)v.get(i);
			log.add("fs.word: "+word.getDefinition()+" id "+word.getId());
			i++;
		}
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
}
