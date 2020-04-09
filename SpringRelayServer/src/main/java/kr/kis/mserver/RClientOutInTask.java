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

public class RClientOutInTask implements Callable<Void>{ 

	private Socket inSocket;
	private InputStream in;
	private Socket outSocket;
	private OutputStream out;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;
	
	public static String taskClose ="N";
	
	public String taskName;
	
	public RClientOutInTask(Socket _inSocket, InputStream _in, Socket _outSocket, OutputStream _out, String taskName) {
		
		this.in = _in;
		this.out = _out;
		this.inSocket = _inSocket;
		this.outSocket = _outSocket;
		
		long threadId = Thread.currentThread().getId();
		
		this.taskName = taskName;
		
		this.log = new LogUtil(this.getClass().getName() + ":"+ taskName);
	}
	
	public Void call(){
		log.info(" RclientOutInTask :: output -> input 전송 start ...");
//		String t = "InOutTask-Pool-";
//		final String key = t + taskName.substring(t.length());
//		log.info(" RclientOutInTask :: key :: " + key);
//
//		final SharedVars sv = new SharedVars();
		

		try {
//			while(true) {
				// 연결정보 획득
				boolean isServerConnected = outSocket.isConnected() && !outSocket.isClosed();
//				log.info("outSocket.isConnected():" + outSocket.isConnected());
//				log.info("outSocket.isClosed():" + outSocket.isClosed());
//				log.info("inSocket.isConnected():" + inSocket.isConnected());
//				log.info("inSocket.isClosed():" + inSocket.isClosed());
				if(isServerConnected) {
//					log.info("Server connection is opened");
					log.info("relay client -> Server 복사");
					try {
						StreamUtils.copy(in, out);
						
					}catch (Exception e) {
						
						outSocket.close();
						
					}
					
				} else {
					// client 에서 close 처리
//					inSocket.close(); // insocket만 close() :: close_wait : 52개
					outSocket.close(); // outsocket만 close() :: close_wait :
					// relay 서버의 소켓이 안닫히는 현상 파악위해. 
					// 33590 포트 사용의 close_wait 없애기 위함 목적 테스트
					// port 33590관련 socket close 테스트 용
//					try {
//						if(outSocket.isClosed()) {
//							inSocket.close();
//							log.info("Server connection is closed");
//						}
//					} catch (Exception e) {
//					}
				}
				
//			}
		} catch (IOException e) {
//			e.printStackTrace();
//			if("Socket closed".equals(e.getMessage())) {
//				log.info("Socket closed ");
//			} else {
//				log.error("error ::"+e.getMessage(), e);
//			}
		} finally {
//			try {
//				if(outSocket.isClosed()) {
//					inSocket.close();
//					log.info("Client connection is closed");
////					inSocket.close();
//				}
//				if(!inSocket.isClosed()) {
//					inSocket.close();
//					log.info("Client connection is closed");
////					inSocket.close();
//				}
//			} catch (Exception e) {
//			}
			
//			try {
//				outSocket.close(); // outsocket만 close() :: close_wait :
				// port 33590관련 socket close 테스트 용
//				try {
//					if(outSocket.isClosed()) {
//						inSocket.close();
//					}
//				} catch (Exception e) {
//				}				
//			} catch (Exception e) {
//			}			
		}
		
		return null;

	}
	
	


}
