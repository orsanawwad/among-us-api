package me.nullicorn.amongus.io.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.logging.Logger;
import me.nullicorn.amongus.api.io.Packet;
import me.nullicorn.amongus.io.BasicAmongUsClient;
import me.nullicorn.amongus.io.packet.UDPPacket;
import me.nullicorn.amongus.util.HexUtil;

/**
 * Deserializes {@link UDPPacket}s
 *
 * @author Nullicorn
 */
public class PacketDecoder extends ByteToMessageDecoder {

  private static final Logger logger = Logger.getLogger(PacketDecoder.class.getSimpleName());

  private final BasicAmongUsClient client;

  public PacketDecoder(BasicAmongUsClient client) {
    this.client = client;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    try {
      logger.finest("Received data: " + HexUtil.byteBufToHex(in));

      byte packetId = in.readByte();
      Packet<BasicAmongUsClient> packet = client.getProtocol().createPacketInstance(packetId);
      packet.deserialize(in);

      logger.finest("Received packet: " + packet);
      packet.handle(client);

    } finally {
      in.release();
    }
  }
}