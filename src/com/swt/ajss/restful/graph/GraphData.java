package com.swt.ajss.restful.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.StartService;

public class GraphData {
	
	public static void main(String[] args) {
		getRel("69997");
	}
	
	/*
	 * 查询结果实体统计
	 */
	/**
	 * 
	 * @param list 统计结果
	 * @param data 待处理数据
	 * @param idStrings 统计标签
	 */
	public static void countEnt(List<Map<String, String>> list, String data, List<String> idStrings){		
    	JSONObject json = JSONObject.parseObject(data);
    	String dataArr =  json.getString("results");
    	Map<String, String> map = list.get(0);
    	Map<String, String> map2 = list.get(1);
    	List<String> labelAll = OntologyAnalyzer.getLabel();
    	JSONObject object = JSONObject.parseObject(dataArr.substring(1, dataArr.length() - 1));
		JSONArray array = object.getJSONArray("data");
		for (int j = 0; j < array.size(); j++){
			JSONObject json2 = array.getJSONObject(j);
			JSONObject json3 = json2.getJSONObject("graph");
			JSONArray jsonArray = json3.getJSONArray("nodes");
			for (int i = 0; i < jsonArray.size(); i++){
				JSONObject object2 = jsonArray.getJSONObject(i);
				String label = object2.get("labels").toString();
				String id = object2.get("id").toString();
				if (idStrings.contains(id)){
					continue;
				}
				idStrings.add(id);
//				label = label.substring(2, label.length() - 2);
	//				System.err.println("=="+label);
				for (String all: labelAll){
					if (label.contains("\""+ all + "\"")){
						if (map.containsKey(all)){
							int c = Integer.parseInt(map.get(all)) + 1;
							map.remove(all);
							map.put(all, "" + c);
			 			}else{
			 				map.put(all, "1");
			 			}
						if (map2.containsKey(all)){
							String cy = map2.get(all) + "," + id; 
							map2.remove(all);
							map2.put(all, cy);
			 			}else{
			 				String cy = "START n=node(" + id;
			 				map2.put(all, cy);
			 			}
					}
				}
			}
		}
    }
	
	
	/*
	 * 获取所有点之间的关系
	 */
	public static String useIDs(List<String> idStrings) {
		String nodes = "";
		for (int i = 0; i < idStrings.size() - 1; i ++){
			nodes = nodes + idStrings.get(i) + ",";
		}
		nodes = nodes + idStrings.get(idStrings.size() - 1);
		String cypher = "Start n = node(" + nodes + "), m = node(" + nodes + ") match p = (m)-[r]-(n) return p";
		String reString = StartService.neo4jHandle.getCypherResult(cypher);
		if (reString.contains("\"graph\"") && reString.contains("\"nodes\"")){
			return reString;
		}else {
			return "";
		}		
	}
	
	
	/**
	 * 
	 * @param result 图数据
	 * @return 结果数组
	 */
	public static ArrayList<String> getArrayResult(String result){
		ArrayList<String> list = new ArrayList<String>();
		JSONObject json = JSONObject.parseObject(result);
		JSONArray dataArr =  json.getJSONArray("data");
		for (int i = 0; i < dataArr.size(); i++){
			if (dataArr.getString(i).length() > 4) {
				list.add(dataArr.getString(i).substring(2, dataArr.getString(i).length() - 2));
			}
		}
		return list;
	}
	
	
	/**
	 * 
	 * @param atrs	待获取属性列表
	 * @param result	cypher执行结果
	 * @return	数据对应属性值
	 */
	public static List<Map<String, String>> getPros(String[] atrs, String result) {
		Set<Map<String, String>> datas = new HashSet();
		JSONObject object = JSONObject.parseObject(result).getJSONArray("results").getJSONObject(0);
		JSONArray array = object.getJSONArray("data");
		for (int i = 0; i < array.size(); i++){
			Map<String, String> pros = new HashMap();
			JSONObject graph = array.getJSONObject(i).getJSONObject("graph");
			JSONArray array2 = graph.getJSONArray("nodes");
			for (int k = 0; k < array2.size(); k++){
				JSONObject properties = array2.getJSONObject(k).getJSONObject("properties");
				for (String atr: atrs){
					if (!pros.containsKey(atr) && !"".equals(properties.getString(atr)) && null != properties.getString(atr)){
						pros.put(atr, properties.getString(atr));
					}
				}
			}
			datas.add(pros);
		}
		
		return new ArrayList<>(datas);		
	}

	
	/**
	 * 
	 * @param id 节点id
	 * @return 节点关系
	 */
	public static String getRel(String id) {
		String cypher = "start n = node(" + id + ") match (n)-[r]-() return distinct type(r)";
		StartService.set();
		String result = StartService.connection.exectCypher1(cypher);
		System.out.println(result);
		if (result.contains("data")){
			JSONObject object = JSONObject.parseObject(result);
			String data = object.getString("data");
			if (!data.equals("[]")){
				List<String> relSame = OntologyAnalyzer.getRelSame();
				for (String rel: relSame){
					data = data.replace("[\"" + rel + "\"]", "[\"" + rel + "r\"],[\"" + rel + "l\"]");
				}
				data = "[[\"所有\"]," + data.substring(1);
			}else {
				data = "[]";
			}
			return data;
		}else {
			return "[]";
		}
		
	}
	
	
	/**
	 * 
	 * @param id	节点id
	 * @param rel	关系类型
	 * @return		节点的该类型的关系数据
	 */
	public static String getRelAll(String id, String rel) {
		String cypher = "start n = node(" + id + ") match p = (n)-[r:" + rel + "]-(m) return p";
		StartService.set();
		String result = StartService.neo4jHandle.getCypherResult(cypher);
//		data = data.replace("[", "").replace("]", "");
//		String[] datas = data.split(",");
//		
		return result;		
	}
	
	/**
	 * 基于规则的关系补全（推理）
	 * @param conditions	每个关系一条记录，组成的数组
	 * 
	 */
	public static void reasoning(String conditions) {
		StartService.set();
		JSONObject jsonAll = JSONObject.parseObject(conditions);
		JSONArray arrayConditions = jsonAll.getJSONArray("conditions");
		String cypher = "";
		List<String> wheres = new ArrayList<>();
		for (int i = 0; i < arrayConditions.size(); i++){
			String match1 = "", match2 = "", match3 = "";
			JSONObject condition = arrayConditions.getJSONObject(i);
			JSONObject ent1 = condition.getJSONObject("node1");
			String id1 = ent1.getString("id");
			match1 = "(n" + id1 + ":" + ent1.getString("label") + ")";
			JSONObject properties1 = ent1.getJSONObject("properties"); 
			for (String key: properties1.keySet()){
				wheres.add("n" + id1 + "." + key + " = '" + properties1.getString(key) + "'");
			}
			
			JSONObject ent2 = condition.getJSONObject("node2");
			String id2 = ent2.getString("id");
			match2 = "(n" + id2 + ":" + ent2.getString("label") + ")";
			JSONObject properties2 = ent2.getJSONObject("properties"); 
			for (String key: properties2.keySet()){
				wheres.add("n" + id2 + "." + key + " = '" + properties2.getString(key) + "'");
			}			
			
			JSONObject rel = condition.getJSONObject("relationships");
			String id3 = rel.getString("id");
			match3 = "[r" + id3 + ":" + rel.getString("type") + "]";
			JSONObject properties3 = rel.getJSONObject("properties"); 
			for (String key: properties3.keySet()){
				wheres.add("r" + id3 + "." + key + " = '" + properties1.getString(key) + "'");
			}
			
			cypher = cypher + "match " + match1 + "-" + match3 + "->" + match2 + " ";
		}
		String wString = "";
		if (wheres.size() > 0){
			wString = "where ";
			for (int i = 0; i < wheres.size() - 1; i++){
				wString = wString + wheres.get(i) + " and ";
			}
			wString = wString + wheres.get(wheres.size() - 1);
		}
		
		JSONObject create = jsonAll.getJSONObject("create");
		String idc1 = create.getJSONObject("node1").getString("id");
		String idc2 = create.getJSONObject("node2").getString("id");
		JSONObject relCreate = create.getJSONObject("relationships");
		String typeC = relCreate.getString("type");
		JSONObject proCreate = relCreate.getJSONObject("properties");
		String set = "";
		for (String key: proCreate.keySet()){
			set = set + "r." + key + " = " + proCreate.getString(key) + ", ";
		}
		cypher = cypher + wString + " merge (n" + idc1 + ")-[r:" + typeC + "]->(n"+ idc2 + ")";
		if (!"".equals(set)){
			cypher = cypher + "set " + set.substring(0, set.length() - 2);
		}
		System.out.println(StartService.connection.exectCypher(cypher));
	}
	
	/**
	 * 
	 * @param label 删除label以及与其相关联的边
	 * @return 200为删除成功
	 */
	public static int deleteNodes(String label) {
		StartService.set();
		String cypher = "match (n:" + label + ")-[r]-() delete r,n";
		return StartService.connection.exectCypher(cypher);
	}
	
	/**
	 * 
	 * @param rel
	 * @return 200为删除成功
	 */
	public static int deleteRels(String rel) {
		StartService.set();
		String cypher = "match ()-[r:" + rel + "]-() delete r,n";
		return StartService.connection.exectCypher(cypher);
	}

	
	/**
	 * 
	 * @param id 删除节点及与其相关联的边
	 * @return 200为删除成功
	 */
	public static int deleteNode(String id) {
		// TODO 自动生成的方法存根
		StartService.set();
		String cypher = "start n = node (" + id + ") match (n)-[r]-() delete r,n";
		return StartService.connection.exectCypher(cypher);
	}
}
