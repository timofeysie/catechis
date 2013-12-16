/**
 *
 */
package org.catechis.gwangali;

import java.util.Hashtable;
import java.util.Vector;

import org.catechis.file.ROTFileStorage;
import org.catechis.interfaces.ROTStorage;

/**
 * @author a
 * ROTConstants has the millisecond values for seconds, minutes, hours, days.
 *
 *
 * Record rate of forgetting (ROF) and rate of remembering (ROR) and a test by test basis.  
 * This implies saving a running count of rates and the total of all those rates so that
 * the formula value 1 + value 2 + value n ... = total/n.  So we store the total and n,
 * and then for new tests, add the total time in milliseconds, increment n, make the calulation,
 * and store the final.
 * Create rate of testing (ROT) properties that will become the new exclude level test (ELT) values,
 * and can also be used in way that are as yet undecided.  
 * Retroactively they will be used to replace the ELT values and then re-create the words next test date lists (WNTD).
 * The ROT will also require a delta values (ROTD).
 * DV   1 min, 1 hour, 2 hours, 4 hours.
 * ROT 1 hour, 1 day, 2 days, 3 days,
 * Another option might be a percentage of the ROT, such as 10 %, so that the ROT values themselves will not ba able to reach a zero value, which would defeat the pupose of the application in implementing a intermittent re-inforcement algorythm.
 */
public class RateOfTesting extends GwangaliLogi
{
    
    public RateOfTesting()
    {
        super();
    }
    
    /**
     * Increment or decrement the appropriate ROT value using the appropriate ROT Delta value
     * depending on a failed or passed test grade.
     * @param type of test run (ie: reading or writing)
     * @param level of the test (ie: 0-3)
     * @param test_result (ie: pass/fail)
     */
    public void updateRateOfTesting(String type, String rot_level, String grade, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        rotfs.editROT(type, ROTConstants.ROT, rot_level, grade, user_id, subject, current_dir);
        append(rotfs.getLog());
    }
    
    /**
     * The vector returned has the values in milliseconds for the users Rate of Testing.
     * @param type
     * @param user_id
     * @param subj
     * @param current_dir
     * @return
     */
    public Vector getROTVector(String type, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        Vector rot = new Vector();
        rot = rotfs.getROTVector(type, user_id, subject, current_dir);
        append(rotfs.getLog());
        return rot;
    }
    
    /**
     * Inside this method, we must get the rot vector first, and then the rotd vector
     * becomes available.
     * @param type
     * @param user_id
     * @param subj
     * @param current_dir
     * @return
     */
    public Vector getROTDVector(String type, String user_id, String subj, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        rotfs.getROTVector(type, user_id, subj, current_dir);
        Vector rotd = rotfs.getROTDVector();
        return rotd;
    }
    
    public void cancelUpdate(String user_id, String subj, String current_dir)
    {
        
    }
    
    /**
     * Reverse whatever the update method did before.  The arguments should be the same as were passed in.
     * Basically the same as the update method, but here we reverse the score, and call the reverseUpdate method
     * which increments/decrements the rot twice so that it goes back to normal, and then is changed once more.
     * @param type
     * @param rot_level
     * @param grade
     * @param user_id
     * @param subj
     * @param current_dir
     */
    public void reverseUpdate(String type, String rot_level, String grade, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        String new_grade = "";
        if (grade.equals(ROTConstants.PASS))
        {
            new_grade = ROTConstants.FAIL;
        } else if (grade.equals(ROTConstants.FAIL))
        {
            new_grade = ROTConstants.PASS;
        }
        rotfs.reverseEditROT(type, ROTConstants.ROT, rot_level, new_grade, user_id, subject, current_dir);
        append(rotfs.getLog());
    }
    
    /**
     * THis method implements the Gwangali Method, which states that if a test is passed,
     * then the rate of testing time should be increased, or if the test is failed,
     * then the rate of testing should be decreased.
     * The rate of change is held in the rate of test delta vector,
     * which has different values according to the level.
     * @param rot_level
     * @param grade
     * @return
     */
    public String getNewROTValue(String type, String rot_level, String grade, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        Hashtable options_hash = rotfs.getROTOptionsHash(type, user_id, subject, current_dir);
        Vector rot_vector = getROTVector(options_hash, type);
        Vector rotd_vector = getROTDVector(options_hash, type);
        int lvl = Integer.parseInt(rot_level);
        String rot_string = (String)rot_vector.get(lvl);
        String rotd_string = (String)rotd_vector.get(lvl);
        int rot = Integer.parseInt(rot_string);
        int rotd = Integer.parseInt(rotd_string);
        int new_value = -1;
        if (grade.equals(ROTConstants.PASS))
        {
            new_value = rot+rotd;
        } else if (grade.equals(ROTConstants.FAIL))
        {
            new_value = rot-rotd;
        }
        return (""+new_value);
    }
    
    /**
     * THis method is the same as the getNewROTValue method, but it increments or decrements the rot value
     * twice to reverse the test score.
     * @param rot_level
     * @param grade
     * @return
     */
    public String getReverseROTValue(String type, String rot_level, String grade, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        Hashtable options_hash = rotfs.getROTOptionsHash(type, user_id, subject, current_dir);
        Vector rot_vector = getROTVector(options_hash, type);
        Vector rotd_vector = getROTDVector(options_hash, type);
        int lvl = Integer.parseInt(rot_level);
        String rot_string = (String)rot_vector.get(lvl);
        String rotd_string = (String)rotd_vector.get(lvl);
        int rot = Integer.parseInt(rot_string);
        int rotd = Integer.parseInt(rotd_string);
        int new_value = -1;
        if (grade.equals(ROTConstants.PASS))
        {
            new_value = rot+rotd;
            new_value = new_value+rotd;
        } else if (grade.equals(ROTConstants.FAIL))
        {
            new_value = rot-rotd;
            new_value = new_value-rotd;
        }
        return (""+new_value);
    }
    
    public Hashtable getROTOptionsHash(String type, String user_id, String subject, String current_dir)
    {
        ROTStorage rotfs = new ROTFileStorage();
        Hashtable roth = rotfs.getROTOptionsHash(type, user_id, subject, current_dir);
        return roth;
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
    
}
