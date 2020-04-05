package kr.kis.mserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TPooledServer {

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
	
	public TPooledServer() {
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
	}
	
	
	public TPooledServer(String envPath) {
		
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
	}
	
	public static void main(String[] args ) {
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
		
		TPooledServer ts = new TPooledServer(envPath);
		

		
		log.info("Thread Pool 생성  :: pool 수 : " + threadNum);
		// application.properties 파일에 정의된 thread 수 만큼 풀 생성. 
		ExecutorService pool = Executors.newFixedThreadPool(threadNum);
		
		ServerSocket tsServer = null; // 서버 자신의 서버소켓 
		
		OutputStream requestToServer = null;  // 클라이언트 -> 서버 : 클라이언트가 서버에요청
		OutputStream requestToRelay = null;  // 서버에서 생성된 클라이언트 -> 릴레이 서버 : 클라이언트가 서버에요청
		InputStream responseFromServer = null; // 서버 -> 클라이언트 : 서버가 클라이언트에 응답
		InputStream responseFromRelay = null; // 릴레이 서버 -> 릴레이가 생성한 클라이언트 : 서버가 클라이언트에 응답
		
		// 서버 정보 요청 
		HashMap<String, Object> hmap = new HashMap<String, Object>();
		try {
			hmap = util.getRelayServerInfo();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String rip = hmap.get("relayServerIp").toString();
		int rport = Integer.parseInt(hmap.get("relayServerPort").toString());
		String rencode = hmap.get("relayServerEncodeType").toString();
		
		try {
			log.info("Server Type :: " + serverType);
			log.info("Server Ip :: " + socketIp);
			log.info("Server Listen port :: " + socketPort);
			log.info("Server encodeType :: " + encodeType);
			log.info("ServerSocket 생성 :: " );
			tsServer = new ServerSocket(socketPort);
			
			log.info("relay Server Ip :: " + rip);
			log.info("relay Server Listen port :: " + rport);
			log.info("relay Server encodeType :: " + rencode);
			Socket cn = null;
			Socket rs = null;
			while(true) {
				try {
					log.info("Client accept 대기 ");
					cn = tsServer.accept();
					

		            // 연결 요청이 오면 연결이 되었다는 메시지 출력, client ip 출력 
		            InetSocketAddress remoteSocketAddress =(InetSocketAddress)cn.getRemoteSocketAddress();
		            String remoteHostName = remoteSocketAddress.getAddress().getHostAddress();
		            int remoteHostPort = remoteSocketAddress.getPort();
		            log.info("[server] connected! :: connected socket address(client ip)::" + remoteHostName
		                    + ", port:" + remoteHostPort);	
		            
					log.info("relay ServerSocket create:: " );
					rs = new Socket(rip, rport);
					
					// 다음 릴레이 서버로 쓸 스트림 생성 
					responseFromServer 	= cn.getInputStream();
					requestToRelay 		= rs.getOutputStream(); //new PrintWriter(rs.getOutputStream(), true);
					responseFromRelay 	= rs.getInputStream();
					requestToServer 	= cn.getOutputStream(); //new PrintWriter(cn.getOutputStream(), true);
					
					// thread pool 에 넣고 관리.
					String sendtaskName = "InOutTask-Pool-" + getThreadId();
					Callable<Void> sendtask = new RClientInOutTask(cn, responseFromServer, rs, requestToRelay, sendtaskName); // 서버 -> 릴레이
					log.info("Client sendtask :" +  sendtaskName);

					String recvtaskName = "OutInTask-Pool-" + getThreadId();
					Callable<Void> recvtask = new RClientOutInTask(rs, responseFromRelay, cn, requestToServer, recvtaskName); // 릴레이 -> 서버
					log.info("Client recvtask :" + recvtaskName );
					
					threadID++;
					
					pool.submit(sendtask);
					pool.submit(recvtask);
					
			        
					
				} catch (Exception e) {
					log.error("server error", e);
					try {
						if(!rs.isClosed()) {
							rs.close();
						}
					} catch (Exception e2) {
					}     
				}
			}
		} catch (Exception e) {
			log.error("Server listening error : ", e);
		}
		
	}
	

	public static String getThreadId() {
		String tid = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		Calendar c = Calendar.getInstance();

		String td = sdf.format(c.getTime());
		
		if("".equals(curDate)) {
			curDate = td;
		} 
		
		if(!td.equals(curDate)) {
			threadID = 1;
		}
		
		tid = td + "-" + threadID;
		
		return tid;
	}

}
