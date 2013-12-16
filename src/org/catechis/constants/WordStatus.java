package org.catechis.constants;

public class WordStatus 
{

	private String [] word_status_type_chart;
	private int [] word_status_level_chart;
	
	private String [] setupWordStatusTypeChart()
	{
		word_status_type_chart = new String[9];
		word_status_type_chart[0] = Constants.NEW_WORD;
		word_status_type_chart[1] = Constants.READING;
		word_status_type_chart[2] = Constants.WRITING;
		word_status_type_chart[3] = Constants.READING;
		word_status_type_chart[4] = Constants.WRITING;
		word_status_type_chart[5] = Constants.READING;
		word_status_type_chart[6] = Constants.WRITING;
		word_status_type_chart[7] = Constants.READING;
		word_status_type_chart[8] = Constants.WRITING;
		word_status_type_chart[9] = Constants.RETIRED;
		return word_status_type_chart;
	}
	
	public String [] getWordStatusTypeChart()
	{
		return word_status_type_chart;
	}
	
	public String getWordStatusType(int status)
	{
		setupWordStatusTypeChart();
		return word_status_type_chart[status];
	}
	
	private int [] setupWordStatusLevelChart()
	{
		word_status_level_chart = new int[9];
		word_status_level_chart[0] = -1;
		word_status_level_chart[1] = 0;
		word_status_level_chart[2] = 0;
		word_status_level_chart[3] = 1;
		word_status_level_chart[4] = 1;
		word_status_level_chart[5] = 2;
		word_status_level_chart[6] = 2;
		word_status_level_chart[7] = 3;
		word_status_level_chart[8] = 3;
		word_status_level_chart[9] = 4;
		return word_status_level_chart;
	}
	
	public int [] getWordStatusLevelChart()
	{
		return word_status_level_chart;
	}
	
	public int getWordStatusLevel(int status)
	{
		setupWordStatusLevelChart();
		return word_status_level_chart[status];
	}
	
	
}
