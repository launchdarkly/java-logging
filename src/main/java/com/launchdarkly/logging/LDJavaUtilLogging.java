package com.launchdarkly.logging;

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
    // pre-format messages for disabled levels. We'll avoid that by checking if the
    // level is enabled first. It would be simpler to use the overloads of Logger
    // methods that take a Supplier<String> rather than a String-- but, those don't
    // exist in older Android API versions.
    
    @Override
    public void log(LDLogLevel level, Object message) {
      if (isEnabled(level)) {
        logInternal(level, message == null ? "" : message.toString());
      }
    }
    
    @Override
    public void log(LDLogLevel level, String format, Object param) {
      if (isEnabled(level)) {
        logInternal(level, format(format, param));
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      if (isEnabled(level)) {
        logInternal(level, format(format, param1, param2));
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      if (isEnabled(level)) {
        logInternal(level, format(format, params));
      }
    }
    
    private void logInternal(LDLogLevel level, String text) {
      switch (level) {
      case DEBUG:
        logger.fine(text);
        break;
      case INFO:
        logger.info(text);
        break;
      case WARN:
        logger.warning(text);
        break;
      case ERROR:
        logger.severe(text);
        break;
      default:
        break;
      }
    }
  }
}
