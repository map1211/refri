/**
 * 
 */
package kr.kis.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

/**
 * @author jbs
 *
 */
public class ServerThread extends Thread {

	private Abortable abortable;
	private String envPath;
	private List<Thread> clientList = new ArrayList<Thread>();

	
	private static int 		socketPort ;
	private static String 	socketIp;
	private static String 	serverType;
	private static int 		socketTimeout;
	private static String 	encodeType;
	private static int 		threadNum;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;
	public static int threadID = 1;
	
	
	
	public ServerThread(Abortable abortable) {
		this.abortable = abortable;
		
	}
	
	public ServerThread(Abortable abortable, String envPath) {
		this.abortable = abortable;
		this.envPath = envPath;
		
		if("".equals(envPath)) {
			envPath = ".";
		}
		System.setProperty("LOGPATH", envPath);
		
		this.log = new LogUtil(this.getClass().getName(), envPath);
		util = new ServerInfoUtil(envPath);
		
		log.info("######### ServerThread() :: envPath : " + envPath);
		
		HashMap<String, Object> map = util.getSocketServerInfo();
		this.serverType = map.get("relayServerType").toString();
		this.socketIp 	= map.get("relayServerIp").toString();
		this.socketTimeout = Integer.parseInt(map.get("relayServerSocketTimeout").toString());
		this.encodeType = map.get("relayServerEncodeType").toString();
		
		this.socketPort = Integer.parseInt(map.get("relayServerPort").toString());
		this.threadNum = Integer.parseInt(map.get("relayServerThreadNum").toString());		
		
	}
	
	@Override
	public void run() {
		super.run();
		
		ServerSocketChannel server = null;
		
		Selector selector = null;
		
		try {
			System.out.println("Server :: started");
			server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress("", socketPort));
			server.configureBlocking(false);
			
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			
			log.info("Server :: waiting for accept");
			
			while (!Thread.interrupted() && !abortable.isDone()) {
				selector.select(500);
				
				Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
				
				while(iter.hasNext()) {
					SelectionKey key = iter.next();
					if(key.isAcceptable()) {
						SocketChannel client = server.accept();
						
						if(client != null) {
							log.info("Server :: accepted - client [" + client +"]" );
							Thread t = new ClientHandlerThread(abortable, client, envPath);
							
							t.start();
							clientList.add(t);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for(Thread t: clientList) {
				if(t != null && t.isAlive()) {
					t.interrupt();
				}
				
				try {
					t.join(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			
			if(server != null) {
				try {
					server.close();
				} catch (IOException e3) {
					e3.printStackTrace();
				}
			}
			
			log.info("Server :: done");
		}
		
	}
	
	
}


