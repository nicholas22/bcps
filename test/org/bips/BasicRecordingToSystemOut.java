package org.bips;

import org.bips.appender.ConsolePrintingAppender;
import org.bips.queueing.EventQueueDisruptor;
import org.bips.queueing.EventQueuePassThrough;
import org.bips.recording.EventRecorder;
import org.bips.recording.EventRecorderImpl;
import org.junit.Test;

/**
 * A very simple example recording events to console
 */
public class BasicRecordingToSystemOut
{
  private EventRecorder recorder;

  @Test
  public void givenEvents_whenRecordedUsingPassThroughQueue_thenPrinted()
  {
    recorder = new EventRecorderImpl(new EventQueuePassThrough(new ConsolePrintingAppender()));
    recorder.start("REQUEST_ID_342", "onReceive");
    recorder.start("REQUEST_ID_342", "calculate");
    recorder.complete("REQUEST_ID_342", "calculate");
    recorder.complete("REQUEST_ID_342", "onReceive");
  }

  @Test
  public void givenEvents_whenRecordedUsingDisruptorThroughQueue_thenPrinted()
      throws Exception
  {
    recorder = new EventRecorderImpl(new EventQueueDisruptor(new ConsolePrintingAppender()));
    recorder.start("REQUEST_ID_342", "onReceive");
    recorder.start("REQUEST_ID_342", "calculate");
    recorder.complete("REQUEST_ID_342", "calculate");
    recorder.complete("REQUEST_ID_342", "onReceive");
    Thread.sleep(1000);
  }
}
