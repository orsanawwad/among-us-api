package me.nullicorn.amongus.protocol.client.api;

import java.net.InetSocketAddress;

/**
 * A runnable client that can talk to servers using the _Among Us_ protocol
 *
 * @author Nullicorn
 */
public interface RunnableProtocolClient {

  /**
   * Start the client and connect to the server
   *
   * @param serverAddr Address of the server to connect to
   */
  void start(InetSocketAddress serverAddr);

  /**
   * Stop the client and disconnect from the server (if connected)
   */
  void stop();

  /**
   * @return Whether or not the client is currently connected to the server
   */
  boolean isRunning();
}
