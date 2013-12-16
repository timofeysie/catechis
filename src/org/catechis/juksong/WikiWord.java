package org.catechis.juksong;

import org.catechis.juksong.Definition;

/**
 * Word id, text and multiple definitions, plus encoding and the url where the word came from
 * A Definition has the following properties:
* String definition; one possible definition.
* String grammar; grammar type.
 * @author a
 *
 */
public class WikiWord 
{
	private long id;
	private String text;
	private Definition [] definition;
	private String encoding;
	private String source;
	private Category [] category;

	public Category[] getCategory() 
	{
		return category;
	}

	public void setCategory(Category[] category) 
	{
		this.category = category;
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getText() 
	{
		return text;
	}

	public String getEncoding() 
	{
		return encoding;
	}

	public void setEncoding(String encoding) 
	{
		this.encoding = encoding;
	}

	public String getSource() 
	{
		return source;
	}

	public void setSource(String source) 
	{
		this.source = source;
	}

	public void setText(String text) 
	{
		this.text = text;
	}
	
	//Methods to access the entire indexed property array
	public Definition[] getDefinitions()
	{
		return definition;
	}
	
	public void setDefinitions(Definition[] _definition)
	{
		definition = _definition;
	}

	//Methods to access individual values
	public Definition getDefinitions(int index)
	{
		return definition[index];
	}
	
	public void setDefinitions(int index, Definition _Definition)
	{
		definition[index] = _Definition;
	}
	
	
}
