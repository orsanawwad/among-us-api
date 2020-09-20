package me.nullicorn.amongus.io;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.nullicorn.amongus.api.io.AmongUsClient;
import me.nullicorn.amongus.api.io.Packet;

/**
 * A UDP client for connecting to Among Us servers
 *
 * @author Nullicorn
 */
public abstract class AmongUsUDPClient extends ChannelInitializer<DatagramChannel> implements AmongUsClient {

  protected static final Logger logger = Logger.getLogger(AmongUsUDPClient.class.getSimpleName());

  /**
   * The client's underlying channel
   */
  protected Channel channel;

  /**
   * Called once a connection is established between the client and server
   */
  protected abstract void onConnected();

  /**
   * Called once the client has disconnected from the server
   */
  protected abstract void onDisconnected();

  @Override
  protected abstract void initChannel(DatagramChannel ch) throws Exception;

  @Override
  public boolean isConnected() {
    return channel != null && channel.isActive();
  }

  @Override
  public void connect(SocketAddress addr) throws InterruptedException {
    if (isConnected()) {
      throw new IllegalStateException("Client is already connected to a server");
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap()
          .group(bossGroup)
          .channel(NioDatagramChannel.class)
          .option(ChannelOption.SO_BROADCAST, true)
          .handler(this);

      // Connect to the server
      ChannelFuture f = b.connect(addr).sync();
      this.channel = f.channel();

      onConnected();

      // Wait until the connection is closed
      f.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
    }
  }

  @Override
  public ChannelFuture disconnect() {
    if (!isConnected()) {
      throw new IllegalStateException("Client is already disconnected");
    }

    // Call disconnect handler
    return channel.close().addListener((GenericFutureListener<Future<Object>>) ignored -> onDisconnected());
  }

  @Override
  public ChannelFuture sendPacket(Packet<?> packet) {
    if (!isConnected()) {
      throw new IllegalStateException("Cannot send message to server: client is disconnected");
    }

    // Write & listen for errors
    return channel.writeAndFlush(packet).addListener((GenericFutureListener<Future<Object>>) future -> {
      if (!future.isSuccess()) {
        logger.log(Level.WARNING, "Failed to write data", future.cause());
      }
    });
  }
}
