package org.bcps.queueing;

/**
 * Queueing interface used for transporting events from recording to analysis
 */
public interface EventQueue
{
  /**
   * Enqueues an event
   * 
   * @param isStartEvent Whether event signifies a start (otherwise its a completion event).
   * @param timestamp The timestamp of the event.
   * @param id An identifier, that groups similar operations e.g. a request ID, or thread name.
   * @param operation The name of the operation being recorded.
   * @param params Optional extra params
   */
  void enqueue(boolean isStartEvent, long timestamp, String id, String operation, String params);
}
