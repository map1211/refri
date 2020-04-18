package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayWorker implements Runnable {
	private final Socket sourceSocket;
	private final Socket targetSocket;
	public String curDate = "";
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

		if ("".equals(curDate)) {
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
		InputStream sourceIs = null;
		OutputStream sourceOs = null;
		InputStream targetIs = null;
		OutputStream targetOs = null;
		
		try {
			sourceIs = sourceSocket.getInputStream();
			sourceOs = sourceSocket.getOutputStream();
			targetIs = targetSocket.getInputStream();
			targetOs = targetSocket.getOutputStream();

			String threadId = getThreadId();
			
			Thread inboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.INBOUND, sourceIs, targetOs, threadId, envPath));
			log.info("Send service:: ");
			log.info("Server connected Info ::" + ((InetSocketAddress) sourceSocket.getRemoteSocketAddress()).getAddress().getHostAddress() + ", port:"
					+ ((InetSocketAddress) sourceSocket.getRemoteSocketAddress()).getPort());
			log.info("Client connected Info ::" + ((InetSocketAddress) targetSocket.getRemoteSocketAddress()).getAddress().getHostAddress() + ", port:"
					+ ((InetSocketAddress) targetSocket.getRemoteSocketAddress()).getPort() + ", localPort:" + targetSocket.getLocalPort());
			
			Thread outboundWorker = new Thread(new TcpRelayIOWorker(IOWorkerType.OUTBOUND, targetIs, sourceOs, threadId, envPath));
			log.info("Receive service:: ");
			log.info("Server connected Info ::::" + ((InetSocketAddress) targetSocket.getRemoteSocketAddress()).getAddress().getHostAddress() + ", port:"
					+ targetSocket.getLocalPort());
			log.info("[server] connected! :: connected socket address(client ip)::" + ((InetSocketAddress) sourceSocket.getRemoteSocketAddress()).getAddress().getHostAddress()
					+ ", port:" + sourceSocket.getLocalPort() + ", localPort:" + sourceSocket.getLocalPort());

			inboundWorker.start();
			outboundWorker.start();

			inboundWorker.join();
			outboundWorker.join();
		} catch (Exception e) {
			log.error("ERROR", e);
		} finally {
			try {
				sourceIs.close();
			} catch (Exception e) {
			}

			try {
				sourceOs.close();
			} catch (Exception e) {
			}

			try {
				targetIs.close();
			} catch (Exception e) {
			}

			try {
				targetOs.close();
			} catch (Exception e) {
			}

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
