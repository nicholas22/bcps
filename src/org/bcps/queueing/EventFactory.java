package org.bcps.queueing;

/**
 * Factory for {@link Event}s
 */
public final class EventFactory
    implements com.lmax.disruptor.EventFactory<Event>
{
  @Override
  public Event newInstance()
  {
    return new Event();
  }
}
