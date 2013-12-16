package org.catechis.filter;

import org.catechis.Domartin;
import org.catechis.FileStorage;
import org.catechis.Filter;
import org.catechis.Storage;
import org.catechis.file.FileTestRecords;
import org.catechis.dto.UserInfo;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.WordTestRecordOptions;

import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class ImportListFilter
{
	
	private Vector log;

	public ImportListFilter()
	{
		log = new Vector();
	}

	/**
	*<p>
	*/
	public Hashtable parse(String data, String word_separator, String line_separator)
	{
		log.add("ImportListFilter.parse: data size "+data.length());
		Hashtable words = new Hashtable();
		int i = data.indexOf(line_separator);
		boolean first_pass = true;
		while (i != -1)
		{
			String line = new String();
			if (first_pass)
			{
				line = data.substring(0, i);
				first_pass = false;
			} else
			{
				line = data.substring(0, i);
			}
			line.trim();
			int j = line.indexOf(word_separator);
			if (j == -1)
			{
				break;
			}
			String text = line.substring(0, j);
			String def  = line.substring(j+1, line.length());
			words.put(text, def);
			data = data.substring(i+1, data.length());
			i = data.indexOf(line_separator);
			log.add("ImportListFilter.parse: added "+text+" "+def);
		}
		log.add("ImportListFilter.parse: words "+words.size());
		return words;
	}
	
	/**
	 * 
	 * @param long_time
	 * @param results
	 * @param session
	 * @param encoding
	 * @param context
	 */
	public void saveWords(long long_time, Hashtable words, String file_name, UserInfo user_info)
	{
		for (Enumeration e = words.keys() ; e.hasMoreElements() ;) 
		{
			String text = (String)e.nextElement();
			String definition = (String)words.get(text);
			long id = Domartin.getNewID();
			Word word = new Word();
			if (long_time == 0)
			{
				Date date = new Date();
				long_time = date.getTime();
			}
			word.setText(text);
			word.setDefinition(definition);
			word.setDateOfEntry(long_time);
			word.setId(id);
			word.setReadingLevel(0); // should allow for the user to pass this in 
			word.setWritingLevel(0); // but the default is 0
			word.setCategory(file_name);
			String context_path = user_info.getRootPath();
			String user_id = user_info.getUserId();
			String encoding = user_info.getEncoding();
			FileStorage store = new FileStorage(context_path);
			store.addWord(word, file_name, user_id, encoding);
		}
		//printLog(store.getLog(), context);
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
			log.add(v.get(i));
			i++;
		}
	}

}
