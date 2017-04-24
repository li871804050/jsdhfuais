package com.swt.ajss.restful.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 
 * @author Administrator
 * GraphPath 路径计算接口
 * 调用	OntologyAnalyze的relation
 */
public class GraphPath {
	
	
	public static void main(String[] args) {
		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/20170417.owl");
		List<String> paths = getPathSE("人员", "人员_1");
		for (String path: paths){
			System.out.println(path);
		}
	}
	
	/*
	 * 返回节点start到end的所有路径
	 */
	/**
	 * 
	 * @param start	起始实体
	 * @param end	终止实体
	 * @return	所有路径
	 */
	public static List<String> getPathSE(String start, String end) {
		List<Object> rList = getRelationGraph();
		List<String> resultAll = new ArrayList<>();
		HashMap<String, List<String>> rMap = (HashMap<String, List<String>>) rList.get(0);
		HashMap<String, List<String>> entMap = (HashMap<String, List<String>>) rList.get(1);
		List<List<String>> paths = new ArrayList<>();//有用路径
		Map<String, List<String>> allPath = new HashMap<>();//所有路径，过度作用
		getPath(entMap, "", start, end, allPath, paths);
		
		for (int i = 0; i < paths.size(); i++){
			String cy = "";
			List<List<String>> allRel = new ArrayList<>(); 
			for (int j = 0; j < paths.get(i).size() - 1; j++){

				allRel.add(rMap.get(paths.get(i).get(j) + "-" + paths.get(i).get(j + 1)));
			}
//			cy = cy + res.get(i).get(res.get(i).size() - 1);
//			System.err.println(cy);
			//多种关系处理
			List<Integer> countRel = new ArrayList<>();
			for (int k = 0; k < allRel.size(); k++){
				countRel.add(0);
			}
			
			while (true && countRel.size() > 0){
				int num = countRel.size() - 1;
				String mString = cy;
				cy = "";
				for (int k = 0; k < paths.get(i).size() - 1; k++){
					cy = cy + "(:" + paths.get(i).get(k) + ")-[:" + rMap.get(paths.get(i).get(k) + "-" + 
							paths.get(i).get(k + 1)).get(countRel.get(k)) + "]-";
				}
				cy = cy + "(:" + paths.get(i).get(paths.get(i).size() - 1) + ")";
				resultAll.add(cy);
				int count = 0;
				while (true){
					if (countRel.get(num) < allRel.get(num).size() - 1){
						int c = countRel.get(num) + 1;
						countRel.remove(num);
						countRel.add(num, c);
						break;
					}else {
						countRel.remove(num);
						countRel.add(num, 0);
						count = count + 1;
						num = num - 1;
					}
					if (num == -1 || count == countRel.size()){
						break;
					}
				}
				if (count == countRel.size() || num == -1){
					break;
				}
			}
		}
		return resultAll;
	}
	
	
	/*
	 * 返回关系组成的图数据
	 * rMap保存的为关系对应的节点信息 键为A-B 值为关系类型
	 */
	
	/**
	 * relationMap key为实体1-实体2 value为所有关系组成的数组的map
	 * entMap key为实体 value 为可以到达的实体组成的数组
	 * 两map组成的数组
	 * @return 
	 */
	public static List<Object> getRelationGraph(){
		List<Object> result = new ArrayList<>();
		HashMap<String, List<String>> relationMap = new HashMap<>();
		HashMap<String, List<String>> entMap = new HashMap<>();
		List<String> relatons = OntologyAnalyzer.getRelation();
		List<String> same = new ArrayList<>();
		for (int i = 0; i < relatons.size(); i++){
			String[] wString = relatons.get(i).split("-");
			boolean theSame = false;
			//同类实体之间关系第二个实体命名添加_1
			if (wString[0].equals(wString[1])){
				theSame = true;
				wString[1] = wString[1] + "_1";				
				
				//处理重复关系 补全
				if (!relationMap.containsKey(wString[0] + "-" + wString[1])){
					List<String> keys = new ArrayList<>(relationMap.keySet());
					for (String rel: keys){
						String[] relEnt = rel.split("-");
						if (wString[0].equals(relEnt[0])){
							relationMap.put(wString[1] + "-" + relEnt[1], relationMap.get(rel));
						} else if (wString[0].equals(relEnt[1]) ) {
							relationMap.put(relEnt[0] + "-" + wString[1], relationMap.get(rel));
						}
					}
				}
				if (!entMap.containsKey(wString[1])){
					List<String> keys = new ArrayList<>(entMap.keySet());
					for (String ent: entMap.keySet()){
						if (wString[0].equals(ent)){
							entMap.put(wString[1], entMap.get(ent));
							break;
						}
					}
				}
			}
			if (!theSame){
				if (same.contains(wString[0])){
					addRelationMap(relationMap, wString);
					addEntMap(entMap, wString);
					wString[0] = wString[0] + "_1";
					addRelationMap(relationMap, wString);
					addEntMap(entMap, wString);
				} else if (same.contains(wString[1])){
					addRelationMap(relationMap, wString);
					addEntMap(entMap, wString);
					wString[1] = wString[1] + "_1";
					addRelationMap(relationMap, wString);
					addEntMap(entMap, wString);
				} else {
					addRelationMap(relationMap, wString);
					addEntMap(entMap, wString);
				}
			}else {
				addRelationMap(relationMap, wString);
				addEntMap(entMap, wString);
			}
		
			if (theSame && !same.contains(wString[0])){
				same.add(wString[0]);
			}
		}
		result.add(relationMap);
		result.add(entMap);
		return result;
	}
	
	
	/*
	 * 路径求解函数
	 */
	/**
	 * 
	 * @param entMap	map存储的图
	 * @param fa		上一处理节点
	 * @param start		起始节点
	 * @param end		终止节点
	 * @param all		所有已处理节点
	 * @param result	结果
	 */
	public static void getPath(HashMap<String, List<String>> entMap, String fa, String start, String end, 
			Map<String, List<String>> all, List<List<String>> result) {
		if (entMap.containsKey(start)){
			List<String> path = new ArrayList<>();
			if (all.containsKey(fa + "-" + start)){
				path = all.get(fa + "-" + start);
			}
			path.add(start);
//			if (all.containsKey(start)){
//				path = all.get(start);
//			}else {
//				all.put(start, path);
//			}
			List<String> ents = entMap.get(start);
			for (String ent: ents){
				List<String> bl = new ArrayList<>(path);
				all.put(start + "-" + ent, bl);
			}
			for (String ent: ents){
				if (path.contains(ent)){
					continue;
				}else if (ent.equals(end)) {
					path.add(end);
					result.add(path);
				}else {
					getPath(entMap, start, ent, end, all, result);
				}
			}
		}
	}
	
	
	
	/*
	 * 带关系的路径
	 */
	public static List<String> getPathRelation(List<String> datas) {
		List<String> all = new ArrayList<>();
		int i = 0;
		int j = i + 1;
		List<String> allList = new ArrayList<>();
		while (true){
			all = getPathSE(datas.get(i), datas.get(j));
			allList.addAll(all);
			if (j >= datas.size() - 1){
				i = i + 1;
				j = i + 1;
			}else {
				j = j + 1;
			}
			if (i == datas.size() - 1){
				break;
			}
		}
		all.clear();
		for (int m = 0; m < allList.size(); m++){
			int count = 0;
			for (int n = 0; n < datas.size(); n++){
				if (allList.get(m).contains(datas.get(n))){
					count += 1;
				}else {
					break;
				}
			}
			if (count == datas.size()){
				all.add(allList.get(m));
			}
		}
		return all;
		
	}
	
	
	/**
	 * 
	 * @param relationMap
	 * @param wString
	 */
	public static void addRelationMap(HashMap<String, List<String>> relationMap, String[] wString) {
		if (relationMap.containsKey(wString[0] + "-" + wString[1])){
			if (!relationMap.get(wString[0] + "-" + wString[1]).contains(wString[2])){
				relationMap.get(wString[0] + "-" + wString[1]).add(wString[2]);
			}				
		}else {
			List<String> reList = new ArrayList<>();
			reList.add(wString[2]);
			relationMap.put(wString[0] + "-" + wString[1], reList);
		}
		
		if (relationMap.containsKey(wString[1] + "-" + wString[0])){
			if (!relationMap.get(wString[1] + "-" + wString[0]).contains(wString[2])){
				relationMap.get(wString[1] + "-" + wString[0]).add(wString[2]);
			}				
		}else {
			List<String> reList = new ArrayList<>();
			reList.add(wString[2]);
			relationMap.put(wString[1] + "-" + wString[0], reList);
		}
	}
	
	
	/**
	 * 
	 * @param entMap 
	 * @param wString
	 */
	public static void addEntMap(HashMap<String, List<String>> entMap, String[] wString) {
		if (entMap.containsKey(wString[0]) && !entMap.get(wString[0]).contains(wString[1])){
			entMap.get(wString[0]).add(wString[1]);
		}else if (!entMap.containsKey(wString[0])) {
			List<String> eList = new ArrayList<>();
			eList.add(wString[1]);
			entMap.put(wString[0], eList);
		}
		
		if (entMap.containsKey(wString[1]) && !entMap.get(wString[1]).contains(wString[0])){
			entMap.get(wString[1]).add(wString[0]);
		}else if (!entMap.containsKey(wString[1])) {
			List<String> eList = new ArrayList<>();
			eList.add(wString[0]);
			entMap.put(wString[1], eList);
		}
		
	}
}
