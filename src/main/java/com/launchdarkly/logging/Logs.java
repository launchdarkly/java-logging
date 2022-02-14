package com.launchdarkly.logging;

import java.io.PrintStream;

/**
 * Factory methods for the basic logging implementations in this package.
 * <p>
 * See {@link LDLogAdapter} for more about how {@code com.launchdarkly.logging} works with
 * different implementations of logging. The methods and properties in {@link Logs} provide
 * easy access to basic behaviors like logging to the console or to a file, or capturing log
 * output for testing; if you need to direct the log output to another logging framework that
 * your application is using, you will use an {@link LDLogAdapter} implementation specific
 * to that framework instead.
 */
public abstract class Logs {
  private Logs() {}
  
  /**
   * A stub that generates no log output.
   * 
   * @return a no-op log adapter that
   */
  public static LDLogAdapter none() {
    return NullLogging.INSTANCE;
  }
  
  /**
   * Disables log output below the specified level.
   * <p>
   * This is a decorator that can be applied to any {@link LDLogAdapter}, either one of
   * the standard ones available in {@link Logs} or a custom implementation. Any log
   * messages for a lower level will be immediately discarded; all others will be forwarded to
   * the underlying logging implementation (which may also have other filtering rules of its
   * own).
   * <pre><code>
   *     // This one will write all log messages to System.err, including Debug messages
   *     LDLogAdapter unfilteredLogging = Logs.toConsole();
   *     
   *     // This one will write only WARN and ERROR messages
   *     LDLogAdapter filteredLogging = Logs.level(Logs.toConsole(), LDLogLevel.WARN);
   * </code></pre>
   * @param adapter a log adapter
   * @param minimumLevel the lowest log level that should be enabled
   * @return a new log adapter based on the previous one with filtering applied
   */
  public static LDLogAdapter level(LDLogAdapter adapter, LDLogLevel minimumLevel) {
    return new LevelFilter(adapter, minimumLevel);
  }
  
  /**
   * A default implementation that writes to the standard error stream at
   * {@link LDLogLevel#INFO} level.
   * 
   * @return a log adapter
   */
  public static LDLogAdapter basic() {
    return level(toConsole(), LDLogLevel.INFO);
  }
  
  /**
   * 
   * @return a new {@link LogCapture} instance
   */
  public static LogCapture capture() {
    return new LogCapture();
  }
  
  /**
   * A simple logging implementation that writes to the standard error stream.
   * <p>
   * This is equivalent to {@code toStream(System.err)}.
   * <p>
   * By default, all logging is enabled including {@link LDLogLevel#DEBUG} level. 
   * To filter by level, use {@link #level(LDLogAdapter, LDLogLevel)}. You can also
   * use {@link SimpleLogging} methods for additional configuration.
   * 
   * @return a log adapter
   */
  public static SimpleLogging toConsole() {
    return toStream(System.err);
  }
  
  /**
   * A simple logging implementation that writes to any {@code PrintStream}.
   * <p>
   * This could be a built-in stream such as {@code System.out}, or a file.
   * <p>
   * By default, all logging is enabled including {@link LDLogLevel#DEBUG} level. 
   * To filter by level, use {@link #level(LDLogAdapter, LDLogLevel)}. You can also
   * use {@link SimpleLogging} methods for additional configuration.
   * 
   * @param stream an output print stream
   * @return a log adapter
   */
  public static SimpleLogging toStream(PrintStream stream) {
    return toMethod(new StreamLineWriter(stream));
  }
  
  /**
   * A simple logging implementation that calls an interface method or lambda that you specify
   * for each line of output.
   * <p>
   * By default, all logging is enabled including {@link LDLogLevel#DEBUG} level. 
   * To filter by level, use {@link #level(LDLogAdapter, LDLogLevel)}. You can also
   * use {@link SimpleLogging} methods for additional configuration.
   * 
   * @param lineWriter a {@link SimpleLogging.LineWriter} implementation or lambda
   *   that writes a line of text
   * @return a log adapter
   */
  public static SimpleLogging toMethod(SimpleLogging.LineWriter lineWriter) {
    return new SimpleLogging(lineWriter, null, SimpleLogging.getDefaultTimestampFormat());
  }
  
  /**
   * A logging implementation that delegates to the {@code java.util.logging} API.
   * <p>
   * {@link LDLogLevel} levels are mapped to {@code java.util.logging.Level} levels
   * as follows: {@code DEBUG} to {@code FINE}, {@code INFO} to {@code INFO},
   * {@code WARN} to {@code WARNING}, and {@code ERROR} to {@code SEVERE}.
   * <p>
   * The returned object has no configuration methods; it will use whatever
   * global configuration is defined by {@code java.util.logging} (for instance,
   * the standard system property {@code java.util.logging.config.file}).
   * <p>
   * In Android, using this method requires Android API 26 or higher.
   *
   * @return a log adapter
   */
  public static LDLogAdapter toJavaUtilLogging() {
    return LDJavaUtilLogging.INSTANCE;
  }
  
  /**
   * A logging implementation that delegates to any number of destinations.
   *
   * <pre><code>
   *   // Send log output both to System.err and a file
   *   PrintWriter fileWriter = new PrintWriter("output.log");
   *   LDLogAdapter logAdapter = Logs.toMultiple(
   *     Logs.toConsole(),
   *     Logs.toStream(fileWriter)
   *   );
   * </code></pre>

   * @param adapters any number of log adapters
   * @return a log adapter that writes to all of them
   */
  public static LDLogAdapter toMultiple(LDLogAdapter... adapters) {
    return (adapters == null || adapters.length == 0) ? none() : new MultiLogging(adapters);
  }
  
  static final class StreamLineWriter implements SimpleLogging.LineWriter {
    final PrintStream stream; // exposed for testing
    
    StreamLineWriter(PrintStream stream) {
      this.stream = stream;
    }
    
    @Override
    public void writeLine(String line) {
      stream.println(line);
    }
  }
}
