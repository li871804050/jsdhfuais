package com.swt.ajss.restful.resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.lucene.HanLPAnalyzer;
import com.swt.ajss.restful.service.StartService;


public class creatIndexFromNeo4j {
	public static String indexPath = StartService.dicDir + "/index2";
	public static void main(String[] args) {

		creatIndex(indexPath, 1);
//		List<String> reStrings = searchIndex(indexPath, "维生素B");
//		for (int i = 0; i < reStrings.size(); i++){
//			System.out.println(reStrings.get(i));
//		}
	}
	
	/* 需要读入实体属性关系文件
	*  输入索引所在路径
	*  关系的属性名统一为type
	*/
	public static void creatIndex(String dicDir, int anl){
		StartService.set();
//		List<String> list = addLable.read();
//		StartService.connection = new Neo4jConnection(list.get(0), list.get(1), list.get(2), list.get(3));
		try {
			
			File file = new File(dicDir + "/index2");
			if (file.exists()){
				for (File f: file.listFiles()){
					f.delete();
				}
			}
			Path path = Paths.get(dicDir + "/index2");
			Directory directory = FSDirectory.open(path);
			Analyzer analyzer = null;
			if (anl == 1){
				analyzer = new HanLPAnalyzer();
			}else {
				analyzer = new StandardAnalyzer();
			}
			IndexWriterConfig config = new IndexWriterConfig(analyzer);			 
			IndexWriter writer = new IndexWriter(directory, config);
						
//			String[] labels = {"ClassID1", "ClassID1Name", "ClassID2", "ClassID2Name", "ClassID3", "ClassID3Name", "ClassID", "ClassID4Name", "conf", "label", "lift", "Price_Com", "Price_Must" ,"rulePR", "sku", "SkuName", "Standards", "sup", "Time_Def", "Unit_Sale"};
			//labels 应该为文件输入
			Map <String, List<String>> pros = readPro();
//			String ent = "Store0044";
			//实体类型为文件输入
			//
			
			
			for (String key: pros.keySet()){
				List<String> labels = pros.get(key);
				/*
				 * label索引创建（n:y）
				 * 实体名为实体
				 * 属性值为y
				 * 属性名为y
				 * */
				Document documnet = new Document();
				Field field1 = new TextField("实体名", "实体", Field.Store.YES);
				Field field2 = new TextField("属性值", key, Field.Store.YES);
				Field field3 = new TextField("属性名", key, Field.Store.YES);
				documnet.add(field1);
				documnet.add(field2);
				documnet.add(field3);
//				System.out.println(documnet.toString());
				writer.addDocument(documnet);
				
				for (String label: labels){
					String result = "";
					String cypher = "MATCH (n:" + key +") RETURN DISTINCT n." + label;
					result = StartService.connection.exectCypher1(cypher);
					List<String> allPro = getArrayResult(result);										
					
					for (String pro: allPro){
					/*
					 * 属性索引创建match（n:y）return n.x
					 * 实体名为y
					 * 属性值为n.x
					 * 属性名为x
					 * */
						pro = pro.replace("\\", ".+");
						documnet = new Document();
						field1 = new TextField("实体名", key, Field.Store.YES);
						field2 = new TextField("属性值", pro, Field.Store.YES);
						field3 = new TextField("属性名", label, Field.Store.YES);
						documnet.add(field1);
						documnet.add(field2);
						documnet.add(field3);
//						System.out.println(documnet.toString());
						writer.addDocument(documnet);
					}
				}
				
			} 
			
			/*
			 * 关系索引创建（n:y）-[r:R]-（m:x）
			 * 实体名为y-x
			 * 属性值为r.sup
			 * 属性名为关系R
			 * */
			List<String> reList = readRel();
			for (String rel: reList){
				String[] relDatas = rel.split("-");
				Field field1 = new TextField("实体名", relDatas[0]+ "%%%" + relDatas[1], Field.Store.YES);
				Field field2 = new TextField("属性值", relDatas[2], Field.Store.YES);
				Field field3 = new TextField("属性名", relDatas[2], Field.Store.YES);
//				String result = "";
//				String cypher = "MATCH (n:" + relDatas[0] +")-[r:" + relDatas[2] + "]-(" + relDatas[1] + ") RETURN DISTINCT r.sup";				
//				result = StartService.connection.exectCypher1(cypher);
//				List<String> allPro = getArrayResult(result);										
//				
//				for (String pro: allPro){ 
				Document documnet = new Document();
//					Field field1 = new TextField("实体名", relDatas[0]+ "%%%" + relDatas[1], Field.Store.YES);
//					Field field2 = new TextField("属性值", pro, Field.Store.YES);
//					Field field3 = new TextField("属性名", relDatas[2], Field.Store.YES);
				documnet.add(field1);
				documnet.add(field2);
				documnet.add(field3);
				writer.addDocument(documnet);
//				}
			}
			
			Set<String> levelSet = readLevel2();
			for (String rel: levelSet){
				Field field1 = new TextField("实体名", "实体", Field.Store.YES);
				Field field2 = new TextField("属性值", rel, Field.Store.YES);
				Field field3 = new TextField("属性名", rel, Field.Store.YES);
//				String result = "";
//				String cypher = "MATCH (n:" + relDatas[0] +")-[r:" + relDatas[2] + "]-(" + relDatas[1] + ") RETURN DISTINCT r.sup";				
//				result = StartService.connection.exectCypher1(cypher);
//				List<String> allPro = getArrayResult(result);										
//				
//				for (String pro: allPro){ 
				Document documnet = new Document();
//					Field field1 = new TextField("实体名", relDatas[0]+ "%%%" + relDatas[1], Field.Store.YES);
//					Field field2 = new TextField("属性值", pro, Field.Store.YES);
//					Field field3 = new TextField("属性名", relDatas[2], Field.Store.YES);
				documnet.add(field1);
				documnet.add(field2);
				documnet.add(field3);
				writer.addDocument(documnet);
//				}
			}
			
			
			
			
			writer.close();
			directory.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String cypher = "MATCH (n) RETURN DISTINCT keys(n)";
//		String returnDataFormat = "";
//		System.out.println(StartService.connection.exectCypher(cypher));
	}
	
	
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
     * 函数功能:根据字段查询索引
     */
    public static List<String> searchIndex(String indexDir, String keyWord) {
    	List<String> result = new ArrayList<>();
		if ("".equals(keyWord) || keyWord == null){
			return result;
		}
        try {
        	Path path = Paths.get(indexDir);
            Directory dir = FSDirectory.open(path);
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new HanLPAnalyzer();
            QueryBuilder builder = new QueryBuilder(analyzer);
//            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
            Query query = builder.createPhraseQuery("属性值", keyWord);
            ScoreDoc[] sd = searcher.search(query, 1000).scoreDocs;
          
            for (int i = 0; i < sd.length; i++) {
                Document hitDoc = reader.document(sd[i].doc);
                String en = hitDoc.get("实体名");
                String pN = hitDoc.get("属性名");
                String pR = hitDoc.get("属性值");
                result.add(en + "######" + pN + "######" + pR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		return result;
    }

/*    
    public static ArrayList<List<String>> seachNeo4j(List<String> list) {
    	List<String> result1 = new ArrayList<>();
    	List<String> result2 = new ArrayList<>();
    	ArrayList<List<String>> resultAll = new ArrayList<>();
    	List<Integer> count = new ArrayList<>();
    	for (int i = 0; i < list.size(); i++){
    		count.add(0);
    	}
    	int max = 0;
    	boolean re = false;	
    	while (true){
    		String cypher = "";
    		String cypher1 = "";
    		String ent = "";
    		try{
    		if (count.size() > 0){
	    		for (int i = 0; i < count.size(); i++){	    				
	    			String[] words = list.get(i).get(count.get(i)).split("-");
	    			if (!words[0].contains("%") && !"实体".equals(words[0])){
	    				if ("".equals(ent)){
	    					ent = words[0];
	    					cypher = "match (n:" + words[0] + ") where n." + words[1] + " = '" + words[2];
	    					cypher1 = "match (n:" + words[0] + ")-[r]-(m) where n." + words[1] + " = '" + words[2];
	    				}else if(words[0].equals(ent)){
	    					cypher = cypher + "' and n." + words[1] + " = '" + words[2];
	    					cypher1 = cypher1 + "' and n." + words[1] + " = '" + words[2];
	    				}else {
	    					cypher = "";
							break;
						}
	    			}else if (words[0].contains("%")){
							String[] ents = words[0].split("%");
							cypher = "match (n:" + ents[0] + ")-[r:" + words[1] + "]-(m: " + ents[1] + ") where r.type = " + words[2] 
									+ "return r";
							String resData = StartService.connection.exectCypher1(cypher); 
							if (!"error".equals(resData)){
								result1.add(resData);
							}
							cypher = "";
							
					}else {
						cypher = "match (n:" + words[1] + ") return n";
						String resData = StartService.connection.exectCypher1(cypher);					
						if (!"error".equals(resData)){
							result1.add(resData);
						}
						cypher = "";
						cypher1 = "match (n:" + words[1] + ")-[r]-(m) return m";
						resData = StartService.connection.exectCypher1(cypher1); 
						if (!"error".equals(resData)){
							result2.add(resData);
						}
					}
	
	    		}
    		} 
    		}catch (IndexOutOfBoundsException e) {
    			break;
				// TODO: handle exception
			}
    		if (!"".equals(cypher)){
    			cypher = cypher + "' return n";
    			String resData = StartService.connection.exectCypher1(cypher); 
    			if (!"error".equals(resData)){
					result1.add(resData);
				}
    			cypher1 = cypher1 + "' return m";
    			resData = StartService.connection.exectCypher1(cypher1); 
    			if (!"error".equals(resData)){
					result2.add(resData);
				}
    		}
    		for (int j = max; j < count.size(); j++){
    			if (count.get(j) == list.get(j).size() - 1){
    				max = max + 1;
    			}else {
    				int c = count.get(j) + 1;
    				count.remove(j);
    				count.add(j, c);
					break;
				}
    		}
    		if (max == count.size()){
    			break;
    		}
    	}
    	resultAll.add(result1);
    	resultAll.add(result2);
    	return resultAll;		
	}
*/   
    public static String seachNeo4j2(List<String> list) {
		Map<String, String> rel = readRels();
    	List<String> result1 = new ArrayList<>();
    	List<String> result2 = new ArrayList<>();
    	ArrayList<List<String>> resultAll = new ArrayList<>();
    	List<Integer> count = new ArrayList<>();
    	for (int i = 0; i < list.size(); i++){
    		count.add(0);
    	}

    	List<String> cyMatchs = new ArrayList<>();
    	String cyMatch = "";
		String cyWhere = "";
		String cyP = "";
		Map<String, Integer> ent = new HashMap<>();
		Map<String, String> cyWh = new HashMap<>();
		String entAll = "";
		int entCount = 0;
		for (String li: list){
			String[] words = li.split("######");
			if (!words[0].contains("%") && !"实体".equals(words[0]) && !entAll.contains(words[0])){
				entAll = entAll + "-" + words[0];
				entCount += 1;
			}
		}
		String return2 = "return ";
    	for (String li: list){
    			String[] words = li.split("######");
    			if (!words[0].contains("%") && !"实体".equals(words[0])){
					if (entCount == 1){
						cyMatch = "(n1:" + words[0] + ")";
						cyMatchs.add(cyMatch);
						return2 = return2 + "n1,";
					}
    				if (ent.size() == 0){
    					ent.put(words[0], 1);
    					cyWh.put(words[1], "n" + ent.get(words[0]) + "." + words[1] + " = '" + words[2] + "'");
    				}else if(ent.containsKey(words[0])){
    					if (cyWh.containsKey(words[1])){
    						cyWhere = cyWh.get(words[1]) + " or n" + ent.get(words[0]) + "."+ words[1] + " = '" + words[2] + "'";
    						cyWh.remove(words[1]);
    						cyWh.put(words[1], cyWhere);
    					}else {  						
    						cyWh.put(words[1], "n" + ent.get(words[0]) + "." + words[1] + " = '" + words[2] + "'");
    					}
    				}else if(!ent.containsKey(words[0]) && ent.size() > 0 && entCount == 2){
    					List<String> l = new ArrayList<String>(ent.keySet());
    					cyMatch = "(n1:" + l.get(0) + ")-[*1..3]-(n2:" + words[0] + ")";
						cyMatchs.add(cyMatch);
						ent.put(words[0], ent.size() + 1);
						return2 = return2 + "n1,n2,";
    					cyWh.put(words[1], "n" + ent.get(words[0]) + "." + words[1] + " = '" + words[2] + "'");
					}else if(!ent.containsKey(words[0]) && ent.size() > 0 && entCount > 2){
						List<String> l = new ArrayList<String>(ent.keySet());
						ent.put(words[0], ent.size() + 1);
						for (int k = 0; k < l.size(); k++) {
							if (rel.containsKey(l.get(k) + "-" + words[0])) {
								cyMatch = "(n" + ent.get(l.get(k)) + ":" + l.get(k) + ")" + "-[:" + rel.get(l.get(k) + "-" + words[0]) + "]-(n" + ent.get(words[0]) + ":" + words[0] + ")";
								if (!return2.contains("n" + ent.get(l.get(k)))){
									return2 = return2 + "n" + ent.get(l.get(k)) + ",";
								}
								if (!return2.contains("n" + ent.get(words[0]))){
									return2 = return2 + "n" + ent.get(words[0]) + ",";
								}
								cyMatchs.add(cyMatch);
							}
						}

						cyWh.put(words[1], "n" + ent.get(words[0]) + "." + words[1] + " = '" + words[2] + "'");
					}
    			}else if (words[0].contains("%")){//关系
						String[] ents = words[0].split("%");
						if ("".equals(cyMatch)){
							cyMatch = "(n:" + ents[0] + ")-[r:" + words[1];
						}
						
				}else if (words[0].equals("实体")){
					if (ent.size() == 0){
						ent.put(words[1], 1);
						cyMatch = "(n" + ent.get(words[1]) + ":" + words[1] + ")";
					}else if(!ent.containsKey(words[1]) && ent.size() < 2){
						cyMatch = cyMatch + "-[*1..3]-(n" + ent.get(words[1]) + ":" + words[1] + ")";
					}
				}
    	}
    	
    	String cyString = "";

		String return1 = "return ";
		if (cyMatchs.size() > 0){
    		
			cyMatch = "";
			for (int i = 0; i < cyMatchs.size(); i++){
				if (entCount == 1) {
					cyMatch = "match " + cyMatchs.get(i);
				}else {
					cyMatch = cyMatch + "match p" + i + " = " + cyMatchs.get(i) + " ";
					return1 = return1 + "p" + i + ",";
				}			
			}
			
			cyWhere = "";
			for (int i = 1; i <= entCount; i++){
				if (cyMatch.contains("n" + i)){
			    	for (String value: cyWh.values()){
			    		if (value.contains("n" + i)){
			    			cyWhere = cyWhere + " and (" + value + ")";
			    		}
			    	}
				}
			}
//			System.out.println(cyMatch);
			if (cyWhere.length() > 4){
				cyWhere = " where " + cyWhere.substring(4);
			}
			
			cyString= cyMatch + cyWhere + return2.substring(0, return2.length() - 1);
    		cyMatch = cyMatch + cyWhere + return1.substring(0, return1.length() - 1);
			if (entCount == 1){
				cyMatch = cyMatch + " n1";
				cyString = cyMatch;
			}
    	}else {
			cyMatch = cyString;
		}
//    	System.err.println(cyMatch + "%%%%" +  cyString);
    	return cyMatch + "%%%%%" + cyString;		
	}
    
    
    public static Map<String, List<String>> readPro() {
    	Map<String, List<String>> map = new HashMap<>();
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/pro.txt")));
			String line = "";
			while ((line = reader.readLine()) != null){
				String[] word = line.split(":");
				String[] pros = word[1].split("\t");
				List<String> pList = new ArrayList<>();
				for (String pro: pros){
					pList.add(pro);
				}
				map.put(word[0], pList);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
	}
    
    public static Map<String, List<String>> readLevel() {
    	Map<String, List<String>> map = new HashMap<>();
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/pro.txt")));
			String line = "";
			while ((line = reader.readLine()) != null){
				String[] word = line.split(":");
				String[] pros = word[1].split("\t");
				List<String> pList = new ArrayList<>();
				for (String pro: pros){
					pList.add(pro);
				}
				map.put(word[0], pList);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return map;
	}
    
    
    public static Set<String> readLevel2() {
    	Set<String> map = new HashSet<>();
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/level.txt")));
			String line = "";
			while ((line = reader.readLine()) != null){
				String[] word = line.split(":");
				String[] pros = word[1].split("\t");
				for (String pro: pros){
					map.add(pro);
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
    	return map;
	}
    
    public static List<String> readRel() {
    	List<String> list = new ArrayList<>();
    	try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/rel.txt")));
			String line = "";
			while ((line = reader.readLine()) != null){
				list.add(line);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return list;
	}
    
    public static List<Object> search(String input) {
    	StartService.set();
//    	List<String> list4 = addLable.read();
//		StartService.connection = new Neo4jConnection(list4.get(0), list4.get(1), list4.get(2), list4.get(3));
    	List<String> results = new ArrayList<>();
    	List<Object> list2 = new ArrayList<>();
    	String[] words = input.split(" ");
    	List<String> list = new ArrayList<>();
    	for (String word: words){
    		List<String> map = searchIndex(indexPath, word);
    		list.addAll(map);  		
    	}
    	//显示需要的cypher语句
    	String rightRes = seachNeo4j2(list);
    	String rcy = "";
    	String lcy = "";
    	if (!rightRes.equals("%%%%%")){
    		lcy = rightRes.split("%%%%%")[0];//中间
    		rcy = rightRes.split("%%%%%")[1];//右边
        	List<Map<String, String>> listMap = new ArrayList<>();
        	Map<String, String> m = new HashMap<>();
        	listMap.add(m);
        	m = new HashMap<>();
        	listMap.add(m);
//        	System.out.println("***" + rcy);
        	String data = StartService.connection.exectCypher1(rcy);
        	
//        	String cypher = "";
//        	cypher = getCypher(cypher, data);
//        	if (!"".equals(cypher)){
//        		cypher = cypher + ") match (n)-[r]-(m) return m";
//        	}
//        	data = StartService.connection.exectCypher1(cypher);
        	countEnt(listMap, data);
        	Map<String, String> map = listMap.get(1);
        	List<String> keys = new ArrayList<>(map.keySet());
        	for (String key: keys){
        		String cy =  map.get(key) + ") return n";
        		map.remove(key);
        		map.put(key, cy);
        	}
       	
//        	System.err.println(cypher);
        	list2.add(lcy);
        	list2.add(listMap.get(0));
        	list2.add(map);
    	}
//    
    	return list2;
    }
    
    public static void countEnt(List<Map<String, String>> list, String data){
		if (data == null || "error".equals(data)){
			return;
		}
    	JSONObject json = JSONObject.parseObject(data);
    	JSONArray dataArr =  json.getJSONArray("data");
    	Map<String, String> map = list.get(0);
    	Map<String, String> map2 = list.get(1);
    	String[] labelAll = {"人", "案件", "物"};
		for (int i = 0; i < dataArr.size(); i++){
			JSONArray array = dataArr.getJSONArray(i);
			for (int j = 0; j < array.size(); j++){
				JSONObject json2 = array.getJSONObject(j);
				JSONObject jsonArray = (JSONObject) json2.get("metadata");
				String label = jsonArray.get("labels").toString();
				String id = jsonArray.get("id").toString();
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
	
		public static String getCypher(String cypher, String data){
			if (data.equals("error")){
				return  "";
			}
	    	JSONObject json = JSONObject.parseObject(data);
	    	JSONArray dataArr =  json.getJSONArray("data");
			for (int i = 0; i < dataArr.size(); i++){
//				System.out.println(dataArr.getString(i));
				JSONArray json2 = JSONArray.parseArray(dataArr.getString(i));
				for (int j = 0; j < json2.size(); j++){
					JSONObject jsonObject = (JSONObject) json2.get(j);
					JSONObject jsonArray = (JSONObject) jsonObject.get("metadata");
					String id = jsonArray.get("id").toString();
					if ("".equals(cypher)){
						cypher = "START n=node(" + id;
					}else {
						cypher = cypher + "," + id;
					}
				}
			}
		return cypher;
    }

	public static Map<String, String> readRels(){
		Map<String, String> rel = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/rel.txt")));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] words = line.split("-");
				rel.put(words[0] + "-" + words[1], words[2]);
				rel.put(words[1] + "-" + words[0], words[2]);
			}
			reader.close();
		}catch (IOException e){
			return rel;
		}
		return rel;
	}
}
