package me.nullicorn.amongus.protocol.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import me.nullicorn.amongus.protocol.client.api.RunnableProtocolClient;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;
import me.nullicorn.amongus.protocol.common.pipeline.PacketDecoder;
import me.nullicorn.amongus.protocol.common.pipeline.PacketEncoder;
import me.nullicorn.amongus.protocol.common.pipeline.reliable.ReliablePacketHandler;
import org.jetbrains.annotations.NotNull;

/**
 * A simple protocol client that uses UDP
 *
 * @author Nullicorn
 */
public abstract class UdpProtocolClient implements RunnableProtocolClient {

  private   EventLoopGroup        bossGroup;
  private   ReliablePacketHandler reliablePacketHandler;
  protected Channel               channel;

  /**
   * Called when the client first connects to the server
   */
  protected abstract void onStart();

  /**
   * Called when the client disconnects from the server
   */
  protected abstract void onStop();

  /**
   * Called before a packet is sent to the server.
   * <p>
   * If this method returns false, the packet will not be sent. To send all packets, always return true.
   *
   * @param packet The packet that will be sent
   * @return Whether or not to send the packet (e.g. `false` means the packet will not be sent)
   */
  protected abstract boolean onPacketSend(Packet packet);

  /**
   * Called when the server sends a packet to the client
   */
  protected abstract void onPacketReceived(Packet packet);

  /**
   * Called when an error occurs while handling a packet
   */
  protected abstract void onException(Throwable cause);

  /**
   * Starts the client and connects it to the provided address
   */
  @Override
  public final void start(@NotNull InetSocketAddress serverAddr) {
    if (isRunning()) {
      stop();
    }

    bossGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap()
        .group(bossGroup)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_BROADCAST, true)
        .handler(new UdpClientInitializer());

    reliablePacketHandler = new ReliablePacketHandler();
    reliablePacketHandler.start();

    bootstrap.connect(serverAddr).addListener(future -> {
      if (!future.isSuccess()) {
        onException(future.cause());
      } else {
        channel = ((ChannelFuture) future).channel();
      }
    });
  }

  /**
   * Stops the client and disconnects it from the server
   */
  @Override
  public final void stop() {
    if (reliablePacketHandler != null) {
      reliablePacketHandler.stop();
      reliablePacketHandler = null;
    }

    if (isRunning()) {
      channel.close().syncUninterruptibly();
      bossGroup.shutdownGracefully();
    }
    channel = null;
  }

  /**
   * @return Whether or not the client is connected to a server
   */
  @Override
  public final boolean isRunning() {
    return channel != null && channel.isActive();
  }

  /**
   * Send a packet to the server that this client is connected to
   */
  protected void sendPacket(@NotNull Packet packet) {
    if (onPacketSend(packet)) {
      channel.writeAndFlush(packet).addListener(future -> {
        if (!future.isSuccess()) {
          onException(future.cause());
        }
      });
    } else {
      onException(new IllegalStateException("Cannot send packet while client is not running"));
    }
  }

  /**
   * Initializes the channel pipeline for {@link UdpProtocolClient}s
   */
  private class UdpClientInitializer extends ChannelInitializer<DatagramChannel> {

    @Override
    protected void initChannel(DatagramChannel ch) {
      // Disconnect automatically after 30 seconds of silence
      ch.pipeline().addLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS));

      // Inbound: Decode the content of UDP packets (DatagramPacket -> Packet)
      ch.pipeline().addLast(new DatagramPacketDecoder(new PacketDecoder()));

      // Outbound: Encode packet (Packet -> ByteBuf)
      ch.pipeline().addLast(new PacketEncoder());

      // Bidirectional: Ack' reliable packets; resend un-ack'ed packets; listen for inbound ack's (Packet -> Packet)
      ch.pipeline().addLast(reliablePacketHandler);

      // Inbound: Handle packets (Packet -> Packet)
      ch.pipeline().addLast(new UdpInboundHandler(UdpProtocolClient.this));
    }
  }
}
