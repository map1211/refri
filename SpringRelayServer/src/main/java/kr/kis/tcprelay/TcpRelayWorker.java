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

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private byte[] buffer = new byte[1024];

	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
	}

	public TcpRelayWorker(Socket sourceSocket, Socket targetSocket, String envPath) {
		this.sourceSocket = sourceSocket;
		this.targetSocket = targetSocket;
		this.envPath = envPath;
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
			sourceIs = sourceSocket.getInputStream();
			sourceOs = sourceSocket.getOutputStream();
			targetIs = targetSocket.getInputStream();
			targetOs = targetSocket.getOutputStream();

			log = new LogUtil(this.getClass().getName() + ":" + getThreadId(), envPath);

			int tempLength;
			int length;
			int totalLength = 0;
			do {
				totalLength = 0;

				// IN
				{
					tempLength = 0;
					length = 0;
					while ((tempLength = sourceIs.read(buffer)) != -1) {
						targetOs.write(buffer, 0, tempLength);
						length += tempLength;

						if (targetIs.available() == 0) {
							break;
						}
					}
					targetOs.flush();
					log.info("INBOUND [" + length + "]");
					totalLength += length;
				}

				// OUT
				{
					tempLength = 0;
					length = 0;
					while ((tempLength = targetIs.read(buffer)) != -1) {
						sourceOs.write(buffer, 0, tempLength);
						length += tempLength;

						if (targetIs.available() == 0) {
							break;
						}
					}
					sourceOs.flush();
					log.info("OUTBOUND [" + length + "]");
					totalLength += length;
				}
			} while (totalLength > 0);
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
			} catch (Exception e1) {
			}

			try {
				targetSocket.close();
			} catch (Exception e1) {
			}
		}
	}
}
