package com.swt.ajss.restful.graph;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.StartService;

public class GraphMap {
	public static void main(String[] args) {
//		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/20170417.owl");	
//		GraphPath.getRelationGraph();
		String cypher = "MATCH p=()-[r:`子公司`]->() RETURN p LIMIT 25";
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
		if (!"#".equals(id)){
			String cypher = "start n = node (" + id + ") match (n:化工企业)-[:子公司]-(m:化工企业)-[:生产]-(k) return m,k";
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				return getMapJson(result);
			}else {
				cypher = "start n = node (" + id + ") match (n:化工产品)-[:生产]-(m:化工企业) return m,n";
				result = StartService.neo4jHandle.getCypherResult(cypher);
				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
					return getMapJson(result);
				}
			}	
		}else {
			String cypher = "match (n:化工企业)-[:生产]-(m) where n.重大危险源 = '是' return m,n";
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				return getMapJson(result);
			}
		}
		return "";
	}
	
	
	public static String getMapJson(String result) {
		String[] pros = {"中文名称", "公司名", "经度", "纬度"};
		List<Map<String, String>> res = GraphData.getPros(pros, result);
		double maxL = -0.1, minL = -0.1, maxW = -0.1, minW = -0.1;
		JSONArray array = new JSONArray();
		for (Map<String, String> re: res){			
			if (re.size() == 4 && re.get("经度").matches("[0-9.]+") && re.get("纬度").matches("[0-9.]+")){
				JSONObject object = new JSONObject();
				object.put("conment", re.get("公司名"));
				object.put("source", re.get("中文名称"));
				object.put("longitude", re.get("经度"));
				object.put("latitude", re.get("纬度"));
				array.add(object);
				if (minL == -0.1 || (minL > Double.parseDouble(re.get("经度")))){
					minL = Double.parseDouble(re.get("经度"));
				}
				if (minW == -0.1 || (minW > Double.parseDouble(re.get("纬度")))){
					minW = Double.parseDouble(re.get("纬度"));
				}
				if (maxW == -0.1 || (maxW < Double.parseDouble(re.get("纬度")))){
					maxW = Double.parseDouble(re.get("纬度"));
				}
				if (maxL == -0.1 || (maxL < Double.parseDouble(re.get("经度")))){
					maxL = Double.parseDouble(re.get("经度"));
				}
			}			
		}
		if (maxL != -0.1){
			JSONObject object = new JSONObject();
			object.put("longitude", (maxL + minL)/2);
			object.put("latitude", (maxW + minW)/2);
			object.put("data", array);
			return object.toJSONString();
		}
		return "";
	}
	
}
