package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class LogCaptureTest extends ParameterizedTestWithLevel {
  private final LDLogLevel outputLevel;
  
  public LogCaptureTest(LDLogLevel outputLevel) {
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    String logName = "logname";
    
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, logName);
    writeTestMessages(logger, outputLevel);
    
    verifyCapturedOutput(outputLevel, LDLogLevel.DEBUG, logName, sink);
  }
  
  public static void verifyCapturedOutput(
      LDLogLevel outputLevel,
      LDLogLevel enableLevel,
      String logName,
      LogCapture sink
      ) {
    List<LogCapture.Message> messages = sink.getMessages();
    List<String> messageStrings = sink.getMessageStrings();
    
    if (outputLevel == LDLogLevel.NONE || (enableLevel != null && outputLevel.compareTo(enableLevel) < 0)) {
      assertThat(messages, hasSize(0));
      assertThat(messageStrings, hasSize(0));
    } else {
      LogCapture.Message[] expectedMessages = new LogCapture.Message[] {
          new LogCapture.Message(logName, outputLevel, SIMPLE_MESSAGE),
          new LogCapture.Message(logName, outputLevel, MESSAGE_FORMAT_1_RESULT),
          new LogCapture.Message(logName, outputLevel, MESSAGE_FORMAT_2_RESULT),
          new LogCapture.Message(logName, outputLevel, MESSAGE_FORMAT_3_RESULT)
      };
      String prefix = outputLevel.name() + ":";
      String[] expectedStrings = new String[] {
          prefix + SIMPLE_MESSAGE,
          prefix + MESSAGE_FORMAT_1_RESULT,
          prefix + MESSAGE_FORMAT_2_RESULT,
          prefix + MESSAGE_FORMAT_3_RESULT
      };
      assertThat(messages, contains(expectedMessages));
      assertThat(messageStrings, contains(expectedStrings));
    }
  }
}
