package com.launchdarkly.logging;

class LevelFilter implements LDLogAdapter {
  final LDLogAdapter wrappedAdapter; // exposed for testing
  private final LDLogLevel enableLevel;
  
  public LevelFilter(LDLogAdapter wrappedAdapter, LDLogLevel enableLevel) {
    this.wrappedAdapter = wrappedAdapter;
    this.enableLevel = enableLevel == null ? LDLogLevel.DEBUG : enableLevel;
  }

  @Override
  public Channel newChannel(String name) {
    return new ChannelImpl(wrappedAdapter.newChannel(name));
  }
  
  private class ChannelImpl implements Channel {
    private final Channel wrappedChannel;
    
    public ChannelImpl(Channel wrappedChannel) {
      this.wrappedChannel = wrappedChannel;
    }
    
    @Override
    public boolean isEnabled(LDLogLevel level) {
      return enableLevel.compareTo(level) <= 0 && wrappedChannel.isEnabled(level);
    }
    
    @Override
    public void log(LDLogLevel level, Object message) {
      if (isEnabled(level)) {
        wrappedChannel.log(level, message);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param) {
      if (isEnabled(level)) {
        wrappedChannel.log(level, format, param);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      if (isEnabled(level)) {
        wrappedChannel.log(level, format, param1, param2);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      if (isEnabled(level)) {
        wrappedChannel.log(level, format, params);
      }
    }
  }
}
