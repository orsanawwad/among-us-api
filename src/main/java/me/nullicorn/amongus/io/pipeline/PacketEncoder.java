package me.nullicorn.amongus.io.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.logging.Logger;
import me.nullicorn.amongus.io.BasicAmongUsClient;
import me.nullicorn.amongus.io.packet.UDPPacket;
import me.nullicorn.amongus.util.HexUtil;

/**
 * Serializes {@link UDPPacket}s
 *
 * @author Nullicorn
 */
public class PacketEncoder extends MessageToByteEncoder<UDPPacket> {

  private static final Logger logger = Logger.getLogger(PacketEncoder.class.getSimpleName());

  private final BasicAmongUsClient client;

  public PacketEncoder(BasicAmongUsClient client) {
    this.client = client;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, UDPPacket msg, ByteBuf out) {
    out.writeByte(client.getProtocol().getPacketId(msg));
    msg.serialize(out);
    logger.finest("Sent packet: " + msg + " (Data: " + HexUtil.byteBufToHex(out) + ")");
  }
}
