package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;

import java.io.File;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import junit.framework.TestCase;

import org.catechis.admin.FileUserUtilities;
import org.catechis.constants.Constants;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.dto.TestStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordNextTestDate;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileCategories;
import org.catechis.file.WordNextTestDates;
import org.catechis.filter.ImportListFilter;
import org.catechis.Transformer;

public class NewUserExperienceTest extends TestCase
{
	
    public NewUserExperienceTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
    }
    
    public void testParseIEvsFirefox()
    {
    	System.out.println("INewUserExperienceTest.testParseIEvsFirefox");
	    String word_separator = new String("=");
	    //String line_separator = new String("\000D");
	    String line_separator = new String(";");
	    ImportListFilter ilf = new ImportListFilter();
	    // ie
	    String ie = new String("È­º¸"+word_separator+"pictoral spread"+line_separator
	    +"°è´Ü"+word_separator+"stairs"+line_separator
	    +"Çâ³ª¹«"+word_separator+"juniper tree"+line_separator
	    +"¸ñ·Ã"+word_separator+"magnolia"+line_separator
	    +"Á¶Á¾»ç"+word_separator+"pilot"+line_separator
	    +"±¤°ø¼­"+word_separator+"office"+line_separator
	    +"º¹Åë"+word_separator+"stomacheache"+line_separator
	    +"±Í¾ÎÀÌ"+word_separator+"ear ache"+line_separator
	    +"~¿¡¼­ ¶³¾îÁ®"+word_separator+"off"+line_separator
	    +"³à¼®"+word_separator+"boys & girls"+line_separator
	    +"Á¦¸Ú´ë·Î"+word_separator+"at will"+line_separator);
	    // firefox
	    String ff = new String("&#54868;&#48372;"+word_separator+"pictoral spread"+line_separator
	    +"&#44228;&#45800;"+word_separator+"stairs"+line_separator
	    +"&#54693;&#45208;&#47924;"+word_separator+"juniper tree"+line_separator
	    +"&#47785;&#47144;"+word_separator+"magnolia"+line_separator
	    +"&#51312;&#51333;&#49324;"+word_separator+"pilot"+line_separator
	    +"&#44305;&#44277;&#49436;"+word_separator+"office"+line_separator
	    +"&#48373;&#53685;"+word_separator+"stomacheache"+line_separator
	    +"&#44480;&#50515;&#51060;"+word_separator+"ear ache"+line_separator
	    +"~&#50640;&#49436; &#46504;&#50612;&#51256;"+word_separator+"off"+line_separator
	    +"&#45376;&#49437;"+word_separator+"boys & girls"+line_separator
	    +"&#51228;&#47691;&#45824;&#47196;"+word_separator+"at will, as one pleases"+line_separator
	    +"&#47960;&#54616;&#45716;&#44163;"+word_separator+"something"+line_separator
	    +"&#44032;&#46301;&#52268; &#49324;&#49373;&#54876;"+word_separator+"private life"+line_separator);
	    Hashtable ie_results = ilf.parse(ie, word_separator, line_separator);
	    System.out.println("ImportListFIlterTest.testParseIEvsFirefox --------- ie");
	    dumpHash(ie_results);
	    //dumpLog(ilf.getLog());
	    Hashtable ff_results = ilf.parse(ff, word_separator, line_separator);
	    System.out.println("NewUserExperienceTest.testParseIEvsFirefox --------- ff");
	    dumpHash(ff_results);
	    dumpLog(ilf.getLog());
	    int expected = 3;
	    int actual = ie_results.size();
	    assertEquals(expected, actual);
    }
    
 // 
    public void testFirstWord()
    {
    	// first add a new user
    	File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String user_name = "new";
		String password = "new";
		String e_mail = "new@hot.com";
		boolean test = fut.addNewUser(user_name, password, e_mail);
		System.out.println("NewUserExperienceTest.testFirstWord: "+test);
		fut = new FileUserUtilities(current_dir);
		Vector admins = new Vector();
		admins.add("admin");
		Vector actual_users = fut.getUsers(admins);
		if (actual_users.contains(user_name))
		{
			System.out.println("NewUserExperienceTest.testFirstWord: new user confirmed");
		}
		String user_id = fut.getId(user_name);
		FileStorage store = new FileStorage(current_dir);
		Hashtable user_opts = store.getUserOptions(user_id, current_dir);
		// add a new category file
	    String file_type = (".xml");
		FileCategories cats = new FileCategories(current_dir);
	    String new_category = "first_file"+file_type;
	    cats.addCategory(new_category, current_dir, user_name);
	    // add words to the cat
	    String word_separator = new String("=");
	    //String line_separator = new String("\000D");
	    String line_separator = new String(";");
	    ImportListFilter ilf = new ImportListFilter();
	    String ff = new String("A"+word_separator+"pictoral spread"+line_separator
	    	    +"B"+word_separator+"off"+line_separator
	    	    +"C"+word_separator+"boys & girls"+line_separator
	    	    +"D"+word_separator+"private life"+line_separator);
	    Hashtable words = ilf.parse(ff, word_separator, line_separator);
	    String subject = Constants.VOCAB;
	    String encoding = (String)user_opts.get("encoding");
	    UserInfo user_info = new UserInfo(encoding, current_dir, user_id, subject);
	    ilf.saveWords(0, words, new_category, user_info);
	    // get the first word to test
	    String time = Long.toString(new Date().getTime());
	    WordNextTestDates wntds = new WordNextTestDates();
		WordNextTestDate wntd = new WordNextTestDate();
		Word word = wntds.getNextTestWord(user_id, current_dir, time, subject);
		System.out.println("NewUserExperienceTest.testFirstWord: "+Domartin.getBriefElapsedTime(word.getDateOfEntry()+""));
		dumpLog(Transformer.createTable(word));
		String category = wntds.getWordCategory();
	    String action_id = wntds.getWordsNextTextDateFileName();// link to wntd file, same as next ???
		wntd = wntds.getWordNextTestDate();	// the name of the next test date file
	    String action_type = wntds.getNextTestType();
	    //dumpHash(results);
	    //dumpLog(ilf.getLog());
	    int expected = 3;
	    int actual = 0;
	    
	    // clean up
	    fut.deleteUser(user_name);
	    
	    assertEquals(expected, actual);
    }
    
    private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

    }
    
    private void dumpHash(Hashtable log)
    {
	    Enumeration keys = log.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" - "+val);
	    }
    }

}
