package me.nullicorn.amongus.protocol.common.api.packet;

import java.util.List;
import me.nullicorn.amongus.protocol.common.api.ServerMeta;

/**
 * Sent to the client after they connect to a Master server. Contains a list of other master servers in the player's region
 *
 * @author Nullicorn
 */
public interface MasterServerList extends Packet {

  /**
   * @return The list of servers sent in this packet
   */
  List<ServerMeta> getServers();

  /**
   * @param servers The list of servers sent in this packet
   */
  void setServers(List<ServerMeta> servers);
}
