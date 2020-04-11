package kr.kis.tcprelay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TcpRelayWorker implements Runnable {
	private final Socket sourceSocket;
	private final Socket targetSocket;
	public  String curDate = "";
	public  int threadID = 1;
	
	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
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

			Thread inboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.INBOUND, sourceIs, targetOs));
			Thread outboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.OUTBOUND, targetIs, sourceOs));

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
//				System.out.println("sourceIs is not null");
				try {
					sourceIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (sourceOs != null) {
//				System.out.println("sourceOs is not null");
				try {
					sourceOs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (sourceSocket != null || sourceSocket.getKeepAlive() == false) {
//					System.out.println("sourceSocket is not null");
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
//				System.out.println("targetIs is not null");
				try {
					targetIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetOs != null) {
//				System.out.println("targetOs is not null");
				try {
					targetOs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (targetSocket != null || targetSocket.getKeepAlive() == false) {
//					System.out.println("targetSocket is not null");
					try {
						targetSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
