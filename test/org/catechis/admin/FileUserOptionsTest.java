package org.catechis.admin;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.io.File;

import org.catechis.Transformer;
import org.catechis.Domartin;

import junit.framework.TestCase;

public class FileUserOptionsTest extends TestCase
{

	public FileUserOptionsTest(String name)
	{
		super(name);
	}
    
	protected void setUp() throws Exception
	{
	}
	
	/**
	*<p>addUsersOptions(String option, String initial_value, Vector users)
	*/
	public void testAddOption()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserUtilities fut = new FileUserUtilities(current_dir);
		Vector admins = new Vector();	// dont need to exclude anyone
		Vector users = fut.getUsers(admins);
		String option = "test_option";
		String initial_value = "test_value";
		//dumpLog(users);
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.addUsersOptions(option, initial_value, users);
		String user_name = "test_user";
		fuo = new FileUserOptions(current_dir);
		Hashtable user_opts = fuo.getUserOptions(user_name);
		boolean actual = user_opts.containsKey("test_option");
		boolean expected = true;
		//System.out.println("FileUserOptionsTest.testAddOption: user_opts ---");
		//dumpLog(Transformer.createTable(user_opts));
		//System.out.println("FileUserOptionsTest.estAddOption: fuo log");
		//dumpLog(fuo.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	*<p>addUsersOptions(String option, String initial_value, Vector users)
	*/
	public void testGetUserName()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		//FileUserUtilities fut = new FileUserUtilities(current_dir);
		//Vector admins = new Vector();	// dont need to exclude anyone
		//Vector users = fut.getUsers(admins);
		//String option = "test_option";
		//String initial_value = "test_value";
		FileUserOptions fuo = new FileUserOptions(current_dir);
		String user_id = "test_user";
		String actual_user_name = fuo.getUserName(user_id);
		String expected_user_name = "test_user";
		//System.out.println("FileUserOptionsTest.testGetUserName: user_opts ---");
		Hashtable user_opts = fuo.getUserOptions(user_id);
		//dumpLog(Transformer.createTable(user_opts));
		//System.out.println("FileUserOptionsTest.estAddOption: fuo log");
		//dumpLog(fuo.getLog());
		assertEquals(expected_user_name, actual_user_name);
	}
	
	public void testAddUserNameIndex()
	{
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath(); 
		String user_name = "jess";
		long long_id = Domartin.getNewID();
		String id = Long.toString(long_id);
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.addUserNameIndex(user_name, id);
		//fuo.writeDocument();
		String user_folder = (current_dir+File.separator+"files"+File.separator+"admin"+File.separator+"users");
		String file_name = new String(user_folder+File.separator+id+".usr");
		File user_name_index_file = new File(file_name);
		boolean actual = user_name_index_file.exists();
		boolean expected = true;
		fuo.deleteUserNameIndex(id);
		assertEquals(expected, actual);
	}
	
	public void testGetJSPOptions()
	{
		String guest_id = "2960548470376610112";
		String jsp_name = "weekly_list_format";
		String subject = "vocab";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath(); 
		//String user_name = "jess";
		//long id = Domartin.getNewID();
		FileUserOptions fuo = new FileUserOptions(current_dir);
		Hashtable options = fuo.getJSPOptions(guest_id, jsp_name, subject);
		//System.out.println("FileUserOptionsTest.testGetJSPOptions: user_opts ---");
		//dumpLog(options);
		String expected = "125";
		String actual = (String)options.get("format_tr_width");
		assertEquals(expected, actual);
	}
	
	public void testGetJSPOptions0()
	{
		String guest_id = "test_user";
		String jsp_name = "daily_test_result";
		String subject = "vocab";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath(); 
		FileUserOptions fuo = new FileUserOptions(current_dir);
		Hashtable options = fuo.getJSPOptions(guest_id, jsp_name, subject);
		
		System.out.println("FileUserOptionsTest.testGetJSPOptions0: user_opts ---");
		dumpLog(options);
		System.out.println("FileUserOptionsTest.testGetJSPOptions0: user_opts --- end");
		
		System.out.println("FileUserOptionsTest.testGetJSPOptions0: log ---");
		dumpLog(fuo.getLog());
		System.out.println("FileUserOptionsTest.testGetJSPOptions0: log --- end");
		
		
		String expected = "true";
		String actual = (String)options.get("format_print_missed_reading");
		assertEquals(expected, actual);
	}
	
	/**
	*This method causes a system.err java.io.FileNotFoundException but recovers 
	* and getJSPOptions returns an empty hashtable.  Teach me how to cover this
	* up and pass this test, please?
	*/
	public void testGetJSPOptionsFail()
	{
		String guest_id = "2960548470376610111";
		String jsp_name = "weekly_list_format";
		String subject = "vocab";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath(); 
		//String user_name = "jess";
		//long id = Domartin.getNewID();
		FileUserOptions fuo = new FileUserOptions(current_dir);
		Hashtable options = fuo.getJSPOptions(guest_id, jsp_name, subject);
		boolean expected = true;
		boolean actual = options.isEmpty();
		assertEquals(expected, actual);
	}
	
	/*
	public final void testEditOption()
	{
		String option_name = "exclude_level0_test";
		Random rnd = new Random();
		String new_value = ""+rnd.nextInt();
		//String user_id = "6677398954116912310";
		String user_id = "test_user";
		File path_file = new File("");
		String current_dir = path_file.getAbsolutePath();
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.editOption(option_name, new_value, user_id);
		System.out.println("FileUserOptionsTest.testEditOption log ---");
		dumpLog(fuo.getLog());
		
		Hashtable user_opts = fuo.getUserOptions(user_id);
		System.out.println("FileUserOptionsTest.testEditOption: user_opts --- ");
		dumpLog(user_opts);
		String expected = new_value;
		String actual = (String)user_opts.get(option_name);
		assertEquals(expected, actual);
	}
	*/
	
	//------------------------------------------ made in juksong
    public void testEditOption2()
    {
        //System.out.println("FileUserOptionsTest.testEditOption() *** ---");
        //String user_id = "2960548470376610111";
        String user_id = "WLTD test";
        String jsp_name = "weekly_list_format";
        String subject = "vocab";
        File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        String option_name = "exclude_level0_test";
        long now = new Date().getTime();
        String new_value = ""+now;
        FileUserOptions fuo = new FileUserOptions(current_dir);
        fuo.editOption(option_name, new_value, user_id);
        
        String user_name = fuo.getUserName(user_id);
        Hashtable options = fuo.getUserOptions(user_id);
        String expected = new_value;
        String actual = (String)options.get("exclude_level0_test");
        //dumpLog(fuo.getLog());
        
        //System.out.println("options ---");
        //dumpLog(Transformer.createTable(options));
        
        //System.out.println("FileUserOptionsTest.testEditOption() *** ---");
        assertEquals(expected, actual);
    }
    //------------------------------------------ juksong

    private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

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

    public static void main(String args[]) 
    {
        junit.textui.TestRunner.run(FileUserUtilitiesTest.class);
    }

}
