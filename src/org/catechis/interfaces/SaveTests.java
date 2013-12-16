package org.catechis.interfaces;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import org.catechis.dto.Word;
import org.catechis.dto.UserInfo;
import org.catechis.dto.SavedTest;

/**
*An interface of method signatures for saving and retrieving words for off-line or teacher assisted testing.
*/
public interface SaveTests
{
	/**
	*Save a list of word objects (in a certain order) for testing off-line.
	*/
	void save(UserInfo user_info, Vector words, String test_id);
	
	/**
	 * Save test info for a whole class.
	 */
	void saveClassTest(String class_id, String test_id, String test_name,
			String creation_time, UserInfo user_info);
			
	/**
	*Add information about the saved test to a list of other saved tests.
	*/
	void addToSavedTestsList(UserInfo user_info, SavedTest saved_test, String str_id);
	
	/**
	*Retrieve a list of words that has been saved for off-line testing.
	*/
	Hashtable load(UserInfo user_info, String test_id);
	

	/**
	 * Load infor for a test shared by a class.
	 * @param teacher_id
	 * @param path
	 * @return a Vector of SavedTest objects which only have the above members filled in.
	
	 */
	Vector loadClassTestList(UserInfo user_info);

	/**
	*Get the list of information objects of all saved tests,
	*/
	Vector getSavedTestsList(UserInfo user_info);
	
	/**
	*Go through each test word result, load the word and update its level and other
	* status files based on the result of pass or fail.  Return a Vector of test
	* score objects to notify the user
	*/
	Vector scoreSavedTests(UserInfo user_info, SavedTest saved_test, 
			Vector test_words, Vector test_word_results);
	
	/**
	 * Save a list of words so that they can be tested off line, and scored later.
	 * @param name of the saved test.
	 * @param type can be either reading, writing or reading_and_writing.
	 * @param words to be saved  if the test type is both reading and writing, 
	 * the particular test type for each word is contained in a blank test. 
	 * @param user_id
	 */
	String saveWordList(String name, String type, Vector words, UserInfo user_info);
	
	/*
	* Other possible methods:
	* update status
	* filter test info lists by status.
	* delete tests
	* look at a list of test results for saved tests
	*/
	
	/**
	*Homegrown debugging may be more expensive, but it has more local flavor!
	*/
	Vector getLog();
}
