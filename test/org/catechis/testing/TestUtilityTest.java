package org.catechis.testing;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ArrayList;
import junit.framework.TestCase;

import org.catechis.dto.Word;
import org.catechis.dto.Test;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordTestDates;
import org.catechis.dto.WordTestResult;
import org.catechis.dto.WordLastTestDates;
import org.catechis.WordTestDateUtility;
import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.FileStorage;
import org.catechis.constants.Constants;

import org.catechis.testing.TestUtility;

public class TestUtilityTest extends TestCase
{

    public TestUtilityTest(String name)
    {
        super(name);
    }
    
    /**
    * updateLevel(AllWordsTest awt, String user_name, String user_id, String context_path)
    */
    public void testUpdateLevel()
    {
        String user_name = "test_user";
        String cat = "word.xml";
        String user_id = "test_user";
        File path_file = new File("");
            String current_dir = path_file.getAbsolutePath();
            FileStorage store = new FileStorage(current_dir);
            Vector actual_words = store.getWordObjects(cat, user_name);
            Word first_word = (Word)actual_words.get(0);
            System.out.println("word______________________________");
            dumpLog(Transformer.createTable(first_word));
            System.out.println("tests____________________________");
            printTests(first_word);
            String actual_definition = first_word.getDefinition();
           String expected_definition = new String("beach");
        String type = new String("reading");
        long word_id = 111;
        String expected_date = new Date().toString();
        String encoding = "euc-kr";
        AllWordsTest awt = new AllWordsTest();
            awt.setText(expected_definition);
            awt.setDefinition(expected_definition);
            awt.setCategory(cat);
            awt.setTestType(type);
            awt.setId(Long.parseLong("-6178075242551977456"));
            awt.setAnswer(Constants.PASS);
        WordTestResult wtr = new WordTestResult();
            wtr.setText("text");
            wtr.setDefinition("def");
            wtr.setAnswer("and");
            wtr.setGrade("fail");
            wtr.setLevel("2");
            wtr.setId(1);
            wtr.setOriginalLevel("3");
            wtr.setEncoding(encoding);
            wtr.setDate(expected_date);
            wtr.setWordId(word_id);
            wtr.setOriginalText("zzz");
            wtr.setOriginalDefinition("beach");
        TestUtility tu = new TestUtility();
        WordTestResult new_wtr = tu.reverseTestResult(wtr, awt, user_name, user_id, current_dir);
        System.out.println("WTDUT_________________________________ after reverse");
        System.out.println("new wtr ------------ ");
        printLog(Transformer.createTable(new_wtr));
        System.out.println("tu log  ------------ ");
        printLog(tu.getLog());

        actual_words = store.getWordObjects(cat, user_name);
            first_word = (Word)actual_words.get(0);
            System.out.println("word______________________________");
            dumpLog(Transformer.createTable(first_word));
            System.out.println("tests____________________________");
            printTests(first_word);
        /*
        Vector words = new Vector();
        WordTestDateUtility wtd_utility = new WordTestDateUtility(3);
        wtd_utility.evaluateListOfWTD(words, type);
        WordLastTestDates ltd = wtd_utility.getWordLastTestDates(type);
        ArrayList actual_key_list = ltd.getSortedWLTDKeys();
        System.out.println("WTDUT_________________________________ after reverse");
        printArrayList(actual_keyList);
        */
        String expected = "did't I just tell you?";
        String actual = "not yet";
        assertEquals(expected, actual);
    }
    
    private void printArrayList(ArrayList al)
    {
        int size = al.size();
        int i = 0;
        while (i<size)
        {
            System.out.println(i+" "+al.get(i));
            i++;
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
               System.out.println(" "+i+" "+test.getName()+" "+test.getDate()+" "+test.getGrade());
               i++;
           }
        }
        
    
    private void printLog(Vector log)
    {
        int total = log.size();
        int i = 0;
        while (i<total)
        {
            System.err.println(i+" "+log.get(i));
            i++;
        }
    }
    
    private void dumpLog(Vector log)
    {
        int i = 0;
        while (i<log.size())
        {
            System.out.println("log - "+log.get(i));
            i++;
        }

    }

}
