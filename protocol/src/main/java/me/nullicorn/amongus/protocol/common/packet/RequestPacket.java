package me.nullicorn.amongus.protocol.common.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.nullicorn.amongus.protocol.common.api.ProtocolException;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;

/**
 * A packet sent for a particular event / action
 *
 * @author Nullicorn
 */
public abstract class RequestPacket implements Packet {

  protected boolean isReliable;

  public RequestPacket(boolean isReliable) {
    this.isReliable = isReliable;
  }

  abstract RequestType getRequestType();

  @Override
  public boolean isReliable() {
    return isReliable;
  }

  /**
   * The types of requests that can be sent in data packets
   */
  public enum RequestType {
    MASTER_SERVER_LIST(0x0E, MasterServerListPacket.class);

    private static final Int2ObjectMap<RequestType> payloadById = new Int2ObjectOpenHashMap<>();

    private final int                     id;
    private final Class<? extends Packet> packetClass;

    RequestType(int id, Class<? extends Packet> packetClass) {
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
     * Reverse lookup a request type by its protocol ID
     */
    public static RequestType fromId(int id) {
      return payloadById.get(id);
    }

    /**
     * Get the request type of a packet
     */
    public static RequestType fromPacket(Packet packet) {
      if (packet == null) {
        throw new IllegalArgumentException("Cannot get payload ID of null packet");
      } else if (!(packet instanceof RequestPacket)) {
        throw new IllegalArgumentException("Cannot get request type of non DataPacket: " + packet.getClass().getSimpleName());
      }
      return ((RequestPacket) packet).getRequestType();
    }

    public Packet createPacketInstance() {
      try {
        return packetClass.getConstructor().newInstance();

      } catch (Exception e) {
        throw new ProtocolException("Unable to instantiate packet class: " + packetClass.getSimpleName(), e);
      }
    }

    static {
      for (RequestType type : values()) {
        payloadById.put(type.getId(), type);
      }
    }
  }
}
