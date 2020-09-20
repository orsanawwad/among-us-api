package me.nullicorn.amongus.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;
import me.nullicorn.amongus.matchmaker.data.MatchServerMeta;

/**
 * Sent to the client to tell it what matchmaker servers are available
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundMatchmakerList implements MatchmakerPacket {

  /*
  ========== PACKET STRUCTURE ==========
   - 1 byte:  Length of the remaining data minus 2
   - 3 bytes: Unknown (00 0E 01)
   - 1 byte:  The number of servers in the following list
   - List of matchmaking servers. For each server:
        - 1 byte:  UNKNOWN; Seems to always be 0x11
        - 3 bytes: Length of the server's name
        - n bytes: ASCII encoding of the server's name (where n is the length from the previous field)
        - 4 bytes: The server's IPv4 address
        - 2 bytes: The server's port (little endian)
        - 2 bytes: UNKNOWN; Examples: {63 47}, {9D 08}, {D2 05}, {A6 05}, {E6 05}, {EE 06}, {83 07}
   */

  private List<MatchServerMeta> servers;

  @Override
  public void deserialize(ByteBuf in) {
    in.skipBytes(4);
    int serverCount = in.readUnsignedByte();

    servers = new ArrayList<>();
    for (int i = 0; i < serverCount; i++) {
      in.skipBytes(1);
      int nameLength = in.readMedium();
      String name = in.readCharSequence(nameLength, StandardCharsets.US_ASCII).toString();
      String address = in.readUnsignedByte() + "." + in.readUnsignedByte() + "." + in.readUnsignedByte() + "." + in.readUnsignedByte();
      int port = in.readShortLE();
      in.skipBytes(2);

      servers.add(new MatchServerMeta(name, new InetSocketAddress(address, port)));
    }
  }

  @Override
  public void serialize(ByteBuf out) {
    // TODO: 9/19/20 Add serializer for matchmaker list
  }

  @Override
  public void handle(MatchmakerClient client) {
    // TODO: 9/19/20 Add handler for matchmaker list
  }

  @Override
  public String toString() {
    return "ClientboundMatchmakerList{" +
        "servers=" + servers +
        '}';
  }
}
