package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

import java.util.Locale;
import java.util.Hashtable;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*<p>This Internationalization utility sets the content type and locale.
*<p>The values must already exist in the session.
*<p>Additions to it would use the constructor to pass in the request and response
* so that it could be overridden for other applications use.
*/
public class I18NWebUtility
{

	public I18NWebUtility()
	{}
	
	/**
	*This method gets the content type and local from the HttpSession object
	* which can be had from the request argument/object/thingy.  The response
	* is then set with these and then nothing needs to be returned!  How bout that?
	*/
	public static void setContentTypeAndLocale(HttpServletRequest hs_request, HttpServletResponse hs_response)
	{
		HttpSession h_session = hs_request.getSession();
		String content_type = (String)h_session.getAttribute("content_type");
		Locale locale = (Locale)h_session.getAttribute("locale");
		hs_response.setLocale(locale);
		hs_response.setContentType(content_type);
	}
	
	/**
	*Old version, when we didn't pass in the user_options hash.
	*/
	public static void forceContentTypeAndLocale(HttpServletRequest hs_request, HttpServletResponse hs_response, String full_locale, String encoding)
	{
		int underscore = full_locale.lastIndexOf("_");
		String language = full_locale.substring(0,underscore);
		String country = full_locale.substring((underscore+1),full_locale.length());
		Locale locale = new Locale(language, country);
		String content_type = new String("text/html;charset="+encoding);
		hs_response.setLocale(locale);
		hs_response.setContentType(content_type);
	}
	
	/**
	* This method will construct the locale for you from information in the user_opts Hashtable,
	* and set the charset which is then set as the response content type along with the locale.
	*This didn't work for actions like RetireWordAction.  See for yourself!
	*/
	public static void forceContentTypeAndLocale(HttpServletRequest hs_request, HttpServletResponse hs_response, Hashtable user_opts)
	{
		// default values
		String language = "ko";
		String country = "KR";
		String encoding = "euc-kr";
		try
		{
			String full_locale = (String)user_opts.get("locale");
			encoding = (String)user_opts.get("encoding");
			int underscore = full_locale.lastIndexOf("_");
			language = full_locale.substring(0,underscore);
			country = full_locale.substring((underscore+1),full_locale.length());
		} catch (java.lang.NullPointerException npe)
		{
			// go with default
		}
		Locale locale = new Locale(language, country);
		String content_type = new String("text/html;charset="+encoding);
		hs_response.setLocale(locale);
		hs_response.setContentType(content_type);
	}

}
