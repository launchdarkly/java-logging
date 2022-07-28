package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class LDJavaUtilLoggingTest extends ParameterizedTestWithLevel {
  private static final String LOGGING_CONFIGURATION =
      // This configuration produces the "[logname] LEVEL: message" format we expect in our test
      "java.util.logging.SimpleFormatter.format=[%3$s] %4$s: %5$s%n\n" +
      ".level=ALL\n";
  private static final ByteArrayOutputStream logOutput;
  private static final StreamHandler streamHandler;
  
  static {
    // java.util.logging is configured statically, but since we're not using it anywhere in our
    // unit tests except in this class, it's safe to set its global configuration to redirect
    // all j.u.l output to our logOutput buffer.
    logOutput = new ByteArrayOutputStream();
    try {
      LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(LOGGING_CONFIGURATION.getBytes()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Logger rootLogger = Logger.getLogger("");
    streamHandler = new StreamHandler(logOutput, new SimpleFormatter());
    streamHandler.setLevel(Level.ALL);
    rootLogger.addHandler(streamHandler);
  }
  
  private final LDLogLevel outputLevel;
  
  public LDJavaUtilLoggingTest(LDLogLevel outputLevel) {
    logOutput.reset();
    
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    String logName = "logname";

    LDLogger logger = LDLogger.withAdapter(Logs.toJavaUtilLogging(), logName);
    writeTestMessages(logger, outputLevel);
    
    streamHandler.flush();
    List<String> outputLines = Arrays.asList(logOutput.toString().split(System.lineSeparator()));
    
    String prefix = "[logname] " + javaLevelFor(outputLevel).toString() + ": ";
    assertThat(outputLines, contains(
        prefix,
        prefix + SIMPLE_MESSAGE,
        prefix + MESSAGE_FORMAT_1_RESULT,
        prefix + MESSAGE_FORMAT_2_RESULT,
        prefix + MESSAGE_FORMAT_3_RESULT
        ));
    
    assertThat(logger.isEnabled(outputLevel), is(true));
  }
  
  @Test
  public void isLevelFilterConfiguredExternally() {
    assertThat(Logs.toJavaUtilLogging().isLevelFilterConfiguredExternally(), is(true));
  }

  private Level javaLevelFor(LDLogLevel level) {
    switch (outputLevel) {
    case DEBUG:
      return Level.FINE;
    case INFO:
      return Level.INFO;
    case WARN:
      return Level.WARNING;
    default:
      return Level.SEVERE;
    }
  }
}
