package kr.kis.KisServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(23594);
			while (true) {
				Socket accept = null;

				try {
					accept = serverSocket.accept();
					new Thread(new Test(accept)).start();
				} catch (Exception e) {
					e.printStackTrace();
					if (accept != null) {
						try {
							accept.close();
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
	}
}

class Test implements Runnable {

	private Socket socket;
	byte[] buffer = new byte[1024];

	public Test(Socket accept) {
		this.socket = accept;
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			outputStream = socket.getOutputStream();
			outputStream.write("Hi".getBytes());
			outputStream.flush();

			inputStream = socket.getInputStream();
			String string = null;
			if (inputStream.read(buffer) != -1) {
				string = new String(buffer);
				System.out.println("Echo " + string);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e1) {
			}

			try {
				outputStream.close();
			} catch (Exception e1) {
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}