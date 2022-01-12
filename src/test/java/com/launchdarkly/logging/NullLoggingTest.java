package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class NullLoggingTest extends ParameterizedTestWithLevel {
  private final LDLogLevel outputLevel;
  
  public NullLoggingTest(LDLogLevel outputLevel) {
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    // just verifies that writing to the null logger doesn't throw an exception
    LDLogger logger = LDLogger.withAdapter(Logs.none(), "logname");
    writeTestMessages(logger, outputLevel);
  }
  
  @Test
  public void isEnabled() {
    LDLogger logger = LDLogger.withAdapter(Logs.none(), "logname");
    assertThat(logger.isEnabled(outputLevel), is(false));
  }
}
