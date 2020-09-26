package me.nullicorn.amongus.protocol.common.api;

import io.netty.buffer.ByteBuf;

/**
 * An object that can be serialized to a Netty {@link ByteBuf}
 *
 * @author Nullicorn
 */
public interface ByteBufSerializable {

  /**
   * Serialize this object and write it to a buffer
   *
   * @param out The buffer to serialize this object to
   */
  void serialize(ByteBuf out);

  /**
   * Deserialize this object from a buffer
   *
   * @param in The buffer to read from
   */
  void deserialize(ByteBuf in);
}
