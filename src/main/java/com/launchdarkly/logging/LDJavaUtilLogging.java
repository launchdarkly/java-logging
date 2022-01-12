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
    
    @Override
    public void log(LDLogLevel level, String message) {
      switch (level) {
      case DEBUG:
        logger.fine(message);
        break;
      case INFO:
        logger.info(message);
        break;
      case WARN:
        logger.warning(message);
        break;
      case ERROR:
        logger.severe(message);
        break;
      default:
        break;
      }
    }

    // To avoid unnecessary string computations for debug output, we don't want to
    // pre-format messages for disabled levels. We handle that by using the overloads
    // of Logger methods that take a Supplier<String> rather than a String. Note that
    // we're using anonymous classes here instead of a nicer lambda syntax, because
    // lambdas don't work right in Android.
    
    @Override
    public void log(LDLogLevel level, final String format, final Object param) {
      Supplier<String> deferFormat = new Supplier<String>() {
        @Override
        public String get() {
          return format(format, param);
        }
      };
      switch (level) {
      case DEBUG:
        logger.fine(deferFormat);
        break;
      case INFO:
        logger.info(deferFormat);
        break;
      case WARN:
        logger.warning(deferFormat);
        break;
      case ERROR:
        logger.severe(deferFormat);
        break;
      default:
        break;
      }
    }

    @Override
    public void log(LDLogLevel level, final String format, final Object param1, final Object param2) {
      Supplier<String> deferFormat = new Supplier<String>() {
        @Override
        public String get() {
          return format(format, param1, param2);
        }
      };
      switch (level) {
      case DEBUG:
        logger.fine(deferFormat);
        break;
      case INFO:
        logger.info(deferFormat);
        break;
      case WARN:
        logger.warning(deferFormat);
        break;
      case ERROR:
        logger.severe(deferFormat);
        break;
      default:
        break;
      }
    }

    @Override
    public void log(LDLogLevel level, final String format, final Object... params) {
      Supplier<String> deferFormat = new Supplier<String>() {
        @Override
        public String get() {
          return format(format, params);
        }
      };
      switch (level) {
      case DEBUG:
        logger.fine(deferFormat);
        break;
      case INFO:
        logger.info(deferFormat);
        break;
      case WARN:
        logger.warning(deferFormat);
        break;
      case ERROR:
        logger.severe(deferFormat);
        break;
      default:
        break;
      }
    }
  }
}
