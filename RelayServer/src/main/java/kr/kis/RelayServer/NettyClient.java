package kr.kis.RelayServer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

public class NettyClient {
	/**
	* The Logger.
	*/
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Tcp ip.
	 */
//	@Value("${socket.host.ip}")
	private String tcpIp;

	/**
	 * The Tcp port.
	 */
//	@Value("${socket.host.port}")
	private int tcpPort;

	private static Selector selector;

	private static HashMap<SocketChannel, Channel> channelMap = new HashMap<SocketChannel, Channel>();

	public NettyClient() throws Exception {
		selector = Selector.open();
	}

	/**
	 * Start.
	 * @param channel 
	 * @return 
	 */
	public SocketChannel openClientChannel(Channel channel) {
		SocketChannel socketChannel = null;

		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.connect(new InetSocketAddress(tcpIp, tcpPort));
			socketChannel.register(selector, SelectionKey.OP_READ);

			channelMap.put(socketChannel, channel);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}

		return socketChannel;
	}
}