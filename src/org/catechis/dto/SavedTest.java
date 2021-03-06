package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

/**
* THis class is used to transfer data to and from the following xml format.
	*		<test_id></test_id>
	*		<test_name></test_name>
	*		<test_date></test_date>
	*		<test_type></test_type>
	*		<test_format></test_format>
	*		<creation_time></creation_time>
*/
public class SavedTest
{
	private String test_id;
	private String test_date;
	private String test_name;
	private String test_type;
	private String test_status;
	private String test_format;
	private String creation_time;
	private String score_time;
	private String test_score;
	private String number_of_words;

	// ----------------------------------- id
	public void setTestId(String _test_id)
	{
		this.test_id = _test_id;
	}
	
	public String getTestId()
	{
		return test_id;
	}
	
	// ----------------------------------- date
	public void setTestDate(String _test_date)
	{
		this.test_date = _test_date;
	}
	
	public String getTestDate()
	{
		return test_date;
	}	
	
	// ----------------------------------- name
	public void setTestName(String _test_name)
	{
		this.test_name = _test_name;
	}
	
	public String getTestName()
	{
		return test_name;
	}
	
	// ----------------------------------- type
	public void setTestType(String _test_type)
	{
		this.test_type = _test_type;
	}
	
	public String getTestType()
	{
		return test_type;
	}

	// ----------------------------------- status
	public void setTestStatus(String _test_status)
	{
		this.test_status = _test_status;
	}
	
	public String getTestStatus()
	{
		return test_status;
	}
	
	// ----------------------------------- format
	public void setTestFormat(String _test_format)
	{
		this.test_format = _test_format;
	}
	
	public String getTestFormat()
	{
		return test_format;
	}
	
	// ----------------------------------- creation time
	public void setCreationTime(String _creation_time)
	{
		this.creation_time = _creation_time;
	}
	
	public String getCreationTime()
	{
		return creation_time;
	}
	
	//------------------------------------ score time
	public String getScoreTime() 
	{
		return score_time;
	}

	public void setScoreTime(String _score_time) 
	{
		this.score_time = _score_time;
	}
	
	//------------------------------------ test_score
	public String getTestScore() 
	{
		return test_score;
	}

	public void setTestScore(String _test_score) 
	{
		this.test_score = _test_score;
	}
	
	//------------------------------------ number_of_words
	public String getNumberOfWords() 
	{
		return number_of_words;
	}

	public void setNumberOfWords(String _number_of_words) 
	{
		this.number_of_words = _number_of_words;
	}
	
}
