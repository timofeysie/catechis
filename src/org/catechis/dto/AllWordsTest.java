package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*
*/
public class AllWordsTest
{
	// data kept on xml file
	private String text;
	private String definition;
	private String category;
	private String test_type;
	private String level;
	private int daily_test_index;
	private long id;
	
	// users answers
	private String answer;
	
	public AllWordsTest()
	{
	}
	
	public void setText(String _text)
	{
		text = _text;
	}
	
	public String getText()
	{
		return text;
	}	
	
	public void setDefinition(String _definition)
	{
		definition = _definition;
	}
	
	public String getDefinition()
	{
		return definition;
	}
	
	public void setCategory(String _category)
	{
		category = _category;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setTestType(String _test_type)
	{
		test_type = _test_type;
	}
	
	public String getTestType()
	{
		return test_type;
	}
	
	public String getLevel()
	{
		return level;
	}
	
	public void setLevel(String _level)
	{
		level = _level;
	}
	
	public void setAnswer(String _answer)
	{
		answer = _answer;
	}
	
	public String getAnswer()
	{
		return answer;
	}
	
	public void setDailyTestIndex(int _daily_test_index)
	{
		daily_test_index = _daily_test_index;
	}
	
	public int getDailyTestIndex()
	{
		return daily_test_index;
	}
	
	public void setId(long _id)
	{
		this.id = _id;
	}
	
	public long getId()
	{
		return this.id;
	}

}
