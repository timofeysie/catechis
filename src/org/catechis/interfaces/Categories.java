package org.catechis.interfaces;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllWordStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordLastTestDates;

public interface Categories
{
	Hashtable getSortedWordCategories(String user_name);
	Vector getWords(String file_name, String folder, String user_name);
	Vector getFilteredFiles(String user_name, String file_type, String folder);
	String getFirstDate(Word word);
	ArrayList<Object> getSortedKeys(Hashtable hash);
	void addCategory(String category, String root_path, String user_name);
}
