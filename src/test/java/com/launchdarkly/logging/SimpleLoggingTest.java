package com.launchdarkly.logging;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("javadoc")
public class SimpleLoggingTest extends BaseTest {
  @Test
  public void testLogsToMethod() {
    final List<String> lines = new ArrayList<>();
    SimpleLogging adapter = Logs.toMethod(new SimpleLogging.LineWriter() {
      @Override
      public void writeLine(String line) {
        lines.add(line);
      }
    }).timestampFormat(null);
    LDLogger logger = LDLogger.withAdapter(adapter, "logname");
    
    logger.info("hello");
    
    assertThat(lines, contains("[logname] INFO: hello"));
  }
  
  @Test
  public void testConsole() {
    SimpleLogging adapter = Logs.toConsole();
    assertThat(((Logs.StreamLineWriter)adapter.lineWriter).stream,
        Matchers.sameInstance(System.err));
  }

  @Test
  public void testBasic() {
    LevelFilter filtered = (LevelFilter)Logs.basic();
    SimpleLogging adapter = (SimpleLogging)filtered.wrappedAdapter; 
    assertThat(((Logs.StreamLineWriter)adapter.lineWriter).stream,
        Matchers.sameInstance(System.err));
    assertThat(LDLogger.withAdapter(filtered, "").isEnabled(LDLogLevel.DEBUG), is(false));
    assertThat(LDLogger.withAdapter(filtered, "").isEnabled(LDLogLevel.INFO), is(true));
  }
  
  @Test
  public void testTag() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    
    LDLogger logger = LDLogger.withAdapter(
        Logs.toStream(ps).tag("logtag").timestampFormat(null),
        "logname"
    );
    logger.warn("message");
    
    List<String> resultLines = SimpleLoggingParameterizedTest.parseLines(bos.toString());
    assertEquals(Arrays.asList("{logtag} [logname] WARN: message"), resultLines);
  }

  @Test
  public void testDefaultDateFormat() {
    String exampleDate = SimpleLogging.getDefaultTimestampFormat().format(new Date());
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    
    LDLogger logger = LDLogger.withAdapter(
        Logs.toStream(ps),
        "logname"
    );
    logger.warn("message");
    
    List<String> resultLines = SimpleLoggingParameterizedTest.parseLines(bos.toString());
    assertEquals(1, resultLines.size());
    String line = resultLines.get(0);
    assertEquals("[logname] WARN: message", line.substring(exampleDate.length() + 1));
  }
  
  @Test
  public void testCustomDateFormat() {
    String currentYear = new SimpleDateFormat("yyyy").format(new Date());
    
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    
    LDLogger logger = LDLogger.withAdapter(
        Logs.toStream(ps).timestampFormat(new SimpleDateFormat("yyyy")),
        "logname"
    );
    logger.warn("message");
    
    List<String> resultLines = SimpleLoggingParameterizedTest.parseLines(bos.toString());
    assertEquals(1, resultLines.size());
    String line = resultLines.get(0);
    assertEquals(currentYear + " [logname] WARN: message", line);    
  }
  
  @Test
  public void testNoDateFormat() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bos);
    
    LDLogger logger = LDLogger.withAdapter(
        Logs.toStream(ps).timestampFormat(null),
        "logname"
    );
    logger.warn("message");
    
    List<String> resultLines = SimpleLoggingParameterizedTest.parseLines(bos.toString());
    assertEquals(1, resultLines.size());
    String line = resultLines.get(0);
    assertEquals("[logname] WARN: message", line);    
  }
}
