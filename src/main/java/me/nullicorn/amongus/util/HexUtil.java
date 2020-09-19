package me.nullicorn.amongus.util;

import io.netty.buffer.ByteBuf;

/**
 * @author Nullicorn
 */
public final class HexUtil {

  private HexUtil() {
    // Should not be instantiated
  }

  /**
   * Convert a ByteBuf to a hex string. The ByteBuf is copied so that the original may still be read from
   */
  public static String byteBufToHex(ByteBuf buf) {
    ByteBuf copy = buf.copy();
    try {
      StringBuilder sb = new StringBuilder();
      sb.append('{');

      while (copy.isReadable()) {
        sb.append(String.format("%02x", copy.readByte()).toUpperCase());
        if (copy.isReadable()) {
          sb.append(' ');
        }
      }

      sb.append('}');
      return sb.toString();
    } finally {
      copy.release();
    }
  }
}
