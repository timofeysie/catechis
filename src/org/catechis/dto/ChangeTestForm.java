package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
public class ChangeTestForm
{
	private String text;
	private String definition;
	private String answer;
	private String grade;
	private String reverse_grade;
	private String add_answer;

	public ChangeTestForm()
	{
		// Yer usual average no-args constructor
	}
	
	public void setText(String _text)
	{
		this.text = _text;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setDefinition(String _definition)
	{
		this.definition = _definition;
	}
	
	public String getDefinition()
	{
		return this.definition;
	}

	public void setAnswer(String _answer)
	{
		this.answer = _answer;
	}
	
	public String getAnswer()
	{
		return this.answer;
	}

	public void setGrade(String _grade)
	{
		this.grade = _grade;
	}
	
	public String getGrade()
	{
		return this.grade;
	}

	public void setReverseGrade(String _reverse_grade)
	{
		this.reverse_grade = _reverse_grade;
	}
	
	public String getReverseGrade()
	{
		return this.reverse_grade;
	}
	
	public void setAddAnswer(String _add_answer)
	{
		this.add_answer = _add_answer;
	}
	
	public String getAddAnswer()
	{
		return this.add_answer;
	}
}
