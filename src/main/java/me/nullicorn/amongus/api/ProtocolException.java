package me.nullicorn.amongus.api;

/**
 * Thrown when a {@link Protocol} is violated
 *
 * @author Nullicorn
 */
public class ProtocolException extends Exception {

  public ProtocolException() {
    this("An unknown protocol error occurred");
  }

  public ProtocolException(String message) {
    super(message);
  }

  public ProtocolException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProtocolException(Throwable cause) {
    super(cause);
  }

  public ProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
