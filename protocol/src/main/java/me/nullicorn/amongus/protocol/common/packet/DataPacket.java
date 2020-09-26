package me.nullicorn.amongus.protocol.common.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.nullicorn.amongus.protocol.common.api.ProtocolException;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;

/**
 * A packet sent for a particular game event / action
 *
 * @author Nullicorn
 */
public abstract class DataPacket implements Packet {

  @Override
  public boolean isReliable() {
    return isReliable;
  }

  protected boolean isReliable;

  public DataPacket(boolean isReliable) {
    this.isReliable = isReliable;
  }

  abstract PayloadType getPayloadType();

  /**
   * The types of payloads that can be sent in data packets
   */
  public enum PayloadType {
    MASTER_SERVER_LIST(0x0E, MasterServerListPacket.class);

    private static final Int2ObjectMap<PayloadType> payloadById = new Int2ObjectOpenHashMap<>();

    private final int                     id;
    private final Class<? extends Packet> packetClass;

    PayloadType(int id, Class<? extends Packet> packetClass) {
      this.id = id;
      this.packetClass = packetClass;
    }

    /**
     * @return The ID used to indicate payloads of this type
     */
    public int getId() {
      return id;
    }

    /**
     * @return The class used to represent payloads of this type
     */
    public Class<? extends Packet> getPacketClass() {
      return packetClass;
    }

    /**
     * Reverse lookup a payload types by its protocol ID
     */
    public static PayloadType fromId(int id) {
      return payloadById.get(id);
    }

    /**
     * Get the payload type of a packet
     */
    public static PayloadType fromPacket(Packet packet) {
      if (packet == null) {
        throw new IllegalArgumentException("Cannot get payload ID of null packet");
      } else if (!(packet instanceof DataPacket)) {
        throw new IllegalArgumentException("Cannot get payload type of non DataPacket: " + packet.getClass().getSimpleName());
      }
      return ((DataPacket) packet).getPayloadType();
    }

    public Packet createPacketInstance() {
      try {
        return packetClass.getConstructor().newInstance();

      } catch (Exception e) {
        throw new ProtocolException("Unable to instantiate packet class: " + packetClass.getSimpleName(), e);
      }
    }

    static {
      for (PayloadType type : values()) {
        payloadById.put(type.getId(), type);
      }
    }
  }
}
