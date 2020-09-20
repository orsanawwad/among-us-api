package me.nullicorn.amongus.io.packet;

import io.netty.buffer.ByteBuf;
import me.nullicorn.amongus.io.BasicAmongUsClient;

/**
 * @author Nullicorn
 */
public class ServerboundDataRequest extends Hearbeat {

  /*
  ========== PACKET STRUCTURE ==========
   - 2 byte: Nonce; any number; must be sent back to the sender in an ACK
   - 2 byte: Length of the payload (little endian)
   - 1 byte: Packet ID (this is a guess)
   - n byte: Payload (length determined by
   */

  /*
  Game List Packet:
  2C 00 10 00 2A 02 {0A} {Language: 2 bytes} 00 00 {Map Bitmask: 1 byte} {00 00 80 3F   00 00 80 3F   00 00 C0 3F   00 00 70 41}
                     ^ Possibly the max number of games to return (10)         ^ Very similar groups of 4 bytes. Look into this
  ^ Remaining packet length minus 2

  - # of Imposters:
      - 0 = Any
      - 1 = 1
      - 2 = 2
      - 3 = 3
  - Maps to Search (Bitmask):
      - 00000001 = The Skeld
      - 00000010 = Mira HQ
      - 00000100 = Polus

   */

  @Override
  public void deserialize(ByteBuf in) {

  }

  @Override
  public void serialize(ByteBuf out) {
    super.serialize(out);
  }

  @Override
  public void handle(BasicAmongUsClient client) {

  }
}
