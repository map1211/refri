package kr.kis.mserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.springframework.util.StreamUtils;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class RClientOutInTask implements Callable<Void>{ 

	private Socket inSocket;
	private InputStream in;
	private Socket outSocket;
	private OutputStream out;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;	
	
	public RClientOutInTask(Socket _inSocket, InputStream _in, Socket _outSocket, OutputStream _out, String taskName) {
		
		this.in = _in;
		this.out = _out;
		this.inSocket = _inSocket;
		this.outSocket = _outSocket;
		
		long threadId = Thread.currentThread().getId();
		this.log = new LogUtil(this.getClass().getName() + ":"+ taskName);
	}
	
	public Void call(){

		log.info(" RclientOutInTask :: output -> input 전송 start ...");
		try {
//			while(true) {
				// 연결정보 획득
				boolean isServerConnected = outSocket.isConnected() && !outSocket.isClosed();
				log.info("outSocket.isConnected():" + outSocket.isConnected());
				log.info("outSocket.isClosed():" + outSocket.isClosed());
				log.info("inSocket.isConnected():" + inSocket.isConnected());
				log.info("inSocket.isClosed():" + inSocket.isClosed());
				if(isServerConnected) {
					log.info("Server connection is opened");
					log.info("relay client -> Server 복사");
					StreamUtils.copy(in, out);
				} else {
					// client 에서 close 처리 
//					try {
//						if(outSocket.isClosed()) {
//							inSocket.close();
//							log.info("Server connection is closed");
////							inSocket.close();
//						}
//					} catch (Exception e) {
//					}
//					break;
				}
				
//			}
		} catch (IOException e) {
//			e.printStackTrace();
			if("Socket closed".equals(e.getMessage())) {
				log.info("Socket closed ");
			} else {
				log.error("error ::"+e.getMessage(), e);
			}
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
			
		}
		
		return null;

	}

}
