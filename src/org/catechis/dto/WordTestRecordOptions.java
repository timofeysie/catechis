package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import java.util.Hashtable;

public class WordTestRecordOptions
{
	private String type;
	private String user_name;
	private String root_path;
	private boolean record_failed_tests;
	private boolean record_passed_tests;
	private String record_exclude_level;
	private int record_limit;
	private long word_id;
	
	/**
	*<p>This is a helper bean to pass options to TestRecords so that unwanted tests
	* don't have to be saved, or retrieved.
	*<p>Members such as type, user_name, and root_path are added just so that the
	* calling methods don't have to have an endless string of arguments.
	*/
	public WordTestRecordOptions() {}
	
	/**
	*This convenient constructor takes care of populating the wtro from the user options hash.
	* The original version that didnt have ids.
	*/
	public WordTestRecordOptions(String type, String user_name, String root_path, 
		Hashtable user_opts)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_name);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	}
	
	/**
	*This convenient constructor takes care of populating the wtro from the user options hash.
	*/
	public WordTestRecordOptions(String type, String user_name, String root_path, 
		Hashtable user_opts, long id)
	{
	    boolean record_failed_tests = Boolean.valueOf((String)user_opts.get("record_failed_tests")).booleanValue();
	    boolean record_passed_tests = Boolean.valueOf((String)user_opts.get("record_passed_tests")).booleanValue();
	    String record_exclude_level = (String)user_opts.get("record_exclude_level");
	    int record_limit = Integer.parseInt((String)user_opts.get("record_limit"));
	    WordTestRecordOptions wtro = new WordTestRecordOptions();
	    wtro.setType(type);
	    wtro.setUserName(user_name);
	    wtro.setRootPath(root_path);
	    wtro.setRecordFailedTests(record_failed_tests);
	    wtro.setRecordPassedTests(record_passed_tests);
	    wtro.setRecordExcludeLevel(record_exclude_level);
	    wtro.setRecordLimit(record_limit);
	    wtro.setWordId(id);
	}
	
	public void setType(String _type)
	{
		this.type = _type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public void setUserName(String _user_name)
	{
		this.user_name = _user_name;
	}
	
	public String getUserName()
	{
		return this.user_name;
	}

	public void setRootPath(String _root_path)
	{
		this.root_path = _root_path;
	}
	
	public String getRootPath()
	{
		return this.root_path;
	}

	public void setRecordFailedTests(boolean _record_failed_tests)
	{
		this.record_failed_tests = _record_failed_tests;
	}
	
	public boolean getRecordFailedTests()
	{
		return this.record_failed_tests;
	}
	
	public void setRecordPassedTests(boolean _record_passed_tests)
	{
		this.record_passed_tests = _record_passed_tests;
	}
	
	public boolean getRecordPassedTests()
	{
		return record_passed_tests;
	}	
	
	public void setRecordLimit(int _record_limit)
	{
		this.record_limit = _record_limit;
	}
	
	public int getRecordLimit()
	{
		return record_limit;
	}
	
	public void setRecordExcludeLevel(String _record_exclude_level)
	{
		this.record_exclude_level = _record_exclude_level;
	}
	
	public String getRecordExcludeLevel()
	{
		return this.record_exclude_level;
	}
		
	public void setWordId(long _id)
	{
		this.word_id = _id;
	}
	
	public long getWordId()
	{
		return this.word_id;
	}
}
