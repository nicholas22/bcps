package org.bips;

import org.bips.appender.InMemoryAppender;
import org.bips.queueing.EventQueueDisruptor;
import org.bips.recording.EventRecorderImpl;
import org.bips.recording.ThreadEventDecorator;
import org.junit.Before;
import org.junit.Test;

/**
 * Example using a thread-name decorator to auto-populate IDs and use an in-memory growing appender.
 */
public class ThreadRecordingInMemory
{
  private ThreadEventDecorator recorder;

  @Before
  public void setup()
  {
    recorder = new ThreadEventDecorator(new EventRecorderImpl(new EventQueueDisruptor(new InMemoryAppender())));
  }

  @Test
  public void givenEvents_whenRecordedUsingDisruptorThroughQueue_thenPrinted()
      throws Exception
  {
    recorder.start("onReceive");
    recorder.start("calculate");
    recorder.complete("calculate");
    recorder.complete("onReceive");
    Thread.sleep(1000);
    System.out.println(InMemoryAppender.removeAndGet(Thread.currentThread().getName()));

  }
}
