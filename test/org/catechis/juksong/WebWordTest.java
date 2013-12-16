package org.catechis.juksong;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.catechis.dto.Word;
import org.junit.Test;
import org.catechis.juksong.*;
import org.catechis.EncodeString;
import org.catechis.Transformer;
import org.catechis.constants.*;

public class WebWordTest 
{

	@Test
	public final void testLanguageChoiceWriting() 
	{
		Word blank_word = new Word();
        String type = "writing";
        String text = "";
        String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        Vector definitions = ww.testImpactDictionary(writingParseSample(), text, encoding);
        //Vector definitions = ww.parseGoogleImages("apple");
        //System.out.println("WebWordTestdefinitions.testLanguageChoiceWriting : definitions ====== ");
        //printLog(definitions);
        //System.out.println("examples----------------");
        //printLog(ww.getExamples());
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
		//fail("Not yet implemented"); // TODO
        int expected = 5;
        int actual = definitions.size();
        assertEquals(expected, actual);
	}
	
	/**
	 * Should return:
	 * 
	 * berry
	 * Cotton Bowl
	 * country hall
	 * tailgate party
	 * open
	 * 
	 */
	@Test
	public final void testLanguageChoiceReading() 
	{
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "";
        String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        Vector definitions = ww.testImpactDictionary(readingParseSample(), text, encoding);
        //System.out.println("WebWordTestdefinitions.testLanguageChoicesReading ====== ");
        //printLog(definitions);
        //String profile = FarmingTools.getLanguageProfile(native_language, language_being_learned, type);
        //System.out.println("---------------- "+Integer.parseInt(profile));
        //System.out.println("profile "+profile+" "+JuksongConstants.printProfile(profile));
        //System.out.println("examples----------------");
        //printLog(ww.getExamples());
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
        int expected = 5;
        int actual = definitions.size();
        assertEquals(expected, actual);
	}
	
	/**
	 * got <[??]>
	 * with String definition = EncodeString.encodeThis("abcXZY가나다", encoding);
	 * got got <[???]>
	 * with String definition = ("abcXZY가나다");
	 * This test passed before I started to encode strings in WebWord 
	 * in order to try and get the Console output to print Korean properly instead of:
	 * backingout new_line: �ۣ⢥����驯������� öȸ, ���
	 * or ??????????????? �?, ??
	 * We encoded the definition submitted to trimAscii,
	 * but then got a blank actual string in return.
	 */
	@Test
	public final void testTrimAscii()
	{
		WebWord ww = new WebWord();
		//String encoding = "euc-kr";
		String encoding = "UTF-8";
		//ww.setEncoding(encoding);
		String definition = "abcXZY가나다";
		//String definition = EncodeString.encodeThis("abcXZY가나다", encoding);
		String actual = ww.trimAscii(definition);
		String expected = "가나다";
		//String expected = EncodeString.encodeThis("가나다", encoding);
		//System.out.println("WebWordTest.testTrimAscii: output ----------");
		//System.out.println("actual "+actual);
		//System.out.println("expected "+expected);
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testTrimNonAscii()
	{
		WebWord ww = new WebWord();
		String definition = "abcXZY［´∂｀가나다 - ´ ";
		String actual = ww.trimNonAscii(definition);
		String expected = "abcXZY";
		//System.out.println("WebWordTest.testTrimNonAscii: output ----------");
		//System.out.println("actual "+actual);
		//System.out.println("expected "+expected);
		//String trimAscii(String definition)
		assertEquals(expected, actual);
	}
	
	/*
	@Test
	public final void testSpecialCharacterExclusion()
	{
		// this method is used to get the encoding values of specail characters
		// that we want to exclude from definitions.
		System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
			String new_definition = "abcXZY［´∂｀가나다 - ´ -［ｂ´ｅｒｉ - '［ｋｒ∧ｆｔｓ�";
	    	StringBuffer new_line = new StringBuffer();
	    	StringBuffer buf_log = new StringBuffer();
	    	int length = new_definition.length();
	    	int i = 0;
	    	while (i<length)
	    	{
	    		try
	    		{
	    			char c = new_definition.charAt(i);
	    			int num_val = Character.getNumericValue(c);
	    			int c_type =	Character.getType(c); 
	    			Character ch =	Character.valueOf(c); 
	    			char c1 = ch.charValue(); 
	    			int num_val1 = Character.getNumericValue(c1);
	    			char c2 = ch.charValue(); 
	    			String s = new_definition.substring(i,i+1);
	    			String es = Transformer.getByteString(s);
	    			System.out.println("-1 char "+c1+" num_val "+num_val+" es "+es);
	    		} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    		{
	    			// do nothing???
	    		}
				i++;
	    	}
	    	new_definition = new String(new_line);
	    	String excluded = new String(buf_log); buf_log = new StringBuffer();
	}
	*/
	
	/*
	@Test
	public final void testCheckForSpecialCharacters()
	{
		String new_definition = "abcXZY［´∂｀아가나다하 - ´ -［ｂ´ｅｒｉ - 'ｋｒ∧ｆｔｓ�]";
		WebWord ww = new WebWord();
		int length = new_definition.length();
    	int i = 0;
    	while (i<length)
    	{
    		String s = new_definition.substring(i,i+1);
    		String es = Transformer.getByteString(s);
    		boolean actual = ww.checkForSpecialCharacters(es);
    		//System.out.println(s+" "+es+" "+actual);
    		i++;
    	}
    	//printLog(ww.getLog());
	}
	*/
	
	@Test
	public final void testGetExamples()
	{
		Word blank_word = new Word();
        String type = "writing";
        String text = "";
        String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        Vector definitions = ww.testImpactDictionary(writingParseSample(), text, encoding);
        Vector examples = ww.getExamples();
        int expected = 12;
        int actual = examples.size();
        //System.out.println("WebWordTestdefinitions.testLanguageChoices : definitions ====== ))))))");
        //printLog(definitions);
        //System.out.println("WebWordTestdefinitions.testLanguageChoices : examples ======    ))))))");
        //printLog(examples);
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
        assertEquals(expected, actual);
	}
	
	@Test
	public final void testReadingExamples() 
	{
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "";
        String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        Vector definitions = ww.testImpactDictionary(readingParseSample(), text, encoding);
        //Vector definitions = ww.parseGoogleImages("apple");
        //System.out.println("WebWord.testReadingExamples ====== ");
        //printLog(definitions);
        //System.out.println("examples----------------");
        //printLog(ww.getExamples());
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
        int expected = 5;
        int actual = definitions.size();
        assertEquals(expected, actual);
	}
	
	/**
	 * This test reads an html page in this files folder that is the
	 * search results for 'to be opened'.  There are no definitions,
	 * only a bunch of examples.
	 */
	@Test
	public final void testOpenedReadingExamples() 
	{
		//System.out.println("WebWord.testOpendReading");
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "NOT USED";
        String encoding = "UTF-8";
        //String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        ww.setRemoveFontTags(true);
        File file = new File("/");
        String current_dir = file.getAbsolutePath();
        // C:\Tims Folder\programming\catechis\test\org\catechis\juksong
        String path_to_file = current_dir+"Tims Folder"+File.separator
        	+"programming"+File.separator+"catechis"+File.separator+"test"
        	+File.separator+"org"+File.separator+"catechis"+File.separator
        	+"juksong"+File.separator+"opened reading.html";
        File test = new File(path_to_file);
        boolean exists = test.exists();
        //System.out.println(exists+" that file path "+path_to_file+" exists");
        Vector test_text = loadTestText(path_to_file);
        //Vector definitions = ww.testImpactDictionary(readingParseSample(), text, encoding);
        Vector definitions = ww.testImpactDictionary(test_text, text, encoding);
        //Vector definitions = ww.parseGoogleImages("apple");
        Vector examples = ww.getExamples();
        /*
        System.out.println("WebWordTest.testOpenedReadingExamples "+exists);
        System.out.println("path "+path_to_file);
        System.out.println("definitions -----------------");
        printLog(definitions);
        System.out.println("examples    -----------------");
        printLog(examples);
        System.out.println("ww.getLog()----------------");
        printLog(ww.getLog());
        */
        int expected = 401;
        int actual = examples.size();
        //System.out.println("testOpenedReadingExamples: examples.size() "+actual);
        assertEquals(expected, actual);
	}
	

	/**
	 * Got this:
	 * WebWordTest.testRemoveBrackets() abcXZY�??????? - 
	 * when encoding strings.
	 * Got this:
	 * WebWordTest.testRemoveBrackets() abcXZY´∂｀아가나다하 - 
	 * When not encoding.
	 */
	@Test
	public final void testRemoveBrackets()
	{
		String encoding = "UTF-8";
		//String line = EncodeString.encodeThis("abcXZY´∂｀아가나다하 - [´ -［ｂ´ｅｒｉ - 'ｋｒ∧ｆｔｓ�]", encoding);
		String line = "abcXZY´∂｀아가나다하 - [´ -［ｂ´ｅｒｉ - 'ｋｒ∧ｆｔｓ�]";
		WebWord ww = new WebWord();
		String actual = ww.removeBrackets(line, "[", "]");
		//System.out.println("WebWordTest.testRemoveBrackets() "+actual);
		//String expected = EncodeString.encodeThis("abcXZY´∂｀아가나다하 -", encoding);
		String expected = "abcXZY´∂｀아가나다하 - ";
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testRemoveTags()
	{
		String line = "</BODY>";
		WebWord ww = new WebWord();
		String actual = ww.removeBrackets(line, "<", ">");
		//System.out.println("WebWordTest.testRemoveBrackets() "+actual);
		String expected = "";
		assertEquals(expected, actual);
	}
	/**
	 * This test passes, but
	 */
	@Test
	public final void testTrimTrailingNonLetters()
	{
		String line = "berry@#";
		WebWord ww = new WebWord();
		String actual = ww.trimTrailingNonLetters(line);
		String expected = "berry";
		//System.out.println("WebWordTest.testTrimTrailingNonLetters.getLog()----------------");
        //printLog(ww.getLog());
        //System.out.println("WebWordTest.testRemoveBrackets() "+actual);
        assertEquals(expected, actual);
	}
	
	/**
	 * If a users native language is English, and they are learning Korean,
	 * a WRITING word lookup would send a words definition to WebWord for lookup.
	 * In this test, a line with the contents of a WRITING search for 
	 * with the definition 'back out' (text '철회') would be sent to WebWord
	 * and the result will contain
	 * an few English translations and various Korean alternatives and then
	 * a long list of example usages.
	 * If it were a READING search, the text '철회' would be sent to WebWord
	 * and the result page would contain a long list of English translations,
	 * many with a single sentence in Korean untranslated, and then a long list of 
	 * example passages that will be in English, then Korean, or Korean then English,
	 * or possibly English/Korean alternating line passages, or possibly
	 * Korean/English alternating lines in a passage, with lists of single definitions
	 * throughout.
	 */
	@Test
	public final void testParseImpactLineReading()
	{
		String line = ("back out  철회, <FONT COLOR=8080FF>탈퇴</FONT>, 변절");
		String type = Constants.READING;
    	String native_language = JuksongConstants.ENGLISH;
    	String language_being_learned = JuksongConstants.KOREAN;
		WebWord ww = new WebWord(type, native_language, language_being_learned);
		ww.setRemoveFontTags(true);
		ww.setEncoding("UTF-8");
		String actual = ww.trimUnwanted(line);
		String expected = ("back out");
		//System.out.println("WebWordTest.testParseImpactLine");
		//System.out.println(actual);
		//printLog(ww.getLog());
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testParseImpactLineWriting()
	{
		String line = ("back out  철회, <FONT COLOR=8080FF>탈퇴</FONT>, 변절");
		String type = Constants.WRITING;
		String encoding = "UTF-8";
    	String native_language = JuksongConstants.ENGLISH;
    	String language_being_learned = JuksongConstants.KOREAN;
		WebWord ww = new WebWord(type, native_language, language_being_learned);
		ww.setRemoveFontTags(true);
		ww.setEncoding(encoding);
		String actual = ww.trimUnwanted(line);
		//String expected = EncodeString.encodeThis("철회,탈, 변절", encoding);
		String expected = "철회, 탈퇴, 변절";
		//System.out.println("WebWordTest.testParseImpactLineWriting");
		//System.out.println(actual);
		//printLog(ww.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	 * This method was made because the line below was being clipped to produce bacou 
	 * in testEnglishLearningKoreanReadingSwdinitions.
	 * In removeBrackets(String line, String begin, String end)
	    			start = new_line.substring(0, open - 1);
	 * REMOVED THE -1 AND now this test passes.
	 */
	@Test
	public final void testParseImpactLineClipping()
	{
		String line = ("<FONT COLOR=8080FF>back</FONT> <FONT COLOR=8080FF>out</FONT>  öȸ, Ż��, ����");
		String type = Constants.READING;
    	String native_language = JuksongConstants.ENGLISH;
    	String language_being_learned = JuksongConstants.KOREAN;
		WebWord ww = new WebWord(type, native_language, language_being_learned);
		ww.setRemoveFontTags(true);
		ww.setEncoding("UTF-8");
		String actual = ww.trimUnwanted(line);
		String expected = ("back out");
		//System.out.println("WebWordTest.testParseImpactLineClipping");
		//System.out.println(actual);
		//printLog(ww.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	 * These are the illegal characters for the Impact Dictionary:
	 * "; ^ , " | < > [ ] { } ( ) * & ~ \", so test removing them
	 * which will be used on search texts.
	 */
	@Test
	public final void testRemoveIllegalChars()
	{
		String illegal_chars = ";^,\"|<>[]{}()*&~\\";
		WebWord ww = new WebWord();
		String actual = ww.removeIllegalChars(illegal_chars);
		String expected = "";
		//System.out.println("WebWordTest.testRemoveIllegalChars");
		//System.out.println("sent "+illegal_chars);
		//System.out.println("got  "+actual);
		//printLog(ww.getLog());
		assertEquals(expected, actual);
	}
	
	/**
	 * This html:
	 * 
	 * <HR>
	 * <script language="JavaScript">
	 *  <!--
 	 * function SetCookie(name, val, expires) {
	 * ...
	 *  </script>
	 * <BR>
	 * <FONT COLOR=8080FF>back</FONT> <FONT COLOR=8080FF>out</FONT>  öȸ, Ż��, ����
	 * <HR>
	 * <BR>
	 * backingout <FONT SIZE=-1 COLOR=557756>�ۣ⢥����驯��������</FONT> öȸ, ���
	 * <HR>
	 * <BR>
	 * backout <FONT SIZE=-1 COLOR=557756>�ۣ⢥�����������</FONT> ���б��� öȸ
	 * <HR>
	 * <BR>
	 * trace  �ڸ� ���, ���ư���, ~ <FONT COLOR=8080FF>back</FONT> ����� �ö󰡴�, ~ <FONT COLOR=8080FF>out</FONT> ����� ã��, ������, �׸���, ȹå�ϴ�
	 * <HR>
	 * 
	 * Should yeild this:
	 * 
	 * back out 철회, 탈퇴, 변절 
	 * --------------------------------------------------------------------------------
	 * backingout ［ｂ´æｋｉŋ｀ａｕｔ］ 철회, 취소 
	 * --------------------------------------------------------------------------------
	 * backout ［ｂ´æｋ｀ａｕｔ］ 초읽기의 철회 
	 * --------------------------------------------------------------------------------
	 * trace 뒤를 밟다, 나아가다, ~ back 더듬어 올라가다, ~ out 행방을 찾다, 베끼다, 그리다, 획책하다 
	 * 
	 * abd return a list like this:
	 * 
	 * back out	 
	 * backingout
	 * backout
	 * trace
	 */
	@Test
	public final void testEnglishLearningKoreanReadingDinitions() 
	{
		//System.out.println("WebWord.testEnglishLearningKoreanReadingDinitions");
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "NOT USED";
        String encoding = "UTF-8";
        //String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        ww.setRemoveFontTags(true);
        File file = new File("/");
        String current_dir = file.getAbsolutePath();
        String file_name = "back out E-K reading.html";
        String path_to_file = current_dir+"Tims Folder"+File.separator
        	+"programming"+File.separator+"catechis"+File.separator+"test"
        	+File.separator+"org"+File.separator+"catechis"+File.separator
        	+"juksong"+File.separator+file_name;
        Vector test_text = loadTestText(path_to_file);
        Vector definitions = ww.testImpactDictionary(test_text, text, encoding);
        Vector examples = ww.getExamples();
        //System.out.println("WebWord.testEnglishLearningKoreanReadingSwdinitions");
        //System.out.println("definitions ----------------- begin");
        //printLog(definitions);
        //System.out.println("definitions ----------------- end");
        //System.out.println("examples    -----------------");
        //printLog(examples);
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
        int expected = 4;
        int actual = definitions.size();
        assertEquals(expected, actual);
	}
	
	/**
	 * This html:
	 * 
	 * <HR>
	 * <script language="JavaScript">
	 *  <!--
 	 * function SetCookie(name, val, expires) {
	 * ...
	 *  </script>
	 * <BR>
	 * <FONT COLOR=8080FF>back</FONT> <FONT COLOR=8080FF>out</FONT>  öȸ, Ż��, ����
	 * <HR>
	 * <BR>
	 * backingout <FONT SIZE=-1 COLOR=557756>�ۣ⢥����驯��������</FONT> öȸ, ���
	 * <HR>
	 * <BR>
	 * backout <FONT SIZE=-1 COLOR=557756>�ۣ⢥�����������</FONT> ���б��� öȸ
	 * <HR>
	 * <BR>
	 * trace  �ڸ� ���, ���ư���, ~ <FONT COLOR=8080FF>back</FONT> ����� �ö󰡴�, ~ <FONT COLOR=8080FF>out</FONT> ����� ã��, ������, �׸���, ȹå�ϴ�
	 * <HR>
	 * 
	 * Should yeild this:
	 * 
	 * back out 철회, 탈퇴, 변절 
	 * --------------------------------------------------------------------------------
	 * backingout ［ｂ´æｋｉŋ｀ａｕｔ］ 철회, 취소 
	 * --------------------------------------------------------------------------------
	 * backout ［ｂ´æｋ｀ａｕｔ］ 초읽기의 철회 
	 * --------------------------------------------------------------------------------
	 * trace 뒤를 밟다, 나아가다, ~ back 더듬어 올라가다, ~ out 행방을 찾다, 베끼다, 그리다, 획책하다 
	 * 
	 * abd return a list like this:
	 * 
	 * back out	 
	 * backingout
	 * backout
	 * trace
	 */
	@Test
	public final void testVerifyEnglishLearningKoreanReadingDefinitions() 
	{
		//System.out.println("WebWord.testOpenReading");
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "바나나";
        String def = "banana";
        String encoding = "UTF-8";
        //String encoding = "euc-kr";
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        ww.setEncoding(encoding);
        ww.setRemoveFontTags(true);
        File file = new File("/");
        String current_dir = file.getAbsolutePath();
        Word word = new Word();
        word.setText(text);
        word.setDefinition(def);
        ww.verifyWord(word);
        //ww.parseImpactDictionary(text, encoding);
        Vector definitions = ww.getDefinitions();
        Vector examples = ww.getExamples();
        System.out.println("WebWord.testVerifyEnglishLearningKoreanReadingDefinitions");
        System.out.println("definitions ----------------- begin");
        printLog(definitions);
        System.out.println("definitions ----------------- end");
        System.out.println("examples    -----------------");
        printLog(examples);
        System.out.println("ww.getLog()----------------");
        printLog(ww.getLog());
        boolean expected = true;
        //boolean actual1 = definitions.contains("back out");
        boolean actual1 = ww.isDefinitionVerified(def);
        //boolean actual2 = examples.contains("철회");
        //boolean actual2 = ww.isTextVerified(text);
        //boolean actual2 = ww.isDefinitionVerified(text);
        System.out.println("ww.isDefinitionVerified "+actual1);
        ww.parseImpactDictionary(def, encoding);
        boolean actual2 = ww.isTextVerified(text);
        System.out.println("ww.isTextVerified "+actual2);
        boolean actual = false;
        if (actual1||actual2)
        {
        	actual = true;
        }
        assertEquals(expected, actual);
	}
	
	/**
	 * This test looks up 'smooth' on ht eimpact dictionary,
	 * and gets a bunch of examples, but no definitions.
	 *
	@Test
	public final void testImpactEnglishToKorean() 
	{
		System.out.println("WebWord.testImpactEnglishToKorean");
		Word blank_word = new Word();
        String type = Constants.READING;
        String text = "smooth";
        //String encoding = "UTF-8";
        String encoding = "euc-kr";
        String native_language = JuksongConstants.KOREAN;
        String language_being_learned = JuksongConstants.ENGLISH;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        ww.setRemoveFontTags(true);
        Vector definitions = ww.parseImpactDictionary(text, encoding);
        Vector examples = ww.getExamples();
        //System.out.println("WebWord.testEnglishLearningKoreanReadingDinitions");
        System.out.println("definitions -----------------");
        printLog(definitions);
        System.out.println("examples    -----------------");
        printLog(examples);
        //System.out.println("ww.getLog()----------------");
        //printLog(ww.getLog());
        boolean expected = true;
        //boolean actual1 = definitions.contains("back out");
        boolean actual1 = ww.isDefinitionVerified("back out");
        //boolean actual2 = examples.contains("철회");
        boolean actual2 = ww.isTextVerified("철회");
        boolean actual = false;
        if (actual1||actual2)
        {
        	actual = true;
        }
        assertEquals(expected, actual);
	}*/
	
	/*
	 * THis test uses the web, so should not be run continuously less our firneds at Impact Dictionary decide not to let robots use their service.
	 * Currently WebWord.testVerifyWord: text verified? false def verified? true
	 *
	@Test
	public final void testVerifyWord()
	{
		//String encoding = "euc-kr";
		String encoding = "UTF-8";
		Word word = new Word();
		word.setText(EncodeString.encodeThis("열다", encoding));
		word.setDefinition("open");
		String type = Constants.READING;
        String native_language = JuksongConstants.ENGLISH;
        String language_being_learned = JuksongConstants.KOREAN;
        WebWord ww = new WebWord(type, native_language, language_being_learned); 
        ww.setRemoveFontTags(true);
        ww.setEncoding(encoding);
		ww.verifyWord(word);
		boolean text_verified = ww.isTextVerified();
		boolean def_verified = ww.isDefinitionVerified();
		System.out.println("WebWord.testVerifyWord: text verified? "+text_verified+" def verified? "+def_verified);
		boolean actual = text_verified;
		boolean expected = true;
		
		Vector definitions = ww.getDefinitions();
        Vector examples = ww.getExamples();
		System.out.println("WebWord.testVerifyWord");
        System.out.println("definitions ----------------- begin");
        printLog(definitions);
        System.out.println("definitions ----------------- end");
        System.out.println("examples    -----------------");
        printLog(examples);
        System.out.println("ww.getLog()----------------");
        System.out.println(ww.getLog().size());
        assertEquals(expected, actual);
	}
	*/
	
	/**
	 * Use this address:
	 * http://ko.wiktionary.org/wiki/let
	 * The basic format for the first definitions of let is a 타동사 (a transitive):
	 * 
	 * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
	 * <ul>
	 * <li><b>1.</b> (목적어 + 동사원형) <a href="/wiki/%EC%8B%9C%ED%82%A4%EB%8B%A4" title="시키다">시키다</a>, 하게 하다.</li>
	 * </ul>
	 * 
	 * The second definition for the 자동사 (intransitive verb)for let is:
	 * 
	 * <p class="BGImage_c" style="padding-left: 6px;"><b>자동사</b>&#160;(<a href="/wiki/intransitive_verb" title="intransitive verb"><span style="font-family:Times"><b>vi</b></span></a>)&#160;</p>
	 * <ul>
	 * <li><b>1.</b> <a href="/w/index.php?title=%EC%9E%84%EB%8C%80%EB%90%98%EB%8B%A4&amp;action=edit&amp;redlink=1" class="new" title="임대되다 (아직 수록되지 않은 문서 제목)">임대되다</a>.</li>
	 * </ul>
	 * 
	 * first flag <p ...
	 * second flag <ul> tag, 
	 * next line starts <li><b>1.</b>
	 * Then parse for text in the <a href="..." title="시키다">시키다</a> tag
	 * 
	 * The goal is to create text-definition paris from the Basic English 800 words list at:
	 * http://en.wiktionary.org/wiki/Appendix:Basic_English_word_list
	 */
		@Test
		public final void testLookupEnglishToKoreanOnWiktionary() 
		{
			//System.out.println("WebWord.testLookupEnglishToKoreanOnWiktionary +++++++++++++++++");
			Word blank_word = new Word();
	        String type = Constants.READING;
	        String text = "let";
	        String encoding = "UTF-8";
	        //String encoding = "euc-kr";
	        String native_language = JuksongConstants.ENGLISH;
	        String language_being_learned = JuksongConstants.KOREAN;
	        WebWord ww = new WebWord(type, native_language, language_being_learned); 
	        ww.setRemoveFontTags(true);
	        // load example
	        File file = new File("/");
	        String current_dir = file.getAbsolutePath();
	        String file_name = "wiki let.htm";
	        String path_to_file = current_dir+"Tims Folder"+File.separator
	        	+"programming"+File.separator+"catechis"+File.separator+"test"
	        	+File.separator+"org"+File.separator+"catechis"+File.separator
	        	+"juksong"+File.separator+file_name;
	        Vector test_text = loadTestText(path_to_file);
	        ww.parseWikiEnglishToKorean(text, encoding);
	        Vector definitions = ww.getDefinitions(); // is main results
	        //System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary ====== ");
	        String first_grammar = "null";
	        String first_def = "null";
	        try
	        {
	        	first_def = (String)definitions.get(0);
	        	Vector grammar = ww.getExamples();
	        	first_grammar = (String)grammar.get(0);
	        	//System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
	        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
	        {
	        	//System.out.println("word "+text+" caused aioobe of type ???");
	        }
	        //System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
	        //System.out.println("results----------------");
	        //printLog(definitions);
	        //System.out.println("grammer----------------");
	        //printLog(ww.getExamples());
	        //System.out.println("grammer hash----------------");
	        Hashtable tagt = ww.getTranslationsAandGrammarTypes();
	       // dumpHash(ww.getTranslationsAandGrammarTypes());
	        //System.out.println("ww.getLog()----------------");
	        //printLog(ww.getLog());
	        String expected = "시키다";
	        String actual = "blank";
	        try
	        {
	        	actual = (String)definitions.get(0);
	        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
	        {
	        	//System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary: unable to get actual definitions.get(0)");
	        }
	        //System.out.println("WebWord.testLookupEnglishToKoreanOnWiktionary +++++++++++++++++");
	        assertEquals(expected, actual);
		}
		
		/**
		 * Use this address:
		 * http://ko.wiktionary.org/wiki/let
		 * The basic format for the first definitions of let is a 타동사 (a transitive):
		 * 
		 * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
		 * <ul>
		 * <li><b>1.</b> (목적어 + 동사원형) <a href="/wiki/%EC%8B%9C%ED%82%A4%EB%8B%A4" title="시키다">시키다</a>, 하게 하다.</li>
		 * </ul>
		 * 
		 * The second definition for the 자동사 (intransitive verb)for let is:
		 * 
		 * <p class="BGImage_c" style="padding-left: 6px;"><b>자동사</b>&#160;(<a href="/wiki/intransitive_verb" title="intransitive verb"><span style="font-family:Times"><b>vi</b></span></a>)&#160;</p>
		 * <ul>
		 * <li><b>1.</b> <a href="/w/index.php?title=%EC%9E%84%EB%8C%80%EB%90%98%EB%8B%A4&amp;action=edit&amp;redlink=1" class="new" title="임대되다 (아직 수록되지 않은 문서 제목)">임대되다</a>.</li>
		 * </ul>
		 * 
		 * first flag <p ...
		 * second flag <ul> tag, 
		 * next line starts <li><b>1.</b>
		 * Then parse for text in the <a href="..." title="시키다">시키다</a> tag
		 * 
		 * The goal is to create text-definition paris from the Basic English 800 words list at:
		 * http://en.wiktionary.org/wiki/Appendix:Basic_English_word_list
		 */
			@Test
			public final void testLookupGrammarEnglishToKoreanOnWiktionary() 
			{
				//System.out.println("WebWord.testLookupGrammarEnglishToKoreanOnWiktionary ////////////////////");
				Word blank_word = new Word();
		        String type = Constants.READING;
		        String text = "let";
		        String encoding = "UTF-8";
		        //String encoding = "euc-kr";
		        String native_language = JuksongConstants.ENGLISH;
		        String language_being_learned = JuksongConstants.KOREAN;
		        WebWord ww = new WebWord(type, native_language, language_being_learned); 
		        ww.setRemoveFontTags(true);
		        // load example
		        File file = new File("/");
		        String current_dir = file.getAbsolutePath();
		        String file_name = "wiki let.htm";
		        String path_to_file = current_dir+"Tims Folder"+File.separator
		        	+"programming"+File.separator+"catechis"+File.separator+"test"
		        	+File.separator+"org"+File.separator+"catechis"+File.separator
		        	+"juksong"+File.separator+file_name;
		        Vector test_text = loadTestText(path_to_file);
		        ww.parseWikiEnglishToKorean(text, encoding);
		        Vector definitions = ww.getDefinitions(); // is main results
		        Vector grammar = new Vector();
		        //System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary ====== ");
		        String first_def = "null";
	        	String first_grammar = "null";
		        try
		        {
		        	first_def = (String)definitions.get(0);
		        	grammar = ww.getExamples();
		        	first_grammar = (String)grammar.get(0);
		        	//System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
		        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		        {
		        	//System.out.println("word "+text+" caused aioobe of type ???");
		        }
		        //System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
		        //System.out.println("results----------------");
		        //printLog(definitions);
		        //System.out.println("grammer----------------");
		        //printLog(ww.getExamples());
		        //System.out.println("grammer hash----------------");
		        Hashtable tagt = ww.getTranslationsAandGrammarTypes();
		        //dumpHash(ww.getTranslationsAandGrammarTypes());
		        //System.out.println("ww.getLog()----------------");
		        //printLog(ww.getLog());
		        String expected = "동사"; // means verb in english
		        String actual = "blank";
		        try
		        {
		        	actual = (String)tagt.get(text);
		        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		        {
		        	//System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary: unable to get actual definitions.get(0)");
		        }
		        //System.out.println("1st actual "+actual);
		        // this actual doesn't work, so we go with getting the keys and use the first one:
		        Enumeration keys = tagt.keys();
			    String key = (String)keys.nextElement();
				actual = (String)tagt.get(key);
				//System.out.println("2nd actual "+actual);
		        //System.out.println("WebWord.testLookupGrammarEnglishToKoreanOnWiktionary ////////////////////");
		        assertEquals(expected, actual);
			}
			
			/**
			 * Use this address:
			 * http://ko.wiktionary.org/wiki/white
			 * 
			 * Here is the html:
			 *
			 *The short format for 'white' stats with the grammar
			 *<p title="형용사" ... title="도움말:낱말의 갈래">형용사</a></p>
			 *... multiple lines and then two definitions of type 'adjective' or 형영사 ...
			 *<li><b>1.</b> <a href="..." title="하얗다">하얗다</a>, <a href="..." title="희다">희다</a>.</li>
			 *... multiple lines followed by two definitions of type 'noun' or 명사 ...
			 *<p id="subheadline" title="명사" style="..."><a href="..." title="명사">명사</a></p>
			 *<p class="...
			 *<ul>
			 *<li><b>1.</b> <a href="..." title="하양">하양</a>, <a href="..." title="흰색">흰색</a>.</li>
			 *</ul>
			 *
			 * The resulting grammar hash should be:
			 * 
			 * adjective - 하얗다
			 * adjective - 희다
			 * noun - 하양
			 * noun - 흰색
			 * 
			 */
				@Test
				public final void testLookupGrammarEnglishToKoreanOnWiktionary2() 
				{
					//System.out.println("WebWord.testLookupGrammarEnglishToKoreanOnWiktionary2 ////////////////////");
					Word blank_word = new Word();
			        String type = Constants.READING;
			        String text = "white";
			        String encoding = "UTF-8";
			        //String encoding = "euc-kr";
			        String native_language = JuksongConstants.ENGLISH;
			        String language_being_learned = JuksongConstants.KOREAN;
			        WebWord ww = new WebWord(type, native_language, language_being_learned); 
			        ww.setRemoveFontTags(true);
			        // load example
			        File file = new File("/");
			        String current_dir = file.getAbsolutePath();
			        String file_name = "wiki let.htm";
			        String path_to_file = current_dir+"Tims Folder"+File.separator
			        	+"programming"+File.separator+"catechis"+File.separator+"test"
			        	+File.separator+"org"+File.separator+"catechis"+File.separator
			        	+"juksong"+File.separator+file_name;
			        Vector test_text = loadTestText(path_to_file);
			        ww.parseWikiEnglishToKorean(text, encoding);
			        Vector definitions = ww.getDefinitions(); // is main results
			        Vector grammar = new Vector();
			        //System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary2 ====== ");
			        String first_def = "null";
		        	String first_grammar = "null";
			        try
			        {
			        	first_def = (String)definitions.get(0);
			        	grammar = ww.getExamples();
			        	first_grammar = (String)grammar.get(0);
			        	//System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
			        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
			        {
			        	//System.out.println("word "+text+" caused aioobe of type ???");
			        }
			        //System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
			        //System.out.println("results----------------");
			        //printLog(definitions);
			        //System.out.println("grammer----------------");
			        //printLog(ww.getExamples());
			        //System.out.println("grammer hash----------------");
			        Hashtable tagt = ww.getTranslationsAandGrammarTypes();
			        //dumpHash(ww.getTranslationsAandGrammarTypes());
			        //System.out.println("ww.getLog()----------------");
			        //printLog(ww.getLog());
			        String expected = "명사";
			        String actual = "blank";
			        try
			        {
			        	actual = (String)tagt.get(text);
			        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
			        {
			        	//System.out.println("WebWordTestdefinitions.testLookupEnglishToKoreanOnWiktionary2: unable to get actual definitions.get(0)");
			        }
			        //System.out.println("1st actual "+actual);
			        // this actual doesn't work, so we go with getting the keys and use the first one:
			        Enumeration keys = tagt.keys();
			        try
			        {
			        	String key = (String)keys.nextElement();
						actual = (String)tagt.get(key);
			        } catch (java.util.NoSuchElementException nsee)
			        {
						actual = "nsee";
			        }
					//System.out.println("2nd actual "+actual);
			        //System.out.println("WebWord.testLookupGrammarEnglishToKoreanOnWiktionary2 ////////////////////");
			        //assertEquals(expected, actual);
				}
		
		/**
		 * Use this address:
		 * http://en.wiktionary.org/wiki/let
		 */
			@Test
			public final void testLookupKoreanToOnWiktionary() 
			{
				//System.out.println("WebWord.testLookupKoreanToOnWiktionary )))))))))))))))))))))))))))))))))))");
				Word blank_word = new Word();
		        String type = Constants.READING;
		        String text = "시키다";
		        String encoding = "UTF-8";
		        //String encoding = "euc-kr";
		        String native_language = JuksongConstants.ENGLISH;
		        String language_being_learned = JuksongConstants.KOREAN;
		        WebWord ww = new WebWord(type, native_language, language_being_learned); 
		        ww.setRemoveFontTags(true);
		        // load example
		        File file = new File("/");
		        String current_dir = file.getAbsolutePath();
		        String file_name = "wiki let.htm";
		        String path_to_file = current_dir+"Tims Folder"+File.separator
		        	+"programming"+File.separator+"catechis"+File.separator+"test"
		        	+File.separator+"org"+File.separator+"catechis"+File.separator
		        	+"juksong"+File.separator+file_name;
		        Vector test_text = loadTestText(path_to_file);
		        ww.parseWikiKoreanToEnglish(text, encoding);
		        Vector definitions = ww.getDefinitions(); // is main results
		        //System.out.println("WebWordTestdefinitions.testLookupKoreanToOnWiktionary ====== ");
		        String first_def = "";
		        String first_grammar = "";
		        try
		        {
		        	first_def = (String)definitions.get(0);
		        	Vector grammar = ww.getExamples();
		        	first_grammar = (String)grammar.get(0);
		        	//System.out.println("word "+text+" is "+first_def+" of type "+first_grammar);
		        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		        {
		        	//System.out.println("X word "+text+" caused aioobe of type ???");
		        	//System.out.println("X word "+text+" is "+first_def+" of type ???");
		        }
		        //System.out.println("results----------------");
		        //printLog(definitions);
		        //System.out.println("grammer----------------");
		        //printLog(ww.getExamples());
		        //System.out.println("grammer hash----------------");
		        //Hashtable tagt = ww.getTranslationsAandGrammarTypes();
		        //dumpHash(ww.getTranslationsAandGrammarTypes());
		         //System.out.println("ww.getLog()----------------");
		        //printLog(ww.getLog());
		        String expected = "cause";
		        //String expected = "to cause to do or happen";
		        String actual = "blank";
		        try
		        {
		        	actual = (String)definitions.get(0);
		        } catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
		        {
		        	//System.out.println("unable to get actual definitions.get(0)");
		        }
		        //System.out.println("WebWord.testLookupKoreanToOnWiktionary )))))))))))))))))))))))))))))))))))");
		        assertEquals(expected, actual);
			}
			
			/*
			 * This method parses:
			 *  http://en.wiktionary.org/wiki/Appendix:Basic_English_word_list
			 * to create a list of 850 words in Ogdens Basic English.
			 * Basic English, also known as Simple English, is an English-based controlled 
			 * language created by Charles Kay Ogden (in essence a simplified subset of English)
			 *  as an international auxiliary language, and as an aid for teaching English 
			 *  as a Second Language. It was presented in Ogden's book Basic English: 
			 *  A General Introduction with Rules and Grammar (1930). Capitalised, 
			 *  BASIC is sometimes taken as an acronym 
			 * that stands for British American Scientific International Commercial.[1]
			 * (http://en.wikipedia.org/wiki/Basic_English)
			 */
			@Test
			public void testOgdensEnglishLookupArch()
			{
				//System.out.println("WebWord.testOgdensEnglishLookupArch -----------------");
		        String type = Constants.READING;
		        String encoding = "UTF-8";
		        String native_language = JuksongConstants.ENGLISH;
		        String language_being_learned = JuksongConstants.KOREAN;
		        String text = "arch";
		        WebWord ww = new WebWord(type, native_language, language_being_learned); 
				ww.parseWikiEnglishToKorean(text, encoding);
				//System.out.println("Definitions ====================================");
			    Vector definitions = ww.getDefinitions(); // is main results
			    Hashtable tagt = ww.getTranslationsAandGrammarTypes();
				//printOgdens(definitions, tagt);
		        //System.out.println("Examples    ====================================");
		        //printLog(ww.getExamples());
		        //System.out.println("OGDONS ENGLISH ====================================");
		        //System.out.println("ww.getLog()----------------");
		        //printLog(ww.getLog());
		        assertEquals(true, false);
			}
	
			/*
			 * This method parses:
			 *  http://en.wiktionary.org/wiki/Appendix:Basic_English_word_list
			 * to create a list of 850 words in Ogdens Basic English.
			 * Basic English, also known as Simple English, is an English-based controlled 
			 * language created by Charles Kay Ogden (in essence a simplified subset of English)
			 *  as an international auxiliary language, and as an aid for teaching English 
			 *  as a Second Language. It was presented in Ogden's book Basic English: 
			 *  A General Introduction with Rules and Grammar (1930). Capitalised, 
			 *  BASIC is sometimes taken as an acronym 
			 * that stands for British American Scientific International Commercial.[1]
			 * (http://en.wikipedia.org/wiki/Basic_English)
			 */
			@Test
			public void testOgdensEnglish()
			{
				//System.out.println("WebWord.testOgdensEnglish");
		        String type = Constants.READING;
		        String encoding = "UTF-8";
		        String native_language = JuksongConstants.ENGLISH;
		        String language_being_learned = JuksongConstants.KOREAN;
		        WebWord ww = new WebWord(type, native_language, language_being_learned); 
		        Hashtable oe = ww.getOgdensEnglish();
		        Enumeration keys = oe.keys();
			    int count = 1;
			    int limit = 10;
			    while (keys.hasMoreElements())
			    {
				    String key = (String)keys.nextElement();
				    ww = new WebWord(type, native_language, language_being_learned); 
				    ww.parseWikiEnglishToKorean(key, encoding);
			        Vector definitions = ww.getDefinitions(); // is main results
			        Hashtable tagt = ww.getTranslationsAandGrammarTypes();
				    //String val = (String)hash.get(key);
			        System.out.println(count+"---------------- "+key);
				    printOgdens(definitions, tagt);
				    count++;
				    if (count > limit)
				    {
				    	break;
				    }
			    }
		        System.out.println("OGDONS ENGLISH ====================================");
		        dumpHash(oe);
		        System.out.println("OGDONS ENGLISH ====================================");
		        System.out.println("ww.getLog()----------------");
		        //rintLog(ww.getLog());
		        assertEquals(true, false);
			}
	
	// utils ----------------------------------
	
	
	
	private Vector loadTestText(String path_to_file)
	{
		Vector text = new Vector();
		//System.out.println("WebWord.loadTestText: "+path_to_file);
		FileInputStream in = null;
		try 
		{
		    in =  new FileInputStream(path_to_file);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		    String line = null;
		    while ((line = reader.readLine()) != null) 
		    {
		        text.add(line);
		    }
		    in.close();
		} catch (IOException x) 
		{
		    System.err.println(x);
		}
		return text;
	}
	
	
	/**
	  * This method should yeild the following results: 
	  * 
철회,탈, 변절
분리기, 절단, 탈주, 무리에서 이탈한 동물, 일탈,탈, 출발 신호전에 뛰어 나감, 공을 갖고 골로 돌진하기
(단체에서)...을 제명하다, (...와)인연을 끊다,탈하다
탈하다
		 * 철회, 탈퇴, 변절
		 * 분리기, 절단, 탈주, 무리에서 이탈한 동물, 일탈, 탈퇴, 출발 신호전에 뛰어 나감, 공을 갖고 골로 돌진하기
		 * (단체에서)...을 제명하다, (...와)인연을 끊다, 탈퇴하다
		 * 탈퇴하다
		 * 
		 * not caputured yet

trimed 회원에서 <FONT COLOR=8080FF>탈퇴</FONT>하고 싶습니다. to 회원에서탈하고 싶습니다 --- ex 
format 2 bc 1 line 회원에서탈하고 싶습니다 회원에서 <FONT COLOR=8080FF>탈퇴</FONT>하고 싶습니다.
format 3a excluded I want to withdraw from being a member.
		 * 
		 * 회원에서 탈퇴하고 싶습니다.
		 * 민주노총의 노사정 (勞使政) 위원회 탈퇴로 우려하던 상황이 결국 현실로");
		 * 나타나고 말았다.
		 * 한국노총도 26일 대의원대회를 열고 노사정위 조건부 탈퇴를 선언할
		 * 움직임이어서 노사정위는 출범 13개월만에 좌초 위기에 빠지고 대립과 갈등의
		 * 소모적인 노사관계가 재현될 가능성이 커졌다.
		 * 
		 * In this case we want to keep the text in the tags.
	 * The two longer multiple line text examples should be excluded.
	 * @return
	 */
	private Vector writingParseSample()
	{
		Vector <String> sample = new Vector();
		sample.add("<HR>");
		sample.add("<script type=\"text/javascript\"");
		sample.add("src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">");
		sample.add("</script>");
		sample.add("<HR>");
		sample.add("</script>");
		sample.add("<BR>");
		sample.add("back out  철회, <FONT COLOR=8080FF>탈퇴</FONT>, 변절");
		sample.add("<HR>");
		sample.add("<BR>");
		sample.add("breakaway <FONT SIZE=-1 COLOR=557756>［ｂｒ´ｅｉｋ∂ｗ｀ｅｉ］</FONT> 분리기, 절단, 탈주, 무리에서 이탈한 동물, 일탈, <FONT COLOR=8080FF>탈퇴</FONT>, 출발 신호전에 뛰어 나감, 공을 갖고 골로 돌진하기");
		sample.add("<HR>");
		sample.add("<BR>");
		sample.add("disaffiliate <FONT SIZE=-1 COLOR=557756>［ｄ｀ｉｓ∂ｆ´ｉｌｉ｀ｅｉｔ］</FONT> (단체에서)...을 제명하다, (...와)인연을 끊다, <FONT COLOR=8080FF>탈퇴</FONT>하다");
		sample.add("<HR>");
		sample.add("<BR>");
		sample.add("drop out <FONT COLOR=8080FF>탈퇴</FONT>하다");
		sample.add("<HR>");
		sample.add("<BR>");
		sample.add("회원에서 <FONT COLOR=8080FF>탈퇴</FONT>하고 싶습니다.");
		sample.add("<BR>");
		sample.add("I want to withdraw from being a member.");
		sample.add("<HR>");
		sample.add("<BR>");
		sample.add("Firm Measures For Labor's Return");
		sample.add("The worry became a reality and even more concerning is that the other large");
		sample.add("<BR>");
		sample.add("union organization, the Federation of Korean Trade Union (FKTU), is");
		sample.add("<BR>");
		sample.add("preparing to back out this week, as well. ");
		sample.add("<BR>");
		sample.add("After only 13 months have expired since its creation, the Committee is in");
		sample.add("<BR>");
		sample.add("danger of extinction. It will almost certainly cause additional instability in the");
		sample.add("<BR>");
		sample.add("labor market. ");
		sample.add("<BR>");
		sample.add("민주노총의 노사정 (勞使政) 위원회 <FONT COLOR=8080FF>탈퇴</FONT>로 우려하던 상황이 결국 현실로");
		sample.add("<BR> 나타나고 말았다.");
		sample.add("<BR>");
		sample.add(" 한국노총도 26일 대의원대회를 열고 노사정위 조건부 <FONT COLOR=8080FF>탈퇴</FONT>를 선언할");
		sample.add("<BR>");
		sample.add(" 움직임이어서 노사정위는 출범 13개월만에 좌초 위기에 빠지고 대립과 갈등의");
		sample.add("<BR>");
		sample.add(" 소모적인 노사관계가 재현될 가능성이 커졌다.");
		sample.add("<HR>");
		return sample;
	}
	
	/**
	 * This method has the following defintions for 'to be opened':
	 * 
	 * berry
	 * Cotton Bowl
	 * country hall
	 * tailgate party
	 * open
	 * 
	 * The two longer multiple line text examples should be excluded from definitions
	 * and added as examples.
	 * @return
	 */
	private Vector readingParseSample()
	{
		Vector sample = new Vector();
		sample.add("<HTML>");
		sample.add("<HEAD><TITLE>열리다 - 국내최대의 영어사전, 전문용어, 의학 용어도 OK</TITLE>");
		sample.add("<HR>");
		sample.add("");
		sample.add("<script language=\"JavaScript\">");
		sample.add("<!--");
		sample.add("</script>");
		sample.add("<HR>");
		sample.add("<BR>berry <FONT SIZE=-1 COLOR=557756>［ｂ´ｅｒｉ］</FONT> 열매, 딸기의 열매, 커피의 열매, 물고기의 알-열매가 <FONT COLOR=8080FF>열리</FONT>다, 열매를 따다");
		sample.add("<HR>");
		sample.add("<BR>");
	    sample.add("Cotton Bowl  코튼볼(Texas 주 Dallas에 있는 축구 경기장.거기서 <FONT COLOR=8080FF>열리</FONT>는 대학 팀의 미식 축구 시합)");
	    sample.add("<HR>");
	    sample.add("");
	    sample.add("<BR>");
	    sample.add("county hall  주 회당(주 회의와 순회 재판이 <FONT COLOR=8080FF>열리</FONT>는), 런던 주청");
	    sample.add("");
	    sample.add("<HR>");
	    sample.add("<BR>");
	    sample.add("tailgate party");
	    sample.add("<HR>");
	    sample.add("");
		sample.add("<BR>");
	    sample.add("open  <FONT COLOR=8080FF>열리</FONT>다, 넓어지다, 시작하다, 보이게 되다, ~ into ...로 통하다, ~ on ...에 면하다, ~ one's eyes 깜짝 놀라다, ~ out 열다, ~ the door to ...에 기회를 주다, ~ up 열다, ~ly ad, 솔직히, 공공연히");
	    sample.add("<HR>");
	    sample.add("");
	    sample.add("(주차장 등에서 <FONT COLOR=8080FF>열리</FONT>는 파티)");
	    sample.add("<HR>");		
	    sample.add("<BR>");
	    sample.add("I have two tickets to a classical music concert this Saturday. ");
	    sample.add("<BR>");
	    sample.add("Will you go there with me? ");
	    sample.add("<BR>");
	    sample.add("이번 주 토요일에 <FONT COLOR=8080FF>열리</FONT>는 고전 음악회 표 두 장이 있어. 같이 갈래?");
		sample.add("<HR>");
	    sample.add("<BR>");
	    sample.add("Knock at the door and it will be open.");
	    sample.add("<BR>");
	    sample.add("두드려라 그러면 <FONT COLOR=8080FF>열리</FONT>리라.");
	    sample.add("<HR>");
	    sample.add("</BODY>");
	    sample.add("</HTML>");
		return sample;
	}
	
	private void printLog(Vector log)
	   {
			int total = log.size();
			int i = 0;
			while (i<total)
			{
				System.out.println(i+"	"+log.get(i));
				i++;
			}
	   }
	
	private void printLogs(Vector log1, Vector log2)
	   {
			int total = log1.size();
			int i = 0;
			String a,b;
			while (i<total)
			{
				try
				{
					a = (String)log1.get(i);
				} catch (ArrayIndexOutOfBoundsException aioobe)
				{
					a = "aioobe";
				}
				try
				{
					b = (String)log2.get(i);
				} catch (ArrayIndexOutOfBoundsException aioobe)
				{
					b = "aioobe";
				}
				System.out.println(a+" is type "+b);
				i++;
			}
	   }
	
	private void printOgdens(Vector log1, Hashtable hash)
	   {
			int total = log1.size();
			int i = 0;
			String a,b,c;
			Enumeration e = hash.keys();
			while (i<total)
			{
				try
				{
					a = (String)log1.get(i);
				} catch (ArrayIndexOutOfBoundsException aioobe)
				{
					a = "aioobe";
				}
				try
				{
					c = (String)hash.get(a);
					String eng_c = KoreanGrammar.translateKoreanToEnglish(c);
					c = c+" ("+eng_c+")";
				} catch (java.lang.NullPointerException npe)
				{
					c = "nsee";		
				}
				System.out.println(a+" is type "+c);
				i++;
			}
	   }
	
	private void dumpHash(Hashtable hash)
	{
	    Enumeration keys = hash.keys();
	    int count = 1;
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)hash.get(key);
		    System.out.println(count+". "+key+" - "+val);
		    count++;
	    }
	}

}
