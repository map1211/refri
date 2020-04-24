package kr.kis.RelayServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

@ChannelHandler.Sharable
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
	/**
	 * The Logger.
	 */
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Channels.
	 */
	private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/**
	 * Channel active.
	 *
	 * @param ctx the ctx
	 * @throws Exception the exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
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
			ctx.channel().writeAndFlush(msg);
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