package esclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class JavaNetURLRESTFulClient {
	
	
	
	public static String post(String index,String value,ArrayList<String> list1) {
		String targetURL = "http://192.168.0.141:9200/"+index+"/_xpack/_graph/_explore";//查询的端点
		String out = null;//查询的结果
		try {
			 
			URL targetUrl = new URL(targetURL);//将字符串转换成URL
			
			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();//创建链接
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");//设置方法
			httpConnection.setRequestProperty("Content-Type", "application/json");
			//ArrayList<String> list1 = new ArrayList<String>();
			//list1.add("车次.keyword");
			//list1.add("姓名.keyword");
		
			//String b ="";
			if(list1.size()==0){
				return null;
			};
	    	String c = "{\"field\":\""+list1.get(0)+"\""+",\"size\":5,\"min_doc_count\":1}";
		    for (int i =1;i<list1.size();i++) {
	
				c=c+",{\"field\":\""+list1.get(i)+"\""+",\"size\":5,\"min_doc_count\":1}";
				
			}
		   // System.out.println(c);
		   // String aString = "{\"vertices\":[{\"field\":\"车次.keyword\",\"size\":5,\"min_doc_count\":1},{\"field\":\"姓名.keyword\",\"size\":5,\"min_doc_count\":1}]}";
		   // System.out.println(aString);
			String input = "{\"query\":{\"query_string\":{\"query\":"+"\""+value+"\""+"}},\"controls\":{\"use_significance\":true,\"sample_size\":2000,\"timeout\":5000}"+
		    ",\"connections\":{\"vertices\":["+c+"]},\"vertices\":["+c+"]}";

	        //System.out.println(input);
			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();
	 
			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ httpConnection.getResponseCode());
			}
	 
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
	 
			String output;
			//System.out.println("Output from Server:\n");
			while ((output = responseBuffer.readLine()) != null) {
				//System.out.println(output);
				out =output;
			}
			
			httpConnection.disconnect();
	 
		  } catch (MalformedURLException e) {
	 
			e.printStackTrace();
	 
		  } catch (IOException e) {
	 
			e.printStackTrace();
	 
		 }
		return out ;
	 
		
	}
}

