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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import com.swt.ajss.restful.service.StartService;

public class graphRel {
	public static String relPath = StartService.dicDir + "/rel2";
	public static void main(String[] args) {
		String[] wString = {"B", "A"};
		List<String> aList = new ArrayList<>();
		for (String w: wString){
			aList.add(w);
		}
		List<String> reStrings = getPathR(aList);
		for (String res: reStrings){
			System.err.println(res);
		}
//		List<Object> rList = readReal("dic/rel2");
//		HashMap<String, List<String>> rMap = (HashMap<String, List<String>>) rList.get(0);
//		HashMap<String, List<String>> entMap = (HashMap<String, List<String>>) rList.get(1);
//		List<List<String>> res = new ArrayList<>();
//		Map<String, List<String>> all = new HashMap<>();
//		getPath(entMap, "", "A", "B", all, res);
//		
//		for (int i = 0; i < res.size(); i++){
//			String cy = "";
//			List<List<String>> allRel = new ArrayList<>(); 
//			for (int j = 0; j < res.get(i).size() - 1; j++){
////				cy = cy + res.get(i).get(j) + "-[:" + rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)).get(0) + "]-";
////				if (rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)).size() > 1){
////					
////				}
//				allRel.add(rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)));
//			}
////			cy = cy + res.get(i).get(res.get(i).size() - 1);
////			System.err.println(cy);
//			//多种关系处理
//			List<Integer> countRel = new ArrayList<>();
//			for (int k = 0; k < allRel.size(); k++){
//				countRel.add(0);
//			}
//			
//			while (true && countRel.size() > 0){
//				int num = countRel.size() - 1;
//				String mString = cy;
//				cy = "";
//				for (int k = 0; k < res.get(i).size() - 1; k++){
//					cy = cy + res.get(i).get(k) + "-[:" + rMap.get(res.get(i).get(k) + "-" + res.get(i).get(k + 1)).get(countRel.get(k)) + "]-";
//				}
//				cy = cy + res.get(i).get(res.get(i).size() - 1);
//				System.out.println(cy);
//				int count = 0;
//				while (true){
//					if (countRel.get(num) < allRel.get(num).size() - 1){
//						int c = countRel.get(num) + 1;
//						countRel.remove(num);
//						countRel.add(num, c);
//						break;
//					}else {
//						countRel.remove(num);
//						countRel.add(num, 0);
//						count = count + 1;
//						num = num - 1;
//					}
//					if (num == -1 || count == countRel.size()){
//						break;
//					}
//				}
//				if (count == countRel.size() || num == -1){
//					break;
//				}
//			}
//		}
	}
	
	
	/*
	 * 返回节点start到end的所有路径
	 */
	public static List<String> getCypher(String start, String end) {
		List<Object> rList = readReal();
		List<String> resultAll = new ArrayList<>();
		HashMap<String, List<String>> rMap = (HashMap<String, List<String>>) rList.get(0);
		HashMap<String, List<String>> entMap = (HashMap<String, List<String>>) rList.get(1);
		List<List<String>> res = new ArrayList<>();
		Map<String, List<String>> all = new HashMap<>();
		getPath(entMap, "", start, end, all, res);
		
		for (int i = 0; i < res.size(); i++){
			String cy = "";
			List<List<String>> allRel = new ArrayList<>(); 
			for (int j = 0; j < res.get(i).size() - 1; j++){
//				cy = cy + res.get(i).get(j) + "-[:" + rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)).get(0) + "]-";
//				if (rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)).size() > 1){
//					
//				}
				allRel.add(rMap.get(res.get(i).get(j) + "-" + res.get(i).get(j + 1)));
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
				for (int k = 0; k < res.get(i).size() - 1; k++){
					cy = cy + "(:" + res.get(i).get(k) + ")-[:" + rMap.get(res.get(i).get(k) + "-" + res.get(i).get(k + 1)).get(countRel.get(k)) + "]-";
				}
				cy = cy + "(:" + res.get(i).get(res.get(i).size() - 1) + ")";
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
	public static List<Object> readReal(){
		List<Object> result = new ArrayList<>();
		HashMap<String, List<String>> rMap = new HashMap<>();
		HashMap<String, List<String>> entMap = new HashMap<>();
		List<String> same = new ArrayList<>();
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(relPath));
			String line = "";
			while ((line = reader.readLine()) != null){
				String[] wString = line.split("-");
//				if (wString[0].equals(wString[1])){
//					if (same.contains(wString[0])){
//						int count = 1;
//						while (!same.contains(wString[0] + "-" + count)){
//							count = count + 1;
//						}
//						same.add(wString[0] + "-" + count);
//					}
//					
//				}
				
				if (rMap.containsKey(wString[0] + "-" + wString[1])){
					rMap.get(wString[0] + "-" + wString[1]).add(wString[2]);
				}else {
					List<String> reList = new ArrayList<>();
					reList.add(wString[2]);
					rMap.put(wString[0] + "-" + wString[1], reList);
				}
				
				if (rMap.containsKey(wString[1] + "-" + wString[0])){
					rMap.get(wString[1] + "-" + wString[0]).add(wString[2]);
				}else {
					List<String> reList = new ArrayList<>();
					reList.add(wString[2]);
					rMap.put(wString[1] + "-" + wString[0], reList);
				}
				
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
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.add(rMap);
		result.add(entMap);
		return result;
	}
	
	
	/*
	 * 路径求解函数
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
	public static List<String> getPathR(List<String> datas) {
		List<String> all = new ArrayList<>();
		int i = 0;
		int j = i + 1;
		List<String> allList = new ArrayList<>();
		while (true){
			all = getCypher(datas.get(i), datas.get(j));
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
}
