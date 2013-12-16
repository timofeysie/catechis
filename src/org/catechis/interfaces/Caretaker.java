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
public interface Caretaker
{
	/**
	*Delete the old backups copy the old wtd info there for a possible undo.
	*/
	public void backupWTDFiles(String old_wltd_file, String old_wntd_file, String user_id, String subject, String current_dir);
	
	public void saveNewWLTDFileInfo(String new_wltd_file_key, String user_id, String subject, String current_dir);
	
	public void saveNewWNTDFileInfo(String new_wntd_file_key, String user_id, String subject, String current_dir);
	
	/**
    	* Use the key created wtd references created last time
    	* then copy the saved wtd references to the wltd/wntd reference lists.
    	*/
    	public void restoreWTDFiles(String type, String user_id, String subject, String current_dir);
    
	/**
	*Homegrown debugging may be more expensive, but it has more local flavor!
	*/
	Vector getLog();
}
