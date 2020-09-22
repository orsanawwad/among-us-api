package me.nullicorn.amongus.client.api;

import java.util.Set;
import me.nullicorn.amongus.common.api.GameCode;
import me.nullicorn.amongus.common.api.ServerMeta;

/**
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
