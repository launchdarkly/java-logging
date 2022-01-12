/**
 * A simple logging abstraction that is used by LaunchDarkly Java and Android code.
 * <p>
 * This package provides a facade for LaunchDarkly code to write log output in a
 * generic way without referencing any specific logging framework or third-party facade.
 * <p>
 * It does not deal with administrative tasks such as rotating log files; the assumption
 * is that those would be set up at an OS level or by an application framework.
 * <p>
 * There are built-in implementations for basic logging: see {@link com.launchdarkly.logging.Logs}.
 * The API can also be connected to other logging frameworks with a simple adapter
 * interface, and LaunchDarkly provides several such adapters.
 * <p>
 * The reason for this indirect approach to logging is that LaunchDarkly tools can run
 * on both server-side Java and Android, and there is no single logging framework that
 * is consistently favored across all platforms. For instance, some server-side Java
 * applications may use <a href="https://www.slf4j.org/">SLF4J</a> while others use
 * {@code java.util.logging}, and Android applications may use
 * <a href="https://github.com/JakeWharton/timber">Timber</a> or use the native Android
 * logging API directly. Therefore, it's undesirable for the LaunchDarkly libraries to
 * have built-in dependencies on any of these. This package, with its small feature set
 * geared toward the needs of LaunchDarkly SDKs, aims to make the task of writing and
 * maintaining logging adapters very straightforward, and to reduce the chance that a
 * change in third-party APIs will cause backward incompatibillity.
 *
 * <h2>Examples</h2>
 * <p>
 * The example code below shows how to configure the LaunchDarkly server-side SDK for
 * Java to use some of the standard logging implementations. For more examples of how
 * to specify a logging implementation when using the LaunchDarkly SDKs or other
 * libraries, consult the documentation for those libraries. Each library may have its
 * own rules for what the default logging implementation is if you don't specify one.
 * <p>
 * In this configuration, logging goes to the standard output stream (`System.out`):
 * <pre><code>
 *   LDConfig config = new LDConfig.Builder()
 *     .logging(Components.logging(Logs.toStream(System.out)))
 *     .build();
 * </code></pre>
 * <p>
 * This is the same, except all logging below Warn level is suppressed:
 * <pre><code>
 *   LDConfig config = new LDConfig.Builder()
 *     .logging(Components.logging(Logs.toStream(System.out).level(LDLogLevel.WARN)))
 *     .build();
 * </code></pre>
 *
 * <h2>Adapters</h2>
 * <p>
 * If you want to send logging to a destination that isn't built into this package,
 * the {@code com.launchDarkly.logging} API allows you to define your own adapter by
 * implementing the {@link com.launchdarkly.logging.LDLogAdapter} interface. We have
 * already created implementations for use with several popular logging frameworks:
 * <ul>
 * <li> <a href="https://www.slf4j.org/">SLF4J</a>: Use
 *   {@link com.launchdarkly.logging.LDSLF4J}. </li>
 * <li> <a href="https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html">java.util.logging</a>:
 *   Use {@link com.launchdarkly.logging.Logs#toJavaUtilLogging()}. </li>
 * <li> <a href="https://github.com/JakeWharton/timber">Timber</a>, or the native
 *   Android logging API: Provided in the LaunchDarkly Android SDK.</li>
 * </ul>
 */
package com.launchdarkly.logging;
