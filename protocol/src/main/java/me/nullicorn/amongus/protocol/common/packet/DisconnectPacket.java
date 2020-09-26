package me.nullicorn.amongus.protocol.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.nullicorn.amongus.protocol.common.api.packet.Disconnect;
import me.nullicorn.amongus.protocol.common.util.ByteBufUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Simple implementation of {@link Disconnect}
 *
 * @author Nullicorn
 */
@AllArgsConstructor
public class DisconnectPacket implements Disconnect {

  @Getter
  @Setter
  private int reason;

  @Getter
  @Setter
  @Nullable
  private String message;

  public DisconnectPacket() {
    this(-1, null);
  }

  public DisconnectPacket(@Nullable String reason) {
    // Disconnects with messages always seem to use 0x08
    this(8, reason);
  }

  @Override
  public void serialize(ByteBuf out) {
    if (reason != -1) {
      ByteBuf payloadOut = Unpooled.buffer();
      try {
        payloadOut.writeByte(reason); // Reason code
        if (hasMessage()) {
          ByteBufUtil.writeUtf8(message, payloadOut); // Custom message
        }

        out.writeBoolean(true); // Has reason
        out.writeShortLE(payloadOut.writerIndex()); // Payload length
        out.writeByte(0x00); // Unknown
        out.writeBytes(payloadOut); // Payload

      } finally {
        payloadOut.release();
      }
    }
  }

  @Override
  public void deserialize(ByteBuf in) {
    if (in.isReadable() && in.readBoolean()) {
      int payloadLength = in.readUnsignedShortLE(); // Payload length
      ByteBuf payloadIn = in.readBytes(payloadLength); // Payload
      try {
        reason = in.readUnsignedByte(); // Reason code
        if (reason == 8) {
          message = ByteBufUtil.readUtf8(in); // Custom message
        }
      } finally {
        payloadIn.release();
      }
    }
  }

  @Override
  public boolean isReliable() {
    return false;
  }

  @Override
  public boolean hasMessage() {
    return getReason() == 8;
  }

  @Override
  public String toString() {
    return "DisconnectPacket{" +
        "reason=" + reason +
        ", message='" + message + '\'' +
        '}';
  }
}
