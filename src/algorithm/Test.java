package algorithm;

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

public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
//		FileReader file = new FileReader("D:\\json.txt");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\test.txt"),"UTF-8"));
		try {
			String str=reader.readLine();
			
			Algorithm gl=new Algorithm();
//			SWGraph sg=gl.parseTheDefineGraphJson("degree-without-arrow",str);
//			GraphAlgorithm ga=new GraphAlgorithm();
			
			
			System.out.println(gl.TriangleCount_Define(str).toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		

	}

}
