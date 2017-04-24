package com.swt.ajss.restful.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.StartService;

public class GraphData {
	
	
	
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
	public static List<List<String>> getPros(String[] atrs, String result) {
		List<List<String>> datas = new ArrayList<>();
		JSONObject object = JSONObject.parseObject(result).getJSONArray("results").getJSONObject(0);
		JSONArray array = object.getJSONArray("data");
		for (int i = 0; i < array.size(); i++){
			List<String> pros = new ArrayList<>();
			JSONObject graph = array.getJSONObject(i).getJSONObject("graph");
			JSONObject properties = graph.getJSONArray("nodes").getJSONObject(0).getJSONObject("properties");
			for (String atr: atrs){
				pros.add(properties.getString(atr));
			}
			datas.add(pros);
		}
		
		return datas;
		
	}

}
