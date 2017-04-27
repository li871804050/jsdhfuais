package algorithm;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class placeGet {
	public Map<String,String> getLngAndLat(String address){
		Map<String,String> map=new HashMap<String, String>();
		String url = "http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=5hGkGSuCQTdv7HxDN5kfgQ9D";
	    String json = loadJSON(url);
	    System.out.println("返回坐标"+json);
	    if(json.equals("")||json.contains("[]"))
	    	return null;
	    String x=json.substring(json.indexOf("lng")+5,json.indexOf("lat")-2);
	    String y=json.substring(json.indexOf("lat")+5,json.indexOf("precise")-3);
	    map.put("x",x);
	    map.put("y", y);
	    return map;
	}
	
	 public static String loadJSON (String url) {
	        StringBuilder json = new StringBuilder();
	        try {
	            URL oracle = new URL(url);
	            URLConnection yc = oracle.openConnection();
	            BufferedReader in = new BufferedReader(new InputStreamReader(
	                                        yc.getInputStream()));
	            String inputLine = null;
	            while ( (inputLine = in.readLine()) != null) {
	                json.append(inputLine);
	            }
	            in.close();
	        }
	        catch (MalformedURLException e){}
	        catch(IOException e){}
	    return json.toString();
	 }
}
