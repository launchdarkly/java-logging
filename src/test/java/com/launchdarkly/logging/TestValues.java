package com.launchdarkly.logging;

@SuppressWarnings("javadoc")
public interface TestValues {
  static final String SIMPLE_MESSAGE = "m0";
  static final Object MESSAGE_PARAM_1 = "xxx";
  static final Object MESSAGE_PARAM_2 = 567;
  static final Object MESSAGE_PARAM_3 = true;
  static final String MESSAGE_FORMAT_1 = "m1:1={}.";
  static final String MESSAGE_FORMAT_1_RESULT = "m1:1=xxx.";
  static final String MESSAGE_FORMAT_2 = "m2:1={},2={}.";
  static final String MESSAGE_FORMAT_2_RESULT = "m2:1=xxx,2=567.";
  static final String MESSAGE_FORMAT_3 = "m3:1={},2={},3={}.";
  static final String MESSAGE_FORMAT_3_RESULT = "m3:1=xxx,2=567,3=true.";
}
