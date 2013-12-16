package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*
*/
public class UserInfo
{

	private String encoding;
	private String root_path;
	private String user_id;
	private String subject;
	
	public UserInfo()
	{
	
	}
	
	public UserInfo(String _encoding, String _root_path, String _user_id, String _subject)
	{
		this.encoding = _encoding;
		this.root_path = _root_path;
		this.user_id = _user_id;
		this.subject = _subject;
	}

	// --------------------- encoding
	public void setEncoding(String _encoding)
	{
		this.encoding = _encoding;
	}
	
	public String getEncoding()
	{
		return encoding;
	}	
	
	// --------------------- root_path
	public void setRootPath(String _root_path)
	{
		this.root_path = _root_path;
	}
	
	public String getRootPath()
	{
		return root_path;
	}
	
	// --------------------- user_id
	public void setUserId(String _user_id)
	{
		this.user_id = _user_id;
	}
	
	public String getUserId()
	{
		return user_id;
	}
	
	// --------------------- subject
	public void setSubject(String _subject)
	{
		this.subject = _subject;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
}
