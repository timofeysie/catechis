package org.catechis.dto;

import java.util.Vector;

public class WordCategory 
{

	private long id;
	private String name;
	/**
	 * Can be exclusive or non-exclusive.
	 * A thing can only have one exclusive category,
	 * but many non-exclusive categories.
	 */
	private String category_type;
	private long creation_time;
	private Vector category_words;
	private int total_words;
	
	public long getId() 
	{
		return id;
	}

	public void setId(long _id) 
	{
		this.id = _id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String _name) 
	{
		this.name = _name;
	}

	public String getCategoryType() 
	{
		return category_type;
	}

	public void setCategoryType(String _category_type) 
	{
		this.category_type = _category_type;
	}
	
	public void setCreationTime(long _creation_time)
	{
		this.creation_time = _creation_time;
	}
	
	public long getCreationTime()
	{
		return creation_time;
	}
	
	/**
	 * This is a Vector of SimpleWord objects.
	 * @return
	 */
	public Vector getCategoryWords()
	{
		return category_words;
	}
	
	public void setCategoryWords(Vector _category_words)
	{
		this.category_words = _category_words;
	}

	public int calculateTotalWords()
	{
		try
		{
			return category_words.size();
		} catch (java.lang.NullPointerException npe)
		{
			return 0;
		}
	}
	
	public int getTotalWordCount()
	{
		return total_words;
	}
	
	public void setTotalWordCount(int _total_words)
	{
		total_words = _total_words;
	}
}
