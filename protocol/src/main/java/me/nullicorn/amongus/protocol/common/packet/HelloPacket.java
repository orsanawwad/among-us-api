package me.nullicorn.amongus.protocol.common.packet;

import io.netty.buffer.ByteBuf;
import me.nullicorn.amongus.protocol.common.api.packet.Hello;
import me.nullicorn.amongus.protocol.common.util.ByteBufUtil;

/**
 * Simple implementation of {@link Hello}
 *
 * @author Nullicorn
 */
public class HelloPacket implements Hello {

  // Possibly the sender's version number
  private static final byte[] UNKNOWN_BYTES = new byte[]{0x00, 0x46, (byte) 0xD2, 0x02, 0x03};

  private String message;

  public HelloPacket() {
  }

  public HelloPacket(String message) {
    this.message = message;
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeBytes(UNKNOWN_BYTES); // Unknown
    ByteBufUtil.writeUtf8(message, out); // Client username
  }

  @Override
  public void deserialize(ByteBuf in) {
    in.skipBytes(UNKNOWN_BYTES.length); // Unknown
    message = ByteBufUtil.readUtf8(in); // Client username
  }

  @Override
  public boolean isReliable() {
    return true;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "HelloPacket{" +
        "message='" + message + '\'' +
        '}';
  }
}
