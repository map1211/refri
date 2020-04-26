package kr.kis.KisServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
	public static void main(String[] args) throws InterruptedException {
		long t = System.currentTimeMillis();

		ThreadGroup threadGroup = new ThreadGroup("test");
		int cnt1 = 10;
		int cnt2 = 10;
		final SimpleDateFormat x = new SimpleDateFormat("yyyyMMdd");
		for (int j = 0; j < cnt1; j++) {
			Thread.sleep(1000);
			for (int i = 0; i < cnt2; i++) {
				final String xx = j + " - " + i;
				Thread thread = new Thread(threadGroup, new Runnable() {
					private final byte[] buffer = new byte[1024];

					@Override
					public void run() {
						Socket socket = null;
						InputStream inputStream = null;
						OutputStream outputStream = null;
						try {
							socket = new Socket("127.0.0.1", 20000);
							
							outputStream = socket.getOutputStream();
							String format = x.format(new Date());
							System.out.println(xx + ">>> " + format);
							outputStream.write(format.getBytes());
							outputStream.flush();
							
							inputStream = socket.getInputStream();
							inputStream.read(buffer);
							System.out.println(xx + "<<< " + new String(buffer));
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								outputStream.close();
							} catch (Exception e) {
							}

							try {
								System.out.println(xx + " close");
								socket.close();
							} catch (Exception e) {
								System.out.println(e.getMessage());
							}
						}
					}
				});
				thread.start();
			}
		}

		while (threadGroup.activeCount() > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println((System.currentTimeMillis() - t) / 1000f);
	}
}
