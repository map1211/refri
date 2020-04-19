package kr.kis.tcprelay;

import java.io.InputStream;
import java.io.OutputStream;
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
				System.out.println(type + ":\n" + new String(buffer));
				os.write(buffer, 0, readBytes);
				totalBytes += readBytes;
			}
			os.flush();
			log.info("Data byte : [" + totalBytes + "]");
		} catch (Exception e) {
			log.error(type.toString() + " ERROR", e);

			try {
				TcpRelayService.closeSocketQueue.put(source);
				TcpRelayService.closeSocketQueue.put(target);
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
