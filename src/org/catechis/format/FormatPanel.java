package org.catechis.format;

import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
//import java.awt.geom.Rectangle2D;
import org.catechis.dto.Word;

/**
*This object has to calculate the widths of vectors to find out if word list string
*will get wrapped in a td table cell in the weekly_list_format.jsp.
*<p>These are the lists needed to format in the jsp.
*<p>First we have two lists which usually represent words objects at level 0 first for reading tests,
* then for writing tests
<p>Vector r_all_words
<p>Vector w_all_words
*<p>Next are the missed words lists which repeats text-definition pairs for words with a level
* higher than 0 which the user has failed in a recent test.and usually called missed words.
<p>Hashtable r_words_defs
<p>Hashtable w_words_defs
*<p>We will create similar result objects to contain the widths in pixels of the text-def pairs.
*<p>The Vectors will contain blank word objects with the text-def pairs replaced by widths.
*<p>Vector r_all_words_widths;
*<p>Vector w_all_words_widths;
*<p>Since the Hashtables are accessed using the text as a key, wel will leave the text the same
* and replace the def with a blank word object that has the text-def members replaced with the widths.
*<p>Hashtable r_words_defs_widths;
*<p>Hashtable w_words_defs_widths;
*<p>If FontMetrics doesn't work, try this:
*<p>//Rectangle2D r2d = fm.getStringBounds(str, g);
*<p>//double r2d_width = r2d.getWidth();
*/
public class FormatPanel extends JFrame
{

	private Font font;
	private String str;
	private Vector strings;
	private Vector vect_widths;
	private Hashtable hash_widths;
	
	private Vector r_all_words;
	private Vector w_all_words;
	private Hashtable r_words_defs;
	private Hashtable w_words_defs;
	
	private Vector r_all_words_widths;
	private Vector w_all_words_widths;
	private Hashtable r_words_defs_widths;
	private Hashtable w_words_defs_widths;
	
	/**
	*THis is the max number of words to put into each table so that
	* page breaks and fold breaks will all be aligned.
	*/
	private int table_format;
	
	private Vector r_increments;
	private Vector w_increments;
	
	/*Logging */
	Vector log;
	
	public FormatPanel()
	{
		super();
		log = new Vector();
		table_format = 140;
	}
	
	public FormatPanel(int t_f)
	{
		super();
		log = new Vector();
		table_format = t_f;
	}
	
	/**
	*Welcome to the headless boilerplate which calculates the amount of pixels
	*  a string will occupy given a particular font. You can then divide that
	* by the number of pixels in a cell before it wraps and get a reminder
	* which represents the number of lines that the phrase will represent in the
	* table cell, so for example if you print out of the page will have four equal tables.
	*/
	public void paint(Graphics g)
	{
		//super.paint(g); 
		System.out.println("FormatPanel.paint()");
		//String str = "testing is good for the soul"; 
		FontMetrics fm  = g.getFontMetrics();
		calculateVectorWidths("reading", fm);
		calculateVectorWidths("writing", fm);
		font = g.getFont();
		String font_name = font.getName();
		//System.out.println("length = "+width);
		//System.out.println("r2d length = "+r2d_width);
		//System.out.println("String = "+str);
		log.add("font = "+font_name);                                             
	}
	
	public void setupForAllWidths(Vector _r_all_words, Vector _w_all_words,
					Hashtable _r_words_defs, Hashtable _w_words_defs)
	{
		r_all_words = _r_all_words;
		w_all_words = _w_all_words;
		r_words_defs = _r_words_defs;
		w_words_defs = _w_words_defs;
		r_all_words_widths = new Vector();
		w_all_words_widths = new Vector();
		r_words_defs_widths = new Hashtable();
		w_words_defs_widths = new Hashtable();
		
		r_increments = new Vector();
		w_increments = new Vector();
	}
	
	/**This is a basic method to set the font, which should be the same as the
	* browser which is going to display the text.
	*/
	public void setFont(Font _font)
	{
		font = _font;
	}
	
	public void setVectorStrings(Vector _strings)
	{
		strings = _strings;
	}
	
	public Hashtable getHashtableWidths()
	{
		return hash_widths;
	}
	
	public Vector getVectorWidths(String type)
	{
		if (type.equals("reading"))
		{
			return r_all_words_widths;
		} else
		{
			return w_all_words_widths;
		}
	}
	
	private void calculateVectorWidths(String type, FontMetrics fm)
	{
		System.out.println("FormatPanel.alculateVectorWidths() type "+type);
		Vector word_objs = new Vector();
		if (type.equals("reading"))
		{
			word_objs = r_all_words;
		} else
		{
			word_objs = w_all_words;
		}
		int size = word_objs.size();
		
		System.out.println("word objs "+size);
		
		int i = 0;
		while (i<size)
		{
			Word word = (Word)word_objs.get(i);
			String text =  word.getText();
			String def = word.getDefinition();
			int text_w = fm.stringWidth(text);
			int def_w = fm.	stringWidth(def);
			int inc = getIncrement(text_w, def_w);
			//log.add(i+" "+text+" "+text_w+" - "+def+" "+def_w+" = "+inc); 
			word = new Word();
			word.setText(Integer.toString(text_w));
			word.setDefinition(Integer.toString(def_w));
			if (type.equals("reading"))
			{
				r_all_words_widths.add(word);	// old
				r_increments.add(Integer.toString(inc));
				log.add("-"+inc+"-reading-"+i+"-added-"+text+"-"+text_w+"---"); 
				
			} else
			{
				w_all_words_widths.add(word);
				w_increments.add(Integer.toString(inc));
				log.add(inc+"-writing-"+i+"-added-"+text+"-"+text_w+"---"); 
			}
			i++;
		}
	}
	
	private int getIncrement(int r_pixels, int w_pixels)
	{
		int pixels = r_pixels;
		if (w_pixels>r_pixels)
		{
			pixels = w_pixels;
		}
		float f_increment = pixels / table_format;
		Float f = new Float(f_increment);
		int increment = f.intValue();
		return increment;
	}
	
	public Vector getReadingIncrements()
	{
		return r_increments;
	}
	
	public Vector getWritingIncrements()
	{
		return w_increments;
	}
	
	public Vector getLog()
	{
		return log;
	}

}
