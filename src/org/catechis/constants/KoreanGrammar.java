package org.catechis.constants;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Work in progress:
 * 
 * WikiWord

동사
let
발음 듣기ⓕ 미국 
  

타동사 (vt) 
1. (목적어 + 동사원형) 시키다, 하게 하다. 
▪ He lets me laugh.
▪ Let me explain...
2. 가게 하다. 
▪ The officer lets through the bridge.
3. 빌리다. 또는 세를 놓다. 
▪ I let the car of my father.
자동사 (vi) 
1. 임대되다. 
▪ This garage lets for twenty dollars.
관용구 let alone : 개의치 않다
관용구 let drop : 무심코 말하다
관용구 let fall : 떨어뜨리다
관용구 let go : 석방하다
관용구 let oneself go : 굴하다, 감정에 빠지다
관용구 let oneself loose : 말을 주저없이 하다.
관용구 let pass : 넘겨보다, 눈 감아 주다.

 * @author a
 *
 */
public class KoreanGrammar 
{
	public static final String verb = "동사";
	public static final String adjective = "형용사";
	public static final String determiner = "관형사";
	public static final String noun = "명사";
	public static final String pronoun = "대명사";
	public static final String adverb = "부사";
	public static final String particle = "조사";
	public static final String interjection = "감탄사";
	public static final String number = "수사 ";
	
	public static final String undetermined = "undetermined";
	
	public static final String 동사 = "verb";
	public static final String 형용사 = "adjective";
	public static final String 관형사 = "determiner";
	public static final String 명사 = "noun";
	public static final String 대명사 = "pronoun";
	public static final String 부사 = "adverb";
	public static final String 조사 = "particle";
	public static final String 감탄사 = "interjection";
	public static final String 수사 = "number";
	
	public static Vector getAllGrammarStrings()
	{
		Vector grammar = new Vector();
		grammar.add("동사");
		grammar.add("형용사");
		grammar.add("관형사");
		grammar.add("명사");
		grammar.add("대명사");
		grammar.add("부사"); 
		grammar.add("조사"); 
		grammar.add("감탄사");
		grammar.add("수사");
		return grammar;
	}
	
	/**
	 * public static final String 동사 = "verb";
	public static final String 형용사 = "adjective";
	public static final String 관형사 = "determiner";
	public static final String 명사 = "noun";
	public static final String 대명사 = "pronoun";
	public static final String 부사 = "adverb";
	public static final String 조사 = "particle";
	public static final String 감탄사 = "interjection";
	public static final String 수사 = "number";
	 * @return
	 */
	public static Hashtable getKoreanToEnglish()
	{
		Hashtable grammar = new Hashtable();
		grammar.put("동사", "verb");
		grammar.put("형용사", "adjective");
		grammar.put("관형사", "determiner");
		grammar.put("명사", "noun");
		grammar.put("대명사", "pronoun");
		grammar.put("부사", "adverb"); 
		grammar.put("조사", "particle"); 
		grammar.put("감탄사", "interjection");
		grammar.put("수사", "number");
		return grammar;
	}
	
	public static Vector getKoreanGrammarStrings()
	{
		Vector grammar = new Vector();
		grammar.add("동사");
		grammar.add("형용사");
		grammar.add("관형사");
		grammar.add("명사");
		grammar.add("대명사");
		grammar.add("부사"); 
		grammar.add("조사"); 
		grammar.add("감탄사");
		grammar.add("수사");
		return grammar;
	}
	
	public static Vector getAllGrammarKeys()
	{
		Vector grammar = new Vector();
		grammar.add("verb");
		grammar.add("adjective");
		grammar.add("determiner");
		grammar.add("noun");
		grammar.add("pronoun");
		grammar.add("adverb");
		grammar.add("particle");
		grammar.add("interjection");
		grammar.add("number");
		return grammar;
	}
	
	public static String translateKoreanToEnglish(String korean)
	{
		Hashtable korean_to_english = getKoreanToEnglish();
		return (String)korean_to_english.get(korean);
	}

}
