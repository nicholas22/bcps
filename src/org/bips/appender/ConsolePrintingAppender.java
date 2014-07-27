package org.bips.appender;

import org.bips.queueing.Event;

/**
 * Basic console appender, prints events to the console. As opposed to most other appenders, this one heap-allocates objects, hence is not
 * recommended for GC-free environments. It is meant to be used for debugging only. This class is thread-safe.
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
      sb.append(event.isStartEvent ? "\tSTART\t\t" : "\tCOMPLETED\t");
      sb.append(event.id);
      sb.append("\t");
      sb.append(event.operation);
      System.out.println(sb.toString());
    }
  }
}
