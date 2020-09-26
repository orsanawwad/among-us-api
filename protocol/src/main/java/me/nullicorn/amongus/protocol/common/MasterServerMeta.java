package me.nullicorn.amongus.protocol.common;

import java.net.InetSocketAddress;
import me.nullicorn.amongus.protocol.common.api.ServerMeta;
import me.nullicorn.amongus.protocol.common.packet.MasterServerListPacket;

/**
 * Metadata for an _Among Us_ Master server (a server that handles matchmaking). Sent in the {@link MasterServerListPacket} packet
 *
 * @author Nullicorn
 */
public class MasterServerMeta implements ServerMeta {

  private String            name;
  private InetSocketAddress address;
  private int               clientCount;

  public MasterServerMeta(String name, InetSocketAddress addr) {
    this(name, addr, 0);
  }

  public MasterServerMeta(String name, InetSocketAddress addr, int clientCount) {
    this.name = name;
    this.address = addr;
    this.clientCount = clientCount;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public InetSocketAddress getAddress() {
    return address;
  }

  @Override
  public int getClientCount() {
    return clientCount;
  }

  @Override
  public String toString() {
    return "MasterServerMeta{" +
        "name='" + name + '\'' +
        ", address=" + address +
        ", clientCount=" + clientCount +
        '}';
  }
}
