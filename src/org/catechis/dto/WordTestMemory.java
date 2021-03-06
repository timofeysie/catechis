package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
public class WordTestMemory
{
	private String category;
	private String type;
	private String date;
	private String score;
	private String index;
	private String number_correct;
	private long word_id;
	/** THis is a (hopefully) legacy member which contains the test file name like level 2 reading.test*/
	private String test_name;

	public WordTestMemory()
	{
		// Yer usual average no-args constructor
	}
	

	public void setCategory(String _category)
	{
		this.category = _category;
	}
	
	public String getCategory()
	{
		return this.category;
	}
	
	public void setType(String _type)
	{
		this.type = _type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public void setDate(String _date)
	{
		this.date = _date;
	}
	
	public String getDate()
	{
		return this.date;
	}
	
	public void setScore(String _score)
	{
		this.score = _score;
	}
	
	public String getScore()
	{
		return this.score;
	}
	
	public void setIndex(String _index)
	{
		this.index = _index;
	}
	
	public String getIndex()
	{
		return index;
	}

	public void setNumberCorrect(String _number_correct)
	{
		this.number_correct = _number_correct;
	}
	
	public String getNumberCorrect()
	{
		return this.number_correct;
	}
	
	
	public void setTestName(String _test_name)
	{
		this.test_name = _test_name;
	}
	
	public String getTestName()
	{
		return this.test_name;
	}
	
	public void setWordId(long _word_id)
	{
		this.word_id = _word_id;
	}
	
	public long getWordId()
	{
		return this.word_id;
	}
}
