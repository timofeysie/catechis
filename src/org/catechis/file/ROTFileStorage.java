package org.catechis.file;

import java.io.File;
import java.io.Reader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import java.lang.NumberFormatException;

import org.jdom.Element;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.catechis.EncodeString;
import org.catechis.admin.JDOMFiles;
import org.catechis.admin.JDOMOptions;
import org.catechis.constants.Constants;
import org.catechis.dto.Test;
import org.catechis.dto.Word;
import org.catechis.dto.WordFilter;
import org.catechis.dto.AllStatsHistory;
import org.catechis.dto.WordTestMemory;
import org.catechis.dto.WordTestResult;

import org.catechis.gwangali.GwangaliLogi;
import org.catechis.gwangali.ROTConstants;
import org.catechis.gwangali.RateOfTesting;
import org.catechis.interfaces.ROTStorage;

/**
 *
 * @author a
 *
 */
public class ROTFileStorage extends GwangaliLogi implements ROTStorage
{

    private Document doc;
    //private String file_name;
    private EncodeString encoder;
    
    /**
     * This holds the Rate of Testing values from 0 to 3 levels.
     */
    private Vector rot;
    
    /**
     * This holds the Rate of Testing delta values from 0 to 3 levels.
     */
    private Vector rotd;
    
    private Vector log;
    

    /**
     * If the folder
     * user/subject/options.rot_options does not exists, create it
     * @param _file_name
     */
    public ROTFileStorage()
    {
        log = new Vector();
    }
    
    public void loadDocument(String file_name)
    {
        File file = new File(file_name);
        EncodeString encoder = new EncodeString();
        doc = null;
        log = new Vector();
        try
        {
            encoder = new EncodeString();
            SAXBuilder builder = new SAXBuilder();
            Reader reader = encoder.encodeReader(file);
            doc = builder.build(reader);
        } catch (org.jdom.JDOMException j)
        {
            log.add("FileEdit<init> "+j.toString());
        } catch (java.io.IOException i)
        {
            log.add("FileEdit<init> "+i.toString());
        }
    }
    

    /**
     * String type, String user_id, String subject, String current_dir
     */
    public Vector getROTVector(String type, String user_id, String subject, String current_dir)
    {
            // from LoginAction.loginUser method
            log.add("FileUserOptions.getUserOptions user_name "+user_id);
            Hashtable options = new Hashtable();
            String file_folder = createPathToROTOptionsFolder(user_id, subject, type, current_dir);
            log.add("ROTFileStorage.getROTVectorCreated "+file_folder);
            String file_name = createROTOPtionsFileName(file_folder);
            log.add("ROTFileStorage.getROTVectorCreated "+file_name);
            File test_folder = new File(file_folder);
            if (!test_folder.exists())
            {
                test_folder.mkdir();
                boolean success = createFile(file_folder);
                log.add("ROTFileStorage.getROTVectorCreated "+file_folder);
                log.add("ROTFileStorage.getROTVectorCreated exists? "+success);
                createDefaultProperties(file_name, type);
            }
            loadDocument(file_name);
            Hashtable options_hash = getOptionsHash();
            rot = getROTVector(options_hash, type);
            rotd = getROTDVector(options_hash, type);
        return rot;
    }
    
    public void createROTFolderAndFiles(String type, String user_id, String subject, String current_dir)
    {
        String file_folder = createPathToROTOptionsFolder(user_id, subject, type, current_dir);
        String file_name = createROTOPtionsFileName(file_folder);
        File test_folder = new File(file_folder);
        test_folder.mkdir();
        boolean success = createFile(file_folder);
        //System.err.println("ROTFileStorage.getROTVectorCreated "+file_folder);
        //System.err.println("ROTFileStorage.getROTVectorCreated exists? "+success);
        createDefaultProperties(file_name, type);
    }
    
    public Vector getROTDVector()
    {
        return this.rotd;
    }
    
    /**
     * Create an options folder if one doesn't exists (for backwards user compatibility, and then create the path to the
     * options folder.  If that doesn't exists then the method calling this method is responsible for creating it.
     * @param user_id
     * @param subject
     * @param type
     * @param current_dir
     * @return
     */
    public String createPathToROTOptionsFolder(String user_id, String subject, String type, String current_dir)
    {
        String user_folder = (current_dir+File.separator+"files"+File.separator+user_id);
        String opts_folder_path = new String(user_folder+File.separator+subject
                +File.separator+Constants.OPTIONS);
        File options_folder = new File(opts_folder_path);
        if (!options_folder.exists())
        {
            options_folder.mkdir();
            //System.err.println("ROTFileStorage.createPathToROTOptionsFolder opts_folder_path "+opts_folder_path);
            //System.err.println("ROTFileStorage.createPathToROTOptionsFolder exists? "+options_folder.exists());
        }
        String file_folder = new String(opts_folder_path+File.separator+ROTConstants.ROT_OPTIONS+"_"+type);
        return file_folder;
    }
    
    public String createROTOPtionsFileName(String folder_path)
    {
        String file = (folder_path+File.separator+ROTConstants.ROT_OPTIONS+".xml");
        return file;
    }
    
    /**
     * Return true is the time between the wltd and now is shorter that the ROT value for this level
     * @return
     */
    public boolean checkForUpdate(String wltd_file_name, String level)
    {
        
        return false;
    }
    
    /**
     * Get the default hashtable and create the xml file.
     * @param file_name
     */
    public void createDefaultProperties(String file_name, String type)
    {
            JDOMFiles jf = new JDOMFiles(file_name);
            Hashtable options_values = createDefaultVector(type);
            jf.createOptions(options_values);
            jf.writeDocument(file_name);
    }
    
    /**
     * Create the default values for the ROT and ROTD vectors, and store them in the xml file.
     * Times are based on these millisecond values.
     * seconds- 10000
     * minutes- 60000
     * hours  - 3600000
     * days   - 86400000
     */
    public Hashtable createDefaultVector(String type)
    {
        Hashtable options_values = new Hashtable();
        //Initial values:
        // level    ROT       ROTD
        // 0          1 hour    2 minutes
        // 1          1 day     1 hour
        // 2          2 days    2 hours
        // 3          5 days    5 hours
        String two_minutes = Long.toString((ROTConstants.MINUTE*2));
        String one_hour = ""+ROTConstants.HOUR;    
        String two_hours = ""+(ROTConstants.HOUR*2);
        String five_hours = ""+(ROTConstants.HOUR*5);
        String one_day = ""+ROTConstants.DAY;
        String two_days = ""+(ROTConstants.DAY*2);
        String five_days = ""+(ROTConstants.DAY*5);
        options_values.put(type+"_rate_of_test_level_0", one_hour);
        options_values.put(type+"_rate_of_test_level_1", one_day);
        options_values.put(type+"_rate_of_test_level_2", two_days);
        options_values.put(type+"_rate_of_test_level_3", five_days);
        options_values.put(type+"_rate_of_test_delta_level_0", two_minutes);
        options_values.put(type+"_rate_of_test_delta_level_1", one_hour);
        options_values.put(type+"_rate_of_test_delta_level_2", two_hours);
        options_values.put(type+"_rate_of_test_delta_level_3", five_hours);
        return options_values;
        }
    
        public Hashtable getOptionsHash()
        {
            log.add("JDOMOptions.getOptionsHash");
            Hashtable options = new Hashtable();
            try
            {
                Element root = doc.getRootElement();
                List option_list = root.getChildren("option");
                int size = option_list.size();
                log.add("size "+size);
                int i = 0;
                while (i<size)
                {
                    Element e = (Element)option_list.get(i);
                    String name = e.getChildText("name");
                    String value  = e.getChildText("value");
                    log.add(name+" "+value);
                    options.put(name, value);
                    i++;
                }
            } catch (java.lang.NullPointerException npe)
            {
                log.add("JDOMOptions.getOptionsHash: npe");
                log.add(npe.toString());
            }
            return options;
        }
        
        public Vector getROTVector(Hashtable options_hash, String type)
        {
            Vector rot_vector = new Vector();
            rot_vector.add((String)options_hash.get(type+"_rate_of_test_level_0"));
            rot_vector.add((String)options_hash.get(type+"_rate_of_test_level_1"));
            rot_vector.add((String)options_hash.get(type+"_rate_of_test_level_2"));
            rot_vector.add((String)options_hash.get(type+"_rate_of_test_level_3"));
            return rot_vector;
        }
        
        public Vector getROTDVector(Hashtable options_hash, String type)
        {
            Vector rotd_vector = new Vector();
            rotd_vector.add((String)options_hash.get(type+"_rate_of_test_delta_level_0"));
            rotd_vector.add((String)options_hash.get(type+"_rate_of_test_delta_level_1"));
            rotd_vector.add((String)options_hash.get(type+"_rate_of_test_delta_level_2"));
            rotd_vector.add((String)options_hash.get(type+"_rate_of_test_delta_level_3"));
            return rotd_vector;
        }
        
        public Hashtable getROTOptionsHash(String type, String user_id, String subject, String current_dir)
        {
            Hashtable options = new Hashtable();
            String file_folder = createPathToROTOptionsFolder(user_id, subject, type, current_dir);
            String file_name = createROTOPtionsFileName(file_folder);
            File test_folder = new File(file_folder);
            if (!test_folder.exists())
            {
                test_folder.mkdir();
                boolean success = createFile(file_folder);
                //System.err.println("ROTFileStorage.getROTVectorCreated "+file_folder);
                //System.err.println("ROTFileStorage.getROTVectorCreated exists? "+success);
                createDefaultProperties(file_name, type);
            }
            loadDocument(file_name);
            Hashtable options_hash = getOptionsHash();
            return options_hash;
        }
        
        /**
         * Assemble the name option_name+"_"+
         * @param option_name either ROTCOnstants.ROT or ROTConstants.ROTD
         * @param rot_level
         * @param grade
         * @param user_id
         * @param subject
         */
        public void editROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir)
        {
            String search_name = assembleOptionName(type, option_name, rot_level);
            String folder_path = createPathToROTOptionsFolder(user_id, subject, type, current_dir);
            String file_path = createROTOPtionsFileName(folder_path);
            loadDocument(file_path);
            log.add("ROTFileStorage.editROT "+file_path);
            try
            {
                Element root = doc.getRootElement();
                List option_list = root.getChildren("option");
                int size = option_list.size();
                log.add("size "+size);
                int i = 0;
                while (i<size)
                {
                    Element e = (Element)option_list.get(i);
                    String name = e.getChildText("name");
                    log.add("ROTFileStorage.editROT found "+name+" looking for "+search_name);
                    if (name.equals(search_name))
                    {
                        String value  = e.getChildText("value");
                        log.add("ROTFileStorage.editROT type "+type+" rot_level "+rot_level+" grade "+grade);
                        RateOfTesting rot = new RateOfTesting();
                        String new_value = rot.getNewROTValue(type, rot_level, grade, user_id, subject, current_dir);
                        log.add("ROTFileStorage.editROT changing "+value+" to "+new_value);
                        Element value_element  = e.getChild("value");
                        value_element.setText(new_value);
                        break;
                    }
                    i++;
                }
                writeDocument(file_path);
            } catch (java.lang.NullPointerException npe)
            {
                log.add("ROTFileStorage.editROT: npe");
                npe.printStackTrace();
            }
        }
        
        /**
         * Similar to the edit method, but here we increment twice for a passed test,
         * or decrement twice for a failed test to reverse a previous test which would have had
         * the opposite grade that is passed in here.  RateOfTesting will reverse the grade for us.
         * @param option_name either ROTCOnstants.ROT or ROTConstants.ROTD
         * @param rot_level
         * @param grade
         * @param user_id
         * @param subject
         */
        public void reverseEditROT(String type, String option_name, String rot_level, String grade, String user_id, String subject, String current_dir)
        {
            String search_name = assembleOptionName(type, option_name, rot_level);
            String folder_path = createPathToROTOptionsFolder(user_id, subject, type, current_dir);
            String file_path = createROTOPtionsFileName(folder_path);
            loadDocument(file_path);
            log.add("ROTFileStorage.editROT "+file_path);
            try
            {
                Element root = doc.getRootElement();
                List option_list = root.getChildren("option");
                int size = option_list.size();
                log.add("size "+size);
                int i = 0;
                while (i<size)
                {
                    Element e = (Element)option_list.get(i);
                    String name = e.getChildText("name");
                    log.add("ROTFileStorage.editROT found "+name+" looking for "+search_name);
                    if (name.equals(search_name))
                    {
                        String value  = e.getChildText("value");
                        log.add("ROTFileStorage.editROT type "+type+" rot_level "+rot_level+" grade "+grade);
                        RateOfTesting rot = new RateOfTesting();
                        String new_value = rot.getReverseROTValue(type, rot_level, grade, user_id, subject, current_dir);
                        log.add("ROTFileStorage.editROT changing "+value+" to "+new_value);
                        Element value_element  = e.getChild("value");
                        value_element.setText(new_value);
                        break;
                    }
                    i++;
                }
                writeDocument(file_path);
            } catch (java.lang.NullPointerException npe)
            {
                log.add("ROTFileStorage.editROT: npe");
                npe.printStackTrace();
            }
        }
        
        /**
         * Assemble the name option_name+"_"+rot_level
         * @param option_name is either ROTCOnstants.ROT or ROTConstants.ROTD
         * @param rot_level level 0-3
         * @return
         */
        private String assembleOptionName(String type, String option_name, String rot_level)
        {
            String result = null;
            if (option_name.equals(ROTConstants.ROT))
            {
                result = type+"_rate_of_test_level_"+rot_level;
            } else if (option_name.equals(ROTConstants.ROT))
            {
                result = type+"_rate_of_test_delta_level_"+rot_level;
            }
            return result;
        }
        
        
        
        public void addUserOption(String option, String init_value)
        {
            Element root = doc.getRootElement();
            Element new_option = new Element("option");
            Element name = new Element("name");
            Element value = new Element("value");
            name.addContent(option);
            value.addContent(init_value);
            new_option.addContent(name);
            new_option.addContent(value);
            root.addContent(new_option);
        }
        
        /**
        *This creates a new document for a blank file which should already exist
        * with a root element "options".
        */
        public boolean createFile(String file_name)
        {
            Element root = new Element(Constants.OPTIONS);
            doc = new Document(root);
            writeDocument(file_name);
            File file = new File(file_name);
            return file.exists();
        }
        
        /**
        *<p>Write document with no encoding.</p>
        */
        public void writeDocument(String file_name)
        {
            try
            {
                XMLOutputter outputter = new XMLOutputter("  ", false);
                FileWriter writer = new FileWriter(file_name);
                outputter.output(doc, writer);
                writer.close();
            } catch (java.io.FileNotFoundException fnfe)
            {
            	log.add("ROTFileStoraage.writeDocument: fucked up man "+file_name);
            } catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            //context.log("CreateJDOMList.writeDocument: 1 "+file_name);
        }
        
        public Vector getLog()
        {
            return log;
        }

}
