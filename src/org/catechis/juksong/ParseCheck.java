package org.catechis.juksong;

import java.io.*;

import javax.xml.parsers.*;

import org.catechis.constants.Loggy;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class uses the org.w3c.dom and org.xml.sax packages.
 * @author a
 *
 */
public class ParseCheck extends Loggy
{
	
	private int test_index;
	
    public String check(String user, String file_name)
    {
    	String message = "";
    	File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        //String file = new String(""March II.xml"");
        //String user = new String("-5519451928541341468");

    		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    		String path = new String(current_dir+File.separator+"files"
	    		+File.separator+user+File.separator+file_name);
    		log.add("ParseCheck.parse: path "+path);
    		File file = new File(path);
    		if(file.exists())
    		{
    			try 
    			{
    				// Create a new factory to create parsers 
    				DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
    				// Use the factory to create a parser (builder) and use
    				// it to parse the document.
    				DocumentBuilder builder = dBF.newDocumentBuilder();
    				// builder.setErrorHandler(new MyErrorHandler());
    				InputSource is = new InputSource(path);
    				Document doc = builder.parse(is);
    				printDoc(doc);
    				message = (path + " is well-formed!");
    			}
    			catch (Exception e) 
    			{
    				message = (path + " isn't well-formed!");
    				System.exit(1);
    			}
    		} else
    		{
    			message = ("File not found!");
    		}
    		return message;
    }
    
    public String check(String path)
    {
    	String message = "";
    	File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        //String file = new String(""March II.xml"");
        //String user = new String("-5519451928541341468");

    		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    		log.add("ParseCheck.parse: path "+path);
    		File file = new File(path);
    		if(file.exists())
    		{
    			try 
    			{
    				// Create a new factory to create parsers 
    				DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
    				// Use the factory to create a parser (builder) and use
    				// it to parse the document.
    				DocumentBuilder builder = dBF.newDocumentBuilder();
    				// builder.setErrorHandler(new MyErrorHandler());
    				InputSource is = new InputSource(path);
    				Document doc = builder.parse(is);
    				printDoc(doc);
    				message = (path + " is well-formed!");
    			}
    			catch (Exception e) 
    			{
    				message = (path + " isn't well-formed!");
    				System.exit(1);
    			}
    		} else
    		{
    			message = ("File not found!");
    		}
    		return message;
    }
    
    public Word unbindWord(String word_id, String user, String file_path, String subject)
    {
    	Word word = new Word();
    	String message = "";
    	File path_file = new File("");
        String current_dir = path_file.getAbsolutePath();
        //String file = new String(""March II.xml"");
        //String user = new String("-5519451928541341468");

    		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    		log.add("ParseCheck.parse: path "+file_path);
    		File file = new File(file_path);
    		if(file.exists())
    		{
    			try 
    			{
    				// Create a new factory to create parsers 
    				DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
    				// Use the factory to create a parser (builder) and use
    				// it to parse the document.
    				DocumentBuilder builder = dBF.newDocumentBuilder();
    				// builder.setErrorHandler(new MyErrorHandler());
    				InputSource is = new InputSource(file_path);
    				Document doc = builder.parse(is);
    				NodeList nl = doc.getChildNodes();
    				word = findWord(nl, word_id);
    				//printDoc(doc);
    				message = (file_path + " is well-formed!");
    			}
    			catch (Exception e) 
    			{
    				message = (file_path + " isn't well-formed!");
    				System.exit(1);
    			}
    		} else
    		{
    			message = ("File not found!");
    		}
    		return word;
    }
    
    private Word findWord(NodeList nl, String id)
    {
    	Word word = new Word();
    	int length = nl.getLength();
    	//log.add("findWord length "+length);
    	Node vocab_node = nl.item(0);
    	NodeList words = vocab_node.getChildNodes();
    	int size = words.getLength();
    	//log.add("words "+size);
    	
    	int i = 0;
    	while (i<size)
    	{
    		Node this_word = words.item(i);
    		NodeList items = this_word.getChildNodes();
    		if (items.getLength()>0)
    		{
    			//log.add(i+" item "+items.getLength());
    			String name = this_word.getNodeName();
    			String value = this_word.getNodeValue();
    			short type = this_word.getNodeType();
    			//log.add(name+" - "+value+ " - "+type);
    			NodeList word_node = this_word.getChildNodes();
    			word = getValues(this_word);
    		}
    		i++;
    	}
    	
    	return word;
    }
    
    private Word getValues(Node this_word)
    {
    	test_index = 0;
    	Word word = new Word();
    	NodeList word_node = this_word.getChildNodes();
		for (int j = 0; j<word_node.getLength();j++)
		{
			Node item = word_node.item(j);
			String item_name = item.getNodeName();
			String item_value = item.getNodeValue();
			if (item_name.equals("text"))
			{
				NodeList child_nodes = item.getChildNodes();
				Node item_0 = child_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				word.setText(item_0_value);
				//log.add(item_name+" "+item_0_value);
			} else if (item_name.equals("definition"))
			{
				NodeList child_nodes = item.getChildNodes();
				Node item_0 = child_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				word.setDefinition(item_0_value);
				//log.add(item_name+" "+item_0_value);
			} else if (item_name.equals("reading-level"))
			{
				NodeList child_nodes = item.getChildNodes();
				Node item_0 = child_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				word.setReadingLevel(Integer.parseInt(item_0_value));
				//log.add(item_name+" "+item_0_value);
			} else if (item_name.equals("writing-level"))
			{
				NodeList child_nodes = item.getChildNodes();
				Node item_0 = child_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				word.setWritingLevel(Integer.parseInt(item_0_value));
				//log.add(item_name+" "+item_0_value);
			} else if (item_name.equals("id"))
			{
				NodeList child_nodes = item.getChildNodes();
				Node item_0 = child_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				word.setId(Long.parseLong(item_0_value));
				//log.add(item_name+" "+item_0_value);
			} else if (item_name.equals("test"))
			{
				NodeList child_nodes = item.getChildNodes();
				Test test = unbindTest(child_nodes);
				//word.setTests(test_index, test);
				//test_index++;
			}
		}
		return word;
    }
    
    /**
     * <test>
     * 	<date>Thu Oct 27 16:30:04 KST 2005</date>
     * 	<file>level 0 reading.test</file>
     * 	<grade>pass</grade>
     * </test>
     * @param child_nodes
     * @return
     */
    private Test unbindTest(NodeList child_nodes)
    {
    	Test test = new Test();
    	for (int j = 0; j<child_nodes.getLength();j++)
    	{
    		Node item = child_nodes.item(j);
    		String item_name = item.getNodeName();
    		String item_value = item.getNodeValue();
    		if (item_name.equals("date"))
    		{
    			NodeList test_nodes = item.getChildNodes();
				Node item_0 = test_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				test.setDate(item_0_value);
				log.add(item_name+" "+item_0_value);
    		} else if (item_name.equals("file"))
    		{
    			NodeList test_nodes = item.getChildNodes();
				Node item_0 = test_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				test.setName(item_0_value);
				log.add(item_name+" "+item_0_value);
    		} else if (item_name.equals("grade"))
    		{
    			NodeList test_nodes = item.getChildNodes();
				Node item_0 = test_nodes.item(0);
				String item_0_value = item_0.getNodeValue();
				test.setGrade(item_0_value);
				log.add(item_name+" "+item_0_value);
    		}
    	}
    	return test;
    }
    
    private void printDoc(Document doc)
    {
    	NodeList nodes = doc.getElementsByTagName("word");
    	int i = 0;
    	int size = nodes.getLength();
    	while (i<size)
    	{
    		Node word_node = nodes.item(i);
    		NodeList word_nodes = word_node.getChildNodes();
    		int j = 0;
        	int nize = word_nodes.getLength();
        	while (j<nize)
        	{
        		Node sub_node = word_nodes.item(j);
        		String text_content = sub_node.getTextContent();
        		String name = sub_node.getNodeName();
        		String base_uri = sub_node.getBaseURI();
        		//log.add(base_uri+" : "+name+" - "+text_content);
        		log.add(name+" - "+text_content);
        		j++;
        	}
    		i++;
    	}
    	
    }
} 