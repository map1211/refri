package kr.kis.KisServer;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class SocketChannelVO {
	private SelectionKey clientKey;
	private SocketChannel clientChannel;
	private SocketChannel hostServerChannel;

	public SocketChannelVO(SelectionKey clientKey, SocketChannel clientChannel, SocketChannel hostServerChannel) {
		this.clientKey = clientKey;
		this.clientChannel = clientChannel;
		this.hostServerChannel = hostServerChannel;
	}

	public SocketChannel getClientChannel() {
		return clientChannel;
	}

	public void setClientChannel(SocketChannel clientChannel) {
		this.clientChannel = clientChannel;
	}

	public SocketChannel getHostServerChannel() {
		return hostServerChannel;
	}

	public void setHostServerChannel(SocketChannel hostServerChannel) {
		this.hostServerChannel = hostServerChannel;
	}

	public SelectionKey getClientKey() {
		return clientKey;
	}

	public void setClientKey(SelectionKey clientKey) {
		this.clientKey = clientKey;
	}

}
