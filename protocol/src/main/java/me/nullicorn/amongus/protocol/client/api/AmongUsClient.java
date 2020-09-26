package me.nullicorn.amongus.protocol.client.api;

import java.util.Set;
import me.nullicorn.amongus.protocol.common.api.GameCode;
import me.nullicorn.amongus.protocol.common.api.ServerMeta;

/**
 * A client that can connect to _Among Us_ servers
 *
 * @author Nullicorn
 */
public interface AmongUsClient {

  /**
   * @return The username used by the client to login
   */
  String getUsername();

  /**
   * @return A list of Master servers that are available for the client to connect to
   */
  Set<ServerMeta> getMasterServerList();

  /**
   * Connect this client to a game using its game code
   */
  void joinGame(GameCode code);
}
