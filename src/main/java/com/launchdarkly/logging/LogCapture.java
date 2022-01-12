package com.launchdarkly.logging;

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
  
  LogCapture() {}
  
  /**
   * Information about a captured log message.
   */
  public static final class Message {
    private final String loggerName;
    private final LDLogLevel level;
    private final String text;
    
    /**
     * Creates an instance.
     * 
     * @param loggerName the logger name
     * @param level the log level
     * @param text the text of the message, after any parameters have been substituted
     */
    public Message(String loggerName, LDLogLevel level, String text) {
      this.loggerName = loggerName;
      this.level = level;
      this.text = text;
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
        return Objects.equals(loggerName, o.loggerName) && level == o.level && Objects.equals(text, o.text);
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(loggerName, level, text);
    }
    
    @Override
    public String toString() {
      return "[" + loggerName + "] " + level.name() + ":" + text;
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
    synchronized (messages) {
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
    synchronized (messages) {
      for (Message m: messages) {
        ret.add(m.getLevel().name() + ":" + m.getText());
      }
    }
    return ret;
  }
  
  private final class ChannelImpl implements Channel {
    private final String name;
    
    ChannelImpl(String name) {
      this.name = name;
    }

    private void addMessage(LDLogLevel level, String message) {
      synchronized (messages) {
        messages.add(new Message(name, level, message));
      }
    }

    @Override
    public boolean isEnabled(LDLogLevel level) {
      return true;
    }
    
    @Override
    public void log(LDLogLevel level, String message) {
      addMessage(level, message);
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
