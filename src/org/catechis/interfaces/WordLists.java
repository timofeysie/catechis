package org.catechis.interfaces;

import java.util.Vector;
import org.catechis.dto.Word;

public interface WordLists
{
	public void addToNewWordsList(Word word, String path, String type, String encoding);
	public Vector getNewWordsList(String type);
	public void removeWordFromNewWordsList(String search_id, String encoding);
}
