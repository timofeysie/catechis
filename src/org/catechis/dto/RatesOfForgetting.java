package org.catechis.dto;
/*
 Copyright 2006 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
import java.util.Vector;

/**
* This _bean_ has do do more than most.  The rate of forgetting information requires
* more inside its methods.
*<p>There are reading, writing, and eventually speaking and listening types.
*<p>There are the number of allowed levels: now 0-3 (these should really be
* from the users options file)
*<p>Then the rates are stored as milliseconds.
*/
public class RatesOfForgetting
{
	private Vector reading_rate_of_forgetting;
	private Vector writing_rate_of_forgetting;

	public RatesOfForgetting()
	{}
	
	public void setReadingRateOfForgetting(Vector _reading_rate_of_forgetting)
	{
		reading_rate_of_forgetting = _reading_rate_of_forgetting;
	}

	public Vector getReadingRateOfForgetting()
	{
		return reading_rate_of_forgetting;
	}
	
	public void setWritingRateOfForgetting(Vector _writing_rate_of_forgetting)
	{
		writing_rate_of_forgetting = _writing_rate_of_forgetting;
	}

	public Vector getWritingRateOfForgetting()
	{
		return writing_rate_of_forgetting;
	}
	
	public void setSpecificRateOfForgetting(int level, String type, String _specific_rate_of_forgetting)
	{
		if (type.equals("reading"))
		{
			reading_rate_of_forgetting.set(level, _specific_rate_of_forgetting);
		} else if (type.equals("writing"))
		{
			writing_rate_of_forgetting.set(level, _specific_rate_of_forgetting);
		}
	}
	
	public String getSpecificRateOfForgetting(int level, String type)
	{
		String specific_rate_of_forgetting = new String();
		if (type.equals("reading"))
		{
			specific_rate_of_forgetting = (String)reading_rate_of_forgetting.get(level);
		} else if (type.equals("writing"))
		{
			specific_rate_of_forgetting = (String)writing_rate_of_forgetting.get(level);
		}
		return specific_rate_of_forgetting;
	}


}
