package org.catechis.constants;

import java.util.Collection;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.catechis.Transformer;
import org.catechis.dto.Word;

public class Loggy
{

    public Vector log;

    public Loggy()
    {
    	log = new Vector();
    }
    
    public void dumpLog(Hashtable log)
    {
	    for (Enumeration e = log.keys() ; e.hasMoreElements() ;) 
	    {
		    String key = (String)e.nextElement();
		    String val = (String)log.get(key);
		    System.out.println(key+" -		"+val);
	    }
    }

    public void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i < log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
    }
    
    public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i < size)
		{
			log.add(v.get(i));
			i++;
		}
	}
	
	public void resetLog()
	{
		log = new Vector();
	}
    
    public Vector getLog()
	{
		return log;
	}
    
    public void printCollection(Collection<?> c) 
    {
        for (Object e : c) 
        {
            System.out.println(e);
        }
    }
    
    public static void dumpWordsVector(Vector words)
    {
    	int size = words.size();
		int i = 0;
		while (i < size)
		{
			System.out.println(i+" ---");
			Word word = (Word)words.get(i);
			printLog(Transformer.createTable(word));
			i++;
		}
    }
    
    public static void printLog(Vector log)
    {
	    int i = 0;
	    while (i < log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }
    }


}
