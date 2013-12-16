package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Set;
import java.util.Date;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import org.catechis.Storage;
import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.WordTestDateUtility;
import org.catechis.constants.Constants;
import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.WordNextTestDate;
import org.catechis.file.FileCaretaker;
import org.catechis.file.WordNextTestDates;
import org.catechis.file.FileJDOMWordLists;
import org.catechis.gwangali.ROTConstants;
import org.catechis.juksong.TestTimeMemory;
import org.catechis.juksong.TestDateEvaluator;
import org.catechis.interfaces.Caretaker;

/**
This class stores a hash of Word objects using a test date as a key.
It can return a sorted list of these words.
It is up to the calling code to determine what type (reading/writing) of words are added, etc.
*/
public class WordLastTestDates
{
    
    private Hashtable ltd_hash;
    private Vector elt_vector;
    private String type;
    // These methods mark where we are in the list of categories and words so we can come back to the same spot later.
    private int limit_of_words;
    private int limit_i;
    private int limit_j;
    private boolean limit;
    /**Starting with vocab, there could be grammar, etc. Check Constants for those available.*/
    private String subject;
    
    /**This was for backwards compatibility */
    private String backup_directory;
    
    private boolean exclude_retired_words;
    private int number_of_retired_words;
    private int number_of_total_words;
    private final long day = 86400000;
    
    /**
    *This is a backup so that the Caretaker can use this value without changing the interface.
    */
    private String wntd_file_key_backup;

    public WordLastTestDates()
    {
        ltd_hash = new Hashtable();
        /** Exclude level times */
        elt_vector = new Vector();
        /** */
        limit_of_words = 0;
        limit_i = 0;
        limit_j = 0;
        Storage store;
        /* degugging */
        log = new Vector();
        limit = false;
        exclude_retired_words = true;
        number_of_retired_words = 0;
        number_of_total_words = 0;
    }
    
    /**
    *<p>Add the word using the passed in date turned into milliseconds as the key.
    *<p>If the key already exists in the list because many words were tested in the same group of ten
    * for instance, then we add one to the milliseconds to differentiate it.  We do this recursively
    * until we find a new unused key.
    *<p>We must:
    *<p>know the type, reading or writing to use the exclude level times
    *<p>times each day by the static day member.
    *<p>If type is null then we add the word as-is.
    *Otherwise we exclude the word if it is in the exclude time period.
    *<p>For instance, if the standard value for level zero exclude date is set to one,
    * and the type is set to reading, and the word is at reading level zero and has been
    * tested in the last 24 hours, then that word is skipped.
    *<p>If the limit of words value is set, no more words are added after the initial check.
    // got exclude value for that date
    // exclude value times 86400000 milliseconds in a day
    // get currant time
    // minus the exclude time
    // if the word being passed in has a key in this period
    // do nothing to exclude this word
    // add it to the list
    */
    public void addWord(Word word, String last_test_date)
    {
        if (ltd_hash.size()>limit_of_words)
        {
            if (limit==true)
            {
                /*dont add word*/
            } else {
                //log.add("WLTD limit of words reached ");limit=true;
            }
        } else
        {
            long milliseconds = Domartin.getMilliseconds(last_test_date);
            if (type==null)
            {
                Long long_key = getUniqueKey(milliseconds);
                ltd_hash.put(long_key, word);
                //log.add("WLTD added with "+milliseconds);
            } else if (word.getRetired()==true)
            {
                    //log.add("WLTD retired "+word.getText()+" "+word.getDefinition());
                    number_of_retired_words++;
            } else
            {
                int word_level = 0;
                if (type.equals("reading"))
                {word_level = word.getReadingLevel();
                } else if (type.equals("writing"))
                {word_level = word.getWritingLevel();}
                if (word_level>3)
                {word_level=3;log.add("WLTD level over 3 for word "+word.getText()+" "+word.getDefinition());}
                String o = (String)elt_vector.get(word_level);
                int i = Integer.parseInt(o);int screen_lvl = i;
                long exclude_time = (screen_lvl*day);
                Date now = new Date();
                long this_instant = now.getTime();
                long exclude_period = (this_instant - exclude_time);
                if (milliseconds > exclude_period)
                {

                    //log.add(type+" excluded "+word.getText()+" "+word.getDefinition()+" "+(milliseconds - exclude_period));
                    
                    /*log.add("word_level "+word_level);
                    log.add("elt_vector "+o);
                    log.add("exclude_time["+exclude_time+"] = (screen_lvl*day) ["+screen_lvl+"]*["+day+"]");
                    log.add("(this_instant - exclude_time) = "+(this_instant - exclude_time)+" ["+this_instant+" - "+exclude_time+"]");
                    log.add("milliseconds > exclude_period "+milliseconds+" > "+exclude_period);
                    */
                } else
                {
                    Long long_key = getUniqueKey(milliseconds);
                    ltd_hash.put(long_key, word);
                    //log.add("WLTD added "+word.getText()+" "+word.getDefinition()+" at "+milliseconds);
                }
            }
        }
    }
    
    /**
    *<p>Same as addWord except that this method checks the words last test date
    * to see if it is within the exclude level period, in which case it will
    * not be included in the list.
    *<p>Add the word using the passed in date turned into milliseconds as the key.
    *<p>If the key already exists in the list because many words were tested in the same group of ten
    * for instance, then we add one to the milliseconds to differentiate it.  We do this recursively
    * until we find a new unused key.
    *<p>We must:
    *<p>know the type, reading or writing to use the exclude level times
    *<p>times each day by the static day member.
    *<p>If type is null then we add the word as-is.
    * Otherwise we exclude the word if it is in the exclude time period. This happens in the setWord method)
    *<p>For instance, if the standard value for level zero exclude date is set to one,
    * and the type is set to reading, and the word is at reading level zero and has been
    * tested in the last 24 hours, then that word is skipped.
    *<p>If the limit of words value is set, no more words are added after the initial check.
    // got exclude value for that date
    // exclude value times 86400000 milliseconds in a day
    // get currant time
    // minus the exclude time
    // if the word being passed in has a key in this period
    // do nothing to exclude this word
    // add it to the list
    */
    public void addWordOrExclude(Word word, String last_test_date)
    {
        //log.add("2 WLTD recieved "+word.getText()+" "+word.getDefinition()+" ltd_hash_size "+ltd_hash.size());
        if (ltd_hash.size()>limit_of_words)
        {
            if (limit==true)
            {
                /*dont add word*/
            } else {
                //log.add("WLTD limit of words reached ");limit=true;
            }
        } else
        {
             if (word.getRetired()==true)
             {
                 //log.add("WLTD retired "+word.getText()+" "+word.getDefinition());
             } else
             {
                 long milliseconds = Domartin.getMilliseconds(last_test_date);
                 if (type==null)
                 {
                     Long long_key = getUniqueKey(milliseconds);
                     ltd_hash.put(long_key, word);
                     //log.add("WLTD added with "+milliseconds);
                 } else
                 {
                     setWord(word, milliseconds);
                 }
             }
        }
    }
    
    /**
    *Apply the exclude level time vector to find out if the word should be added to the list or not.
    *<p>    Hashtable ltd_hash, Vector elt_vector, String type, int limit_of_words;
    */
    private void setWord(Word word, long milliseconds)
    {
        int word_level = 0;
        if (type.equals("reading"))
        {word_level = word.getReadingLevel();
        } else if (type.equals("writing"))
        {word_level = word.getWritingLevel();}
        if (word_level>3)
        {
            word_level=3;    // log.add("WLTD level over 3 for word "+word.getText()+" "+word.getDefinition());
        }
        String o = (String)elt_vector.get(word_level);
        int i = Integer.parseInt(o);int screen_lvl = i;
        long exclude_time = (screen_lvl*day);
        Date now = new Date();
        long this_instant = now.getTime();
        long exclude_period = (this_instant - exclude_time);
        //log.add("WLTD retired "+word.getRetired());
        if (milliseconds > exclude_period)
        {
            //log.add("b WLTD excluded "+word.getText()+" "+word.getDefinition());
            //printTests(word);
                /*(String def = word.getDefinition();if (def.equals("department")){printTests(word);}*/
        } else
        {
            Long long_key = getUniqueKey(milliseconds);
            ltd_hash.put(long_key, word);
            //log.add("WLTD added "+word.getText()+" "+word.getDefinition()+" at "+milliseconds);
        }
    }
    
    /**
    *Add the exclude date to the last test date to decide when the next test should be.
    */
    private String getTimeTilNextTest(Word word, long milliseconds)
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
        long exclude_time = (screen_lvl*day);
        Date now = new Date();
        long this_instant = now.getTime();
        long exclude_period = (milliseconds + exclude_time);
        /*
        log.add("------------------------------------------------------------------------------");
        log.add("level "+word_level+" WLTD "+Transformer.getLongDateFromMilliseconds(Long.toString(milliseconds)));
        log.add("elt   "+screen_lvl+" WNTD "+Transformer.getLongDateFromMilliseconds(Long.toString(exclude_period)));
        log.add("(screen_lvl*day) "+screen_lvl+"*"+day+"="+exclude_time);
        log.add("day "+Domartin.getElapsedTime(new Long(day).toString()));
        long delta = exclude_period-milliseconds;
        log.add("delta "+Domartin.getElapsedTime(new Long(delta).toString()));
        log.add("------------------------------------------------------------------------------");
        */
        return (new Long(exclude_period).toString());
    }
    
    /**
    *Add the exclude date to the last test date to decide when the next test should be.
    *<p>This version of the method is for when we don't have the word object.
    */
    private String getTimeTilNextTest(int word_level, long milliseconds)
    {
        String o = (String)elt_vector.get(word_level);
        int i = Integer.parseInt(o);
        int screen_lvl = i;
        long exclude_time = (screen_lvl*day);
        Date now = new Date();
        long this_instant = now.getTime();
        long exclude_period = (milliseconds + exclude_time);
        return (new Long(exclude_period).toString());
    }
    
    public ArrayList getSortedWLTDKeys()
    {
         Set set = ltd_hash.keySet(); // Returns a Set view of the keys contained in this Hashtable.
         ArrayList ltd_milliseconds = new ArrayList(set);
         ArrayList sortedKeys = Domartin.sortList(ltd_milliseconds);
         return sortedKeys;
    }
    
    /**
    *If there is already a key with the exact same time, add 1 millisecond until
    * the key is unique.
    *<p>This is needed because when words are imported, they all get the same date of entry.
    */
    private Long getUniqueKey(long milliseconds)
    {
        long this_milliseconds = milliseconds;
        Long long_key = new Long(this_milliseconds);
        if (ltd_hash.containsKey(long_key))
        {
            this_milliseconds = this_milliseconds+1;
            long_key = getUniqueKey(this_milliseconds);
        }
        return long_key;
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

    public Word getWord(String milliseconds_key)
    {
        //printContents();
        Long long_key = Long.decode(milliseconds_key);
        Word word = (Word)ltd_hash.get(long_key);
        int size = ltd_hash.size();
        /*
        try
        {
            log.add("WLTD size "+size+" requested "+milliseconds_key+" text "+word.getText());
        } catch (java.lang.NullPointerException npe)
        {
            log.add("WLTD size "+size+" requested "+milliseconds_key+" npe");
        }
        */
        return word;
    }
    
    /**
    *Not sure if we should also remove the key.
    *Right now I think that it doesn't hurt to leave that list alone,
    *and when the list is empty, the user can get a new list.
    *Currently the key is removed in the Struts Action
    */
    public void removeWord(Long key_to_remove)
    {
        ltd_hash.remove(key_to_remove);
    }
    
    /**
    *This utility method takes a list of categories and a Storage object and returns an integer
    * of total words found in all the categories.  Passing in the categories which can be obtained themselves
    * from the store lets the calling methods set up their own categories.  Its used in a test like this:
    * Vector all_word_categories = store.getWordCategories("exclusive", user_name);
    * int expected_words = wltd.getList(all_word_categories, store, user_name);
    */
    public int getList(Vector all_word_categories, Storage store, String user_name)
    {
        int i = 0;
        int j = 0;
        int expected_words = 0;
        for (i = limit_i; i < all_word_categories.size(); i++)
        {
            String category = (String)all_word_categories.get(i);
            Vector category_words = store.getWordObjectsForTest(category, user_name);
            //append(store.getLog());
            int words_in_this_cat = category_words.size();
            expected_words = expected_words + words_in_this_cat;
            //addToLog(i+" category "+category+" has "+words_in_this_cat+" words");
            j = limit_j;
            while (j<words_in_this_cat)
            {
                Word word = (Word)category_words.get(j);
                WordTestDateUtility wtdu = new WordTestDateUtility(3);
                wtdu.evaluateWordTestDates(word);
                Test[] tests = new Test[0];
                word.setTests(tests);  // replace tests to save memory
                String last_date = wtdu.getWordsLastTestDate(type);
                addWord(word, last_date);
                if (limit==true)
                {
                    limit_i = i;
                    limit_j = j;
                    //addToLog("WLTD i "+i+" j "+j);
                    return expected_words;
                }
                //addToLog(j+" "+word.getText()+" "+word.getDefinition()+" "+last_date+" in "+category);
                j++;
            }
        }
        //addToLog("WLTD end i "+i+" j "+j);
        limit_i = 0;
        limit_j = 0;        // reset these if we make it all the way thru the list.
        //addToLog("WLTD reset i "+i+" j "+j);
        return expected_words;
    }
    
    public String [] getWNTFiles(String user_id, String current_dir)
    {
        String wntd_folder_path = getPathToFile(user_id, current_dir, "wntd");
        String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
        //log.add(wntd_folder_path);
        File files = new File(wntd_folder_path);
        String [] file_names = files.list();
        return file_names;
    }
    
    /**
    *Return the path to a directory like user_id/vocab/lists/wntd or wltd.
    */
    public String getPathToFile(String user_id, String current_dir, String last_dir_name)
    {
        if (subject == null) {subject = Constants.VOCAB;}
        String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wtd_folder_path = (lists_folder_path+File.separator+last_dir_name+" "+type);
        return wtd_folder_path;
    }
    
    /**
    *Returns the file names in the wntd folder.
    */
    public Vector getWaitingTests(String user_id, String current_dir)
    {
        if (subject == null) {subject = Constants.VOCAB;}
        String user_folder_path = (current_dir+File.separator+"files"+File.separator+user_id);
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wtd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
        String [] files = getWNTFiles(user_id, current_dir);
        Date now = new Date();
        long this_instant = now.getTime();
        Vector results = new Vector();
        int i = 0;
        int l = files.length;
        while (i<l)
        {
            String file_w_ext = files [i];
            String file = Domartin.getFileWithoutExtension(file_w_ext);
            long file_long = Long.parseLong(file);
            if (file_long<this_instant)
            {
                results.add(file);
            }
            i++;
        }
        return results;
    }

    /**
    *Erase the old last test date file and create a new file with the current date
    * and then erase the old next test date file and create a new file for the next test date.
    *<p>The next_test_date argument represents the a test that was just run, so at this point
    * it is the last_test_date, so the last_test_date should be set in the word from the action
    * and this becomes the name of the new wltd/time.xml file, and the current time plus the
    * time till the next test date becomes the new file in wntd/next_time.xml.
    *<p>We also need to back up the erased files and store references to the new files for a potential undo,
    * as well as record links to the new test dates to erase them in such a case.
    */
    public void updateTestDateRecords(String user_id, String current_dir, WordTestResult wtr)
    {
        // The WordTestResult object has the test information
        String wntd_name = wtr.getWntdName();                // file name of old test
        String test_date = wtr.getDate();                // date of new test
        //log.add("udapteTestDateRecords.wntd_name: "+wntd_name);
        //log.add("udapteTestDateRecords.new_date: "+test_date);
        long word_id = wtr.getWordId();
        int word_lvl = Integer.parseInt(wtr.getLevel());
        // get the WordNextTestDate object from the wntd type file which has the file name of the wltd file
        String wntd_folder_path = getPathToFile(user_id, current_dir, "wntd");
        String wntd_path_and_file = wntd_folder_path+File.separator+wntd_name; // this should be user_id/vocab/lists/wntd type/long_date.xml
        //log.add("udapteTestDateRecords.wntd_path_and_file: "+wntd_path_and_file);
        // WordNextTestDate getWordNextTestDate(String current_dir, String user_id, String next_test_type, String next_test_time)
        WordNextTestDates wntds = new WordNextTestDates();
        if (subject == null) {subject = Constants.VOCAB;}
        Word word = wntds.getNextTestWord(user_id, current_dir, Domartin.getCurrentTime(), subject);
        WordNextTestDate wntd_obj = wntds.getWordNextTestDate();
        String wltd_file_key = wntd_obj.getWltdFileKey();
        String category = wntd_obj.getCategory();
        String wltd_folder_path = getPathToFile(user_id, current_dir, "wltd");
        //log.add("wntd object ################# ");
        //append(Transformer.createTable(wntd_obj));
        //log.add("wntd object ################# ");
        
        // delete old files now that we have all the info we need
        //File wltd_file = new File(wltd_folder_path+File.separator+wltd_file_key+".xml");
        //File wntd_file = new File(wntd_path_and_file); // already has the xml extension
        String path_to_wltd_file = wltd_folder_path+File.separator+wltd_file_key+".xml";
        String wltd_file = (path_to_wltd_file);
        String wntd_file = (wntd_path_and_file); // already has the xml extension
        FileCaretaker caretaker = new FileCaretaker();
        caretaker.backupWTDFiles(path_to_wltd_file, wntd_path_and_file, user_id, subject, current_dir); // back up files before deleting them
        //log.add("exists: "+wltd_file.exists());
        //boolean success1 = wltd_file.delete();
        //boolean success2 = wntd_file.delete();
        //log.add(success1+" deleted "+wltd_file.getAbsolutePath());
        //log.add(success2+" deleted "+wntd_file.getAbsolutePath());
        //log.add("udapteTestDateRecords.wntd_folder_path: "+wntd_folder_path);
        // Now create the new entries
        String user_folder_path = current_dir+File.separator+"files"+File.separator+user_id;
        String new_date = test_date;
        String new_wltd_file_key = createWLTDEntry(test_date, word_id, category, user_id, user_folder_path);
        caretaker.saveNewWLTDFileInfo(new_wltd_file_key, user_id, subject, current_dir);
        //log.add("udapteTestDateRecords.user_folder_path: "+user_folder_path);
        //log.add("udapteTestDateRecords.new_date: "+new_date+" from test_date "+test_date);
        //log.add("udapteTestDateRecords.new_wltd_file_key: "+new_wltd_file_key);
        createWNTDEntry(test_date, word_id, category, user_id, user_folder_path, new_wltd_file_key, word_lvl);
        caretaker.saveNewWNTDFileInfo(wntd_file_key_backup, user_id, subject, current_dir);		// save a reference for a potential undo after wntd_file_key has been calculated
        log.add("caretaker log )))))))))))))");
        append(caretaker.getLog());
        // the saveNewWNTDFileInfo method, like the saveNewWLTDFileInfo and backupWTDFiles methods
        // will be called with the createWNTDEntry method.
    }
    
    
    /**
    *Same as updateTestDateRecords but returns a TestTimeMemory object.
    *<p>This object has the following members:
    * original_wltd;
    * new_wltd;
    * original_wntd;
    * new_wntd;
    * type;
    **/
    public TestTimeMemory updateTestDateRecordsAndReturnMemory(String user_id,
            String current_dir, WordTestResult wtr)
    {
        TestTimeMemory ttm = new TestTimeMemory();
        // The WordTestResult object has the test information
        String wntd_name = wtr.getWntdName();                // file name of old test
        String test_date = wtr.getDate();                // date of new test
        //log.add("udapteTestDateRecords.wntd_name: "+wntd_name);
        //log.add("udapteTestDateRecords.new_date: "+test_date);
        long word_id = wtr.getWordId();
        int word_lvl = Integer.parseInt(wtr.getLevel());
        // get the WordNextTestDate object from the wntd type file which has the file name of the wltd file
        String wntd_folder_path = getPathToFile(user_id, current_dir, "wntd");
        String wntd_path_and_file = wntd_folder_path+File.separator+wntd_name; // this should be user_id/vocab/lists/wntd type/long_date.xml
        //log.add("udapteTestDateRecords.wntd_path_and_file: "+wntd_path_and_file);
        // WordNextTestDate getWordNextTestDate(String current_dir, String user_id, String next_test_type, String next_test_time)
        WordNextTestDates wntds = new WordNextTestDates();
        if (subject == null) {subject = Constants.VOCAB;}
        Word word = wntds.getNextTestWord(user_id, current_dir, Domartin.getCurrentTime(), subject);
        WordNextTestDate wntd_obj = wntds.getWordNextTestDate();
        String wltd_file_key = wntd_obj.getWltdFileKey();
        //log.add("********* wltd_file_key = "+wltd_file_key+" = "+Transformer.getDateFromMilliseconds(wltd_file_key));
        //append(wntds.getLog());
        String category = wntd_obj.getCategory();
        String wltd_folder_path = getPathToFile(user_id, current_dir, "wltd");
        //log.add("wntd object ################# ");
        //append(Transformer.createTable(wntd_obj));
        //log.add("wntd object ################# ");
        
        // delete old files now that we have all the info we need
        String path_to_wltd_file = wltd_folder_path+File.separator+wltd_file_key+".xml";
        File wltd_file = new File(path_to_wltd_file);
        File wntd_file = new File(wntd_path_and_file); // already has the xml extension
        Caretaker caretaker = new FileCaretaker(); // copy the files we're about to delete
        caretaker.backupWTDFiles(path_to_wltd_file, wntd_path_and_file, user_id, subject, current_dir);
        //log.add("exists: "+wltd_file.exists());
        boolean success1 = wltd_file.delete();
        boolean success2 = wntd_file.delete();
        //log.add(success1+" deleted "+wltd_file.getAbsolutePath());
        //log.add(success2+" deleted "+wntd_file.getAbsolutePath());
        //log.add("udapteTestDateRecords.wntd_folder_path: "+wntd_folder_path);
        // Now create the new entires
        String user_folder_path = current_dir+File.separator+"files"+File.separator+user_id;
        String new_date = test_date;
        String new_wltd_file_key = createWLTDEntry(test_date, word_id, category, user_id, user_folder_path);
        caretaker.saveNewWLTDFileInfo(new_wltd_file_key, user_id, subject, current_dir);
        //log.add("udapteTestDateRecords.user_folder_path: "+user_folder_path);
        //log.add("udapteTestDateRecords.new_date: "+new_date+" from test_date "+test_date);
        //log.add("udapteTestDateRecords.new_wltd_file_key: "+new_wltd_file_key);
        String wntd_file_key = createWNTDEntryAndReturnMemory(test_date, word_id, category, 
        	user_id, user_folder_path, new_wltd_file_key, word_lvl, current_dir);
        caretaker.saveNewWNTDFileInfo(wntd_file_key_backup, user_id, subject, current_dir);// save a reference for a potential undo after wntd_file_key has been calculated
        log.add("caretaker log ++++++++");
        append(caretaker.getLog());
        ttm.setOriginalWLTD(wltd_file_key);
        ttm.setNewWLTD(test_date);
        ttm.setOriginalWNTD(wntd_name);
        ttm.setNewWNTD(wntd_file_key);
        ttm.setType(type);
        return ttm;
    }
    
    /**
    *This utility creates a file named after the words last test date (type specific)
    * which contains the word id and the words category file name.
    *<p>the return argument is stored in the wntd file so that both files can be changed later.
    *<p>This method caused a lot of problems for us.  We created the whole quaquaverse 0.4/0.5
    *app just to track down all our problems to this spot.
    */
    public void createLists(Vector all_word_categories, Storage store, String user_name)
    {
        Vector ids = new Vector();
        int repeats = 0;
        
        for (int i = 0; i < all_word_categories.size(); i++)
        {
            String category = (String)all_word_categories.get(i);
            Vector test_category_words = store.getWordObjectsForTest(category, user_name);
            //log.add("words for test "+test_category_words.size());
            Vector category_words = store.getWordObjects(category, user_name);
            //log.add("category words "+category_words.size());
            int words_in_this_cat = category_words.size();
            int j = 0;
            while (j<words_in_this_cat)
            {
                Word word = (Word)category_words.get(j);
                
                long id = word.getId();
                if (ids.contains(id))
                {
                    repeats++;
                } else
                {
                    ids.add(id);
                }
                
                //log.add("word  "+word.getDefinition());
                number_of_total_words++;
                if (word.getRetired()==exclude_retired_words)
                {
                    //log.add("excluded retired "+word.getDefinition());
                    number_of_retired_words++;
                    // skip word
                } else
                {
                    //WordTestDateUtility wtdu = new WordTestDateUtility(3);
                    //wtdu.evaluateWordTestDates(word);
                    //Test[] tests = word.getTests();
                    //word.setTests(tests);  // replace tests to save memory... doesn't the store.getWordObjectsForTest method already do this?
                    // create a flag to only save one next test date.
                    //String last_reading_date = getLastDate(Constants.READING, wtdu, word);
                    //String last_writing_date = getLastDate(Constants.WRITING, wtdu, word);
                    TestDateEvaluator tde = new TestDateEvaluator();
                    tde.evaluteLastTestDates(word);
                    String last_reading_date = Long.toString(tde.getReadingTime());
                    String last_writing_date = Long.toString(tde.getWritingTime());
                    //log.add(word.getDefinition()+" ltd reading "+last_reading_date+" ltd writing "+last_writing_date);
                    //append(wtdu.getLog());
                    //log.add("----------");
                    String path_to_files = store.getPathToFiles();
                    type = Constants.READING;
                    String wltd_file_key_r = createWLTDEntry(last_reading_date, word, user_name, path_to_files);  // the return argument is stored in the wntd file so that both files can be changed later.
                    createWNTDEntry(last_reading_date, word, user_name, path_to_files, wltd_file_key_r);
                    type = Constants.WRITING;
                    String wltd_file_key_w = createWLTDEntry(last_writing_date, word, user_name, path_to_files);
                    createWNTDEntry(last_writing_date, word, user_name, path_to_files, wltd_file_key_w);
                    //log.add(word.getDefinition()+" last_reading_date "+last_reading_date+" last_writing_date "+last_writing_date);
                }
                j++;
            }
        }
        System.err.println("num of ids "+ids.size()+" repeats "+repeats);
    }
    
    /**
     * Based on createLists but uses longs instead of strings for the dates.
    *This utility creates a file named after the words last test date (type specific)
    * which contains the word id and the words category file name.
    *<p>the return argument is stored in the wntd file so that both files can be changed later.
    *<p>This method caused a lot of problems for us.  We created the whole quaquaverse 0.4/0.5
    *app just to track down all our problems to this spot.
    */
    public void createNewLists(Vector all_word_categories, Storage store, String user_name)
    {
        for (int i = 0; i < all_word_categories.size(); i++)
        {
            String category = (String)all_word_categories.get(i);
            Vector category_words = store.getWordObjects(category, user_name);
            try
            {
            	int words_in_this_cat = category_words.size();
            	int j = 0;
            	while (j<words_in_this_cat)
            	{
            		Word word = (Word)category_words.get(j);
            		number_of_total_words++;
            		if (word.getRetired()==exclude_retired_words)
            		{
            			number_of_retired_words++; // skip word
            		} else
            		{
            			TestDateEvaluator tde = new TestDateEvaluator();
            			tde.evaluteLastTestDates(word);
            			long last_reading_date = tde.getReadingTime();
            			long last_writing_date = tde.getWritingTime();
            			long doe = word.getDateOfEntry(); // increment writing date 1 hour for new words
            			if (last_reading_date == doe)
            			{
            				last_reading_date = last_reading_date - day;
            				last_writing_date = last_writing_date + day;
            			}
            			String path_to_files = store.getPathToFiles();
            			type = Constants.READING;
            			String wltd_file_key_r = createWLTDEntry(last_reading_date, word, user_name, path_to_files);  // the return argument is stored in the wntd file so that both files can be changed later.
            			String wntd_file_key_r = createWNTDEntry(last_reading_date, word, user_name, path_to_files, wltd_file_key_r);
            			type = Constants.WRITING;
            			String wltd_file_key_w = createWLTDEntry(last_writing_date, word, user_name, path_to_files);
            			String wntd_file_key_w = createWNTDEntry(last_writing_date, word, user_name, path_to_files, wltd_file_key_w);
            			//store.saveWTDKeys(wntd_file_key_r, wntd_file_key_w, word.getId()); // this method might be needed to delete a word later, so we don't go looking for it to test after it's gone.  Instead we just create new lists when deleting a word.
            		}
            		j++;
            	}
            } catch (java.lang.NullPointerException npe)
            {
            	log.add("wltd.createNewLists: npe for category "+category);
            }
        }
    }
    
    /**
    *This method assumes the word entered has no tests, and so its first test date will be calculated by its date of entry.
    <p>The reading test should be before the writing test, based on the value in the elt vector for level 0 words.
    */
    public void createNewWordList(Word word, Storage store, String user_id, String current_dir)
    {
        String user_folder_path = current_dir+File.separator+"files"+File.separator+user_id;
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        //String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        //String wltd_folder_path = (lists_folder_path+File.separator+"wltd "+type);
        //String path_to_files = wltd_folder_path;
        String path_to_files = user_folder_path;
        String date_of_entry = Transformer.getLongDateFromMilliseconds(Long.toString(word.getDateOfEntry()));
        // String path_to_files = store.getPathToFiles();
        //String type = Constants.READING;
        String wltd_file_key_r = createWLTDEntry(date_of_entry, word, user_id, path_to_files);  // the return argument is stored in the wntd file so that both files can be changed later.
        createWNTDEntry(date_of_entry, word, user_id, path_to_files, wltd_file_key_r);
        type = Constants.WRITING;
        long doe_milliseconds = word.getDateOfEntry();
        String new_test_date = getTimeTilNextNewWordTest(doe_milliseconds);    // add level 0 exclude period
        String wltd_file_key_w = createWLTDEntry(date_of_entry, word, user_id, path_to_files);
        createWNTDEntry(new_test_date, word, user_id, path_to_files, wltd_file_key_w);
    }

    /**
    *Add the exclude date to the last test date to decide when the next test should be.
    */
    private String getTimeTilNextNewWordTest(long milliseconds)
    {
        int word_level = 0;
        String o = (String)elt_vector.get(word_level);
        int i = Integer.parseInt(o);
        int screen_lvl = i;
        long exclude_time = (screen_lvl*day);
        Date now = new Date();
        long this_instant = now.getTime();
        long exclude_period = (milliseconds + exclude_time);
        return (new Long(exclude_period).toString());
    }
    
    /**
    *Create a file entry in user_id_folder/vocab/lists/wldt *type* folder for the last test.
    *<p>"Your going away with me all right, in a garbage bag." (Dexter to Leila).
    */
    public String createWLTDEntry(String last_date, Word word, String user_name, String user_folder_path)
    {
        long milliseconds = Domartin.getMilliseconds(last_date);
        if (subject == null) {subject = Constants.VOCAB;}
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wltd_folder_path = (lists_folder_path+File.separator+"wltd "+type);
        String wltd_file_key = getUniqueFile(milliseconds, wltd_folder_path);
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wltd_folder_path);
        fjdomwl.createLastTestDateFile(word.getId(), word.getCategory(), wltd_file_key);
        return wltd_file_key;
    }
    
    /**
     * *Based on createWLTDEntry but with a long passed in directly.
    *Create a file entry in user_id_folder/vocab/lists/wldt *type* folder for the last test.
    */
    public String createWLTDEntry(long last_date, Word word, String user_name, String user_folder_path)
    {
        if (subject == null) {subject = Constants.VOCAB;}
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wltd_folder_path = (lists_folder_path+File.separator+"wltd "+type);
        String wltd_file_key = getUniqueFile(last_date, wltd_folder_path);
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wltd_folder_path);
        fjdomwl.createLastTestDateFile(word.getId(), word.getCategory(), wltd_file_key);
        return wltd_file_key;
    }
    
    /**
    *Create a file entry in user_id_folder/vocab/lists/wldt *type* folder for the last test.
    *<p>This version skips the Word object which we can do without.
    */
    public String createWLTDEntry(String last_date, long word_id, String category, String user_name,
        String user_folder_path)
    {
        //log.add("createWLTDEntry.last_date: "+last_date);
        //log.add("createWLTDEntry.word_id: "+word_id);
        //log.add("createWLTDEntry.category: "+category);
        //log.add("createWLTDEntry.user_name: "+user_name);
        //log.add("createWLTDEntry.user_folder_path: "+user_folder_path);
        long milliseconds = Domartin.getMilliseconds(last_date);
        if (milliseconds == 0)
        {
            milliseconds = Long.parseLong(last_date);
        }
        if (subject == null) {subject = Constants.VOCAB;}
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wltd_folder_path = (lists_folder_path+File.separator+"wltd "+type);
        String wltd_file_key = getUniqueFile(milliseconds, wltd_folder_path);
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wltd_folder_path);
        fjdomwl.createLastTestDateFile(word_id, category, wltd_file_key);
        return wltd_file_key;
    }
    
    /**
    *Create a file entry for the next test in user_id_folder/vocab/lists/wndt *type* folder.
    */
    public void createWNTDEntry(String last_date, Word word, String user_id, String user_folder_path,
        String wltd_file_key)
    {
        long milliseconds = Domartin.getMilliseconds(last_date);
        if (subject == null) {subject = Constants.VOCAB;}
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wntd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
        String wntd_not_unique = getTimeTilNextTest(word, milliseconds);
        String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
        wntd_file_key_backup = wntd_file_key;  // so the caretaker can use this in it's saveNewWNTDFileInfo method.
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
        fjdomwl.createNextTestDateFile(word.getId(), word.getCategory(), wntd_file_key, wltd_file_key);
    }
    
    /**
    *Create a file entry for the next test in user_id_folder/vocab/lists/wndt *type* folder.
    *This is the version used in createNewLists
    */
    public String createWNTDEntry(long last_date, Word word, String user_id, String user_folder_path,
        String wltd_file_key)
    {
        if (subject == null) {subject = Constants.VOCAB;}
        String vocab_folder_path = (user_folder_path+File.separator+subject);
        String lists_folder_path = (vocab_folder_path+File.separator+"lists");
        String wntd_folder_path = (lists_folder_path+File.separator+"wntd "+type);
        String wntd_not_unique = getTimeTilNextTest(word, last_date);
        String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
        wntd_file_key_backup = wntd_file_key;  // so the caretaker can use this in it's saveNewWNTDFileInfo method.
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
        fjdomwl.createNextTestDateFile(word.getId(), word.getCategory(), wntd_file_key, wltd_file_key);
        return wntd_file_key;
    }
    
    /**
    *Create a file entry for the next test without the Word object arg in user_id_folder/vocab/lists/wndt *type* folder.
    */
    public void createWNTDEntry(String last_date, long word_id, String category, String user_name, String user_folder_path,
        String wltd_file_key, int word_lvl)
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
        String wntd_not_unique = getTimeTilNextTest(word_lvl, milliseconds);
        String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
        wntd_file_key_backup = wntd_file_key;  // so the caretaker can use this in it's saveNewWNTDFileInfo method.
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
        fjdomwl.createNextTestDateFile(word_id, category, wntd_file_key, wltd_file_key);
    }
    
    /**
    *Same as createWNTDEntry but returns the new time file name.
    */
    public String createWNTDEntryAndReturnMemory(String last_date, long word_id, String category, String user_name, String user_folder_path,
        String wltd_file_key, int word_lvl, String current_dir)
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
        String wntd_not_unique = getTimeTilNextTest(word_lvl, milliseconds);
        String wntd_file_key = getUniqueFile(Long.parseLong(wntd_not_unique), wntd_folder_path);
        wntd_file_key_backup = wntd_file_key;  // so the caretaker can use this in it's saveNewWNTDFileInfo method.
        FileJDOMWordLists fjdomwl = new FileJDOMWordLists(wntd_folder_path);
        fjdomwl.createNextTestDateFile(word_id, category, wntd_file_key, wltd_file_key);
        return wntd_file_key;
    }
    
    /**
    *This utility method is like getList, except it uses the ExcludeWord object
    * to reject words that have been tested recently.  This is used when the
    * list of words is to be tested, and not a study list.
    */
    public int getAdjustedList(Vector all_word_categories, Storage store, String user_name)
    {
        int expected_words = 0;
        for (int i = 0; i < all_word_categories.size(); i++)
        {
            String category = (String)all_word_categories.get(i);
            Vector category_words = store.getWordObjectsForTest(category, user_name);
            int words_in_this_cat = category_words.size();
            expected_words = expected_words + words_in_this_cat;
            //addToLog(i+" category "+category+" has "+words_in_this_cat+" words");
            int j = 0;
            while (j<words_in_this_cat)
            {
                Word word = (Word)category_words.get(j);
                WordTestDateUtility wtdu = new WordTestDateUtility(3);
                wtdu.evaluateWordTestDates(word);
                Test[] tests = new Test[0];
                word.setTests(tests);  // replace tests to save memory
                String last_date = wtdu.getWordsLastTestDate(type);
                addWordOrExclude(word, last_date);
                //addToLog(j+" "+word.getText()+" "+word.getDefinition()+" "+last_date+" in "+category);
                if (limit==true)
                {
                    break;
                }
                j++;
            }
            if (limit==true)
            {
                break;
            }
        }
        return expected_words;
    }
    
    /**
     * Erase the contents of the wltd, wntd reading and writing folders.
     * @param user_id
     */
    public boolean eraseTestDateLists(String user_id, String current_dir)
    {
        int delete = 0;
        // delete reading wltd
        type = Constants.READING;
        String ptf = getPathToFile(user_id, current_dir, "wltd");
        File file = new File(ptf);
        boolean deleted = deleteDir(file);
        if (deleted == false){delete++;}
        file.mkdir();
        // delete reading wntd
        ptf = getPathToFile(user_id, current_dir, "wntd");
        file = new File(ptf);
        deleted = deleteDir(file);
        if (deleted == false){delete++;}
        file.mkdir();
        // delete writing wltd
        type = Constants.WRITING;
        ptf = getPathToFile(user_id, current_dir, "wltd");
        file = new File(ptf);
        deleted = deleteDir(file);
        if (deleted == false){delete++;}
        file.mkdir();
        // delete writing wntd
        ptf = getPathToFile(user_id, current_dir, "wntd");
        file = new File(ptf);
        deleted = deleteDir(file);
        if (deleted == false){delete++;}
        file.mkdir();
        System.out.println(ptf+" deleted? "+deleted);
        if (delete > 0){deleted=false;}
        return deleted;
    }
    
    // Deletes all files and sub directories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
    }
    
    public void setExcludeLevelTimes(Vector levels)
    {
        this.elt_vector = levels;
    }
    
    public void setType(String _type)
    {
        this.type = _type;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setLimitOfWords(int _low)
    {
        this.limit_of_words = _low;
    }
    
    /**
    Limit I represents the counter of categories.
    */
    public void setLimitI(int _limit_i)
    {
        limit_i = _limit_i;
    }
    
    public int getLimitI()
    {
        return limit_i;
    }
    
    /**
    Limit J represents a point in the list of words in a category.
    */
    public void setLimitJ(int _limit_j)
    {
        limit_j = _limit_j;
    }
    
    public int getLimitJ()
    {
        return limit_j;
    }
    
    /**
    *What is in a name?
    */
    public int getNumberOfRetiredWords()
    {
        return number_of_retired_words;
    }
    
    public int getNumberOfTotalWords()
    {
        return number_of_total_words;
    }
    
    /**
    *Starting with vocab, there could be grammer, ext.
    * Check Constants for those available.
    */
    public void setSubject(String _subject)
    {
        this.subject = _subject;
    }
    
    public String getSubject()
    {
        return subject;
    }
    
    /**
    *This method is for testing purposes, unless you can find something else to use it for that is... 
    */
    public String getWNTDFileKeyBackup()
    {
    	return wntd_file_key_backup;
    }
    
    /** DEGUGGING -------------------------------------------------- */
    private Vector log;
    
    public Vector getLog()
    {
        return log;
    }
    
    public void addToLog(String s)
    {
        log.add(s);
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
    
    public void printContents()
    {
        //log.add("WordLastTestDates.printContents");
        //Enumeration e =    ltd_hash.keys();
        for (Enumeration e = ltd_hash.elements() ; e.hasMoreElements() ;)
        {
            log.add("WordLastTestDates.printContents "+Transformer.createTable(e.nextElement()));

        }
        
    }
    
    private void printTests(Word word)
    {
        Test all_tests[] = word.getTests();
        int size = all_tests.length;
        int i = 0;
        while (i<size)
        {
            Test test = all_tests[i];
            log.add(" "+i+" "+test.getName()+" "+test.getDate()+" "+test.getGrade());
            i++;
        }
    }

}

