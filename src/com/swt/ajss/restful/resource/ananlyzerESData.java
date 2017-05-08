package com.swt.ajss.restful.resource;

import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ananlyzerESData {
	
	public static void main(String[] args) {
		String eString = "[{\"ent1\":{\"field\":\"车次\",\"term\":\"D3068\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20150222\"},\"rel\":{\"count\":1}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name9\"},\"ent2\":{\"field\":\"车次\",\"term\":\"D3068\"},\"rel\":{\"count\":1}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name10\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20150222\"},\"rel\":{\"count\":1}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name9\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20150222\"},\"rel\":{\"count\":1}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name10\"},\"ent2\":{\"field\":\"车次\",\"term\":\"D3068\"},\"rel\":{\"count\":1}},{\"ent1\":{\"field\":\"车次\",\"term\":\"D5242\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20160704\"},\"rel\":{\"count\":4}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name9\"},\"ent2\":{\"field\":\"车次\",\"term\":\"D5242\"},\"rel\":{\"count\":4}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name10\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20160704\"},\"rel\":{\"count\":4}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name9\"},\"ent2\":{\"field\":\"日期\",\"term\":\"20160704\"},\"rel\":{\"count\":4}},{\"ent1\":{\"field\":\"姓名\",\"term\":\"name10\"},\"ent2\":{\"field\":\"车次\",\"term\":\"D5242\"},\"rel\":{\"count\":4}}]";
		System.out.println(ananlyzerESData.anaES(eString));
	}
	
	public static String anaES(String ESData) {
		JSONArray arrayAll = JSONArray.parseArray(ESData);
		int id = 0;
		JSONObject object1 = new JSONObject();
		JSONObject object2 = new JSONObject();
		HashMap<String, Integer> map = new HashMap<>();
		JSONArray graphs = new JSONArray();
		for (int i = 0; i < arrayAll.size(); i++){
			JSONObject esObject = arrayAll.getJSONObject(i);
			JSONObject datas2 = new JSONObject();
			JSONObject datas = new JSONObject();
			JSONArray nodes = new JSONArray();

			JSONObject ent1 = esObject.getJSONObject("ent1");
			JSONObject ent2 = esObject.getJSONObject("ent2");
			JSONObject rel = esObject.getJSONObject("rel");
			JSONObject node1 = new JSONObject();
			if (!map.containsKey(ent1.getString("field") + "-" + ent1.getString("term"))){
				id = id - 1;
				node1 = esDataToNode(ent1, id);
			}else {
				node1 = esDataToNode(ent1, map.get(ent1.getString("field") + "-" + ent1.getString("term")));
			}
			JSONObject node2 = new JSONObject();
			if (!map.containsKey(ent2.getString("field") + "-" + ent2.getString("term"))){
				id = id - 1;
				node2 = esDataToNode(ent2, id);
			}else {
				node2 = esDataToNode(ent2, map.get(ent2.getString("field") + "-" + ent2.getString("term")));
			}
			

			id = id - 1;
			JSONObject rel2 = new JSONObject();
			rel2.put("id", id);
			rel2.put("type", rel.get("count"));
			JSONObject object = new JSONObject();
			rel2.put("properties", object);
			rel2.put("startNode", map.get(ent1.getString("field") + "-" + ent1.getString("term")));
			rel2.put("endNode", map.get(ent2.getString("field") + "-" + ent2.getString("term")));
			nodes.add(node1);
			nodes.add(node2);
			datas.put("nodes", nodes);
			datas.put("relationships", rel2);
			datas2.put("graph", datas);
			graphs.add(datas2);
		}
		object1.put("data", graphs);
		JSONArray array3 = new JSONArray();
		array3.add("p");
		object1.put("columns", array3);
		JSONArray array = new JSONArray();
		array.add(object1);
		object2.put("results", array);
		JSONArray array2 = new JSONArray();
		object2.put("errors", array2);
		return object2.toJSONString().replace("\\", "");
	}
	
	
	public static JSONObject esDataToNode(JSONObject ent, int id) {
		JSONObject node = new JSONObject();
		node .put("id", id);
		JSONArray array = new JSONArray();
		array.add(ent.get("field"));
		node .put("labels", array);
		JSONObject object2 = new JSONObject();
		object2.put("term", node.getString("term"));
		node.put("properties", object2);
		return node;
	}
}
