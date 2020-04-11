package kr.kis.tcprelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.StreamUtils;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayIOWorker implements Runnable {
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	protected static LogUtil log;
	public static ServerInfoUtil util;		
	
	private final IOWorkerType type;
	private final InputStream is;
	private final OutputStream os;
	private final String threadName;

	public TcpRelayIOWorker(IOWorkerType type, InputStream is, OutputStream os) {
		this.type = type;
		this.is = is;
		this.os = os;
		this.threadName = type + "-"; 
				
	}
	
	public TcpRelayIOWorker(IOWorkerType type, InputStream is, OutputStream os, String threadId, String envPath) {
		this.type = type;
		this.is = is;
		this.os = os;
		this.threadName = type + "-" + threadId;
		this.log = new LogUtil(this.getClass().getName() + ":"+ threadName, envPath);
	}

	@Override
	public void run() {
		try {
//			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//			int readBytes;
//			while ((readBytes = is.read(buffer)) != -1) {
////				System.out.println(type + ":\n" + new String(buffer));
//				os.write(buffer, 0, readBytes);
//			}
			if("INBOUND".equals(type)) {
				log.info("INBOUND -> OUTBOUND stream copy");
			} else {
				log.info("OUTBOUND -> INBOUND stream copy");
			}
			
			StreamUtils.copy(is, os);
			
		} catch (IOException e) {
			log.info("TYPE :" + type + " message :" + e.getMessage());
			
			if("Socket closed".equals(e.getMessage())) { // inbound
				if (is != null ) {
					try {
						is.close();
						log.info("is.close");
					} catch (IOException e1) {
						e.printStackTrace();
					}
				}
			}
			
			if("Connection reset".equals(e.getMessage())) { // outbound
				if (os != null ) {
					try {
						os.close();
						log.info("os.close");
					} catch (IOException e1) {
						e.printStackTrace();
					}
				}
			}
			
			
			

		}
	}
}
