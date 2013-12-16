package org.catechis.gwangali;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.catechis.Domartin;
import org.catechis.Transformer;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordCategory;

public class GwangaliLogi 
{
	public Vector log;
	
	public GwangaliLogi()
	{
		log = new Vector();
	}
	
	public Vector getLog()
	{
		return log;
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
	
	public void dumpLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
	}
	
	private void dumpHash(Hashtable hash)
	{
	    Enumeration keys = hash.keys();
	    while (keys.hasMoreElements())
	    {
		    String key = (String)keys.nextElement();
		    String val = (String)hash.get(key);
		    System.out.println(key+" - "+val);
	    }
	}
	
	public void printLog(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			System.out.println("log- "+log.get(i));
			i++;
		}
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
	
	public void addArrayListToLog(ArrayList list)
	{
		int total = list.size();
		int i = 0;
		while (i<total)
		{
			log.add(Transformer.createTable(list.get(i)));
			i++;
		}
	}
	
	public void addErrorToLog(java.lang.NullPointerException npe)
	{
			log.add("toString           : "+npe.toString());
			log.add("getMessage         : "+npe.getMessage());
			log.add("getLocalziedMessage:"+npe.getLocalizedMessage());
			Throwable throwup = npe.getCause();
			Throwable init_cause = npe.initCause(throwup);
			log.add("thowable.msg       :"+init_cause.toString());
			StackTraceElement[] ste = npe.getStackTrace();
			for (int j=0;j<ste.length;j++)
			{
				log.add(j+" - "+ste[j].toString());
				if (j>6)
				{
					log.add("  ...");
					break;
				}
			}
	}
	
	public void printFiles(String [] files)
	{
		int i = 0;
		int l = files.length;
		while (i<l)
		{
			String file_w_ext = files [i];
			String file = Transformer.getLongDateFromMilliseconds(Domartin.getFileWithoutExtension(file_w_ext));
			System.out.println(i+" "+file);
			i++;
		}
	}
	
	public void findNPE(String attempt, String label)
	{
		try
		{
			//System.out.println("attempted: "+label+" = "+attempt+": success");
		} catch (java.lang.NullPointerException npe)
		{
			log.add("attempted: "+label+" failed");
		}
	}
	
	public void findNPEs(Test test)
	{
		try
		{
			String str_date = test.getDate();
			String str_name = test.getName();
			String type  = Domartin.getTestType(str_name);
			String level = Domartin.getTestLevel(str_name);
			findNPE(str_date, "str_date");
			findNPE(str_name, "str_name");
			findNPE(type, "type");
			findNPE(level, "level");
		} catch (java.lang.NullPointerException npe)
		{
			log.add("entire test is null");
		}
	}
	
	public void printLog(Vector log, ServletContext context, String title)
	{
		context.log("=================_ "+title+" _=================");
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			context.log("log "+(String)log.get(i));
			i++;
		}
	}
	
	public void printLog(Hashtable log, ServletContext context, String title)
	{
		context.log("=================_ "+title+" _=================");
		for (Enumeration e = log.elements() ; e.hasMoreElements() ;) 
		{
			String key = (String)e.nextElement();
			String val = (String)log.get(key);
			context.log(key+" - "+val);
		}
	}	
	
	public void printCategories(Vector log)
	{
		System.out.println("name 	type		date	id				words");
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			WordCategory word_cat = (WordCategory)log.get(i);
			System.out.println(word_cat.getName()
					+"	 "+word_cat.getCategoryType()
					+Transformer.getDateFromMilliseconds(Long.toString(word_cat.getCreationTime()))
					+"	"+word_cat.getId()
					+"	"+word_cat.getTotalWordCount());
			i++;
		}
	}
	
	public void printWords(Vector log)
	{
		int total = log.size();
		int i = 0;
		while (i<total)
		{
			Word word = (Word)log.get(i);
			System.out.println(word.getText()+"	"+word.getDefinition());
			i++;
		}
	}
	
}
