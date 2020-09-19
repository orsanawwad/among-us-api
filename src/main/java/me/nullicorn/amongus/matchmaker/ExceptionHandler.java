package me.nullicorn.amongus.matchmaker;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A two-way channel handler for catching exceptions
 *
 * @author Nullicorn
 */
public class ExceptionHandler extends ChannelDuplexHandler {

  private static final Logger logger = Logger.getLogger(ExceptionHandler.class.getSimpleName());

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.log(Level.WARNING, "Exception thrown in pipeline", cause);
  }
}
