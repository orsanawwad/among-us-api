package me.nullicorn.amongus.packet;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.MatchmakerClient;
import me.nullicorn.amongus.data.MatchServerMeta;

/**
 * Sent to the client to tell it what matchmaker servers are available
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class ClientboundMatchmakerList implements MatchmakerPacket {

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
