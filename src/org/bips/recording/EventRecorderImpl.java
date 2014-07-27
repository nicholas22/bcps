package org.bips.recording;

import org.bips.helpers.TimingHelper;
import org.bips.queueing.EventQueue;

/**
 * Implementation of an {@link EventRecorder}
 */
public final class EventRecorderImpl
    implements EventRecorder
{
  private final EventQueue eventQueue;

  public EventRecorderImpl(final EventQueue eventQueue)
  {
    if (eventQueue == null)
      throw new NullPointerException("eventQueue");
    this.eventQueue = eventQueue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(final String id, final String operation)
  {
    if (id != null && operation != null)
      eventQueue.enqueue(true, TimingHelper.getNanoTimestamp(), id, operation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void complete(final String id, final String operation)
  {
    if (id != null && operation != null)
      eventQueue.enqueue(false, TimingHelper.getNanoTimestamp(), id, operation);
  }

}
