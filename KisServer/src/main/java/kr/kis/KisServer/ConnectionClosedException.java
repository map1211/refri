package kr.kis.KisServer;

import java.net.SocketAddress;

public class ConnectionClosedException extends Exception {
	private static final long serialVersionUID = 8488411678489580218L;

	public ConnectionClosedException(SocketAddress socketAddress) {
		super("Connection closed by client: " + socketAddress);
	}
}
