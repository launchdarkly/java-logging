package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class MultiLogTest extends ParameterizedTestWithLevel {
  private final LDLogLevel outputLevel;
  
  public MultiLogTest(LDLogLevel outputLevel) {
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    String logName = "logname";
    
    LogCapture sink1 = Logs.capture();
    LogCapture sink2 = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(Logs.toMultiple(sink1, sink2), logName);
    writeTestMessages(logger, outputLevel);
    
    LogCaptureTest.verifyCapturedOutput(outputLevel, null, logName, sink1);
    LogCaptureTest.verifyCapturedOutput(outputLevel, null, logName, sink2);
  }
  
  @Test
  public void isEnabled() {
    String logName = "logname";
    
    // no level is enabled if there are no destinations
    LDLogAdapter multi0 = Logs.toMultiple();
    assertThat(LDLogger.withAdapter(multi0, logName).isEnabled(outputLevel), is(false));

    // level is disabled if it's disabled in all destinations 
    LDLogAdapter multi1 = Logs.toMultiple(Logs.none());
    assertThat(LDLogger.withAdapter(multi1, logName).isEnabled(outputLevel), is(false));

    // level is enabled if it's enabled in at least one destination
    LDLogAdapter multi2 = Logs.toMultiple(Logs.none(), Logs.toConsole());
    assertThat(LDLogger.withAdapter(multi2, logName).isEnabled(outputLevel), is(true));
  }
}
