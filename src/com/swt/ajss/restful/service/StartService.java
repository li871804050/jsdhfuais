package com.swt.ajss.restful.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.sun.grizzly.http.SelectorThread;
import com.swt.ajss.restful.resource.Neo4jConnection;
import com.swt.ajss.restful.resource.addLable;
import com.swt.ajss.restful.resource.analyzer;
import com.swt.ajss.restful.resource.creatIndexFromNeo4j;

import algorithm.Neo4jHandle;

public class StartService {

	SelectorThread threadSelector;

	private int defaultPort = 9900; // 默认端口
	public static Neo4jConnection connection = null;
	public static Neo4jHandle neo4jHandle = new Neo4jHandle("neo4j", "123456", "192.168.0.191", "7474");
	public static String dicDir = "dic";
	public static void main(String[] args) {
//		dicDir = args[0];
		System.err.println("添加label");
		addLable.startLabel();
		System.err.println("添加label完成");
		System.err.println("本体解析");
//		owlTest.deal(dicDir + "/" + args[1]);
		owlTest.deal(dicDir + "/20170417.owl");
		System.err.println("本体解析完成");
		System.err.println("索引创建");
		creatIndexFromNeo4j.creatIndex(dicDir, 1);
		System.err.println("索引创建完成");
		StartService.start();
		
	}
	
	public static void set() {
		List<String> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(dicDir + "/config.txt")));
			String line = "";
			while ((line = reader.readLine()) != null ) {
				list.add(line);
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		dicDir = args[0];
//		System.out.println(list.get(0)+list.get(1)+list.get(2)+list.get(3));
		connection = new Neo4jConnection(list.get(0), list.get(1), list.get(2), list.get(3));
		neo4jHandle = new Neo4jHandle(list.get(2), list.get(3), list.get(0), list.get(1));
	}
	
	public static void start() {
		StartService service = new StartService();
		service.setupNoGUI(0);
		
	}


	public void runService(String port) throws Exception {

		if (null == threadSelector) {
			try {
				RestFullService.setPort(port);
				threadSelector = RestFullService.startServer();
			} catch (IOException e) {
				throw new BindException("IO异常，绑定端口到" + RestFullService.getPort() + "出错.");
			}
		} else {
			threadSelector.start();
		}

	}

	public void stopService() {
		if (null != threadSelector) {
			threadSelector.stopEndpoint();
			threadSelector = null;
			System.out.println("服务已停止~");
		}
	}

	protected void setupNoGUI(int port) {
		int portNum = 0;
		if (port <= 0) {
			portNum = this.defaultPort;
		} else {
			portNum = port;
		}
		try {
			runService(String.valueOf(portNum));
			System.out.println("服务已启动~" + String.format(
					"Jersey app started with WADL available at "
							+ "%sapplication.wadl\nTry out %s\nHit enter to stop it...",
					RestFullService.getBaseURI(), RestFullService.getBaseURI()));
		} catch (BindException ex) {
			System.err.println("启动失败！" + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e1) {
			System.err.println("启动出错！" + e1.getMessage());
			e1.printStackTrace();
		}

//		@SuppressWarnings("resource")
//		Scanner s = new Scanner(System.in);
//		s.nextLine(); // 等待输入换行符结束
//		stopService();

	}
	
	

}
