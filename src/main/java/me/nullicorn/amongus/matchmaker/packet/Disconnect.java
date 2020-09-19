package me.nullicorn.amongus.matchmaker.packet;

import io.netty.buffer.ByteBuf;
import java.util.logging.Logger;
import me.nullicorn.amongus.matchmaker.MatchmakerClient;

/**
 * A bidirectional packet that tells the receiver that the sender is disconnecting
 *
 * @author Nullicorn
 */
public class Disconnect implements MatchmakerPacket {

  private static final Logger logger = Logger.getLogger(Disconnect.class.getSimpleName());

  @Override
  public void deserialize(ByteBuf in) {
    // Payload is empty
  }

  @Override
  public void serialize(ByteBuf out) {
    // Payload is empty
  }

  @Override
  public void handle(MatchmakerClient client) {
    if (!client.isConnected()) {
      client.disconnect();
    } else {
      logger.warning("Server sent disconnect packet (0x09) when client was already disconnected");
    }
  }

  @Override
  public String toString() {
    return "Disconnect{}";
  }
}
