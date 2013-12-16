package org.catechis.interfaces;

import java.util.Vector;
import java.util.Hashtable;

/**
 * Most methods in the file implementation require the context path to be
 * passed into the constructor.
 * @author a
 *
 */
public interface UserOptions
{
    public Hashtable getUserOptions(String user_name);
    public Hashtable getJSPOptions(String user_id, String jsp_name, String subject);
    public String getUserName(String user_id);
    public void addUserNameIndex(String user_name, String id);
    public void deleteUserNameIndex(String id);
    public void addUsersOptions(String option, String initial_value, Vector users);
    public void createNewLists(String user_id, String subject, String current_dir);
    public Vector getELTVector(Hashtable user_opts);
    public void editOption(String option_name, String new_value, String user_id);
}
