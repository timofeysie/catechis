package org.catechis.juksong;

import java.util.Vector;

public class Category 
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
	private Vector word_ids;
	
	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getCategoryType() 
	{
		return category_type;
	}

	public void setCategoryType(String type) {
		this.category_type = category_type;
	}
	
	public void setCreationTime(long _creation_time)
	{
		this.creation_time = _creation_time;
	}
	
	public long getCreationTime()
	{
		return creation_time;
	}
	
	public void setWordIds(Vector _word_ids)
	{
		this.word_ids = _word_ids;
	}
	
	
	public Vector getWordIds()
	{
		return word_ids;
	}

	
}
