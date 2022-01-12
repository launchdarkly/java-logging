package com.launchdarkly.logging;

import com.launchdarkly.testhelpers.TypeBehavior;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SuppressWarnings("javadoc")
public class LogCaptureMessageTest extends BaseTest {
  @Test
  public void testBasicProperties() {
    LogCapture.Message m = new LogCapture.Message("name", LDLogLevel.INFO, "text");
    assertThat(m.getLoggerName(), equalTo("name"));
    assertThat(m.getLevel(), equalTo(LDLogLevel.INFO));
    assertThat(m.getText(), equalTo("text"));
  }
  
  @Test
  public void equalInstancesAreEqual() {
    List<TypeBehavior.ValueFactory<LogCapture.Message>> valueFactories = new ArrayList<>();
    for (String name: new String[] { "name1", "name2" }) {
      for (LDLogLevel level: LDLogLevel.values()) {
        for (String text: new String[] { "text1", "text2" }) {
          valueFactories.add(() -> new LogCapture.Message(name, level, text));
        }
      }
    }
    TypeBehavior.checkEqualsAndHashCode(valueFactories);
  }
  
  @Test
  public void simpleStringRepresentation() {
    LogCapture.Message m = new LogCapture.Message("name", LDLogLevel.INFO, "text");
    assertThat(m.toString(), equalTo("[name] INFO:text"));
  }
}
