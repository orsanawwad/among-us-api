package me.nullicorn.amongus.matchmaker;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramChannel;
import java.util.Timer;
import java.util.TimerTask;
import me.nullicorn.amongus.AmongUsUDPClient;
import me.nullicorn.amongus.api.Protocol;
import me.nullicorn.amongus.matchmaker.packet.Disconnect;
import me.nullicorn.amongus.matchmaker.packet.Hearbeat;
import me.nullicorn.amongus.matchmaker.packet.HearbeatAck;
import me.nullicorn.amongus.matchmaker.packet.MatchmakerPacket;
import me.nullicorn.amongus.matchmaker.packet.ServerboundHello;

/**
 * A client capable of connecting to Among Us' matchmaking servers
 *
 * @author Nullicorn
 */
public class MatchmakerClient extends AmongUsUDPClient {

  private static final MatchmakerProtocol protocol            = new MatchmakerProtocol();
  private static final String             ENCODER             = "encoder";
  private static final String             DECODER             = "decoder";
  private static final String             EXCEPTION_HANDLER   = "exception-handler";
  private static final String             UDP_CONTENT_DECODER = "udp-decoder";
  private static final long               HEARTBEAT_INTERVAL  = 1500L;
  private static final int                MAX_ACKS_MISSED     = 9;

  private final String  username;
  private       Timer   heartbeatTimer;
  private       short   lastSentHeartbeatNonce = 0;
  private       boolean receivedLastAck        = false;
  private       int     acksMissed             = 0;

  public MatchmakerClient(String username) {
    this.username = username;
  }

  @Override
  public Protocol<MatchmakerPacket> getProtocol() {
    return protocol;
  }

  @Override
  protected void initChannel(DatagramChannel ch) {
    ch.pipeline().addLast(UDP_CONTENT_DECODER, new UDPContentDecoder());
    ch.pipeline().addLast(DECODER, new MMPacketDecoder(this));
    ch.pipeline().addLast(ENCODER, new MMPacketEncoder(this));
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
    sendPacket(new ServerboundHello(lastSentHeartbeatNonce++, username));
    heartbeatTimer = new Timer();
    heartbeatTimer.scheduleAtFixedRate(new HeartbeatTask(), HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL);
  }

  public void onHeartbeatAck(HearbeatAck ack) {
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
