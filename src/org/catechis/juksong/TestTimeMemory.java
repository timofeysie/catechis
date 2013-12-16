package org.catechis.juksong;

/**
 * This object stores time in milliseconds s representing a words
 * last and next test dates (hence wltd/wntd).  There is already an
 * object in the org.catechis.dto package with WordLastTestDate,
 * so the name of TestTimeMemory is used to differentiate this from that.
 * @author a
 *
 */
public class TestTimeMemory
{

	private String original_wltd;
	private String new_wltd;
	private String original_wntd;
	private String new_wntd;
	private String type;
	
	/** --------------------------
	 * OriginalWLTD
	 */
	public void setOriginalWLTD(String _original_wltd)
	{
		this.original_wltd = _original_wltd;
	}
	/**
	 * OriginalWLTD
	 * @return
	 */
	public String getOriginalWLTD()
	{
		return original_wltd;
	}
	// ----------------------------
	
	
	/** --------------------------
	 * NewWLTD
	 */
	public void setNewWLTD(String _new_wltd)
	{
		this.new_wltd = _new_wltd; 
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNewWLTD()
	{
		return new_wltd;
	}
	// ----------------------------
	
	
	
	/** --------------------------
	 * OriginalWNTD
	 */
	public void setOriginalWNTD(String _original_wntd)
	{
		this.original_wntd = _original_wntd; 
	}
	/**
	 * 
	 * @return
	 */
	public String getOriginalWNTD()
	{
		return original_wntd;
	}
	// ----------------------------
	
	
	
	/** --------------------------
	 * NewWNTD
	 */
	public void setNewWNTD(String _new_wntd)
	{
		this.new_wntd = _new_wntd;
	}
	
	/**
	 * NewWNTD
	 * @return
	 */
	public String getNewWNTD()
	{
		return new_wntd;
	}
	// ----------------------------
	
	
	
	/** --------------------------
	 * Type
	 */
	public void setType(String _type)
	{
		this.type = _type;
	}
	
	/**
	 *Type 
	 * @return
	 */
	public String getType()
	{
		return type;
	}
	// ----------------------------
	
}
