package org.bcps.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser of instrumentation configuration file entries to {@link InstrumentationClassConfig}
 */
public class InstrumentationClassConfigParser
{
  private static final String COMMENT = "#";
  private static final String FIELD_SEPARATOR = "\t\\|\t";
  private static final String HEADER_START = "- ";
  private static final int HEADER_PARTS_EXPECTED = 2;

  private InstrumentationClassConfigParser()
  {
    throw new UnsupportedOperationException("private");
  }

  /**
   * Parses the given file's config entries
   * 
   * @throws IllegalArgumentException An entry is invalid
   * @throws IOException An I/O error has occurred
   * @throws NullPointerException An argument is null
   */
  public static Iterable<InstrumentationClassConfig> parse(final File config)
      throws Exception
  {
    if (config == null)
      throw new NullPointerException("config");

    List<InstrumentationClassConfig> result = new ArrayList<InstrumentationClassConfig>(64);
    BufferedReader reader = new BufferedReader(new FileReader(config));
    try
    {
      String line = null;
      while ((line = reader.readLine()) != null)
        parseEntry(line, reader, result);
    }
    finally
    {
      reader.close();
    }

    return result;
  }

  private static void parseEntry(final String line, final BufferedReader reader, final List<InstrumentationClassConfig> result)
      throws IOException
  {
    if (!line.startsWith(COMMENT))
    {
      if (!line.startsWith(HEADER_START))
        throw new IllegalArgumentException("Expected config entry header to start with '" + HEADER_START + "'");
      String[] parts = line.substring(HEADER_START.length()).split(" ");
      if (parts.length < HEADER_PARTS_EXPECTED)
        throw new IllegalArgumentException("Invalid number of parts in entry header (" + parts.length + ", expected "
            + HEADER_PARTS_EXPECTED + "); offending entry was: " + line);
      String classRegex = parts[0];
      String methodRegex = parts[1];

      String beforeMethod = reader.readLine();
      if (beforeMethod == null)
        throw new IllegalArgumentException("EOF while expecting before method code for entry: " + line);
      String afterMethod = reader.readLine();
      if (afterMethod == null)
        throw new IllegalArgumentException("EOF while expecting after method code for entry: " + line);

      try
      {
        // parse
        result.add(new InstrumentationClassConfig(Pattern.compile(classRegex), Pattern.compile(methodRegex), beforeMethod, afterMethod));
      }
      catch(Exception e)
      {
        throw new IllegalArgumentException("Error parsing entry: " + line, e);
      }
    }
  }
}
