package com.launchdarkly.logging;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.launchdarkly.logging.SimpleFormat.format;

/**
 * See {@link Logs#toJavaUtilLogging()}.
 */
final class LDJavaUtilLogging implements LDLogAdapter {
  static final LDLogAdapter INSTANCE = new LDJavaUtilLogging();
  
  private LDJavaUtilLogging() {}
  
  @Override
  public Channel newChannel(String name) {
    return new ChannelImpl(Logger.getLogger(name));
  }
  
  private static final class ChannelImpl implements Channel {
    private final java.util.logging.Logger logger;
    
    ChannelImpl(java.util.logging.Logger logger) {
      this.logger = logger;
    }
    
    @Override
    public boolean isEnabled(LDLogLevel level) {
      switch (level) {
      case DEBUG:
        return logger.isLoggable(Level.FINE);
      case INFO:
        return logger.isLoggable(Level.INFO);
      case WARN:
        return logger.isLoggable(Level.WARNING);
      default:
        return logger.isLoggable(Level.SEVERE);
      }
    }
    
    // To avoid unnecessary string computations for debug output, we don't want to
    // pre-format messages for disabled levels. We handle that by using the overloads
    // of Logger methods that take a Supplier<String> rather than a String.
    
    @Override
    public void log(LDLogLevel level, Object message) {
      logInternal(level, () -> message == null ? "" : message.toString());
    }
    
    @Override
    public void log(LDLogLevel level, String format, Object param) {
      logInternal(level, () -> format(format, param));
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      logInternal(level, () -> format(format, param1, param2));
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      logInternal(level, () -> format(format, params));
    }
    
    private void logInternal(LDLogLevel level, Supplier<String> lazyString) {
      switch (level) {
      case DEBUG:
        logger.fine(lazyString);
        break;
      case INFO:
        logger.info(lazyString);
        break;
      case WARN:
        logger.warning(lazyString);
        break;
      case ERROR:
        logger.severe(lazyString);
        break;
      default:
        break;
      }
    }
  }
}
