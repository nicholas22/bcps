package org.bcps.queueing;

/**
 * POJO storing event data
 */
public final class Event
    implements Cloneable
{
  public boolean isStartEvent;
  public long timestamp;
  public String id;
  public String operation;
  public String params;

  public Event()
  {
    isStartEvent = false;
    timestamp = 0;
    id = "";
    operation = "";
    params = "";
  }

  public Event(final boolean isStartEvent, final long timestamp, final String id, final String operation, final String params)
  {
    this.isStartEvent = isStartEvent;
    this.timestamp = timestamp;
    this.id = id;
    this.operation = operation;
    this.params = params;
  }

  public boolean isValid()
  {
    return timestamp > 0 && id != null && operation != null;
  }

  @Override
  public Event clone()
  {
    return new Event(isStartEvent, timestamp, id, operation, params);
  }

  @Override
  public String toString()
  {
    return "Event [isStartEvent=" + isStartEvent + ", timestamp=" + timestamp + ", id=" + id + ", operation=" + operation + ", params="
        + params + "]";
  }

}
