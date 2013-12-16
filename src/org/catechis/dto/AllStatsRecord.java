package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;

/**
*<p>This bean stores the statistics gathered in the AllWordStats and AllTestStats
* objects.  Based on AllStatsHistory, it now contains everything in the status.record file
* in user_id/subject/status.record file.
<p> Here are the following statistics collected:
<p>
<p>AllTestStats properties
<p>Test  		Total  	Average
<p>All Tests 	182 	49.7
<p>
<p>AllWordStats properties
<p>Category  	total  	R-Avg.  	W-Avg. 
<p>All 		913 	0.81 		1.2
<p>R-0  W-0  	R-1  	W-1  	R-2  	W-2  	R-3  	W-3  
<p>317 	201 	474 	485 	99 	137 	22 	57 
<p>Status.record file:
<status>
	<daily_test_index>0:0</daily_test_index> * not used
	<daily_test_file>0</daily_test_file> * not used
	
	<tests>31200</tests>
	<passed_tests>0</passed_tests>
	<failed_tests>0</failed_tests>
	<reversed_tests>0</reversed_tests>
	<action_name>IntegratedTestResultAction</action_name>
	<action_time>1316941452425</action_time>
	<action_id>IntegratedTestResultAction</action_id>
	<action_type>default</action_type>
	<daily_session_tests>20</daily_session_tests>
	<daily_session_start>0</daily_session_start>
</status>

*/
public class AllStatsRecord
{

	private int tests;
	private int passed_tests;
	private int failed_tests;
	private int reversed_tests;
	private String action_name;
	private String action_time;
	private String action_id;
	private String action_type;
	
	private long session_start;
	private long session_end;
	private int number_of_session_tests;
	
	private int words_at_reading_level_0;
	private int words_at_reading_level_1;
	private int words_at_reading_level_2;
	private int words_at_reading_level_3;
	private int words_at_writing_level_0;
	private int words_at_writing_level_1;
	private int words_at_writing_level_2;
	private int words_at_writing_level_3;
	

	public void setTests(int _tests)
	{
		tests = _tests;
	}

	public int getTests()
	{
		return tests;
	}

	public void setPassedTests(int _passed_tests)
	{
		passed_tests = _passed_tests;
	}
	
	public int getPassedTests()
	{
		return passed_tests;
	}
	
	public void setFailedTests(int _failed_tests)
	{
		failed_tests = _failed_tests;
	}
	
	public int getFailedTests()
	{
		return failed_tests;
	}
	
	public void setReversedTests(int _reversed_tests)
	{
		reversed_tests = _reversed_tests;
	}
	
	public int getReversedTests()
	{
		return reversed_tests;
	}

	public void setActionName(String _action_name)
	{
		action_name = _action_name;
	}
	public String getActionName()
	{
		return action_name;
	}
	
	public void setActionId(String _action_id)
	{
		action_id = _action_id;
	}
	public String getActionId()
	{
		return action_id;
	}
	
	public void setActionType(String _action_type)
	{
		action_type = _action_type;
	}
	
	public String getActionType()
	{
		return action_type;
	}
	
	public void setActionTime(String _action_time)
	{
		action_time = _action_time;
	}
	
	public String getActionTime()
	{
		return action_time;
	}
	
	public void setSessionStart(long _session_start)
	{
		session_start = _session_start;
	}
	
	public long getSessionStart()
	{
		return  session_start;
	}
	
	public void setSessionEnd(long _session_end)
	{
		session_end = _session_end;
	}
	
	public long getSessionEnd()
	{
		return  session_end;
	}
	
	public void setNumberOfSessionTests(int _number_of_session_tests)
	{
		number_of_session_tests = _number_of_session_tests;
	}
	
	public int getNumberOfSessionTests()
	{
		return number_of_session_tests;
	}
	
	public void setWordsAtReadingLevel0(int _words_at_reading_level_0)
	{
		words_at_reading_level_0 = _words_at_reading_level_0;
	}
	
	public int getWordsAtReadingLevel0()
	{
		return words_at_reading_level_0;
	}
	
	public void setWordsAtReadingLevel1(int _words_at_reading_level_1)
	{
		words_at_reading_level_1 = _words_at_reading_level_1;
	}
	
	public int getWordsAtReadingLevel1()
	{
		return words_at_reading_level_1;
	}
	
	public void setWordsAtReadingLevel2(int _words_at_reading_level_2)
	{
		words_at_reading_level_2 = _words_at_reading_level_2;
	}
	
	public int getWordsAtReadingLevel2()
	{
		return words_at_reading_level_2;
	}
	
	public void setWordsAtReadingLevel3(int _words_at_reading_level_3)
	{
		words_at_reading_level_3 = _words_at_reading_level_3;
	}
	
	public int getWordsAtReadingLevel3()
	{
		return words_at_reading_level_3;
	}
	
	public void setWordsAtWritingLevel0(int _words_at_writing_level_0)
	{
		words_at_writing_level_0 = _words_at_writing_level_0;
	}
	
	public int getWordsAtWritingLevel0()
	{
		return words_at_writing_level_0;
	}
	
	public void setWordsAtWritingLevel1(int _words_at_writing_level_1)
	{
		words_at_writing_level_1 = _words_at_writing_level_1;
	}
	
	public int getWordsAtWritingLevel1()
	{
		return words_at_writing_level_1;
	}
	
	public void setWordsAtWritingLevel2(int _words_at_writing_level_2)
	{
		words_at_writing_level_2 = _words_at_writing_level_2;
	}
	
	public int getWordsAtWritingLevel2()
	{
		return words_at_writing_level_2;
	}
	
	public void setWordsAtWritingLevel3(int _words_at_writing_level_3)
	{
		words_at_writing_level_3 = _words_at_writing_level_3;
	}
	
	public int getWordsAtWritingLevel3()
	{
		return words_at_writing_level_3;
	}
	
	
	
}
