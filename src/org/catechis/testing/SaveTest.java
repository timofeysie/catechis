package org.catechis.testing;

import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import org.catechis.dto.Word;
import org.catechis.dto.UserInfo;
import org.catechis.dto.SavedTest;
import org.catechis.dto.WordLastTestDates;
import org.catechis.constants.Constants;
import org.catechis.interfaces.SaveTests;
import org.catechis.Domartin;

/**
*This class provides methods for binding and unbinding different types of tests.
*/
public class SaveTest
{

	private Vector log;
	SaveTests save_tests;
	
	/**
	*
	*/
	public SaveTest()
	{
		log = new Vector();
	}
	
	/**
	*The SaveTests argument is an interface that can be instantiated in a FileSaveTests object
	*That will save tests into an xml file.
	*/
	public SaveTest(SaveTests _st)
	{
		this.save_tests = _st;
		log = new Vector();
	}
	
	/**
	*Retrieve a list of Word objects from a WordLastTestDates object
	* using a ArrayList of keys and save them in an xml file 
	* in the user/subject/tests/saved folder with the name as it's
	* id referenced from the tests/saved tests.xml file with a
	* root <daily_test> and the following format:
	*<test_words>
	*	<test_word>
	*		<index></index>
	*		<word_id></word_id>
	*		<category></category>
	*		<text></text>			so that we dont have to load every word
	*		<definition></definition>	when unbinding a test. 
	*	</test_word>
	*	...
	*</test_words>
	* This method will construct a new id and a date and then add it to the
	* saved test registry in user/subject/tests/saved tests.xml
	*/
	public void fromWLTDsToFile(UserInfo user_info, WordLastTestDates wltd, ArrayList key_list, SavedTest saved_test)
	{
		Vector words = new Vector();
		int size = key_list.size();
		int i = 0;
		while (i<size)
		{
			Long actual_key = (Long)key_list.get(i);
			String string_key = actual_key.toString();
			Word word = wltd.getWord(string_key);
			words.add(word);
			i++;
		}
		long id = Domartin.getNewID();
		String str_id = Long.toString(id);
		save_tests.save(user_info, words, str_id);
		log.add("SaveTest.fromWLTDsToFile: path "+user_info.getRootPath());
		log.add("SaveTest.fromWLTDsToFile SaveTests log === ");
		createNameEntry(user_info, saved_test, str_id);
		append(save_tests.getLog());
		// anything else?
	}
	
	/**
	 * Save a list of words.
	 * 
	 * <daily_test>
		<test_id></test_id>
		<test_name></test_name>
		<test_type></test_type>
		<creation_time></creation_time>
		<test_item>
		<index></index>
		<id></id>
		<category></category>
		</test_item>
		</daily_test>
	 */
	public void saveWordList(String name, String type, Vector words)
	{
		//Transformer.
		long test_id = Domartin.getNewID();
		String creating_time = Long.toString(new Date().getTime());
		
	}
	
	
	/**
	* The saved subj/tests/saved tests.xml is just an id and name value pairs.
	* <saved_tests>
	*	<saved_test>
	*		<test_id></test_id>
	*		<test_name></test_name>
	*		<test_date></test_date>
	*		<test_format></test_format>
	*		<creation_time></creation_time>
	*	</saved_test>
	*/
	public void createNameEntry(UserInfo user_info, SavedTest saved_test, String str_id)
	{
		save_tests.addToSavedTestsList(user_info, saved_test, str_id);
	}
	
	public Vector loadSavedTests(UserInfo user_info)
	{
		Vector saved_tests = save_tests.getSavedTestsList(user_info);
		return saved_tests;
	}
	
	/** deguggin */
	public Vector getLog()
	{
		return log;
	}
	
	public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add(v.get(i));
			i++;
		}
	}
	
}
