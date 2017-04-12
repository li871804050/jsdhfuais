package com.swt.ajss.restful.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.swt.ajss.restful.service.StartService;


public class addLable {
	
	public static void startLabel() {
//		List<String> list = read();
//		System.out.println(list.get(0)+ list.get(1)+ list.get(2)+ list.get(3));
//		StartService.connection = new Neo4jConnection(list.get(0), list.get(1), list.get(2), list.get(3));
		StartService.set();
		String[] labes = {"商业场所", "居民住宅", "内部单位", "道路", "车站码头", "公共场所", "旅店业", "市场", "学生宿舍", "交通道路", "交通运输工具"};
		String cypher = "";
		for (String labe: labes){
			cypher = "match (n:案件) where n.部位 = '" + labe + "' set n:" + labe;
			StartService.connection.exectCypher(cypher);
		}
		
		StartService.connection.exectCypher("match (n:诈骗案) where n.作案手段 = '网络电信诈骗' set n:电信诈骗");
		StartService.connection.exectCypher("match (n:诈骗案) where n.作案手段 = '网络诈骗' set n:网络诈骗");
		StartService.connection.exectCypher("match (n:诈骗案) where n.作案手段 = '其它诈骗' set n:其它诈骗");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}0[0-5][0-9]{4}' set n:凌晨");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}0[6-8][0-9]{4}' set n:早上");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}09[0-9]{4}' set n:上午");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}1[0-1][0-9]{4}' set n:上午");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}1[2-4][0-9]{4}' set n:中午");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}1[5-8][0-9]{4}' set n:下午");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}19[0-9]{4}' set n:晚上");
		StartService.connection.exectCypher("match (n:案件) where n.时间 =~ '[0-9]{8}2[0-3][0-9]{4}' set n:晚上");
		
		StartService.connection.exectCypher("match (n:人) where n.年龄 =~ '[5-8][0-9]岁' set n:年龄段:老年");
		StartService.connection.exectCypher("match (n:人) where n.年龄 =~ '[3-4][0-9]岁' set n:年龄段:中年");
		StartService.connection.exectCypher("match (n:人) where n.年龄 =~ '[2][0-9]岁' set n:年龄段:青年");
		StartService.connection.exectCypher("match (n:人) where n.年龄 =~ '[0-1][0-9]岁' set n:年龄段:少年");
		StartService.connection.exectCypher("match (n:人) where n.性别 = '男' set n:性别:男");
		StartService.connection.exectCypher("match (n:人) where n.性别 = '女' set n:性别:女");
		
		StartService.connection.exectCypher("match (n:案件) where n.作案手段 = '技术开锁' set n:入室盗窃:技术开锁");
		StartService.connection.exectCypher("match (n:案件) where n.作案手段 =~ '.*撬门.*' set n:入室盗窃:撬门入室");
		StartService.connection.exectCypher("match (n:案件) where n.作案手段 =~ '.*溜门.*' set n:入室盗窃:溜门入室");
		StartService.connection.exectCypher("match (n:案件) where n.作案手段 =~ '.*翻墙.*' set n:入室盗窃:翻墙入室");
		StartService.connection.exectCypher("match (n:案件) where n.部位 = '居民住宅' set n:入室盗窃");
		
		StartService.connection.exectCypher("match (n:案件) where n.部位 = '车上' set n:车上被盗");
		StartService.connection.exectCypher("match (n:案件) where n.部位 = '道路' set n:公共场所被盗");
		StartService.connection.exectCypher("match (n:案件)-[r]-(m:物) where m.物品名 = '电动车'  set n:盗窃电动车");
		
		StartService.connection.exectCypher("match (n:物) where n.物品名 =~ '.*卡.*' set n:卡");
		StartService.connection.exectCypher("match (n:物) where n.物品名 = '手机' set n:手机");
		StartService.connection.exectCypher("match (n:物) where n.物品名 =~ '.*车.*' set n:车");	
		StartService.connection.exectCypher("match (n:物) where n.物品名 = '现金' set n:现金");
	}
	
	/**
	 * 修改日期格式
	 * @param args
	 */
	public static void changeDate() {
		String res = StartService.connection.exectCypher1("MATCH (n:案件) where n.时间 =~ '[0-9]+' return distinct n.时间");
		List<String> list = creatIndexFromNeo4j.getArrayResult(res);
		int i ;
		
		for (String string : list) {
			i = StartService.connection.exectCypher("MATCH (n:案件)where n.时间 = '"+string+"' set n.日期 = '"+string.substring(0, 8)+"'");
		 if(i==210){
			 System.err.println("错误:"+string);
		 }
		}
		System.err.println(list.size());
	}
	
//	public static List<String> read() {
//		List<String> list = new ArrayList<>();
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(new File("a.txt")));
//			String line = "";
//			while ((line = reader.readLine()) != null ) {
//				list.add(line);
//				
//			}
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		return list;
//	}
}
