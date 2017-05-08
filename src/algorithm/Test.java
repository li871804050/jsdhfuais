package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shuwei.graph.algorithm.GraphAlgorithm;
import com.shuwei.graph.inter.SWGraph;
import com.shuwei.graph.util.GraphInstance;
import com.swt.ajss.restful.algorthm.Result;

import net.sf.json.JSONArray;

public class Test {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		
		Neo4jHandle neo4jHandle = new Neo4jHandle("neo4j", "123456", "192.168.0.171", "7474");
		String strCypher = "match p = (n2:化学品)-[:原料]-(n1:化工工艺) where n2.化学品名称 = '硝酸铵' return p";
		String strNeo4jResult = neo4jHandle.getCypherResult(strCypher);
		System.out.println(strNeo4jResult);
//		GraphAlgorithm ga=new GraphAlgorithm();
//		SWGraph sg=GraphInstance.getInstanceOfGraph("pagerank");
//		sg=sg.loadSWGraphFromNeo4jJson(strNeo4jResult, null);
//		Map<String, Double> result=ga.PageRank(sg,null,null,null);
//		
//		List<Entry<String, Double>> info = Algorithm.sortDESC_Double(result);
//		
//		
//		Result res=new Result();
//		res.setObj(info);
//
//		String str=JSONArray.fromObject(res).toString();
//		System.out.println(str);
	
	}

}
