package me.nullicorn.amongus.protocol.common.api;

import java.net.InetSocketAddress;

/**
 * Metadata for an _Among Us_ server.
 *
 * @author Nullicorn
 */
public interface ServerMeta {

  /**
   * @return The name of the server
   */
  String getName();

  /**
   * @return The address and port that the server is hosted on
   */
  InetSocketAddress getAddress();

  /**
   * @return The number of clients connected to the server
   */
  int getClientCount();
}
