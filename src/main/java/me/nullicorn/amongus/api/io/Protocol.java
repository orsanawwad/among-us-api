package me.nullicorn.amongus.api.io;

/**
 * A protocol for communication between an {@link AmongUsClient} and an Among Us server
 *
 * @author Nullicorn
 */
public interface Protocol<P extends Packet<?>> {

  /**
   * Create a new packet object with the provided packet ID
   *
   * @param packetId The ID of the packet; this will vary between protocols
   * @return A new packet object for the given ID
   */
  P createPacketInstance(byte packetId);

  /**
   * Get the packet ID that should be used for the provided packet
   *
   * @param packet A valid packet for this protocol
   * @return The packet ID to be used for the given packet
   */
  byte getPacketId(P packet);
}
