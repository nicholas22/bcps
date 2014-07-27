package org.bips.queueing;

/**
 * POJO storing event data
 */
public final class Event
{
  public boolean isStartEvent;
  public long timestamp;
  public String id;
  public String operation;

  public Event()
  {
    isStartEvent = false;
    timestamp = 0;
    id = "";
    operation = "";
  }

  public Event(final boolean isStartEvent, final long timestamp, final String id, final String operation)
  {
    this.isStartEvent = isStartEvent;
    this.timestamp = timestamp;
    this.id = id;
    this.operation = operation;
  }

  public boolean isValid()
  {
    return timestamp > 0 && id != null && operation != null;
  }

}
