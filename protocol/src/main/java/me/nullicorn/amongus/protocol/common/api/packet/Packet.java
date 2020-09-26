package me.nullicorn.amongus.protocol.common.api.packet;

import me.nullicorn.amongus.protocol.common.api.ByteBufSerializable;

/**
 * Holds information that can be sent between a client and server
 *
 * @author Nullicorn
 */
public interface Packet extends ByteBufSerializable {

  boolean isReliable();

  /**
   * Types of packets that can be sent
   */
  enum Type {
    NORMAL(0x00),
    RELIABLE(0x01, true),
    HELLO(0x08, true),
    DISCONNECT(0x09),
    ACKNOWLEDGEMENT(0x0A),
    @Deprecated FRAGMENT(0x0B),
    PING(0x0C, true);

    /**
     * The ID used to indicate packets of this type
     */
    private final int id;

    /**
     * Whether or not this packet is guaranteed to send once
     */
    private final boolean isReliable;

    Type(int id) {
      this(id, false);
    }

    Type(int id, boolean isReliable) {
      this.id = id;
      this.isReliable = isReliable;
    }

    /**
     * @see #id
     */
    public int getId() {
      return id;
    }

    /**
     * @see #isReliable
     */
    public boolean isReliable() {
      return isReliable;
    }

    /**
     * @return The packet type with the given ID
     */
    public static Type fromId(int id) {
      for (Type type : values()) {
        if (type.id == id) {
          return type;
        }
      }
      return NORMAL;
    }
  }
}
