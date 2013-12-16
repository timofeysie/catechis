package org.catechis.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.catechis.Domartin;
import org.catechis.EncodeString;
import org.catechis.Transformer;
import org.catechis.admin.JDOMFiles;
import org.catechis.constants.Constants;
import org.catechis.dto.SimpleWord;
import org.catechis.dto.WeeklyReport;
import org.catechis.dto.Word;
import org.catechis.dto.WordCategory;
import org.catechis.gwangali.GwangaliLogi;
import org.catechis.interfaces.WordCategoriesManager;
import org.catechis.lists.Sarray;
import org.catechis.lists.Sortable;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class FileWordCategoriesManager  extends GwangaliLogi implements WordCategoriesManager
{

	public FileWordCategoriesManager()
	{
		log = new Vector();
	}
	
	/**
	 * This method creates a new category file with the name contained in the WordCategory file.
	 * If other members are set, they are also recorded in the file.  Otherwise, the id and creation time
	 * are created in this method and a blank word_id tag is created.
	 * We also create an entry in the categories.xml index file outside the categories folder.
	 * If the name in the WordCategory already exists, then we return -1.
	 */
	public long addWordCategory(WordCategory word_category, String user_id, String subject, 
			String current_dir, String encoding)
	{
		boolean exists = checkForUniqueName(word_category, user_id, subject, current_dir);
		if (!exists)
		{
			return -1;
		}
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String category_path = subject_path+File.separator
			+Constants.CATEGORIES;
		long id = getId(word_category);
		String full_path = category_path+File.separator+id+".xml";
		File file = new File(full_path);
		try
		{
			file.createNewFile();
		} catch (java.io.IOException ioe)
		{
			return -1;
		}
		log.add("FileWordCategoriesManager.addWordCategory: category_path "+category_path);
		Element root =  createWordCategoryElement(word_category);
		String creation_time = root.getChildText("creation_time");
		Document doc = new Document(root);
		writeDocument(full_path, doc, encoding);
		word_category.setCreationTime(Long.parseLong(creation_time));
		createCategoriesIndexFileEntry(word_category, subject_path, encoding);
		return word_category.getId();
	}
	
	private boolean checkForUniqueName(WordCategory word_category, String user_id, String subject, 
			String current_dir)
	{
		boolean exists = false;
		Vector word_cats = getAllExclusiveCategories(user_id, subject, current_dir);
		TreeSet set = new TreeSet();
		for (int i = 0;  i < word_cats.size(); i++)
		{
			WordCategory word_cat = (WordCategory)word_cats.get(i);
			set.add(word_cat.getName());
		}
		exists = set.add(word_category.getName());
		return exists;
	}
	
	private long getId(WordCategory word_category)
	{
		long id = word_category.getId();
		if (id==0)
		{
			id = Domartin.getNewID();
			word_category.setId(id);
			log.add("FileWordCategoriesManager.addWordCategory: created new id "+id);
		} else
		{
			log.add("FileWordCategoriesManager.addWordCategory: used id passed in "+id);
		}
		return id;
	}
	
	/**
	 *Create an entry in the user_id/subject/categories.xml file with this structure:
	 * <categories>
	 * 	<category>
	 * 		<id/>
	 * 		<name/>
	 * 		<total_words/>
	 * 	<category>
	 *  ...
	 * </categories>
	 * @param word_category
	 * @return
	 */
	private void createCategoriesIndexFileEntry(WordCategory word_category, String subject_path, String encoding)
	{
		String file_path = subject_path+File.separator
			+Constants.CATEGORIES+".xml";
		log.add("FileWordCategoriesManager.createCategoriesIndexFileEntry: path "+file_path);
		Document doc = loadFile(file_path);
		Element root = doc.getRootElement();
		Element category =  new Element("category");
		Element name =  new Element("name");
		Element id =  new Element("id");
		Element total_words =  new Element("total_words");
		Element creation_time =  new Element("creation_time");
		Element category_type =  new Element("category_type");
		id.addContent(word_category.getId()+"");
		name.addContent(word_category.getName());
		total_words.addContent(word_category.calculateTotalWords()+"");
		creation_time.addContent(word_category.getCreationTime()+"");
		category_type.addContent(word_category.getCategoryType());
		category.addContent(id);
		category.addContent(name);
		category.addContent(total_words);
		category.addContent(creation_time);
		category.addContent(category_type);
		root.addContent(category);
		writeDocument(file_path, doc, encoding);
	}
	
	public Document loadFile(String file_name)
	{
		File file_f = new File(file_name);
		Document doc = null;
		try
		{
			EncodeString encoder = new EncodeString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = encoder.encodeReader(file_f);
			doc = builder.build(reader);
		} catch (org.jdom.JDOMException j)
		{
			log.add(j.toString());
		} catch (java.io.FileNotFoundException fnfe)
		{
			log.add(fnfe.toString());
		} catch (java.io.IOException i)
		{
			log.add(i.toString());
		}
		return doc;
	}
	
	/**
	*Create  Word Category Element.  If there is not time set, a new time is created.
	*If category words contains a vector of SimpleWord objects, those are added,
	*otherwise a blank category_words element is created.
	*This method will create words with the word_path as the same as the categoryName.
	*<word_category>
	*	<name/>
	*	<id/> ** the file name so we dont save it in the file (redundante, duh!)
	*	<category_type>exclusive</category_type>
	*	<creation_time/>
	*	<category_words>
	*		<simple_word>
	*			<word_id>0000000000002</word_id>
	*			<word_path>random.xml</word_path>
	*			<text/>
	*			<definition/>
	*		</simple_word>
	*	</category_words>
	*</word_category>
	*/
	private Element createWordCategoryElement(WordCategory word_category)
	{
		Vector words = word_category.getCategoryWords();
		//log.add("createWordCategoryElement words "+words.size());
		Element root =  new Element("word_category");
		Element name =  new Element("name");
		Element category_type =  new Element("category_type");
		Element creation_time =  new Element("creation_time");
		String word_file_name = word_category.getName(); 
		name.addContent(word_file_name);
		category_type.addContent(word_category.getCategoryType());
		long time = word_category.getCreationTime();
		if (time==0)
		{
			time = new Date().getTime();
			word_category.setCreationTime(time);
			log.add(".FileWordCategoriesManager.createWordCategoryElement: set new creation time "+time);
		}
		creation_time.addContent(time+"");
		// add words if any exist
		Element category_words = bindCategoryWords(word_category.getCategoryWords(), word_file_name);
		root.addContent(name);
		root.addContent(category_type);
		root.addContent(creation_time);
		root.addContent(category_words);
		return root;
	}
	
	private Element createWordCategoryElementForExistingCategory(WordCategory word_category)
	{
		Vector words = word_category.getCategoryWords();
		log.add("createWordCategoryElement words "+words.size());
		Element root =  new Element("word_category");
		Element name =  new Element("name");
		Element category_type =  new Element("category_type");
		Element creation_time =  new Element("creation_time");
		String word_file_name = word_category.getName(); 
		word_category.getTotalWordCount();
		name.addContent(word_file_name);
		category_type.addContent(word_category.getCategoryType());
		creation_time.addContent(word_category.getCreationTime()+"");
		Element category_words = bindCategoryWords(word_category.getCategoryWords(), word_file_name);
		root.addContent(name);
		root.addContent(category_type);
		root.addContent(creation_time);
		root.addContent(category_words);
		return root;
	}
	
	/**
	 * Create an element of each word object.  If the word_file_name argument is null, the word category will be used.
 	*	<category_words>
	*		<simple_word>
	*			<word_id>0000000000002</word_id>
	*			<word_path>random.xml</word_path>
	*			<text/>
	*			<definition/>
	*		</simple_word>
	*	</category_words>
	*
	 * @param category_words
	 * @return
	 */
	private Element bindCategoryWords(Vector category_words, String word_file_name)
	{
		Element category_words_element = new Element("category_words");
		log.add("FileWordCategoriesManager.bindCategoryWords: words "+category_words.size());
		for (int i = 0; i < category_words.size(); i++)
		{
			SimpleWord simple_word = (SimpleWord)category_words.get(i);
			Element simple_word_element = new Element("simple_word");
			Element word_id = new Element("word_id");
			Element word_path = new Element("word_path");
			Element text = new Element("text");
			Element definition = new Element("definition");
			word_id.addContent(simple_word.getWordId()+"");
			word_path.addContent(simple_word.getWordPath());
			text.addContent(simple_word.getText());
			definition.addContent(simple_word.getDefinition());
			simple_word_element.addContent(word_id);
			simple_word_element.addContent(word_path);
			simple_word_element.addContent(text);
			simple_word_element.addContent(definition);
			category_words_element.addContent(simple_word_element);
			//log.add("added "+simple_word.getText()+" "+simple_word.getDefinition());
		}
		return category_words_element;
	}
	
	/**
	 * Append the words to an existing words element.
	 * @param category_words
	 * @param word_file_name
	 * @param category_words_element
	 * @return
	 */
	private void bindNewCategoryWords(Vector category_words, String word_file_name, String file_path, String encoding)
	{
		Document doc = loadFile(file_path);
		Element root = doc.getRootElement();
		Element category_words_element = root.getChild("category_words");
		List list = root.getChildren("category_words");
		int previous_words_size = list.size();
		int new_words_size = category_words.size();
		int i = 0;
		int size = new_words_size + previous_words_size;
		log.add("FileWordCategoriesManager.bindCategoryWords: old words "+previous_words_size+" new words size "+size);
		while (i<new_words_size)
		{
			SimpleWord simple_word = null;
			try
			{
				simple_word = (SimpleWord)category_words.get(i);
			} catch (java.lang.ClassCastException cce)
			{
				log.add("convert word to simple word");
				simple_word = convertWordToSimpleWord((Word)category_words.get(i), word_file_name);
			}
			Element simple_word_element = new Element("simple_word");
			Element word_id = new Element("word_id");
			Element word_path = new Element("word_path");
			Element text = new Element("text");
			Element definition = new Element("definition");
			word_id.addContent(simple_word.getWordId()+"");
			word_path.addContent(simple_word.getWordPath());
			text.addContent(simple_word.getText());
			definition.addContent(simple_word.getDefinition());
			simple_word_element.addContent(word_id);
			simple_word_element.addContent(word_path);
			simple_word_element.addContent(text);
			simple_word_element.addContent(definition);
			category_words_element.addContent(simple_word_element);
			//list.add(simple_word_element);
			//log.add("added "+simple_word.getText());
			i++;
		}	
		//Element number_of_words = root.getChild("number_of_words");
		//number_of_words.setText(Integer.toString(size));
		writeDocument(file_path, doc, encoding);
	}
	
	/**
	 * Convert Word to SimpleWord.  
	 * @param word
	 * @param word_file_name If the name is null, the the name will come from the word.getCategory method.
	 * @return
	 */
	private SimpleWord convertWordToSimpleWord(Word word, String word_file_name)
	{
		SimpleWord simple_word = new SimpleWord();
		simple_word.setWordId(word.getId());
		if (word_file_name == null)
		{
			word_file_name = word.getCategory();
		}
		simple_word.setWordPath(word_file_name);
		simple_word.setText(word.getText());
		simple_word.setDefinition(word.getDefinition());
		return simple_word;
	}
	
	/**
	 * Load a vector of WordCategory objects with no words.
	 */
	public Vector getWordCategoryNames(String user_id, String subject, String current_dir)
	{
		String categories_file_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject+File.separator
			+Constants.CATEGORIES+".xml";
		log.add("FileWordCategoriesManager.getWordCategoryNames");
		log.add("path "+categories_file_path);
		Document doc = loadFile(categories_file_path);
		Element root = doc.getRootElement();
		Vector cat_names = unbindCategories(root);
		//log.add("cat_names unbound "+cat_names.size());
		return cat_names;
	}
	
	/**
	 *  <categories>
	 * 		<category>
	 * 			<id/>
	 * 			<name/>
	 * 			<total_words/>
	 * 			<creation_time>
	 * 		<category>
	 *  	...
	 *  </categories>
	 * @param root
	 * @return
	 */
	private Vector unbindCategories(Element root)
	{
		Vector cat_names = new Vector();
		List categories = root.getChildren();
		int i = 0;
		int size = categories.size();
		while (i<size)
		{
			WordCategory word_cat = new WordCategory();
			Element e = (Element)categories.get(i);
			String name = e.getChildText("name");
			word_cat.setName(name);
			word_cat.setId(Long.parseLong(e.getChildText("id")));
			word_cat.setCategoryType(e.getChildText("category_type"));
			word_cat.setCreationTime(Long.parseLong(e.getChildText("creation_time")));
			word_cat.setTotalWordCount(Integer.parseInt(e.getChildText("total_words")));
			cat_names.add(word_cat);
			//log.add("unbindCategories: unbound "+name);
			i++;
		}
		return cat_names;
	}
	
	/**
	 * Load a file with a single category populated with SimpleWord objects.
	 * The total word count will not be set, as the number of words returned has the caount
	 * if you call getCategoryWords and check the size.
	<word_category>
	<name>Test</name>
	<category_type>exclusive</category_type>
	<creation_time>1337509597490</creation_time>
	<category_words>
		<simple_word>
			<word_id>1</word_id>
			<word_path>test.xml</word_path>
			<text>text1</text>
			<definition>def1</definition>
		</simple_word>
		<simple_word>
			<word_id>2</word_id>
			<word_path>test.xml</word_path>
			<text>text2</text>
			<definition>def2</definition>
		</simple_word>
	</category_words>
	</word_category>
	 */
	public WordCategory getWordCategory(long category_id, String user_id, String subject, String current_dir)
	{
		WordCategory word_cat = new WordCategory();
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String file_path = subject_path+File.separator
			+Constants.CATEGORIES+File.separator
			+category_id+".xml";
		boolean exists = new File(file_path).exists();
		log.add("getWordCategory: category exists? "+exists);
		Document doc = loadFile(file_path);
		try
		{
			Element root = doc.getRootElement();
			word_cat = unbindWordCategory(root, category_id);
		} catch (java.lang.NullPointerException npe)
		{
			log.add("getWordCategory: subject_path "+subject_path);
			log.add("getWordCategory: file_path "+file_path);
			log.add(npe.toString());
		}
		return word_cat;
	}
	
	/**
	 * Takes the string name of a category and finds the id.
	 * @param category_name
	 * @param user_id
	 * @param subject
	 * @param current_dir
	 * @return
	 */
	public long getCategoryId(String category_name, String user_id, String subject, String current_dir)
	{
		WordCategory word_cat = new WordCategory();
		Vector word_cats = getWordCategoryNames(user_id, subject, current_dir);
		log.add("getCategoryId.word_cats "+word_cats.size());
		long cat_id;
		for (int i=0;i<word_cats.size();i++)
		{
			word_cat = (WordCategory)word_cats.get(i);
			String cat_name = word_cat.getName();
			if (cat_name.equals(category_name))
			{
				cat_id = word_cat.getId();
				log.add("getCategoryId: cat "+i+" cat_name "+cat_name+" match!");
				return cat_id;
			} else
			{
				log.add("getCategoryId: cat "+i+" cat_name "+cat_name+" match!");
			}
		}
		log.add("getCategoryId: no match, return -1");
		return Long.parseLong("-1");
	}
	
	/**
	<word_category>
		<name>Test</name>
		<category_type>exclusive</category_type>
		<creation_time>1337509597490</creation_time>
		<category_words>
			<simple_word>
				<word_id>1</word_id>
				<word_path>test.xml</word_path>
				<text>text1</text>
				<definition>def1</definition>
			</simple_word>
			...
		</category_words>
	</word_category>
	 * @param e
	 * @return
	 */
	private WordCategory unbindWordCategory(Element e, long category_id)
	{
		WordCategory word_cat = new WordCategory();
		word_cat.setName(e.getChildText("name"));
		word_cat.setId(category_id);
		word_cat.setCategoryType(e.getChildText("category_type"));
		word_cat.setCreationTime(Long.parseLong(e.getChildText("creation_time")));
		Element category_words = e.getChild("category_words");
		List simple_words_element = category_words.getChildren();
		int size = simple_words_element.size();
		Vector simple_words = new Vector();
		int i = 0;
		while (i<size)
		{
			SimpleWord simple_word = new SimpleWord();
			Element word_element = (Element)simple_words_element.get(i);
			simple_word.setWordId(Long.parseLong((String)word_element.getChildText("word_id")));
			simple_word.setWordPath((String)word_element.getChildText("word_path"));
			simple_word.setText((String)word_element.getChildText("text"));
			simple_word.setDefinition((String)word_element.getChildText("definition"));
			simple_words.add(simple_word);
			//log.add("unbindWordCategory: loaded "+simple_word.getDefinition());
			i++;
		}
		word_cat.setCategoryWords(simple_words); 
		log.add(("unbindWordCategory:calculated number of words: "+word_cat.calculateTotalWords()));
		return word_cat;
	}
	
	/**
	 * Delete the entry for the category and then delete the file containing the category words.
	 */
	public boolean deleteCategory(long search_id, String user_id, String subject, String current_dir, String encoding)
	{
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String file_path = subject_path+File.separator
			+Constants.CATEGORIES+".xml";
		boolean exists = new File(file_path).exists();
		log.add(exists+" path "+file_path);
		Document doc = loadFile(file_path);
		Element root = doc.getRootElement();
		List categories = root.getChildren("category");
		int size = categories.size();
		log.add("FileWordCategoriesManager.getWordCategory: list size "+size);
		Vector words = new Vector();
		int i = 0;
		while (i<size)
		{
			Element e = (Element)categories.get(i);
			long this_id = Long.parseLong(e.getChildText("id"));
			if (this_id==search_id)
			{
				categories.remove(e);
				writeDocument(file_path, doc, encoding);
				boolean deleted = deleteCategoryFile(current_dir, search_id, user_id, subject);
				return deleted;
			}
			i++;	
		}
		return false;
	}
	
	private boolean deleteCategoryFile(String current_dir, long search_id, String user_id, String subject)
	{
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String category_path = subject_path+File.separator
			+Constants.CATEGORIES;
		String full_path = category_path+File.separator+search_id+".xml";
		log.add("FileWordCategoriesManager.deleteCategoryFile: path "+full_path);
		File file = new File(full_path);
		boolean deleted = false;
		if (file.exists())
		{
			deleted = file.delete();
			log.add("deleted file");
		}
		return deleted;
	}
	
	public Vector getAllExclusiveCategories(String user_id, String subject, String current_dir)
	{
		//log.add("FileWordCategoriesManager.getAllExclusiveCategories");
		Vector exclusive_cats = new Vector();
		Vector all_cats = getWordCategoryNames(user_id, subject, current_dir);
		//log.add("number of cats "+all_cats.size());
		for (int i = 0; i < all_cats.size();i++)
		{
			WordCategory word_cat = (WordCategory)all_cats.get(i);
			String cat_type = word_cat.getCategoryType();
			//log.add(i+" "+cat_type);
			if (cat_type.equals(Constants.EXCLUSIVE))
			{
				exclusive_cats.add(word_cat);
			}
		}
		return exclusive_cats;
	}
	
	/**
	 * The Sarray returned is a sortable Array of Category objects returned by this method.
	 * You get a category from the Sarray like this:
	 *   Sarray word_catsarray = (Sarray)session.getAttribute("categories");
     *   for (int i = 0; i < word_catsarray.size(); i++)
     *   {
     *       Sortable s = (Sortable)word_catsarray.elementAt(i);
     *       String object_key = (String)s.getKey();
     *       WordCategory word_cat = (WordCategory)s.getObject();
     *   }
	 * @param user_id
	 * @param subject
	 * @param current_dir
	 * @param category_type
	 * @return
	 */
	public Sarray getSortedCategories(String user_id, String subject, String current_dir, String category_type)
	{
		Vector exclusive_cats = new Vector();
		Vector all_cats = getWordCategoryNames(user_id, subject, current_dir);
		log.add("getSortedCategories: all_cats "+all_cats.size());
		Sarray  sarry = new Sarray();
		for (int i = 0; i < all_cats.size(); i++) 
		{
			WordCategory word_cat = (WordCategory)all_cats.get(i);
			long creation_time = word_cat.getCreationTime();
			Sortable s = new Sortable (creation_time+"" , word_cat);
            sarry.add(s);
		}
        Collections.sort(sarry);
		return sarry;
	}
	
	/**
	 * Add a Vector of Word objects to an existing category.
	 * @param user_id
	 * @param category_id
	 * @param subject
	 * @param current_dir
	 * @param words
	 * @param encoding
	 */
	public void addWordsToCategory(String user_id, long category_id, String subject, String current_dir, Vector words, String encoding)
	{
		WordCategory word_cat = getWordCategory(category_id, user_id, subject, current_dir);
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String file_path = subject_path+File.separator
			+Constants.CATEGORIES+File.separator
			+category_id+".xml";
		boolean exists = new File(file_path).exists();
		log.add("path "+file_path+" exists? "+exists);
		bindNewCategoryWords(words, word_cat.getName(), file_path, encoding);
		incrementNumberOfWords(user_id, category_id, subject, current_dir, words.size(), encoding);
		// document will be written in the bind method.
	}
	
	/**
	 * Replace the previous category_id.xml with the new one passed in,
	 * and update the word_count element in the categories.xml file.
	 * @param word_cat
	 * @param user_id
	 * @param subject
	 * @param current_dir
	 * @param encoding
	 */
	public void updateCategory(WordCategory word_cat, String user_id, String subject, String current_dir, String encoding)
	{
		String method = "";
		String subject_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject;
		String file_path = subject_path+File.separator
			+Constants.CATEGORIES+File.separator
			+word_cat.getId()+".xml";
		boolean exists = new File(file_path).exists();
		log.add("getWordCategory: category exists? "+exists);
		Document doc = loadFile(file_path);
		Element root =  createWordCategoryElementForExistingCategory(word_cat);
		doc = new Document(root);
		writeDocument(file_path, doc, encoding);
		//int new_number_of_words = word_cat.calculateTotalWords();
		int new_number_of_words = word_cat.getCategoryWords().size();
		log.add(method+" new_number_of_words "+new_number_of_words);
		replaceNumberOfWords(user_id, word_cat.getId(), subject, current_dir, new_number_of_words, encoding);
	}
	
	private WordCategory getWordCategory(long cat_id, Element root, String user_id, String subject, String current_dir)
	{
		Vector word_cats = getWordCategoryNames(user_id, subject, current_dir);
		WordCategory word_cat = null;
		for (int i = 0; i < word_cats.size(); i++)
		{
			WordCategory this_cat = (WordCategory)word_cats.get(i);
			if (this_cat.getId() == cat_id)
			{
				word_cat = this_cat;
				break;
			}
		}
		return word_cat;
	}
	
	/**
	 * LOad the document and send it to the next method which will go through the categories and add the
	 * new number of words to the existing number.
	 * @param user_id
	 * @param category_id
	 * @param subject
	 * @param current_dir
	 * @param number_of_new_words
	 * @param encoding
	 */
	private void incrementNumberOfWords(String user_id, long category_id, String subject, 
			String current_dir, int number_of_new_words, String encoding)
	{
		String categories_file_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject+File.separator
			+Constants.CATEGORIES+".xml";
		log.add("FileWordCategoriesManager.incrementNumberOfWords: "+categories_file_path);
		log.add("path "+categories_file_path);
		Document doc = loadFile(categories_file_path);
		unbindCategoriesAndUpdateWordCount(doc, category_id, number_of_new_words, categories_file_path, encoding);
	}
	
	/**
	 * LOad the document and send it to the next method which will go through the categories and 
	 * replace the number of words with the existing number.
	 * @param user_id
	 * @param category_id
	 * @param subject
	 * @param current_dir
	 * @param number_of_new_words
	 * @param encoding
	 */
	private void replaceNumberOfWords(String user_id, long category_id, String subject, 
			String current_dir, int new_number_of_words, String encoding)
	{
		String categories_file_path = current_dir+File.separator
			+"files"+File.separator
			+user_id+File.separator
			+subject+File.separator
			+Constants.CATEGORIES+".xml";
		log.add("FileWordCategoriesManager.incrementNumberOfWords: "+categories_file_path);
		log.add("path "+categories_file_path);
		Document doc = loadFile(categories_file_path);
		unbindCategoriesAndReplaceWordCount(doc, category_id, new_number_of_words, categories_file_path, encoding);
	}
	
	private void unbindCategoriesAndUpdateWordCount(Document doc, long category_id, int number_of_new_words, String file_path, String encoding)
	{
		Element root = doc.getRootElement();
		Vector cat_names = new Vector();
		List categories = root.getChildren();
		int i = 0;
		int size = categories.size();
		while (i<size)
		{
			WordCategory word_cat = new WordCategory();
			Element e = (Element)categories.get(i);
			String this_id = e.getChildText("id");
			if (this_id.equals(Long.toString(category_id)))
			{
				Element total_words = e.getChild("total_words");
				String old_count = e.getChildText("total_words");
				int new_total = Integer.parseInt(old_count)+number_of_new_words;
				total_words.setText(new_total+"");
				log.add("unbindCategories: changed "+old_count+" to "+new_total);
				writeDocument(file_path, doc, encoding);
				break;
			}
			i++;
		}
	}
	
	private void unbindCategoriesAndReplaceWordCount(Document doc, long category_id, int new_number_of_words, String file_path, String encoding)
	{
		Element root = doc.getRootElement();
		Vector cat_names = new Vector();
		List categories = root.getChildren();
		int i = 0;
		int size = categories.size();
		while (i<size)
		{
			WordCategory word_cat = new WordCategory();
			Element e = (Element)categories.get(i);
			String this_id = e.getChildText("id");
			if (this_id.equals(Long.toString(category_id)))
			{
				Element total_words = e.getChild("total_words");
				String old_count = e.getChildText("total_words");
				total_words.setText(new_number_of_words+"");
				log.add("unbindCategories: changed "+old_count+" to "+new_number_of_words);
				writeDocument(file_path, doc, encoding);
				break;
			}
			i++;
		}
	}
	
	public void writeDocument(String file_name, Document doc, String encoding)
	{
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
			//log.add("JDOMFiles.writeDocument2: IOE "+file_name);
			e.printStackTrace();
		}

	}
	
}
