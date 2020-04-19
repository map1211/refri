package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;

import kr.kis.utils.LogUtil;
import kr.kis.utils.ServerInfoUtil;

public class TcpRelayIOWorker implements Runnable {
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	private static final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

	protected static LogUtil log;
	public static ServerInfoUtil util;

	private final IOWorkerType type;
	private final InputStream is;
	private final OutputStream os;
	private final String threadName;

	public TcpRelayIOWorker(IOWorkerType type, InputStream is, OutputStream os, String threadId, String envPath) {
		this.type = type;
		this.is = is;
		this.os = os;
		this.threadName = type + "-" + threadId;
		log = new LogUtil(this.getClass().getName() + ":" + threadName, envPath);
	}

	@Override
	public void run() {
		try {
			if (type == IOWorkerType.INBOUND) {
				log.info("INBOUND -> OUTBOUND stream copy");
			} else {
				log.info("OUTBOUND -> INBOUND stream copy");
			}

			int readBytes;
			int totalBytes = 0;
			while ((readBytes = is.read(buffer)) != -1) {
				System.out.println(type + ":\n" + new String(buffer));
				os.write(buffer, 0, readBytes);
				totalBytes += readBytes;
			}
			log.info("Data byte : [" + totalBytes + "]");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ERROR", e);
		}
	}
}
