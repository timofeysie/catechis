package org.catechis.legacy;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Vector;

import org.catechis.dto.UserInfo;
import org.junit.Test;

import org.catechis.legacy.CreateJDOMList;
import org.catechis.Domartin;

public class CreateJDOMListTest 
{

	@Test
	public void testGetTextDefVector() 
	{ 
		System.out.println("CreateJDOMListTest.testGetTextDefVector.");
		String user_id = new String("-5519451928541341468");
   		String category_file_name = "Ganada I1 workbook.xml";
   		//String subject = new String("vocab");
   		//String second_word_id = "-2672985807382824350";
   		String encoding = new String("euc-kr");
   		//String encoding = new String("UTF-8");
		File path_file = new File("");
		String root_path = path_file.getAbsolutePath();
		String user_path = new String(root_path+File.separator
				+"files"+File.separator+user_id);
		File file_chosen = Domartin.createFileFromUserNPath(category_file_name, user_path);
		boolean exists = file_chosen.exists();
		System.out.println("file chosen "+file_chosen.getAbsolutePath());
		System.out.println("exists? "+exists);
		System.out.println("CreateJDOMListTest.testGetTextDefVector: jdom log --------------");
		CreateJDOMList jdom = new CreateJDOMList(file_chosen, encoding);
		Vector elements = jdom.getTextDefVector();
		System.out.println("CreateJDOMListTest.testGetTextDefVector: "+elements.size()+" ---");
		dumpLog(elements);
		System.out.println("CreateJDOMListTest.testGetTextDefVector: jdom log --------------");
		dumpLog(jdom.getLog());
		fail("Not yet implemented"); // TODO
	}
	
	private void dumpLog(Vector log)
    {
	    int i = 0;
	    while (i<log.size())
	    {
		    System.out.println("log - "+log.get(i));
		    i++;
	    }

    }

}
