package me.nullicorn.amongus.io.packet;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.io.BasicAmongUsClient;

/**
 * The very first packet sent to the server; contains the client's username.
 *
 * @author Nullicorn
 */
@NoArgsConstructor
public class Login extends Hearbeat {

  // TODO: 9/18/20 Find out what these bytes actually mean
  private static final byte[] header = new byte[]{0x00, 0x46, (byte) 0xD2, 0x02, 0x03};

  /**
   * The client player's username
   */
  @Getter
  private String clientUsername;

  public Login(short nonce, String username) {
    super(nonce);
    this.clientUsername = username;
  }

  @Override
  public void deserialize(ByteBuf in) {
    this.nonce = in.readShort();
    in.skipBytes(header.length);

    byte usernameLength = in.readByte();
    this.clientUsername = in.readCharSequence(usernameLength, StandardCharsets.US_ASCII).toString();
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeShort(getNonce());
    out.writeBytes(header);

    out.writeByte(clientUsername.length());
    out.writeCharSequence(clientUsername, StandardCharsets.US_ASCII);
  }

  @Override
  public void handle(BasicAmongUsClient client) {
    // Not necessary; packet is serverbound only
  }

  @Override
  public String toString() {
    return "ServerboundHello{" +
        "username='" + clientUsername + '\'' +
        ", nonce=" + nonce +
        '}';
  }
}
