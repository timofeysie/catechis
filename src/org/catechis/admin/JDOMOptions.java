package org.catechis.admin;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import java.lang.NumberFormatException;

import org.jdom.Element;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.EncodeString;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;

/**
*<p>This is a lightweight object to work with user options file.
*<p>
*/
public class JDOMOptions
{
	private Document doc;
	private String file_name;
	private EncodeString encoder;
	private Vector log;
	
	/**
	*This constructor was created for testing purposes.  We need to create a
	* test document by hand so that we can test the respective methods.
	*/
	public JDOMOptions(Document _doc)
	{
		this.doc = _doc;
		log = new Vector();
	}

	public JDOMOptions(String _file_name)
	{
		log = new Vector();
		log.add("JDOMOptions<init> "+ _file_name);
		this.file_name = _file_name;
		File file = new File(file_name);
		log.add("JDOMOptions<init> extsts? "+file.exists()+" "+_file_name);
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
			log.add("JDOMOptions<init> JDOMException"); // +j.toString());
		} catch (java.io.IOException i)
		{
			log.add("JDOMOptions<init> IOException"); // +i.toString());
		}
	}

	public Hashtable getOptionsHash()
	{
		log.add("JDOMOptions.getOptionsHash");
		Hashtable options = new Hashtable();
		try
		{
			Element root = doc.getRootElement();
			List option_list = root.getChildren("option");
			int size = option_list.size();
			log.add("size "+size);
			int i = 0;
			while (i<size)
			{
				Element e = (Element)option_list.get(i);
				String name = e.getChildText("name");
				String value  = e.getChildText("value");
				log.add(name+" "+value);
				options.put(name, value);
				i++;
			}
		} catch (java.lang.NullPointerException npe)
		{
			log.add("JDOMOptions.getOptionsHash: npe");
			log.add(npe.toString());
		}
		return options;
	}
	
	public void addUserOption(String option, String init_value)
	{
		Element root = doc.getRootElement();
		Element new_option = new Element("option");
		Element name = new Element("name");
		Element value = new Element("value");
		name.addContent(option);
		value.addContent(init_value);
		new_option.addContent(name);
		new_option.addContent(value);
		root.addContent(new_option);
	}
	
	/**
	*This creates a new document for a blank file which should already exist
	* with a root element "user_options".
	*/
	public void createFile(String file_path)
	{
		file_name = file_path;
		Element root = new Element("user_options");
		doc = new Document(root);
		writeDocument();
	}
	
	/**
	*
	* Replace an old option with a new value
	*/
	public void editOption(String option_name, String new_value)
    	{
        	log.add("JDOMOptions.editOptions: recieved "+option_name+" with new value "+new_value);
        	try
        	{
            		Element root = doc.getRootElement();
            		List option_list = root.getChildren("option");
            		int size = option_list.size();
            		log.add("size "+size);
            		int i = 0;
            		while (i<size)
            		{
            			Element e = (Element)option_list.get(i);
                		String name = e.getChildText("name");
                		String value  = e.getChildText("value");
                		if (name.equals(option_name))
                		{
                    			Element value_element  = e.getChild("value");
                    			value_element.setText(new_value);
                    			break;
                		}
                		i++;
                	}
            	} catch (java.lang.NullPointerException npe)
        	{
        		log.add("JDOMOptions.editOptions: npe");
            		log.add(npe.toString());
        	}
        	writeDocument();
    	}
	
	/**
	*<p>Write document with no encoding.</p>
	*/
	public void writeDocument()
	{
		try
		{
			XMLOutputter outputter = new XMLOutputter("  ", false);
			FileWriter writer = new FileWriter(file_name);
			outputter.output(doc, writer);
			writer.close();
		} catch (java.io.IOException e)
		{
			e.printStackTrace();
		} catch (java.lang.NullPointerException npe)
		{
			log.add("CreateJDOMList.writeDocument: npe "+file_name);
		}
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

    public void resetLog()
    {
	log = new Vector();
    }
    
    public Vector getLog()
    {
	    return log;
    }

}
