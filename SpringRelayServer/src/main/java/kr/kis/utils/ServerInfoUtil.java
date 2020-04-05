package kr.kis.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

public class ServerInfoUtil {

	private static LogUtil log;
	

	private FileReader resources;
	private Properties properties;
	private Properties sysProp;
	private String envPath = "";
	
	public ServerInfoUtil() {
//		log = new LogUtil(this.getClass().getName());
	}
	
	public ServerInfoUtil(String envPath) {
//		log = new LogUtil(this.getClass().getName(), envPath);
//		log.info("######## ServerInfoUtil(envPath) :: envPath :: " + envPath );
		this.envPath = envPath;
	}
	
	private Properties utilInit() {

		properties = new Properties();
		
		sysProp = System.getProperties();
		
		if("".equals(envPath)) envPath = ".";
		
		try {
			// jar에서 실행시 사용
			resources = new FileReader( envPath + "/resources/application.properties" );
			// eclipse 에서 테스트시 적용 
//			resources = new FileReader( ResourceUtils.getFile("classpath:application.properties") );
			properties.load(resources);
		} catch (FileNotFoundException e) {
//			log.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (IOException e) {
			ServerLog.getInstance().error(this.getClass().getName(),e.getMessage());
//			log.error(e.getMessage(),e);
			e.printStackTrace();
		} 
		
		return properties;
	}
	

	

	
	public HashMap<String, Object> getRelayServerInfo() throws Exception {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");
		if("SA".equals(relayServerType) ) {
			// stand alone 형 인 경우
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));			
			svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));			
			svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));			
			
		} else if("ST".equals(relayServerType) || "FN".equals(relayServerType)) {
			// 중계서버 중 맨 마지막 서버 
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.host.ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.host.port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));
			svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));
			svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));
		}
		
		return svrinfoMap;
	}
	
	
	
	
	public HashMap<String, Object> getSocketServerInfo() {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");

		svrinfoMap.put("relayServerType", relayServerType);
		svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
		svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
		svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));
		svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));
		svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));
		
		return svrinfoMap;
	}
	
	
	

}
