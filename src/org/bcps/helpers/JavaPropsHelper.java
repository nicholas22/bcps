package org.bcps.helpers;

/**
 * Helper for parsing Java properties
 */
public class JavaPropsHelper
{
  private JavaPropsHelper()
  {
    throw new UnsupportedOperationException("private");
  }

  /**
   * Returns the value of a system property as a String. Uses the default value upon missing property or parsing errors.
   * 
   * @throws NullPointerException NullPointerException
   */
  public static String getString(final String name, final String defaultValue)
  {
    if (name == null)
      throw new NullPointerException("name");

    String strValue = System.getProperty(name);
    if (strValue == null)
      return defaultValue;
    return strValue;
  }

  /**
   * Returns the value of a system property as an int. Uses the default value upon missing property or parsing errors.
   * 
   * @throws NullPointerException NullPointerException
   */
  public static int getInt(final String name, final int defaultValue)
  {
    if (name == null)
      throw new NullPointerException("name");

    String strValue = System.getProperty(name);
    try
    {
      return Integer.parseInt(strValue);
    }
    catch(Exception e)
    {
      return defaultValue;
    }
  }

  /**
   * Returns the value of a system property as an int. Uses the default value upon missing property or parsing errors.
   * 
   * @throws NullPointerException NullPointerException
   */
  public static int getBufferSize(final String name, final int defaultValue)
  {
    int result = getInt(name, defaultValue);
    while (result % 2 != 0)
      result++;
    if (result < 64)
      throw new IllegalArgumentException("Buffer " + name + " is too small: " + result);
    return result;
  }
}
