package org.bips.appender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bips.helpers.JavaPropsHelper;
import org.bips.queueing.Event;

/**
 * Simple in-memory appender, stores events in a map. Events can be fetched by interested parties via static methods. Retrieving events
 * removes them from the cache. When the cache reaches a configurable size, it will evict eldest entries. This appender heap-allocates
 * objects. This class is thread-safe.
 */
public class InMemoryAppender
    implements EventAppender
{
  private static final String INITIAL_SIZE_PROP = "bips.inmem.initial.size";
  private static final int INITIAL_SIZE_DEFAULT = 128;
  private static final int INITIAL_SIZE = JavaPropsHelper.getBufferSize(INITIAL_SIZE_PROP, INITIAL_SIZE_DEFAULT);
  private static final String MAX_SIZE_PROP = "bips.inmem.max.size";
  private static final int MAX_SIZE_DEFAULT = 1024;
  private static final int MAX_SIZE = JavaPropsHelper.getBufferSize(MAX_SIZE_PROP, MAX_SIZE_DEFAULT);

  // stores copied events
  private static final LinkedHashMap<String, List<Event>> map = new LinkedHashMap<String, List<Event>>(INITIAL_SIZE) {
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean removeEldestEntry(final Entry<String, List<Event>> eldest)
    {
      return map.size() >= MAX_SIZE;
    }

  };

  @Override
  public void process(final Event event)
  {
    if (event != null)
      synchronized(map)
      {
        List<Event> list = map.get(event.id);
        if (list == null)
        {
          // create event list
          list = new ArrayList<Event>(INITIAL_SIZE);
          map.put(event.id, list);
        }
        list.add(event.clone());
      }
  }

  public static List<Event> removeAndGet(final String id)
  {
    synchronized(map)
    {
      return map.remove(id);
    }
  }

}
