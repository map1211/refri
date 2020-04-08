package kr.kis.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

public class ServerInfoUtil {

	private FileReader resources;
	private Properties properties;

	private static LogUtil log;
	
	private String envPath = "";
	
	
	public ServerInfoUtil() {
	}
	
	public ServerInfoUtil(String envPath) {
		this.envPath = envPath;
	}
	
	/**
	 * 프로퍼티 정보 로딩.
	 * 
	 * @return
	 */
	private Properties utilInit() {

		Properties properties = new Properties();
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		
		System.out.println("### ServerInfoUtil. utilInit:: envPath::" + envPath);
		try {
			resources = new FileReader( envPath+"/resources/application.properties" );
//			resources = new FileReader( ResourceUtils.getFile("classpath:application.properties") );
			properties.load(resources);
		} catch (FileNotFoundException e) {
			ServerLog.getInstance().error(this.getClass().getName(),e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			ServerLog.getInstance().error(this.getClass().getName(),e.getMessage());
			e.printStackTrace();
		} 
		
		return properties;
	}
	
	
	/**
	 * 릴레이서버 정보
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRelayServerInfo() throws Exception {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");
		if("SA".equals(relayServerType) || "FN".equals(relayServerType)) {
			
			// 서버모드 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			// stand alone 형 인 경우
			log.info("stand alone 형 인 경우");
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));		
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));		
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			int rcount=0;
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// 수신 파일 리스트를 배열에 담기 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
			svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));

			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server 사용여부 확인 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// 클라이언트 종료 후 실행할 명령어  
			svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
			
		} else if("ST".equals(relayServerType)) {
			log.info("ST 형 인 경우");
			// 중계서버 중 맨 처음 시작하는 서버 
			int rcount = 0;
			// 프로퍼티에 등록된 항목 중 key 가 어떤게 있는지 확인하기 위해 
			// 키를 확인.
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(".R")) {
					rcount++;
					break;
				}
				
	        } 
			
			String midStr = "";

			// R로 시작하는 key가 존재하면 ST가 시작하는 relay이기 때문에 다음 서버는 RO부터 시작
			// R로 시작하는 key가 존재하지 않으면 FN 으로 설정. 
			if(rcount > 0) {
				midStr = "R0";
			} else {
				midStr = "FN";
				
			}
			
			// 서버모드 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// 수신 파일 리스트를 배열에 담기 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
			svrinfoMap.put("relayServerSendConfigPath", envPath + properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
			
			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server 사용여부 확인 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// 클라이언트 종료 후 실행할 명령어  
			svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
			
		} else if(!"ST".equals(relayServerType) && !"FN".equals(relayServerType)) {
			log.info("중계서버 중 R0 ~ R9 에 해당 하는 경우");
			// 중계서버 중 R0 ~ R9 에 해당 하는 경우 
			int svrNo = Integer.parseInt(relayServerType.replace("R", ""));
			svrNo++;
			
			if(svrNo > 9 ) {
				ServerLog.getInstance().info(this.getClass().getName(),"중계서버 횟수를 초과했습니다.");
				throw new Exception("중계서버 횟수를 초과했습니다.");
			}
			String nextSvrNo = "R" + (svrNo);
			ServerLog.getInstance().info(this.getClass().getName(),"nextSvrNo ::" + nextSvrNo );
			
			String midStr = nextSvrNo;
			int rcount = 0;
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(midStr)) {
					rcount++;
					break;
				}
	        }
			// 
			if(rcount == 0) {
				midStr = "FN";
			}
			
			// 서버모드 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
			
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// 수신 파일 리스트를 배열에 담기 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties 에 정의된 receive file list 가져오기 
			 * 
			 */
			
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));

			svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
			
			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server 사용여부 확인 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// 클라이언트 종료 후 실행할 명령어  
			svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));

			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
		}
		
		return svrinfoMap;
	}
	
	/**
	 * relay 서버 정보 
	 * 
	 * @param midfix
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRelayServerInfo(String midfix) throws Exception {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();
		
		Properties properties = utilInit();
		
		String relayServerType 	= midfix; 
		// 서버모드 T: test , R: real
		svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
		svrinfoMap.put("relayServerType", relayServerType);
		svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
		svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
		svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));	
		svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
		
		/**
		 * properties 에 정의된 receive file list 가져오기 
		 * 
		 */
		int rcount = 0;
		String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
		ArrayList arrRcvFileName = new ArrayList();
		for (String key : properties.stringPropertyNames()) {
			if(key.contains(rcvFileNameStr)) {
				arrRcvFileName.add(properties.getProperty(key).toString());
				rcount++;
			}
        }
		
		svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
		/**
		 * properties 에 정의된 receive file list 가져오기 
		 * 
		 */
		
		svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
		svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
		svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
		svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
		
		svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
		svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
		
		// relay server 사용여부 확인 
		//svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
		svrinfoMap.put("relayServerRelayUseYn", "N");
		
		// 클라이언트 종료 후 실행할 명령어  
		svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));

		
		return svrinfoMap;
	}
	
	/**
	 * socket 서버 정보
	 * @return
	 */
	public HashMap<String, Object> getSocketServerInfo() {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");
		
		// 서버모드 T: test , R: real
		svrinfoMap.put("serverMode", properties.getProperty("socket.server.mode"));  //
		
		svrinfoMap.put("serverType", relayServerType);
		svrinfoMap.put("serverIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
		svrinfoMap.put("serverPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
		svrinfoMap.put("serverEncodeType", properties.getProperty("socket.encode"));
		svrinfoMap.put("serverRecvPath", properties.getProperty("socket.client."+ relayServerType+".recvPath"));
		svrinfoMap.put("serverRecvCode", properties.getProperty("socket.server."+ relayServerType+".recvCode"));
		svrinfoMap.put("serverOrgCode", properties.getProperty("socket.server."+ relayServerType+".orgCode"));
		svrinfoMap.put("serverSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
		
		/**
		 * properties 에 정의된 receive file list 가져오기 
		 * 
		 */
		int rcount = 0;
		String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
		ArrayList arrRcvFileName = new ArrayList();
		for (String key : properties.stringPropertyNames()) {
			if(key.contains(rcvFileNameStr)) {
				// 수신 파일 리스트를 배열에 담기 
				arrRcvFileName.add(properties.getProperty(key).toString());
				rcount++;
			}
        }
		
		svrinfoMap.put("serverRecvFiles", arrRcvFileName);
		/**
		 * properties 에 정의된 receive file list 가져오기 
		 * 
		 */
		
		svrinfoMap.put("serverSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
		svrinfoMap.put("serverSendConfigPath", properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
		svrinfoMap.put("serverSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
		svrinfoMap.put("serverSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
		
		svrinfoMap.put("serverSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
		svrinfoMap.put("serverSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
		// relay server 사용여부 확인 
//		svrinfoMap.put("serverRelayUseYn", properties.getProperty("socket.server.relayYn"));
		svrinfoMap.put("serverRelayUseYn", "N");
		
		// 클라이언트 종료 후 실행할 명령어  
		svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));

		
		return svrinfoMap;
	}
}
