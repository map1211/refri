package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import kr.kis.utils.LogUtil;

public class TcpRelayIOWorker implements Runnable {
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private LogUtil log;

	private final IOWorkerType type;
	private Socket source;
	private Socket target;
	private final String threadName;

	public TcpRelayIOWorker(IOWorkerType type, Socket source, Socket target, String threadId, String envPath) {
		this.type = type;
		this.source = source;
		this.target = target;
		this.threadName = type + "-" + threadId;
		log = new LogUtil(this.getClass().getName() + ":" + threadName, envPath);
	}

	@Override
	public void run() {
		InputStream is = null;
		OutputStream os = null;

		try {
			if (type == IOWorkerType.INBOUND) {
				log.info("INBOUND -> OUTBOUND stream copy");
			} else {
				log.info("OUTBOUND -> INBOUND stream copy");
			}

			is = source.getInputStream();
			os = target.getOutputStream();

			int readBytes;
			int totalBytes = 0;
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			while ((readBytes = is.read(buffer)) != -1) {
				os.write(buffer, 0, readBytes);
				totalBytes += readBytes;
				log.info("수신 byte : [" + totalBytes + "]   송신 byte : [" + totalBytes + "]");
			}
			log.info("수신 byte : [" + totalBytes + "]   송신 byte : [" + totalBytes + "]");
			os.flush();
		} catch (Exception e) {
			if(e.getMessage().contains("Socket closed")) {
				log.error(type.toString() + " : Socket closed");
			} else if(e.getMessage().contains("Connection reset")) {
				log.error(type.toString() + " : Connection reset");
			} else {
				log.error(type.toString() + " : Exception : ", e);
			}

			try {
				log.debug(threadName + " - Socket Close Reg");
				TcpRelayService.closeSocketQueue.put(source);
				log.debug(threadName + " >> Socket Close Reg : " + ((InetSocketAddress) source.getRemoteSocketAddress()).getAddress().getHostAddress());
				TcpRelayService.closeSocketQueue.put(target);
				log.debug(threadName + " >> Socket Close Reg : " + ((InetSocketAddress) target.getRemoteSocketAddress()).getAddress().getHostAddress());
			} catch (Exception e1) {
			}
			
			try {
				is.close();
			} catch (Exception e1) {
			}
			
			try {
				os.close();
			} catch (Exception e1) {
			}

			try {
				source.close();
			} catch (Exception e1) {
			}

			try {
				target.close();
			} catch (Exception e1) {
			}
		}
	}
}
