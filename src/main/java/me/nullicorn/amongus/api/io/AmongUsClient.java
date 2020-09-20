package me.nullicorn.amongus.api.io;

import io.netty.channel.ChannelFuture;
import java.net.SocketAddress;

/**
 * A client capable of connecting to a server for the game Among Us
 *
 * @author Nullicorn
 */
public interface AmongUsClient {

  /**
   * Connect this client to an Among Us server
   *
   * @param addr The address of the server
   * @throws Exception If a connection could not be made
   */
  void connect(SocketAddress addr) throws Exception;

  /**
   * Disconnect from the server. The client must be connected for this to work.
   *
   * @see #connect(SocketAddress)
   * @see #isConnected()
   */
  ChannelFuture disconnect();

  /**
   * @return Whether or not the client is currently connected to the server
   */
  boolean isConnected();

  /**
   * @return The protocol used by this client to process incoming and outgoing packets
   */
  Protocol<?> getProtocol();

  /**
   * Send a packet from this client to the server it's connected to
   *
   * @param packet A valid packet for this client's protocol (see {@link #getProtocol()})
   * @see #getProtocol()
   */
  ChannelFuture sendPacket(Packet<?> packet);
}
