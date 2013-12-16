package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordLastTestDates;

public class MockStorage implements Storage
{

	public Hashtable findUsers()
	{
		return null;
	}
	
	public Vector getUserLists()
	{
		return null;
	}
	
	public String getRole()
	{
		return new String();
	}
	public String getType(String content_name)
	{
		return new String();
	}
	public Hashtable getWordsDefs(String content_name, String user_name)
	{
		return new Hashtable();
	}
	public Hashtable getTests(String content_name, String user_name)
	{
		return new Hashtable();
	}
	public Vector getWordObjects(String content_name, String user_name)
	{
		return new Vector();
	}
	
	public Vector getWordObjectsForTest(String content_name, String user_name)
	{
		return new Vector();
	}
	
	public Word getWordObject(String search_property, String search_value, String category, String user_name)
	{
		return new Word();
	}
	
	public Vector getTestCategories(String user_name)
	{
		return new Vector();
	}
	
	public Vector getCategories(String user_name)
	{
		return new Vector();
	}
	
	public Vector getWordCategories(String content_name, String user_name)
	{
		return new Vector();
	}
	
	public Vector getFilteredWordObjects(WordFilter word_filter, String user_name)
	{
		return new Vector();
	}
	
	public AllTestStats getTestStats(Vector tests, String user_name)
	{
		return new AllTestStats();
	}
	
	public AllWordStats getWordStats(Vector words, String user_name)
	{
		return new AllWordStats();
	}
	
	public Vector getAllStatsHistory(String user_name)
	{
		return new Vector();
	}
	
	public long updateHistory(AllTestStats all_test_stats, AllWordStats all_word_stats, String user_name)
	{
		return Long.parseLong("0");
	}
	public void addAllStatsHistory(AllStatsHistory all_stats_history, String user_name)
	{
	}
	
	public void login(String user)
	{}
	
	public void loggit(String msg)
	{}
	
	public void setWords(Vector words)
	{}
	
	public void setTests(Vector tests)
	{}
	
	public void setOther(Vector other)
	{
	}
	public void updateWord(WordTestMemory wtm, WordTestResult wtr, String user_name, String encoding)
	{
	}
	public String updateWordLevel(WordTestMemory wtm, WordTestResult wtr, String user_name, String encoding)
	{
		return new String();
	}
	public void updateTest(WordTestMemory wtm, String new_score, String user_name)
	{
	}
	public Word getWordWithoutTests(String text, String category, String user_name)
	{
		return new Word();
	}
	public void addWord(Word word, String category, String user_name, String encoding)
	{
	}
	
	public void deleteWord(Word word, String category, String user_name, String encoding)
	{}
	
	public void resetLog()
	{}
	
	public Vector getLog()
	{
		return new Vector();
	}
	
	public void retireWord(Word word, String category, String user_name, String encoding)
	{}
	
	public Vector getNoRepeatsNewWordsList(String type, Vector previous_list, String user_name)
	{
		return new Vector();
	}
	
	public void editWord(Word old_word, Word new_word, String category, String user_name, String encoding)
	{}
	
	
	public ArrayList getWordLastTestDatesList(WordLastTestDates wltd, String user_name, String subject)
	{
		return new ArrayList();
	}
	
	public ArrayList getAdjustedWordLastTestDatesList(WordLastTestDates wltd, String user_name)
	{
		return new ArrayList();
	}
	
	
	public String getPathToFiles()
	{
		return new String();
	}
	
	public WordTestResult scoreSingleWordTest(AllWordsTest awt, String user_name)
	{
		return new WordTestResult();
	}
	
	public String recordWordTestScore(WordTestResult wtr, AllWordsTest awt, String max_level, String test_name, String user_name)
	{
		return new String();
	}
	
	public Hashtable getUserOptions(String user_name, String context_path)
	{
		return new Hashtable();
	}
	
	public Hashtable getTeacherOptions(String teacher_id, String context_path)
	{
		return new Hashtable();
	}
	
	public Vector getNewWordsList(String type, String user_name)
	{
		return new Vector();
	}
	
	public Vector getNewWordsList(String type, String user_name, String subject)
	{
		return new Vector();
	}
	
	public void saveWTDKeys(String wntd_file_key_r, String wntd_file_key_w, long word_id) // so a word can be deleted later
	{
		
	}
	
	public void writeJDOMDocument(String file_name)
	{
		
	}

	@Override
	public void saveSessionHistory(AllTestStats all_test_stats,
			AllWordStats all_word_stats, String user_id) {
		// TODO Auto-generated method stub
		
	}
}
