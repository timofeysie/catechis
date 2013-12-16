package org.catechis.juksong;

import java.util.Date;
import java.util.ArrayList;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.constants.*;

public class TestDateEvaluator 
{

	private String type;
	private long time;
	
	private long reading_time;
	private long writing_time;
	
	private boolean used_doe;
	
	private Vector log;
	
	public TestDateEvaluator()
	{
		
	}
	
	/**
	 * Reset all member variables.
	 */
	private void setup()
	{
		type = "null";
		time = 0;
		reading_time = 0;
		writing_time = 0;
		used_doe = false;
	}
	/**
	 * Default values for a word w/o tests would be it's date of entry and reading.
	 * @param word
	 */
	public void evaluteSingleLastTestDate(Word word)
	{
		//System.out.println("TestDateEvaluator.evaluteSingleLastTestDate");
		ArrayList reading_tests = new ArrayList();
		ArrayList writing_tests = new ArrayList();
		Test[] tests = word.getTests();
		int length = tests.length;
		if (length == 0)
		{
			time = word.getDateOfEntry();
			type = Constants.WRITING;
			//System.out.println(word.getDefinition()+" time set from doe "+time);
		} else
		{
			int i = 0;
			while (i<length)
			{
				String str_date = tests[i].getDate();
				String type  = Domartin.getTestType(tests[i].getName());
				String level = Domartin.getTestLevel(tests[i].getName());
				if (level==null)
				{
					// ignore old tests with indeterminable level
				} else
				{
					long this_time = Domartin.getMilliseconds(str_date);
					if (type.equals(Constants.READING))
					{
						//reading_tests.add(this_time);
						if (this_time>reading_time)
						{
							reading_time = this_time;
						}
						//System.out.println(i+" reading "+this_time);
					} else if (type.equals(Constants.WRITING))
					{
						if (this_time>writing_time)
						{
							writing_time = this_time;
						}
						//writing_tests.add(this_time);
						//System.out.println(i+" writing "+this_time);
					}
				}
				i++;
			}
			//ArrayList rtemp = Domartin.sortList(reading_tests);
			//ArrayList wtemp = Domartin.sortList(writing_tests);
			//String recent_reading_time_str = (String)rtemp.get(rtemp.size());
			//String recent_writing_time_str = (String)rtemp.get(wtemp.size());
			//long recent_reading_time = Long.parseLong(recent_reading_time_str);
			//long recent_writing_time = Long.parseLong(recent_reading_time_str);
			if (reading_time>writing_time)
			{
				time = reading_time;
				type = Constants.READING;
			} else
			{
				time = writing_time;
				type = Constants.WRITING;
			}
			//System.out.println("time "+time+" type "+type);
		}
	}
	
	public String getType()
	{
		return type;
	}
	
	public long getTime()
	{
		return time;
	}
	
	/**
	 * Default values for a word w/o tests would be it's date of entry and reading.
	 * @param word
	 */
	public void evaluteLastTestDates(Word word)
	{
		String error_string = new String("none");
		reading_time = word.getDateOfEntry();
		writing_time = word.getDateOfEntry();
		Test[] tests = word.getTests();
		int length = tests.length;
		if (length == 0)
		{
			time = word.getDateOfEntry();
		} else
		{
			int i = 0;
			while (i<length)
			{
				Test this_test = tests[i];
				try
				{
					String str_date = this_test.getDate();
					String str_name = this_test.getName();
					String type  = Domartin.getTestType(str_name);
					String level = Domartin.getTestLevel(str_name);
					
					findNPE(str_date, "str_date");
					findNPE(str_name, "str_name");
					findNPE(type, "type");
					findNPE(level, "level");
					
					if (level==null)
					{
						// ignore old tests with indeterminable level
					} else
					{
						long this_time = Domartin.getMilliseconds(str_date);
						if (type.equals(Constants.READING))
						{
							if (this_time>reading_time)
							{
								reading_time = this_time;
							}
						} else if (type.equals(Constants.WRITING))
						{
							if (this_time>writing_time)
							{
								writing_time = this_time;
							}
						}	
					}
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{
					//System.err.println("TestDateEvaluator.evaluteLastTestDate: aioob for "+word.getDefinition()+" at "+i+" with length "+length);
				} catch (java.lang.NullPointerException npe)
				{
					//System.err.println("TestDateEvaluator.evaluteLastTestDate: npe for "+word.getDefinition()+" at "+i+" with length "+length);
					error_string = ("TestDateEvaluator.evaluteLastTestDate: npe for "+word.getDefinition()+" at "+i+" with length "+length);
					//System.err.println("Transformer log ----------");
					//printLog(Transformer.createTable(this_test));
					//findNPEs(this_test);
					//System.err.println("Transformer log ----------");
					
				}
				i++;
			}
			printError(error_string);
		}
		used_doe = false;
		checkForZeroTime(word);
		//System.out.println(word.getDefinition()+" reading time "+reading_time+" writing time "+writing_time+" used_doe "+used_doe);
		used_doe = false;
	}
	
	/**
	 * This would be used for words with a set of tests.
	 * If a word has no tests, then the date of entry would be used, which should be set elswhere.
	 * @param word
	 */
	public void evaluteFirstTestDates(Word word)
	{
		String error_string = new String("none");
		Date date = new Date();
		Long now = date.getTime();
		reading_time = now;
		writing_time = now;
		Test[] tests = word.getTests();
		int length = tests.length;
		if (length == 0)
		{
			time = now;
		} else
		{
			int i = 0;
			while (i<length)
			{
				Test this_test = tests[i];
				try
				{
					String str_date = this_test.getDate();
					String str_name = this_test.getName();
					String type  = Domartin.getTestType(str_name);
					String level = Domartin.getTestLevel(str_name);
					
					findNPE(str_date, "str_date");
					findNPE(str_name, "str_name");
					findNPE(type, "type");
					findNPE(level, "level");
					
					if (level==null)
					{
						// ignore old tests with indeterminable level
					} else
					{
						long this_time = Domartin.getMilliseconds(str_date);
						if (type.equals(Constants.READING))
						{
							if (this_time<reading_time)
							{
								reading_time = this_time;
							}
						} else if (type.equals(Constants.WRITING))
						{
							if (this_time<writing_time)
							{
								writing_time = this_time;
							}
						}	
					}
				} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
				{
					//System.err.println("TestDateEvaluator.evaluteLastTestDate: aioob for "+word.getDefinition()+" at "+i+" with length "+length);
				} catch (java.lang.NullPointerException npe)
				{
					//System.err.println("TestDateEvaluator.evaluteLastTestDate: npe for "+word.getDefinition()+" at "+i+" with length "+length);
					error_string = ("TestDateEvaluator.evaluteLastTestDate: npe for "+word.getDefinition()+" at "+i+" with length "+length);
					//System.err.println("Transformer log ----------");
					//printLog(Transformer.createTable(this_test));
					//findNPEs(this_test);
					//System.err.println("Transformer log ----------");
					
				}
				i++;
			}
			printError(error_string);
		}
		used_doe = false;
		checkForZeroTime(word);
		//System.out.println(word.getDefinition()+" reading time "+reading_time+" writing time "+writing_time+" used_doe "+used_doe);
		used_doe = false;
	}
	
	public void checkForZeroTime(Word word)
	{
		if (reading_time==0)
		{ 
			reading_time = word.getDateOfEntry();
			used_doe = true;
		}
		if (writing_time==0)
		{
			writing_time = word.getDateOfEntry();
			used_doe = true;
		}
	}
	
	public long getReadingTime()
	{
		return reading_time;
	}
	
	public long getWritingTime()
	{
		return writing_time;
	}
	
	public long getFirstTime()
	{
		if (reading_time < writing_time)
		{
			return reading_time;
		} else 
		{
			return writing_time;
		}
	}
	
	private void printLog(Vector log)
	   {
			int total = log.size();
			int i = 0;
			while (i<total)
			{
				System.err.println(log.get(i));
				i++;
			}
	   }
	
	private void findNPE(String attempt, String label)
	{
		try
		{
			//System.out.println("attempted: "+label+" = "+attempt+": success");
		} catch (java.lang.NullPointerException npe)
		{
			System.err.println("attempted: "+label+" failed");
		}
	}
	
	private void findNPEs(Test test)
	{
		try
		{
			String str_date = test.getDate();
			String str_name = test.getName();
			String type  = Domartin.getTestType(str_name);
			String level = Domartin.getTestLevel(str_name);
			findNPE(str_date, "str_date");
			findNPE(str_name, "str_name");
			findNPE(type, "type");
			findNPE(level, "level");
		} catch (java.lang.NullPointerException npe)
		{
			log.add("entire test is null");
		}
	}
	
	private void printError(String error_string)
	{
		if (error_string.equals("none"))
		{
			
		} else
		{
			log.add(error_string);
		}
	}
	
	private Vector getLog()
	{
		return log;
	}
	
}
