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
  private static final int PARTS_EXPECTED = 4;

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
        parseEntry(line, result);
    }
    finally
    {
      reader.close();
    }

    return result;
  }

  private static void parseEntry(final String line, final List<InstrumentationClassConfig> result)
  {
    if (!line.startsWith(COMMENT))
    {
      // split config entry parts
      String[] parts = line.split(FIELD_SEPARATOR);
      if (parts.length < PARTS_EXPECTED)
        throw new IllegalArgumentException("Invalid number of parts in config file entry (" + parts.length + ", expected " + PARTS_EXPECTED
            + "); offending entry was: " + line);

      try
      {
        // parse
        result.add(new InstrumentationClassConfig(Pattern.compile(parts[0]), Pattern.compile(parts[1]), parts[2], parts[3]));
      }
      catch(Exception e)
      {
        throw new IllegalArgumentException("Error parsing entry: " + line, e);
      }
    }
  }
}
