package kr.kis.tcprelay;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;
import kr.kis.vo.ServerInfoVO;

public class TcpRelayService {

	private static String targetServer;
	private static int targetPort;

	private boolean running = false;

	private static int socketPort;
	private static String socketIp;
	private static String serverType;
	//	private static int socketTimeout;
	private static String encodeType;
	private static int threadNum;

	protected static LogUtil log;

	public static ServerInfoUtil util;
	public static int threadID = 1;
	String envPath = "";
	public static String curDate = "";

	private ExecutorService executorService;
	public static BlockingQueue<Socket> closeSocketQueue;

	public TcpRelayService(String envPath) {
		try {
			initTcpRelayService(envPath);

			// Init SocketServer
			getSocketServerInfo();

			// 서버 정보 요청
			getRelayServerInfo();

			// 쓰레드 풀 갯수 설정.
			executorService = Executors.newFixedThreadPool(threadNum);
			closeSocketQueue = new LinkedBlockingQueue<Socket>(threadNum);
			{
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (true) {
							try {
								Socket socket = closeSocketQueue.take();

								try {
									socket.close();
								} catch (Exception e) {
								}
							} catch (Exception e) {
							}
						}
					}
				});
			}
		} catch (Exception e) {
			try {
				log.error("ERROR", e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void getRelayServerInfo() throws Exception {
		ServerInfoVO serverInfoVO = util.getRelayServerInfo();
		targetServer = serverInfoVO.relayServerIp;
		targetPort = serverInfoVO.relayServerPort;
	}

	private void initTcpRelayService(String envPath) {
		this.envPath = envPath;
		if ("".equals(this.envPath)) {
			this.envPath = ".";
		}
		System.setProperty("LOGPATH", this.envPath);

		log = new LogUtil(this.getClass().getName(), this.envPath);
		util = new ServerInfoUtil(this.envPath);
	}

	private void getSocketServerInfo() {
		ServerInfoVO serverInfoVO = util.getSocketServerInfo();
		serverType = serverInfoVO.relayServerType;
		socketIp = serverInfoVO.relayServerIp;
		//		socketTimeout = serverInfoVO.relayServerSocketTimeout;
		encodeType = serverInfoVO.relayServerEncodeType;
		socketPort = serverInfoVO.relayServerPort;
		threadNum = serverInfoVO.relayServerThreadNum;
	}

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

		log.info("Relay Service is starting ");

		running = true;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(socketPort);
			log.info("Sever is ready.");

			while (running) {
				Socket sourceSocket = null;
				Socket targetSocket = null;
				try {
					sourceSocket = serverSocket.accept();

					targetSocket = new Socket(targetServer, targetPort);
					executorService.submit(new TcpRelayWorker(sourceSocket, targetSocket, envPath));
				} catch (Exception e) {
					log.error("ERROR", e);

					try {
						sourceSocket.close();
					} catch (Exception e1) {
						log.error(e1.getMessage());
					}

					try {
						targetSocket.close();
					} catch (Exception e1) {
						log.error(e1.getMessage());
					}
				}
			}
		} catch (Exception e) {
			log.error("ERROR", e);
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

	public void stop() {
		running = false;
	}

	public static void main(String[] args) {
		String envPath = "";
		if (args.length > 1 && StringUtils.isNotBlank(args[0])) {
			envPath = System.getenv(args[0].toString());
		}

		new TcpRelayService(envPath).start();
	}

}
