package com.swt.ajss.restful.algorthm;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class CommunityWrite {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileOutputStream fileWriter = new FileOutputStream("D:\\CommunityJson.txt", true); 
		String cypher="match(n:STORE7189)-[r:rule]->(m:STORE7189) WHERE n.communityrule1 is not null and m.communityrule1 is not null return r ";
		System.out.println(cypher);
		Neo4jHandle nh=new Neo4jHandle("neo4j","123456","192.168.0.15","7474");
		fileWriter.write(nh.getCypherResult(cypher).getBytes());

	}

}
