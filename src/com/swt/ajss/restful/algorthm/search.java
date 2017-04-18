package com.swt.ajss.restful.algorthm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.resource.creatIndexFromNeo4j;
import com.swt.ajss.restful.service.StartService;

public class search {
	public static String indexPath = StartService.dicDir + "/index2";
	public static String levelPath = StartService.dicDir + "/level.txt";
	public static void main(String[] args) {
		getResult("陈永兴 诊断 疾病");
//		String match = "match (n1:案件) where (n1.地址 = '*金鹤园小区85栋\\401室家//里*号**' ) return n1";
//		System.out.println(StartService.neo4jHandle.getCypherResult(match));
	}
	
	public static List<Object> getResult(String wString) {
		String[] datas = wString.split(" ");
		List<Map<String, List<String>>> indexRes = new ArrayList<>();
		List<Integer> countRes = new ArrayList<>();
		for (String d: datas){
			Map<String, List<String>> res = dealIndexSearch(creatIndexFromNeo4j.searchIndex(indexPath, d));
			if (res.size() >0){
				indexRes.add(res);
			}
			
		}
		
		
		List<Map<String, List<List<String>>>>  results = fullArrangement(indexRes);
		List<String> resData = new ArrayList<>();
		for (int i = 0; i < results.size(); i++){
			List<String> reString = useSearch(results.get(i));
			if (reString.size() > 0){
//				System.err.println(reString.get(0));
				resData.addAll(reString);
			}
		}		
		List<Map<String, String>> listMap = new ArrayList<>();
    	Map<String, String> m = new HashMap<>();
    	listMap.add(m);
    	m = new HashMap<>();
    	listMap.add(m);
    	List<String> idStrings = new ArrayList<>();
    	for (String res: resData){
    		countEnt(listMap, res, idStrings);
    	}
    	if (idStrings.size() > 0){
	    	String resIds = useIDs(idStrings);
	    	if (!"".equals(resIds)){
	    		resData.add(resIds);
	    	}
    	}
    	Map<String, String> map = listMap.get(1);
    	List<String> keys = new ArrayList<>(map.keySet());
    	for (String key: keys){
    		String cy =  map.get(key) + ") return n";
    		map.remove(key);
    		map.put(key, cy);
    	}
   	
//    	System.err.println(cypher);
    	List<Object> returnData = new ArrayList<>();
    	
    	
    	returnData.add(resData);
    	returnData.add(listMap.get(0));
    	returnData.add(map);
    	
    	return returnData;
	}
	
	/*
	 * 全排列
	 * 处理搜索结果
	 * 针对搜索结果组合
	 * 例如：A 结果 A1,A2 B 结果B1,B2 返回A1,B1 A1,B2 A2,B1 A2,B2
	 * 
	 */
	public static List<Map<String, List<List<String>>>>  fullArrangement(List<Map<String, List<String>>> data){
		List<Map<String, List<List<String>>>> arrangement = new ArrayList<>();
		
		List<Integer> countData = new ArrayList<>();
		List<List<String>> keys = new ArrayList<>();
		for (int i = 0; i < data.size(); i++){
			countData.add(0);
			if (data.get(i).keySet().size() > 0){
				List<String> key = new ArrayList<>(data.get(i).keySet());
				keys.add(key);
			}
		}
		if (keys.size() == 0){
			return arrangement;
		}
		
		while (true){
			int num = data.size() - 1;
			Map<String, List<List<String>>> reStrings = new HashMap<>();
			for (int j = 0; j < countData.size(); j++){
				if (reStrings.containsKey(keys.get(j).get(countData.get(j)))){
					reStrings.get(keys.get(j).get(countData.get(j))).add(data.get(j).get(keys.get(j).get(countData.get(j))));
				} else{
					List<List<String>> list = new ArrayList<>();
					list.add(data.get(j).get(keys.get(j).get(countData.get(j))));
					reStrings.put(keys.get(j).get(countData.get(j)), list);
				}
			}
			arrangement.add(reStrings);
			int count = 0;
			while (true){
				if (countData.get(num) < data.get(num).size() - 1){
					int c = countData.get(num) + 1;
					countData.remove(num);
					countData.add(num, c);
					break;
				}else {
					countData.remove(num);
					countData.add(num, 0);
					num = num - 1;
					count = count + 1;
				}
				if (num == -1 || count == data.size()){
					break;
				}
			}
			if (count == data.size() || num == -1){
				break;
			}
		}
		
		return arrangement;
	}
	
	
	/*
	 * 对搜索结果进行再组合
	 * 将实体名和属性名相同的结果放在一起
	 */
	public static Map<String, List<String>> dealIndexSearch(List<String> datas) {
		Map<String, List<String>> dic = new HashMap<>();
		
		for (String data: datas){
			String[] wStrings = data.split("######");
			if (dic.containsKey(wStrings[0] + "####" + wStrings[1])){
				dic.get(wStrings[0] + "####" + wStrings[1]).add(wStrings[2]);
			}else {
				List<String> wList = new ArrayList<>();
				wList.add(wStrings[2]);
				dic.put(wStrings[0] + "####" + wStrings[1], wList);
			}
		}				
		return dic;
	}
	
	
	/*
	 * 对搜索结果进行组合在Neo4j中进行查询
	 */
	public static List<String> useSearch(Map<String, List<List<String>>>  res) {
		StartService.set();
		List<String> data = new ArrayList<>();
		List<String> ent = new ArrayList<>();
		Map<String, Integer> cyMatches = new HashMap<>();
		List<String> cyWheres = new ArrayList<>();
		String cyWhere = "";
		List<String> relationShip = new ArrayList<>();
		List<String> resKey = new ArrayList<>(res.keySet());
		for (int i = 0; i < res.size(); i ++){
			String[] words = resKey.get(i).split("####");
			if ("实体".equals(words[0])){
				if (!ent.contains(words[1])){
					ent.add(words[1]);
				}
				continue;
			}else if (words[0].contains("%%%")) {
				relationShip.add(words[0].split("%%%")[0]);
				relationShip.add(words[0].split("%%%")[1]);
				relationShip.add(words[1]);
				continue;
			}else if (!ent.contains(words[0])){
				ent.add(words[0]);
			}
			if (!cyMatches.containsKey(words[0])){
				int c = cyMatches.size() + 1;
				cyMatches.put(words[0], c);
			}
			if (!"".equals(cyWhere)){
				cyWhere = cyWhere.substring(0, cyWhere.length() - 3) + ") and (";
			}else {
				cyWhere = "(";
			}
			
			for (int j = 0; j < res.get(resKey.get(i)).size(); j++){
				String cy = "(";
				for (int k = 0; k < res.get(resKey.get(i)).get(j).size(); k++){
					if (res.get(resKey.get(i)).get(j).get(k).contains(".+")){
						cy = cy + "n" + cyMatches.get(words[0]) + "." + words[1]  + " =~ '" + res.get(resKey.get(i)).get(j).get(k) + "' or ";
						cyWhere = cyWhere + "n" + cyMatches.get(words[0]) + "." + words[1]  + " =~ '" + res.get(resKey.get(i)).get(j).get(k) + "' or ";
					}else {
						cy = cy + "n" + cyMatches.get(words[0]) + "." + words[1]  + " = '" + res.get(resKey.get(i)).get(j).get(k) + "' or ";
						cyWhere = cyWhere + "n" + cyMatches.get(words[0]) + "." + words[1]  + " = '" + res.get(resKey.get(i)).get(j).get(k) + "' or ";
					}	
				}
				cyWheres.add(cy.substring(0, cy.length() - 3));
			}
			
				
		}
		
		if (cyWhere.matches(".* or ")){
			cyWhere = cyWhere.substring(0, cyWhere.length() - 3);
		}
		
		if (!"".equals(cyWhere)){
			cyWhere = "where " + cyWhere + ")";
		}
		
		for (int i = 0; i < cyWheres.size(); i++){
			if (cyWheres.get(i).matches(".* or ")){
				String cy = cyWheres.get(i);
				cy = cy.substring(0, cy.length() - 3);
				cyWheres.remove(i);
				cyWheres.add(i, cy);
			}
		}
//			System.out.println(cyWhere + ")");
		
		
		
		if (ent.size() > 1 && relationShip.size() == 0){//处理多个实体的查询
			
			List<String> results = searchAll.dealMoreEnt(ent, cyMatches, cyWhere);
			if (results.size() > 0){
				data.addAll(results);
			}
//			List<String> paths = graphRel.getPathR(ent);//计算实体的路径
//			for (String path: paths){
//				String cypher = "";
//				for (String key: cyMatches.keySet()){
//					path = path.replace(":"+ key, "n" + cyMatches.get(key) + ":" + key);
//				}
//				cypher = "match p = " + path + " " + cyWhere + " return p";
//				System.out.println("0:" + cypher);
//				String result = StartService.neo4jHandle.getCypherResult(cypher);
//				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//					data.add(result);
//				}
//				
//			}
			
		}else if (ent.size() == 1 && relationShip.size() == 0){//处理只有一个实体的查询
			String result = searchAll.dealEntOne(ent, cyWhere);
			if (!"".equals(result)){
				data.add(result);
			}
//			String cypher = "";			
//			cypher = "match (n1:" + ent.get(0) + ") " + cyWhere + " return n1";
//			String result = StartService.neo4jHandle.getCypherResult(cypher);
//			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//				data.add(result);
//			}			
			
			//两实体之间存在关系，需要自动添加此处需要修改
		}else if (relationShip.size() == 3){	
			//关系查询，只处理查询内容为一个属性一个关系或没有属性
			if (ent.size() == 1){//处理查询内容为一个属性一个关系
				String result = searchAll.dealRelEnt(ent, cyWhere, relationShip);
				if (!"".equals(result)){
					data.add(result);
				}
//				if (ent.get(0).equals(relationShip.get(0))){
//					cypher = "";
//					cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") " + cyWhere + " return p";
//					String result = StartService.neo4jHandle.getCypherResult(cypher);
//					if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//						data.add(result);
//					}
//				}else if (ent.get(0).equals(relationShip.get(1))){
//					cypher = "";
//					cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(0)+ ") " + cyWhere + " return p";
//					String result = StartService.neo4jHandle.getCypherResult(cypher);
//					if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//						data.add(result);
//					}
//				}else {
//					cypher = "";
//					cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(0)+ ") " + cyWhere + " return p";
//					String result = StartService.neo4jHandle.getCypherResult(cypher);
//					if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//						data.add(result);
//					}else{
//						cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") " + cyWhere + " return p";
//						result = StartService.neo4jHandle.getCypherResult(cypher);
//						if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//							data.add(result);
//						}
//					}
//				}
			}else if (ent.size() == 0){//处理查询只有关系
				String result = searchAll.dealRelOnly(relationShip);				
				if (!"".equals(result)){
					data.add(result);
				}
//				cypher = "";
//				cypher = "match p = (:" + relationShip.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") return p";
//				String result = StartService.neo4jHandle.getCypherResult(cypher);
//				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//					data.add(result);
//				}
			}else if (ent.size() == 2){
				String result = searchAll.dealRelEntTwo(relationShip, ent, cyWhere, cyMatches);
				if (!"".equals(result)){
					data.add(result);
				}
			}
		}
		
		if (data.size() == 0){
			if (ent.size() == 2 && relationShip.size() == 0){
				String result = searchAll.getShortPathTwo(ent, cyWhere);
				if (!"".equals(result)){
					data.add(result);
				}
//				String cypher = "";
//				cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(1) + ")) " + cyWhere + " return p";
//				System.out.println("3:" + cypher);
//				String result = StartService.neo4jHandle.getCypherResult(cypher);
//				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//					data.add(result);
//				}
			}
		}
		
		if (ent.size() == 1 && cyWheres.size() == 2){
			String result = searchAll.getShortPathOne(ent, cyWheres);
			if (!"".equals(result)){
				data.add(result);
			}
//			String cypher = "";
////			cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(0) + ")) where " + cyWheres.get(0).substring(0, cyWheres.get(0).length() - 4) + ") and "+ cyWheres.get(1).substring(0, cyWheres.get(1).length() - 4).replace("n1", "n2") + ") return p";
//			cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(0) + ")) where " + cyWheres.get(0) + ") and "+ cyWheres.get(1).replace("n1", "n2") + ") return p";
//			System.out.println("4:" + cypher);
//			String result = StartService.neo4jHandle.getCypherResult(cypher);
//			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
//				data.add(result);
//			}
		}
		
		return data;
	}
	
	/*
	 * 获取需要统计的label
	 * 只统计二级标签
	 */
	public static List<String> getLabel() {
		List<String> list = new ArrayList<>();
		try {
			Map<String, String> dic = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(new File(levelPath)));
			String line = "";
			while ((line = reader.readLine()) != null){
				String[] wStrings = line.split(":");
				dic.put(wStrings[0], wStrings[1]);
			}
			
			for (String key: dic.keySet()){
				boolean find = false;
				for (String value: dic.values()){
					if (value.contains(key)){
						find = true;
						break;
					}
				}
				if (!find){
					String[] words = dic.get(key).split("\t");
					for (String w: words){
						list.add(w);
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;		
	}
	
	
	/*
	 * 查询结果实体统计
	 */
	public static void countEnt(List<Map<String, String>> list, String data, List<String> idStrings){		
    	JSONObject json = JSONObject.parseObject(data);
    	String dataArr =  json.getString("results");
    	Map<String, String> map = list.get(0);
    	Map<String, String> map2 = list.get(1);
    	List<String> labelAll = getLabel();
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
				label = label.substring(2, label.length() - 2);
	//				System.err.println("=="+label);
				for (String all: labelAll){
					if (label.contains(all)){
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
}
