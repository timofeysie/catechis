package org.catechis.dto;

import java.util.Vector;

public class SimpleWord
{

	private long word_id;
	private String word_path;
	private String text;
	private String definition;
	
	public SimpleWord()
	{
		
	}
	
	public SimpleWord(long _word_id, String _word_path, String _text, String _definition)
	{
		word_id = _word_id;
		word_path = _word_path;
		text = _text;
		definition = _definition;
	}

	public long getWordId() 
	{
		return word_id;
	}

	public void setWordId(long _word_id) 
	{
		this.word_id = _word_id;
	}

	public String getWordPath() 
	{
		return word_path;
	}

	public void setWordPath(String _word_path) 
	{
		this.word_path = _word_path;
	}

	public void setText(String _text)
	{
		text = _text;
	}
	
	public String getText()
	{
		return text;
	}	
	
	public void setDefinition(String _definition)
	{
		definition = _definition;
	}
	
	public String getDefinition()
	{
		return definition;
	}
	
}
