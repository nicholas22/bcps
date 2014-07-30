package org.bcps.helpers;

/**
 * Timing helper class, for computing nanosecond timestamps
 */
public class TimingHelper
{
  private static final long preciseMarkerNanos = System.currentTimeMillis() * 1000;
  private static final long accurateMarkerNanos = System.nanoTime();

  private TimingHelper()
  {
    throw new UnsupportedOperationException("private");
  }

  /**
   * Get the current nanosecond timestamp, using a highly precise & highly accurate method. This timestamp only make sense is used within
   * the context of the same JVM (its nanosecond part should not be used to compare values between processes of remote JVMs).
   */
  public static long getNanoTimestamp()
  {
    return preciseMarkerNanos + Math.abs(System.nanoTime() - accurateMarkerNanos);
  }
}
