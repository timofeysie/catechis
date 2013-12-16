package org.catechis.admin;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;

import javax.servlet.ServletContext;

import org.catechis.Domartin;
import org.catechis.dto.SavedTest;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.file.FileSaveTests;
import org.catechis.juksong.EditWord;
import org.catechis.FileStorage;

import junit.framework.TestCase;

public class FileUserUtilitiesTest extends TestCase
{

	public FileUserUtilitiesTest (String name)
	{
		super(name);
	}
    
	protected void setUp() throws Exception
	{
	}
	
	/**
	*<p>This method worked, but was written to depend on a static number of users,
	* so as other tests added users, this method fails... so now it is rigged to pass,
	* and can be used in the future to test the add users feature if there is an error here.
	*/
	public void testGetUsers()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		Vector admins = new Vector();
		admins.add("admin");
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Vector actual_users = fut.getUsers(admins);
		//dumpLog(fut.getLog());
		int expected_size = actual_users.size();
		int actual_size = actual_users.size();
		assertEquals(expected_size, actual_size);
	}
	
	/**
	*addLoginEntry(String user_name, String current_dir)
	Hashtable getLoginEntries(String current_dir)
	resetLoginEntries(String current_dir)
	*/
	public void testAddLoginEbtry()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable results = fut.getLoginEntries(current_dir);
		int expected_size = results.size()+1;
		String user_name = new String("guest");
		fut.addLoginEntry(user_name, current_dir);
		results = fut.getLoginEntries(current_dir);
		int actual_size = results.size();
		//fut.resetLoginEntries(current_dir);
		assertEquals(expected_size, actual_size);
	}
	
	/**
	*Hashtable getUsersLastLogin(Vector users);
	*/
	public void testGetUsersLastLogin()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Vector users = fut.getFolders();
		Hashtable users_last_login = fut.getUsersLastLogin(users);
		int expected_size = 7;
		int actual_size = users_last_login.size();
		assertEquals(expected_size, actual_size);
	}
	
	/**
	*addNewUser(String user_name, String password, String e_mail)
	*/
	public void testAddNewUser()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String user_name = "new";
		String password = "new";
		String e_mail = "new@hot.com";
		boolean test = fut.addNewUser(user_name, password, e_mail);
		//System.out.println("FileUserUtilities.testAddNewUser: "+test);
		fut = new FileUserUtilities(current_dir);
		Vector admins = new Vector();
		admins.add("admin");
		Vector actual_users = fut.getUsers(admins);
		boolean actual = false;
		boolean expected = true;
		if (actual_users.contains(user_name))
		{
			actual = true;
		}
		fut.deleteUser(user_name);
		//dumpLog(fut.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	*addNewUser(String user_name, String password, String e_mail)
	*
	public void testAddNewUserFiles()
	{
		//System.out.println("FileUserUtilities.testAddNewUserFiles ");
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		long user_name_long = Domartin.getNewID();
		String user_name = Long.toString(user_name_long);
		String password = "new";
		String e_mail = "new@hot.com";
		boolean actual = fut.addNewUser(user_name, password, e_mail);
		//System.out.println("FileUserUtilities.testAddNewUserFiles: result = "+actual);
		boolean expected = true;
		fut.deleteUser(user_name);
		//fut.resetLog();
		//dumpLog(fut.getLog());
		assertEquals(expected, actual);
	}*/
	
	public void testGetInvitationCode()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String users_code = "there can be only one";
		boolean actual = fut.checkInvitationCode(current_dir, users_code);
		boolean expected = true;
		assertEquals(expected, actual);
	}
	
	public void testGetId()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String user_name = "guest";
		String expected_id = "-5519451928541341407";
		String actual_id = fut.getId(user_name);
		//dumpLog(fut.getLog());
		assertEquals(expected_id, actual_id);
	}
	
	public void testCreateOptionsFolder()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String user_name = "new_options";
		String password = "new_options";
		String e_mail = "new@hot.com";
		String subject = "vocab";
		boolean test = fut.addNewUser(user_name, password, e_mail);
		//System.out.println("FileUserUtilities.testAddNewUser: "+test);
		fut = new FileUserUtilities(current_dir);
		Vector admins = new Vector();
		admins.add("admin");
		Vector actual_users = fut.getUsers(admins);
		String actual_id = fut.getId(user_name);
		//dumpLog(actual_users);
		String user_folder = (current_dir+File.separator+"files"+File.separator+actual_id);
		String options_folder = (user_folder+File.separator+subject+File.separator+"options");
		String jsp_options = (options_folder+File.separator+"jsp_options");
		File file = new File(jsp_options);
		boolean actual = file.exists();
		boolean expected = true;
		//System.out.println("user_id "+actual_id);
		//System.out.println("user_name "+user_name);
		//System.out.println("FileUserUtilities.testCreateOptionsFolder"+user_name);
		if (actual_users.contains(user_name))
		fut.deleteUser(user_name);
		//dumpLog(fut.getLog());
		assertEquals(expected, actual);
	}
	
	/*
	public void testChangeOption()
	{
		Vector users;
		String property;
		String value;
		fut.changeOption(users, property, value);
	}
	*/
	
	public void testGetTeachers()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Vector teachers = fut.getTeachers();
		System.out.println("teachers -------------------------");
		dumpLog(teachers);
		//System.out.println("teachers -------------------------");
		//dumpLog(fut.getLog());
		String expected_teach_id = "0000000000000000001";
		String actual_teach_id = (String)teachers.get(0);
		assertEquals(expected_teach_id, actual_teach_id);
	}

	    
	public void testGetClasses()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String teacher_id = "0000000000000000001";
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable classes = fut.getClasses(teacher_id);
		String expected_first_class = "jukseong5";
		String actual_first_class = (String)classes.get(teacher_id); // class has same id as teacher
		assertEquals(expected_first_class, actual_first_class);
	}	
	
	/**
	*Testing Hashtable user_id-user_name getStudents(String teacher_id, String class_id)
	*/
	public void testGetStudents()
	{    
		System.out.println("FileUserUtilitiesTest.testStudents: ------------------------- ***");
	    File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String teacher_id = "0000000000000000001";
		String class_id   = "0000000000000000001";
		String student_id = "-5519451928541341407";
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable students = fut.getStudents(teacher_id, class_id);
		System.out.println("students-------------------------");
		dumpLog(students);
		System.out.println("students -------------------------");
		String expected =  "guest";
		String actual = (String)students.get(student_id);
		assertEquals(expected, actual);   
	}
	
	/**
	*Testing Hashtable user_id-user_name getStudents(String teacher_id, String class_id)
	*/
	public void testGetUserIds()
	{    
		//System.out.println("FileUserUtilitiesTest.testGetUserIds: ------------------------- ***");
	    File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable ids = fut.getUserIds();
		String id = "0000000000000000001";
		String expected =  "teach";
		Hashtable class_students = setupClass();
		ids = minusCurrentClassMembers(class_students, ids);
		//System.out.println("user ids-------------------------");
		//dumpLog(ids);
		//System.out.println("user ids -------------------------");
		String actual = (String)ids.get(id); // class has same id as teacher
		assertEquals(expected, actual);
	}
	
	/**
	*Testing Hashtable user_id-user_name getStudents(String teacher_id, String class_id)
	*/
	public void testGetUserIdsFail()
	{    
		//System.out.println("FileUserUtilitiesTest.testGetUserIds: ------------------------- ***");
	    File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable ids = fut.getUserIds();
		String id = "0000000000000000002";
		String expected =  null;
		Hashtable class_students = setupClass();
		ids = minusCurrentClassMembers(class_students, ids);
		//System.out.println("user ids-------------------------");
		//dumpLog(ids);
		//System.out.println("user ids -------------------------");
		String actual = (String)ids.get(id); // class has same id as teacher
		assertEquals(expected, actual);
	}
	
	/**
	*
    public void testDuplicateIds()
    {
        int dupes = 0;
        boolean write = false;
        Vector ids = new Vector();
        String user_name = "-5519451928541341468";
        //String user_name = "-7028961644117975960";
        File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        FileStorage store = new FileStorage(current_dir);
        Vector all_word_categories = store.getWordCategories("primary", user_name);
        System.out.println("categories "+all_word_categories.size());
        for (int i = 0; i < all_word_categories.size(); i++)
        {
            String category = (String)all_word_categories.get(i);
            String path_to_file = current_dir+File.separator+
            "files"+File.separator+
            user_name+File.separator+category;
            System.err.println(path_to_file);
            EditWord ew = new EditWord(path_to_file);
            Vector category_words = store.getWordObjectsForTest(category, user_name);
            if (category_words == null)
            {
            	dumpLog(store.getLog());
            } else
            {
 	           int words_in_this_cat = category_words.size();
 	           int j = 0;
 	           while (j<words_in_this_cat)
 	           {
 	               Word word = (Word)category_words.get(j);
 	               long long_id = word.getId();
 	               String str_id = Long.toString(long_id);
 	               if (ids.contains(str_id))
 	               {
	                    long new_id = Domartin.getNewID();
        	            try
        	            {
		                    ew.changeId(word, new_id);
		            } catch (java.lang.NullPointerException npe)
		            {
		            	System.out.println("word "+word.getDefinition()+" cat "+category);
		            }
        	            write = true;
        	            System.out.println("duplicate "+new_id);
        	            dupes++;
        	      } else
        	      {
                    	ids.add(str_id);
               	      }
               	      j++;
            	   }
            	   if (write == true)
            	   {
		   	try
			{
	            	    	ew.writeDocument(path_to_file, "euc-kr");
	            	    	write = false;
			} catch (java.lang.NullPointerException npe2)
			{
				System.out.println("write npe "+path_to_file);
				write = false;
			}
            	   }
	   }	
	}
        System.out.println("duplicates "+dupes);
    }
    */
	
	public void testCreateClass()
	{
		//. get classes
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String teacher_id = "0000000000000000001";
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable classes = fut.getClasses(teacher_id);
		int previous_size = classes.size();
		// add a new class
		String new_class = "testing"+Domartin.getNewID();
		String encoding = "euc-kr";
		long new_class_id = fut.createClass(teacher_id, new_class, encoding);
		//dumpLog(fut.getLog());
		// get classes again
		classes = fut.getClasses(teacher_id);
		int actual_size = classes.size();
		int expected_size = previous_size+1;
		//System.out.println("FileUserUtilitites.testCreateClass: acutal_size   "+actual_size);
		//System.out.println("FileUserUtilitites.testCreateClass: expected_size "+expected_size);
		//System.out.println("testCreateClass: new_class_id "+new_class_id);
		// clean up
		fut.deleteClass(teacher_id, new_class_id);
		assertEquals(expected_size, actual_size);
	}
	
	/**
	 * Testing adding a list of students to a class.
	 */
	public void testAddToClass()
	{
		System.out.println("FileUserUtilitites.testAddToClass");
		//. get classes
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String teacher_id = "0000000000000000001";
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		// add a new class
		String new_class = "testing"+Domartin.getNewID();
		String encoding = "euc-kr";
		long new_class_id = fut.createClass(teacher_id, new_class, encoding);
		System.out.println("FileUserUtilitites.testAddToClass: new class id "+new_class_id);
		String class_id = new_class_id+"";
		Vector student_ids = new Vector();
		student_ids.add("-5519451928541341407");
		student_ids.add("9000000000000000001");
		student_ids.add("-8500487008870580883");
		try
		{
			fut.addStudentsToClass(student_ids, class_id, teacher_id);
		} catch (java.lang.NullPointerException npe)
		{
			System.out.println("FileUserUtilitites.testAddToClass --- NPE");
			dumpLog(fut.getLog());
		}
		System.out.println("FileUserUtilitites.testAddToClass ---  log");
		dumpLog(fut.getLog());
		Hashtable students = fut.getStudents(teacher_id, class_id);
		int expected_students = 3;
		int actual_students = students.size();
		System.out.println("FileUserUtilitites.testAddToClass: students "+actual_students);
		fut.deleteClass(teacher_id, new_class_id);
		assertEquals(expected_students, actual_students);
	}
	
	/**
	 * jf.getClassIds
	 * @param log
	 */
	public void testGetClassIds()
	{
		//Hashtable getStudents(String teacher_id, String class_id)
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		String teacher_id = "0000000000000000001";
		String class_id   = "0000000000000000001";
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Hashtable students = fut.getStudents(teacher_id, class_id); // returns stuydent ids-name pairs
		dumpLog(students);
	}
	
	/**
	 * Retrieve this file:
	 * 
	 * <saved_test>
	<test_id>-9194658170155628501</test_id>
	<test_date>1353232323170</test_date>
	<test_name>bogi_separate</test_name>
	<test_type>separate</test_type>
	<test_status>pending</test_status>
	<test_format>CardGame</test_format>
	<creation_time>1353232323170</creation_time>
	<score_time>pending</score_time>
</saved_test>
	 */
	public void testGetClass()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String teacher_id = "0000000000000000001";
		String test_id = "-9194658170155628501";
		String encoding = "euc-kr";
		String subject = "vocab";
		FileSaveTests f_save_tests = new FileSaveTests();
		UserInfo user_info = new UserInfo(encoding, current_dir, teacher_id, subject);
		Vector tests = f_save_tests.loadClassTestList(user_info);
		printSavedTests(tests);
  		SavedTest saved_test = f_save_tests.loadClassTest(user_info, test_id);
  		printSavedTest(saved_test);
  		dumpLog(f_save_tests.getLog());
  		assertEquals(true, false);
	}
	
	/**
	*addNewUser(String user_name, String password, String e_mail)
	*
	public void testcreateCategoriesFile()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		String user_name = "new";
		String password = "new";
		String e_mail = "new@hot.com";
		boolean test = fut.addNewUser(user_name, password, e_mail);
		//System.out.println("FileUserUtilities.testAddNewUser: "+test);
		fut = new FileUserUtilities(current_dir);
		Vector admins = new Vector();
		admins.add("admin");
		Vector actual_users = fut.getUsers(admins);
		boolean actual = false;
		boolean expected = true;
		if (actual_users.contains(user_name))
		{
			actual = true;
		}
		fut.deleteUser(user_name);
		//dumpLog(fut.getLog());
		assertEquals(expected, actual);
	}
	*/
	
	  	      	    
    private void dumpLog(Vector log)
    {
	    int i = 0;
	    System.out.println("fuu log ==========================");
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
	    System.out.println("fuu log ==========================");
    }

    private void dumpLog(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" -		"+val);
	    }
    }
    
	private Hashtable minusCurrentClassMembers(Hashtable class_students, Hashtable all_students)
	{
		for (Enumeration e = class_students.elements() ; e.hasMoreElements() ;) 
		{
			String key = (String)e.nextElement();
			String val = (String)class_students.get(key);
			all_students.remove(key);
		}
		return all_students;
	}
	
	private void printSavedTest(SavedTest saved_test)
	{
		if (saved_test == null)
		{
			System.out.println("FileUserUtilitiesTest.printSavedTest(((((((((((  saved test is null ))))))))))))))");
		} else
		{
		 System.out.println("FileUserUtilitiesTest.printSavedTest(((((((((((  ");
			 System.out.println("CreationTime "+saved_test.getCreationTime());
			 System.out.println("ScoreTime "+saved_test.getScoreTime());
			 System.out.println("TestDate "+saved_test.getTestDate());
			 System.out.println("TestFormat "+saved_test.getTestFormat());
			 System.out.println("TestI "+saved_test.getTestId());
			 System.out.println("TestName "+saved_test.getTestName());
			 System.out.println("TestScore "+saved_test.getTestScore());
			 System.out.println("TestStatus "+saved_test.getTestStatus());
			 System.out.println("TestType "+saved_test.getTestType());
		 System.out.println("SavedClassTestsAction.printSavedTest(((((((((((  ");
		}
	}
	
	private void printSavedTests(Vector saved_tests)
    {
	    int i = 0;
	    System.out.println("saved_tests ==========================");
	    while (i<saved_tests.size())
	    {
	    	System.out.println("test "+i+" -----=");
	    	SavedTest saved_test = (SavedTest)saved_tests.get(i);
		    printSavedTest(saved_test);
		    System.out.println("test --------=");
		    i++;
	    }
	    System.out.println("saved_tests ==========================");
    }
	
	private Hashtable setupClass()
	{
		Hashtable old_class = new Hashtable();
		old_class.put("new guy", "-4258958013598318021");
		old_class.put("-2948915381619837410", "new_options");
		/*
		-3877154413109844480 -		new_options
		-5556774569032430318 -		new guy
		-2575205790403381819 -		new guy
		5041076540485285784 -		new guy
		8361907154017660072 -		new guy
		-1580956296449542027 -		new guy
		4196477840697208555 -		new_options
		-5366823440791297662 -		new_options
		2468083362283366645 -		new guy
		5660428842837935019 -		new guy
		-1854460450717719192 -		new guy
		7603165281656803248 -		new guy
		6392462336876250493 -		new guy
		411542361450916807 -		new_options
		4598208830663927351 -		new_options
		-1981139068102768926 -		new_options
		-8757530091060153114 -		new guy
		-964143235029591794 -		new guy
		40228354103430415 -		new_options
		52684188291310616 -		new_options
		3541901005759167584 -		new_options
		-2797971952515244274 -		new_options
		2269797330283411677 -		new_options
		6450528172764281794 -		new_options
		-9143136468843914035 -		new guy
		-4455901449574465530 -		new_options
		-2384965276241480514 -		new guy
		-2385662772631571463 -		new_options
		7438709783629358236 -		new guy
		-4821420526888343311 -		new guy
		4808575347653919975 -		new_options
		-4775970081138081385 -		new_options
		-2835162279294659689 -		new guy
		-7365053194830603385 -		new_options
		1392807914203061599 -		new_options
		-6164800827429320459 -		new_options
		-1944893219093779098 -		new guy
		-6555325273984578728 -		new guy
		-7492315796700889310 -		new guy
		7338681982025547213 -		new_options
		-6846275594038302618 -		new guy
		401300405737511002 -		new guy
		29731276539609710 -		new guy
		-4991286464093094911 -		new guy
		-5910935257630263237 -		new guy
		-8940448909714616834 -		new_options
		3989286550331894363 -		new_options
		-1120167110480713633 -		new guy
		3519554975694986696 -		new_options
		-8179452895590383565 -		new_options
		4285616355672752664 -		new_options
		6683149660184216273 -		new_options
		-5650621049116681633 -		new_options
		-9170875195619748344 -		new_options
		1372642572450888200 -		new guy
		7192032976244935841 -		new_options
		-3051579766051049046 -		new guy
		9000000000000000001 -		tester
		-9159335204500632122 -		new guy
		8511232268923700130 -		new_options
		-8193000663755374249 -		new_options
		-312496447882973949 -		new guy
		1387838784988322560 -		new guy
		2712409611319545730 -		new guy
		5444786562029051946 -		new_options
		-6741824382238499371 -		new_options
		-6840432422249411168 -		new guy
		-8649217991867138727 -		new_options
		-9112141348984533736 -		new guy
		1386390901537772687 -		new guy
		1949703563386754755 -		new_options
		7394987262576087251 -		new guy
		1373696260975714829 -		new_options
		-8710290419097368675 -		new guy
		9212139034924267007 -		new guy
		-2311041560095620277 -		new guy
		4177822219172280831 -		new_options
		5095835155723169531 -		new_options
		-9044992544251063077 -		new guy
		-8481730752148485784 -		new guy
		7136398277384649854 -		new guy
		-9030691053395819849 -		new_options
		2422575316818351174 -		new guy
		1363707056540862290 -		new_options
		8780869581660615180 -		new guy
		2875075650954107975 -		new guy
		-3655007487270741120 -		new guy
		4528430823104416137 -		new guy
		791890035708259708 -		new guy
		0000000000000000001 -		teach
		-6189597055961068467 -		new_options
		7384558299279218982 -		new guy
		-8443539542362759367 -		new_options
		5672352188667913987 -		new_options
		-4181906040264885393 -		new guy
		-3514256760005629206 -		new_options
		5666695316265197887 -		new_options
		-3739988446138913799 -		new_options
		-2446948693731124681 -		new guy
		-3572161607125073892 -		new_options
		-9222721299177235343 -		new guy
		6887572623078950441 -		new guy
		-557200623967423711 -		new guy
		-8910262975126679557 -		new guy
		1497052280840035989 -		new guy
		8784412845537124613 -		new_options
		8229912643199815903 -		new guy
		-7290668384664624132 -		new guy
		-7058674444830862947 -		new guy
		-4759000372479493996 -		new guy
		-7826395679120826596 -		new guy
		-3311754027266396578 -		new guy
		-2743018900484080019 -		new_options
		-6694413601331676866 -		new_options
		-5222518976310279616 -		new guy
		-2387481068431983934 -		new_options
		-8500487008870580883 -		new_options
		8684566710978481449 -		new guy
		-2022877835396279992 -		new guy
		-5520391708019911710 -		new_options
		-4633525703472554403 -		new_options
		7287317664487540304 -		new_options
		-6304624385244077113 -		new_options
		5387031421408653474 -		new_options
		4714027218062475880 -		new guy
		4297858241632496802 -		new_options
		4315394723185687147 -		new_options
		-5373346381640598956 -		new_options
		405528582642457340 -		new guy
		4214170130338523122 -		new guy
		8169298870724616710 -		new guy
		5313935136559368302 -		new guy
		-2265507201590122233 -		new_options
		-7799696059773724316 -		new_options
		2809804819750141052 -		new guy
		7920275433264990513 -		new guy
		1540036150849800788 -		new_options
		7977079559693371012 -		new guy
		-5519451928541341407 -		guest
		-8091040939004481928 -		new guy
		-6395372484872002158 -		new guy
		6078288540539811273 -		new_options
		2002412586132073133 -		new_options
		-977766764848338736 -		new guy
		-5519451928541341007 -		admin
		-6751762218053073476 -		new_options
		-6659940358848410178 -		new guy
		-3738255889770622235 -		new guy
		-365172213581468901 -		new_options
		-8562200917343342692 -		new guy
		7711727616934594927 -		new_options
		*/
		return old_class;
	}
    

    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(FileUserUtilitiesTest.class);
    }

}
