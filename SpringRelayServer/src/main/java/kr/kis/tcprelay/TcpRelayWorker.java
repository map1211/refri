package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;
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

	private static final int DEFAULT_BUFFER_SIZE = 1024;
	byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

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
		InputStream sourceIs = null;
		OutputStream sourceOs = null;
		InputStream targetIs = null;
		OutputStream targetOs = null;

		try {
			// Inbound
			{
				int dataLength = 0;
				int tmpDataLength = 0;
				sourceIs = sourceSocket.getInputStream();
				targetOs = targetSocket.getOutputStream();

				while (sourceIs.available() > 0 && (tmpDataLength = sourceIs.read(buffer)) != -1) {
					dataLength += tmpDataLength;
					targetOs.write(buffer);
				}
				targetOs.flush();
				log.info("INBOUND -> OUTBOUND stream copy [" + dataLength + "]");
			}

			// Outbound
			{
				int dataLength = 0;
				int tmpDataLength = 0;
				targetIs = targetSocket.getInputStream();
				sourceOs = sourceSocket.getOutputStream();

				while (targetIs.available() > 0 && (tmpDataLength = targetIs.read(buffer)) != -1) {
					dataLength += tmpDataLength;
					sourceOs.write(buffer);
				}
				sourceOs.flush();
				log.info("OUTBOUND -> INBOUND stream copy [" + dataLength + "]");
			}
		} catch (Exception e) {
			log.error("ERROR", e);
		} finally {
			try {
				sourceIs.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			try {
				sourceOs.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			try {
				targetIs.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			try {
				targetOs.close();
			} catch (Exception e) {
				log.error(e.getMessage());
			}

			try {
				sourceSocket.close();
			} catch (Exception e1) {
				log.error(e1.getMessage());
			}

			TcpRelayService.restoreSocket(targetSocket);
		}
	}
}
