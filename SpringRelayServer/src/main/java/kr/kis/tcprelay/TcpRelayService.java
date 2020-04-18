package kr.kis.tcprelay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.kis.mserver.TPooledServer;
import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayService {

	private static String targetServer ;
	private static int targetPort;

	private boolean running = false;

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
	public static String curDate = "";	
	
	
	//private final ExecutorService executorService = Executors.newFixedThreadPool(100);
	private final ExecutorService executorService;

	public TcpRelayService(String envPath) {
		
		this.envPath = envPath;
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		util = new ServerInfoUtil(envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());
	
			
		// 서버 정보 요청 
		HashMap<String, Object> hmap = new HashMap<String, Object>();
		try {
			hmap = util.getRelayServerInfo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String rip = hmap.get("relayServerIp").toString();
		int rport = Integer.parseInt(hmap.get("relayServerPort").toString());
		String rencode = hmap.get("relayServerEncodeType").toString();
		
		this.targetServer = rip;
		this.targetPort = rport;
		
		// 쓰레드 풀 갯수 설정.
		executorService = Executors.newFixedThreadPool( threadNum );
		
		
		
	}
	
//	public TcpRelayService(String targetServer, int targetPort) {
//		this.targetServer = targetServer;
//		this.targetPort = targetPort;
//		
//	}

	public void start() {
		log.info("###### LISTENING INFO ##############################");
		log.info("Server Type :: " + serverType);
		log.info("Server Ip :: " + socketIp);
		log.info("Server Listen port :: " + socketPort);
		log.info("Server encodeType :: " + encodeType);
		log.info("###### 연계 서버 연결 INFO #############################");
		log.info("relay Server Ip :: " + targetServer);
		log.info("relay Server Listen port :: " + targetPort);
		log.info("######################################################");
		
		log.info("Relay Service is starting " );
		
		running = true;
		ServerSocket serverSocket = null;
		Socket sourceSocket = null;
		Socket targetSocket = null;
		try {
			serverSocket = new ServerSocket(this.socketPort);
			log.info("Sever is ready.");
			
			while (running) {
				sourceSocket = serverSocket.accept();
				log.info("accepting.");
				targetSocket = new Socket(targetServer, targetPort);
				TcpRelayWorker worker = new TcpRelayWorker(sourceSocket, targetSocket, envPath);
				executorService.submit(worker);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sourceSocket != null) {
				try {
					sourceSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetSocket != null) {
				try {
					targetSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		running = false;
	}

	public static void main(String[] args) {
		TcpRelayService tcpRelayService = null;
		
		String envVar = "";
		String envPath = "";
		
		if(args.length > 1 ) {
			envVar = args[0].toString(); 
		}
		
		if(!"".equals(envVar)) { 
			envPath = System.getenv(args[0].toString());
		}
		
		//TcpRelayService tcpRelayService = new TcpRelayService(envPath);		
		
//		switch (args.length) {
//		case 0:
//			tcpRelayService = new TcpRelayService();
//			break;
//
//		case 2:
//			String targetServer = args[0];
//			int targetPort = Integer.parseInt(args[1]);
//			tcpRelayService = new TcpRelayService(targetServer, targetPort);
//			break;
//
//		default:
//			System.out.println("java " + TcpRelayService.class.getCanonicalName() + " <target server> <target port>");
//			System.exit(1);
//		}

		//int targetPort = Integer.parseInt(args[1]);
//		tcpRelayService = new TcpRelayService(targetServer, targetPort);
		tcpRelayService = new TcpRelayService(envPath);
		tcpRelayService.start();
	}
	
	
}
