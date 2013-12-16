package org.catechis.admin;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;

import org.catechis.Domartin;
import org.catechis.EncodeString;
import org.catechis.FileStorage;
import org.catechis.JDOMSolution;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;
import org.catechis.dto.WordLastTestDates;
import org.catechis.interfaces.UserOptions;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
*<p>This class gets a users options as well as adds a new option to every users option file,
* which is only to retrofit older users after (File)UserUtilities is changed..
*<p>When an admin does retrofit users options files, to see the new option on the
* admins page, the jsp file must be altered to display the new option, and
* the properties file needs to include a label in various languages for this option.
*<p>Ideally, new options should be added in the (File)UserUtilities class.
*/
public class FileUserOptions implements UserOptions
{

    private String current_dir;
    private Document doc;
    private Vector log;
    
    /**Users might just want to use the cleanFolder utility without nay hoopla.
    */
    public FileUserOptions()
    {
        log = new Vector();
        log.add("FileUserOptions.getUserOptions<init>");
    }
    
    public FileUserOptions(String _current_dir)
    {
        this.current_dir = _current_dir;
        log = new Vector();
        log.add("FileUserOptions.getUserOptions<init> "+current_dir);
    }

    public Hashtable getUserOptions(String user_name)
    {
        // from LoginAction.loginUser method
        log.add("FileUserOptions.getUserOptions user_name "+user_name);
        Hashtable options = new Hashtable();
        String user_folder = (current_dir+File.separator+"files"+File.separator+user_name);
        String file_name = new String(user_folder+File.separator+user_name+".options");
        log.add("FileUserOptions.getUserOptions file_name "+file_name);
        //File file_chosen = Domartin.createFileFromUserNPath(file_name, user_folder);
        //String file_path = file_chosen.getAbsolutePath();
        JDOMOptions jdom_opts = new JDOMOptions(file_name);
        options = jdom_opts.getOptionsHash();
        log.add("FileUserOptions.getUserOptions");
        append(jdom_opts.getLog());
        return options;
    }
    
    /**
    *This method gets an options file associated with a jsp file stored in the users
    * folder like this <user_id>/<subject>/options/jsp_options/<jsp_name>.options.
    *<p>This id is a string so that this method is backwards compatible with
    * users who's folders are named instead of in a folder named after their id.
    */
    public Hashtable getJSPOptions(String user_id, String jsp_name, String subject)
    {
        //String user_name = new String();
        Hashtable options = new Hashtable();
        // from LoginAction.loginUser method
        log.add("FileUserOptions.getJSPOptions: user_id "+user_id);
        String user_name = getUserName(user_id);
        //if (user_name == null)
        //{
            // user is not in the admin//
          //  return options;
        //}
        log.add("FileUserOptions.getJSPOptions: user_name "+user_name);
        //String search_id = Domartin.checkOldUser(user_name, user_id, current_dir);
        String search_id = user_id;
        String user_folder = (current_dir+File.separator+"files"+File.separator+search_id);
        String file_name = new String(user_folder+File.separator+subject+File.separator+
            "options"+File.separator+"jsp_options"+File.separator+jsp_name+".options");
        if (user_name==null)
        {
            // old users had their folders anmed after their actual login names...yeah, i know...
            user_folder = (current_dir+File.separator+"files"+File.separator+user_id);
            file_name = new String(user_folder+File.separator+subject+File.separator+
            "options"+File.separator+"jsp_options"+File.separator+jsp_name+".options");
            
        }
        log.add("FileUserOptions.getUserOptions: file_name "+file_name);
         File test_file = new File (file_name);
         log.add("FileUserOptions.getUserOptions:exists? "+test_file.exists());
        //File file_chosen = Domartin.createFileFromUserNPath(file_name, user_folder);
        //String file_path = file_chosen.getAbsolutePath();
        JDOMOptions jdom_opts = new JDOMOptions(file_name);    // -
        options = jdom_opts.getOptionsHash();
        log.add("FileUserOptions.getJSPOptions.JDOMOptions log ---- start ");
        append(jdom_opts.getLog());
        log.add("FileUserOptions.getJSPOptions.JDOMOptions log ---- end ");
        return options;
    }
    
    /**
    *The users name is stored in a file /files/admin/users/<id>.usr.
    *<p>This id is a string so that this method is backwards compatible with
    * users who's folders are named instead of in a folder named after their id.
    */
    public String getUserName(String user_id)
    {
        String user_folder = (current_dir+File.separator+"files"+File.separator+"admin"+File.separator+"users");
        String file_name = new String(user_folder+File.separator+user_id+".usr");
        log.add("FileUserOptions.getUserName: file_name "+file_name);
        // /home/timmy/projects/catechis/files/admin/users/2960548470376610112.usr
        log.add("FileUserOptions.getUserName: exists?   "+(new File(file_name).exists()));
        String user_name = "default";
        JDOMOptions jdom_opts = new JDOMOptions(file_name);
        log.add("FileUserOptions.getUserName: jdom_opts after init ========= ");
	append(jdom_opts.getLog());
	jdom_opts.resetLog();
        try
        {
	        Hashtable options = jdom_opts.getOptionsHash();
	        log.add("FileUserOptions.getUserName: options.size "+options.size());
	        user_name = (String)options.get("user_name");
	        log.add("FileUserOptions.getUserName: GOT "+user_name);
	} catch (java.lang.NullPointerException npe)
	{
		log.add("FileUserOptions.getUserName: npe "+file_name);
		log.add("FileUserOptions.getUserName: jdom_opts log ========= ");
		append(jdom_opts.getLog());
	}
	log.add("FileUserOptions.getUserName: "+user_name);
        return user_name;
    }
    
    /**
    *This utility creates a file with the id as the filename, and options
    * such as a users option files.
    */
    public void addUserNameIndex(String user_name, String id)
    {
        String user_folder = (current_dir+File.separator+"files"+File.separator+"admin"+File.separator+"users");
        String file_name = new String(user_folder+File.separator+id+".usr");
        File user_name_index_file = new File(file_name);
        try
        {
            user_name_index_file.createNewFile();
        } catch (java.io.IOException ioe)
        {
            
        }
        JDOMOptions jdom_opts = new JDOMOptions(file_name);
        jdom_opts.createFile(file_name);
        String option = "user_name";
        String init_value = user_name;
        jdom_opts.addUserOption(option, init_value);
        jdom_opts.writeDocument();
        log.add("FileUserOptions.getUserName: "+user_name);
        append(jdom_opts.getLog());
    }
    
    public void deleteUserNameIndex(String id)
    {
        String user_folder = (current_dir+File.separator+"files"+File.separator+"admin"+File.separator+"users");
        String file_name = new String(user_folder+File.separator+id+".usr");
        File user_name_index_file = new File(file_name);
        user_name_index_file.delete();
    }
    
    /**
    * Admin utility to add an option to every users <name> option file.
    */
    public void addUsersOptions(String option, String initial_value, Vector users)
    {
        Vector users_opts = new Vector();
        String user_folders = (current_dir+File.separator+"files"+File.separator);
        int size = users.size();
        int i = 0;
        while (i<size)
        {
            String user_name = (String)users.get(i);
            String ext = Domartin.getFileExtension(user_name);
            String file_name = new String();
            JDOMOptions jdom_opts = null;
            if (ext.equals("-1"))
            {
                // Domartin returns -1 if there is no file extension
                try
                {
	                file_name = (user_folders+user_name+File.separator+user_name+".options");
	                jdom_opts = new JDOMOptions(file_name);
	                jdom_opts.addUserOption(option, initial_value);
	                jdom_opts.writeDocument();
	        } catch (java.lang.NullPointerException npe)
	        {
	                log.add("FileUserOptions.addUsersOptions npe "+file_name+" "+user_name);
	        }
            } else
            {
                log.add("FileUserOptions.addUsersOptions ignored "+user_name);
            }
            i++;
        }
    }
    
    /**
     * Load a users options file and get the Exclude Time Vector (ELTVector)
     * then erase the old word's last test dates and  word's next test date reading and writing folders
     * and regenerate the files based on the ELTVector.
     */
    public void createNewLists(String user_id, String subject, String current_dir)
    {
        FileStorage store = new FileStorage(current_dir);
        Hashtable user_opts = store.getUserOptions(user_id, current_dir);
        Vector categories = store.getWordCategories(subject, user_id);
        WordLastTestDates wltd = new WordLastTestDates();
        cleanFolders(user_id, current_dir);
        wltd.setExcludeLevelTimes(getELTVector(user_opts));
        //wltd.setType(type); // not used
        wltd.setLimitOfWords(100);
        wltd.createNewLists(categories, store, user_id);
        log.add("FileUserOptions.createNewLists: wltd log");
        append(wltd.getLog());
    }
    
    /**
     * delete the contends of wltd/wntd r/w/ folders.
     * @param user_id
     * @param current_dir
     */
    public void cleanFolders(String user_id, String current_dir)
    {
        // delete reading wltd
        WordLastTestDates wltd = new WordLastTestDates();
        wltd.setType(Constants.READING);
        String ptf = wltd.getPathToFile(user_id, current_dir, "wltd");
        File file = new File(ptf);
        boolean exist = file.exists();
        boolean write =    file.canWrite();
        log.add("exists? "+exist+" write? "+write);
        boolean deleted = deleteDir(file);
        file.mkdir();
        log.add(ptf+" deleted? "+deleted);
        // delete reading wntd
        ptf = wltd.getPathToFile(user_id, current_dir, "wntd");
        file = new File(ptf);
        deleted = deleteDir(file);
        file.mkdir();
        log.add(ptf+" deleted? "+deleted);
        // delete writing wltd
        wltd.setType(Constants.WRITING);
        ptf = wltd.getPathToFile(user_id, current_dir, "wltd");
        file = new File(ptf);
        deleted = deleteDir(file);
        file.mkdir();
        log.add(ptf+" deleted? "+deleted);
        // delete writing wntd
        ptf = wltd.getPathToFile(user_id, current_dir, "wntd");
        file = new File(ptf);
        deleted = deleteDir(file);
        file.mkdir();
        log.add(ptf+" deleted? "+deleted);
    }
    
    /**
     * Delete the contents of a directory passed in as the second argument.
     * @param user_id
     * @param current_dir
     */
    public void cleanFolder(String user_id, String dir_to_delete)
    {
        File file = new File(dir_to_delete);
        boolean exist = file.exists();
        boolean write = file.canWrite();
        log.add("exists? "+exist+" write? "+write);
        boolean deleted = deleteDir(file);
        file.mkdir();
        exist = file.exists();
        log.add("deleted? "+deleted+" recreated? "+exist);
    }
    
    /** Deletes all files and sub-directories under dir.
    * Returns true if all deletions were successful.
    * If a deletion fails, the method stops attempting to delete and returns false.
    */
    private boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) 
		{
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    /**
     * ELT = Exclude Level Times.  These are integers representing multiples days where a word should be excluded from testing.
     * This utility method returns a Vector of four elements corresponding to reading levels 0-3, and their multiplies
     * which are stored in a users option file.
     * @param user_opts
     * @return
     */
    public Vector getELTVector(Hashtable user_opts)
    {
        Vector elt_vector = new Vector();
        elt_vector.add(user_opts.get("exclude_level0_test"));
        elt_vector.add(user_opts.get("exclude_level1_test"));
        elt_vector.add(user_opts.get("exclude_level2_test"));
        elt_vector.add(user_opts.get("exclude_level3_test"));
        return elt_vector;
    }
    
    /**
     *
     */
    public void editOption(String option_name, String new_value, String user_id)
    {
        String file_name = current_dir+File.separator+Constants.FILES+File.separator
        	+user_id+File.separator+user_id+".options";
        log.add("FileUserOptions.editOption "+file_name);
        File file = new File(file_name);
        JDOMOptions jdom_opts = new JDOMOptions(file_name);
        try
        {
        	jdom_opts.editOption(option_name, new_value);
        } catch (java.lang.NullPointerException npe)
        {
        	log.add("FileUserOptions.editOption npe in jdom_opts");
        }
        append(jdom_opts.getLog());
    }
    
    
    /** deguggin */
    public Vector getLog()
    {
        return log;
    }

    public void resetLog()
    {
	log = new Vector();
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

