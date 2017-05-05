package com.swt.ajss.restful.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Administrator
 * IndexResultDeal 查询结果处理类
 */
public class IndexResultDeal {
	
	
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
			List<String> listEnt = new ArrayList<>();
			for (int j = 0; j < countData.size(); j++){
				List<List<String>> list = new ArrayList<>();
				list.add(data.get(j).get(keys.get(j).get(countData.get(j))));
				String ent = keys.get(j).get(countData.get(j)).split(GraphIndex.LINK_1)[0];
				if (listEnt.contains(ent)){
					reStrings.put(keys.get(j).get(countData.get(j)).replace(ent, ent + "_1"), list);
				}else {
					reStrings.put(keys.get(j).get(countData.get(j)), list);
					listEnt.add(ent);
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
	
	

	/**
	 * 
	 * @param datas 索引文件中查询结果
	 * @return 相同实体名和属性名作为Key 属性值作为value的Map
	 * 整理索引查找结果
	 */
	public static Map<String, List<String>> dealIndexSearch(List<String> datas) {
		Map<String, List<String>> dic = new HashMap<>();
		
		for (String data: datas){
			String[] wStrings = data.split(GraphIndex.LINK_1);
			if (dic.containsKey(wStrings[0] + GraphIndex.LINK_1 + wStrings[1])){
				dic.get(wStrings[0] + GraphIndex.LINK_1 + wStrings[1]).add(wStrings[2]);
			}else {
				List<String> wList = new ArrayList<>();
				wList.add(wStrings[2]);
				dic.put(wStrings[0] + GraphIndex.LINK_1 + wStrings[1], wList);
			}
		}				
		return dic;
	}
	
}
