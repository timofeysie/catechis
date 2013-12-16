package org.catechis;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
public class Workaround
{

	public Workaround()
	{}
	
	/**
	<p>Because String file_name = request.getHeader("filename"); returns null,
	this is a dirty workaround just so I can get on with the project.
	<p>Yes I'm ignorant enough, but my requirements don't specify using the best way.
	*/
	public static String getHeader(String headers)
	{
		int equ = headers.lastIndexOf("=");
		int len = headers.length();
		String key = headers.substring(0,equ);
		String val = headers.substring(equ+1,len);
		String finished_filename = new String();
		while (val.lastIndexOf("%")!=(-1))
		{
			int per = val.lastIndexOf("%");
			String filler = val.substring(per,(per+3));
			if (filler.equals("%20"))
			{
				String first_part = val.substring(0,per);
				String last_part = val.substring(per+3);
				finished_filename = new String(first_part+" "+last_part);
			}
			val=finished_filename;
		}
		finished_filename=val;
		return finished_filename;
	}

}
