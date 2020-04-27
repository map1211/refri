package kr.kis.KisServer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

	private static HashMap<SocketChannel, SocketChannelVO> CHANNEL_MAP = new HashMap<SocketChannel, SocketChannelVO>();
	private static BlockingQueue<SelectionKey> SELECTION_KEYS;

	public void close(String sessionId, SelectionKey key) {
		SocketChannel channel = null;

		try {
			try {
				key.cancel();
				takeSelectionKeys();

				channel = (SocketChannel) key.channel();
				CHANNEL_MAP.remove(channel);
			} catch (Exception e) {
			}

			try {
				channel.socket().close();
			} catch (Exception e) {
			}

			try {
				channel.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
			logger.error(sessionId + " ERROR - Close : " + e.getMessage(), e.getMessage());
		}
	}

	public void putSelectionKeys(SelectionKey key) throws Exception {
		if (SELECTION_KEYS == null) {
			logger.debug("Work Queue Create : " + THREAD_MAX_NUM);
			SELECTION_KEYS = new ArrayBlockingQueue<SelectionKey>(THREAD_MAX_NUM);
		}

		logger.debug("Work Register Star (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		SELECTION_KEYS.put(key);
		logger.debug("Work Register End (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
	}

	public void takeSelectionKeys() throws Exception {
		logger.debug("Work Unregister Start (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		try {
			if (SELECTION_KEYS.isEmpty()) {
				return;
			}

			SELECTION_KEYS.take();
		} catch (Exception e) {
		} finally {
			logger.debug("Work Unregister End (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		}
	}

	public void mappingServer(SocketChannel clientChannel, SocketChannel hostServerChannel) throws Exception {
		logger.debug("[" + System.identityHashCode(clientChannel) + "] MappingServer : " //
				+ clientChannel.socket().getRemoteSocketAddress() + "[" + System.identityHashCode(clientChannel) + "] - " //
				+ hostServerChannel.socket().getRemoteSocketAddress() + "[" + System.identityHashCode(hostServerChannel) + "]");

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
		String hostName = socketAddress.getHostName();
		if ("localhost".equals(hostName)) {
			hostName = "127.0.0.1";
		}
		String address = hostName + socketAddress.getPort();
		String hostAddress = hostIp + hostPort;
		return hostAddress.equals(address);
	}
}
