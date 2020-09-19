package me.nullicorn.amongus.matchmaker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.logging.Logger;
import me.nullicorn.amongus.api.Packet;
import me.nullicorn.amongus.matchmaker.packet.MatchmakerPacket;
import me.nullicorn.amongus.util.HexUtil;

/**
 * Deserializes {@link MatchmakerPacket}s
 *
 * @author Nullicorn
 */
public class MMPacketDecoder extends ByteToMessageDecoder {

  private static final Logger logger = Logger.getLogger(MMPacketDecoder.class.getSimpleName());

  private final MatchmakerClient client;

  public MMPacketDecoder(MatchmakerClient client) {
    this.client = client;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    try {
      logger.finest("Received data: " + HexUtil.byteBufToHex(in));

      byte packetId = in.readByte();
      Packet<MatchmakerClient> packet = client.getProtocol().createPacketInstance(packetId);
      packet.deserialize(in);

      logger.finest("Received packet: " + packet);
      packet.handle(client);

    } finally {
      in.release();
    }
  }
}