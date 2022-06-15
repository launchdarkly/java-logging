package com.launchdarkly.logging;

/**
 * Enumeration of the logging levels defined by the LaunchDarkly logging abstraction.
 * <p>
 * This is the same basic level concept that exists in most logging frameworks. Levels
 * are ranked in ascending order from {@link #DEBUG} to {@link #ERROR}. Whatever
 * minimum level is enabled for the logger, any messages at a lower level will be
 * suppressed: for instance, if the minimum level is {@link #WARN}, then there will be
 * no output for {@link #DEBUG} or {@link #INFO}.
 */
public enum LDLogLevel {
  /**
   * This level is for very detailed and verbose messages that are rarely useful except
   * in diagnosing an unusual problem.
   */
  DEBUG,
  
  /**
   * This level is for informational messages that are logged during normal operation of
   * the SDK. 
   */
  INFO,
  
  /**
   * This level is for messages about unexpected conditions that may be worth noting,
   * but that do not necessarily prevent the SDK from working normally.
   */
  WARN,
  
  /**
   * This level is for errors that should not happen during normal operation of the SDK
   * and should be investigated.
   */
  ERROR,
  
  /**
   * This level is not actually used for output, but setting the minimum enabled level to
   * {@code NONE} disables all output. 
   */
  NONE
}
