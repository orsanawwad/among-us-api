package me.nullicorn.amongus.protocol.common.util;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Various utilities related to Netty {@link ByteBuf}s
 *
 * @author Nullicorn
 */
public final class ByteBufUtil {

  /**
   * Read a length-prefixed UTF-8 string from a buffer.
   * <p>
   * The length should be encoded as a single, unsigned byte. The length may also be zero.
   */
  public static String readUtf8(ByteBuf in) {
    int length = in.readUnsignedByte();
    return in.readCharSequence(length, StandardCharsets.UTF_8).toString();
  }

  /**
   * Read an IPv4 address and port from the buffer.
   * <p>
   * The IP address should be 4 bytes long (32 bits) and the port number should be a 2-byte short (little-endian).
   */
  public static InetSocketAddress readAddress(ByteBuf in) {
    String hostStr = in.readUnsignedByte()
        + "." + in.readUnsignedByte()
        + "." + in.readUnsignedByte()
        + "." + in.readUnsignedByte();
    int port = in.readShortLE();
    return new InetSocketAddress(hostStr, port);
  }

  /**
   * Read a length-prefixed UTF-8 string from a buffer.
   * <p>
   * The length should be encoded as a single, unsigned byte. The length may also be zero.
   *
   * @param value The string value to write out
   */
  public static void writeUtf8(String value, ByteBuf out) {
    int length = value == null ? 0 : Math.min(value.length(), Byte.MAX_VALUE);
    if (value != null) {
      value = value.substring(0, length);
    }
    out.writeByte(length);
    out.writeCharSequence(value, StandardCharsets.UTF_8);
  }

  /**
   * Read an IPv4 address and port from the buffer.
   * <p>
   * The IP address should be 4 bytes long (32 bits) and the port number should be a 2-byte short (little-endian).
   *
   * @param value The address to write out
   */
  public static void writeAddress(InetSocketAddress value, ByteBuf out) {
    out.writeBytes(value.getAddress().getAddress());
    out.writeShortLE(value.getPort());
  }

  /**
   * Convert a ByteBuf to a Hex string for logging.
   */
  public static String toLoggableString(ByteBuf buf) {
    if (buf.refCnt() == 0) {
      // Unable to read
      return "{Unable to stringify (refCnt == 0)}";
    }

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

  private ByteBufUtil() {
    // Should not be instantiated
  }
}
