package com.swt.ajss.restful.resource;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.StartService;

public class analyzerDrug {
	
	public static void main(String[] args) {
		shenji();
	}
	
	public static List<String> useDrug() {
		List<String> result = new ArrayList<>();
		String data = StartService.neo4jHandle.getCypherResult("match (n:患者)-[r:用药]-(m:药品) where r.单日用量 > m.单日用量 return r,m,n");
		result.add(data);
		result.add(dealData(data));
		return result;
	}
	
	public static String dealData(String data) {
		JSONObject resultsAll = new JSONObject();
		JSONObject head = new JSONObject();
		head.put("title1", "药品名");
		head.put("title2", "实际用药");
		head.put("title3", "最大用量");
		resultsAll.put("head", head);
		JSONObject res = JSONObject.parseObject(data);
		String reString = res.getString("results");
		JSONObject results = JSONObject.parseObject(reString.substring(1, reString.length() - 1));
		JSONArray array = results.getJSONArray("data");
		List<List<String>> all = new ArrayList<>();
		JSONArray resArray = new JSONArray();
		for (int i = 0; i < array.size(); i++){
			JSONObject object = array.getJSONObject(i);
			JSONObject graph = object.getJSONObject("graph");
			JSONArray nodes = graph.getJSONArray("nodes");			
			String id0 = nodes.getJSONObject(0).getString("id");
			String id1 = nodes.getJSONObject(1).getString("id");
			String drug = "";
			String useDrug = "";
			if (nodes.getJSONObject(0).getString("labels").contains("药品")){
				JSONObject prObject = nodes.getJSONObject(0).getJSONObject("properties");
				drug = prObject.getString("通用名称");
				useDrug = prObject.getString("单日用量");
			}
			else if (nodes.getJSONObject(1).getString("labels").contains("药品")){
				JSONObject prObject = nodes.getJSONObject(1).getJSONObject("properties");
				drug = prObject.getString("通用名称");
				useDrug = prObject.getString("单日用量");
			}			
			String cypher = "start n = node (" + id0 + "), m = node (" + id1 + ") match (n)-[r:用药]-(m) where r.单日用量 > m.单日用量  or r.单日用量 > n.单日用量 return r,m,n";	
			
			String rel = graph.getString("relationships");
			JSONObject relation = JSONObject.parseObject(rel.substring(1, rel.length() - 1));
			String useToday = relation.getJSONObject("properties").getString("单日用量");
			JSONObject object2 = new JSONObject();
			object2.put("title1", drug);
			object2.put("title2", useToday);
			object2.put("title3", useDrug);
			object2.put("cypher", cypher);
			resArray.add(object2);
		}
		resultsAll.put("data", resArray);
		return resultsAll.toJSONString();
	}
	
	
	public static List<String> peiWu() {
		List<String> result = new ArrayList<>();
		String data = StartService.neo4jHandle.getCypherResult("match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:用药]-(m2:药品),(m1)-[r3:配伍禁忌]-(m2) return n,m1,m2,r1,r2,r3");
		result.add(data);
		result.add(dealDataPeiwu(data));
		return result;
	}
	
	public static String dealDataPeiwu(String data) {
		
		JSONObject resultsAll = new JSONObject();
		JSONObject head = new JSONObject();
		head.put("title1", "药品1");
		head.put("title2", "药品2");
		head.put("title3", "用药时间");
		resultsAll.put("head", head);
		JSONObject res = JSONObject.parseObject(data);
		String reString = res.getString("results");
		JSONObject results = JSONObject.parseObject(reString.substring(1, reString.length() - 1));
		JSONArray array = results.getJSONArray("data");
		List<List<String>> all = new ArrayList<>();
		JSONArray resArray = new JSONArray();
		for (int i = 0; i < array.size(); i++){
			JSONObject object = array.getJSONObject(i);
			JSONObject graph = object.getJSONObject("graph");
			JSONArray nodes = graph.getJSONArray("nodes");			
			String id0 = "";
			String id1 = "";
			String id2 = "";
			String drug1 = "", drug2 = "", useTime = "";
			for (int j = 0; j < 3; j++){
				if (drug1 == null || "".equals(drug1)){
					drug1 = nodes.getJSONObject(j).getJSONObject("properties").getString("通用名称");
				}else if("".equals(drug2) || drug2 == null){
					drug2 = nodes.getJSONObject(j).getJSONObject("properties").getString("通用名称");
				}
				if (nodes.getJSONObject(j).getString("labels").contains("药品")){
					if ("".equals(id1) || id1 == null){
						id1= nodes.getJSONObject(j).getString("id");
					}else if ("".equals(id2) || id2== null){
						id2= nodes.getJSONObject(j).getString("id");
					}
				}else if (nodes.getJSONObject(j).getString("labels").contains("患者")){
					id0 = nodes.getJSONObject(j).getString("id");
				}
				JSONArray rel = graph.getJSONArray("relationships");
				JSONObject relation = rel.getJSONObject(j);
				if ("用药".equals(relation.getString("type"))){
					useTime = relation.getJSONObject("properties").getString("用药时间");
				}
			}
			String cypher = "start n = node (" + id0 + "), m1 = node (" + id1 + "), m2 = node (" + id2 + ") match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:用药]-(m2:药品),(m1)-[r3:配伍禁忌]-(m2) return n,m1,m2,r1,r2,r3";				
			
			JSONObject object2 = new JSONObject();
			object2.put("title1", drug1);
			object2.put("title2", drug2);
			object2.put("title3", useTime);
			object2.put("cypher", cypher);
			resArray.add(object2);
		}
		resultsAll.put("data", resArray);
		return resultsAll.toJSONString();
	}
	
	
	public static List<String> jinji() {
		List<String> result = new ArrayList<>();
		String data = StartService.neo4jHandle.getCypherResult("match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:诊断]-(m2:疾病),(m1)-[r3:禁忌疾病]-(m2) return n,m1,m2,r1,r2,r3");
		result.add(data);
		result.add(dealDatajinji(data));
		return result;
	}
	
	public static String dealDatajinji(String data) {
//		System.out.println(data);
		JSONObject resultsAll = new JSONObject();
		JSONObject head = new JSONObject();
		head.put("title1", "疾病");
		head.put("title2", "药品");
		head.put("title3", "用药时间");
		resultsAll.put("head", head);
		JSONObject res = JSONObject.parseObject(data);
		String reString = res.getString("results");
		JSONObject results = JSONObject.parseObject(reString.substring(1, reString.length() - 1));
		JSONArray array = results.getJSONArray("data");
		List<List<String>> all = new ArrayList<>();
		JSONArray resArray = new JSONArray();
		for (int i = 0; i < array.size(); i++){
			JSONObject object = array.getJSONObject(i);
			JSONObject graph = object.getJSONObject("graph");
			JSONArray nodes = graph.getJSONArray("nodes");			
			String iddrug = nodes.getJSONObject(0).getString("id");
			String iddise = nodes.getJSONObject(1).getString("id");
			String idpeo = nodes.getJSONObject(2).getString("id");
			String drug = "", disease = "";
			for (int j = 0; j < 3; j++){
				if ("".equals(drug) || drug == null){
					drug = nodes.getJSONObject(j).getJSONObject("properties").getString("通用名称");
				}
				if ("".equals(disease) || disease == null){
					disease = nodes.getJSONObject(j).getJSONObject("properties").getString("疾病名");	
				}
				if (nodes.getJSONObject(j).getString("labels").contains("疾病")){
					iddise = nodes.getJSONObject(j).getString("id");
				}else if (nodes.getJSONObject(j).getString("labels").contains("药品")){
					iddrug = nodes.getJSONObject(j).getString("id");
				}else if (nodes.getJSONObject(j).getString("labels").contains("患者")){
					idpeo = nodes.getJSONObject(j).getString("id");
				}
			}
			
			JSONArray rel = graph.getJSONArray("relationships");
			String useTime = "";
			for (int j = 0; j < 3; j++){
				if ("".equals(useTime) || useTime == null){
					JSONObject relation = rel.getJSONObject(j);
					useTime = relation.getJSONObject("properties").getString("用药时间");
				}else {
					break;
				}
			}
			
			String cypher = "start n = node (" + idpeo + "), m1 = node (" + iddrug + "), m2 = node (" + iddise + ") match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:诊断]-(m2:疾病),(m1)-[r3:禁忌疾病]-(m2) where r1.用药时间 = '" + useTime + "'return n,m1,m2,r1,r2,r3";				
			
			JSONObject object2 = new JSONObject();
			object2.put("title2", drug);
			object2.put("title1", disease);
			object2.put("title3", useTime);
			object2.put("cypher", cypher);
			resArray.add(object2);
		}
		resultsAll.put("data", resArray);
//		System.out.println(resultsAll.toJSONString());
		return resultsAll.toJSONString();
	}
	
	public static String shenji() {
		StartService.set();
		String data = StartService.connection.exectCypher1("match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:诊断]-(m2:疾病),(m1)-[r3:禁忌疾病]-(m2) return count(r1)");
		JSONObject objects = JSONObject.parseObject(data);
		String count = objects.getString("data");
		count = count.substring(2, count.length() - 2);
		
		
		String data1 = StartService.connection.exectCypher1("match (n:患者)-[r:用药]-(m:药品) where r.单日用量 > m.单日用量 return count(r)");
		JSONObject objects1 = JSONObject.parseObject(data1);
		String count1 = objects1.getString("data");
		count1 = count1.substring(2, count1.length() - 2);
		
		
		String data2 = StartService.connection.exectCypher1("match p = (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:用药]-(m2:药品),(m1)-[r3:配伍禁忌]-(m2) return count(r3)");
		JSONObject objects2 = JSONObject.parseObject(data2);
		String count2 = objects2.getString("data");
		count2 = count2.substring(2, count2.length() - 2);
		
		
		JSONObject object = new JSONObject();
		object.put("left", "");
		JSONObject resultsAll = new JSONObject();
		JSONObject head = new JSONObject();
		head.put("title1", "审计");
		head.put("title2", "审计结果");
		head.put("title3", "");
		resultsAll.put("head", head);
		JSONArray resArray = new JSONArray();
		JSONObject object2 = new JSONObject();
		object2.put("title1", "配伍禁忌");
		object2.put("title2", count2);
		object2.put("title3", "");
		object2.put("cypher", "match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:用药]-(m2:药品),(m1)-[r3:配伍禁忌]-(m2) return n,m1,m2,r1,r2,r3");
		resArray.add(object2);
		JSONObject object3 = new JSONObject();
		object3.put("title1", "过度用药");
		object3.put("title2", count1);
		object3.put("title3", "");
		object3.put("cypher", "match (n:患者)-[r:用药]-(m:药品) where r.单日用量 > m.单日用量 return r,m,n");
		resArray.add(object3);
		JSONObject object4 = new JSONObject();
		object4.put("title1", "用药禁忌");
		object4.put("title2", count);
		object4.put("title3", "");
		object4.put("cypher", "match (n:患者)-[r1:用药]-(m1:药品),(n:患者)-[r2:诊断]-(m2:疾病),(m1)-[r3:禁忌疾病]-(m2) return n,m1,m2,r1,r2,r3");
		resArray.add(object4);
		resultsAll.put("data", resArray);
		object.put("right", resultsAll);
		return object.toJSONString();
	}
	
}
