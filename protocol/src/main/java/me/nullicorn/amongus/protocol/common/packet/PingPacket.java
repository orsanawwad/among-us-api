package me.nullicorn.amongus.protocol.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import me.nullicorn.amongus.protocol.common.api.packet.Ping;

/**
 * Simple implementation of {@link Ping}
 *
 * @author Nullicorn
 */
@NoArgsConstructor
public class PingPacket implements Ping {

  @Override
  public void serialize(ByteBuf out) {
    // No payload
  }

  @Override
  public void deserialize(ByteBuf in) {
    // No payload
  }

  @Override
  public boolean isReliable() {
    return true;
  }

  @Override
  public String toString() {
    return "PingPacket{}";
  }
}
