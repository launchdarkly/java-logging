package com.launchdarkly.logging;

/**
 * See {@link Logs#toMultiple(LDLogAdapter...)}.
 */
final class MultiLogging implements LDLogAdapter {
  private final LDLogAdapter[] adapters;
    
  MultiLogging(LDLogAdapter[] adapters) {
    this.adapters = new LDLogAdapter[adapters.length];
    System.arraycopy(adapters, 0, this.adapters, 0, adapters.length);
  }

  @Override
  public Channel newChannel(String name) {
    Channel[] channels = new Channel[adapters.length];
    for (int i = 0; i < adapters.length; i++) {
      channels[i] = adapters[i].newChannel(name);
    }
    return new ChannelImpl(channels);
  }

  private static final class ChannelImpl implements Channel {
    private final Channel[] channels;
    
    ChannelImpl(Channel[] channels) {
      this.channels = channels;
    }

    @Override
    public boolean isEnabled(LDLogLevel level) {
      for (Channel c: channels) {
        if (c.isEnabled(level)) {
          return true;
        }
      }
      return false;
    }
    
    @Override
    public void log(LDLogLevel level, String message) {
      for (Channel c: channels) {
        c.log(level, message);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param) {
      for (Channel c: channels) {
        c.log(level, format, param);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      for (Channel c: channels) {
        c.log(level, format, param1, param2);
      }
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      for (Channel c: channels) {
        c.log(level, format, params);
      }
    }
  }
}
