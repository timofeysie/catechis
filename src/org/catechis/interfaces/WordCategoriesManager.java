package org.catechis.interfaces;

import java.util.Hashtable;
import java.util.Vector;

import org.catechis.dto.WordCategory;

public interface WordCategoriesManager 
{
	long addWordCategory(WordCategory word_category, String user_id, String subject, String current_dir, String encoding);
	Vector getWordCategoryNames(String user_id, String subject, String current_dir);
	WordCategory getWordCategory(long category_id, String user_id, String subject, String current_dir);
	boolean deleteCategory(long search_id, String user_id, String subject, String current_dir, String encoding);
	Vector getAllExclusiveCategories(String user_id, String subject, String current_dir);
}
