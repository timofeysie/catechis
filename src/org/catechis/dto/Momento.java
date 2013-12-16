package org.catechis.dto;
/*
 Copyright 2008 Timothy Curchod Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
*/
/**
*This is the object that keeps track of the application state
*/
public class Momento
{
	// data kept on xml file
	
	/**Name of the action used in the web app*/
	private String action_name;
	/**Time the action was initiated.*/
	private String action_time;
	/**This is only meaningful in the action.  For example, an integrated test result action
	* uses this id to get the wntd object stored in a file, which has a link to the wltd so it can
	* erase those files and create new ones.*/
	private String action_id;
	/**Extra info, such as the test type, or whatever the action chooses to use this info for...*/
	private String action_type;
	
	public Momento()
	{
	}
	
	/**
	*All Mod Cons...
	*/
	public Momento(String _action_name, String _action_time, String _action_id, String _action_type)
	{
		action_name = _action_name;
		action_time = _action_time;
		action_id   = _action_id;
		action_type = _action_type;
	}
	
	//---------------------------------------------------------------------- action_name
	public void setActionName(String _action_name)
	{
		action_name = _action_name;
	}
	
	public String getActionName()
	{
		return action_name;
	}
	
	//---------------------------------------------------------------------- action_time
	public void setActionTime(String _action_time)
	{
		action_time = _action_time;
	}
	
	public String getActionTime()
	{
		return action_time;
	}

	//---------------------------------------------------------------------- action_id
	public void setActionId(String _action_id)
	{
		action_id = _action_id;
	}
	
	public String getActionId()
	{
		return action_id;
	}	
	
	//---------------------------------------------------------------------- action_type
	public void setActionType(String _action_type)
	{
		action_type = _action_type;
	}
	
	public String getActionType()
	{
		return action_type;
	}	

}
