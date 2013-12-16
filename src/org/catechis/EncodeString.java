package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.ResourceBundle;
import java.util.Locale;
import java.io.File;
import java.io.Reader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

// debugging
// import javax.servlet.ServletContext;
import java.lang.StringBuffer;

public class EncodeString
{
	private static String original;
	private static byte[] utf8Bytes;
	private static String tempString;
	private static Reader in;

	public EncodeString()
	{}
	
	public Reader encodeReader(File file)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "euc-kr");
			in = new BufferedReader(isr);
		} catch (java.io.FileNotFoundException fnfe)
		{
			// this prints during FileUserOptionsTest.testGetJSPOptionsFail,
			// which fails on purpose, but we don't want to see a message in the prompt output...
			//System.err.println("EncodeString.encodeReader:fnf "+file.getAbsolutePath());
			//fnfe.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return in;
	}
	
	public Reader encodeReader(File file, String encoding)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			in = new BufferedReader(isr);
		} catch (java.io.FileNotFoundException fnfe)
		{
			// this prints during FileUserOptionsTest.testGetJSPOptionsFail,
			// which fails on purpose, but we dont want to see a message in the prompt output...
			//System.err.println("EncodeString.encodeReader:fnf "+file.getAbsolutePath());
			//fnfe.printStackTrace();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	<p>This method takes a string from the ResourceBundle to encode.
	<p>The errors, if any, are returned in the are returned in the String.
	<p>There are definitely better ways to do that, such as exceptions, etc.
	*/
	public static String encodeThis(String value, ResourceBundle messages)
	{
		try
		{
			original = messages.getString(value);
			utf8Bytes = original.getBytes("UTF8");
			tempString = new String(utf8Bytes, "UTF8");
			return tempString;
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Unencoded";
		} catch (java.lang.NullPointerException n)
		{
			n.printStackTrace();
			return "Null";
		}
	}
	
	/**
	<p>This method will encode any string using a preset encoding.
	*/
	public static String encodeThis(String value)
	{
		try
		{
			original = value;
			utf8Bytes = original.getBytes("UTF8");
			tempString = new String(utf8Bytes, "UTF8");
			return tempString;
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Unencoded";
		} catch (java.lang.NullPointerException n)
		{
			n.printStackTrace();
			return "Null";
		}
	}
	
	/**
	<p>This method will encode any string using a passed in encoding.
	*/
	public static String encodeThis(String value, String encoding)
	{
		try
		{
			original = value;
			utf8Bytes = original.getBytes("ISO-8859-1");
			tempString = new String(utf8Bytes, encoding);
			return tempString;
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "Unencoded";
		} catch (java.lang.NullPointerException n)
		{
			n.printStackTrace();
			return "Null";
		}
	}

	public Locale getKoreanLocale()
	{
		String language = new String("ko");
		String country = new String("KR");
		Locale current_locale = new Locale(language, country);
		return current_locale;
	}
	
	/**
	<p>This method came from the com.businessglue.excogit.Swing class.
	<p>It requires the org.xml.sax.InputSource class, which we don't
	use in this app, so this is just a reference to show us something that worked
	for us before.  In the header comments of the that class, the author wrote
	the following:
	<p> Encoding a document in MS word with the Korean(EUC) format 
	and then using the InputStreamReader(fis, "ms949") encoding works! 
	However, Xerces only accepts the 
	<?xml version="1.0" encoding="euc-kr"?> header, not ms949 
	I guess nobody told me i18n was easy.  That's ok, because it's worthwhile.
	<p>The method signature is set to void for now, but to be useful, it should 
	return the encoding, or file create, depending on what is needed.
	*/
	public void getFileEncoding(String file_name)
	{
		/*
		 try
		 {
			 FileInputStream fis = new FileInputStream(file_name);
			 InputStreamReader isr = new InputStreamReader(fis, "ms949");
		   	 Reader in = new BufferedReader(isr);
		   	 is =  new InputSource(in);
		   	 enc = is.getEncoding();
		   	 System.out.println("Encoding - " + enc);
			 int ch;
			 while ((ch = in.read()) > -1)
			 {
				 buffer.append((char)ch);
			 }
			 in.close();
		  } catch (IOException e)
		  {
			  e.printStackTrace();
		  }
		  */
	}

}
