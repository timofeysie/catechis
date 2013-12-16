package org.catechis;

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;

import org.catechis.dto.Word;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestResult;

public class ScorringNotUsed
{

	public ScorringNotUsed()
	{
	
	}
	
	/**
	*
		AllWordsTest
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
	
	?How do we get user options?
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
		WordTestResult wtr = new WordTestResult();
		String question = new String();
		String answer = awt.getAnswer();
		String def = word.getDefinition();
		String text = word.getText();
		String test_type = awt.getTestType();
		int score = 0; int level = 0;
		if (test_type.equals("writing"))
		{
			question = awt.getDefinition();
			answer = awt.getAnswer();
			text = question;
			level = word.getWritingLevel();
		} else if (test_type.equals("reading"))
		{
			question = awt.getText();
			answer = awt.getAnswer();
			level = word.getReadingLevel();
			def = question;
		}
		String modified_question = applyOptions(user_options, question);
		String modified_answer = applyOptions(user_options, answer);
		String max_level = (String)user_options.get("max_level");
		if (modified_question.equalsIgnoreCase(modified_answer))
		{
			wtr = scoreGrade("pass", text, def, answer, level, max_level);
		} else
		{
			wtr = scoreGrade("fail", text, def, answer, level, max_level);
		}
		return wtr;
	}
	
	private String applyOptions(Hashtable user_options, String text)
	{
		String grade_whitespace = (String)user_options.get("grade_whitespace");
		if (grade_whitespace.equals("false"))
		{
			text = StringUtils.deleteWhitespace(text); 
		}
		String exclude_chars = (String)user_options.get("exclude_chars");
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
			String c = exclude_chars.substring(i);
			if (text.contains(c))
			{
				text = text.replace(c, "");
			}
			i++;
		}
		return text;
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

}

