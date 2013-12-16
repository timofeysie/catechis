package org.catechis.dto;

import org.catechis.juksong.FarmingTools;

/**
 * In conjunction with a SavedTest object, this provides
 * the test result info for a word among a list of words
 * that share the same test info in SavedTest after scoring.
 * @author a
 *
 */
public class SavedTestResult 
{
	private String text;
	private String definition;
	private String category;
	private long word_id;					
	private String testing_type;
	private String original_level;
	private String new_level;
	private int test_index;		
	private String grade;	
	private String encoding;
	
	public SavedTestResult(Word word, String _testing_type, 
			int _test_index, String _grade, String _encoding)
	{
		this.text = word.getText();
		this.definition = word.getDefinition();
		this.category = word.getCategory();
		this.word_id = word.getId();
		this.testing_type = _testing_type;
		this.original_level = FarmingTools.getAppropriateLevel(word, testing_type);
		// new level unknown so far.
		this.test_index = _test_index;
		this.grade = _grade;
		this.encoding = _encoding;
	}
	
	public String getText() 
	{
		return text;
	}
	public void setText(String text) 
	{
		this.text = text;
	}
	public String getDefinition() 
	{
		return definition;
	}
	public void setDefinition(String definition) 
	{
		this.definition = definition;
	}
	public String getCategory() 
	{
		return category;
	}
	public void setCategory(String category) 
	{
		this.category = category;
	}
	public long getWordId() 
	{
		return word_id;
	}
	public void setWordId(long word_id) 
	{
		this.word_id = word_id;
	}
	public String getTestingType() 
	{
		return testing_type;
	}
	public void setTestingType(String testing_type) 
	{
		this.testing_type = testing_type;
	}
	public String getOriginalLevel() 
	{
		return original_level;
	}
	public void setOriginalLevel(String original_level) 
	{
		this.original_level = original_level;
	}
	public String getNewLevel() 
	{
		return new_level;
	}
	public void setNewLevel(String new_level) 
	{
		this.new_level = new_level;
	}
	public int getTest_index() 
	{
		return test_index;
	}
	public void setTestIndex(int test_index) 
	{
		this.test_index = test_index;
	}
	public String getGrade() 
	{
		return grade;
	}
	public void setGrade(String grade) 
	{
		this.grade = grade;
	}
	public String getEncoding() 
	{
		return encoding;
	}
	public void setEncoding(String encoding) 
	{
		this.encoding = encoding;
	}
	
}