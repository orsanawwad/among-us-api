package me.nullicorn.amongus.protocol.common.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.logging.Logger;
import me.nullicorn.amongus.protocol.common.api.packet.Acknowledgement;
import me.nullicorn.amongus.protocol.common.api.packet.Disconnect;
import me.nullicorn.amongus.protocol.common.api.packet.Hello;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;
import me.nullicorn.amongus.protocol.common.api.packet.Packet.Type;
import me.nullicorn.amongus.protocol.common.api.packet.Ping;
import me.nullicorn.amongus.protocol.common.packet.DataPacket.PayloadType;
import me.nullicorn.amongus.protocol.common.pipeline.reliable.PendingPacket;
import me.nullicorn.amongus.protocol.common.util.ByteBufUtil;

/**
 * Outbound handler for serializing {@link Packet}s
 *
 * @author Nullicorn
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {

  private static final Logger logger = Logger.getLogger(PacketEncoder.class.getSimpleName());

  @Override
  protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
    Type type;
    if (packet instanceof PendingPacket) {
      type = getPacketType(((PendingPacket) packet).getActualPacket());
    } else {
      type = getPacketType(packet);
    }

    // Write the packet's type
    out.writeByte(type.getId());

    // Write the reliable packet's ID
    if (packet.isReliable() && packet instanceof PendingPacket) {
      out.writeShort(((PendingPacket) packet).getId());
    }

    // Write the packet's payload
    if (type == Type.RELIABLE || type == Type.NORMAL || type == Type.DISCONNECT) {
      ByteBuf payloadOut = Unpooled.buffer();
      try {
        packet.serialize(payloadOut);

        // Write the packet's size, payload type, and the actual payload
        out.writeShortLE(payloadOut.writerIndex());
        out.writeByte(PayloadType.fromPacket(packet).getId());
        out.writeBytes(payloadOut);
      } finally {
        payloadOut.release();
      }
    } else {
      packet.serialize(out);
    }

    logger.finest("Sending raw: " + ByteBufUtil.toLoggableString(out));
  }

  private Type getPacketType(Packet packet) {
    if (packet instanceof Hello) {
      return Type.HELLO;
    } else if (packet instanceof Disconnect) {
      return Type.DISCONNECT;
    } else if (packet instanceof Acknowledgement) {
      return Type.ACKNOWLEDGEMENT;
    } else if (packet instanceof Ping) {
      return Type.PING;
    } else if (packet != null && packet.isReliable()) {
      return Type.RELIABLE;
    } else {
      return Type.NORMAL;
    }
  }
}
