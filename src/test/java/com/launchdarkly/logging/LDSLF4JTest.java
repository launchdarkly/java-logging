package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class LDSLF4JTest extends ParameterizedTestWithLevel {
  private final LDLogLevel outputLevel;
  
  public LDSLF4JTest(LDLogLevel outputLevel) {
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    String logName = "logname";

    TestLogger targetLogger = TestLoggerFactory.getTestLogger(logName);
    targetLogger.clearAll();

    LDLogger logger = LDLogger.withAdapter(LDSLF4J.adapter(), logName);
    writeTestMessages(logger, outputLevel);
    
    List<LoggingEvent> events = targetLogger.getAllLoggingEvents();
    assertThat(events, hasSize(5));
    
    Level expectedLevel = slf4jLevelFor(outputLevel);

    assertThat(events.get(0).getLevel(), equalTo(expectedLevel));
    assertThat(events.get(0).getMessage(), equalTo(""));
    assertThat(events.get(0).getArguments(), hasSize(0));

    assertThat(events.get(1).getLevel(), equalTo(expectedLevel));
    assertThat(events.get(1).getMessage(), equalTo(SIMPLE_MESSAGE));
    assertThat(events.get(1).getArguments(), hasSize(0));
    
    assertThat(events.get(2).getLevel(), equalTo(expectedLevel));
    assertThat(events.get(2).getMessage(), equalTo(MESSAGE_FORMAT_1));
    assertThat(events.get(2).getArguments(), contains(MESSAGE_PARAM_1));
    
    assertThat(events.get(3).getLevel(), equalTo(expectedLevel));
    assertThat(events.get(3).getMessage(), equalTo(MESSAGE_FORMAT_2));
    assertThat(events.get(3).getArguments(), contains(MESSAGE_PARAM_1, MESSAGE_PARAM_2));
    
    assertThat(events.get(4).getLevel(), equalTo(expectedLevel));
    assertThat(events.get(4).getMessage(), equalTo(MESSAGE_FORMAT_3));
    assertThat(events.get(4).getArguments(), contains(MESSAGE_PARAM_1, MESSAGE_PARAM_2, MESSAGE_PARAM_3));
  }
  
  @Test
  public void isEnabled() {
    String logName = "logname";

    TestLogger targetLogger = TestLoggerFactory.getTestLogger(logName);
    LDLogger logger = LDLogger.withAdapter(LDSLF4J.adapter(), logName);

    targetLogger.setEnabledLevels(slf4jLevelFor(outputLevel));
    assertThat(logger.isEnabled(outputLevel), is(true));

    targetLogger.setEnabledLevels();
    assertThat(logger.isEnabled(outputLevel), is(false));
  }
  
  @Test
  public void isLevelFilterConfiguredExternally() {
    assertThat(LDSLF4J.adapter().isLevelFilterConfiguredExternally(), is(true));
  }
  
  private Level slf4jLevelFor(LDLogLevel level) {
    switch (outputLevel) {
    case DEBUG:
      return Level.DEBUG;
    case INFO:
      return Level.INFO;
    case WARN:
      return Level.WARN;
    default:
      return Level.ERROR;
    }
  }
}
