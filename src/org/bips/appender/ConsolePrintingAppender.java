package org.bips.appender;

import org.bips.queueing.Event;

/**
 * Basic console appender, prints events to the console. It is meant to be used for debugging only. This appender heap-allocates objects.
 * This class is thread-safe.
 */
public class ConsolePrintingAppender
    implements EventAppender
{
  /**
   * {@inheritDoc}
   */
  @Override
  public void process(final Event event)
  {
    if (event != null)
    {
      StringBuilder sb = new StringBuilder(256);
      sb.append(event.timestamp);
      sb.append(event.isStartEvent ? ",START," : ",COMPLETED,");
      sb.append(event.id);
      sb.append(",");
      sb.append(event.operation);
      System.out.println(sb.toString());
    }
  }
}
