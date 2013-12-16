package org.catechis.juksong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import org.catechis.constants.Constants;

/**
 * Like the 400 year old ruins of the Japanese castle on the hill overlooking Juksong
 * is the resting place for old stones, this class is the resting place for old code.
 * @author a
 *
 */
public class BambooCastle 
{
	
	String address;
	Vector <String> log;
	private Vector <String> results;
	
    public BambooCastle(String _address) 
    {
    	results = new Vector<String>();
    	address = _address;
    	log = new Vector<String>();
    }

	/**
     * Due to Googles legions of programmers, we can't parse their images
     * without using their Web Service interface.
     * Who has time for that?  But if we could, it would be this simple.
     * Got the following error:
     * Server returned HTTP response code: 403 for URL
     * http://images.google.co.kr/images?hl=en&um=1&sa=1&q=apple&aq=f&oq=&aqi=g10&start=0
     * http://images.google.co.kr/images?complete=1&hl=ko&q=apple&lr=&um=1&ie=UTF-8&sa=N&tab=wi
     * 
     * Server returned HTTP response code: 405 for URL: 
     * http://images.google.co.kr/images?complete=1&hl=ko&q=apple&lr=&um=1&ie=UTF-8&sa=N&tab=wi
     * 
     * do I need this?
     * urlConn.setRequestProperty("User-agent","Mozilla/2.0.0.11");
     * Yes: "If you're going to use the java URL class, you need to write out the request headers manually (i.e. User-Agent: myuseragent). "
	 * 403 (or 405) is an automatic redirect. Use the 
	 * setFollowRedirects() method of the HttpUrlConnection class 
	 * before writing to it and it should work. 
	 * i had to set the request property "User-Agent" and it works. 
URL url=new URL("http://www....");
HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
urlConn.setRequestProperty("User-agent","Mozilla/2.0.0.11");
urlConn.connect();
	 * How to use HttpURLConnection and BufferedReader
     */
    private Vector loadGooglePage(String text, String _encoding)
    {
    	Vector html = new Vector();
    	address = "http://images.google.co.kr/images";
    	String before_text = "?hl=en&um=1&sa=1&q=";
    	//String before_text = "?complete=1&hl=ko&q=";
    	String trailer = "&aq=f&oq=&aqi=g10&start=0";
    	//String trailer = "&lr=&um=1&ie=UTF-8&sa=N&tab=wi";
    	String url_string = address+before_text+text+trailer;
    	String search_url = "";
    	try
    	{
    		search_url = URLEncoder.encode(text, _encoding);
    	} catch (UnsupportedEncodingException uee)
    	{
    		html.add("UnsupportedEncodingException");
    		return html;
    	}
    	String inputLine = "";
    	try
    	{
    		URL url = new URL(address+before_text+search_url+trailer);
    		HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
    		//urlConn.setRequestProperty("User-agent","Mozilla/2.0.0.11");  
    		urlConn.setRequestProperty("User-Agent", ""); 
    		urlConn.setDoOutput(true);
    		//urlConn.setDoInput(true);
    		//urlConn.connect();
    		//urlConn.setRequestMethod("GET"); // default
            PrintWriter out = new PrintWriter(urlConn.getOutputStream());
            InputStream is = urlConn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
    		while ((inputLine = in.readLine()) != null)
    		{
    			html.add(inputLine);
    		}
    		in.close();
    	} catch (MalformedURLException efurle)
    	{
    			System.err.println("MalformedURLException");
    	}
    	 catch (IOException ioe)
	    {
    		 System.err.println("IOException");
    		 System.err.println(ioe.toString());

	    }
    	return html;
    }
    
    /**
     * The images start after the 'Related Searches' text:
     * <font color="#000000">Related searches:</font>
     * 
     * Next there will be a series of links to other searches which we want to skip.
     * They look like this:
     * <a href="/images?hl=en&amp;um=1&amp;q=apple+tablet&amp;revid=663329519&amp;ei=x12gSrrpJZ_4tgO20dmHDg&amp;sa=X&amp;oi=revisions_inline&amp;resnum=0&amp;ct=broad-revision&amp;cd=1"
	 * style="padding-right:10px;white-space:nowrap">apple<b>tablet</b></a>
	 * 
	 *  The image links are then embedded in a long script, and look thus:
	 *  ["/imgres?imgurl\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/apple-logo1.jpg\x26imgrefurl\x3dhttp://weblogs.baltimoresun.com/business/consuminginterests/blog/technology/\x26usg\x3d__ZRYiHpjAeWfxq74V5CGJfAEt_Dk\x3d\x26h\x3d480\x26w\x3d397\x26sz\x3d22\x26hl\x3den\x26start\x3d1\x26sig2\x3dgFdiLZSxNIXs3o1jiGaNnA\x26um\x3d1","","HhYIr-Oz_5sLkM:","http://weblogs.baltimoresun.com/business/consuminginterests/blog/apple-logo1.jpg","107","129","\x3cb\x3eApple\x3c/b\x3e,","","","397 x 480 - 22k","jpg","weblogs.baltimoresun.com","","","http://t0.gstatic.com/images","1",[],"",1],
	 *  
	 *  ["/imgres?imgurl\x3dhttp://www.waterfootprint.org/images/gallery/original/apple.jpg\x26imgrefurl\x3dhttp://www.waterfootprint.org/%3Fpage%3Dfiles/productgallery%26product%3Dapple\x26usg\x3d__OgCvDhaP2-gGffYIgyoi1LEKDd8\x3d\x26h\x3d400\x26w\x3d400\x26sz\x3d48\x26hl\x3den\x26start\x3d2\x26sig2\x3dKKyLjjk4mt3BbRjSlYmSzw\x26um\x3d1","","FH6Irxo0S8bMIM:","http://www.waterfootprint.org/images/gallery/original/apple.jpg","124","124","\x3cb\x3eApple\x3c/b\x3e","","","400 x 400 - 48k","jpg","waterfootprint.org","","","http://t0.gstatic.com/images","1",[],"",1],["/imgres?imgurl\x3dhttp://showclix.com/blog/wp-content/uploads/2009/08/Apple-Logo.jpg\x26imgrefurl\x3dhttp://www.showclix.com/blog/%3Fcat%3D7\x26usg\x3d__9P2VZ6OYmeK8UHZijktHBaxPuP8\x3d\x26h\x3d474\x26w\x3d406\x26sz\x3d27\x26hl\x3den\x26start\x3d3\x26sig2\x3dQxyLFioQlQqfQ8EauNbseA\x26um\x3d1","","u40OX3r-3P4S0M:",
     *
     * We look for dyn.setResults([ in the script to start our capure point.
     * The images start with the cryptic ["/imgres?imgurl\x3d
     * Then the image ends after the .jpg with the string \x26imgrefurl\x3d
     * 
     * Google link:
     * http://images.google.com/images?gbv=2&hl=en&sa=1&q=%22%EA%BD%A4%EC%A0%9C%EB%B2%95%22&aq=f&oq=&aqi=
     */
    public Vector parseGoogleImages(String text)
    {
    	results = new Vector();
    	String encoding = "euc-kr";
    	Vector html = loadGooglePage(text, encoding);
    	//Vector html = testImages();
    	String start_marker_text = "dyn.setResults([";
    	int size = html.size();
    	int i = 0;
    	while (i<size)
    	{
    		String line = (String)html.get(i);
    		String image_start = "[\"/imgres?imgurl\\x3d";
			String image_end = "\\x26imgrefurl\\x3d";
    		if (line.contains(start_marker_text))
    		{
    			while (line.contains(image_start))
    			{
    				int start_marker = line.indexOf(image_start);
    				int start_char = start_marker+image_start.length();
    				int end_char = line.indexOf(image_end, start_char);
    				log.add(line.substring(0, end_char));
    				try
    				{
    					String image_url = line.substring(start_char, end_char);
    					log.add("WebWord.parseGoogleImages: "+i+" added "+image_url);
    					results.add(image_url);
    				} catch (java.lang.StringIndexOutOfBoundsException sioobe)
    				{
    					log.add("WebWord.parseGoogleImages: sioobe");
    				}
    				line = line.substring(end_char, line.length());
    				log.add("line:"+line);
    			}
    		}
    		i++;
    	}
    	return results;
    }
	
}
