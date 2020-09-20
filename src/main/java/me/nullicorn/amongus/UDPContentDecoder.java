package me.nullicorn.amongus;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Gets the content from a datagram packet and passes its content onto the next layer of the pipeline (e.g. {@link ByteToMessageDecoder})
 *
 * @author Nullicorn
 */
public class UDPContentDecoder extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = ((DatagramPacket) msg).content();
    in.retain();
    ctx.fireChannelRead(in);
  }
}
