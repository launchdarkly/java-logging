package com.launchdarkly.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper methods for logging special variables.
 */
public abstract class LogValues {
  private LogValues() {}
  
  /**
   * Functional interface for use with {@link #defer}.
   */
  public interface StringProvider {
    /**
     * Computes a string value.
     * 
     * @return the value
     */
    String get(); 
  }
  
  /**
   * Converts any method or lambda returning a string into an object for calling it lazily.
   * <p>
   * This returns an object which delegates its {@code toString()} implementation to the
   * specified method or lambda.
   * <p> 
   * Sometimes log messages may include a computed value that has enough computational
   * overhead that you would prefer not to compute it unless it really will be logged.
   * The {@link LDLogger} methods that take parameters of type {@code Object} do not
   * call {@code toString()} to convert those parameters to strings immediately; if
   * logging of this message has been disabled, for instance by level filtering, or if
   * all logging is disabled because the destination is {@link Logs#none()}, then
   * {@code toString()} is not called.
   *
   * <pre><code>
   *   // Here, computeJsonData is only called if debug-level logging is enabled
   *   logger.debug("The JSON data is: {}", () -&gt; computeJsonData());
   * </code></pre>
   * 
   * @param stringProvider a method or lambda that returns a string
   * @return an object that calls {@code stringProvider} if {@code toString} is called
   */
  public static Object defer(StringProvider stringProvider) {
    return new DeferImpl(stringProvider);
  }
  
  /**
   * Returns an object whose {@code toString()} method returns only the class name and
   * optional message of the exception.
   * <p>
   * That is already the behavior of {@code Exception.toString()}, so this method just
   * returns the exception itself. It is provided here in order to make the intended
   * behavior clearer in logging code (and for consistency with the
   * {@code LogValues.ExceptionSummary} method in the .NET equivalent of this package).
   * Also, future versions of this package might define additional behavior, with the
   * constraint that it will never include a stacktrace.
   * 
   * @param e an exception
   * @return the same exception
   */
  public static Object exceptionSummary(Throwable e) {
    return e;
  }
  
  /**
   * Returns an object that lazily constructs an exception stacktrace.
   * <p>
   * Calling {@code toString()} on the object returned by this method returns the
   * exception's stacktrace as a string. This string is not constructed unless
   * {@code toString()} is called, so writing exceptions to the log in this way incurs
   * very little overhead if logging is not enabled for the specified log level.
   * <pre><code>
   *   try { ... }
   *   catch (Exception e) {
   *     logger.error("Error: {}", LogValues.exceptionSummary(e));
   *     logger.debug("{}", LogValues, exceptionTrace(e));
   * </code></pre>
   * 
   * @param e an exception
   * @return an object whose {@code toString()} method provides a stacktrace
   */
  public static Object exceptionTrace(final Throwable e) {
    return e == null ? null : defer(new StringProvider() {
      @Override
      public String get() {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
      }
    });    
  }
  
  private static final class DeferImpl {
    private final StringProvider stringProvider;
    
    DeferImpl(StringProvider stringProvider) {
      this.stringProvider = stringProvider;
    }
    
    @Override
    public String toString() {
      return stringProvider.get();
    }
  }
}
