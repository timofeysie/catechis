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
import org.catechis.dto.WordTestRecordOptions;

public interface TestRecords
{
	void addWordTestMemory(WordTestMemory wtm, String user, String root_path);
	Vector getDailyTestRecords(String user_name, String type, String root_path, Hashtable user_opts);
	Hashtable getLastUserHistoryHash(String user_name, String current_dir);
	void addDailyTestRecord(WordTestResult wtr, WordTestRecordOptions wtro); 
	Hashtable getSpecificDailyTestRecords(WordTestRecordOptions wtro);
	void clearTestRecord(String type, String user_name, String root_path);
}
