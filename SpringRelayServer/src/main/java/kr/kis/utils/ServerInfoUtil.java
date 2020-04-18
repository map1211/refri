package kr.kis.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import kr.kis.vo.ServerInfoVO;

public class ServerInfoUtil {
	private Properties properties;
	private String envPath = "";

	public ServerInfoUtil(String envPath) {
		this.envPath = envPath;
	}

	private Properties utilInit() {
		properties = new Properties();

		if ("".equals(envPath))
			envPath = ".";

		FileReader reader = null;
		
		try {
			reader = new FileReader(envPath + "/resources/application.properties");
			properties.load(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			ServerLog.getInstance().error(this.getClass().getName(), e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}

		return properties;
	}

	public ServerInfoVO getRelayServerInfo() throws Exception {
		ServerInfoVO serverInfoVO = new ServerInfoVO();

		Properties properties = utilInit();
		String relayServerType = properties.getProperty("socket.server.relayType");
		if ("SA".equals(relayServerType)) {
			// stand alone 형 인 경우
			serverInfoVO.relayServerType = relayServerType;
			serverInfoVO.relayServerIp = properties.getProperty("socket.server." + relayServerType + ".ip");
			serverInfoVO.relayServerPort = Integer.parseInt(properties.getProperty("socket.server." + relayServerType + ".port"));
			serverInfoVO.relayServerEncodeType = properties.getProperty("socket.server.encoding");
			serverInfoVO.relayServerSocketTimeout = Integer.parseInt(properties.getProperty("socket.server.timeout"));
			serverInfoVO.relayServerThreadNum = Integer.parseInt(properties.getProperty("thread.maxNum"));

		} else if ("ST".equals(relayServerType) || "FN".equals(relayServerType)) {
			// 중계서버 중 맨 마지막 서버 
			serverInfoVO.relayServerType = relayServerType;
			serverInfoVO.relayServerIp = properties.getProperty("socket.host.ip");
			serverInfoVO.relayServerPort = Integer.parseInt(properties.getProperty("socket.host.port"));
			serverInfoVO.relayServerEncodeType = properties.getProperty("socket.server.encoding");
			serverInfoVO.relayServerSocketTimeout = Integer.parseInt(properties.getProperty("socket.server.timeout"));
			serverInfoVO.relayServerThreadNum = Integer.parseInt(properties.getProperty("thread.maxNum"));
		}

		return serverInfoVO;
	}

	public ServerInfoVO getSocketServerInfo() {
		ServerInfoVO serverInfoVO = new ServerInfoVO();

		Properties properties = utilInit();
		String relayServerType = properties.getProperty("socket.server.relayType");
		serverInfoVO.relayServerType = relayServerType;
		serverInfoVO.relayServerIp = properties.getProperty("socket.server." + relayServerType + ".ip");
		serverInfoVO.relayServerPort = Integer.parseInt(properties.getProperty("socket.server." + relayServerType + ".port"));
		serverInfoVO.relayServerEncodeType = properties.getProperty("socket.server.encoding");
		serverInfoVO.relayServerSocketTimeout = Integer.parseInt(properties.getProperty("socket.server.timeout"));
		serverInfoVO.relayServerThreadNum = Integer.parseInt(properties.getProperty("thread.maxNum"));

		return serverInfoVO;
	}

}
