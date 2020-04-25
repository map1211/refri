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

	//	private static ConcurrentHashMap<SocketChannel, SocketChannelVO> IN_BOUND_CHANNEL_KEY_MAP = new ConcurrentHashMap<SocketChannel, SocketChannelVO>();
	//	private static ConcurrentHashMap<SocketChannel, SocketChannelVO> OUT_BOUND_CHANNEL_KEY_MAP = new ConcurrentHashMap<SocketChannel, SocketChannelVO>();
	private static HashMap<SocketChannel, SocketChannelVO> CLIENT_CHANNEL_MAP = new HashMap<SocketChannel, SocketChannelVO>();
	private static HashMap<SocketChannel, SocketChannelVO> HOST_SERVER_CHANNEL_MAP = new HashMap<SocketChannel, SocketChannelVO>();

	private static BlockingQueue<SelectionKey> SELECTION_KEYS;

	public SocketChannelService() {
		logger.debug("THREAD_MAX_NUM : " + THREAD_MAX_NUM);
	}

	public void closeSelectionKey(SelectionKey key) {
		SocketChannel channel = null;

		try {
			channel = (SocketChannel) key.channel();

			if (isHostServerChannel(channel)) {
				logger.debug("### Close Host Server Channel : " + channel.socket().getRemoteSocketAddress());
			} else {
				logger.debug("### Close Client Channel : " + channel.socket().getRemoteSocketAddress());
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
		} finally {
			try {
				takeSelectionKeys();
			} catch (Exception e) {
			}

			key.cancel();

			if (channel != null) {
				if (isHostServerChannel(channel) == false) {
					try {
						closeSelectionKey(getSocketChannelVO(channel).getHostServerKey());
					} catch (Exception e) {
					}
				}
				CLIENT_CHANNEL_MAP.remove(channel);
				HOST_SERVER_CHANNEL_MAP.remove(channel);
			}
		}
	}

	public void putSelectionKeys(SelectionKey key) throws Exception {
		if (SELECTION_KEYS == null) {
			logger.debug("### Work Queue Create : " + THREAD_MAX_NUM);
			SELECTION_KEYS = new ArrayBlockingQueue<SelectionKey>(THREAD_MAX_NUM);
		}

		logger.debug("### Work Register Star (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		SELECTION_KEYS.put(key);
		logger.debug("### Work Register End (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
	}

	public void takeSelectionKeys() throws Exception {
		logger.debug("### Work Unregister Start (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		try {
			if (SELECTION_KEYS.isEmpty()) {
				return;
			}

			SELECTION_KEYS.take();
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			logger.debug("### Work Unregister End (" + SELECTION_KEYS.size() + " / " + THREAD_MAX_NUM + ")");
		}
	}

	public void mappingServer(SelectionKey clientServerKey, SocketChannel clientChannel, SocketChannel hostServerChannel) throws Exception {
		logger.debug("### MappingServer : " + clientChannel.socket().getRemoteSocketAddress() + " - " + hostServerChannel.socket().getRemoteSocketAddress());

		SocketChannelVO socketChannelVO = new SocketChannelVO(clientServerKey, clientChannel, hostServerChannel);
		CLIENT_CHANNEL_MAP.put(hostServerChannel, socketChannelVO);
		HOST_SERVER_CHANNEL_MAP.put(clientChannel, socketChannelVO);
	}

	public SocketChannelVO getSocketChannelVO(SocketChannel socketChannel) {
		if (CLIENT_CHANNEL_MAP.containsKey(socketChannel)) {
			return CLIENT_CHANNEL_MAP.get(socketChannel);
		} else {
			return HOST_SERVER_CHANNEL_MAP.get(socketChannel);
		}
	}

	public SocketChannel getHostServerChannel(SocketChannel socketChannel) throws Exception {
		SocketChannel hostServerChannel = HOST_SERVER_CHANNEL_MAP.get(socketChannel).getHostServerChannel();
		return hostServerChannel;
	}

	public SocketChannel getClientChannel(SocketChannel socketChannel) throws Exception {
		SocketChannel clientChannel = CLIENT_CHANNEL_MAP.get(socketChannel).getClientChannel();
		return clientChannel;
	}

	public boolean isHostServerChannel(SocketChannel channel) {
		InetSocketAddress SocketAddress = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
		String address = SocketAddress.getHostName() + SocketAddress.getPort();
		return (hostIp + hostPort).equals(address);
	}
}
