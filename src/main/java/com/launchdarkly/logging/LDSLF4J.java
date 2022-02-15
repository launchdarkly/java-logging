package com.launchdarkly.logging;

import org.slf4j.LoggerFactory;

/**
 * An adapter for redirecting LaunchDarkly log output to SLF4J.
 * <p>
 * This is implemented as a separate public class with the factory method
 * {@link LDSLF4J#adapter()}, rather than as a method of {@link Logs}, so that the rest
 * of the {@code com.launchdarkly.logging} package can be used even if SLF4J is not in
 * the classpath. Any application or library that uses this method is responsible for
 * making sure SLF4J is in the classpath; it is not listed as a transitive dependency
 * of this library.
 * <p>
 * With this integration, SLF4J is responsible for all other configuration in terms of
 * specifying where the output actually goes and what log levels to enable. See SLF4J
 * documentation for details.
 */
public final class LDSLF4J {
  private LDSLF4J() {}
  
  /**
   * Provides the {@link LDLogAdapter} implementation for sending log output to SLF4J.
   * <p>
   * There are no configuration methods on the returned adapter, because SLF4J's
   * behavior is controlled by its own configuration file and/or methods.
   *  
   * @return the log adapter
   */
  public static LDLogAdapter adapter() {
    return AdapterImpl.INSTANCE;
  }

  private static final class AdapterImpl implements LDLogAdapter {
    private static final AdapterImpl INSTANCE = new AdapterImpl();

    @Override
    public Channel newChannel(String name) {
      return new ChannelImpl(LoggerFactory.getLogger(name));
    }
  }
  
  private static final class ChannelImpl implements LDLogAdapter.Channel {
    private final org.slf4j.Logger logger;
    
    ChannelImpl(org.slf4j.Logger logger) {
      this.logger = logger;
    }
    
    @Override
    public boolean isEnabled(LDLogLevel level) {
      switch (level) {
      case DEBUG:
        return logger.isDebugEnabled();
      case INFO:
        return logger.isInfoEnabled();
      case WARN:
        return logger.isWarnEnabled();
      default:
        return logger.isErrorEnabled();
      }
    }
    
    @Override
    public void log(LDLogLevel level, Object message) {
      if (isEnabled(level)) {
        String s = message == null ? "" : message.toString();
        switch (level) {
        case DEBUG:
          logger.debug(s);
          break;
        case INFO:
          logger.info(s);
          break;
        case WARN:
          logger.warn(s);
          break;
        case ERROR:
          logger.error(s);
          break;
        default:
          break;
        }
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param) {
      switch (level) {
      case DEBUG:
        logger.debug(format, param);
        break;
      case INFO:
        logger.info(format, param);
        break;
      case WARN:
        logger.warn(format, param);
        break;
      case ERROR:
        logger.error(format, param);
        break;
      default:
        break;
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      switch (level) {
      case DEBUG:
        logger.debug(format, param1, param2);
        break;
      case INFO:
        logger.info(format, param1, param2);
        break;
      case WARN:
        logger.warn(format, param1, param2);
        break;
      case ERROR:
        logger.error(format, param1, param2);
        break;
      default:
        break;
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      switch (level) {
      case DEBUG:
        logger.debug(format, params);
        break;
      case INFO:
        logger.info(format, params);
        break;
      case WARN:
        logger.warn(format, params);
        break;
      case ERROR:
        logger.error(format, params);
        break;
      default:
        break;
      }
    }      
  }
}
