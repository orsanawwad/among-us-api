package me.nullicorn.amongus.protocol.common.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import java.util.logging.Logger;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;
import me.nullicorn.amongus.protocol.common.api.packet.Packet.Type;
import me.nullicorn.amongus.protocol.common.packet.AcknowledgementPacket;
import me.nullicorn.amongus.protocol.common.packet.DataPacket.PayloadType;
import me.nullicorn.amongus.protocol.common.packet.DisconnectPacket;
import me.nullicorn.amongus.protocol.common.packet.HelloPacket;
import me.nullicorn.amongus.protocol.common.packet.PingPacket;
import me.nullicorn.amongus.protocol.common.pipeline.reliable.PendingPacket;

/**
 * Inbound handler for deserializing {@link Packet}s
 *
 * @author Nullicorn
 */
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

  private static final Logger logger = Logger.getLogger(PacketDecoder.class.getSimpleName());

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    // Read packet type
    int typeId = in.readUnsignedByte();
    Packet.Type type = Packet.Type.fromId(typeId);

    // Read reliable ID (if present)
    int reliableId = -1;
    if (type.isReliable()) {
      reliableId = in.readUnsignedShort();
    }

    // Create a new packet object
    Packet packet;
    switch (type) {
      case NORMAL:
      case RELIABLE:
        // Ignore packet length field
        int payloadLength = in.readUnsignedShortLE();

        // Read payload type
        int payloadTypeId = in.readUnsignedByte();
        PayloadType payloadType = PayloadType.fromId(payloadTypeId);

        // Swap out the entire buffer for just the payload buffer
        in = in.readBytes(payloadLength);

        if (payloadType != null) {
          packet = payloadType.createPacketInstance();
          if (type == Type.RELIABLE) {
            packet = new PendingPacket(packet, reliableId);
          }
        } else {
          logger.warning("Received data packet with unknown payload type: " + payloadTypeId);
          return;
        }
        break;

      case HELLO:
        packet = new PendingPacket(new HelloPacket(), reliableId);
        break;

      case DISCONNECT:
        packet = new DisconnectPacket();
        break;

      case ACKNOWLEDGEMENT:
        packet = new AcknowledgementPacket();
        break;

      case PING:
        packet = new PendingPacket(new PingPacket(), reliableId);
        break;

      default:
        // Unknown packet type; ignore
        return;
    }

    // Deserialize the packet
    packet.deserialize(in);
    out.add(packet);
  }
}
