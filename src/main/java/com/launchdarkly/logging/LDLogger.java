package com.launchdarkly.logging;

import com.launchdarkly.logging.LDLogAdapter.Channel;

import static com.launchdarkly.logging.LDLogLevel.DEBUG;
import static com.launchdarkly.logging.LDLogLevel.ERROR;
import static com.launchdarkly.logging.LDLogLevel.INFO;
import static com.launchdarkly.logging.LDLogLevel.WARN;

/**
 * A basic logger facade that delegates to an underlying output implementation.
 * <p>
 * The LaunchDarkly SDK sends all of its logging output through this class. What happens
 * to the output depends on the {@link LDLogAdapter} that has been used to configure the
 * SDK.
 * <p>
 * Applications will not normally need to interact with {@link LDLogger} directly. See
 * the SDK's configuration builder for how to configure logging using a log adapter.
 * <p>
 * The logger has output methods for each of the levels defined in {@link LDLogLevel}.
 * For efficiency (to avoid unnecessarily creating varargs arrays), each level has four
 * methods: one for non-parameterized messages, one for messages with a single parameter,
 * one for messages with two parameters, and one for messages with an arbitrary number
 * of parameters.
 * <p>
 * Parameter substitution uses the syntax defined by {@link SimpleFormat}: the format
 * string can contain "{}" placeholders, which are replaced by the result of calling
 * {@code toString()} on the corresponding parameter.
 * <p>
 * To avoid unnecessary computations, {@code toString()} is not called on parameters
 * (or, in the single-argument methods, on the single {@code Object}) if logging at
 * the specified level is disabled.
 */
public final class LDLogger {
  private final String name;
  final LDLogAdapter adapter; // exposed for testing
  private final Channel channel;
  
  LDLogger(String name, LDLogAdapter adapter, Channel channel) {
    this.name = name;
    this.adapter = adapter;
    this.channel = channel;
  }
  
  /**
   * Returns a logger instance using the specified adapter.
   * 
   * @param adapter the {@link LDLogAdapter} that provides the output implementation
   * @param name an identifier for the logger which may be included in output
   * @return a logger instance
   */
  public static LDLogger withAdapter(LDLogAdapter adapter, String name) {
    return new LDLogger(name, adapter, adapter.newChannel(name));
  }

  /**
   * Returns a logger instance that does nothing.
   * 
   * @return a no-op logger
   */
  public static LDLogger none() {
    return withAdapter(Logs.none(), "");
  }
  
  /**
   * Returns a logger instance derived from this instance.
   * 
   * @param nameSuffix will be appended to the current logger's name, separated by a
   *   period, to create the new logger's name
   * @return a logger instance that uses the same adapter
   */
  public LDLogger subLogger(String nameSuffix) {
    if (nameSuffix == null || nameSuffix.equals("")) {
      return this;
    }
    String subName = name + "." + nameSuffix;
    return new LDLogger(subName, adapter, adapter.newChannel(subName));
  }
  
  /**
   * Tests whether log output for a certain level is at least potentially visible.
   * <p>
   * Generally, any desired level filtering should be set up in the initial logging
   * configuration, and code that generates log messages should simply call methods like
   * {@link LDLogger#info(Object)} without having to know whether that particular level
   * is enabled or is being filtered out. However, if some kind of log message is
   * particularly expensive to compute, you may call {@link #isEnabled(LDLogLevel)};
   * a false value means you can skip trying to log any message at that level.
   * <p>
   * Another approach is to generate any computationally expensive output lazily, such
   * as by using the methods in {@link LogValues}.
   * 
   * @param level a log level
   * @return true if this level is potentially visible
   */
  public boolean isEnabled(LDLogLevel level) {
    return channel.isEnabled(level);
  }
  
  /**
   * Writes a message at {@link LDLogLevel#DEBUG} level.
   * @param message the message (will be converted to a string with {@code toString()})
   */
  public void debug(Object message) {
    channel.log(DEBUG, message);
  }

  /**
   * Writes a message at {@link LDLogLevel#DEBUG} level with one parameter.
   * @param format the format string, containing "{}" placeholders
   * @param param the parameter
   */
  public void debug(String format, Object param) {
    channel.log(DEBUG, format, param);
  }

  /**
   * Writes a message at {@link LDLogLevel#DEBUG} level with two parameters.
   * @param format the format string, containing "{}" placeholders
   * @param param1 the first parameter
   * @param param2 the second parameter
   */
  public void debug(String format, Object param1, Object param2) {
    channel.log(DEBUG, format, param1, param2);
  }

  /**
   * Writes a message at {@link LDLogLevel#DEBUG} level with any number of parameters.
   * @param format the format string, containing "{}" placeholders
   * @param params the parameters
   */
  public void debug(String format, Object... params) {
    channel.log(DEBUG, format, params);
  }

  /**
   * Writes a message at {@link LDLogLevel#INFO} level.
   * @param message the message (will be converted to a string with {@code toString()})
   */
  public void info(Object message) {
    channel.log(INFO, message);
  }

  /**
   * Writes a message at {@link LDLogLevel#INFO} level with one parameter.
   * @param format the format string, containing "{}" placeholders
   * @param param the parameter
   */
  public void info(String format, Object param) {
    channel.log(INFO, format, param);
  }

  /**
   * Writes a message at {@link LDLogLevel#INFO} level with two parameters.
   * @param format the format string, containing "{}" placeholders
   * @param param1 the first parameter
   * @param param2 the second parameter
   */
  public void info(String format, Object param1, Object param2) {
    channel.log(INFO, format, param1, param2);
  }

  /**
   * Writes a message at {@link LDLogLevel#INFO} level with any number of parameters.
   * @param format the format string, containing "{}" placeholders
   * @param params the parameters
   */
  public void info(String format, Object... params) {
    channel.log(INFO, format, params);
  }

  /**
   * Writes a message at {@link LDLogLevel#WARN} level.
   * @param message the message (will be converted to a string with {@code toString()})
   */
  public void warn(Object message) {
    channel.log(WARN, message);
  }

  /**
   * Writes a message at {@link LDLogLevel#WARN} level with one parameters.
   * @param format the format string, containing "{}" placeholders
   * @param param the parameter
   */
   public void warn(String format, Object param) {
    channel.log(WARN, format, param);
  }

   /**
    * Writes a message at {@link LDLogLevel#WARN} level with two parameters.
   * @param format the format string, containing "{}" placeholders
    * @param param1 the first parameter
    * @param param2 the second parameter
    */
  public void warn(String format, Object param1, Object param2) {
    channel.log(WARN, format, param1, param2);
  }

  /**
   * Writes a message at {@link LDLogLevel#WARN} level with any number of parameters.
   * @param format the format string, containing "{}" placeholders
   * @param params the parameters
   */
  public void warn(String format, Object... params) {
    channel.log(WARN, format, params);
  }

  /**
   * Writes a message at {@link LDLogLevel#ERROR} level.
   * @param message the message (will be converted to a string with {@code toString()})
   */
  public void error(Object message) {
    channel.log(ERROR, message);
  }

  /**
   * Writes a message at {@link LDLogLevel#ERROR} level with one parameter.
   * @param format the format string, containing "{}" placeholders
   * @param param the parameter
   */
   public void error(String format, Object param) {
    channel.log(ERROR, format, param);
  }

   /**
    * Writes a message at {@link LDLogLevel#ERROR} level with two parameters.
   * @param format the format string, containing "{}" placeholders
    * @param param1 the first parameter
    * @param param2 the second parameter
    */
  public void error(String format, Object param1, Object param2) {
    channel.log(ERROR, format, param1, param2);
  }

  /**
   * Writes a message at {@link LDLogLevel#ERROR} level with any number of parameters.
   * @param format the format string, containing "{}" placeholders
   * @param params the parameters
   */
  public void error(String format, Object... params) {
    channel.log(ERROR, format, params);
  }
}
