package com.swt.ajss.restful.algorthm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.swt.ajss.restful.service.StartService;

public class searchAll {
	
	/*
	 * 查询多个实体
	 */
	public static List<String> dealMoreEnt(List<String> ent, Map<String, Integer> cyMatches, String cyWhere) {
		List<String> paths = graphRel.getPathR(ent);//计算实体的路径
		List<String> reStrings = new ArrayList<>();
		for (String path: paths){
			String cypher = "";
			for (String key: cyMatches.keySet()){
				path = path.replace(":"+ key, "n" + cyMatches.get(key) + ":" + key);
			}
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
	public static String dealEntOne(List<String> ent, String cyWhere) {
		String cypher = "";			
		cypher = "match (n1:" + ent.get(0) + ") " + cyWhere + " return n1";
		System.out.println("1:" + cypher);
		String result = StartService.neo4jHandle.getCypherResult(cypher);
		if (result.contains("\"graph\"") && result.contains("\"nodes\"")){
			return result;
		}					
		return "";
	}
	
	
	/*
	 * 查询关系和一个实体
	 */
	public static String dealRelEnt(List<String> ent, String cyWhere, List<String> relationShip) {
		String cypher = "";
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
		cypher = "match p = (:" + relationShip.get(0) + ")-[r:"+ relationShip.get(2) + "]-(:" + relationShip.get(1)+ ") return p";
		
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
	public static String getShortPathOne(List<String> ent, List<String> cyWheres) {
		String cypher = "";
		cypher = "match p =  shortestPath((n1:" + ent.get(0) + ")-[*1..5]-(n2:" + ent.get(0) + ")) where " + cyWheres.get(0) + ") and "+ cyWheres.get(1).replace("n1", "n2") + ") return p";
		
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
