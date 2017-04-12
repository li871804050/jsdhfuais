package com.swt.ajss.restful.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;


public class owlTest {
	public static void main(String[] args) {
		deal(StartService.dicDir + "/20170325.owl");
	}
	public static List<Object> deal(String filePath){
		List<Object> list = new ArrayList<>();
		try {
			SAXBuilder builder = new SAXBuilder();//实例JDOM解析器  
	        Document document = builder.build(filePath);
			Element root = document.getRootElement();
			Namespace ns = Namespace.getNamespace("http://www.w3.org/2002/07/owl#");
			List<Element> elements = root.getChildren("SubClassOf", ns);
			HashMap<String, List<String>> level = new HashMap<>();
			HashMap<String, List<String>> property = new HashMap<>();
			List<String> pross = new ArrayList<>();
			List<String> relation = new ArrayList<>();
			for (Element e: elements){
				try{
					String entrity = e.getChild("Class", ns).getAttributeValue("IRI").replace("#", "");
					Element dataFrom = e.getChild("DataSomeValuesFrom", ns);
					String pro = dataFrom.getChild("DataProperty", ns).getAttributeValue("IRI").replace("#", "");
					if (property.containsKey(entrity)){
						property.get(entrity).add(pro);
						pross.add(pro);
					}else{
						List<String> pros = new ArrayList<>();
						pros.add(pro);
						property.put(entrity, pros);
						pross.add(pro);
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
			list.add(level);
			list.add(property);
			list.add(relation);
			list.add(pross);
			JSONArray jsonArray = new JSONArray();
			FileWriter writer = new FileWriter(new File(StartService.dicDir + "/level.txt"));
			for (String key: level.keySet()){
				writer.write(key + ":");
				for (String value: level.get(key)){
					writer.write(value + "\t");
				}
				writer.write("\r\n");
			}
			writer.close();
			Map<String, List<Map>> map = new HashMap<>();
			int count = -1;
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
						boolean la = false;
						for (String ch : keys) {
							JSONObject object1 = new JSONObject();
							JSONArray array1 = new JSONArray();
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
			FileWriter writer5 = new FileWriter(new File(StartService.dicDir +  "/level2.txt"));
			JSONArray array = new JSONArray();
			for (String la: keyAll){
				if (!allPro.contains(la)) {
					array.add(arrayAll.get(la));
				}
			}
			writer5.write(array.toJSONString());
			writer5.close();

			FileWriter writer2 = new FileWriter(new File(StartService.dicDir + "/rel.txt"));
			for (String rels: relation){
				writer2.write(rels + "\r\n");
			}
			writer2.close();
			FileWriter writer3 = new FileWriter(new File(StartService.dicDir + "/pro.txt"));
			for (String key: property.keySet()){
				writer3.write(key + ":");
				for (String value: property.get(key)){
					writer3.write(value + "\t");
				}
				writer3.write("\r\n");
			}
			
			writer3.close();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	} 

}
