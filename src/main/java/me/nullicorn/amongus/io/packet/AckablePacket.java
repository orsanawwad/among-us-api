package me.nullicorn.amongus.io.packet;

/**
 * A packet that must be acknowledged by the receiver via a {@link Acknowledgement} packet
 *
 * @author Nullicorn
 */
public interface AckablePacket extends UDPPacket {

  /**
   * @return The nonce of this packet (must be sent back in the corresponding {@link Acknowledgement})
   */
  int getNonce();
}
