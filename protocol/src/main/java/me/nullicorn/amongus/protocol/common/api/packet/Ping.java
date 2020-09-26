package me.nullicorn.amongus.protocol.common.api.packet;

/**
 * A Ping packet sent to make sure that the receiver is still listening and responding to packets. Like reliable packets, this should be responded to
 * with a {@link Acknowledgement}
 *
 * @author Nullicorn
 */
public interface Ping extends Packet {
  // Nothing special here ¯\_(ツ)_/¯
}
