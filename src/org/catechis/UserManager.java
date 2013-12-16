package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;
import java.util.Hashtable;

public class UserManager
{

	/** This is the singleton instance */
	private static UserManager manager;
	static boolean instance_flag = false;
	
	private Storage store;
	
	public UserManager() throws ThereCanBeOnlyOneException
	{
		if (instance_flag)
		{
			throw new ThereCanBeOnlyOneException("Only 1 UserManager allowed");
		} else
		{
			instance_flag = true;
		}
	}
	
	public void setStorage(Storage _store)
	{
		this.store = _store;
	}
	
	public static synchronized UserManager getManager()  throws ThereCanBeOnlyOneException
	{
		if (instance_flag == false)
		{
			throw new ThereCanBeOnlyOneException("Only 1 UserManager allowed");
		} else
		{
			return manager;
		}
	}
	
	public boolean loginUser(String user_name, String _password)
	{
		Hashtable users = store.findUsers();
		try
		{
			String password = (String)users.get(user_name);
				if (password.equals(_password))
				{
					store.login(user_name);
					filterLists();
					return true;
				}
				else 
				{
					return false;
				}
		}
		catch (java.lang.NullPointerException e)
		{
			// if there is no passwords
			return false;
		}
	}
	
	private void filterLists()
	{
		Vector lists = store.getUserLists();
		Vector tests = new Vector();
		Vector words = new Vector();
		Vector other = new Vector();
		int num_of_lists = lists.size();
		for(int i = 0; i<num_of_lists; i++) 
		{
			String name = (String)lists.get(i);
			String ext = Domartin.getFileExtension(name);
			String abbr = Domartin.getFileWithoutExtension(name);
			if (ext.equals(".test"))
			{
				tests.add(abbr);
			} else if (ext.equals(".xml"))
			{
				words.add(abbr);
			}
			else
			{
				other.add(name);
			}
		}
		store.loggit("UserManager.filterLists: tests "+tests.size()
			+"  words "+words.size()+" other "+other.size());
		store.setTests(tests);
		store.setWords(words);
		store.setOther(other);
	}

	public Storage getStorage()
	{
		return this.store;
	}

}
