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
						}else{
							List<String> next = new ArrayList<>();
							next.add(first);
							level.put(second, next);
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
				List<String> keys2 = new ArrayList<>(level.keySet());
				for (String key :keys2) {
					child = true;
					for (String ch : level.get(key)) {
						if (level.containsKey(ch)) {
							child = false;
							break;
						}
					}
					if (child) {
						JSONObject object = new JSONObject();
						object.put("name", key);
						JSONArray array = new JSONArray();
						List<String> keys = new ArrayList<>(level.get(key));
						for (String ch : keys) {
							JSONObject object1 = new JSONObject();
							allPro.add(ch);
							if (arrayAll.containsKey(ch)) {
//								System.err.println(arrayAll.get(ch));
								array.add(arrayAll.get(ch));
							}else {
								object1.put("name", ch);
							}
							array.add(object1);
						}
						object.put("children", array);
						arrayAll.put(key, object);
						keyAll.add(key);
						level.remove(key);
					}
				}
				if (level.size() == 0){
					break;
				}
			}
//			FileWriter writer5 = new FileWriter(new File(StartService.dicDir +  "/level2.txt"));
			JSONArray array = new JSONArray();
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
		List<String> list = new ArrayList<>();		
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
				for (String w: words){
					list.add(w);
				}
			}
		}		
		return list;		
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
}
