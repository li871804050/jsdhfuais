package com.swt.ajss.restful.graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


/**
 * 
 * @author Administrator
 *	GraphIndex类主要是用来对图数据库中的数据创建索引，使用索引
 */
public class GraphIndex {
	public static final String LINK_1 = "####";
	public static final String LINK_2 = "%%%";
	public static final String KEYWORDS_1 = "实体";
	public static final String KEYFIELD_1 = "实体名";
	public static final String KEYFIELD_2 = "属性名";
	public static final String KEYFIELD_3 = "属性值";
	public static String indexPath = StartService.dicDir + "/index2";
	public static void main(String[] args) {

		OntologyAnalyzer ontologyAnalyzer = new OntologyAnalyzer("dic/化工.owl");
		creatIndex(indexPath, 1);
//		List<String> reStrings = searchIndex(indexPath, "维生素B");
//		for (int i = 0; i < reStrings.size(); i++){
//			System.out.println(reStrings.get(i));
//		}
	}
	
	
	/**
	 * 
	 * @param dicDir 索引文件目录
	 * @param anChose 分词器选择，1为hanlp分词器，其他为Lucene自带标准分词器
	 * @param 对图数据库中的KEYFIELD_3，KEYFIELD_1，关系名创建索引
	 */
	public static void creatIndex(String dicDir, int anChose){
		StartService.set();
//		List<String> list = addLable.read();
//		StartService.connection = new Neo4jConnection(list.get(0), list.get(1), list.get(2), list.get(3));
		try {
			
			File file = new File(dicDir);
			if (file.exists()){
				for (File f: file.listFiles()){
					f.delete();
				}
			}
			Path path = Paths.get(dicDir);
			Directory directory = FSDirectory.open(path);
			Analyzer analyzer = null;
			if (anChose == 1){
				analyzer = new HanLPAnalyzer();
			}else {
				analyzer = new StandardAnalyzer();
			}
			IndexWriterConfig config = new IndexWriterConfig(analyzer);			 
			IndexWriter writer = new IndexWriter(directory, config);
						
//			String[] labels = {"ClassID1", "ClassID1Name", "ClassID2", "ClassID2Name", "ClassID3", "ClassID3Name", "ClassID", "ClassID4Name", "conf", "label", "lift", "Price_Com", "Price_Must" ,"rulePR", "sku", "SkuName", "Standards", "sup", "Time_Def", "Unit_Sale"};
			//labels 应该为文件输入
			Map <String, List<String>> pros = OntologyAnalyzer.getproperty();
//			String ent = "Store0044";
			//实体类型为文件输入
			//
			
			
			for (String key: pros.keySet()){
				List<String> labels = pros.get(key);
				/*
				 * label索引创建（n:y）
				 * KEYFIELD_1为实体
				 * KEYFIELD_3为y
				 * 属性名为y
				 * */
				Document documnet = new Document();
				Field field1 = new TextField(KEYFIELD_1, KEYWORDS_1, Field.Store.YES);
				Field field2 = new TextField(KEYFIELD_3, key, Field.Store.YES);
				Field field3 = new TextField(KEYFIELD_2, key, Field.Store.YES);
				documnet.add(field1);
				documnet.add(field2);
				documnet.add(field3);
//				System.out.println(documnet.toString());
				writer.addDocument(documnet);
				
				for (String label: labels){
					String result = "";
					String cypher = "MATCH (n:" + key +") RETURN DISTINCT n." + label;
					result = StartService.connection.exectCypher1(cypher);
					List<String> allPro = GraphData.getArrayResult(result);										
					
					for (String pro: allPro){
					/*
					 * 属性索引创建match（n:y）return n.x
					 * KEYFIELD_1为y
					 * KEYFIELD_3为n.x
					 * 属性名为x
					 * */
						pro = pro.replace("\\", ".+");
						documnet = new Document();
						field1 = new TextField(KEYFIELD_1, key, Field.Store.YES);
						field2 = new TextField(KEYFIELD_3, pro, Field.Store.YES);
						field3 = new TextField(KEYFIELD_2, label, Field.Store.YES);
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
			 * KEYFIELD_1为y-x
			 * KEYFIELD_3为r.sup
			 * 属性名为关系R
			 * */
			List<String> reList = OntologyAnalyzer.getRelation();
			for (String rel: reList){
				String[] relDatas = rel.split("-");
				Field field1 = new TextField(KEYFIELD_1, relDatas[0]+ LINK_2 + relDatas[1], Field.Store.YES);
				Field field2 = new TextField(KEYFIELD_3, relDatas[2], Field.Store.YES);
				Field field3 = new TextField(KEYFIELD_2, relDatas[2], Field.Store.YES);
//				String result = "";
//				String cypher = "MATCH (n:" + relDatas[0] +")-[r:" + relDatas[2] + "]-(" + relDatas[1] + ") RETURN DISTINCT r.sup";				
//				result = StartService.connection.exectCypher1(cypher);
//				List<String> allPro = getArrayResult(result);										
//				
//				for (String pro: allPro){ 
				Document documnet = new Document();
//					Field field1 = new TextField("KEYFIELD_1", relDatas[0]+ "%%%" + relDatas[1], Field.Store.YES);
//					Field field2 = new TextField("KEYFIELD_3", pro, Field.Store.YES);
//					Field field3 = new TextField(KEYFIELD_2, relDatas[2], Field.Store.YES);
				documnet.add(field1);
				documnet.add(field2);
				documnet.add(field3);
				writer.addDocument(documnet);
//				}
			}
			
			List<String> levelSet = OntologyAnalyzer.getEntity();
			for (String rel: levelSet){
				Field field1 = new TextField(KEYFIELD_1, KEYWORDS_1, Field.Store.YES);
				Field field2 = new TextField(KEYFIELD_3, rel, Field.Store.YES);
				Field field3 = new TextField(KEYFIELD_2, rel, Field.Store.YES);
//				String result = "";
//				String cypher = "MATCH (n:" + relDatas[0] +")-[r:" + relDatas[2] + "]-(" + relDatas[1] + ") RETURN DISTINCT r.sup";				
//				result = StartService.connection.exectCypher1(cypher);
//				List<String> allPro = getArrayResult(result);										
//				
//				for (String pro: allPro){ 
				Document documnet = new Document();
//					Field field1 = new TextField("KEYFIELD_1", relDatas[0]+ "%%%" + relDatas[1], Field.Store.YES);
//					Field field2 = new TextField("KEYFIELD_3", pro, Field.Store.YES);
//					Field field3 = new TextField(KEYFIELD_2, relDatas[2], Field.Store.YES);
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
			System.out.println("error");
			e.printStackTrace();
		}
		
//		String cypher = "MATCH (n) RETURN DISTINCT keys(n)";
//		String returnDataFormat = "";
//		System.out.println(StartService.connection.exectCypher(cypher));
	}
	
	
	
	
	/**
	 * 
	 * @param indexDir	索引目录
	 * @param keyWord	查找的内容
	 * @return 在索引文件中查找到的所有结果
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
            Query query = builder.createPhraseQuery(KEYFIELD_3, keyWord);
            ScoreDoc[] sd = searcher.search(query, 1000).scoreDocs;
          
            for (int i = 0; i < sd.length; i++) {
                Document hitDoc = reader.document(sd[i].doc);
                String en = hitDoc.get(KEYFIELD_1);
                String pN = hitDoc.get(KEYFIELD_2);
                String pR = hitDoc.get(KEYFIELD_3);
                result.add(en + LINK_1 + pN + LINK_1 + pR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		return result;
    }
    
}
