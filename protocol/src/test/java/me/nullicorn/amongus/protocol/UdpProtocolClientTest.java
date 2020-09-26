package me.nullicorn.amongus.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import me.nullicorn.amongus.protocol.client.UdpProtocolClient;
import me.nullicorn.amongus.protocol.common.api.packet.Disconnect;
import me.nullicorn.amongus.protocol.common.api.packet.MasterServerList;
import me.nullicorn.amongus.protocol.common.api.packet.Packet;
import me.nullicorn.amongus.protocol.common.packet.HelloPacket;
import org.jetbrains.annotations.NotNull;

/**
 * A simple UdpProtocolClient for testing the default protocol impl
 *
 * @author Nullicorn
 */
public class UdpProtocolClientTest extends UdpProtocolClient {

  private static final Logger logger = Logger.getLogger(UdpProtocolClientTest.class.getSimpleName());

  public static void main(String[] args) throws IOException {
    // Init logger
    InputStream stream = UdpProtocolClientTest.class.getClassLoader().getResourceAsStream("logging.properties");
    LogManager.getLogManager().readConfiguration(stream);

    // Start client
    String username = "PlayerName";
    new UdpProtocolClientTest(username).start(new InetSocketAddress("50.116.1.42", 22023));
  }

  private final String username;

  public UdpProtocolClientTest(@NotNull String username) {
    this.username = username;
  }

  @Override
  protected void onStart() {
    logger.info("Starting client");
    sendPacket(new HelloPacket(username));
  }

  @Override
  protected void onStop() {
    logger.info("Stopping client...");
  }

  @Override
  protected boolean onPacketSend(Packet packet) {
    logger.info("Sending packet: " + packet);
    return true;
  }

  @Override
  protected void onPacketReceived(Packet packet) {
    logger.info("Received packet: " + packet.toString());

    if (packet instanceof Disconnect) {
      Disconnect disconnect = (Disconnect) packet;
      logger.info(String.format("Client was disconnected by server: 0x%02X - %s", disconnect.getReason(),
          disconnect.hasMessage()
              ? disconnect.getMessage()
              : "(No message provided)"));
      stop();

    } else if (packet instanceof MasterServerList) {
      logger.info("========== MASTER SERVERS ==========");
      ((MasterServerList) packet).getServers().forEach(serverMeta -> {
        logger.info(serverMeta.getName() + ' '
            + '(' + serverMeta.getAddress() + ')'
            + " - " + serverMeta.getClientCount() + " players connected");
      });
      logger.info("====================================");
      stop();
    }
  }

  @Override
  protected void onException(Throwable cause) {
    logger.log(Level.SEVERE, "Error in pipeline: " + cause.getClass().getSimpleName(), cause);
    if (cause instanceof SocketException) {
      stop();
    }
  }
}
