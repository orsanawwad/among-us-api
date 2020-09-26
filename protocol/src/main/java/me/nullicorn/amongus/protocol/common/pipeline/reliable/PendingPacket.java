package me.nullicorn.amongus.protocol.common.pipeline.reliable;

import io.netty.buffer.ByteBuf;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import me.nullicorn.amongus.protocol.common.api.packet.Acknowledgement;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;

/**
 * A packet that is waiting to be acknowledged (via an {@link Acknowledgement})
 *
 * @author Nullicorn
 */
@RequiredArgsConstructor
public class PendingPacket implements Packet {

  /**
   * The actual packet awaiting acknowledgement
   */
  @Getter
  private final Packet actualPacket;

  /**
   * The unique ID for this packet
   */
  @Getter
  private final int id;

  /**
   * The number of times this packet has been sent without being acknowledged
   */
  @Getter
  private AtomicInteger timesSent = new AtomicInteger(0);

  /**
   * Whether or not this packet has been acknowledged
   */
  @Getter(onMethod_ = {@Synchronized})
  @Setter(onMethod_ = {@Synchronized})
  private boolean acknowledged;

  @Override
  public void serialize(ByteBuf out) {
    actualPacket.serialize(out);
  }

  @Override
  public void deserialize(ByteBuf in) {
    actualPacket.deserialize(in);
  }

  @Override
  public boolean isReliable() {
    return true;
  }
}