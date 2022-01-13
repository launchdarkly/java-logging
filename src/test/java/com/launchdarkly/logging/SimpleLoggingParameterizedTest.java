package com.launchdarkly.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.launchdarkly.logging.TestHelpers.writeTestMessages;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class SimpleLoggingParameterizedTest extends ParameterizedTestWithLevel {
  private final LDLogLevel outputLevel;
  
  public SimpleLoggingParameterizedTest(LDLogLevel outputLevel) {
    this.outputLevel = outputLevel;
  }

  @Test
  public void testOutput() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    
    LDLogger logger = LDLogger.withAdapter(
        Logs.toStream(ps).timestampFormat(null),
        "logname"
    );
    writeTestMessages(logger, outputLevel);
    List<String> resultLines = parseLines(bos.toString());
    
    String prefix = "[logname] " + outputLevel + ": ";
    List<String> expectedLines = Arrays.asList(
        prefix,
        prefix + SIMPLE_MESSAGE,
        prefix + MESSAGE_FORMAT_1_RESULT,
        prefix + MESSAGE_FORMAT_2_RESULT,
        prefix + MESSAGE_FORMAT_3_RESULT
        );
    assertEquals(expectedLines, resultLines);
  }
  
  public static List<String> parseLines(String output) {
    if (output.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> ret = new ArrayList<>();
    for (String line: output.split("\r?\n")) { // might be using Unix linefeeds or Windows CRLF
      if (!line.isEmpty()) {
        ret.add(line);
      }
    }
    return ret;
  }
}
