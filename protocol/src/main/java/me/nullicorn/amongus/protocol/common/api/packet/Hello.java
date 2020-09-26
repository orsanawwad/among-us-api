package me.nullicorn.amongus.protocol.common.api.packet;

/**
 * A Hello packet sent by the client when it firsts connects to any server.
 *
 * @author Nullicorn
 */
public interface Hello extends Packet {

  /**
   * @return The message sent in this packet. If this packet is sent by the client, this message should be the client's username. If it is sent by the
   * server, it should be an error message.
   */
  String getMessage();

  /**
   * @param value The message sent in this packet. If this packet is sent by the client, this message should be the client's username. If it is sent
   *              by the server, it should be an error message.
   */
  void setMessage(String value);
}
