package me.nullicorn.amongus.io.packet;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.data.MasterServerMeta;
import me.nullicorn.amongus.io.BasicAmongUsClient;

/**
 * Tells the client what master servers are available to connect to
 *
 * @author Nullicorn
 */
@NoArgsConstructor
@AllArgsConstructor
public class MasterServerList implements UDPPacket {

  private List<MasterServerMeta> servers;

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

      servers.add(new MasterServerMeta(name, new InetSocketAddress(address, port)));
    }
  }

  @Override
  public void serialize(ByteBuf out) {
    // TODO: 9/19/20 Add serializer for server list
  }

  @Override
  public void handle(BasicAmongUsClient client) {
    // TODO: 9/19/20 Add handler for server list
  }

  @Override
  public String toString() {
    return "MasterServerList{" +
        "servers=" + servers +
        '}';
  }
}
