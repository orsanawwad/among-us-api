package me.nullicorn.amongus.matchmaker.data;

import java.net.SocketAddress;
import lombok.Getter;
import me.nullicorn.amongus.matchmaker.packet.ClientboundMatchmakerList;

/**
 * Metadata for a matchmaker server (sent in {@link ClientboundMatchmakerList})
 *
 * @author Nullicorn
 */
@Getter
public class MatchServerMeta {

  /**
   * The name of the matchmaking server (e.g. "Master-1")
   */
  private final String name;

  /**
   * The address of the matchmaking server
   */
  private final SocketAddress address;

  public MatchServerMeta(String name, SocketAddress address) {
    this.name = name;
    this.address = address;
  }

  @Override
  public String toString() {
    return "MatchServerMeta{" +
        "name='" + name + '\'' +
        ", address=" + address +
        '}';
  }
}
