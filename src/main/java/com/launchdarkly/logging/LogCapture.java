package com.launchdarkly.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A mechanism for capturing logger output in memory.
 * <p>
 * Calling {@link Logs#capture()} provides a {@link LogCapture} object that accumulates
 * all log output from any code that is configured to use it as the log adapter. This is
 * mainly intended for testing.
 * <p>
 * All messages that come to this object are captured regardless of the log level. If you
 * want to filter out messages below a certain level, you can apply {@link Logs#level(LDLogAdapter, LDLogLevel)} 
 * and pass the resulting filtered adapter to whatever component will be doing the logging,
 * in place of the original {@link LogCapture} object.
 * <p>
 * Example of usage in the server-side Java SDK:
 * 
 * <pre><code>
 *     LogCapture logSink = Logs.capture();
 *     LDConfig config = new LDConfig.Builder()
 *       .logging(
 *         Components.logging().adapter(logSink)
 *       )
 *       .build();
 *     // create the LDClient and do some things that might produce log output...
 *     // now, retrieve the captured output
 *     List&lt;LogCapture.Message&gt; messages = logSink.getMessages();
 * </code></pre>
 */
public final class LogCapture implements LDLogAdapter {
  private final List<Message> messages = new ArrayList<>();
  private final Object messagesLock = new Object();
  
  LogCapture() {}
  
  /**
   * Information about a captured log message.
   */
  public static final class Message {
    private final Instant timestamp;
    private final String loggerName;
    private final LDLogLevel level;
    private final String text;
    
    /**
     * Creates an instance.
     * 
     * @param timestamp the time the message was generated
     * @param loggerName the logger name
     * @param level the log level
     * @param text the text of the message, after any parameters have been substituted
     */
    public Message(Instant timestamp, String loggerName, LDLogLevel level, String text) {
      this.timestamp = timestamp;
      this.loggerName = loggerName;
      this.level = level;
      this.text = text;
    }

    /**
     * Returns the time the message was generated.
     * 
     * @return the timestamp
     */
    public Instant getTimestamp() {
      return timestamp;
    }
    
    /**
     * Returns the name of the logger that produced the message.
     * 
     * @return the logger name
     */
    public String getLoggerName() {
      return loggerName;
    }
    
    /**
     * Returns the log level of the message.
     * 
     * @return the log level
     */
    public LDLogLevel getLevel() {
      return level;
    }

    /**
     * Returns the text of the message.
     * 
     * @return the text of the message, after any parameters have been substituted
     */
    public String getText() {
      return text;
    }
    
    @Override
    public boolean equals(Object other) {
      if (other instanceof Message) {
        Message o = (Message)other;
        return Objects.equals(timestamp, o.timestamp) &&
            Objects.equals(loggerName, o.loggerName) &&
            level == o.level &&
            Objects.equals(text, o.text);
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(timestamp, loggerName, level, text);
    }
    
    /**
     * Returns a basic string representation of the log item, in the format
     * "[logger name] LEVEL: text".
     * 
     * @return a string representation
     * @see #toStringWithTimestamp()
     */
    @Override
    public String toString() {
      return "[" + loggerName + "] " + level.name() + ":" + text;
    }
    
    /**
     * Equivalent to {@link #toString()}, but also prefixes the line with a millisecond timestamp.
     * 
     * @return a string representation
     */
    public String toStringWithTimestamp() {
      return SimpleLogging.DEFAULT_TIMESTAMP_FORMAT.format(timestamp) + " " + toString();
    }
  }

  /**
   * Called internally by the SDK.
   * 
   * @param name a logger name assigned by the SDK
   */
  @Override
  public Channel newChannel(String name) {
    return new ChannelImpl(name);
  }
  
  /**
   * Returns all captured messages.
   * 
   * @return a copy of the messages
   */
  public List<Message> getMessages() {
    synchronized (messagesLock) {
      return new ArrayList<>(messages);
    }
  }
  
  /**
   * Returns all captured messages converted to strings.
   * <p>
   * The format is always "LEVEL:text".
   * 
   * @return a copy of the messages as strings
   */
  public List<String> getMessageStrings() {
    List<String> ret = new ArrayList<>();
    synchronized (messagesLock) {
      for (Message m: messages) {
        ret.add(m.getLevel().name() + ":" + m.getText());
      }
    }
    return ret;
  }
  
  /**
   * Removes and returns a captured log message in FIFO order, waiting if
   * necessary until one is available.
   * <p>
   * This method and {@link #requireMessage(Duration)} allow you to use
   * {@link LogCapture} like a blocking queue, so a test can wait for log output
   * that is being generated by another thread.
   *  
   * @param timeout the maximum time to wait
   * @return the next available log message, or null if none
   */
  public Message awaitMessage(Duration timeout) {
    return awaitMessage(null, timeout);
  }

  /**
   * Removes and returns a captured log message of the specified level in FIFO
   * order, waiting if necessary until one is available.
   * <p>
   * This method and {@link #requireMessage(Duration)} allow you to use
   * {@link LogCapture} like a blocking queue, so a test can wait for log output
   * that is being generated by another thread.
   *
   * @param level the desired message level, or null for any
   * @param timeout the maximum time to wait
   * @return the next available log message, or null if none
   */
  public Message awaitMessage(LDLogLevel level, Duration timeout) {
    Instant deadline = Instant.now().plus(timeout);
    synchronized (messagesLock) {
      for (;;) {
        for (int i = 0; i < messages.size(); i++) {
          Message m = messages.get(i);
          if (level == null || m.level == level) {
            messages.remove(i);
            return m; 
          }
        }
        long remainingTime = deadline.toEpochMilli() - System.currentTimeMillis();
        if (remainingTime <= 0) {
          return null;
        }
        try {
          messagesLock.wait(remainingTime);
        }
        catch (InterruptedException e) {
          return null;
        }
      }
    }
  }
  
  /**
   * Same as {@link #awaitMessage(Duration)}, but throws an exception on timeout.
   * 
   * @param timeout the maximum time to wait
   * @return the next available log message
   * @throws AssertionError if no log message was available within the timeout
   */
  public Message requireMessage(Duration timeout) {
    return requireMessage(null, timeout);
  }

  /**
   * Same as {@link #awaitMessage(LDLogLevel, Duration)}, but throws an exception on timeout.
   * 
   * @param level the desired message level, or null for any
   * @param timeout the maximum time to wait
   * @return the next available log message
   * @throws AssertionError if no log message was available within the timeout
   */
  public Message requireMessage(LDLogLevel level, Duration timeout) {
    Message m = awaitMessage(level, timeout);
    if (m == null) {
      throw new AssertionError("expected a log message but did not get one");
    }
    return m;
  }
  
  private final class ChannelImpl implements Channel {
    private final String name;
    
    ChannelImpl(String name) {
      this.name = name;
    }

    private void addMessage(LDLogLevel level, String message) {
      synchronized (messagesLock) {
        messages.add(new Message(Instant.now(), name, level, message));
        messagesLock.notifyAll();
      }
    }

    @Override
    public boolean isEnabled(LDLogLevel level) {
      return true;
    }
    
    @Override
    public void log(LDLogLevel level, Object message) {
      addMessage(level, message == null ? "" : message.toString());
    }

    @Override
    public void log(LDLogLevel level, String format, Object param) {
      addMessage(level, SimpleFormat.format(format, param));
    }

    @Override
    public void log(LDLogLevel level, String format, Object param1, Object param2) {
      addMessage(level, SimpleFormat.format(format, param1, param2));
    }

    @Override
    public void log(LDLogLevel level, String format, Object... params) {
      addMessage(level, SimpleFormat.format(format, params));
    }
  }
}
