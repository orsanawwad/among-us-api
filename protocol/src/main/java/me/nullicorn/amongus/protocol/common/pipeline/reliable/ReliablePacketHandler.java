package me.nullicorn.amongus.protocol.common.pipeline.reliable;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import me.nullicorn.amongus.protocol.common.api.packet.Acknowledgement;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;
import me.nullicorn.amongus.protocol.common.packet.AcknowledgementPacket;
import me.nullicorn.amongus.protocol.common.packet.DisconnectPacket;

/**
 * @author Nullicorn
 */
public class ReliablePacketHandler extends ChannelDuplexHandler {

  private final long                         resendFrequency;
  private final int                          maxSendAttempts;
  private final Int2ObjectMap<PendingPacket> pendingPackets;
  private       Timer                        timer;
  private final AtomicInteger                nextReliableId;

  // TODO: 9/26/20 Add a repeating task to send Ping packets at even intervals. Currently, we're only listening for them

  /**
   * @see #ReliablePacketHandler(long, int)
   */
  public ReliablePacketHandler() {
    this(1500, 10);
  }

  /**
   * @param resendFrequency How often (in milliseconds) packets should be resent if they are not acknowledged
   * @param maxSendAttempts The maximum number of times that a packet will be sent (without being acknowledged) before it is assumed that the client
   *                        has disconnected
   */
  public ReliablePacketHandler(long resendFrequency, int maxSendAttempts) {
    this.resendFrequency = resendFrequency;
    this.maxSendAttempts = maxSendAttempts;

    this.pendingPackets = Int2ObjectMaps.synchronize(new Int2ObjectOpenHashMap<>());
    this.timer = new Timer();
    this.nextReliableId = new AtomicInteger(0);
  }

  public void start() {
    timer = new Timer();
  }

  public void stop() {
    if (timer != null) {
      timer.cancel();
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof Acknowledgement) {
      handleAcknowledgement(((Acknowledgement) msg).getOriginalId());

    } else if (msg instanceof PendingPacket) {
      // Acknowledge inbound reliable packets
      PendingPacket reliablePacket = (PendingPacket) msg;
      ctx.writeAndFlush(new AcknowledgementPacket(reliablePacket.getId()));
      msg = reliablePacket.getActualPacket();
    }

    ctx.fireChannelRead(msg);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    Packet packet = (Packet) msg;
    if (packet.isReliable()) {
      sendUntilAcknowledged(packet, ctx);
    } else {
      ctx.writeAndFlush(msg);
    }
  }

  /**
   * Send a packet at even intervals until the receiver acknowledges that they have received it
   */
  private void sendUntilAcknowledged(Packet packet, ChannelHandlerContext ctx) {
    PendingPacket pending = packet instanceof PendingPacket
        ? (PendingPacket) packet
        : new PendingPacket(packet, nextReliableId.getAndIncrement());
    pendingPackets.put(pending.getId(), pending);

    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        // If the packet was acknowledged, remove it from the unack'd list and end this repeating task
        if (pending.isAcknowledged().get()) {
          cancel();
          pendingPackets.remove(pending.getId());
          return;
        }

        // If the packet has been sent too many times, disconnect the client for being unresponsive
        if (pending.getTimesSent().get() >= maxSendAttempts) {
          ctx.writeAndFlush(new DisconnectPacket("Reliable packet " + pending.getId() + " not ack'd after " + (maxSendAttempts - 1) + " resends"));
          return;
        }

        // Send the packet
        pending.getTimesSent().getAndIncrement();
        ctx.writeAndFlush(pending);
      }
    }, 0, resendFrequency);
  }

  /**
   * Called when an acknowledgement packet has been received
   */
  private void handleAcknowledgement(int id) {
    PendingPacket pending = pendingPackets.remove(id);
    if (pending != null) {
      pending.isAcknowledged().set(true);
    }
  }
}
