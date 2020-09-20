package me.nullicorn.amongus.api.io;

import io.netty.buffer.ByteBuf;

/**
 * A packet of binary data sent between an {@link AmongUsClient} and a server
 *
 * @author Nullicorn
 */
public interface Packet<C extends AmongUsClient> {

  /**
   * Read the data from a buffer into this packet object
   *
   * @param in The buffer to read from
   */
  void deserialize(ByteBuf in);

  /**
   * Write the data from this packet object to a buffer
   *
   * @param out The buffer to write to
   */
  void serialize(ByteBuf out);

  /**
   * Called when this packet is received (and after {@link #serialize(ByteBuf)}
   *
   * @param client The client that received this packet
   */
  void handle(C client);
}
