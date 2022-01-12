package com.launchdarkly.logging;

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
}
