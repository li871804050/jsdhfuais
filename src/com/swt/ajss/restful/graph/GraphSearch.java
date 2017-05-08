package com.swt.ajss.restful.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swt.ajss.restful.service.StartService;

/**
 * 
 * @author Administrator
 * GraphSearch 图查找
 */
public class GraphSearch {
	public static String indexPath = StartService.dicDir + "/index2";
//	public static String levelPath = StartService.dicDir + "/level.txt";
	
	
	public static void main(String[] args) {
		StartService.set();
//		System.out.println(StartService.neo4jHandle.getCypherResult("MATCH ()-[r:`子公司`]->(m)-[:生产]-(n) RETURN  m,n limit 5"));
		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/20140427.owl");
		getResult("李兰清 王梓怡");
	}
	
	/**
	 * 
	 * @param wString 待查询内容
	 * @return 
	 */
	public static List<Object> getResult(String wString) {
		String[] datas = wString.split(" ");
		List<Map<String, List<String>>> indexRes = new ArrayList<>();
		
		for (String d: datas){
			Map<String, List<String>> res = IndexResultDeal.dealIndexSearch(GraphIndex.searchIndex(indexPath, d));
			if (res.size() >0){
				indexRes.add(res);
			}
			
		}
		
		
		List<Map<String, List<List<String>>>>  results = IndexResultDeal.fullArrangement(indexRes);
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
    		GraphData.countEnt(listMap, res, idStrings);
    	}
//    	if (idStrings.size() > 0){
//	    	String resIds = GraphData.useIDs(idStrings);
//	    	if (!"".equals(resIds)){
//	    		resData.add(resIds);
//	    	}
//    	}
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
			String[] words = resKey.get(i).split(GraphIndex.LINK_1);
			if (GraphIndex.KEYWORDS_1.equals(words[0])){
				if (!ent.contains(words[1])){
					ent.add(words[1]);
				}
				continue;
			}else if (words[0].contains(GraphIndex.LINK_2)) {
				relationShip.add(words[0].split(GraphIndex.LINK_2)[0]);
				relationShip.add(words[0].split(GraphIndex.LINK_2)[1]);
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
						cy = cy + "n" + cyMatches.get(words[0]) + "." + words[1]  + " =~ '" + 
								res.get(resKey.get(i)).get(j).get(k) + "' or ";
						cyWhere = cyWhere + "n" + cyMatches.get(words[0]) + "." + words[1]  + " =~ '" + 
								res.get(resKey.get(i)).get(j).get(k) + "' or ";
					}else {
						cy = cy + "n" + cyMatches.get(words[0]) + "." + words[1]  + " = '" + 
								res.get(resKey.get(i)).get(j).get(k) + "' or ";
						cyWhere = cyWhere + "n" + cyMatches.get(words[0]) + "." + words[1]  + " = '" + 
								res.get(resKey.get(i)).get(j).get(k) + "' or ";
					}	
				}
				cyWheres.add(cy.substring(0, cy.length() - 3));
			}
			
				
		}
		
		if (cyWhere.matches(".* or ")){
			cyWhere = cyWhere.substring(0, cyWhere.length() - 3);
		}
		if (cyWhere.matches(".*_1.*")){
			cyWhere = cyWhere.replace("_1", "");
		}
		
		if (!"".equals(cyWhere)){
			cyWhere = "where " + cyWhere + ")";
		}
		
		for (int i = 0; i < cyWheres.size(); i++){
			if (cyWheres.get(i).matches(".* or ")){
				String cy = cyWheres.get(i);
				cy = cy.substring(0, cy.length() - 3);
				cyWheres.set(i, cy);
			}
		}
			
		
		
		
		if (ent.size() > 1 && relationShip.size() == 0){//处理多个实体的查询
			
			List<String> results = dealMoreEnt(ent, cyMatches, cyWhere);
			if (results.size() > 0){
				data.addAll(results);
			}else {
				String result = "";
				if (ent.size() == 2){
					result = getShortPathTwo(ent, cyWhere);
				}
				if (!"".equals(result)){
					data.add(result);
				}else {
					result = getNodes(ent, cyMatches, cyWhere);
					data.add(result);
				}
			}
		}else if (ent.size() == 1 && relationShip.size() == 0){//处理只有一个实体的查询
			String result = dealEntOne(ent, cyWhere);
			if (!"".equals(result)){
				data.add(result);
			}				
			//两实体之间存在关系，需要自动添加此处需要修改
		}else if (relationShip.size() == 3){	
			//关系查询，只处理查询内容为一个属性一个关系或没有属性
			if (ent.size() == 1){//处理查询内容为一个属性一个关系
				String result = dealRelEnt(ent, cyWhere, relationShip);
				if (!"".equals(result)){
					data.add(result);
				}
			}else if (ent.size() == 0){//处理查询只有关系
				String result = dealRelOnly(relationShip);				
				if (!"".equals(result)){
					data.add(result);
				}
			}else if (ent.size() == 2){
				String result = dealRelEntTwo(relationShip, ent, cyWhere, cyMatches);
				if (!"".equals(result)){
					data.add(result);
				}
			}
		}
		
		if (data.size() == 0){
			if (ent.size() == 2 && relationShip.size() == 0){
				String result = getShortPathTwo(ent, cyWhere);
				if (!"".equals(result)){
					data.add(result);
				}
			}
		}
		
		if (ent.size() == 1 && cyWheres.size() == 2){
			ent.add(ent.get(0) + "_1");
			cyMatches.put(ent.get(0) + "_1", 2);
			cyWhere = " where " + cyWheres.get(0) + ") and "+ cyWheres.get(1).replace("n1", "n2") + ")";
			List<String> results = dealMoreEnt(ent, cyMatches, cyWhere);
			if (results.size() == 0){
				String result = getShortPathOne(ent, cyWhere);
				if (!"".equals(result)){
					data.add(result);
				}
			}else {
				data.addAll(results);
			}
		}
		
		return data;
	}
	
	
	public static String getNodes(List<String> ent, Map<String, Integer> cyMatches, String cyWhere) {
		String cypher = "match ";
		String ret = "return ";
		String result = "";
		StartService.set();
		for (int i = 0; i < ent.size() - 1; i++){
			cypher = cypher + "(n" + cyMatches.get(ent.get(i)) + ":" + ent.get(i) + "),";
			ret = ret + "n" + cyMatches.get(ent.get(i)) + ",";
		}
		cypher = cypher + "(n" + cyMatches.get(ent.get(ent.size() - 1)) + ":" + ent.get(ent.size() - 1) + ") ";
		ret = ret + "n" + cyMatches.get(ent.get(ent.size() - 1));
		cypher = cypher + cyWhere + ret;
		for (int i = 1; i <= ent.size(); i++){
			cypher = cypher.replace("_" + i, "");
		}
		System.out.println("-1:" + cypher);
		result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			return result;
		}
		// TODO 自动生成的方法存根
		return result;
	}

	/**
	 * 多个实体之间的查询
	 * @param ent	出现的实体
	 * @param cyMatches	实体对应的代号
	 * @param cyWhere	where条件
	 * @return 查询结果
	 */
	public static List<String> dealMoreEnt(List<String> ent, Map<String, Integer> cyMatches, String cyWhere) {
		List<String> paths = GraphPath.getPathRelation(ent);//计算实体的路径		
		List<String> reStrings = new ArrayList<>();
		StartService.set();
		for (String path: paths){
			String cypher = "";
			for (String key: cyMatches.keySet()){				
				path = path.replace(":"+ key, "n" + cyMatches.get(key) + ":" + key);
				if (path.contains(key + "_1")){
					path = path.replace("n" + cyMatches.get(key) + ":" + key + "_1", ":" + key + "_1");
				}
			}
			path = path.replace("_1", "");			
			cypher = "match p = " + path + " " + cyWhere + " return p";
			System.out.println("0:" + cypher);
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				reStrings.add(result);
			}
		}
		return reStrings;
	}
	
	/*
	 * 查询单个实体
	 */
	/**
	 * 
	 * @param ent	实体
	 * @param cyWhere where限制条件
	 * @return 查询结果
	 */
	public static String dealEntOne(List<String> ent, String cyWhere) {
		String cypher = "";			
		cypher = "match (n1:" + ent.get(0) + ") " + cyWhere + " return n1";
		System.out.println("1:" + cypher);
		StartService.set();
//		System.out.println(StartService.neo4jHandle.toString());
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			return result;
		}					
		return "";
	}
	
	
	/*
	 * 查询关系和一个实体
	 */
	/**
	 * 
	 * @param ent	
	 * @param cyWhere
	 * @param relationShip
	 * @return
	 */
	public static String dealRelEnt(List<String> ent, String cyWhere, List<String> relationShip) {
		String cypher = "";
		StartService.set();
		if (ent.get(0).equals(relationShip.get(0))){
			cypher = "";
			cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") " + cyWhere + " return p";
			System.out.println("2:" + cypher);
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				return result;
			}
		}else if (ent.get(0).equals(relationShip.get(1))){
			cypher = "";
			cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(0)+ ") " + cyWhere + " return p";
			System.out.println("2:" + cypher);
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				return result;
			}
		}else {
			cypher = "";
			cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(0)+ ") " + cyWhere + " return p";
			
			String result = StartService.neo4jHandle.getCypherResult(cypher);
			if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
				System.out.println("2:" + cypher);
				return result;
			}else {
				cypher = "match p = (n1:" + ent.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") " + cyWhere + " return p";
				
				result = StartService.neo4jHandle.getCypherResult(cypher);
				if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
					System.out.println("2:" + cypher);
					return result;
				}
			}
		}
		
		return "";
	}
	
	/*
	 * 只查询关系
	 */
	public static String dealRelOnly(List<String> relationShip) {
		String cypher = "";
		cypher = "match p = (:" + relationShip.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") return p limit 200";
		StartService.set();
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			System.out.println("3:" + cypher);
			return result;
		}
		return "";
	}
	
	/*
	 * 两类实体之间最短路径
	 */
	public static String getShortPathTwo(List<String> ent, String cyWhere) {
		String cypher = "";
		cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(1) + ")) " + cyWhere + " return p";
		cypher = cypher.replace("_1", "");
		StartService.set();
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			System.out.println("4:" + cypher);
			return result;
		}
		return "";
	}
	
	/*
	 * 两类实体之间所有路径（3次以内）
	 */
	public static String getPathAll(List<String> ent, String cyWhere) {
		String cypher = "";
		cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..3]-(n2:" + ent.get(1) + ")) " + cyWhere + " return p";
		StartService.set();
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			System.out.println("5:" + cypher);
			return result;
		}
		return "";
	}
	
	/*
	 * 同类实体之间最短路径
	 */
	public static String getShortPathOne(List<String> ent, String cyWhere) {
		String cypher = "";
		cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(0) + "))" + cyWhere + " return p";
		StartService.set();
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			System.out.println("6:" + cypher);
			return result;
		}
		return "";
	}

	
	/*
	 * 两实体和关系
	 */
	public static String dealRelEntTwo(List<String> relationShip, List<String> ent, String cyWhere, Map<String, Integer> cyMatches) {
		// TODO Auto-generated method stub
		StartService.set();
		String cypher = "";
		if (cyMatches.containsKey(ent.get(0))){
			cypher = "match p = (n" + cyMatches.get(ent.get(0)) + ":" + ent.get(0) + ")-[:" + relationShip.get(2) + "]-(n";
		}else {
			cypher = "match p = (n:" + ent.get(0) + ")-[:" + relationShip.get(2) + "]-(n";
		}
		if (cyMatches.containsKey(ent.get(1))){
			cypher = cypher + cyMatches.get(ent.get(1)) + ":"+ ent.get(1) + ") "  + cyWhere + " return p";
		}else {
			cypher = cypher + ":"+ ent.get(1) + ") "  + cyWhere + " return p";
		}		
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			System.out.println("7:" + cypher);
			return result;
		}
		return "";
	}
}
