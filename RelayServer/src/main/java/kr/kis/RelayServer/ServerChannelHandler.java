package kr.kis.RelayServer;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private HashMap<Channel, SocketChannel> channelMap = new HashMap<Channel, SocketChannel>();

	private NettyClient nettyClient;

	@Value("${socket.server.port}")
	private int tcpPort;

	public ServerChannelHandler(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

	/**
	 * Channel active.
	 *
	 * @param ctx the ctx
	 * @throws Exception the exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("tcpPort: " + tcpPort);

		SocketChannel clientChannel = nettyClient.openClientChannel(ctx.channel());
		channelMap.put(ctx.channel(), clientChannel);
	}

	/**
	 * Channel read.
	 *
	 * @param ctx the ctx
	 * @param msg the msg
	 * @throws Exception the exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = null;
		try {
			byteBuf = (ByteBuf) msg;
			channelMap.get(ctx.channel()).write(byteBuf.nioBuffer());
		} catch (Exception e) {
			logger.error("ERROR", e);
		} finally {
			try {
				byteBuf.release();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("ERROR", cause);
		ctx.close();
	}
}