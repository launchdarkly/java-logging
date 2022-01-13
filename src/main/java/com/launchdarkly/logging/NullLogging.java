package com.launchdarkly.logging;

/**
 * See {@link Logs#none()}.
 */
final class NullLogging implements LDLogAdapter {
  static NullLogging INSTANCE = new NullLogging();
  
  private NullLogging() {}
  
  @Override
  public Channel newChannel(String name) {
    return ChannelImpl.INSTANCE;
  }
  
  private static final class ChannelImpl implements Channel {
    static ChannelImpl INSTANCE = new ChannelImpl();

    @Override
    public boolean isEnabled(LDLogLevel level) {
      return false;
    }
    
    @Override
    public void log(LDLogLevel level, Object message) {}

    @Override
    public void log(LDLogLevel level, String format, Object param) {}

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {}

    @Override
    public void log(LDLogLevel level, String format, Object... params) {}
  }
}
