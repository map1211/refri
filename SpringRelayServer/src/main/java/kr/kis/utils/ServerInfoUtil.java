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
//			resources = new FileReader( envPath + "/resources/application.properties" );
			resources = new FileReader( ResourceUtils.getFile("classpath:application.properties") );
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
//		} else if("ST".equals(relayServerType)) {
//			// 중계서버 중 맨 처음 시작하는 서버 
//			int rcount = 0;
//			// 프로퍼티에 등록된 항목 중 key 가 어떤게 있는지 확인하기 위해 
//			// 키를 확인.
//			for (String key : properties.stringPropertyNames()) {
//	            //Object value = properties.getProperty(key);
////				ServerLog.getInstance().info(this.getClass().getName(),"### properties :: key :: " + key);
//				if(key.contains(".R")) {
//					rcount++;
//					break;
//				}
//				
//	        }
//			
//			String midStr = "";
//
//			// R로 시작하는 key가 존재하면 ST가 시작하는 relay이기 때문에 다음 서버는 RO부터 시작
//			// R로 시작하는 key가 존재하지 않으면 FN 으로 설정. 
//			if(rcount > 0) {
//				midStr = "R0";
//			} else {
//				midStr = "FN";
//				
//			}
//			
//			svrinfoMap.put("relayServerType", relayServerType);
//			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
//			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
//			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));
//			svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));
//			svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));
			
		} else if("ST".equals(relayServerType) || "FN".equals(relayServerType)) {
			// 중계서버 중 맨 마지막 서버 
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.host.ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.host.port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));
			svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));
			svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));
//		} else if(!"ST".equals(relayServerType) && !"FN".equals(relayServerType)) {
//			// 중계서버 중 R0 ~ R9 에 해당 하는 경우 
//			int svrNo = Integer.parseInt(relayServerType.replace("R", ""));
//			svrNo++;
//			
//			if(svrNo > 9 ) {
//				ServerLog.getInstance().info(this.getClass().getName(),"중계서버 횟수를 초과했습니다.");
//				throw new Exception("중계서버 횟수를 초과했습니다.");
//			}
////			String nextSvrNo = "R" + (svrNo < 10 ? "0"+svrNo : svrNo);
//			String nextSvrNo = "R" + (svrNo);
//			//ServerLog.getInstance().info(this.getClass().getName(),"nextSvrNo ::" + nextSvrNo );
//			log.info("nextSvrNo ::" + nextSvrNo );
//			
//			String midStr = nextSvrNo;
//			int rcount = 0;
//			for (String key : properties.stringPropertyNames()) {
//	            //Object value = properties.getProperty(key);
//	//			ServerLog.getInstance().info(this.getClass().getName(),"### properties :: key :: " + key);
//				if(key.contains(midStr)) {
//					rcount++;
//					break;
//				}
//	        }
//			// 
//			if(rcount == 0) {
//				midStr = "FN";
//			}
//			log.info("midStr ::" + midStr );
//			svrinfoMap.put("relayServerType", relayServerType);
//			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
//			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
//			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.server.encoding"));
//			svrinfoMap.put("relayServerSocketTimeout", properties.getProperty("socket.server.timeout"));
//			svrinfoMap.put("relayServerThreadNum", properties.getProperty("thread.maxNum"));
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
	
	
	
	 /**
	   * Look for an open port, starting with port+1.
	   */
	public int findNextOpenPortAbove(int port) throws IOException {
		    boolean foundPort = false;
		    int nextTrialPort = port;
		    ServerSocket serverSocket = null;
		    while (!foundPort) {
		      nextTrialPort++;
		      try {
		        serverSocket = new ServerSocket(nextTrialPort);
		        foundPort = true;
		      } catch (IOException e) {
		        // continue with the attempts until we find an open port
		        if (nextTrialPort == 65535) throw new IOException("No open port.");
		      } finally {
		        try { serverSocket.close(); } catch (Exception e) {}
		      }
		    }
		    return nextTrialPort;
	}
	
	/**
	 * Look for an open port, starting with port+1.
	 */
	public int findNextOpenPortAbove() throws IOException {
		boolean foundPort = false;
		String sip = "210.112.100.97";
		int nextTrialPort = 9029;
		ServerSocket serverSocket = null;
		while (!foundPort) {
			nextTrialPort++;
			try {
				serverSocket = new ServerSocket(nextTrialPort);
				foundPort = true;
			} catch (IOException e) {
				// continue with the attempts until we find an open port
				if (nextTrialPort == 65535) throw new IOException("No open port.");
			} finally {
				try { serverSocket.close(); } catch (Exception e) {}
			}
		}
		return nextTrialPort;
	}
}
