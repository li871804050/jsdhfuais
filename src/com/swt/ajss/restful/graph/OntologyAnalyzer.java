package com.swt.ajss.restful.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.swt.ajss.restful.service.owlTest;

/**
 * 
 * @author Administrator
 *	OntologyAnalyzer	  对本体文件进行解析的类
 */
public class OntologyAnalyzer {
	
	private static HashMap<String, List<String>> level = new HashMap<>();	//层级关系
	private static HashMap<String, List<String>> property = new HashMap<>();	//实体属性
	private static List<String> relation = new ArrayList<>();				//关系
	private static JSONArray array = new JSONArray();						//层级json数组
	private static List<String> relSame = new ArrayList<>();						//层级json数组
	
	
	public static void main(String[] args) {
		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/20140427.owl");
		System.out.println(getjsonLevel());
	}
	
	/**
	 * 
	 * @param filePath  本体文件所在路径
	 * @return 
	 */
	public OntologyAnalyzer(String filePath){
		try {
			SAXBuilder builder = new SAXBuilder();//实例JDOM解析器  
	        Document document = builder.build(filePath);
			Element root = document.getRootElement();
			HashMap<String, List<String>> levelCopy = new HashMap<>();
			Namespace ns = Namespace.getNamespace("http://www.w3.org/2002/07/owl#");
			List<Element> elements = root.getChildren("SubClassOf", ns);
			for (Element e: elements){
				try{
					String entrity = e.getChild("Class", ns).getAttributeValue("IRI").replace("#", "");
					Element dataFrom = e.getChild("DataSomeValuesFrom", ns);
					String pro = dataFrom.getChild("DataProperty", ns).getAttributeValue("IRI").replace("#", "");
					if (property.containsKey(entrity)){
						property.get(entrity).add(pro);
					}else{
						List<String> pros = new ArrayList<>();
						pros.add(pro);
						property.put(entrity, pros);
					}										
				} catch (NullPointerException e2) {
					List<Element> elements2 = e.getChildren("Class", ns);
					if (elements2.size() == 2){
						String first = elements2.get(0).getAttributeValue("IRI").replace("#", "");
						String second = elements2.get(1).getAttributeValue("IRI").replace("#", "");
						if (level.containsKey(second)){
							level.get(second).add(first);
							levelCopy.get(second).add(first);
						}else{
							List<String> next = new ArrayList<>();
							next.add(first);
							level.put(second, next);
							levelCopy.put(second, next);
						}
					}
				}
				try {
					Element OBFrom = e.getChild("ObjectSomeValuesFrom", ns);
					String entrity = e.getChild("Class", ns).getAttributeValue("IRI").replace("#", "");
					String relFirst  = OBFrom.getChild("Class", ns).getAttributeValue("IRI").replace("#", "");
					String relName = OBFrom.getChild("ObjectProperty", ns).getAttributeValue("IRI").replace("#", "");
//					System.err.println(relFirst + "-" + entrity + "-" + relName);
					relation.add(relFirst + "-" + entrity + "-" + relName);
					if (relFirst.equals(entrity)){
						relSame.add(relName);
					}
				} catch (NullPointerException e3) {
//					System.out.println(e.getChild("Class", ns).getAttributeValue("IRI").replace("#", ""));
					// TODO: handle exception
				}
			}
			
			boolean child = true;
			List<String> keyAll = new ArrayList<>();
			List<String> allPro = new ArrayList<>();
			
			JSONObject arrayAll = new JSONObject();
			while (true) {
//				System.out.println(level.size());
				List<String> keys2 = new ArrayList<>(levelCopy.keySet());
				for (String key :keys2) {
					child = true;
					for (String ch : levelCopy.get(key)) {
						if (levelCopy.containsKey(ch)) {
							child = false;
							break;
						}
					}
					if (child) {
						JSONObject object = new JSONObject();
						object.put("name", key);
						JSONArray array = new JSONArray();
						List<String> keys = new ArrayList<>(levelCopy.get(key));
						for (String ch : keys) {
							JSONObject object1 = new JSONObject();
							allPro.add(ch);
							if (arrayAll.containsKey(ch)) {
//								System.err.println(arrayAll.get(ch));
								if (!array.contains(arrayAll.get(ch))){
									array.add(arrayAll.get(ch));
								}
							}else {
								object1.put("name", ch);
							}
							if (!array.contains(object1)){
								array.add(object1);
							}
						}
						object.put("children", array);
						arrayAll.put(key, object);
						keyAll.add(key);
						levelCopy.remove(key);
					}
				}
				if (levelCopy.size() == 0){
					break;
				}
			}
//			FileWriter writer5 = new FileWriter(new File(StartService.dicDir +  "/level2.txt"));
			array = new JSONArray();
			for (String la: keyAll){
				if (!allPro.contains(la)) {
					array.add(arrayAll.get(la));
				}
			}
//			writer5.write(array.toJSONString());
//			writer5.close();
//
//			FileWriter writer2 = new FileWriter(new File(StartService.dicDir + "/rel.txt"));
//			for (String rels: relation){
//				writer2.write(rels + "\r\n");
//			}
//			writer2.close();
//			FileWriter writer3 = new FileWriter(new File(StartService.dicDir + "/pro.txt"));
//			for (String key: property.keySet()){
//				writer3.write(key + ":");
//				for (String value: property.get(key)){
//					writer3.write(value + "\t");
//				}
//				writer3.write("\r\n");
//			}
//			
//			writer3.close();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

    
    
    public static List<String> getEntity() {
//    	Set<String> map = new HashSet<>();
//    	try {
//			BufferedReader reader = new BufferedReader(new FileReader(new File(StartService.dicDir + "/level.txt")));
//			String line = "";
//			while ((line = reader.readLine()) != null){
//				String[] word = line.split(":");
//				String[] pros = word[1].split("\t");
//				for (String pro: pros){
//					map.add(pro);
//				}
//			}
//			reader.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	Set<String> map = new HashSet<>();
    	for (String key: level.keySet()){
    		map.addAll(level.get(key));
    	}
    	
    	return new ArrayList<String>(map);
	}
    
    /*
	 * 获取需要统计的label
	 * 只统计二级标签
	 */
	public static List<String> getLabel() {
		HashSet<String> list = new HashSet();		
		for (String key: level.keySet()){
			boolean find = false;
			for (List<String> value: level.values()){
				if (value.contains(key)){
					find = true;
					break;
				}
			}
			if (!find){
				List<String> words = level.get(key);
				list.addAll(words);
			}
		}		
		return new ArrayList<>(list);		
	}
	
	/**
	 * 
	 * @return 一级标签
	 */
	public static List<String> getLabelFirst() {
		HashSet<String> list = new HashSet();		
		for (String key: level.keySet()){
			boolean find = false;
			for (List<String> value: level.values()){
				if (value.contains(key)){
					find = true;
					break;
				}
			}
			if (!find){
				list.add(key);
			}
		}		
		return new ArrayList<>(list);		
	}
	
	
	/**
	 * 
	 * @return 层级关系
	 */
	public static HashMap<String, List<String>> getLevel() {
		return level;
	}
	
	/**
	 * 
	 * @return 实体key属性数组为Value
	 */
	public static HashMap<String, List<String>> getproperty() {
		return property;
	}
	
	/**
	 * 
	 * @return 层级关系json数据
	 */
	public static String getjsonLevel() {
		return array.toJSONString().replace(",{}", "");
	}
	
	
	public static List<String> getRelation() {
		return relation;
		
	}
	
	/**
	 * 
	 * @return 同类之间存在关系的实体集合
	 */
	public static List<String> getRelSame() {
		return relSame;
		
	}
	
	/**
	 * 
	 * @return 关系名和其对应的行业
	 */
	public static String relationType() {
		List<String> listRel = OntologyAnalyzer.getRelation();
		HashMap<String, List<String>> listLevel = OntologyAnalyzer.getLevel();
		List<String> listLable = OntologyAnalyzer.getLabelFirst();
		HashMap<String, List<String>> mapRel = new HashMap<>();
		for (int i = 0; i < listRel.size(); i++){
			String strLevel = listRel.get(i);
			String[] strLevels = strLevel.split("-");
			String str = strLevels[0];
			while (true){
				for (String key: listLevel.keySet()){
					if (listLevel.get(key).contains(str)){
						str = key;
						break;
					}
				}
				if (listLable.contains(str)){
					break;
				}
			}
			if (mapRel.containsKey(str) && !mapRel.get(str).contains(strLevels[2])){
				mapRel.get(str).add(strLevels[2]);
			} else {
				List<String> reList = new ArrayList<>();
				reList.add(strLevels[2]);
				mapRel.put(str, reList);
			}
		}
		JSONArray array = new JSONArray();
		for (String key: mapRel.keySet()){
			JSONObject object = new JSONObject();
			object.put("name", key);
			JSONArray array1 = new JSONArray();
			for (int i = 0; i < mapRel.get(key).size(); i++){
				JSONObject object1 = new JSONObject();
				object1.put("name", mapRel.get(key).get(i));
				array1.add(object1);
			}
			object.put("children", array1);
			array.add(object);
		}
		return array.toJSONString();
	}
	
	
	/**
	 * 
	 * @return lable与其中某一属性
	 */
	public static String getLabelPro() {
		HashMap<String, List<String>> mapLevel = OntologyAnalyzer.getLevel();
		HashMap<String, List<String>> mapLabel = OntologyAnalyzer.getproperty();
		HashMap<String, String> mapPro = new HashMap<>();

		
		for (String key: mapLabel.keySet()){
			mapPro.put(key, mapLabel.get(key).get(0));
		}
		
		while (true){
			boolean hasFind = false;
			List<String> keys = new ArrayList<>(mapPro.keySet());
			for (String key: keys){
				if (mapLevel.containsKey(key)){
					for (int i = 0; i < mapLevel.get(key).size(); i++){
						String levle = mapLevel.get(key).get(i);
						if (!mapPro.containsKey(levle)){
							hasFind = true;
							mapPro.put(levle, mapPro.get(key));
						}
					}
				}
			}
			if (!hasFind){
				break;
			}
		}
		JSONObject object = new JSONObject();
		for (String key: mapPro.keySet()){
			JSONObject object2 = new JSONObject();
			object2.put("categoryAttribute", mapPro.get(key));
			object.put(key, object2);
		}
		
		return object.toJSONString();
	}
	
}
