package org.catechis.juksong;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.constants.Constants;
import org.catechis.dto.SavedTestResult;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
/**
 * @author a
 *
 */
public class FarmingTools 
{
	
	private static Vector log;
	
	public FarmingTools()
	{
		log = new Vector();
	}

	public static String getAppropriateText(Word word, String type)
    {
		String text = new String();
    	if (type.equals(Constants.READING))
    	{
    		text = word.getText();
     	} else if (type.equals(Constants.WRITING))
    	{
    		text = word.getDefinition();
    	}
    	return text;
    }
	
	/**
	 * This method returns a profile for native speakers learning a specific language 
	 * needing a reading or writing function.
	 * It setup for only English and Korean speakers at this time.
	 * @param language_being_learned
	 * @param type
	 * @return a JuksongConstants for this specific combination of languages.
	 */
	public static String getLanguageProfile(String native_language, String language_being_learned, String type)
	{
		String profile = "0";
		if (native_language.equals(JuksongConstants.ENGLISH) && language_being_learned.equals(JuksongConstants.KOREAN))
    	{
    		if (type.equals(Constants.READING))
    		{
    			profile = JuksongConstants.EKR; // default
    			//System.err.println("native_language.equals(JuksongConstants.ENGLISH) && language_being_learned.equals(JuksongConstants.KOREAN) reading");
    		} else if (type.equals(Constants.WRITING))
    		{
    			profile = JuksongConstants.EKW;
    			//System.err.println("native_language.equals(JuksongConstants.ENGLISH) && language_being_learned.equals(JuksongConstants.KOREAN) writing");
    		}
    	} else if (native_language.equals(JuksongConstants.KOREAN) && language_being_learned.equals(JuksongConstants.ENGLISH))
    	{
    		if (type.equals(Constants.READING))
    		{
    			profile = JuksongConstants.KER;
    			//System.err.println("native_language.equals(JuksongConstants.KOREAN) && language_being_learned.equals(JuksongConstants.ENGLISH) reading");
    		} else if (type.equals(Constants.WRITING))
    		{
    			profile = JuksongConstants.KEW;
    			//System.err.println("native_language.equals(JuksongConstants.KOREAN) && language_being_learned.equals(JuksongConstants.ENGLISH) writing");
    		}
    	}
		return profile;
	}
	
	/**
	 * I'm a little vague about this myself, so if you don't understand what is going on here, I sympathise with you.
	 * @param native_language
	 * @param language_being_learned
	 * @param text_of_def
	 * @return
	 */
	public static String getAppropriateType(String native_language, String language_being_learned, String text_of_def)
	{
		String type = "";
		if (native_language.equals(JuksongConstants.ENGLISH) && language_being_learned.equals(JuksongConstants.KOREAN))
    	{
    		if (text_of_def.equals(JuksongConstants.TEXT))
    		{
    			type = Constants.READING;
    		} else if (text_of_def.equals(JuksongConstants.DEFINITION))
    		{
    			type = Constants.WRITING;
    		}
    	} else if (native_language.equals(JuksongConstants.KOREAN) && language_being_learned.equals(JuksongConstants.ENGLISH))
    	{
    		if (text_of_def.equals(JuksongConstants.TEXT))
    		{
    			type = Constants.WRITING;
    		} else if (text_of_def.equals(JuksongConstants.DEFINITION))
    		{
    			type = Constants.READING;
    		}
    	}
		return type;
	}
	
	/**
	 * Create the folder if it doesn't already exist.
	 * @param folder_path -  Path to folder.
	 * @return true if it already exists, false if it didn't (but now does).
	 */
	public static boolean ifFolderDoesntExistCreateIt(String folder_path)
	{
		File folder = new File (folder_path);
		if (!folder.exists())
		{
			folder.mkdir();
		} else
		{
			return true;
		}
		return false;
	}
	
	/**
	 * This method returns the appropriate reading or writing level based on the type passed in.
	 * This may already be in another class, but that would require some effort to find it.
	 * @param word
	 * @param test_type
	 * @return
	 */
	public static String getAppropriateLevel(Word word, String test_type)
	{
		String level = null;
		if (test_type.equals(Constants.READING))
		{
			level = ""+word.getReadingLevel();
		} else if (test_type.equals(Constants.WRITING))
		{
			level = ""+word.getWritingLevel();
		}
		return level;
	}
	
	public static int ratePerformanceStatic(Word word, String type_wanted)
	{
		int pass = 0;
		int fail = 0;
		int rate = 0;
		int i = 0;
		Test tests[] = word.getTests();
		int size = tests.length;
		log.add("tests: "+size);
		while (i<size)
		{
			try
			{
				Test test = tests[i];
				String test_name = test.getName();
				int level = Integer.parseInt(Domartin.getTestLevel(test_name));
				int increment = level*10;
				String type = Domartin.getTestType(test_name);
				String grade = test.getGrade();
				if (type.equals(type_wanted))
				{
					if (grade.equals(Constants.PASS))
					{
						pass = pass + increment;
					} else if (grade.equals(Constants.FAIL))
					{
						//fail = fail - Integer.parseInt(level);
						fail = fail - increment;
					}
					//log.add(level+" "+type+" "+grade);
				} 	
			} catch (java.lang.NullPointerException npe)
			{
				System.out.println("FarmingTools.ratePerformance: npe ");
			}
			try 
			{
				//rate = (pass/size);
				rate = (pass-fail);
			} catch (java.lang.ArithmeticException ae)
			{
				return -1;
			}
			i++;
		}
		//System.out.println("rating "+rate);
		return rate;
	}
	
	public int ratePerformance(Word word, String type_wanted)
	{
		int pass = 0;
		int fail = 0;
		int rate = 0;
		int i = 0;
		Test tests[] = word.getTests();
		int size = tests.length;
		log.add("tests: "+size);
		while (i<size)
		{
			try
			{
				Test test = tests[i];
				String test_name = test.getName();
				int level = Integer.parseInt(Domartin.getTestLevel(test_name));
				int increment = level*10;
				String type = Domartin.getTestType(test_name);
				String grade = test.getGrade();
				if (type.equals(type_wanted))
				{
					if (grade.equals(Constants.PASS))
					{
						pass = pass + increment;
					} else if (grade.equals(Constants.FAIL))
					{
						//fail = fail - Integer.parseInt(level);
						fail = fail - increment;
					}
					//log.add(level+" "+type+" "+grade);
				} 	
			} catch (java.lang.NullPointerException npe)
			{
				System.out.println("FarmingTools.ratePerformance: npe ");
			}
			try 
			{
				//rate = (pass/size);
				rate = (pass-fail);
			} catch (java.lang.ArithmeticException ae)
			{
				return -1;
			}
			i++;
		}
		//System.out.println("rating "+rate);
		return rate;
	}
	
	/**
	 * THis variable is glabal so that limit methods can alter it when activated.
	 */
	private int green;
	/**
	 * Go thru a words tests and create a color based on the words testing performance.
	 * For a passed tests, the green component is increased by a multiple of the level of that passed test.
	 * For a failed test, the red component is increased by a multiple of the level of that test level.
	 * @param word
	 * @param type_wanted
	 * @return
	 */
	public Color ratePerformancebyColor(Word word, String type_wanted)
	{
		green = 255;
		int red = 0;
		int blue = 0;
		int increment = 1;
		Color colour = new Color(0,0,255);
		int i = 0;
		Test tests[] = word.getTests();
		int size = tests.length;
		while (i<size)
		{
			try
			{
				Test test = tests[i];
				String test_name = test.getName();
				int level = 0;
				try
				{
					level = Integer.parseInt(Domartin.getTestLevel(test_name));
				} catch (java.lang.NumberFormatException nfe)
				{
					log.add("FarmingTools.ratePerformance: nfe: "+test_name);
				}
				String type = Domartin.getTestType(test_name);
				String grade = test.getGrade();
				increment = (level+1)*10;
				if (type.equals(type_wanted))
				{
					if (grade.equals(Constants.PASS))
					{
						//red = red - increment;
						//red = limitUnder(red, increment);
						blue = blue + increment;
						blue = limitOver(blue, increment);
						green = green - increment;
						green = limitUnder(green, increment);
					} else if (grade.equals(Constants.FAIL))
					{
						red = red + increment;
						red = limitOver(red, increment);
						//blue = blue - increment;
						//blue = limitUnder(blue, increment);
						green = green - increment;
						green = limitUnder(green, increment);
					}
				} 	
			} catch (java.lang.NullPointerException npe)
			{
				log.add("FarmingTools.ratePerformance: npe ");
			}
			try
			{
				colour = new Color(red,green,blue);
			} catch (java.lang.IllegalArgumentException iae)
			{
				colour = new Color(255,0,0);
				log.add("FarmingTools.ratePerformance: iae red "+red+" green "+green);
			}
			i++;
		}
		log.add("FarmingTools.ratePerformancebyColor: "+word.getText()+" "+word.getDefinition()+" tests: "+size+" - red "+red+" green "+green+" blue "+blue+" increment "+increment);
		return colour;
	}
	
	
	public Color ratePerformancebyLightColor(Word word, String type_wanted)
	{
		green = 200;
		int red = 200;
		int blue = 200;
		int increment = 1;
		Color colour = new Color(0,0,255);
		Test tests[] = word.getTests();
		for (int i = 0;i<tests.length;i++)
		{
			Test test = tests[i];
			String test_name = test.getName();
			try
			{
				String type = Domartin.getTestType(test_name);
				String grade = test.getGrade();
				int level = Integer.parseInt(Domartin.getTestLevel(test_name));
				increment = level+increment;
				if (type.equals(type_wanted))
				{
					if (grade.equals(Constants.PASS))
					{
						blue = blue + increment;
						blue = limitOver(blue, increment);
						green = green + increment;
						green = limitUnder(green, increment);
						log.add("ratePerformancebyLightColor: pass - green "+green+" blue "+blue);
					} else if (grade.equals(Constants.FAIL))
					{
						blue = blue - increment;
						blue = limitUnder(blue, increment);
						green = green - increment;
						green = limitUnder(green, increment);
						log.add("ratePerformancebyLightColor: fail - green "+green+" blue "+blue);
					}	
				}
			} catch (java.lang.NullPointerException npe)
			{
				log.add("npe for "+i);
				append(Transformer.createTable(test));
			} catch (java.lang.NumberFormatException nfe)
			{
				log.add("nfe for "+i);
				append(Transformer.createTable(test));
				String grade = test.getGrade();
				blue = blue - increment;
				blue = limitUnder(blue, increment);
				green = green - increment;
				green = limitUnder(green, increment);
				log.add("ratePerformancebyLightColor: fail - green "+green+" blue "+blue);	
			}
		}
		colour = new Color(red,green,blue);
		log.add("FarmingTools.ratePerformancebyColor: "+word.getText()+" "+word.getDefinition()+" - red "+red+" green "+green+" blue "+blue+" increment "+increment);
		return colour;
	}
	
	
	private int limitOver(int color, int increment)
	{
		if (color>255)
		{
			color = 255;
		}
		green = green - increment;
		if (green<0)
		{
			green = 0;
		}
		return color;
	}
	
	private int limitUnder(int color, int increment)
	{
		if (color<0)
		{
			color = 0;
		}
		green = green - increment;
		if (green<0)
		{
			green = 0;
		}
		return color;
	}

    public Vector getLog()
    {
	    return log;
    }
    
    private static Vector append(Vector add, Vector data)
    {
	    int i = 0;
	    while (i<add.size())
	    {
		    data.add(add.get(i));
		    i++;
	    }
	    return data;
    }
    
    private void append(Vector data)
	{
		int total = data.size();
		int i = 0;
		while (i<total)
		{
			log.add(data.get(i));
			i++;
		}
	}
}
