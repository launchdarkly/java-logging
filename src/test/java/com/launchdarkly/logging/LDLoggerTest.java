package com.launchdarkly.logging;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.sameInstance;

@SuppressWarnings("javadoc")
public class LDLoggerTest extends BaseTest {
  @Test
  public void canCreateRootLogger() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "logname");
    logger.debug("hello");
    assertThat(sink.getMessages(), hasSize(1));
    assertThat(sink.getMessages().get(0).getLoggerName(), equalTo("logname"));
  }

  @Test
  public void canCreateSubLogger() {
    LogCapture sink = Logs.capture();
    LDLogger logger1 = LDLogger.withAdapter(sink, "logname");
    LDLogger logger2 = logger1.subLogger("other");
    logger2.debug("hello");
    assertThat(sink.getMessages(), hasSize(1));
    assertThat(sink.getMessages().get(0).getLoggerName(), equalTo("logname.other"));
  }
  
  @Test
  public void subLoggerWithNoNameIsSameAsParentLogger() {
    LDLogger logger = LDLogger.withAdapter(Logs.capture(), "logname");
    LDLogger logger1 = logger.subLogger(null);
    LDLogger logger2 = logger.subLogger("");
    assertThat(logger1, sameInstance(logger));
    assertThat(logger2, sameInstance(logger));
  }
  
  @Test
  public void noneLogger() {
    LDLogger logger = LDLogger.none();
    assertThat(logger.adapter, sameInstance(Logs.none()));
  }
}
