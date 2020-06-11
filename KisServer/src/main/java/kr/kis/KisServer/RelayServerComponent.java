package kr.kis.KisServer;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
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

	@Value("${log.exception.ip}")
	private String logExceptionIps;
	private ArrayList<String> logExceptionIpList;
	private ArrayList<Integer> logExceptionIdList;
	private int maxLogExceptionIpList = 100;

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

			// 로그 예외 대상
			{
				if (logExceptionIps == null) {
					logExceptionIps = "";
				}

				logExceptionIpList = new ArrayList<String>(Arrays.asList(logExceptionIps.split(",")));
				logger.debug("logExceptionIps : " + Arrays.toString(logExceptionIpList.toArray()));
				logExceptionIdList = new ArrayList<Integer>(logExceptionIpList.size() * 2);
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
		int sessionId = 0;
		SocketChannel clientChannel = null;
		ServerSocketChannel serverChannel = null;

		try {
			serverChannel = (ServerSocketChannel) key.channel();

			clientChannel = serverChannel.accept();
			clientChannel.configureBlocking(false);
			sessionId = System.identityHashCode(clientChannel);

			socket = clientChannel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			String removeAddrStr = ((InetSocketAddress) remoteAddr).getAddress().getHostAddress();

			// 로그 예외 IP 확인
			checkLogExceptionId(sessionId, removeAddrStr);

			debugLog(sessionId, " Session Count (" + SocketChannelService.SESSION_COUNT.incrementAndGet() + ")");
			debugLog(sessionId, " Relay Server Connection Detect : " + remoteAddr + " (" + removeAddrStr + ")");

			connectionHostServer(clientChannel);
		} catch (Exception e) {
			errorLog(sessionId, " ERROR - Accept: " + e.getMessage());
			close(clientChannel);
		}
	}

	private void readable(SelectionKey key) {
		int sessionId = 0;
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

			sessionId = System.identityHashCode(clientChannel);

			buffer.clear();
			int size = channel.read(buffer);
			if (size == -1) {
				throw new ConnectionClosedException(socket.getRemoteSocketAddress());
			}

			if (socketChannelService.isHostServerChannel(channel)) {
				SocketChannel hostServerChannel = channel;

				debugLog(sessionId, " Host Server Response : " + (InetSocketAddress) hostServerChannel.socket().getRemoteSocketAddress());
				debugLog(sessionId, " Host Server Response Data Length : " + size);
				clientChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
				debugLog(sessionId, " Client Response Data Length : " + size);
			} else {
				debugLog(sessionId, " Client Request : " + (InetSocketAddress) clientChannel.socket().getRemoteSocketAddress());
				debugLog(sessionId, " Client Request Data Length : " + size);

				SocketChannel hostChannel = socketChannelService.getHostServerChannel(clientChannel);
				hostChannel.write(ByteBuffer.wrap(buffer.array(), 0, size));
				debugLog(sessionId, " Host Server Request Data Length : " + size);
			}
		} catch (Exception e) {
			errorLog(sessionId, " ERROR - Read : " + e.getMessage());
			close(channel);
		}
	}

	private void connectionHostServer(SocketChannel clientChannel) throws Exception {
		int sessionId = 0;
		SocketChannel hostServerChannel = null;

		try {
			sessionId = System.identityHashCode(clientChannel);

			debugLog(sessionId, " Host Server Connection Waiting : " + hostIp + ":" + hostPort);
			hostServerChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostPort));
			hostServerChannel.configureBlocking(false);
			debugLog(sessionId, " Session Count (" + SocketChannelService.SESSION_COUNT.incrementAndGet() + ")");

			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
			SelectionKey hostServerKey = hostServerChannel.register(selector, SelectionKey.OP_READ);
			socketChannelService.mappingServer((SocketChannel) clientKey.channel(), (SocketChannel) hostServerKey.channel());
			debugLog(sessionId, " Host Server Connection Success: " + hostIp + ":" + hostPort);
		} catch (Exception e) {
			errorLog(sessionId, " ERROR - Connection Host Server : " + e.getMessage());
			throw e;
		}
	}

	private void close(SocketChannel channel) {
		int sessionId = System.identityHashCode(channel);
		boolean isHostServerChannel = false;
		SocketChannel mappingChannel = null;

		try {
			isHostServerChannel = socketChannelService.isHostServerChannel(channel);
			if (isHostServerChannel) {
				sessionId = System.identityHashCode(socketChannelService.getClientChannel(channel));
				mappingChannel = socketChannelService.getClientChannel(channel);
			} else {
				mappingChannel = socketChannelService.getHostServerChannel(channel);
			}

			debugLog(sessionId, " Mapping Channel Close Start [" + System.identityHashCode(channel) + "]");
			SelectionKey mappingSelectionKey = mappingChannel.keyFor(selector);
			socketChannelService.close(mappingSelectionKey);
			debugLog(sessionId, " Mapping Channel Close End [" + System.identityHashCode(channel) + "]");
		} catch (Exception e) {
			errorLog(sessionId, " ERROR - " + e.getMessage());
		} finally {
			try {
				debugLog(sessionId, " Channel Close Start [" + System.identityHashCode(channel) + "]");
				SelectionKey selectionKey = channel.keyFor(selector);
				socketChannelService.close(selectionKey);
				debugLog(sessionId, " Channel Close End [" + System.identityHashCode(channel) + "]");
			} catch (Exception e) {
				errorLog(sessionId, " ERROR - " + e.getMessage());

				// Mapping 하지 못한 Session 처리
				try {
					if (channel.socket().isClosed() == false) {
						try {
							channel.socket().close();
							debugLog(sessionId, " Session Count (" + SocketChannelService.SESSION_COUNT.decrementAndGet() + ")");
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

			debugLog(sessionId, " Channel Map Count (" + SocketChannelService.CHANNEL_MAP.size() + ")");
		}
	}

	/**
	 * 로그 제외 대상인지 확인.
	 * . 로그 제외 대상이면 해당 대상에 sessionId를 저장한다.
	 * . IP String 값보단 ID int 값을 비교해 상대적 성능 향상을 기대
	 * 
	 * @param sessionId
	 * @param ip
	 */
	private void checkLogExceptionId(int sessionId, String ip) {
		if (logExceptionIpList.contains(ip)) {
			logExceptionIdList.add(sessionId);
			
			/*
			 * 매번 달라지는 ID를 수집 후 해제 할 시점이 다양하므로
			 * 단순히 예외 대상 ID를 Array에 넣고 개수가 너무 많아지면
			 * 가장 오래된 데이터를 정리하는 방식으로 예외대상을 관리한다.
			 */
			if (logExceptionIdList.size() > maxLogExceptionIpList) {
				logExceptionIdList.remove(0);
			}
		}
	}

	private void debugLog(int sessionId, String msg) {
		if (isLogExceptionId(sessionId)) {
			return;
		}

		logger.debug("[" + sessionId + "]" + msg);
	}

	private void errorLog(int sessionId, String msg) {
		if (isLogExceptionId(sessionId)) {
			return;
		}

		logger.error("[" + sessionId + "]" + msg);
	}

	private boolean isLogExceptionId(int sessionId) {
		return logExceptionIdList.contains(sessionId);
	}
}
