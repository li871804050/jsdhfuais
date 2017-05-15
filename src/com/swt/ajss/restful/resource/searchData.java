package com.swt.ajss.restful.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gephi.graph.api.Graph;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuwei.graph.algorithm.GraphAlgorithm;
import com.shuwei.graph.inter.SWGraph;
import com.shuwei.graph.util.GraphInstance;
import com.swt.ajss.restful.algorthm.Degree;
import com.swt.ajss.restful.algorthm.Neo4jHandle;
import com.swt.ajss.restful.algorthm.Result;
import com.swt.ajss.restful.graph.GraphData;
import com.swt.ajss.restful.graph.GraphSearch;
import com.swt.ajss.restful.graph.OntologyAnalyzer;
import com.swt.ajss.restful.service.StartService;

import esclient.Tupu;

@Path("/query")
public class searchData {
//	@POST
////	@Path("/anjian")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
	public String getClichedMessage(String dataStr) {
		System.out.println("搜索!");
		JSONObject paramJSON = JSON.parseObject(dataStr);
		System.out.println(paramJSON.toString());
		//搜索结果
		List<Object> result = creatIndexFromNeo4j.search(paramJSON.get("casetype").toString());
		
		//在这里把结果加入到JSON
		JSONObject json = new JSONObject();

		json.put("left", result.get(0));
		Map<String, String> map1 = (Map<String, String>) result.get(1);
		Map<String, String> map2 = (Map<String, String>) result.get(2);
		JSONObject object1 = new JSONObject();
		for (String key: map1.keySet()){
			JSONObject object = new JSONObject();
			object.put("num", map1.get(key));
			object.put("cypher", map2.get(key));
			object1.put(key, object);
		}
		json.put("right", object1);
		return json.toString();
	}
	
	@POST
	@Path("/neo4j")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getNeo4jConfig(){
	
		System.out.println("获取neo4j的配置信息!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果
		String con = analyzer.configNeo4j();
		System.out.println(con);
		return con;
	}
	


	@POST
	@Path("/anjian")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getMessage(String casetype){
	
		System.out.println("搜索!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
		System.out.println(casetype);
		//搜索结果
		JSONObject paramJSON = JSON.parseObject(casetype);
		casetype = paramJSON.getString("casetype");
		casetype = casetype.replace("'", "");
		List<Object> result = GraphSearch.getResult(casetype);
//		List<Object> result = search.getResult(casetype);
		//在这里把结果加入到JSON
		JSONObject json = new JSONObject();
		if (result.size() == 3) {
//			System.out.println(result.get(0));
			json.put("left", result.get(0));
			Map<String, String> map1 = (Map<String, String>) result.get(1);
			Map<String, String> map2 = (Map<String, String>) result.get(2);
			JSONArray array = new JSONArray();
			for (String key : map1.keySet()) {
				JSONObject object = new JSONObject();
				object.put("num", map1.get(key));
				object.put("cypher", map2.get(key));
				object.put("label", key);
				array.add(object);
			}
			json.put("right", array);
		}
		return json.toString();
	}

	
	@POST
	@Path("/reasoning")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int addRelation(String conditions){
	
		System.out.println("推理!");

		return GraphData.reasoning(conditions);
	}
	
	
	@POST
	@Path("/delete/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int deleteNodes(String labels){
	
		System.out.println("删除一类实体节点!");
		JSONObject object = JSONObject.parseObject(labels);
		
		return GraphData.deleteNodes(object.getString("label"));
	}
	
	@POST
	@Path("/delete/node")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int deleteNode(String id){
	
		System.out.println("删除一个节点!");
		JSONObject object = JSONObject.parseObject(id);
		
		return GraphData.deleteNode(object.getString("id"));
	}
	
	
	@POST
	@Path("/delete/rel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int deleteRel(String rel){
	
		System.out.println("删除关系!");
		JSONObject object = JSONObject.parseObject(rel);
		
		return GraphData.deleteRels(object.getString("rel"));
	}
	
	@POST
	@Path("/map")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getMap(String casetype){
	
		System.out.println("地图!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果
		JSONObject paramJSON = JSON.parseObject(casetype);
		casetype = paramJSON.getString("casetype");
		casetype = casetype.replace("'", "");
		return com.swt.ajss.restful.graph.GraphMap.analyzeMap(casetype);
	}
	
	
	@POST
	@Path("/onto/rel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String showOntoRel(){
		System.out.println("本体关系！");
		return GraphData.showOntologyRelation();
	}
	
	@POST
	@Path("/rel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getRel(String casetype){
	
		System.out.println("关系!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果
		JSONObject paramJSON = JSON.parseObject(casetype);
		casetype = paramJSON.getString("casetype");
		return GraphData.getRel(casetype);
	}

	
	/**
	 * 暂时没被使用
	 * 展示对应节点下的某种关系
	 * @param casetype
	 * @return
	 */
	@POST
	@Path("/relAll")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getRelAll(String casetype){
	
		System.out.println("关系!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果
		JSONObject paramJSON = JSON.parseObject(casetype);
		String id = paramJSON.getString("id");
		String rel = paramJSON.getString("rel");
		return GraphData.getRelAll(id, rel);
	}

	
	
	@POST
	@Path("/relType")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getRelType(){
	
		System.out.println("关系级别!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果		
		return OntologyAnalyzer.relationType();
	}
	
	@POST
	@Path("/labelPro")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getLabelPro(){
	
		System.out.println("默认属性显示!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果		
		return OntologyAnalyzer.getLabelPro();
	}

	
	@POST
	@Path("/anjian/all")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendJson(){

		System.out.println("层级关系!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
//		String line = "";
//		try {
//			Map<String, List> map = new HashMap<>();
//			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/level2.txt")));
//			line = reader.readLine().replace(",{}", "");
//
//		}catch (IOException e){
//
//		}
		String line = OntologyAnalyzer.getjsonLevel();
		return line;
	}

	
	/**
	 * 
	 * @param label
	 * @return 相似案件分析
	 */
	@POST
	@Path("/anjian/same")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendSame(String label){

		System.out.println("相似案件分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果
		JSONObject paramJSON = JSON.parseObject(label);
		label = paramJSON.getString("label");
		//在这里把结果加入到JSON
		System.err.println(label);
		JSONArray array = new JSONArray();
		array.add(analyzer.getSame(label));
		return array.toJSONString();
	}



	@POST
	@Path("/anjian/cx")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String sendCX(){

		System.out.println("传销分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
		Map<String, List> res = analyzer.getCX();
		JSONArray array = new JSONArray();
		for (String key: res.keySet()){
			JSONObject object1 = new JSONObject();
			object1.put("label", key);
			object1.put("num", res.get(key).get(0));
			object1.put("cypher", res.get(key).get(1));
			array.add(object1);
		}
		JSONObject object = new JSONObject();
		object.put("right", "match (n:非法经营案) return n limit 800");
		object.put("left", array);
		return object.toJSONString();
	}

	
	@POST
	@Path("/yiyao/pw")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String peiwu(){

		System.out.println("配伍分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
		List<String> list = analyzerDrug.peiWu();
		JSONObject object = new JSONObject();
		object.put("left", JSONObject.parse(list.get(0)));
		object.put("right", JSONObject.parse(list.get(1)));
		return object.toJSONString();
		
	}
	
	@POST
	@Path("/es")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String esData(String es){
		System.out.println("es!");
		JSONObject object = JSONObject.parseObject(es);
		
		String index = object.getString("index");
		String fString = object.getString("fields").replace("\"", "");
		String[] fields = fString.substring(1, fString.length() - 1).split(",");
		String vString = object.getString("values").replace("\"", "");
		String[] values = vString.substring(1,  vString.length() - 1).split(",");
		net.sf.json.JSONArray array = Tupu.searchGraph(index, fields, values);
		return ananlyzerESData.anaES(array.toString());		
	}

	@POST
	@Path("/add/rel")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String addRel(String EntRel){
		System.out.println("rel!");
		
		JSONObject object = JSONObject.parseObject(EntRel);
		String ent1 = object.getString("ent1");
		String rel = object.getString("rel");
		String ent2 = object.getString("ent2");
		OntologyAnalyzer.addRelOnt(ent1, ent2, rel, StartService.dicDir + "/" + StartService.owlName);
		return "添加完成！";
	}

	@POST
	@Path("/add/relnode")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public int addRelNode(String EntRel){
		System.out.println("rel!");
		
		JSONObject object = JSONObject.parseObject(EntRel);
		String id1 = object.getString("id1");
		String rel = object.getString("rel");
		String id2 = object.getString("id2");
		return GraphData.addRelNode(id1, id2, rel);
	}
	
	
	@POST
	@Path("/yiyao/yongyao")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String yongyao(){

		System.out.println("用药分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
		List<String> list = analyzerDrug.useDrug();
		JSONObject object = new JSONObject();
		object.put("left", JSONObject.parse(list.get(0)));
		object.put("right", JSONObject.parse(list.get(1)));
		return object.toJSONString();
	}


	@POST
	@Path("/yiyao/jinji")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String jinji(){

		System.out.println("禁忌分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
//		List<String> list = analyzerDrug.jinji();
//		JSONObject object = new JSONObject();
//		object.put("left", JSONObject.parse(list.get(0)));
//		object.put("right", JSONObject.parse(list.get(1)));
//		return object.toJSONString();
//		JSONObject object = new JSONObject();
//		object.put("left", "");
//		JSONObject resultsAll = new JSONObject();
//		JSONObject head = new JSONObject();
//		head.put("title1", "审计");
//		head.put("title2", "审计结果");
//		head.put("title3", "");
//		resultsAll.put("head", head);
//		JSONArray resArray = new JSONArray();
//		JSONObject object2 = new JSONObject();
//		object2.put("title1", "配伍禁忌");
//		object2.put("title2", "40");
//		object2.put("title3", "");
//		object2.put("cypher", "match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:用药]-(m2:药品),(m1)-[r3:配伍禁忌]-(m2) return n,m1,m2,r1,r2,r3");
//		resArray.add(object2);
//		JSONObject object3 = new JSONObject();
//		object3.put("title1", "过度用药");
//		object3.put("title2", "81");
//		object3.put("title3", "");
//		object3.put("cypher", "match (n:患者)-[r:用药]-(m:药品) where r.单日用量 > m.单日用量 return r,m,n");
//		resArray.add(object3);
//		JSONObject object4 = new JSONObject();
//		object4.put("title1", "用药禁忌");
//		object4.put("title2", "3");
//		object4.put("title3", "");
//		object4.put("cypher", "match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:诊断]-(m2:疾病),(m1)-[r3:禁忌疾病]-(m2) return n,m1,m2,r1,r2,r3");
//		resArray.add(object4);
//		resultsAll.put("data", resArray);
//		object.put("right", resultsAll);
//		return object.toJSONString();
		return analyzerDrug.shenji();
	}
	
	
/*	@POST
	@Path("/yiyao/jinji")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)	
	public String jinji(){

		System.out.println("禁忌分析!");
//		JSONObject paramJSON = JSON.parseObject(dataStr);
//		System.err.println("搜索!");
//		System.out.println(paramJSON.toString());
		//搜索结果

		//在这里把结果加入到JSON
		List<String> list = analyzerDrug.jinji();
		JSONObject object = new JSONObject();
		object.put("left", JSONObject.parse(list.get(0)));
		object.put("right", JSONObject.parse(list.get(1)));
		return object.toJSONString();
	}
	*/
	
	/**
	 * 
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("cc")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String ConnectedComponent(String Neo4jJson){
		
//		GraphAlgorithm ga=new GraphAlgorithm();
//		SWGraph sg=GraphInstance.getInstanceOfGraph("connectedcomponent");
//		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
//		Queue<Integer>[] result=ga.ConnectedComponent(sg);
//		Result res=new Result();
//		res.setObj(result);
//
//		String str= algorithm.Algorithm.getJSON(res);
//		return str;
		return "";
	}
	
	/**
	 * 
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("cc_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String ConnectedComponent_Define(String definJson){
		
//		GraphAlgorithm ga=new GraphAlgorithm();
//		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("connectedcomponent", definJson);
//		Queue<Integer>[] result=ga.ConnectedComponent(sg);
//		Result res=new Result();
//		res.setObj(result);
//
//		String str=algorithm.Algorithm.getJSON(res);
//		return str;
		return "";
		
	}
	
	
	/**
	 * 
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("pagerank")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String PageRank(String Neo4jJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=GraphInstance.getInstanceOfGraph("pagerank");
		sg=sg.loadSWGraphFromNeo4jJson(Neo4jJson, null);
		Map<String, Double> result=ga.PageRank(sg,null,null,null);
		
		List<Entry<String, Double>> info= algorithm.Algorithm.sortDESC_Double(result);
		
		
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	
	/**
	 * 重要点分析
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("pagerank_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String PageRank_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("pagerank",definJson);
		Map<String, Double> result=ga.PageRank(sg,null,null,null);
		
		List<Entry<String, Double>> info= algorithm.Algorithm.sortDESC_Double(result);
		
		
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	
	/**
	 * 稳定点分析
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("trianglecount")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
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
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	/**
	 * 稳定点分析
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("trianglecount_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String TriangleCount_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("trianglecount",definJson);
		Map<Integer, Integer> result=ga.TriangleCount(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultTC=0;
		for(int key:result.keySet()){
			
			resultTC=result.get(key);
			if(resultTC!=0){
				re.put(String.valueOf(key),resultTC);
			}
			
			
		}
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	
	/**
	 * 出度入度统计
	 * @param define
	 * @return
	 */
	@POST
	@Path("degree_Define01")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String inDegree_Define01(String define){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("degree_with_arrow",define);
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

		String str=algorithm.Algorithm.getJSON(reslist);
		return str;
		
	}
	
	
	/**
	 * 有向图入度
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("indegree")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
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
	    
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	@POST
	@Path("indegree_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String inDegree_Define(String define){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("degree_with_arrow",define);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new TreeMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
			
			
		}
	    
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	/**
	 * 有向图出度
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("outdegree")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
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
		
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	
	/**
	 * 无向图度数
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("outdegree_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String outDegree_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("degree_with_arrow",definJson);
		Map<Integer, Integer> result=ga.outDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		return str;
		
	}
	
	/**
	 * 无向图度数
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("degree")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
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
		
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		
		return str;
		
	}
	
	/**
	 * 无向图度数
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("degree_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String Degree_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("degree_without_arrow",definJson);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		
		return str;
		
	}
	
	
	/**
	 * 最短路径
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("shortestpath_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String shortestPath_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		SWGraph sg=algorithm.Algorithm.parseTheDefineGraphJson("shortestpath_arrow",definJson);
		Map<Integer, Integer> result=ga.inDegree(sg);
		
		Map<String,Integer>re=new HashMap<String, Integer>();
		int resultDegree=0;
		for(int key:result.keySet()){
			resultDegree = result.get(key);
			
			if(resultDegree!=0){
				re.put(String.valueOf(key), resultDegree);
			}
		}
		
		List<Entry<String, Integer>> info= algorithm.Algorithm.sortDESC(re);
		Result res=new Result();
		res.setObj(info);

		String str=algorithm.Algorithm.getJSON(res);
		
		return str;
		
	}
	
	
	/**
	 * 无向图度数
	 * @param Neo4jJson  neo4j查询结果graph格式
	 */
	@POST
	@Path("computeModularity_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String ComputeModularity_Define(String definJson){
		
		GraphAlgorithm ga=new GraphAlgorithm();
		Graph sg=algorithm.Algorithm.parseDefineJsonToGephiGraph(definJson,null);
		Map<String, Integer> result=ga.ComputeModularity(sg, 1., true, false);
		
		

		String str=algorithm.Algorithm.getJSON(result);
		
		return str;
		
	}
	
	
	
	@GET
	@Path("test")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String test(){
		
		
		return "333";
		
	}
	
	@POST
	@Path("test_define")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String test1(){
		
		
		return "333";
		
	}
	
	
	@GET
	@Path("communitysearch")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String CommunitySearch(){
		String cypher="match(n:STORE7189)-[r:rule]->(m:STORE7189) WHERE n.communityrule1 is not null and m.communityrule1 is not null return r";
		Neo4jHandle nh=new Neo4jHandle("neo4j","123456","192.168.0.15","7474");
		return nh.getCypherResult(cypher);
		
	}
	
	
	@GET
	@Path("communitysearchsmall")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String CommunitySearchSmall(){
		String cypher="match(n:STORE7189)-[r:rule]->(m:STORE7189) WHERE n.communityrule1 is not null and m.communityrule1 is not null return r limit 200";
		Neo4jHandle nh=new Neo4jHandle("neo4j","123456","192.168.0.15","7474");
		return nh.getCypherResult(cypher);
		
	}
}
