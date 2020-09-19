package me.nullicorn.amongus.matchmaker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.logging.Logger;
import me.nullicorn.amongus.matchmaker.packet.MatchmakerPacket;
import me.nullicorn.amongus.util.HexUtil;

/**
 * Serializes {@link MatchmakerPacket}s
 *
 * @author Nullicorn
 */
public class MMPacketEncoder extends MessageToByteEncoder<MatchmakerPacket> {

  private static final Logger logger = Logger.getLogger(MMPacketEncoder.class.getSimpleName());

  private final MatchmakerClient client;

  public MMPacketEncoder(MatchmakerClient client) {
    this.client = client;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, MatchmakerPacket msg, ByteBuf out) {
    out.writeByte(client.getProtocol().getPacketId(msg));
    msg.serialize(out);
    logger.finest("Sent packet: " + msg + " (Data: " + HexUtil.byteBufToHex(out) + ")");
  }
}
