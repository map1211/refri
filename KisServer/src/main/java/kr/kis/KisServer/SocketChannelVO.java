package kr.kis.KisServer;

import java.nio.channels.SocketChannel;

public class SocketChannelVO {
	private SocketChannel clientChannel;
	private SocketChannel hostServerChannel;

	public SocketChannelVO(SocketChannel clientChannel, SocketChannel hostServerChannel) {
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

}
