package algorithm;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.gephi.graph.api.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
/**
 * Neo4j�Ĳ���
 * @author HongYong-Qiao
 *
 */
public class Neo4jHandle {

	private static String SERVER_ROOT_URI = "http://%s:%s/db/data/";

	/** �Ƿ�򿪵��� */
	private boolean DEBUG = false;
	/**Neo4j�û���*/
	private String USERNAME = "neo4j";  
	/**Neo4j�û�����*/
	private String PASSWORD = "123456"; 
	
	private String PORT="7474";
	
	private int fail = 400;
	
	
	 /**
	  * �趨�����IP
	  * @param ip  ip��ַ
	  * @return  ����Neo4j����uri
	  */
	 private String setUri(String ip,String port){
		 SERVER_ROOT_URI=String.format(SERVER_ROOT_URI, ip,port);
	     return SERVER_ROOT_URI;
	 }
	 
	 public Neo4jHandle(String username,String password,String ip,String port){
		 this.USERNAME=username;
		 this.PASSWORD=password;
		 this.PORT=port;
		 SERVER_ROOT_URI=setUri(ip, port);
		 
		 
		 
	 }
	 
	 
	 /**
	  * ����DEBUGģʽ
	  * @param dEBUG
	  */
	 public void setDEBUG(boolean dEBUG) {
			DEBUG = dEBUG;
	 }
	 
	/**
	 * ��Neo4j��cypher��ѯ��graph��ʽ����
	 * @param cypher 
	 * @return
	 */
	 public String getCypherResult(String cypher){
		 
//		String cypher="MATCH ()-[r]->() RETURN r ";
		 
		return exectCypher(cypher);
		 
	 }

	 public void insertNeo4j(Map<String,Double> pageRankResult){
		Set<String> key =  pageRankResult.keySet();
		for(String nodeid:key){
			//String cypher="start n=node("+nodeid+") set n.weight=\""+pageRankResult.get(nodeid)+"\" return n";
			String cypher="match(n) where id(n)="+nodeid+" set n.pr='"+pageRankResult.get(nodeid)+"' return n";
//			System.out.println(cypher);
			exectCypher1(cypher);
		}
		 
		 
	 }
	 
	 
	 public void insertNeo4jCommunity(Map<String,Integer> communityResult){
			Set<String> key =  communityResult.keySet();
			for(String nodeid:key){
				//String cypher="start n=node("+nodeid+") set n.weight=\""+pageRankResult.get(nodeid)+"\" return n";
				String cypher="match(n) where id(n)="+nodeid+" set n.communitybuy1='"+communityResult.get(nodeid)+"' return n";
//				System.out.println(cypher);
				exectCypher1(cypher);
	        }
			 
			 
	}
	 
	 
	 public void insertNeo4jCommunityDegree(Iterator<Node>node,double comDegree){
		while(node.hasNext()){
			Node nodeItem=node.next();
			String nodeId=(String) nodeItem.getId();
			String cypher="match(n) where id(n)="+nodeId+" set n.communityrule1Degree='"+comDegree+"' return n";
//			System.out.println(cypher);
			exectCypher1(cypher);
		}
		 
//			for(String nodeid:key){
//				//String cypher="start n=node("+nodeid+") set n.weight=\""+pageRankResult.get(nodeid)+"\" return n";
//				String cypher="match(n) where id(n)="+nodeid+" set n.communitybuy1='"+communityResult.get(nodeid)+"' return n";
//				System.out.println(cypher);
//				exectCypher1(cypher);
//	        }
			 
			 
	}
	 
	 public void insertNeo4jGoodsPR(String nodeID,double pr){
		 String cypher="match(n) where id(n)="+nodeID+" set n.rulePR="+pr+" ";
//		 System.out.println(cypher);
	     exectCypher1(cypher);
	 }
	 
	 
	 public void insertNeo4jProperrityOfRel(int startID,int targetID,String proKey,double confidence){
		 
		 String cypher="start n=node("+startID+"),m=node("+targetID+") match (n)-[r:`buy`]-(m) set r."+proKey+"="+confidence+"";
//		 System.out.println(cypher);
		 exectCypher1(cypher);
		 
	 }
	 
//	 public void test1(String cypher){
//		 String cypher ="MATCH (n {name: '����'})-[r:`ǰ��`]-(m {name: '�����'}) RETURN r";
//		 System.out.println(exectCypher(cypher));
//	 }
	 
	/**
	 * ִ��cypher���
	 * @param cypher ��Ҫִ�е�cypher���
	 * @return ����cypher���ִ�е�json���
	 */
	private String exectCypher(String cypher) {
		final String txUri = SERVER_ROOT_URI + "transaction/commit";
		Client client = Client.create();
		
		client.addFilter(new HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource resource = client.resource(txUri);
		String payload = "{\"statements\":[{\"statement\":\""+cypher+"\",\"resultDataContents\":[\"graph\"]}]}";
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).entity(payload)
				.post(ClientResponse.class);
		String returnData = response.getEntity(String.class);
		if (DEBUG) {
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%d]", cypher, txUri,
					response.getStatus()));
		}
//		int status=response.getStatus();
		
//		System.out.println(returnData);
		return returnData;
		
	}
	
	
	public String exectCypher1(String cypher) {
		final String txUri = SERVER_ROOT_URI + "cypher";
		Client client = Client.create();
		//�趨Neo4j�û���������
		client.addFilter(new HTTPBasicAuthFilter(USERNAME, PASSWORD));
		WebResource resource = client.resource(txUri);
		String payload = "{\"query\" : \"" + cypher + "\"}";
//		System.out.println(payload);
		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).entity(payload)
				.post(ClientResponse.class);
		String returnData = response.getEntity(String.class);
		if (DEBUG) {
			System.out.println(String.format(
					"CQL [%s],  POST to [%s], status code [%d]", cypher, txUri,
					response.getStatus()));
		}
//		int status=response.getStatus();
		
		return returnData;
		
	}
	
	public String toString() {
		return SERVER_ROOT_URI;
	}
	 

}
