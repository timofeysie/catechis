/**
 * 
 */
package org.catechis.interfaces;

import java.util.Hashtable;
import java.util.Vector;

import org.catechis.gwangali.GwangaliLogi;

/**
 * @author a
 *
 */
public interface ROTStorage 
{
	public Vector getROTVector(String type, String user_id, String subject, String current_dir);
	public Vector getROTDVector();
	public Hashtable getROTOptionsHash(String type, String user_id, String subject, String current_dir);
	public void editROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir);
	public void reverseEditROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir);
	public void createROTFolderAndFiles(String type, String user_id, String subject, String current_dir);
	public Vector getLog();
}
