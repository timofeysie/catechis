package org.catechis.juksong;
/*
 Copyright 2007 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.Locale;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.dto.Word;

import org.catechis.interfaces.Edit;
import org.catechis.EncodeString;

/**
*This class was made to create file specific methods to used categories or words.
*<p>This object is not threadsafe. 
*/
public class EditWord implements Edit
{
	
	private Vector log;
	private Document doc;
	private String file_name;
	
	public EditWord(String _file_name)
	{
		file_name = _file_name;
		File file = new File(file_name);
		EncodeString encoder = new EncodeString();
		doc = null;
		log = new Vector();
		try
		{
			encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add("FileEdit<init> "+j.toString());
		} catch (java.io.IOException i)
		{
			log.add("FileEdit<init> "+i.toString());
		}
	}
	
	public void editWord(Word old_word_obj, Word new_word_obj)
	{
		log.add("FileEdit.editWord");
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		Element text = null;
		Element def  = null;
		String search_text = old_word_obj.getText();
		String search_def  = old_word_obj.getDefinition();
		int size = list.size();
		log.add("list size "+size);
		log.add("search text "+search_text);
		log.add("search def "+search_def);
		log.add("new text "+new_word_obj.getText());
		log.add("new def "+new_word_obj.getDefinition());
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				log.add("FileEdit.editWord: found word at "+i);
				text = e.getChild("text");
				def  = e.getChild("definition");
				text.setText(new_word_obj.getText());
				def.setText(new_word_obj.getDefinition());
				break;
			}
			i++;
		}
	}
	
	/**
	*<p>The original delete word where the caller has to call writeDocument separately.
	*/
	public void deleteWord(Word word_obj) 
	{
		log.add("FileEdit.deleteWord");
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = word_obj.getText();
		String search_def  = word_obj.getDefinition();
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				root.removeContent(e);
				break;
			}
			i++;
		}
	}
	
	/**
	*<p>This note was in FileJDOMWordLists:
	* We had a problem where the text elements content was being multiplied each time a doc was saved, 
	* but this seemed to be due to the editor nedit showing a compounded text, 
	* but the text appeared fine in the program.
	*<p>So now we have the same problem here.  Can it really be nedits problem?
	* In JEdit, the values appear compounded, so what did we do in WordLists that we don't do here..
	*/
	public void deleteWord(Word word_obj, String encoding) 
	{
		log.add("FileEdit.deleteWord");
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = word_obj.getText();
		String search_def  = word_obj.getDefinition();
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				root.removeContent(e);
				break;
			}
			i++;
		}
		writeDocument(file_name, encoding); 
	}
	
	public void changeId(Word word, long new_id)
	{
		Element root = doc.getRootElement();
		List list = root.getChildren("word");
		String search_text = word.getText();
		String search_def  = word.getDefinition();
		int size = list.size();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)list.get(i);
			String str_text = e.getChildText("text");
			String str_def  = e.getChildText("definition");
			log.add("looking at "+str_text+" "+str_def);
			if (str_text.equals(search_text)||str_def.equals(search_def))
			{
				Element new_id_elem = new Element("id");
				new_id_elem.addContent(Long.toString(new_id));
				e.removeChild("id");
				e.addContent(new_id_elem);
				break;
			}
			i++;
		}
	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument(String file_name, Document doc2)
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(doc2, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
			log.add("FileCategories.writeDocument: ioe for "+file_name);
		}
		//context.log("CreateJDOMList.writeDocument: 1 "+file_name);
	}
	
	/**
	*<p>Write document with encoding.</p>
	*<p>XMLOutputter(java.lang.String indent, boolean newlines, java.lang.String encoding)</p>
	*/	
	public void writeDocument(String file_name, String encoding)
	{
		//log.add("JDOMSolution.writeDocument: writing "+file_name+" ...");
		if (doc==null)
		{
			//log.add("JDOMSolution.writeDocument: 2 doc is null");
		}
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false, encoding);
			FileOutputStream fos = new FileOutputStream(file_name);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			outputter.output(doc, osw);
			//log.add("JDOMSolution.writeDocument: "+file_name);
			osw.close();
		} catch (java.io.IOException e)
		{
			//log.add("JDOMSolution.writeDocument: io error");
			e.printStackTrace();
		}
		//log.add("JDOMSolution.writeDocument: did it work?");
		//log.add("");
	}
	
	/** deguggin */
	public Vector getLog()
	{
		return log;
	}
	
	public void append(Vector v)
	{
		int size = v.size();
		int i = 0;
		while (i<size)
		{
			log.add(v.get(i));
			i++;
		}
	}
}
