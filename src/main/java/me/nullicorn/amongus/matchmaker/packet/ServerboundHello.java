package me.nullicorn.amongus.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;

/**
 * Sent to the server to set the client's username. This also doubles as the first {@link Hearbeat} packet.
 *
 * @author Nullicorn
 */
@NoArgsConstructor
public class ServerboundHello extends Hearbeat {

  /*
  ========== PACKET STRUCTURE ==========
   - 2 byte:  Nonce, same as in Heartbeat
   - 5 bytes: UNKNOWN; Example: {0x00, 0x46, 0xD2, 0x02, 0x03}
   - 1 byte:  The number of servers in the following list
   - 1 byte:  Length of the player's username
   - n bytes: ASCII encoding of the player's username (where n is the length from the previous field)
   */

  // TODO: 9/18/20 Find out what these bytes actually mean
  private static final byte[] header = new byte[]{0x00, 0x46, (byte) 0xD2, 0x02, 0x03};

  /**
   * The client player's username
   */
  private String username;

  public ServerboundHello(short nonce, String username) {
    super(nonce);
    this.username = username;
  }

  @Override
  public void deserialize(ByteBuf in) {
    this.nonce = in.readShort();
    in.skipBytes(header.length);

    byte usernameLength = in.readByte();
    this.username = in.readCharSequence(usernameLength, StandardCharsets.US_ASCII).toString();
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeShort(getNonce());
    out.writeBytes(header);

    out.writeByte(username.length());
    out.writeCharSequence(username, StandardCharsets.US_ASCII);
  }

  @Override
  public void handle(MatchmakerClient client) {
    // Not necessary; packet is serverbound only
  }

  @Override
  public String toString() {
    return "ServerboundHello{" +
        "username='" + username + '\'' +
        ", nonce=" + nonce +
        '}';
  }
}
