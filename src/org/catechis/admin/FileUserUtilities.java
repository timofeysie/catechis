package org.catechis.admin;

import java.io.File;
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;

import org.catechis.Domartin;
import org.catechis.JDOMSolution;
import org.catechis.FileStorage;
import org.catechis.constants.Constants;
import org.catechis.dto.AllStatsHistory;
import org.catechis.interfaces.UserUtilities;

/**
*This class gets a list of users, their folders/categories adds/gets login entries
* and sets up a list of options in the createUserOptions method 
* to be passed to JDOMFiles which will create
* a new users option file.
*/
public class FileUserUtilities implements UserUtilities
{

	private long id;
	
	private String current_dir;
	private Vector log;
	
	public FileUserUtilities(String _current_dir)
	{
		this.current_dir = _current_dir;
		log = new Vector();
		log.add("FileUserUtilities.<init> "+current_dir);
	}

	public Vector getUsers(Vector admins)
	{
		Vector users = new Vector();
		Vector folders = getFolders();
		int i = 0;
		int size = folders.size();
		while (i<size)
		{
			String user = (String)folders.get(i);
			if (admins.contains(user))
			{
				// dont add admins
			} else
			{
				users.add(user);
			}
			i++;
		}
		return users;
	}
	
	public Vector getFolders()
	{
		String file_path = new String(current_dir+File.separator+"files");
		File folders_file = new File(file_path);
		String [] folders = folders_file.list();
		Vector users = new Vector();
		int size = folders.length;
		int i = 0;
		while (i<size)
		{
			String propsect = (String)folders[i];
			String possible_extension = Domartin.getFileExtension(propsect);
			if (possible_extension.equals("-1"))
			{
				// Domartin returns -1 if there is no file extension.
				users.add(folders[i]);
				//log.add(folders[i]);
			}
			i++;
		}
		return users;
	}
	
	public void addLoginEntry(String user_name, String current_dir)
	{
		String file_name = "logins.xml";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		JDOMFiles jf = new JDOMFiles(file_path);
		String date = Long.toString(new Date().getTime());
		jf.addLoginEntry(user_name, date);
		jf.writeDocument(file_path);
	}
	
	public Hashtable getLoginEntries(String current_dir)
	{
		String file_name = "logins.xml";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		JDOMFiles jf = new JDOMFiles(file_path);
		Hashtable hash = jf.getLoginEntries();
		log.add("FileUserUtilities.getLoginEntries: "+file_path);
		append(jf.getLog());
		return hash;
	}
	
	public void resetLoginEntries(String current_dir)
	{
		String file_name = "logins.xml";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		JDOMFiles jf = new JDOMFiles(file_path);
		jf.resetLoginEntries();
		jf.writeDocument(file_path);
	}
	
	public ArrayList getSortedLoginEntries()
	{
		Hashtable logins = getLoginEntries(current_dir);
		Set list = logins.keySet();
		ArrayList a_list = new ArrayList(list);
		ArrayList sorted_keys = Domartin.sortList(a_list);
		return sorted_keys;
	}
	
	public Hashtable getUsersLastLogin(Vector users)
	{
		Hashtable user_logins = new Hashtable();
		Hashtable logins = getLoginEntries(current_dir);
		Set list = logins.keySet();
		ArrayList a_list = new ArrayList(list);
		ArrayList sorted_keys = Domartin.sortList(a_list);
		int size = sorted_keys.size();
		int i = 0;
		while (i<size)
		{
			String key = (String)sorted_keys.get(i);
			String value = (String)logins.get(key);
			if (users.contains(value))
			{
				if (!user_logins.contains(value))
				{
					user_logins.put(value, key);
				}
			}
			i++;
		}
		return user_logins;
	}
	
	/**
	*This method has to do quite a lot.  As this project has grown,
	*<p>the architecture has grown in a not so organized or consistent way.
	*<p>Here are the basic steps:
	<p>check to see if user already exists
	<p>add entry in user passes
	<p>create user folder inside /web_inf/files/ folder
	<p>create vocab subfolder
	<p>create the vocab/history folder to hold year-month-week.xml weekly session history reports
	<p>create status.record file in vocab folder
	<p>create user_name.hist file
	<p>create daily <r/w> test.record files
	<p>create user_name.options file
	<p>create level <#> <type>.test files
	<p>Returns false if the user name already exists.
	*/
	public boolean addNewUser(String user_name, String password, String e_mail)
	{
		String subject = "vocab";					// right now this is only vocab
		// check to see if user already exists
		Vector actual_users = getUsers(new Vector());
		if (actual_users.contains(user_name))
		{
			return false;
		}
		// add entry in user passes
		String file_name = "user.passes";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		id = Domartin.getNewID();
		String str_id = Long.toString(id);
		JDOMFiles jf = new JDOMFiles(file_path);
		jf.addUserPass(user_name, password, e_mail, id);
		jf.writeDocument(file_path);
		// create folder
		String user_folder_path = new String(current_dir+File.separator+"files"+File.separator+id);
		File user_folder = new File(user_folder_path);
		user_folder.mkdir();
		// create vocab subfolder
		String vocab_folder_path = new String(user_folder_path+File.separator+subject);
		File vocab_folder = new File(vocab_folder_path);
		vocab_folder.mkdir();
		// create lists folder for new words lists, etc...
		String lists_folder_path = new String(vocab_folder_path+File.separator+"lists");
		File lists_folder = new File(lists_folder_path);
		lists_folder.mkdir();
		createStatusFile(vocab_folder_path);				// create status.record file in 
		// create history folder
		String history_folder_path = new String(vocab_folder_path+File.separator+"history");
		File history_folder = new File(history_folder_path);
		history_folder.mkdir(); // this will hold the year-month-week.xml weekly session history reports
		// vocab folder
		createHistoryFile(user_folder_path, Constants.READING);		// create daily <r/w> test.record filess
		createHistoryFile(user_folder_path, Constants.WRITING);
		createFile(user_folder_path, (str_id+".hist"), "history");	// create user_name.hist file
		createUserOptions(user_folder_path, str_id);			// create user_name.options file
		createTestFiles(user_folder_path);				// create level <#> <type>.test files
		createFirstHistoryRecord(user_folder_path, str_id);		// create a record in FileTestRecords
		// for date of creation???
		createUserNameIndexFile(current_dir, user_name, str_id);	// create an index file with 
		//an option containing the users name
		createNewWordsListFiles(lists_folder_path);			// create new word lists *
		createRetiredWordsListFile(lists_folder_path);			// create retired words file
		String options_folder_path = (user_folder+File.separator+subject+File.separator+"options");
		createOptionsFolders(options_folder_path, str_id, subject);	// create an options file, and option files for some jsp pages.
		createTestDateFolders(lists_folder_path);			// these folders hold files of word ids,
		// types, and filenames named after the words last test dates (wltd) and words next text dates (wntd)
		// tests
		String tests_folder_path = new String(vocab_folder_path+File.separator+"tests");
		File tests_folder = new File(tests_folder_path);
		tests_folder.mkdir();
		createSavedTestsFile(tests_folder_path);
		// saved tests
		String saved_folder_path = new String(tests_folder_path+File.separator+"saved");
		File saved_folder = new File(saved_folder_path);
		saved_folder.mkdir();
		// create categories folder
		String categories_folder_path = new String(vocab_folder_path+File.separator+Constants.CATEGORIES);
		File categories_folder = new File(categories_folder_path);
		categories_folder.mkdir();
		// create categories index file
		createCategoriesFile(vocab_folder_path);
		return true;
	}
	
	/**
	<p>Create a new password entry for the user in user.pases.
	<p>create teacher folder.
	<p>Create a file to class/group lists in their folder.
	* Returns false if the user name/id already exists.
	* The directory structure for a teacher is as follows:
	* files/admin/teachers.xml
	* files/admin/teachers/teacher_id/teacher_id.options
	* files/admin/teachers/teacher_id/classes.xml
	* files/admin/teachers/teacher_id/lists.xml
	* files/admin/teachers/teacher_id/classes/class_id.xml
	* files/admin/teachers/teacher_id/lists/list_id.xml
	*/
	public boolean addNewTeacher(String teacher_name, String password, String e_mail)
	{
		// check to see if user already exists
		Vector actual_teachers = getTeachers();
		String teachers_name = "new_teachers";
		if (actual_teachers.contains(teacher_name))
		{
			return false;
		}
		// add entry in user passes
		String file_name = "user.passes";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		long new_id = Domartin.getNewID();
		String str_id = Long.toString(new_id);
		JDOMFiles jf = new JDOMFiles(file_path);
		jf.addUserPass(teachers_name, password, e_mail, id);
		jf.writeDocument(file_path);
		String admin_folder_path = new String(current_dir+File.separator+"files"
			+File.separator+"admin");
		// create folder files/admin/teachers/str_id
		String teacher_folder_path = new String(admin_folder_path+File.separator+"teachers"
			+File.separator+str_id);
		File teacher_folder = new File(teacher_folder_path);
		teacher_folder.mkdir();
		// create classes folder
		String classes_folder_path = new String(teacher_folder_path+File.separator+"classes");
		File classes_folder = new File(classes_folder_path);
		classes_folder.mkdir();
		// create word lists folder and lists.xml file
		String word_lists_folder_path = new String(teacher_folder_path+File.separator+"lists");
		File word_lists_folder = new File(word_lists_folder_path);
		word_lists_folder.mkdir();
		jf.createNewDocument(classes_folder_path, Constants.LISTS);
		// create saved test file
		String saved_tests_path = teacher_folder_path+File.separator+Constants.SAVED_TESTS+".xml";
		jf.createNewDocument(saved_tests_path, Constants.SAVED_TESTS);
		// add id to admin/teachers.xml file/ or just get the contenxts of the teachers folder for the
		// sub menus that are named after tyhe teacherś ids.
		
		// create classes lists file.
		//reateFile(teacher_folder_path, (str_id+".groups"), "group");	// create groups list file
		// eachg group will have afolder with studetns.xml files, and a group.options file.
		return true;
	}
	
	/**
	 * In file/admin/teachers/teacher_id/classes/class_id.xml
	 * There is this format:
	 * <class>
		<student>-7028961644117975960</student>
		...
		<student>4201001381186235795</student>
	 * </class>
	 * @param student_id
	 * @param class_id
	 * @param teacher_id
	 */
	public void addStudentToClass(String student_id, String class_id, String teacher_id)
	{
		String class_file = new String(current_dir+File.separator+"files"
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id
				+File.separator+Constants.CLASSES
				+File.separator+class_id+".xml");
		log.add("FileUserUtilitites.addStudentToClass: file "+class_file+" exists? "+new File(class_file).exists());
		JDOMFiles jf = new JDOMFiles(class_file);
		Vector student_ids = new Vector();
		student_ids.add(student_id+"");
		jf.addToClass(student_id, class_file);
	}
	
	public void addStudentsToClass(Vector student_ids, String class_id, String teacher_id)
	{
		String class_file = new String(current_dir+File.separator+"files"
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id
				+File.separator+Constants.CLASSES
				+File.separator+class_id+".xml");
		log.add("FileUserUtilitites.addStudentToClass: file "+class_file+" exists? "+new File(class_file).exists());
		JDOMFiles jf = new JDOMFiles(class_file);
		jf.addToClass(student_ids, class_file);
		append(jf.getLog());
	}
	
	/**
	 * 
	 * @param student_ids
	 * @param class_id
	 * @param teacher_id
	 */
	public void addWordsToList(Vector student_ids, String class_id, String teacher_id)
	{
		String class_file = new String(current_dir+File.separator+"files"
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id
				+File.separator+Constants.CLASSES
				+File.separator+class_id+".xml");
		log.add("FileUserUtilitites.addStudentToClass: file "+class_file+" exists? "+new File(class_file).exists());
		JDOMFiles jf = new JDOMFiles(class_file);
		jf.addToClass(student_ids, class_file);
		append(jf.getLog());
	}
	
	public long createNewEntry(String teacher_id, String new_word_list_name, String encoding)
	{
		String teacher_path = new String(current_dir+File.separator+"files"
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id);
		String word_list_path = teacher_path+File.separator+Constants.LISTS+".xml";
		log.add("FileUserUtilitites.createNewWordList: file "+word_list_path+" exists? "+new File(word_list_path).exists());
		JDOMFiles jf = new JDOMFiles(word_list_path);
		long new_id = Domartin.getNewID();
		jf.addToList(new_word_list_name, new_id, word_list_path, encoding);
		// create blank class file named after the id
		String class_file = teacher_path+File.separator+Constants.LISTS
			+File.separator+new_id+".xml";
		jf.createNewDocument(word_list_path, Constants.LISTS);
		append(jf.getLog());
		return new_id;
	}
	
	/**
	*This method returns a list of teacher ids from the files/admin/teachers.xml file/
	*The caller must send the full path to the file in the constructor.
	*/
	public Vector getTeachers()
	{
		String teacher_folder_path = new String(current_dir+File.separator+"files"
			+File.separator+"admin"+File.separator+"teachers.xml");
		log.add("FileUserUtilities.getTeachers: path "+teacher_folder_path);
		File file = new File(teacher_folder_path);
		boolean exists = file.exists();
		log.add("FileUserUtilities.getTeachers: file exists "+exists);
		try
		{
			JDOMFiles jf = new JDOMFiles(teacher_folder_path);
			Vector teachers = jf.getTeacherNames();
			append(jf.getLog());
			return teachers;
		} catch (java.lang.NullPointerException npe)
		{
		
		}
		return new Vector();
	}
	
	/**
	*In the teacher_id's folder is a file classes.xml with the following format:
	* <classes>
	* <class>
	*	<class_id>0000000000000000001</class_id>
	*	<class_name>jukseong5<class_name> 
	* </class>
	* </classes>
	*The class_id element represents a file inside the classes folder which is
	* just a list of student id's for students in each class.
	*This method will return the class_id-class_name pairs as a Hashtable.
	*/
	public Hashtable getClasses(String teacher_id)
	{
		String classes_file = new String(current_dir+File.separator+"files"
			+File.separator+"admin"+File.separator+"teachers"
			+File.separator+teacher_id+File.separator+"classes.xml");
		log.add("FileUserUtilities.getClasses: path "+classes_file);
		File file = new File(classes_file);
		boolean exists = file.exists();
		log.add("FileUserUtilities.getClasses: file exists "+exists);
		JDOMFiles jf = new JDOMFiles(classes_file);
		try
		{
			Hashtable classes = jf.getClassNames();
			append(jf.getLog());
			return classes;
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileUserUtilities.getClasses: npe ");
			append(jf.getLog());
		}
		return new Hashtable();
	}
	
	/**
	*Return a hash of student ids/student names from the file
	* /home/timmy/Desktop/tomcat WEB-INF/files/admin/teachers/<teacher_id>/classes/<class_id>.xml file
	* which has the format:
	*<p><class>
	*  <student>-7028961644117975960</student>
	*	...
	*<p>This method gets a Hash of user_id-user_name pairs from the user.passes files, 
	* and uses these to get the user_names for the list of ids in the class_id.xml file. 
	*/
	public Hashtable getStudents(String teacher_id, String class_id)
	{
		log.add("FileUserUtilities.getStudents");
		Hashtable user_ids = getUserIds();
		dumpHash(user_ids, "user_ids");
		Hashtable name_ids = new Hashtable();
		String class_file = new String(current_dir+File.separator+"files"
			+File.separator+"admin"+File.separator+"teachers"
			+File.separator+teacher_id+File.separator+"classes"+File.separator+class_id+".xml");
		log.add("FileUserUtilities.getStudents: path "+class_file);
		JDOMFiles jf = new JDOMFiles(class_file);
		File file = new File(class_file);
		boolean exists = file.exists();
		log.add("FileUserUtilities.getStudents: file exists "+exists);
		try
		{
			Vector students = jf.getClassIds();
			int size = students.size();
			log.add("FileUserUtilities.getStudents: students.size() "+students.size());
			append(students, "students");
			int i = 0;
			while (i<size)
			{
				String user_id = "-1";
				String user_name = "npe";
				try
				{
					user_id = (String)students.get(i);
				} catch (java.lang.NullPointerException npe)
				{
					// user_id is the prob.
					log.add(" user_id is the prob.");
				}
				try
				{
					user_name = (String)user_ids.get(user_id);
				} catch (java.lang.NullPointerException npe)
				{
					// user_name is the prob.
					log.add(" user_name is the prob.");
				}
				name_ids.put(user_id, user_name);
				i++;
			}
			append(jf.getLog());
			return name_ids;
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileUserUtilities.getStudentss: npe ");
			append(jf.getLog());
		}
		return new Hashtable();
	}
	
	/**
	*Return a hash of student ids-student names from the file
	* /home/timmy/Desktop/tomcat WEB-INF/files/admin/teachers/<teacher_id>/classes/<class_id>.xml file
	* which has the format:
	*<p><class>
	*  <student>-7028961644117975960</student>
	*	...
	*<p>This method gets a Hash of user_id-user_name pairs from the user.passes files, 
	* and uses these to get the user_names for the list of ids in the class_id.xml file. 
	*/
	public Vector getStudentIds(String teacher_id, String class_id)
	{
		Vector student_ids = new Vector();
		String class_file = new String(current_dir+File.separator+"files"
			+File.separator+"admin"+File.separator+"teachers"
			+File.separator+teacher_id+File.separator+"classes"+File.separator+class_id+".xml");
		log.add("FileUserUtilities.getStudents: path "+class_file);
		File file = new File(class_file);
		boolean exists = file.exists();
		log.add("FileUserUtilities.getStudents: file exists "+exists);
		try
		{
			JDOMFiles jf = new JDOMFiles(class_file);
			student_ids = jf.getClassIds();
			return student_ids;
		} catch (java.lang.NullPointerException npe)
		{
			log.add("FileUserUtilities.getStudentss: npe ");
		}
		return new Vector();
	}
	
	/**
	*<p>This method uses the files/user.passes file to make a
	* id-name pair list returned in a Hastable.
	*/
	public Hashtable getUserIds()
	{
		String passes_path = new String(current_dir+File.separator+"files"
			+File.separator+"user.passes");
			
		// debug
		log.add("FileUserUtilities.getUserIds: path "+passes_path);
		File file = new File(passes_path);
		boolean exists = file.exists();
		log.add("FileUserUtilities.getUserIds: file exists "+exists);
		JDOMFiles jf = new JDOMFiles(passes_path);
		Hashtable user_ids = jf.getUserIds();
		log.add("FileUserUtilities.getUserIds: user_ids "+user_ids.size());
		//append(jf.getLog());
		return user_ids;
	}
	
	/**
	 * Trasnlate a student_id - name pair into just a list of student ids.
	 * @param student_id_pairs
	 * @return
	 */
	public Vector getStudentIds(Hashtable student_id_pairs)
	{
		Vector student_ids = new Vector();
		for (Enumeration keys = student_id_pairs.keys(); keys.hasMoreElements() ;) 
		{
			String key = (String)keys.nextElement();
			student_ids.add(key);
		}
		return student_ids;
	}
	
	/**
	*Path: catechis/files/admin/teachers/<teacher_id>/classes/<class_id>.xml
	<class>
		<student>7946057380387841280</student>
		<student>4201001381186235795</student>
		...
	</class>
	*/
	private Hashtable getClass(String teacher_id, String class_id)
	{
		Hashtable teachers_class = new Hashtable();
		String class_file = new String(current_dir+File.separator+"files"
			+File.separator+"admin"+File.separator+"teachers"
			+File.separator+teacher_id+File.separator+"classes"
			+File.separator+class_id+".xml");
		JDOMFiles jf = new JDOMFiles(class_file);
		Vector students = jf.getClassIds();
		// once we have the studnets, use the same loginAdminAction methods
		// to get the same list of info on each student, as well as the same
		// jsp to display the info.
		return teachers_class;
	}
	
	/**
	 * Add an entry in files/admin/teachers/teacher_id/classes.xml
	 * and a blank root  file with the id as the file name like this:
	 * files/admin/teachers/teacher_id/classes/class_id.xml
	 * @param teacher_id
	 * @param class_name
	 * @return
	 */
	public long createClass(String teacher_id, String class_name, String encoding)
	{
		// make a new id and create an entry in the classes file with the id and name.
		long class_id = Domartin.getNewID();
		String classes_dir = new String(current_dir
				+File.separator+Constants.FILES
				+File.separator+Constants.ADMIN
				+File.separator+Constants.TEACHERS
				+File.separator+teacher_id);
		String classes_file = classes_dir+File.separator+"classes.xml";
		log.add("FileUserUtilitites.createClass: file "+classes_file+" exists? "+new File(classes_file).exists());
		JDOMFiles jf = new JDOMFiles(classes_file);
		jf.createClass(class_name, class_id, classes_file, encoding);
		// create blank class file named after the id
		String class_file = classes_dir+File.separator+Constants.CLASSES
			+File.separator+class_id+".xml";
		jf.createClassFile(class_id, class_file);
		append(jf.getLog());
		return class_id;
	}
	
	/**
	 * Add an entry in files/admin/teachers/teacher_id/classes.xml
	 * @param teacher_id
	 * @param class_name
	 * @return
	 */
	public void deleteClass(String teacher_id, long class_id)
	{
		String classes_file = new String(current_dir+File.separator+"files"
				+File.separator+"admin"+File.separator+"teachers"
				+File.separator+teacher_id+File.separator+"classes.xml");
		log.add("FileUserUtilitites.deleteClass: file "+classes_file+" exists? "+new File(classes_file).exists());
		JDOMFiles jf = new JDOMFiles(classes_file);
		jf.deleteClass(class_id, classes_file);
	}
	
	/**
	*Create four folders in the vocab/lists/ folder to hold the last/next tests time objects for both types.
	*/
	private void createTestDateFolders(String lists_folder_path)
	{
		String wltd_folder_path = new String(lists_folder_path+File.separator+"wltd");
		String wltd_folder_type_path = new String(wltd_folder_path+" "+Constants.READING);
		File wltd_folder = new File(wltd_folder_type_path);
		wltd_folder.mkdir();
		wltd_folder_type_path = new String(wltd_folder_path+" "+Constants.WRITING);
		wltd_folder = new File(wltd_folder_type_path);
		wltd_folder.mkdir();
		String wntd_folder_path = new String(lists_folder_path+File.separator+"wntd");
		String wntd_folder_type_path = new String(wntd_folder_path+" "+Constants.READING);
		File wntd_folder = new File(wntd_folder_type_path);
		wntd_folder.mkdir();
		wntd_folder_type_path = new String(wntd_folder_path+" "+Constants.WRITING);
		wntd_folder = new File(wntd_folder_type_path);
		wntd_folder.mkdir();
	}
	
	private void createTestFiles(String user_folder_path)
	{
		for (int i = 0; i<4; i++)
		{
			createTestFile(user_folder_path, i, Constants.READING);
			createTestFile(user_folder_path, i, Constants.WRITING);
		}
	}
	
	/**
	*Create the status.record file in the vocab folder of the new user.
	*/
	public void createStatusFile(String vocab_folder_path)
	{
		String status_record_file = (vocab_folder_path+File.separator+"status.record");
		File status_file = new File(status_record_file);
		try
		{
			status_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			
		}
		JDOMFiles jf = new JDOMFiles(status_record_file);
		jf.createStatusRecord();
		jf.writeDocument(status_record_file);
	}
	
	/**
	*Create two files to hold test info.
	*/
	private void createHistoryFile(String user_folder_path, String type)
	{
		String history_file_path = (user_folder_path+File.separator+"daily "+type+" tests.record");
		File history_file = new File(history_file_path);
		try
		{
			history_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			
		}
		JDOMFiles jf = new JDOMFiles(history_file_path);
		jf.createHistoryRecord();
		jf.writeDocument(history_file_path);
	}
	
	/**
	*Create two files to hold test info.
	* @arg user_folder_path the path to the folder to create the file in.
	* @arg file_name The file name with extension.
	* @arg root_name The name of the root of the xml file.
	*/
	private void createFile(String user_folder_path, String file_name, String root_name)
	{
		String history_file_path = (user_folder_path+File.separator+file_name);
		File history_file = new File(history_file_path);
		try
		{
			history_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			
		}
		JDOMFiles jf = new JDOMFiles(history_file_path);
		jf.createBlankRecord(root_name);
		jf.writeDocument(history_file_path);
	}
	
	/**
	*The options in the individual test files are as such:
	description=Not even really necessary, as all text should be in the bundles...
	type=reading
	length=10	* this is the number of words tested, ten or less by default.
	levels=0-0	* test words between this test range
	index=10	* I have no idea what this is for.  I did make this file format three years ago, so dont u judge ME!
	shuffle=yes	* This definitely is not used, I tried, but you know how it goes...
	*/
	private void createTestFile(String user_folder_path, int i, String type)
	{
		String test_file_path = (user_folder_path+File.separator+"level "+i+" "+type+".test");
		File test_file = new File(test_file_path);
		try
		{
			test_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			log.add("createTestFile "+ioe.toString());
		}
		JDOMFiles jf = new JDOMFiles(test_file_path);
		Hashtable names_values = new Hashtable();
		names_values.put("description", "test");
		names_values.put("type", type);
		names_values.put("length", "10");
		names_values.put("levels", i+"-"+i);
		names_values.put("index", "10");
		names_values.put("shuffle", "yes");
		jf.createLevelTestFiles(i, type, names_values);
		//append(jf.getLog());
		try
		{
			jf.writeDocument(test_file_path);
		} catch (java.lang.NullPointerException npe)
		{
			log.add("unable to write "+test_file_path);
			log.add(npe.toString());
		}
	}
	
	/**
	*The user.options file in each users folder has the following properties
	* held in name=value pairs:
		<p>native_languge=English
		<p>language_being_learned=Korean
		<p>encoding=euc-kr
		<p>locale=ko_KR
		<p>grade_whitespace=false
		<p>max_level=3
		<p>exclude_chars=,
		<p>exclude_area=true
		<p>exclude_area_begin_char=[
		<p>exclude_area_end_char=]
		<p>alternate_answer_separator=/
		<p>exclude_level0_test=1
		<p>exclude_level1_test=15
		<p>exclude_level2_test=60
		<p>exclude_level3_test=100
		<p>daily_words_limit=100
		<p>record_passed_tests=false
		<p>record_failed_tests=true
		<p>record_limit=25
		<p>record_exclude_level=0
		<p>weekly_list_remove_repeats=false
		<p>format_tr_width=140			This is the table row width for a single page format
		<p>format_table_width=340		This option is the width of an individual table in a single page format.
		<p>format_table_limit=14		This is the number of words to include in each table, less the number of wrapped lines.
		<p>format_tr_pixels=75			This is used in the FormatPanel to judge the number of pixels before a string wraps happens in a bable row
		<p>These options turn on or off the lists that will be printed for instance in the weekly_list_format.jsp page.
		<p>format_print_reading=true		
		<p>format_print_writing=false
		<p>format_print_missed_reading=true
		<p>format_print_missed_witing=false
	*/
	private void createUserOptions(String user_folder_path, String user_name)
	{
		String options_file_path = (user_folder_path+File.separator+user_name+".options");
		File options_file = new File(options_file_path);
		try
		{
			options_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			log.add(ioe.toString());
		}
		JDOMFiles jf = new JDOMFiles(options_file_path);
		Hashtable options_values = new Hashtable();
		options_values.put("native_languge", "English");
		options_values.put("language_being_learned", "Korean");
		options_values.put("encoding", "euc-kr");
		options_values.put("locale", "ko_KR");
		options_values.put("subject", "vocab");
		options_values.put("grade_whitespace", "false");
		options_values.put("max_level", "3");
		options_values.put("exclude_chars", ",");
		options_values.put("exclude_area", "true");
		options_values.put("exclude_area_begin_char", "[");
		options_values.put("exclude_area_end_char", "]");
		options_values.put("alternate_answer_separator", "/");
		options_values.put("exclude_level0_test", "1");
		options_values.put("exclude_level1_test", "15");
		options_values.put("exclude_level2_test", "60");
		options_values.put("exclude_level3_test", "100");
		options_values.put("daily_words_limit", "100");
		options_values.put("record_passed_tests", "false");
		options_values.put("record_failed_tests", "true");
		options_values.put("record_limit", "25");
		options_values.put("record_exclude_level", "0");
		options_values.put("weekly_list_remove_repeats", "false");
		options_values.put("reading_before_writing", "true");
		options_values.put("fail_back_to_0", "false");
		jf.createOptions(options_values);
		//append(jf.getLog());
		jf.writeDocument(options_file_path);
	}
	
	/**
	*This registers the date of creation, as well as zero values for all history elements.
	*/
	private void createFirstHistoryRecord(String file_user_path, String user_name)
	{
		String history_path = new String(file_user_path+File.separator+user_name+".hist");
		File history_file = new File(history_path);
		String date = Domartin.getCurrentFormattedDate();
		FileStorage store = new FileStorage();
		JDOMSolution jdom = new JDOMSolution(history_file, store);
		AllStatsHistory all_stats_history = new AllStatsHistory();
		all_stats_history.setDate(date);
		all_stats_history.setNumberOfTests(0);
		all_stats_history.setAverageScore(00);
		all_stats_history.setNumberOfWords(0);
		all_stats_history.setWritingAverage(0.0);
		all_stats_history.setReadingAverage(0.0);
		all_stats_history.setSessionStart(0);
		all_stats_history.setSessionEnd(0);
		all_stats_history.setNumberOfSessionTests(0);
		Vector writing_levels = new Vector();
		Vector reading_levels = new Vector();
		int i = 0;
		while(i<4)
		{
			reading_levels.add(i, Integer.toString(0));
			writing_levels.add(i, Integer.toString(0));
			i++;
		}
		all_stats_history.setReadingLevels(reading_levels);
		all_stats_history.setWritingLevels(writing_levels);
		jdom.addHistory(all_stats_history);
		jdom.writeDocument(history_path);
	}
	
	public void deleteUser(String user_name)
	{
		String file_name = "user.passes";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		JDOMFiles jf = new JDOMFiles(file_path);
		jf.deleteUserPass(user_name);
		jf.writeDocument(file_path);
		file_path = new String(current_dir+File.separator+"files"+File.separator+user_name);
		File folder = new File(file_path);
		folder.delete();
	}
	
	public boolean checkInvitationCode(String current_dir, String users_code)
	{
		String file_name = "user.passes";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		JDOMFiles jf = new JDOMFiles(file_path);
		String invitation_code = jf.getInvitationCode();
		if (users_code.equals(invitation_code))
		{
			return true;
		}
		return false;
	}
	
	/**
	*The tags kept in this file are created in FileJDOMWordLists.addToRetiredWordsList like this:
	*<p><new_word>
	*<p><id>602368600589700127</id>
	*<p><primary_path>old words.xml</primary_path> This is the words file category, not really a path...
	*<p><text>¿ÀÂ¡¾î</text>
	*<p><definition>squid</definition>
	*<p></new_word>
	*<p>Right now if a user edits a text or a definition, every word in the lists folder, should also be updated.
	*<p>The thing is, these files came from the idea of recording tests, and then only recording missed word test greater than level 0.
	*<p>Then the comceptual dilemma comes, if a user edits the text or definition contents, should test histories be updated?
	*<p>I know what your saying, there should be no text or def tags in these files, only id that would link to the master word file.
	*<p>The problem with this is that right now, late 2008, to make a list of words to study like WeeklyWordListAction does,
	*<p> about 200 to 400 files would have to be opened to make the list, which is just too darn much.
	*<p>So hire me a dba to setup MySql and Hibernate for me.  I have programming to do!
	*/
	public void createNewWordsListFiles(String lists_folder_path)
	{
		String reading_file_name = "new words "+Constants.READING+".list";
		String reading_file_path = (lists_folder_path+File.separator+reading_file_name);
		createNewFile(reading_file_path);
		JDOMFiles jf = new JDOMFiles(reading_file_path);
		jf.createNewXMLFile("new_words");
		jf.writeDocument(reading_file_path);
		log.add("created "+reading_file_path);
		//append(jf.getLog());
		
		String writing_file_name = "new words "+Constants.WRITING+".list";
		String writing_file_path = (lists_folder_path+File.separator+writing_file_name);
		createNewFile(writing_file_path);
		JDOMFiles writing_jf = new JDOMFiles(writing_file_path);
		writing_jf = new JDOMFiles(writing_file_path);
		writing_jf.createNewXMLFile("new_words");
		writing_jf.writeDocument(writing_file_path);
		//append(jf.getLog());
		log.add("created "+writing_file_path);
	}
	
	/**
	*The tags kept in this file are the same asin a new words -type-.list file
	* and are created in FileJDOMWordLists.addToRetiredWordsList
	*/
	public void createRetiredWordsListFile(String lists_folder_path)
	{
		String file_name = "retired words.list";
		String file_path = new String(lists_folder_path+File.separator+file_name);
		createNewFile(file_path);
		JDOMFiles jf = jf = new JDOMFiles(file_path);
		jf.createNewXMLFile("retired_words");
		jf.writeDocument(file_path);
		log.add("created "+file_path);
		append(jf.getLog());
	}
	
	public void createNewFile(String path)
	{
		File file = new File(path);
		try
		{
			file.createNewFile();
			log.add("FileUserUtilities.createNewFile: created "+path);
		} catch (java.io.IOException ioe)
		{
			log.add("FileUserUtilities.createNewFile: failed to create "+path);
			log.add(ioe.toString());
			log.add("FileUserUtilities.createNewFile: end of error ");
		}
	}
	
	/**
	*These options files are used by actions to create features for specific pages.
	*/
	private void createOptionsFolders(String options_folder, String id, String subject)
	{
		String jsp_options = (options_folder+File.separator+"jsp_options");
		File file = new File(options_folder);
		file.mkdir();
		File file2 = new File(jsp_options);
		file2.mkdir();
		createJSPWeeklyListFormatOptions(jsp_options);
		createJSPDailyTestResultFormatOptions(jsp_options);
	}
	
	public void createUserNameIndexFile(String current_dir, String user_name, String id)
	{
		FileUserOptions fuo = new FileUserOptions(current_dir);
		fuo.addUserNameIndex(user_name, id);
	}
	
	/**
	*Tis asks the JDOMFiles getUserId method to search the /files/user.passes file 
	*and find the users name and return the id for that user.
	*/
	public String getId(String user_name)
	{
		String user_id = new String();
		String file_name = "user.passes";
		String file_path = new String(current_dir+File.separator+"files"+File.separator+file_name);
		log.add(file_path);
		JDOMFiles jf = jf = new JDOMFiles(file_path);
		user_id = jf.getUserId(user_name); 
		append(jf.getLog());
		return user_id;
	}
	
		/**
	*The optins folder has the following properties
	* held in name=value pairs:
		<p>format_tr_width=140			This is the table row width for a single page format
		<p>format_table_width=340		This option is the width of an individual table in a single page format.
		<p>format_table_limit=14		This is the number of words to include in each table, less the number of wrapped lines.
		<p>format_tr_pixels=75			This is used in the FormatPanel to judge the number of pixels before a string wraps happens in a bable row
		<p>These options turn on or off the lists that will be printed for instance in the weekly_list_format.jsp page.
		<p>format_print_reading=true		
		<p>format_print_writing=false
		<p>format_print_missed_reading=true
		<p>format_print_missed_witing=false
	*/
	private void createJSPWeeklyListFormatOptions(String jsp_options_folder_path)
	{
		String options_file_path = (jsp_options_folder_path+File.separator+"weekly_list_format.options");
		File options_file = new File(options_file_path);
		try
		{
			options_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			log.add(ioe.toString());
		}
		JDOMFiles jf = new JDOMFiles(options_file_path);
		Hashtable options_values = new Hashtable();
		options_values.put("format_tr_width", "140");
		options_values.put("format_table_width", "340");
		options_values.put("format_table_limit", "14");
		options_values.put("format_tr_pixels", "14");
		options_values.put("format_print_reading", "true");		
		options_values.put("format_print_writing", "false");
		options_values.put("format_print_missed_reading", "true");
		options_values.put("format_print_missed_writing", "false");
		options_values.put("daily_list_print_new_words_reading", "true");
		options_values.put("daily_list_print_new_words_writing", "false");
		jf.createOptions(options_values);
		//append(jf.getLog());
		jf.writeDocument(options_file_path);
	}
	
	/**
	* The saved subj/tests/saved tests.xml is just an id and name value pairs.
	* <saved_tests>
	*	<saved_test>
	*		<test_id></test_id>
	*		<test_name></test_name>
	*		<test_date></test_date>
	*		<test_type></test_type>
	*		<test_format></test_format>
	*		<creation_time></creation_time>
	*	</saved_test>
	* </saved_tests>
	* The class org.catechis.dto.SavedTest is a bean that carries an instance of a saved_test element.
	*
	* The test_format depends on the action creating it, such as
	* DailyTest, IntegratedTest, SpecificTest.
	* The files inside can have a format like this:
	* The files will be named after the id.
	*<daily_test>
	* 	<test_item>
	*		<index></index>
	*		<id></id>
	*		<category></category>
	*		<text></text>	
	*		<definition></definition>
	* 	</test_item>
	* </daily_test>
	*
	* <p>The method is public so that we can retroactively add this file to old users if necessary.
	*/
	public void createSavedTestsFile(String tests_folder_path)
	{
		String file_name = "saved tests.xml";
		String file_path = new String(tests_folder_path+File.separator+file_name);
		log.add("fut.createSavedTestsFile: Trying "+file_path);
		createNewFile(file_path);
		JDOMFiles jf = jf = new JDOMFiles(file_path);
		jf.createNewXMLFile("saved_tests");
		jf.writeDocument(file_path);
		log.add("created "+file_path);
		//append(jf.getLog());
	}
	
	/**
	 * Create a blank categories index file to hold this info:
	 * <categories>
	 * 	<category>
	 * 		<id/>
	 * 		<name/>
	 * 		<words/>
	 * 	<category>
	 *  ...
	 * </categories>
	 * @param vocab_folder_path
	 */
	public void createCategoriesFile(String vocab_folder_path)
	{
		String file_name = "categories.xml";
		String file_path = new String(vocab_folder_path+File.separator+file_name);
		log.add("fut.createSavedTestsFile: Trying "+file_path);
		createNewFile(file_path);
		JDOMFiles jf = jf = new JDOMFiles(file_path);
		jf.createNewXMLFile("categories");
		jf.writeDocument(file_path);
		log.add("created "+file_path);
		//append(jf.getLog());
	}
	
	/**
	*Default options:
	*format_print_missed_reading=true
	*format_print_missed_writing=true
	*record_failed_tests=>true
	*record_passed_tests=true
	*record_exclude_level=0
	*record_limit=17
	*/
	private void createJSPDailyTestResultFormatOptions(String jsp_options_folder_path)
	{
		String options_file_path = (jsp_options_folder_path+File.separator+"daily_test_result.options");
		File options_file = new File(options_file_path);
		try
		{
			options_file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			log.add(ioe.toString());
		}
		JDOMFiles jf = new JDOMFiles(options_file_path);
		Hashtable options_values = new Hashtable();
		options_values.put("format_print_missed_reading", "true");
		options_values.put("format_print_missed_writing", "false");
		options_values.put("record_failed_tests", "true");
		options_values.put("record_passed_tests", "true");
		options_values.put("record_exclude_level", "0");		
		options_values.put("record_limit", "17");
		jf.createOptions(options_values);
		//append(jf.getLog());
		jf.writeDocument(options_file_path);
	}
	
	public long getNewUserId()
	{
		return id;
	}
	
	public void changeOption(Vector users, String property, String value)
	{
		log.add("FileUserUtilities.changeOption");
		int size = users.size();
		int i = 0;
		while (i<size)
		{
			String user_id = (String)users.get(i);
			String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
			String options_file_path = (user_folder_path+File.separator+user_id+".options");
			log.add("FileUserUtilities.changeOption: path "+options_file_path);
			JDOMFiles jf = new JDOMFiles(options_file_path);
			jf.changeOption(property, value);
			jf.writeDocument(options_file_path);
			i++;
			append(jf.getLog());
		}
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
	
	public void append(Vector v, String title)
	{
		log.add("=================_ "+title+" _=================");
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add(v.get(i));
			i++;
		}
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
	
	private void append(File [] files)
	{
		int length = files.length;
		int i = 0;
		while (i<length)
		{
			log.add(files[i]);
			i++;
		}
	}
	
	private void dumpHash(Hashtable hash, String title)
	{
		log.add("=================_ "+title+" _=================");
	    Enumeration keys = hash.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)hash.get(key);
		    log.add(key+" - "+val);
	    }
	}
	
}
