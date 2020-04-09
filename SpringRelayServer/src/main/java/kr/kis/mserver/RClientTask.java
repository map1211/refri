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

public class RClientTask implements Callable<Void>{ 

	private Socket inSocket;
	private InputStream in;
	private Socket outSocket;
	private OutputStream out;
	
	private InputStream in2;
	private OutputStream out2;
	
	protected static LogUtil log;

	public static ServerInfoUtil util;	
	
	public static String taskClose ="N";
	
	public String taskName;
	
	
	public RClientTask(Socket _inSocket, InputStream _in, Socket _outSocket, OutputStream _out, 
			InputStream _in2, OutputStream _out2, String taskName) {
		this.in = _in;
		this.out = _out;
		this.in2 = _in2;
		this.out2 = _out2;
		
		this.inSocket = _inSocket;
		this.outSocket = _outSocket;
		
		long threadId = Thread.currentThread().getId();
		
		this.taskName = taskName;
		
		this.log = new LogUtil(this.getClass().getName() + ":"+ taskName);
		
	}
	
//	public void sendTask() {
//		try {
//			StreamUtils.copy(in, out);
//		} catch (Exception e) {
//			try {
//				inSocket.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
//	
//	
//	public void recvTask() {
//		try {
//			StreamUtils.copy(in2, out2);
//		} catch (Exception e) {
//			try {
//				outSocket.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//		
//	}
	public Void call(){


//		log.info(" RclientTask :: input -> output 전송 start ...");
		
//		final SharedVars sv = new SharedVars();
//		String t = "InOutTask-Pool-";
//		final String key = t + taskName.substring(t.length());
		
		try {
			MsgSend send = new MsgSend();
			MsgRecv recv = new MsgRecv();
//			log.info("Server -> relay client 복사");
			
//			while(true) {
				// 연결정보 획득
//				boolean isServerConnected = outSocket.isConnected() && !outSocket.isClosed();
//				boolean isServerConnected = inSocket.isConnected() && !inSocket.isClosed();
//				log.info("inSocket.isConnected():" + inSocket.isConnected());
//				log.info("inSocket.isClosed():" + inSocket.isClosed());
//				log.info("outSocket.isConnected():" + outSocket.isConnected());
//				log.info("outSocket.isClosed():" + outSocket.isClosed());
//					log.info("Server connection is opened");
//					log.info("Server -> relay client 복사");
//				try {
//					int ii = 0;
//					if((ii=in.read()) != -1){
//						sendTask();
//		            }
//			        
//				}catch (Exception e) {
//					if("Socket closed".equals(e.getMessage())) {
//						log.info("Socket closed ");
//						inSocket.close(); // client 연결 닫음.
//					} else {
//						log.error("error ::"+e.getMessage(), e);
//					}			
//				}
//				
//				try {
//					int jj = 0;
//					if((jj=in.read()) != -1){
//						recvTask();
//		            }
//					
//			        
//				}catch (Exception e) {
//					if("Socket closed".equals(e.getMessage())) {
//						log.info("Socket closed ");
//						outSocket.close(); // client 연결 닫음.
//					} else {
//						log.error("error ::"+e.getMessage(), e);
//					}			
//				}				
//			}
		} catch (Exception e) {
		} finally {
//			try {
//				inSocket.close();
//				outSocket.close();
//			} catch (Exception e) {
//			}
		}
		
		return null;

	}
	

	
	class MsgSend extends Thread {
		public void run(){
			startMsgSend();
		}
		
		public void startMsgSend(){
			try {
				StreamUtils.copy(in, out);
			} catch (Exception e) {
				try {
					outSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}	
	
	
	class MsgRecv extends Thread {
		public void run(){
			startMsgRecv();
		}
		
		public void startMsgRecv(){
			try {
				StreamUtils.copy(in2, out2);
			} catch (Exception e) {
				try {
					inSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}

