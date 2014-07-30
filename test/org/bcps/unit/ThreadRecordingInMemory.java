package org.bcps.unit;

import org.bcps.appender.InMemoryAppender;
import org.bcps.dsl.instrumentation.ThreadedRecorder;
import org.bcps.queueing.EventQueueDisruptor;
import org.bcps.recording.EventRecorderImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Example using a thread-name decorator to auto-populate IDs and use an in-memory growing appender.
 */
public class ThreadRecordingInMemory
{
  @Before
  public void setup()
  {
    ThreadedRecorder.setEventRecorder(new EventRecorderImpl(new EventQueueDisruptor(new InMemoryAppender())));
  }

  public void teardown()
  {
    ThreadedRecorder.setEventRecorder(null);
  }

  @Test
  public void givenEvents_whenRecordedUsingDisruptorThroughQueue_thenPrinted()
      throws Exception
  {
    ThreadedRecorder.start("onReceive");
    ThreadedRecorder.start("calculate");
    ThreadedRecorder.complete("calculate");
    ThreadedRecorder.complete("onReceive");
    Thread.sleep(1000);
    System.out.println(InMemoryAppender.removeAndGet(Thread.currentThread().getName()));
  }
}
