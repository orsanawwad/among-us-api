package me.nullicorn.amongus.io;

import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import me.nullicorn.amongus.api.io.Protocol;
import me.nullicorn.amongus.api.io.ProtocolException;
import me.nullicorn.amongus.io.packet.Acknowledgement;
import me.nullicorn.amongus.io.packet.Disconnect;
import me.nullicorn.amongus.io.packet.Hearbeat;
import me.nullicorn.amongus.io.packet.Login;
import me.nullicorn.amongus.io.packet.MasterServerList;
import me.nullicorn.amongus.io.packet.UDPPacket;

/**
 * The UDP protocol for communication with Among Us servers
 *
 * @author Nullicorn
 */
public class AmongUsProtocol implements Protocol<UDPPacket> {

  private Map<Byte, Class<? extends UDPPacket>> packetsById = new HashMap<>();
  private Map<Class<? extends UDPPacket>, Byte> packetIds   = new HashMap<>();

  public AmongUsProtocol() {
    registerPacketType(0x00, MasterServerList.class);
    registerPacketType(0x08, Login.class);
    registerPacketType(0x09, Disconnect.class);
    registerPacketType(0x0A, Acknowledgement.class);
    registerPacketType(0x0C, Hearbeat.class);
  }

  @SneakyThrows
  @Override
  public UDPPacket createPacketInstance(byte packetId) {
    Class<? extends UDPPacket> packetClass = packetsById.get(packetId);
    if (packetClass == null) {
      throw new ProtocolException("No such packet with ID 0x" + String.format("%02x", packetId).toUpperCase());
    }
    return packetClass.getConstructor().newInstance();
  }

  @Override
  public byte getPacketId(UDPPacket packet) {
    return packetIds.get(packet.getClass());
  }

  /**
   * Registers a new packet type with the provided ID and class (for deserialization)
   */
  protected void registerPacketType(int id, Class<? extends UDPPacket> clazz) {
    packetsById.put((byte) id, clazz);
    packetIds.put(clazz, (byte) id);
  }
}
