package com.swt.ajss.restful.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.algorthm.MyLevenshtein;
import com.swt.ajss.restful.service.StartService;



/**
 * Created by Administrator on 2017/3/23.
 */
public class analyzer {
//    public static void main(String[] arg){
//        System.out.println(neo4jHandle.getCypherResult("match (n:技术开锁), (m:技术开锁) where n.作案手段 = m.作案手段 and n.部位 = m.部位 and n <> m return n, m limit 10"));
//    	getCX();
//    	getSame("案件");
    	public static void main(String[] args) {
    		try {
    			BufferedReader reader = new BufferedReader(new FileReader(new File("E:/360data/重要数据/桌面/Cypher.txt")));
    			FileWriter writer = new FileWriter("E:/360data/重要数据/桌面/cypher.csv");
    			String line = "";
    			String fir = "";
    			int i = 0;
    			StartService.set();
    			while ((line = reader.readLine()) != null){
//    				i = i + 1;
//    				line = line.replace("a", "a" + i);
//    				line = line.replace("b", "b" + i);
//    				writer.write(line + "\r\n"); 
    				StartService.connection.exectCypher1(line);
    			}
    			writer.close();
    			reader.close();
    		} catch (FileNotFoundException e) {
    			// TODO 自动生成的 catch 块
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO 自动生成的 catch 块
    			e.printStackTrace();
    		}
    		
    	}
//    }
    
    public static Map<String, List> getCX(){
    	StartService.set();
        String cypher = "";
        cypher = "match (n:非法经营案) return distinct n.地址";
        String result = StartService.connection.exectCypher1(cypher);
        List<String> place = creatIndexFromNeo4j.getArrayResult(result);
        Map<String, List> map = new HashMap<>();
        for (String p: place){
            cypher = "match (n:非法经营案) where n.地址 = '" + p +"' return count(n)";
            result = StartService.connection.exectCypher1(cypher);

            JSONObject object = JSONObject.parseObject(result);
            String data = object.getString("data");
            data = data.substring(2, data.length() - 2);
            if (Integer.parseInt(data) > 20){
                List<String> l = new ArrayList<>();
                l.add(data);
                l.add("match (n:非法经营案) where n.地址 = '" + p +"' return n");
                map.put(p, l);
            }
        }
        return  map;
    }

    public static void countEnt2(List<Map<String, String>> list, String data){
        if (data == null || "error".equals(data)){
            return;
        }
        JSONObject json = JSONObject.parseObject(data);
        JSONArray dataArr =  json.getJSONArray("data");
        Map<String, String> map = list.get(0);
        Map<String, String> map2 = list.get(1);
        String cy = "START n=node(";
        for (int i = 0; i < dataArr.size(); i++){
            JSONObject json2 = JSONObject.parseObject(dataArr.getString(i).substring(1, dataArr.getString(i).length() - 1));
            JSONObject jsonArray = (JSONObject) json2.get("metadata");
            String label = jsonArray.get("labels").toString();
            String id = jsonArray.get("id").toString();
            label = label.substring(2, label.length() - 2);
            cy = cy + id + ",";
        }
        cy = cy.substring(0, cy.length() - 1) + ") match (n)-[r]-(n) return n";
    }

    public static List<String> getSame(String label){
    	StartService.set();
        List<String> listCypher = new ArrayList<>();
        String cypher = "match (n:" + label + ") return distinct n.部位";
        List<String> area = creatIndexFromNeo4j.getArrayResult(StartService.connection.exectCypher1(cypher));
        String[] time = {"上午", "下午", "中午", "凌晨", "早上", "晚上"};
        Map<String, List<String>> res = new HashMap<>();
        for (String t: time){
	        for (String aString: area){
	        	cypher = "match (n:" + label + ":" + aString + ":" + t + ")-[]-(k)-[]-(m:" + label + ":" + aString + ":" + t  + ") where n.作案手段 = m.作案手段  and n <> m and n.日期 = m.日期  return k,m,n";
	        	getSameTwo(res, cypher);
	        }
        }
        for (String key: res.keySet()){
        	String cString = "start k = node(" + key + "), n = node (";
        	for (String word: res.get(key)){
        		cString = cString + word + ",";
        	}
        	cString = cString.substring(0, cString.length() - 1) + ")" + "match p = (k)-[]-(n) return p";
        	String data = StartService.neo4jHandle.getCypherResult(cString);
//        	System.out.println(data);
        	listCypher.add(data);
        }
        return listCypher;
    }
    
    public static void getSameTwo(Map<String, List<String>> res, String cypher) {
    	StartService.set();
    	String result = StartService.connection.exectCypher1(cypher);
		JSONObject object1 = JSONObject.parseObject(result);
		JSONArray array = object1.getJSONArray("data");
		for (int i = 0; i < array.size(); i++){
			JSONArray data = JSONArray.parseArray(array.get(i).toString());
			JSONObject object = data.getJSONObject(0);
			String idk = getId(object);
			object = data.getJSONObject(1);
			String idm = getId(object);
			String plm = getPlace(object);
			object = data.getJSONObject(2);
			String idn = getId(object);
			String pln = getPlace(object);
			if (MyLevenshtein.levenshtein(plm, pln) > 0.4){
				if (res.containsKey(idk)){
					if (res.get(idk).contains(idn)){
						res.get(idk).add(idn);
					}
					if (res.get(idk).contains(idm)){
						res.get(idk).add(idm);
					}
				}else {
					List<String> rList = new ArrayList<>();
					rList.add(idn);
					rList.add(idm);
					res.put(idk, rList);
				}
			}
		}
	}
    
    private static String getId(JSONObject object) {
		JSONObject mate = object.getJSONObject("metadata");
		String id = mate.getString("id");
		return id;
	}
    
    private static String getPlace(JSONObject object) {
		JSONObject mate = object.getJSONObject("data");
		String id = mate.getString("地址");
		return id;
	}
    
    public static String configNeo4j() {
    	List<String> con = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/config.txt")));
			String line = "";
			while ((line = reader.readLine()) != null ) {
				con.add(line.split("=")[1]);
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    	JSONObject object = new JSONObject();
    	object.put("ip", con.get(0));
    	object.put("port", con.get(1));
    	object.put("userName", con.get(2));
    	object.put("passWord", con.get(3));
    	
		return object.toJSONString();
	}
    
    
}
