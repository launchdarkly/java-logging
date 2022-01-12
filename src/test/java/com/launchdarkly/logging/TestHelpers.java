package com.launchdarkly.logging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("javadoc")
public class TestHelpers implements TestValues {  
  public static void writeTestMessages(LDLogger logger, LDLogLevel level) {
    switch (level) {
    case DEBUG:
      logger.debug(SIMPLE_MESSAGE);
      logger.debug(MESSAGE_FORMAT_1, MESSAGE_PARAM_1);
      logger.debug(MESSAGE_FORMAT_2, MESSAGE_PARAM_1, MESSAGE_PARAM_2);
      logger.debug(MESSAGE_FORMAT_3, MESSAGE_PARAM_1, MESSAGE_PARAM_2, MESSAGE_PARAM_3);
      break;
    case INFO:
      logger.info(SIMPLE_MESSAGE);
      logger.info(MESSAGE_FORMAT_1, MESSAGE_PARAM_1);
      logger.info(MESSAGE_FORMAT_2, MESSAGE_PARAM_1, MESSAGE_PARAM_2);
      logger.info(MESSAGE_FORMAT_3, MESSAGE_PARAM_1, MESSAGE_PARAM_2, MESSAGE_PARAM_3);
      break;
    case WARN:
      logger.warn(SIMPLE_MESSAGE);
      logger.warn(MESSAGE_FORMAT_1, MESSAGE_PARAM_1);
      logger.warn(MESSAGE_FORMAT_2, MESSAGE_PARAM_1, MESSAGE_PARAM_2);
      logger.warn(MESSAGE_FORMAT_3, MESSAGE_PARAM_1, MESSAGE_PARAM_2, MESSAGE_PARAM_3);
      break;
    case ERROR:
      logger.error(SIMPLE_MESSAGE);
      logger.error(MESSAGE_FORMAT_1, MESSAGE_PARAM_1);
      logger.error(MESSAGE_FORMAT_2, MESSAGE_PARAM_1, MESSAGE_PARAM_2);
      logger.error(MESSAGE_FORMAT_3, MESSAGE_PARAM_1, MESSAGE_PARAM_2, MESSAGE_PARAM_3);
      break;
    case NONE:
      break;
    }
  }
  
  public static <T> List<T> listFromIterable(Iterable<T> it) {
    List<T> list = new ArrayList<>();
    for (T t: it) {
      list.add(t);
    }
    return list;
  }

  public static <T> Set<T> setFromIterable(Iterable<T> it) {
    Set<T> set = new HashSet<>();
    for (T t: it) {
      set.add(t);
    }
    return set;
  }
  
  public static <T> void doEqualityTests(List<List<T>> testValues) {
    // Each element of testValues should be a list of two values that should be equal to each other,
    // but not equal to any of the other values in testValues. It would have been nicer to use a
    // single function that *creates* a value and call it twice, but since we can't use lambdas in
    // Java 7 that would be very verbose.
    for (int i = 0; i < testValues.size(); i++) {
      List<T> equalValues = testValues.get(i);
      T equalValue0 = equalValues.get(0);
      T equalValue1 = equalValues.get(1);
      assertEquals(equalValue0, equalValue0);
      assertEquals(equalValue0, equalValue1);
      assertEquals(equalValue1, equalValue0);
      assertEquals(equalValue0.hashCode(), equalValue1.hashCode());
      assertNotEquals(new ArbitraryClassThatDoesNotEqualOtherClasses(), equalValue0);
      assertNotEquals(equalValue0, new ArbitraryClassThatDoesNotEqualOtherClasses());
      assertNotEquals(null, equalValue0);
      assertNotEquals(equalValue0, null);
      for (int j = 0; j < testValues.size(); j++) {
        if (j != i) {
          T unequalValue = testValues.get(j).get(0);
          assertNotEquals(equalValue0, unequalValue);
          assertNotEquals(unequalValue, equalValue0);
        }
      }
    }
  }
  
  private static final class ArbitraryClassThatDoesNotEqualOtherClasses {}
}
