package kr.kis.KisServer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("socketChannelService")
public class SocketChannelService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${thread.maxNum}")
	private int THREAD_MAX_NUM;

	@Value("${socket.host.ip}")
	private String hostIp;

	@Value("${socket.host.port}")
	private int hostPort;

	public static HashMap<SocketChannel, SocketChannelVO> CHANNEL_MAP = new HashMap<SocketChannel, SocketChannelVO>();

	public void close(SelectionKey key) throws Exception {
		SocketChannel channel = null;

		try {
			key.cancel();
			channel = (SocketChannel) key.channel();
			CHANNEL_MAP.remove(channel);

			if (channel == null) {
				return;
			}

			try {
				if (channel.socket() != null && channel.socket().isClosed()) {
					channel.socket().close();
				}
			} catch (Exception e) {
				logger.error("ERROR - " + e.getMessage());
			}

			try {
				channel.close();
			} catch (Exception e) {
				logger.error("ERROR - " + e.getMessage());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void mappingServer(SocketChannel clientChannel, SocketChannel hostServerChannel) throws Exception {
		logger.debug("[" + System.identityHashCode(clientChannel) + "] MappingServer : " //
				+ ((InetSocketAddress) clientChannel.socket().getRemoteSocketAddress()).getAddress().getHostAddress() + "[" + System.identityHashCode(clientChannel) + "] - " //
				+ ((InetSocketAddress) hostServerChannel.socket().getRemoteSocketAddress()).getAddress().getHostAddress() + "[" + System.identityHashCode(hostServerChannel) + "]");

		SocketChannelVO socketChannelVO = new SocketChannelVO(clientChannel, hostServerChannel);
		CHANNEL_MAP.put(hostServerChannel, socketChannelVO);
		CHANNEL_MAP.put(clientChannel, socketChannelVO);
	}

	public SocketChannelVO getSocketChannelVO(SocketChannel socketChannel) {
		return CHANNEL_MAP.get(socketChannel);
	}

	public SocketChannel getHostServerChannel(SocketChannel socketChannel) throws Exception {
		SocketChannelVO socketChannelVO = CHANNEL_MAP.get(socketChannel);
		if (socketChannelVO == null) {
			throw new Exception("Not found Host Server Channel");
		}

		return socketChannelVO.getHostServerChannel();
	}

	public SocketChannel getClientChannel(SocketChannel socketChannel) throws Exception {
		SocketChannelVO socketChannelVO = CHANNEL_MAP.get(socketChannel);
		if (socketChannelVO == null) {
			throw new Exception("Not found Client Channel");
		}

		return socketChannelVO.getClientChannel();
	}

	public boolean isHostServerChannel(SocketChannel channel) {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
		String hostName = socketAddress.getAddress().getHostAddress();
		String address = hostName + socketAddress.getPort();
		String hostAddress = hostIp + hostPort;
		return hostAddress.equals(address);
	}
}
