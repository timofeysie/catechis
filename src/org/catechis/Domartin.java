package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.List;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Comparator;
import java.util.Collections;
import java.util.Enumeration;
// import org.jdom.Element;
import java.io.File;
import java.lang.Long;
import java.lang.SecurityManager;
import java.lang.SecurityException;
import java.lang.NullPointerException;
import org.apache.commons.beanutils.BeanComparator;

import org.catechis.dto.Word;
import org.catechis.dto.Test;

/**
<p>This class contains general purpose static methods for all commoners.</p>
<p>The name reflects the Commons of Domartin in Switzerland when the Authors ancestors live.</p>
<p>To Do:</p>
<p>Create an exception for getFileExtension in the case that there is no extension.</p>
<p>@author Timothy Butler Curchod</p>
<p>@version Setpember 11, 2004</p>
*/
public class Domartin
{

	public Domartin()
	{
	}
	
	/**
	*@param user Name/id of the user which equates to a folder in the 'files' directory.
	*@param path Path that is added to the name from which the file is create.
	*@return A file that is created by adding the path to the user separated by the system separator.
	*/
	public static File createFileFromUserNPath(String user, String path)
	{
		Properties properties = System.getProperties();
		String sep = new String(properties.getProperty("file.separator"));
		String full_path = new String(path+sep+user);
		File file = new File(full_path);
		return file;
	}
	
	public static String createStringFromUserNPath(String user, String path)
	{
		Properties properties = System.getProperties();
		String sep = new String(properties.getProperty("file.separator"));
		String full_path = new String(path+sep+user);
		return full_path;
	}	
	
	/**
	*<P>This method returns the extension WITH THE "DOT" ATTACHED.
	<p>Returns -1 if there is no extension.
	*/
	public static String getFileExtension(String file_name)
	{
		String ext = new String();
		try
		{
			int dot = file_name.lastIndexOf(".");
			int len = file_name.length();
			ext = file_name.substring(dot,len);
		}
		catch (java.lang.StringIndexOutOfBoundsException e)
		{
			ext = "-1";
		}
		return ext;
	}
	
	/**
	<p>This method was created when we were trying to load a file from a folder
	created manually in the webapps directory because we had Tomcat set to not
	expand .war files, which is faster.  It may still be possible to work with
	the file system when using an unexpanded war file, but the problem turned out
	to have nothing to do with permissions.</p>
	
	public static void checkPathPermissions(File _file, ServletContext _context)
	{
		String file_path = _file.getAbsolutePath();
		ServletContext context = _context;
		String msg = new String();
		SecurityManager security = System.getSecurityManager();
		if (security != null) 
		{
			try
			{
				security.checkRead(file_path);
			}
			catch (java.lang.NullPointerException ne)
			{
				msg = ("null security "+ ne.toString());
				context.log(msg);
			}
			catch (SecurityException se)
			{
				msg = ("security exception "+se.toString());
				context.log(msg);
			}
		}
		else
		{
			msg = ("Security is null:  What should we do?");
			context.log(msg);
		}
	}
	*/
	
	/**
	<p>This method could return -1 if you pass in a string with no '.'</p>
	*/
	public static String getFileWithoutExtension(String file_w_ext)
	{
		String file_wo_ext = new String();
		try
		{
			int dot = file_w_ext.lastIndexOf(".");
			file_wo_ext = file_w_ext.substring(0, dot);
		}
		catch (java.lang.StringIndexOutOfBoundsException e)
		{
			file_wo_ext = "-1";
		}
		return file_wo_ext;
	}
	
	/**
	*<p>This method needs to know what range of words has been tested, because we only want
	* to evaluate the levels of those words.</p>
	*<p>Right now this is not possible as the number of words is fixed at 10,
	* and each test only tests ten words, which most test files have anyway.</p>
	*<p>Currently we have to cycle thru all the words, and look for a "level" child and create
	* it if it doesn't already exist.</p>
	*<p>Then we have to look at the most recent test, and adjust the level if it meets
	* the criteria of the options file level settings.  For example, if the word was answered
	* correctly, then a "pass" is registered in the test sub-element.  Then, if we find the
	* word is at level 0, or if there is no level sub-element, we create it, and check to options
	* level, which, if level-1 is set to one, that means one passed test is enough to advance the
	* level to 1.</p>
	
	public static Hashtable evaluateWordLevels(CreateJDOMList levels_jdom, String test_name)
	{
		Hashtable new_word_levels = new Hashtable();
		List word_list = levels_jdom.getWhateverElementsList("word");
		Iterator words = word_list.iterator();
		while (words.hasNext())
		{
			Element word = (Element)words.next();
			String current_level = levels_jdom.ifXDontExistCreateIt(word, "level", "0");
			List tests = word.getChildren("test");
			Iterator all_tests = tests.iterator();
			String recent_result = mostRecentTestResult(all_tests, test_name);
			String new_level = new String();
			if (recent_result.equals("pass"))
				new_level = incrementWordLevel(current_level);
			else
				new_level = decrementWordLevel(current_level);
			Element level = word.getChild("level");
			level.setText(new_level);
			String word_text = word.getChildText("text");
			new_word_levels.put(word_text,new_level);
		}
		return new_word_levels;
	}
	*/
	
	/**
	*<p>Here we check for tests from only the given test file passed in as an argument.</p>
	*<p>We return only the pass/fail string of the most recent test.</p>
	
	private static String mostRecentTestResult(Iterator all_tests, String test_name)
	{
		String most_recent_result = new String();
		DateFormat df = DateFormat.getDateInstance();
		Date recent_date = null;
		while (all_tests.hasNext())
		{
			Element test = (Element)all_tests.next();
			String file_name = test.getChildText("file");
			if (file_name.equals(test_name))
			{
				// find most recent test date.
			}
		}
		return most_recent_result;
	}
	*/
	
	public static String incrementWordLevel(String current_level)
	{
		Integer current_int = Integer.valueOf(current_level);
		int current = current_int.intValue();
		int new_level = (current+1);
		String return_level = Integer.toString(new_level);
		return return_level;
	}
	
	/**
	*This method limits the incremet so the max_level set in user options.
	*/
	public static String incrementWordLevelLimited(String current_level, String max_level)
	{
		Integer current_max = Integer.valueOf(max_level);
		int max = current_max.intValue();
		Integer current_int = Integer.valueOf(current_level);
		int current = current_int.intValue();
		int new_level = (current+1);
		if (new_level > max)
		{
			new_level = max;
		}
		String return_level = Integer.toString(new_level);
		return return_level;
	}
	
	public static String decrementWordLevel(String current_level)
	{
		Integer current_int = Integer.valueOf(current_level);
		int current = current_int.intValue();
		int new_level = (current-1);
		if (new_level<0)
		{
			new_level = 0;
		}
		String return_level = Integer.toString(new_level);
		return return_level;
	}
	
	/**
	*Parse level from the likes of:
	level 0 reading.test
	* and return null for the likes of:
	basic_writing.test
	basic_ten_word.test
	*/
	public static String getTestLevel(String test_file)
	{
		String level = new String();
		String test_file_w_o_ext = Domartin.getFileWithoutExtension(test_file);
		int space = test_file_w_o_ext.indexOf(" ");
		int len = test_file_w_o_ext.length();
		if (len == -1)
		{
			// set non-conforming names to null
			level = null;
		} 
		else
		{
			try
			{
				level = test_file_w_o_ext.substring(space+1, space+2);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{
				level = null;
			}
			try
			{
				int int_level = Integer.parseInt(level);
			} catch (java.lang.NumberFormatException nfe)
			{
				level = null;
			}
		}
		return level;
	}
	
	/**
	* Here we parse the test type.
	* Original tests were like this: basic_writing.test
	* They included level 1 to 3 or something.
	* Therefore we can't use them to establish R.O.F.
	* After that the looked like: level 0 reading.test
	*<p>Old tests looked like this:
	*<p>basic_writing.test
	*<p> for levels 0 to 1?
	*<p>or this:
	*<p>intermediate_writing.test
	*<p>for tests 1 to 2?
	*<p>and even:
	*<p>beginner_reading.test
	*<p>same as basic???
	*<p>I had to construct them based on the order of the tests, to see at
	* what level they changed.
	*<p>And were chenged around Dec 15th, 2005 during the end of the hey
	* days with Bommie the beautiful.
	*/
	public static String getTestType(String test_file)
	{
		String type = new String();
		String test_file_w_o_ext = Domartin.getFileWithoutExtension(test_file);
		int space = test_file_w_o_ext.lastIndexOf(" ");
		int len = test_file_w_o_ext.length();
		if (len == -1)
		{
			// set non-conforming names to null
			type = null;
		} 
		else
		{
			try
			{
				type = test_file_w_o_ext.substring(space+1, len);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{
				type = null;
			}
		}
		int underline = type.lastIndexOf("_");
		if (underline != -1)
		{
			len = type.length();
			type = type.substring(underline+1, len);
		}
		return type;
	}
	
	public static String getOldTestType(String test_file)
	{
		String type = new String();
		String test_file_w_o_ext = Domartin.getFileWithoutExtension(test_file);
		int space = test_file_w_o_ext.lastIndexOf("_");
		int len = test_file_w_o_ext.length();
		if (len == -1)
		{
			// set non-conforming names to null
			type = null;
		} 
		else
		{
			try
			{
				type = test_file_w_o_ext.substring(space+1, len);
			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
			{
				type = null;
			}
		}
		return type;
	}
	
	public static ArrayList<Object> sortList(ArrayList<Object> list, String sort_by)
	{
		Comparator<Object> comparator = new BeanComparator(sort_by);
		Collections.sort(list, comparator);
		return list;
	}
	
	public static ArrayList<Object> sortList(ArrayList list)
	{
		Collections.sort(list);
		return list;
	}
	
	public static ArrayList<Object> turnVectorToList(Vector vec)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		int i = vec.size();
		int j = 0;
		while (i<j)
		{
			list.add(vec.get(j));
			j++;
		}
		return list;
	}
	
	/**
	*Get milliseconds from a long format date like this: EEE MMM dd HH:mm:ss zzz yyyy.
	*/
	public static long getMilliseconds(String str_date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		long time = 0;
		try
		{
			date = sdf.parse(str_date, pp);
			time = date.getTime();
		} catch (java.lang.NullPointerException npe)
		{
		}
		return time;
	}
	
	public static long getMillisecondsFromShortDate(String str_date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		ParsePosition pp = new ParsePosition(0);
		long time = 0;
		try
		{
			date = sdf.parse(str_date, pp);
			time = date.getTime();
		} catch (java.lang.NullPointerException npe)
		{
		}
		return time;
	}
	
	public static String getCurrentFormattedDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		Date date = new Date();
		String str_date = date.toString();
		ParsePosition pp = new ParsePosition(0);
		long time = 0;
		try
		{
			date = sdf.parse(str_date, pp);
		} catch (java.lang.NullPointerException npe)
		{
		}
		str_date = date.toString();
		return str_date;
	}
	
	/**
	*This poetic method returns the time in days, hours, and minutes of a milliseconds.
	*"the epoch", namely January 1, 1970, 00:00:00 GMT.
	*/
	public static String getElapsedTime(String milliseconds_string)
	{
		long milliseconds = Long.parseLong(milliseconds_string);
		long secs = Long.parseLong("1000");
		long time = (milliseconds/secs);
		Long long_time = Long.valueOf(Long.toString(time));
		int timeInSeconds = long_time.intValue();
		int days, hours, minutes, seconds;
		String day_s = new String("s");
		String hour_s = new String("s");
		String minute_s = new String("s");
		String second_s = new String("s");
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		days = hours / 24;
		hours = hours - (days * 24);
		if (days==1) {day_s = "";}
		if (hours==1) {hour_s = "";}
		if (minutes==1) {minute_s = "";}
		if (seconds==1) {second_s = "";}
		String elapsed_time = (days+" day"+day_s+" "+hours+" "+"hour"+hour_s+" "+minutes+" "+"minute"+minute_s+" "+seconds+" "+"second"+second_s);
		return elapsed_time;
	}
	
	/**
	*This poetic method returns the time in days, hours, and minutes of a milliseconds.
	*"the epoch", namely January 1, 1970, 00:00:00 GMT.
	*/
	public static String getBriefElapsedTime(String milliseconds_string)
	{
		long milliseconds = Long.parseLong(milliseconds_string);
		long secs = Long.parseLong("1000");
		long time = (milliseconds/secs);
		Long long_time = Long.valueOf(Long.toString(time));
		int timeInSeconds = long_time.intValue();
		int days, hours, minutes, seconds;
		String day_s = new String("s");
		String hour_s = new String("s");
		String minute_s = new String("s");
		String second_s = new String("s");
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		days = hours / 24;
		hours = hours - (days * 24);
		if (days==1) {day_s = "";}
		if (hours==1) {hour_s = "";}
		if (minutes==1) {minute_s = "";}
		if (seconds==1) {second_s = "";}
		String day_string = ""; String hour_string = ""; String minute_string = ""; String second_string = "";
		if (days != 0) {day_string = days+" day"+day_s+" ";}
		if (hours != 0) {hour_string = hours+" "+"hour"+hour_s+" ";}
		if (minutes != 0) {minute_string = minutes+" "+"minute"+minute_s+" ";}
		if (seconds != 0) {second_string = seconds+" "+"second"+second_s;}
		String elapsed_time = (day_string+hour_string+minute_string+second_string);
		return elapsed_time;
	}
	
	public static int getDays(String milliseconds_string)
	{
		long milliseconds = Long.parseLong(milliseconds_string);
		long secs = Long.parseLong("1000");
		long time = (milliseconds/secs);
		Long long_time = Long.valueOf(Long.toString(time));
		int timeInSeconds = long_time.intValue();
		int days, hours, minutes, seconds;
		String day_s = new String("s");
		String hour_s = new String("s");
		String minute_s = new String("s");
		String second_s = new String("s");
		hours = timeInSeconds / 3600;
		timeInSeconds = timeInSeconds - (hours * 3600);
		minutes = timeInSeconds / 60;
		timeInSeconds = timeInSeconds - (minutes * 60);
		seconds = timeInSeconds;
		days = hours / 24;
		return days;
	}
	
	public static Vector setupLongVector(int allowed_number_of_levels)
	{
		Vector setup_vector = new Vector();
		int inde = 0;
		while (inde<allowed_number_of_levels)
		{
			setup_vector.add(inde, null);
			inde++;
		}
		return setup_vector;
	}
	
	static long current= System.currentTimeMillis();
	
	public static synchronized long getNewID()
	{
		Random rnd = new Random();
		long uniqueId = ((System.currentTimeMillis()>>>16)<<16)+rnd.nextLong();
		return current+uniqueId;
	}
	
	public static int getDailyTestIndexMark(String index, String type)
	{
		int colon = index.indexOf(":");
		if (colon == -1)
		{
			return 0;
		}
		String is = index.substring(0, colon);
		String js = index.substring(colon+1, index.length());
		if (type.equals("j"))
		{
			return Integer.parseInt(js);
		} else
		{
			return Integer.parseInt(is);
		}
	}
	
	/**
	*If the folder is named after the user_id, then the user_name needs to be
	* set as the id, as the user_name used to be used as the id.
	*<p>Returns the users name if there is a folder under the users name.
	*<p>Returns the users id if there is no folder under their name, cause its named after their id.
	*/
	public static String checkOldUser(String user_name, String user_id, String context_path)
	{
		String user_folder = (context_path+File.separator+"files"+File.separator+user_name);
		File folder = new File(user_folder);
		if (folder.exists())
		{
			return user_name;
		}
		return user_id;
	}
	
	/**
	*Get the current date and time in milliseconds.
	*/
	public static String getCurrentTime()
	{
		Date today = new Date();
		long now = today.getTime();
		String time = Long.toString(now);
		return time;
	}
}
