package com.launchdarkly.logging;

/**
 * Defines a simple parameter-substitution format, identical to the format used by SLF4J.
 * <p>
 * All logger implementations must support this format, either by using {@link SimpleFormat} or
 * by implementing it themselves.
 * <p> 
 * The format string may contain any number of placeholders. A placeholder is simply the string
 * "{}"; there is no special type-specific syntax as there is in {@code String.format()}. Each
 * placeholder is substituted with the result of calling {@code toString()} on the corresponding
 * parameter, in the order that they appear. If there are more placeholders than there are
 * parameters, or vice versa, the excess ones are ignored.
 */
public abstract class SimpleFormat {
  private SimpleFormat() {}

  /**
   * Substitutes a single parameter into a format string.
   * 
   * @param format the format string
   * @param param the parameter
   * @return the result
   */
  public static String format(String format, Object param) {
    StringBuilder out = new StringBuilder();
    int p = substituteNext(format, 0, param, out);
    out.append(format.substring(p));
    return out.toString();
  }

  /**
   * Substitutes two parameters into a format string.
   * 
   * @param format the format string
   * @param param1 the first parameter
   * @param param2 the second parameter
   * @return the result
   */
  public static String format(String format, Object param1, Object param2) {
    StringBuilder out = new StringBuilder();
    int p = substituteNext(format, 0, param1, out);
    p = substituteNext(format, p, param2, out);
    out.append(format.substring(p));
    return out.toString();
  }

  /**
   * Substitutes any number of parameters into a format string.
   * 
   * @param format the format string
   * @param params the parameters
   * @return the result
   */
  public static String format(String format, Object... params) {
    StringBuilder out = new StringBuilder();
    int p = 0;
    for (Object param: params) {
      p = substituteNext(format, p, param, out);
    }
    out.append(format.substring(p));
    return out.toString();
  }

  private static int substituteNext(String format, int pos, Object param, StringBuilder out) {
    while (pos < format.length()) {
      int next = format.indexOf("{}", pos);
      if (next < 0) {
        out.append(format.substring(pos));
        return format.length();
      }
      if (next > 0 && format.charAt(next - 1) == '\\') {
        out.append(format.substring(pos, next - 1));
        out.append(format.substring(next, next + 2));
        pos = next + 2;
        continue;
      }
      out.append(format.substring(pos, next));
      out.append(param == null ? "" : param.toString());
      return next + 2;
    }
    return format.length();
  }
}
