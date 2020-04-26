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

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		ServerSocketChannel serverChannel = null;

		try {
			selector = Selector.open();

			serverChannel = ServerSocketChannel.open();
			{
				serverChannel.configureBlocking(false);
				serverChannel.socket().bind(new InetSocketAddress(relayServerPort));
				serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				logger.debug("Server running : " + relayServerPort);
			}

			while (selector.select() > 0) {
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while (keys.hasNext()) {
					final SelectionKey key = keys.next();

					keys.remove();
					if (!key.isValid()) {
						continue;
					}

					if (key.isAcceptable()) {
						acceptable(selector, key);
					} else if (key.isReadable()) {
						readable(key);
					}
				}
			}
		} catch (Exception e) {
			logger.error("!!! ERROR", e);
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
		String sessionId = "";

		try {
			socketChannelService.putSelectionKeys(key);

			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

			SocketChannel clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";

			Socket socket = clientChannel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			logger.debug(sessionId + "Relay Server Connection Detect : " + remoteAddr);

			connectionHostServer(key, clientChannel);
		} catch (Exception e) {
			logger.error("!!! " + sessionId + " ERROR : " + e.getMessage(), e.getMessage());
			socketChannelService.closeSelectionKey(key);
		}
	}

	private void readable(SelectionKey key) {
		String sessionId = "";
		SocketChannel clientChannel = null;

		try {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.configureBlocking(false);
			Socket socket = channel.socket();

			if (socketChannelService.isHostServerChannel(channel)) {
				SocketChannel hostServerChannel = channel;
				clientChannel = socketChannelService.getClientChannel(hostServerChannel);
			} else {
				clientChannel = channel;
			}
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";

			ByteBuffer buffer = ByteBuffer.allocate(4096);
			int size = channel.read(buffer);
			if (size == -1) {
				throw new ConnectionClosedException(socket.getRemoteSocketAddress());
			}

			if (socketChannelService.isHostServerChannel(channel)) {
				SocketChannel hostServerChannel = channel;
				clientChannel.configureBlocking(false);

				logger.debug(sessionId + " Host Request : " + (InetSocketAddress) hostServerChannel.socket().getRemoteSocketAddress());
				logger.debug(sessionId + " Host Server Response Data Length : " + size);

				logger.debug(sessionId + " Client Response Data Length : " + size);
				clientChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
			} else {
				logger.debug(sessionId + " Client Request : " + (InetSocketAddress) clientChannel.socket().getRemoteSocketAddress());
				logger.debug(sessionId + " Client Request Data Length : " + size);

				logger.debug(sessionId + " Host Server Request Data Length : " + size);
				SocketChannel hostChannel = socketChannelService.getHostServerChannel(clientChannel);
				hostChannel.configureBlocking(false);
				hostChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
			}

			buffer.clear();
		} catch (Exception e) {
			logger.error(sessionId + " ERROR : " + e.getMessage(), e.getMessage());
			socketChannelService.closeSelectionKey(key);
		}
	}

	private void connectionHostServer(SelectionKey key, SocketChannel clientChannel) {
		String sessionId = "";
		SocketChannel hostServerChannel = null;

		try {
			sessionId = "[" + System.identityHashCode(clientChannel) + "]";

			hostServerChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostPort));
			hostServerChannel.configureBlocking(false);

			logger.debug(sessionId + " Host Server Connection Waiting : " + hostIp + ":" + hostPort);
			hostServerChannel.finishConnect();
			logger.debug(sessionId + " Host Server Connection Success: " + hostIp + ":" + hostPort);

			socketChannelService.mappingServer(key, clientChannel, hostServerChannel);
			clientChannel.register(selector, SelectionKey.OP_READ);
			hostServerChannel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			logger.error(sessionId + " ERROR : " + e.getMessage(), e.getMessage());
			socketChannelService.closeSelectionKey(key);
		}
	}
}
