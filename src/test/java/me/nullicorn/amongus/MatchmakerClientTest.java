package me.nullicorn.amongus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;

/**
 * Used for debugging and testing of {@link MatchmakerClient}
 *
 * @author Nullicorn
 */
public class MatchmakerClientTest {

  // Europe Matchmaking Server (Master-1): 50.116.1.42:22023
  // Asia Matchmaking Server (Asia-Master-1): 139.162.111.196:22023

  private static final SocketAddress SERVER_ASIA_1   = new InetSocketAddress("139.162.111.196", 22023);
  private static final SocketAddress SERVER_EUROPE_1 = new InetSocketAddress("50.116.1.42", 22023);

  private static final String USERNAME = "Nullicorn";

  public static void main(String[] args) throws InterruptedException, IOException {
    // Setup test logger
    Logger.getGlobal().setLevel(Level.FINEST);
    LogManager.getLogManager().readConfiguration(MatchmakerClientTest.class.getClassLoader().getResourceAsStream("logging.properties"));

    // Create & start the client
    new MatchmakerClient(USERNAME).connect(SERVER_EUROPE_1);
  }
}
