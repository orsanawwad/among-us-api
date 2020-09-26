package me.nullicorn.amongus.protocol.common.api.packet;

import org.jetbrains.annotations.Nullable;

/**
 * A Disconnect packet sent by either side to indicate that they are no longer listening for packets.
 *
 * @author Nullicorn
 */
public interface Disconnect extends Packet {

  /**
   * @return The reason code for this packet
   */
  int getReason();

  /**
   * @param value The reason code for this packet
   */
  void setReason(int value);

  /**
   * @return The reason message for this packet
   */
  @Nullable
  String getMessage();

  /**
   * @param value The custom message for this packet
   */
  void setMessage(@Nullable String value);

  /**
   * @return Whether or not this packet has a custom message
   */
  boolean hasMessage();
}
