package org.bcps.queueing;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.bcps.appender.EventAppender;
import org.bcps.helpers.JavaPropsHelper;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Event queue implementation using a fast & bounded ring buffer, using no heap allocations on the hot path.
 */
public class EventQueueDisruptor
    implements EventQueue
{
  private static final String RING_SIZE_PROP = "bcps.ring.size";
  private final RingBuffer<Event> ringBuffer;

  @SuppressWarnings("unchecked")
  public EventQueueDisruptor(final EventAppender eventAnalyzer)
  {
    final AtomicInteger threadCounter = new AtomicInteger();
    // create disruptor queue
    Disruptor<Event> disruptor = new Disruptor<Event>(new EventFactory(), JavaPropsHelper.getInt(RING_SIZE_PROP, 32),
        Executors.newCachedThreadPool(new ThreadFactory() {
          @Override
          public Thread newThread(final Runnable runnable)
          {
            Thread thread = new Thread(runnable, EventQueueDisruptor.class.getSimpleName() + "Thread-" + threadCounter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
          }
        }), ProducerType.SINGLE, new SleepingWaitStrategy());
    // event handler passes events to the {@link EventAnalyzer}
    disruptor.handleEventsWith(new com.lmax.disruptor.EventHandler<Event>() {
      @Override
      public void onEvent(final Event event, final long sequence, final boolean endOfBatch)
          throws Exception
      {
        eventAnalyzer.process(event);
      }
    });
    // start processing
    ringBuffer = disruptor.start();
  }

  @Override
  public void enqueue(final boolean isStartEvent, final long timestamp, final String id, final String operation)
  {
    // claim next slot in ring buffer
    long sequence = ringBuffer.next();
    Event event = ringBuffer.get(sequence);

    // copy fields
    event.isStartEvent = isStartEvent;
    event.timestamp = timestamp;
    event.id = id;
    event.operation = operation;

    ringBuffer.publish(sequence);
  }

}
