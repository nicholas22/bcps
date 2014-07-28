package org.bips.recording;

/**
 * Decorator for an {@link EventRecorder}, using the current thread name as ID
 */
public class ThreadEventDecorator
{
  private final EventRecorder eventRecorder;

  public ThreadEventDecorator(final EventRecorder eventRecorder)
  {
    if (eventRecorder == null)
      throw new NullPointerException("eventRecorder");
    this.eventRecorder = eventRecorder;
  }

  /**
   * @see EventRecorder
   */
  public void start(final String operation)
  {
    if (operation != null)
      eventRecorder.start(Thread.currentThread().getName(), operation);
  }

  /**
   * @see EventRecorder
   */
  public void complete(final String operation)
  {
    // TODO: low-latency subclass of this which caches thread names to avoid String allocation
    if (operation != null)
      eventRecorder.complete(Thread.currentThread().getName(), operation);
  }

}
