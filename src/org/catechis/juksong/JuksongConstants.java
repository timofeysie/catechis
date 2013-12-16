package org.catechis.juksong;

public class JuksongConstants
{

	public static final String ENGLISH = "English";
	public static final String KOREAN = "Korean";
	/** English speaker learning Korean Reading. */
	public static final String EKR = "1";
	/** English speaker learning Korean Writing. */
	public static final String EKW = "2";
	/** Korean speaker learning English Reading. */
	public static final String KER = "3";
	/** Korean speaker learning English Writing. */
	public static final String KEW = "4";
	public static final String TEXT = "text";
	public static final String DEFINITION = "definition";
	/** A folder inside vocab for shared stuff like a dictionary */
	public static final String SHARED = "shared";
	/** A Eng./Kor. dictionary */
	public static final String DICTIONAY = "dictionary";
	/** English to Korean dictionary */
	public static final String EK = "ek";
	/** Korean to English dictionary */
	public static final String KE = "ke";
	
	public JuksongConstants()
	{
	}
	
	public static String printProfile(String profile)
	{
		String answer = "";
		switch (Integer.parseInt(profile))
		{
			case 1: answer = JuksongConstants.EKR+" English speaker learning Korean reading";break;
			case 2: answer = JuksongConstants.EKW+" English speaker learning Korean writing";break;
			case 3: answer = JuksongConstants.KER+" Korean speaker learning English reading";break;
			case 4: answer = JuksongConstants.KEW+" Korean speaker learning English writing";break;
		}	
		return answer;
	}
}