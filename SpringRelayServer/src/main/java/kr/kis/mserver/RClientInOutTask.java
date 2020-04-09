package kr.kis.mserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StreamUtils;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;
import kr.kis.utils.SharedVars;

public class RClientInOutTask implements Callable<Void>{ 

	private Socket inSocket;
	private InputStream in;
	private Socket outSocket;
	private OutputStream out;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;	
	
	public static String taskClose ="N";
	
	public String taskName;
	
	
	public RClientInOutTask(Socket _inSocket, InputStream _in, Socket _outSocket, OutputStream _out, String taskName) {
		this.in = _in;
		this.out = _out;
		this.inSocket = _inSocket;
		this.outSocket = _outSocket;
		
		long threadId = Thread.currentThread().getId();
		
		this.taskName = taskName;
		
		this.log = new LogUtil(this.getClass().getName() + ":"+ taskName);
		
	}
	
	public Void call(){

		
		log.info(" RclientInOutTask :: input -> output 전송 start ...");
		
		try {
			log.info("Server -> relay client 복사");
			
//			while(true) {
				// 연결정보 획득
				boolean isServerConnected = inSocket.isConnected() && !inSocket.isClosed();
				log.info("inSocket.isConnected():" + inSocket.isConnected());
				log.info("inSocket.isClosed():" + inSocket.isClosed());
				log.info("outSocket.isConnected():" + outSocket.isConnected());
				log.info("outSocket.isClosed():" + outSocket.isClosed());
				if(isServerConnected) {
//					log.info("Server connection is opened");
					log.info("Server -> relay client 복사");
					try {
						StreamUtils.copy(in, out);
						
					}catch (Exception e) {
						if("Socket closed".equals(e.getMessage())) {
							log.info("Socket closed ");
							
							outSocket.close(); // client 연결 닫음.
							
						} else {
							log.error("error ::"+e.getMessage(), e);
						}			
					}
				} else {
//					log.info("Server connection is closed");
					// 클라이언트에서 close() 처리 
					try {
//						if(inSocket.isClosed()) {
							log.info("Server connection is closed");
//							outSocket.close(); // outsocket만 close() :: close_wait : 52개 
							inSocket.close(); // insocket만 close() :: close_wait :  
//							if(!outSocket.isClosed()) {
//								outSocket.close();
//							}
//						}
					} catch (Exception e) {
//						outSocket.close();
						inSocket.close();
//						log.info("Server connection is closed");

					}					
				}
				
//			}
		} catch (IOException e) {
		} finally {
			try {
//				if(inSocket.isClosed()) {
//					outSocket.close();
//					log.info("Server connection is closed");
//				}
//				if(!outSocket.isClosed()) {
//					outSocket.close();
//					log.info("Client connection is closed");
//				}
//				outSocket.close();
				inSocket.close();
			} catch (Exception e) {
			}
		}
		
		return null;

	}
	

}
