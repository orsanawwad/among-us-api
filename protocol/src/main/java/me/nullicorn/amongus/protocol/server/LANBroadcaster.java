package me.nullicorn.amongus.protocol.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import me.nullicorn.amongus.protocol.client.api.AmongUsClient;
import me.nullicorn.amongus.protocol.server.api.AmongUsServer;

/**
 * Used to let other _Among Us_ clients on a LAN know that an _Among Us_ game is available to join on the current machine.
 *
 * @author Nullicorn
 */
@Sharable
public class LANBroadcaster extends ChannelInboundHandlerAdapter {

  /**
   * How often (in milliseconds) to send a broadcast message
   */
  private static final long BROADCAST_FREQUENCY = 1000;

  /**
   * The string used in the broadcast message to indicate that the server is open to new connections
   */
  private static final String OPEN_MSG = "Open";

  private final AmongUsServer server;
  private final AmongUsClient host;

  private final Bootstrap bootstrap;
  private       Channel   channel;

  private Timer     broadcastTimer;
  private TimerTask broadcastTask;

  /**
   * @param server The LAN server to broadcast information for
   * @param host   The host of the LAN server (their username is displayed in the server list)
   */
  public LANBroadcaster(AmongUsServer server, AmongUsClient host) {
    this.server = server;
    this.host = host;

    this.broadcastTimer = new Timer("au-lan-broadcast-timer");

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    this.bootstrap = new Bootstrap()
        .group(bossGroup)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(this);
  }

  /**
   * @return Whether or not this instance is currently sending out broadcast messages
   */
  public boolean isRunning() {
    return channel != null && channel.isOpen();
  }

  /**
   * Begin broadcasting to other devices on the local network
   */
  public void start() {
    if (isRunning()) {
      stop();
    }

    ChannelFuture f = bootstrap.connect(new InetSocketAddress("255.255.255.255", 47777)).syncUninterruptibly();
    this.channel = f.channel();

    // Start broadcasting
    broadcastTask = new BroadcastTask();
    broadcastTimer.scheduleAtFixedRate(broadcastTask, 0, BROADCAST_FREQUENCY);
  }

  /**
   * Stops broadcasting
   */
  public void stop() {
    if (isRunning()) {
      channel.close().syncUninterruptibly();
    }
    if (broadcastTask != null) {
      broadcastTask.cancel();
    }
    channel = null;
  }

  /**
   * Repeating task that sends the actual broadcast message
   */
  private class BroadcastTask extends TimerTask {

    @Override
    public void run() {
      channel.writeAndFlush(createBroadcastMessage());
    }

    /**
     * Create a new broadcast message. This will be received by other _Among Us_ clients on the LAN that are looking for a local game.
     *
     * @return A new ByteBuf that can be broadcast to the LAN
     */
    private ByteBuf createBroadcastMessage() {
      StringBuilder b = new StringBuilder();
      b.append(host.getUsername());
      b.append('~');
      b.append(OPEN_MSG);
      b.append('~');
      b.append(server.getClientCount());
      b.append('~');

      ByteBuf out = Unpooled.buffer(2 + b.length());
      out.writeShortLE(516);
      out.writeCharSequence(b, StandardCharsets.UTF_8);
      return out;
    }
  }
}