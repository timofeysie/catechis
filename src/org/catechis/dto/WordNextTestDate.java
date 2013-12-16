package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

/**
*This class represents a file in /category/lists/wntd type/ folder that looks like this:
*<p><test>
*<p><word_id>-7983252723503331340</word_id>
*<p><category>random words 14.xml</category>
*<p><wltd_file_key>1224386366000</wltd_file_key>
*<p></test>
*<p>
*/
public class WordNextTestDate
{
	private String wprd_id;
	private String category;
	/**
	*Although this is the next test object, we store the name of the last test name for easy retrieval/
	*/
	private String wltd_file_key;
	
	public WordNextTestDate()
	{
		// Yer usual average no-args constructor
	}
	
	public void setWordId(String _wprd_id)
	{
		this.wprd_id = _wprd_id;
	}
	
	public String getWordId()
	{
		return this.wprd_id;
	}
	
	public void setCategory(String _category)
	{
		this.category = _category;
	}
	
	public String getCategory()
	{
		return this.category;
	}

	public void setWltdFileKey(String _wltd_file_key)
	{
		this.wltd_file_key = _wltd_file_key;
	}
	
	public String getWltdFileKey()
	{
		return this.wltd_file_key;
	}
}
