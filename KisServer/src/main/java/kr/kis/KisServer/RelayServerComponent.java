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
import java.util.LinkedList;

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

	@Autowired
	private SocketChannelService socketChannelService;

	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	private LinkedList<byte[]> bufferList = new LinkedList<byte[]>();

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
				logger.debug("### Server running : " + relayServerPort);
			}

			while (selector.select() > 0) {
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
				while (keys.hasNext()) {
					final SelectionKey key = keys.next();

					keys.remove();
					if (!key.isValid()) {
						continue;
					}

					if (key.isConnectable()) {
						connectable(key);
					} else if (key.isAcceptable()) {
						acceptable(selector, key);
					} else if (key.isReadable()) {
						readable(key);
					} else if (key.isWritable()) {
						writable(selector, key);
					}
				}
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
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

	private void connectable(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			socketChannel.finishConnect();
			
			if (socketChannelService.isHostServerChannel(socketChannel)) {
				logger.debug("### Host Server New Connection : " + hostIp + ":" + hostPort);
				socketChannelService.getSocketChannelVO(socketChannel).setHostServerKey(key);
			}
		} catch (Exception e) {
			socketChannelService.closeSelectionKey(key);
		}
	}

	private void acceptable(Selector selector, SelectionKey key) {
		try {
			socketChannelService.putSelectionKeys(key);

			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

			SocketChannel clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);

			Socket socket = clientChannel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			logger.debug("### Relay Server Connection Detect : " + remoteAddr);

			clientChannel.register(selector, SelectionKey.OP_READ);

			connectionHostServer(key, clientChannel);
		} catch (Exception e) {
			logger.error("ERROR", e);
			socketChannelService.closeSelectionKey(key);
		}
	}

	private void readable(SelectionKey key) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.configureBlocking(false);

			Socket socket = channel.socket();

			buffer.clear();
			int size = channel.read(buffer);
			if (size == -1) {
				throw new ConnectionClosedException(socket.getRemoteSocketAddress());
			}
			buffer.flip();

			if (socketChannelService.isHostServerChannel(channel)) {
				logger.debug("<<< Host Server Response Data Length : " + buffer.limit());
				buffer.clear();
				socketChannelService.getClientChannel(channel).register(selector, SelectionKey.OP_WRITE, ByteBuffer.wrap(buffer.array()));
			} else {
				logger.debug(">>> Client Request Data Length : " + buffer.limit());
				socketChannelService.getHostServerChannel(channel).register(selector, SelectionKey.OP_WRITE, ByteBuffer.wrap(buffer.array()));
			}
		} catch (Exception e) {
			if (e instanceof ConnectionClosedException) {
				logger.error("ERROR : " + e.getMessage(), e.getMessage());
			} else {
				logger.error("ERROR", e);
			}

			socketChannelService.closeSelectionKey(key);
		}
	}

	private void writable(Selector selector, SelectionKey key) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.configureBlocking(false);

			buffer.clear();
			buffer.put((ByteBuffer) key.attachment());
			buffer.flip();

			if (socketChannelService.isHostServerChannel(channel)) {
				logger.debug(">>> Host Server Request Data Length : " + buffer.limit());
			} else {
				logger.debug("<<< Client Response Data Length : " + buffer.limit());
			}

			channel.write(ByteBuffer.wrap(buffer.array()));
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			logger.error("ERROR", e);
			socketChannelService.closeSelectionKey(key);
		}
	}

	private void connectionHostServer(SelectionKey key, SocketChannel clientChannel) {
		SocketChannel hostServerChannel = null;

		try {
			hostServerChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostPort));
			hostServerChannel.configureBlocking(false);
			hostServerChannel.register(selector, SelectionKey.OP_READ);
			socketChannelService.mappingServer(key, clientChannel, hostServerChannel);
		} catch (Exception e) {
			logger.error("ERROR", e);
			socketChannelService.closeSelectionKey(key);
		}
	}
}
