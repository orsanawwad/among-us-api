package me.nullicorn.amongus.io.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.io.BasicAmongUsClient;

/**
 * Tells the other side that the {@link AckablePacket} with the matching nonce has been received
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class Acknowledgement implements UDPPacket {

  /**
   * The nonce from the received packet
   */
  @Getter
  private int nonce;

  public Acknowledgement(AckablePacket original) {
    this.nonce = original.getNonce();
  }

  @Override
  public void deserialize(ByteBuf in) {
    this.nonce = in.readShort();
    in.skipBytes(1);
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeShort(nonce);
    out.writeByte(0xFF);
  }

  @Override
  public void handle(BasicAmongUsClient client) {
    client.onHeartbeatAck(this);
  }

  @Override
  public String toString() {
    return "HearbeatAck{" +
        "nonce=" + nonce +
        '}';
  }
}
