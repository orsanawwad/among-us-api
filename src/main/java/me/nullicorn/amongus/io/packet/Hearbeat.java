package me.nullicorn.amongus.io.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.nullicorn.amongus.io.BasicAmongUsClient;

/**
 * Tells the receiver to send an {@link Acknowledgement}. If they do, the sender can be sure that the other side is still listening for packets.
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class Hearbeat implements AckablePacket {

  @Getter
  protected int nonce;

  @Override
  public void deserialize(ByteBuf in) {
    this.nonce = in.readShort();
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeShort(nonce);
  }

  @SneakyThrows
  @Override
  public void handle(BasicAmongUsClient client) {
    // Respond with an ACK
    client.sendPacket(new Acknowledgement(this));
  }

  @Override
  public String toString() {
    return "Hearbeat{" +
        "nonce=" + nonce +
        '}';
  }
}
