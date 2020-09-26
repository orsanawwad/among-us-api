package me.nullicorn.amongus.protocol.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import me.nullicorn.amongus.protocol.common.MasterServerMeta;
import me.nullicorn.amongus.protocol.common.api.ServerMeta;
import me.nullicorn.amongus.protocol.common.api.packet.MasterServerList;
import me.nullicorn.amongus.protocol.common.util.ByteBufUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Simple implementation of {@link MasterServerList}
 *
 * @author Nullicorn
 */
public class MasterServerListPacket implements MasterServerList {

  private List<ServerMeta> serverList;


  public MasterServerListPacket() {
    // Required for deserialization
  }

  public MasterServerListPacket(@NotNull List<ServerMeta> serverList) {
    this.serverList = serverList;
  }

  @Override
  public List<ServerMeta> getServers() {
    return serverList;
  }

  @Override
  public void setServers(List<ServerMeta> servers) {
    serverList = servers;
  }

  @Override
  public void serialize(ByteBuf out) {
    out.writeByte(0x01); // Unknown (possibly a boolean)
    out.writeByte(serverList == null ? 0 : serverList.size()); // List size

    if (serverList != null) {
      serverList.forEach(server -> {
        ByteBuf serverOut = Unpooled.buffer();

        try {
          ByteBufUtil.writeUtf8(server.getName(), serverOut); // Server name
          ByteBufUtil.writeAddress(server.getAddress(), serverOut); // Server address
          serverOut.writeShortLE(server.getClientCount()); // # of clients

          out.writeShortLE(serverOut.writerIndex()); // Server data length
          out.writeByte(0x00); // Unknown
          out.writeBytes(serverOut); // Server data
        } finally {
          serverOut.release();
        }
      });
    }
  }

  @Override
  public void deserialize(ByteBuf in) {
    in.skipBytes(1); // Unknown (possibly a boolean)
    int serverCount = in.readUnsignedByte();

    serverList = new ArrayList<>();
    for (int i = 0; i < serverCount; i++) {
      int serverDataLength = in.readUnsignedShortLE();
      in.skipBytes(1); // Unknown

      ByteBuf serverIn = in.readBytes(serverDataLength);
      try {
        String name = ByteBufUtil.readUtf8(serverIn); // Server name
        InetSocketAddress address = ByteBufUtil.readAddress(serverIn); // Server address
        int clientCount = serverIn.readUnsignedShortLE(); // # of clients

        serverList.add(new MasterServerMeta(name, address, clientCount));
      } finally {
        serverIn.release();
      }
    }
  }

  @Override
  public boolean isReliable() {
    return false;
  }

  @Override
  public String toString() {
    return "MasterServerList{" +
        "serverList=" + serverList +
        '}';
  }
}
