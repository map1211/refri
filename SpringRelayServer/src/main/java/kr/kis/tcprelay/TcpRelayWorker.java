package kr.kis.tcprelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayWorker implements Runnable {
	private final Socket sourceSocket;
	private final Socket targetSocket;
	public  String curDate = "";
	public  static int threadID = 1;
	public  String envPath;
	
	protected static LogUtil log;
	public static ServerInfoUtil util;		
	
	
	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
	}
	
	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket, String envPath) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
		this.envPath = envPath;
		this.log = new LogUtil(this.getClass().getName(), envPath);
	}

	public String getThreadId() {
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
	
	@Override
	public void run() {
		InputStream sourceIs = null;
		OutputStream sourceOs = null;
		InputStream targetIs = null;
		OutputStream targetOs = null;
		try {
			sourceIs = sourceSocket.getInputStream();
			sourceOs = sourceSocket.getOutputStream();
			targetIs = targetSocket.getInputStream();
			targetOs = targetSocket.getOutputStream();

			Thread inboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.INBOUND, sourceIs, targetOs, getThreadId(), envPath));
			log.info("Send service:: " );			
			log.info("Server connected Info ::" + ((InetSocketAddress)sourceSocket.getRemoteSocketAddress()).getAddress().getHostAddress()
					+ ", port:" + ((InetSocketAddress)sourceSocket.getRemoteSocketAddress()).getPort()
					);
            log.info("Client connected Info ::" + ((InetSocketAddress)targetSocket.getRemoteSocketAddress()).getAddress().getHostAddress()
                    + ", port:" + ((InetSocketAddress)targetSocket.getRemoteSocketAddress()).getPort()
                    + ", localPort:" + targetSocket.getLocalPort()
                    );
            threadID++;
            
			Thread outboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.OUTBOUND, targetIs, sourceOs, getThreadId(), envPath));
			log.info("Receive service:: " );
            log.info("Server connected Info ::::" + ((InetSocketAddress)targetSocket.getRemoteSocketAddress()).getAddress().getHostAddress()
            		+ ", port:" + targetSocket.getLocalPort()
            		);
            log.info("Client connected Info::" + ((InetSocketAddress)sourceSocket.getRemoteSocketAddress()).getAddress().getHostAddress()
            		+ ", port:" + sourceSocket.getLocalPort()
            		+ ", localPort:" + sourceSocket.getLocalPort()
            		);
            
            threadID++;
            
            
            
			inboundWorker.start();
			outboundWorker.start();
			
			inboundWorker.join();
			outboundWorker.join();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (sourceIs != null ) {
				try {
					sourceIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (sourceOs != null) {
				try {
					sourceOs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (sourceSocket != null || sourceSocket.getKeepAlive() == false) {
					try {
						sourceSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e1) {
				e1.printStackTrace();
			}

			if (targetIs != null) {
				try {
					targetIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetOs != null) {
				try {
					targetOs.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}

			try {
				if (targetSocket != null || targetSocket.getKeepAlive() == false) {
					try {
						targetSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}
}
