package me.nullicorn.amongus.protocol.common.pipeline.reliable;

import io.netty.buffer.ByteBuf;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.nullicorn.amongus.protocol.common.api.packet.Acknowledgement;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;

/**
 * A packet that is waiting to be acknowledged (via an {@link Acknowledgement})
 *
 * @author Nullicorn
 */
public class PendingPacket implements Packet {

  /**
   * The actual packet awaiting acknowledgement
   */
  private final Packet actualPacket;

  /**
   * The unique ID for this packet
   */
  private final int id;

  /**
   * The number of times this packet has been sent without being acknowledged
   */
  private AtomicInteger timesSent = new AtomicInteger(0);

  /**
   * Whether or not this packet has been acknowledged
   */
  private AtomicBoolean acknowledged;

  public PendingPacket(Packet actualPacket, int id) {
    this.actualPacket = actualPacket;
    this.id = id;
    this.acknowledged = new AtomicBoolean(false);
  }

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

  /**
   * @see #actualPacket
   */
  public Packet getActualPacket() {
    return actualPacket;
  }

  /**
   * @see #id
   */
  public int getId() {
    return id;
  }

  /**
   * @see #timesSent
   */
  public AtomicInteger getTimesSent() {
    return timesSent;
  }

  /**
   * @see #isAcknowledged()
   */
  public AtomicBoolean isAcknowledged() {
    return acknowledged;
  }
}