package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.InflaterOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.gephi.graph.api.Configuration;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.shuwei.graph.algorithm.GraphAlgorithm;
import com.shuwei.graph.inter.SWGraph;
import com.shuwei.graph.util.GraphInstance;
import com.swt.ajss.restful.algorthm.Degree;
import com.swt.ajss.restful.algorthm.Result;

//import net.sf.json.JSONArray;
//
//import com.shuwei.graph.algorithm.GraphAlgorithm;
//import com.shuwei.graph.inter.SWGraph;
//import com.shuwei.graph.util.GraphInstance;
//import com.sun.jersey.spi.resource.Singleton;

//import net.sf.json.JSONArray;
//
//import com.shuwei.graph.algorithm.GraphAlgorithm;
//import com.shuwei.graph.inter.SWGraph;
//import com.shuwei.graph.util.GraphInstance;

@Path("algorithm")
public class Algorithm {
	
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("cc")
	@Produces("application/json")
	@Consumes("text/plain")
	public String ConnectedComponent(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("connectedcomponent");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Queue<Integer>[] result=ga.ConnectedComponent(sg);
		Result res=new Result();
		res.setObj(result);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("cc_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String ConnectedComponent_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("connectedcomponent", definJson);
		Queue<Integer>[] result=ga.ConnectedComponent(sg);
		Result res=new Result();
		res.setObj(result);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("pagerank")
	@Produces("application/json")
	@Consumes("text/plain")
	public String PageRank(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("pagerank");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<String, Double> result=ga.PageRank(sg,null,null,null);
		
		List<Entry<String, Double>> info= sortDESC_Double(result);
		
		
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("pagerank_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String PageRank_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("pagerank",definJson);
		Map<String, Double> result=ga.PageRank(sg,null,null,null);
		
		List<Entry<String, Double>> info= sortDESC_Double(result);
		
		
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("trianglecount")
	@Produces("application/json")
	@Consumes("text/plain")
	public String TriangleCount(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("trianglecount");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<Integer, Integer> result=ga.TriangleCount(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultTC=0;
		for(int key:result.keySet()){
			
			resultTC=result.get(key);
			if(resultTC!=0){
				re.put(String.valueOf(key),resultTC);
			}
			
			
		}
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONObject.fromObject(res).toString();
		return str;
		
	}
	
	/**
	 * 
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("trianglecount_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String TriangleCount_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("trianglecount",definJson);
		Map<Integer, Integer> result=ga.TriangleCount(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultTC=0;
		for(int key:result.keySet()){
			
			resultTC=result.get(key);
			if(resultTC!=0){
				re.put(String.valueOf(key),resultTC);
			}
			
			
		}
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONObject.fromObject(res).toString();
		return str;
		
	}
	
	@POST
	@Path("degree_Define01")
	@Produces("application/json")
	@Consumes("text/plain")
	public String inDegree_Define01(String define){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("degree_with_arrow",define);
		Map<Integer, Integer> in=ga.inDegree(sg);
		System.out.println(in.size());
		Map<Integer, Integer> out=ga.outDegree(sg);
		System.out.println(out.size());
		List<Degree>reslist=new ArrayList<Degree>();
		
		for(int key:in.keySet()){
			Degree degree=new Degree();
			degree.setKey(key);
			degree.setInDegree(in.get(key));
			degree.setOutDegree(out.get(key));
			reslist.add(degree);
			
		}

		String str=JSONArray.fromObject(reslist).toString();
		return str;
		
	}
	
	
	/**
	 * ����ͼ���
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("indegree")
	@Produces("application/json")
	@Consumes("text/plain")
	public String inDegree(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("degree_with_arrow");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new TreeMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
			
			
		}
	    
		List<Entry<String, Integer>> info= sortDESC(re);
		
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	@POST
	@Path("indegree_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String inDegree_Define(String define){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("degree_with_arrow",define);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new TreeMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
			
			
		}
	    
		List<Entry<String, Integer>> info= sortDESC(re);
		
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("outdegree")
	@Produces("application/json")
	@Consumes("text/plain")
	public String outDegree(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("degree_with_arrow");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<Integer, Integer> result=ga.outDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("outdegree_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String outDegree_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("degree_with_arrow",definJson);
		Map<Integer, Integer> result=ga.outDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		return str;
		
	}
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("degree")
	@Produces("application/json")
	@Consumes("text/plain")
	public String Degree(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("degree_without_arrow");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		
		return str;
		
	}
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("degree_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String Degree_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("degree_without_arrow",definJson);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		
		return str;
		
	}
	
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("shortestpath_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String shortestPath_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=parseTheDefineGraphJson("shortestpath_arrow",definJson);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=JSONArray.fromObject(res).toString();
		
		return str;
		
	}
	
	
	/**
	 * ����ͼ����
	 * @param Neo4jJson  neo4j��ѯ���graph��ʽ
	 */
	@POST
	@Path("computeModularity_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String ComputeModularity_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		Graph sg=parseDefineJsonToGephiGraph(definJson,null);
		Map<String, Integer> result=ga.ComputeModularity(sg, 1., true, false);
		
		

		String str=JSONArray.fromObject(result).toString();
		
		return str;
		
	}
	
	
	
	@GET
	@Path("test")
	@Produces("application/json")
	@Consumes("text/plain")
	public String test(){
		
		
		return "333";
		
	}
	
	@POST
	@Path("test_define")
	@Produces("application/json")
	@Consumes("text/plain")
	public String test1(){
		
		
		return "333";
		
	}
	
	
	@GET
	@Path("communitysearch")
	@Produces("application/json")
	@Consumes("text/plain")
	public String CommunitySearch(){
		String cypher="match(n:STORE7189)-[r:rule]->(m:STORE7189) WHERE n.communityrule1 is not null and m.communityrule1 is not null return r";
		Neo4jHandle nh=new Neo4jHandle("neo4j","123456","192.168.0.15","7474");
		return nh.getCypherResult(cypher);
		
	}
	
	
	@GET
	@Path("communitysearchsmall")
	@Produces("application/json")
	@Consumes("text/plain")
	public String CommunitySearchSmall(){
		String cypher="match(n:STORE7189)-[r:rule]->(m:STORE7189) WHERE n.communityrule1 is not null and m.communityrule1 is not null return r limit 200";
		Neo4jHandle nh=new Neo4jHandle("neo4j","123456","192.168.0.15","7474");
		return nh.getCypherResult(cypher);
		
	}
	
	
	/**
	 * �����Զ����ͼjson��ʽ
	 * @param deginJson
	 * @return
	 * @throws IOException 
	 */
	public static SWGraph parseTheDefineGraphJson(String key,String degfinJson) {
		
		SWGraph sg=GraphInstance.getInstanceOfGraph(key);
		if(sg==null){
			return null;
		}
		
		Set<Integer>nodeSet=new HashSet<Integer>();
//		HashMap<Integer, Integer>rel=new HashMap<Integer, Integer>();
		
		List<String>list=new ArrayList<String>();
		
		JSONArray  jsonArrary=JSONArray.fromObject(degfinJson);
		
		for(int i=0;i<jsonArrary.size();i++){
			
			JSONObject jsonArraryObject= jsonArrary.getJSONObject(i);
			
			JSONObject jsonObjectSource=jsonArraryObject.getJSONObject("source");
			JSONObject jsonObjectTarget=jsonArraryObject.getJSONObject("target");
			JSONObject jsonObjectProperties=jsonArraryObject.getJSONObject("properties");
			
			String source_id=jsonObjectSource.getString("id");
			String target_id=jsonObjectTarget.getString("id");
//			String rel_weight=jsonObjectProperties.getString("weight");
			nodeSet.add(Integer.parseInt(source_id));
			nodeSet.add(Integer.parseInt(target_id));

//			rel.put(Integer.parseInt(source_id), Integer.parseInt(target_id));
			list.add(source_id+","+target_id);
			
		}
		
		for(int nodeID:nodeSet){
			
			sg.addNode(nodeID);
			
		}
		
		String []edgeStr=null;
//		Set<Integer>relKeySet=rel.keySet();
		for(String str:list){
			edgeStr=str.split(",");
			sg.addEdge(Integer.parseInt(edgeStr[0]), Integer.parseInt(edgeStr[1]));
			
		}
        
		
	    
	    
	    
		return sg;
		
	}
	
	public static Graph parseDefineJsonToGephiGraph(String degfinJson,String weightLabel){
		
		GraphModel graphModel = GraphModel.Factory.newInstance(new Configuration());
        UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
		JSONArray  jsonArrary=JSONArray.fromObject(degfinJson);
		Node nodeSource=null;
        Node nodeTarget = null;
		for(int i=0;i<jsonArrary.size();i++){
			
			JSONObject jsonArraryObject= jsonArrary.getJSONObject(i);
			
			JSONObject jsonObjectSource=jsonArraryObject.getJSONObject("source");
			JSONObject jsonObjectTarget=jsonArraryObject.getJSONObject("target");
			JSONObject jsonObjectProperties=jsonArraryObject.getJSONObject("properties");
			
			String source_id=jsonObjectSource.getString("id");
			String target_id=jsonObjectTarget.getString("id");
			
			
			
	        
	        if(!undirectedGraph.hasNode(source_id)){
	        	nodeSource = graphModel.factory().newNode(source_id);
	        	undirectedGraph.addNode(nodeSource);
	        }
	        
	        if(!undirectedGraph.hasNode(target_id)){
	        	nodeTarget=graphModel.factory().newNode(target_id);
	        	undirectedGraph.addNode(nodeTarget);
	        }
	        
	        
	        Edge edge = graphModel.factory().newEdge(undirectedGraph.getNode(source_id),undirectedGraph.getNode(target_id) , false);
	        undirectedGraph.addEdge(edge);
		}

		
		
		return undirectedGraph;
		
	}
	
	public static List<Entry<String, Integer>> sortDESC(Map<String,Integer> input){
		List<Entry<String, Integer>> info = new ArrayList<Entry<String, Integer>>(input.entrySet());
		
		Collections.sort(info, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1,Entry<String, Integer> o2) {

				return (o2.getValue() - o1.getValue()); 
				
			} 
		}); 
		
		return info;
	}
	
	public static List<Entry<String, Double>> sortDESC_Double(Map<String,Double> input){
		List<Entry<String, Double>> info = new ArrayList<Entry<String, Double>>(input.entrySet());
		
		Collections.sort(info, new Comparator<Entry<String, Double>>() {

			public int compare(Entry<String, Double> o1,Entry<String, Double> o2) {

				return o2.getValue().compareTo(o1.getValue()); 
				
			}
			

		
		}); 
		
		return info;
	}
	
	public static String getJSON(Result res){
		String str=JSONArray.fromObject(res).toString();
		return str;
	}

	public static String getJSON(Map<String, Integer> res){
		String str=JSONArray.fromObject(res).toString();
		return str;
	}
	

	public static String getJSON(List<Degree>res){
		String str=JSONArray.fromObject(res).toString();
		return str;
	}
}