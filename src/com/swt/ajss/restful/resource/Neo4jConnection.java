package com.swt.ajss.restful.resource;



import java.net.ConnectException;

import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class Neo4jConnection {
	private Client client;
	
	private String ip;
	private String port;
	private  String USERNAME;  //Neo4j用户名
	private  String PASSWORD; //Neo4j用户密码
	
	/**是否打开调试*/
	private  boolean DEBUG = true;
	private int fail = 400;
	
	private String SERVER_ROOT_URI;
	
	public Neo4jConnection(){
		this.client = Client.create();
		this.ip = "192.168.0.191";
		this.port = "7474";
		this.USERNAME = "neo4j";
		this.PASSWORD = "1234";
		
	}
	
	public Neo4jConnection(String host, String port, String userName, String passWd){
		this();
		this.ip = host;
		this.port = port;
		this.USERNAME = userName;
		this.PASSWORD = passWd;
	}
	
	
	
	public void setDEBUG(boolean debug) {
			this.DEBUG = debug;
	}
	 
	public ClientResponse execSimpleStr(String str){
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return null;
		}
		String txUri = serverRootUri;
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource res = client.resource(txUri);
		try {
			ClientResponse response = res.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.get(ClientResponse.class);
			//response.close();
			return response;
		} catch (Exception e){
			System.out.println("链接执行出错："+txUri);
			return null;
		}
	}
	
	public ClientResponse execStr(String str){
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return null;
		}
		String txUri = serverRootUri;
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource res = client.resource(txUri);
		ClientResponse response = res.accept(MediaType.APPLICATION_JSON)
									.type(MediaType.APPLICATION_JSON_TYPE)
									.entity(str)   //str作为参数
									.post(ClientResponse.class);
		//response.close();
		return response;
	}
	
	
	/**
	  * 执行cypher语句，只有增删改,针对/db/data操作
	  * @param  cypher  需要执行的cypher语句
	  * @return 返回200表示操作成功
	 * @throws JSONException 
	  */
	
	public int exectCypher(String cypher) {
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return 210;
		}
		String txUri = serverRootUri + "cypher";
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource  resource = client.resource( txUri );
		String payload = "{\"query\" : \"" + cypher + "\"}";
		ClientResponse response=resource
				.accept( MediaType.APPLICATION_JSON )  //接收数据类型json
		        .type( MediaType.APPLICATION_JSON_TYPE )
		        .entity(payload)   //请求参数内容
		        .post( ClientResponse.class ); //执行post请求
		String returnData=response.getEntity( String.class ); //返回请求数据
		////
		//System.out.println(payload);
//		System.out.println("result="+returnData);
		int status = response.getStatus();
		response.close();
		if(200!=status){
			////
			System.out.println("返回数据为：" + returnData);
			if(DEBUG){
				System.out.println(String.format(
						"查询语句为： [%s],  POST to [%s], status code [%s]",cypher,
			txUri, status));                                     
				 }	 
			////
		}
		return status;	
	 }
	
	
	public String exectCypher1(String cypher) {
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return "210";
		}
		String txUri = serverRootUri + "cypher";
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource  resource = client.resource( txUri );
		String payload = "{\"query\" : \"" + cypher + "\"}";
		ClientResponse response=resource
				.accept( MediaType.APPLICATION_JSON )  //接收数据类型json
		        .type( MediaType.APPLICATION_JSON_TYPE )
		        .entity(payload)   //请求参数内容
		        .post( ClientResponse.class ); //执行post请求
		String returnData=response.getEntity( String.class ); //返回请求数据
		////
		//System.out.println(payload);
		//System.out.println("result="+returnData);
		int status = response.getStatus();
		response.close();
		if(200!=status){
			////			
			System.out.println("返回数据为：" + returnData);
			if(DEBUG){
				System.out.println(String.format(
						"查询语句为： [%s],  POST to [%s], status code [%s]",cypher,
			txUri, status));                                     
				 }	 
			////
			return "error";
		}
		return returnData;	
	 }
	
	/**
	 * 执行Cypher语句，针对location传来的地址进行操作，如 /db/data/batch
	 * @param cypher
	 * @param location 地址，如 /db/data或/db/data/batch
	 * @return
	 */
	public int exectCypher(String cypher, String location) {
		String serverRootUri = getSERVER_ROOT_URI(location);
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return 210;
		}
		String txUri = serverRootUri + "cypher";
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource  resource = client.resource( txUri );
		String payload = "{\"query\" : \"" + cypher + "\"}";
		ClientResponse response=resource
				.accept( MediaType.APPLICATION_JSON )  //接收数据类型json
		        .type( MediaType.APPLICATION_JSON_TYPE )
		        .entity(payload)   //请求参数内容
		        .post( ClientResponse.class ); //执行post请求
		String returnData=response.getEntity( String.class ); //返回请求数据
		//System.out.println(returnData);

		/*if(DEBUG){
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%s]",cypher,
		txUri, response.getStatus()));                                     
			 }*/	 
		int status = response.getStatus();
		response.close();
		return status;	
	 }
	
	
	
	public String exectCypherEx(String cypher) {
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return null;
		}
		String txUri = serverRootUri + "cypher";
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource  resource = client.resource( txUri );
		String payload = "{\"query\" : \"" + cypher + "\"}";
		ClientResponse response=resource
				.accept( MediaType.APPLICATION_JSON )  //接收数据类型json
		        .type( MediaType.APPLICATION_JSON_TYPE )
		        .entity(payload)   //请求参数内容
		        .post( ClientResponse.class ); //执行post请求
		String returnData = response.getEntity( String.class ); //返回请求数据
/*
		if(DEBUG){
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%s]",cypher,
		txUri, response.getStatus())); 
			
			System.out.println(returnData);
			 }	 */
		response.close();
		return returnData;	
	 }
	
	/**
	 * 执行cypher查询语句
	 * @param cypher 需要执行的cypher语句
	 * @return 返回cypher语句执行的json结果，节点和关系分开显示
	 */
	public  String exectSearchCypher(String cypher,String returnDataFormat) {
		String serverRootUri = getSERVER_ROOT_URI();
		if(null==serverRootUri || ""==serverRootUri){
			if(DEBUG){
				System.out.println("ecectCypher: getServerRootUri 为空!");
			}
			return null;
		}
		String txUri = serverRootUri + "transaction/commit";
		client.addFilter(new HTTPBasicAuthFilter(USERNAME,PASSWORD));
		WebResource  resource = client.resource( txUri );   //这里的uri与上面不一样
		String payload = "{\"statements\":[{\"statement\":\""+cypher+"\",\"resultDataContents\":[\"" + returnDataFormat+"\"]}]}";
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).entity(payload)
				.post(ClientResponse.class);
		String returnData = response.getEntity(String.class);
		/*if (DEBUG) {
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%d]", cypher, txUri,
					response.getStatus()));
		}*/
//		int status=response.getStatus();
		response.close();
		return returnData;
		
	}
	
	/**
	 * 返回资源地址，默认为/db/data/
	 * @return
	 */
	public String getSERVER_ROOT_URI() {
		this.SERVER_ROOT_URI = "http://"+ip+ ":" +port+ "/db/data/";
		return SERVER_ROOT_URI;
	}

	public void setSERVER_ROOT_URI(String sERVER_ROOT_URI) {
		SERVER_ROOT_URI = sERVER_ROOT_URI;
	}



	/**
	 * 返回资源地址，传入地址，如“/db/data/batch”
	 * @return
	 */
	public String getSERVER_ROOT_URI(String location) {
		this.SERVER_ROOT_URI = "http://"+ip+ ":" +port+ location;
		return SERVER_ROOT_URI;
	}
	
}
	 /**
	  * 执行cypher查询语句
	  * @param  cypher  需要执行的cypher语句
	  * @return 返回查询结果，json顺序列表显示
	 * @throws JSONException 
	  */
	
//	public String exectSearchCypherList(String cypher) {
//		String payload = "{\"query\" : \"" + cypher + "\"}";
//		ClientResponse response=resource
//				.accept( MediaType.APPLICATION_JSON )
//		        .type( MediaType.APPLICATION_JSON_TYPE )
//		        .entity(payload)
//		        .post( ClientResponse.class );
//		String returnData=response.getEntity( String.class );
//		//System.out.println(returnData);
//
//		if(DEBUG){
//			System.out.println(String.format(
//					"CQL [%s],  POST to [%s], status code [%s]",cypher,
//		txUri, response.getStatus()));                                     
//			 }	 
//		response.close();
//		return returnData;	
//	 }
	
	
/*public  String exectSearchCypherRow(String cypher) {
		
		String payload = "{\"statements\":[{\"statement\":\""+cypher+"\",\"resultDataContents\":[\"row\"]}]}";
		ClientResponse response = resource1.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(payload)
				.post(ClientResponse.class);
		String returnData = response.getEntity(String.class);
		if (DEBUG) {
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%d]", cypher, txUri1,
					response.getStatus()));
		}
//		int status=response.getStatus();
		
		return returnData;
		
	}
*/

/*public  String exectSearchCypherGraph(String cypher) {
	
	String payload = "{\"statements\":[{\"statement\":\""+cypher+"\",\"resultDataContents\":[\"graph\"]}]}";
	ClientResponse response = resource1.accept(MediaType.APPLICATION_JSON)
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(payload)
			.post(ClientResponse.class);
	String returnData = response.getEntity(String.class);
	if (DEBUG) {
		System.out.println(String.format(
				"CQL [%s],  POST to [%s], status code [%d]", cypher, txUri1,
				response.getStatus()));
	}
//	int status=response.getStatus();
	
	return returnData;
	
}*/
	