package me.nullicorn.amongus.server.api;

import java.util.Set;
import me.nullicorn.amongus.client.api.AmongUsClient;
import me.nullicorn.amongus.common.api.ServerMeta;

/**
 * @author Nullicorn
 */
public interface AmongUsServer extends ServerMeta {

  /**
   * @return Whether or not the server is intended for use on a LAN
   */
  boolean isLocal();

  /**
   * @return Whether or not the server is currently running
   */
  boolean isRunning();

  /**
   * Begin running the server
   */
  void start();

  /**
   * Stop running the server
   */
  void stop();

  /**
   * @return All identifiable clients connected to this server
   */
  Set<AmongUsClient> getClients();

  @Override
  default int getClientCount() {
    Set<AmongUsClient> clients = getClients();
    return clients == null ? 0 : clients.size();
  }
}
