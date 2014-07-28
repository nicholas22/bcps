package org.bips;

import java.io.File;
import org.bips.appender.LowLatencyFileAppender;
import org.bips.queueing.EventQueueDisruptor;
import org.bips.recording.EventRecorder;
import org.bips.recording.EventRecorderImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * One of the better examples, recording events to file with low-latency, without any heap allocations.
 */
public class GcFreeRecordingToFile
{
  private EventRecorder recorder;

  @Before
  public void setup()
  {
    recorder = new EventRecorderImpl(new EventQueueDisruptor(new LowLatencyFileAppender()));
  }

  @After
  public void teardown()
      throws Exception
  {
    File output = new File(LowLatencyFileAppender.OUTPUT_FILE_DEFAULT);
    output.delete();
  }

  @Test
  public void givenEvents_whenRecordedUsingDisruptorThroughQueue_thenPrinted()
      throws Exception
  {
    recorder.start("REQUEST_ID_343", "onReceive");
    recorder.start("REQUEST_ID_343", "calculate");
    recorder.complete("REQUEST_ID_343", "calculate");
    recorder.complete("REQUEST_ID_343", "onReceive");
    Thread.sleep(1000);
  }
}
