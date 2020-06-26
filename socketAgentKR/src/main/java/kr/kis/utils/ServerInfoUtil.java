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
	 * �봽濡쒗띁�떚 �젙蹂� 濡쒕뵫.
	 * 
	 * @return
	 */
	private Properties utilInit() {

		Properties properties = new Properties();
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		
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
	 * 由대젅�씠�꽌踰� �젙蹂�
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRelayServerInfo() throws Exception {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");
		if("SA".equals(relayServerType) || "FN".equals(relayServerType)) {
			
			// �꽌踰꾨え�뱶 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			// stand alone �삎 �씤 寃쎌슦
			log.info("stand alone �삎 �씤 寃쎌슦");
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));		
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
			svrinfoMap.put("relayServerSocketPacketCount", Integer.parseInt(properties.getProperty("socket.server.packetcnt")));
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			int rcount=0;
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// �닔�떊 �뙆�씪 由ъ뒪�듃瑜� 諛곗뿴�뿉 �떞湲� 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
			svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));

			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server �궗�슜�뿬遺� �솗�씤 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// �겢�씪�씠�뼵�듃 醫낅즺 �썑 �떎�뻾�븷 紐낅졊�뼱  
			if( properties.getProperty("fin.exec.command") != null) {
				svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
			}
			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
			
		} else if("ST".equals(relayServerType)) {
			log.info("ST �삎 �씤 寃쎌슦");
			// 以묎퀎�꽌踰� 以� 留� 泥섏쓬 �떆�옉�븯�뒗 �꽌踰� 
			int rcount = 0;
			// �봽濡쒗띁�떚�뿉 �벑濡앸맂 �빆紐� 以� key 媛� �뼱�뼡寃� �엳�뒗吏� �솗�씤�븯湲� �쐞�빐 
			// �궎瑜� �솗�씤.
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(".R")) {
					rcount++;
					break;
				}
				
	        } 
			
			String midStr = "";

			// R濡� �떆�옉�븯�뒗 key媛� 議댁옱�븯硫� ST媛� �떆�옉�븯�뒗 relay�씠湲� �븣臾몄뿉 �떎�쓬 �꽌踰꾨뒗 RO遺��꽣 �떆�옉
			// R濡� �떆�옉�븯�뒗 key媛� 議댁옱�븯吏� �븡�쑝硫� FN �쑝濡� �꽕�젙. 
			if(rcount > 0) {
				midStr = "R0";
			} else {
				midStr = "FN";
				
			}
			
			// �꽌踰꾨え�뱶 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
			svrinfoMap.put("relayServerSocketPacketCount", Integer.parseInt(properties.getProperty("socket.server.packetcnt")));
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// �닔�떊 �뙆�씪 由ъ뒪�듃瑜� 諛곗뿴�뿉 �떞湲� 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
			svrinfoMap.put("relayServerSendConfigPath", envPath + properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
			
			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server �궗�슜�뿬遺� �솗�씤 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// �겢�씪�씠�뼵�듃 醫낅즺 �썑 �떎�뻾�븷 紐낅졊�뼱
			if( properties.getProperty("fin.exec.command") != null) {
				svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
			}
			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
			
		} else if(!"ST".equals(relayServerType) && !"FN".equals(relayServerType)) {
			log.info("以묎퀎�꽌踰� 以� R0 ~ R9 �뿉 �빐�떦 �븯�뒗 寃쎌슦");
			// 以묎퀎�꽌踰� 以� R0 ~ R9 �뿉 �빐�떦 �븯�뒗 寃쎌슦 
			int svrNo = Integer.parseInt(relayServerType.replace("R", ""));
			svrNo++;
			
			if(svrNo > 9 ) {
				ServerLog.getInstance().info(this.getClass().getName(),"以묎퀎�꽌踰� �슏�닔瑜� 珥덇낵�뻽�뒿�땲�떎.");
				throw new Exception("以묎퀎�꽌踰� �슏�닔瑜� 珥덇낵�뻽�뒿�땲�떎.");
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
			
			// �꽌踰꾨え�뱶 T: test , R: real
			svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
			
			svrinfoMap.put("relayServerType", relayServerType);
			svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ midStr+".ip"));
			svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ midStr+".port")));
			svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));
			svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
			svrinfoMap.put("relayServerSocketPacketCount", Integer.parseInt(properties.getProperty("socket.server.packetcnt")));
			
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
			ArrayList arrRcvFileName = new ArrayList();
			for (String key : properties.stringPropertyNames()) {
				if(key.contains(rcvFileNameStr)) {
					// �닔�떊 �뙆�씪 由ъ뒪�듃瑜� 諛곗뿴�뿉 �떞湲� 
					arrRcvFileName.add(properties.getProperty(key).toString());
					rcount++;
				}
	        }
			
			svrinfoMap.put("relayServerRecvFiles", arrRcvFileName);
			/**
			 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
			 * 
			 */
			
			svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));

			svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
			svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
			svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
			
			svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
			svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
			
			// relay server �궗�슜�뿬遺� �솗�씤 
//			svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
			svrinfoMap.put("relayServerRelayUseYn", "N");
			
			// �겢�씪�씠�뼵�듃 醫낅즺 �썑 �떎�뻾�븷 紐낅졊�뼱  
			if( properties.getProperty("fin.exec.command") != null) {
				svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
			}

			
			log.info("relayServerType : " + svrinfoMap.get("relayServerType").toString());
			log.info("relayServerIp : " + svrinfoMap.get("relayServerIp").toString());
			log.info("relayServerPort : " + svrinfoMap.get("relayServerPort").toString());
			log.info("relayServerEncodeType : " + svrinfoMap.get("relayServerEncodeType").toString());
		}
		
		return svrinfoMap;
	}
	
	/**
	 * relay �꽌踰� �젙蹂� 
	 * 
	 * @param midfix
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getRelayServerInfo(String midfix) throws Exception {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();
		
		Properties properties = utilInit();
		
		String relayServerType 	= midfix; 
		// �꽌踰꾨え�뱶 T: test , R: real
		svrinfoMap.put("relayServerMode", properties.getProperty("socket.server.mode")); 
		svrinfoMap.put("relayServerType", relayServerType);
		svrinfoMap.put("relayServerIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
		svrinfoMap.put("relayServerPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
		svrinfoMap.put("relayServerEncodeType", properties.getProperty("socket.encode"));	
		svrinfoMap.put("relayServerSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
		svrinfoMap.put("relayServerSocketPacketCount", Integer.parseInt(properties.getProperty("socket.server.packetcnt")));
		
		/**
		 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
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
		 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
		 * 
		 */
		
		svrinfoMap.put("relayServerSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
		svrinfoMap.put("relayServerSendConfigPath", envPath+ properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
		svrinfoMap.put("relayServerSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
		svrinfoMap.put("relayServerSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
		
		svrinfoMap.put("relayServerSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
		svrinfoMap.put("relayServerSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
		
		// relay server �궗�슜�뿬遺� �솗�씤 
		//svrinfoMap.put("relayServerRelayUseYn", properties.getProperty("socket.server.relayYn"));
		svrinfoMap.put("relayServerRelayUseYn", "N");
		
		// �겢�씪�씠�뼵�듃 醫낅즺 �썑 �떎�뻾�븷 紐낅졊�뼱  
		if( properties.getProperty("fin.exec.command") != null) {
			svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
		}

		
		return svrinfoMap;
	}
	
	/**
	 * socket �꽌踰� �젙蹂�
	 * @return
	 */
	public HashMap<String, Object> getSocketServerInfo() {
		
		HashMap<String, Object> svrinfoMap = new HashMap<String, Object>();

		Properties properties = utilInit();
		
		String relayServerType 	= properties.getProperty("socket.server.relayType");
		
		// �꽌踰꾨え�뱶 T: test , R: real
		svrinfoMap.put("serverMode", properties.getProperty("socket.server.mode"));  //
		
		svrinfoMap.put("serverType", relayServerType);
		svrinfoMap.put("serverIp", properties.getProperty("socket.server."+ relayServerType+".ip"));
		svrinfoMap.put("serverPort", Integer.parseInt(properties.getProperty("socket.server."+ relayServerType+".port")));
		svrinfoMap.put("serverEncodeType", properties.getProperty("socket.encode"));
		svrinfoMap.put("serverRecvPath", properties.getProperty("socket.client."+ relayServerType+".recvPath"));
		svrinfoMap.put("serverRecvCode", properties.getProperty("socket.server."+ relayServerType+".recvCode"));
		svrinfoMap.put("serverOrgCode", properties.getProperty("socket.server."+ relayServerType+".orgCode"));
		svrinfoMap.put("serverSocketTimeout", Integer.parseInt(properties.getProperty("socket.server.timeout")));
		svrinfoMap.put("serverSocketPacketCount", Integer.parseInt(properties.getProperty("socket.server.packetcnt")));
		
		//System.out.println("### serverSocketPacketCount : " + svrinfoMap.get("serverSocketPacketCount") );
		/**
		 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
		 * 
		 */
		int rcount = 0;
		String rcvFileNameStr = "socket.server."+ relayServerType +".recvFileList";
		ArrayList arrRcvFileName = new ArrayList();
		for (String key : properties.stringPropertyNames()) {
			if(key.contains(rcvFileNameStr)) {
				// �닔�떊 �뙆�씪 由ъ뒪�듃瑜� 諛곗뿴�뿉 �떞湲� 
				arrRcvFileName.add(properties.getProperty(key).toString());
				rcount++;
			}
        }
		
		svrinfoMap.put("serverRecvFiles", arrRcvFileName);
		/**
		 * properties �뿉 �젙�쓽�맂 receive file list 媛��졇�삤湲� 
		 * 
		 */
		
		svrinfoMap.put("serverSendPath", properties.getProperty("socket.client."+ relayServerType+".sendPath"));
		svrinfoMap.put("serverSendConfigPath", properties.getProperty("socket.client."+ relayServerType+".sendConfigPath"));
		svrinfoMap.put("serverSendConfigName", properties.getProperty("socket.client."+ relayServerType+".sendConfigName"));
		svrinfoMap.put("serverSendConfigDateType", properties.getProperty("socket.client."+ relayServerType+".sendConfigDateType"));
		
		svrinfoMap.put("serverSendCode", properties.getProperty("socket.server."+ relayServerType+".sendCode"));
		svrinfoMap.put("serverSendTestYn", properties.getProperty("socket.server."+ relayServerType+".sendTestYn"));
		// relay server �궗�슜�뿬遺� �솗�씤 
//		svrinfoMap.put("serverRelayUseYn", properties.getProperty("socket.server.relayYn"));
		svrinfoMap.put("serverRelayUseYn", "N");
		
		// �겢�씪�씠�뼵�듃 醫낅즺 �썑 �떎�뻾�븷 紐낅졊�뼱  
		if( properties.getProperty("fin.exec.command") != null) {
			svrinfoMap.put("execCommand", properties.getProperty("fin.exec.command"));
		}

		
		return svrinfoMap;
	}
}
