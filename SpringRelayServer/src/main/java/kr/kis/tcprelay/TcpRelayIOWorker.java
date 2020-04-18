package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

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
		this.log = new LogUtil(this.getClass().getName() + ":" + threadName, envPath);
	}

	@Override
	public void run() {
		try {
			if (type == IOWorkerType.INBOUND) {
				log.info("INBOUND -> OUTBOUND stream copy");
			} else {
				log.info("OUTBOUND -> INBOUND stream copy");
			}

			log.info("Data byte : [" + IOUtils.copy(is, os, DEFAULT_BUFFER_SIZE) + "]");
		} catch (Exception e) {
			log.error("ERROR", e);
		}
	}
}
