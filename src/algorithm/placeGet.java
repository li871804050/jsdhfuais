package algorithm;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class placeGet {
	public static void main(String[] args) {
		String place = "淮南泉山	松木岛化工园区	贵州省贵阳市息烽县	淄博市沂源县城东风路36号	开封市顺河区新宋路西段89号付1号	义马市人民路西段	大丰市大丰港经济区石化产业园	柳州市北雀路67号	山西省新绛县三泉镇冯古庄村	兴平立交与迎宾大道交叉口南200米	内蒙古乌海市海南区西来峰工业园区	山西省潞城市中华东大街	湛江市人民大道中42号泰华大厦6层	桂溪镇南新街117号	";
		String[] pStrings = place.split("\t");
		for (int i = 0; i < pStrings.length; i++){
//			System.out.println(pStrings[i]);
			Map<String, String> map = getLngAndLat(pStrings[i]);
			if (map != null){
				System.out.println(map.get("x") + "\t" + map.get("y"));
			}else {
				System.out.println("x" + "\t" + "y");
			}
			
		}
	}
	
	
	public static Map<String,String> getLngAndLat(String address){
		Map<String,String> map=new HashMap<String, String>();
		String url = "http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=5hGkGSuCQTdv7HxDN5kfgQ9D";
	    String json = loadJSON(url);
//	    System.out.println("返回坐标"+json);
	    if(json.equals("")||json.contains("[]"))
	    	return null;
	    try {
		    String x=json.substring(json.indexOf("lng")+5,json.indexOf("lat")-2);
		    String y=json.substring(json.indexOf("lat")+5,json.indexOf("precise")-3);
		    map.put("x",x);
		    map.put("y", y);
		    return map;
	    }catch (StringIndexOutOfBoundsException e) {
	    	return null;
			// TODO: handle exception
		}
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
