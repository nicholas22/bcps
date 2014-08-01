package org.bcps.dsl.instrumentation;

import org.bcps.recording.EventRecorder;

/**
 * DSL for an {@link EventRecorder}, using the current thread name as ID
 */
public class ThreadedRecorder
{
  private static volatile EventRecorder eventRecorder;

  private ThreadedRecorder()
  {
    throw new UnsupportedOperationException("private");
  }

  /**
   * Sets the event recorder, should be the first call before using this class
   */
  public synchronized static void setEventRecorder(final EventRecorder newValue)
  {
    if (eventRecorder != null)
      throw new IllegalStateException("Event recorder already set");
    eventRecorder = newValue;
  }

  /**
   * @see EventRecorder
   */
  public static void start(final String operation)
  {
    start(operation, "");
  }

  /**
   * @see EventRecorder
   */
  public static void start(final String operation, final String params)
  {
    EventRecorder rec = eventRecorder;
    if (rec != null)
      if (operation != null)
        rec.start(Thread.currentThread().getName(), operation, params);
  }

  /**
   * @see EventRecorder
   */
  public static void complete(final String operation)
  {
    EventRecorder rec = eventRecorder;
    if (rec != null)
      if (operation != null)
        // TODO: low-latency subclass of this which caches thread names, or looks up native offset of name, to avoid String allocation
        eventRecorder.complete(Thread.currentThread().getName(), operation);
  }

}
