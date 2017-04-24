package com.swt.ajss.restful.graph;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.StartService;

public class test {
	public static void main(String[] args) {
//		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/20170417.owl");	
//		GraphPath.getRelationGraph();
		String cypher = "match (n:化工企业)-[:子公司]-(m) where n.公司名 = '武汉市合中生化制造有限公司' return m,n";
		StartService.set();
//		System.out.println(StartService.neo4jHandle.toString());
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		System.out.println(result);
	}
	
	public static void name() {
//		List<String> level = OntologyAnalyzer.getRelation();
//		for (String l: level){
//			System.err.println(l);
//		}
		
	}
	
	
	/**
	 * 
	 * @param id 数据所在的id
	 * @return
	 */
	public static String analyzeMap(String id) {
		if (!"".equals(id)){
			String cypher = "start n = node (" + id + ") match (n:化工企业)-[:子公司]-(m:化工企业) return m,n";
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				String[] pros = {"公司名", "经度", "维度"};
				List<List<String>> res = GraphData.getPros(pros, result);
				JSONArray array = new JSONArray();
				for (List<String> re: res){
					JSONObject object = new JSONObject();
					object.put("注释", re.get(0));
					object.put("经度", re.get(1));
					object.put("维度", re.get(2));
					array.add(object);
				}
				return array.toJSONString();
			}else {
				cypher = "start n = node (" + id + ") match (n:化工产品)-[:生产]-(m:化工企业) return m";
				result = StartService.neo4jHandle.getCypherResult(cypher);
				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
					String[] pros = {"公司名", "经度", "维度"};
					List<List<String>> res = GraphData.getPros(pros, result);
					JSONArray array = new JSONArray();
					for (List<String> re: res){
						JSONObject object = new JSONObject();
						object.put("注释", re.get(0));
						object.put("经度", re.get(1));
						object.put("维度", re.get(2));
						array.add(object);
					}
					return array.toJSONString();
				}
			}	
		}else {
			String cypher = "match (n:化工企业)-[:生产]-(m) return m,n";
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				String[] pros = {"公司名", "经度", "维度"};
				List<List<String>> res = GraphData.getPros(pros, result);
				JSONArray array = new JSONArray();
				for (List<String> re: res){
					JSONObject object = new JSONObject();
					object.put("注释", re.get(0));
					object.put("经度", re.get(1));
					object.put("维度", re.get(2));
					array.add(object);
				}
				return array.toJSONString();
			}
		}
		return "";
	}
	
	
	
	
}
