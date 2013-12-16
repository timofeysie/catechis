package org.catechis.filter;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import junit.framework.TestCase;

import java.io.File;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import junit.framework.TestCase;

import org.catechis.constants.Constants;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.dto.TestStats;
import org.catechis.dto.AllTestStats;
import org.catechis.dto.AllWordsTest;
import org.catechis.dto.WordNextTestDate;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;
import org.catechis.file.WordNextTestDates;
import org.catechis.Transformer;

public class ImportListFilterTest extends TestCase
{
	
    public ImportListFilterTest(String name) 
    {
        super(name);
    }
    
    protected void setUp() throws Exception
    {
    }
    
    // 
    public void testParse()
    {
	    String word_separator = new String("=");
	    //String line_separator = new String("\000D");
	    String line_separator = new String(";");
	    ImportListFilter ilf = new ImportListFilter();
	    String text = new String("one"+word_separator+"def"+line_separator
	    +"two"+word_separator+"def2"+line_separator
	    +"three"+word_separator+"def3"+line_separator);
	    Hashtable results = ilf.parse(text, word_separator, line_separator);
	    //dumpHash(results);
	    //dumpLog(ilf.getLog());
	    int expected = 3;
	    int actual = results.size();
	    assertEquals(expected, actual);
    }
    
    public void testParseIEvsFirefox()
    {
    	System.out.println("ImportListFIlterTest.testParseIEvsFirefox");
	    String word_separator = new String("=");
	    //String line_separator = new String("\000D");
	    String line_separator = new String(";");
	    ImportListFilter ilf = new ImportListFilter();
	    // ie
	    String ie = new String("È­º¸"+word_separator+"pictoral spread"+line_separator
	    +"°è´Ü"+word_separator+"stairs"+line_separator
	    +"Çâ³ª¹«"+word_separator+"juniper tree"+line_separator
	    +"¸ñ·Ã"+word_separator+"magnolia"+line_separator
	    +"Á¶Á¾»ç"+word_separator+"pilot"+line_separator
	    +"±¤°ø¼­"+word_separator+"office"+line_separator
	    +"º¹Åë"+word_separator+"stomacheache"+line_separator
	    +"±Í¾ÎÀÌ"+word_separator+"ear ache"+line_separator
	    +"~¿¡¼­ ¶³¾îÁ®"+word_separator+"off"+line_separator
	    +"³à¼®"+word_separator+"boys & girls"+line_separator
	    +"Á¦¸Ú´ë·Î"+word_separator+"at will"+line_separator);
	    // firefox
	    String ff = new String("&#54868;&#48372;"+word_separator+"pictoral spread"+line_separator
	    +"&#44228;&#45800;"+word_separator+"stairs"+line_separator
	    +"&#54693;&#45208;&#47924;"+word_separator+"juniper tree"+line_separator
	    +"&#47785;&#47144;"+word_separator+"magnolia"+line_separator
	    +"&#51312;&#51333;&#49324;"+word_separator+"pilot"+line_separator
	    +"&#44305;&#44277;&#49436;"+word_separator+"office"+line_separator
	    +"&#48373;&#53685;"+word_separator+"stomacheache"+line_separator
	    +"&#44480;&#50515;&#51060;"+word_separator+"ear ache"+line_separator
	    +"~&#50640;&#49436; &#46504;&#50612;&#51256;"+word_separator+"off"+line_separator
	    +"&#45376;&#49437;"+word_separator+"boys & girls"+line_separator
	    +"&#51228;&#47691;&#45824;&#47196;"+word_separator+"at will, as one pleases"+line_separator
	    +"&#47960;&#54616;&#45716;&#44163;"+word_separator+"something"+line_separator
	    +"&#44032;&#46301;&#52268; &#49324;&#49373;&#54876;"+word_separator+"private life"+line_separator);
	    Hashtable ie_results = ilf.parse(ie, word_separator, line_separator);
	    System.out.println("ImportListFIlterTest.testParseIEvsFirefox --------- ie");
	    dumpHash(ie_results);
	    //dumpLog(ilf.getLog());
	    Hashtable ff_results = ilf.parse(ff, word_separator, line_separator);
	    System.out.println("ImportListFIlterTest.testParseIEvsFirefox --------- ff");
	    dumpHash(ff_results);
	    dumpLog(ilf.getLog());
	    int expected = 3;
	    int actual = ie_results.size();
	    assertEquals(expected, actual);
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
    
    private void dumpHash(Hashtable log)
    {
	    Enumeration keys = log.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" - "+val);
	    }
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ImportListFilterTest.class);
    }

}

