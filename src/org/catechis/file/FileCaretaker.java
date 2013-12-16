package org.catechis.file;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import org.catechis.interfaces.Caretaker;

import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.Locale;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.dto.Word;
import org.catechis.dto.UserInfo;
import org.catechis.dto.SavedTest;
import org.catechis.constants.Constants;
import org.catechis.interfaces.SaveTests;
import org.catechis.EncodeString;
import org.catechis.admin.FileUserOptions;

/**
*The backupWTDFiles is the first part of backing up test list files,
Then saveNewWLTDFileInfo is called to save a reference to the new wltd file.
Then saveNewWNTDFileInfo is called to save a reference to the new wntd file after wntd_file_key has been calculated.
*/
public class FileCaretaker implements Caretaker
{
	
	private Vector log;
	private Document doc;
	//private String file_name;
	
	
	public FileCaretaker()
	{
		// no arguments here...
		log = new Vector();
	}
		    

	/**
    * Delete the old backup folder in root/files/<subject>/lists/backup.
    * Create a new path, copy the old wtd files there.
    */
    public void backupWTDFiles(String old_wltd_file, String old_wntd_file, String user_id, String subject, String current_dir)
    {
    	log.add("FileCaretaker.backupWTDFiles: old_wltd_file ---- "+old_wltd_file);
    	log.add("FileCaretaker.backupWTDFiles: old_wntd_file ---- "+old_wntd_file);
    	File wltd_file = new File(old_wltd_file);
        File wntd_file = new File(old_wntd_file);
        // backup files
        String backup_folder_path = createBackupFolderPath(user_id, subject, current_dir);
        File backup_folder = new File(backup_folder_path);
        if (backup_folder.exists())
        {
        	//backup_folder.delete(); we knew this didn't work ages ago.  what was up with you guys that day?
        	FileUserOptions fuo = new FileUserOptions();
        	fuo.cleanFolder(user_id, backup_folder_path);
        	log.add("FileCaretaker.backupWTDFiles: fuo log ------------- ");
        	append(fuo.getLog());
        }
        backup_folder.mkdir();
        String wltd_file_name = wltd_file.getName();
        String wntd_file_name = wntd_file.getName();
        String backup_wltd_file_path = backup_folder_path+File.separator+wltd_file_name;
        String backup_wntd_file_path = backup_folder_path+File.separator+wntd_file_name;
        File backedup_wltd_file = new File(backup_wltd_file_path);
        File backedup_wntd_file = new File(backup_wntd_file_path);
        wltd_file.renameTo(backedup_wltd_file);
        wntd_file.renameTo(backedup_wntd_file);
        log.add("FileCaretaker.backupWTDFiles: backed up wltd? "+backedup_wltd_file.exists()); // why false???
        log.add("FileCaretaker.backupWTDFiles: backed up wntd? "+backedup_wntd_file.exists());
        //boolean success1 = wltd_file.delete();
        //boolean success2 = wntd_file.delete();
    }
    
    /**
    *Method so far is public for testing purposes.
    */
    public String createBackupFolderPath(String user_id, String subject, String current_dir)
    {
    	String backup_folder_path = (current_dir+File.separator+Constants.FILES
        	+File.separator+user_id
        	+File.separator+subject
        	+File.separator+Constants.LISTS
        	+File.separator+Constants.BACKUP);
        //log.add("FileCaretaker.createBackupFolderPath");
    	return backup_folder_path;
    }
    
    /**
    * This method creates two folders:
    * root/files/<subject>/lists/backup/wltd_backup
    * root/files/<subject>/lists/backup/wltd_backup/<new_wltd_file_key>
    * These are just links to files create in the root/files/<subject>/lists/wltd & wntd reading & writing folders.
    */
    public void saveNewWLTDFileInfo(String new_wltd_file_key, String user_id, String subject, String current_dir)
    {
    	String backup_folder_path = createBackupFolderPath(user_id, subject, current_dir);
    	String backup_new_wltd_folder_path = backup_folder_path+File.separator+Constants.WLTD_BACKUP;
    	String backup_new_wltd_folder = backup_new_wltd_folder_path+File.separator+new_wltd_file_key;
    	boolean backup_new_wltd_folder_path_folder = new File(backup_new_wltd_folder_path).mkdir();
    	boolean backup_new_wltd_folder_folder = new File(backup_new_wltd_folder).mkdir();
    	log.add("FileCaretaker.saveNewWLTDFileInfo: created dir?"+backup_new_wltd_folder_path_folder+" path "+backup_new_wltd_folder_path);
    	log.add("FileCaretaker.saveNewWLTDFileInfo: created dir?"+backup_new_wltd_folder_folder+" path "+backup_new_wltd_folder);
    }
    
    /**
    * This method creates two folders:
    * root/files/<subject>/lists/backup/wntd_backup
    * root/files/<subject>/lists/backup/wntd_backup/<new_wltd_file_key>
    * These are just links to files create in the root/files/<subject>/lists/wltd & wntd reading & writing folders.
    */
    public void saveNewWNTDFileInfo(String new_wntd_file_key, String user_id, String subject, String current_dir)
    {
    	String backup_folder_path = createBackupFolderPath(user_id, subject, current_dir);
    	String backup_new_wntd_folder_path = backup_folder_path+File.separator+Constants.WNTD_BACKUP;
    	String backup_new_wntd_folder = backup_new_wntd_folder_path+File.separator+new_wntd_file_key;
    	new File(backup_new_wntd_folder_path).mkdir();
    	new File(backup_new_wntd_folder).mkdir();
    	log.add("FileCaretaker.saveNewWNTDFileInfo: created dir "+backup_new_wntd_folder_path);
    	log.add("FileCaretaker.saveNewWNTDFileInfo: created dir "+backup_new_wntd_folder);
    }
    
    /**
    * Delete the folders in root/files/<subject>/lists/backup/wntd_backup
    * root/files/<subject>/lists/backup/wntd_backup/<new_wltd_file_key>
    * and use the key to delete the wltd/wntd files,
    * then copy the files in the backup dir back to the wltd/wntd folders.
    *
    * I guess we need to move the copies back to their original place,
    * delete the files who's names are represented in the backup/wntd/wltd folders,
    * and then that's it right?  because a new undo would what?
    * Should we switch the files?
    * No, because a new undo would call updateTestDateRecords again?
    * Wrong, that would only happen when scoring a test.
    * so we do need to switch the files so that another undo would
    * simply reverse this situation.
    *
    */
    public void restoreWTDFiles(String type, String user_id, String subject, String current_dir)
    {
    	//move the copies back to their original place,
    	
    	// move the files who's names are represented in the backup/wntd/wltd folders where?
    	// these folder names represent the files in the lists folders that need to be moved into
    	// the backup folder.  but thereś only one file int he backup folder.
    	// This file has a wltd file key, so it represents a wnt file...
    	// <wltd_file_key>1259503785000</wltd_file_key></test>
    	// this is because the wltd file can be generated from the words last test date.
    	// where is this file generated?  in the WordLastTestDates.updateTestDateRecords method?
    	// THis seems like a chicken and the egg exercise.
    	// so check the undo action to see if it calls this method.  otherwise we need to do this here

    }
	
	/** deguggin */
	public Vector getLog()
	{
		return this.log;
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
