package org.bcps.recording;

/**
 * Interface of an event recorder. This is capable of registering points where an operation has started and completed, tracking the time it
 * has taken for the operation. Methods of this class are not thread-safe.
 */
public interface EventRecorder
{
  /**
   * Marks the start time of an operation. The operation is tied to an identifier, which groups similar operation types. Note that null
   * arguments cause the event start recording to be skipped.
   * 
   * @param id An identifier, that groups similar operations e.g. a request ID, or thread name.
   * @param operation The name of the operation being recorded.
   * 
   */
  void start(String id, String operation);

  /**
   * Records the end time of an operation. To match a start of an operation, the same identifier and operation name should be used. Not all
   * complete calls need to be matched with started ones, in which case their recordings are thrown away. Note that null arguments cause the
   * event completion recording to be skipped.
   * 
   * @param id An identifier, that groups similar operations e.g. a request ID, or thread name.
   * @param operation The name of the operation being recorded.
   * 
   * @throws NullPointerException An argument is null
   */
  void complete(String id, String operation);
}
