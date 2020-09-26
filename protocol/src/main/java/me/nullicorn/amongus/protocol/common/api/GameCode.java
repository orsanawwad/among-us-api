package me.nullicorn.amongus.protocol.common.api;

import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;

/**
 * A unique code used to connect to a game in _Among Us_.
 * <p>
 * The original code for converting V2 codes to and from strings comes from AeonLucid's <a href=https://github.com/AeonLucid/Impostor>Imposter</a>
 * library, which is definitely worth checking out. I definitely would not have figured it out without their code.
 *
 * @author AeonLucid, Nullicorn
 */
public class GameCode {

  /*
  Internal ID for each version
   */
  private static final byte VERSION_1 = 0x00;
  private static final byte VERSION_2 = 0x01;

  /*
  Regular expression to match each version
   */
  private static final String V1_REGEXP = "[A-Z]{4}";
  private static final String V2_REGEXP = "[A-Z]{6}";

  /*
  Mappings for converting between V1 and V2 game codes
   */
  private static final String V2_CHAR_MAPPINGS = "QWXRTYLPESDFGHUJKZOCVBINMA";
  private static final byte[] V2_BYTE_MAPPINGS = new byte[]{
      0x19,
      0x15,
      0x13,
      0x0A,
      0x08,
      0x0B,
      0x0C,
      0x0D,
      0x16,
      0x0F,
      0x10,
      0x06,
      0x18,
      0x17,
      0x12,
      0x07,
      0x00,
      0x03,
      0x09,
      0x04,
      0x0E,
      0x14,
      0x01,
      0x02,
      0x05,
      0x11
  };

  /**
   * The value of this game code stored as an integer
   */
  private final int value;

  /**
   * The version of this game code; currently only 0 or 1 for V1 and V2 respectively
   */
  private final byte version;

  /**
   * @param value The game code encoded as a 32-bit integer (little-endian)
   */
  public GameCode(int value) {
    version = value >= 0
        ? VERSION_1
        : VERSION_2;

    this.value = value;
  }

  /**
   * @param codeStr The stringified version of a game code
   */
  public GameCode(@NotNull String codeStr) {
    codeStr = codeStr.trim().toUpperCase();
    if (codeStr.length() == 4 && codeStr.matches(V1_REGEXP)) {
      value = v1CodeToInt(codeStr);
      version = VERSION_1;

    } else if (codeStr.length() == 6 && codeStr.matches(V2_REGEXP)) {
      value = v2CodeToInt(codeStr);
      version = VERSION_2;

    } else {
      throw new IllegalArgumentException("Invalid game code: " + codeStr);
    }
  }

  /**
   * @see #value
   */
  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (version == VERSION_1) {
      byte[] strBytes = new byte[]{
          (byte) (value >> 24),
          (byte) (value >> 16),
          (byte) (value >> 8),
          (byte) (value)
      };
      return new String(strBytes, StandardCharsets.UTF_8);

    } else if (version == VERSION_2) {
      int msb = value & 0x3FF;
      int lsb = (value >> 10) & 0xFFFFF;
      byte[] strBytes = new byte[]{
          (byte) V2_CHAR_MAPPINGS.charAt(msb % 26),
          (byte) V2_CHAR_MAPPINGS.charAt(msb / 26),
          (byte) V2_CHAR_MAPPINGS.charAt(lsb % 26),
          (byte) V2_CHAR_MAPPINGS.charAt(lsb / 26 % 26),
          (byte) V2_CHAR_MAPPINGS.charAt(lsb / (26 * 26) % 26),
          (byte) V2_CHAR_MAPPINGS.charAt(lsb / (26 * 26 * 26) % 26)
      };
      return new String(strBytes, StandardCharsets.UTF_8);

    } else {
      throw new UnsupportedOperationException("Cannot stringify game code with unsupported version: " + version);
    }
  }

  /**
   * Utility method for converting a V1 code string into an integer (for storage in {@link #value})
   */
  private static int v1CodeToInt(String codeStr) {
    byte[] codeBytes = codeStr.getBytes(StandardCharsets.UTF_8);
    byte b1 = (byte) (codeBytes[0] & 0xFF);
    byte b2 = (byte) (codeBytes[1] & 0xFF);
    byte b3 = (byte) (codeBytes[2] & 0xFF);
    byte b4 = (byte) (codeBytes[3] & 0xFF);
    return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
  }

  /**
   * Utility method for converting a V2 code string into an integer (for storage in {@link #value})
   */
  private static int v2CodeToInt(String codeStr) {
    byte b1 = V2_BYTE_MAPPINGS[codeStr.charAt(0) - 65];
    byte b2 = V2_BYTE_MAPPINGS[codeStr.charAt(1) - 65];
    byte b3 = V2_BYTE_MAPPINGS[codeStr.charAt(2) - 65];
    byte b4 = V2_BYTE_MAPPINGS[codeStr.charAt(3) - 65];
    byte b5 = V2_BYTE_MAPPINGS[codeStr.charAt(4) - 65];
    byte b6 = V2_BYTE_MAPPINGS[codeStr.charAt(5) - 65];

    int msb = (b1 + 26 * b2) & 0x3FF;
    int lsb = (b3 + 26 * (b4 + 26 * (b5 + 26 * b6)));
    return msb | ((lsb << 10) & 0x3FFFFC00) | 0x80000000;
  }
}
