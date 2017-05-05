package com.swt.ajss.restful.algorthm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.shuwei.graph.algorithm.GraphAlgorithm;
import com.shuwei.graph.inter.SWGraph;
import com.swt.ajss.restful.service.StartService;

public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		FileReader file = new FileReader("D://json.txt");
		
	
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/360data/重要数据/桌面/化学品/中石化安工院/过氧化物.csv"),"UTF-8"));
			String line = "";
			StartService.set();
			while ((line = reader.readLine()) != null) {
				String cypher = "match (n:化学品) where n.化学品名称 = '" + line + "' set n:有机过氧化物";
				System.out.println(StartService.connection.exectCypher(cypher));
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		

	}

}
