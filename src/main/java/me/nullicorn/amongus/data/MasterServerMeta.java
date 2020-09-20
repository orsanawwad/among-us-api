package me.nullicorn.amongus.data;

import java.net.SocketAddress;
import lombok.Getter;
import me.nullicorn.amongus.io.packet.MasterServerList;

/**
 * Metadata for a Master server (sent in {@link MasterServerList})
 *
 * @author Nullicorn
 */
@Getter
public class MasterServerMeta {

  /**
   * The name of the master server (e.g. "Master-1")
   */
  private final String name;

  /**
   * The address of the master server
   */
  private final SocketAddress address;

  public MasterServerMeta(String name, SocketAddress address) {
    this.name = name;
    this.address = address;
  }

  @Override
  public String toString() {
    return "MasterServerMeta{" +
        "name='" + name + '\'' +
        ", address=" + address +
        '}';
  }
}
