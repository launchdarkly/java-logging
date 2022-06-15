package com.launchdarkly.logging;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

@SuppressWarnings("javadoc")
public class LogValuesTest {
  @Test
  public void deferCallsFunctionOnlyWhenStringified() {
    final AtomicInteger calls = new AtomicInteger(0);
    Object thing = LogValues.defer(new LogValues.StringProvider() {
      @Override
      public String get() {
        return "calls=" + calls.incrementAndGet();
      }
    });
    assertThat(calls.get(), equalTo(0));
    assertThat(thing.toString(), equalTo("calls=1"));;
    assertThat(calls.get(), equalTo(1));
    assertThat(thing.toString(), equalTo("calls=2"));;
    assertThat(calls.get(), equalTo(2));
  }
  
  @Test
  public void exceptionSummary() {
    Exception e1 = new IllegalArgumentException();
    assertThat(LogValues.exceptionSummary(e1), sameInstance((Object)e1));
    assertThat(LogValues.exceptionSummary(e1).toString(),
        equalTo("java.lang.IllegalArgumentException"));

    Exception e2 = new IllegalArgumentException("sorry");
    assertThat(LogValues.exceptionSummary(e2), sameInstance((Object)e2));
    assertThat(LogValues.exceptionSummary(e2).toString(),
        equalTo("java.lang.IllegalArgumentException: sorry"));
  }
  
  @Test
  public void exceptionTrace() {
    Exception e = new IllegalArgumentException();
    String s = LogValues.exceptionTrace(e).toString();
    assertThat(s, containsString("IllegalArgumentException"));
    assertThat(s, containsString("at com.launchdarkly.logging.LogValuesTest.exceptionTrace"));
    
    assertThat(LogValues.exceptionTrace(null), nullValue());
  }
}
