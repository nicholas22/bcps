package org.bcps;

import org.bcps.appender.ConsolePrintingAppender;
import org.bcps.dsl.instrumentation.ThreadedRecorder;
import org.bcps.queueing.EventQueueDisruptor;
import org.bcps.recording.EventRecorderImpl;

/**
 * Demo for end-to-end instrumentation agent test. To run this you need to setup Eclipse with these JVM args:
 * 
 * <pre>
 * -javaagent:/path/to/bcpsproj/jar/bcps.jar=instrument -Dbcps.instrumentation.config=/path/to/bcpsproj/config/instrumentation.config
 * </pre>
 * 
 * The end result will be output to Console, showing the START/COMPLETED cutpoints of this class's methods, e.g.:
 * 
 * <pre>
 * 1406754555083807,START,main,someMethodFoo
 * 1406754555147053,COMPLETED,main,someMethodFoo
 * 1406754555150667,START,main,someMethodBar
 * 1406755560679535,COMPLETED,main,someMethodBar
 * </pre>
 * 
 * You may also see a lot of "baz" lines printed out, these are instrumented sun.* methods (as configured in the instrumentation config)
 * 
 * To enable DEBUG set -Dbcps.instrumentation.debug=true
 */
public class InstrumentationTest
{

  public static void main(final String[] args)
      throws Exception
  {
    ThreadedRecorder.setEventRecorder(new EventRecorderImpl(new EventQueueDisruptor(new ConsolePrintingAppender())));
    someMethodFoo();
    someMethodBar(5, "test arg");
    Thread.sleep(1000);
  }

  private static void someMethodFoo()
  {
    System.out.println("test");
  }

  private static void someMethodBar(final int a, final String b)
      throws InterruptedException
  {
    Thread.sleep(1000);
  }
}
