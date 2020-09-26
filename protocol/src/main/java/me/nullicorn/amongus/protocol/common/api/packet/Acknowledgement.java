package me.nullicorn.amongus.protocol.common.api.packet;

/**
 * An Acknowledgement packet sent to acknowledge the receipt of a reliable packet. This lets the receiver know that their reliable packet was received
 * (and that they don't need to resend it).
 *
 * @author Nullicorn
 */
public interface Acknowledgement extends Packet {

  /**
   * @return The ID of the reliable packet that this packet is acknowledging
   */
  int getOriginalId();

  /**
   * @param value The ID of the reliable packet that this packet is acknowledging
   */
  void setOriginalId(int value);
}
