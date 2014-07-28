package org.bips.queueing;

import org.bips.appender.EventAppender;

/**
 * An implementation of {@link EventQueue} that uses the calling thread to call into the analyzer. This queue heap-allocates objects. It is
 * meant to be used for debugging only. This class's thread-safety depends on the given {@link EventAppender}.
 */
public class EventQueuePassThrough
    implements EventQueue
{
  private final EventAppender eventAnalyzer;

  public EventQueuePassThrough(final EventAppender eventAnalyzer)
  {
    if (eventAnalyzer == null)
      throw new NullPointerException("eventAnalyzer");
    this.eventAnalyzer = eventAnalyzer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void enqueue(final boolean isStartEvent, final long timestamp, final String id, final String operation)
  {
    Event event = new Event(isStartEvent, timestamp, id, operation);
    if (event.isValid())
      eventAnalyzer.process(event);
  }
}
