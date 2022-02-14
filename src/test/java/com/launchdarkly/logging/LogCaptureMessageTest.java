package com.launchdarkly.logging;

import com.launchdarkly.testhelpers.TypeBehavior;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@SuppressWarnings("javadoc")
public class LogCaptureMessageTest extends BaseTest {
  @Test
  public void testBasicProperties() {
    Date timestamp = new Date();
    LogCapture.Message m = new LogCapture.Message(timestamp, "name", LDLogLevel.INFO, "text");
    assertThat(m.getTimestamp(), equalTo(timestamp));
    assertThat(m.getLoggerName(), equalTo("name"));
    assertThat(m.getLevel(), equalTo(LDLogLevel.INFO));
    assertThat(m.getText(), equalTo("text"));
  }
  
  @Test
  public void equalInstancesAreEqual() {
    List<TypeBehavior.ValueFactory<LogCapture.Message>> valueFactories = new ArrayList<>();
    Date time1 = new Date(1000);
    Date time2 = new Date(2000);
    for (Date time: new Date[] { time1, time2 }) {
      for (String name: new String[] { "name1", "name2" }) {
        for (LDLogLevel level: LDLogLevel.values()) {
          for (String text: new String[] { "text1", "text2" }) {
            valueFactories.add(() -> new LogCapture.Message(time, name, level, text));
          }
        }
      }
    }
    TypeBehavior.checkEqualsAndHashCode(valueFactories);
  }
  
  @Test
  public void simpleStringRepresentation() {
    LogCapture.Message m = new LogCapture.Message(new Date(), "name", LDLogLevel.INFO, "text");
    assertThat(m.toString(), equalTo("[name] INFO:text"));
  }

  @Test
  public void stringRepresentationWithTimestamp() {
    Date timestamp = new Date(100000000);
    LogCapture.Message m = new LogCapture.Message(timestamp, "name", LDLogLevel.INFO, "text");
    assertThat(m.toStringWithTimestamp(), equalTo("1970-01-02 03:46:40.000 UTC [name] INFO:text"));
  }

  @Test
  public void awaitMessageReturnsPreviouslyAddedMessages() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    logger.info("text1");
    logger.info("text2");
    LogCapture.Message m1 = sink.awaitMessage(1000);
    assertThat(m1.getText(), equalTo("text1"));
    LogCapture.Message m2 = sink.awaitMessage(1000);
    assertThat(m2.getText(), equalTo("text2"));
  }
  
  @Test
  public void awaitMessageWaitsForMessages() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    new Thread(() -> {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {}
      logger.info("text1");
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {}
      logger.info("text2");
    }).start();
    LogCapture.Message m1 = sink.awaitMessage(1000);
    assertThat(m1.getText(), equalTo("text1"));
    LogCapture.Message m2 = sink.awaitMessage(1000);
    assertThat(m2.getText(), equalTo("text2"));
  }
  
  @Test
  public void awaitMessageTimesOut() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
      logger.info("text1");
    }).start();
    LogCapture.Message m = sink.awaitMessage(10);
    assertThat(m, nullValue());
  }
  
  @Test
  public void awaitMessageCanBeInterrupted() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    AtomicReference<LogCapture.Message> received = new AtomicReference<>();
    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
      logger.info("text1");
    }).start();
    Thread t = new Thread(() -> {
      received.set(sink.awaitMessage(1000));
    });
    t.start();
    t.interrupt();
    assertThat(received.get(), nullValue());
  }
  
  @Test
  public void awaitForSpecificLevelReturnsPreviouslyAddedMessages() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    logger.warn("text2");
    logger.info("text1");
    LogCapture.Message m1 = sink.awaitMessage(LDLogLevel.INFO, 1000);
    assertThat(m1.getText(), equalTo("text1"));
    LogCapture.Message m2 = sink.awaitMessage(LDLogLevel.WARN, 1000);
    assertThat(m2.getText(), equalTo("text2"));
  }

  @Test
  public void awaitForSpecificLevelWaitsForMessages() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    new Thread(() -> {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {}
      logger.warn("text2");
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {}
      logger.info("text1");
    }).start();
    LogCapture.Message m1 = sink.awaitMessage(LDLogLevel.INFO, 1000);
    assertThat(m1.getText(), equalTo("text1"));
    LogCapture.Message m2 = sink.awaitMessage(LDLogLevel.WARN, 1000);
    assertThat(m2.getText(), equalTo("text2"));
  }
  
  @Test
  public void requireMessageReturnsMessage() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    logger.info("text1");
    LogCapture.Message m1 = sink.requireMessage(1000);
    assertThat(m1.getText(), equalTo("text1"));
  }
  
  @Test(expected=AssertionError.class)
  public void requireMessageTimesOut() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
      logger.info("text1");
    }).start();
    sink.requireMessage(10);
  }
  
  @Test
  public void requireMessageForSpecificLevel() {
    LogCapture sink = Logs.capture();
    LDLogger logger = LDLogger.withAdapter(sink, "");
    logger.warn("text2");
    logger.info("text1");
    LogCapture.Message m1 = sink.requireMessage(LDLogLevel.INFO, 1000);
    assertThat(m1.getText(), equalTo("text1"));
  }
}
