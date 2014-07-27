package org.bips.appender;

/**
 * A thread-local appender, uses for events. You can override the destination file via system properties. Note that this class does no heap
 * allocation during processing, with the caveat of not supporting Unicode strings. It supports ASCII instead, because parsing UTF bytes
 * with JDK libraries causes heap allocations to occur. It will truncate very long strings, length being configurable. This class if not
 * thread-safe.
 */
public class ThreadLocalAppender
{

}
