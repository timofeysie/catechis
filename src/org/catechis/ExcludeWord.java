package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import org.catechis.dto.Word;
import org.catechis.WordTestDateUtility;

import org.catechis.dto.Test;
import java.util.Enumeration;
import org.catechis.Transformer;

public class ExcludeWord
{

	Vector log;
	
	public ExcludeWord()
	{
		log = new Vector();
	}

	/**
	*<p>Returns true for exclude if the words Last Test Date is within the period of days
	* defined in the Words Last Dest Date Vector taken from the users option file.
	*<p>if (last_test_time > this_instant - (exclude_level*1day))
	* then result = true.
	*<p> For instance, if the last test was 1 hour ago, and an hour equals 10, 
	* and the exclude level is 1, and 1 day is 100,
	* and for arguments sake, this_instant is 1000
	* then if (10 > (1000 - (1*100))) then result is true,
	* because we only want to test words that are outside this period, for instance,
	* more than a day since the last test.
	*
	*/
	public boolean checkExclusionDate(Vector elt_vector, int level, Word word, String type)
	{
		boolean result = false;
		//log.add("ExcludeWord.checkExclusionDate: checking "+word.getDefinition());
		Test [] tests = word.getTests();
		//log.add("ExcludeWord.checkExclusionDate: num of tests = "+tests.length);
		
		try
		{
			
		long day = 86400000;
		String o = (String)elt_vector.get(level);
		int exclude_level = Integer.parseInt(o); 			// for instance 1 = 1 day
		long exclude_time = (exclude_level*day); 			// 1 * day in milliseconds
		Date now = new Date();
		String noww = now.toString();
		long this_instant = now.getTime();
		WordTestDateUtility wtdu = new WordTestDateUtility(3);		// find the last test date for the level passed in...
		wtdu.evaluateWordTestDates(word);
		String wltd = wtdu.getWordsLastTestDate(type);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
		ParsePosition pp = new ParsePosition(0);
		DateFormat df = DateFormat.getDateInstance();			//log.add("ExcludeWord.checkExclusionDate wltd "+wltd);
		Date ltd = sdf.parse(wltd, pp);
		long last_test_time = ltd.getTime(); 				// 1 * day in milliseconds
		long exclude_period = this_instant - exclude_time;	
		if (last_test_time > exclude_period)
		{
			result = true;

		} else
		{
			result = false;
		}
		
		} catch (java.lang.NullPointerException npe)
		{
			/*log.add("ExcludeWord.checkExclusionDate: NULLLLLLLL ");
			Vector v = Transformer.createTable(word);
			for (Enumeration e = v.elements() ; e.hasMoreElements() ;) 
			{
				log.add(e.nextElement());

			}
			*/
		}
		return result;
	}
	
	public Vector getLog()
	{
		return log;
	}
}
