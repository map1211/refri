package kr.kis.tcprelay;

import java.net.Socket;
import java.text.SimpleDateFormat;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayWorker implements Runnable {
	private final Socket sourceSocket;
	private final Socket targetSocket;
	public String curDate = null;
	public static int threadID = 1;
	public String envPath;

	protected static LogUtil log;
	public static ServerInfoUtil util;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
	}

	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket, String envPath) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
		this.envPath = envPath;
		log = new LogUtil(this.getClass().getName(), envPath);
	}

	public String getThreadId() {
		String td = sdf.format(System.currentTimeMillis());

		if (curDate == null) {
			curDate = td;
		}

		if (!td.equals(curDate)) {
			threadID = 1;
			curDate = td;
		}

		return td + "-" + threadID++;
	}

	@Override
	public void run() {
		try {
			String threadId = getThreadId();
			
			// Inbound
			Thread inThread = new Thread(new TcpRelayIOWorker(IOWorkerType.INBOUND, sourceSocket, targetSocket, threadId, this.envPath));
			inThread.start();

			// Outbound
			Thread outThread = new Thread(new TcpRelayIOWorker(IOWorkerType.OUTBOUND, targetSocket, sourceSocket, threadId, this.envPath));
			outThread.start();

			inThread.join();
			outThread.join();
			
			log.debug("END : " + threadId);
		} catch (Exception e) {
			log.error("ERROR", e);
		} finally {
			try {
				sourceSocket.close();
			} catch (Exception e) {
			}

			try {
				targetSocket.close();
			} catch (Exception e) {
			}
		}
	}
}
