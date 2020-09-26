package me.nullicorn.amongus.protocol.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;

/**
 * Inbound handler for forwarding networking events to a {@link UdpProtocolClient}
 *
 * @author Nullicorn
 */
class UdpInboundHandler extends ChannelInboundHandlerAdapter {

  private final UdpProtocolClient client;

  public UdpInboundHandler(UdpProtocolClient client) {
    this.client = client;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    client.onStart();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    client.onStop();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    client.onPacketReceived((Packet) msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    client.onException(cause);
  }
}
