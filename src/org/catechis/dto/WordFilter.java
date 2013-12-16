package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*<p>This object is an include/exclude filter object to hold properties to limit
* a list of words returned.
*<p>If properties are not null, then they should be filtered.
*/
public class WordFilter
{

	private String type;
	private String category;
	private int start_index;
	private String min_max_range;
	private String[] categories;
	private int number_of_words;
	private String exclude_time;
	
	// Unimplemented properties
	private String date_from_present;
	private String from_date;
	private String until_date;
	private String exclude_recently_failed_date;
	private String include_recently_passed_date;
	private String exclude_recently_tested_date;
	private boolean randomize_list;
	
	public void setType(String _type)
	{
		type = _type;
	}
	
	public String getType()
	{
		return type;
	}	
	
	public void setCategory(String _category)
	{
		category = _category;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setStartIndex(int _start_index)
	{
		start_index = _start_index;
	}
	
	public int getStartIndex()
	{
		return start_index;
	}
	
	public void setMinMaxRange(String _min_max_range)
	{
		this.min_max_range = _min_max_range;
	}
	
	public String getMinMaxRange()
	{
		return this.min_max_range;
	}
	
	//Methods to access the entire indexed property array
	public String[] getCategories()
	{
		return categories;
	}
	
	public void setCategories(String[] _categories)
	{
		categories = _categories;
	}

	//Methods to access individual values
	public String getCategories(int index)
	{
		return categories[index];
	}
	
	public void setCategories(int index, String _categories)
	{
		categories[index] = _categories;
	}
	
	public void setNumberOfWords(int _number_of_words)
	{
		number_of_words = _number_of_words;
	}
	
	public int getNumberOfWords()
	{
		return number_of_words;
	}
	
	public void setExcludeTime(String _exclude_time)
	{
		exclude_time = _exclude_time;
	}
	
	public String getExcludeTime()
	{
		return exclude_time;
	}

}
