package org.catechis.interfaces;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;

public interface UserUtilities
{

	public Vector getUsers(Vector admins);
	public Vector getFolders();
	public void addLoginEntry(String user_name, String current_dir);
	public Hashtable getLoginEntries(String current_dir);
	public void resetLoginEntries(String current_dir);
	public ArrayList getSortedLoginEntries();
	public Hashtable getUsersLastLogin(Vector users);
	public boolean addNewUser(String user_name, String password, String e_mail);
	public boolean checkInvitationCode(String current_dir, String users_code);
}
