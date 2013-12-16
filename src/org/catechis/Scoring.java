package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.lang.Double;
import java.util.Vector;
import java.util.Hashtable;

import org.catechis.dto.Word;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestResult;

import org.apache.commons.lang.StringUtils;
/**
* This class provides utility methods for scoring test results.
*/
public class Scoring
{
	private Vector log;

	public Scoring()
	{
		log = new Vector();
	}

	public String getPercentageScore(double index, double score)
	{
		//  index 7 score 10 string ratio 1.0 ratio 1.0 scorepercentage_score 100.0
		double ratio = (score / index);
		Double d_ratio = new Double(ratio);
		String string_ratio = d_ratio.toString();
		Double percentage_score = new Double(ratio * 100);
		String new_score = percentage_score.toString();
		int dot = new_score.lastIndexOf(".");
		String return_score = new_score.substring(0,dot);
		return return_score;
	}
	
	public String getRatioString(int index, int score)
	{
		String ratio = new String(score+"/"+index);
		return ratio;
	}
	
	/**
	*	AllWordsTest
	// data kept on xml file
	private String text;
	private String definition;
	private String category;
	private String test_type;
	// users answers
	private String answer;
	
		WordTestResult
	private String text;
	private String definition;
	private String answer;
	private String grade;
	private String level;
	private int id;
	private String original_text;
	private String original_definition;
	private String original_level;
	
	?How do we communicate with Storage to get at JDOM?
	Answer= we send all the info JDOM needs back in the wtr
		and then FileStorage calls the following:
		words_jdom.recordWordScore(question, "pass", test_name, date, test_type, max_level);
		(Note the date will be set in jdom now)
		Also, original, and new levels must be gotten later...
	// writing: def/question - text/answer
	// reading: text/key - answer/def
	*/
	public WordTestResult scoreSingleWord(AllWordsTest awt, Word word, Hashtable user_options)
	{
		log.add("created wtr");
		WordTestResult wtr = new WordTestResult();
		String correct_answer = new String();
		String user_answer = awt.getAnswer();
		String def = word.getDefinition();
		String text = word.getText();
		String test_type = awt.getTestType();
		log.add(text+" "+def);
		int score = 0; int level = 0;
		if (test_type.equals("writing"))
		{
			correct_answer = awt.getText();
			text = correct_answer;
			level = word.getWritingLevel();
		} else if (test_type.equals("reading"))
		{
			correct_answer = awt.getDefinition();
			level = word.getReadingLevel();
			def = correct_answer;
		}
		String modified_correct_answer = applyOptions(user_options, correct_answer);
		String modified_user_answer = applyOptions(user_options, user_answer);
		log.add("modified correct answer "+modified_correct_answer);
		log.add("modified user answer    "+modified_user_answer);
		String max_level = (String)user_options.get("max_level");
		if (modified_correct_answer.equalsIgnoreCase(modified_user_answer))
		{
			wtr = scoreGrade("pass", text, def, user_answer, level, max_level);
			log.add("pass");
		} else
		{
			wtr = scoreGrade("fail", text, def, user_answer, level, max_level);
			log.add("fail");
		}
		wtr.setWordId(awt.getId());
		log.add("word id    "+word.getId());
		return wtr;
	}
	
	public String applyOptions(Hashtable user_options, String text)
	{
		String grade_whitespace = (String)user_options.get("grade_whitespace");
		String exclude_chars = (String)user_options.get("exclude_chars");
		String exclude_area = (String)user_options.get("exclude_area");
		String exclude_area_begin_char = (String)user_options.get("exclude_area_begin_char");
		String exclude_area_end_char = (String)user_options.get("exclude_area_end_char");
		String alternate_answer_separator = (String)user_options.get(" alternate_answer_separator");
		
		log.add("Scoring.applyOptions: exclude_chars "+exclude_chars);
		
		if (grade_whitespace.equals("false"))
		{
			text = StringUtils.deleteWhitespace(text); 
		}
		if (exclude_area.equals("true"))
		{
			text = excludeArea(exclude_area_begin_char, exclude_area_end_char, text);
		}
		if (exclude_chars.length()>0)
		{
			text = removeExcludeChars(text, exclude_chars);
		}
		return text;
	}
	
	public String removeExcludeChars(String text, String exclude_chars)
	{
		int i = 0;
		int size = exclude_chars.length();
		while (i<size)
		{
			String c = exclude_chars.substring(i, i+1);
			log.add(i+"looking at "+c);
			if (text.contains(c))
			{
				log.add("text.contains("+c+")");
				String debug = text;
				text = text.replace(c, "");
				log.add("replaced "+debug+" with "+text);
			}
			i++;
		}
		return text;
	}
	
	private String excludeArea(String exclude_area_begin, String exclude_area_end, String text)
	{
		String first_part = new String();
		int first = text.indexOf(exclude_area_begin);
		int last = text.lastIndexOf(exclude_area_end);
		int length = text.length();
		try
		{
			first_part = text.substring(0, first);
			if (length != last)
			{
				String last_part = text.substring(last+1, length);
				first_part = first_part.concat(last_part);
			}
		} catch (java.lang.StringIndexOutOfBoundsException sioob)
		{
			first_part = text;
		}
		return first_part;
	}
	
	private WordTestResult scoreGrade(String grade, String text, String def, String answer, int level, String max)
	{
		int max_level = Integer.parseInt(max);
		String org_level = Integer.toString(level);
		if (grade.equals("pass"))
		{
			level = limitLevel((level++), max_level); // check level
		} else
		{
			level = limitLevel((level--), max_level); // check level
		}
		String new_level =  Integer.toString(level);
		WordTestResult wtr = addWordTestResults(text, def, answer, grade, org_level, new_level);
		return wtr;
	}
	
	// (text, def, answer, "fail", index)
	private WordTestResult addWordTestResults(String text,String def,String answer,String grade, String org_level, String new_level)
	{
		WordTestResult wtr = new WordTestResult();
		wtr.setText(text);
		wtr.setDefinition(def);
		wtr.setAnswer(answer);
		wtr.setGrade(grade);
		wtr.setLevel(new_level);
		wtr.setOriginalText(text);
		wtr.setOriginalDefinition(def);
		wtr.setOriginalLevel(org_level);
		return wtr;
	}
	
	private int limitLevel(int level, int max_level)
	{
		if (level>max_level)
		{
			level = max_level;
		}
		return level;
	}
	
	public Vector getLog()
	{
		return log;
	}

}

