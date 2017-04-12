package com.swt.ajss.restful.service;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class RestFullService {

	private static int port = 0; 
	
	public static void setPort(int defaultPort) {
        String port = System.getenv("JERSEY_HTTP_PORT");   //优先采用系统变量端口号
        if (null != port) {
            try {
                RestFullService.port =  Integer.parseInt(port);
                return;
            } catch (NumberFormatException e) {
            	e.printStackTrace();
            }
        }else{
        	RestFullService.port = defaultPort;
        }
             
    }
	
	public static void setPort(String port){
		if(null==port||"".equals(port)){
			throw new NumberFormatException("端口格式错误~");
		}
		setPort(Integer.parseInt(port));
	}
     
	public static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(port).build();
    }


    public static SelectorThread startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();

        initParams.put("com.sun.jersey.config.property.packages", 
                "com.swt.ajss.restful.resource");

        System.out.println("Starting grizzly...");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(getBaseURI(), initParams);     
        return threadSelector;
    }
    
    public static int getPort() throws Exception{
    	if(0>=port){
    		throw new Exception("端口号不能为0.");
    	}
    	return port;
    }
    
    
    
}
