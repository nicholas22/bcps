package org.bcps.appender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bcps.helpers.JavaPropsHelper;
import org.bcps.queueing.Event;

/**
 * A text file appender for events. You can override the destination file via system properties. Note that this class does no heap
 * allocation during processing, with the caveat of not supporting Unicode strings. It supports ASCII instead, because parsing UTF bytes
 * with JDK libraries causes heap allocations to occur. It will truncate very long strings, the length being configurable. This class is not
 * thread-safe.
 */
public class LowLatencyFileAppender
    implements EventAppender
{
  public static final String OUTPUT_FILE_DEFAULT = "bcps." + new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").format(new Date()) + "..tsv";
  private static final String OUTPUT_FILE_PROP = "bcps.output.file";
  private static final int OUTPUT_BUFFER_BYTES_DEFAULT = 64 * 1024;
  private static final String OUTPUT_STREAM_BUFFER_PROP = "bcps.stream.buffer.bytes";
  private static final String MAX_TEXT_BUFFER_PROP = "bcps.max.text.buffer.bytes";
  private static final int MAX_TEXT_BUFFER_DEFAULT = 256;

  private static final String FIELD_SEP = ",";
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private static final byte[] IS_START = (FIELD_SEP + "START" + FIELD_SEP).getBytes();
  private static final byte[] IS_COMPLETE = (FIELD_SEP + "COMPLETE" + FIELD_SEP).getBytes();

  private final OutputStream output;
  private final byte[] textBuffer;
  private final byte[] timestampBuffer;

  public LowLatencyFileAppender()
  {
    // output file
    String outputFileName = JavaPropsHelper.getString(OUTPUT_FILE_PROP, OUTPUT_FILE_DEFAULT);
    File outputFile = new File(outputFileName);
    // get output stream size
    int outputStreamBufferSize = JavaPropsHelper.getBufferSize(OUTPUT_STREAM_BUFFER_PROP, OUTPUT_BUFFER_BYTES_DEFAULT);
    // timestamp intermediary buffer, capable of holding maximum Long text, +4 bytes alignment
    timestampBuffer = new byte[24];
    // get max logged text buffer size
    int maxTextLength = JavaPropsHelper.getBufferSize(MAX_TEXT_BUFFER_PROP, MAX_TEXT_BUFFER_DEFAULT);
    textBuffer = new byte[maxTextLength];

    // attempt to create parent path, if not existent
    if (!outputFile.exists())
      try
      {
        File parentFolder = outputFile.getParentFile();
        if (parentFolder != null)
          if (!parentFolder.exists())
            parentFolder.mkdirs();
      }
      catch(Exception e)
      {
        throw new IllegalArgumentException("Could not touch output file path: " + outputFile, e);
      }

    // open output for writing
    try
    {
      output = new BufferedOutputStream(new FileOutputStream(outputFile), outputStreamBufferSize);
    }
    catch(Exception e)
    {
      throw new RuntimeException("Could not open file for writing: " + outputFile, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void process(final Event event)
  {
    // clear out all buffers in preparation of next message
    zeroOutBuffers();

    // timestamp is converted to byte-array text
    longToByteArrayText(event.timestamp, timestampBuffer);
    // check if a start or complete event
    byte[] isStart = event.isStartEvent ? IS_START : IS_COMPLETE;
    // maintain output limits, for truncating text if size exceeds max buffer

    // convert ASCII strings to byte[] without creating new objects
    int textBufferSize = truncatingToBytes(event.id, textBuffer, 0);
    textBufferSize = truncatingToBytes(FIELD_SEP, textBuffer, textBufferSize);
    textBufferSize = truncatingToBytes(event.operation, textBuffer, textBufferSize);
    textBufferSize = truncatingToBytes(LINE_SEPARATOR, textBuffer, textBufferSize);

    try
    {
      output.write(timestampBuffer);
      output.write(isStart);
      output.write(textBuffer, 0, textBufferSize);
      output.flush();
    }
    catch(Exception e)
    {
      throw new RuntimeException("Could not write " + textBufferSize + " bytes to output", e);
    }
  }

  private void zeroOutBuffers()
  {
    // zero-out timestamp array
    for (int i = 0; i < timestampBuffer.length; i++)
      timestampBuffer[i] = (byte) '0';
    for (int i = 0; i < textBuffer.length; i++)
      textBuffer[i] = 0;
  }

  /**
   * Converts the given (positive) timestamp to text, directly on the given byte array without allocating a string for the conversion
   */
  private static void longToByteArrayText(long timestamp, final byte[] destination)
  {
    // convert long to textual representation directly on the byte array, to avoid String allocation
    int size = (int) (Math.log10(timestamp) + 1);
    for (int i = 0; i < size; i++)
    {
      long temp = (long) Math.pow(10, size - i - 1);
      destination[i + destination.length - size] = (byte) ((timestamp / temp) + 48);
      timestamp = timestamp % temp;
    }
  }

  @SuppressWarnings("deprecation")
  private static int truncatingToBytes(final String data, final byte[] buffer, final int currentPos)
  {
    data.getBytes(0, data.length(), buffer, currentPos);
    return currentPos + data.length();
  }
}
