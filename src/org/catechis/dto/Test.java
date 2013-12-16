package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*
		<test><date>Mon Apr 11 09:33:15 PDT 2005</date><file>level 1 reading.test</file><grade>pass</grade></test>
		<test><date>Thu May 05 15:43:13 PDT 2005</date><file>level 2 reading.test</file><grade>pass</grade></test>
		<test><date>Mon Jun 27 16:19:48 PDT 2005</date><file>level 5 writing.test</file><grade>pass</grade></test>
*/
public class Test
{

	private String date;
	
	/**The original tests were in xml files and this property was the filename.
	* It contained date, name and grade, where the name was a combination
	* of type and level, such as: reading level 1.xml
	* Very early tests were named beginning reading, and the like.*/
	private String name;
	private String grade;
	private String type;
	private String level;
	private long   milliseconds;
	
	public void setDate(String _date)
	{
		this.date = _date;
	}
	
	public String getDate()
	{
		return date;
	}	
	
	public void setName(String _name)
	{
		this.name = _name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setGrade(String _grade)
	{
		this.grade = _grade;
	}
	
	public String getGrade()
	{
		return grade;
	}
	
	public void setType(String _type)
	{
		this.type = _type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setLevel(String _level)
	{
		this.level = _level;
	}
	
	public String getLevel()
	{
		return level;
	}
	
	public void setMilliseconds(long _milliseconds)
	{
		this.milliseconds = _milliseconds;
	}
	
	public long getMilliseconds()
	{
		return milliseconds;
	}
	
}
