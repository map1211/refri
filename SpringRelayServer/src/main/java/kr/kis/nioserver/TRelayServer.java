/**
 * 
 */
package kr.kis.nioserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.HashMap;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

/**
 * @author jbs
 *
 */
public class TRelayServer {
	
	private static int 		socketPort ;
	private static String 	socketIp;
	private static String 	serverType;
	private static int 		socketTimeout;
	private static String 	encodeType;
	private static int 		threadNum;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;
	public static int threadID = 1;
	String envPath = "";
	
	private Abortable abortable;
	private ServerThread serverThread;
	
	public TRelayServer() {
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		this.log = new LogUtil(this.getClass().getName());
		util = new ServerInfoUtil();
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());
		
		abortable = new Abortable();

	}
	
	
	
	public TRelayServer(String envPath) {
		this.envPath = envPath;
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		util = new ServerInfoUtil(envPath);
		
		log.info("######### TPooledServer() :: envPath : " + envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());
		
		abortable = new Abortable();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String envVar = "";
		String envPath = "";
		
		if(args.length > 1 ) {
			envVar = args[0].toString(); 
//			System.out.println("######### envVar : " + envVar);
		}
		
		if(!"".equals(envVar)) { 
			envPath = System.getenv(args[0].toString());
//			System.out.println("######### main :: envPath : " + envPath);
		}
		
		TRelayServer server = new TRelayServer(envPath);
		
		server.start(envPath);
		
		Thread.sleep(500);
		
		log.info("Server Type :: " + serverType);
		log.info("Server Ip :: " + socketIp);
		log.info("Server Listen port :: " + socketPort);
		log.info("Server encodeType :: " + encodeType);
		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		reader.readLine();
		
		server.stop();
		
		System.out.println("BYE");
	}
	
	
	/**
	 * start server
	 */
	public void start(String envPath) {
		abortable.init();
		
		if(serverThread == null || serverThread.isAlive()) {
			serverThread = new ServerThread(abortable, envPath);
			serverThread.start();
		}
	}
	
	/**
	 * stop server
	 */
	public void stop() {
		abortable.done = true;
		
		if(serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
		}
	}
	
	
	
}
