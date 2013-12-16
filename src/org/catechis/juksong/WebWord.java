package org.catechis.juksong;

import java.net.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.Vector;
import java.awt.Image;
import javax.imageio.ImageIO;

import org.catechis.EncodeString;
import org.catechis.Transformer;
import org.catechis.constants.Constants;
import org.catechis.constants.EnglishGrammar;
import org.catechis.constants.KoreanGrammar;
import org.catechis.dto.Word;
import org.catechis.juksong.JuksongConstants;

import java.util.Hashtable;

/**
 * Utilities for looking up words from the web.
 * Impact Dictionary requires searches to be in
 * encoding = "euc-kr";
 * tried UTF-8 but it didn't work.
 * 
 * Got a message from impact:
 * 다음 문자들은 키워드에 포함될 수 없습니다.
 * "The following characters can not be included"
"; ^ , " | < > [ ] { } ( ) * & ~ \"

from http://dic.impact.pe.kr/ecmaster-cgi/search.cgi?kwd=%B2%CF%2C+%C1%A6%B9%FD&bool=and&word=yes
in indoct when trying to look up 	꽤, 제법 	pretty much 

 * @author a
 *
 */
public class WebWord
{
	private String type;
	private String native_language;
	private String language_being_learned;
	private String language_profile;
	private String encoding;
	private boolean remove_font_tags;
	// used for impact dictionary parsing
	private Vector results;
	private Vector examples;
	boolean first_horizontal_rule_marker; // this should only be set after first appearance and left on after that.
	boolean horizontal_rule_marker;
	boolean bracket_marker;
	boolean bracket_marker_2;
	boolean return_results;
    int bracket_counter;
    boolean example_marker;
    private boolean text_verified;
    private boolean definition_verified;
    // used for wiki
    
    private boolean first_mark;
    private boolean second_mark;
    private boolean third_mark; 
    private boolean grammar_mark; // used to collect definitions with the same grammar type
    private String grammar_name; // saved for the <p ... first mark so that the grammar tag can be retrieved later
    private String extra_info; // saved in Korean to English parsing for extra info if the def is found later.
    private Hashtable translations_and_grammar_types;
    
    private Hashtable ogdens_english;
	
	    String address;
	    Vector <String> log;
	    StringBuffer buf_log;
	    
	    /**
	     * The default base url is http://dic.impact.pe.kr/ecmaster-cgi/
	     */
	    public WebWord() 
	    {
	    	address = 				 "http://dic.impact.pe.kr/ecmaster-cgi/";
	    	//String writing_address = "http://dic.impact.pe.kr/ecmaster-cgi/";
	    	//String start= "search.cgi?kwd=";
	    	//String end = "&bool=and&word=yes";
	    	type = Constants.READING;
	    	native_language = JuksongConstants.KOREAN;
	    	language_being_learned = JuksongConstants.ENGLISH;
	    	remove_font_tags = true;
	    	language_profile = FarmingTools.getLanguageProfile(native_language, language_being_learned, type);
	    	log = new Vector();
	    	log.add("type "+type);
	    	log.add("native_language "+native_language);
	    	log.add("language_being_learned "+language_being_learned);
	    	log.add("remove_font_tags "+remove_font_tags);
	    }
	    
	    public WebWord(String _address) 
	    {
	    	address = _address;
	    	type = Constants.READING;
	    	native_language = JuksongConstants.KOREAN;
	    	language_being_learned = JuksongConstants.ENGLISH;
	    	remove_font_tags = true;
	    	language_profile = FarmingTools.getLanguageProfile(native_language, language_being_learned, type);
	    	log = new Vector();
	    	log.add("type "+type);
	    	log.add("native_language "+native_language);
	    	log.add("language_being_learned "+language_being_learned);
	    	log.add("remove_font_tags "+remove_font_tags);
	    }
	    
	    /**
	     * This constructor should be used with various online dictionries.
	     * @param _type of test.
	     * @param _native_language is used to exclude this language from reading test lookups.
	     * @param _language_being_learned for the opposite reason.
	     * 
	     * Reading tests have text as the question and definitions as answers.
	     * Writing tests have definitions as questions and texts as answers.
	     * 
	     * If native language is English, Language being learned is Korean, 
	     * a reading test lookup result:
	     * <BR>
		 * back out  철회, <FONT COLOR=8080FF>탈퇴</FONT>, 변절
		 * <HR>
		 * 
	     * a writing test lookup result:
	     * <BR>
		 * <FONT COLOR=8080FF>withdrawn</FONT>  내성적인, 인적이 드문
		 * <HR>
		 * 
		 * This is for the word 
		 * text = definition
		 * 탈퇴  = withdraw
		 * 
		 * 
		 * 
		 * If native language is Korean, Language being learned is English,
		 * text is English, definition is Korean(or English)
		 * text 	= definition
		 * withdraw = 탈퇴
		 * 
		 * This is important because we want to srtip out all non-ascii text from reading definitions.
		 * And all ascii from writing questions.
	     */
	    public WebWord(String _type, String _native_language, String _language_being_learned) 
	    {
	    	type = _type;
	    	native_language = _native_language;
	    	language_being_learned = _language_being_learned;
	    	remove_font_tags = true;
	    	language_profile = FarmingTools.getLanguageProfile(native_language, language_being_learned, type);
	    	examples = new Vector(); // this is done here for the parseImpactLine test
	    	log = new Vector();
	    	log.add("type "+type);
	    	log.add("native_language "+native_language);
	    	log.add("language_being_learned "+language_being_learned);
	    	log.add("remove_font_tags "+remove_font_tags);
	    }
	    
	    public String removeIllegalChars(String search_text)
	    {
	    	append(log);
	    	Vector <String> illegal_chars = getIllegalChars();
	    	int size = illegal_chars.size();
	    	int i = 0;
	    	while (i<size)
	    	{
	    		String illegal_char = (String)illegal_chars.get(i);
	    		if (search_text.contains(illegal_char))
	    		{
	    			search_text = search_text.replace(illegal_char, "");
	    			//log.add("replaced "+illegal_char+" "+search_text);
	    		} else
	    		{
	    			//log.add("doesn't contain "+illegal_char);
	    		}
	    		i++;
	    	}
	    	return search_text;
	    }
	    
	    /**
	     * Return a vector with these chars:
	     * "; ^ , " | < > [ ] { } ( ) * & ~ \"
	     * @return
	     */
	    private Vector <String> getIllegalChars()
	    {
	    	Vector <String> illegal_chars = new Vector <String> ();
	    	illegal_chars.add(";");
	    	illegal_chars.add("^");
	    	illegal_chars.add(",");
	    	illegal_chars.add("\"");
	    	illegal_chars.add("|");
	    	illegal_chars.add("<");
	    	illegal_chars.add(">");
	    	illegal_chars.add("[");
	    	illegal_chars.add("]");
	    	illegal_chars.add("{");
	    	illegal_chars.add("}");
	    	illegal_chars.add("(");
	    	illegal_chars.add(")");
	    	illegal_chars.add("*");
	    	illegal_chars.add("&");
	    	illegal_chars.add("~");
	    	illegal_chars.add("\\");
	    	return illegal_chars;
	    }
	    
	    /**
	     * The problem with checking if a line contains both text and definition, is that this might cover
	     * for a misspelled text or definition, if just one of them checks out.  Also, advanced users define a word
	     * in the language being learned, which might contain the original text, or part of it.
	     * @param text
	     * @param definition
	     * @return
	     */
	    private boolean verifyParse(String text, String definition)
	    {
	    	boolean check = true;
	    	String input_line = "";
	    	boolean verified = false;
	    	BufferedReader in = getBuff(text);
    		try
    		{
    			while ((input_line = in.readLine()) != null)
        		{
    				if (check)
    				{
    					if (input_line.contains(definition)||input_line.contains(text))
    					{
    						verified = true;
    						check = false;
    					}
    				}
        			parseImpactLine(input_line);
        		}
    			in.close();
    		} catch (IOException ioe)
		    {
	    		 System.err.println("IOException 1");
		    }
    		return verified;
	    }
	    
	    /**
	     * To use this method, the caller is responsible for setting the encoding, and getting the definition results and examples.
	     * This is a separate method as it takes longer to verify a word.
	     * This includes saving definitions and examples for each lookup of text and definition.
	     * @param word
	     * @return
	     */
	    public void verifyWord(Word word)
	    {
	    	boolean check = true;
	    	String text = word.getText();
	    	String definition = word.getDefinition();
	    	setupForParse();
	    	// first search for with the text
	    	type = FarmingTools.getAppropriateType(native_language, language_being_learned, "text");
	    	text_verified = verifyParse(text, definition);
    		Vector definitions = results;
    		// now search for the definition
    		setupForParse();
	    	type = FarmingTools.getAppropriateType(native_language, language_being_learned, "definition");
	    	definition_verified = verifyParse(definition, text);
	    	// replace results with previous definitions;
    		results = definitions;
	    }
	    
	    public void setupForParse()
	    {
	    	address = "http://dic.impact.pe.kr/ecmaster-cgi/";
	    	results = new Vector();
	    	examples = new Vector();
	    	first_horizontal_rule_marker = false; // this should only be set after first appearance and left on after that.
	    	horizontal_rule_marker = false;
	    	bracket_marker = false;
	    	bracket_marker_2 = false;
		    bracket_counter = 0;
		    example_marker = false;
		    text_verified = false;
		    definition_verified = false;
	    }
	    
	    private BufferedReader getBuff(String text)
	    {
	    	BufferedReader in = null;
	    	String trailer = "&bool=and&word=yes";
	    	String search_text = "";
	    	try
	    	{
	    		search_text = URLEncoder.encode(text, encoding);
	    	} catch (UnsupportedEncodingException uee)
	    	{
	    		results.add("UnsupportedEncodingException");
	    		return_results = true;
	    	} catch (java.lang.NullPointerException npe)
	    	{
	    		System.err.println("WebWord.getBuff: encoding "+encoding);
	    		System.err.println(npe.toString());
	    	}
	    	search_text = removeIllegalChars(search_text);
	    	URL url = null;
	    	try
	    	{
	    		url = new URL(address+"search.cgi?kwd="+search_text+trailer);
	    		in = new BufferedReader(new InputStreamReader(url.openStream()));
	    	} catch (MalformedURLException efurle)
	    	{
	    		String url_str = url.toString();
	    		if (url_str == null)
	    		{
	    			log.add("url is null "+search_text+" ");
	    		}
	    		log.add("MalformedURLException");
	    	} catch (IOException ioe)
		    {
	    		 System.err.println("IOException 2");
		    }
	    	return in;
	    }
	    
	    /**
	     * Load an html page using address+"search.cgi?kwd="+search_text+trailer format
	     * 
	     * and put each line in a Vector.
	     */
	    public Vector parseImpactDictionary(String text, String _encoding)
	    { 
	    	encoding = _encoding;
	    	setupForParse();
	    	String trailer = "&bool=and&word=yes";
	    	String search_text = "";
	    	try
	    	{
	    		search_text = URLEncoder.encode(text, encoding);
	    	} catch (UnsupportedEncodingException uee)
	    	{
	    		results.add("UnsupportedEncodingException");
	    		return results;
	    	}
	    	search_text = removeIllegalChars(search_text);
	    	String input_line = "";
	    	URL url = null;
	    	try
	    	{
	    		url = new URL(address+"search.cgi?kwd="+search_text+trailer);
	    		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    		while ((input_line = in.readLine()) != null)
	    		{
	    			if (language_profile.equals(JuksongConstants.EKR))
	    			{
	    				parseImpactDefinitionLine(input_line);
	    			} else
	    			{
	    				parseImpactLine(input_line);
	    			}
	    			if (return_results==true)
	    			{
	    				return results;
	    			}
	    		}
	    		in.close();
	    	} catch (MalformedURLException efurle)
	    	{
	    		String url_str = url.toString();
	    		if (url_str == null)
	    		{
	    			log.add("url is null "+search_text+" ");
	    		}
	    		log.add("MalformedURLException");
	    	} catch (IOException ioe)
		    {
	    		 System.err.println("IOException 3");
		    }
	    	log.add("WebWord.parseImpactDictionary: loaded "+url.toString());
	    	return results;
	    }
	    
	    /** --------------------- wiki parse ----------------------------------
	     * Load a page from the Wiktionary to get a definition from the first one.
	     * first mark <p ...
	     * second mark <ul> tag, 
	     * next line starts <li><b>1.</b>
	     * Then parse for text in the <a href="..." title="시키다">시키다</a> tag
	     * @param text
	     * @param _encoding
	     * @return
	     */
	    public Vector parseWikiEnglishToKorean(String text, String _encoding)
	    {
	    	address = "http://ko.wiktionary.org/wiki/";
	    	setupForWikiParse(_encoding);
	    	String search_text = "";
	    	try
	    	{
	    		search_text = URLEncoder.encode(text, encoding);
	    	} catch (UnsupportedEncodingException uee)
	    	{
	    		results.add("UnsupportedEncodingException");
	    		return results;
	    	}
	    	search_text = removeIllegalChars(search_text);
	    	String input_line = "";
	    	URL url = null;
	    	try
	    	{
	    		url = new URL(address+search_text);
	    		//System.err.println(url);
	    		log.add(url.toString());
	    		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    		int count = 0;
	    		while ((input_line = in.readLine()) != null)
	    		{
	    			//System.out.println(count+" "+input_line);count++;
	    			parseWikiEnglishToKoreanLine(input_line);
	    		}
	    		in.close();
	    	} catch (MalformedURLException efurle)
	    	{
	    		String url_str = url.toString();
	    		if (url_str == null)
	    		{
	    			log.add("url is null "+search_text+" ");
	    		}
	    		log.add("MalformedURLException");
	    	} catch (IOException ioe)
		    {
	    		 log.add("IOException 4: no definitions found ");
		    }
	    	log.add("WebWord.parseWikiEnglishToKorean: loaded "+url.toString());
	    	return results;
	    }
	    
	    /** --------------------- wiki parse ----------------------------------
	     * Load a page from the English Wiktionary to get a definition of a Korean word in English.
	     * 
	     * Definitions look like this:
	     * <h3><span class="editsection">[<a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EB%8B%A4&amp;action=edit&amp;section=4" title="Edit section: Verb">edit</a>]</span> <span class="mw-headline" id="Verb">Verb</span></h3>
		 * <p><b><span class="Kore KO" lang="ko" xml:lang="ko">시키다</span></b> (sikida)&#160;<i>infinitive</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%EC%BC%9C&amp;action=edit&amp;redlink=1" class="new" title="시켜 (page does not exist)">시켜</a></span>&#160;<i>or</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EC%96%B4&amp;action=edit&amp;redlink=1" class="new" title="시키어 (page does not exist)">시키어</a></span>, <i>sequential</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EB%8B%88&amp;action=edit&amp;redlink=1" class="new" title="시키니 (page does not exist)">시키니</a></span></p>
		 * <ol>
		 * <li>to <a href="/wiki/cause" title="cause">cause</a> to do or happen</li>
		 * 
		 * first_mark starts with <h3>...
		 * save text in id="Verb" for latter grab.
		 * 
		 * second mark
	     * @param text
	     * @param _encoding
	     * @return
	     */
	    public Vector parseWikiKoreanToEnglish(String text, String _encoding)
	    {
	    	address = "http://en.wiktionary.org/wiki/";
	    	setupForWikiParse(_encoding);
	    	String search_text = "";
	    	try
	    	{
	    		search_text = URLEncoder.encode(text, encoding);
	    	} catch (UnsupportedEncodingException uee)
	    	{
	    		results.add("UnsupportedEncodingException");
	    		return results;
	    	}
	    	search_text = removeIllegalChars(search_text);
	    	String input_line = "";
	    	URL url = null;
	    	try
	    	{
	    		url = new URL(address+search_text);
	    		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    		while ((input_line = in.readLine()) != null)
	    		{
	    			parseWikiKoreanToEnglishLine(input_line, text);
	    		}
	    		in.close();
	    	} catch (MalformedURLException efurle)
	    	{
	    		String url_str = url.toString();
	    		if (url_str == null)
	    		{
	    			log.add("url is null "+search_text+" ");
	    		}
	    		log.add("MalformedURLException");
	    	} catch (IOException ioe)
		    {
	    		 System.err.println("IOException in parseWikiKoreanToEnglish()");
		    }
	    	log.add("WebWord.parseWikiKoreanToEnglish: loaded "+url.toString());
	    	return results;
	    }
	    
	    public void setupForWikiParse(String _encoding)
	    {
	    	encoding = _encoding;
	    	first_mark = false;
	    	second_mark = false;
	    	results = new Vector();
	    	examples = new Vector();
	    	grammar_name = "";
	    	translations_and_grammar_types = new Hashtable();
	    }
	    
	    /**
	     * First type:
	     * 
	     * first mark <p ... (capture grammar here)
	     * second mark <ul> tag, 
	     * next line starts <li><b>1.</b>
	     * Then parse for text in the <a href="..." title="시키다">시키다</a> tag
	     * 
	     * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
	     * <ul>
	     * <li><b>1.</b> (목적어 + 동사원형) <a href="/wiki/%EC%8B%9C%ED%82%A4%EB%8B%A4" title="시키다">시키다</a>, 하게 하다.</li>
	     * 
	     * 
	     * 
	     * <p title="동사" id="subheadline" style="font-weight: bold; background-color: #FFF;"><a href="/wiki/%EB%8F%84%EC%9B%80%EB%A7%90:%EB%82%B1%EB%A7%90%EC%9D%98_%EA%B0%88%EB%9E%98" title="도움말:낱말의 갈래">동사</a></p>
	     * <p class="BGImage_c" style="padding-left: 6px;"><b>attack</b></p>
	     * <ul>
	     * <li><b>1.</b> <a href="/wiki/%EA%B3%B5%EA%B2%A9%ED%95%98%EB%8B%A4" title="공격하다">공격하다</a>, <a href="/w/index.php?title=%EC%B9%A8%EB%B2%94%ED%95%98%EB%8B%A4&amp;action=edit&amp;redlink=1" class="new" title="침범하다 (아직 수록되지 않은 문서 제목)">침범하다</a>.</li></ul>
	     * 
	     * Format for:
	     * http://ko.wiktionary.org/wiki/white
		 *
		 * ...
		 * <p title="형용사" ... title="도움말:낱말의 갈래">형용사</a></p>											1st mark, save line
		 * ... multiple lines and then two definitions of type 'adjective' or 형영사 ...
		 * <li><b>1.</b> <a href="..." title="하얗다">하얗다</a>, <a href="..." title="희다">희다</a>.</li>
		 * ... multiple lines followed by two definitions of type 'noun' or 명사 ...
		 * ... the next first mark is <p id="subheadline" title=
		 * <p id="subheadline" title="명사" style="..."><a href="..." title="명사">명사</a></p>
		 * <p class="...
		 * <ul>																									2nd mark
		 * <li><b>1.</b> <a href="..." title="하양">하양</a>, <a href="..." title="흰색">흰색</a>.</li>			3rd mark, parse for multiple defs which all use the same grammer found in the parsed saved line above
		 * </ul>
		 * 
		 * The
		 *
		 * The resulting grammar hash should be:
		 * 
		 * adjective - 하얗다
		 * adjective - 희다
		 * noun - 하양
		 * noun - 흰색
		 *  
	     * @param input_line
	     */
	    private void parseWikiEnglishToKoreanLine(String input_line)
	    {
	    	if (second_mark)
	    	{
	    		if (input_line.contains("<li><b>"))
	    		{
	    			parseForKoreanDefinitions(input_line);
	    			//log.add("reset marks -------------- ");
	    			first_mark = false;
	    			second_mark = false;
	    			grammar_name = "";
	    		} else
	    		{
	    			log.add("second markfailure: "+grammar_name);
	    		}
	    	} else if (first_mark)
	    	{
	    		if (input_line.contains("<ul>"))
	    		{
	    			second_mark = true;
	    			//log.add("second_mark = true "+input_line);
	    		} else
	    		{
	    			// continue to wait
	    			//first_mark = false;
	    			//log.add("first_mark set, skip 2nd "+input_line);
	    		}
	    	} else if (input_line.contains("<p title=")||input_line.contains("<p id=\"subheadline\" title="))
	    	{
	    		//log.add("first_mark_test "+first_mark_test+" input_line: "+input_line);
	    		first_mark = true;
	    		grammar_name = input_line; // save for grammar parsing if this turns out to be a title for a translation.
	    		//log.add("first_mark = true "+input_line);
	    	}
	    	//log.add("no muark "+input_line);
	    }
	    
	    /**
	     * parse for text in the <a href="..." title="시키다">시키다</a> tag
      	 * First get a the tag.  Then the contents.
      	 * A sample line like this for 'white' can have multiple definitions:
      	 * <li><b>1.</b> <a href="..." title="하얗다">하얗다</a>, <a href="..." title="희다">희다</a>.</li>
      	 * Use a while loop until there is no occurance of -1
	     * @param input_line
	     */
	    private void parseForKoreanDefinitions(String input_line)
	    {
	    	//log.add("parseForKoreanDefinitions "+input_line);
	    	//log.add("grammar_line "+grammar_name);
	    	while(input_line.indexOf("<a ")!=-1)
	    	{
	    		int a_start = input_line.indexOf("<a ");
	    		int a_end = input_line.indexOf("</a>");
	    		int a_content_start = input_line.indexOf(">", a_start);
	    		String line = "";
	    		try
	    		{
	    			line = input_line.substring(a_content_start+1, a_end);
	    			results.add(line);
	    			//String grammar = getWikiGrammarType(line); // operates on grammar_name global String
	    			String grammar = "";
	    			getWikiKoreanGrammarType(line); // operates on grammar_name global String
	    			grammar = grammar_name;
	    			//String grammar = getWikiKoreanGrammarType();
	    			if (!grammar.equals(EnglishGrammar.undetermined))
	    			{
	    				//log.add("undetermined grammar "+grammar);
	    				examples.add(grammar);
	    			} else
	    			{
	    				//log.add("no grammar from input_line: "+input_line);
	    			}
	    		} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    		{
	    			//log.add("aioob from input_line: "+input_line);
	    		}
	    		input_line = input_line.substring(a_end+4, input_line.length());
	    	}
	    }
	    
	    /**
	     * Not sure how many definition types there are (we present two so far):
	     * 
	     * Some definitions look like this:
	     * <h3><span class="editsection">[<a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EB%8B%A4&amp;action=edit&amp;section=4" title="Edit section: Verb">edit</a>]</span> <span class="mw-headline" id="Verb">Verb</span></h3>
		 * <p><b><span class="Kore KO" lang="ko" xml:lang="ko">시키다</span></b> (sikida)&#160;<i>infinitive</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%EC%BC%9C&amp;action=edit&amp;redlink=1" class="new" title="시켜 (page does not exist)">시켜</a></span>&#160;<i>or</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EC%96%B4&amp;action=edit&amp;redlink=1" class="new" title="시키어 (page does not exist)">시키어</a></span>, <i>sequential</i> <span class="Kore KO" lang="ko" xml:lang="ko"><a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EB%8B%88&amp;action=edit&amp;redlink=1" class="new" title="시키니 (page does not exist)">시키니</a></span></p>
		 * <ol>
		 * <li>to <a href="/wiki/cause" title="cause">cause</a> to do or happen</li>
		 * 
		 * first_mark starts with "<h3>..."  save text in id="Verb" for latter grab.
		 * 
	     * second line contains prununciation guid (shikida), correlations, conjurgations? and such.  Parse and potentialy save.
<p>
	<b>
		<span class="Kore KO" lang="ko" xml:lang="ko">시키다</span>
        </b> 
        (sikida)&#160
	<i>infinitive</i>
	<span class="Kore KO" lang="ko" xml:lang="ko">
		<a href="/w/index.php?title=%EC%8B%9C%EC%BC%9C&amp;action=edit&amp;redlink=1" class="new" title="시켜 (page does not exist)">
			시켜
		</a>
	</span>
		&#160;<i>or</i> 
	<span class="Kore KO" lang="ko" xml:lang="ko">
		<a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EC%96%B4&amp;action=edit&amp;redlink=1" class="new" 
			title="시키어 (page does not exist)">
			시키어
		</a>
	</span>, 
	<i>sequential</i> 
	<span class="Kore KO" lang="ko" xml:lang="ko">
		<a href="/w/index.php?title=%EC%8B%9C%ED%82%A4%EB%8B%88&amp;action=edit&amp;redlink=1" class="new" title="시키니 (page does not exist)">
			시키니
		</a>
	</span>
</p>
	     *
	     * third_mark is "<ol>".  Parse next line for main definitions.
	     * 
	     * fourth line "<li>to <a href="/wiki/cause" title="cause">cause</a> to do or happen</li>
	     * Main definition in the <a href=...>definition</a>
	     * More complete definition adds <li>"first part of " + definition+ after </a>" to the end"</li>
	     * 
	     * Other definitions look like this:
	     * <h4><span class="editsection">[<a href="/w/index.php?title=%EA%BD%A4&amp;action=edit&amp;section=7" title="Edit section: Adverb">edit</a>]</span> <span class="mw-headline" id="Adverb">Adverb</span></h4>
		 * <p><b>꽤</b> (kkwae)</p>
		 * <ol>
		 * <li>To a more than ordinary degree; <a href="/wiki/rather" title="rather">rather</a>, <a href="/wiki/fairly" title="fairly">fairly</a>.</li>
		 * </ol>
		 * 
		 * first_mark = <h4> capture grammar, and jump to third_mark
		 * w_2nd_mark = <ol>
		 * w_3rd_mark = <li> capture definitions
	     */
	    private void parseWikiKoreanToEnglishLine(String input_line, String text)
	    {
	    	String not_found = "한국어 위키낱말사전에는 ";
	    	if (input_line.contains(not_found))
	    	{
	    		results.add(not_found+text+" 제목을 가진 낱말이 현재 수록되어 있지 않습니다.");
	    	}
	    	if (third_mark)
	    	{
	    		// pasre for definition
	    		parseKtEDefinition(input_line);
	    		resetMarks();
	    	} else if (second_mark)
	    	{
	    		if (input_line.equals("<ol>"))
	    		{
	    			third_mark = true;
	    			//log.add("second_mark = true "+input_line);
	    		} else
	    		{
	    			resetMarks();
	    			//log.add("second_mark cancelled "+input_line);
	    		}
	    	} else if (first_mark)
	    	{
	    		if (input_line.startsWith("<p><b><span"))
	    		{
	    				second_mark = true;
	    				extra_info = input_line; // save for grammar parsing if this turns out to be a title for a translation.
	    				//log.add("first_mark = true "+input_line);
	    		} else
	    		{
	    			resetMarks();
	    		}
	    	} else if (input_line.startsWith("<h3>"))
	    	{
	    		// first format begin
	    		grammar_name = input_line; // capture potential grammar indicator
	    		first_mark = true;
	    		//log.add("first_mark "+input_line);
	    	} else if (input_line.startsWith("<h4>"))
	    	{
	    		// second format begin
	    		grammar_name = input_line; // capture potential grammar indicator
	    		first_mark = true;
	    		second_mark = true;
	    	}
	    }
	    
	    /**
	     * Routine to clear flags if the pattern doesnt match in some way.
	     */
	    private void resetMarks()
	    {
	    	first_mark = false;
	    	second_mark = false;
	    	third_mark = false;
	    	grammar_name = "";
	    	extra_info = "";
	    }


	    	
	    /**
	     * Parse: <li>to <a href="/wiki/cause" title="cause">cause</a> to do or happen</li>
	     * Return primary definition and secodndary one:
	     * cause
	     * to cause to do or happen
	     * @param line
	     */
	    private void parseKtEDefinition(String line)
	    {
	    	// first get primary definition
	    	// remove li tags
	    	int start = line.indexOf("<li>");
	    	String new_line = line.substring(start+4,line.length()-5);
	    	String main_definition = getTagContents(new_line);
	    	log.add("main_definition "+main_definition);
	    	if (main_definition.equals("aioobe"))
	    	{
	    		log.add("WebWord.parseKtEDefinition: main_definition aioobe");
	    	} else
	    	{
	    		results.add(main_definition);
	    		String full_definition = removeTags(line, "<", ">");
	    		results.add(full_definition);
	    		String grammar = getWikiGrammarType();
	    		grammar_name = grammar;
	    		examples.add(grammar);
	    	}
	    	
	    }
	    
	    /**
	     * Search for grammar definitions.  Korean to English definitions have more complex formats.
	     * Like this:
	     * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
		 * for grammar names in EnglishGrammar labels.
		 * Grammar is '...title="transitive verb"...'
		 *
		 * or:
		 * 
		 *  <h4><span class="editsection">[<a href="/w/index.php?title=%EA%BD%A4&amp;action=edit&amp;section=7" title="Edit section: Adverb">edit</a>]</span> <span class="mw-headline" id="Adverb">Adverb</span></h4>
		 *  Grammar is '...id="Adverb">Adverb<...'
		 *   How to tell the difference between transitive verb and transitive???
		 * 
	     * @return EnglishGrammar.undetermined if no grammar match is found otherwise returns the first definition that matches a grammar type in the list.
	     */
	    private String getWikiGrammarType()
	    {
	    	String potential = grammar_name;
	    	log.add("WebWord.getWikiGrammarType: potential "+potential);
	    	Vector grammar_labels = EnglishGrammar.getAllGrammarKeys();
	    	Vector candidates = new Vector();
	    	int i = 0;
	    	int size = grammar_labels.size();
	    	while (i<size)
	    	{
	    		String grammar_label = (String)grammar_labels.get(i);
	    		String grammar_label_nocase = grammar_label.toLowerCase();
	    		String potential_check = potential.toLowerCase();
	    		//log.add("check "+grammar_label);
	    		if (potential_check.contains(grammar_label_nocase))
	    		{
	    			/* At this point there could be multiple candidates that have matched,
	    		     * such as transitive, and transitive verb.  To distinguish which one it is, we must capture the
	    		     * text in the title="transitive verb" part of the anchor tag.*/
	    			int title_start = potential.indexOf("title=");
	    			int title_end = potential.indexOf("\"", title_start+7);
	    			String actual_title = potential.substring(title_start+7, title_end);
	    			if (actual_title.contains("%"))
	    			{
	    				//second grammar type format
	    				title_start = potential.indexOf("id=");
		    			title_end = potential.indexOf("\"", title_start+4);
		    			actual_title = potential.substring(title_start+4, title_end);
	    			}
	    			log.add("found "+actual_title);
	    			translations_and_grammar_types.put(grammar_label, actual_title);
	    			potential = (String)grammar_labels.get(i);
	    			candidates.add(grammar_label);
	    		}
	    		i++;
	    	}
	    	if (candidates.size()>=0)
	    	{
	    		try
	    		{
	    			return (String)candidates.get(0);
	    		} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
	    		{
	    			log.add("aioob: no candidates!");
	    		}
	    	}
	    	return EnglishGrammar.undetermined;
	    }
	    
	    /**
	     * getAllGrammarStrings
	     * Search for grammar definitions.  Korean to English definitions have more complex formats.
	     * Like this:
	     * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
		 * for grammar names in EnglishGrammar labels.
		 * Grammar is '...title="transitive verb"...'
		 *
		 * or:
		 * 
		 *  <h4><span class="editsection">[<a href="/w/index.php?title=%EA%BD%A4&amp;action=edit&amp;section=7" title="Edit section: Adverb">edit</a>]</span> <span class="mw-headline" id="Adverb">Adverb</span></h4>
		 *  Grammar is '...id="Adverb">Adverb<...'
		 *   How to tell the difference between transitive verb and transitive???
		 *   
		 * or:
		 * 
		 *   <p title="동사" id="subheadline" style="font-weight: bold; background-color: #FFF;"><a href="/wiki/%EB%8F%84%EC%9B%80%EB%A7%90:%EB%82%B1%EB%A7%90%EC%9D%98_%EA%B0%88%EB%9E%98" title="도움말:낱말의 갈래">동사</a></p>
		 * 
	     * @return EnglishGrammar.undetermined if no grammar match is found otherwise returns the first definition that matches a grammar type in the list.
	     */
	    private String getWikiKoreanGrammarType(String translation)
	    {
	    	String potential = grammar_name;
	    	log.add("WebWord.getWikiGrammarType: potential "+potential);
	    	Vector grammar_labels = KoreanGrammar.getKoreanGrammarStrings();
	    	Vector candidates = new Vector();
	    	int i = 0;
	    	int size = grammar_labels.size();
	    	while (i<size)
	    	{
	    		String grammar_label = (String)grammar_labels.get(i);
	    		String grammar_label_nocase = grammar_label.toLowerCase();
	    		String potential_check = potential.toLowerCase();
	    		//log.add("check "+grammar_label);
	    		if (potential_check.contains(grammar_label_nocase))
	    		{
	    			/* At this point there could be multiple candidates that have matched,
	    		     * such as transitive, and transitive verb.  To distinguish which one it is, we must capture the
	    		     * text in the title="transitive verb" part of the anchor tag.*/
	    			int title_start = potential.indexOf("title=");
	    			int title_end = potential.indexOf("\"", title_start+7);
	    			String actual_title = potential.substring(title_start+7, title_end);
	    			if (actual_title.contains("%"))
	    			{
	    				//second grammar type format
	    				title_start = potential.indexOf("id=");
		    			title_end = potential.indexOf("\"", title_start+4);
		    			actual_title = potential.substring(title_start+4, title_end);
	    			}
	    			log.add("found "+actual_title);
	    			translations_and_grammar_types.put(translation, actual_title);
	    			potential = (String)grammar_labels.get(i);
	    			candidates.add(grammar_label);
	    		}
	    		i++;
	    	}
	    	if (candidates.size()>=0)
	    	{
	    		try
	    		{
	    			return (String)candidates.get(0);
	    		} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
	    		{
	    			log.add("aioob: no candidates!");
	    		}
	    	}
	    	return KoreanGrammar.undetermined;
	    }
	    
	    /**
	     * This will remove anything in between the start and end tags.
	     * @param line to parse
	     * @param begin bracket, like <, [, {
	     * @param end bracket like >, ], }
	     * @return
	     */
	    public String removeTags(String line, String begin, String end)
	    {
	    	log.add("WebWord.removeTags ");
	    	//int i = 0;
	    	String new_line = line;
	    	while (new_line.contains(begin)&&new_line.contains(end))
	    	{
	    		int open = new_line.indexOf(begin, 0);
	    		int clos = new_line.indexOf(end, open);
	    		String start = new_line;
	    		String end_o = "";
	    		if (open == -1)
	    		{
	    			break;
	    		} else if (open==0)
	    		{
	    			start = "";
	    		} else
	    		{
	    			start = new_line.substring(0, open);
	    		}
	    		if (clos == -1)
	    		{
	    			break;
	    		} else if (clos == new_line.length())
	    		{
	    			end_o = "";
	    		} else
	    		{
	    			end_o = new_line.substring(clos+1, new_line.length());
	    		}
	    		new_line = start+end_o;
	    	}
	    	log.add("WebWord.removeTags: "+begin+end+" got: "+new_line+" --- from: "+line);
	    	return new_line;
	    }
	    
	    /**
	     * Get a string like this:
	     * to <a href="/wiki/cause" title="cause">cause</a> to do or happen
	     * and return this:
	     * cause
	     * java.lang.StringIndexOutOfBoundsException: String index out of range: -39
	     * @param line to parse.
	     * @return result.
	     */
	    private String getTagContents(String line)
	    {
	    	String tag_contents = "";
	    	int start = 0;
	    	if (line.contains(">"))
	    	{
	    		start = line.indexOf(">");
	    		try
	    		{
	    			tag_contents = line.substring(start+1, line.length());
	    		} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    		{
	    			log.add("WebWord.getTagContents: sioobe at "+start+" line= "+line);
	    			//printString(line);
	    			return "sioobe";
	    		}
	    	} else
	    	{
	    		tag_contents = line;
	    	}
	    	start = line.indexOf("<");
	    	String contents = tag_contents.substring(0, start+2);
	    	log.add("WebWord.getTagContents: got "+contents+" from "+tag_contents+" from "+line);
	    	return contents;
	    }
	    
	    private void printString(String line)
	    {
	    	int i = 0;
	    	while (i<line.length())
	    	{
	    		log.add(i+" "+line.charAt(i));
	    		i++;
	    	}
	    }
	    
	    /**
	     * Search
	     * <p class="BGImage_c" style="padding-left: 6px;"><b>타동사</b>&#160;(<a href="/wiki/transitive_verb" title="transitive verb"><span style="font-family:Times"><b>vt</b></span></a>)&#160;</p>
		 * for grammar names in EnglishGrammar labels.
		 * 
		 * How to tell the difference between transitive verb and transitive???
		 * 
	     * @return EnglishGrammar.undetermined if no grammar match is found otherwise returns the first definition that matches a grammar type in the list.
	     */
	    private String getWikiGrammarType(String line)
	    {
	    	String potential = grammar_name;
	    	Vector english_grammar_labels = EnglishGrammar.getAllGrammarKeys();
	    	Vector grammar_labels = KoreanGrammar.getAllGrammarKeys();
	    	Vector candidates = new Vector();
	    	int i = 0;
	    	int size = grammar_labels.size();
	    	//System.err.println("grammar size "+size);
	    	while (i<size)
	    	{
	    		if (potential.contains((String)grammar_labels.get(i)))
	    		{
	    			/* At this point there could be multiple candidates that have matched,
	    		     * such as transitive, and transitive verb.  To distinguish which one it is, we must capture the
	    		     * text in the title="transitive verb" part of the anchor tag.*/
	    			int title_start = potential.indexOf("title=");
	    			int title_end = potential.indexOf("\"", title_start+7);
	    			String actual_title = potential.substring(title_start+7, title_end);
	    			translations_and_grammar_types.put(line, actual_title);
	    			//System.err.println(i+" actual title "+actual_title);
	    			potential = (String)grammar_labels.get(i);
	    			candidates.add(potential);
	    		}
	    		i++;
	    	}
	    	//System.err.println("candidates.size() "+candidates.size());
	    	if (candidates.size()>0)
	    	{
	    		// find the longest grammar name to make sure 'transitive verb' doesn't return 'verb'.
	    		String number_one = "";
	    		int j = 0;
	    		while (j<candidates.size())
	    		{
	    			String is_it = (String)candidates.get(j);
	    			if (is_it.length()>number_one.length())
	    			{
	    				number_one = is_it;
	    			}
	    			potential = number_one;
	    			j++;
	    		}
	    	} else
	    	{
	    		try
	    		{
	    			return (String)candidates.get(0);
	    		} catch (java.lang.ArrayIndexOutOfBoundsException aioob)
	    		{
	    			log.add("aioob: no candidates!");
	    			return EnglishGrammar.undetermined;
	    		}
	    	}
	    	return potential;
	    }
	    
	    public Hashtable getTranslationsAandGrammarTypes()
	    {
	    	return translations_and_grammar_types;
	    }

	    /**
	     * At this point in the search for a grammar type, there are multiple candidates that have matched,
	     * such as transitive, and transitive verb.  To distinguish which one it is, we must capture the
	     * text in the title="transitive verb" part of the anchor tag, which has been saved in the grammar_name
	     * global variable, and then determine which candidate it is.
	     * @param candidates
	     * @param grammar_labels
	     */
	    private void resolveGrammarType(Vector candidates, Vector grammar_labels)
	    {
	    	
			
	    	
	    }
	    
	    // ------------------- end wiki parse
	    
	    /**
	     * Load an html page using address+"search.cgi?kwd="+search_text+trailer format
	     * and put each line in a Vector.
	     */
	    public Vector testImpactDictionary(Vector test, String text, String _encoding)
	    { 
	    	log.add("File size "+test.size());
	    	encoding = _encoding;
	    	setupForParse();
	    	int i = 0;
	    	int size = test.size();
	    	while (i<size)
	    	{
	    		String input_line = (String)test.get(i);
	    		if (language_profile.equals(JuksongConstants.EKR))
    			{
    				parseImpactDefinitionLine(input_line);
    			} else
    			{
    				parseImpactLine(input_line);
    			}
    			if (return_results==true)
    			{
    				return results;
    			}
	    		if (return_results==true)
	    		{
	    			return results;
	    		}
	    		i++;
	    	}
	    	return results;
	    }
	    
	    /**
	     * Load an html page using address+"search.cgi?kwd="+search_text+trailer format
	     * and put each line in a Vector.
	     */
	    public Vector testImpactDictionary(String text, String _encoding)
	    { 
	    	encoding = _encoding;
	    	setupForParse();
	    	String trailer = "&bool=and&word=yes";
	    	String search_text = "";
	    	Vector test = testWritingParse();
	    	int i = 0;
	    	int size = test.size();
	    	while (i<size)
	    	{
	    		String input_line = (String)test.get(i);
	    		//String input_line = parseImpactLine(EncodeString.encodeThis((String)test.get(i), encoding));
	    		if (language_profile.equals(JuksongConstants.EKR))
    			{
    				parseImpactDefinitionLine(input_line);
    			} else
    			{
    				parseImpactLine(input_line);
    			}
	    		if (return_results==true)
	    		{
	    			return results;
	    		}
	    		i++;
	    	}
	    	return results;
	    }
	    
	    /**
	     * The search text for dic.impact.pe.k must use the euc-kr encoding.
	     * There are two types of definitions we want to capture and one we want to exclude:
	     * 
	     * Format #1
	     * The first format we are trying to parse has definitions like this:
	     * <HR>
		 * <script language="JavaScript"> 
		 * <!-- ... //--> </script> 
		 * <BR>berry 
		 * <FONT SIZE=-1 COLOR=557756>£Û£â¢¥£å£ò£é£Ý</FONT>
 		 * ¿­¸Å, µþ±âÀÇ ¿­¸Å, Ä¿ÇÇÀÇ ¿­¸Å, ¹°°í±âÀÇ ¾Ë-¿­¸Å°¡ 
		 * <FONT COLOR=8080FF>¿­¸®</FONT>
		 * ´Ù, ¿­¸Å¸¦ µû´Ù
		 * 
		 * berry ［ｂ´ｅｒｉ］ 열매, 딸기의 열매, 커피의 열매, 물고기의 알-열매가 열리다, 열매를 따다 
		 * 
		 * The script element appears only on the first definition.
		 * The COLOR=557756 is the optional [pronunciation] guide.
		 * The COLOR=8080FF is the blue colored search text in the example sentences.
		 * 
		 * In this case, we searched for the Korean verb in infinitive form 'to open'
		 * The definition we were looking for was 15th in a list of hundreds of words and full paragraphs.
		 * 
		 * the correct definition looked like this (we added the <cr>s):
		 * open 열리다, 넓어지다, 시작하다, 보이게 되다, 
		 * ~ into ...로 통하다, 
		 * ~ on ...에 면하다, 
		 * ~ one's eyes 깜짝 놀라다, 
		 * ~ out 열다, 
		 * ~ the door to ...에 기회를 주다, 
		 * ~ up 열다, 
		 * ~ly ad, 솔직히, 공공연히 
		 * 
		 * The html for this was:
		 * <HR>
		 * <BR>open  
		 * <FONT COLOR=8080FF>¿­¸®</FONT> ´Ù, ³Ð¾îÁö´Ù, ½ÃÀÛÇÏ´Ù, º¸ÀÌ°Ô µÇ´Ù, 
		 * ~ into ...·Î ÅëÇÏ´Ù, 
		 * ~ on ...¿¡ ¸éÇÏ´Ù, 
		 * ~ one's eyes ±ôÂ¦ ³î¶ó´Ù, 
		 * ~ out ¿­´Ù, 
		 * ~ the door to ...¿¡ ±âÈ¸¸¦ ÁÖ´Ù, 
		 * ~ up ¿­´Ù, 
		 * ~ly ad, ¼ÖÁ÷È÷, °ø°ø¿¬È÷
		 *
		 * So to do a spell check, we need to search for all parts of the definition,
		 * allowing options to be set for exclusions such as 'to <verb form>'
		 * and also collect all these other examples as definitions (blue font included)
		 * with optional pronunciations guides and grammatical variations shown in the correct definition above.
	     *
	     * Format #2
	     * This is the 2nd format we are looking for:
	     * 200 <HR>
		 * 201 
		 * 202 <BR>
		 * 203 untapped ¸¶°³°¡ <FONT COLOR=8080FF>¿­¸®</FONT>Ao ¾EAº, ≫c¿eμCAo ¾EAº, ¹I°³¹ßAC
		 * 204 <HR>
		 *
		 * Or possibly with no HR:
		 *
		 * 114 <BR>
		 * 115 berry <FONT SIZE=-1 COLOR=557756>£Û£â¢¥£å£ò£é£Ý</FONT> ¿­¸Å, µþ±âÀÇ ¿­¸Å, Ä¿ÇÇÀÇ ¿­¸Å, ¹°°í±âÀÇ ¾Ë-¿­¸Å°¡ <FONT COLOR=8080FF>¿­¸®</FONT>´Ù, ¿­¸Å¸¦ µû´Ù
		 * 116 <HR>
		 * From this log output, it looks like the definition is coming after a br tag.
		 *
		 * Format #3 (Exclude from definition results, but capture in separate examples Vector)
		 * We want to exclude multiple line text examples from definition results which have a format like this:
		 * <HR>
		 * <BR>
		 * I have two tickets to a classical music concert this Saturday. 
		 * <BR>
		 * 
		 * Will you go there with me? 
		 * <BR>
		 * 이번 주 토요일에 <FONT COLOR=8080FF>열리</FONT>는 고전 음악회 표 두 장이 있어. 같이 갈래?
		 * <HR>

		 * <BR>
		 * Knock at the door and it will be open.
		 * <BR>
		 *  두드려라 그러면 <FONT COLOR=8080FF>열리</FONT>리라.
		 * <HR>
		 * 
		 * <BR>
		 * 
		 * A golden key opens every door.
		 * <BR>
		 * ⇒ Money makes the mare go.
		 * <BR>
		 * ⇒ Money talks.
		 * <BR>
		 *  돈으로 안 <FONT COLOR=8080FF>열리</FONT>는 문이 없다.
		 * <HR>
		 * 
		 * Notice there are multiple <BR> tags for this type.
		 * Therefore, we have a bracket_counter, which if it goes over 1 before another <HR>,
		 * We can exclude the definition.
		 * However, we wont know this until after the second <BR>, which if we find id, we want to
		 * delete the last added definition, and then return the Vector,
		 * As these text examples always come at the end of the dictionary style entries.
		 * 
		 * 
		 * All these types of parsing are demonstrated in the testParse() method.
		 *
		 *The 'if else if' blocks work backwards.
		 *For instance, the when the first horizontal rule is found, the marker is set in the last else if block.
		 *
		 *
	     * @param text
	     * @param encoding
	     * @return Vector of definitions from the passed in text.
	     */
	    public void parseImpactLine(String line)
	    {
	    	if (example_marker)
	    	{
	    		if (line.contains("<HR>"))
	    		{
	    			example_marker = false;
	    			bracket_marker = false;
		    		bracket_marker_2 = false;
		    		horizontal_rule_marker = true;
	    		} else if (!line.contains("<BR>"))
	    		{
	    			// capture line
	    			if (remove_font_tags)
	    			{
	    				line = removeBrackets(line, "<", ">");
	    			}
	    			if (!line.isEmpty())
	    			{
	    				//examples.add(EncodeString.encodeThis(line, encoding));
	    				examples.add(line);
	    				//log.add("added examples "+line);
	    			}
	    		}
	    	} else if (bracket_counter>1)
	    	{
	    		example_marker = true;
	    		int last_index = results.size() - 1;
	    		try
	    		{
	    			if(horizontal_rule_marker)
	    			{
	    				String first_line = (String)results.remove(last_index);
	    				if (remove_font_tags)
		    			{
	    					first_line = removeBrackets(first_line, "<", ">");
		    			}
	    				//examples.add(EncodeString.encodeThis(first_line, encoding));
	    				examples.add(first_line);
	    				//log.add("added examples first_line "+first_line);
	    			} else
	    			{
	    				String trim = trimUnwanted(line);	//  Format #2
	    	    		results.add(trim);
	    	    		log.add("added definitions rule 1 in rule 3 "+line);
	    	    		horizontal_rule_marker = true;
	    			}
	    			log.add("examples marker set "+line);
	    		} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
	    		{
	    			// obviously there are no results yet!
	    			log.add("aioobe: obviously there are no results yet! "+line);
	    		}
	    		// return_results = true;
	    	} else if (bracket_marker_2)
	    	{
	    		String trim = trimUnwanted(line);	//  Format #2
	    		results.add(trim);
	    		//bracket_marker = false;
	    		bracket_marker_2 = false;
	    		horizontal_rule_marker = false;
	    		log.add("----- format 2 bc "+bracket_counter+" trimed: "+trim+" from: "+line);
	    	} else if (line.equals("<BR>") && horizontal_rule_marker==true)
	    	{
	    		bracket_marker_2 = true; 		//  setup for Format #2
	    		bracket_counter++;
	    		log.add("----- setup format 2 bc "+bracket_counter+" "+line);
	    	} else if (horizontal_rule_marker==true && line.contains("<BR>"))
	    	{
	    		String trim = trimUnwanted(line); //  Format #1
	    		results.add(trim);
	    		boolean is = trim.contains("<");
	    		bracket_marker = false;
	    		horizontal_rule_marker = false;
	    		log.add("added format 1 "+trim+" "+line);
	    	} else if (line.contains("<HR>") && first_horizontal_rule_marker==true)
	    	{
	    		horizontal_rule_marker = true; // format one starts after a hr 
	    		//log.add("bracket counter rest from "+bracket_counter+" to 0 "+" "+line);
	    		bracket_counter = 0;
	    	} else if (line.contains("<HR>") && first_horizontal_rule_marker==false)
	    	{
	    		first_horizontal_rule_marker = true; // the part before this has no definitions 
	    	} else if (line.contains("<BR>") && first_horizontal_rule_marker==true)
	    	{
	    		bracket_counter++;
	    	}
	    }
	    
	    /**
	     * This method only looks up words for English speaking Korean Learners Reading texts
	     * @param line
	     */
	    public void parseImpactDefinitionLine(String line)
	    {
	    	if (bracket_counter>2)
	    	{
	    		example_marker = true;
	    		int last_index = results.size() - 1;
	    		try
	    		{
	    			return_results = true;
	    			String first_line = (String)results.remove(last_index);
	    			log.add("removed "+first_line);
	    			//last_index = results.size();
	    			//first_line = (String)results.remove(last_index);
	    			//log.add("removed "+first_line);
	    		} catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
	    		{
	    			// obviously there are no results yet!
	    			log.add("WebWord.parseImpactDefinitionLine: aioobe - obviously there are no results yet! "+line);
	    		}
	    		// return_results = true;
	    	} else if (bracket_marker_2)
	    	{
	    		String trim = trimUnwanted(line);	//  Format #2
	    		results.add(trim);
	    		//bracket_marker = false;
	    		bracket_marker_2 = false;
	    		horizontal_rule_marker = false;
	    		log.add("----- format 2 bc "+bracket_counter+" trimed: "+trim+" from: "+line);
	    	} else if (line.equals("<BR>") && horizontal_rule_marker==true)
	    	{
	    		bracket_marker_2 = true; 		//  setup for Format #2
	    		bracket_counter++;
	    		log.add("----- setup format 2 bc "+bracket_counter+" "+line);
	    	} else if (horizontal_rule_marker==true && line.contains("<BR>"))
	    	{
	    		String trim = trimUnwanted(line); //  Format #1
	    		results.add(trim);
	    		boolean is = trim.contains("<");
	    		bracket_marker = false;
	    		horizontal_rule_marker = false;
	    		log.add("added format 1 "+trim+" "+line);
	    	} else if (line.contains("<HR>") && first_horizontal_rule_marker==true)
	    	{
	    		horizontal_rule_marker = true; // format one starts after a hr 
	    		log.add("bracket counter reset from "+bracket_counter+" to 0 "+" "+line);
	    		bracket_counter = 0;
	    	} else if (line.contains("<HR>") && first_horizontal_rule_marker==false)
	    	{
	    		first_horizontal_rule_marker = true; // the part before this has no definitions 
	    		log.add("1st horizontal marker set to true");
	    	} else if (line.contains("<BR>") && first_horizontal_rule_marker==true)
	    	{
	    		bracket_counter++;
	    		log.add("bracket counter incremented");
	    	}
	    }
	    
	    /**
	     * If the language being learned is English,
	     * then the text is in English, and the definition
	     * should be in another language (like Korean)
	     * So for a reading test, we want to remove all the
	     * ascii characters from the definition, and for a
	     * writing test, we want to do the opposite.
	     * If a Korean student is learning English,
	     * a reading tests shows an English word that the
	     * student types in a Korean word to pass.
	     * In this situation, the student would want to see
	     * a list of similar answers from the web, such that
	     * a list of only English translations of the word
	     * should be shown.  So we should remove all characters
	     * except English text from the result.
	     * 
	     */
	    public String trimUnwanted(String line)
	    {
	    	String result = "";
	    	if (language_profile.equals(JuksongConstants.EKR) || language_profile.equals(JuksongConstants.KER))
	    	{
	    		log.add(JuksongConstants.printProfile(language_profile)+" trim non ascii");
	    		 result = trimNonAscii(line);
	    	} else if (language_profile.equals(JuksongConstants.EKW) || language_profile.equals(JuksongConstants.KER))
	    	{
	    		log.add(JuksongConstants.printProfile(language_profile)+" trim ascii");
	    		result = trimAscii(line);
	    	}
	    	return result;
	    }
	    
	    /**
	     * Keep moving the end_mark closer if non-askii characters are found,
	     * then return the string from the beginning to the mark.
	     * Then call trimTrailingNonLetters to remove non-letter endings.
	     * @param definition
	     * @return
	     */
	    public String trimNonAscii(String definition)
	    {
	    	String new_definition = removeBrackets(definition, "［", "］"); // remove pronunciation guide first
	    	new_definition = removeBrackets(new_definition, "[", "]");
	    	new_definition = removeBrackets(new_definition, "<", ">");
	    	int length = new_definition.length();
	    	int end_mark = length;
	    	int i = length;
	    	while (i>0)
	    	{
	    		try
	    		{
	    			char c = new_definition.charAt(i);
	    			int num_val = Character.getNumericValue(c);
	    			if ( c >= 0 && c <= 127 )
	    			{
	    				// ascii cahracter
	    			} else
	    			{
	    				end_mark = i;
	    			}
	    		} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    		{
	    			// do nothing???
	    		}
				i--;
	    	}
	    	try
	    	{
	    		new_definition = new_definition.substring(0, end_mark);
	    	} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    	{
	    		log.add("WebWord.trimNonAscii: sioobe: end_mark "+end_mark);
	    	}
	    	new_definition = trimTrailingNonLetters(new_definition);
	    	log.add("WebWord.trimAscii: got "+new_definition+" from "+definition);
	    	return new_definition;
	    }
	    
	    /**
	     * Based on trimNonAscii but instead excludes all ascii characters.
	     * 
	     * We need to take a line like this:
	     * <FONT COLOR=8080FF>withdrawn</FONT>  내성적인, 인적이 드문
	     * And produce one like this:
	     * 내성적인, 인적이 드문
	     * 
	     * We had a failing testTrimmAscii test because of the EncodeString.encodeThis() statement.
	     * @param definition
	     * @return
	     */
	    public String trimAscii(String definition)
	    {
	    	String new_definition = removeBrackets(definition, "［", "］"); // remove pronunciation guide first
	    	new_definition = removeBrackets(new_definition, "<", ">");
	    	new_definition = removeBrackets(new_definition, "[", "]");
	    	StringBuffer new_line = new StringBuffer();
	    	buf_log = new StringBuffer();
	    	int length = new_definition.length();
	    	int i = 0;
	    	while (i<length)
	    	{
	    		try
	    		{
	    			char c = new_definition.charAt(i);
	    			int num_val = Character.getNumericValue(c);
	    			Character ch =	Character.valueOf(c); 
	    			char c1 = ch.charValue(); 
	    			String s = new_definition.substring(i,i+1);
	    			if ( num_val == -1 )
	    			{
	    				if (!checkForSpecialCharacters(s))
	    				{
	    					new_line.append(c1);
	    				}
	    			} else
	    			{
	    				buf_log.append(c1);
	    			}
	    		} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    		{
	    			// do nothing???
	    		}
				i++;
	    	}
	    	new_definition = new String(new_line);
	    	String excluded = new String(buf_log); buf_log = new StringBuffer();
	    	//new_definition = EncodeString.encodeThis(trimTrailingNonLetters(new_definition), encoding);
	    	new_definition = trimTrailingNonLetters(new_definition);
	    	log.add("WebWord.trimAscii: got "+new_definition+" from "+definition+" --- ex "+excluded);
	    	log.add(" excluded: "+excluded+" new_line: "+new_line);
	    	return new_definition;
	    }
	    
	    public boolean checkForSpecialCharacters(String s)
	    {
	    	String es = Transformer.getByteString(s);
	    	try
	    	{
	    		String sub = s.substring(0,3);
	    		log.add(sub);
	    		if (sub.equals("-17"))
	    		{
	    			return true;
	    		}
	    		Vector special_chars = getSpecailCharacters();
	    		if (special_chars.contains(es))
	    		{
	    			return true;
	    		}
	    		return false;
	    	} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    	{
	    		return false;
	    	}
	    }
	    
	    private Vector getSpecailCharacters()
	    {
	    	String single_quote = "-62-76"; //-1 char ∂ num_val -1 es 
	    	String upside_down_e = "-30-120-126";//-1 char ｀ num_val -1 es 
	    	Vector special_chars = new Vector();//special_chars.add(open_br);
	    	special_chars.add(single_quote);
	    	special_chars.add(upside_down_e);
	    	return special_chars;
	    }
	    
	    public String trimTrailingNonLetters(String definition)
	    {
	    	String new_definition = definition;
	    	try
	    	{
	    		int last = new_definition.length();
	    		new_definition = definition.trim();
	    		Character c = new_definition.charAt(last-1);
	    		while (!c.isLetter(c))
	    		{
	    			new_definition = new_definition.substring(0, last-1);
	    			last = new_definition.length();
	    			c = new_definition.charAt(last-1);
	    		}
	    		return new_definition;
	    	} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    	{
	    		log.add("WebWord.trimTrailingNonLetters: sioobe "+new_definition);
	    		log.add("----------------------------original : "+definition);
	    		return new_definition;
	    	}
	    	//return "";
	    }
	    
	    /**
	     * This will remove brackets from a string with open and close types passed in as args.
	     * @param line to parse
	     * @param begin bracket, like <, [, {
	     * @param end bracket like >, ], }
	     * @return
	     */
	    public String removeBrackets(String line, String begin, String end)
	    {
	    	//int i = 0;
	    	String new_line = line;
	    	while (new_line.contains(begin)&&new_line.contains(end))
	    	{
	    		int open = new_line.indexOf(begin, 0);
	    		int clos = new_line.indexOf(end, open);
	    		String start = new_line;
	    		String end_o = "";
	    		if (open == -1)
	    		{
	    			break;
	    		} else if (open==0)
	    		{
	    			start = "";
	    		} else
	    		{
	    			start = new_line.substring(0, open);
	    		}
	    		if (clos == -1)
	    		{
	    			break;
	    		} else if (clos == new_line.length())
	    		{
	    			end_o = "";
	    		} else
	    		{
	    			end_o = new_line.substring(clos+1, new_line.length());
	    		}
	    		new_line = start+end_o;
	    		//i++;
	    		//if (i>new_line.length())
	    		//if (i>5)
	    		//{
	    			//break;
	    		//}
	    	}
	    	log.add("WebWord.removeBrackets: "+begin+end+" got: "+new_line+" --- from: "+line);
	    	return new_line;
	    }
	    
	    
	    /**
	     * Add characters up until the frist non-letter.
	     * 
	     try
	    			{
	    				char c = line.charAt(line.length()-1); // cases error???
		    			if (String.valueOf(c).equals(">"))
		    			{
		    				bracket_marker = true;
		    				log.add("WebWord.bracket marker set to true"+line);
		    			} else
		    			{
		    				// reset all markers???
		    				bracket_marker = false;
		    				horizontal_rule_marker = false;
		    				first_horizontal_rule_marker = false;
		    			}
	    			} catch (java.lang.StringIndexOutOfBoundsException sioobe)
	    			{
	    				log.add("sioobe at line "+i+" couldn't set braket marker"+line);
	    				String second_format = captureSecondFormat(line);
	    				if (second_format != null)
	    				{
	    					results.add(second_format);
    						log.add("WebWord.lookup: definition added by second form: "+second_format);
	    				}
	    			}
	     * @param line
	     * @return the numeric value of the character, as a nonnegative int  value; 
	     * -2 if the character has a numeric value that is not a nonnegative integer; 
	     * -1 if the character has no numeric value.
	     * This method cannot handle  supplementary characters. To support all Unicode characters, 
	     * including supplementary characters, use the getNumericValue(int) method. 
	     */
	    private String captureSecondFormat(String line)
	    {
	    	String second_capture = null;
	    	int j_size = line.length();
			int j = 0;
			while (j<j_size)
			{
				char c = line.charAt(j);
				int num_val = Character.getNumericValue(c);
				boolean letter = Character.isLetter(c);
				if (letter == false)
				{
					// definition = definition.substring(0 , j-1);
					
					int end_char = Character.getNumericValue(line.charAt(j));
					second_capture = new String(line.substring(0 , j));
					
					log.add("second capture. end char "+end_char+" 2nd cap "+second_capture+" line "+line);
				}
				j++;
			}
			int definition_start = 0;
			int definition_end = second_capture.indexOf("<", definition_start);
			if (definition_end!=-1)
			{
				second_capture = line.substring(definition_start, definition_end);
			}
			
			// this causes a sioobe at org.catechis.juksong.WebWord.trimNonAscii(Unknown Source)
			//second_capture = trimNonAscii(second_capture);
	    	return second_capture;
	    }
	       
	    public Vector getExamples()
	    {
	    	return examples;
	    }
	    
	    public Vector getDefinitions()
	    {
	    	return results;
	    }
	    
	    public boolean isTextVerified(String text)
	    {
	    	if (results.contains(text))
	    	{
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public boolean isDefinitionVerified(String definition)
	    {
	    	if (examples.contains(definition))
	    	{
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public void setRemoveFontTags(boolean _remove_font_tags)
	    {
	    	remove_font_tags = _remove_font_tags;
	    }
	    
	    public void setEncoding(String _encoding)
	    {
	    	this.encoding = _encoding;
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
				log.add(i+" "+v.get(i));
				i++;
			}
		}
		
		private String captureLineTest(String input_line)
		{
			return input_line;
		}
		
		
		/**
		 * Parse Ogdens 850 Basic English Words.
		 * Returns a hash with words as keys and categories as values.
		 * The first category on Wikitionary looks like this:
		 * 
		 * Category Title: Operations
		 * 
		 * <h2><span class="editsection">[<a href="/w/index.php?title=Appendix:Basic_English_word_list&amp;action=edit&amp;section=1" 
		 * title="Edit section: Operations - 100 words">edit</a>]</span> 
		 * <span class="mw-headline" id="Operations_-_100_words">Operations - 100 words</span></h2>
		 * 
		 * <p><a href="/wiki/come" title="come">come</a>, 
		 * <a href="/wiki/get" title="get">get</a>, 
		 * <a href="/wiki/give" title="give">give</a>, 
		 * <a href="/wiki/go" title="go">go</a>, 
		 * <a href="/wiki/keep" title="keep">keep</a>, 
		 * ...
		 * 
		 * 400 general words
		 * 
		 * Next is a category heading like this -> [edit] 400 general wordsA-F 
		 * <h2><span class="editsection">[<a href="/w/index.php?title=Appendix:Basic_English_word_list&amp;action=edit&amp;section=2" title="Edit section: 400 general words">edit</a>]</span> <span class="mw-headline" id="400_general_words">400 general words</span></h2>
		 * <dl>
		 * <dt><b>A-F</b></dt>
		 * </dl>
		 * <p><a href="/wiki/account" title="account">account</a>,
		 *  <a href="/wiki/act" title="act">act</a>, 
		 *  ...
		 *  
		 *  
		 * first mark '<h2><span' capture for category parse later
		 * 
		 * second_mark '<p><a href="/wiki/' need to parse the entire line for words in and attach the category to all words. 
		 * 	ends with .</p>
		 * 
		 * third mark goes thru a series of lines
		 * <dl>
		 * <dt><b>A-F</b></dt>
		 * </dl>
		 * before coming to the second mark again
		 * 
		 * The remain three categories are in the first format.
		 */
		public Hashtable getOgdensEnglish()
		{
			ogdens_english = new Hashtable();
			address = "http://en.wiktionary.org/wiki/Appendix:Basic_English_word_list";
		    setupForWikiParse("encoding not needed");
		    String input_line = "";
		    URL url = null;
		    try
		    {
		    	url = new URL(address);
		    	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    	while ((input_line = in.readLine()) != null)
		    	{
		    		parseOgdensEnglishLine(input_line);
		    	}
		    	in.close();
		    } catch (MalformedURLException efurle)
		    {
		    	String url_str = url.toString();
		    	if (url_str == null)
		    	{
		    		log.add("url is null");
		    	}
		    	log.add("MalformedURLException");
		    } catch (IOException ioe)
			{
		    	System.err.println("IOException 5");
			}
		    log.add("WebWord.parseWikiKoreanToEnglish: loaded "+url.toString());
			return ogdens_english;
		}
		
		/**
		* first mark '<h2><span' capture for category parse later
		 * 
		 * second_mark '<p><a href="/wiki/' need to parse the entire line for words in and attach the category to all words. 
		 * 	ends with .</p>
		 * 
		 * third mark goes thru a series of lines
		 * <dl>
		 * <dt><b>A-F</b></dt>
		 * </dl>
		 * before coming to the second mark again
		 * 
		 * The remain three categories are in the first format.
		 * */
		private void parseOgdensEnglishLine(String input_line)
	    {
			if (third_mark==true && input_line.startsWith("<p><a href="))
			{
				// parse entire line and add category label we already know.
	    		parseOgdensWords(input_line, "400 general words");
	    		log.add("third_mark capture "+input_line);
	    		if (input_line.contains("."))
	    		{
	    			setupForWikiParse("encoding not needed"); // reset flags for G - Z list
	    			log.add("reset all marks");
	    		} else
	    		{
	    			third_mark = false; // A - F or G - O lists.
	    			log.add("wait for thrid mark");
	    		}
			} else if (second_mark==true && input_line.contains("</dl>"))
	    	{
	    		third_mark = true;
	    		log.add("third_mark = true "+input_line);
	    	} else if (second_mark==true && input_line.contains("<dt>"))
	    	{
	    		// second line of format 2, do nothing
	    		log.add("seond_mark waiting "+input_line);
	    	} else if (first_mark==true && input_line.startsWith("<p><a href="))
	    	{
	    		// parse entire line and add category label
	    		String grammar_name = parseOgdensGrammarName();
	    		parseOgdensWords(input_line, grammar_name);
	    		setupForWikiParse("encoding not needed"); // reset flags
	    		log.add("capture "+input_line);
	    	} else if (first_mark==true && input_line.startsWith("<dl>"))
	    	{
	    		// second category format
	    		second_mark = true;
	    		first_mark = false; // so that on ly the second category fromat will be caught now.
	    		log.add("seond_mark = true "+input_line);
	    		
	    	} else if (input_line.startsWith("<h2><span"))
	    	{
	    		grammar_name = input_line; // capture potential grammar indicator
	    		first_mark = true;
	    		log.add("first_mark = true "+input_line);
	    	}
	    }
		
		/**
		 * Parse a line to get the grammar:
		 * ex:
		 * <h2><span class="editsection">[<a href="/w/index.php?title=Appendix:Basic_English_word_list&amp;action=edit&amp;section=1" 
		 * title="Edit section: Operations - 100 words">edit</a>]</span> 
		 * <span class="mw-headline" id="Operations_-_100_words">Operations - 100 words</span></h2>
		 * @return
		 * 
		 */
		private String parseOgdensGrammarName()
		{
			String grammar = "";
			int id_tag = grammar_name.indexOf("id=");
			int span_end = grammar_name.indexOf(">", id_tag);
			int content_end = grammar_name.indexOf("<", span_end+1);
			grammar = grammar_name.substring(span_end+1, content_end);
			log.add("got "+grammar+" from "+grammar_name);
			return grammar;
		}
		
		/**
		 * Parse a single string for multiple words like this:
		 * <p><a href="/wiki/come" title="come">come</a>, 
		 * <a href="/wiki/get" title="get">get</a>, 
		 * <a href="/wiki/give" title="give">give</a>, 
		 * <a href="/wiki/go" title="go">go</a>, 
		 * <a href="/wiki/keep" title="keep">keep</a>, 
		 * @param line
		 */
		private void parseOgdensWords(String line, String grammar_name)
		{
			while (line.length()>0)
			{
				int from_index = line.indexOf("<a href=");
				int a_tag_contents_start = line.indexOf(">", from_index);
				int a_tag_contents_end = line.indexOf("<", a_tag_contents_start+1);
				String a_tag_contents = line.substring(a_tag_contents_start+1, a_tag_contents_end);
				ogdens_english.put(a_tag_contents, grammar_name);
				line = line.substring(a_tag_contents_end, line.length());
				if (!line.contains("<a href"))
				{
					line = "";
				}
			}
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
		 * The two longer multiple line text examples should be excluded.
		 * @return
		 */
		private Vector testParse()
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
		    sample.add("<HR>						");		
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
		
		
		
		
		private Vector testImages()
		{
			
			Vector sample = new Vector();
			sample.add("<html><head><meta http-equiv=content-type content=\"text/html;charset=UTF-8\"><title>apple - Google Images</title><script>window.google={kEI:\"x12gSrrpJZ_4tgO20dmHDg\",kEXPI:\"17259,21717\",kCSIE:\"17259,21717\",kCSI:{e:\"17259,21717\",ei:\"x12gSrrpJZ_4tgO20dmHDg\"},kHL:\"en\"};");
			sample.add("dyn.setResults([[\"/imgres?imgurl\\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/apple-logo1.jpg\\x26imgrefurl\\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/technology/\\x26usg\\x3d__ZRYiHpjAeWfxq74V5CGJfAEt_Dk\\x3d\\x26h\\x3d480\\x26w\\x3d397\\x26sz\\x3d22\\x26hl\\x3den\\x26start\\x3d1\\x26sig2\\x3dgFdiLZSxNIXs3o1jiGaNnA\\x26um\\x3d1\",\"\",\"HhYIr-Oz_5sLkM:\",\"http://weblogs.baltimoresun.com/business/consuminginterests/blog/apple-logo1.jpg\",\"107\",\"129\",\"\\x3cb\\x3eApple\\x3c/b\\x3e,\",\"\",\"\",\"397 x 480 - 22k\",\"jpg\",\"weblogs.baltimoresun.com\",\"\",\"\",\"http://t0.gstatic.com/images\",\"1\",[],\"\",1],[\"/imgres?imgurl\\x3dhttp://www.waterfootprint.org/images/gallery/original/apple.jpg\\x26imgrefurl\\x3dhttp://www.waterfootprint.org/%3Fpage%3Dfiles/productgallery%26product%3Dapple\\x26usg\\x3d__OgCvDhaP2-gGffYIgyoi1LEKDd8\\x3d\\x26h\\x3d400\\x26w\\x3d400\\x26sz\\x3d48\\x26hl\\x3den\\x26start\\x3d2\\x26sig2\\x3dKKyLjjk4mt3BbRjSlYmSzw\\x26um\\x3d1\",\"\",\"FH6Irxo0S8bMIM:\",\"http://www.waterfootprint.org/images/gallery/original/apple.jpg\",\"124\",\"124\",\"\\x3cb\\x3eApple\\x3c/b\\x3e\",\"\",\"\",\"400 x 400 - 48k\",\"jpg\",\"waterfootprint.org\",\"\",\"\",\"http://t0.gstatic.com/images\",\"1\",[],\"\",1],[\"/imgres?imgurl\\x3dhttp://showclix.com/blog/wp-content/uploads/2009/08/Apple-Logo.jpg\\x26imgrefurl\\x3dhttp://www.showclix.com/blog/%3Fcat%3D7\\x26usg\\x3d__9P2VZ6OYmeK8UHZijktHBaxPuP8\\x3d\\x26h\\x3d474\\x26w\\x3d406\\x26sz\\x3d27\\x26hl\\x3den\\x26start\\x3d3\\x26sig2\\x3dQxyLFioQlQqfQ8EauNbseA\\x26um\\x3d1\",\"\",\"u40OX3r-3P4S0M:\",\"http://showclix.com/blog/wp-content/uploads/2009/08/Apple-Logo.jpg\",\"110\",\"129\",\"\\x3cb\\x3eApple\\x3c/b\\x3e to compete\",\"\",\"\",\"406 x 474 - 27k\",\"jpg\",\"showclix.com\",\"\",\"\",\"http://t2.gstatic.com/images\",\"1\",[],\"\",1],\" +\"");
			//sample.add("dyn.setResults([[\"/imgres?imgurl\\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/apple-logo1.jpg\\x26imgrefurl\\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/technology/\\x26usg\\x3d__ZRYiHpjAeWfxq74V5CGJfAEt_Dk\\x3d\\x26h\\x3d480\\x26w\\x3d397\" +");			
			sample.add("</script></body></html>");
			return sample;
		}
		
		/**
		 * This method should yeild the following results: 
		 * 철회, 탈퇴, 변절
		 * 분리기, 절단, 탈주, 무리에서 이탈한 동물, 일탈, 탈퇴, 출발 신호전에 뛰어 나감, 공을 갖고 골로 돌진하기
		 * (단체에서)...을 제명하다, (...와)인연을 끊다, 탈퇴하다
		 * 탈퇴하다
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
		private Vector testWritingParse()
		{
			Vector <String> sample = new Vector();
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
	
}