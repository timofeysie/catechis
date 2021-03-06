package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*
	<word>
		<text>바닷가</text>
		<definition>beach</definition>
		<level>1</level>
		<writing-level>6</writing-level>
		<reading-level>3</reading-level>
		<test>
			<date>Sun Jan 30 13:43:01 PST 2005</date>
			<file>level 0 reading.test</file>
			<grade>pass</grade>
		</test>
		<test><date>Mon Apr 11 09:33:15 PDT 2005</date><file>level 1 reading.test</file><grade>pass</grade></test>
		<test><date>Thu May 05 15:43:13 PDT 2005</date><file>level 2 reading.test</file><grade>pass</grade></test>
		<test><date>Mon Jun 27 16:19:48 PDT 2005</date><file>level 5 writing.test</file><grade>pass</grade></test>
	</word>
*/
public class Word
{
	// data kept on xml file
	private String text;
	private String definition;
	private int writing_level;
	private int reading_level;
	private long date_of_entry;
	private Test[] tests;
	private long id;
	// data used for tests, as well as in FileJDOMWordLists to save bytes in the new word lists.
	private String category;
	private long category_id;
	/** When a word is well known and doesn't need to be tested anymore.	 */
	private boolean retired;
	/** These are option_name-value strings */
	private String[] testing_options;
	
	public Word()
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
	
	public void setWritingLevel(int _writing_level)
	{
		writing_level = _writing_level;
	}
	
	public int getWritingLevel()
	{
		return writing_level;
	}	
	
	public void setReadingLevel(int _reading_level)
	{
		reading_level = _reading_level;
	}
	
	public int getReadingLevel()
	{
		return reading_level;
	}
	
	public void setDateOfEntry(long time)
	{
		this.date_of_entry = time;
	}
	
	public long getDateOfEntry()
	{
		return this.date_of_entry;
	}
	
	//Methods to access the entire indexed property array
	public Test[] getTests()
	{
		return tests;
	}
	
	public void setTests(Test[] _tests)
	{
		tests = _tests;
	}

	//Methods to access individual values
	public Test getTests(int index)
	{
		return tests[index];
	}
	
	public void setTests(int index, Test _test)
	{
		tests[index] = _test;
	}
	
	public void setId(long _id)
	{
		this.id = _id;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	
	public void setCategory(String _category)
	{
		category = _category;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategoryId(long _category_id)
	{
		category_id = _category_id;
	}
	
	public long getCategoryId()
	{
		return category_id;
	}
	
	public void setRetired(boolean _retired)
	{
		this.retired = _retired;
	}
	
	public boolean getRetired()
	{
		return this.retired;
	}
	
	//Methods to access the entire indexed property array
	public String[] getTestingOptions()
	{
		return testing_options;
	}
	
	public void setTestingOptions(String[] _testing_options)
	{
		testing_options = _testing_options;
	}

	//Methods to access individual values
	public String getTestingOption(int index)
	{
		return testing_options[index];
	}
	
	public void setTestingOption(int index, String _testing_option)
	{
		testing_options[index] = _testing_option;
	}

}
