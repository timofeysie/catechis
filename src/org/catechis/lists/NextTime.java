package org.catechis.lists;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.catechis.Storage;
import org.catechis.constants.Constants;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.gwangali.ROTConstants;
import org.catechis.juksong.TestDateEvaluator;

/**
 * This class provides utilities for creating and working with test lists.
 * <p>The createNewLists method was taken from org.catechis.dto.WordLastTestDates.
 * @author timmy
 *
 */
public class NextTime 
{
    
    NextTime()
    {
    }
    
	/**
     *
    * This utility creates a file named after the words last test date in milliseconds 
    * (reading or writing type specific)
    * which contains the word id and the words category file name.
    * <p>Old notes:
    * @param Vector elt_reading_vector is the Elapsed Level Time vector for reading times.
    * @param elt_writing_vector for writing times.  These give the times till the next test by level.
    *<p>the return argument is stored in the wntd file so that both files can be changed later.
    *<p>This method caused a lot of problems for us.  We created the whole quaquaverse 0.4/0.5
    * app just to track down all our problems to this spot.
    *<p> Based on createLists in org.catechis.dto.WordLastTestDates but uses longs instead of 
    * strings for the dates.
    */
    public void createNewLists(Vector words, UserInfo user_info, 
    		Vector elt_reading_vector, Vector elt_writing_vector)
    {
    	String test_list_folder = makePathToLists(user_info);
        int words_in_this_cat = words.size();
        int j = 0;
        while (j<words_in_this_cat)
        {
          	Word word = (Word)words.get(j);
           	if (!word.getRetired()==false)
           	{
           		long id = word.getId();
           		TestDateEvaluator tde = new TestDateEvaluator();
           		tde.evaluteLastTestDates(word);
           		long last_reading_date = tde.getReadingTime();
           		long last_writing_date = tde.getWritingTime();
           		long doe = word.getDateOfEntry(); // increment writing date 1 hour for new words
           		if (last_reading_date == doe)
           		{
           			last_writing_date = last_writing_date + ROTConstants.HOUR;
           		}
           		String wltd_file_key_r = createWLTDEntry(last_reading_date, word, user_info.getSubject(), 
           				test_list_folder, Constants.READING);  // the return argument is stored in the wntd file so that both files can be changed later.
           		createWNTDEntry(last_reading_date, word, user_info.getSubject(), test_list_folder,
           				Constants.READING, wltd_file_key_r, elt_reading_vector);
           		String wltd_file_key_w = createWLTDEntry(last_writing_date, word, user_info.getSubject(), 
           				test_list_folder, Constants.WRITING);
           		createWNTDEntry(last_writing_date, word, user_info.getSubject(), test_list_folder, 
           				Constants.WRITING, wltd_file_key_w, elt_writing_vector);
           	} 
           	j++;
        }
    }
    
    /**
     * *Based on createWLTDEntry but with a long passed in directly.
    *Create a file entry in user_id_folder/vocab/lists/wldt *type* folder for the last test.
    */
    public String createWLTDEntry(long last_date, Word word, 
    		String test_list_folder, String subject, String type)
    {
        String wltd_folder_path = (test_list_folder+File.separator+" "+type);
        String wltd_file_key = getUniqueFile(last_date, wltd_folder_path);
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wltd_folder_path);
        fjdomwl.createLastTestDateFile(word.getId(), word.getCategory(), wltd_file_key);
        return wltd_file_key;
    }
    
    /**
     *Create a file entry for the next test in user_id_folder/vocab/lists/wndt *type* folder.
     */
     public void createWNTDEntry(long last_date, Word word, String subject, 
    		 String test_list_folder, String type, String wltd_file_key,
    		 Vector elt_vector)
     {
         String wntd_folder_path = (test_list_folder+File.separator+" "+type);
         String wntd_not_unique = getTimeTilNextTest(word, last_date, type, elt_vector);
         String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
         FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
         fjdomwl.createNextTestDateFile(word.getId(), word.getCategory(), wntd_file_key, wltd_file_key);
     }
      
      /**
       * <p>We check the folder/file.xml passed in to see if it exists,
       * if it does, then we increment the milliseconds by one and check
       * repeatedly until we get a unique file name to return.
      *<p>Same as getUniqueKey in org,catechis.dto,WordNextTestDates 
      * except instead of keys the files in the /vocab/lists/wltd/ folder
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
       */
       private String getTimeTilNextTest(Word word, long milliseconds, String type, 
    		   Vector elt_vector)
       {
           int word_level = 0;
           if (type.equals("reading"))
           {
               word_level = word.getReadingLevel();
           } else if (type.equals("writing"))
           {
               word_level = word.getWritingLevel();
           }
           if (word_level>3)
           {
               word_level=3;
               //log.add("WLTD level over 3 for word "+word.getText()+" "+word.getDefinition());
           }
           String o = (String)elt_vector.get(word_level);
           int i = Integer.parseInt(o);
           int screen_lvl = i;
           long exclude_time = (screen_lvl*86400000); // one day in milliseconds
           Date now = new Date();
           long this_instant = now.getTime();
           long exclude_period = (milliseconds + exclude_time);
           return (new Long(exclude_period).toString());
       }
       
       /**
        * Creates a path to the test lists folder.
        * @param root_path
        * @param user_id
        * @param subject
        * @return "root/files/id/subj/lists/wltd" so the user can add " "+type.
        */
       private String makePathToLists(UserInfo user_info)
       {
       	   String files_folder_path = new String(user_info.getRootPath()+File.separator+Constants.FILES);
       	   String user_folder_path = (files_folder_path+File.separator+user_info.getUserId());
           String vocab_folder_path = (user_folder_path+File.separator+user_info.getSubject());
           String lists_folder_path = (vocab_folder_path+File.separator+"lists");
           String wltd_folder_path = (lists_folder_path+File.separator+"wltd");
           return wltd_folder_path;
       }
	
}
