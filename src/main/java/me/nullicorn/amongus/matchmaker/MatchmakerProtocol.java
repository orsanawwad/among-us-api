package me.nullicorn.amongus.matchmaker;

import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import me.nullicorn.amongus.api.Protocol;
import me.nullicorn.amongus.api.ProtocolException;
import me.nullicorn.amongus.matchmaker.packet.ClientboundMatchmakerList;
import me.nullicorn.amongus.matchmaker.packet.Disconnect;
import me.nullicorn.amongus.matchmaker.packet.Hearbeat;
import me.nullicorn.amongus.matchmaker.packet.HearbeatAck;
import me.nullicorn.amongus.matchmaker.packet.MatchmakerPacket;
import me.nullicorn.amongus.matchmaker.packet.ServerboundHello;

/**
 * The protocol for communication with Among Us' matchmaking servers
 *
 * @author Nullicorn
 */
public class MatchmakerProtocol implements Protocol<MatchmakerPacket> {

  private Map<Byte, Class<? extends MatchmakerPacket>> packetsById = new HashMap<>();
  private Map<Class<? extends MatchmakerPacket>, Byte> packetIds   = new HashMap<>();

  public MatchmakerProtocol() {
    registerPacketType(0x00, ClientboundMatchmakerList.class);
    registerPacketType(0x08, ServerboundHello.class);
    registerPacketType(0x09, Disconnect.class);
    registerPacketType(0x0A, HearbeatAck.class);
    registerPacketType(0x0C, Hearbeat.class);
  }

  @SneakyThrows
  @Override
  public MatchmakerPacket createPacketInstance(byte packetId) {
    Class<? extends MatchmakerPacket> packetClass = packetsById.get(packetId);
    if (packetClass == null) {
      throw new ProtocolException("No such packet with ID 0x" + String.format("%02x", packetId).toUpperCase());
    }
    return packetClass.getConstructor().newInstance();
  }

  @Override
  public byte getPacketId(MatchmakerPacket packet) {
    return packetIds.get(packet.getClass());
  }

  /**
   * Registers a new packet type with the provided ID and class (for deserialization)
   */
  protected void registerPacketType(int id, Class<? extends MatchmakerPacket> clazz) {
    packetsById.put((byte) id, clazz);
    packetIds.put(clazz, (byte) id);
  }
}
