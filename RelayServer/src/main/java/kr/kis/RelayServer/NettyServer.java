package kr.kis.RelayServer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
	/**
	 * The Tcp port.
	 */
//	@Value("${socket.server.port}")
	private int tcpPort;

	/**
	 * The Boss count.
	 */
//	@Value("${thread.maxNum}")
	private int bossCount;

	/**
	 * The Worker count. 
	 */
//	@Value("${thread.maxNum}")
	private int workerCount;

	/**
	 * The constant SERVICE_HANDLER.
	 */
	private static ServerChannelHandler SERVICE_HANDLER;

	public NettyServer(NettyClient client) {
		SERVICE_HANDLER = new ServerChannelHandler(client);
	}

	/**
	 * Start.
	 */
	public void start() {
		/**
		 * Ŭ���̾�Ʈ ������ �����ϴ� �θ� ������ �׷�
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup(bossCount);
		/**
		 * ����� Ŭ���̾�Ʈ���� �������� ���� ������ ����� �� �̺�Ʈ�� ����ϴ� �ڽ� ������
		 */
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) //���� ���� ����� ��带 NIO�� ����
					.handler(new LoggingHandler(LogLevel.INFO)) //���� ���� ä�� �ڵ鷯 ���
					.childHandler(new ChannelInitializer<SocketChannel>() { //�ۼ��� �Ǵ� ������ ���� �ڵ鷯
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new LoggingHandler(LogLevel.INFO));
							pipeline.addLast(SERVICE_HANDLER);
						}
					});

			ChannelFuture channelFuture = b.bind(tcpPort).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}