package com.launchdarkly.logging;

import org.junit.Test;

import static com.launchdarkly.logging.SimpleFormat.format;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("javadoc")
public class SimpleFormatTest extends BaseTest {
  @Test
  public void messageWithOneParam() {
    assertEquals("A.x:y", format("{}.x:y", "A"));
    assertEquals("x.A:y", format("x.{}:y", "A"));
    assertEquals("x.y:A", format("x.y:{}", "A"));
  }
  
  @Test
  public void messageWithTwoParams() {
    assertEquals("A.B:x", format("{}.{}:x", "A", "B"));
    assertEquals("A.x:B", format("{}.x:{}", "A", "B"));
    assertEquals("x.A:B", format("x.{}:{}", "A", "B"));
  }

  @Test
  public void messageWithThreeParams() {
    assertEquals("A.B:x-C", format("{}.{}:x-{}", "A", "B", "C"));
    assertEquals("x.A:B-C", format("x.{}:{}-{}", "A", "B", "C"));
    assertEquals("A.B:C-x", format("{}.{}:{}-x", "A", "B", "C"));
  }
  
  @Test
  public void excessPlaceholdersAreIgnored() {
    assertEquals("A but not {}", format("{} but not {}", "A"));
    assertEquals("A and B but not {} or {}", format("{} and {} but not {} or {}", "A", "B"));
    assertEquals("A and B and C but not {} or {}", format("{} and {} and {} but not {} or {}", "A", "B", "C"));
  }

  @Test
  public void excessParametersAreIgnored() {
    assertEquals("no placeholders here", format("no placeholders here", "A", "B"));
    assertEquals("A is all", format("{} is all", "A", "B"));
    assertEquals("A and B are all", format("{} and {} are all", "A", "B", "C"));
  }

  @Test
  public void placeholdersCanBeEscaped() {
    assertEquals("not here {} or here {} but here!",
        format("not here \\{} or here \\{} but here{}", "!"));
  }
  
  @Test
  public void nullParameterIsTreatedAsEmptyString() {
    assertEquals("not a thing", format("not {}a thing", (Object)null));
  }
}
