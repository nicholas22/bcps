package org.bcps.config;

import java.util.regex.Pattern;

/**
 * A POJO holding the class regex for matching classes, and the methods regex to decide which methods in matched classes to instrument. The
 * before/after method code blocks that should be weaved, can also be specified here, but may be null or empty.
 */
public class InstrumentationClassConfig
{
  private final Pattern classRegex;
  private final Pattern methodRegex;

  private final String beforeMethod;
  private final String afterMethod;

  public InstrumentationClassConfig(final Pattern classRegex, final Pattern methodRegex, final String beforeMethod, final String afterMethod)
  {
    if (classRegex == null)
      throw new NullPointerException("classRegex");
    if (methodRegex == null)
      throw new NullPointerException("methodRegex");

    this.classRegex = classRegex;
    this.methodRegex = methodRegex;
    this.beforeMethod = beforeMethod;
    this.afterMethod = afterMethod;
  }

  public Pattern getClassRegex()
  {
    return classRegex;
  }

  public Pattern getMethodRegex()
  {
    return methodRegex;
  }

  public String getBeforeMethod()
  {
    return beforeMethod;
  }

  public String getAfterMethod()
  {
    return afterMethod;
  }

}
