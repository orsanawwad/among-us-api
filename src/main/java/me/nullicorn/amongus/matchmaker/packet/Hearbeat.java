package me.nullicorn.amongus.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;

/**
 * A bidirectional packet that tells the receiver to send a {@link HearbeatAck}. If the receiver sends an ACK back, then the sender can be sure that
 * they are still connected.
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class Hearbeat implements MatchmakerPacket {

  /*
  ========== PACKET STRUCTURE ==========
   - 2 byte: Nonce; any number; must be sent back to the sender in an ACK
   */

  @Getter
  protected short nonce;

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
  public void handle(MatchmakerClient client) {
    // Respond with an ACK
    client.sendPacket(new HearbeatAck(nonce));
  }

  @Override
  public String toString() {
    return "Hearbeat{" +
        "nonce=" + nonce +
        '}';
  }
}
