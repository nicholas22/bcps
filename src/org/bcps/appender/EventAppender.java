package org.bcps.appender;

import org.bcps.queueing.Event;

/**
 * Interface of an event appender, accepts events and processes them using some strategy.
 */
public interface EventAppender
{
  /**
   * Processes the given event. Note that if the even is null, it will be discarded.
   */
  void process(Event event);
}
