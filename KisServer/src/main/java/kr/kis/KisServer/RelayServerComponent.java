package kr.kis.KisServer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class RelayServerComponent implements ApplicationRunner {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${socket.server.port}")
	private int relayServerPort;

	@Value("${socket.host.ip}")
	private String hostIp;

	@Value("${socket.host.port}")
	private int hostPort;

	@Value("${socket.server.encoding}")
	private String encoding;

	@Autowired
	private SocketChannelService socketChannelService;

	private Selector selector = null;

	private ByteBuffer buffer = ByteBuffer.allocate(4096);

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		SelectionKey selectedKey = null;
		Iterator<SelectionKey> selectedKeysIterator = null;
		ServerSocketChannel serverChannel = null;

		try {
			selector = Selector.open();

			// Relay Server Channel 설정
			serverChannel = ServerSocketChannel.open();
			{
				serverChannel.configureBlocking(false);
				serverChannel.socket().bind(new InetSocketAddress(relayServerPort));
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				logger.debug("Server running : " + relayServerPort);
			}

			// Selector
			while (selector.select() > 0) {
				selectedKeysIterator = selector.selectedKeys().iterator();
				while (selectedKeysIterator.hasNext()) {
					// Selected Key 획득
					selectedKey = selectedKeysIterator.next();

					// SelectedKays 정리
					selectedKeysIterator.remove();

					// Validation
					if (!selectedKey.isValid()) {
						continue;
					}

					if (selectedKey.isAcceptable()) {
						acceptable(selector, selectedKey);
					} else if (selectedKey.isReadable()) {
						readable(selectedKey);
					}
				}
			}
		} catch (Exception e) {
			logger.error("ERROR - " + e.getMessage());
		} finally {
			try {
				serverChannel.close();
			} catch (Exception e1) {
			}

			try {
				selector.close();
			} catch (Exception e) {
			}
		}
	}

	private void acceptable(Selector selector, SelectionKey key) {
		Socket socket = null;
		String sessionId = "";
		SocketChannel clientChannel = null;
		ServerSocketChannel serverChannel = null;

		try {
			serverChannel = (ServerSocketChannel) key.channel();

			clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";
			logger.debug("Session Count (" + SocketChannelService.SESSION_COUNT.incrementAndGet() + ")");

			socket = clientChannel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			logger.debug(sessionId + "Relay Server Connection Detect : " + remoteAddr);

			connectionHostServer(clientChannel);
		} catch (Exception e) {
			logger.error(sessionId + " ERROR - Accept: " + e.getMessage());
			close(clientChannel);
		}
	}

	private void readable(SelectionKey key) {
		String sessionId = "";
		SocketChannel channel = null;
		SocketChannel clientChannel = null;

		try {
			channel = (SocketChannel) key.channel();
			Socket socket = channel.socket();

			if (socketChannelService.isHostServerChannel(channel)) {
				SocketChannel hostServerChannel = channel;
				clientChannel = socketChannelService.getClientChannel(hostServerChannel);
			} else {
				clientChannel = channel;
			}
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";

			buffer.clear();
			int size = channel.read(buffer);
			if (size == -1) {
				throw new ConnectionClosedException(socket.getRemoteSocketAddress());
			}

			if (socketChannelService.isHostServerChannel(channel)) {
				SocketChannel hostServerChannel = channel;

				logger.debug(sessionId + " Host Server Response : " + (InetSocketAddress) hostServerChannel.socket().getRemoteSocketAddress());
				logger.debug(sessionId + " Host Server Response Data Length : " + size);
				clientChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
				logger.debug(sessionId + " Client Response Data Length : " + size);
			} else {
				logger.debug(sessionId + " Client Request : " + (InetSocketAddress) clientChannel.socket().getRemoteSocketAddress());
				logger.debug(sessionId + " Client Request Data Length : " + size);

				SocketChannel hostChannel = socketChannelService.getHostServerChannel(clientChannel);
				hostChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
				logger.debug(sessionId + " Host Server Request Data Length : " + size);
			}
		} catch (Exception e) {
			logger.error(sessionId + " ERROR - Read : " + e.getMessage());
			close(channel);
		}
	}

	private void connectionHostServer(SocketChannel clientChannel) throws Exception {
		String sessionId = "";
		SocketChannel hostServerChannel = null;

		try {
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";

			logger.debug(sessionId + " Host Server Connection Waiting : " + hostIp + ":" + hostPort);
			hostServerChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostPort));
			hostServerChannel.configureBlocking(false);
			logger.debug("Session Count (" + SocketChannelService.SESSION_COUNT.incrementAndGet() + ")");

			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			SelectionKey hostServerKey = hostServerChannel.register(selector, SelectionKey.OP_READ);
			socketChannelService.mappingServer((SocketChannel) clientKey.channel(), (SocketChannel) hostServerKey.channel());
			logger.debug(sessionId + " Host Server Connection Success: " + hostIp + ":" + hostPort);
		} catch (Exception e) {
			logger.error(sessionId + " ERROR - Connection Host Server : " + e.getMessage());
			throw e;
		}
	}

	private void close(SocketChannel channel) {
		String sessionId = "[" + System.identityHashCode(channel) + "]";
		boolean isHostServerChannel = false;
		SocketChannel mappingChannel = null;

		try {
			isHostServerChannel = socketChannelService.isHostServerChannel(channel);
			if (isHostServerChannel) {
				sessionId = "[" + System.identityHashCode(socketChannelService.getClientChannel(channel)) + "]";
				mappingChannel = socketChannelService.getClientChannel(channel);
			} else {
				mappingChannel = socketChannelService.getHostServerChannel(channel);
			}

			logger.debug(sessionId + " Mapping Channel Close Start [" + System.identityHashCode(channel) + "]");
			SelectionKey mappingSelectionKey = mappingChannel.keyFor(selector);
			socketChannelService.close(sessionId, mappingSelectionKey);
			logger.debug(sessionId + " Mapping Channel Close End [" + System.identityHashCode(channel) + "]");
		} catch (Exception e) {
			logger.error(sessionId + " ERROR - " + e.getMessage());
		} finally {
			try {
				logger.debug(sessionId + " Channel Close Start [" + System.identityHashCode(channel) + "]");
				SelectionKey selectionKey = channel.keyFor(selector);
				socketChannelService.close(sessionId, selectionKey);
				logger.debug(sessionId + " Channel Close End [" + System.identityHashCode(channel) + "]");
			} catch (Exception e) {
				logger.error(sessionId + " ERROR - " + e.getMessage());

				// Mapping 하지 못한 Session 처리
				try {
					if (channel.socket().isClosed() == false) {
						try {
							channel.socket().close();
							logger.debug("Session Count (" + SocketChannelService.SESSION_COUNT.decrementAndGet() + ")");
							SocketChannelService.CHANNEL_MAP.remove(channel);
						} catch (Exception e1) {
						}

						try {
							channel.close();
						} catch (Exception e1) {
						}
					}
				} catch (Exception e1) {
				}
			}

			logger.debug("Channel Map Count (" + SocketChannelService.CHANNEL_MAP.size() + ")");
		}
	}
}
