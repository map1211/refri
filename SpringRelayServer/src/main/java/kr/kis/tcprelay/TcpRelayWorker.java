package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

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

	private static final int DEFAULT_BUFFER_SIZE = 1024;

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

			boolean isInbound = sourceIs.available() > 0;
			boolean isOutbound = targetIs.available() > 0;
			if (isInbound) {
				log.info("INBOUND -> OUTBOUND stream copy [" + IOUtils.copy(sourceIs, targetOs, DEFAULT_BUFFER_SIZE) + "]");
			} else if (isOutbound) {
				log.info("OUTBOUND -> INBOUND stream copy [" + IOUtils.copy(targetIs, sourceOs, DEFAULT_BUFFER_SIZE) + "]");
			}
			
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

			TcpRelayService.restoreSocket(targetSocket);
		}
	}
}
