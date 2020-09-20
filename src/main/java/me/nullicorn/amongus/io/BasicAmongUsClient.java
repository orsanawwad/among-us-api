package me.nullicorn.amongus.io;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;
import java.util.Timer;
import java.util.TimerTask;
import me.nullicorn.amongus.api.io.Protocol;
import me.nullicorn.amongus.io.packet.Acknowledgement;
import me.nullicorn.amongus.io.packet.Disconnect;
import me.nullicorn.amongus.io.packet.Hearbeat;
import me.nullicorn.amongus.io.packet.Login;
import me.nullicorn.amongus.io.packet.UDPPacket;
import me.nullicorn.amongus.io.pipeline.ExceptionHandler;
import me.nullicorn.amongus.io.pipeline.PacketDecoder;
import me.nullicorn.amongus.io.pipeline.PacketEncoder;
import me.nullicorn.amongus.io.pipeline.UDPContentDecoder;

/**
 * A basic client capable of connecting to Among Us servers
 *
 * @author Nullicorn
 */
public class BasicAmongUsClient extends AmongUsUDPClient {

  private static final AmongUsProtocol protocol            = new AmongUsProtocol();
  private static final String          ENCODER             = "encoder";
  private static final String          DECODER             = "decoder";
  private static final String          EXCEPTION_HANDLER   = "exception-handler";
  private static final String          UDP_CONTENT_DECODER = "udp-decoder";
  private static final long            HEARTBEAT_INTERVAL  = 1500L;
  private static final int             MAX_ACKS_MISSED     = 9;

  private final String  username;
  private       Timer   heartbeatTimer;
  private       short   lastSentHeartbeatNonce = 0;
  private       boolean receivedLastAck        = false;
  private       int     acksMissed             = 0;

  public BasicAmongUsClient(String username) {
    this.username = username;
  }

  @Override
  public Protocol<UDPPacket> getProtocol() {
    return protocol;
  }

  @Override
  protected void initChannel(DatagramChannel ch) {
    ch.pipeline().addLast(UDP_CONTENT_DECODER, new UDPContentDecoder());
    ch.pipeline().addLast(DECODER, new PacketDecoder(this));
    ch.pipeline().addLast(ENCODER, new PacketEncoder(this));
    ch.pipeline().addLast(EXCEPTION_HANDLER, new ExceptionHandler());
  }

  @Override
  public ChannelFuture disconnect() {
    if (isConnected()) {
      return sendPacket(new Disconnect()).addListener((future -> super.disconnect()));
    } else {
      return super.disconnect();
    }
  }

  @Override
  protected void onConnected() {
    sendPacket(new Login(lastSentHeartbeatNonce++, username));
    heartbeatTimer = new Timer();
    heartbeatTimer.scheduleAtFixedRate(new HeartbeatTask(), HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL);
  }

  public void onHeartbeatAck(Acknowledgement ack) {
    if (ack.getNonce() == lastSentHeartbeatNonce) {
      receivedLastAck = true;
      logger.finer("Received heartbeat ack");
    }
  }

  @Override
  protected void onDisconnected() {
    heartbeatTimer.cancel();
    logger.fine("Shut down heartbeat task");
  }

  /**
   * A repeating task that ensures that the server is ack'ing the client's {@link Hearbeat}s
   */
  private class HeartbeatTask extends TimerTask {

    @Override
    public void run() {
      if (!receivedLastAck) {
        acksMissed++;
      } else {
        acksMissed = 0;
      }

      if (acksMissed > MAX_ACKS_MISSED) {
        logger.info("Server missed more than " + MAX_ACKS_MISSED + " acks. Disconnecting...");
        cancel();
        disconnect();
      } else {
        sendPacket(new Hearbeat(++lastSentHeartbeatNonce));
      }
    }
  }
}
