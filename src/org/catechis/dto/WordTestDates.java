package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import org.catechis.dto.Word;
import org.catechis.Domartin;

/**
* This _bean_, like RatesOfFogetting has to do more than most.  It requires even
* more inside its methods to handle the way we want to use this information.
*<p>There are reading, writing, and eventually speaking and listening types.
*<p>There are the number of allowed levels: now 0-3 (these should really be
* from the users options file)
*<p>Then there is a Hashtable of dates and Word objects.
*<p>We also need to access these objects sequentially by date, so an ArrayList of
* dates, which are the keys in the Hashtable is kept as well.
*<p>To make it easy to use, we want to provide all this functionality.
*<p>Each word should be listed twice here (if it has been tested for reading and writing)
*<p>(Eventually we will add speaking and listening also)
*<p>Each word will have only one last test date, at a particular level.
*<p>The millisecond keys represent the lst test date of each word, reading or writing,
* represented in milliseconds to that they can be sorted and.
*<p>reading_wtd_milliseconds(0-3)= reading hashtable keys in milliseconds.
*<p>writing_wtd_milliseconds(0-3)= writing hashtable keys in milliseconds.
*<p>
*<p>reading_wtd_hashes(0-3)= reading milliseconds key - word
*<p>writing_wtd_hashes(0-3)= writing milliseconds key - word
*<p>
*<p>All the try catch blocks are needed to catch incomplete information, such as if there is
* no previous test, etc.  They may not all be needed, but caused a lot of problems while developing
* and testing the add method.
*/
public class WordTestDates
{
	/** The maximum levels*/
	int max_levels;
	
	/** Vectors of array lists from level 0 to 3 */
	private Vector reading_wtd_milliseconds;
	/** Vectors of array lists from level 0 to 3 */
	private Vector writing_wtd_milliseconds;
	/** Vectors of hash tables from level 0 to 3 */
	private Vector reading_wtd_hashes;
	/** Vectors of hash tables from level 0 to 3 */
	private Vector writing_wtd_hashes;
	
	/** DEGUGGING */
	private Vector log;

	public WordTestDates(int _max_levels)
	{
		this.max_levels = _max_levels;
		reading_wtd_milliseconds = Domartin.setupLongVector(max_levels);
		writing_wtd_milliseconds = Domartin.setupLongVector(max_levels);
		reading_wtd_hashes = Domartin.setupLongVector(max_levels);
		writing_wtd_hashes = Domartin.setupLongVector(max_levels);
		log = new Vector();
	}
	
	public ArrayList getSortedTestDatesList(int level, String type)
	{
		ArrayList specific_wtd_milliseconds = new ArrayList();
		try
		{
			specific_wtd_milliseconds = getSpecificWordTestDatesMilliseconds(level, type);
		} catch (java.lang.NullPointerException npe)
		{
			specific_wtd_milliseconds = new ArrayList();
			log.add("@@@getSortedTestDatesList: npe");
		}
		ArrayList sortedKeys = new ArrayList();
		try
		{
			sortedKeys = Domartin.sortList(specific_wtd_milliseconds);
		} catch (java.lang.NullPointerException npe)
		{
			sortedKeys = new ArrayList();
			log.add("@@@getSortedTestDatesList: sorted list npe");
		}
		return sortedKeys;
	}

	public ArrayList getSpecificWordTestDatesMilliseconds(int level, String type)
	{
		ArrayList specific_wtd_milliseconds = new ArrayList();
		if (type.equals("reading"))
		{
			specific_wtd_milliseconds = (ArrayList)reading_wtd_milliseconds.get(level);
		} else if (type.equals("writing"))
		{
			specific_wtd_milliseconds = (ArrayList)writing_wtd_milliseconds.get(level);
		}
		return specific_wtd_milliseconds;
	}
	
	public void addWordTestDate(int level, String type, Word word, String test_date)
	{
		log.add("level - "+level+" type "+type+" word "+word.getText()+" "+word.getDefinition()+" date "+test_date);
		String milliseconds_string = getMillisecondsString(test_date);
		if (type.equals("reading"))
		{
			ArrayList specific_wtd_milliseconds = getSpecificReadingWTDMilliseconds(level);
			Hashtable specific_wtd_hash = getSpecificReadingWTDHash(level);
			specific_wtd_milliseconds = addSpecificWTDMIlliseconds(specific_wtd_milliseconds, milliseconds_string);
			specific_wtd_hash = putSpecificWTDHash(specific_wtd_hash, test_date, word);
			reading_wtd_milliseconds.set(level, specific_wtd_milliseconds);
			reading_wtd_hashes.set(level, (Object)specific_wtd_hash);
		} else if (type.equals("writing"))
		{
			ArrayList specific_wtd_milliseconds = getSpecificWritingWTDMilliseconds(level);
			Hashtable specific_wtd_hash = getSpecificWritingWTDHash(level);
			specific_wtd_milliseconds = addSpecificWTDMIlliseconds(specific_wtd_milliseconds, milliseconds_string);
			specific_wtd_hash = putSpecificWTDHash(specific_wtd_hash, test_date, word);
			writing_wtd_milliseconds.set(level, specific_wtd_milliseconds);
			writing_wtd_hashes.set(level, (Object)specific_wtd_hash);
		}
	}
	
	private String getMillisecondsString(String test_date)
	{
		String milliseconds_string = new String();
		try
		{
			long milliseconds = Domartin.getMilliseconds(test_date);
			milliseconds_string = new String(Long.toString(milliseconds));
		} catch (java.lang.NullPointerException npe)
		{
			milliseconds_string = new String("0");
			log.add("@@@getMillisecondsString: npe");
		}
		return milliseconds_string;
	}
	
	private ArrayList getSpecificReadingWTDMilliseconds(int level)
	{
		ArrayList specific_wtd_milliseconds = new ArrayList();
		try
		{
			specific_wtd_milliseconds = (ArrayList)reading_wtd_milliseconds.get(level);
		} catch (java.lang.NullPointerException npe)
		{
			specific_wtd_milliseconds = new ArrayList();
			log.add("@@@getSpecificReadingWTDMilliseconds: npe");
		}
		return specific_wtd_milliseconds;
	}
	
	private Hashtable getSpecificReadingWTDHash(int level)
	{
		Hashtable specific_wtd_hash = new Hashtable();
		try
		{
			specific_wtd_hash = (Hashtable)reading_wtd_hashes.get(level);
		} catch (java.lang.NullPointerException npe)
		{
			specific_wtd_hash = new Hashtable();
			log.add("@@@getSpecificReadingWTDHash: npe");
		}
		return specific_wtd_hash;
	}
	
	private ArrayList getSpecificWritingWTDMilliseconds(int level)
	{
		ArrayList specific_wtd_milliseconds = new ArrayList();
		try
		{
			specific_wtd_milliseconds = (ArrayList)writing_wtd_milliseconds.get(level);
		} catch (java.lang.NullPointerException npe)
		{
			specific_wtd_milliseconds = new ArrayList();
			log.add("@@@getSpecificWritingWTDMilliseconds: npe");
		}
		return specific_wtd_milliseconds;
	}
	
	private Hashtable getSpecificWritingWTDHash(int level)
	{
		Hashtable specific_wtd_hash = new Hashtable();
		try
		{
			specific_wtd_hash = (Hashtable)writing_wtd_hashes.get(level);
		} catch (java.lang.NullPointerException npe)
		{
			specific_wtd_hash = new Hashtable();
			log.add("@@@getSpecificWritingWTDHash: npe");
		}
		return specific_wtd_hash;
	}
	
	private ArrayList addSpecificWTDMIlliseconds(ArrayList specific_wtd_milliseconds, String milliseconds_string)
	{
		try
		{
			specific_wtd_milliseconds.add(milliseconds_string);
		} catch (java.lang.NullPointerException npe)
		{
			if (specific_wtd_milliseconds==null)
			{
				specific_wtd_milliseconds = new ArrayList();
				specific_wtd_milliseconds.add(milliseconds_string);
				log.add("@@@addSpecificWTDMIlliseconds: specific_wtd_milliseconds==null");
			}
			if (milliseconds_string==null)
			{
				milliseconds_string = new String("0");
				specific_wtd_milliseconds.add(milliseconds_string);
				log.add("@@@addSpecificWTDMIlliseconds: milliseconds_string==null");
			}
		}
		specific_wtd_milliseconds.add(milliseconds_string);
		return specific_wtd_milliseconds;
	}
	
	private Hashtable putSpecificWTDHash(Hashtable specific_wtd_hash, String test_date, Word word)
	{
		try
		{
			specific_wtd_hash.put(test_date, word);
		} catch (java.lang.NullPointerException npe)
		{
			if (specific_wtd_hash==null)
			{
				specific_wtd_hash = new Hashtable();
				log.add("@@@putSpecificWTDHash: specific_wtd_hash==null");
			}
			if (test_date==null)
			{
				test_date = new String("0");
				log.add("@@@putSpecificWTDHash: test_date==null");
			}
			if (word==null)
			{
				word = new Word();
				log.add("@@@putSpecificWTDHash: word==null");
			}
			specific_wtd_hash.put(test_date, word);
		}
		return specific_wtd_hash;
	}
	
	public Word getSpecificWord(int level, String type, String date_key)
	{
		Word word = new Word();
		if (type.equals("reading"))
		{
			Hashtable specific_wtd_hash = (Hashtable)reading_wtd_hashes.get(level);
			try
			{
				word = (Word)specific_wtd_hash.get(date_key);
			} catch (java.lang.NullPointerException npe)
			{
				word = new Word();
			}
		} else if (type.equals("writing"))
		{
			Hashtable specific_wtd_hash = (Hashtable)writing_wtd_hashes.get(level);
			try
			{
				word = (Word)specific_wtd_hash.get(date_key);
			} catch (java.lang.NullPointerException npe)
			{
				word = new Word();
			}
		}
		return word;
	}
	
	/**
	*Get the specific word, and then remove the old hashtable and arraylist entries,
	* and add the new entry.
	*<p>Notice that the new date argument is represented as a date with the normal format we use, and not milliseconds like the
	* old date key argument.
	*/
	public void changeSpecificWordTestDate(int old_level, int new_level, String type, String old_date_key, String new_date)
	{
		Word word = getSpecificWord(old_level, type, old_date_key);
		Hashtable specific_wtd_hash = (Hashtable)reading_wtd_hashes.get(old_level);
		try
		{
			specific_wtd_hash.remove(old_date_key);
		}  catch (java.lang.NullPointerException npe)
		{}
		ArrayList specific_wtd_milliseconds = getSpecificWordTestDatesMilliseconds(old_level, type);
		try
		{
			specific_wtd_milliseconds.remove(old_date_key);
		} catch (java.lang.NullPointerException npe)
		{}
		addWordTestDate(new_level, type, word, new_date);
	}
	
	/**
	*This method can be used when we don't know the last test date of a particular word.
	*So we have to cycle through all the words at all levels of a particular type.
	*I would suggest to the developers to keep a copy of the last test dates inside the
	* word object so that this method doesn't have to be called, as it is costly on resources.
	*/
	public String getSpecificWordTestMIllisecondsKey(String type, Word word)
	{
		String milliseconds = new String("0");
		String text = word.getText();
		String def  = word.getDefinition();
		int level = 0;
		if (type.equals("reading"))
		{
			while (level<max_levels)
			{
				milliseconds = getSpecificReadingWordTestMillisecondsKey(level, text, def);
				level++;
			}
		} else if (type.equals("writing"))
		{
			while (level<max_levels)
			{
				Hashtable hash = (Hashtable)writing_wtd_hashes.get(level);
				Enumeration keys = hash.keys();
				while (keys.hasMoreElements())
				{
					String this_key = (String)keys.nextElement();
					Word this_word = (Word)hash.get(this_key);
					String this_text = this_word.getText();
					String this_def  = this_word.getDefinition();
					if (this_text.equals(text)&&this_def.equals(def))
					{
						milliseconds = this_key;
					}
				}
				level++;
			}
		}
		return milliseconds;
	}
	
	public String getSpecificReadingWordTestMillisecondsKey(int level, String text, String def)
	{
		String milliseconds = new String("0");
		try
		{
			Hashtable hash = (Hashtable)reading_wtd_hashes.get(level);
			Enumeration keys = hash.keys();
			while (keys.hasMoreElements())
			{
				String this_key = (String)keys.nextElement();
				Word this_word = (Word)hash.get(this_key);
				String this_text = this_word.getText();
				String this_def  = this_word.getDefinition();
				addToLog("getSpecificReadingWordTestMillisecondsKey: key("+this_key+") "+this_text+" "+this_def);
				if (this_text.equals(text)&&this_def.equals(def))
				{
					milliseconds = this_key;
					break;
				}
			}
		} catch (java.lang.NullPointerException npe)
		{
			milliseconds = new String("0");
		}
		return milliseconds;
	}

	/** debuggin */
	public Vector getLog()
	{
		return log;
	}
	
	public void addToLog(String s)
	{
		log.add(s);
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
}
