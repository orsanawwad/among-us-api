package me.nullicorn.amongus.protocol.common.packet;

import io.netty.buffer.ByteBuf;
import me.nullicorn.amongus.protocol.common.api.packet.Acknowledgement;

/**
 * Simple implementation of {@link Acknowledgement}
 *
 * @author Nullicorn
 */
public class AcknowledgementPacket implements Acknowledgement {

  private int originalId;

  public AcknowledgementPacket() {
    this(-1);
  }

  /**
   * @param originalId See {@link Acknowledgement#getOriginalId()}
   */
  public AcknowledgementPacket(int originalId) {
    this.originalId = originalId;
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeShort(originalId);
    out.writeByte(0xFF); // Unknown
  }

  @Override
  public void deserialize(ByteBuf in) {
    originalId = in.readUnsignedShort();
    in.skipBytes(1); // Unknown
  }

  @Override
  public boolean isReliable() {
    return false;
  }

  @Override
  public int getOriginalId() {
    return originalId;
  }

  @Override
  public void setOriginalId(int originalId) {
    this.originalId = originalId;
  }

  @Override
  public String toString() {
    return "AcknowledgementPacket{" +
        "originalId=" + originalId +
        '}';
  }
}
