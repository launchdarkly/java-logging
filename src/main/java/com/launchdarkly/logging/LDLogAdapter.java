package com.launchdarkly.logging;

/**
 * An abstraction of some mechanism for producing log output.
 * <p>
 * Any LaunchDarkly library that can generate log output through {@code com.launchDarkly.logging}
 * has a configuration option of type {@link LDLogAdapter}, which defines the implementation
 * details of what to do with the log output. Built-in basic implementations are available
 * through the {@link Logs} factory class, and adapters that delegate to other logging
 * frameworks can be provided by other LaunchDarkly packages or by the application.
 * <p>
 * The basic model is that whatever component will be writing to the logs will define at least
 * one name for an output channel. The adapter's {@link LDLogAdapter#newChannel(String)} method
 * takes a name and returns a low-level {@link LDLogAdapter.Channel} implementation that accepts
 * log messages for any {@link LDLogLevel}; this is wrapped in the standard {@link LDLogger}
 * class, which is what the rest of the LaunchDarkly library code interacts with.
 * <p>
 * Applications should not need to interact directly with {@link LDLogAdapter}, beyond the
 * initial configuration step of choosing which one to use.
 */
public interface LDLogAdapter {
  /**
   * The logger calls this method to obtain a named output channel.
   * <p>
   * The name will be included in all log output for this channel. Channels are meant to be
   * retained and reused by the components they belong to, so the {@link LDLogAdapter} does
   * not need to cache them.
   * 
   * @param name an identifying name 
   * @return an implementation of {@link Channel}
   */
  Channel newChannel(String name);
  
  /**
   * The underlying implementation object used by some {@link LDLogger} instance.
   * <p>
   * Applications or libraries that generate log output do not need to interact directly with
   * {@link Channel}; implementations of it are created by whatever {@link LDLogAdapter} is
   * being used.
   * <p>
   * The logger will send messages to this object, each with an {@link LDLogLevel}. If
   * output is known to be completely disabled for the specified level, the {@link Channel}
   * method should return immediately and do no other processing. Otherwise, for simple
   * messages it should call {@code toString()} on the message parameter. It can always
   * assume that {@code message} and {@code format} parameters are non-null.
   * <p>
   * The {@code format} parameter must use the same format defined by {@link SimpleFormat}.
   * The reason that {@code format}/{@code param} values are passed straight through from
   * {@link LDLogger} to {@link Channel}, instead of having {@link LDLogger} do the string
   * interpolation, is that a {@link Channel} implementation that is delegating to another
   * logging framework may not be able to know for sure whether a given log level is enabled
   * (since filtering rules might be configured elsewhere in that framework); providing the
   * parameters separately lets the implementation class decide whether or not to incur the
   * overhead of string interpolation.
   * <p>
   * The reason that there are four overloads for {@link #log} is for efficiency, to avoid
   * allocating a params array in the common case of a message with fewer than three parameters.
   */
  public interface Channel {
    /**
     * Tests whether log output for a certain level is at least potentially visible.
     * <p>
     * This is the underlying implementation of {@link LDLogger#isEnabled(LDLogLevel)}.
     * The method should return true if the specified level is enabled in the sense that it will
     * not be automatically discarded by this {@link Channel}. It should only return false if
     * the {@link Channel} will definitely discard that level.
     * 
     * @param level a log level
     * @return true if this level is potentially visible
     */
    boolean isEnabled(LDLogLevel level);
    
    /**
     * Logs a simple message with no parameters.
     * 
     * @param level the log level
     * @param message the message
     */
    void log(LDLogLevel level, Object message);

    /**
     * Logs a message with a single parameter.
     * 
     * @param level the log level
     * @param format the format string
     * @param param the parameter
     */
    void log(LDLogLevel level, String format, Object param);

    /**
     * Logs a message with two parameters.
     * 
     * @param level the log level
     * @param format the format string
     * @param param1 the first parameter
     * @param param2 the second parameter
     */
    void log(LDLogLevel level, String format, Object param1, Object param2);

    /**
     * Logs a message with any number of parameters.
     * 
     * @param level the log level
     * @param format the format string
     * @param params the parameters
     */
    void log(LDLogLevel level, String format, Object... params);
  }
  
  /**
   * Marker interface indicating that this adapter is for an external framework that has its
   * own configuration mechanism. If the adapter implements this interface, the LaunchDarkly
   * logging framework will not try to do its own level filtering.
   * @since 1.1.0
   */
  public interface IsConfiguredExternally {}
}
