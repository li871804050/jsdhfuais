package esclient;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @param String index 索引名称
 * @param String[] values 需要查询分析关系的两个value
 * @param String[] fields 字段组，其中第一个fields[0]为value对应的字段
 * @author Administrator
 *
 */
public class Tupu {

	public static void main(String[] args) {
		String index = "a111";
		String[] fields = new String[]{"姓名","车次","日期"};
		String[] values = new String[]{"name6","name7"};

		JSONArray jsons = searchGraph(index,fields,values);
		System.out.println(jsons);
	}

	public static JSONArray searchGraph(String index, String[] fields, String[] values){
		terms terms1 = new terms(fields[0], values[0]);
		terms terms2 = new terms(fields[0], values[1]);
		JSONArray jsons12 = new JSONArray();

		try {

			//设置集群名称
			Settings settings = Settings.builder().put("cluster.name", "PYL").build();
			//创建client
			TransportClient client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.0.141"), 9300));


			/*
			 * 根据其中一个field和value查询所有的文档
			 */

			SearchResponse response3 = client.prepareSearch(index)
					// .setTypes("csv")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(QueryBuilders.termQuery(terms1.field, terms1.term))                 // Query
					//.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
					// .setFrom(0).setSize(60)
					.setExplain(true)
					.get();

			SearchHit[] bb =  response3.getHits().hits();


			List list = new ArrayList();

			Map<String, String> map5 = new LinkedHashMap<String, String>();
			map5.put(new String("field"),fields[0]);
			map5.put(new String("term"),values[0] );
			//	 map5.put(new String("index"), String.valueOf(0));

			Map<String, String> map6 = new LinkedHashMap<String, String>();
			map6.put(new String("field"),fields[0]);
			map6.put(new String("term"),values[1] );
			//	 map6.put(new String("index"), String.valueOf(1));

			JSONObject json20 = JSONObject.fromObject(map5);
			JSONObject json21 = JSONObject.fromObject(map6);
			
			for (int i = 0; i < bb.length; i++) {
				Map<String, Object> dd = bb[i].getSource();
				String rr = dd.get(fields[1])+","+dd.get(fields[2]);
				if(list.contains(rr)){

				}else{
					list.add(rr);
					//   System.out.println(dd.get(fields[1])+","+dd.get(fields[2]));

					BoolQueryBuilder qb = boolQuery()
							.must(termQuery(fields[1]+".keyword", dd.get(fields[1])))
							.must(termQuery(fields[2]+".keyword", dd.get(fields[2])))
							.must(termQuery(fields[0]+".keyword", values[1]));

					SearchResponse response4 = client.prepareSearch(index)

							.setSearchType(SearchType.DFS_QUERY_THEN_FETCH) 
							.setQuery(qb)
							.setExplain(true)
							.get();
					// System.out.println(response4);
					int s = response4.getHits().hits().length;
					//  System.out.println(s);

					if(s!=0){
						Map<String, String> map1 = new LinkedHashMap<String, String>();
						Map<String, String> map3 = new LinkedHashMap<String, String>();
						
						Map<String, Object> map11 = new LinkedHashMap<String, Object>();

						// String a1 = String.valueOf(index++);
						map1.put(new String("field"),fields[1]);
						map1.put(new String("term"),(String) dd.get(fields[1]) );
						// map1.put(new String("index"), a1);

						// String a2 = String.valueOf(index++);
						map3.put(new String("field"),fields[2]);
						map3.put(new String("term"),(String) dd.get(fields[2]) );
						
						map11.put("count", s);

						JSONObject json1 = JSONObject.fromObject(map1);
						JSONObject json3 = JSONObject.fromObject(map3);
						JSONObject jsons1 = new JSONObject();
				
						JSONObject jsons20 = new JSONObject();
						JSONObject jsons21 = new JSONObject();
						JSONObject jsons22 = new JSONObject();
						JSONObject jsons23 = new JSONObject();

						JSONObject json11 = JSONObject.fromObject(map11);
						jsons20.put("ent1", json20);
						jsons20.put("ent2", json1);
						jsons20.put("rel", json11);
						jsons22.put("ent1", json20);
						jsons22.put("ent2", json3);
						jsons22.put("rel", json11);
						jsons23.put("ent1", json21);
						jsons23.put("ent2", json1);
						jsons23.put("rel", json11);
						jsons21.put("ent1", json21);
						jsons21.put("ent2", json3);
						jsons21.put("rel", json11);
						jsons1.put("ent1", json1);

						jsons1.put("ent2", json3);

						

						jsons1.put("rel", json11);

						jsons12.add(jsons1);
						jsons12.add(jsons20);
						jsons12.add(jsons21);
						jsons12.add(jsons22);
						jsons12.add(jsons23);
						
					}
				}
			}

			

			client.close();


		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsons12;

	}
}
