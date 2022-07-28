package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class LevelFilterTest {
  @Parameters(name = "outputLevel={0}, enableLevel={1}")
  public static Collection<Object[]> outputLevelAndEnableLevel() {
    List<Object[]> ret = new ArrayList<>();
    for (LDLogLevel outputLevel: LDLogLevel.values()) {
      if (outputLevel != LDLogLevel.NONE) {
        for (LDLogLevel enableLevel: LDLogLevel.values()) {
          ret.add(new Object[] { outputLevel, enableLevel });
        }
      }
    }
    return ret;
  }
  
  private final LDLogLevel outputLevel;
  private final LDLogLevel enableLevel;

  public LevelFilterTest(LDLogLevel outputLevel, LDLogLevel enableLevel) {
    this.outputLevel = outputLevel;
    this.enableLevel = enableLevel;
  }
  
  @Test
  public void testLevelFiltering() {
    LogCapture sink = Logs.capture();
    LDLogAdapter filtered = Logs.level(sink, enableLevel);
    LDLogger logger = LDLogger.withAdapter(filtered, "logname");
    writeTestMessages(logger, outputLevel);
    LogCaptureTest.verifyCapturedOutput(outputLevel, enableLevel, "logname", sink);
  }
  
  @Test
  public void testLevelIsEnabled() {
    LogCapture sink = Logs.capture();
    LDLogAdapter filtered = Logs.level(sink, enableLevel);
    LDLogger logger = LDLogger.withAdapter(filtered, "logname");
    assertThat(logger.isEnabled(outputLevel),
        is(outputLevel.compareTo(enableLevel) >= 0));
  }
  
  @Test
  public void testNullLevelIsSameAsDebug() {
    LogCapture sink = Logs.capture();
    LDLogAdapter filtered = Logs.level(sink, null);
    LDLogger logger = LDLogger.withAdapter(filtered, "logname");
    assertThat(logger.isEnabled(LDLogLevel.DEBUG), is(true));
  }
  
  @Test
  public void levelFilterIsIgnoredForExternallyConfiguredAdapter() {
    LogCapture sink = Logs.capture();
    LDLogAdapter adapter = new MyExternallyConfiguredAdapter(sink);
    LDLogAdapter filtered = Logs.level(adapter, LDLogLevel.ERROR);
    LDLogger logger = LDLogger.withAdapter(filtered, "logname");
    writeTestMessages(logger, outputLevel);
    LogCaptureTest.verifyCapturedOutput(outputLevel, LDLogLevel.DEBUG, "logname", sink);
  }
  
  public static class MyExternallyConfiguredAdapter implements LDLogAdapter {
    private final LDLogAdapter wrappedAdapter;
    
    public MyExternallyConfiguredAdapter(LDLogAdapter wrappedAdapter) {
      this.wrappedAdapter = wrappedAdapter;
    }
    
    @Override
    public Channel newChannel(String name) {
      return wrappedAdapter.newChannel(name);
    }
    
    @Override
    public boolean isLevelFilterConfiguredExternally() {
      return true;
    }
  }
}
