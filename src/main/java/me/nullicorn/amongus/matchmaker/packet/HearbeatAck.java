package me.nullicorn.amongus.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;

/**
 * A bidirectional packet that ACKnowledges that is sent in response to a {@link Hearbeat}. This lets the receiver know that the sender is still
 * connected.
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class HearbeatAck implements MatchmakerPacket {

  /*
  ========== PACKET STRUCTURE ==========
   - 2 byte: Nonce; must be the same as one sent in a Heartbeat packet
   */

  @Getter
  private short nonce;

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
  public void handle(MatchmakerClient client) {
    client.onHeartbeatAck(this);
  }

  @Override
  public String toString() {
    return "HearbeatAck{" +
        "nonce=" + nonce +
        '}';
  }
}
