package com.launchdarkly.logging;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * A basic logging implementation that formats output and sends it to an arbitrary destination. 
 * <p>
 * Factory methods for this class are {@link Logs#toStream(PrintStream)},
 * {@link Logs#toConsole()}, and {@link Logs#toMethod(LineWriter)}.
 * <p>
 * Currently the output is always in the format "Timestamp [LoggerName] LEVEL: text", or, if a
 * tag is specified, "Timestamp {Tag} [LoggerName] LEVEL: text". The Timestamp format defaults
 * to {@link #getDefaultTimestampFormat()} but can be customized.
 * <p>
 * By itself, this class provides no level filtering. You may use
 * {@link Logs#level(LDLogAdapter, LDLogLevel)} to filter by level, although the LaunchDarkly
 * SDKs also provide their own configuration syntax for this.
 * <p>
 * Example of usage in the server-side Java SDK:
 * 
 * <pre><code>
 *     LDConfig config = new LDConfig.Builder()
 *       .logging(
 *         Components.logging().adapter(Logs.toStream(System.out))
 *           .Level(LDLogLevel.INFO)
 *       )
 *       .build();
 * </code></pre>
 */
public final class SimpleLogging implements LDLogAdapter {
  /**
   * Functional interface for a method or lambda that writes a line of text.
   * <p>
   * {@link SimpleLogging} will format the line of text first and then call this method.
   */
  public static interface LineWriter {
    /**
     * Writes a line of text that has already been formatted.
     * <p>
     * This method must be thread-safe.
     * 
     * @param line a line of text
     */
    void writeLine(String line);
  }
  
  /**
   * The default format for log timestamps. This is a {@code SimpleDateFormat} with the pattern
   * {@code "yyyy-MM-dd HH:mm:ss.SSS zzz"}.
   * <p>
   * Because {@link SimpleDateFormat} is not thread-safe, this method always creates a new
   * instance. We cannot use <code>java.time.DateTimeFormatter</code> because currently we need
   * to support Android API versions that do not have <code>java.time</code>.
   * 
   * @return a date/time format
   */
  public static SimpleDateFormat getDefaultTimestampFormat() {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
    f.setTimeZone(TimeZone.getTimeZone("UTC"));
    return f;
  }

  final LineWriter lineWriter; // exposed for testing
  private final String tag;
  private final DateFormat timestampFormat;
  
  SimpleLogging(LineWriter lineWriter, String tag, DateFormat timestampFormat) {
    this.lineWriter = lineWriter;
    this.tag = tag;
    this.timestampFormat = timestampFormat;
  }
  
  /**
   * Specifies an optional tag string to distinguish this from the output of other loggers.
   * <p>
   * This method does not modify the current instance, but returns a new adapter based on this one.
   * 
   * @param tag a string that should appear in log output, or null for none
   * @return an adapter with the specified configuration
   */
  public SimpleLogging tag(String tag) {
    return new SimpleLogging(this.lineWriter, tag, this.timestampFormat);
  }
  
  /**
   * Specifies the format for date/timestamps. The default is {@link #getDefaultTimestampFormat()}.
   * <p>
   * This method does not modify the current instance, but returns a new adapter based on this one.
   * 
   * @param timestampFormat a {@code DateFormat}, or null to omit the date and time
   * @return an adapter with the specified configuration
   */
  public SimpleLogging timestampFormat(DateFormat timestampFormat) {
    return new SimpleLogging(this.lineWriter, this.tag, timestampFormat);
  }
  
  @Override
  public Channel newChannel(String name) {
    return new ChannelImpl(name);
  }
  
  private final class ChannelImpl implements Channel {
    private final String name;
    
    ChannelImpl(String name) {
      this.name = name;
    }

    @Override
    public boolean isEnabled(LDLogLevel level) {
      return true;
    }
    
    @Override
    public void log(LDLogLevel level, Object message) {
      print(level, message == null ? "" : message.toString());
    }

    @Override
    public void log(LDLogLevel level, String format, Object param) {
      print(level, SimpleFormat.format(format, param));
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      print(level, SimpleFormat.format(format, param1, param2));
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      print(level, SimpleFormat.format(format, params));
    }
    
    private void print(LDLogLevel level, String message) {
      StringBuilder s = new StringBuilder();
      if (timestampFormat != null) {
        // Unfortunately, SimpleDateFormat is not thread-safe
        DateFormat clonedFormat = (DateFormat)timestampFormat.clone();
        s.append(clonedFormat.format(new Date())).append(" ");
      }
      if (tag != null && !tag.isEmpty()) {
        s.append("{").append(tag).append("} ");
      }
      s.append("[").append(name).append("] ").append(level).append(": ").append(message);
      lineWriter.writeLine(s.toString());
    }
  }
}
