package com.launchdarkly.logging;

import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("javadoc")
public abstract class ParameterizedTestWithLevel implements TestValues {
  @Parameters(name = "{0}")
  public static Collection<Object[]> outputLevel() {
    List<Object[]> ret = new ArrayList<>();
    for (LDLogLevel outputLevel: LDLogLevel.values()) {
      if (outputLevel != LDLogLevel.NONE) {
        ret.add(new Object[] { outputLevel });
      }
    }
    return ret;
  }
}
