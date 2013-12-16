package org.catechis.juksong;

import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.constants.Constants;
import org.catechis.gwangali.ROTConstants;
import org.catechis.dto.Word;
import org.catechis.dto.WordLastTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.file.FileJDOMWordLists;

/**
 * This class was created to provide patches for Catechis packages other than Juksong or Gwangali
 * both of which are managed in separate locations than the core Catechis packages.
 * The first patch is the method revrseUpdateTestDateRecords() which uses the TimeTestMeory and
 * a WordTestResult object to reverse changes made in the test lists.
 * @author a
 *
 */
public class ThePatches 
{

	/**
	 * Reverse what WordLastTestDates.updateTestDateRecords does.
	 * The wntd_file_key might be different this time around, depending on the
	 * getUniqueFile method.
	 * 
	 * WordTestMemory
    private String category;      ***
    private String type;        ***
    private String date;        ***
    private String score;        SET LATer
    private String index;        *** -1
    private String number_correct;    ?
    private String level;        ?
    private String test_name;    needed
    long word_id
    
WordTestResult
    private String text;
    private String definition;
    private String answer;
    private String grade;
    private String level;
    private int id;
    private String original_text;
    private String original_definition;
    private String original_level;
    private String encoding;
    private String date;
    long word_id
	 * @param ttm
	 */
	public TestTimeMemory revrseUpdateTestDateRecords(String user_id, String category, 
			String subject, TestTimeMemory ttm, WordTestResult wtr, String current_dir,
			Vector elt_vector)
	{
		String last_date = ttm.getOriginalWLTD();
		Word word = new Word(); // we only need the category and id and use a word for backwards compatibility...
		word.setId(wtr.getWordId());
		word.setCategory(category);
		WordLastTestDates wltds = new WordLastTestDates();
		
		// this is in the TestTimeMeory object
		String new_wltd = ttm.getNewWLTD();  // delete
		String new_wntd = ttm.getNewWNTD();  // delete
		String org_wltd = ttm.getOriginalWLTD();  // recreate
		String org_wntd = ttm.getOriginalWNTD();  // recreate
		String type = ttm.getType(); // don't need it
		// this is in the WordTestResult object
		String test_date = wtr.getDate();
		String word_id = ""+wtr.getWordId();
		String word_lvl = wtr.getLevel();
		
		// delete files that deserve it
		String user_folder_path = current_dir+File.separator+"files"+File.separator+user_id;
		boolean old_wltd = deleteEntry(new_wltd, "wltd", type, subject, user_id, user_folder_path);
		boolean old_wntd = deleteEntry(new_wltd, "wntd", type, subject, user_id, user_folder_path);
		
		// recreate files that should be recreated
		String new_wltd_file_key = createWLTDEntry(test_date, word, subject, type, user_id, user_folder_path); 
		String wntd_file_key = createWNTDEntryAndReturnMemory(test_date, Long.parseLong(word_id), 
				category, user_id, user_folder_path, new_wltd_file_key, Integer.parseInt(word_lvl), elt_vector, subject, type);		
		
		// create a new ttm that is a reverse of the old, in case the user changes their mind AGAIN!
		TestTimeMemory new_ttm = new TestTimeMemory();
		new_ttm.setOriginalWLTD(new_wltd);
		new_ttm.setNewWLTD(test_date);
		new_ttm.setOriginalWNTD(new_wntd);
		new_ttm.setNewWNTD(wntd_file_key);
		new_ttm.setType(type);
		
		//report
		//log.add("udapteTestDateRecords.user_folder_path: "+user_folder_path);
		//log.add("udapteTestDateRecords.new_date: "+new_date+" from test_date "+test_date);
		//log.add("udapteTestDateRecords.new_wltd_file_key: "+new_wltd_file_key);
		
		return new_ttm;
	}
	
	public boolean deleteEntry(String wtd, String last_or_next, 
			String type, String subject, String user_id, String user_folder_path)
	{
		String vocab_folder_path = (user_folder_path+File.separator+subject);
		String lists_folder_path = (vocab_folder_path+File.separator+"lists");
		String wtd_folder_path = (lists_folder_path+File.separator+last_or_next+" "+type);
		String wtd_file_path = (wtd_folder_path+File.separator+"wltd "+wtd+".xml");
		File wtd_file = new File(wtd+wtd_file_path);
		boolean deleted = wtd_file.delete();
		return deleted;
	}
	
	public String createWLTDEntry(String last_date, Word word, 
			String subject, String type, String user_name, String user_folder_path)
	{
		long milliseconds = Domartin.getMilliseconds(last_date);
		String vocab_folder_path = (user_folder_path+File.separator+subject);
		String lists_folder_path = (vocab_folder_path+File.separator+"lists");
		String wltd_folder_path = (lists_folder_path+File.separator+"wltd "+type);
		String wltd_file_key = getUniqueFile(milliseconds, wltd_folder_path);
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wltd_folder_path);
		fjdomwl.createLastTestDateFile(word.getId(), word.getCategory(), wltd_file_key);
		return wltd_file_key;
	}
	
	/**
	*Same as createWNTDEntry but returns the new time file name.
	*/
	public String createWNTDEntryAndReturnMemory(String last_date, long word_id, String category, String user_name, String user_folder_path, 
		String wltd_file_key, int word_lvl, Vector elt_vector, String subject, String type)
	{
		long milliseconds = Domartin.getMilliseconds(last_date);
		if (milliseconds == 0)
		{
			milliseconds = Long.parseLong(last_date);
		}
		if (subject == null) {subject = Constants.VOCAB;}
		String vocab_folder_path = (user_folder_path+File.separator+subject);
		String lists_folder_path = (vocab_folder_path+File.separator+"lists");
		String wntd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
		String wntd_not_unique = getTimeTilNextTest(word_lvl, milliseconds, elt_vector);
		String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
		FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
		fjdomwl.createNextTestDateFile(word_id, category, wntd_file_key, wltd_file_key);
		return wntd_file_key;
	}
	
	/**
	*Same as getUniqueKey except instead of keys the files in the /vocab/lists/wltd/ folder
	* there are files named after the milliseconds.
	*/
	private String getUniqueFile(long milliseconds, String folder)
	{
		long this_milliseconds = milliseconds;
		Long long_key = new Long(this_milliseconds);
		String result = long_key.toString();
		String path_to_wltd = (folder+File.separator+long_key.toString()+".xml");
		File file = new File(path_to_wltd);
		if (file.exists())
		{
			this_milliseconds = this_milliseconds+1;
			result = getUniqueFile(this_milliseconds, folder);
		} else
		{
			return long_key.toString();
		}
		return result; 
	}
	
	/**
	*Add the exclude date to the last test date to decide when the next test should be.
	*<p>This version of the method is for when we don't have the word object.
	*/
	private String getTimeTilNextTest(int word_level, long milliseconds, 
			Vector elt_vector)
	{
		String o = (String)elt_vector.get(word_level);
		int i = Integer.parseInt(o);
		int screen_lvl = i;
		long exclude_time = (screen_lvl*ROTConstants.DAY);
		Date now = new Date();
		long this_instant = now.getTime();
		long exclude_period = (milliseconds + exclude_time);
		return (new Long(exclude_period).toString());
	}
}
